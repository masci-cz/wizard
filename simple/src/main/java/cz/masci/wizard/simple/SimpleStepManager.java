package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.*;

import java.util.Optional;
import java.util.function.Function;

/**
 * A simple implementation of the StepManager interface.
 *
 * @param <T> StepState type
 * @param <L> Value type held by the StepState it means the type of data in the leaf step
 * @param <H> Status type held by the StepState it means the type of data in the hierarchical step
 */
public class SimpleStepManager<T extends StepState<H, L>, H, L> implements StepManager<T, H, L> {

    private final T state;
    private final HierarchicalStep<H> root;

    /**
     * Constructs a {@code SimpleStepManager} with the given state and root hierarchical step.
     *
     * @param state the current step state; must not be {@code null}
     * @param root  the root hierarchical step of the wizard; must not be {@code null}
     */
    public SimpleStepManager(T state, HierarchicalStep<H> root) {
        assert state != null : "state must not be null";
        assert root != null : "root must not be null";

        this.state = state;
        this.root = root;
    }

    /**
     * <p>
     * Moves to the next step in the wizard only if the leaf step is valid.
     * </p>
     */
    @Override
    public void next() {
        if (!state.isValid()) {
            return;
        }

        step(HierarchicalStep::next, this::next);
    }

    /**
     * <p>
     * Moves to the previous step in the wizard. Independent of the leaf step validity.
     * </p>
     */
    @Override
    public void prev() {
        step(HierarchicalStep::prev, this::prev);
    }

    /**
     * Returns the current step state.
     *
     * @return the current step state
     */
    @Override
    public T get() {
        return state;
    }

    private void step(Function<HierarchicalStep<H>, Step> stepDirectionFnc, Runnable directionFnc) {
        state.completeLeafStep();

        var currentHierarchicalStep = state.getHierarchicalStep();
        var stepOptional = Optional.ofNullable(currentHierarchicalStep != null ? stepDirectionFnc.apply(currentHierarchicalStep) : root);

        if (stepOptional.isPresent()) {
            var step = stepOptional.get();
            if (step instanceof HierarchicalStep<?>) {
                @SuppressWarnings("unchecked") var hierarchicalStep = (HierarchicalStep<H>) step;
                state.setHierarchicalStep(hierarchicalStep);
                state.setLeafStep(null);
                directionFnc.run();
            } else {
                @SuppressWarnings("unchecked") var leafStep = (LeafStep<L>) step;
                state.setLeafStep(leafStep);
            }
        } else {
            var parent = state.getParent();
            state.setHierarchicalStep(parent.orElse(null));
            state.setLeafStep(null);
            if (parent.isPresent()) {
                directionFnc.run();
            }
        }
    }
}

package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.*;

import java.util.Optional;
import java.util.function.Function;

/**
 * A simple implementation of the StepManager interface.
 *
 * @param <T> StepState type
 * @param <U> Value type held by the StepState it means the type of date in the leaf step
 */
public class SimpleStepManager<T extends StepState<U>, U> implements StepManager<T, U> {

    private final T state;
    private final HierarchicalStep root;

    public SimpleStepManager(T state, HierarchicalStep root) {
        assert state != null : "state must not be null";
        assert root != null : "root must not be null";

        this.state = state;
        this.root = root;
    }

    @Override
    public void next() {
        step(HierarchicalStep::next, this::next);
    }

    @Override
    public void prev() {
        step(HierarchicalStep::prev, this::prev);
    }

    @Override
    public T get() {
        return state;
    }

    private void step(Function<HierarchicalStep, Step> stepDirectionFnc, Runnable directionFnc) {
        if (!state.isValid()) {
            return;
        }

        state.completeLeafStep();

        var currentHierarchicalStep = state.getHierarchicalStep();
        var stepOptional = Optional.ofNullable(currentHierarchicalStep != null ? stepDirectionFnc.apply(currentHierarchicalStep) : root);

        if (stepOptional.isPresent()) {
            var step = stepOptional.get();
            if (step instanceof HierarchicalStep hierarchicalStep) {
                state.setHierarchicalStep(hierarchicalStep);
                state.setLeafStep(null);
                directionFnc.run();
            } else {
                // noinspection unchecked
                state.setLeafStep((LeafStep<U>) step);
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

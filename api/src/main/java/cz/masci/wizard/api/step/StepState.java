package cz.masci.wizard.api.step;

import java.util.Optional;

/**
 * <pre>
 * Represents the state of a step in a wizard.
 * It holds the current leaf step and hierarchical step.
 * It provides methods to check if the step is valid,
 * get the current value, name, complete the leaf step,
 * and get the parent hierarchical step.
 * </pre>
 *
 * @param <H> the type of status held by the hierarchical step
 * @param <L> the type of value held by the leaf step
 */
public interface StepState<H, L> {
    void setLeafStep(LeafStep<L> leafStep);

    LeafStep<L> getLeafStep();

    void setHierarchicalStep(HierarchicalStep<H> hierarchicalStep);

    HierarchicalStep<H> getHierarchicalStep();

    /**
     * The step is valid when the current leaf step is valid or there is no leaf step set.
     *
     * @return <code>true</code> if the step is valid, false otherwise
     */
    default boolean isValid() {
        return Optional.ofNullable(getLeafStep())
                .map(LeafStep::isValid)
                .orElse(true);
    }

    /**
     * Get the current leaf step value.
     *
     * @return an {@link Optional} containing the current leaf step value or empty if there is no leaf step set
     */
    default Optional<L> getValue() {
        return Optional.ofNullable(getLeafStep())
                .map(LeafStep::getValue);
    }

    /**
     * Get the current leaf step value.
     *
     * @return an {@link Optional} containing the current leaf step value or empty if there is no leaf step set
     */
    default Optional<H> getStatus() {
        return Optional.ofNullable(getHierarchicalStep())
                .map(HierarchicalStep::getStatus);
    }

    /**
     * Get the current leaf step name.
     *
     * @return an {@link Optional} containing the current leaf step name or empty if there is no leaf step set
     */
    default Optional<String> getName() {
        return Optional.ofNullable(getLeafStep())
                .map(LeafStep::getName);
    }

    /**
     * Complete the current leaf step if it exists.
     */
    default void completeLeafStep() {
        Optional.ofNullable(getLeafStep())
                .ifPresent(LeafStep::complete);
    }

    /**
     * Get the parent hierarchical step of the current hierarchical step.
     *
     * @return an {@link Optional} containing the parent hierarchical step or empty if there is no parent
     */
    default Optional<HierarchicalStep<H>> getParent() {
        return Optional.ofNullable(getHierarchicalStep())
                .map(HierarchicalStep::getParent);
    }
}

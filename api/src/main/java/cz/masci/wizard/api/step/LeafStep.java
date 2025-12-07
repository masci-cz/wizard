package cz.masci.wizard.api.step;

/**
 * <pre>
 * A leaf step in a wizard.
 * It represents a single step that does not have any child steps.
 * For hierarchical step use {@link HierarchicalStep}.
 * </pre>
 */
public interface LeafStep<T> extends Step {
    /**
     * Name of the step.
     */
    String getName();

    /**
     * Get the value associated with this leaf step.
     *
     * @return the value of type T
     */
    T getValue();

    /**
     * Update the step value with the completed status.
     */
    void complete();

    /**
     * Check if the step is valid.
     *
     * @return true if the step is valid, false otherwise
     */
    default boolean isValid() {
        return true;
    }
}

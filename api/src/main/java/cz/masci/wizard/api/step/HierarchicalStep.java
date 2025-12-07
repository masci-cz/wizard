package cz.masci.wizard.api.step;

/**
 * <pre>
 * A hierarchical step in a wizard.
 * It contains multiple child steps and manage their navigation.
 * For leaf step use {@link LeafStep}.
 * </pre>
 */
public interface HierarchicalStep extends Step {
    /**
     * Set the parent hierarchical step.
     */
    void setParent(HierarchicalStep parent);

    /**
     * Get the parent hierarchical step.
     */
    HierarchicalStep getParent();

    /**
     * <pre>
     * Get the current child step index.
     * It is -1 when the step has not been started yet.
     * </pre>
     */
    int getCurrentIdx();

    /**
     * Get the next child step or null if there is no step left.
     *
     * @return the next child step or null if there is no step left
     */
    Step next();

    /**
     * Get the previous child step or null if there is no step left.
     *
     * @return the previous child step or null if there is no step left
     */
    Step prev();

    /**
     * <pre>
     * Rewind the hierarchical step to before the first child step.
     * If any of children are hierarchical steps, they should also be reset.
     * </pre>
     */
    void reset();
}

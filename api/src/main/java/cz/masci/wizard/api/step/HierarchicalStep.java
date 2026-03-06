package cz.masci.wizard.api.step;

/**
 * <pre>
 * A hierarchical step in a wizard.
 * It contains multiple child steps and manage their navigation.
 * For leaf step use {@link LeafStep}.
 * </pre>
 *
 * The value hold by hierarchical step is used to hold information
 * about the hierarchical step as a whole, e.g., a summary or aggregate data,
 * next/prev is enabled/disabled state, etc.
 *
 * @param <T> the type of value held by the hierarchical steps
 */
public interface HierarchicalStep<T> extends Step {
    /**
     * Set the parent hierarchical step.
     *
     * @param parent the parent {@link HierarchicalStep}, or {@code null} if this is the root step
     */
    void setParent(HierarchicalStep<T> parent);

    /**
     * Get the parent hierarchical step.
     *
     * @return the parent {@link HierarchicalStep}, or {@code null} if this is the root step
     */
    HierarchicalStep<T> getParent();

    /**
     * <pre>
     * Get the current child step index.
     * It is -1 when the step has not been started yet.
     * </pre>
     *
     * @return the zero-based index of the current child step, or {@code -1} if navigation has not started
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

    /**
     * <pre>
     *     Rewind the hierarchical step to the first child step.
     *     The current index will be set to 0.
     *     All child hierarchical steps should be reset to be ready for navigation from the start.
     * </pre>
     */
    void rewind();

    /**
     * Get the status value held by this hierarchical step.
     * The status can represent aggregate data, enabled/disabled navigation state, or any other summary.
     *
     * @return the status value of type {@code T}, or {@code null} if no status is set
     */
    T getStatus();
}

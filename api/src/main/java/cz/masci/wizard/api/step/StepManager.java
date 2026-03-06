package cz.masci.wizard.api.step;

/**
 * <pre>
 * Manages the progression through steps in a wizard.
 * It allows moving to the next and previous step and retrieving the current step state.
 * </pre>
 *
 * @param <T> the type of StepState
 * @param <L> the type of value held by the LeafStep
 * @param <H> the type of status held by the HierarchicalStep
 */
public interface StepManager<T extends StepState<H, L>, H, L> {
  /**
   * Advances the wizard to the next step.
   * Implementations may skip the transition if the current step is not valid.
   */
  void next();

  /**
   * Returns the wizard to the previous step.
   */
  void prev();

  /**
   * Returns the current step state reflecting the active leaf and hierarchical step.
   *
   * @return the current {@code T} step state instance
   */
  T get();
}

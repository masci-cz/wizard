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
  void next();
  void prev();
  T get();
}

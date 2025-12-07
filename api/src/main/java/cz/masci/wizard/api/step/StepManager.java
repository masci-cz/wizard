package cz.masci.wizard.api.step;

/**
 * <pre>
 * Manages the progression through steps in a wizard.
 * It allows moving to the next and previous step and retrieving the current step state.
 * </pre>
 *
 * @param <T> the type of StepState
 * @param <U> the type of value held by the StepState
 */
public interface StepManager<T extends StepState<U>, U> {
  void next();
  void prev();
  T get();
}

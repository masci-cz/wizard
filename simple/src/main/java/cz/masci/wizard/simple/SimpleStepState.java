package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import cz.masci.wizard.api.step.StepState;
import lombok.Data;

/**
 * A simple implementation of the {@link StepState} interface.
 * <p>
 * This class holds the current state of a wizard by maintaining references to
 * the current leaf step and hierarchical step. It provides a straightforward
 * implementation with mutable properties for both steps.
 * </p>
 * <p>
 * The Lombok {@code @Data} annotation automatically generates getters, setters,
 * toString, equals, and hashCode methods for the class properties.
 * </p>
 *
 * @param <H> the type of status held by the hierarchical step
 * @param <L> the type of value held by the leaf step
 */
@Data
public class SimpleStepState<H, L> implements StepState<H, L> {
  /**
   * The current leaf step in the wizard.
   * This represents the actual step where user interaction occurs.
   */
  private LeafStep<L> leafStep;
  
  /**
   * The current hierarchical step in the wizard.
   * This represents the container step that manages navigation between child steps.
   */
  private HierarchicalStep<H> hierarchicalStep;
}

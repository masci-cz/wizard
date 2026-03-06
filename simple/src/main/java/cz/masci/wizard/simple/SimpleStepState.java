package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import cz.masci.wizard.api.step.StepState;
import lombok.Data;

/**
 * A simple implementation of the {@link StepState} interface.
 *
 * @param <H> the type of status held by the hierarchical step
 * @param <L> the type of value held by the leaf step
 */
@Data
public class SimpleStepState<H, L> implements StepState<H, L> {
  private LeafStep<L> leafStep;
  private HierarchicalStep<H> hierarchicalStep;
}

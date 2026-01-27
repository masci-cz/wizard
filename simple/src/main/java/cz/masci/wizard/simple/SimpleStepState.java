package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import cz.masci.wizard.api.step.StepState;
import lombok.Data;

@Data
public class SimpleStepState<H, L> implements StepState<H, L> {
  private LeafStep<L> leafStep;
  private HierarchicalStep<H> hierarchicalStep;
}

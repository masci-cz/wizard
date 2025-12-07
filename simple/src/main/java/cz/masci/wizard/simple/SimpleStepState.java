package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import cz.masci.wizard.api.step.StepState;
import lombok.Data;

@Data
public class SimpleStepState<T> implements StepState<T> {
  private LeafStep<T> leafStep;
  private HierarchicalStep hierarchicalStep;
}

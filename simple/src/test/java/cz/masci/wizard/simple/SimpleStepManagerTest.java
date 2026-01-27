package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import cz.masci.wizard.api.step.StepState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleStepManagerTest {

    @Mock
    HierarchicalStep<String> currentHierarchicalStep;
    @Mock
    LeafStep<String> leafStep;
    @Mock
    HierarchicalStep<String> hierarchicalStep;

    @Mock
    private StepState<String, String> state;
    @Mock
    private HierarchicalStep<String> root;

    @InjectMocks
    private SimpleStepManager<StepState<String, String>, String, String> stepManager;

    // region constructor tests
    @Test
    void constructor_whenStateIsNull_thenThrowNullPointerException() {
        // given / when / then
        assertThrows(AssertionError.class, () -> new SimpleStepManager<>(null, root));
    }

    @Test
    void constructor_whenRootIsNull_thenThrowNullPointerException() {
        // given / when / then
        assertThrows(AssertionError.class, () -> new SimpleStepManager<>(state, null));
    }
    // endregion

    // region get tests
    @Test
    void get_shouldReturnState() {
        // given
        // state is initialized in setUp

        // when
        var result = stepManager.get();

        // then
        assertEquals(state, result);
    }
    // endregion

    // region next tests
    @Test
    void next_whenStateIsInvalid_shouldDoNothing() {
        // given
        when(state.isValid()).thenReturn(false);

        // when
        stepManager.next();

        // then
        verify(state, never()).completeLeafStep();
    }

    @Test
    void next_whenOnLeafStep_shouldMoveToNextLeafStep() {
        // given
        when(state.isValid()).thenReturn(true);
        when(state.getHierarchicalStep()).thenReturn(currentHierarchicalStep);
        when(currentHierarchicalStep.next()).thenReturn(leafStep);

        // when
        stepManager.next();

        // then
        verify(state).completeLeafStep();
        verify(state).setLeafStep(leafStep);
    }

    @Test
    void next_whenOnLastLeafStep_shouldMoveToNextHierarchicalStep() {
        // given
        when(state.isValid()).thenReturn(true);
        when(state.getHierarchicalStep()).thenReturn(currentHierarchicalStep).thenReturn(hierarchicalStep);
        when(currentHierarchicalStep.next()).thenReturn(hierarchicalStep);
        when(hierarchicalStep.next()).thenReturn(leafStep);

        // when
        stepManager.next();

        // then
        verify(state, times(2)).completeLeafStep();
        verify(state).setHierarchicalStep(hierarchicalStep);
        verify(state).setLeafStep(null);
        verify(state).setLeafStep(leafStep);
    }

    @Test
    void next_whenAtTheEnd_shouldMoveToParent() {
        // given
        when(state.isValid()).thenReturn(true);
        when(state.getHierarchicalStep()).thenReturn(currentHierarchicalStep).thenReturn(root);
        when(currentHierarchicalStep.next()).thenReturn(null);
        when(state.getParent()).thenReturn(Optional.of(root)).thenReturn(Optional.empty());
        when(root.next()).thenReturn(null); // End of parent's children

        // when
        stepManager.next();

        // then
        verify(state, times(2)).completeLeafStep();
        verify(state).setHierarchicalStep(root);
        verify(state, times(2)).setLeafStep(null);
    }
    // endregion

    // region prev tests
    @Test
    void prev_whenOnLeafStep_shouldMoveToPrevLeafStep() {
        // given
        when(state.getHierarchicalStep()).thenReturn(currentHierarchicalStep);
        when(currentHierarchicalStep.prev()).thenReturn(leafStep);

        // when
        stepManager.prev();

        // then
        verify(state).completeLeafStep();
        verify(state).setLeafStep(leafStep);
    }

    @Test
    void prev_whenOnFirstLeafStep_shouldMoveToPrevHierarchicalStep() {
        // given
        when(state.getHierarchicalStep()).thenReturn(currentHierarchicalStep).thenReturn(hierarchicalStep);
        when(currentHierarchicalStep.prev()).thenReturn(hierarchicalStep);
        when(hierarchicalStep.prev()).thenReturn(leafStep);

        // when
        stepManager.prev();

        // then
        verify(state, times(2)).completeLeafStep();
        verify(state).setHierarchicalStep(hierarchicalStep);
        verify(state).setLeafStep(null);
        verify(state).setLeafStep(leafStep);
    }

    @Test
    void prev_whenAtTheStart_shouldMoveToParent() {
        // given
        when(state.getHierarchicalStep()).thenReturn(currentHierarchicalStep).thenReturn(root);
        when(currentHierarchicalStep.prev()).thenReturn(null);
        when(state.getParent()).thenReturn(Optional.of(root)).thenReturn(Optional.empty());
        when(root.prev()).thenReturn(null); // Start of parent's children

        // when
        stepManager.prev();

        // then
        verify(state, times(2)).completeLeafStep();
        verify(state).setHierarchicalStep(root);
        verify(state, times(2)).setLeafStep(null);
    }
    // endregion
}
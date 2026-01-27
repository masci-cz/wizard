package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link SimpleStepState}.
 * <p>
 * This test class verifies the basic functionality of the SimpleStepState class,
 * including setting and getting leaf steps and hierarchical steps, as well as
 * testing the default methods inherited from the StepState interface.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class SimpleStepStateTest {

    @Mock
    private LeafStep<String> leafStep;

    @Mock
    private HierarchicalStep<String> hierarchicalStep;

    @Mock
    private HierarchicalStep<String> parentStep;

    private SimpleStepState<String, String> state;

    @BeforeEach
    void setUp() {
        state = new SimpleStepState<>();
    }

    // region Basic getter/setter tests

    /**
     * Tests that a leaf step can be set and retrieved.
     */
    @Test
    void setLeafStep_shouldSetAndGetLeafStep() {
        // when
        state.setLeafStep(leafStep);

        // then
        assertEquals(leafStep, state.getLeafStep());
    }

    /**
     * Tests that a hierarchical step can be set and retrieved.
     */
    @Test
    void setHierarchicalStep_shouldSetAndGetHierarchicalStep() {
        // when
        state.setHierarchicalStep(hierarchicalStep);

        // then
        assertEquals(hierarchicalStep, state.getHierarchicalStep());
    }

    /**
     * Tests that the initial state has no leaf step.
     */
    @Test
    void getLeafStep_shouldReturnNullInitially() {
        // when
        LeafStep<String> result = state.getLeafStep();

        // then
        assertNull(result);
    }

    /**
     * Tests that the initial state has no hierarchical step.
     */
    @Test
    void getHierarchicalStep_shouldReturnNullInitially() {
        // when
        HierarchicalStep<String> result = state.getHierarchicalStep();

        // then
        assertNull(result);
    }

    // endregion

    // region isValid tests

    /**
     * Tests that the state is valid when there is no leaf step set.
     */
    @Test
    void isValid_shouldReturnTrue_whenNoLeafStepIsSet() {
        // when
        boolean result = state.isValid();

        // then
        assertTrue(result);
    }

    /**
     * Tests that the state is valid when the leaf step is valid.
     */
    @Test
    void isValid_shouldReturnTrue_whenLeafStepIsValid() {
        // given
        state.setLeafStep(leafStep);
        when(leafStep.isValid()).thenReturn(true);

        // when
        boolean result = state.isValid();

        // then
        assertTrue(result);
    }

    /**
     * Tests that the state is invalid when the leaf step is invalid.
     */
    @Test
    void isValid_shouldReturnFalse_whenLeafStepIsInvalid() {
        // given
        state.setLeafStep(leafStep);
        when(leafStep.isValid()).thenReturn(false);

        // when
        boolean result = state.isValid();

        // then
        assertFalse(result);
    }

    // endregion

    // region getValue tests

    /**
     * Tests that getValue returns empty when no leaf step is set.
     */
    @Test
    void getValue_shouldReturnEmpty_whenNoLeafStepIsSet() {
        // when
        Optional<String> result = state.getValue();

        // then
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that getValue returns the leaf step's value when a leaf step is set.
     */
    @Test
    void getValue_shouldReturnLeafStepValue_whenLeafStepIsSet() {
        // given
        String expectedValue = "test value";
        state.setLeafStep(leafStep);
        when(leafStep.getValue()).thenReturn(expectedValue);

        // when
        Optional<String> result = state.getValue();

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedValue, result.get());
    }

    // endregion

    // region getStatus tests

    /**
     * Tests that getStatus returns empty when no hierarchical step is set.
     */
    @Test
    void getStatus_shouldReturnEmpty_whenNoHierarchicalStepIsSet() {
        // when
        Optional<String> result = state.getStatus();

        // then
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that getStatus returns the hierarchical step's status when set.
     */
    @Test
    void getStatus_shouldReturnHierarchicalStepStatus_whenHierarchicalStepIsSet() {
        // given
        String expectedStatus = "test status";
        state.setHierarchicalStep(hierarchicalStep);
        when(hierarchicalStep.getStatus()).thenReturn(expectedStatus);

        // when
        Optional<String> result = state.getStatus();

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedStatus, result.get());
    }

    // endregion

    // region getName tests

    /**
     * Tests that getName returns empty when no leaf step is set.
     */
    @Test
    void getName_shouldReturnEmpty_whenNoLeafStepIsSet() {
        // when
        Optional<String> result = state.getName();

        // then
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that getName returns the leaf step's name when a leaf step is set.
     */
    @Test
    void getName_shouldReturnLeafStepName_whenLeafStepIsSet() {
        // given
        String expectedName = "test step";
        state.setLeafStep(leafStep);
        when(leafStep.getName()).thenReturn(expectedName);

        // when
        Optional<String> result = state.getName();

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedName, result.get());
    }

    // endregion

    // region completeLeafStep tests

    /**
     * Tests that completeLeafStep does nothing when no leaf step is set.
     */
    @Test
    void completeLeafStep_shouldDoNothing_whenNoLeafStepIsSet() {
        // when/then - should not throw exception
        assertDoesNotThrow(() -> state.completeLeafStep());
    }

    /**
     * Tests that completeLeafStep calls complete on the leaf step when set.
     */
    @Test
    void completeLeafStep_shouldCallCompleteOnLeafStep_whenLeafStepIsSet() {
        // given
        state.setLeafStep(leafStep);

        // when
        state.completeLeafStep();

        // then
        org.mockito.Mockito.verify(leafStep).complete();
    }

    // endregion

    // region getParent tests

    /**
     * Tests that getParent returns empty when no hierarchical step is set.
     */
    @Test
    void getParent_shouldReturnEmpty_whenNoHierarchicalStepIsSet() {
        // when
        Optional<HierarchicalStep<String>> result = state.getParent();

        // then
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that getParent returns empty when hierarchical step has no parent.
     */
    @Test
    void getParent_shouldReturnEmpty_whenHierarchicalStepHasNoParent() {
        // given
        state.setHierarchicalStep(hierarchicalStep);
        when(hierarchicalStep.getParent()).thenReturn(null);

        // when
        Optional<HierarchicalStep<String>> result = state.getParent();

        // then
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that getParent returns the parent when hierarchical step has one.
     */
    @Test
    void getParent_shouldReturnParent_whenHierarchicalStepHasParent() {
        // given
        state.setHierarchicalStep(hierarchicalStep);
        when(hierarchicalStep.getParent()).thenReturn(parentStep);

        // when
        Optional<HierarchicalStep<String>> result = state.getParent();

        // then
        assertTrue(result.isPresent());
        assertEquals(parentStep, result.get());
    }

    // endregion
}

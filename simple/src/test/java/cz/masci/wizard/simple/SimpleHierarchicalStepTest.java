package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.Step;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleHierarchicalStepTest {
    @Mock
    private Step step1;
    @Mock
    private Step step2;
    @Mock
    private Step step3;

    // region next tests
    @Test
    void next_returnsFirstStep_whenCalledFirstTime() {
        // given
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .addChild(step2)
                .build();

        // when
        var result = hierarchicalStep.next();

        // then
        assertSame(step1, result);
        assertEquals(0, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void next_returnsNextStep_whenCalledSubsequently() {
        // given
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .addChild(step2)
                .build();
        hierarchicalStep.next(); // Move to step1

        // when
        var result = hierarchicalStep.next();

        // then
        assertSame(step2, result);
        assertEquals(1, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void next_returnsNull_whenAtEndOfChildren() {
        // given
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .build();
        hierarchicalStep.next(); // Move to step1

        // when
        var result = hierarchicalStep.next();

        // then
        assertNull(result);
        assertEquals(1, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void next_skipsStep_whenSkipPredicateIsTrue() {
        // given
        BiPredicate<Integer, Step> skipPredicate = (idx, step) -> step == step2;
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .addChild(step2)
                .addChild(step3)
                .skipNextStepPredicate(skipPredicate)
                .build();
        hierarchicalStep.next(); // Move to step1

        // when
        var result = hierarchicalStep.next();

        // then
        assertSame(step3, result);
        assertEquals(2, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void next_callsDoBeforeEntryAndDoBeforeNext_onFirstCall() {
        // given
        var beforeEntryCalled = new AtomicBoolean(false);
        var beforeNextCalled = new AtomicBoolean(false);
        Consumer<SimpleHierarchicalStep<String>> doBeforeEntry = s -> beforeEntryCalled.set(true);
        Consumer<SimpleHierarchicalStep<String>> doBeforeNext = s -> beforeNextCalled.set(true);

        var hierarchicalStep = SimpleHierarchicalStep.<String>builder()
                .addChild(step1)
                .doBeforeEntry(doBeforeEntry)
                .doBeforeNext(doBeforeNext)
                .build();

        // when
        hierarchicalStep.next();

        // then
        assertTrue(beforeEntryCalled.get());
        assertTrue(beforeNextCalled.get());
    }

    // endregion

    // region prev tests
    @Test
    void prev_returnsPreviousStep() {
        // given
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .addChild(step2)
                .build();
        hierarchicalStep.next(); // -> step1
        hierarchicalStep.next(); // -> step2

        // when
        var result = hierarchicalStep.prev();

        // then
        assertSame(step1, result);
        assertEquals(0, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void prev_returnsNull_whenAtStartOfChildren() {
        // given
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .build();
        hierarchicalStep.next(); // -> step1

        // when
        var result = hierarchicalStep.prev();

        // then
        assertNull(result);
        assertEquals(-1, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void prev_skipsStep_whenSkipPredicateIsTrue() {
        // given
        BiPredicate<Integer, Step> skipPredicate = (idx, step) -> step == step2;
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .addChild(step2)
                .addChild(step3)
                .skipPrevStepPredicate(skipPredicate)
                .build();
        hierarchicalStep.next(); // -> step1
        hierarchicalStep.next(); // -> step2
        hierarchicalStep.next(); // -> step3

        // when
        var result = hierarchicalStep.prev();

        // then
        assertSame(step1, result);
        assertEquals(0, hierarchicalStep.getCurrentIdx());
    }

    @Test
    void prev_callsDoBeforePrev() {
        // given
        var beforePrevCalled = new AtomicBoolean(false);
        Consumer<SimpleHierarchicalStep<String>> doBeforePrev = s -> beforePrevCalled.set(true);

        var hierarchicalStep = SimpleHierarchicalStep.<String>builder()
                .addChild(step1)
                .addChild(step2)
                .doBeforePrev(doBeforePrev)
                .build();
        hierarchicalStep.next();
        hierarchicalStep.next();

        // when
        hierarchicalStep.prev();

        // then
        assertTrue(beforePrevCalled.get());
    }
    // endregion

    // region reset tests
    @Test
    void reset_resetsCurrentIndexAndChildHierarchies() {
        // given
        var childHierarchicalStep = spy(SimpleHierarchicalStep.builder().build());
        var childLeafStep = spy(SimpleLeafStep.<String>builder().build());
        var hierarchicalStep = SimpleHierarchicalStep.builder()
                .addChild(step1)
                .addChild(childHierarchicalStep)
                .addChild(childLeafStep)
                .build();
        hierarchicalStep.next();
        hierarchicalStep.next();
        hierarchicalStep.next();

        // when
        hierarchicalStep.reset();

        // then
        assertEquals(-1, hierarchicalStep.getCurrentIdx());
        verify(childHierarchicalStep, times(1)).reset();
        verify(childLeafStep, never()).complete();
    }
    // endregion

    // region addChild tests
    @Test
    void addChild_setsParentOnHierarchicalStep() {
        // given
        var parentStep = SimpleHierarchicalStep.builder().build();
        var childStep = SimpleHierarchicalStep.builder().build();

        // when
        parentStep.addChild(childStep);

        // then
        assertSame(parentStep, childStep.getParent());
    }
    // endregion

}
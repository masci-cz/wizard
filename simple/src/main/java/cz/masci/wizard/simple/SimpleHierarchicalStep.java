package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
import cz.masci.wizard.api.step.LeafStep;
import cz.masci.wizard.api.step.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * <pre>
 * A simple implementation of a hierarchical step in a wizard.
 * It can contain multiple child steps and manage navigation between them.
 *
 * It provides hooks to execute custom logic before entering the step,
 * before moving to the next step, and before moving to the previous step.
 *
 * There are also predicates to determine if certain steps should be skipped
 * when navigating forward or backward.
 *
 * The hook for entry is executed only once when the step is first accessed,
 * while the hooks for next and previous steps are executed each time the respective navigation occurs.
 * Also, the entry hook is executed for both directions when reaching the end or the beginning of the child steps.
 * </pre>
 */
public class SimpleHierarchicalStep<T> implements HierarchicalStep<T> {

    private static final BiPredicate<Integer, Step> NO_SKIP = (idx, step) -> false;

    protected final List<Step> children = new ArrayList<>();
    private final Consumer<SimpleHierarchicalStep<T>> doBeforeEntry;
    private final Consumer<SimpleHierarchicalStep<T>> doBeforeReverseEntry;
    private final Consumer<SimpleHierarchicalStep<T>> doBeforeNext;
    private final Consumer<SimpleHierarchicalStep<T>> doAfterNext;
    private final Consumer<SimpleHierarchicalStep<T>> doBeforePrev;
    private final Consumer<SimpleHierarchicalStep<T>> doAfterPrev;
    private final BiPredicate<Integer, Step> skipNextStepPredicate;
    private final BiPredicate<Integer, Step> skipPrevStepPredicate;
    private final BiPredicate<Integer, SimpleHierarchicalStep<T>> cancelNextStepPredicate;
    private final BiPredicate<Integer, SimpleHierarchicalStep<T>> cancelPrevStepPredicate;

    @Getter
    @Setter
    private HierarchicalStep<T> parent;
    @Getter
    private int currentIdx = -1;
    @Getter
    private final T status;

    @Builder
    public SimpleHierarchicalStep(
            @Singular("addChild") List<Step> children,
            T status,
            Consumer<SimpleHierarchicalStep<T>> doBeforeEntry,
            Consumer<SimpleHierarchicalStep<T>> doBeforeReverseEntry,
            Consumer<SimpleHierarchicalStep<T>> doBeforeNext,
            Consumer<SimpleHierarchicalStep<T>> doAfterNext,
            Consumer<SimpleHierarchicalStep<T>> doBeforePrev,
            Consumer<SimpleHierarchicalStep<T>> doAfterPrev,
            BiPredicate<Integer, Step> skipNextStepPredicate,
            BiPredicate<Integer, Step> skipPrevStepPredicate,
            BiPredicate<Integer, SimpleHierarchicalStep<T>> cancelNextStepPredicate,
            BiPredicate<Integer, SimpleHierarchicalStep<T>> cancelPrevStepPredicate
    ) {
        children.forEach(this::addChild);
        this.status = status;
        Consumer<SimpleHierarchicalStep<T>> NO_OP = step -> {
        };
        this.doBeforeEntry = Optional.ofNullable(doBeforeEntry).orElse(NO_OP);
        this.doBeforeReverseEntry = Optional.ofNullable(doBeforeReverseEntry).orElse(NO_OP);
        this.doBeforeNext = Optional.ofNullable(doBeforeNext).orElse(NO_OP);
        this.doAfterNext = Optional.ofNullable(doAfterNext).orElse(NO_OP);
        this.doBeforePrev = Optional.ofNullable(doBeforePrev).orElse(NO_OP);
        this.doAfterPrev = Optional.ofNullable(doAfterPrev).orElse(NO_OP);
        this.skipNextStepPredicate = Optional.ofNullable(skipNextStepPredicate).orElse(NO_SKIP);
        this.skipPrevStepPredicate = Optional.ofNullable(skipPrevStepPredicate).orElse(NO_SKIP);
        this.cancelNextStepPredicate = Optional.ofNullable(cancelNextStepPredicate).orElse((idx, step) -> false);
        this.cancelPrevStepPredicate = Optional.ofNullable(cancelPrevStepPredicate).orElse((idx, step) -> false);
    }

    /**
     * Returns the next step in the hierarchy, taking into account any steps that should be skipped.
     * <pre>
     *     During the navigation some hooks are executed:
     *     - doBeforeEntry: executed once when navigating back into the step from the end
     *     - cancelNextStepPredicate: predicate to determine if a step should be canceled when moving forwards
     *       other hooks are executed except the skipNextStepPredicate and the current step is not changed
     *     - doBeforeNext: executed before moving to the next step
     *     - skipNextStepPredicate: predicate to determine if a step should be skipped when moving forwards
     *     - doAfterNext: executed after moving to the next step
     * </pre>
     *
     * @return the next Step, or null if there are no more steps
     */
    @Override
    public Step next() {
        // do before entry if it is the first step
        if (currentIdx == -1) {
            doBeforeEntry.accept(this);
        }
        // do before next step
        var cancelStep = cancelNextStepPredicate.test(currentIdx, this);
        doBeforeNext.accept(this);
        if (!cancelStep) {
            do {
                // increment the current index
                currentIdx++;
                // set the current index to size if we are past the last element
                if (currentIdx > children.size()) {
                    currentIdx = children.size();
                }
                // skip steps that should be skipped
            } while (isValidCurrentIdx() && skipNextStepPredicate.test(currentIdx, children.get(currentIdx)));
        }
        // return the next step or current when cancel step is true
        var next = isValidCurrentIdx() ? children.get(currentIdx) : null;
        doAfterNext.accept(this);
        return next;
    }

    /**
     * Returns the previous step in the hierarchy, taking into account any steps that should be skipped.
     * <pre>
     *     During the navigation some hooks are executed:
     *     - doBeforeReverseEntry: executed once when navigating back into the step from the end
     *     - cancelPrevStepPredicate: predicate to determine if a step should be canceled when moving backwards
     *       other hooks are executed except the skipPrevStepPredicate and the current step is not changed
     *     - doBeforePrev: executed before moving to the previous step
     *     - skipPrevStepPredicate: predicate to determine if a step should be skipped when moving backwards
     *     - doAfterPrev: executed after moving to the previous step
     * </pre>
     *
     * @return the previous Step, or null if there are no previous steps
     */
    @Override
    public Step prev() {
        // do before reverse entry if it is the last step
        if (currentIdx == children.size()) {
            doBeforeReverseEntry.accept(this);
        }
        // do before prev step
        // TODO GoTo step has to be propagated to StepManager but event then there is a problem with stepping into hierarchical step moving backward
        var cancelStep = cancelPrevStepPredicate.test(currentIdx, this);
        doBeforePrev.accept(this);
        if (!cancelStep) {
            do {
                // decrement the current index
                currentIdx--;
                // set the current index to -1 if we are before the first element
                if (currentIdx < -1) {
                    currentIdx = -1;
                }
                // skip steps that should be skipped
            } while (isValidCurrentIdx() && skipPrevStepPredicate.test(currentIdx, children.get(currentIdx)));
        }
        // return the prev step or current when cancel step is true
        var prev = isValidCurrentIdx() ? children.get(currentIdx) : null;
        doAfterPrev.accept(this);
        return prev;
    }

    /**
     * Resets the hierarchical step and all its child hierarchical steps to their initial state.</br>
     * Returns the hierarchical step to before the first child step.
     */
    @Override
    public void reset() {
        rewindTo(-1);
    }

    @Override
    public void rewind() {
        rewindTo(0);
    }

    /**
     * Adds a child step to the hierarchical step.
     * If the added step is a hierarchical step, its parent is set to this step.
     *
     * @param step the child step to add
     */
    public void addChild(Step step) {
        children.add(step);
        if (step instanceof HierarchicalStep<?>) {
            @SuppressWarnings("unchecked") var hierarchicalStep = (HierarchicalStep<T>) step;
            hierarchicalStep.setParent(this);
        }
    }

    public <S extends LeafStep<?>> Optional<S> getCurrentLeafStep(Class<S> clazz) {
        Optional<S> leafStep = Optional.empty();

        if (isValidCurrentIdx()) {
            var step = children.get(currentIdx);
            if (clazz.isAssignableFrom(step.getClass())) {
                leafStep = Optional.of(clazz.cast(step));
            } else if (step instanceof SimpleHierarchicalStep<?> hierarchicalStep) {
                leafStep = hierarchicalStep.getCurrentLeafStep(clazz);
            }
        }

        return leafStep;
    }

    /**
     * Clears all child steps from the hierarchical step.
     */
    public void clearChildren() {
        children.clear();
    }

    /**
     * Returns the number of child steps in the hierarchical step.
     *
     * @return the number of child steps
     */
    public int getChildrenCount() {
        return children.size();
    }

    public boolean isFirstStep() {
        return currentIdx <= 0;
    }

    public boolean isLastStep() {
        return currentIdx >= children.size() - 1;
    }

    public void setCurrentIdx(int idx) {
        if (idx < -1 || idx > children.size()) {
            throw new IndexOutOfBoundsException("Index: " + idx + ", Size: " + children.size());
        }
        this.currentIdx = idx;
    }

    private boolean isValidCurrentIdx() {
        return currentIdx >= 0 && currentIdx < children.size();
    }

    private void rewindTo(int idx) {
        // the current index could be already at the end of the list and therefor higher than the size
        for (int i = currentIdx >= children.size() ? currentIdx - 1 : currentIdx; i >= idx; i--) {
            if (i >= 0) {
                var step = children.get(i);
                if (step instanceof HierarchicalStep<?> hierarchicalStep) {
                    hierarchicalStep.reset();
                }
            }
        }
        currentIdx = idx;
    }
}

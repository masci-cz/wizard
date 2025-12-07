package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.HierarchicalStep;
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
public class SimpleHierarchicalStep implements HierarchicalStep {

    private static final Consumer<SimpleHierarchicalStep> NO_OP = step -> {
    };
    private static final BiPredicate<Integer, Step> NO_SKIP = (idx, step) -> false;

    private final List<Step> children = new ArrayList<>();
    private final Consumer<SimpleHierarchicalStep> doBeforeEntry;
    private final Consumer<SimpleHierarchicalStep> doBeforeNext;
    private final Consumer<SimpleHierarchicalStep> doBeforePrev;
    private final BiPredicate<Integer, Step> skipNextStepPredicate;
    private final BiPredicate<Integer, Step> skipPrevStepPredicate;

    @Getter
    @Setter
    private HierarchicalStep parent;
    @Getter
    private int currentIdx = -1;

    @Builder
    private SimpleHierarchicalStep(
            @Singular("addChild") List<Step> children,
            Consumer<SimpleHierarchicalStep> doBeforeEntry,
            Consumer<SimpleHierarchicalStep> doBeforeNext,
            Consumer<SimpleHierarchicalStep> doBeforePrev,
            BiPredicate<Integer, Step> skipNextStepPredicate,
            BiPredicate<Integer, Step> skipPrevStepPredicate
    ) {
        children.forEach(this::addChild);
        this.doBeforeEntry = Optional.ofNullable(doBeforeEntry).orElse(NO_OP);
        this.doBeforeNext = Optional.ofNullable(doBeforeNext).orElse(NO_OP);
        this.doBeforePrev = Optional.ofNullable(doBeforePrev).orElse(NO_OP);
        this.skipNextStepPredicate = Optional.ofNullable(skipNextStepPredicate).orElse(NO_SKIP);
        this.skipPrevStepPredicate = Optional.ofNullable(skipPrevStepPredicate).orElse(NO_SKIP);
    }

    /**
     * Returns the next step in the hierarchy, taking into account any steps that should be skipped.
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
        doBeforeNext.accept(this);
        // increment the current index
        // skip steps that should be skipped
        do {
            currentIdx++;
        } while (isValidCurrentIdx() && skipNextStepPredicate.test(currentIdx, children.get(currentIdx)));
        // return the next step
        return isValidCurrentIdx() ? children.get(currentIdx) : null;
    }

    /**
     * Returns the previous step in the hierarchy, taking into account any steps that should be skipped.
     *
     * @return the previous Step, or null if there are no previous steps
     */
    @Override
    public Step prev() {
        // do before entry if it is the last step
        if (currentIdx == children.size()) {
            doBeforeEntry.accept(this);
        }
        // do before next step
        doBeforePrev.accept(this);
        // increment the current index
        // skip steps that should be skipped
        do {
            currentIdx--;
        } while (isValidCurrentIdx() && skipPrevStepPredicate.test(currentIdx, children.get(currentIdx)));
        // return the next step
        return isValidCurrentIdx() ? children.get(currentIdx) : null;
    }

    /**
     * Resets the hierarchical step and all its child hierarchical steps to their initial state.</br>
     * Returns the hierarchical step to before the first child step.
     */
    @Override
    public void reset() {
        // the current index could be already at the end of the list and therefor higher than the size
        for (int i = currentIdx >= children.size() ? currentIdx - 1 : currentIdx; i >= 0; i--) {
            var step = children.get(i);
            if (step instanceof HierarchicalStep hierarchicalStep) {
                hierarchicalStep.reset();
            }
        }
        currentIdx = -1;
    }

    /**
     * Adds a child step to the hierarchical step.
     * If the added step is a hierarchical step, its parent is set to this step.
     *
     * @param step the child step to add
     */
    public void addChild(Step step) {
        children.add(step);
        if (step instanceof HierarchicalStep hierarchicalStep) {
            hierarchicalStep.setParent(this);
        }
    }

    /**
     * Clears all child steps from the hierarchical step.
     */
    public void clearChildren() {
        children.clear();
    }

    private boolean isValidCurrentIdx() {
        return currentIdx >= 0 && currentIdx < children.size();
    }
}

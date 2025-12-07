package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.LeafStep;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A simple implementation of the LeafStep interface.
 *
 * @param <T> the type of the value held by the leaf step
 */
@Builder
public class SimpleLeafStep<T> implements LeafStep<T> {
    @Getter
    private final T value;
    @Getter
    private final String name;
    private final Predicate<T> validator;
    private final Consumer<SimpleLeafStep<T>> complete;

    /**
     * The step is valid when the validator predicate returns true for the current value.
     *
     * @return true if the step is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        if (validator != null) {
            return validator.test(value);
        }
        return LeafStep.super.isValid();
    }

    /**
     * Complete the current leaf step by invoking the complete consumer if it is set.
     */
    @Override
    public void complete() {
        if (complete != null) {
            complete.accept(this);
        }
    }
}

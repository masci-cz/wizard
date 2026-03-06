package cz.masci.wizard.simple;

import cz.masci.wizard.api.step.LeafStep;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A simple implementation of the LeafStep interface.
 *
 * <p>Instances are created via the Lombok-generated builder: {@code SimpleLeafStep.builder()...build()}.
 * All fields are set through the builder; validation and completion behaviour are provided via
 * the optional {@code validator}, {@code complete} and {@code cancel} callbacks.</p>
 *
 * @param <T> the type of the value held by the leaf step
 */
public class SimpleLeafStep<T> implements LeafStep<T> {
    @Getter
    private final T value;
    @Getter
    private final String name;
    private final Predicate<SimpleLeafStep<T>> validator;
    private final Consumer<SimpleLeafStep<T>> complete;
    private final Consumer<SimpleLeafStep<T>> cancel;

    /**
     * All-args constructor used by the Lombok-generated builder.
     *
     * @param value     the value associated with this step
     * @param name      the display name of this step
     * @param validator optional predicate to determine step validity
     * @param complete  optional consumer invoked when the step is completed
     * @param cancel    optional consumer invoked when the step is cancelled
     */
    @Builder
    private SimpleLeafStep(
            T value,
            String name,
            Predicate<SimpleLeafStep<T>> validator,
            Consumer<SimpleLeafStep<T>> complete,
            Consumer<SimpleLeafStep<T>> cancel
    ) {
        this.value = value;
        this.name = name;
        this.validator = validator;
        this.complete = complete;
        this.cancel = cancel;
    }

    /**
     * The step is valid when the validator predicate returns true for the current value.
     *
     * @return true if the step is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        if (validator != null) {
            return validator.test(this);
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

    /**
     * Cancel the current leaf step by invoking the cancel consumer if it is set.
     */
    @Override
    public void cancel() {
        if (cancel != null) {
            cancel.accept(this);
        }
    }
}

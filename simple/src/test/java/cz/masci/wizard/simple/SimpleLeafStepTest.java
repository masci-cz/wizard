package cz.masci.wizard.simple;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class SimpleLeafStepTest {

    @Test
    void isValid_returnsTrue_whenValidatorIsNull() {
        // given
        SimpleLeafStep<String> step = SimpleLeafStep.<String>builder()
                .value("value")
                .validator(null)
                .build();

        // when
        boolean result = step.isValid();

        // then
        assertTrue(result);
    }

    @Test
    void isValid_returnsTrue_whenValidatorPasses() {
        // given
        Predicate<String> validator = value -> value.length() > 3;
        SimpleLeafStep<String> step = SimpleLeafStep.<String>builder()
                .value("valid")
                .validator(validator)
                .build();

        // when
        boolean result = step.isValid();

        // then
        assertTrue(result);
    }

    @Test
    void isValid_returnsFalse_whenValidatorFails() {
        // given
        Predicate<String> validator = value -> value.length() > 5;
        SimpleLeafStep<String> step = SimpleLeafStep.<String>builder()
                .value("short")
                .validator(validator)
                .build();

        // when
        boolean result = step.isValid();

        // then
        assertFalse(result);
    }

    @Test
    void complete_invokesConsumer_whenSet() {
        // given
        AtomicBoolean consumerCalled = new AtomicBoolean(false);
        Consumer<SimpleLeafStep<String>> consumer = s -> consumerCalled.set(true);
        SimpleLeafStep<String> step = SimpleLeafStep.<String>builder()
                .complete(consumer)
                .build();

        // when
        step.complete();

        // then
        assertTrue(consumerCalled.get());
    }

    @Test
    void complete_doesNothing_whenConsumerIsNull() {
        // given
        SimpleLeafStep<String> step = SimpleLeafStep.<String>builder()
                .complete(null)
                .build();

        // when & then
        assertDoesNotThrow(step::complete);
    }
}
package cz.masci.wizard.simple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

}
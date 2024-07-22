package com.ui.ubiquitiassignment.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MacAddressValidatorTest {

    @InjectMocks
    private MacAddressValidator macAddressValidator;

    @Mock
    private ConstraintValidatorContext context;

    @ParameterizedTest
    @ValueSource(strings = {"01:23:45:67:89:AB", "01-23-45-67-89-AB", ""})
    void testValidMacAddress(String macAddress) {
        assertTrue(macAddressValidator.isValid(macAddress, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"01:23:45:67:89:ZZ", "01:23:45:67:89", "01:23:45:67:89:AB:CD", "0123.4567.89AB"})
    void testInvalidMacAddress(String macAddress) {
        assertFalse(macAddressValidator.isValid(macAddress, context));
    }

}

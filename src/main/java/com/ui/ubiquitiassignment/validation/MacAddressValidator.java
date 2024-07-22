package com.ui.ubiquitiassignment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Validator class for MAC addresses.
 * This validator checks if the provided MAC address is valid. It returns true if the MAC address
 * is either empty or matches the specified pattern.

 * The MAC address pattern used is {@code ^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$}, which allows for
 * MAC addresses in the formats like {@code 00:11:22:33:44:55} or {@code 00-11-22-33-44-55}.
 */
public class MacAddressValidator implements ConstraintValidator<MacAddress, String> {

    private static final String MAC_ADDRESS_PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

    @Override
    public void initialize(MacAddress constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !StringUtils.hasText(value) || Pattern.matches(MAC_ADDRESS_PATTERN, value);
    }

}

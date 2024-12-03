/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import java.util.List;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public final class PropertyValidator
implements Validator<Property> {
    public static final String ASSERT_NONE_MESSAGE = "Property [{0}] is not applicable";
    public static final String ASSERT_ONE_OR_LESS_MESSAGE = "Property [{0}] must only be specified once";
    public static final String ASSERT_ONE_MESSAGE = "Property [{0}] must be specified once";
    public static final String ASSERT_ONE_OR_MORE_MESSAGE = "Property [{0}] must be specified at least once";
    private final List<ValidationRule> rules;

    public PropertyValidator(List<ValidationRule> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(Property target) throws ValidationException {
        for (ValidationRule rule : this.rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") && rule.isRelaxedModeSupported();
            switch (rule.getType()) {
                case None: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getParameter((String)s) != null, "Parameter [{0}] is not applicable", warnOnly, target.getParameters(), s));
                    break;
                }
                case One: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getParameters((String)s).size() != 1, "Parameter [{0}] must be specified once", warnOnly, target.getParameters(), s));
                    break;
                }
                case OneOrLess: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getParameters((String)s).size() > 1, "Parameter [{0}] must only be specified once", warnOnly, target.getParameters(), s));
                }
            }
        }
    }

    public static void assertOneOrLess(String propertyName, PropertyList properties) throws ValidationException {
        Validator.assertFalse(input -> input.getProperties(propertyName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false, properties, propertyName);
    }

    public static void assertOneOrMore(String propertyName, PropertyList properties) throws ValidationException {
        Validator.assertFalse(input -> input.getProperties(propertyName).size() < 1, ASSERT_ONE_OR_MORE_MESSAGE, false, properties, propertyName);
    }

    public static void assertOne(String propertyName, PropertyList properties) throws ValidationException {
        Validator.assertFalse(input -> input.getProperties(propertyName).size() != 1, ASSERT_ONE_MESSAGE, false, properties, propertyName);
    }

    public static void assertNone(String propertyName, PropertyList properties) throws ValidationException {
        Validator.assertFalse(input -> input.getProperty(propertyName) != null, ASSERT_NONE_MESSAGE, false, properties, propertyName);
    }
}


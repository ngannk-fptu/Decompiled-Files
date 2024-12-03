/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import java.util.Arrays;
import java.util.List;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class ComponentValidator<T extends Component>
implements Validator<T> {
    private static final String ASSERT_NONE_MESSAGE = "Component [{0}] is not applicable";
    private static final String ASSERT_ONE_OR_LESS_MESSAGE = "Component [{0}] must only be specified once";
    private final List<ValidationRule> rules;

    public ComponentValidator(ValidationRule ... rules) {
        this.rules = Arrays.asList(rules);
    }

    @Override
    public void validate(T target) throws ValidationException {
        for (ValidationRule rule : this.rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") && rule.isRelaxedModeSupported();
            switch (rule.getType()) {
                case None: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getProperty((String)s) != null, "Property [{0}] is not applicable", warnOnly, target.getProperties(), s));
                    break;
                }
                case One: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getProperties((String)s).size() != 1, "Property [{0}] must be specified once", warnOnly, target.getProperties(), s));
                    break;
                }
                case OneOrLess: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getProperties((String)s).size() > 1, "Property [{0}] must only be specified once", warnOnly, target.getProperties(), s));
                    break;
                }
                case OneOrMore: {
                    rule.getInstances().forEach(s -> Validator.assertFalse(input -> input.getProperties((String)s).size() < 1, "Property [{0}] must be specified at least once", warnOnly, target.getProperties(), s));
                }
            }
        }
    }

    public static void assertNone(String componentName, ComponentList<?> components) throws ValidationException {
        Validator.assertFalse(input -> input.getComponent(componentName) != null, ASSERT_NONE_MESSAGE, false, components, componentName);
    }

    public static void assertOneOrLess(String componentName, ComponentList<?> components) throws ValidationException {
        Validator.assertFalse(input -> input.getComponents(componentName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false, components, componentName);
    }
}


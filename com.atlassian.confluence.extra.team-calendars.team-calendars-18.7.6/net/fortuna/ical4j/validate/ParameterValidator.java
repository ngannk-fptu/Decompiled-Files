/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

public final class ParameterValidator {
    public static final String ASSERT_NONE_MESSAGE = "Parameter [{0}] is not applicable";
    public static final String ASSERT_ONE_OR_LESS_MESSAGE = "Parameter [{0}] must only be specified once";
    public static final String ASSERT_ONE_MESSAGE = "Parameter [{0}] must be specified once";
    private static final String ASSERT_NULL_OR_EQUAL_MESSAGE = "Parameter [{0}] is invalid";

    private ParameterValidator() {
    }

    public static void assertOneOrLess(String paramName, ParameterList parameters) throws ValidationException {
        Validator.assertFalse(parameters1 -> parameters1.getParameters(paramName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false, parameters, paramName);
    }

    public static void assertOne(String paramName, ParameterList parameters) throws ValidationException {
        Validator.assertFalse(parameters1 -> parameters1.getParameters(paramName).size() != 1, ASSERT_ONE_MESSAGE, false, parameters, paramName);
    }

    public static void assertNone(String paramName, ParameterList parameters) throws ValidationException {
        Validator.assertFalse(parameters1 -> parameters1.getParameter(paramName) != null, ASSERT_NONE_MESSAGE, false, parameters, paramName);
    }

    public static void assertNullOrEqual(Parameter param, ParameterList parameters) throws ValidationException {
        Object p = parameters.getParameter(param.getName());
        if (p != null && !param.equals(p)) {
            throw new ValidationException(ASSERT_NULL_OR_EQUAL_MESSAGE, new Object[]{p});
        }
    }
}


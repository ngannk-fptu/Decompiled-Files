/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class GenericValidator
implements Serializable {
    private static final long serialVersionUID = -7212095066891517618L;
    private static final UrlValidator URL_VALIDATOR = new UrlValidator();
    private static final CreditCardValidator CREDIT_CARD_VALIDATOR = new CreditCardValidator();

    public static boolean isBlankOrNull(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean matchRegexp(String value, String regexp) {
        if (regexp == null || regexp.length() <= 0) {
            return false;
        }
        return Pattern.matches(regexp, value);
    }

    public static boolean isByte(String value) {
        return GenericTypeValidator.formatByte(value) != null;
    }

    public static boolean isShort(String value) {
        return GenericTypeValidator.formatShort(value) != null;
    }

    public static boolean isInt(String value) {
        return GenericTypeValidator.formatInt(value) != null;
    }

    public static boolean isLong(String value) {
        return GenericTypeValidator.formatLong(value) != null;
    }

    public static boolean isFloat(String value) {
        return GenericTypeValidator.formatFloat(value) != null;
    }

    public static boolean isDouble(String value) {
        return GenericTypeValidator.formatDouble(value) != null;
    }

    public static boolean isDate(String value, Locale locale) {
        return DateValidator.getInstance().isValid(value, locale);
    }

    public static boolean isDate(String value, String datePattern, boolean strict) {
        return org.apache.commons.validator.DateValidator.getInstance().isValid(value, datePattern, strict);
    }

    public static boolean isInRange(byte value, byte min, byte max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(float value, float min, float max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(short value, short min, short max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static boolean isCreditCard(String value) {
        return CREDIT_CARD_VALIDATOR.isValid(value);
    }

    public static boolean isEmail(String value) {
        return EmailValidator.getInstance().isValid(value);
    }

    public static boolean isUrl(String value) {
        return URL_VALIDATOR.isValid(value);
    }

    public static boolean maxLength(String value, int max) {
        return value.length() <= max;
    }

    public static boolean maxLength(String value, int max, int lineEndLength) {
        int adjustAmount = GenericValidator.adjustForLineEnding(value, lineEndLength);
        return value.length() + adjustAmount <= max;
    }

    public static boolean minLength(String value, int min) {
        return value.length() >= min;
    }

    public static boolean minLength(String value, int min, int lineEndLength) {
        int adjustAmount = GenericValidator.adjustForLineEnding(value, lineEndLength);
        return value.length() + adjustAmount >= min;
    }

    private static int adjustForLineEnding(String value, int lineEndLength) {
        int nCount = 0;
        int rCount = 0;
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == '\n') {
                ++nCount;
            }
            if (value.charAt(i) != '\r') continue;
            ++rCount;
        }
        return nCount * lineEndLength - (rCount + nCount);
    }

    public static boolean minValue(int value, int min) {
        return value >= min;
    }

    public static boolean minValue(long value, long min) {
        return value >= min;
    }

    public static boolean minValue(double value, double min) {
        return value >= min;
    }

    public static boolean minValue(float value, float min) {
        return value >= min;
    }

    public static boolean maxValue(int value, int max) {
        return value <= max;
    }

    public static boolean maxValue(long value, long max) {
        return value <= max;
    }

    public static boolean maxValue(double value, double max) {
        return value <= max;
    }

    public static boolean maxValue(float value, float max) {
        return value <= max;
    }
}


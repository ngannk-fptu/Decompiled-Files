/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Deprecated
public class DateValidator {
    private static final DateValidator DATE_VALIDATOR = new DateValidator();

    public static DateValidator getInstance() {
        return DATE_VALIDATOR;
    }

    protected DateValidator() {
    }

    public boolean isValid(String value, String datePattern, boolean strict) {
        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);
        try {
            formatter.parse(value);
        }
        catch (ParseException e) {
            return false;
        }
        return !strict || datePattern.length() == value.length();
    }

    public boolean isValid(String value, Locale locale) {
        if (value == null) {
            return false;
        }
        DateFormat formatter = null;
        formatter = locale != null ? DateFormat.getDateInstance(3, locale) : DateFormat.getDateInstance(3, Locale.getDefault());
        formatter.setLenient(false);
        try {
            formatter.parse(value);
        }
        catch (ParseException e) {
            return false;
        }
        return true;
    }
}


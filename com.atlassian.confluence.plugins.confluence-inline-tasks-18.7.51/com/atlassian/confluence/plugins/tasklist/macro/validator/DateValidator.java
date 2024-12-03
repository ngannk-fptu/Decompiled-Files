/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTimeFieldType
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.DateTimeFormatterBuilder
 */
package com.atlassian.confluence.plugins.tasklist.macro.validator;

import com.atlassian.confluence.plugins.tasklist.macro.validator.AbstractValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.ValidatedErrorType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class DateValidator
extends AbstractValidator {
    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().appendDecimal(DateTimeFieldType.dayOfMonth(), 1, 2).appendLiteral("-").appendDecimal(DateTimeFieldType.monthOfYear(), 1, 2).appendLiteral("-").appendFixedDecimal(DateTimeFieldType.year(), 4).toFormatter();

    public DateValidator(String fieldName, String input) {
        super(fieldName);
        this.input = StringUtils.isBlank((CharSequence)input) ? "" : input;
    }

    @Override
    public boolean validate() {
        boolean result = true;
        try {
            if (StringUtils.isNotBlank((CharSequence)this.input)) {
                DATE_FORMAT.parseDateTime(this.input);
            }
        }
        catch (IllegalArgumentException e) {
            this.error = new ValidatedErrorType(this.fieldNameCode, "com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.date.error");
            result = false;
        }
        return result;
    }
}


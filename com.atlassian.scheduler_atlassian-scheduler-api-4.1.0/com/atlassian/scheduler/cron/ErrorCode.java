/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.cron;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum ErrorCode {
    COMMA_WITH_LAST_DOM("You cannot use 'L' or 'W' with multiple day-of-month values."),
    COMMA_WITH_LAST_DOW("You cannot use 'L' with multiple day-of-week values."),
    COMMA_WITH_NTH_DOW("You cannot use '#' with multiple day-of-week values."),
    COMMA_WITH_WEEKDAY_DOM("You cannot use 'W' with multiple day-of-month values."),
    INTERNAL_PARSER_FAILURE("Internal parser failure: ", ""),
    ILLEGAL_CHARACTER("Unexpected character: '", "'"),
    ILLEGAL_CHARACTER_AFTER_QM("Illegal character after '?': '", "'"),
    ILLEGAL_CHARACTER_AFTER_HASH("A numeric value between 1 and 5 must follow the '#' option."),
    ILLEGAL_CHARACTER_AFTER_INTERVAL("Illegal character after '/': '", "'"),
    INVALID_STEP("The step interval character '/' must be followed by a positive integer."),
    INVALID_STEP_DAY_OF_MONTH("The step interval for day-of-month must be less than 31: ", ""),
    INVALID_STEP_DAY_OF_WEEK("The step interval for day-of-week must be less than 7: ", ""),
    INVALID_STEP_HOUR("The step interval for hour must be less than 24: ", ""),
    INVALID_STEP_MONTH("The step interval for month must be less than 12: ", ""),
    INVALID_STEP_SECOND_OR_MINUTE("The step interval for second or minute must be less than 60: ", ""),
    INVALID_NAME("Invalid name: '", "'"),
    INVALID_NAME_FIELD("This field does not support names: '", "'"),
    INVALID_NAME_RANGE("Cannot specify a range using month or day-of-week names unless a valid name is used for both bounds."),
    INVALID_NAME_MONTH("Invalid month name: '", "'"),
    INVALID_NAME_DAY_OF_WEEK("Invalid day-of-week name: '", "'"),
    INVALID_NUMBER_SEC_OR_MIN("The values for seconds and minutes must be from 0 to 59."),
    INVALID_NUMBER_HOUR("The values for hours must be from 0 to 23."),
    INVALID_NUMBER_DAY_OF_MONTH("The values for day-of-month must be from 1 to 31."),
    INVALID_NUMBER_DAY_OF_MONTH_OFFSET("The offset from the last day day of the month must be no more than 30."),
    INVALID_NUMBER_MONTH("The values for month must be from 1 to 12."),
    INVALID_NUMBER_DAY_OF_WEEK("The values for day-of-week must be from 1 to 7."),
    INVALID_NUMBER_YEAR("The values for year must be from 1970 to 2299."),
    INVALID_NUMBER_YEAR_RANGE("Year ranges must specify the earlier year first."),
    QM_CANNOT_USE_HERE("You can only use '?' for the day-of-month or the day-of-week."),
    QM_CANNOT_USE_FOR_BOTH_DAYS("You cannot specify '?' for both the day-of-month and the day-of-week."),
    QM_MUST_USE_FOR_ONE_OF_DAYS("You must use '?' for either the day-of-month or day-of-week."),
    UNEXPECTED_TOKEN_FLAG_L("The 'L' option was used incorrectly."),
    UNEXPECTED_TOKEN_FLAG_W("The 'W' option was used incorrectly."),
    UNEXPECTED_TOKEN_HASH("The '#' option was used incorrectly."),
    UNEXPECTED_TOKEN_HYPHEN("Ranges specified with '-' must have both a starting and ending value."),
    UNEXPECTED_END_OF_EXPRESSION("Unexpected end of expression");

    private final String message;
    private final String suffix;

    private ErrorCode(String message) {
        this(message, null);
    }

    private ErrorCode(String message, String suffix) {
        this.message = message;
        this.suffix = suffix;
    }

    @Nonnull
    public String toMessage(@Nullable String value) {
        if (this.suffix != null) {
            return this.message + value + this.suffix;
        }
        return this.message;
    }
}


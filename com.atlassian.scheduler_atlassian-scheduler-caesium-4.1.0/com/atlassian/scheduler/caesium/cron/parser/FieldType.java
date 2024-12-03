/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.cron.ErrorCode
 */
package com.atlassian.scheduler.caesium.cron.parser;

import com.atlassian.scheduler.caesium.cron.parser.CronLexer;
import com.atlassian.scheduler.caesium.cron.parser.NameResolver;
import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.FieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.RangeFieldRule;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.cron.ErrorCode;

enum FieldType {
    SECOND(ErrorCode.INVALID_NUMBER_SEC_OR_MIN, ErrorCode.INVALID_STEP_SECOND_OR_MINUTE, DateTimeTemplate.Field.SECOND),
    MINUTE(ErrorCode.INVALID_NUMBER_SEC_OR_MIN, ErrorCode.INVALID_STEP_SECOND_OR_MINUTE, DateTimeTemplate.Field.MINUTE),
    HOUR(ErrorCode.INVALID_NUMBER_HOUR, ErrorCode.INVALID_STEP_HOUR, DateTimeTemplate.Field.HOUR),
    DAY_OF_MONTH(ErrorCode.INVALID_NUMBER_DAY_OF_MONTH, ErrorCode.INVALID_STEP_DAY_OF_MONTH, DateTimeTemplate.Field.DAY),
    MONTH(ErrorCode.INVALID_NUMBER_MONTH, ErrorCode.INVALID_STEP_MONTH, DateTimeTemplate.Field.MONTH){

        @Override
        int resolveName(CronLexer.Token token) throws CronSyntaxException {
            return NameResolver.MONTH.resolveName(token);
        }
    }
    ,
    DAY_OF_WEEK(ErrorCode.INVALID_NUMBER_DAY_OF_WEEK, ErrorCode.INVALID_STEP_DAY_OF_WEEK, DateTimeTemplate.Field.DAY){

        @Override
        int resolveName(CronLexer.Token token) throws CronSyntaxException {
            return NameResolver.DAY_OF_WEEK.resolveName(token);
        }

        @Override
        int getMaximumValue() {
            return 7;
        }
    }
    ,
    YEAR(ErrorCode.INVALID_NUMBER_YEAR, ErrorCode.INVALID_STEP, DateTimeTemplate.Field.YEAR);

    private final ErrorCode valueErrorCode;
    private final ErrorCode stepErrorCode;
    private final DateTimeTemplate.Field field;
    private final FieldRule all;

    private FieldType(ErrorCode valueErrorCode, ErrorCode stepErrorCode, DateTimeTemplate.Field field) {
        this.valueErrorCode = valueErrorCode;
        this.stepErrorCode = stepErrorCode;
        this.field = field;
        this.all = RangeFieldRule.of(field, this.getMinimumValue(), this.getMaximumValue());
    }

    int getMinimumValue() {
        return this.field.getMinimumValue();
    }

    int getMaximumValue() {
        return this.field.getMaximumValue();
    }

    int getWrapOffset() {
        return this.getMaximumValue() - this.getMinimumValue() + 1;
    }

    ErrorCode getValueErrorCode() {
        return this.valueErrorCode;
    }

    ErrorCode getStepErrorCode() {
        return this.stepErrorCode;
    }

    DateTimeTemplate.Field getField() {
        return this.field;
    }

    int resolveName(CronLexer.Token token) throws CronSyntaxException {
        String name = token.getText();
        ErrorCode errorCode = name.length() >= 3 ? ErrorCode.INVALID_NAME_FIELD : ErrorCode.INVALID_NAME;
        throw CronSyntaxException.builder().cronExpression(token.getCronExpression()).errorCode(errorCode).errorOffset(token.getStart()).value(name).build();
    }

    FieldRule all() {
        return this.all;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.cron.CronSyntaxException$Builder
 *  com.atlassian.scheduler.cron.ErrorCode
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.cron.parser;

import com.atlassian.scheduler.caesium.cron.parser.CronLexer;
import com.atlassian.scheduler.caesium.cron.parser.FieldType;
import com.atlassian.scheduler.caesium.cron.parser.TokenType;
import com.atlassian.scheduler.caesium.cron.rule.CronExpression;
import com.atlassian.scheduler.caesium.cron.rule.field.BitSetFieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.DayOfWeekFieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.FieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.SpecialDayOfMonthFieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.SpecialDayOfWeekLastFieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.SpecialDayOfWeekNthFieldRule;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.cron.ErrorCode;
import java.util.BitSet;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CronExpressionParser {
    private final String cronExpression;
    private final CronLexer lexer;
    private FieldRule secondField;
    private FieldRule minuteField;
    private FieldRule hourField;
    private FieldRule monthField;
    private FieldRule dayField;
    private FieldRule yearField;
    private FieldType fieldType;
    private final BitSet values = new BitSet(64);

    public static CronExpression parse(String cronExpression) throws CronSyntaxException {
        return new CronExpressionParser(cronExpression).parseCronExpression();
    }

    public static boolean isValid(String cronExpression) {
        try {
            CronExpressionParser.parse(cronExpression);
            return true;
        }
        catch (CronSyntaxException cse) {
            return false;
        }
    }

    private CronExpressionParser(String cronExpression) {
        String toUpper;
        this.cronExpression = toUpper = Objects.requireNonNull(cronExpression, "cronExpression").toUpperCase(Locale.US);
        this.lexer = new CronLexer(toUpper);
    }

    private void initField(FieldType fieldType) throws CronSyntaxException {
        this.fieldType = fieldType;
        this.values.clear();
        this.assertMoreTokens();
    }

    private void parseSlashInterval(int min, int max) throws CronSyntaxException {
        if (this.peekType() != TokenType.SLASH) {
            this.setValues(min, max);
            return;
        }
        this.skip(TokenType.SLASH);
        int step = this.parseStep(this.lexer.nextToken());
        this.setValues(min, max, step);
        CronLexer.Token token = this.lexer.peekToken();
        switch (token.getType()) {
            case COMMA: 
            case NOTHING: 
            case WHITESPACE: {
                break;
            }
            default: {
                throw this.syntaxError(ErrorCode.ILLEGAL_CHARACTER_AFTER_INTERVAL).errorOffset(token.getStart()).value(token.getChar()).build();
            }
        }
    }

    private void parseNumberRange(int min) throws CronSyntaxException {
        this.skip(TokenType.HYPHEN);
        CronLexer.Token token = this.lexer.peekToken();
        switch (token.getType()) {
            case NUMBER: {
                int max = this.parseFieldValue(this.lexer.nextToken());
                if (this.fieldType == FieldType.YEAR && max < min) {
                    throw this.syntaxErrorAt(token, ErrorCode.INVALID_NUMBER_YEAR_RANGE);
                }
                this.parseSlashInterval(min, max);
                return;
            }
            case NAME: {
                this.fieldType.resolveName(token);
                throw this.syntaxErrorAt(token, ErrorCode.INVALID_NAME_RANGE);
            }
        }
        throw token.unexpected();
    }

    private void parseNameRange(int min) throws CronSyntaxException {
        this.skip(TokenType.HYPHEN);
        CronLexer.Token token = this.lexer.peekToken();
        switch (token.getType()) {
            case NAME: {
                int max = this.fieldType.resolveName(this.lexer.nextToken());
                this.parseSlashInterval(min, max);
                return;
            }
            case NUMBER: {
                throw this.syntaxErrorAt(token, ErrorCode.INVALID_NAME_RANGE);
            }
        }
        throw token.unexpected();
    }

    private void parseNumberExpression() throws CronSyntaxException {
        int min = this.parseFieldValue(this.lexer.nextToken());
        if (this.peekType() == TokenType.HYPHEN) {
            this.parseNumberRange(min);
        } else {
            this.parseSlashInterval(min, -1);
        }
    }

    private void parseNameExpression() throws CronSyntaxException {
        int min = this.fieldType.resolveName(this.lexer.nextToken());
        if (this.peekType() == TokenType.HYPHEN) {
            this.parseNameRange(min);
        } else {
            this.parseSlashInterval(min, -1);
        }
    }

    private void parseAsteriskExpression() throws CronSyntaxException {
        this.skip(TokenType.ASTERISK);
        this.parseSlashInterval(this.fieldType.getMinimumValue(), this.fieldType.getMaximumValue());
    }

    private void parseSimpleExpression() throws CronSyntaxException {
        switch (this.peekType()) {
            case NUMBER: {
                this.parseNumberExpression();
                break;
            }
            case NAME: {
                this.parseNameExpression();
                break;
            }
            case ASTERISK: {
                this.parseAsteriskExpression();
                break;
            }
            case SLASH: {
                this.parseSlashInterval(this.fieldType.getMinimumValue(), this.fieldType.getMaximumValue());
                break;
            }
            case FLAG_L: {
                throw this.unexpectedFlagL(this.lexer.nextToken());
            }
            case FLAG_W: {
                throw this.syntaxErrorAt(this.lexer.nextToken(), ErrorCode.UNEXPECTED_TOKEN_FLAG_W);
            }
            case QUESTION_MARK: {
                throw this.syntaxErrorAt(this.lexer.nextToken(), ErrorCode.QM_CANNOT_USE_HERE);
            }
            default: {
                throw this.lexer.nextToken().unexpected();
            }
        }
    }

    private void parseSimpleField() throws CronSyntaxException {
        this.parseSimpleExpression();
        while (this.peekType() == TokenType.COMMA) {
            this.skip(TokenType.COMMA);
            this.parseSimpleExpression();
        }
    }

    @Nonnull
    private FieldRule parseSimpleFieldToRule() throws CronSyntaxException {
        CronLexer.Token firstToken = this.lexer.peekToken();
        if (firstToken.getType() == TokenType.ASTERISK) {
            this.skip(TokenType.ASTERISK);
            if (this.peekType().isFieldSeparator()) {
                return this.fieldType.all();
            }
            this.lexer.moveTo(firstToken);
        }
        this.parseSimpleField();
        return BitSetFieldRule.of(this.fieldType.getField(), this.values);
    }

    private FieldRule parseSpecialDomLastOffsetNumber(int days) throws CronSyntaxException {
        CronLexer.Token token = this.lexer.peekToken();
        switch (token.getType()) {
            case FLAG_W: {
                this.skip(TokenType.FLAG_W);
                this.assertFieldSeparator(ErrorCode.COMMA_WITH_LAST_DOM);
                return new SpecialDayOfMonthFieldRule(-days, true);
            }
            case COMMA: {
                throw this.syntaxErrorAt(token, ErrorCode.COMMA_WITH_LAST_DOM);
            }
        }
        this.assertFieldSeparator(ErrorCode.COMMA_WITH_LAST_DOM);
        return new SpecialDayOfMonthFieldRule(-days, false);
    }

    private FieldRule parseSpecialDomLastOffset() throws CronSyntaxException {
        this.skip(TokenType.HYPHEN);
        switch (this.peekType()) {
            case NUMBER: {
                int days = this.parseNumber(this.lexer.nextToken(), ErrorCode.INVALID_NUMBER_DAY_OF_MONTH_OFFSET, 1, 30);
                return this.parseSpecialDomLastOffsetNumber(days);
            }
            case FLAG_W: {
                return this.parseSpecialDomLastWeekday();
            }
        }
        throw this.syntaxError(ErrorCode.UNEXPECTED_TOKEN_FLAG_L).errorOffset(this.lexer.peekToken().getStart() - 2).build();
    }

    private FieldRule parseSpecialDomLastWeekday() throws CronSyntaxException {
        this.skip(TokenType.FLAG_W);
        this.assertFieldSeparator(ErrorCode.COMMA_WITH_LAST_DOM);
        return new SpecialDayOfMonthFieldRule(0, true);
    }

    @Nullable
    private FieldRule parseSpecialDomLast() throws CronSyntaxException {
        this.skip(TokenType.FLAG_L);
        CronLexer.Token token = this.lexer.peekToken();
        switch (token.getType()) {
            case HYPHEN: {
                return this.parseSpecialDomLastOffset();
            }
            case FLAG_W: {
                return this.parseSpecialDomLastWeekday();
            }
            case WHITESPACE: {
                return new SpecialDayOfMonthFieldRule(0, false);
            }
            case COMMA: {
                this.assertFieldSeparator(ErrorCode.COMMA_WITH_LAST_DOM);
            }
            case NOTHING: {
                throw this.syntaxError(ErrorCode.UNEXPECTED_END_OF_EXPRESSION).errorOffset(this.cronExpression.length()).build();
            }
        }
        throw this.illegalChar(token);
    }

    @Nullable
    private FieldRule parseSpecialDomWeekday() throws CronSyntaxException {
        int cronDayOfWeek = this.parseFieldValue(this.lexer.nextToken());
        if (this.lexer.nextToken().getType() == TokenType.FLAG_W) {
            this.assertFieldSeparator(ErrorCode.COMMA_WITH_WEEKDAY_DOM);
            return new SpecialDayOfMonthFieldRule(cronDayOfWeek, true);
        }
        return null;
    }

    @Nullable
    private FieldRule parseSpecialDomField() throws CronSyntaxException {
        switch (this.peekType()) {
            case FLAG_L: {
                return this.parseSpecialDomLast();
            }
            case NUMBER: {
                return this.parseSpecialDomWeekday();
            }
        }
        return null;
    }

    private FieldRule parseSpecialDowNth(int cronDayOfWeek) throws CronSyntaxException {
        CronLexer.Token token = this.lexer.nextToken();
        int nth = this.parseNumber(token, ErrorCode.ILLEGAL_CHARACTER_AFTER_HASH, 1, 5);
        this.assertFieldSeparator(ErrorCode.COMMA_WITH_NTH_DOW);
        return new SpecialDayOfWeekNthFieldRule(cronDayOfWeek, nth);
    }

    @Nullable
    private FieldRule parseSpecialDowName(CronLexer.Token name) throws CronSyntaxException {
        int cronDayOfWeek = this.fieldType.resolveName(name);
        if (this.lexer.nextToken().getType() == TokenType.HASH) {
            return this.parseSpecialDowNth(cronDayOfWeek);
        }
        return null;
    }

    @Nullable
    private FieldRule parseSpecialDowNumber(CronLexer.Token number) throws CronSyntaxException {
        int cronDayOfWeek = this.parseFieldValue(number);
        switch (this.lexer.nextToken().getType()) {
            case HASH: {
                return this.parseSpecialDowNth(cronDayOfWeek);
            }
            case FLAG_L: {
                this.assertFieldSeparator(ErrorCode.COMMA_WITH_LAST_DOW);
                return new SpecialDayOfWeekLastFieldRule(cronDayOfWeek);
            }
        }
        return null;
    }

    @Nullable
    private FieldRule parseSpecialDowField() throws CronSyntaxException {
        CronLexer.Token token = this.lexer.nextToken();
        switch (token.getType()) {
            case NAME: {
                return this.parseSpecialDowName(token);
            }
            case NUMBER: {
                return this.parseSpecialDowNumber(token);
            }
            case FLAG_L: {
                this.assertFieldSeparator(ErrorCode.COMMA_WITH_LAST_DOW);
                return DayOfWeekFieldRule.saturday();
            }
            case QUESTION_MARK: {
                throw this.syntaxErrorAt(token, ErrorCode.QM_CANNOT_USE_FOR_BOTH_DAYS);
            }
        }
        return null;
    }

    private void parseSecondField() throws CronSyntaxException {
        this.initField(FieldType.SECOND);
        this.secondField = this.parseSimpleFieldToRule();
    }

    private void parseMinuteField() throws CronSyntaxException {
        this.initField(FieldType.MINUTE);
        this.minuteField = this.parseSimpleFieldToRule();
    }

    private void parseHourField() throws CronSyntaxException {
        this.initField(FieldType.HOUR);
        this.hourField = this.parseSimpleFieldToRule();
    }

    private void parseDomField() throws CronSyntaxException {
        this.initField(FieldType.DAY_OF_MONTH);
        CronLexer.Token mark = this.lexer.peekToken();
        this.dayField = this.parseSpecialDomField();
        if (this.dayField == null) {
            this.lexer.moveTo(mark);
            this.dayField = this.parseSimpleFieldToRule();
        }
    }

    private void parseMonthField() throws CronSyntaxException {
        this.initField(FieldType.MONTH);
        this.monthField = this.parseSimpleFieldToRule();
    }

    private void parseDowField() throws CronSyntaxException {
        this.initField(FieldType.DAY_OF_WEEK);
        CronLexer.Token mark = this.lexer.peekToken();
        this.dayField = this.parseSpecialDowField();
        if (this.dayField == null) {
            this.lexer.moveTo(mark);
            this.parseSimpleField();
            this.dayField = DayOfWeekFieldRule.of(this.values);
        }
    }

    private void parseYearField() throws CronSyntaxException {
        this.yearField = FieldType.YEAR.all();
        if (this.lexer.hasMoreTokens()) {
            this.parseWhitespace();
            if (this.lexer.hasMoreTokens()) {
                this.initField(FieldType.YEAR);
                this.yearField = this.parseSimpleFieldToRule();
                if (this.lexer.hasMoreTokens()) {
                    this.parseWhitespace();
                }
            }
        }
    }

    private void parseSecondMinuteHour() throws CronSyntaxException {
        this.parseSecondField();
        this.parseWhitespace();
        this.parseMinuteField();
        this.parseWhitespace();
        this.parseHourField();
        this.parseWhitespace();
    }

    private void parseQmMonthDow() throws CronSyntaxException {
        this.parseQuestionMark();
        this.parseWhitespace();
        this.parseMonthField();
        this.parseWhitespace();
        this.parseDowField();
    }

    private void parseDomMonthQm() throws CronSyntaxException {
        this.parseDomField();
        this.parseWhitespace();
        this.parseMonthField();
        this.parseWhitespace();
        this.parseQuestionMark();
    }

    private void parseDomMonthDow() throws CronSyntaxException {
        if (this.peekType() == TokenType.QUESTION_MARK) {
            this.parseQmMonthDow();
        } else {
            this.parseDomMonthQm();
        }
    }

    private CronExpression parseCronExpression() throws CronSyntaxException {
        try {
            if (this.peekType() == TokenType.WHITESPACE) {
                this.parseWhitespace();
            }
            this.parseSecondMinuteHour();
            this.parseDomMonthDow();
            this.parseYearField();
            return new CronExpression(this.cronExpression, this.yearField, this.monthField, this.dayField, this.hourField, this.minuteField, this.secondField);
        }
        catch (RuntimeException re) {
            throw this.syntaxError(ErrorCode.INTERNAL_PARSER_FAILURE).cronExpression(this.cronExpression).cause((Throwable)re).value(re.toString()).build();
        }
    }

    private void parseWhitespace() throws CronSyntaxException {
        CronLexer.Token token = this.lexer.nextToken();
        switch (token.getType()) {
            case WHITESPACE: {
                return;
            }
            case FLAG_L: {
                throw this.unexpectedFlagL(token);
            }
            case FLAG_W: {
                throw this.unexpectedFlagW(token);
            }
            case HASH: {
                throw this.unexpectedHash(token);
            }
            case NOTHING: {
                throw this.syntaxError(ErrorCode.UNEXPECTED_END_OF_EXPRESSION).errorOffset(this.cronExpression.length()).build();
            }
        }
        throw token.unexpected();
    }

    private void parseQuestionMark() throws CronSyntaxException {
        CronLexer.Token token = this.lexer.nextToken();
        if (token.getType() != TokenType.QUESTION_MARK) {
            throw this.syntaxError(ErrorCode.QM_MUST_USE_FOR_ONE_OF_DAYS).errorOffset(token.getStart()).build();
        }
        this.assertNothingAfterQuestionMark();
    }

    private void assertNothingAfterQuestionMark() throws CronSyntaxException {
        CronLexer.Token token = this.lexer.peekToken();
        if (!token.getType().isFieldSeparator()) {
            throw this.syntaxError(ErrorCode.ILLEGAL_CHARACTER_AFTER_QM).errorOffset(token.getStart()).value(token.getChar()).build();
        }
    }

    private void assertFieldSeparator() throws CronSyntaxException {
        CronLexer.Token token = this.lexer.peekToken();
        if (!token.getType().isFieldSeparator()) {
            switch (token.getType()) {
                case FLAG_L: {
                    throw this.unexpectedFlagL(token);
                }
                case FLAG_W: {
                    throw this.unexpectedFlagW(token);
                }
            }
            throw token.unexpected();
        }
    }

    private void assertFieldSeparator(ErrorCode errorCodeForComma) throws CronSyntaxException {
        CronLexer.Token token = this.lexer.peekToken();
        if (token.getType() == TokenType.COMMA) {
            throw this.syntaxError(errorCodeForComma).errorOffset(token.getStart()).build();
        }
        this.assertFieldSeparator();
    }

    private void assertMoreTokens() throws CronSyntaxException {
        if (this.peekType() == TokenType.NOTHING) {
            throw this.syntaxError(ErrorCode.UNEXPECTED_END_OF_EXPRESSION).errorOffset(this.cronExpression.length()).build();
        }
    }

    private void skip(TokenType expected) {
        CronLexer.Token skipped = this.lexer.nextToken();
        if (skipped.getType() != expected) {
            throw new IllegalStateException("Expected to skip " + (Object)((Object)expected) + "; found " + skipped);
        }
    }

    private TokenType peekType() {
        return this.lexer.peekToken().getType();
    }

    private CronSyntaxException.Builder syntaxError(ErrorCode errorCode) {
        return CronSyntaxException.builder().cronExpression(this.cronExpression).errorCode(errorCode);
    }

    private CronSyntaxException syntaxErrorAt(CronLexer.Token token, ErrorCode errorCode) {
        return this.syntaxError(errorCode).errorOffset(token.getStart()).build();
    }

    private CronSyntaxException illegalChar(CronLexer.Token token) {
        return this.syntaxError(ErrorCode.ILLEGAL_CHARACTER).errorOffset(token.getStart()).value(token.getChar()).build();
    }

    private CronSyntaxException unexpectedFlagL(CronLexer.Token token) {
        switch (this.fieldType) {
            case DAY_OF_MONTH: {
                return this.syntaxErrorAt(token, ErrorCode.COMMA_WITH_LAST_DOM);
            }
            case DAY_OF_WEEK: {
                return this.syntaxErrorAt(token, ErrorCode.COMMA_WITH_LAST_DOW);
            }
        }
        return token.unexpected();
    }

    private CronSyntaxException unexpectedFlagW(CronLexer.Token token) {
        if (this.fieldType == FieldType.DAY_OF_MONTH) {
            return this.syntaxErrorAt(token, ErrorCode.COMMA_WITH_WEEKDAY_DOM);
        }
        return token.unexpected();
    }

    private CronSyntaxException unexpectedHash(CronLexer.Token token) {
        if (this.fieldType == FieldType.DAY_OF_WEEK) {
            return this.syntaxErrorAt(token, ErrorCode.COMMA_WITH_NTH_DOW);
        }
        return token.unexpected();
    }

    private int parseNumber(CronLexer.Token token, ErrorCode errorCode) throws CronSyntaxException {
        try {
            switch (token.getType()) {
                case NOTHING: {
                    throw this.syntaxErrorAt(token, ErrorCode.UNEXPECTED_END_OF_EXPRESSION);
                }
                case NUMBER: {
                    return Integer.parseInt(token.getText());
                }
            }
            throw this.syntaxErrorAt(token, errorCode);
        }
        catch (NumberFormatException nfe) {
            throw this.syntaxError(errorCode).errorOffset(token.getStart()).cause((Throwable)nfe).build();
        }
    }

    private int parseNumber(CronLexer.Token token, ErrorCode errorCode, int min, int max) throws CronSyntaxException {
        int value = this.parseNumber(token, errorCode);
        if (value < min || value > max) {
            throw this.syntaxErrorAt(token, errorCode);
        }
        return value;
    }

    private int parseFieldValue(CronLexer.Token token) throws CronSyntaxException {
        return this.parseNumber(token, this.fieldType.getValueErrorCode(), this.fieldType.getMinimumValue(), this.fieldType.getMaximumValue());
    }

    private int parseStep(CronLexer.Token token) throws CronSyntaxException {
        if (token.getType() != TokenType.NUMBER) {
            throw this.syntaxErrorAt(token, ErrorCode.INVALID_STEP);
        }
        int step = this.parseNumber(token, this.fieldType.getStepErrorCode());
        if (step <= 0) {
            throw this.syntaxErrorAt(token, ErrorCode.INVALID_STEP);
        }
        if (this.fieldType != FieldType.YEAR && step > this.fieldType.getMaximumValue() - this.fieldType.getMinimumValue()) {
            throw this.syntaxError(this.fieldType.getStepErrorCode()).errorOffset(token.getStart()).value(token.getText()).build();
        }
        return step;
    }

    private void setValues(int min, int max) {
        if (max == -1 || max == min) {
            this.values.set(min);
        } else if (max > min) {
            this.values.set(min, max + 1);
        } else {
            this.values.set(min, this.fieldType.getMaximumValue() + 1);
            this.values.set(this.fieldType.getMinimumValue(), max + 1);
        }
    }

    private void setValues(int min, int max, int step) {
        int end;
        int n = end = max != -1 ? max : this.fieldType.getMaximumValue();
        if (step == 1) {
            this.setValues(min, end);
        } else if (min <= end) {
            this.setValuesRange(min, end, step);
        } else {
            int wrapValue = this.setValuesRange(min, this.fieldType.getMaximumValue(), step);
            this.setValuesRange(wrapValue - this.fieldType.getWrapOffset(), end, step);
        }
    }

    private int setValuesRange(int start, int end, int step) {
        int value;
        for (value = start; value <= end; value += step) {
            this.values.set(value);
        }
        return value;
    }
}


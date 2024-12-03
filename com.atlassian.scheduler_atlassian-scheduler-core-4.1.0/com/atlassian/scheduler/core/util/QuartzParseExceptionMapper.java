/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.cron.ErrorCode
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.scheduler.core.util;

import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.cron.ErrorCode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuartzParseExceptionMapper {
    static final ExceptionMapper INVALID_NAME_MAPPER = new InvalidNameMapper();
    static final ExceptionMapper INVALID_NAME_RANGE_MAPPER = QuartzParseExceptionMapper.ignoreValue(ErrorCode.INVALID_NAME_RANGE);
    static final ExceptionMapper GENERAL_PARSE_FAILURE_MAPPER = new GeneralParseFailureMapper();
    static final ExceptionMapper UNEXPECTED_FLAG_L_MAPPER = QuartzParseExceptionMapper.ignoreValue(ErrorCode.UNEXPECTED_TOKEN_FLAG_L);
    static final ExceptionMapper UNEXPECTED_FLAG_W_MAPPER = QuartzParseExceptionMapper.ignoreValue(ErrorCode.UNEXPECTED_TOKEN_FLAG_W);
    private static final Map<String, ErrorCode> SIMPLE_MAPPERS = ImmutableMap.builder().put((Object)"Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", (Object)ErrorCode.QM_MUST_USE_FOR_ONE_OF_DAYS).put((Object)"Support for specifying 'L' and 'LW' with other days of the month is not implemented", (Object)ErrorCode.COMMA_WITH_LAST_DOM).put((Object)"Support for specifying 'L' with other days of the week is not implemented", (Object)ErrorCode.COMMA_WITH_LAST_DOW).put((Object)"'?' can only be specfied for Day-of-Month or Day-of-Week.", (Object)ErrorCode.QM_CANNOT_USE_HERE).put((Object)"'?' can only be specified for Day-of-Month or Day-of-Week.", (Object)ErrorCode.QM_CANNOT_USE_HERE).put((Object)"'?' can only be specfied for Day-of-Month -OR- Day-of-Week.", (Object)ErrorCode.QM_CANNOT_USE_FOR_BOTH_DAYS).put((Object)"'?' can only be specified for Day-of-Month -OR- Day-of-Week.", (Object)ErrorCode.QM_CANNOT_USE_FOR_BOTH_DAYS).put((Object)"Support for specifying multiple \"nth\" days is not imlemented.", (Object)ErrorCode.COMMA_WITH_NTH_DOW).put((Object)"Minute and Second values must be between 0 and 59", (Object)ErrorCode.INVALID_NUMBER_SEC_OR_MIN).put((Object)"Hour values must be between 0 and 23", (Object)ErrorCode.INVALID_NUMBER_HOUR).put((Object)"Day of month values must be between 1 and 31", (Object)ErrorCode.INVALID_NUMBER_DAY_OF_MONTH).put((Object)"Month values must be between 1 and 12", (Object)ErrorCode.INVALID_NUMBER_MONTH).put((Object)"Day-of-Week values must be between 1 and 7", (Object)ErrorCode.INVALID_NUMBER_DAY_OF_WEEK).put((Object)"Offset from last day must be <= 30", (Object)ErrorCode.INVALID_NUMBER_DAY_OF_MONTH_OFFSET).put((Object)"'/' must be followed by an integer.", (Object)ErrorCode.INVALID_STEP).put((Object)"A numeric value between 1 and 5 must follow the '#' option", (Object)ErrorCode.ILLEGAL_CHARACTER_AFTER_HASH).put((Object)"The 'W' option does not make sense with values larger than 31 (max number of days in a month)", (Object)ErrorCode.INVALID_NUMBER_DAY_OF_MONTH).put((Object)"Illegal cron expression format (java.lang.IllegalArgumentException: Start year must be less than stop year)", (Object)ErrorCode.INVALID_NUMBER_YEAR_RANGE).build();
    private static final Map<String, ExceptionMapper> PREFIX_MAPPERS = ImmutableMap.builder().put((Object)"Unexpected end of expression.", (Object)new UnexpectedEndOfExpressionMapper()).put((Object)"Invalid Day-of-Week value: '", (Object)new RemovePrefixAndSuffix("'", new InvalidDayOfWeekNameMapper())).put((Object)"Invalid Month value: '", (Object)new RemovePrefixAndSuffix("'", new InvalidMonthNameMapper())).put((Object)"Illegal character after '?': ", (Object)new SingleCharAfterPrefix(QuartzParseExceptionMapper.error(ErrorCode.ILLEGAL_CHARACTER_AFTER_QM))).put((Object)"Illegal characters for this position: '", (Object)new RemovePrefixAndSuffix("'", new IllegalCharactersMapper())).put((Object)"Increment > 60 : ", (Object)new RemovePrefix(QuartzParseExceptionMapper.error(ErrorCode.INVALID_STEP_SECOND_OR_MINUTE))).put((Object)"Increment > 31 : ", (Object)new RemovePrefix(QuartzParseExceptionMapper.error(ErrorCode.INVALID_STEP_DAY_OF_MONTH))).put((Object)"Increment > 24 : ", (Object)new RemovePrefix(QuartzParseExceptionMapper.error(ErrorCode.INVALID_STEP_HOUR))).put((Object)"Increment > 7 : ", (Object)new RemovePrefix(QuartzParseExceptionMapper.error(ErrorCode.INVALID_STEP_DAY_OF_WEEK))).put((Object)"Increment > 12 : ", (Object)new RemovePrefix(QuartzParseExceptionMapper.error(ErrorCode.INVALID_STEP_MONTH))).put((Object)"Unexpected character: ", (Object)new SingleCharAfterPrefix(QuartzParseExceptionMapper.error(ErrorCode.ILLEGAL_CHARACTER))).put((Object)"Unexpected character '", (Object)new RemovePrefixAndSuffix("' after '/'", QuartzParseExceptionMapper.error(ErrorCode.ILLEGAL_CHARACTER_AFTER_INTERVAL))).put((Object)"'L' option is not valid here. (pos=", (Object)new RemovePrefixAndSuffix(")", UNEXPECTED_FLAG_L_MAPPER)).put((Object)"'W' option is not valid here. (pos=", (Object)new RemovePrefixAndSuffix(")", UNEXPECTED_FLAG_W_MAPPER)).put((Object)"'#' option is not valid here. (pos=", (Object)new RemovePrefixAndSuffix(")", QuartzParseExceptionMapper.ignoreValue(ErrorCode.UNEXPECTED_TOKEN_HASH))).put((Object)"Illegal cron expression format (java.lang.NumberFormatException: For input string: \"", (Object)new RemovePrefixAndSuffix("\")", new NumberFormatExceptionMapper())).put((Object)"Illegal cron expression format (java.lang.StringIndexOutOfBoundsException: ", (Object)new RemovePrefixAndSuffix(")", new StringIndexOutOfBoundsMapper())).put((Object)"Illegal cron expression format (", (Object)new RemovePrefixAndSuffix(")", GENERAL_PARSE_FAILURE_MAPPER)).build();

    public static CronSyntaxException mapException(String cronExpression, ParseException pe) {
        String message = pe.getMessage();
        if (message == null) {
            return QuartzParseExceptionMapper.mapGeneral(cronExpression, pe);
        }
        ErrorCode errorCode = SIMPLE_MAPPERS.get(message);
        if (errorCode != null) {
            return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(errorCode).cause((Throwable)pe).build();
        }
        return QuartzParseExceptionMapper.mapExceptionByPrefix(cronExpression, pe);
    }

    private static CronSyntaxException mapExceptionByPrefix(String cronExpression, ParseException pe) {
        String message = pe.getMessage();
        for (Map.Entry<String, ExceptionMapper> entry : PREFIX_MAPPERS.entrySet()) {
            String prefix = entry.getKey();
            if (!message.startsWith(prefix)) continue;
            return entry.getValue().map(cronExpression, pe, prefix);
        }
        return QuartzParseExceptionMapper.mapGeneral(cronExpression, pe);
    }

    private static CronSyntaxException mapGeneral(String cronExpression, ParseException pe) {
        Throwable cause = pe.getCause();
        if (cause == null) {
            cause = pe;
        }
        return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(ErrorCode.INTERNAL_PARSER_FAILURE).cause(cause).value(pe.getMessage()).build();
    }

    static boolean startsWithNumber(String s) {
        if (s.isEmpty()) {
            return false;
        }
        char c = s.charAt(0);
        return c >= '0' && c <= '9';
    }

    static int toInt(String s) {
        try {
            if (s != null) {
                return Integer.parseInt(s);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return -1;
    }

    private static ErrorCodeMapper error(ErrorCode errorCode) {
        return new ErrorCodeMapper(errorCode);
    }

    private static IgnoreValue ignoreValue(ErrorCode errorCode) {
        return new IgnoreValue(errorCode);
    }

    static /* synthetic */ ErrorCodeMapper access$000(ErrorCode x0) {
        return QuartzParseExceptionMapper.error(x0);
    }

    static class GeneralParseFailureMapper
    implements ExceptionMapper {
        GeneralParseFailureMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            return QuartzParseExceptionMapper.mapGeneral(cronExpression, pe);
        }
    }

    static class StringIndexOutOfBoundsMapper
    implements ExceptionMapper {
        private static Pattern simplePattern = Pattern.compile("String index out of range: (\\d+)");
        private static Pattern rangePattern = Pattern.compile("begin \\d+, end (\\d+), length \\d+");

        StringIndexOutOfBoundsMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            int len = StringIndexOutOfBoundsMapper.extractLength(value);
            switch (len) {
                case 1: 
                case 2: 
                case 3: {
                    return INVALID_NAME_MAPPER.map(cronExpression, pe, null);
                }
                case 5: 
                case 6: 
                case 7: {
                    return INVALID_NAME_RANGE_MAPPER.map(cronExpression, pe, null);
                }
            }
            return QuartzParseExceptionMapper.mapGeneral(cronExpression, pe);
        }

        private static int extractLength(String value) {
            Matcher matcher = simplePattern.matcher(value);
            if (!matcher.matches() && !(matcher = rangePattern.matcher(value)).matches()) {
                return -1;
            }
            return QuartzParseExceptionMapper.toInt(matcher.group(1));
        }
    }

    static class NumberFormatExceptionMapper
    implements ExceptionMapper {
        private static final Pattern REGEX_FIND_BAD_RANGE = Pattern.compile("[0-9]+-([A-Za-z]+)");
        private static final Pattern REGEX_FIND_BAD_STEP = Pattern.compile("/[^0-9]");

        NumberFormatExceptionMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String ignored) {
            Matcher range = REGEX_FIND_BAD_RANGE.matcher(cronExpression);
            Matcher step = REGEX_FIND_BAD_STEP.matcher(cronExpression);
            if (range.find()) {
                if (step.find() && step.start() < range.start()) {
                    return NumberFormatExceptionMapper.mapStep(cronExpression, pe, step);
                }
                return NumberFormatExceptionMapper.mapRange(cronExpression, pe, range);
            }
            if (step.find()) {
                return NumberFormatExceptionMapper.mapStep(cronExpression, pe, step);
            }
            return QuartzParseExceptionMapper.mapGeneral(cronExpression, pe);
        }

        private static CronSyntaxException mapRange(String cronExpression, ParseException pe, Matcher range) {
            return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(ErrorCode.INVALID_NAME_RANGE).errorOffset(range.start(1)).cause((Throwable)pe).build();
        }

        private static CronSyntaxException mapStep(String cronExpression, ParseException pe, Matcher step) {
            return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(ErrorCode.INVALID_STEP).errorOffset(step.start() + 1).cause((Throwable)pe).build();
        }
    }

    static class InvalidMonthNameMapper
    implements ExceptionMapper {
        private static final ExceptionMapper BAD_MONTH = QuartzParseExceptionMapper.access$000(ErrorCode.INVALID_NAME_MONTH);

        InvalidMonthNameMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            ExceptionMapper mapper = QuartzParseExceptionMapper.startsWithNumber(value) ? INVALID_NAME_RANGE_MAPPER : BAD_MONTH;
            return mapper.map(cronExpression, pe, value);
        }
    }

    static class InvalidDayOfWeekNameMapper
    implements ExceptionMapper {
        private static final ExceptionMapper BAD_DAY_OF_WEEK = QuartzParseExceptionMapper.access$000(ErrorCode.INVALID_NAME_DAY_OF_WEEK);

        InvalidDayOfWeekNameMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            ExceptionMapper mapper = QuartzParseExceptionMapper.startsWithNumber(value) ? INVALID_NAME_RANGE_MAPPER : BAD_DAY_OF_WEEK;
            return mapper.map(cronExpression, pe, value);
        }
    }

    static class InvalidNameMapper
    implements ExceptionMapper {
        private static final Pattern REGEX_FIND_NAMES = Pattern.compile("[A-Z]+");
        private static final Pattern REGEX_FIND_BAD_FLAG_L = Pattern.compile("[^A-Z]L-( |\t|$)");
        private static final Set<String> L_AND_W_FLAGS = ImmutableSet.of((Object)"L", (Object)"W", (Object)"LW");

        InvalidNameMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String ignored) {
            Matcher matcher = REGEX_FIND_NAMES.matcher(cronExpression);
            while (matcher.find()) {
                String name = matcher.group(0);
                if (name.length() >= 3 || L_AND_W_FLAGS.contains(name)) continue;
                return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(ErrorCode.INVALID_NAME).errorOffset(matcher.start()).value(name).cause((Throwable)pe).build();
            }
            matcher = REGEX_FIND_BAD_FLAG_L.matcher(cronExpression);
            if (matcher.find()) {
                return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(ErrorCode.UNEXPECTED_TOKEN_FLAG_L).errorOffset(matcher.start() + 1).cause((Throwable)pe).build();
            }
            return QuartzParseExceptionMapper.mapGeneral(cronExpression, pe);
        }
    }

    static class IllegalCharactersMapper
    implements ExceptionMapper {
        private static final ExceptionMapper WRONG_FIELD = QuartzParseExceptionMapper.access$000(ErrorCode.INVALID_NAME_FIELD);

        IllegalCharactersMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            String s;
            int hyphen = value.indexOf(45);
            String string = s = hyphen != -1 ? value.substring(0, hyphen) : value;
            if ("L".equals(s)) {
                return UNEXPECTED_FLAG_L_MAPPER.map(cronExpression, pe, null);
            }
            if ("W".equals(s)) {
                return UNEXPECTED_FLAG_W_MAPPER.map(cronExpression, pe, null);
            }
            return WRONG_FIELD.map(cronExpression, pe, value);
        }
    }

    static class UnexpectedEndOfExpressionMapper
    implements ExceptionMapper {
        UnexpectedEndOfExpressionMapper() {
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(ErrorCode.UNEXPECTED_END_OF_EXPRESSION).errorOffset(cronExpression.length()).cause((Throwable)pe).build();
        }
    }

    static class IgnoreValue
    extends ErrorCodeMapper {
        IgnoreValue(ErrorCode errorCode) {
            super(errorCode);
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            return super.map(cronExpression, pe, null);
        }
    }

    static class ErrorCodeMapper
    implements ExceptionMapper {
        private final ErrorCode errorCode;

        ErrorCodeMapper(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String value) {
            return CronSyntaxException.builder().cronExpression(cronExpression).errorCode(this.errorCode).cause((Throwable)pe).value(value).build();
        }
    }

    static class RemovePrefixAndSuffix
    implements ExceptionMapper {
        private final String suffix;
        private final ExceptionMapper delegate;

        RemovePrefixAndSuffix(String suffix, ExceptionMapper delegate) {
            this.suffix = suffix;
            this.delegate = delegate;
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String prefix) {
            String value = pe.getMessage();
            int pos = value.lastIndexOf(this.suffix);
            if (pos > prefix.length()) {
                return this.delegate.map(cronExpression, pe, value.substring(prefix.length(), pos));
            }
            return this.delegate.map(cronExpression, pe, value.substring(prefix.length()));
        }
    }

    static class SingleCharAfterPrefix
    implements ExceptionMapper {
        private final ExceptionMapper delegate;

        SingleCharAfterPrefix(ExceptionMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String prefix) {
            char c = pe.getMessage().charAt(prefix.length());
            return this.delegate.map(cronExpression, pe, String.valueOf(c));
        }
    }

    static class RemovePrefix
    implements ExceptionMapper {
        private final ExceptionMapper delegate;

        RemovePrefix(ExceptionMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CronSyntaxException map(String cronExpression, ParseException pe, String prefix) {
            return this.delegate.map(cronExpression, pe, pe.getMessage().substring(prefix.length()));
        }
    }

    static interface ExceptionMapper {
        public CronSyntaxException map(String var1, ParseException var2, String var3);
    }
}


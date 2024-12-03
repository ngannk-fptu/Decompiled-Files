/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.util;

import java.text.DateFormatSymbols;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.util.LocaleUtil;

public class DateParser {
    protected DateParser() {
    }

    private static int parseMonth(String monthPart) {
        try {
            return Integer.parseInt(monthPart);
        }
        catch (NumberFormatException numberFormatException) {
            String[] months = DateFormatSymbols.getInstance(LocaleUtil.getUserLocale()).getMonths();
            for (int month = 0; month < months.length; ++month) {
                if (!months[month].toLowerCase(LocaleUtil.getUserLocale()).startsWith(monthPart.toLowerCase(LocaleUtil.getUserLocale()))) continue;
                return month + 1;
            }
            return -1;
        }
    }

    public static LocalDate parseLocalDate(String strVal) throws EvaluationException {
        for (Format format : Format.values()) {
            Matcher matcher = format.pattern.matcher(strVal);
            if (!matcher.find()) continue;
            MatchResult matchResult = matcher.toMatchResult();
            ArrayList<String> groups = new ArrayList<String>();
            for (int i = 1; i <= matchResult.groupCount(); ++i) {
                groups.add(matchResult.group(i));
            }
            int year = format.hasYear ? Integer.parseInt((String)groups.get(format.yearIndex)) : LocalDate.now(LocaleUtil.getUserTimeZone().toZoneId()).getYear();
            int month = DateParser.parseMonth((String)groups.get(format.monthIndex));
            int day = Integer.parseInt((String)groups.get(format.dayIndex));
            try {
                return LocalDate.of(year, month, day);
            }
            catch (DateTimeException e) {
                throw new DateTimeException("Failed to parse date-string " + strVal);
            }
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }

    public static Calendar parseDate(String strVal) throws EvaluationException {
        LocalDate date = DateParser.parseLocalDate(strVal);
        return DateParser.makeDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    private static Calendar makeDate(int year, int month, int day) throws EvaluationException {
        if (month < 1 || month > 12) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        Calendar cal = LocaleUtil.getLocaleCalendar(year, month - 1, 1, 0, 0, 0);
        if (day < 1 || day > cal.getActualMaximum(5)) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        cal.set(5, day);
        return cal;
    }

    private static enum Format {
        YMD_DASHES("^(\\d{4})-(\\w+)-(\\d{1,2})( .*)?$", "ymd"),
        DMY_DASHES("^(\\d{1,2})-(\\w+)-(\\d{4})( .*)?$", "dmy"),
        MD_DASHES("^(\\w+)-(\\d{1,2})( .*)?$", "md"),
        MDY_SLASHES("^(\\w+)/(\\d{1,2})/(\\d{4})( .*)?$", "mdy"),
        YMD_SLASHES("^(\\d{4})/(\\w+)/(\\d{1,2})( .*)?$", "ymd"),
        MD_SLASHES("^(\\w+)/(\\d{1,2})( .*)?$", "md");

        private Pattern pattern;
        private boolean hasYear;
        private int yearIndex;
        private int monthIndex;
        private int dayIndex;

        private Format(String patternString, String groupOrder) {
            this.pattern = Pattern.compile(patternString);
            this.hasYear = groupOrder.contains("y");
            if (this.hasYear) {
                this.yearIndex = groupOrder.indexOf("y");
            }
            this.monthIndex = groupOrder.indexOf("m");
            this.dayIndex = groupOrder.indexOf("d");
        }
    }
}


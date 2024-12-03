/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusDateMatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSisyphusDateMatcher
implements SisyphusDateMatcher {
    private static final Logger log = LoggerFactory.getLogger(DefaultSisyphusDateMatcher.class);
    private static final String regexDelimiter = "[-:\\/., ]";
    private static final String regexDay = "((?:[0-2]?\\d{1})|(?:[3][01]{1}))";
    private static final String regexMonth = "(?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))";
    private static final String regexYear = "((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))";
    private static final String regexHourMinuteSecond = "(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?";
    private static final String regexEndswith = "(?![\\d])";
    private static final String regexDateEuropean = "((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])";
    private static final Pattern dateEuropeanPattern = Pattern.compile("((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])", 34);
    private static final String regexDateAmerican = "(?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])";
    private static final Pattern dateAmericanPattern = Pattern.compile("(?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])", 34);
    private static final String regexDateTechnical = "((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])";
    private static final Pattern dateTechnicalPattern = Pattern.compile("((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])", 34);
    private static final String generalRegex = "((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])|(?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])|((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])";
    private static final Pattern dateGeneralPattern = Pattern.compile("((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])|(?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/., ]((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])|((?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))[-:\\/., ](?:([0]?[1-9]|[1][012])|(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Sept|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))[-:\\/., ]((?:[0-2]?\\d{1})|(?:[3][01]{1}))(?:(?:\\s)((?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):([0-5][0-9])(?::([0-5][0-9]))?(?:\\s?(am|AM|pm|PM))?)?(?![\\d])", 34);

    @Override
    public Date extractDate(String text) {
        Date date = null;
        Boolean dateFound = false;
        String year = null;
        String month = null;
        String monthName = null;
        String day = null;
        String hour = null;
        String minute = null;
        String second = null;
        Matcher m = dateEuropeanPattern.matcher(text);
        if (m.find()) {
            day = m.group(1);
            month = m.group(2);
            monthName = m.group(3);
            year = m.group(4);
            hour = m.group(5);
            minute = m.group(6);
            second = m.group(7);
            dateFound = true;
        }
        if (!dateFound.booleanValue() && (m = dateAmericanPattern.matcher(text)).find()) {
            month = m.group(1);
            monthName = m.group(2);
            day = m.group(3);
            year = m.group(4);
            hour = m.group(5);
            minute = m.group(6);
            second = m.group(7);
            dateFound = true;
        }
        if (!dateFound.booleanValue() && (m = dateTechnicalPattern.matcher(text)).find()) {
            year = m.group(1);
            month = m.group(2);
            monthName = m.group(3);
            day = m.group(4);
            hour = m.group(5);
            minute = m.group(6);
            second = m.group(7);
            dateFound = true;
        }
        if (dateFound.booleanValue()) {
            String dateFormatPattern = "";
            String dayPattern = "";
            String dateString = "";
            if (day != null) {
                dayPattern = "d" + (day.length() == 2 ? "d" : "");
            }
            if (day != null && month != null && year != null) {
                dateFormatPattern = "yyyy MM " + dayPattern;
                dateString = year + " " + month + " " + day;
            } else if (monthName != null) {
                dateFormatPattern = monthName.length() == 3 ? "yyyy MMM " + dayPattern : "yyyy MMMM " + dayPattern;
                dateString = year + " " + monthName + " " + day;
            }
            if (hour != null && minute != null) {
                dateFormatPattern = dateFormatPattern + " hh:mm";
                dateString = dateString + " " + hour + ":" + minute;
                if (second != null) {
                    dateFormatPattern = dateFormatPattern + ":ss";
                    dateString = dateString + ":" + second;
                }
            }
            if (!dateFormatPattern.equals("") && !dateString.equals("")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern.trim(), Locale.US);
                try {
                    date = dateFormat.parse(dateString.trim());
                }
                catch (ParseException e) {
                    log.info("Cannot parse string to match a date format");
                }
            }
        }
        return date;
    }

    public static String extractStringDate(String text) {
        String date = null;
        Matcher m = dateGeneralPattern.matcher(text);
        while (m.find()) {
            date = text.substring(m.start(), m.end());
        }
        return date;
    }
}


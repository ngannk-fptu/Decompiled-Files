/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util;

import com.atlassian.core.i18n.I18nTextProvider;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.InvalidDurationException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class DurationUtils {
    private static final String UNIT_DAY = "core.durationutils.unit.day";
    private static final String UNIT_HOUR = "core.durationutils.unit.hour";
    private static final String UNIT_MINUTE = "core.durationutils.unit.minute";
    private static final Pattern COUNT_WITH_OPTIONAL_UNITS = Pattern.compile("([,\\.\\xA0'\\p{Nd}]+)\\s*(?:([^,\\s]+),?)?\\s*");

    public static long getDurationSeconds(String durationStr, long secondsPerDay, long secondsPerWeek, DateUtils.Duration defaultUnit, Locale locale, I18nTextProvider i18n) throws InvalidDurationException {
        Map<String, DateUtils.Duration> durationTokens = DurationUtils.getDurationTokens(i18n);
        return DurationUtils.getDurationSeconds(durationStr, secondsPerDay, secondsPerWeek, defaultUnit, locale, durationTokens);
    }

    public static long getDurationSeconds(String durationStr, long secondsPerDay, long secondsPerWeek, DateUtils.Duration defaultUnit, Locale locale, Map<String, DateUtils.Duration> tokens) throws InvalidDurationException {
        if (StringUtils.isBlank((CharSequence)durationStr)) {
            return 0L;
        }
        durationStr = durationStr.trim();
        NumberFormat nf = DecimalFormat.getNumberInstance(locale);
        long seconds = 0L;
        Matcher m = COUNT_WITH_OPTIONAL_UNITS.matcher(durationStr);
        while (m.lookingAt()) {
            DateUtils.Duration unit;
            ParsePosition pp = new ParsePosition(0);
            String number = m.group(1);
            Number n = nf.parse(number, pp);
            if (pp.getIndex() != number.length()) {
                throw new InvalidDurationException("Bad number '" + number + "' in duration string '" + durationStr + "'");
            }
            String unitName = m.group(2);
            if (unitName != null) {
                unit = tokens.get(unitName);
                if (unit == null) {
                    throw new InvalidDurationException("No unit for '" + unitName + "'");
                }
            } else {
                unit = defaultUnit;
            }
            long s = (long)((double)unit.getModifiedSeconds(secondsPerDay, secondsPerWeek) * n.doubleValue());
            if (unit != defaultUnit && s % 60L != 0L) {
                throw new InvalidDurationException("Durations must be in whole minutes");
            }
            seconds += s;
            m.region(m.end(), durationStr.length());
        }
        if (m.regionStart() != durationStr.length()) {
            throw new InvalidDurationException("Invalid characters in duration: " + durationStr);
        }
        return seconds;
    }

    public static Map<String, DateUtils.Duration> getDurationTokens(I18nTextProvider i18n) {
        HashMap<String, DateUtils.Duration> tokens = new HashMap<String, DateUtils.Duration>();
        tokens.put(DurationUtils.getDurationToken(i18n, UNIT_DAY), DateUtils.Duration.DAY);
        tokens.put(DurationUtils.getDurationToken(i18n, UNIT_HOUR), DateUtils.Duration.HOUR);
        tokens.put(DurationUtils.getDurationToken(i18n, UNIT_MINUTE), DateUtils.Duration.MINUTE);
        for (DateUtils.Duration d : DateUtils.Duration.values()) {
            String n = d.name().toLowerCase();
            tokens.put(i18n.getText("core.dateutils." + n), d);
            tokens.put(i18n.getText("core.dateutils." + n + "s"), d);
        }
        return tokens;
    }

    private static String getDurationToken(I18nTextProvider i18n, String unit) {
        String s = i18n.getText(unit);
        return s.replace("{0}", "").trim();
    }
}


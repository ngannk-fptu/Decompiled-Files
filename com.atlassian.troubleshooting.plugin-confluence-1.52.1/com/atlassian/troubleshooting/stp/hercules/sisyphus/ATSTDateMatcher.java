/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.hercules.sisyphus;

import com.atlassian.sisyphus.SisyphusDateMatcher;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATSTDateMatcher
implements SisyphusDateMatcher {
    private final Pattern dateTimePattern = Pattern.compile("^\\s*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})\\D");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss,SSS");

    @Override
    public Date extractDate(String s) {
        Matcher matcher = this.dateTimePattern.matcher(s);
        if (matcher.find()) {
            return Date.from(LocalDateTime.parse(matcher.group(1), this.formatter).atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.util.StringUtil;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DiagnosticsLogWriterImpl
implements DiagnosticsLogWriter {
    private static final String STR_LONG_MIN_VALUE = String.format(StringUtil.LOCALE_INTERNAL, "%,d", Long.MIN_VALUE);
    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final String[] INDENTS = new String[]{StringUtil.LINE_SEPARATOR + "                          ", StringUtil.LINE_SEPARATOR + "                                  ", StringUtil.LINE_SEPARATOR + "                                          ", StringUtil.LINE_SEPARATOR + "                                                  ", StringUtil.LINE_SEPARATOR + "                                                            "};
    private static final int CHARS_LENGTH = 32;
    private final StringBuilder tmpSb = new StringBuilder();
    private final boolean includeEpochTime;
    private int sectionLevel = -1;
    private PrintWriter printWriter;
    private final Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
    private final Date date = new Date();
    private char[] chars = new char[32];
    private StringBuilder stringBuilder = new StringBuilder();

    public DiagnosticsLogWriterImpl() {
        this(false);
    }

    public DiagnosticsLogWriterImpl(boolean includeEpochTime) {
        this.includeEpochTime = includeEpochTime;
    }

    @Override
    public void writeSectionKeyValue(String sectionName, long timeMillis, String key, long value) {
        this.startSection(sectionName, timeMillis);
        this.write(key);
        this.write('=');
        this.write(value);
        this.endSection();
    }

    @Override
    public void writeSectionKeyValue(String sectionName, long timeMillis, String key, double value) {
        this.startSection(sectionName, timeMillis);
        this.write(key);
        this.write('=');
        this.write(value);
        this.endSection();
    }

    @Override
    public void writeSectionKeyValue(String sectionName, long timeMillis, String key, String value) {
        this.startSection(sectionName, timeMillis);
        this.write(key);
        this.write('=');
        this.write(value);
        this.endSection();
    }

    @Override
    public void startSection(String sectionName) {
        this.startSection(sectionName, System.currentTimeMillis());
    }

    public void startSection(String name, long timeMillis) {
        if (this.sectionLevel == -1) {
            this.appendDateTime(timeMillis);
            this.write(' ');
            if (this.includeEpochTime) {
                this.write(timeMillis);
                this.write(' ');
            }
        }
        if (this.sectionLevel >= 0) {
            this.write(INDENTS[this.sectionLevel]);
        }
        this.write(name);
        this.write('[');
        ++this.sectionLevel;
    }

    @Override
    public void endSection() {
        this.write(']');
        --this.sectionLevel;
        if (this.sectionLevel == -1) {
            this.write(StringUtil.LINE_SEPARATOR);
        }
    }

    @Override
    public void writeEntry(String s) {
        this.write(INDENTS[this.sectionLevel]);
        this.write(s);
    }

    @Override
    public void writeKeyValueEntry(String key, String value) {
        this.writeKeyValueHead(key);
        this.write(value);
    }

    void writeLong(long value) {
        if (value == Long.MIN_VALUE) {
            this.write(STR_LONG_MIN_VALUE);
            return;
        }
        if (value < 0L) {
            this.write('-');
            value = -value;
        }
        int digitsWithoutComma = 0;
        this.tmpSb.setLength(0);
        do {
            if (++digitsWithoutComma == 4) {
                this.tmpSb.append(',');
                digitsWithoutComma = 1;
            }
            int mod = (int)(value % 10L);
            this.tmpSb.append(DIGITS[mod]);
        } while ((value /= 10L) > 0L);
        for (int k = this.tmpSb.length() - 1; k >= 0; --k) {
            char c = this.tmpSb.charAt(k);
            this.write(c);
        }
    }

    @Override
    public void writeKeyValueEntry(String key, double value) {
        this.writeKeyValueHead(key);
        this.write(value);
    }

    @Override
    public void writeKeyValueEntry(String key, long value) {
        this.writeKeyValueHead(key);
        this.writeLong(value);
    }

    @Override
    public void writeKeyValueEntry(String key, boolean value) {
        this.writeKeyValueHead(key);
        this.write(value);
    }

    @Override
    public void writeKeyValueEntryAsDateTime(String key, long epochMillis) {
        this.writeKeyValueHead(key);
        this.appendDateTime(epochMillis);
    }

    private void writeKeyValueHead(String key) {
        this.write(INDENTS[this.sectionLevel]);
        this.write(key);
        this.write('=');
    }

    public void init(PrintWriter printWriter) {
        this.sectionLevel = -1;
        this.printWriter = printWriter;
    }

    protected DiagnosticsLogWriter write(char c) {
        this.printWriter.write(c);
        return this;
    }

    protected DiagnosticsLogWriter write(int i) {
        this.stringBuilder.append(i);
        this.flushSb();
        return this;
    }

    protected DiagnosticsLogWriter write(double i) {
        this.stringBuilder.append(i);
        this.flushSb();
        return this;
    }

    protected DiagnosticsLogWriter write(long i) {
        this.stringBuilder.append(i);
        this.flushSb();
        return this;
    }

    private void flushSb() {
        int length = this.stringBuilder.length();
        this.stringBuilder.getChars(0, length, this.chars, 0);
        this.printWriter.write(this.chars, 0, length);
        this.stringBuilder.setLength(0);
    }

    protected DiagnosticsLogWriter write(boolean b) {
        this.write(b ? "true" : "false");
        return this;
    }

    protected DiagnosticsLogWriter write(String s) {
        this.printWriter.write(s == null ? "null" : s);
        return this;
    }

    private void appendDateTime(long epochMillis) {
        this.date.setTime(epochMillis);
        this.calendar.setTime(this.date);
        this.appendDate();
        this.write(' ');
        this.appendTime();
    }

    private void appendDate() {
        int dayOfMonth = this.calendar.get(5);
        if (dayOfMonth < 10) {
            this.write('0');
        }
        this.write(dayOfMonth);
        this.write('-');
        int month = this.calendar.get(2) + 1;
        if (month < 10) {
            this.write('0');
        }
        this.write(month);
        this.write('-');
        this.write(this.calendar.get(1));
    }

    private void appendTime() {
        int hour = this.calendar.get(11);
        if (hour < 10) {
            this.write('0');
        }
        this.write(hour);
        this.write(':');
        int minute = this.calendar.get(12);
        if (minute < 10) {
            this.write('0');
        }
        this.write(minute);
        this.write(':');
        int second = this.calendar.get(13);
        if (second < 10) {
            this.write('0');
        }
        this.write(second);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.MinutesFormatter;
import com.twelvemonkeys.util.SecondsFormatter;
import com.twelvemonkeys.util.TextFormatter;
import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.TimeFormatter;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.StringTokenizer;
import java.util.Vector;

public class TimeFormat
extends Format {
    static final String MINUTE = "m";
    static final String SECOND = "s";
    static final String TIME = "S";
    static final String ESCAPE = "\\";
    private static final TimeFormat DEFAULT_FORMAT = new TimeFormat("m:ss");
    protected String formatString = null;
    protected TimeFormatter[] formatter;

    static void main(String[] stringArray) {
        Time time = null;
        TimeFormat timeFormat = null;
        TimeFormat timeFormat2 = null;
        if (stringArray.length >= 3) {
            System.out.println("Creating out TimeFormat: \"" + stringArray[2] + "\"");
            timeFormat2 = new TimeFormat(stringArray[2]);
        }
        if (stringArray.length >= 2) {
            System.out.println("Creating in TimeFormat: \"" + stringArray[1] + "\"");
            timeFormat = new TimeFormat(stringArray[1]);
        } else {
            System.out.println("Using default format for in");
            timeFormat = DEFAULT_FORMAT;
        }
        if (timeFormat2 == null) {
            timeFormat2 = timeFormat;
        }
        if (stringArray.length >= 1) {
            System.out.println("Parsing: \"" + stringArray[0] + "\" with format \"" + timeFormat.formatString + "\"");
            time = timeFormat.parse(stringArray[0]);
        } else {
            time = new Time();
        }
        System.out.println("Time is \"" + timeFormat2.format(time) + "\" according to format \"" + timeFormat2.formatString + "\"");
    }

    public TimeFormat(String string) {
        this.formatString = string;
        Vector<TimeFormatter> vector = new Vector<TimeFormatter>();
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\\msS", true);
        String string2 = null;
        String string3 = null;
        int n = 0;
        while (stringTokenizer.hasMoreElements()) {
            string3 = stringTokenizer.nextToken();
            if (string2 != null && string2.equals(ESCAPE)) {
                string3 = (string3 != null ? string3 : "") + (stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : "");
                string2 = null;
                n = 0;
            }
            if (string2 == null || string2.equals(string3)) {
                ++n;
                string2 = string3;
                continue;
            }
            if (string2.equals(MINUTE)) {
                vector.add(new MinutesFormatter(n));
            } else if (string2.equals(SECOND)) {
                vector.add(new SecondsFormatter(n));
            } else if (string2.equals(TIME)) {
                vector.add(new SecondsFormatter(-1));
            } else {
                vector.add(new TextFormatter(string2));
            }
            n = 1;
            string2 = string3;
        }
        if (string2 != null) {
            if (string2.equals(MINUTE)) {
                vector.add(new MinutesFormatter(n));
            } else if (string2.equals(SECOND)) {
                vector.add(new SecondsFormatter(n));
            } else if (string2.equals(TIME)) {
                vector.add(new SecondsFormatter(-1));
            } else {
                vector.add(new TextFormatter(string2));
            }
        }
        this.formatter = vector.toArray(new TimeFormatter[vector.size()]);
    }

    public static TimeFormat getInstance() {
        return DEFAULT_FORMAT;
    }

    public String getFormatString() {
        return this.formatString;
    }

    @Override
    public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        if (!(object instanceof Time)) {
            throw new IllegalArgumentException("Must be instance of " + Time.class);
        }
        return stringBuffer.append(this.format(object));
    }

    public String format(Time time) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.formatter.length; ++i) {
            stringBuilder.append(this.formatter[i].format(time));
        }
        return stringBuilder.toString();
    }

    @Override
    public Object parseObject(String string, ParsePosition parsePosition) {
        Time time = this.parse(string);
        parsePosition.setIndex(string.length());
        return time;
    }

    public Time parse(String string) {
        Time time = new Time();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        boolean bl = false;
        for (int i = 0; i < this.formatter.length && n3 + n4 < string.length(); ++i) {
            n3 += n4;
            if (this.formatter[i] instanceof MinutesFormatter) {
                if (i + 1 < this.formatter.length && this.formatter[i + 1] instanceof TextFormatter) {
                    n4 = string.indexOf(((TextFormatter)this.formatter[i + 1]).text, n3);
                    if (n4 < 0) {
                        n4 = string.length();
                    }
                } else {
                    n4 = i + 1 >= this.formatter.length ? string.length() : this.formatter[i].digits;
                }
                if (n4 <= n3) continue;
                n2 = Integer.parseInt(string.substring(n3, n4));
                continue;
            }
            if (this.formatter[i] instanceof SecondsFormatter) {
                if (this.formatter[i].digits == -1) {
                    if (i + 1 < this.formatter.length && this.formatter[i + 1] instanceof TextFormatter) {
                        n4 = string.indexOf(((TextFormatter)this.formatter[i + 1]).text, n3);
                    } else if (i + 1 >= this.formatter.length) {
                        n4 = string.length();
                    } else {
                        n4 = 0;
                        continue;
                    }
                    n = Integer.parseInt(string.substring(n3, n4));
                    bl = true;
                    break;
                }
                n4 = i + 1 < this.formatter.length && this.formatter[i + 1] instanceof TextFormatter ? string.indexOf(((TextFormatter)this.formatter[i + 1]).text, n3) : (i + 1 >= this.formatter.length ? string.length() : this.formatter[i].digits);
                n = Integer.parseInt(string.substring(n3, n4));
                continue;
            }
            if (!(this.formatter[i] instanceof TextFormatter)) continue;
            n4 = this.formatter[i].digits;
        }
        if (!bl) {
            time.setMinutes(n2);
        }
        time.setSeconds(n);
        return time;
    }
}


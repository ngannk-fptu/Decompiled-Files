/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfString;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class PdfDate
extends PdfString {
    private static final int[] DATE_SPACE = new int[]{1, 4, 0, 2, 2, -1, 5, 2, 0, 11, 2, 0, 12, 2, 0, 13, 2, 0};

    public PdfDate(Calendar d) {
        StringBuilder date = new StringBuilder("D:");
        date.append(this.setLength(d.get(1), 4));
        date.append(this.setLength(d.get(2) + 1, 2));
        date.append(this.setLength(d.get(5), 2));
        date.append(this.setLength(d.get(11), 2));
        date.append(this.setLength(d.get(12), 2));
        date.append(this.setLength(d.get(13), 2));
        int timezone = (d.get(15) + d.get(16)) / 3600000;
        if (timezone == 0) {
            date.append('Z');
        } else if (timezone < 0) {
            date.append('-');
            timezone = -timezone;
        } else {
            date.append('+');
        }
        if (timezone != 0) {
            date.append(this.setLength(timezone, 2)).append('\'');
            int zone = Math.abs((d.get(15) + d.get(16)) / 60000) - timezone * 60;
            date.append(this.setLength(zone, 2)).append('\'');
        }
        this.value = date.toString();
    }

    public PdfDate() {
        this(new GregorianCalendar());
    }

    private String setLength(int i, int length) {
        StringBuilder tmp = new StringBuilder();
        tmp.append(i);
        while (tmp.length() < length) {
            tmp.insert(0, "0");
        }
        tmp.setLength(length);
        return tmp.toString();
    }

    public String getW3CDate() {
        return PdfDate.getW3CDate(this.value);
    }

    public static String getW3CDate(String d) {
        if (d.startsWith("D:")) {
            d = d.substring(2);
        }
        StringBuilder sb = new StringBuilder();
        if (d.length() < 4) {
            return "0000";
        }
        sb.append(d, 0, 4);
        d = d.substring(4);
        if (d.length() < 2) {
            return sb.toString();
        }
        sb.append('-').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            return sb.toString();
        }
        sb.append('-').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            return sb.toString();
        }
        sb.append('T').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append(":00Z");
            return sb.toString();
        }
        sb.append(':').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append('Z');
            return sb.toString();
        }
        sb.append(':').append(d, 0, 2);
        d = d.substring(2);
        if (d.startsWith("-") || d.startsWith("+")) {
            String sign = d.substring(0, 1);
            d = d.substring(1);
            String h = "00";
            String m = "00";
            if (d.length() >= 2) {
                h = d.substring(0, 2);
                if (d.length() > 2 && (d = d.substring(3)).length() >= 2) {
                    m = d.substring(0, 2);
                }
                sb.append(sign).append(h).append(':').append(m);
                return sb.toString();
            }
        }
        sb.append('Z');
        return sb.toString();
    }

    public static Calendar decode(String s) {
        try {
            GregorianCalendar calendar;
            if (s.startsWith("D:")) {
                s = s.substring(2);
            }
            int slen = s.length();
            int idx = s.indexOf(90);
            if (idx >= 0) {
                slen = idx;
                calendar = new GregorianCalendar(new SimpleTimeZone(0, "ZPDF"));
            } else {
                int sign = 1;
                idx = s.indexOf(43);
                if (idx < 0 && (idx = s.indexOf(45)) >= 0) {
                    sign = -1;
                }
                if (idx < 0) {
                    calendar = new GregorianCalendar();
                } else {
                    int offset = Integer.parseInt(s.substring(idx + 1, idx + 3)) * 60;
                    if (idx + 5 < s.length()) {
                        offset += Integer.parseInt(s.substring(idx + 4, idx + 6));
                    }
                    calendar = new GregorianCalendar(new SimpleTimeZone(offset * sign * 60000, "ZPDF"));
                    slen = idx;
                }
            }
            calendar.clear();
            idx = 0;
            for (int k = 0; k < DATE_SPACE.length && idx < slen; idx += DATE_SPACE[k + 1], k += 3) {
                calendar.set(DATE_SPACE[k], Integer.parseInt(s.substring(idx, idx + DATE_SPACE[k + 1])) + DATE_SPACE[k + 2]);
            }
            return calendar;
        }
        catch (Exception e) {
            return null;
        }
    }
}


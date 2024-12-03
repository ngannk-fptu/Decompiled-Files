/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.util;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public final class SystemTimeUtils {
    public static Date getDate(byte[] data) {
        return SystemTimeUtils.getDate(data, 0);
    }

    public static Date getDate(byte[] data, int offset) {
        Calendar cal = LocaleUtil.getLocaleCalendar();
        cal.set(1, LittleEndian.getShort(data, offset));
        cal.set(2, LittleEndian.getShort(data, offset + 2) - 1);
        cal.set(5, LittleEndian.getShort(data, offset + 6));
        cal.set(11, LittleEndian.getShort(data, offset + 8));
        cal.set(12, LittleEndian.getShort(data, offset + 10));
        cal.set(13, LittleEndian.getShort(data, offset + 12));
        cal.set(14, LittleEndian.getShort(data, offset + 14));
        return cal.getTime();
    }

    public static void storeDate(Date date, byte[] dest) {
        SystemTimeUtils.storeDate(date, dest, 0);
    }

    public static void storeDate(Date date, byte[] dest, int offset) {
        Calendar cal = LocaleUtil.getLocaleCalendar();
        cal.setTime(date);
        LittleEndian.putShort(dest, offset + 0, (short)cal.get(1));
        LittleEndian.putShort(dest, offset + 2, (short)(cal.get(2) + 1));
        LittleEndian.putShort(dest, offset + 4, (short)(cal.get(7) - 1));
        LittleEndian.putShort(dest, offset + 6, (short)cal.get(5));
        LittleEndian.putShort(dest, offset + 8, (short)cal.get(11));
        LittleEndian.putShort(dest, offset + 10, (short)cal.get(12));
        LittleEndian.putShort(dest, offset + 12, (short)cal.get(13));
        LittleEndian.putShort(dest, offset + 14, (short)cal.get(14));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.zip;

import java.util.TimeZone;
import java.util.zip.ZipEntry;

public class ZipUtil {
    static TimeZone tz = TimeZone.getDefault();

    public static long getModifiedTime(ZipEntry entry) {
        long time = entry.getTime();
        time += (long)tz.getOffset(time);
        return Math.min(time, System.currentTimeMillis() - 1L);
    }

    public static void setModifiedTime(ZipEntry entry, long utc) {
        utc -= (long)tz.getOffset(utc);
        entry.setTime(utc);
    }
}


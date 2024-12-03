/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.time.ZoneOffset;
import net.fortuna.ical4j.model.UtcOffset;

public class ZoneOffsetAdapter
implements Serializable {
    private final ZoneOffset offset;

    public ZoneOffsetAdapter(ZoneOffset offset) {
        this.offset = offset;
    }

    public ZoneOffset getOffset() {
        return this.offset;
    }

    public String toString() {
        String retVal = "";
        if (this.offset != null) {
            int hours = Math.abs(this.offset.getTotalSeconds()) / 3600;
            if (this.offset.getTotalSeconds() < 0) {
                hours = -hours;
            }
            int minutes = Math.abs(this.offset.getTotalSeconds()) % 3600 / 60;
            int seconds = Math.abs(this.offset.getTotalSeconds()) % 3600 % 60;
            retVal = seconds > 0 ? String.format("%+03d%02d%02d", hours, minutes, seconds) : String.format("%+03d%02d", hours, minutes);
        }
        return retVal;
    }

    public static ZoneOffset from(UtcOffset utcOffset) {
        return ZoneOffset.of(utcOffset.toString());
    }
}


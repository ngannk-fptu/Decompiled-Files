/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

public class TimezonesException
extends Throwable {
    public static String unknownTimezone = "org.bedework.timezones.exc.unknownTimezone";
    public static String cacheError = "org.bedework.timezones.exc.cacheerror";
    public static String badDate = "org.bedework.timezones.exc.baddate";
    public static String noPrimary = "org.bedework.timezones.exc.no.primary";
    private String extra;

    public TimezonesException() {
    }

    public TimezonesException(Throwable t) {
        super(t);
    }

    public TimezonesException(String msg) {
        super(msg);
    }

    public TimezonesException(String msg, String extra) {
        super(msg);
        this.extra = extra;
    }

    public String getExtra() {
        return this.extra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (this.extra != null) {
            sb.append(": ");
            sb.append(this.extra);
        }
        return sb.toString();
    }
}


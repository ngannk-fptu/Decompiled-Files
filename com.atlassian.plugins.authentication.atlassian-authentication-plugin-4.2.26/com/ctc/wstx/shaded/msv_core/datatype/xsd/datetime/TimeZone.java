/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.io.Serializable;
import java.util.SimpleTimeZone;

public class TimeZone
implements Serializable {
    public int minutes;
    public static final java.util.TimeZone ZERO = new JavaZeroTimeZone();
    public static final java.util.TimeZone MISSING = new JavaMissingTimeZone();
    private static final long serialVersionUID = 1L;

    private Object readResolve() {
        return new SimpleTimeZone(this.minutes * 60 * 1000, "");
    }

    private static class JavaMissingTimeZone
    extends SimpleTimeZone
    implements Serializable {
        private static final long serialVersionUID = 1L;

        JavaMissingTimeZone() {
            super(0, "XSD missing timezone");
        }

        protected Object readResolve() {
            return MISSING;
        }
    }

    private static class JavaZeroTimeZone
    extends SimpleTimeZone
    implements Serializable {
        private static final long serialVersionUID = 1L;

        JavaZeroTimeZone() {
            super(0, "XSD 'Z' timezone");
        }

        protected Object readResolve() {
            return ZERO;
        }
    }

    static class ZeroTimeZone
    extends TimeZone {
        private static final long serialVersionUID = 1L;

        ZeroTimeZone() {
        }

        protected Object readResolve() {
            return ZERO;
        }
    }
}


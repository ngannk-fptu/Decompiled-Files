/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.valves;

public final class Constants {
    public static final String Package = "org.apache.catalina.valves";

    public static final class AccessLog {
        public static final String COMMON_ALIAS = "common";
        public static final String COMMON_PATTERN = "%h %l %u %t \"%r\" %s %b";
        public static final String COMBINED_ALIAS = "combined";
        public static final String COMBINED_PATTERN = "%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"";
    }
}


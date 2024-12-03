/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.util.SimpleTimeZone;
import java.util.TimeZone;

final class UTC {
    static final TimeZone timeZone = new SimpleTimeZone(0, "UTC");

    private UTC() {
    }
}


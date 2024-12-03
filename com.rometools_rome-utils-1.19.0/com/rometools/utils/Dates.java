/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

import java.util.Date;

public final class Dates {
    private Dates() {
    }

    public static Date copy(Date d) {
        if (d == null) {
            return null;
        }
        return new Date(d.getTime());
    }
}


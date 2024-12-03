/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.util;

import java.util.UUID;

public class UUIDGenerate {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics;

import java.util.Arrays;

public enum Severity {
    INFO(100),
    WARNING(200),
    ERROR(300);

    private final int id;

    private Severity(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static Severity fromId(int id) {
        return Arrays.stream(Severity.values()).filter(severity -> severity.id == id).findFirst().orElseThrow(() -> new IllegalArgumentException("No Severity is associated with ID [" + id + "]"));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;

public final class Depth {
    public static final Depth ROOT = new Depth(1);
    public static final Depth ALL = new Depth(-1);
    private int level;

    private Depth(int level) {
        this.level = level;
    }

    public static Depth valueOf(String str) {
        if (str == null || str.isEmpty()) {
            throw new BadRequestException("Depth must be a valid string.");
        }
        if (str.equals("1") || str.toLowerCase().equals("root")) {
            return ROOT;
        }
        if (str.toLowerCase().equals("all")) {
            return ALL;
        }
        throw new BadRequestException("Unrecognised Depth string: " + str);
    }

    public String toString() {
        if (this == ALL) {
            return "all";
        }
        return String.valueOf(this.level);
    }
}


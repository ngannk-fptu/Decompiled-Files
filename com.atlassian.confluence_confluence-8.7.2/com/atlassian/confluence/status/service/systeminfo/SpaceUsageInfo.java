/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

class SpaceUsageInfo {
    private final int totalSpaces;
    private final int globalSpaces;
    private final int personalSpaces;

    public SpaceUsageInfo(int totalSpaces, int globalSpaces, int personalSpaces) {
        this.totalSpaces = totalSpaces;
        this.globalSpaces = globalSpaces;
        this.personalSpaces = personalSpaces;
    }

    public int getTotalSpaces() {
        return this.totalSpaces;
    }

    public int getGlobalSpaces() {
        return this.globalSpaces;
    }

    public int getPersonalSpaces() {
        return this.personalSpaces;
    }
}


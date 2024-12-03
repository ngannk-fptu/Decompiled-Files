/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.external;

public final class ModelVersion
implements Comparable<ModelVersion> {
    private final int version;

    private ModelVersion(int version) {
        this.version = version;
    }

    @Override
    public int compareTo(ModelVersion mv) {
        return this.version - mv.version;
    }

    public boolean isOlderThan(ModelVersion mv) {
        return this.compareTo(mv) < 0;
    }

    public boolean isNewerThan(ModelVersion mv) {
        return this.compareTo(mv) > 0;
    }

    public boolean isSame(ModelVersion mv) {
        return this.compareTo(mv) == 0;
    }

    public String toString() {
        return String.valueOf(this.version);
    }

    public static ModelVersion valueOf(String s) {
        return s == null ? ModelVersion.zero() : new ModelVersion(Integer.valueOf(s));
    }

    private static ModelVersion zero() {
        return new ModelVersion(0);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.attribute;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum AnnotationRetention {
    ENABLED(true),
    DISABLED(false);

    private final boolean enabled;

    private AnnotationRetention(boolean enabled) {
        this.enabled = enabled;
    }

    public static AnnotationRetention of(boolean enabled) {
        return enabled ? ENABLED : DISABLED;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}


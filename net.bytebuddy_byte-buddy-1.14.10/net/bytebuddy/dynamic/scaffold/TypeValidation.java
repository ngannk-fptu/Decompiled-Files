/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum TypeValidation {
    ENABLED(true),
    DISABLED(false);

    private final boolean enabled;

    private TypeValidation(boolean enabled) {
        this.enabled = enabled;
    }

    public static TypeValidation of(boolean enabled) {
        return enabled ? ENABLED : DISABLED;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.security.password;

public final class Credential {
    public static final Credential NONE = Credential.encrypted("x");
    private final boolean encrypted;
    private final String value;

    public static Credential encrypted(String hash) {
        return new Credential(true, hash);
    }

    public static Credential unencrypted(String password) {
        return new Credential(false, password);
    }

    private Credential(boolean encrypted, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Credential value cannot be null. Use Credential.NONE instead.");
        }
        this.encrypted = encrypted;
        this.value = value;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Credential that = (Credential)o;
        return this.encrypted == that.encrypted && this.value.equals(that.value);
    }

    public int hashCode() {
        return 31 * (this.encrypted ? 1 : 0) + this.value.hashCode();
    }
}


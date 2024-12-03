/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.sso.idp;

import java.util.Objects;

public class PemPrivateKey {
    private String pemKey;

    public PemPrivateKey() {
    }

    public PemPrivateKey(String pemKey) {
        this.pemKey = pemKey;
    }

    public String getPemKey() {
        return this.pemKey;
    }

    public void setPemKey(String pemKey) {
        this.pemKey = pemKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PemPrivateKey that = (PemPrivateKey)o;
        return Objects.equals(this.pemKey, that.pemKey);
    }

    public int hashCode() {
        return Objects.hash(this.pemKey);
    }

    public String toString() {
        return "PemPrivateKey{pemKey='" + this.pemKey + '\'' + '}';
    }
}


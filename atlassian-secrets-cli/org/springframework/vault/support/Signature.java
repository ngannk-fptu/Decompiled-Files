/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Objects;
import org.springframework.util.Assert;

public class Signature {
    private final String signature;

    private Signature(String signature) {
        this.signature = signature;
    }

    public static Signature of(String signature) {
        Assert.hasText(signature, "Signature must not be null or empty");
        return new Signature(signature);
    }

    public String getSignature() {
        return this.signature;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Signature)) {
            return false;
        }
        Signature that = (Signature)o;
        return this.signature.equals(that.signature);
    }

    public int hashCode() {
        return Objects.hash(this.signature);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [signature='").append(this.signature).append('\'');
        sb.append(']');
        return sb.toString();
    }
}


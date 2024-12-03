/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Objects;

public class SignatureValidation {
    private static final SignatureValidation VALID = new SignatureValidation(true);
    private static final SignatureValidation INVALID = new SignatureValidation(false);
    private final boolean state;

    private SignatureValidation(boolean state) {
        this.state = state;
    }

    public static SignatureValidation valid() {
        return VALID;
    }

    public static SignatureValidation invalid() {
        return INVALID;
    }

    public boolean isValid() {
        return this.state;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureValidation)) {
            return false;
        }
        SignatureValidation that = (SignatureValidation)o;
        return this.state == that.state;
    }

    public int hashCode() {
        return Objects.hash(this.state);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [state=").append(this.state);
        sb.append(']');
        return sb.toString();
    }
}


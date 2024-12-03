/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import java.util.Objects;
import org.springframework.util.Assert;

public class Hmac {
    private final String hmac;

    private Hmac(String hmac) {
        this.hmac = hmac;
    }

    public static Hmac of(String hmac) {
        Assert.hasText((String)hmac, (String)"Hmac digest must not be null or empty");
        return new Hmac(hmac);
    }

    public String getHmac() {
        return this.hmac;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hmac)) {
            return false;
        }
        Hmac other = (Hmac)o;
        return this.hmac.equals(other.hmac);
    }

    public int hashCode() {
        return Objects.hash(this.hmac);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [hmac='").append(this.hmac).append('\'');
        sb.append(']');
        return sb.toString();
    }
}


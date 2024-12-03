/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import java.util.Arrays;
import org.springframework.util.Assert;

public class VaultToken {
    private final char[] token;

    protected VaultToken(char[] token) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        Assert.isTrue((token.length > 0 ? 1 : 0) != 0, (String)"Token must not be empty");
        this.token = Arrays.copyOf(token, token.length);
    }

    public static VaultToken of(String token) {
        Assert.hasText((String)token, (String)"Token must not be empty");
        return VaultToken.of(token.toCharArray());
    }

    public static VaultToken of(char[] token) {
        return new VaultToken(token);
    }

    public String getToken() {
        return new String(this.token);
    }

    public char[] toCharArray() {
        return Arrays.copyOf(this.token, this.token.length);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VaultToken)) {
            return false;
        }
        VaultToken that = (VaultToken)o;
        return Arrays.equals(this.token, that.token);
    }

    public int hashCode() {
        return Arrays.hashCode(this.token);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}


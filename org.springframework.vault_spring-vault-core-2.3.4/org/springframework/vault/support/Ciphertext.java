/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import java.util.Objects;
import org.springframework.util.Assert;
import org.springframework.vault.support.VaultTransitContext;

public class Ciphertext {
    private final String ciphertext;
    private final VaultTransitContext context;

    private Ciphertext(String ciphertext, VaultTransitContext context) {
        this.ciphertext = ciphertext;
        this.context = context;
    }

    public static Ciphertext of(String ciphertext) {
        Assert.hasText((String)ciphertext, (String)"Ciphertext must not be null or empty");
        return new Ciphertext(ciphertext, VaultTransitContext.empty());
    }

    public String getCiphertext() {
        return this.ciphertext;
    }

    public VaultTransitContext getContext() {
        return this.context;
    }

    public Ciphertext with(VaultTransitContext context) {
        Assert.notNull((Object)context, (String)"VaultTransitContext must not be null");
        return new Ciphertext(this.getCiphertext(), context);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ciphertext)) {
            return false;
        }
        Ciphertext that = (Ciphertext)o;
        return this.ciphertext.equals(that.ciphertext) && this.context.equals(that.context);
    }

    public int hashCode() {
        return Objects.hash(this.ciphertext, this.context);
    }
}


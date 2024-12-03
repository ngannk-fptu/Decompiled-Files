/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Objects;
import org.springframework.util.Assert;
import org.springframework.vault.support.VaultTransformContext;

public class TransformCiphertext {
    private final String ciphertext;
    private final VaultTransformContext context;

    private TransformCiphertext(String ciphertext, VaultTransformContext context) {
        this.ciphertext = ciphertext;
        this.context = context;
    }

    public static TransformCiphertext of(String ciphertext) {
        Assert.hasText(ciphertext, "Ciphertext must not be null or empty");
        return new TransformCiphertext(ciphertext, VaultTransformContext.empty());
    }

    public String getCiphertext() {
        return this.ciphertext;
    }

    public VaultTransformContext getContext() {
        return this.context;
    }

    public TransformCiphertext with(VaultTransformContext context) {
        Assert.notNull((Object)context, "VaultTransitContext must not be null");
        return new TransformCiphertext(this.getCiphertext(), context);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransformCiphertext)) {
            return false;
        }
        TransformCiphertext that = (TransformCiphertext)o;
        return this.ciphertext.equals(that.ciphertext) && this.context.equals(that.context);
    }

    public int hashCode() {
        return Objects.hash(this.ciphertext, this.context);
    }
}


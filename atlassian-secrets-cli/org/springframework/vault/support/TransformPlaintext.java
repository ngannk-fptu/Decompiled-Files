/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.nio.charset.Charset;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.vault.support.VaultTransformContext;

public class TransformPlaintext {
    private static final TransformPlaintext EMPTY = new TransformPlaintext(new byte[0], VaultTransformContext.empty());
    private final byte[] plaintext;
    private final VaultTransformContext context;

    private TransformPlaintext(byte[] plaintext, VaultTransformContext context) {
        this.plaintext = plaintext;
        this.context = context;
    }

    public static TransformPlaintext empty() {
        return EMPTY;
    }

    public static TransformPlaintext of(byte[] plaintext) {
        Assert.notNull((Object)plaintext, "Plaintext must not be null");
        if (plaintext.length == 0) {
            return TransformPlaintext.empty();
        }
        return new TransformPlaintext(plaintext, VaultTransformContext.empty());
    }

    public static TransformPlaintext of(String plaintext) {
        return TransformPlaintext.of(plaintext, Charset.defaultCharset());
    }

    public static TransformPlaintext of(String plaintext, Charset charset) {
        Assert.notNull((Object)plaintext, "Plaintext must not be null");
        Assert.notNull((Object)charset, "Charset must not be null");
        if (plaintext.length() == 0) {
            return TransformPlaintext.empty();
        }
        return TransformPlaintext.of(plaintext.getBytes(charset));
    }

    public byte[] getPlaintext() {
        return this.plaintext;
    }

    public VaultTransformContext getContext() {
        return this.context;
    }

    public TransformPlaintext with(VaultTransformContext context) {
        Assert.notNull((Object)context, "VaultTransformContext must not be null");
        return new TransformPlaintext(this.getPlaintext(), context);
    }

    public String asString() {
        return this.asString(Charset.defaultCharset());
    }

    public String asString(Charset charset) {
        Assert.notNull((Object)charset, "Charset must not be null");
        return new String(this.getPlaintext(), charset);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransformPlaintext)) {
            return false;
        }
        TransformPlaintext that = (TransformPlaintext)o;
        if (!ObjectUtils.nullSafeEquals(this.plaintext, that.plaintext)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.context, that.context);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.plaintext);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.context);
        return result;
    }
}


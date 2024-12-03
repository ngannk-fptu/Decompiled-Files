/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.util.Assert;
import org.springframework.vault.support.VaultTransitContext;

public class Plaintext {
    private static final Plaintext EMPTY = new Plaintext(new byte[0], VaultTransitContext.empty());
    private final byte[] plaintext;
    private final VaultTransitContext context;

    private Plaintext(byte[] plaintext, VaultTransitContext context) {
        this.plaintext = plaintext;
        this.context = context;
    }

    public static Plaintext empty() {
        return EMPTY;
    }

    public static Plaintext of(byte[] plaintext) {
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        if (plaintext.length == 0) {
            return Plaintext.empty();
        }
        return new Plaintext(plaintext, VaultTransitContext.empty());
    }

    public static Plaintext of(String plaintext) {
        return Plaintext.of(plaintext, Charset.defaultCharset());
    }

    public static Plaintext of(String plaintext, Charset charset) {
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        Assert.notNull((Object)charset, (String)"Charset must not be null");
        if (plaintext.length() == 0) {
            return Plaintext.empty();
        }
        return Plaintext.of(plaintext.getBytes(charset));
    }

    public byte[] getPlaintext() {
        return this.plaintext;
    }

    public VaultTransitContext getContext() {
        return this.context;
    }

    public Plaintext with(VaultTransitContext context) {
        return new Plaintext(this.getPlaintext(), context);
    }

    public String asString() {
        return this.asString(Charset.defaultCharset());
    }

    public String asString(Charset charset) {
        Assert.notNull((Object)charset, (String)"Charset must not be null");
        return new String(this.getPlaintext(), charset);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plaintext)) {
            return false;
        }
        Plaintext plaintext1 = (Plaintext)o;
        return Arrays.equals(this.plaintext, plaintext1.plaintext) && this.context.equals(plaintext1.context);
    }

    public int hashCode() {
        int result = Objects.hash(this.context);
        result = 31 * result + Arrays.hashCode(this.plaintext);
        return result;
    }
}


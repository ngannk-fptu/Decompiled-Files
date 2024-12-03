/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Arrays;
import org.springframework.util.Assert;

public class VaultTransitContext {
    private static final VaultTransitContext EMPTY = new VaultTransitContext(new byte[0], new byte[0]);
    private final byte[] context;
    private final byte[] nonce;

    VaultTransitContext(byte[] context, byte[] nonce) {
        this.context = context;
        this.nonce = nonce;
    }

    public static VaultTransitRequestBuilder builder() {
        return new VaultTransitRequestBuilder();
    }

    public static VaultTransitContext empty() {
        return EMPTY;
    }

    public static VaultTransitContext fromContext(byte[] context) {
        return VaultTransitContext.builder().context(context).build();
    }

    public static VaultTransitContext fromNonce(byte[] nonce) {
        return VaultTransitContext.builder().nonce(nonce).build();
    }

    public byte[] getContext() {
        return this.context;
    }

    public byte[] getNonce() {
        return this.nonce;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VaultTransitContext)) {
            return false;
        }
        VaultTransitContext that = (VaultTransitContext)o;
        return Arrays.equals(this.context, that.context) && Arrays.equals(this.nonce, that.nonce);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.context);
        result = 31 * result + Arrays.hashCode(this.nonce);
        return result;
    }

    public static class VaultTransitRequestBuilder {
        private byte[] context = new byte[0];
        private byte[] nonce = new byte[0];

        VaultTransitRequestBuilder() {
        }

        public VaultTransitRequestBuilder context(byte[] context) {
            Assert.notNull((Object)context, "Context must not be null");
            this.context = context;
            return this;
        }

        public VaultTransitRequestBuilder nonce(byte[] nonce) {
            Assert.notNull((Object)nonce, "Nonce must not be null");
            this.nonce = nonce;
            return this;
        }

        public VaultTransitContext build() {
            return new VaultTransitContext(this.context, this.nonce);
        }
    }
}


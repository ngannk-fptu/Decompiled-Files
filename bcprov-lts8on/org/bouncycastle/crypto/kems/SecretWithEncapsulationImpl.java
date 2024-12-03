/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.util.Arrays;

class SecretWithEncapsulationImpl
implements SecretWithEncapsulation {
    private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
    private final byte[] sessionKey;
    private final byte[] cipher_text;

    public SecretWithEncapsulationImpl(byte[] sessionKey, byte[] cipher_text) {
        this.sessionKey = sessionKey;
        this.cipher_text = cipher_text;
    }

    @Override
    public byte[] getSecret() {
        byte[] clone = Arrays.clone(this.sessionKey);
        this.checkDestroyed();
        return clone;
    }

    @Override
    public byte[] getEncapsulation() {
        byte[] clone = Arrays.clone(this.cipher_text);
        this.checkDestroyed();
        return clone;
    }

    @Override
    public void destroy() throws DestroyFailedException {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            Arrays.clear(this.sessionKey);
            Arrays.clear(this.cipher_text);
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    void checkDestroyed() {
        if (this.isDestroyed()) {
            throw new IllegalStateException("data has been destroyed");
        }
    }
}


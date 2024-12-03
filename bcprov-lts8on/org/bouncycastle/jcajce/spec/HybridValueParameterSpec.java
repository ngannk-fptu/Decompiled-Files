/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.Destroyable;
import org.bouncycastle.util.Arrays;

public class HybridValueParameterSpec
implements AlgorithmParameterSpec,
Destroyable {
    private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
    private volatile byte[] t;
    private volatile AlgorithmParameterSpec baseSpec;

    public HybridValueParameterSpec(byte[] t, AlgorithmParameterSpec baseSpec) {
        this.t = t;
        this.baseSpec = baseSpec;
    }

    public byte[] getT() {
        byte[] tVal = this.t;
        this.checkDestroyed();
        return tVal;
    }

    public AlgorithmParameterSpec getBaseParameterSpec() {
        AlgorithmParameterSpec rv = this.baseSpec;
        this.checkDestroyed();
        return rv;
    }

    @Override
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    @Override
    public void destroy() {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            Arrays.clear(this.t);
            this.t = null;
            this.baseSpec = null;
        }
    }

    private void checkDestroyed() {
        if (this.isDestroyed()) {
            throw new IllegalStateException("spec has been destroyed");
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.dispose;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class NativeDisposer
implements Runnable {
    private final long reference;
    private boolean called = false;

    public NativeDisposer(long reference) {
        this.reference = reference;
    }

    @Override
    public void run() {
        if (this.called) {
            return;
        }
        this.called = true;
        this.dispose(this.reference);
    }

    protected abstract void dispose(long var1);
}


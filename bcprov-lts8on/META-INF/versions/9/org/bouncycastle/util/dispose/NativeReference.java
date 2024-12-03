/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.dispose;

import org.bouncycastle.util.dispose.Disposable;
import org.bouncycastle.util.dispose.DisposalDaemon;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class NativeReference
implements Disposable {
    protected final long reference;
    protected final String label;

    public NativeReference(long reference, String name) {
        this.reference = reference;
        this.label = "Reference(" + name + ") 0x" + Long.toHexString(reference);
        DisposalDaemon.addDisposable(this);
    }

    @Override
    public final Runnable getDisposeAction() {
        return this.createAction();
    }

    protected abstract Runnable createAction();

    public long getReference() {
        return this.reference;
    }

    public String toString() {
        return this.label;
    }
}


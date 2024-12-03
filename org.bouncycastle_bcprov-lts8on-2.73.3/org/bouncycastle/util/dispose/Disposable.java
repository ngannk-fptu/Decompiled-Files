/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.dispose;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface Disposable {
    public Runnable getDisposeAction();
}


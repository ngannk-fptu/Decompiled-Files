/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class AsymmetricKeyParameter
implements CipherParameters {
    boolean privateKey;

    public AsymmetricKeyParameter(boolean privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isPrivate() {
        return this.privateKey;
    }
}


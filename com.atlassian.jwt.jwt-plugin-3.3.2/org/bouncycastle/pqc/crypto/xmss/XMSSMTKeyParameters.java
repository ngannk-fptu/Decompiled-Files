/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class XMSSMTKeyParameters
extends AsymmetricKeyParameter {
    private final String treeDigest;

    public XMSSMTKeyParameters(boolean bl, String string) {
        super(bl);
        this.treeDigest = string;
    }

    public String getTreeDigest() {
        return this.treeDigest;
    }
}


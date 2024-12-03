/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.dvcs.TargetEtcChain
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.TargetEtcChain;

public class TargetChain {
    private final TargetEtcChain certs;

    public TargetChain(TargetEtcChain certs) {
        this.certs = certs;
    }

    public TargetEtcChain toASN1Structure() {
        return this.certs;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.OutputStream;

public abstract class ASN1Generator {
    protected OutputStream _out;

    public ASN1Generator(OutputStream out) {
        this._out = out;
    }

    public abstract OutputStream getRawOutputStream();
}


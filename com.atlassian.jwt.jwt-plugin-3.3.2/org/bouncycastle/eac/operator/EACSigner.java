/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface EACSigner {
    public ASN1ObjectIdentifier getUsageIdentifier();

    public OutputStream getOutputStream();

    public byte[] getSignature();
}


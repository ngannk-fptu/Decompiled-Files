/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

public interface OutputEncryptor {
    public AlgorithmIdentifier getAlgorithmIdentifier();

    public OutputStream getOutputStream(OutputStream var1);

    public GenericKey getKey();
}


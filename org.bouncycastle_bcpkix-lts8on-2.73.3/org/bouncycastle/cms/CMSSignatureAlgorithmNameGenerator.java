/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface CMSSignatureAlgorithmNameGenerator {
    public String getSignatureName(AlgorithmIdentifier var1, AlgorithmIdentifier var2);
}


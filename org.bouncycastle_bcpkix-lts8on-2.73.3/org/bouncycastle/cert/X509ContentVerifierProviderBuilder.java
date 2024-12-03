/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;

public interface X509ContentVerifierProviderBuilder {
    public ContentVerifierProvider build(SubjectPublicKeyInfo var1) throws OperatorCreationException;

    public ContentVerifierProvider build(X509CertificateHolder var1) throws OperatorCreationException;
}


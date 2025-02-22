/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.TBSCertificate;

public interface BCX509Certificate {
    public X500Name getIssuerX500Name();

    public TBSCertificate getTBSCertificateNative();

    public X500Name getSubjectX500Name();
}


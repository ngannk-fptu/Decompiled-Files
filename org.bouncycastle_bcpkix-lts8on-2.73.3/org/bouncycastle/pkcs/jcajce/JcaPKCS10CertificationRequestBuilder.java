/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.pkcs.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

public class JcaPKCS10CertificationRequestBuilder
extends PKCS10CertificationRequestBuilder {
    public JcaPKCS10CertificationRequestBuilder(X500Name subject, PublicKey publicKey) {
        super(subject, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public JcaPKCS10CertificationRequestBuilder(X500Principal subject, PublicKey publicKey) {
        super(X500Name.getInstance((Object)subject.getEncoded()), SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
}


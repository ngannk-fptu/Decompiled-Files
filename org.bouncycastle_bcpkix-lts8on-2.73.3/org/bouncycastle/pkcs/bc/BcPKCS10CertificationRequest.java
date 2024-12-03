/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.CertificationRequest
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.util.PublicKeyFactory
 */
package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;

public class BcPKCS10CertificationRequest
extends PKCS10CertificationRequest {
    public BcPKCS10CertificationRequest(CertificationRequest certificationRequest) {
        super(certificationRequest);
    }

    public BcPKCS10CertificationRequest(byte[] encoding) throws IOException {
        super(encoding);
    }

    public BcPKCS10CertificationRequest(PKCS10CertificationRequest requestHolder) {
        super(requestHolder.toASN1Structure());
    }

    public AsymmetricKeyParameter getPublicKey() throws PKCSException {
        try {
            return PublicKeyFactory.createKey((SubjectPublicKeyInfo)this.getSubjectPublicKeyInfo());
        }
        catch (IOException e) {
            throw new PKCSException("error extracting key encoding: " + e.getMessage(), e);
        }
    }
}


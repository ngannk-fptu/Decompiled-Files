/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.crmf.EncryptedValue
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OutputEncryptor;

public class JcaEncryptedValueBuilder
extends EncryptedValueBuilder {
    public JcaEncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor) {
        super(wrapper, encryptor);
    }

    public EncryptedValue build(X509Certificate certificate) throws CertificateEncodingException, CRMFException {
        return this.build(new JcaX509CertificateHolder(certificate));
    }

    public EncryptedValue build(PrivateKey privateKey) throws CertificateEncodingException, CRMFException {
        return this.build(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()));
    }
}


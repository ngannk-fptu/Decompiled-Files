/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.crmf.EncryptedValue
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.util.PrivateKeyInfoFactory
 */
package org.bouncycastle.cert.crmf.bc;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OutputEncryptor;

public class BcEncryptedValueBuilder
extends EncryptedValueBuilder {
    public BcEncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor) {
        super(wrapper, encryptor);
    }

    public EncryptedValue build(X509Certificate certificate) throws CertificateEncodingException, CRMFException {
        return this.build(new JcaX509CertificateHolder(certificate));
    }

    public EncryptedValue build(AsymmetricKeyParameter privateKey) throws CRMFException, IOException {
        return this.build(PrivateKeyInfoFactory.createPrivateKeyInfo((AsymmetricKeyParameter)privateKey));
    }
}


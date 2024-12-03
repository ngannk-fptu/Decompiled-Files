/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateId
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate$Builder
 */
package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.jcajce.JcaITSPublicVerificationKey;
import org.bouncycastle.its.jcajce.JceITSPublicEncryptionKey;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

public class JcaITSExplicitCertificateBuilder
extends ITSExplicitCertificateBuilder {
    private JcaJceHelper helper;

    public JcaITSExplicitCertificateBuilder(ITSContentSigner signer, ToBeSignedCertificate.Builder tbsCertificate) {
        this(signer, tbsCertificate, (JcaJceHelper)new DefaultJcaJceHelper());
    }

    private JcaITSExplicitCertificateBuilder(ITSContentSigner signer, ToBeSignedCertificate.Builder tbsCertificate, JcaJceHelper helper) {
        super(signer, tbsCertificate);
        this.helper = helper;
    }

    public JcaITSExplicitCertificateBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcaITSExplicitCertificateBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKey verificationKey) {
        return this.build(certificateId, verificationKey, null);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKey verificationKey, ECPublicKey encryptionKey) {
        JceITSPublicEncryptionKey publicEncryptionKey = null;
        if (encryptionKey != null) {
            publicEncryptionKey = new JceITSPublicEncryptionKey(encryptionKey, this.helper);
        }
        return super.build(certificateId, new JcaITSPublicVerificationKey(verificationKey, this.helper), publicEncryptionKey);
    }
}


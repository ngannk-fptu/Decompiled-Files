/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaSimpleSignerInfoGeneratorBuilder {
    private Helper helper = new Helper();
    private boolean hasNoSignedAttributes;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    private AlgorithmIdentifier contentDigest;

    public JcaSimpleSignerInfoGeneratorBuilder setProvider(String providerName) throws OperatorCreationException {
        this.helper = new NamedHelper(providerName);
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setProvider(Provider provider) throws OperatorCreationException {
        this.helper = new ProviderHelper(provider);
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.hasNoSignedAttributes = hasNoSignedAttributes;
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier contentDigest) {
        this.contentDigest = contentDigest;
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(AttributeTable attrTable) {
        this.signedGen = new DefaultSignedAttributeTableGenerator(attrTable);
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }

    public SignerInfoGenerator build(String algorithmName, PrivateKey privateKey, X509CertificateHolder certificate) throws OperatorCreationException {
        privateKey = CMSUtils.cleanPrivateKey(privateKey);
        ContentSigner contentSigner = this.helper.createContentSigner(algorithmName, privateKey);
        return this.configureAndBuild().build(contentSigner, certificate);
    }

    public SignerInfoGenerator build(String algorithmName, PrivateKey privateKey, X509Certificate certificate) throws OperatorCreationException, CertificateEncodingException {
        privateKey = CMSUtils.cleanPrivateKey(privateKey);
        ContentSigner contentSigner = this.helper.createContentSigner(algorithmName, privateKey);
        return this.configureAndBuild().build(contentSigner, new JcaX509CertificateHolder(certificate));
    }

    public SignerInfoGenerator build(String algorithmName, PrivateKey privateKey, byte[] keyIdentifier) throws OperatorCreationException {
        privateKey = CMSUtils.cleanPrivateKey(privateKey);
        ContentSigner contentSigner = this.helper.createContentSigner(algorithmName, privateKey);
        return this.configureAndBuild().build(contentSigner, keyIdentifier);
    }

    private SignerInfoGeneratorBuilder configureAndBuild() throws OperatorCreationException {
        SignerInfoGeneratorBuilder infoGeneratorBuilder = new SignerInfoGeneratorBuilder(this.helper.createDigestCalculatorProvider());
        infoGeneratorBuilder.setDirectSignature(this.hasNoSignedAttributes);
        infoGeneratorBuilder.setContentDigest(this.contentDigest);
        infoGeneratorBuilder.setSignedAttributeGenerator(this.signedGen);
        infoGeneratorBuilder.setUnsignedAttributeGenerator(this.unsignedGen);
        return infoGeneratorBuilder;
    }

    private static class Helper {
        private Helper() {
        }

        ContentSigner createContentSigner(String algorithm, PrivateKey privateKey) throws OperatorCreationException {
            privateKey = CMSUtils.cleanPrivateKey(privateKey);
            return new JcaContentSignerBuilder(algorithm).build(privateKey);
        }

        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().build();
        }
    }

    private static class NamedHelper
    extends Helper {
        private final String providerName;

        public NamedHelper(String providerName) {
            this.providerName = providerName;
        }

        @Override
        ContentSigner createContentSigner(String algorithm, PrivateKey privateKey) throws OperatorCreationException {
            privateKey = CMSUtils.cleanPrivateKey(privateKey);
            return new JcaContentSignerBuilder(algorithm).setProvider(this.providerName).build(privateKey);
        }

        @Override
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
        }
    }

    private static class ProviderHelper
    extends Helper {
        private final Provider provider;

        public ProviderHelper(Provider provider) {
            this.provider = provider;
        }

        @Override
        ContentSigner createContentSigner(String algorithm, PrivateKey privateKey) throws OperatorCreationException {
            privateKey = CMSUtils.cleanPrivateKey(privateKey);
            return new JcaContentSignerBuilder(algorithm).setProvider(this.provider).build(privateKey);
        }

        @Override
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo
 *  org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.jcajce.spec.MQVParameterSpec
 *  org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec
 *  org.bouncycastle.util.Pack
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipient;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.KeyMaterialGenerator;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.RFC5753KeyMaterialGenerator;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Pack;

public abstract class JceKeyAgreeRecipient
implements KeyAgreeRecipient {
    private static final Set possibleOldMessages = new HashSet();
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    private SecretKeySizeProvider keySizeProvider;
    private AlgorithmIdentifier privKeyAlgID;
    private static KeyMaterialGenerator old_ecc_cms_Generator;
    private static KeyMaterialGenerator simple_ecc_cmsGenerator;
    private static KeyMaterialGenerator ecc_cms_Generator;

    public JceKeyAgreeRecipient(PrivateKey recipientKey) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.keySizeProvider = new DefaultSecretKeySizeProvider();
        this.privKeyAlgID = null;
        this.recipientKey = CMSUtils.cleanPrivateKey(recipientKey);
    }

    public JceKeyAgreeRecipient setProvider(Provider provider) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyAgreeRecipient setProvider(String providerName) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKeyAgreeRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKeyAgreeRecipient setContentProvider(String providerName) {
        this.contentHelper = CMSUtils.createContentHelper(providerName);
        return this;
    }

    public JceKeyAgreeRecipient setPrivateKeyAlgorithmIdentifier(AlgorithmIdentifier privKeyAlgID) {
        this.privKeyAlgID = privKeyAlgID;
        return this;
    }

    private SecretKey calculateAgreedWrapKey(AlgorithmIdentifier keyEncAlg, AlgorithmIdentifier wrapAlg, PublicKey senderPublicKey, ASN1OctetString userKeyingMaterial, PrivateKey receiverPrivateKey, KeyMaterialGenerator kmGen) throws CMSException, GeneralSecurityException, IOException {
        receiverPrivateKey = CMSUtils.cleanPrivateKey(receiverPrivateKey);
        if (CMSUtils.isMQV(keyEncAlg.getAlgorithm())) {
            byte[] ukmKeyingMaterial;
            MQVuserKeyingMaterial ukm = MQVuserKeyingMaterial.getInstance((Object)userKeyingMaterial.getOctets());
            SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(this.getPrivateKeyAlgorithmIdentifier(), ukm.getEphemeralPublicKey().getPublicKeyData());
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubInfo.getEncoded());
            KeyFactory fact = this.helper.createKeyFactory(keyEncAlg.getAlgorithm());
            PublicKey ephemeralKey = fact.generatePublic(pubSpec);
            KeyAgreement agreement = this.helper.createKeyAgreement(keyEncAlg.getAlgorithm());
            byte[] byArray = ukmKeyingMaterial = ukm.getAddedukm() != null ? ukm.getAddedukm().getOctets() : null;
            if (kmGen == old_ecc_cms_Generator) {
                ukmKeyingMaterial = old_ecc_cms_Generator.generateKDFMaterial(wrapAlg, this.keySizeProvider.getKeySize(wrapAlg), ukmKeyingMaterial);
            }
            agreement.init((Key)receiverPrivateKey, (AlgorithmParameterSpec)new MQVParameterSpec(receiverPrivateKey, ephemeralKey, ukmKeyingMaterial));
            agreement.doPhase(senderPublicKey, true);
            return agreement.generateSecret(wrapAlg.getAlgorithm().getId());
        }
        KeyAgreement agreement = this.helper.createKeyAgreement(keyEncAlg.getAlgorithm());
        UserKeyingMaterialSpec userKeyingMaterialSpec = null;
        if (CMSUtils.isEC(keyEncAlg.getAlgorithm())) {
            if (userKeyingMaterial != null) {
                byte[] ukmKeyingMaterial = kmGen.generateKDFMaterial(wrapAlg, this.keySizeProvider.getKeySize(wrapAlg), userKeyingMaterial.getOctets());
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(ukmKeyingMaterial);
            } else {
                byte[] ukmKeyingMaterial = kmGen.generateKDFMaterial(wrapAlg, this.keySizeProvider.getKeySize(wrapAlg), null);
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(ukmKeyingMaterial);
            }
        } else if (CMSUtils.isRFC2631(keyEncAlg.getAlgorithm())) {
            if (userKeyingMaterial != null) {
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(userKeyingMaterial.getOctets());
            }
        } else if (CMSUtils.isGOST(keyEncAlg.getAlgorithm())) {
            if (userKeyingMaterial != null) {
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(userKeyingMaterial.getOctets());
            }
        } else {
            throw new CMSException("Unknown key agreement algorithm: " + keyEncAlg.getAlgorithm());
        }
        agreement.init((Key)receiverPrivateKey, (AlgorithmParameterSpec)userKeyingMaterialSpec);
        agreement.doPhase(senderPublicKey, true);
        return agreement.generateSecret(wrapAlg.getAlgorithm().getId());
    }

    protected Key unwrapSessionKey(ASN1ObjectIdentifier wrapAlg, SecretKey agreedKey, ASN1ObjectIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException, InvalidKeyException, NoSuchAlgorithmException {
        Cipher keyCipher = this.helper.createCipher(wrapAlg);
        keyCipher.init(4, agreedKey);
        return keyCipher.unwrap(encryptedContentEncryptionKey, this.helper.getBaseCipherName(contentEncryptionAlgorithm), 3);
    }

    /*
     * Exception decompiling
     */
    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier contentEncryptionAlgorithm, SubjectPublicKeyInfo senderKey, ASN1OctetString userKeyingMaterial, byte[] encryptedContentEncryptionKey) throws CMSException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier() {
        if (this.privKeyAlgID == null) {
            this.privKeyAlgID = PrivateKeyInfo.getInstance((Object)this.recipientKey.getEncoded()).getPrivateKeyAlgorithm();
        }
        return this.privKeyAlgID;
    }

    static {
        possibleOldMessages.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        possibleOldMessages.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        old_ecc_cms_Generator = new KeyMaterialGenerator(){

            @Override
            public byte[] generateKDFMaterial(AlgorithmIdentifier keyAlgorithm, int keySize, byte[] userKeyMaterialParameters) {
                ECCCMSSharedInfo eccInfo = new ECCCMSSharedInfo(new AlgorithmIdentifier(keyAlgorithm.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), userKeyMaterialParameters, Pack.intToBigEndian((int)keySize));
                try {
                    return eccInfo.getEncoded("DER");
                }
                catch (IOException e) {
                    throw new IllegalStateException("Unable to create KDF material: " + e);
                }
            }
        };
        simple_ecc_cmsGenerator = new KeyMaterialGenerator(){

            @Override
            public byte[] generateKDFMaterial(AlgorithmIdentifier keyAlgorithm, int keySize, byte[] userKeyMaterialParameters) {
                return userKeyMaterialParameters;
            }
        };
        ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
    }
}


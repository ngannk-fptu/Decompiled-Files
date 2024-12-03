/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cms.PasswordRecipientInfo
 *  org.bouncycastle.asn1.cms.RecipientInfo
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.cms.PasswordRecipientInformation;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.util.Arrays;

public abstract class PasswordRecipientInfoGenerator
implements RecipientInfoGenerator {
    protected char[] password;
    private AlgorithmIdentifier keyDerivationAlgorithm;
    private ASN1ObjectIdentifier kekAlgorithm;
    private SecureRandom random;
    private int schemeID;
    private int keySize;
    private int blockSize;
    private PasswordRecipient.PRF prf;
    private byte[] salt;
    private int iterationCount;

    protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password) {
        this(kekAlgorithm, password, PasswordRecipientInfoGenerator.getKeySize(kekAlgorithm), (Integer)PasswordRecipientInformation.BLOCKSIZES.get(kekAlgorithm));
    }

    protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password, int keySize, int blockSize) {
        this.password = password;
        this.schemeID = 1;
        this.kekAlgorithm = kekAlgorithm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.prf = PasswordRecipient.PRF.HMacSHA1;
        this.iterationCount = 1024;
    }

    private static int getKeySize(ASN1ObjectIdentifier kekAlgorithm) {
        Integer size = (Integer)PasswordRecipientInformation.KEYSIZES.get(kekAlgorithm);
        if (size == null) {
            throw new IllegalArgumentException("cannot find key size for algorithm: " + kekAlgorithm);
        }
        return size;
    }

    public PasswordRecipientInfoGenerator setPasswordConversionScheme(int schemeID) {
        this.schemeID = schemeID;
        return this;
    }

    public PasswordRecipientInfoGenerator setPRF(PasswordRecipient.PRF prf) {
        this.prf = prf;
        return this;
    }

    public PasswordRecipientInfoGenerator setSaltAndIterationCount(byte[] salt, int iterationCount) {
        this.salt = Arrays.clone((byte[])salt);
        this.iterationCount = iterationCount;
        return this;
    }

    public PasswordRecipientInfoGenerator setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override
    public RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        byte[] iv = new byte[this.blockSize];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(iv);
        if (this.salt == null) {
            this.salt = new byte[20];
            this.random.nextBytes(this.salt);
        }
        this.keyDerivationAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(this.salt, this.iterationCount, this.prf.prfAlgID));
        byte[] derivedKey = this.calculateDerivedKey(this.schemeID, this.keyDerivationAlgorithm, this.keySize);
        AlgorithmIdentifier kekAlgorithmId = new AlgorithmIdentifier(this.kekAlgorithm, (ASN1Encodable)new DEROctetString(iv));
        byte[] encryptedKeyBytes = this.generateEncryptedBytes(kekAlgorithmId, derivedKey, contentEncryptionKey);
        DEROctetString encryptedKey = new DEROctetString(encryptedKeyBytes);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)this.kekAlgorithm);
        v.add((ASN1Encodable)new DEROctetString(iv));
        AlgorithmIdentifier keyEncryptionAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_PWRI_KEK, (ASN1Encodable)new DERSequence(v));
        return new RecipientInfo(new PasswordRecipientInfo(this.keyDerivationAlgorithm, keyEncryptionAlgorithm, (ASN1OctetString)encryptedKey));
    }

    protected abstract byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException;

    protected abstract byte[] generateEncryptedBytes(AlgorithmIdentifier var1, byte[] var2, GenericKey var3) throws CMSException;
}


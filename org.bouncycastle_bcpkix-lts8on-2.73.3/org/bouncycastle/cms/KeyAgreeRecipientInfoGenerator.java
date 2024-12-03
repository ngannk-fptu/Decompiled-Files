/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo
 *  org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey
 *  org.bouncycastle.asn1.cms.OriginatorPublicKey
 *  org.bouncycastle.asn1.cms.RecipientInfo
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;

public abstract class KeyAgreeRecipientInfoGenerator
implements RecipientInfoGenerator {
    private ASN1ObjectIdentifier keyAgreementOID;
    private ASN1ObjectIdentifier keyEncryptionOID;
    private SubjectPublicKeyInfo originatorKeyInfo;

    protected KeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier keyAgreementOID, SubjectPublicKeyInfo originatorKeyInfo, ASN1ObjectIdentifier keyEncryptionOID) {
        this.originatorKeyInfo = originatorKeyInfo;
        this.keyAgreementOID = keyAgreementOID;
        this.keyEncryptionOID = keyEncryptionOID;
    }

    @Override
    public RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        OriginatorIdentifierOrKey originator = new OriginatorIdentifierOrKey(this.createOriginatorPublicKey(this.originatorKeyInfo));
        AlgorithmIdentifier keyEncAlg = CMSUtils.isDES(this.keyEncryptionOID.getId()) || this.keyEncryptionOID.equals((ASN1Primitive)PKCSObjectIdentifiers.id_alg_CMSRC2wrap) ? new AlgorithmIdentifier(this.keyEncryptionOID, (ASN1Encodable)DERNull.INSTANCE) : (CMSUtils.isGOST(this.keyAgreementOID) ? new AlgorithmIdentifier(this.keyEncryptionOID, (ASN1Encodable)new Gost2814789KeyWrapParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet)) : new AlgorithmIdentifier(this.keyEncryptionOID));
        AlgorithmIdentifier keyAgreeAlg = new AlgorithmIdentifier(this.keyAgreementOID, (ASN1Encodable)keyEncAlg);
        ASN1Sequence recipients = this.generateRecipientEncryptedKeys(keyAgreeAlg, keyEncAlg, contentEncryptionKey);
        byte[] userKeyingMaterial = this.getUserKeyingMaterial(keyAgreeAlg);
        if (userKeyingMaterial != null) {
            return new RecipientInfo(new KeyAgreeRecipientInfo(originator, (ASN1OctetString)new DEROctetString(userKeyingMaterial), keyAgreeAlg, recipients));
        }
        return new RecipientInfo(new KeyAgreeRecipientInfo(originator, null, keyAgreeAlg, recipients));
    }

    protected OriginatorPublicKey createOriginatorPublicKey(SubjectPublicKeyInfo originatorKeyInfo) {
        return new OriginatorPublicKey(originatorKeyInfo.getAlgorithm(), originatorKeyInfo.getPublicKeyData().getBytes());
    }

    protected abstract ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier var1, AlgorithmIdentifier var2, GenericKey var3) throws CMSException;

    protected abstract byte[] getUserKeyingMaterial(AlgorithmIdentifier var1) throws CMSException;
}


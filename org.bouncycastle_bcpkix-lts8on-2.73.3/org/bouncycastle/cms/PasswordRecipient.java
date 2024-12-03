/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientOperator;

public interface PasswordRecipient
extends Recipient {
    public static final int PKCS5_SCHEME2 = 0;
    public static final int PKCS5_SCHEME2_UTF8 = 1;

    public byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException;

    public RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4) throws CMSException;

    public int getPasswordConversionScheme();

    public char[] getPassword();

    public static final class PRF {
        public static final PRF HMacSHA1 = new PRF("HMacSHA1", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE));
        public static final PRF HMacSHA224 = new PRF("HMacSHA224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, (ASN1Encodable)DERNull.INSTANCE));
        public static final PRF HMacSHA256 = new PRF("HMacSHA256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, (ASN1Encodable)DERNull.INSTANCE));
        public static final PRF HMacSHA384 = new PRF("HMacSHA384", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, (ASN1Encodable)DERNull.INSTANCE));
        public static final PRF HMacSHA512 = new PRF("HMacSHA512", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE));
        private final String hmac;
        final AlgorithmIdentifier prfAlgID;

        private PRF(String hmac, AlgorithmIdentifier prfAlgID) {
            this.hmac = hmac;
            this.prfAlgID = prfAlgID;
        }

        public String getName() {
            return this.hmac;
        }

        public AlgorithmIdentifier getAlgorithmID() {
            return this.prfAlgID;
        }
    }
}


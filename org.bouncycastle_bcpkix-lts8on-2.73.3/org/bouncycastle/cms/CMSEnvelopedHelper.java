/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.KEKRecipientInfo
 *  org.bouncycastle.asn1.cms.KEMRecipientInfo
 *  org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo
 *  org.bouncycastle.asn1.cms.KeyTransRecipientInfo
 *  org.bouncycastle.asn1.cms.OtherRecipientInfo
 *  org.bouncycastle.asn1.cms.PasswordRecipientInfo
 *  org.bouncycastle.asn1.cms.RecipientInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.KEKRecipientInformation;
import org.bouncycastle.cms.KEMRecipientInformation;
import org.bouncycastle.cms.KeyAgreeRecipientInformation;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.PasswordRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.operator.DigestCalculator;

class CMSEnvelopedHelper {
    CMSEnvelopedHelper() {
    }

    static RecipientInformationStore buildRecipientInformationStore(ASN1Set recipientInfos, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable) {
        return CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, messageAlgorithm, secureReadable, null);
    }

    static RecipientInformationStore buildRecipientInformationStore(ASN1Set recipientInfos, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        ArrayList<RecipientInformation> infos = new ArrayList<RecipientInformation>();
        for (int i = 0; i != recipientInfos.size(); ++i) {
            RecipientInfo info = RecipientInfo.getInstance((Object)recipientInfos.getObjectAt(i));
            CMSEnvelopedHelper.readRecipientInfo(infos, info, messageAlgorithm, secureReadable, additionalData);
        }
        return new RecipientInformationStore(infos);
    }

    private static void readRecipientInfo(List infos, RecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        ASN1Encodable recipInfo = info.getInfo();
        if (recipInfo instanceof KeyTransRecipientInfo) {
            infos.add(new KeyTransRecipientInformation((KeyTransRecipientInfo)recipInfo, messageAlgorithm, secureReadable, additionalData));
        } else if (recipInfo instanceof OtherRecipientInfo) {
            OtherRecipientInfo otherRecipientInfo = OtherRecipientInfo.getInstance((Object)recipInfo);
            if (CMSObjectIdentifiers.id_ori_kem.equals((ASN1Primitive)otherRecipientInfo.getType())) {
                infos.add(new KEMRecipientInformation(KEMRecipientInfo.getInstance((Object)otherRecipientInfo.getValue()), messageAlgorithm, secureReadable, additionalData));
            }
        } else if (recipInfo instanceof KEKRecipientInfo) {
            infos.add(new KEKRecipientInformation((KEKRecipientInfo)recipInfo, messageAlgorithm, secureReadable, additionalData));
        } else if (recipInfo instanceof KeyAgreeRecipientInfo) {
            KeyAgreeRecipientInformation.readRecipientInfo(infos, (KeyAgreeRecipientInfo)recipInfo, messageAlgorithm, secureReadable, additionalData);
        } else if (recipInfo instanceof PasswordRecipientInfo) {
            infos.add(new PasswordRecipientInformation((PasswordRecipientInfo)recipInfo, messageAlgorithm, secureReadable, additionalData));
        }
    }

    static class CMSAuthenticatedSecureReadable
    implements CMSSecureReadable {
        private AlgorithmIdentifier algorithm;
        private final ASN1ObjectIdentifier contentType;
        private CMSReadable readable;

        CMSAuthenticatedSecureReadable(AlgorithmIdentifier algorithm, ASN1ObjectIdentifier contentType, CMSReadable readable) {
            this.algorithm = algorithm;
            this.contentType = contentType;
            this.readable = readable;
        }

        @Override
        public ASN1ObjectIdentifier getContentType() {
            return this.contentType;
        }

        @Override
        public InputStream getInputStream() throws IOException, CMSException {
            return this.readable.getInputStream();
        }
    }

    static class CMSDigestAuthenticatedSecureReadable
    implements CMSSecureReadable {
        private DigestCalculator digestCalculator;
        private final ASN1ObjectIdentifier contentType;
        private CMSReadable readable;

        public CMSDigestAuthenticatedSecureReadable(DigestCalculator digestCalculator, ASN1ObjectIdentifier contentType, CMSReadable readable) {
            this.digestCalculator = digestCalculator;
            this.contentType = contentType;
            this.readable = readable;
        }

        @Override
        public ASN1ObjectIdentifier getContentType() {
            return this.contentType;
        }

        @Override
        public InputStream getInputStream() throws IOException, CMSException {
            return new FilterInputStream(this.readable.getInputStream()){

                @Override
                public int read() throws IOException {
                    int b = this.in.read();
                    if (b >= 0) {
                        digestCalculator.getOutputStream().write(b);
                    }
                    return b;
                }

                @Override
                public int read(byte[] inBuf, int inOff, int inLen) throws IOException {
                    int n = this.in.read(inBuf, inOff, inLen);
                    if (n >= 0) {
                        digestCalculator.getOutputStream().write(inBuf, inOff, n);
                    }
                    return n;
                }
            };
        }

        public byte[] getDigest() {
            return this.digestCalculator.getDigest();
        }
    }

    static class CMSEnvelopedSecureReadable
    implements CMSSecureReadable {
        private AlgorithmIdentifier algorithm;
        private final ASN1ObjectIdentifier contentType;
        private CMSReadable readable;

        CMSEnvelopedSecureReadable(AlgorithmIdentifier algorithm, ASN1ObjectIdentifier contentType, CMSReadable readable) {
            this.algorithm = algorithm;
            this.contentType = contentType;
            this.readable = readable;
        }

        @Override
        public ASN1ObjectIdentifier getContentType() {
            return this.contentType;
        }

        @Override
        public InputStream getInputStream() throws IOException, CMSException {
            return this.readable.getInputStream();
        }
    }
}


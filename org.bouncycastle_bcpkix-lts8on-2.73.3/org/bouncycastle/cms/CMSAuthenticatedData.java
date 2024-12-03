/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.AuthenticatedData
 *  org.bouncycastle.asn1.cms.CMSAlgorithmProtection
 *  org.bouncycastle.asn1.cms.CMSAttributes
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSAuthenticatedData
implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier macAlg;
    private ASN1Set authAttrs;
    private ASN1Set unauthAttrs;
    private byte[] mac;
    private OriginatorInformation originatorInfo;

    public CMSAuthenticatedData(byte[] authData) throws CMSException {
        this(CMSUtils.readContentInfo(authData));
    }

    public CMSAuthenticatedData(byte[] authData, DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this(CMSUtils.readContentInfo(authData), digestCalculatorProvider);
    }

    public CMSAuthenticatedData(InputStream authData) throws CMSException {
        this(CMSUtils.readContentInfo(authData));
    }

    public CMSAuthenticatedData(InputStream authData, DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this(CMSUtils.readContentInfo(authData), digestCalculatorProvider);
    }

    public CMSAuthenticatedData(ContentInfo contentInfo) throws CMSException {
        this(contentInfo, null);
    }

    public CMSAuthenticatedData(ContentInfo contentInfo, DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this.contentInfo = contentInfo;
        AuthenticatedData authData = AuthenticatedData.getInstance((Object)contentInfo.getContent());
        if (authData.getOriginatorInfo() != null) {
            this.originatorInfo = new OriginatorInformation(authData.getOriginatorInfo());
        }
        ASN1Set recipientInfos = authData.getRecipientInfos();
        this.macAlg = authData.getMacAlgorithm();
        this.authAttrs = authData.getAuthAttrs();
        this.mac = authData.getMac().getOctets();
        this.unauthAttrs = authData.getUnauthAttrs();
        ContentInfo encInfo = authData.getEncapsulatedContentInfo();
        CMSProcessableByteArray readable = new CMSProcessableByteArray(encInfo.getContentType(), ASN1OctetString.getInstance((Object)encInfo.getContent()).getOctets());
        if (this.authAttrs != null) {
            if (digestCalculatorProvider == null) {
                throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
            }
            AttributeTable table = new AttributeTable(this.authAttrs);
            ASN1EncodableVector protectionAttributes = table.getAll(CMSAttributes.cmsAlgorithmProtect);
            if (protectionAttributes.size() > 1) {
                throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }
            if (protectionAttributes.size() > 0) {
                Attribute attr = Attribute.getInstance((Object)protectionAttributes.get(0));
                if (attr.getAttrValues().size() != 1) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                }
                CMSAlgorithmProtection algorithmProtection = CMSAlgorithmProtection.getInstance((Object)attr.getAttributeValues()[0]);
                if (!CMSUtils.isEquivalent(algorithmProtection.getDigestAlgorithm(), authData.getDigestAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                }
                if (!CMSUtils.isEquivalent(algorithmProtection.getMacAlgorithm(), this.macAlg)) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for macAlgorithm");
                }
            }
            try {
                CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable secureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(digestCalculatorProvider.get(authData.getDigestAlgorithm()), encInfo.getContentType(), readable);
                this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable, new AuthAttributesProvider(){

                    @Override
                    public ASN1Set getAuthAttributes() {
                        return CMSAuthenticatedData.this.authAttrs;
                    }

                    @Override
                    public boolean isAead() {
                        return false;
                    }
                });
            }
            catch (OperatorCreationException e) {
                throw new CMSException("unable to create digest calculator: " + e.getMessage(), e);
            }
        } else {
            CMSEnvelopedHelper.CMSAuthenticatedSecureReadable secureReadable = new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, encInfo.getContentType(), readable);
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable);
        }
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public byte[] getMac() {
        return Arrays.clone((byte[])this.mac);
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlg;
    }

    public String getMacAlgOID() {
        return this.macAlg.getAlgorithm().getId();
    }

    public byte[] getMacAlgParams() {
        try {
            return this.encodeObj(this.macAlg.getParameters());
        }
        catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public AttributeTable getAuthAttrs() {
        if (this.authAttrs == null) {
            return null;
        }
        return new AttributeTable(this.authAttrs);
    }

    public AttributeTable getUnauthAttrs() {
        if (this.unauthAttrs == null) {
            return null;
        }
        return new AttributeTable(this.unauthAttrs);
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance((Object)this.getAuthAttrs().get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}


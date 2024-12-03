/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1OctetStringParser
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1SetParser
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.AuthenticatedDataParser
 *  org.bouncycastle.asn1.cms.CMSAttributes
 *  org.bouncycastle.asn1.cms.ContentInfoParser
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthenticatedDataParser;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableInputStream;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class CMSAuthenticatedDataParser
extends CMSContentInfoParser {
    RecipientInformationStore recipientInfoStore;
    AuthenticatedDataParser authData = new AuthenticatedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
    private AlgorithmIdentifier macAlg;
    private byte[] mac;
    private AttributeTable authAttrs;
    private ASN1Set authAttrSet;
    private AttributeTable unauthAttrs;
    private boolean authAttrNotRead = true;
    private boolean unauthAttrNotRead;
    private OriginatorInformation originatorInfo;

    public CMSAuthenticatedDataParser(byte[] envelopedData) throws CMSException, IOException {
        this(new ByteArrayInputStream(envelopedData));
    }

    public CMSAuthenticatedDataParser(byte[] envelopedData, DigestCalculatorProvider digestCalculatorProvider) throws CMSException, IOException {
        this(new ByteArrayInputStream(envelopedData), digestCalculatorProvider);
    }

    public CMSAuthenticatedDataParser(InputStream envelopedData) throws CMSException, IOException {
        this(envelopedData, null);
    }

    public CMSAuthenticatedDataParser(InputStream envelopedData, DigestCalculatorProvider digestCalculatorProvider) throws CMSException, IOException {
        super(envelopedData);
        OriginatorInfo info = this.authData.getOriginatorInfo();
        if (info != null) {
            this.originatorInfo = new OriginatorInformation(info);
        }
        ASN1Set recipientInfos = ASN1Set.getInstance((Object)this.authData.getRecipientInfos().toASN1Primitive());
        this.macAlg = this.authData.getMacAlgorithm();
        AlgorithmIdentifier digestAlgorithm = this.authData.getDigestAlgorithm();
        if (digestAlgorithm != null) {
            if (digestCalculatorProvider == null) {
                throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
            }
            ContentInfoParser data = this.authData.getEncapsulatedContentInfo();
            CMSProcessableInputStream readable = new CMSProcessableInputStream(((ASN1OctetStringParser)data.getContent(4)).getOctetStream());
            try {
                CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable secureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(digestCalculatorProvider.get(digestAlgorithm), data.getContentType(), readable);
                this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable, new AuthAttributesProvider(){

                    @Override
                    public ASN1Set getAuthAttributes() {
                        try {
                            return CMSAuthenticatedDataParser.this.getAuthAttrSet();
                        }
                        catch (IOException e) {
                            throw new IllegalStateException("can't parse authenticated attributes!");
                        }
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
            ContentInfoParser data = this.authData.getEncapsulatedContentInfo();
            CMSProcessableInputStream readable = new CMSProcessableInputStream(((ASN1OctetStringParser)data.getContent(4)).getOctetStream());
            CMSEnvelopedHelper.CMSAuthenticatedSecureReadable secureReadable = new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, data.getContentType(), readable);
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable);
        }
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlg;
    }

    public String getMacAlgOID() {
        return this.macAlg.getAlgorithm().toString();
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

    public byte[] getMac() throws IOException {
        if (this.mac == null) {
            this.getAuthAttrs();
            this.mac = this.authData.getMac().getOctets();
        }
        return Arrays.clone((byte[])this.mac);
    }

    private ASN1Set getAuthAttrSet() throws IOException {
        if (this.authAttrs == null && this.authAttrNotRead) {
            ASN1SetParser set = this.authData.getAuthAttrs();
            if (set != null) {
                this.authAttrSet = (ASN1Set)set.toASN1Primitive();
            }
            this.authAttrNotRead = false;
        }
        return this.authAttrSet;
    }

    public AttributeTable getAuthAttrs() throws IOException {
        ASN1Set set;
        if (this.authAttrs == null && this.authAttrNotRead && (set = this.getAuthAttrSet()) != null) {
            this.authAttrs = new AttributeTable(set);
        }
        return this.authAttrs;
    }

    public AttributeTable getUnauthAttrs() throws IOException {
        if (this.unauthAttrs == null && this.unauthAttrNotRead) {
            ASN1SetParser set = this.authData.getUnauthAttrs();
            this.unauthAttrNotRead = false;
            if (set != null) {
                ASN1Encodable o;
                ASN1EncodableVector v = new ASN1EncodableVector();
                while ((o = set.readObject()) != null) {
                    ASN1SequenceParser seq = (ASN1SequenceParser)o;
                    v.add((ASN1Encodable)seq.toASN1Primitive());
                }
                this.unauthAttrs = new AttributeTable((ASN1Set)new DERSet(v));
            }
        }
        return this.unauthAttrs;
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance((Object)this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}


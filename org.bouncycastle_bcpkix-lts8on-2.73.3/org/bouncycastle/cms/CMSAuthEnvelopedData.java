/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.AuthEnvelopedData
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSAuthEnvelopedData
implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private OriginatorInformation originatorInfo;
    private AlgorithmIdentifier authEncAlg;
    private ASN1Set authAttrs;
    private byte[] mac;
    private ASN1Set unauthAttrs;

    public CMSAuthEnvelopedData(byte[] authEnvData) throws CMSException {
        this(CMSUtils.readContentInfo(authEnvData));
    }

    public CMSAuthEnvelopedData(InputStream authEnvData) throws CMSException {
        this(CMSUtils.readContentInfo(authEnvData));
    }

    public CMSAuthEnvelopedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        AuthEnvelopedData authEnvData = AuthEnvelopedData.getInstance((Object)contentInfo.getContent());
        if (authEnvData.getOriginatorInfo() != null) {
            this.originatorInfo = new OriginatorInformation(authEnvData.getOriginatorInfo());
        }
        ASN1Set recipientInfos = authEnvData.getRecipientInfos();
        final EncryptedContentInfo authEncInfo = authEnvData.getAuthEncryptedContentInfo();
        this.authEncAlg = authEncInfo.getContentEncryptionAlgorithm();
        this.mac = authEnvData.getMac().getOctets();
        CMSSecureReadable secureReadable = new CMSSecureReadable(){

            @Override
            public ASN1ObjectIdentifier getContentType() {
                return authEncInfo.getContentType();
            }

            @Override
            public InputStream getInputStream() throws IOException, CMSException {
                return new ByteArrayInputStream(Arrays.concatenate((byte[])authEncInfo.getEncryptedContent().getOctets(), (byte[])CMSAuthEnvelopedData.this.mac));
            }
        };
        this.authAttrs = authEnvData.getAuthAttrs();
        this.unauthAttrs = authEnvData.getUnauthAttrs();
        this.recipientInfoStore = this.authAttrs != null ? CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.authEncAlg, secureReadable, new AuthAttributesProvider(){

            @Override
            public ASN1Set getAuthAttributes() {
                return CMSAuthEnvelopedData.this.authAttrs;
            }

            @Override
            public boolean isAead() {
                return true;
            }
        }) : CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.authEncAlg, secureReadable);
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
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

    public byte[] getMac() {
        return Arrays.clone((byte[])this.mac);
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}


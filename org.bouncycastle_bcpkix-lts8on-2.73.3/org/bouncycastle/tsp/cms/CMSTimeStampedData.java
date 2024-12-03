/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.Evidence
 *  org.bouncycastle.asn1.cms.TimeStampAndCRL
 *  org.bouncycastle.asn1.cms.TimeStampTokenEvidence
 *  org.bouncycastle.asn1.cms.TimeStampedData
 */
package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.cms.ImprintDigestInvalidException;
import org.bouncycastle.tsp.cms.TimeStampDataUtil;

public class CMSTimeStampedData {
    private TimeStampedData timeStampedData;
    private ContentInfo contentInfo;
    private TimeStampDataUtil util;

    public CMSTimeStampedData(ContentInfo contentInfo) {
        this.initialize(contentInfo);
    }

    public CMSTimeStampedData(InputStream in) throws IOException {
        try {
            this.initialize(ContentInfo.getInstance((Object)new ASN1InputStream(in).readObject()));
        }
        catch (ClassCastException e) {
            throw new IOException("Malformed content: " + e);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Malformed content: " + e);
        }
    }

    public CMSTimeStampedData(byte[] baseData) throws IOException {
        this(new ByteArrayInputStream(baseData));
    }

    private void initialize(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        if (!CMSObjectIdentifiers.timestampedData.equals((ASN1Primitive)contentInfo.getContentType())) {
            throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
        }
        this.timeStampedData = TimeStampedData.getInstance((Object)contentInfo.getContent());
        this.util = new TimeStampDataUtil(this.timeStampedData);
    }

    public byte[] calculateNextHash(DigestCalculator calculator) throws CMSException {
        return this.util.calculateNextHash(calculator);
    }

    public CMSTimeStampedData addTimeStamp(TimeStampToken token) throws CMSException {
        TimeStampAndCRL[] timeStamps = this.util.getTimeStamps();
        TimeStampAndCRL[] newTimeStamps = new TimeStampAndCRL[timeStamps.length + 1];
        System.arraycopy(timeStamps, 0, newTimeStamps, 0, timeStamps.length);
        newTimeStamps[timeStamps.length] = new TimeStampAndCRL(token.toCMSSignedData().toASN1Structure());
        return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, (ASN1Encodable)new TimeStampedData(this.timeStampedData.getDataUriIA5(), this.timeStampedData.getMetaData(), this.timeStampedData.getContent(), new Evidence(new TimeStampTokenEvidence(newTimeStamps)))));
    }

    public byte[] getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctets();
        }
        return null;
    }

    public URI getDataUri() throws URISyntaxException {
        ASN1IA5String dataURI = this.timeStampedData.getDataUriIA5();
        if (dataURI != null) {
            return new URI(dataURI.getString());
        }
        return null;
    }

    public String getFileName() {
        return this.util.getFileName();
    }

    public String getMediaType() {
        return this.util.getMediaType();
    }

    public AttributeTable getOtherMetaData() {
        return this.util.getOtherMetaData();
    }

    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        return this.util.getTimeStampTokens();
    }

    public void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(calculator);
    }

    public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider calculatorProvider) throws OperatorCreationException {
        return this.util.getMessageImprintDigestCalculator(calculatorProvider);
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(calculatorProvider, dataDigest);
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(calculatorProvider, dataDigest, timeStampToken);
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}


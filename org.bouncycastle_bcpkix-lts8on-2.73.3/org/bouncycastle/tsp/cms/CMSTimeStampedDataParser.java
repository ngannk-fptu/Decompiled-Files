/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfoParser
 *  org.bouncycastle.asn1.cms.TimeStampedDataParser
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.cms.ImprintDigestInvalidException;
import org.bouncycastle.tsp.cms.TimeStampDataUtil;
import org.bouncycastle.util.io.Streams;

public class CMSTimeStampedDataParser
extends CMSContentInfoParser {
    private TimeStampedDataParser timeStampedData;
    private TimeStampDataUtil util;

    public CMSTimeStampedDataParser(InputStream in) throws CMSException {
        super(in);
        this.initialize(this._contentInfo);
    }

    public CMSTimeStampedDataParser(byte[] baseData) throws CMSException {
        this(new ByteArrayInputStream(baseData));
    }

    private void initialize(ContentInfoParser contentInfo) throws CMSException {
        try {
            if (!CMSObjectIdentifiers.timestampedData.equals((ASN1Primitive)contentInfo.getContentType())) {
                throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
            }
            this.timeStampedData = TimeStampedDataParser.getInstance((Object)contentInfo.getContent(16));
        }
        catch (IOException e) {
            throw new CMSException("parsing exception: " + e.getMessage(), e);
        }
    }

    public byte[] calculateNextHash(DigestCalculator calculator) throws CMSException {
        return this.util.calculateNextHash(calculator);
    }

    public InputStream getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctetStream();
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

    public void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(calculator);
    }

    public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider calculatorProvider) throws OperatorCreationException {
        try {
            this.parseTimeStamps();
        }
        catch (CMSException e) {
            throw new OperatorCreationException("unable to extract algorithm ID: " + e.getMessage(), e);
        }
        return this.util.getMessageImprintDigestCalculator(calculatorProvider);
    }

    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        this.parseTimeStamps();
        return this.util.getTimeStampTokens();
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest) throws ImprintDigestInvalidException, CMSException {
        this.parseTimeStamps();
        this.util.validate(calculatorProvider, dataDigest);
    }

    public void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        this.parseTimeStamps();
        this.util.validate(calculatorProvider, dataDigest, timeStampToken);
    }

    private void parseTimeStamps() throws CMSException {
        try {
            if (this.util == null) {
                InputStream cont = this.getContent();
                if (cont != null) {
                    Streams.drain((InputStream)cont);
                }
                this.util = new TimeStampDataUtil(this.timeStampedData);
            }
        }
        catch (IOException e) {
            throw new CMSException("unable to parse evidence block: " + e.getMessage(), e);
        }
    }
}


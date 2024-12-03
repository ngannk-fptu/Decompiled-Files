/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.DERIA5String
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.Evidence
 *  org.bouncycastle.asn1.cms.TimeStampAndCRL
 *  org.bouncycastle.asn1.cms.TimeStampTokenEvidence
 *  org.bouncycastle.asn1.cms.TimeStampedData
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.cms.CMSTimeStampedData;
import org.bouncycastle.tsp.cms.CMSTimeStampedGenerator;
import org.bouncycastle.util.io.Streams;

public class CMSTimeStampedDataGenerator
extends CMSTimeStampedGenerator {
    public CMSTimeStampedData generate(TimeStampToken timeStamp) throws CMSException {
        return this.generate(timeStamp, (InputStream)null);
    }

    public CMSTimeStampedData generate(TimeStampToken timeStamp, byte[] content) throws CMSException {
        return this.generate(timeStamp, new ByteArrayInputStream(content));
    }

    public CMSTimeStampedData generate(TimeStampToken timeStamp, InputStream content) throws CMSException {
        ByteArrayOutputStream contentOut = new ByteArrayOutputStream();
        if (content != null) {
            try {
                Streams.pipeAll((InputStream)content, (OutputStream)contentOut);
            }
            catch (IOException e) {
                throw new CMSException("exception encapsulating content: " + e.getMessage(), e);
            }
        }
        BEROctetString encContent = null;
        if (contentOut.size() != 0) {
            encContent = new BEROctetString(contentOut.toByteArray());
        }
        TimeStampAndCRL stamp = new TimeStampAndCRL(timeStamp.toCMSSignedData().toASN1Structure());
        DERIA5String asn1DataUri = null;
        if (this.dataUri != null) {
            asn1DataUri = new DERIA5String(this.dataUri.toString());
        }
        return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, (ASN1Encodable)new TimeStampedData((ASN1IA5String)asn1DataUri, this.metaData, (ASN1OctetString)encContent, new Evidence(new TimeStampTokenEvidence(stamp)))));
    }
}


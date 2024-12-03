/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1OctetStringParser
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1SequenceParser
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.MetaData;

public class TimeStampedDataParser {
    private ASN1Integer version;
    private ASN1IA5String dataUri;
    private MetaData metaData;
    private ASN1OctetStringParser content;
    private Evidence temporalEvidence;
    private ASN1SequenceParser parser;

    private TimeStampedDataParser(ASN1SequenceParser parser) throws IOException {
        this.parser = parser;
        this.version = ASN1Integer.getInstance((Object)parser.readObject());
        ASN1Encodable obj = parser.readObject();
        if (obj instanceof ASN1IA5String) {
            this.dataUri = ASN1IA5String.getInstance((Object)obj);
            obj = parser.readObject();
        }
        if (obj instanceof MetaData || obj instanceof ASN1SequenceParser) {
            this.metaData = MetaData.getInstance(obj.toASN1Primitive());
            obj = parser.readObject();
        }
        if (obj instanceof ASN1OctetStringParser) {
            this.content = (ASN1OctetStringParser)obj;
        }
    }

    public static TimeStampedDataParser getInstance(Object obj) throws IOException {
        if (obj instanceof ASN1Sequence) {
            return new TimeStampedDataParser(((ASN1Sequence)obj).parser());
        }
        if (obj instanceof ASN1SequenceParser) {
            return new TimeStampedDataParser((ASN1SequenceParser)obj);
        }
        return null;
    }

    public int getVersion() {
        return this.version.getValue().intValue();
    }

    public ASN1IA5String getDataUriIA5() {
        return this.dataUri;
    }

    public MetaData getMetaData() {
        return this.metaData;
    }

    public ASN1OctetStringParser getContent() {
        return this.content;
    }

    public Evidence getTemporalEvidence() throws IOException {
        if (this.temporalEvidence == null) {
            this.temporalEvidence = Evidence.getInstance(this.parser.readObject().toASN1Primitive());
        }
        return this.temporalEvidence;
    }
}


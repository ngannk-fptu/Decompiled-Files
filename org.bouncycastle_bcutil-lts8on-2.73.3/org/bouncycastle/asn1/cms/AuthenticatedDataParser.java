/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1SetParser
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1TaggedObjectParser
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AuthenticatedDataParser {
    private ASN1SequenceParser seq;
    private ASN1Integer version;
    private ASN1Encodable nextObject;
    private boolean originatorInfoCalled;

    public AuthenticatedDataParser(ASN1SequenceParser seq) throws IOException {
        this.seq = seq;
        this.version = ASN1Integer.getInstance((Object)seq.readObject());
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() throws IOException {
        ASN1TaggedObjectParser o;
        this.originatorInfoCalled = true;
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser && (o = (ASN1TaggedObjectParser)this.nextObject).hasContextTag(0)) {
            ASN1SequenceParser originatorInfo = (ASN1SequenceParser)o.parseBaseUniversal(false, 16);
            this.nextObject = null;
            return OriginatorInfo.getInstance(originatorInfo.getLoadedObject());
        }
        return null;
    }

    public ASN1SetParser getRecipientInfos() throws IOException {
        if (!this.originatorInfoCalled) {
            this.getOriginatorInfo();
        }
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        ASN1SetParser recipientInfos = (ASN1SetParser)this.nextObject;
        this.nextObject = null;
        return recipientInfos;
    }

    public AlgorithmIdentifier getMacAlgorithm() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            ASN1SequenceParser o = (ASN1SequenceParser)this.nextObject;
            this.nextObject = null;
            return AlgorithmIdentifier.getInstance((Object)o.toASN1Primitive());
        }
        return null;
    }

    public AlgorithmIdentifier getDigestAlgorithm() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser) {
            AlgorithmIdentifier obj = AlgorithmIdentifier.getInstance((ASN1TaggedObject)((ASN1TaggedObject)this.nextObject.toASN1Primitive()), (boolean)false);
            this.nextObject = null;
            return obj;
        }
        return null;
    }

    public ContentInfoParser getEncapsulatedContentInfo() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            ASN1SequenceParser o = (ASN1SequenceParser)this.nextObject;
            this.nextObject = null;
            return new ContentInfoParser(o);
        }
        return null;
    }

    public ASN1SetParser getAuthAttrs() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser) {
            ASN1TaggedObjectParser o = (ASN1TaggedObjectParser)this.nextObject;
            this.nextObject = null;
            return (ASN1SetParser)ASN1Util.parseContextBaseUniversal((ASN1TaggedObjectParser)o, (int)2, (boolean)false, (int)17);
        }
        return null;
    }

    public ASN1OctetString getMac() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        ASN1Encodable o = this.nextObject;
        this.nextObject = null;
        return ASN1OctetString.getInstance((Object)o.toASN1Primitive());
    }

    public ASN1SetParser getUnauthAttrs() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            ASN1TaggedObject o = (ASN1TaggedObject)this.nextObject;
            this.nextObject = null;
            return (ASN1SetParser)ASN1Util.parseContextBaseUniversal((ASN1TaggedObjectParser)o, (int)3, (boolean)false, (int)17);
        }
        return null;
    }
}


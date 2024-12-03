/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1SetParser
 *  org.bouncycastle.asn1.ASN1TaggedObjectParser
 *  org.bouncycastle.asn1.ASN1Util
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;

public class EnvelopedDataParser {
    private ASN1SequenceParser _seq;
    private ASN1Integer _version;
    private ASN1Encodable _nextObject;
    private boolean _originatorInfoCalled;

    public EnvelopedDataParser(ASN1SequenceParser seq) throws IOException {
        this._seq = seq;
        this._version = ASN1Integer.getInstance((Object)seq.readObject());
    }

    public ASN1Integer getVersion() {
        return this._version;
    }

    public OriginatorInfo getOriginatorInfo() throws IOException {
        ASN1TaggedObjectParser o;
        this._originatorInfoCalled = true;
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject instanceof ASN1TaggedObjectParser && (o = (ASN1TaggedObjectParser)this._nextObject).hasContextTag(0)) {
            ASN1SequenceParser originatorInfo = (ASN1SequenceParser)o.parseBaseUniversal(false, 16);
            this._nextObject = null;
            return OriginatorInfo.getInstance(originatorInfo.getLoadedObject());
        }
        return null;
    }

    public ASN1SetParser getRecipientInfos() throws IOException {
        if (!this._originatorInfoCalled) {
            this.getOriginatorInfo();
        }
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        ASN1SetParser recipientInfos = (ASN1SetParser)this._nextObject;
        this._nextObject = null;
        return recipientInfos;
    }

    public EncryptedContentInfoParser getEncryptedContentInfo() throws IOException {
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject != null) {
            ASN1SequenceParser o = (ASN1SequenceParser)this._nextObject;
            this._nextObject = null;
            return new EncryptedContentInfoParser(o);
        }
        return null;
    }

    public ASN1SetParser getUnprotectedAttrs() throws IOException {
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject != null) {
            ASN1TaggedObjectParser o = (ASN1TaggedObjectParser)this._nextObject;
            this._nextObject = null;
            return (ASN1SetParser)ASN1Util.parseContextBaseUniversal((ASN1TaggedObjectParser)o, (int)1, (boolean)false, (int)17);
        }
        return null;
    }
}


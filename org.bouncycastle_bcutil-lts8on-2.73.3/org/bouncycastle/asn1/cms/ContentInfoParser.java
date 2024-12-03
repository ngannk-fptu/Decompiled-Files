/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1TaggedObjectParser
 *  org.bouncycastle.asn1.ASN1Util
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1Util;

public class ContentInfoParser {
    private ASN1ObjectIdentifier contentType;
    private ASN1TaggedObjectParser content;

    public ContentInfoParser(ASN1SequenceParser seq) throws IOException {
        this.contentType = (ASN1ObjectIdentifier)seq.readObject();
        this.content = (ASN1TaggedObjectParser)seq.readObject();
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public ASN1Encodable getContent(int tag) throws IOException {
        if (this.content != null) {
            return ASN1Util.parseExplicitContextBaseObject((ASN1TaggedObjectParser)this.content, (int)0);
        }
        return null;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;

public class ContentInfoParser {
    private ASN1ObjectIdentifier contentType;
    private ASN1TaggedObjectParser content;

    public ContentInfoParser(ASN1SequenceParser aSN1SequenceParser) throws IOException {
        this.contentType = (ASN1ObjectIdentifier)aSN1SequenceParser.readObject();
        this.content = (ASN1TaggedObjectParser)aSN1SequenceParser.readObject();
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public ASN1Encodable getContent(int n) throws IOException {
        if (this.content != null) {
            return this.content.getObjectParser(n, true);
        }
        return null;
    }
}


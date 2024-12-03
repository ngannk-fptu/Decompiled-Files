/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1TaggedObjectParser
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedContentInfoParser {
    private ASN1ObjectIdentifier _contentType;
    private AlgorithmIdentifier _contentEncryptionAlgorithm;
    private ASN1TaggedObjectParser _encryptedContent;

    public EncryptedContentInfoParser(ASN1SequenceParser seq) throws IOException {
        this._contentType = (ASN1ObjectIdentifier)seq.readObject();
        this._contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.readObject().toASN1Primitive());
        this._encryptedContent = (ASN1TaggedObjectParser)seq.readObject();
    }

    public ASN1ObjectIdentifier getContentType() {
        return this._contentType;
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this._contentEncryptionAlgorithm;
    }

    public ASN1Encodable getEncryptedContent(int tag) throws IOException {
        return ASN1Util.parseContextBaseUniversal((ASN1TaggedObjectParser)this._encryptedContent, (int)0, (boolean)false, (int)tag);
    }
}


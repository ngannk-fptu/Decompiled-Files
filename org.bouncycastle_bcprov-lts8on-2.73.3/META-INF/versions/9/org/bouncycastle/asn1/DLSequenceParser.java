/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DLFactory;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLSequenceParser
implements ASN1SequenceParser {
    private ASN1StreamParser _parser;

    DLSequenceParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return DLFactory.createSequence(this._parser.readVector());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}


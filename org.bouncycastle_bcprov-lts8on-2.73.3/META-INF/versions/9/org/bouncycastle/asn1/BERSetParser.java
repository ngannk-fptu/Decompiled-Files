/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.BERSet;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BERSetParser
implements ASN1SetParser {
    private ASN1StreamParser _parser;

    BERSetParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return BERSetParser.parse(this._parser);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException e) {
            throw new ASN1ParsingException(e.getMessage(), e);
        }
    }

    static BERSet parse(ASN1StreamParser sp) throws IOException {
        return new BERSet(sp.readVector());
    }
}


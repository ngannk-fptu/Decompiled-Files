/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.ConstructedOctetStream;
import org.bouncycastle.util.io.Streams;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BEROctetStringParser
implements ASN1OctetStringParser {
    private ASN1StreamParser _parser;

    BEROctetStringParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override
    public InputStream getOctetStream() {
        return new ConstructedOctetStream(this._parser);
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return BEROctetStringParser.parse(this._parser);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException e) {
            throw new ASN1ParsingException("IOException converting stream to byte array: " + e.getMessage(), e);
        }
    }

    static BEROctetString parse(ASN1StreamParser sp) throws IOException {
        return new BEROctetString(Streams.readAll(new ConstructedOctetStream(sp)));
    }
}


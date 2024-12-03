/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1ExternalParser;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DLExternal;
import org.bouncycastle.asn1.DLSequence;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERExternalParser
implements ASN1ExternalParser {
    private ASN1StreamParser _parser;

    public DERExternalParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return DERExternalParser.parse(this._parser);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException ioe) {
            throw new ASN1ParsingException("unable to get DER object", ioe);
        }
        catch (IllegalArgumentException ioe) {
            throw new ASN1ParsingException("unable to get DER object", ioe);
        }
    }

    static DLExternal parse(ASN1StreamParser sp) throws IOException {
        try {
            return new DLExternal(new DLSequence(sp.readVector()));
        }
        catch (IllegalArgumentException e) {
            throw new ASN1Exception(e.getMessage(), e);
        }
    }
}


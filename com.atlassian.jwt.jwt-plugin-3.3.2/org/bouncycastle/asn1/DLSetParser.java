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
import org.bouncycastle.asn1.DLSet;

public class DLSetParser
implements ASN1SetParser {
    private ASN1StreamParser _parser;

    DLSetParser(ASN1StreamParser aSN1StreamParser) {
        this._parser = aSN1StreamParser;
    }

    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    public ASN1Primitive getLoadedObject() throws IOException {
        return new DLSet(this._parser.readVector());
    }

    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException iOException) {
            throw new ASN1ParsingException(iOException.getMessage(), iOException);
        }
    }
}


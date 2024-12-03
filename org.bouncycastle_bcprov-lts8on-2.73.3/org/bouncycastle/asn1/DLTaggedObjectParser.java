/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.BERTaggedObjectParser;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class DLTaggedObjectParser
extends BERTaggedObjectParser {
    private final boolean _constructed;

    DLTaggedObjectParser(int tagClass, int tagNo, boolean constructed, ASN1StreamParser parser) {
        super(tagClass, tagNo, parser);
        this._constructed = constructed;
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return this._parser.loadTaggedDL(this._tagClass, this._tagNo, this._constructed);
    }

    @Override
    public ASN1Encodable parseBaseUniversal(boolean declaredExplicit, int baseTagNo) throws IOException {
        if (declaredExplicit) {
            if (!this._constructed) {
                throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)");
            }
            return this._parser.parseObject(baseTagNo);
        }
        return this._constructed ? this._parser.parseImplicitConstructedDL(baseTagNo) : this._parser.parseImplicitPrimitive(baseTagNo);
    }

    @Override
    public ASN1Encodable parseExplicitBaseObject() throws IOException {
        if (!this._constructed) {
            throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)");
        }
        return this._parser.readObject();
    }

    @Override
    public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
        if (!this._constructed) {
            throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)");
        }
        return this._parser.parseTaggedObject();
    }

    @Override
    public ASN1TaggedObjectParser parseImplicitBaseTagged(int baseTagClass, int baseTagNo) throws IOException {
        return new DLTaggedObjectParser(baseTagClass, baseTagNo, this._constructed, this._parser);
    }
}


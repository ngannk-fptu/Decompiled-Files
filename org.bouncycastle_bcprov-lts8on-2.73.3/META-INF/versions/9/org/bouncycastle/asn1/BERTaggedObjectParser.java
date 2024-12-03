/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class BERTaggedObjectParser
implements ASN1TaggedObjectParser {
    final int _tagClass;
    final int _tagNo;
    final ASN1StreamParser _parser;

    BERTaggedObjectParser(int tagClass, int tagNo, ASN1StreamParser parser) {
        this._tagClass = tagClass;
        this._tagNo = tagNo;
        this._parser = parser;
    }

    @Override
    public int getTagClass() {
        return this._tagClass;
    }

    @Override
    public int getTagNo() {
        return this._tagNo;
    }

    @Override
    public boolean hasContextTag() {
        return this._tagClass == 128;
    }

    @Override
    public boolean hasContextTag(int tagNo) {
        return this._tagClass == 128 && this._tagNo == tagNo;
    }

    @Override
    public boolean hasTag(int tagClass, int tagNo) {
        return this._tagClass == tagClass && this._tagNo == tagNo;
    }

    @Override
    public boolean hasTagClass(int tagClass) {
        return this._tagClass == tagClass;
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return this._parser.loadTaggedIL(this._tagClass, this._tagNo);
    }

    @Override
    public ASN1Encodable parseBaseUniversal(boolean declaredExplicit, int baseTagNo) throws IOException {
        if (declaredExplicit) {
            return this._parser.parseObject(baseTagNo);
        }
        return this._parser.parseImplicitConstructedIL(baseTagNo);
    }

    @Override
    public ASN1Encodable parseExplicitBaseObject() throws IOException {
        return this._parser.readObject();
    }

    @Override
    public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
        return this._parser.parseTaggedObject();
    }

    @Override
    public ASN1TaggedObjectParser parseImplicitBaseTagged(int baseTagClass, int baseTagNo) throws IOException {
        return new BERTaggedObjectParser(baseTagClass, baseTagNo, this._parser);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException e) {
            throw new ASN1ParsingException(e.getMessage());
        }
    }
}


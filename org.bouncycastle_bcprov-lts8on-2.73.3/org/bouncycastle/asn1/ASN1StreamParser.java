/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.BERBitStringParser;
import org.bouncycastle.asn1.BEROctetStringParser;
import org.bouncycastle.asn1.BERSequenceParser;
import org.bouncycastle.asn1.BERSetParser;
import org.bouncycastle.asn1.BERTaggedObjectParser;
import org.bouncycastle.asn1.DERExternalParser;
import org.bouncycastle.asn1.DEROctetStringParser;
import org.bouncycastle.asn1.DLBitStringParser;
import org.bouncycastle.asn1.DLSequenceParser;
import org.bouncycastle.asn1.DLSetParser;
import org.bouncycastle.asn1.DLTaggedObjectParser;
import org.bouncycastle.asn1.DefiniteLengthInputStream;
import org.bouncycastle.asn1.InMemoryRepresentable;
import org.bouncycastle.asn1.IndefiniteLengthInputStream;
import org.bouncycastle.asn1.StreamUtil;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1StreamParser {
    private final InputStream _in;
    private final int _limit;
    private final byte[][] tmpBuffers;

    public ASN1StreamParser(InputStream in) {
        this(in, StreamUtil.findLimit(in));
    }

    public ASN1StreamParser(byte[] encoding) {
        this(new ByteArrayInputStream(encoding), encoding.length);
    }

    public ASN1StreamParser(InputStream in, int limit) {
        this(in, limit, new byte[11][]);
    }

    ASN1StreamParser(InputStream in, int limit, byte[][] tmpBuffers) {
        this._in = in;
        this._limit = limit;
        this.tmpBuffers = tmpBuffers;
    }

    public ASN1Encodable readObject() throws IOException {
        int tagHdr = this._in.read();
        if (tagHdr < 0) {
            return null;
        }
        return this.implParseObject(tagHdr);
    }

    ASN1Encodable implParseObject(int tagHdr) throws IOException {
        this.set00Check(false);
        int tagNo = ASN1InputStream.readTagNumber(this._in, tagHdr);
        int length = ASN1InputStream.readLength(this._in, this._limit, tagNo == 3 || tagNo == 4 || tagNo == 16 || tagNo == 17 || tagNo == 8);
        if (length < 0) {
            if (0 == (tagHdr & 0x20)) {
                throw new IOException("indefinite-length primitive encoding encountered");
            }
            IndefiniteLengthInputStream indIn = new IndefiniteLengthInputStream(this._in, this._limit);
            ASN1StreamParser sp = new ASN1StreamParser(indIn, this._limit, this.tmpBuffers);
            int tagClass = tagHdr & 0xC0;
            if (0 != tagClass) {
                return new BERTaggedObjectParser(tagClass, tagNo, sp);
            }
            return sp.parseImplicitConstructedIL(tagNo);
        }
        DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(this._in, length, this._limit);
        if (0 == (tagHdr & 0xE0)) {
            return this.parseImplicitPrimitive(tagNo, defIn);
        }
        ASN1StreamParser sp = new ASN1StreamParser(defIn, defIn.getLimit(), this.tmpBuffers);
        int tagClass = tagHdr & 0xC0;
        if (0 != tagClass) {
            boolean isConstructed = (tagHdr & 0x20) != 0;
            return new DLTaggedObjectParser(tagClass, tagNo, isConstructed, sp);
        }
        return sp.parseImplicitConstructedDL(tagNo);
    }

    ASN1Primitive loadTaggedDL(int tagClass, int tagNo, boolean constructed) throws IOException {
        if (!constructed) {
            byte[] contentsOctets = ((DefiniteLengthInputStream)this._in).toByteArray();
            return ASN1TaggedObject.createPrimitive(tagClass, tagNo, contentsOctets);
        }
        ASN1EncodableVector contentsElements = this.readVector();
        return ASN1TaggedObject.createConstructedDL(tagClass, tagNo, contentsElements);
    }

    ASN1Primitive loadTaggedIL(int tagClass, int tagNo) throws IOException {
        ASN1EncodableVector contentsElements = this.readVector();
        return ASN1TaggedObject.createConstructedIL(tagClass, tagNo, contentsElements);
    }

    ASN1Encodable parseImplicitConstructedDL(int univTagNo) throws IOException {
        switch (univTagNo) {
            case 3: {
                return new BERBitStringParser(this);
            }
            case 8: {
                return new DERExternalParser(this);
            }
            case 4: {
                return new BEROctetStringParser(this);
            }
            case 17: {
                return new DLSetParser(this);
            }
            case 16: {
                return new DLSequenceParser(this);
            }
        }
        throw new ASN1Exception("unknown DL object encountered: 0x" + Integer.toHexString(univTagNo));
    }

    ASN1Encodable parseImplicitConstructedIL(int univTagNo) throws IOException {
        switch (univTagNo) {
            case 3: {
                return new BERBitStringParser(this);
            }
            case 4: {
                return new BEROctetStringParser(this);
            }
            case 8: {
                return new DERExternalParser(this);
            }
            case 16: {
                return new BERSequenceParser(this);
            }
            case 17: {
                return new BERSetParser(this);
            }
        }
        throw new ASN1Exception("unknown BER object encountered: 0x" + Integer.toHexString(univTagNo));
    }

    ASN1Encodable parseImplicitPrimitive(int univTagNo) throws IOException {
        return this.parseImplicitPrimitive(univTagNo, (DefiniteLengthInputStream)this._in);
    }

    ASN1Encodable parseImplicitPrimitive(int univTagNo, DefiniteLengthInputStream defIn) throws IOException {
        switch (univTagNo) {
            case 3: {
                return new DLBitStringParser(defIn);
            }
            case 8: {
                throw new ASN1Exception("externals must use constructed encoding (see X.690 8.18)");
            }
            case 4: {
                return new DEROctetStringParser(defIn);
            }
            case 17: {
                throw new ASN1Exception("sequences must use constructed encoding (see X.690 8.9.1/8.10.1)");
            }
            case 16: {
                throw new ASN1Exception("sets must use constructed encoding (see X.690 8.11.1/8.12.1)");
            }
        }
        try {
            return ASN1InputStream.createPrimitiveDERObject(univTagNo, defIn, this.tmpBuffers);
        }
        catch (IllegalArgumentException e) {
            throw new ASN1Exception("corrupted stream detected", e);
        }
    }

    ASN1Encodable parseObject(int univTagNo) throws IOException {
        if (univTagNo < 0 || univTagNo > 30) {
            throw new IllegalArgumentException("invalid universal tag number: " + univTagNo);
        }
        int tagHdr = this._in.read();
        if (tagHdr < 0) {
            return null;
        }
        if ((tagHdr & 0xFFFFFFDF) != univTagNo) {
            throw new IOException("unexpected identifier encountered: " + tagHdr);
        }
        return this.implParseObject(tagHdr);
    }

    ASN1TaggedObjectParser parseTaggedObject() throws IOException {
        int tagHdr = this._in.read();
        if (tagHdr < 0) {
            return null;
        }
        int tagClass = tagHdr & 0xC0;
        if (0 == tagClass) {
            throw new ASN1Exception("no tagged object found");
        }
        return (ASN1TaggedObjectParser)this.implParseObject(tagHdr);
    }

    ASN1EncodableVector readVector() throws IOException {
        int tagHdr = this._in.read();
        if (tagHdr < 0) {
            return new ASN1EncodableVector(0);
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        do {
            ASN1Encodable obj;
            if ((obj = this.implParseObject(tagHdr)) instanceof InMemoryRepresentable) {
                v.add(((InMemoryRepresentable)((Object)obj)).getLoadedObject());
                continue;
            }
            v.add(obj.toASN1Primitive());
        } while ((tagHdr = this._in.read()) >= 0);
        return v;
    }

    private void set00Check(boolean enabled) {
        if (this._in instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream)this._in).setEofOn00(enabled);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1GeneralString;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1GraphicString;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1NumericString;
import org.bouncycastle.asn1.ASN1ObjectDescriptor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1RelativeOID;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1T61String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.ASN1VideotexString;
import org.bouncycastle.asn1.ASN1VisibleString;
import org.bouncycastle.asn1.BERBitString;
import org.bouncycastle.asn1.BERBitStringParser;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BEROctetStringParser;
import org.bouncycastle.asn1.BERSequenceParser;
import org.bouncycastle.asn1.BERSetParser;
import org.bouncycastle.asn1.BERTags;
import org.bouncycastle.asn1.DERExternalParser;
import org.bouncycastle.asn1.DLFactory;
import org.bouncycastle.asn1.DefiniteLengthInputStream;
import org.bouncycastle.asn1.IndefiniteLengthInputStream;
import org.bouncycastle.asn1.LazyEncodedSequence;
import org.bouncycastle.asn1.StreamUtil;
import org.bouncycastle.util.io.Streams;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1InputStream
extends FilterInputStream
implements BERTags {
    private final int limit;
    private final boolean lazyEvaluate;
    private final byte[][] tmpBuffers;

    public ASN1InputStream(InputStream is) {
        this(is, StreamUtil.findLimit(is));
    }

    public ASN1InputStream(byte[] input) {
        this((InputStream)new ByteArrayInputStream(input), input.length);
    }

    public ASN1InputStream(byte[] input, boolean lazyEvaluate) {
        this(new ByteArrayInputStream(input), input.length, lazyEvaluate);
    }

    public ASN1InputStream(InputStream input, int limit) {
        this(input, limit, false);
    }

    public ASN1InputStream(InputStream input, boolean lazyEvaluate) {
        this(input, StreamUtil.findLimit(input), lazyEvaluate);
    }

    public ASN1InputStream(InputStream input, int limit, boolean lazyEvaluate) {
        this(input, limit, lazyEvaluate, new byte[11][]);
    }

    private ASN1InputStream(InputStream input, int limit, boolean lazyEvaluate, byte[][] tmpBuffers) {
        super(input);
        this.limit = limit;
        this.lazyEvaluate = lazyEvaluate;
        this.tmpBuffers = tmpBuffers;
    }

    int getLimit() {
        return this.limit;
    }

    protected int readLength() throws IOException {
        return ASN1InputStream.readLength(this, this.limit, false);
    }

    protected void readFully(byte[] bytes) throws IOException {
        if (Streams.readFully(this, bytes, 0, bytes.length) != bytes.length) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }

    protected ASN1Primitive buildObject(int tag, int tagNo, int length) throws IOException {
        DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(this, length, this.limit);
        if (0 == (tag & 0xE0)) {
            return ASN1InputStream.createPrimitiveDERObject(tagNo, defIn, this.tmpBuffers);
        }
        int tagClass = tag & 0xC0;
        if (0 != tagClass) {
            boolean isConstructed = (tag & 0x20) != 0;
            return this.readTaggedObjectDL(tagClass, tagNo, isConstructed, defIn);
        }
        switch (tagNo) {
            case 3: {
                return this.buildConstructedBitString(this.readVector(defIn));
            }
            case 4: {
                return this.buildConstructedOctetString(this.readVector(defIn));
            }
            case 16: {
                if (defIn.getRemaining() < 1) {
                    return DLFactory.EMPTY_SEQUENCE;
                }
                if (this.lazyEvaluate) {
                    return new LazyEncodedSequence(defIn.toByteArray());
                }
                return DLFactory.createSequence(this.readVector(defIn));
            }
            case 17: {
                return DLFactory.createSet(this.readVector(defIn));
            }
            case 8: {
                return DLFactory.createSequence(this.readVector(defIn)).toASN1External();
            }
        }
        throw new IOException("unknown tag " + tagNo + " encountered");
    }

    public ASN1Primitive readObject() throws IOException {
        int tag = this.read();
        if (tag <= 0) {
            if (tag == 0) {
                throw new IOException("unexpected end-of-contents marker");
            }
            return null;
        }
        int tagNo = ASN1InputStream.readTagNumber(this, tag);
        int length = this.readLength();
        if (length >= 0) {
            try {
                return this.buildObject(tag, tagNo, length);
            }
            catch (IllegalArgumentException e) {
                throw new ASN1Exception("corrupted stream detected", e);
            }
        }
        if (0 == (tag & 0x20)) {
            throw new IOException("indefinite-length primitive encoding encountered");
        }
        IndefiniteLengthInputStream indIn = new IndefiniteLengthInputStream(this, this.limit);
        ASN1StreamParser sp = new ASN1StreamParser(indIn, this.limit, this.tmpBuffers);
        int tagClass = tag & 0xC0;
        if (0 != tagClass) {
            return sp.loadTaggedIL(tagClass, tagNo);
        }
        switch (tagNo) {
            case 3: {
                return BERBitStringParser.parse(sp);
            }
            case 4: {
                return BEROctetStringParser.parse(sp);
            }
            case 8: {
                return DERExternalParser.parse(sp);
            }
            case 16: {
                return BERSequenceParser.parse(sp);
            }
            case 17: {
                return BERSetParser.parse(sp);
            }
        }
        throw new IOException("unknown BER object encountered");
    }

    ASN1BitString buildConstructedBitString(ASN1EncodableVector contentsElements) throws IOException {
        ASN1BitString[] strings = new ASN1BitString[contentsElements.size()];
        for (int i = 0; i != strings.length; ++i) {
            ASN1Encodable asn1Obj = contentsElements.get(i);
            if (!(asn1Obj instanceof ASN1BitString)) {
                throw new ASN1Exception("unknown object encountered in constructed BIT STRING: " + asn1Obj.getClass());
            }
            strings[i] = (ASN1BitString)asn1Obj;
        }
        return new BERBitString(strings);
    }

    ASN1OctetString buildConstructedOctetString(ASN1EncodableVector contentsElements) throws IOException {
        ASN1OctetString[] strings = new ASN1OctetString[contentsElements.size()];
        for (int i = 0; i != strings.length; ++i) {
            ASN1Encodable asn1Obj = contentsElements.get(i);
            if (!(asn1Obj instanceof ASN1OctetString)) {
                throw new ASN1Exception("unknown object encountered in constructed OCTET STRING: " + asn1Obj.getClass());
            }
            strings[i] = (ASN1OctetString)asn1Obj;
        }
        return new BEROctetString(strings);
    }

    ASN1Primitive readTaggedObjectDL(int tagClass, int tagNo, boolean constructed, DefiniteLengthInputStream defIn) throws IOException {
        if (!constructed) {
            byte[] contentsOctets = defIn.toByteArray();
            return ASN1TaggedObject.createPrimitive(tagClass, tagNo, contentsOctets);
        }
        ASN1EncodableVector contentsElements = this.readVector(defIn);
        return ASN1TaggedObject.createConstructedDL(tagClass, tagNo, contentsElements);
    }

    ASN1EncodableVector readVector() throws IOException {
        ASN1Primitive p = this.readObject();
        if (null == p) {
            return new ASN1EncodableVector(0);
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        do {
            v.add(p);
        } while ((p = this.readObject()) != null);
        return v;
    }

    ASN1EncodableVector readVector(DefiniteLengthInputStream defIn) throws IOException {
        int remaining = defIn.getRemaining();
        if (remaining < 1) {
            return new ASN1EncodableVector(0);
        }
        return new ASN1InputStream(defIn, remaining, this.lazyEvaluate, this.tmpBuffers).readVector();
    }

    static int readTagNumber(InputStream s, int tag) throws IOException {
        int tagNo = tag & 0x1F;
        if (tagNo == 31) {
            int b = s.read();
            if (b < 31) {
                if (b < 0) {
                    throw new EOFException("EOF found inside tag value.");
                }
                throw new IOException("corrupted stream - high tag number < 31 found");
            }
            tagNo = b & 0x7F;
            if (0 == tagNo) {
                throw new IOException("corrupted stream - invalid high tag number found");
            }
            while ((b & 0x80) != 0) {
                if (tagNo >>> 24 != 0) {
                    throw new IOException("Tag number more than 31 bits");
                }
                tagNo <<= 7;
                b = s.read();
                if (b < 0) {
                    throw new EOFException("EOF found inside tag value.");
                }
                tagNo |= b & 0x7F;
            }
        }
        return tagNo;
    }

    static int readLength(InputStream s, int limit, boolean isParsing) throws IOException {
        int length = s.read();
        if (0 == length >>> 7) {
            return length;
        }
        if (128 == length) {
            return -1;
        }
        if (length < 0) {
            throw new EOFException("EOF found when length expected");
        }
        if (255 == length) {
            throw new IOException("invalid long form definite-length 0xFF");
        }
        int octetsCount = length & 0x7F;
        int octetsPos = 0;
        length = 0;
        do {
            int octet;
            if ((octet = s.read()) < 0) {
                throw new EOFException("EOF found reading length");
            }
            if (length >>> 23 != 0) {
                throw new IOException("long form definite-length more than 31 bits");
            }
            length = (length << 8) + octet;
        } while (++octetsPos < octetsCount);
        if (length >= limit && !isParsing) {
            throw new IOException("corrupted stream - out of bounds length found: " + length + " >= " + limit);
        }
        return length;
    }

    private static byte[] getBuffer(DefiniteLengthInputStream defIn, byte[][] tmpBuffers) throws IOException {
        int len = defIn.getRemaining();
        if (len >= tmpBuffers.length) {
            return defIn.toByteArray();
        }
        byte[] buf = tmpBuffers[len];
        if (buf == null) {
            tmpBuffers[len] = new byte[len];
            buf = tmpBuffers[len];
        }
        defIn.readAllIntoByteArray(buf);
        return buf;
    }

    private static char[] getBMPCharBuffer(DefiniteLengthInputStream defIn) throws IOException {
        int remainingBytes = defIn.getRemaining();
        if (0 != (remainingBytes & 1)) {
            throw new IOException("malformed BMPString encoding encountered");
        }
        char[] string = new char[remainingBytes / 2];
        int stringPos = 0;
        byte[] buf = new byte[8];
        while (remainingBytes >= 8) {
            if (Streams.readFully(defIn, buf, 0, 8) != 8) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            string[stringPos] = (char)(buf[0] << 8 | buf[1] & 0xFF);
            string[stringPos + 1] = (char)(buf[2] << 8 | buf[3] & 0xFF);
            string[stringPos + 2] = (char)(buf[4] << 8 | buf[5] & 0xFF);
            string[stringPos + 3] = (char)(buf[6] << 8 | buf[7] & 0xFF);
            stringPos += 4;
            remainingBytes -= 8;
        }
        if (remainingBytes > 0) {
            if (Streams.readFully(defIn, buf, 0, remainingBytes) != remainingBytes) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            int bufPos = 0;
            do {
                int b1 = buf[bufPos++] << 8;
                int b2 = buf[bufPos++] & 0xFF;
                string[stringPos++] = (char)(b1 | b2);
            } while (bufPos < remainingBytes);
        }
        if (0 != defIn.getRemaining() || string.length != stringPos) {
            throw new IllegalStateException();
        }
        return string;
    }

    static ASN1Primitive createPrimitiveDERObject(int tagNo, DefiniteLengthInputStream defIn, byte[][] tmpBuffers) throws IOException {
        try {
            switch (tagNo) {
                case 3: {
                    return ASN1BitString.createPrimitive(defIn.toByteArray());
                }
                case 30: {
                    return ASN1BMPString.createPrimitive(ASN1InputStream.getBMPCharBuffer(defIn));
                }
                case 1: {
                    return ASN1Boolean.createPrimitive(ASN1InputStream.getBuffer(defIn, tmpBuffers));
                }
                case 10: {
                    return ASN1Enumerated.createPrimitive(ASN1InputStream.getBuffer(defIn, tmpBuffers), true);
                }
                case 27: {
                    return ASN1GeneralString.createPrimitive(defIn.toByteArray());
                }
                case 24: {
                    return ASN1GeneralizedTime.createPrimitive(defIn.toByteArray());
                }
                case 25: {
                    return ASN1GraphicString.createPrimitive(defIn.toByteArray());
                }
                case 22: {
                    return ASN1IA5String.createPrimitive(defIn.toByteArray());
                }
                case 2: {
                    return ASN1Integer.createPrimitive(defIn.toByteArray());
                }
                case 5: {
                    return ASN1Null.createPrimitive(defIn.toByteArray());
                }
                case 18: {
                    return ASN1NumericString.createPrimitive(defIn.toByteArray());
                }
                case 7: {
                    return ASN1ObjectDescriptor.createPrimitive(defIn.toByteArray());
                }
                case 6: {
                    return ASN1ObjectIdentifier.createPrimitive(ASN1InputStream.getBuffer(defIn, tmpBuffers), true);
                }
                case 4: {
                    return ASN1OctetString.createPrimitive(defIn.toByteArray());
                }
                case 19: {
                    return ASN1PrintableString.createPrimitive(defIn.toByteArray());
                }
                case 13: {
                    return ASN1RelativeOID.createPrimitive(defIn.toByteArray(), false);
                }
                case 20: {
                    return ASN1T61String.createPrimitive(defIn.toByteArray());
                }
                case 28: {
                    return ASN1UniversalString.createPrimitive(defIn.toByteArray());
                }
                case 23: {
                    return ASN1UTCTime.createPrimitive(defIn.toByteArray());
                }
                case 12: {
                    return ASN1UTF8String.createPrimitive(defIn.toByteArray());
                }
                case 21: {
                    return ASN1VideotexString.createPrimitive(defIn.toByteArray());
                }
                case 26: {
                    return ASN1VisibleString.createPrimitive(defIn.toByteArray());
                }
                case 14: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: {
                    throw new IOException("unsupported tag " + tagNo + " encountered");
                }
            }
            throw new IOException("unknown tag " + tagNo + " encountered");
        }
        catch (IllegalArgumentException e) {
            throw new ASN1Exception(e.getMessage(), e);
        }
        catch (IllegalStateException e) {
            throw new ASN1Exception(e.getMessage(), e);
        }
    }
}


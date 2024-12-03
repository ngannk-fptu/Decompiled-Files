/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERIA5String
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DERUTF8String
 *  org.bouncycastle.util.BigIntegers
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.encoders.Hex
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.SwitchIndexer;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

public class OERInputStream
extends FilterInputStream {
    private static final int[] bits = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
    private static final int[] bitsR = new int[]{128, 64, 32, 16, 8, 4, 2, 1};
    protected PrintWriter debugOutput = null;
    private int maxByteAllocation = 0x100000;
    protected PrintWriter debugStream = null;

    public OERInputStream(InputStream src) {
        super(src);
    }

    public OERInputStream(InputStream src, int maxByteAllocation) {
        super(src);
        this.maxByteAllocation = maxByteAllocation;
    }

    public static ASN1Encodable parse(byte[] src, Element element) throws IOException {
        OERInputStream in = new OERInputStream(new ByteArrayInputStream(src));
        return in.parse(element);
    }

    private int countOptionalChildTypes(Element element) {
        int optionalElements = 0;
        for (Element e : element.getChildren()) {
            optionalElements += e.isExplicit() ? 0 : 1;
        }
        return optionalElements;
    }

    public ASN1Object parse(Element element) throws IOException {
        switch (element.getBaseType()) {
            case OPAQUE: {
                ElementSupplier es = element.resolveSupplier();
                return this.parse(new Element(es.build(), element));
            }
            case Switch: {
                throw new IllegalStateException("A switch element should only be found within a sequence.");
            }
            case Supplier: {
                return this.parse(new Element(element.getElementSupplier().build(), element));
            }
            case SEQ_OF: {
                int l = this.readLength().intLength();
                byte[] lenEnc = this.allocateArray(l);
                if (Streams.readFully((InputStream)this, (byte[])lenEnc) != lenEnc.length) {
                    throw new IOException("could not read all of count of seq-of values");
                }
                int j = BigIntegers.fromUnsignedByteArray((byte[])lenEnc).intValue();
                this.debugPrint(element + "(len = " + j + ")");
                ASN1EncodableVector avec = new ASN1EncodableVector();
                if (element.getChildren().get(0).getaSwitch() != null) {
                    throw new IllegalStateException("element def for item in SEQ OF has a switch, switches only supported in sequences");
                }
                for (int n = 0; n < j; ++n) {
                    Element def = Element.expandDeferredDefinition(element.getChildren().get(0), element);
                    avec.add((ASN1Encodable)this.parse(def));
                }
                return new DERSequence(avec);
            }
            case SEQ: {
                Sequence sequence = new Sequence(this.in, element);
                this.debugPrint(element + sequence.toString());
                ASN1EncodableVector avec = new ASN1EncodableVector();
                List<Element> children = element.getChildren();
                int t = 0;
                boolean optionalPos = false;
                for (t = 0; t < children.size(); ++t) {
                    Element resolvedChild;
                    Element child = children.get(t);
                    if (child.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                    if (child.getBlock() > 0) break;
                    if ((child = Element.expandDeferredDefinition(child, element)).getaSwitch() != null) {
                        resolvedChild = child.getaSwitch().result(new SwitchIndexer.Asn1EncodableVectorIndexer(avec));
                        if (resolvedChild.getParent() != element) {
                            resolvedChild = new Element(resolvedChild, element);
                        }
                    } else {
                        resolvedChild = child;
                    }
                    if (sequence.valuePresent == null) {
                        avec.add((ASN1Encodable)this.parse(resolvedChild));
                        continue;
                    }
                    if (sequence.valuePresent[t]) {
                        if (resolvedChild.isExplicit()) {
                            avec.add((ASN1Encodable)this.parse(resolvedChild));
                            continue;
                        }
                        avec.add((ASN1Encodable)OEROptional.getInstance(this.parse(resolvedChild)));
                        continue;
                    }
                    if (resolvedChild.getDefaultValue() != null) {
                        avec.add(child.getDefaultValue());
                        continue;
                    }
                    avec.add(this.absent(child));
                }
                if (sequence.extensionFlagSet) {
                    int l = this.readLength().intLength();
                    byte[] rawPresenceList = this.allocateArray(l);
                    if (Streams.readFully((InputStream)this.in, (byte[])rawPresenceList) != rawPresenceList.length) {
                        throw new IOException("did not fully read presence list.");
                    }
                    int stop = rawPresenceList.length * 8 - rawPresenceList[0];
                    for (int presenceIndex = 8; t < children.size() || presenceIndex < stop; ++presenceIndex, ++t) {
                        Element child;
                        Element element2 = child = t < children.size() ? children.get(t) : null;
                        if (child == null) {
                            if ((rawPresenceList[presenceIndex / 8] & bitsR[presenceIndex % 8]) == 0) continue;
                            int len = this.readLength().intLength();
                            while (--len >= 0) {
                                this.in.read();
                            }
                            continue;
                        }
                        if (presenceIndex < stop && (rawPresenceList[presenceIndex / 8] & bitsR[presenceIndex % 8]) != 0) {
                            avec.add(this.parseOpenType(child));
                            continue;
                        }
                        if (child.isExplicit()) {
                            throw new IOException("extension is marked as explicit but is not defined in presence list");
                        }
                        avec.add((ASN1Encodable)OEROptional.ABSENT);
                    }
                }
                return new DERSequence(avec);
            }
            case CHOICE: {
                Choice choice = this.choice();
                this.debugPrint(choice.toString() + " " + choice.tag);
                if (choice.isContextSpecific()) {
                    Element choiceDef = Element.expandDeferredDefinition(element.getChildren().get(choice.getTag()), element);
                    if (choiceDef.getBlock() > 0) {
                        this.debugPrint("Chosen (Ext): " + choiceDef);
                        return new DERTaggedObject(choice.tag, this.parseOpenType(choiceDef));
                    }
                    this.debugPrint("Chosen: " + choiceDef);
                    return new DERTaggedObject(choice.tag, (ASN1Encodable)this.parse(choiceDef));
                }
                if (choice.isApplicationTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                if (choice.isPrivateTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                if (choice.isUniversalTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                throw new IllegalStateException("Unimplemented tag type");
            }
            case ENUM: {
                BigInteger bi = this.enumeration();
                this.debugPrint(element + "ENUM(" + bi + ") = " + element.getChildren().get(bi.intValue()).getLabel());
                return new ASN1Enumerated(bi);
            }
            case INT: {
                BigInteger bi;
                byte[] data;
                int bytesToRead = element.intBytesForRange();
                if (bytesToRead != 0) {
                    data = this.allocateArray(Math.abs(bytesToRead));
                    Streams.readFully((InputStream)this, (byte[])data);
                    bi = bytesToRead < 0 ? new BigInteger(data) : BigIntegers.fromUnsignedByteArray((byte[])data);
                } else if (element.isLowerRangeZero()) {
                    LengthInfo lengthInfo = this.readLength();
                    data = this.allocateArray(lengthInfo.intLength());
                    Streams.readFully((InputStream)this, (byte[])data);
                    bi = data.length == 0 ? BigInteger.ZERO : new BigInteger(1, data);
                } else {
                    LengthInfo lengthInfo = this.readLength();
                    data = this.allocateArray(lengthInfo.intLength());
                    Streams.readFully((InputStream)this, (byte[])data);
                    bi = data.length == 0 ? BigInteger.ZERO : new BigInteger(data);
                }
                if (this.debugOutput != null) {
                    this.debugPrint(element + "INTEGER byteLen= " + data.length + " hex= " + bi.toString(16) + ")");
                }
                return new ASN1Integer(bi);
            }
            case OCTET_STRING: {
                int readSize = 0;
                readSize = element.getUpperBound() != null && element.getUpperBound().equals(element.getLowerBound()) ? element.getUpperBound().intValue() : this.readLength().intLength();
                byte[] data = this.allocateArray(readSize);
                if (Streams.readFully((InputStream)this, (byte[])data) != readSize) {
                    throw new IOException("did not read all of " + element.getLabel());
                }
                if (this.debugOutput != null) {
                    int l = Math.min(data.length, 32);
                    this.debugPrint(element + "OCTET STRING (" + data.length + ") = " + Hex.toHexString((byte[])data, (int)0, (int)l) + " " + (data.length > 32 ? "..." : ""));
                }
                return new DEROctetString(data);
            }
            case IA5String: {
                byte[] data = element.isFixedLength() ? this.allocateArray(element.getUpperBound().intValue()) : this.allocateArray(this.readLength().intLength());
                if (Streams.readFully((InputStream)this, (byte[])data) != data.length) {
                    throw new IOException("could not read all of IA5 string");
                }
                String content = Strings.fromByteArray((byte[])data);
                if (this.debugOutput != null) {
                    this.debugPrint(element.appendLabel("IA5 String (" + data.length + ") = " + content));
                }
                return new DERIA5String(content);
            }
            case UTF8_STRING: {
                byte[] data = this.allocateArray(this.readLength().intLength());
                if (Streams.readFully((InputStream)this, (byte[])data) != data.length) {
                    throw new IOException("could not read all of utf 8 string");
                }
                String content = Strings.fromUTF8ByteArray((byte[])data);
                if (this.debugOutput != null) {
                    this.debugPrint(element + "UTF8 String (" + data.length + ") = " + content);
                }
                return new DERUTF8String(content);
            }
            case BIT_STRING: {
                byte[] data = element.isFixedLength() ? new byte[element.getLowerBound().intValue() / 8] : (BigInteger.ZERO.compareTo(element.getUpperBound()) > 0 ? this.allocateArray(element.getUpperBound().intValue() / 8) : this.allocateArray(this.readLength().intLength() / 8));
                Streams.readFully((InputStream)this, (byte[])data);
                if (this.debugOutput != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("BIT STRING(" + data.length * 8 + ") = ");
                    for (int i = 0; i != data.length; ++i) {
                        byte b = data[i];
                        for (int t = 0; t < 8; ++t) {
                            sb.append((b & 0x80) > 0 ? "1" : "0");
                            b = (byte)(b << 1);
                        }
                    }
                    this.debugPrint(element + sb.toString());
                }
                return new DERBitString(data);
            }
            case NULL: {
                this.debugPrint(element + "NULL");
                return DERNull.INSTANCE;
            }
            case EXTENSION: {
                LengthInfo li = this.readLength();
                byte[] value = new byte[li.intLength()];
                if (Streams.readFully((InputStream)this, (byte[])value) != li.intLength()) {
                    throw new IOException("could not read all of count of open value in choice (...) ");
                }
                this.debugPrint("ext " + li.intLength() + " " + Hex.toHexString((byte[])value));
                return new DEROctetString(value);
            }
            case BOOLEAN: {
                if (this.read() == 0) {
                    return ASN1Boolean.FALSE;
                }
                return ASN1Boolean.TRUE;
            }
        }
        throw new IllegalStateException("Unhandled type " + (Object)((Object)element.getBaseType()));
    }

    private ASN1Encodable absent(Element child) {
        this.debugPrint(child + "Absent");
        return OEROptional.ABSENT;
    }

    private byte[] allocateArray(int requiredSize) {
        if (requiredSize > this.maxByteAllocation) {
            throw new IllegalArgumentException("required byte array size " + requiredSize + " was greater than " + this.maxByteAllocation);
        }
        return new byte[requiredSize];
    }

    public BigInteger parseInt(boolean unsigned, int size) throws Exception {
        byte[] buf = new byte[size];
        int read = Streams.readFully((InputStream)this, (byte[])buf);
        if (read != buf.length) {
            throw new IllegalStateException("integer not fully read");
        }
        return unsigned ? new BigInteger(1, buf) : new BigInteger(buf);
    }

    public BigInteger uint8() throws Exception {
        return this.parseInt(true, 1);
    }

    public BigInteger uint16() throws Exception {
        return this.parseInt(true, 2);
    }

    public BigInteger uint32() throws Exception {
        return this.parseInt(true, 4);
    }

    public BigInteger uint64() throws Exception {
        return this.parseInt(false, 8);
    }

    public BigInteger int8() throws Exception {
        return this.parseInt(false, 1);
    }

    public BigInteger int16() throws Exception {
        return this.parseInt(false, 2);
    }

    public BigInteger int32() throws Exception {
        return this.parseInt(false, 4);
    }

    public BigInteger int64() throws Exception {
        return this.parseInt(false, 8);
    }

    public LengthInfo readLength() throws IOException {
        boolean accumulator = false;
        int byteVal = this.read();
        if (byteVal == -1) {
            throw new EOFException("expecting length");
        }
        if ((byteVal & 0x80) == 0) {
            this.debugPrint("Len (Short form): " + (byteVal & 0x7F));
            return new LengthInfo(BigInteger.valueOf(byteVal & 0x7F), true);
        }
        byte[] lengthInt = new byte[byteVal & 0x7F];
        if (Streams.readFully((InputStream)this, (byte[])lengthInt) != lengthInt.length) {
            throw new EOFException("did not read all bytes of length definition");
        }
        this.debugPrint("Len (Long Form): " + (byteVal & 0x7F) + " actual len: " + Hex.toHexString((byte[])lengthInt));
        return new LengthInfo(BigIntegers.fromUnsignedByteArray((byte[])lengthInt), false);
    }

    public BigInteger enumeration() throws IOException {
        int first = this.read();
        if (first == -1) {
            throw new EOFException("expecting prefix of enumeration");
        }
        if ((first & 0x80) == 128) {
            int l = first & 0x7F;
            if (l == 0) {
                return BigInteger.ZERO;
            }
            byte[] buf = new byte[l];
            int i = Streams.readFully((InputStream)this, (byte[])buf);
            if (i != buf.length) {
                throw new EOFException("unable to fully read integer component of enumeration");
            }
            return new BigInteger(1, buf);
        }
        return BigInteger.valueOf(first);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ASN1Encodable parseOpenType(Element e) throws IOException {
        int len = this.readLength().intLength();
        byte[] openTypeRaw = this.allocateArray(len);
        if (Streams.readFully((InputStream)this.in, (byte[])openTypeRaw) != openTypeRaw.length) {
            throw new IOException("did not fully read open type as raw bytes");
        }
        try (FilterInputStream oerIn = null;){
            ByteArrayInputStream bin = new ByteArrayInputStream(openTypeRaw);
            oerIn = new OERInputStream(bin);
            ASN1Object aSN1Object = ((OERInputStream)oerIn).parse(e);
            return aSN1Object;
        }
    }

    public Choice choice() throws IOException {
        return new Choice(this);
    }

    protected void debugPrint(String what) {
        if (this.debugOutput != null) {
            StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
            int level = -1;
            for (int i = 0; i != callStack.length; ++i) {
                StackTraceElement ste = callStack[i];
                if (ste.getMethodName().equals("debugPrint")) {
                    level = 0;
                    continue;
                }
                if (!ste.getClassName().contains("OERInput")) continue;
                ++level;
            }
            while (level > 0) {
                this.debugOutput.append("    ");
                --level;
            }
            this.debugOutput.append(what).append("\n");
            this.debugOutput.flush();
        }
    }

    public static class Choice
    extends OERInputStream {
        final int preamble = this.read();
        final int tag;
        final int tagClass;

        public Choice(InputStream src) throws IOException {
            super(src);
            if (this.preamble < 0) {
                throw new EOFException("expecting preamble byte of choice");
            }
            this.tagClass = this.preamble & 0xC0;
            int tag = this.preamble & 0x3F;
            if (tag >= 63) {
                tag = 0;
                int part = 0;
                do {
                    if ((part = src.read()) < 0) {
                        throw new EOFException("expecting further tag bytes");
                    }
                    tag <<= 7;
                    tag |= part & 0x7F;
                } while ((part & 0x80) != 0);
            }
            this.tag = tag;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CHOICE(");
            switch (this.tagClass) {
                case 0: {
                    sb.append("Universal ");
                    break;
                }
                case 64: {
                    sb.append("Application ");
                    break;
                }
                case 192: {
                    sb.append("Private ");
                    break;
                }
                case 128: {
                    sb.append("ContextSpecific ");
                }
            }
            sb.append("Tag = " + this.tag);
            sb.append(")");
            return sb.toString();
        }

        public int getTagClass() {
            return this.tagClass;
        }

        public int getTag() {
            return this.tag;
        }

        public boolean isContextSpecific() {
            return this.tagClass == 128;
        }

        public boolean isUniversalTagClass() {
            return this.tagClass == 0;
        }

        public boolean isApplicationTagClass() {
            return this.tagClass == 64;
        }

        public boolean isPrivateTagClass() {
            return this.tagClass == 192;
        }
    }

    private static final class LengthInfo {
        private final BigInteger length;
        private final boolean shortForm;

        public LengthInfo(BigInteger length, boolean shortForm) {
            this.length = length;
            this.shortForm = shortForm;
        }

        private int intLength() {
            return BigIntegers.intValueExact((BigInteger)this.length);
        }
    }

    public static class Sequence
    extends OERInputStream {
        private final int preamble;
        private final boolean[] valuePresent;
        private final boolean extensionFlagSet;

        public Sequence(InputStream src, Element element) throws IOException {
            super(src);
            if (element.hasPopulatedExtension() || element.getOptionals() > 0 || element.hasDefaultChildren()) {
                this.preamble = this.in.read();
                if (this.preamble < 0) {
                    throw new EOFException("expecting preamble byte of sequence");
                }
            } else {
                this.preamble = 0;
                this.extensionFlagSet = false;
                this.valuePresent = null;
                return;
            }
            this.extensionFlagSet = element.hasPopulatedExtension() && (this.preamble & 0x80) == 128;
            this.valuePresent = new boolean[element.getChildren().size()];
            int block = 0;
            int j = element.hasPopulatedExtension() ? 6 : 7;
            int mask = this.preamble;
            int presentIndex = 0;
            for (Element child : element.getChildren()) {
                if (child.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                if (child.getBlock() != block) break;
                if (child.isExplicit()) {
                    this.valuePresent[presentIndex++] = true;
                    continue;
                }
                if (j < 0) {
                    mask = src.read();
                    if (mask < 0) {
                        throw new EOFException("expecting mask byte sequence");
                    }
                    j = 7;
                }
                this.valuePresent[presentIndex++] = (mask & bits[j]) > 0;
                --j;
            }
        }

        public boolean hasOptional(int index) {
            return this.valuePresent[index];
        }

        public boolean hasExtension() {
            return this.extensionFlagSet;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SEQ(");
            sb.append(this.hasExtension() ? "Ext " : "");
            if (this.valuePresent == null) {
                sb.append("*");
            } else {
                for (int t = 0; t < this.valuePresent.length; ++t) {
                    if (this.valuePresent[t]) {
                        sb.append("1");
                        continue;
                    }
                    sb.append("0");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}


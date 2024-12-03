/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.util.BigIntegers
 *  org.bouncycastle.util.Pack
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.encoders.Hex
 */
package org.bouncycastle.oer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.oer.BitBuilder;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.SwitchIndexer;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class OEROutputStream
extends OutputStream {
    private static final int[] bits = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
    private final OutputStream out;
    protected PrintWriter debugOutput = null;

    public OEROutputStream(OutputStream out) {
        this.out = out;
    }

    public static int byteLength(long value) {
        int j;
        long m = -72057594037927936L;
        for (j = 8; j > 0 && (value & m) == 0L; --j) {
            value <<= 8;
        }
        return j;
    }

    public void write(ASN1Encodable encodable, Element oerElement) throws IOException {
        if (encodable == OEROptional.ABSENT) {
            return;
        }
        if (encodable instanceof OEROptional) {
            this.write(((OEROptional)encodable).get(), oerElement);
            return;
        }
        encodable = encodable.toASN1Primitive();
        switch (oerElement.getBaseType()) {
            case Supplier: {
                this.write(encodable, oerElement.getElementSupplier().build());
                break;
            }
            case SEQ: {
                int t;
                int t2;
                ASN1Sequence seq = ASN1Sequence.getInstance((Object)encodable);
                int j = 7;
                int mask = 0;
                boolean extensionDefined = false;
                if (oerElement.isExtensionsInDefinition()) {
                    Element e;
                    for (t2 = 0; t2 < oerElement.getChildren().size() && (e = oerElement.getChildren().get(t2)).getBaseType() != OERDefinition.BaseType.EXTENSION; ++t2) {
                        if (e.getBlock() <= 0 || t2 >= seq.size() || OEROptional.ABSENT.equals(seq.getObjectAt(t2))) continue;
                        extensionDefined = true;
                        break;
                    }
                    if (extensionDefined) {
                        mask |= bits[j];
                    }
                    --j;
                }
                for (t2 = 0; t2 < oerElement.getChildren().size(); ++t2) {
                    Element childOERDescription = oerElement.getChildren().get(t2);
                    if (childOERDescription.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                    if (childOERDescription.getBlock() > 0) break;
                    childOERDescription = Element.expandDeferredDefinition(childOERDescription, oerElement);
                    if (oerElement.getaSwitch() != null) {
                        childOERDescription = oerElement.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(seq));
                        childOERDescription = Element.expandDeferredDefinition(childOERDescription, oerElement);
                    }
                    if (j < 0) {
                        this.out.write(mask);
                        j = 7;
                        mask = 0;
                    }
                    ASN1Encodable asn1EncodableChild = seq.getObjectAt(t2);
                    if (childOERDescription.isExplicit() && asn1EncodableChild instanceof OEROptional) {
                        throw new IllegalStateException("absent sequence element that is required by oer definition");
                    }
                    if (childOERDescription.isExplicit()) continue;
                    ASN1Encodable obj = seq.getObjectAt(t2);
                    if (childOERDescription.getDefaultValue() != null) {
                        if (obj instanceof OEROptional) {
                            if (((OEROptional)obj).isDefined() && !((OEROptional)obj).get().equals(childOERDescription.getDefaultValue())) {
                                mask |= bits[j];
                            }
                        } else if (!childOERDescription.getDefaultValue().equals(obj)) {
                            mask |= bits[j];
                        }
                    } else if (asn1EncodableChild != OEROptional.ABSENT) {
                        mask |= bits[j];
                    }
                    --j;
                }
                if (j != 7) {
                    this.out.write(mask);
                }
                List<Element> childElements = oerElement.getChildren();
                for (t = 0; t < childElements.size(); ++t) {
                    Element childOERElement = oerElement.getChildren().get(t);
                    if (childOERElement.getBaseType() == OERDefinition.BaseType.EXTENSION) continue;
                    if (childOERElement.getBlock() > 0) break;
                    ASN1Encodable child = seq.getObjectAt(t);
                    if (childOERElement.getaSwitch() != null) {
                        childOERElement = childOERElement.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(seq));
                    }
                    if (childOERElement.getDefaultValue() != null && childOERElement.getDefaultValue().equals(child)) continue;
                    this.write(child, childOERElement);
                }
                if (extensionDefined) {
                    int start = t;
                    ByteArrayOutputStream presensceList = new ByteArrayOutputStream();
                    j = 7;
                    mask = 0;
                    for (int i = start; i < childElements.size(); ++i) {
                        if (j < 0) {
                            presensceList.write(mask);
                            j = 7;
                            mask = 0;
                        }
                        if (i < seq.size() && !OEROptional.ABSENT.equals(seq.getObjectAt(i))) {
                            mask |= bits[j];
                        }
                        --j;
                    }
                    if (j != 7) {
                        presensceList.write(mask);
                    }
                    this.encodeLength(presensceList.size() + 1);
                    if (j == 7) {
                        this.write(0);
                    } else {
                        this.write(j + 1);
                    }
                    this.write(presensceList.toByteArray());
                    while (t < childElements.size()) {
                        if (t < seq.size() && !OEROptional.ABSENT.equals(seq.getObjectAt(t))) {
                            this.writePlainType(seq.getObjectAt(t), childElements.get(t));
                        }
                        ++t;
                    }
                }
                this.out.flush();
                this.debugPrint(oerElement.appendLabel(""));
                break;
            }
            case SEQ_OF: {
                Enumeration e;
                if (encodable instanceof ASN1Set) {
                    e = ((ASN1Set)encodable).getObjects();
                    this.encodeQuantity(((ASN1Set)encodable).size());
                } else if (encodable instanceof ASN1Sequence) {
                    e = ((ASN1Sequence)encodable).getObjects();
                    this.encodeQuantity(((ASN1Sequence)encodable).size());
                } else {
                    throw new IllegalStateException("encodable at for SEQ_OF is not a container");
                }
                Element encodingElement = Element.expandDeferredDefinition(oerElement.getFirstChid(), oerElement);
                while (e.hasMoreElements()) {
                    Object o = e.nextElement();
                    this.write((ASN1Encodable)o, encodingElement);
                }
                this.out.flush();
                this.debugPrint(oerElement.appendLabel(""));
                break;
            }
            case CHOICE: {
                ASN1Primitive item = encodable.toASN1Primitive();
                BitBuilder bb = new BitBuilder();
                ASN1Primitive valueToWrite = null;
                if (!(item instanceof ASN1TaggedObject)) {
                    throw new IllegalStateException("only support tagged objects");
                }
                ASN1TaggedObject taggedObject = (ASN1TaggedObject)item;
                int tagClass = taggedObject.getTagClass();
                bb.writeBit(tagClass & 0x80).writeBit(tagClass & 0x40);
                int tag = taggedObject.getTagNo();
                valueToWrite = taggedObject.getBaseObject().toASN1Primitive();
                if (tag <= 63) {
                    bb.writeBits(tag, 6);
                } else {
                    bb.writeBits(255L, 6);
                    bb.write7BitBytes(tag);
                }
                if (this.debugOutput != null && item instanceof ASN1TaggedObject) {
                    taggedObject = (ASN1TaggedObject)item;
                    if (64 == taggedObject.getTagClass()) {
                        this.debugPrint(oerElement.appendLabel("AS"));
                    } else {
                        this.debugPrint(oerElement.appendLabel("CS"));
                    }
                }
                bb.writeAndClear(this.out);
                Element val = oerElement.getChildren().get(tag);
                val = Element.expandDeferredDefinition(val, oerElement);
                if (val.getBlock() > 0) {
                    this.writePlainType((ASN1Encodable)valueToWrite, val);
                } else {
                    this.write((ASN1Encodable)valueToWrite, val);
                }
                this.out.flush();
                break;
            }
            case ENUM: {
                BigInteger ordinal = encodable instanceof ASN1Integer ? ASN1Integer.getInstance((Object)encodable).getValue() : ASN1Enumerated.getInstance((Object)encodable).getValue();
                for (Element child : oerElement.getChildren()) {
                    if (!(child = Element.expandDeferredDefinition(child, oerElement)).getEnumValue().equals(ordinal)) continue;
                    if (ordinal.compareTo(BigInteger.valueOf(127L)) > 0) {
                        byte[] val = ordinal.toByteArray();
                        int l = 0x80 | val.length & 0xFF;
                        this.out.write(l);
                        this.out.write(val);
                    } else {
                        this.out.write(ordinal.intValue() & 0x7F);
                    }
                    this.out.flush();
                    this.debugPrint(oerElement.appendLabel(oerElement.rangeExpression()));
                    return;
                }
                throw new IllegalArgumentException("enum value " + ordinal + " " + Hex.toHexString((byte[])ordinal.toByteArray()) + " no in defined child list");
            }
            case INT: {
                ASN1Integer integer = ASN1Integer.getInstance((Object)encodable);
                int intBytesForRange = oerElement.intBytesForRange();
                if (intBytesForRange > 0) {
                    byte[] encoded = BigIntegers.asUnsignedByteArray((int)intBytesForRange, (BigInteger)integer.getValue());
                    switch (intBytesForRange) {
                        case 1: 
                        case 2: 
                        case 4: 
                        case 8: {
                            this.out.write(encoded);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("unknown uint length " + intBytesForRange);
                        }
                    }
                } else if (intBytesForRange < 0) {
                    byte[] encoded;
                    BigInteger number = integer.getValue();
                    switch (intBytesForRange) {
                        case -1: {
                            encoded = new byte[]{BigIntegers.byteValueExact((BigInteger)number)};
                            break;
                        }
                        case -2: {
                            encoded = Pack.shortToBigEndian((short)BigIntegers.shortValueExact((BigInteger)number));
                            break;
                        }
                        case -4: {
                            encoded = Pack.intToBigEndian((int)BigIntegers.intValueExact((BigInteger)number));
                            break;
                        }
                        case -8: {
                            encoded = Pack.longToBigEndian((long)BigIntegers.longValueExact((BigInteger)number));
                            break;
                        }
                        default: {
                            throw new IllegalStateException("unknown twos compliment length");
                        }
                    }
                    this.out.write(encoded);
                } else {
                    byte[] encoded = oerElement.isLowerRangeZero() ? BigIntegers.asUnsignedByteArray((BigInteger)integer.getValue()) : integer.getValue().toByteArray();
                    this.encodeLength(encoded.length);
                    this.out.write(encoded);
                }
                this.debugPrint(oerElement.appendLabel(oerElement.rangeExpression()));
                this.out.flush();
                break;
            }
            case OCTET_STRING: {
                ASN1OctetString octets = ASN1OctetString.getInstance((Object)encodable);
                byte[] bytes = octets.getOctets();
                if (oerElement.isFixedLength()) {
                    this.out.write(bytes);
                } else {
                    this.encodeLength(bytes.length);
                    this.out.write(bytes);
                }
                this.debugPrint(oerElement.appendLabel(oerElement.rangeExpression()));
                this.out.flush();
                break;
            }
            case IA5String: {
                ASN1IA5String iaf = ASN1IA5String.getInstance((Object)encodable);
                byte[] encoded = iaf.getOctets();
                if (oerElement.isFixedLength() && oerElement.getUpperBound().intValue() != encoded.length) {
                    throw new IOException("IA5String string length does not equal declared fixed length " + encoded.length + " " + oerElement.getUpperBound());
                }
                if (oerElement.isFixedLength()) {
                    this.out.write(encoded);
                } else {
                    this.encodeLength(encoded.length);
                    this.out.write(encoded);
                }
                this.debugPrint(oerElement.appendLabel(""));
                this.out.flush();
                break;
            }
            case UTF8_STRING: {
                ASN1UTF8String utf8 = ASN1UTF8String.getInstance((Object)encodable);
                byte[] encoded = Strings.toUTF8ByteArray((String)utf8.getString());
                this.encodeLength(encoded.length);
                this.out.write(encoded);
                this.debugPrint(oerElement.appendLabel(""));
                this.out.flush();
                break;
            }
            case BIT_STRING: {
                ASN1BitString bitString = ASN1BitString.getInstance((Object)encodable);
                byte[] bytes = bitString.getBytes();
                if (oerElement.isFixedLength()) {
                    this.out.write(bytes);
                    this.debugPrint(oerElement.appendLabel(oerElement.rangeExpression()));
                } else {
                    int padBits = bitString.getPadBits();
                    this.encodeLength(bytes.length + 1);
                    this.out.write(padBits);
                    this.out.write(bytes);
                    this.debugPrint(oerElement.appendLabel(oerElement.rangeExpression()));
                }
                this.out.flush();
                break;
            }
            case NULL: {
                break;
            }
            case EXTENSION: {
                ASN1OctetString octets = ASN1OctetString.getInstance((Object)encodable);
                byte[] bytes = octets.getOctets();
                if (oerElement.isFixedLength()) {
                    this.out.write(bytes);
                } else {
                    this.encodeLength(bytes.length);
                    this.out.write(bytes);
                }
                this.debugPrint(oerElement.appendLabel(oerElement.rangeExpression()));
                this.out.flush();
                break;
            }
            case ENUM_ITEM: {
                break;
            }
            case BOOLEAN: {
                this.debugPrint(oerElement.getLabel());
                ASN1Boolean asn1Boolean = ASN1Boolean.getInstance((Object)encodable);
                if (asn1Boolean.isTrue()) {
                    this.out.write(255);
                } else {
                    this.out.write(0);
                }
                this.out.flush();
            }
        }
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

    private void encodeLength(long len) throws IOException {
        if (len <= 127L) {
            this.out.write((int)len);
        } else {
            byte[] value = BigIntegers.asUnsignedByteArray((BigInteger)BigInteger.valueOf(len));
            this.out.write(value.length | 0x80);
            this.out.write(value);
        }
    }

    private void encodeQuantity(long quantity) throws IOException {
        byte[] quantityEncoded = BigIntegers.asUnsignedByteArray((BigInteger)BigInteger.valueOf(quantity));
        this.out.write(quantityEncoded.length);
        this.out.write(quantityEncoded);
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    public void writePlainType(ASN1Encodable value, Element e) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OEROutputStream oerOutputStream = new OEROutputStream(bos);
        oerOutputStream.write(value, e);
        oerOutputStream.flush();
        oerOutputStream.close();
        this.encodeLength(bos.size());
        this.write(bos.toByteArray());
    }
}


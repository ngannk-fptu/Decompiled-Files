/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GraphicString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public final class ASN1ObjectDescriptor
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1ObjectDescriptor.class, 7){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return new ASN1ObjectDescriptor((ASN1GraphicString)ASN1GraphicString.TYPE.fromImplicitPrimitive(octetString));
        }

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
            return new ASN1ObjectDescriptor((ASN1GraphicString)ASN1GraphicString.TYPE.fromImplicitConstructed(sequence));
        }
    };
    private final ASN1GraphicString baseGraphicString;

    public static ASN1ObjectDescriptor getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1ObjectDescriptor) {
            return (ASN1ObjectDescriptor)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1ObjectDescriptor) {
                return (ASN1ObjectDescriptor)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1ObjectDescriptor)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct object descriptor from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1ObjectDescriptor getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1ObjectDescriptor)TYPE.getContextInstance(taggedObject, explicit);
    }

    public ASN1ObjectDescriptor(ASN1GraphicString baseGraphicString) {
        if (null == baseGraphicString) {
            throw new NullPointerException("'baseGraphicString' cannot be null");
        }
        this.baseGraphicString = baseGraphicString;
    }

    public ASN1GraphicString getBaseGraphicString() {
        return this.baseGraphicString;
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) {
        return this.baseGraphicString.encodedLength(withTag);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeIdentifier(withTag, 7);
        this.baseGraphicString.encode(out, false);
    }

    @Override
    ASN1Primitive toDERObject() {
        ASN1GraphicString der = (ASN1GraphicString)this.baseGraphicString.toDERObject();
        return der == this.baseGraphicString ? this : new ASN1ObjectDescriptor(der);
    }

    @Override
    ASN1Primitive toDLObject() {
        ASN1GraphicString dl = (ASN1GraphicString)this.baseGraphicString.toDLObject();
        return dl == this.baseGraphicString ? this : new ASN1ObjectDescriptor(dl);
    }

    @Override
    boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1ObjectDescriptor)) {
            return false;
        }
        ASN1ObjectDescriptor that = (ASN1ObjectDescriptor)other;
        return this.baseGraphicString.asn1Equals(that.baseGraphicString);
    }

    @Override
    public int hashCode() {
        return ~this.baseGraphicString.hashCode();
    }

    static ASN1ObjectDescriptor createPrimitive(byte[] contents) {
        return new ASN1ObjectDescriptor(ASN1GraphicString.createPrimitive(contents));
    }
}


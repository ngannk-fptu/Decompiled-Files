/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERExternal;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLExternal;
import org.bouncycastle.util.Objects;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1External
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1External.class, 8){

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
            return sequence.toASN1External();
        }
    };
    ASN1ObjectIdentifier directReference;
    ASN1Integer indirectReference;
    ASN1Primitive dataValueDescriptor;
    int encoding;
    ASN1Primitive externalContent;

    public static ASN1External getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1External) {
            return (ASN1External)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1External) {
                return (ASN1External)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1External)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct external from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1External getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1External)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1External(ASN1Sequence sequence) {
        int offset = 0;
        ASN1Primitive asn1 = ASN1External.getObjFromSequence(sequence, offset);
        if (asn1 instanceof ASN1ObjectIdentifier) {
            this.directReference = (ASN1ObjectIdentifier)asn1;
            asn1 = ASN1External.getObjFromSequence(sequence, ++offset);
        }
        if (asn1 instanceof ASN1Integer) {
            this.indirectReference = (ASN1Integer)asn1;
            asn1 = ASN1External.getObjFromSequence(sequence, ++offset);
        }
        if (!(asn1 instanceof ASN1TaggedObject)) {
            this.dataValueDescriptor = asn1;
            asn1 = ASN1External.getObjFromSequence(sequence, ++offset);
        }
        if (sequence.size() != offset + 1) {
            throw new IllegalArgumentException("input sequence too large");
        }
        if (!(asn1 instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("No tagged object found in sequence. Structure doesn't seem to be of type External");
        }
        ASN1TaggedObject obj = (ASN1TaggedObject)asn1;
        this.encoding = ASN1External.checkEncoding(obj.getTagNo());
        this.externalContent = ASN1External.getExternalContent(obj);
    }

    ASN1External(ASN1ObjectIdentifier directReference, ASN1Integer indirectReference, ASN1Primitive dataValueDescriptor, DERTaggedObject externalData) {
        this.directReference = directReference;
        this.indirectReference = indirectReference;
        this.dataValueDescriptor = dataValueDescriptor;
        this.encoding = ASN1External.checkEncoding(externalData.getTagNo());
        this.externalContent = ASN1External.getExternalContent(externalData);
    }

    ASN1External(ASN1ObjectIdentifier directReference, ASN1Integer indirectReference, ASN1Primitive dataValueDescriptor, int encoding, ASN1Primitive externalData) {
        this.directReference = directReference;
        this.indirectReference = indirectReference;
        this.dataValueDescriptor = dataValueDescriptor;
        this.encoding = ASN1External.checkEncoding(encoding);
        this.externalContent = ASN1External.checkExternalContent(encoding, externalData);
    }

    abstract ASN1Sequence buildSequence();

    @Override
    int encodedLength(boolean withTag) throws IOException {
        return this.buildSequence().encodedLength(withTag);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeIdentifier(withTag, 40);
        this.buildSequence().encode(out, false);
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.directReference) ^ Objects.hashCode(this.indirectReference) ^ Objects.hashCode(this.dataValueDescriptor) ^ this.encoding ^ this.externalContent.hashCode();
    }

    @Override
    boolean encodeConstructed() {
        return true;
    }

    @Override
    boolean asn1Equals(ASN1Primitive primitive) {
        if (this == primitive) {
            return true;
        }
        if (!(primitive instanceof ASN1External)) {
            return false;
        }
        ASN1External that = (ASN1External)primitive;
        return Objects.areEqual(this.directReference, that.directReference) && Objects.areEqual(this.indirectReference, that.indirectReference) && Objects.areEqual(this.dataValueDescriptor, that.dataValueDescriptor) && this.encoding == that.encoding && this.externalContent.equals(that.externalContent);
    }

    public ASN1Primitive getDataValueDescriptor() {
        return this.dataValueDescriptor;
    }

    public ASN1ObjectIdentifier getDirectReference() {
        return this.directReference;
    }

    public int getEncoding() {
        return this.encoding;
    }

    public ASN1Primitive getExternalContent() {
        return this.externalContent;
    }

    public ASN1Integer getIndirectReference() {
        return this.indirectReference;
    }

    private static int checkEncoding(int encoding) {
        if (encoding < 0 || encoding > 2) {
            throw new IllegalArgumentException("invalid encoding value: " + encoding);
        }
        return encoding;
    }

    private static ASN1Primitive checkExternalContent(int tagNo, ASN1Primitive externalContent) {
        switch (tagNo) {
            case 1: {
                return ASN1OctetString.TYPE.checkedCast(externalContent);
            }
            case 2: {
                return ASN1BitString.TYPE.checkedCast(externalContent);
            }
        }
        return externalContent;
    }

    private static ASN1Primitive getExternalContent(ASN1TaggedObject encoding) {
        int tagClass = encoding.getTagClass();
        int tagNo = encoding.getTagNo();
        if (128 != tagClass) {
            throw new IllegalArgumentException("invalid tag: " + ASN1Util.getTagText(tagClass, tagNo));
        }
        switch (tagNo) {
            case 0: {
                return encoding.getExplicitBaseObject().toASN1Primitive();
            }
            case 1: {
                return ASN1OctetString.getInstance(encoding, false);
            }
            case 2: {
                return ASN1BitString.getInstance(encoding, false);
            }
        }
        throw new IllegalArgumentException("invalid tag: " + ASN1Util.getTagText(tagClass, tagNo));
    }

    private static ASN1Primitive getObjFromSequence(ASN1Sequence sequence, int index) {
        if (sequence.size() <= index) {
            throw new IllegalArgumentException("too few objects in input sequence");
        }
        return sequence.getObjectAt(index).toASN1Primitive();
    }
}


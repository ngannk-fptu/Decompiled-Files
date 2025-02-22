/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.ASN1UniversalTypes;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.BERFactory;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLFactory;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1TaggedObject
extends ASN1Primitive
implements ASN1TaggedObjectParser {
    private static final int DECLARED_EXPLICIT = 1;
    private static final int DECLARED_IMPLICIT = 2;
    private static final int PARSED_EXPLICIT = 3;
    private static final int PARSED_IMPLICIT = 4;
    final int explicitness;
    final int tagClass;
    final int tagNo;
    final ASN1Encodable obj;

    public static ASN1TaggedObject getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1TaggedObject) {
                return (ASN1TaggedObject)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return ASN1TaggedObject.checkedCast(ASN1TaggedObject.fromByteArray((byte[])obj));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1TaggedObject getInstance(Object obj, int tagClass) {
        if (obj == null) {
            throw new NullPointerException("'obj' cannot be null");
        }
        ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance(obj);
        if (tagClass != taggedObject.getTagClass()) {
            throw new IllegalArgumentException("unexpected tag in getInstance: " + ASN1Util.getTagText(taggedObject));
        }
        return taggedObject;
    }

    public static ASN1TaggedObject getInstance(Object obj, int tagClass, int tagNo) {
        if (obj == null) {
            throw new NullPointerException("'obj' cannot be null");
        }
        ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance(obj);
        if (!taggedObject.hasTag(tagClass, tagNo)) {
            throw new IllegalArgumentException("unexpected tag in getInstance: " + ASN1Util.getTagText(taggedObject));
        }
        return taggedObject;
    }

    public static ASN1TaggedObject getInstance(ASN1TaggedObject taggedObject, boolean declaredExplicit) {
        if (128 != taggedObject.getTagClass()) {
            throw new IllegalStateException("this method only valid for CONTEXT_SPECIFIC tags");
        }
        if (declaredExplicit) {
            return taggedObject.getExplicitBaseTagged();
        }
        throw new IllegalArgumentException("this method not valid for implicitly tagged tagged objects");
    }

    protected ASN1TaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        this(explicit, 128, tagNo, obj);
    }

    protected ASN1TaggedObject(boolean explicit, int tagClass, int tagNo, ASN1Encodable obj) {
        this(explicit ? 1 : 2, tagClass, tagNo, obj);
    }

    ASN1TaggedObject(int explicitness, int tagClass, int tagNo, ASN1Encodable obj) {
        if (null == obj) {
            throw new NullPointerException("'obj' cannot be null");
        }
        if (tagClass == 0 || (tagClass & 0xC0) != tagClass) {
            throw new IllegalArgumentException("invalid tag class: " + tagClass);
        }
        this.explicitness = obj instanceof ASN1Choice ? 1 : explicitness;
        this.tagClass = tagClass;
        this.tagNo = tagNo;
        this.obj = obj;
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        ASN1Primitive p2;
        if (!(other instanceof ASN1TaggedObject)) {
            return false;
        }
        ASN1TaggedObject that = (ASN1TaggedObject)other;
        if (this.tagNo != that.tagNo || this.tagClass != that.tagClass) {
            return false;
        }
        if (this.explicitness != that.explicitness && this.isExplicit() != that.isExplicit()) {
            return false;
        }
        ASN1Primitive p1 = this.obj.toASN1Primitive();
        if (p1 == (p2 = that.obj.toASN1Primitive())) {
            return true;
        }
        if (!this.isExplicit()) {
            try {
                byte[] d1 = this.getEncoded();
                byte[] d2 = that.getEncoded();
                return Arrays.areEqual(d1, d2);
            }
            catch (IOException e) {
                return false;
            }
        }
        return p1.asn1Equals(p2);
    }

    @Override
    public int hashCode() {
        return this.tagClass * 7919 ^ this.tagNo ^ (this.isExplicit() ? 15 : 240) ^ this.obj.toASN1Primitive().hashCode();
    }

    @Override
    public int getTagClass() {
        return this.tagClass;
    }

    @Override
    public int getTagNo() {
        return this.tagNo;
    }

    @Override
    public boolean hasContextTag() {
        return this.tagClass == 128;
    }

    @Override
    public boolean hasContextTag(int tagNo) {
        return this.tagClass == 128 && this.tagNo == tagNo;
    }

    @Override
    public boolean hasTag(int tagClass, int tagNo) {
        return this.tagClass == tagClass && this.tagNo == tagNo;
    }

    @Override
    public boolean hasTagClass(int tagClass) {
        return this.tagClass == tagClass;
    }

    public boolean isExplicit() {
        switch (this.explicitness) {
            case 1: 
            case 3: {
                return true;
            }
        }
        return false;
    }

    boolean isParsed() {
        switch (this.explicitness) {
            case 3: 
            case 4: {
                return true;
            }
        }
        return false;
    }

    public ASN1Object getBaseObject() {
        return this.obj instanceof ASN1Object ? (ASN1Object)this.obj : this.obj.toASN1Primitive();
    }

    public ASN1Object getExplicitBaseObject() {
        if (!this.isExplicit()) {
            throw new IllegalStateException("object implicit - explicit expected.");
        }
        return this.obj instanceof ASN1Object ? (ASN1Object)this.obj : this.obj.toASN1Primitive();
    }

    public ASN1TaggedObject getExplicitBaseTagged() {
        if (!this.isExplicit()) {
            throw new IllegalStateException("object implicit - explicit expected.");
        }
        return ASN1TaggedObject.checkedCast(this.obj.toASN1Primitive());
    }

    public ASN1TaggedObject getImplicitBaseTagged(int baseTagClass, int baseTagNo) {
        if (baseTagClass == 0 || (baseTagClass & 0xC0) != baseTagClass) {
            throw new IllegalArgumentException("invalid base tag class: " + baseTagClass);
        }
        switch (this.explicitness) {
            case 1: {
                throw new IllegalStateException("object explicit - implicit expected.");
            }
            case 2: {
                ASN1TaggedObject declared = ASN1TaggedObject.checkedCast(this.obj.toASN1Primitive());
                return ASN1Util.checkTag(declared, baseTagClass, baseTagNo);
            }
        }
        return this.replaceTag(baseTagClass, baseTagNo);
    }

    public ASN1Primitive getBaseUniversal(boolean declaredExplicit, int tagNo) {
        ASN1UniversalType universalType = ASN1UniversalTypes.get(tagNo);
        if (null == universalType) {
            throw new IllegalArgumentException("unsupported UNIVERSAL tag number: " + tagNo);
        }
        return this.getBaseUniversal(declaredExplicit, universalType);
    }

    ASN1Primitive getBaseUniversal(boolean declaredExplicit, ASN1UniversalType universalType) {
        if (declaredExplicit) {
            if (!this.isExplicit()) {
                throw new IllegalStateException("object explicit - implicit expected.");
            }
            return universalType.checkedCast(this.obj.toASN1Primitive());
        }
        if (1 == this.explicitness) {
            throw new IllegalStateException("object explicit - implicit expected.");
        }
        ASN1Primitive primitive = this.obj.toASN1Primitive();
        switch (this.explicitness) {
            case 3: {
                return universalType.fromImplicitConstructed(this.rebuildConstructed(primitive));
            }
            case 4: {
                if (primitive instanceof ASN1Sequence) {
                    return universalType.fromImplicitConstructed((ASN1Sequence)primitive);
                }
                return universalType.fromImplicitPrimitive((DEROctetString)primitive);
            }
        }
        return universalType.checkedCast(primitive);
    }

    @Override
    public ASN1Encodable parseBaseUniversal(boolean declaredExplicit, int baseTagNo) throws IOException {
        ASN1Primitive primitive = this.getBaseUniversal(declaredExplicit, baseTagNo);
        switch (baseTagNo) {
            case 3: {
                return ((ASN1BitString)primitive).parser();
            }
            case 4: {
                return ((ASN1OctetString)primitive).parser();
            }
            case 16: {
                return ((ASN1Sequence)primitive).parser();
            }
            case 17: {
                return ((ASN1Set)primitive).parser();
            }
        }
        return primitive;
    }

    @Override
    public ASN1Encodable parseExplicitBaseObject() throws IOException {
        return this.getExplicitBaseObject();
    }

    @Override
    public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
        return this.getExplicitBaseTagged();
    }

    @Override
    public ASN1TaggedObjectParser parseImplicitBaseTagged(int baseTagClass, int baseTagNo) throws IOException {
        return this.getImplicitBaseTagged(baseTagClass, baseTagNo);
    }

    @Override
    public final ASN1Primitive getLoadedObject() {
        return this;
    }

    abstract ASN1Sequence rebuildConstructed(ASN1Primitive var1);

    abstract ASN1TaggedObject replaceTag(int var1, int var2);

    @Override
    ASN1Primitive toDERObject() {
        return new DERTaggedObject(this.explicitness, this.tagClass, this.tagNo, this.obj);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLTaggedObject(this.explicitness, this.tagClass, this.tagNo, this.obj);
    }

    public String toString() {
        return ASN1Util.getTagText(this.tagClass, this.tagNo) + this.obj;
    }

    static ASN1Primitive createConstructedDL(int tagClass, int tagNo, ASN1EncodableVector contentsElements) {
        boolean maybeExplicit = contentsElements.size() == 1;
        return maybeExplicit ? new DLTaggedObject(3, tagClass, tagNo, contentsElements.get(0)) : new DLTaggedObject(4, tagClass, tagNo, (ASN1Encodable)DLFactory.createSequence(contentsElements));
    }

    static ASN1Primitive createConstructedIL(int tagClass, int tagNo, ASN1EncodableVector contentsElements) {
        boolean maybeExplicit = contentsElements.size() == 1;
        return maybeExplicit ? new BERTaggedObject(3, tagClass, tagNo, contentsElements.get(0)) : new BERTaggedObject(4, tagClass, tagNo, (ASN1Encodable)BERFactory.createSequence(contentsElements));
    }

    static ASN1Primitive createPrimitive(int tagClass, int tagNo, byte[] contentsOctets) {
        return new DLTaggedObject(4, tagClass, tagNo, (ASN1Encodable)new DEROctetString(contentsOctets));
    }

    private static ASN1TaggedObject checkedCast(ASN1Primitive primitive) {
        if (primitive instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject)primitive;
        }
        throw new IllegalStateException("unexpected object: " + primitive.getClass().getName());
    }
}


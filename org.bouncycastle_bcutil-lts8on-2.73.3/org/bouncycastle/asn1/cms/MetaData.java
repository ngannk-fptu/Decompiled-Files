/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.Attributes;

public class MetaData
extends ASN1Object {
    private ASN1Boolean hashProtected;
    private ASN1UTF8String fileName;
    private ASN1IA5String mediaType;
    private Attributes otherMetaData;

    public MetaData(ASN1Boolean hashProtected, ASN1UTF8String fileName, ASN1IA5String mediaType, Attributes otherMetaData) {
        this.hashProtected = hashProtected;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.otherMetaData = otherMetaData;
    }

    private MetaData(ASN1Sequence seq) {
        this.hashProtected = ASN1Boolean.getInstance((Object)seq.getObjectAt(0));
        int index = 1;
        if (index < seq.size() && seq.getObjectAt(index) instanceof ASN1UTF8String) {
            this.fileName = ASN1UTF8String.getInstance((Object)seq.getObjectAt(index++));
        }
        if (index < seq.size() && seq.getObjectAt(index) instanceof ASN1IA5String) {
            this.mediaType = ASN1IA5String.getInstance((Object)seq.getObjectAt(index++));
        }
        if (index < seq.size()) {
            this.otherMetaData = Attributes.getInstance(seq.getObjectAt(index++));
        }
    }

    public static MetaData getInstance(Object obj) {
        if (obj instanceof MetaData) {
            return (MetaData)((Object)obj);
        }
        if (obj != null) {
            return new MetaData(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.hashProtected);
        if (this.fileName != null) {
            v.add((ASN1Encodable)this.fileName);
        }
        if (this.mediaType != null) {
            v.add((ASN1Encodable)this.mediaType);
        }
        if (this.otherMetaData != null) {
            v.add((ASN1Encodable)this.otherMetaData);
        }
        return new DERSequence(v);
    }

    public boolean isHashProtected() {
        return this.hashProtected.isTrue();
    }

    public ASN1UTF8String getFileNameUTF8() {
        return this.fileName;
    }

    public ASN1IA5String getMediaTypeIA5() {
        return this.mediaType;
    }

    public Attributes getOtherMetaData() {
        return this.otherMetaData;
    }
}


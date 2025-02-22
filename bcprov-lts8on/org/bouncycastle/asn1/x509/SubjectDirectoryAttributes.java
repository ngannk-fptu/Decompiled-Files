/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Attribute;

public class SubjectDirectoryAttributes
extends ASN1Object {
    private Vector attributes = new Vector();

    public static SubjectDirectoryAttributes getInstance(Object obj) {
        if (obj instanceof SubjectDirectoryAttributes) {
            return (SubjectDirectoryAttributes)obj;
        }
        if (obj != null) {
            return new SubjectDirectoryAttributes(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SubjectDirectoryAttributes(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1Sequence s = ASN1Sequence.getInstance(e.nextElement());
            this.attributes.addElement(Attribute.getInstance(s));
        }
    }

    public SubjectDirectoryAttributes(Vector attributes) {
        Enumeration e = attributes.elements();
        while (e.hasMoreElements()) {
            this.attributes.addElement(e.nextElement());
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(this.attributes.size());
        Enumeration e = this.attributes.elements();
        while (e.hasMoreElements()) {
            vec.add((Attribute)e.nextElement());
        }
        return new DERSequence(vec);
    }

    public Vector getAttributes() {
        return this.attributes;
    }
}


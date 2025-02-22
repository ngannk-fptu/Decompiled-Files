/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralNames;

public class IetfAttrSyntax
extends ASN1Object {
    public static final int VALUE_OCTETS = 1;
    public static final int VALUE_OID = 2;
    public static final int VALUE_UTF8 = 3;
    GeneralNames policyAuthority = null;
    Vector values = new Vector();
    int valueChoice = -1;

    public static IetfAttrSyntax getInstance(Object obj) {
        if (obj instanceof IetfAttrSyntax) {
            return (IetfAttrSyntax)obj;
        }
        if (obj != null) {
            return new IetfAttrSyntax(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private IetfAttrSyntax(ASN1Sequence seq) {
        int i = 0;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.policyAuthority = GeneralNames.getInstance((ASN1TaggedObject)seq.getObjectAt(0), false);
            ++i;
        } else if (seq.size() == 2) {
            this.policyAuthority = GeneralNames.getInstance(seq.getObjectAt(0));
            ++i;
        }
        if (!(seq.getObjectAt(i) instanceof ASN1Sequence)) {
            throw new IllegalArgumentException("Non-IetfAttrSyntax encoding");
        }
        seq = (ASN1Sequence)seq.getObjectAt(i);
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            int type;
            ASN1Primitive obj = (ASN1Primitive)e.nextElement();
            if (obj instanceof ASN1ObjectIdentifier) {
                type = 2;
            } else if (obj instanceof ASN1UTF8String) {
                type = 3;
            } else if (obj instanceof DEROctetString) {
                type = 1;
            } else {
                throw new IllegalArgumentException("Bad value type encoding IetfAttrSyntax");
            }
            if (this.valueChoice < 0) {
                this.valueChoice = type;
            }
            if (type != this.valueChoice) {
                throw new IllegalArgumentException("Mix of value types in IetfAttrSyntax");
            }
            this.values.addElement(obj);
        }
    }

    public GeneralNames getPolicyAuthority() {
        return this.policyAuthority;
    }

    public int getValueType() {
        return this.valueChoice;
    }

    public Object[] getValues() {
        if (this.getValueType() == 1) {
            Object[] tmp = new ASN1OctetString[this.values.size()];
            for (int i = 0; i != tmp.length; ++i) {
                tmp[i] = (ASN1OctetString)this.values.elementAt(i);
            }
            return tmp;
        }
        if (this.getValueType() == 2) {
            Object[] tmp = new ASN1ObjectIdentifier[this.values.size()];
            for (int i = 0; i != tmp.length; ++i) {
                tmp[i] = (ASN1ObjectIdentifier)this.values.elementAt(i);
            }
            return tmp;
        }
        Object[] tmp = new ASN1UTF8String[this.values.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = (ASN1UTF8String)this.values.elementAt(i);
        }
        return tmp;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.policyAuthority != null) {
            v.add(new DERTaggedObject(0, this.policyAuthority));
        }
        ASN1EncodableVector v2 = new ASN1EncodableVector(this.values.size());
        Enumeration i = this.values.elements();
        while (i.hasMoreElements()) {
            v2.add((ASN1Encodable)i.nextElement());
        }
        v.add(new DERSequence(v2));
        return new DERSequence(v);
    }
}


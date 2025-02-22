/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.V2Form;

public class AttCertIssuer
extends ASN1Object
implements ASN1Choice {
    ASN1Encodable obj;
    ASN1Primitive choiceObj;

    public static AttCertIssuer getInstance(Object object) {
        if (object == null || object instanceof AttCertIssuer) {
            return (AttCertIssuer)object;
        }
        if (object instanceof V2Form) {
            return new AttCertIssuer(V2Form.getInstance(object));
        }
        if (object instanceof GeneralNames) {
            return new AttCertIssuer((GeneralNames)object);
        }
        if (object instanceof ASN1TaggedObject) {
            return new AttCertIssuer(V2Form.getInstance((ASN1TaggedObject)object, false));
        }
        if (object instanceof ASN1Sequence) {
            return new AttCertIssuer(GeneralNames.getInstance(object));
        }
        throw new IllegalArgumentException("unknown object in factory: " + object.getClass().getName());
    }

    public static AttCertIssuer getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return AttCertIssuer.getInstance(aSN1TaggedObject.getObject());
    }

    public AttCertIssuer(GeneralNames generalNames) {
        this.obj = generalNames;
        this.choiceObj = this.obj.toASN1Primitive();
    }

    public AttCertIssuer(V2Form v2Form) {
        this.obj = v2Form;
        this.choiceObj = new DERTaggedObject(false, 0, this.obj);
    }

    public ASN1Encodable getIssuer() {
        return this.obj;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.choiceObj;
    }
}


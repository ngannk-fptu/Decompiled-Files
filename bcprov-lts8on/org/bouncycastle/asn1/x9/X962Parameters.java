/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x9.X9ECParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X962Parameters
extends ASN1Object
implements ASN1Choice {
    private ASN1Primitive params = null;

    public static X962Parameters getInstance(Object obj) {
        if (obj == null || obj instanceof X962Parameters) {
            return (X962Parameters)obj;
        }
        if (obj instanceof ASN1Primitive) {
            return new X962Parameters((ASN1Primitive)obj);
        }
        if (obj instanceof byte[]) {
            try {
                return new X962Parameters(ASN1Primitive.fromByteArray((byte[])obj));
            }
            catch (Exception e) {
                throw new IllegalArgumentException("unable to parse encoded data: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance()");
    }

    public static X962Parameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return X962Parameters.getInstance(obj.getExplicitBaseObject());
    }

    public X962Parameters(X9ECParameters ecParameters) {
        this.params = ecParameters.toASN1Primitive();
    }

    public X962Parameters(ASN1ObjectIdentifier namedCurve) {
        this.params = namedCurve;
    }

    public X962Parameters(ASN1Null obj) {
        this.params = obj;
    }

    private X962Parameters(ASN1Primitive obj) {
        this.params = obj;
    }

    public boolean isNamedCurve() {
        return this.params instanceof ASN1ObjectIdentifier;
    }

    public boolean isImplicitlyCA() {
        return this.params instanceof ASN1Null;
    }

    public ASN1Primitive getParameters() {
        return this.params;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.params;
    }
}


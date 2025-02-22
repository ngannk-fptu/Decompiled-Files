/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

public class TypeOfBiometricData
extends ASN1Object
implements ASN1Choice {
    public static final int PICTURE = 0;
    public static final int HANDWRITTEN_SIGNATURE = 1;
    ASN1Encodable obj;

    public static TypeOfBiometricData getInstance(Object obj) {
        if (obj == null || obj instanceof TypeOfBiometricData) {
            return (TypeOfBiometricData)obj;
        }
        if (obj instanceof ASN1Integer) {
            ASN1Integer predefinedBiometricTypeObj = ASN1Integer.getInstance(obj);
            int predefinedBiometricType = predefinedBiometricTypeObj.intValueExact();
            return new TypeOfBiometricData(predefinedBiometricType);
        }
        if (obj instanceof ASN1ObjectIdentifier) {
            ASN1ObjectIdentifier BiometricDataID = ASN1ObjectIdentifier.getInstance(obj);
            return new TypeOfBiometricData(BiometricDataID);
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }

    public TypeOfBiometricData(int predefinedBiometricType) {
        if (predefinedBiometricType != 0 && predefinedBiometricType != 1) {
            throw new IllegalArgumentException("unknow PredefinedBiometricType : " + predefinedBiometricType);
        }
        this.obj = new ASN1Integer(predefinedBiometricType);
    }

    public TypeOfBiometricData(ASN1ObjectIdentifier BiometricDataID) {
        this.obj = BiometricDataID;
    }

    public boolean isPredefined() {
        return this.obj instanceof ASN1Integer;
    }

    public int getPredefinedBiometricType() {
        return ((ASN1Integer)this.obj).intValueExact();
    }

    public ASN1ObjectIdentifier getBiometricDataOid() {
        return (ASN1ObjectIdentifier)this.obj;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.obj.toASN1Primitive();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 */
package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class ServiceType
extends ASN1Object {
    public static final ServiceType CPD = new ServiceType(1);
    public static final ServiceType VSD = new ServiceType(2);
    public static final ServiceType VPKC = new ServiceType(3);
    public static final ServiceType CCPD = new ServiceType(4);
    private ASN1Enumerated value;

    public ServiceType(int value) {
        this.value = new ASN1Enumerated(value);
    }

    private ServiceType(ASN1Enumerated value) {
        this.value = value;
    }

    public static ServiceType getInstance(Object obj) {
        if (obj instanceof ServiceType) {
            return (ServiceType)((Object)obj);
        }
        if (obj != null) {
            return new ServiceType(ASN1Enumerated.getInstance((Object)obj));
        }
        return null;
    }

    public static ServiceType getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ServiceType.getInstance(ASN1Enumerated.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public BigInteger getValue() {
        return this.value.getValue();
    }

    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }

    public String toString() {
        int num = this.value.intValueExact();
        return "" + num + (num == ServiceType.CPD.value.intValueExact() ? "(CPD)" : (num == ServiceType.VSD.value.intValueExact() ? "(VSD)" : (num == ServiceType.VPKC.value.intValueExact() ? "(VPKC)" : (num == ServiceType.CCPD.value.intValueExact() ? "(CCPD)" : "?"))));
    }
}


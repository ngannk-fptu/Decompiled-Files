/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class CertificateType
extends ASN1Enumerated {
    public static final CertificateType explicit = new CertificateType(BigInteger.ZERO);
    public static final CertificateType implicit = new CertificateType(BigInteger.ONE);

    public CertificateType(BigInteger ordinal) {
        super(ordinal);
        this.assertValues();
    }

    private CertificateType(ASN1Enumerated instance) {
        this(instance.getValue());
    }

    public static CertificateType getInstance(Object src) {
        if (src instanceof CertificateType) {
            return (CertificateType)((Object)src);
        }
        if (src != null) {
            return new CertificateType(ASN1Enumerated.getInstance((Object)src));
        }
        return null;
    }

    protected void assertValues() {
        if (this.getValue().compareTo(BigInteger.ZERO) < 0 || this.getValue().compareTo(BigIntegers.ONE) > 0) {
            throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
        }
    }
}


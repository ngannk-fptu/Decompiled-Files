/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Enumerated
 */
package org.bouncycastle.oer.its.etsi102941;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;

public class EnrolmentResponseCode
extends ASN1Enumerated {
    public static final EnrolmentResponseCode ok = new EnrolmentResponseCode(0);
    public static final EnrolmentResponseCode cantparse = new EnrolmentResponseCode(1);
    public static final EnrolmentResponseCode badcontenttype = new EnrolmentResponseCode(2);
    public static final EnrolmentResponseCode imnottherecipient = new EnrolmentResponseCode(3);
    public static final EnrolmentResponseCode unknownencryptionalgorithm = new EnrolmentResponseCode(4);
    public static final EnrolmentResponseCode decryptionfailed = new EnrolmentResponseCode(5);
    public static final EnrolmentResponseCode unknownits = new EnrolmentResponseCode(6);
    public static final EnrolmentResponseCode invalidsignature = new EnrolmentResponseCode(7);
    public static final EnrolmentResponseCode invalidencryptionkey = new EnrolmentResponseCode(8);
    public static final EnrolmentResponseCode baditsstatus = new EnrolmentResponseCode(9);
    public static final EnrolmentResponseCode incompleterequest = new EnrolmentResponseCode(10);
    public static final EnrolmentResponseCode deniedpermissions = new EnrolmentResponseCode(11);
    public static final EnrolmentResponseCode invalidkeys = new EnrolmentResponseCode(12);
    public static final EnrolmentResponseCode deniedrequest = new EnrolmentResponseCode(13);

    public EnrolmentResponseCode(int value) {
        super(value);
        this.assertValues();
    }

    public EnrolmentResponseCode(BigInteger value) {
        super(value);
        this.assertValues();
    }

    public EnrolmentResponseCode(byte[] contents) {
        super(contents);
        this.assertValues();
    }

    private EnrolmentResponseCode(ASN1Enumerated enumerated) {
        this(enumerated.getValue());
    }

    public static EnrolmentResponseCode getInstance(Object o) {
        if (o instanceof EnrolmentResponseCode) {
            return (EnrolmentResponseCode)((Object)o);
        }
        if (o != null) {
            return new EnrolmentResponseCode(ASN1Enumerated.getInstance((Object)o));
        }
        return null;
    }

    protected void assertValues() {
        if (this.getValue().intValue() < 0 || this.getValue().intValue() > 13) {
            throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
        }
    }
}


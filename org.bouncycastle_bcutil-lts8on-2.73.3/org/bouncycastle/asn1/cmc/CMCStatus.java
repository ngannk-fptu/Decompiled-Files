/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.asn1.cmc;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class CMCStatus
extends ASN1Object {
    public static final CMCStatus success = new CMCStatus(new ASN1Integer(0L));
    public static final CMCStatus failed = new CMCStatus(new ASN1Integer(2L));
    public static final CMCStatus pending = new CMCStatus(new ASN1Integer(3L));
    public static final CMCStatus noSupport = new CMCStatus(new ASN1Integer(4L));
    public static final CMCStatus confirmRequired = new CMCStatus(new ASN1Integer(5L));
    public static final CMCStatus popRequired = new CMCStatus(new ASN1Integer(6L));
    public static final CMCStatus partial = new CMCStatus(new ASN1Integer(7L));
    private static Map range = new HashMap();
    private final ASN1Integer value;

    private CMCStatus(ASN1Integer value) {
        this.value = value;
    }

    public static CMCStatus getInstance(Object o) {
        if (o instanceof CMCStatus) {
            return (CMCStatus)((Object)o);
        }
        if (o != null) {
            CMCStatus status = (CMCStatus)((Object)range.get(ASN1Integer.getInstance((Object)o)));
            if (status != null) {
                return status;
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }

    static {
        range.put(CMCStatus.success.value, success);
        range.put(CMCStatus.failed.value, failed);
        range.put(CMCStatus.pending.value, pending);
        range.put(CMCStatus.noSupport.value, noSupport);
        range.put(CMCStatus.confirmRequired.value, confirmRequired);
        range.put(CMCStatus.popRequired.value, popRequired);
        range.put(CMCStatus.partial.value, partial);
    }
}


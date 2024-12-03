/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class POPODecKeyRespContent
extends ASN1Object {
    private final ASN1Sequence content;

    private POPODecKeyRespContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public static POPODecKeyRespContent getInstance(Object o) {
        if (o instanceof POPODecKeyRespContent) {
            return (POPODecKeyRespContent)((Object)o);
        }
        if (o != null) {
            return new POPODecKeyRespContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Integer[] toASN1IntegerArray() {
        ASN1Integer[] result = new ASN1Integer[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = ASN1Integer.getInstance((Object)this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}


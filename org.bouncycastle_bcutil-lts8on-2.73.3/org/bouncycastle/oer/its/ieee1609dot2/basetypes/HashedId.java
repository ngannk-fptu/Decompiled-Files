/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class HashedId
extends ASN1Object {
    private final byte[] id;

    protected HashedId(byte[] string) {
        this.id = Arrays.clone((byte[])string);
    }

    public byte[] getHashBytes() {
        return Arrays.clone((byte[])this.id);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.id);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        HashedId hashedId = (HashedId)((Object)o);
        return java.util.Arrays.equals(this.id, hashedId.id);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + java.util.Arrays.hashCode(this.id);
        return result;
    }
}


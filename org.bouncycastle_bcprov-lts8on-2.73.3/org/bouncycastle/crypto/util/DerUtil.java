/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

class DerUtil {
    DerUtil() {
    }

    static ASN1OctetString getOctetString(byte[] data) {
        if (data == null) {
            return new DEROctetString(new byte[0]);
        }
        return new DEROctetString(Arrays.clone(data));
    }

    static byte[] toByteArray(ASN1Primitive primitive) {
        try {
            return primitive.getEncoded();
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot get encoding: " + e.getMessage()){

                @Override
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X9IntegerConverter {
    public int getByteLength(ECCurve c) {
        return (c.getFieldSize() + 7) / 8;
    }

    public int getByteLength(ECFieldElement fe) {
        return (fe.getFieldSize() + 7) / 8;
    }

    public byte[] integerToBytes(BigInteger s, int qLength) {
        byte[] bytes = s.toByteArray();
        if (qLength < bytes.length) {
            byte[] tmp = new byte[qLength];
            System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
            return tmp;
        }
        if (qLength > bytes.length) {
            byte[] tmp = new byte[qLength];
            System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
            return tmp;
        }
        return bytes;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 */
package org.bouncycastle.oer;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEROutputStream;

public class OEREncoder {
    public static byte[] toByteArray(ASN1Encodable encodable, Element oerElement) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new OEROutputStream(bos).write(encodable, oerElement);
            bos.flush();
            bos.close();
            return bos.toByteArray();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
}


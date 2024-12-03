/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.cert.cmp.CMPRuntimeException;

class CMPUtil {
    CMPUtil() {
    }

    static void derEncodeToStream(ASN1Object obj, OutputStream stream) {
        try {
            obj.encodeTo(stream, "DER");
            stream.close();
        }
        catch (IOException e) {
            throw new CMPRuntimeException("unable to DER encode object: " + e.getMessage(), e);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 */
package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.crmf.CRMFRuntimeException;

class CRMFUtil {
    CRMFUtil() {
    }

    static void derEncodeToStream(ASN1Object obj, OutputStream stream) {
        try {
            obj.encodeTo(stream, "DER");
            stream.close();
        }
        catch (IOException e) {
            throw new CRMFRuntimeException("unable to DER encode object: " + e.getMessage(), e);
        }
    }

    static void addExtension(ExtensionsGenerator extGenerator, ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            extGenerator.addExtension(oid, isCritical, value);
        }
        catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
    }
}


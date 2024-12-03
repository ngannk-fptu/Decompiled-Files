/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers
 *  org.bouncycastle.asn1.ocsp.OCSPResponse
 *  org.bouncycastle.asn1.ocsp.OCSPResponseStatus
 *  org.bouncycastle.asn1.ocsp.ResponseBytes
 */
package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;

public class OCSPRespBuilder {
    public static final int SUCCESSFUL = 0;
    public static final int MALFORMED_REQUEST = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int TRY_LATER = 3;
    public static final int SIG_REQUIRED = 5;
    public static final int UNAUTHORIZED = 6;

    public OCSPResp build(int status, Object response) throws OCSPException {
        if (response == null) {
            return new OCSPResp(new OCSPResponse(new OCSPResponseStatus(status), null));
        }
        if (response instanceof BasicOCSPResp) {
            DEROctetString octs;
            BasicOCSPResp r = (BasicOCSPResp)response;
            try {
                octs = new DEROctetString(r.getEncoded());
            }
            catch (IOException e) {
                throw new OCSPException("can't encode object.", e);
            }
            ResponseBytes rb = new ResponseBytes(OCSPObjectIdentifiers.id_pkix_ocsp_basic, (ASN1OctetString)octs);
            return new OCSPResp(new OCSPResponse(new OCSPResponseStatus(status), rb));
        }
        throw new OCSPException("unknown response object");
    }
}


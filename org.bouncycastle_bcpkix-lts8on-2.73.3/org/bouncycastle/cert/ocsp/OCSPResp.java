/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Exception
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ocsp.BasicOCSPResponse
 *  org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers
 *  org.bouncycastle.asn1.ocsp.OCSPResponse
 *  org.bouncycastle.asn1.ocsp.ResponseBytes
 */
package org.bouncycastle.cert.ocsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;

public class OCSPResp {
    public static final int SUCCESSFUL = 0;
    public static final int MALFORMED_REQUEST = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int TRY_LATER = 3;
    public static final int SIG_REQUIRED = 5;
    public static final int UNAUTHORIZED = 6;
    private OCSPResponse resp;

    public OCSPResp(OCSPResponse resp) {
        this.resp = resp;
    }

    public OCSPResp(byte[] resp) throws IOException {
        this(new ByteArrayInputStream(resp));
    }

    public OCSPResp(InputStream resp) throws IOException {
        this(new ASN1InputStream(resp));
    }

    private OCSPResp(ASN1InputStream aIn) throws IOException {
        try {
            this.resp = OCSPResponse.getInstance((Object)aIn.readObject());
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        }
        catch (ASN1Exception e) {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        }
        if (this.resp == null) {
            throw new CertIOException("malformed response: no response data found");
        }
    }

    public int getStatus() {
        return this.resp.getResponseStatus().getIntValue();
    }

    public Object getResponseObject() throws OCSPException {
        ResponseBytes rb = this.resp.getResponseBytes();
        if (rb == null) {
            return null;
        }
        if (rb.getResponseType().equals((ASN1Primitive)OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
            try {
                ASN1Primitive obj = ASN1Primitive.fromByteArray((byte[])rb.getResponse().getOctets());
                return new BasicOCSPResp(BasicOCSPResponse.getInstance((Object)obj));
            }
            catch (Exception e) {
                throw new OCSPException("problem decoding object: " + e, e);
            }
        }
        return rb.getResponse();
    }

    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OCSPResp)) {
            return false;
        }
        OCSPResp r = (OCSPResp)o;
        return this.resp.equals((Object)r.resp);
    }

    public int hashCode() {
        return this.resp.hashCode();
    }

    public OCSPResponse toASN1Structure() {
        return this.resp;
    }
}


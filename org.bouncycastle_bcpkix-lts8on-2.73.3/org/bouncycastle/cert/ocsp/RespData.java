/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ocsp.ResponseData
 *  org.bouncycastle.asn1.ocsp.SingleResponse
 *  org.bouncycastle.asn1.x509.Extensions
 */
package org.bouncycastle.cert.ocsp;

import java.util.Date;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.ocsp.OCSPUtils;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.cert.ocsp.SingleResp;

public class RespData {
    private ResponseData data;

    public RespData(ResponseData data) {
        this.data = data;
    }

    public int getVersion() {
        return this.data.getVersion().intValueExact() + 1;
    }

    public RespID getResponderId() {
        return new RespID(this.data.getResponderID());
    }

    public Date getProducedAt() {
        return OCSPUtils.extractDate(this.data.getProducedAt());
    }

    public SingleResp[] getResponses() {
        ASN1Sequence s = this.data.getResponses();
        SingleResp[] rs = new SingleResp[s.size()];
        for (int i = 0; i != rs.length; ++i) {
            rs[i] = new SingleResp(SingleResponse.getInstance((Object)s.getObjectAt(i)));
        }
        return rs;
    }

    public Extensions getResponseExtensions() {
        return this.data.getResponseExtensions();
    }
}


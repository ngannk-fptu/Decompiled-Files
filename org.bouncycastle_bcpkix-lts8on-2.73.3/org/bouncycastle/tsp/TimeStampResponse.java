/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DLSequence
 *  org.bouncycastle.asn1.cmp.PKIFailureInfo
 *  org.bouncycastle.asn1.cmp.PKIFreeText
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.tsp.TimeStampResp
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.tsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Arrays;

public class TimeStampResponse {
    TimeStampResp resp;
    TimeStampToken timeStampToken;

    public TimeStampResponse(TimeStampResp resp) throws TSPException, IOException {
        this.resp = resp;
        if (resp.getTimeStampToken() != null) {
            this.timeStampToken = new TimeStampToken(resp.getTimeStampToken());
        }
    }

    public TimeStampResponse(byte[] resp) throws TSPException, IOException {
        this(new ByteArrayInputStream(resp));
    }

    public TimeStampResponse(InputStream in) throws TSPException, IOException {
        this(TimeStampResponse.readTimeStampResp(in));
    }

    TimeStampResponse(DLSequence dlSequence) throws TSPException, IOException {
        try {
            this.resp = TimeStampResp.getInstance((Object)dlSequence);
            this.timeStampToken = new TimeStampToken(ContentInfo.getInstance((Object)dlSequence.getObjectAt(1)));
        }
        catch (IllegalArgumentException e) {
            throw new TSPException("malformed timestamp response: " + e, e);
        }
        catch (ClassCastException e) {
            throw new TSPException("malformed timestamp response: " + e, e);
        }
    }

    private static TimeStampResp readTimeStampResp(InputStream in) throws IOException, TSPException {
        try {
            return TimeStampResp.getInstance((Object)new ASN1InputStream(in).readObject());
        }
        catch (IllegalArgumentException e) {
            throw new TSPException("malformed timestamp response: " + e, e);
        }
        catch (ClassCastException e) {
            throw new TSPException("malformed timestamp response: " + e, e);
        }
    }

    public int getStatus() {
        return this.resp.getStatus().getStatus().intValue();
    }

    public String getStatusString() {
        if (this.resp.getStatus().getStatusString() != null) {
            StringBuffer statusStringBuf = new StringBuffer();
            PKIFreeText text = this.resp.getStatus().getStatusString();
            for (int i = 0; i != text.size(); ++i) {
                statusStringBuf.append(text.getStringAtUTF8(i).getString());
            }
            return statusStringBuf.toString();
        }
        return null;
    }

    public PKIFailureInfo getFailInfo() {
        if (this.resp.getStatus().getFailInfo() != null) {
            return new PKIFailureInfo(this.resp.getStatus().getFailInfo());
        }
        return null;
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    public void validate(TimeStampRequest request) throws TSPException {
        TimeStampToken tok = this.getTimeStampToken();
        if (tok != null) {
            TimeStampTokenInfo tstInfo = tok.getTimeStampInfo();
            if (request.getNonce() != null && !request.getNonce().equals(tstInfo.getNonce())) {
                throw new TSPValidationException("response contains wrong nonce value.");
            }
            if (this.getStatus() != 0 && this.getStatus() != 1) {
                throw new TSPValidationException("time stamp token found in failed request.");
            }
            if (!Arrays.constantTimeAreEqual((byte[])request.getMessageImprintDigest(), (byte[])tstInfo.getMessageImprintDigest())) {
                throw new TSPValidationException("response for different message imprint digest.");
            }
            if (!tstInfo.getMessageImprintAlgOID().equals((ASN1Primitive)request.getMessageImprintAlgOID())) {
                throw new TSPValidationException("response for different message imprint algorithm.");
            }
            Attribute scV1 = tok.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
            Attribute scV2 = tok.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
            if (scV1 == null && scV2 == null) {
                throw new TSPValidationException("no signing certificate attribute present.");
            }
            if (scV1 == null || scV2 != null) {
                // empty if block
            }
            if (request.getReqPolicy() != null && !request.getReqPolicy().equals((ASN1Primitive)tstInfo.getPolicy())) {
                throw new TSPValidationException("TSA policy wrong for request.");
            }
        } else if (this.getStatus() == 0 || this.getStatus() == 1) {
            throw new TSPValidationException("no time stamp token found and one expected.");
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        if ("DL".equals(encoding)) {
            if (this.timeStampToken == null) {
                return new DLSequence((ASN1Encodable)this.resp.getStatus()).getEncoded(encoding);
            }
            return new DLSequence(new ASN1Encodable[]{this.resp.getStatus(), this.timeStampToken.toCMSSignedData().toASN1Structure()}).getEncoded(encoding);
        }
        return this.resp.getEncoded(encoding);
    }
}


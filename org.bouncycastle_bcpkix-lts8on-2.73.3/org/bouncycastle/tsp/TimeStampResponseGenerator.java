/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERUTF8String
 *  org.bouncycastle.asn1.DLSequence
 *  org.bouncycastle.asn1.cmp.PKIFreeText
 *  org.bouncycastle.asn1.cmp.PKIStatusInfo
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.tsp.TimeStampResp
 *  org.bouncycastle.asn1.x509.Extensions
 */
package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampTokenGenerator;

public class TimeStampResponseGenerator {
    int status;
    ASN1EncodableVector statusStrings;
    int failInfo;
    private TimeStampTokenGenerator tokenGenerator;
    private Set acceptedAlgorithms;
    private Set acceptedPolicies;
    private Set acceptedExtensions;

    public TimeStampResponseGenerator(TimeStampTokenGenerator tokenGenerator, Set acceptedAlgorithms) {
        this(tokenGenerator, acceptedAlgorithms, null, null);
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator tokenGenerator, Set acceptedAlgorithms, Set acceptedPolicies) {
        this(tokenGenerator, acceptedAlgorithms, acceptedPolicies, null);
    }

    public TimeStampResponseGenerator(TimeStampTokenGenerator tokenGenerator, Set acceptedAlgorithms, Set acceptedPolicies, Set acceptedExtensions) {
        this.tokenGenerator = tokenGenerator;
        this.acceptedAlgorithms = this.convert(acceptedAlgorithms);
        this.acceptedPolicies = this.convert(acceptedPolicies);
        this.acceptedExtensions = this.convert(acceptedExtensions);
        this.statusStrings = new ASN1EncodableVector();
    }

    private void addStatusString(String statusString) {
        this.statusStrings.add((ASN1Encodable)new DERUTF8String(statusString));
    }

    private void setFailInfoField(int field) {
        this.failInfo |= field;
    }

    private PKIStatusInfo getPKIStatusInfo() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)new ASN1Integer((long)this.status));
        if (this.statusStrings.size() > 0) {
            v.add((ASN1Encodable)PKIFreeText.getInstance((Object)new DERSequence(this.statusStrings)));
        }
        if (this.failInfo != 0) {
            FailInfo failInfoBitString = new FailInfo(this.failInfo);
            v.add((ASN1Encodable)failInfoBitString);
        }
        return PKIStatusInfo.getInstance((Object)new DERSequence(v));
    }

    public TimeStampResponse generate(TimeStampRequest request, BigInteger serialNumber, Date genTime) throws TSPException {
        try {
            return this.generateGrantedResponse(request, serialNumber, genTime, "Operation Okay");
        }
        catch (Exception e) {
            return this.generateRejectedResponse(e);
        }
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest request, BigInteger serialNumber, Date genTime) throws TSPException {
        return this.generateGrantedResponse(request, serialNumber, genTime, null);
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest request, BigInteger serialNumber, Date genTime, String statusString) throws TSPException {
        return this.generateGrantedResponse(request, serialNumber, genTime, statusString, null);
    }

    public TimeStampResponse generateGrantedResponse(TimeStampRequest request, BigInteger serialNumber, Date genTime, String statusString, Extensions additionalExtensions) throws TSPException {
        ContentInfo tstTokenContentInfo;
        if (genTime == null) {
            throw new TSPValidationException("The time source is not available.", 512);
        }
        request.validate(this.acceptedAlgorithms, this.acceptedPolicies, this.acceptedExtensions);
        this.status = 0;
        this.statusStrings = new ASN1EncodableVector();
        if (statusString != null) {
            this.addStatusString(statusString);
        }
        PKIStatusInfo pkiStatusInfo = this.getPKIStatusInfo();
        try {
            tstTokenContentInfo = this.tokenGenerator.generate(request, serialNumber, genTime, additionalExtensions).toCMSSignedData().toASN1Structure();
        }
        catch (TSPException e) {
            throw e;
        }
        catch (Exception e) {
            throw new TSPException("Timestamp token received cannot be converted to ContentInfo", e);
        }
        try {
            return new TimeStampResponse(new DLSequence(new ASN1Encodable[]{pkiStatusInfo.toASN1Primitive(), tstTokenContentInfo.toASN1Primitive()}));
        }
        catch (IOException e) {
            throw new TSPException("created badly formatted response!");
        }
    }

    public TimeStampResponse generateRejectedResponse(Exception exception) throws TSPException {
        if (exception instanceof TSPValidationException) {
            return this.generateFailResponse(2, ((TSPValidationException)exception).getFailureCode(), exception.getMessage());
        }
        return this.generateFailResponse(2, 0x40000000, exception.getMessage());
    }

    public TimeStampResponse generateFailResponse(int status, int failInfoField, String statusString) throws TSPException {
        this.status = status;
        this.statusStrings = new ASN1EncodableVector();
        this.setFailInfoField(failInfoField);
        if (statusString != null) {
            this.addStatusString(statusString);
        }
        PKIStatusInfo pkiStatusInfo = this.getPKIStatusInfo();
        TimeStampResp resp = new TimeStampResp(pkiStatusInfo, null);
        try {
            return new TimeStampResponse(resp);
        }
        catch (IOException e) {
            throw new TSPException("created badly formatted response!");
        }
    }

    private Set convert(Set orig) {
        if (orig == null) {
            return orig;
        }
        HashSet<Object> con = new HashSet<Object>(orig.size());
        for (Object o : orig) {
            if (o instanceof String) {
                con.add(new ASN1ObjectIdentifier((String)o));
                continue;
            }
            con.add(o);
        }
        return con;
    }

    static class FailInfo
    extends DERBitString {
        FailInfo(int failInfoValue) {
            super(FailInfo.getBytes((int)failInfoValue), FailInfo.getPadBits((int)failInfoValue));
        }
    }
}


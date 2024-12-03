/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.tsp.MessageImprint
 *  org.bouncycastle.asn1.tsp.TimeStampReq
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 */
package org.bouncycastle.tsp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.tsp.TSPIOException;
import org.bouncycastle.tsp.TSPUtil;
import org.bouncycastle.tsp.TimeStampRequest;

public class TimeStampRequestGenerator {
    private static final DefaultDigestAlgorithmIdentifierFinder dgstAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private ASN1ObjectIdentifier reqPolicy;
    private ASN1Boolean certReq;
    private ExtensionsGenerator extGenerator = new ExtensionsGenerator();

    public void setReqPolicy(ASN1ObjectIdentifier reqPolicy) {
        this.reqPolicy = reqPolicy;
    }

    public void setCertReq(boolean certReq) {
        this.certReq = ASN1Boolean.getInstance((boolean)certReq);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws TSPIOException {
        TSPUtil.addExtension(this.extGenerator, oid, isCritical, value);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] value) {
        this.extGenerator.addExtension(oid, isCritical, value);
    }

    public TimeStampRequest generate(ASN1ObjectIdentifier digestAlgorithm, byte[] digest) {
        return this.generate(dgstAlgFinder.find(digestAlgorithm), digest);
    }

    public TimeStampRequest generate(ASN1ObjectIdentifier digestAlgorithm, byte[] digest, BigInteger nonce) {
        return this.generate(dgstAlgFinder.find(digestAlgorithm), digest, nonce);
    }

    public TimeStampRequest generate(AlgorithmIdentifier digestAlgorithmID, byte[] digest) {
        return this.generate(digestAlgorithmID, digest, null);
    }

    public TimeStampRequest generate(AlgorithmIdentifier digestAlgorithmID, byte[] digest, BigInteger nonce) {
        if (digestAlgorithmID == null) {
            throw new IllegalArgumentException("digest algorithm not specified");
        }
        MessageImprint messageImprint = new MessageImprint(digestAlgorithmID, digest);
        Extensions ext = null;
        if (!this.extGenerator.isEmpty()) {
            ext = this.extGenerator.generate();
        }
        if (nonce != null) {
            return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, new ASN1Integer(nonce), this.certReq, ext));
        }
        return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, null, this.certReq, ext));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.digests.SHA1Digest
 */
package org.bouncycastle.pkcs.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.PKCS12PBEUtils;

public class BcPKCS12MacCalculatorBuilder
implements PKCS12MacCalculatorBuilder {
    private ExtendedDigest digest;
    private AlgorithmIdentifier algorithmIdentifier;
    private SecureRandom random;
    private int saltLength;
    private int iterationCount = 1024;

    public BcPKCS12MacCalculatorBuilder() {
        this((ExtendedDigest)new SHA1Digest(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
    }

    public BcPKCS12MacCalculatorBuilder(ExtendedDigest digest, AlgorithmIdentifier algorithmIdentifier) {
        this.digest = digest;
        this.algorithmIdentifier = algorithmIdentifier;
        this.saltLength = digest.getDigestSize();
    }

    public BcPKCS12MacCalculatorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    @Override
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }

    @Override
    public MacCalculator build(char[] password) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        byte[] salt = new byte[this.saltLength];
        this.random.nextBytes(salt);
        return PKCS12PBEUtils.createMacCalculator(this.algorithmIdentifier.getAlgorithm(), this.digest, new PKCS12PBEParams(salt, this.iterationCount), password);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.cmp.CMPObjectIdentifiers
 *  org.bouncycastle.asn1.cmp.PBMParameter
 *  org.bouncycastle.asn1.iana.IANAObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACValuesCalculator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.Strings;

public class PKMACBuilder
implements PBEMacCalculatorProvider {
    private AlgorithmIdentifier owf;
    private int iterationCount;
    private AlgorithmIdentifier mac;
    private int saltLength = 20;
    private SecureRandom random;
    private PKMACValuesCalculator calculator;
    private PBMParameter parameters;
    private int maxIterations;

    public PKMACBuilder(PKMACValuesCalculator calculator) {
        this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, (ASN1Encodable)DERNull.INSTANCE), calculator);
    }

    public PKMACBuilder(PKMACValuesCalculator calculator, int maxIterations) {
        this.maxIterations = maxIterations;
        this.calculator = calculator;
    }

    private PKMACBuilder(AlgorithmIdentifier hashAlgorithm, int iterationCount, AlgorithmIdentifier macAlgorithm, PKMACValuesCalculator calculator) {
        this.owf = hashAlgorithm;
        this.iterationCount = iterationCount;
        this.mac = macAlgorithm;
        this.calculator = calculator;
    }

    public PKMACBuilder setSaltLength(int saltLength) {
        if (saltLength < 8) {
            throw new IllegalArgumentException("salt length must be at least 8 bytes");
        }
        this.saltLength = saltLength;
        return this;
    }

    public PKMACBuilder setIterationCount(int iterationCount) {
        if (iterationCount < 100) {
            throw new IllegalArgumentException("iteration count must be at least 100");
        }
        this.checkIterationCountCeiling(iterationCount);
        this.iterationCount = iterationCount;
        return this;
    }

    public PKMACBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public PKMACBuilder setParameters(PBMParameter parameters) {
        this.checkIterationCountCeiling(parameters.getIterationCount().intValueExact());
        this.parameters = parameters;
        return this;
    }

    @Override
    public MacCalculator get(AlgorithmIdentifier algorithm, char[] password) throws OperatorCreationException {
        if (!CMPObjectIdentifiers.passwordBasedMac.equals((ASN1Primitive)algorithm.getAlgorithm())) {
            throw new OperatorCreationException("protection algorithm not mac based");
        }
        this.setParameters(PBMParameter.getInstance((Object)algorithm.getParameters()));
        try {
            return this.build(password);
        }
        catch (CRMFException e) {
            throw new OperatorCreationException(e.getMessage(), e.getCause());
        }
    }

    public MacCalculator build(char[] password) throws CRMFException {
        PBMParameter pbmParameter = this.parameters;
        if (pbmParameter == null) {
            pbmParameter = this.genParameters();
        }
        return this.genCalculator(pbmParameter, password);
    }

    private void checkIterationCountCeiling(int iterationCount) {
        if (this.maxIterations > 0 && iterationCount > this.maxIterations) {
            throw new IllegalArgumentException("iteration count exceeds limit (" + iterationCount + " > " + this.maxIterations + ")");
        }
    }

    private MacCalculator genCalculator(final PBMParameter params, char[] password) throws CRMFException {
        byte[] pw = Strings.toUTF8ByteArray((char[])password);
        byte[] salt = params.getSalt().getOctets();
        byte[] K = new byte[pw.length + salt.length];
        System.arraycopy(pw, 0, K, 0, pw.length);
        System.arraycopy(salt, 0, K, pw.length, salt.length);
        this.calculator.setup(params.getOwf(), params.getMac());
        int iter = params.getIterationCount().intValueExact();
        do {
            K = this.calculator.calculateDigest(K);
        } while (--iter > 0);
        final byte[] key = K;
        return new MacCalculator(){
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, (ASN1Encodable)params);
            }

            @Override
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), key);
            }

            @Override
            public OutputStream getOutputStream() {
                return this.bOut;
            }

            @Override
            public byte[] getMac() {
                try {
                    return PKMACBuilder.this.calculator.calculateMac(key, this.bOut.toByteArray());
                }
                catch (CRMFException e) {
                    throw new RuntimeOperatorException("exception calculating mac: " + e.getMessage(), e);
                }
            }
        };
    }

    private PBMParameter genParameters() {
        byte[] salt = new byte[this.saltLength];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(salt);
        return new PBMParameter(salt, this.owf, this.iterationCount, this.mac);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSAUtil;

public class DSASigner
extends SignatureSpi
implements PKCSObjectIdentifiers,
X509ObjectIdentifiers {
    private Digest digest;
    private DSAExt signer;
    private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
    private SecureRandom random;

    protected DSASigner(Digest digest, DSAExt dSAExt) {
        this.digest = digest;
        this.signer = dSAExt;
    }

    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter = DSAUtil.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, asymmetricKeyParameter);
    }

    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        this.engineInitSign(privateKey);
    }

    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters cipherParameters = DSAUtil.generatePrivateKeyParameter(privateKey);
        if (this.random != null) {
            cipherParameters = new ParametersWithRandom(cipherParameters, this.random);
        }
        this.digest.reset();
        this.signer.init(true, cipherParameters);
    }

    protected void engineUpdate(byte by) throws SignatureException {
        this.digest.update(by);
    }

    protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        this.digest.update(byArray, n, n2);
    }

    protected byte[] engineSign() throws SignatureException {
        byte[] byArray = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray, 0);
        try {
            BigInteger[] bigIntegerArray = this.signer.generateSignature(byArray);
            return this.encoding.encode(this.signer.getOrder(), bigIntegerArray[0], bigIntegerArray[1]);
        }
        catch (Exception exception) {
            throw new SignatureException(exception.toString());
        }
    }

    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        BigInteger[] bigIntegerArray;
        byte[] byArray2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray2, 0);
        try {
            bigIntegerArray = this.encoding.decode(this.signer.getOrder(), byArray);
        }
        catch (Exception exception) {
            throw new SignatureException("error decoding signature bytes.");
        }
        return this.signer.verifySignature(byArray2, bigIntegerArray[0], bigIntegerArray[1]);
    }

    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    protected void engineSetParameter(String string, Object object) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    protected Object engineGetParameter(String string) {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    public static class detDSA
    extends DSASigner {
        public detDSA() {
            super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA1())));
        }
    }

    public static class detDSA224
    extends DSASigner {
        public detDSA224() {
            super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA224())));
        }
    }

    public static class detDSA256
    extends DSASigner {
        public detDSA256() {
            super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA256())));
        }
    }

    public static class detDSA384
    extends DSASigner {
        public detDSA384() {
            super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA384())));
        }
    }

    public static class detDSA512
    extends DSASigner {
        public detDSA512() {
            super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA512())));
        }
    }

    public static class detDSASha3_224
    extends DSASigner {
        public detDSASha3_224() {
            super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_224())));
        }
    }

    public static class detDSASha3_256
    extends DSASigner {
        public detDSASha3_256() {
            super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_256())));
        }
    }

    public static class detDSASha3_384
    extends DSASigner {
        public detDSASha3_384() {
            super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_384())));
        }
    }

    public static class detDSASha3_512
    extends DSASigner {
        public detDSASha3_512() {
            super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_512())));
        }
    }

    public static class dsa224
    extends DSASigner {
        public dsa224() {
            super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsa256
    extends DSASigner {
        public dsa256() {
            super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsa384
    extends DSASigner {
        public dsa384() {
            super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsa512
    extends DSASigner {
        public dsa512() {
            super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsaRMD160
    extends DSASigner {
        public dsaRMD160() {
            super(new RIPEMD160Digest(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsaSha3_224
    extends DSASigner {
        public dsaSha3_224() {
            super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsaSha3_256
    extends DSASigner {
        public dsaSha3_256() {
            super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsaSha3_384
    extends DSASigner {
        public dsaSha3_384() {
            super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class dsaSha3_512
    extends DSASigner {
        public dsaSha3_512() {
            super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class noneDSA
    extends DSASigner {
        public noneDSA() {
            super(new NullDigest(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    public static class stdDSA
    extends DSASigner {
        public stdDSA() {
            super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class PSSSignatureSpi
extends SignatureSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private AlgorithmParameters engineParams;
    private PSSParameterSpec paramSpec;
    private PSSParameterSpec originalSpec;
    private AsymmetricBlockCipher signer;
    private Digest contentDigest;
    private Digest mgfDigest;
    private int saltLength;
    private byte trailer;
    private boolean isRaw;
    private RSAKeyParameters key;
    private SecureRandom random;
    private PSSSigner pss;
    private boolean isInitState = true;

    private byte getTrailer(int trailerField) {
        if (trailerField == 1) {
            return -68;
        }
        throw new IllegalArgumentException("unknown trailer field");
    }

    private void setupContentDigest() {
        this.contentDigest = DigestFactory.getDigest(this.paramSpec.getDigestAlgorithm());
        if (this.isRaw) {
            this.contentDigest = new NullPssDigest(this.contentDigest);
        }
    }

    protected PSSSignatureSpi(AsymmetricBlockCipher signer, PSSParameterSpec paramSpecArg) {
        this(signer, paramSpecArg, false);
    }

    protected PSSSignatureSpi(AsymmetricBlockCipher signer, PSSParameterSpec baseParamSpec, boolean isRaw) {
        this.signer = signer;
        this.originalSpec = baseParamSpec;
        this.paramSpec = baseParamSpec == null ? PSSParameterSpec.DEFAULT : baseParamSpec;
        this.mgfDigest = "MGF1".equals(this.paramSpec.getMGFAlgorithm()) ? DigestFactory.getDigest(this.paramSpec.getDigestAlgorithm()) : DigestFactory.getDigest(this.paramSpec.getMGFAlgorithm());
        this.saltLength = this.paramSpec.getSaltLength();
        this.trailer = this.getTrailer(this.paramSpec.getTrailerField());
        this.isRaw = isRaw;
        this.setupContentDigest();
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
        }
        this.key = RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey);
        this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
        this.pss.init(false, this.key);
        this.isInitState = true;
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.random = random;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance");
        }
        this.key = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey);
        this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
        if (this.random != null) {
            this.pss.init(true, new ParametersWithRandom(this.key, this.random));
        } else {
            this.pss.init(true, this.key);
        }
        this.isInitState = true;
    }

    @Override
    protected void engineUpdate(byte b) throws SignatureException {
        this.pss.update(b);
        this.isInitState = false;
    }

    @Override
    protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        this.pss.update(b, off, len);
        this.isInitState = false;
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        this.isInitState = true;
        try {
            return this.pss.generateSignature();
        }
        catch (CryptoException e) {
            throw new SignatureException(e.getMessage());
        }
    }

    @Override
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        this.isInitState = true;
        return this.pss.verifySignature(sigBytes);
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params == null) {
            if (this.originalSpec != null) {
                params = this.originalSpec;
            } else {
                return;
            }
        }
        if (!this.isInitState) {
            throw new ProviderException("cannot call setParameter in the middle of update");
        }
        if (params instanceof PSSParameterSpec) {
            Digest mgfDigest;
            PSSParameterSpec newParamSpec = (PSSParameterSpec)params;
            if (this.originalSpec != null && !DigestFactory.isSameDigest(this.originalSpec.getDigestAlgorithm(), newParamSpec.getDigestAlgorithm())) {
                throw new InvalidAlgorithmParameterException("parameter must be using " + this.originalSpec.getDigestAlgorithm());
            }
            if (newParamSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1") || newParamSpec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
                if (!(newParamSpec.getMGFParameters() instanceof MGF1ParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unknown MGF parameters");
                }
                MGF1ParameterSpec mgfParams = (MGF1ParameterSpec)newParamSpec.getMGFParameters();
                if (!DigestFactory.isSameDigest(mgfParams.getDigestAlgorithm(), newParamSpec.getDigestAlgorithm())) {
                    throw new InvalidAlgorithmParameterException("digest algorithm for MGF should be the same as for PSS parameters.");
                }
                mgfDigest = DigestFactory.getDigest(mgfParams.getDigestAlgorithm());
            } else if (newParamSpec.getMGFAlgorithm().equals("SHAKE128") || newParamSpec.getMGFAlgorithm().equals("SHAKE256")) {
                mgfDigest = DigestFactory.getDigest(newParamSpec.getMGFAlgorithm());
            } else {
                throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
            }
            if (mgfDigest == null) {
                throw new InvalidAlgorithmParameterException("no match on MGF algorithm: " + newParamSpec.getMGFAlgorithm());
            }
            this.engineParams = null;
            this.paramSpec = newParamSpec;
            this.mgfDigest = mgfDigest;
            this.saltLength = this.paramSpec.getSaltLength();
            this.trailer = this.getTrailer(this.paramSpec.getTrailerField());
            this.setupContentDigest();
            if (this.key != null) {
                this.pss = new PSSSigner(this.signer, this.contentDigest, mgfDigest, this.saltLength, this.trailer);
                if (this.key.isPrivate()) {
                    this.pss.init(true, this.key);
                } else {
                    this.pss.init(false, this.key);
                }
            }
        } else {
            throw new InvalidAlgorithmParameterException("Only PSSParameterSpec supported");
        }
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("PSS");
                this.engineParams.init(this.paramSpec);
            }
            catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    @Override
    protected void engineSetParameter(String param, Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String param) {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    private static class NullPssDigest
    implements Digest {
        private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        private Digest baseDigest;
        private boolean oddTime = true;

        public NullPssDigest(Digest mgfDigest) {
            this.baseDigest = mgfDigest;
        }

        @Override
        public String getAlgorithmName() {
            return "NULL";
        }

        @Override
        public int getDigestSize() {
            return this.baseDigest.getDigestSize();
        }

        @Override
        public void update(byte in) {
            this.bOut.write(in);
        }

        @Override
        public void update(byte[] in, int inOff, int len) {
            this.bOut.write(in, inOff, len);
        }

        @Override
        public int doFinal(byte[] out, int outOff) {
            byte[] res = this.bOut.toByteArray();
            if (this.oddTime) {
                System.arraycopy(res, 0, out, outOff, res.length);
            } else {
                this.baseDigest.update(res, 0, res.length);
                this.baseDigest.doFinal(out, outOff);
            }
            this.reset();
            this.oddTime = !this.oddTime;
            return res.length;
        }

        @Override
        public void reset() {
            this.bOut.reset();
            this.baseDigest.reset();
        }

        public int getByteLength() {
            return 0;
        }
    }

    public static class PSSwithRSA
    extends PSSSignatureSpi {
        public PSSwithRSA() {
            super(new RSABlindedEngine(), null);
        }
    }

    public static class SHA1withRSA
    extends PSSSignatureSpi {
        public SHA1withRSA() {
            super(new RSABlindedEngine(), PSSParameterSpec.DEFAULT);
        }
    }

    public static class SHA1withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA1withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA1", "SHAKE128", null, 20, 1));
        }
    }

    public static class SHA1withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA1withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA1", "SHAKE256", null, 20, 1));
        }
    }

    public static class SHA224withRSA
    extends PSSSignatureSpi {
        public SHA224withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), 28, 1));
        }
    }

    public static class SHA224withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA224withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "SHAKE128", null, 28, 1));
        }
    }

    public static class SHA224withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA224withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "SHAKE256", null, 28, 1));
        }
    }

    public static class SHA256withRSA
    extends PSSSignatureSpi {
        public SHA256withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
        }
    }

    public static class SHA256withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA256withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "SHAKE128", null, 32, 1));
        }
    }

    public static class SHA256withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA256withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "SHAKE256", null, 32, 1));
        }
    }

    public static class SHA384withRSA
    extends PSSSignatureSpi {
        public SHA384withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
        }
    }

    public static class SHA384withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA384withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "SHAKE128", null, 48, 1));
        }
    }

    public static class SHA384withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA384withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "SHAKE256", null, 48, 1));
        }
    }

    public static class SHA3_224withRSA
    extends PSSSignatureSpi {
        public SHA3_224withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), 28, 1));
        }
    }

    public static class SHA3_224withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA3_224withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "SHAKE128", null, 28, 1));
        }
    }

    public static class SHA3_224withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA3_224withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "SHAKE256", null, 28, 1));
        }
    }

    public static class SHA3_256withRSA
    extends PSSSignatureSpi {
        public SHA3_256withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), 32, 1));
        }
    }

    public static class SHA3_256withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA3_256withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "SHAKE128", null, 32, 1));
        }
    }

    public static class SHA3_256withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA3_256withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "SHAKE256", null, 32, 1));
        }
    }

    public static class SHA3_384withRSA
    extends PSSSignatureSpi {
        public SHA3_384withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), 48, 1));
        }
    }

    public static class SHA3_384withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA3_384withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "SHAKE128", null, 48, 1));
        }
    }

    public static class SHA3_384withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA3_384withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "SHAKE256", null, 48, 1));
        }
    }

    public static class SHA3_512withRSA
    extends PSSSignatureSpi {
        public SHA3_512withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), 64, 1));
        }
    }

    public static class SHA3_512withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA3_512withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "SHAKE128", null, 64, 1));
        }
    }

    public static class SHA3_512withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA3_512withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "SHAKE256", null, 64, 1));
        }
    }

    public static class SHA512_224withRSA
    extends PSSSignatureSpi {
        public SHA512_224withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "MGF1", new MGF1ParameterSpec("SHA-512(224)"), 28, 1));
        }
    }

    public static class SHA512_224withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA512_224withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "SHAKE128", null, 28, 1));
        }
    }

    public static class SHA512_224withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA512_224withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "SHAKE256", null, 28, 1));
        }
    }

    public static class SHA512_256withRSA
    extends PSSSignatureSpi {
        public SHA512_256withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "MGF1", new MGF1ParameterSpec("SHA-512(256)"), 32, 1));
        }
    }

    public static class SHA512_256withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA512_256withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "SHAKE128", null, 32, 1));
        }
    }

    public static class SHA512_256withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA512_256withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "SHAKE256", null, 32, 1));
        }
    }

    public static class SHA512withRSA
    extends PSSSignatureSpi {
        public SHA512withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1));
        }
    }

    public static class SHA512withRSAandSHAKE128
    extends PSSSignatureSpi {
        public SHA512withRSAandSHAKE128() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "SHAKE128", null, 64, 1));
        }
    }

    public static class SHA512withRSAandSHAKE256
    extends PSSSignatureSpi {
        public SHA512withRSAandSHAKE256() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "SHAKE256", null, 64, 1));
        }
    }

    public static class SHAKE128WithRSAPSS
    extends PSSSignatureSpi {
        public SHAKE128WithRSAPSS() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHAKE128", "SHAKE128", null, 32, 1));
        }
    }

    public static class SHAKE256WithRSAPSS
    extends PSSSignatureSpi {
        public SHAKE256WithRSAPSS() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHAKE256", "SHAKE256", null, 64, 1));
        }
    }

    public static class nonePSS
    extends PSSSignatureSpi {
        public nonePSS() {
            super(new RSABlindedEngine(), null, true);
        }
    }
}


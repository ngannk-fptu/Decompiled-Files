/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class GMSignatureSpi
extends SignatureSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private AlgorithmParameters engineParams;
    private SM2ParameterSpec paramSpec;
    private final SM2Signer signer;

    GMSignatureSpi(SM2Signer signer) {
        this.signer = signer;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param = ECUtils.generatePublicKeyParameter(publicKey);
        if (this.paramSpec != null) {
            param = new ParametersWithID(param, this.paramSpec.getID());
        }
        this.signer.init(false, param);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = ECUtil.generatePrivateKeyParameter(privateKey);
        if (this.appRandom != null) {
            param = new ParametersWithRandom(param, this.appRandom);
        }
        if (this.paramSpec != null) {
            this.signer.init(true, new ParametersWithID(param, this.paramSpec.getID()));
        } else {
            this.signer.init(true, param);
        }
    }

    @Override
    protected void engineUpdate(byte b) throws SignatureException {
        this.signer.update(b);
    }

    @Override
    protected void engineUpdate(byte[] bytes, int off, int length) throws SignatureException {
        this.signer.update(bytes, off, length);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        }
        catch (CryptoException e) {
            throw new SignatureException("unable to create signature: " + e.getMessage());
        }
    }

    @Override
    protected boolean engineVerify(byte[] bytes) throws SignatureException {
        return this.signer.verifySignature(bytes);
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (!(params instanceof SM2ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("only SM2ParameterSpec supported");
        }
        this.paramSpec = (SM2ParameterSpec)params;
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

    public static class sha256WithSM2
    extends GMSignatureSpi {
        public sha256WithSM2() {
            super(new SM2Signer(SHA256Digest.newInstance()));
        }
    }

    public static class sm3WithSM2
    extends GMSignatureSpi {
        public sm3WithSM2() {
            super(new SM2Signer());
        }
    }
}


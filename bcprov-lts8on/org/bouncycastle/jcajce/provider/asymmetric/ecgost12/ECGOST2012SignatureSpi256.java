/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ECGOST2012SignatureSpi256
extends SignatureSpi
implements PKCSObjectIdentifiers,
X509ObjectIdentifiers {
    private Digest digest;
    private DSAExt signer;
    private int size = 64;
    private int halfSize = this.size / 2;

    public ECGOST2012SignatureSpi256() {
        this.digest = new GOST3411_2012_256Digest();
        this.signer = new ECGOST3410Signer();
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        ECKeyParameters param;
        if (publicKey instanceof ECPublicKey) {
            param = (ECKeyParameters)ECGOST2012SignatureSpi256.generatePublicKeyParameter(publicKey);
        } else {
            try {
                byte[] bytes = publicKey.getEncoded();
                publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(bytes));
                param = (ECKeyParameters)ECUtil.generatePublicKeyParameter(publicKey);
            }
            catch (Exception e) {
                throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-256 signer");
            }
        }
        if (param.getParameters().getN().bitLength() > 256) {
            throw new InvalidKeyException("key out of range for ECGOST-2012-256");
        }
        this.digest.reset();
        this.signer.init(false, param);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof ECKey)) {
            throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-256 signer");
        }
        ECKeyParameters param = (ECKeyParameters)ECUtil.generatePrivateKeyParameter(privateKey);
        if (param.getParameters().getN().bitLength() > 256) {
            throw new InvalidKeyException("key out of range for ECGOST-2012-256");
        }
        this.digest.reset();
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(param, this.appRandom));
        } else {
            this.signer.init(true, param);
        }
    }

    @Override
    protected void engineUpdate(byte b) throws SignatureException {
        this.digest.update(b);
    }

    @Override
    protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        this.digest.update(b, off, len);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] sigBytes = new byte[this.size];
            BigInteger[] sig = this.signer.generateSignature(hash);
            byte[] r = sig[0].toByteArray();
            byte[] s = sig[1].toByteArray();
            if (s[0] != 0) {
                System.arraycopy(s, 0, sigBytes, this.halfSize - s.length, s.length);
            } else {
                System.arraycopy(s, 1, sigBytes, this.halfSize - (s.length - 1), s.length - 1);
            }
            if (r[0] != 0) {
                System.arraycopy(r, 0, sigBytes, this.size - r.length, r.length);
            } else {
                System.arraycopy(r, 1, sigBytes, this.size - (r.length - 1), r.length - 1);
            }
            return sigBytes;
        }
        catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        BigInteger[] sig;
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] r = new byte[this.halfSize];
            byte[] s = new byte[this.halfSize];
            System.arraycopy(sigBytes, 0, s, 0, this.halfSize);
            System.arraycopy(sigBytes, this.halfSize, r, 0, this.halfSize);
            sig = new BigInteger[]{new BigInteger(1, r), new BigInteger(1, s)};
        }
        catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }
        return this.signer.verifySignature(hash, sig[0], sig[1]);
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override
    protected void engineSetParameter(String param, Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String param) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        return key instanceof BCECGOST3410_2012PublicKey ? ((BCECGOST3410_2012PublicKey)key).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(key);
    }
}


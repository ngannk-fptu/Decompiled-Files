/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSTU4145Signer;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECKey;

public class SignatureSpi
extends java.security.SignatureSpi
implements PKCSObjectIdentifiers,
X509ObjectIdentifiers {
    private Digest digest;
    private DSAExt signer = new DSTU4145Signer();

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter param;
        if (publicKey instanceof BCDSTU4145PublicKey) {
            param = ((BCDSTU4145PublicKey)publicKey).engineGetKeyParameters();
            this.digest = new GOST3411Digest(this.expandSbox(((BCDSTU4145PublicKey)publicKey).getSbox()));
        } else {
            param = ECUtil.generatePublicKeyParameter(publicKey);
            this.digest = new GOST3411Digest(this.expandSbox(DSTU4145Params.getDefaultDKE()));
        }
        this.signer.init(false, param);
    }

    byte[] expandSbox(byte[] compressed) {
        byte[] expanded = new byte[128];
        for (int i = 0; i < compressed.length; ++i) {
            expanded[i * 2] = (byte)(compressed[i] >> 4 & 0xF);
            expanded[i * 2 + 1] = (byte)(compressed[i] & 0xF);
        }
        return expanded;
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        AsymmetricKeyParameter param = null;
        if (privateKey instanceof BCDSTU4145PrivateKey) {
            param = ECUtil.generatePrivateKeyParameter(privateKey);
            this.digest = new GOST3411Digest(this.expandSbox(DSTU4145Params.getDefaultDKE()));
        } else if (privateKey instanceof ECKey) {
            param = ECUtil.generatePrivateKeyParameter(privateKey);
            this.digest = new GOST3411Digest(this.expandSbox(DSTU4145Params.getDefaultDKE()));
        }
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
            BigInteger[] sig = this.signer.generateSignature(hash);
            byte[] r = sig[0].toByteArray();
            byte[] s = sig[1].toByteArray();
            byte[] sigBytes = new byte[r.length > s.length ? r.length * 2 : s.length * 2];
            System.arraycopy(s, 0, sigBytes, sigBytes.length / 2 - s.length, s.length);
            System.arraycopy(r, 0, sigBytes, sigBytes.length - r.length, r.length);
            return new DEROctetString(sigBytes).getEncoded();
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
            byte[] bytes = ((ASN1OctetString)ASN1OctetString.fromByteArray(sigBytes)).getOctets();
            byte[] r = new byte[bytes.length / 2];
            byte[] s = new byte[bytes.length / 2];
            System.arraycopy(bytes, 0, s, 0, bytes.length / 2);
            System.arraycopy(bytes, bytes.length / 2, r, 0, bytes.length / 2);
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
}


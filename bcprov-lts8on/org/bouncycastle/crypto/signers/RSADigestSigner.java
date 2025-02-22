/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RSADigestSigner
implements Signer {
    private final AsymmetricBlockCipher rsaEngine = new PKCS1Encoding(new RSABlindedEngine());
    private final AlgorithmIdentifier algId;
    private final Digest digest;
    private boolean forSigning;
    private static final Hashtable oidMap = new Hashtable();

    public RSADigestSigner(Digest digest) {
        this(digest, (ASN1ObjectIdentifier)oidMap.get(digest.getAlgorithmName()));
    }

    public RSADigestSigner(Digest digest, ASN1ObjectIdentifier digestOid) {
        this.digest = digest;
        this.algId = digestOid != null ? new AlgorithmIdentifier(digestOid, DERNull.INSTANCE) : null;
    }

    @Override
    public void init(boolean forSigning, CipherParameters parameters) {
        this.forSigning = forSigning;
        AsymmetricKeyParameter k = parameters instanceof ParametersWithRandom ? (AsymmetricKeyParameter)((ParametersWithRandom)parameters).getParameters() : (AsymmetricKeyParameter)parameters;
        if (forSigning && !k.isPrivate()) {
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!forSigning && k.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        this.reset();
        this.rsaEngine.init(forSigning, parameters);
    }

    @Override
    public void update(byte input) {
        this.digest.update(input);
    }

    @Override
    public void update(byte[] input, int inOff, int length) {
        this.digest.update(input, inOff, length);
    }

    @Override
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        if (!this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for signature generation.");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] data = this.derEncode(hash);
            return this.rsaEngine.processBlock(data, 0, data.length);
        }
        catch (IOException e) {
            throw new CryptoException("unable to encode signature: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        byte[] expected;
        byte[] sig;
        if (this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for verification");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            sig = this.rsaEngine.processBlock(signature, 0, signature.length);
            expected = this.derEncode(hash);
        }
        catch (Exception e) {
            return false;
        }
        if (sig.length == expected.length) {
            return Arrays.constantTimeAreEqual(sig, expected);
        }
        if (sig.length == expected.length - 2) {
            int i;
            int sigOffset = sig.length - hash.length - 2;
            int expectedOffset = expected.length - hash.length - 2;
            expected[1] = (byte)(expected[1] - 2);
            expected[3] = (byte)(expected[3] - 2);
            int nonEqual = 0;
            for (i = 0; i < hash.length; ++i) {
                nonEqual |= sig[sigOffset + i] ^ expected[expectedOffset + i];
            }
            for (i = 0; i < sigOffset; ++i) {
                nonEqual |= sig[i] ^ expected[i];
            }
            return nonEqual == 0;
        }
        Arrays.constantTimeAreEqual(expected, expected);
        return false;
    }

    @Override
    public void reset() {
        this.digest.reset();
    }

    private byte[] derEncode(byte[] hash) throws IOException {
        if (this.algId == null) {
            try {
                DigestInfo.getInstance(hash);
                return hash;
            }
            catch (IllegalArgumentException e) {
                throw new IOException("malformed DigestInfo for NONEwithRSA hash: " + e.getMessage());
            }
        }
        DigestInfo dInfo = new DigestInfo(this.algId, hash);
        return dInfo.getEncoded("DER");
    }

    static {
        oidMap.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        oidMap.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        oidMap.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        oidMap.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
        oidMap.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        oidMap.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        oidMap.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        oidMap.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        oidMap.put("SHA-512/224", NISTObjectIdentifiers.id_sha512_224);
        oidMap.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
        oidMap.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
        oidMap.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        oidMap.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
        oidMap.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
        oidMap.put("MD2", PKCSObjectIdentifiers.md2);
        oidMap.put("MD4", PKCSObjectIdentifiers.md4);
        oidMap.put("MD5", PKCSObjectIdentifiers.md5);
    }
}


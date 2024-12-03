/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.IESParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;

public class EthereumIESEngine {
    BasicAgreement agree;
    DerivationFunction kdf;
    Mac mac;
    BufferedBlockCipher cipher;
    byte[] macBuf;
    byte[] commonMac;
    boolean forEncryption;
    CipherParameters privParam;
    CipherParameters pubParam;
    IESParameters param;
    byte[] V;
    private EphemeralKeyPairGenerator keyPairGenerator;
    private KeyParser keyParser;
    private byte[] IV;

    public EthereumIESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, byte[] commonMac) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.commonMac = commonMac;
        this.cipher = null;
    }

    public EthereumIESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, byte[] commonMac, BufferedBlockCipher cipher) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.commonMac = commonMac;
        this.cipher = cipher;
    }

    public void init(boolean forEncryption, CipherParameters privParam, CipherParameters pubParam, CipherParameters params) {
        this.forEncryption = forEncryption;
        this.privParam = privParam;
        this.pubParam = pubParam;
        this.V = new byte[0];
        this.extractParams(params);
    }

    public void init(AsymmetricKeyParameter publicKey, CipherParameters params, EphemeralKeyPairGenerator ephemeralKeyPairGenerator) {
        this.forEncryption = true;
        this.pubParam = publicKey;
        this.keyPairGenerator = ephemeralKeyPairGenerator;
        this.extractParams(params);
    }

    public void init(AsymmetricKeyParameter privateKey, CipherParameters params, KeyParser publicKeyParser) {
        this.forEncryption = false;
        this.privParam = privateKey;
        this.keyParser = publicKeyParser;
        this.extractParams(params);
    }

    private void extractParams(CipherParameters params) {
        if (params instanceof ParametersWithIV) {
            this.IV = ((ParametersWithIV)params).getIV();
            this.param = (IESParameters)((ParametersWithIV)params).getParameters();
        } else {
            this.IV = null;
            this.param = (IESParameters)params;
        }
    }

    public BufferedBlockCipher getCipher() {
        return this.cipher;
    }

    public Mac getMac() {
        return this.mac;
    }

    private byte[] encryptBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int len;
        byte[] C = null;
        byte[] K = null;
        byte[] K1 = null;
        byte[] K2 = null;
        if (this.cipher == null) {
            K1 = new byte[inLen];
            K2 = new byte[this.param.getMacKeySize() / 8];
            K = new byte[K1.length + K2.length];
            this.kdf.generateBytes(K, 0, K.length);
            if (this.V.length != 0) {
                System.arraycopy(K, 0, K2, 0, K2.length);
                System.arraycopy(K, K2.length, K1, 0, K1.length);
            } else {
                System.arraycopy(K, 0, K1, 0, K1.length);
                System.arraycopy(K, inLen, K2, 0, K2.length);
            }
            C = new byte[inLen];
            for (int i = 0; i != inLen; ++i) {
                C[i] = (byte)(in[inOff + i] ^ K1[i]);
            }
            len = inLen;
        } else {
            K1 = new byte[((IESWithCipherParameters)this.param).getCipherKeySize() / 8];
            K2 = new byte[this.param.getMacKeySize() / 8];
            K = new byte[K1.length + K2.length];
            this.kdf.generateBytes(K, 0, K.length);
            System.arraycopy(K, 0, K1, 0, K1.length);
            System.arraycopy(K, K1.length, K2, 0, K2.length);
            if (this.IV != null) {
                this.cipher.init(true, new ParametersWithIV(new KeyParameter(K1), this.IV));
            } else {
                this.cipher.init(true, new KeyParameter(K1));
            }
            C = new byte[this.cipher.getOutputSize(inLen)];
            len = this.cipher.processBytes(in, inOff, inLen, C, 0);
            len += this.cipher.doFinal(C, len);
        }
        byte[] P2 = this.param.getEncodingV();
        byte[] L2 = null;
        if (this.V.length != 0) {
            L2 = this.getLengthTag(P2);
        }
        byte[] T = new byte[this.mac.getMacSize()];
        SavableDigest hash = SHA256Digest.newInstance();
        byte[] K2hash = new byte[hash.getDigestSize()];
        hash.reset();
        hash.update(K2, 0, K2.length);
        hash.doFinal(K2hash, 0);
        this.mac.init(new KeyParameter(K2hash));
        this.mac.update(this.IV, 0, this.IV.length);
        this.mac.update(C, 0, C.length);
        if (P2 != null) {
            this.mac.update(P2, 0, P2.length);
        }
        if (this.V.length != 0) {
            this.mac.update(L2, 0, L2.length);
        }
        this.mac.update(this.commonMac, 0, this.commonMac.length);
        this.mac.doFinal(T, 0);
        byte[] Output = new byte[this.V.length + len + T.length];
        System.arraycopy(this.V, 0, Output, 0, this.V.length);
        System.arraycopy(C, 0, Output, this.V.length, len);
        System.arraycopy(T, 0, Output, this.V.length + len, T.length);
        return Output;
    }

    private byte[] decryptBlock(byte[] in_enc, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] M;
        byte[] K2;
        int len = 0;
        if (inLen < this.V.length + this.mac.getMacSize()) {
            throw new InvalidCipherTextException("length of input must be greater than the MAC and V combined");
        }
        if (this.cipher == null) {
            byte[] K1 = new byte[inLen - this.V.length - this.mac.getMacSize()];
            K2 = new byte[this.param.getMacKeySize() / 8];
            byte[] K = new byte[K1.length + K2.length];
            this.kdf.generateBytes(K, 0, K.length);
            if (this.V.length != 0) {
                System.arraycopy(K, 0, K2, 0, K2.length);
                System.arraycopy(K, K2.length, K1, 0, K1.length);
            } else {
                System.arraycopy(K, 0, K1, 0, K1.length);
                System.arraycopy(K, K1.length, K2, 0, K2.length);
            }
            M = new byte[K1.length];
            for (int i = 0; i != K1.length; ++i) {
                M[i] = (byte)(in_enc[inOff + this.V.length + i] ^ K1[i]);
            }
        } else {
            byte[] K1 = new byte[((IESWithCipherParameters)this.param).getCipherKeySize() / 8];
            K2 = new byte[this.param.getMacKeySize() / 8];
            byte[] K = new byte[K1.length + K2.length];
            this.kdf.generateBytes(K, 0, K.length);
            System.arraycopy(K, 0, K1, 0, K1.length);
            System.arraycopy(K, K1.length, K2, 0, K2.length);
            CipherParameters cp = new KeyParameter(K1);
            if (this.IV != null) {
                cp = new ParametersWithIV(cp, this.IV);
            }
            this.cipher.init(false, cp);
            M = new byte[this.cipher.getOutputSize(inLen - this.V.length - this.mac.getMacSize())];
            len = this.cipher.processBytes(in_enc, inOff + this.V.length, inLen - this.V.length - this.mac.getMacSize(), M, 0);
        }
        byte[] P2 = this.param.getEncodingV();
        byte[] L2 = null;
        if (this.V.length != 0) {
            L2 = this.getLengthTag(P2);
        }
        int end = inOff + inLen;
        byte[] T1 = Arrays.copyOfRange(in_enc, end - this.mac.getMacSize(), end);
        byte[] T2 = new byte[T1.length];
        SavableDigest hash = SHA256Digest.newInstance();
        byte[] K2hash = new byte[hash.getDigestSize()];
        hash.reset();
        hash.update(K2, 0, K2.length);
        hash.doFinal(K2hash, 0);
        this.mac.init(new KeyParameter(K2hash));
        this.mac.update(this.IV, 0, this.IV.length);
        this.mac.update(in_enc, inOff + this.V.length, inLen - this.V.length - T2.length);
        if (P2 != null) {
            this.mac.update(P2, 0, P2.length);
        }
        if (this.V.length != 0) {
            this.mac.update(L2, 0, L2.length);
        }
        this.mac.update(this.commonMac, 0, this.commonMac.length);
        this.mac.doFinal(T2, 0);
        if (!Arrays.constantTimeAreEqual(T1, T2)) {
            throw new InvalidCipherTextException("invalid MAC");
        }
        if (this.cipher == null) {
            return M;
        }
        len += this.cipher.doFinal(M, len);
        return Arrays.copyOfRange(M, 0, len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            if (this.keyPairGenerator != null) {
                EphemeralKeyPair ephKeyPair = this.keyPairGenerator.generate();
                this.privParam = ephKeyPair.getKeyPair().getPrivate();
                this.V = ephKeyPair.getEncodedPublicKey();
            }
        } else if (this.keyParser != null) {
            ByteArrayInputStream bIn = new ByteArrayInputStream(in, inOff, inLen);
            try {
                this.pubParam = this.keyParser.readKey(bIn);
            }
            catch (IOException e) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + e.getMessage(), e);
            }
            catch (IllegalArgumentException e) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + e.getMessage(), e);
            }
            int encLength = inLen - bIn.available();
            this.V = Arrays.copyOfRange(in, inOff, inOff + encLength);
        }
        this.agree.init(this.privParam);
        BigInteger z = this.agree.calculateAgreement(this.pubParam);
        byte[] Z = BigIntegers.asUnsignedByteArray(this.agree.getFieldSize(), z);
        if (this.V.length != 0) {
            byte[] VZ = Arrays.concatenate(this.V, Z);
            Arrays.fill(Z, (byte)0);
            Z = VZ;
        }
        try {
            KDFParameters kdfParam = new KDFParameters(Z, this.param.getDerivationV());
            this.kdf.init(kdfParam);
            byte[] byArray = this.forEncryption ? this.encryptBlock(in, inOff, inLen) : this.decryptBlock(in, inOff, inLen);
            return byArray;
        }
        finally {
            Arrays.fill(Z, (byte)0);
        }
    }

    protected byte[] getLengthTag(byte[] p2) {
        byte[] L2 = new byte[8];
        if (p2 != null) {
            Pack.longToBigEndian((long)p2.length * 8L, L2, 0);
        }
        return L2;
    }

    public static class HandshakeKDFFunction
    implements DigestDerivationFunction {
        private int counterStart;
        private Digest digest;
        private byte[] shared;
        private byte[] iv;

        public HandshakeKDFFunction(int counterStart, Digest digest) {
            this.counterStart = counterStart;
            this.digest = digest;
        }

        @Override
        public void init(DerivationParameters param) {
            if (param instanceof KDFParameters) {
                KDFParameters p = (KDFParameters)param;
                this.shared = p.getSharedSecret();
                this.iv = p.getIV();
            } else if (param instanceof ISO18033KDFParameters) {
                ISO18033KDFParameters p = (ISO18033KDFParameters)param;
                this.shared = p.getSeed();
                this.iv = null;
            } else {
                throw new IllegalArgumentException("KDF parameters required for generator");
            }
        }

        @Override
        public Digest getDigest() {
            return this.digest;
        }

        @Override
        public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
            if (out.length - len < outOff) {
                throw new OutputLengthException("output buffer too small");
            }
            long oBytes = len;
            int outLen = this.digest.getDigestSize();
            if (oBytes > 0x1FFFFFFFFL) {
                throw new IllegalArgumentException("output length too large");
            }
            int cThreshold = (int)((oBytes + (long)outLen - 1L) / (long)outLen);
            byte[] dig = new byte[this.digest.getDigestSize()];
            byte[] C = new byte[4];
            Pack.intToBigEndian(this.counterStart, C, 0);
            int counterBase = this.counterStart & 0xFFFFFF00;
            for (int i = 0; i < cThreshold; ++i) {
                this.digest.update(C, 0, C.length);
                this.digest.update(this.shared, 0, this.shared.length);
                if (this.iv != null) {
                    this.digest.update(this.iv, 0, this.iv.length);
                }
                this.digest.doFinal(dig, 0);
                if (len > outLen) {
                    System.arraycopy(dig, 0, out, outOff, outLen);
                    outOff += outLen;
                    len -= outLen;
                } else {
                    System.arraycopy(dig, 0, out, outOff, len);
                }
                C[3] = (byte)(C[3] + 1);
                if (C[3] != 0) continue;
                Pack.intToBigEndian(counterBase += 256, C, 0);
            }
            this.digest.reset();
            return (int)oBytes;
        }
    }
}


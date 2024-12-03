/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.ChaCha20Poly1305;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class AEAD {
    private final short aeadId;
    private final byte[] key;
    private final byte[] baseNonce;
    private long seq = 0L;
    private AEADCipher cipher;

    public AEAD(short aeadId, byte[] key, byte[] baseNonce) {
        this.key = key;
        this.baseNonce = baseNonce;
        this.aeadId = aeadId;
        this.seq = 0L;
        switch (aeadId) {
            case 1: 
            case 2: {
                this.cipher = GCMBlockCipher.newInstance(AESEngine.newInstance());
                break;
            }
            case 3: {
                this.cipher = new ChaCha20Poly1305();
                break;
            }
        }
    }

    public byte[] seal(byte[] aad, byte[] pt) throws InvalidCipherTextException {
        ParametersWithIV params;
        switch (this.aeadId) {
            case 1: 
            case 2: 
            case 3: {
                params = new ParametersWithIV(new KeyParameter(this.key), this.ComputeNonce());
                break;
            }
            default: {
                throw new IllegalStateException("Export only mode, cannot be used to seal/open");
            }
        }
        this.cipher.init(true, params);
        this.cipher.processAADBytes(aad, 0, aad.length);
        byte[] ct = new byte[this.cipher.getOutputSize(pt.length)];
        int len = this.cipher.processBytes(pt, 0, pt.length, ct, 0);
        this.cipher.doFinal(ct, len);
        ++this.seq;
        return ct;
    }

    public byte[] open(byte[] aad, byte[] ct) throws InvalidCipherTextException {
        ParametersWithIV params;
        switch (this.aeadId) {
            case 1: 
            case 2: 
            case 3: {
                params = new ParametersWithIV(new KeyParameter(this.key), this.ComputeNonce());
                break;
            }
            default: {
                throw new IllegalStateException("Export only mode, cannot be used to seal/open");
            }
        }
        this.cipher.init(false, params);
        this.cipher.processAADBytes(aad, 0, aad.length);
        byte[] pt = new byte[this.cipher.getOutputSize(ct.length)];
        int len = this.cipher.processBytes(ct, 0, ct.length, pt, 0);
        len += this.cipher.doFinal(pt, len);
        ++this.seq;
        return pt;
    }

    private byte[] ComputeNonce() {
        byte[] seq_bytes = Pack.longToBigEndian(this.seq);
        int Nn = this.baseNonce.length;
        byte[] nonce = Arrays.clone(this.baseNonce);
        for (int i = 0; i < 8; ++i) {
            int n = Nn - 8 + i;
            nonce[n] = (byte)(nonce[n] ^ seq_bytes[i]);
        }
        return nonce;
    }
}


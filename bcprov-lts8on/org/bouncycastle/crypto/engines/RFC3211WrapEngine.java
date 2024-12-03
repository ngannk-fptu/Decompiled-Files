/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RFC3211WrapEngine
implements Wrapper {
    private CBCBlockCipher engine;
    private ParametersWithIV param;
    private boolean forWrapping;
    private SecureRandom rand;

    public RFC3211WrapEngine(BlockCipher engine) {
        this.engine = new CBCBlockCipher(engine);
    }

    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom)param;
            this.rand = p.getRandom();
            if (!(p.getParameters() instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("RFC3211Wrap requires an IV");
            }
            this.param = (ParametersWithIV)p.getParameters();
        } else {
            if (forWrapping) {
                this.rand = CryptoServicesRegistrar.getSecureRandom();
            }
            if (!(param instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("RFC3211Wrap requires an IV");
            }
            this.param = (ParametersWithIV)param;
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
    }

    @Override
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        int i;
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (inLen > 255 || inLen < 0) {
            throw new IllegalArgumentException("input must be from 0 to 255 bytes");
        }
        this.engine.init(true, this.param);
        int blockSize = this.engine.getBlockSize();
        byte[] cekBlock = inLen + 4 < blockSize * 2 ? new byte[blockSize * 2] : new byte[(inLen + 4) % blockSize == 0 ? inLen + 4 : ((inLen + 4) / blockSize + 1) * blockSize];
        cekBlock[0] = (byte)inLen;
        System.arraycopy(in, inOff, cekBlock, 4, inLen);
        byte[] pad = new byte[cekBlock.length - (inLen + 4)];
        this.rand.nextBytes(pad);
        System.arraycopy(pad, 0, cekBlock, inLen + 4, pad.length);
        cekBlock[1] = ~cekBlock[4];
        cekBlock[2] = ~cekBlock[5];
        cekBlock[3] = ~cekBlock[6];
        for (i = 0; i < cekBlock.length; i += blockSize) {
            this.engine.processBlock(cekBlock, i, cekBlock, i);
        }
        for (i = 0; i < cekBlock.length; i += blockSize) {
            this.engine.processBlock(cekBlock, i, cekBlock, i);
        }
        return cekBlock;
    }

    @Override
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int i;
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int blockSize = this.engine.getBlockSize();
        if (inLen < 2 * blockSize) {
            throw new InvalidCipherTextException("input too short");
        }
        byte[] cekBlock = new byte[inLen];
        byte[] iv = new byte[blockSize];
        System.arraycopy(in, inOff, cekBlock, 0, inLen);
        System.arraycopy(in, inOff, iv, 0, iv.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), iv));
        for (i = blockSize; i < cekBlock.length; i += blockSize) {
            this.engine.processBlock(cekBlock, i, cekBlock, i);
        }
        System.arraycopy(cekBlock, cekBlock.length - iv.length, iv, 0, iv.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), iv));
        this.engine.processBlock(cekBlock, 0, cekBlock, 0);
        this.engine.init(false, this.param);
        for (i = 0; i < cekBlock.length; i += blockSize) {
            this.engine.processBlock(cekBlock, i, cekBlock, i);
        }
        boolean invalidLength = (cekBlock[0] & 0xFF) > cekBlock.length - 4;
        byte[] key = invalidLength ? new byte[cekBlock.length - 4] : new byte[cekBlock[0] & 0xFF];
        System.arraycopy(cekBlock, 4, key, 0, key.length);
        int nonEqual = 0;
        for (int i2 = 0; i2 != 3; ++i2) {
            byte check = ~cekBlock[1 + i2];
            nonEqual |= check ^ cekBlock[4 + i2];
        }
        Arrays.clear(cekBlock);
        if (nonEqual != 0 | invalidLength) {
            throw new InvalidCipherTextException("wrapped key corrupted");
        }
        return key;
    }
}


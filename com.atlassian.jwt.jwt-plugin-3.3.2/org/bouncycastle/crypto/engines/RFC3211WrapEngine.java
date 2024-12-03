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

    public RFC3211WrapEngine(BlockCipher blockCipher) {
        this.engine = new CBCBlockCipher(blockCipher);
    }

    public void init(boolean bl, CipherParameters cipherParameters) {
        this.forWrapping = bl;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.rand = parametersWithRandom.getRandom();
            if (!(parametersWithRandom.getParameters() instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("RFC3211Wrap requires an IV");
            }
            this.param = (ParametersWithIV)parametersWithRandom.getParameters();
        } else {
            if (bl) {
                this.rand = CryptoServicesRegistrar.getSecureRandom();
            }
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("RFC3211Wrap requires an IV");
            }
            this.param = (ParametersWithIV)cipherParameters;
        }
    }

    public String getAlgorithmName() {
        return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
    }

    public byte[] wrap(byte[] byArray, int n, int n2) {
        int n3;
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (n2 > 255 || n2 < 0) {
            throw new IllegalArgumentException("input must be from 0 to 255 bytes");
        }
        this.engine.init(true, this.param);
        int n4 = this.engine.getBlockSize();
        byte[] byArray2 = n2 + 4 < n4 * 2 ? new byte[n4 * 2] : new byte[(n2 + 4) % n4 == 0 ? n2 + 4 : ((n2 + 4) / n4 + 1) * n4];
        byArray2[0] = (byte)n2;
        System.arraycopy(byArray, n, byArray2, 4, n2);
        byte[] byArray3 = new byte[byArray2.length - (n2 + 4)];
        this.rand.nextBytes(byArray3);
        System.arraycopy(byArray3, 0, byArray2, n2 + 4, byArray3.length);
        byArray2[1] = ~byArray2[4];
        byArray2[2] = ~byArray2[5];
        byArray2[3] = ~byArray2[6];
        for (n3 = 0; n3 < byArray2.length; n3 += n4) {
            this.engine.processBlock(byArray2, n3, byArray2, n3);
        }
        for (n3 = 0; n3 < byArray2.length; n3 += n4) {
            this.engine.processBlock(byArray2, n3, byArray2, n3);
        }
        return byArray2;
    }

    public byte[] unwrap(byte[] byArray, int n, int n2) throws InvalidCipherTextException {
        int n3;
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int n4 = this.engine.getBlockSize();
        if (n2 < 2 * n4) {
            throw new InvalidCipherTextException("input too short");
        }
        byte[] byArray2 = new byte[n2];
        byte[] byArray3 = new byte[n4];
        System.arraycopy(byArray, n, byArray2, 0, n2);
        System.arraycopy(byArray, n, byArray3, 0, byArray3.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), byArray3));
        for (n3 = n4; n3 < byArray2.length; n3 += n4) {
            this.engine.processBlock(byArray2, n3, byArray2, n3);
        }
        System.arraycopy(byArray2, byArray2.length - byArray3.length, byArray3, 0, byArray3.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), byArray3));
        this.engine.processBlock(byArray2, 0, byArray2, 0);
        this.engine.init(false, this.param);
        for (n3 = 0; n3 < byArray2.length; n3 += n4) {
            this.engine.processBlock(byArray2, n3, byArray2, n3);
        }
        n3 = (byArray2[0] & 0xFF) > byArray2.length - 4 ? 1 : 0;
        byte[] byArray4 = n3 != 0 ? new byte[byArray2.length - 4] : new byte[byArray2[0] & 0xFF];
        System.arraycopy(byArray2, 4, byArray4, 0, byArray4.length);
        int n5 = 0;
        for (int i = 0; i != 3; ++i) {
            byte by = ~byArray2[1 + i];
            n5 |= by ^ byArray2[4 + i];
        }
        Arrays.clear(byArray2);
        if (((n5 != 0 ? 1 : 0) | n3) != 0) {
            throw new InvalidCipherTextException("wrapped key corrupted");
        }
        return byArray4;
    }
}


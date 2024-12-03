/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.DESBase;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DESedeEngine
extends DESBase
implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey1 = null;
    private int[] workingKey2 = null;
    private int[] workingKey3 = null;
    private boolean forEncryption;

    public DESedeEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity()));
    }

    @Override
    public void init(boolean encrypting, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DESede init - " + params.getClass().getName());
        }
        byte[] keyMaster = ((KeyParameter)params).getKey();
        if (keyMaster.length != 24 && keyMaster.length != 16) {
            throw new IllegalArgumentException("key size must be 16 or 24 bytes.");
        }
        this.forEncryption = encrypting;
        byte[] key1 = new byte[8];
        System.arraycopy(keyMaster, 0, key1, 0, key1.length);
        this.workingKey1 = this.generateWorkingKey(encrypting, key1);
        byte[] key2 = new byte[8];
        System.arraycopy(keyMaster, 8, key2, 0, key2.length);
        this.workingKey2 = this.generateWorkingKey(!encrypting, key2);
        if (keyMaster.length == 24) {
            byte[] key3 = new byte[8];
            System.arraycopy(keyMaster, 16, key3, 0, key3.length);
            this.workingKey3 = this.generateWorkingKey(encrypting, key3);
        } else {
            this.workingKey3 = this.workingKey1;
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), params, Utils.getPurpose(this.forEncryption)));
    }

    @Override
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override
    public int getBlockSize() {
        return 8;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey1 == null) {
            throw new IllegalStateException("DESede engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        byte[] temp = new byte[8];
        if (this.forEncryption) {
            this.desFunc(this.workingKey1, in, inOff, temp, 0);
            this.desFunc(this.workingKey2, temp, 0, temp, 0);
            this.desFunc(this.workingKey3, temp, 0, out, outOff);
        } else {
            this.desFunc(this.workingKey3, in, inOff, temp, 0);
            this.desFunc(this.workingKey2, temp, 0, temp, 0);
            this.desFunc(this.workingKey1, temp, 0, out, outOff);
        }
        return 8;
    }

    @Override
    public void reset() {
    }

    private int bitsOfSecurity() {
        if (this.workingKey1 != null && this.workingKey1 == this.workingKey3) {
            return 80;
        }
        return 112;
    }
}


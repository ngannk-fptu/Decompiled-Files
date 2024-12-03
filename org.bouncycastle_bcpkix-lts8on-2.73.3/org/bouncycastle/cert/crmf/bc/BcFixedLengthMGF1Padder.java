/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.DerivationParameters
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.generators.MGF1BytesGenerator
 *  org.bouncycastle.crypto.params.MGFParameters
 */
package org.bouncycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.params.MGFParameters;

public class BcFixedLengthMGF1Padder
implements EncryptedValuePadder {
    private int length;
    private SecureRandom random;
    private Digest dig = new SHA1Digest();

    public BcFixedLengthMGF1Padder(int length) {
        this(length, null);
    }

    public BcFixedLengthMGF1Padder(int length, SecureRandom random) {
        this.length = length;
        this.random = random;
    }

    @Override
    public byte[] getPaddedData(byte[] data) {
        int i;
        byte[] bytes = new byte[this.length];
        byte[] seed = new byte[this.dig.getDigestSize()];
        byte[] mask = new byte[this.length - this.dig.getDigestSize()];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(seed);
        MGF1BytesGenerator maskGen = new MGF1BytesGenerator(this.dig);
        maskGen.init((DerivationParameters)new MGFParameters(seed));
        maskGen.generateBytes(mask, 0, mask.length);
        System.arraycopy(seed, 0, bytes, 0, seed.length);
        System.arraycopy(data, 0, bytes, seed.length, data.length);
        for (i = seed.length + data.length + 1; i != bytes.length; ++i) {
            bytes[i] = (byte)(1 + this.random.nextInt(255));
        }
        for (i = 0; i != mask.length; ++i) {
            int n = i + seed.length;
            bytes[n] = (byte)(bytes[n] ^ mask[i]);
        }
        return bytes;
    }

    @Override
    public byte[] getUnpaddedData(byte[] paddedData) {
        byte[] seed = new byte[this.dig.getDigestSize()];
        byte[] mask = new byte[this.length - this.dig.getDigestSize()];
        System.arraycopy(paddedData, 0, seed, 0, seed.length);
        MGF1BytesGenerator maskGen = new MGF1BytesGenerator(this.dig);
        maskGen.init((DerivationParameters)new MGFParameters(seed));
        maskGen.generateBytes(mask, 0, mask.length);
        for (int i = 0; i != mask.length; ++i) {
            int n = i + seed.length;
            paddedData[n] = (byte)(paddedData[n] ^ mask[i]);
        }
        int end = 0;
        for (int i = paddedData.length - 1; i != seed.length; --i) {
            if (paddedData[i] != 0) continue;
            end = i;
            break;
        }
        if (end == 0) {
            throw new IllegalStateException("bad padding in encoding");
        }
        byte[] data = new byte[end - seed.length];
        System.arraycopy(paddedData, seed.length, data, 0, data.length);
        return data;
    }
}


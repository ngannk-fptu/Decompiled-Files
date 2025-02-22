/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.util.ArrayList;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class DSTU7624WrapEngine
implements Wrapper {
    private static final int BYTES_IN_INTEGER = 4;
    private boolean forWrapping;
    private DSTU7624Engine engine;
    private byte[] B;
    private byte[] intArray;
    private byte[] checkSumArray;
    private byte[] zeroArray;
    private ArrayList<byte[]> Btemp;

    public DSTU7624WrapEngine(int blockBitLength) {
        this.engine = new DSTU7624Engine(blockBitLength);
        this.B = new byte[this.engine.getBlockSize() / 2];
        this.checkSumArray = new byte[this.engine.getBlockSize()];
        this.zeroArray = new byte[this.engine.getBlockSize()];
        this.Btemp = new ArrayList();
        this.intArray = new byte[4];
    }

    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            param = ((ParametersWithRandom)param).getParameters();
        }
        this.forWrapping = forWrapping;
        if (!(param instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameters passed to DSTU7624WrapEngine");
        }
        this.engine.init(forWrapping, param);
    }

    @Override
    public String getAlgorithmName() {
        return "DSTU7624WrapEngine";
    }

    @Override
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (inLen % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("wrap data must be a multiple of " + this.engine.getBlockSize() + " bytes");
        }
        if (inOff + inLen > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        int n = 2 * (1 + inLen / this.engine.getBlockSize());
        int V = (n - 1) * 6;
        byte[] wrappedBuffer = new byte[inLen + this.engine.getBlockSize()];
        System.arraycopy(in, inOff, wrappedBuffer, 0, inLen);
        System.arraycopy(wrappedBuffer, 0, this.B, 0, this.engine.getBlockSize() / 2);
        this.Btemp.clear();
        int bHalfBlocksLen = wrappedBuffer.length - this.engine.getBlockSize() / 2;
        int bufOff = this.engine.getBlockSize() / 2;
        while (bHalfBlocksLen != 0) {
            byte[] temp = new byte[this.engine.getBlockSize() / 2];
            System.arraycopy(wrappedBuffer, bufOff, temp, 0, this.engine.getBlockSize() / 2);
            this.Btemp.add(temp);
            bHalfBlocksLen -= this.engine.getBlockSize() / 2;
            bufOff += this.engine.getBlockSize() / 2;
        }
        for (int j = 0; j < V; ++j) {
            System.arraycopy(this.B, 0, wrappedBuffer, 0, this.engine.getBlockSize() / 2);
            System.arraycopy(this.Btemp.get(0), 0, wrappedBuffer, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
            this.engine.processBlock(wrappedBuffer, 0, wrappedBuffer, 0);
            this.intToBytes(j + 1, this.intArray, 0);
            for (int byteNum = 0; byteNum < 4; ++byteNum) {
                int n2 = byteNum + this.engine.getBlockSize() / 2;
                wrappedBuffer[n2] = (byte)(wrappedBuffer[n2] ^ this.intArray[byteNum]);
            }
            System.arraycopy(wrappedBuffer, this.engine.getBlockSize() / 2, this.B, 0, this.engine.getBlockSize() / 2);
            for (int i = 2; i < n; ++i) {
                System.arraycopy(this.Btemp.get(i - 1), 0, this.Btemp.get(i - 2), 0, this.engine.getBlockSize() / 2);
            }
            System.arraycopy(wrappedBuffer, 0, this.Btemp.get(n - 2), 0, this.engine.getBlockSize() / 2);
        }
        System.arraycopy(this.B, 0, wrappedBuffer, 0, this.engine.getBlockSize() / 2);
        bufOff = this.engine.getBlockSize() / 2;
        for (int i = 0; i < n - 1; ++i) {
            System.arraycopy(this.Btemp.get(i), 0, wrappedBuffer, bufOff, this.engine.getBlockSize() / 2);
            bufOff += this.engine.getBlockSize() / 2;
        }
        return wrappedBuffer;
    }

    @Override
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        if (inLen % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("unwrap data must be a multiple of " + this.engine.getBlockSize() + " bytes");
        }
        int n = 2 * inLen / this.engine.getBlockSize();
        int V = (n - 1) * 6;
        byte[] buffer = new byte[inLen];
        System.arraycopy(in, inOff, buffer, 0, inLen);
        byte[] B = new byte[this.engine.getBlockSize() / 2];
        System.arraycopy(buffer, 0, B, 0, this.engine.getBlockSize() / 2);
        this.Btemp.clear();
        int bHalfBlocksLen = buffer.length - this.engine.getBlockSize() / 2;
        int bufOff = this.engine.getBlockSize() / 2;
        while (bHalfBlocksLen != 0) {
            byte[] temp = new byte[this.engine.getBlockSize() / 2];
            System.arraycopy(buffer, bufOff, temp, 0, this.engine.getBlockSize() / 2);
            this.Btemp.add(temp);
            bHalfBlocksLen -= this.engine.getBlockSize() / 2;
            bufOff += this.engine.getBlockSize() / 2;
        }
        for (int j = 0; j < V; ++j) {
            System.arraycopy(this.Btemp.get(n - 2), 0, buffer, 0, this.engine.getBlockSize() / 2);
            System.arraycopy(B, 0, buffer, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
            this.intToBytes(V - j, this.intArray, 0);
            for (int byteNum = 0; byteNum < 4; ++byteNum) {
                int n2 = byteNum + this.engine.getBlockSize() / 2;
                buffer[n2] = (byte)(buffer[n2] ^ this.intArray[byteNum]);
            }
            this.engine.processBlock(buffer, 0, buffer, 0);
            System.arraycopy(buffer, 0, B, 0, this.engine.getBlockSize() / 2);
            for (int i = 2; i < n; ++i) {
                System.arraycopy(this.Btemp.get(n - i - 1), 0, this.Btemp.get(n - i), 0, this.engine.getBlockSize() / 2);
            }
            System.arraycopy(buffer, this.engine.getBlockSize() / 2, this.Btemp.get(0), 0, this.engine.getBlockSize() / 2);
        }
        System.arraycopy(B, 0, buffer, 0, this.engine.getBlockSize() / 2);
        bufOff = this.engine.getBlockSize() / 2;
        for (int i = 0; i < n - 1; ++i) {
            System.arraycopy(this.Btemp.get(i), 0, buffer, bufOff, this.engine.getBlockSize() / 2);
            bufOff += this.engine.getBlockSize() / 2;
        }
        System.arraycopy(buffer, buffer.length - this.engine.getBlockSize(), this.checkSumArray, 0, this.engine.getBlockSize());
        byte[] wrappedBuffer = new byte[buffer.length - this.engine.getBlockSize()];
        if (!Arrays.areEqual(this.checkSumArray, this.zeroArray)) {
            throw new InvalidCipherTextException("checksum failed");
        }
        System.arraycopy(buffer, 0, wrappedBuffer, 0, buffer.length - this.engine.getBlockSize());
        return wrappedBuffer;
    }

    private void intToBytes(int number, byte[] outBytes, int outOff) {
        outBytes[outOff + 3] = (byte)(number >> 24);
        outBytes[outOff + 2] = (byte)(number >> 16);
        outBytes[outOff + 1] = (byte)(number >> 8);
        outBytes[outOff] = (byte)number;
    }
}


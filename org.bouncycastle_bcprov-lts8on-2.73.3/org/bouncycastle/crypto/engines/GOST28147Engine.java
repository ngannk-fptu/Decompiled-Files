/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class GOST28147Engine
implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey = null;
    private boolean forEncryption;
    private byte[] S = Sbox_Default;
    private static byte[] Sbox_Default = new byte[]{4, 10, 9, 2, 13, 8, 0, 14, 6, 11, 1, 12, 7, 15, 5, 3, 14, 11, 4, 12, 6, 13, 15, 10, 2, 3, 8, 1, 0, 7, 5, 9, 5, 8, 1, 13, 10, 3, 4, 2, 14, 15, 12, 7, 6, 0, 9, 11, 7, 13, 10, 1, 0, 8, 9, 15, 14, 4, 6, 12, 11, 2, 5, 3, 6, 12, 7, 1, 5, 15, 13, 8, 4, 10, 9, 14, 0, 3, 11, 2, 4, 11, 10, 0, 7, 2, 1, 13, 3, 6, 8, 5, 9, 12, 15, 14, 13, 11, 4, 1, 3, 15, 5, 9, 0, 10, 14, 7, 6, 8, 2, 12, 1, 15, 13, 0, 5, 7, 10, 4, 9, 2, 3, 14, 6, 11, 8, 12};
    private static byte[] ESbox_Test = new byte[]{4, 2, 15, 5, 9, 1, 0, 8, 14, 3, 11, 12, 13, 7, 10, 6, 12, 9, 15, 14, 8, 1, 3, 10, 2, 7, 4, 13, 6, 0, 11, 5, 13, 8, 14, 12, 7, 3, 9, 10, 1, 5, 2, 4, 6, 15, 0, 11, 14, 9, 11, 2, 5, 15, 7, 1, 0, 13, 12, 6, 10, 4, 3, 8, 3, 14, 5, 9, 6, 8, 0, 13, 10, 11, 7, 12, 2, 1, 15, 4, 8, 15, 6, 11, 1, 9, 12, 5, 13, 3, 7, 10, 0, 14, 2, 4, 9, 11, 12, 0, 3, 6, 7, 5, 4, 8, 14, 15, 1, 10, 2, 13, 12, 6, 5, 2, 11, 0, 9, 13, 3, 14, 7, 10, 15, 4, 1, 8};
    private static byte[] ESbox_A = new byte[]{9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 6, 2, 3, 9, 1, 7, 13, 4};
    private static byte[] ESbox_B = new byte[]{8, 4, 11, 1, 3, 5, 0, 9, 2, 14, 10, 12, 13, 6, 7, 15, 0, 1, 2, 10, 4, 13, 5, 12, 9, 7, 3, 15, 11, 8, 6, 14, 14, 12, 0, 10, 9, 2, 13, 11, 7, 5, 8, 15, 3, 6, 1, 4, 7, 5, 0, 13, 11, 6, 1, 2, 3, 10, 12, 15, 4, 14, 9, 8, 2, 7, 12, 15, 9, 5, 10, 11, 1, 4, 0, 13, 6, 8, 14, 3, 8, 3, 2, 6, 4, 13, 14, 11, 12, 1, 7, 15, 10, 0, 9, 5, 5, 2, 10, 11, 9, 1, 12, 3, 7, 4, 13, 0, 6, 15, 8, 14, 0, 4, 11, 14, 8, 3, 7, 1, 10, 2, 9, 6, 15, 13, 5, 12};
    private static byte[] ESbox_C = new byte[]{1, 11, 12, 2, 9, 13, 0, 15, 4, 5, 8, 14, 10, 7, 6, 3, 0, 1, 7, 13, 11, 4, 5, 2, 8, 14, 15, 12, 9, 10, 6, 3, 8, 2, 5, 0, 4, 9, 15, 10, 3, 7, 12, 13, 6, 14, 1, 11, 3, 6, 0, 1, 5, 13, 10, 8, 11, 2, 9, 7, 14, 15, 12, 4, 8, 13, 11, 0, 4, 5, 1, 2, 9, 3, 12, 14, 6, 15, 10, 7, 12, 9, 11, 1, 8, 14, 2, 4, 7, 3, 6, 5, 10, 0, 15, 13, 10, 9, 6, 8, 13, 14, 2, 0, 15, 3, 5, 11, 4, 1, 12, 7, 7, 4, 0, 5, 10, 2, 15, 14, 12, 6, 1, 11, 13, 9, 3, 8};
    private static byte[] ESbox_D = new byte[]{15, 12, 2, 10, 6, 4, 5, 0, 7, 9, 14, 13, 1, 11, 8, 3, 11, 6, 3, 4, 12, 15, 14, 2, 7, 13, 8, 0, 5, 10, 9, 1, 1, 12, 11, 0, 15, 14, 6, 5, 10, 13, 4, 8, 9, 3, 7, 2, 1, 5, 14, 12, 10, 7, 0, 13, 6, 2, 11, 4, 9, 3, 15, 8, 0, 12, 8, 9, 13, 2, 10, 11, 7, 3, 6, 5, 4, 14, 15, 1, 8, 0, 15, 3, 2, 5, 14, 11, 1, 10, 4, 7, 12, 9, 13, 6, 3, 0, 6, 15, 1, 14, 9, 2, 13, 8, 12, 4, 11, 10, 5, 7, 1, 10, 6, 8, 15, 11, 0, 4, 12, 3, 5, 9, 7, 13, 2, 14};
    private static byte[] Param_Z = new byte[]{12, 4, 6, 2, 10, 5, 11, 9, 14, 8, 13, 7, 0, 3, 15, 1, 6, 8, 2, 3, 9, 10, 5, 12, 1, 14, 4, 7, 11, 13, 0, 15, 11, 3, 5, 8, 2, 15, 10, 13, 14, 1, 7, 4, 12, 9, 6, 0, 12, 8, 2, 1, 13, 4, 15, 6, 7, 0, 10, 5, 3, 14, 9, 11, 7, 15, 5, 10, 8, 1, 6, 13, 0, 9, 3, 14, 11, 4, 2, 12, 5, 13, 15, 6, 9, 2, 12, 10, 11, 7, 8, 1, 4, 3, 14, 0, 8, 14, 2, 5, 6, 9, 1, 12, 15, 4, 11, 0, 13, 10, 3, 7, 1, 7, 14, 13, 0, 5, 8, 3, 4, 15, 10, 6, 9, 12, 11, 2};
    private static byte[] DSbox_Test = new byte[]{4, 10, 9, 2, 13, 8, 0, 14, 6, 11, 1, 12, 7, 15, 5, 3, 14, 11, 4, 12, 6, 13, 15, 10, 2, 3, 8, 1, 0, 7, 5, 9, 5, 8, 1, 13, 10, 3, 4, 2, 14, 15, 12, 7, 6, 0, 9, 11, 7, 13, 10, 1, 0, 8, 9, 15, 14, 4, 6, 12, 11, 2, 5, 3, 6, 12, 7, 1, 5, 15, 13, 8, 4, 10, 9, 14, 0, 3, 11, 2, 4, 11, 10, 0, 7, 2, 1, 13, 3, 6, 8, 5, 9, 12, 15, 14, 13, 11, 4, 1, 3, 15, 5, 9, 0, 10, 14, 7, 6, 8, 2, 12, 1, 15, 13, 0, 5, 7, 10, 4, 9, 2, 3, 14, 6, 11, 8, 12};
    private static byte[] DSbox_A = new byte[]{10, 4, 5, 6, 8, 1, 3, 7, 13, 12, 14, 0, 9, 2, 11, 15, 5, 15, 4, 0, 2, 13, 11, 9, 1, 7, 6, 3, 12, 14, 10, 8, 7, 15, 12, 14, 9, 4, 1, 0, 3, 11, 5, 2, 6, 10, 8, 13, 4, 10, 7, 12, 0, 15, 2, 8, 14, 1, 6, 5, 13, 11, 9, 3, 7, 6, 4, 11, 9, 12, 2, 10, 1, 8, 0, 14, 15, 13, 3, 5, 7, 6, 2, 4, 13, 9, 15, 0, 10, 1, 5, 11, 8, 14, 12, 3, 13, 14, 4, 1, 7, 0, 5, 10, 3, 12, 8, 15, 6, 2, 9, 11, 1, 3, 10, 9, 5, 11, 4, 15, 8, 6, 7, 14, 13, 0, 2, 12};
    private static Hashtable sBoxes = new Hashtable();

    private static void addSBox(String sBoxName, byte[] sBox) {
        sBoxes.put(Strings.toUpperCase(sBoxName), sBox);
    }

    public GOST28147Engine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 178));
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof ParametersWithSBox) {
            ParametersWithSBox param = (ParametersWithSBox)params;
            byte[] sBox = param.getSBox();
            if (sBox.length != Sbox_Default.length) {
                throw new IllegalArgumentException("invalid S-box passed to GOST28147 init");
            }
            this.S = Arrays.clone(sBox);
            if (param.getParameters() != null) {
                this.workingKey = this.generateWorkingKey(forEncryption, ((KeyParameter)param.getParameters()).getKey());
            }
        } else if (params instanceof KeyParameter) {
            this.workingKey = this.generateWorkingKey(forEncryption, ((KeyParameter)params).getKey());
        } else if (params != null) {
            throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + params.getClass().getName());
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 178, params, Utils.getPurpose(forEncryption)));
    }

    @Override
    public String getAlgorithmName() {
        return "GOST28147";
    }

    @Override
    public int getBlockSize() {
        return 8;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey == null) {
            throw new IllegalStateException("GOST28147 engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.GOST28147Func(this.workingKey, in, inOff, out, outOff);
        return 8;
    }

    @Override
    public void reset() {
    }

    private int[] generateWorkingKey(boolean forEncryption, byte[] userKey) {
        this.forEncryption = forEncryption;
        if (userKey.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        int[] key = new int[8];
        for (int i = 0; i != 8; ++i) {
            key[i] = this.bytesToint(userKey, i * 4);
        }
        return key;
    }

    private int GOST28147_mainStep(int n1, int key) {
        int cm = key + n1;
        int om = this.S[0 + (cm >> 0 & 0xF)] << 0;
        om += this.S[16 + (cm >> 4 & 0xF)] << 4;
        om += this.S[32 + (cm >> 8 & 0xF)] << 8;
        om += this.S[48 + (cm >> 12 & 0xF)] << 12;
        om += this.S[64 + (cm >> 16 & 0xF)] << 16;
        om += this.S[80 + (cm >> 20 & 0xF)] << 20;
        om += this.S[96 + (cm >> 24 & 0xF)] << 24;
        return (om += this.S[112 + (cm >> 28 & 0xF)] << 28) << 11 | om >>> 21;
    }

    private void GOST28147Func(int[] workingKey, byte[] in, int inOff, byte[] out, int outOff) {
        int N1 = this.bytesToint(in, inOff);
        int N2 = this.bytesToint(in, inOff + 4);
        if (this.forEncryption) {
            int tmp;
            for (int k = 0; k < 3; ++k) {
                for (int j = 0; j < 8; ++j) {
                    tmp = N1;
                    N1 = N2 ^ this.GOST28147_mainStep(N1, workingKey[j]);
                    N2 = tmp;
                }
            }
            for (int j = 7; j > 0; --j) {
                tmp = N1;
                N1 = N2 ^ this.GOST28147_mainStep(N1, workingKey[j]);
                N2 = tmp;
            }
        } else {
            int tmp;
            for (int j = 0; j < 8; ++j) {
                tmp = N1;
                N1 = N2 ^ this.GOST28147_mainStep(N1, workingKey[j]);
                N2 = tmp;
            }
            for (int k = 0; k < 3; ++k) {
                for (int j = 7; j >= 0 && (k != 2 || j != 0); --j) {
                    tmp = N1;
                    N1 = N2 ^ this.GOST28147_mainStep(N1, workingKey[j]);
                    N2 = tmp;
                }
            }
        }
        this.intTobytes(N1, out, outOff);
        this.intTobytes(N2 ^= this.GOST28147_mainStep(N1, workingKey[0]), out, outOff + 4);
    }

    private int bytesToint(byte[] in, int inOff) {
        return (in[inOff + 3] << 24 & 0xFF000000) + (in[inOff + 2] << 16 & 0xFF0000) + (in[inOff + 1] << 8 & 0xFF00) + (in[inOff] & 0xFF);
    }

    private void intTobytes(int num, byte[] out, int outOff) {
        out[outOff + 3] = (byte)(num >>> 24);
        out[outOff + 2] = (byte)(num >>> 16);
        out[outOff + 1] = (byte)(num >>> 8);
        out[outOff] = (byte)num;
    }

    public static byte[] getSBox(String sBoxName) {
        byte[] sBox = (byte[])sBoxes.get(Strings.toUpperCase(sBoxName));
        if (sBox == null) {
            throw new IllegalArgumentException("Unknown S-Box - possible types: \"Default\", \"E-Test\", \"E-A\", \"E-B\", \"E-C\", \"E-D\", \"Param-Z\", \"D-Test\", \"D-A\".");
        }
        return Arrays.clone(sBox);
    }

    public static String getSBoxName(byte[] sBox) {
        Enumeration en = sBoxes.keys();
        while (en.hasMoreElements()) {
            String name = (String)en.nextElement();
            byte[] sb = (byte[])sBoxes.get(name);
            if (!Arrays.areEqual(sb, sBox)) continue;
            return name;
        }
        throw new IllegalArgumentException("SBOX provided did not map to a known one");
    }

    static {
        GOST28147Engine.addSBox("Default", Sbox_Default);
        GOST28147Engine.addSBox("E-TEST", ESbox_Test);
        GOST28147Engine.addSBox("E-A", ESbox_A);
        GOST28147Engine.addSBox("E-B", ESbox_B);
        GOST28147Engine.addSBox("E-C", ESbox_C);
        GOST28147Engine.addSBox("E-D", ESbox_D);
        GOST28147Engine.addSBox("Param-Z", Param_Z);
        GOST28147Engine.addSBox("D-TEST", DSbox_Test);
        GOST28147Engine.addSBox("D-A", DSbox_A);
    }
}


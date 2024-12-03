/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.ElGamalKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.BigIntegers;

public class ElGamalEngine
implements AsymmetricBlockCipher {
    private ElGamalKeyParameters key;
    private SecureRandom random;
    private boolean forEncryption;
    private int bitSize;
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        Object p;
        if (param instanceof ParametersWithRandom) {
            p = (ParametersWithRandom)param;
            this.key = (ElGamalKeyParameters)((ParametersWithRandom)p).getParameters();
            this.random = ((ParametersWithRandom)p).getRandom();
        } else {
            this.key = (ElGamalKeyParameters)param;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        this.forEncryption = forEncryption;
        p = this.key.getParameters().getP();
        this.bitSize = ((BigInteger)p).bitLength();
        if (forEncryption) {
            if (!(this.key instanceof ElGamalPublicKeyParameters)) {
                throw new IllegalArgumentException("ElGamalPublicKeyParameters are required for encryption.");
            }
        } else if (!(this.key instanceof ElGamalPrivateKeyParameters)) {
            throw new IllegalArgumentException("ElGamalPrivateKeyParameters are required for decryption.");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", ConstraintUtils.bitsOfSecurityFor(this.key.getParameters().getP()), this.key, Utils.getPurpose(forEncryption)));
    }

    @Override
    public int getInputBlockSize() {
        if (this.forEncryption) {
            return (this.bitSize - 1) / 8;
        }
        return 2 * ((this.bitSize + 7) / 8);
    }

    @Override
    public int getOutputBlockSize() {
        if (this.forEncryption) {
            return 2 * ((this.bitSize + 7) / 8);
        }
        return (this.bitSize - 1) / 8;
    }

    @Override
    public byte[] processBlock(byte[] in, int inOff, int inLen) {
        byte[] block;
        int maxLength;
        if (this.key == null) {
            throw new IllegalStateException("ElGamal engine not initialised");
        }
        int n = maxLength = this.forEncryption ? (this.bitSize - 1 + 7) / 8 : this.getInputBlockSize();
        if (inLen > maxLength) {
            throw new DataLengthException("input too large for ElGamal cipher.\n");
        }
        BigInteger p = this.key.getParameters().getP();
        if (this.key instanceof ElGamalPrivateKeyParameters) {
            byte[] in1 = new byte[inLen / 2];
            byte[] in2 = new byte[inLen / 2];
            System.arraycopy(in, inOff, in1, 0, in1.length);
            System.arraycopy(in, inOff + in1.length, in2, 0, in2.length);
            BigInteger gamma = new BigInteger(1, in1);
            BigInteger phi = new BigInteger(1, in2);
            ElGamalPrivateKeyParameters priv = (ElGamalPrivateKeyParameters)this.key;
            BigInteger m = gamma.modPow(p.subtract(ONE).subtract(priv.getX()), p).multiply(phi).mod(p);
            return BigIntegers.asUnsignedByteArray(m);
        }
        if (inOff != 0 || inLen != in.length) {
            block = new byte[inLen];
            System.arraycopy(in, inOff, block, 0, inLen);
        } else {
            block = in;
        }
        BigInteger input = new BigInteger(1, block);
        if (input.compareTo(p) >= 0) {
            throw new DataLengthException("input too large for ElGamal cipher.\n");
        }
        ElGamalPublicKeyParameters pub = (ElGamalPublicKeyParameters)this.key;
        int pBitLength = p.bitLength();
        BigInteger k = BigIntegers.createRandomBigInteger(pBitLength, this.random);
        while (k.equals(ZERO) || k.compareTo(p.subtract(TWO)) > 0) {
            k = BigIntegers.createRandomBigInteger(pBitLength, this.random);
        }
        BigInteger g = this.key.getParameters().getG();
        BigInteger gamma = g.modPow(k, p);
        BigInteger phi = input.multiply(pub.getY().modPow(k, p)).mod(p);
        byte[] out1 = gamma.toByteArray();
        byte[] out2 = phi.toByteArray();
        byte[] output = new byte[this.getOutputBlockSize()];
        if (out1.length > output.length / 2) {
            System.arraycopy(out1, 1, output, output.length / 2 - (out1.length - 1), out1.length - 1);
        } else {
            System.arraycopy(out1, 0, output, output.length / 2 - out1.length, out1.length);
        }
        if (out2.length > output.length / 2) {
            System.arraycopy(out2, 1, output, output.length - (out2.length - 1), out2.length - 1);
        } else {
            System.arraycopy(out2, 0, output, output.length - out2.length, out2.length);
        }
        return output;
    }
}


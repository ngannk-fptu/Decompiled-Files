/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;

class RSACoreEngine {
    private RSAKeyParameters key;
    private boolean forEncryption;

    RSACoreEngine() {
    }

    public void init(boolean forEncryption, CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            this.key = (RSAKeyParameters)rParam.getParameters();
        } else {
            this.key = (RSAKeyParameters)param;
        }
        this.forEncryption = forEncryption;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", ConstraintUtils.bitsOfSecurityFor(this.key.getModulus()), this.key, this.getPurpose(this.key.isPrivate(), forEncryption)));
    }

    public int getInputBlockSize() {
        int bitSize = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (bitSize + 7) / 8 - 1;
        }
        return (bitSize + 7) / 8;
    }

    public int getOutputBlockSize() {
        int bitSize = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (bitSize + 7) / 8;
        }
        return (bitSize + 7) / 8 - 1;
    }

    public BigInteger convertInput(byte[] in, int inOff, int inLen) {
        byte[] block;
        if (inLen > this.getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (inLen == this.getInputBlockSize() + 1 && !this.forEncryption) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (inOff != 0 || inLen != in.length) {
            block = new byte[inLen];
            System.arraycopy(in, inOff, block, 0, inLen);
        } else {
            block = in;
        }
        BigInteger res = new BigInteger(1, block);
        if (res.compareTo(this.key.getModulus()) >= 0) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        return res;
    }

    public byte[] convertOutput(BigInteger result) {
        byte[] rv;
        byte[] output = result.toByteArray();
        if (this.forEncryption) {
            if (output[0] == 0 && output.length > this.getOutputBlockSize()) {
                byte[] tmp = new byte[output.length - 1];
                System.arraycopy(output, 1, tmp, 0, tmp.length);
                return tmp;
            }
            if (output.length < this.getOutputBlockSize()) {
                byte[] tmp = new byte[this.getOutputBlockSize()];
                System.arraycopy(output, 0, tmp, tmp.length - output.length, output.length);
                return tmp;
            }
            return output;
        }
        if (output[0] == 0) {
            rv = new byte[output.length - 1];
            System.arraycopy(output, 1, rv, 0, rv.length);
        } else {
            rv = new byte[output.length];
            System.arraycopy(output, 0, rv, 0, rv.length);
        }
        Arrays.fill(output, (byte)0);
        return rv;
    }

    public BigInteger processBlock(BigInteger input) {
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            RSAPrivateCrtKeyParameters crtKey = (RSAPrivateCrtKeyParameters)this.key;
            BigInteger p = crtKey.getP();
            BigInteger q = crtKey.getQ();
            BigInteger dP = crtKey.getDP();
            BigInteger dQ = crtKey.getDQ();
            BigInteger qInv = crtKey.getQInv();
            BigInteger mP = input.remainder(p).modPow(dP, p);
            BigInteger mQ = input.remainder(q).modPow(dQ, q);
            BigInteger h = mP.subtract(mQ);
            h = h.multiply(qInv);
            h = h.mod(p);
            BigInteger m = h.multiply(q);
            m = m.add(mQ);
            return m;
        }
        return input.modPow(this.key.getExponent(), this.key.getModulus());
    }

    private CryptoServicePurpose getPurpose(boolean isPrivate, boolean forEncryption) {
        boolean isVerifying;
        boolean isSigning = isPrivate && forEncryption;
        boolean isEncryption = !isPrivate && forEncryption;
        boolean bl = isVerifying = !isPrivate && !forEncryption;
        if (isSigning) {
            return CryptoServicePurpose.SIGNING;
        }
        if (isEncryption) {
            return CryptoServicePurpose.ENCRYPTION;
        }
        if (isVerifying) {
            return CryptoServicePurpose.VERIFYING;
        }
        return CryptoServicePurpose.DECRYPTION;
    }
}


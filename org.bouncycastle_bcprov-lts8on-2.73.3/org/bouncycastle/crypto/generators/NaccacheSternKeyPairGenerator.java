/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.NaccacheSternKeyGenerationParameters;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class NaccacheSternKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private static int[] smallPrimes = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557};
    private NaccacheSternKeyGenerationParameters param;
    private static final BigInteger ONE = BigInteger.valueOf(1L);

    @Override
    public void init(KeyGenerationParameters param) {
        this.param = (NaccacheSternKeyGenerationParameters)param;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("NaccacheStern KeyGen", ConstraintUtils.bitsOfSecurityForFF(param.getStrength()), param, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger g;
        BigInteger q;
        BigInteger q_;
        BigInteger p;
        BigInteger p_;
        int i;
        int strength = this.param.getStrength();
        SecureRandom rand = this.param.getRandom();
        int certainty = this.param.getCertainty();
        boolean debug = this.param.isDebug();
        if (debug) {
            System.out.println("Fetching first " + this.param.getCntSmallPrimes() + " primes.");
        }
        Vector smallPrimes = NaccacheSternKeyPairGenerator.findFirstPrimes(this.param.getCntSmallPrimes());
        smallPrimes = NaccacheSternKeyPairGenerator.permuteList(smallPrimes, rand);
        BigInteger u = ONE;
        BigInteger v = ONE;
        for (i = 0; i < smallPrimes.size() / 2; ++i) {
            u = u.multiply((BigInteger)smallPrimes.elementAt(i));
        }
        for (i = smallPrimes.size() / 2; i < smallPrimes.size(); ++i) {
            v = v.multiply((BigInteger)smallPrimes.elementAt(i));
        }
        BigInteger sigma = u.multiply(v);
        int remainingStrength = strength - sigma.bitLength() - 48;
        BigInteger a = NaccacheSternKeyPairGenerator.generatePrime(remainingStrength / 2 + 1, certainty, rand);
        BigInteger b = NaccacheSternKeyPairGenerator.generatePrime(remainingStrength / 2 + 1, certainty, rand);
        long tries = 0L;
        if (debug) {
            System.out.println("generating p and q");
        }
        BigInteger _2au = a.multiply(u).shiftLeft(1);
        BigInteger _2bv = b.multiply(v).shiftLeft(1);
        while (true) {
            ++tries;
            p_ = NaccacheSternKeyPairGenerator.generatePrime(24, certainty, rand);
            p = p_.multiply(_2au).add(ONE);
            if (!p.isProbablePrime(certainty)) continue;
            while (p_.equals(q_ = NaccacheSternKeyPairGenerator.generatePrime(24, certainty, rand)) || !(q = q_.multiply(_2bv).add(ONE)).isProbablePrime(certainty)) {
            }
            if (!sigma.gcd(p_.multiply(q_)).equals(ONE)) continue;
            if (p.multiply(q).bitLength() >= strength) break;
            if (!debug) continue;
            System.out.println("key size too small. Should be " + strength + " but is actually " + p.multiply(q).bitLength());
        }
        if (debug) {
            System.out.println("needed " + tries + " tries to generate p and q.");
        }
        BigInteger n = p.multiply(q);
        BigInteger phi_n = p.subtract(ONE).multiply(q.subtract(ONE));
        tries = 0L;
        if (debug) {
            System.out.println("generating g");
        }
        while (true) {
            Vector<BigInteger> gParts = new Vector<BigInteger>();
            for (int ind = 0; ind != smallPrimes.size(); ++ind) {
                BigInteger i2 = (BigInteger)smallPrimes.elementAt(ind);
                BigInteger e = phi_n.divide(i2);
                do {
                    ++tries;
                } while ((g = BigIntegers.createRandomPrime(strength, certainty, rand)).modPow(e, n).equals(ONE));
                gParts.addElement(g);
            }
            g = ONE;
            for (int i3 = 0; i3 < smallPrimes.size(); ++i3) {
                g = g.multiply(((BigInteger)gParts.elementAt(i3)).modPow(sigma.divide((BigInteger)smallPrimes.elementAt(i3)), n)).mod(n);
            }
            boolean divisible = false;
            for (int i4 = 0; i4 < smallPrimes.size(); ++i4) {
                if (!g.modPow(phi_n.divide((BigInteger)smallPrimes.elementAt(i4)), n).equals(ONE)) continue;
                if (debug) {
                    System.out.println("g has order phi(n)/" + smallPrimes.elementAt(i4) + "\n g: " + g);
                }
                divisible = true;
                break;
            }
            if (divisible) continue;
            if (g.modPow(phi_n.divide(BigInteger.valueOf(4L)), n).equals(ONE)) {
                if (!debug) continue;
                System.out.println("g has order phi(n)/4\n g:" + g);
                continue;
            }
            if (g.modPow(phi_n.divide(p_), n).equals(ONE)) {
                if (!debug) continue;
                System.out.println("g has order phi(n)/p'\n g: " + g);
                continue;
            }
            if (g.modPow(phi_n.divide(q_), n).equals(ONE)) {
                if (!debug) continue;
                System.out.println("g has order phi(n)/q'\n g: " + g);
                continue;
            }
            if (g.modPow(phi_n.divide(a), n).equals(ONE)) {
                if (!debug) continue;
                System.out.println("g has order phi(n)/a\n g: " + g);
                continue;
            }
            if (!g.modPow(phi_n.divide(b), n).equals(ONE)) break;
            if (!debug) continue;
            System.out.println("g has order phi(n)/b\n g: " + g);
        }
        if (debug) {
            System.out.println("needed " + tries + " tries to generate g");
            System.out.println();
            System.out.println("found new NaccacheStern cipher variables:");
            System.out.println("smallPrimes: " + smallPrimes);
            System.out.println("sigma:...... " + sigma + " (" + sigma.bitLength() + " bits)");
            System.out.println("a:.......... " + a);
            System.out.println("b:.......... " + b);
            System.out.println("p':......... " + p_);
            System.out.println("q':......... " + q_);
            System.out.println("p:.......... " + p);
            System.out.println("q:.......... " + q);
            System.out.println("n:.......... " + n);
            System.out.println("phi(n):..... " + phi_n);
            System.out.println("g:.......... " + g);
            System.out.println();
        }
        return new AsymmetricCipherKeyPair(new NaccacheSternKeyParameters(false, g, n, sigma.bitLength()), new NaccacheSternPrivateKeyParameters(g, n, sigma.bitLength(), smallPrimes, phi_n));
    }

    private static BigInteger generatePrime(int bitLength, int certainty, SecureRandom rand) {
        BigInteger p_ = BigIntegers.createRandomPrime(bitLength, certainty, rand);
        while (p_.bitLength() != bitLength) {
            p_ = BigIntegers.createRandomPrime(bitLength, certainty, rand);
        }
        return p_;
    }

    private static Vector permuteList(Vector arr, SecureRandom rand) {
        Vector retval = new Vector();
        Vector tmp = new Vector();
        for (int i = 0; i < arr.size(); ++i) {
            tmp.addElement(arr.elementAt(i));
        }
        retval.addElement(tmp.elementAt(0));
        tmp.removeElementAt(0);
        while (tmp.size() != 0) {
            retval.insertElementAt(tmp.elementAt(0), NaccacheSternKeyPairGenerator.getInt(rand, retval.size() + 1));
            tmp.removeElementAt(0);
        }
        return retval;
    }

    private static int getInt(SecureRandom rand, int n) {
        int val;
        int bits;
        if ((n & -n) == n) {
            return (int)((long)n * (long)(rand.nextInt() & Integer.MAX_VALUE) >> 31);
        }
        while ((bits = rand.nextInt() & Integer.MAX_VALUE) - (val = bits % n) + (n - 1) < 0) {
        }
        return val;
    }

    private static Vector findFirstPrimes(int count) {
        Vector<BigInteger> primes = new Vector<BigInteger>(count);
        for (int i = 0; i != count; ++i) {
            primes.addElement(BigInteger.valueOf(smallPrimes[i]));
        }
        return primes;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.RSAPublicKey
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.X509ObjectIdentifiers
 *  org.bouncycastle.asn1.x9.X962Parameters
 *  org.bouncycastle.asn1.x9.X9FieldID
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.crypto.CryptoServicesRegistrar
 *  org.bouncycastle.math.Primes
 *  org.bouncycastle.math.Primes$MROutput
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.pkix;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.Strings;

public class SubjectPublicKeyInfoChecker {
    private static final Cache validatedQs = new Cache();
    private static final Cache validatedMods = new Cache();
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);
    private static final BigInteger ONE = BigInteger.valueOf(1L);

    public static void checkInfo(SubjectPublicKeyInfo pubInfo) {
        ASN1ObjectIdentifier algorithm = pubInfo.getAlgorithm().getAlgorithm();
        if (X9ObjectIdentifiers.id_ecPublicKey.equals((ASN1Primitive)algorithm)) {
            X962Parameters params = X962Parameters.getInstance((Object)pubInfo.getAlgorithm().getParameters());
            if (params.isImplicitlyCA() || params.isNamedCurve()) {
                return;
            }
            ASN1Sequence ecParameters = ASN1Sequence.getInstance((Object)params.getParameters());
            X9FieldID fieldID = X9FieldID.getInstance((Object)ecParameters.getObjectAt(1));
            if (fieldID.getIdentifier().equals((ASN1Primitive)X9FieldID.prime_field)) {
                BigInteger q = ASN1Integer.getInstance((Object)fieldID.getParameters()).getValue();
                if (validatedQs.contains(q)) {
                    return;
                }
                int maxBitLength = Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
                int certainty = Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
                int qBitLength = q.bitLength();
                if (maxBitLength < qBitLength) {
                    throw new IllegalArgumentException("Fp q value out of range");
                }
                if (Primes.hasAnySmallFactors((BigInteger)q) || !Primes.isMRProbablePrime((BigInteger)q, (SecureRandom)CryptoServicesRegistrar.getSecureRandom(), (int)SubjectPublicKeyInfoChecker.getNumberOfIterations(qBitLength, certainty))) {
                    throw new IllegalArgumentException("Fp q value not prime");
                }
                validatedQs.add(q);
            }
        } else if (PKCSObjectIdentifiers.rsaEncryption.equals((ASN1Primitive)algorithm) || X509ObjectIdentifiers.id_ea_rsa.equals((ASN1Primitive)algorithm) || PKCSObjectIdentifiers.id_RSAES_OAEP.equals((ASN1Primitive)algorithm) || PKCSObjectIdentifiers.id_RSASSA_PSS.equals((ASN1Primitive)algorithm)) {
            RSAPublicKey params;
            try {
                params = RSAPublicKey.getInstance((Object)pubInfo.parsePublicKey());
            }
            catch (IOException e) {
                throw new IllegalArgumentException("unable to parse RSA key");
            }
            if ((params.getPublicExponent().intValue() & 1) == 0) {
                throw new IllegalArgumentException("RSA publicExponent is even");
            }
            if (!validatedMods.contains(params.getModulus())) {
                SubjectPublicKeyInfoChecker.validate(params.getModulus());
                validatedMods.add(params.getModulus());
            }
        }
    }

    private static void validate(BigInteger modulus) {
        int modBitLength;
        if ((modulus.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA modulus is even");
        }
        if (Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
            return;
        }
        int maxBitLength = Properties.asInteger("org.bouncycastle.rsa.max_size", 15360);
        if (maxBitLength < (modBitLength = modulus.bitLength())) {
            throw new IllegalArgumentException("modulus value out of range");
        }
        if (!modulus.gcd(SMALL_PRIMES_PRODUCT).equals(ONE)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
        }
        int bits = modulus.bitLength() / 2;
        int iterations = bits >= 1536 ? 3 : (bits >= 1024 ? 4 : (bits >= 512 ? 7 : 50));
        Primes.MROutput mr = Primes.enhancedMRProbablePrimeTest((BigInteger)modulus, (SecureRandom)CryptoServicesRegistrar.getSecureRandom(), (int)iterations);
        if (!mr.isProvablyComposite()) {
            throw new IllegalArgumentException("RSA modulus is not composite");
        }
    }

    private static int getNumberOfIterations(int bits, int certainty) {
        if (bits >= 1536) {
            return certainty <= 100 ? 3 : (certainty <= 128 ? 4 : 4 + (certainty - 128 + 1) / 2);
        }
        if (bits >= 1024) {
            return certainty <= 100 ? 4 : (certainty <= 112 ? 5 : 5 + (certainty - 112 + 1) / 2);
        }
        if (bits >= 512) {
            return certainty <= 80 ? 5 : (certainty <= 100 ? 7 : 7 + (certainty - 100 + 1) / 2);
        }
        return certainty <= 80 ? 40 : 40 + (certainty - 80 + 1) / 2;
    }

    public static boolean setThreadOverride(String propertyName, boolean enable) {
        return Properties.setThreadOverride(propertyName, enable);
    }

    public static boolean removeThreadOverride(String propertyName) {
        return Properties.removeThreadOverride(propertyName);
    }

    private static class Cache {
        private final Map<BigInteger, Boolean> values = new WeakHashMap<BigInteger, Boolean>();
        private final BigInteger[] preserve = new BigInteger[8];
        private int preserveCounter = 0;

        private Cache() {
        }

        public synchronized void add(BigInteger value) {
            this.values.put(value, Boolean.TRUE);
            this.preserve[this.preserveCounter] = value;
            this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
        }

        public synchronized boolean contains(BigInteger value) {
            return this.values.containsKey(value);
        }

        public synchronized int size() {
            return this.values.size();
        }

        public synchronized void clear() {
            this.values.clear();
            for (int i = 0; i != this.preserve.length; ++i) {
                this.preserve[i] = null;
            }
        }
    }

    private static class Properties {
        private static final ThreadLocal threadProperties = new ThreadLocal();

        private Properties() {
        }

        static boolean isOverrideSet(String propertyName) {
            try {
                return Properties.isSetTrue(Properties.getPropertyValue(propertyName));
            }
            catch (AccessControlException e) {
                return false;
            }
        }

        static boolean setThreadOverride(String propertyName, boolean enable) {
            boolean isSet = Properties.isOverrideSet(propertyName);
            HashMap<String, String> localProps = (HashMap<String, String>)threadProperties.get();
            if (localProps == null) {
                localProps = new HashMap<String, String>();
                threadProperties.set(localProps);
            }
            localProps.put(propertyName, enable ? "true" : "false");
            return isSet;
        }

        static boolean removeThreadOverride(String propertyName) {
            String p;
            Map localProps = (Map)threadProperties.get();
            if (localProps != null && (p = (String)localProps.remove(propertyName)) != null) {
                if (localProps.isEmpty()) {
                    threadProperties.remove();
                }
                return "true".equals(Strings.toLowerCase((String)p));
            }
            return false;
        }

        static int asInteger(String propertyName, int defaultValue) {
            String p = Properties.getPropertyValue(propertyName);
            if (p != null) {
                return Integer.parseInt(p);
            }
            return defaultValue;
        }

        static String getPropertyValue(final String propertyName) {
            String p;
            String val = (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return Security.getProperty(propertyName);
                }
            });
            if (val != null) {
                return val;
            }
            Map localProps = (Map)threadProperties.get();
            if (localProps != null && (p = (String)localProps.get(propertyName)) != null) {
                return p;
            }
            return (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return System.getProperty(propertyName);
                }
            });
        }

        private static boolean isSetTrue(String p) {
            if (p == null || p.length() != 4) {
                return false;
            }
            return !(p.charAt(0) != 't' && p.charAt(0) != 'T' || p.charAt(1) != 'r' && p.charAt(1) != 'R' || p.charAt(2) != 'u' && p.charAt(2) != 'U' || p.charAt(3) != 'e' && p.charAt(3) != 'E');
        }
    }
}


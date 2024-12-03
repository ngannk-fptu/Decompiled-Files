/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.drbg;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

public class DRBG {
    private static final String PREFIX = DRBG.class.getName();

    private static SecureRandom createBaseRandom(boolean isPredictionResistant) {
        if (Properties.getPropertyValue("org.bouncycastle.drbg.entropysource") != null) {
            EntropySourceProvider entropyProvider = DRBG.createEntropySource();
            EntropySource initSource = entropyProvider.get(128);
            byte[] personalisationString = isPredictionResistant ? DRBG.generateDefaultPersonalizationString(initSource.getEntropy()) : DRBG.generateNonceIVPersonalizationString(initSource.getEntropy());
            return new SP800SecureRandomBuilder(entropyProvider).setPersonalizationString(personalisationString).buildHash(new SHA512Digest(), initSource.getEntropy(), isPredictionResistant);
        }
        final EntropySourceProvider entropySourceProvider = CryptoServicesRegistrar.getDefaultEntropySourceProvider();
        EntropySource source = entropySourceProvider.get(256);
        byte[] personalisationString = isPredictionResistant ? DRBG.generateDefaultPersonalizationString(source.getEntropy()) : DRBG.generateNonceIVPersonalizationString(source.getEntropy());
        return new SP800SecureRandomBuilder(new EntropySourceProvider(){

            @Override
            public EntropySource get(int bitsRequired) {
                return entropySourceProvider.get(bitsRequired);
            }
        }).setPersonalizationString(personalisationString).buildHash(new SHA512Digest(), source.getEntropy(), isPredictionResistant);
    }

    private static EntropySourceProvider createEntropySource() {
        final String sourceClass = Properties.getPropertyValue("org.bouncycastle.drbg.entropysource");
        return AccessController.doPrivileged(new PrivilegedAction<EntropySourceProvider>(){

            @Override
            public EntropySourceProvider run() {
                try {
                    Class clazz = ClassUtil.loadClass(DRBG.class, sourceClass);
                    return (EntropySourceProvider)clazz.newInstance();
                }
                catch (Exception e) {
                    throw new IllegalStateException("entropy source " + sourceClass + " not created: " + e.getMessage(), e);
                }
            }
        });
    }

    private static byte[] generateDefaultPersonalizationString(byte[] seed) {
        return Arrays.concatenate(Strings.toByteArray("Default"), seed, Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }

    private static byte[] generateNonceIVPersonalizationString(byte[] seed) {
        return Arrays.concatenate(Strings.toByteArray("Nonce"), seed, Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }

    static /* synthetic */ SecureRandom access$100(boolean x0) {
        return DRBG.createBaseRandom(x0);
    }

    public static class Default
    extends SecureRandomSpi {
        private final SecureRandom random = DRBG.access$100(true);

        @Override
        protected void engineSetSeed(byte[] bytes) {
            this.random.setSeed(bytes);
        }

        @Override
        protected void engineNextBytes(byte[] bytes) {
            this.random.nextBytes(bytes);
        }

        @Override
        protected byte[] engineGenerateSeed(int numBytes) {
            return this.random.generateSeed(numBytes);
        }
    }

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider provider) {
            ((BouncyCastleProvider)provider).setProperty("SecureRandom.DEFAULT ThreadSafe", "true");
            provider.addAlgorithm("SecureRandom.DEFAULT", PREFIX + "$Default");
            ((BouncyCastleProvider)provider).setProperty("SecureRandom.NONCEANDIV ThreadSafe", "true");
            provider.addAlgorithm("SecureRandom.NONCEANDIV", PREFIX + "$NonceAndIV");
        }
    }

    public static class NonceAndIV
    extends SecureRandomSpi {
        private final SecureRandom random = DRBG.access$100(false);

        @Override
        protected void engineSetSeed(byte[] bytes) {
            this.random.setSeed(bytes);
        }

        @Override
        protected void engineNextBytes(byte[] bytes) {
            this.random.nextBytes(bytes);
        }

        @Override
        protected byte[] engineGenerateSeed(int numBytes) {
            return this.random.generateSeed(numBytes);
        }
    }
}


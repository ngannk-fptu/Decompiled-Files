/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicesConstraints;
import org.bouncycastle.crypto.CryptoServicesPermission;
import org.bouncycastle.crypto.DefaultNativeServices;
import org.bouncycastle.crypto.EntropyDaemon;
import org.bouncycastle.crypto.EntropyGatherer;
import org.bouncycastle.crypto.IncrementalEntropySource;
import org.bouncycastle.crypto.IncrementalEntropySourceProvider;
import org.bouncycastle.crypto.NativeEntropySource;
import org.bouncycastle.crypto.NativeLoader;
import org.bouncycastle.crypto.NativeServices;
import org.bouncycastle.crypto.SecureRandomProvider;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.drbg.HMacSP800DRBG;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public final class CryptoServicesRegistrar {
    private static final Logger LOG = Logger.getLogger(CryptoServicesRegistrar.class.getName());
    private static final Permission CanSetDefaultProperty = new CryptoServicesPermission("globalConfig");
    private static final Permission CanSetThreadProperty = new CryptoServicesPermission("threadLocalConfig");
    private static final Permission CanSetDefaultRandom = new CryptoServicesPermission("defaultRandomConfig");
    private static final Permission CanSetConstraints = new CryptoServicesPermission("constraints");
    private static final ThreadLocal<Map<String, Object[]>> threadProperties = new ThreadLocal();
    private static final Map<String, Object[]> globalProperties = Collections.synchronizedMap(new HashMap());
    private static final SecureRandomProvider defaultRandomProviderImpl = new ThreadLocalSecureRandomProvider();
    private static final CryptoServicesConstraints noConstraintsImpl = new CryptoServicesConstraints(){

        @Override
        public void check(CryptoServiceProperties service) {
        }
    };
    private static final AtomicReference<SecureRandomProvider> defaultSecureRandomProvider = new AtomicReference();
    private static final boolean preconfiguredConstraints;
    private static final AtomicReference<CryptoServicesConstraints> servicesConstraints;
    private static final NativeServices nativeServices;
    private static final String[][] initialEntropySourceNames;
    private static EntropyDaemon entropyDaemon;
    private static Thread entropyThread;

    private CryptoServicesRegistrar() {
    }

    public static String getInfo() {
        return "BouncyCastle APIs (LTS edition) v2.73.3";
    }

    public static boolean isNativeEnabled() {
        return NativeLoader.isNativeAvailable();
    }

    public static void setNativeEnabled(boolean enabled) {
        NativeLoader.setNativeEnabled(enabled);
    }

    public static NativeServices getNativeServices() {
        return nativeServices;
    }

    public static boolean hasEnabledService(String feature) {
        return nativeServices != null && nativeServices.isSupported() && nativeServices.isInstalled() && nativeServices.isEnabled() && nativeServices.hasService(feature);
    }

    public static SecureRandom getSecureRandom() {
        defaultSecureRandomProvider.compareAndSet(null, defaultRandomProviderImpl);
        return defaultSecureRandomProvider.get().get();
    }

    public static SecureRandom getSecureRandom(SecureRandom secureRandom) {
        return null == secureRandom ? CryptoServicesRegistrar.getSecureRandom() : secureRandom;
    }

    public static void setSecureRandom(final SecureRandom secureRandom) {
        CryptoServicesRegistrar.checkPermission(CanSetDefaultRandom);
        if (secureRandom == null) {
            defaultSecureRandomProvider.set(defaultRandomProviderImpl);
        } else {
            defaultSecureRandomProvider.set(new SecureRandomProvider(){

                @Override
                public SecureRandom get() {
                    return secureRandom;
                }
            });
        }
    }

    public static void setSecureRandomProvider(SecureRandomProvider secureRandomProvider) {
        CryptoServicesRegistrar.checkPermission(CanSetDefaultRandom);
        defaultSecureRandomProvider.set(secureRandomProvider);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static EntropySourceProvider getDefaultEntropySourceProvider() {
        if (NativeLoader.hasNativeService("DRBG") || NativeLoader.hasNativeService("NRBG")) {
            return new EntropySourceProvider(){

                @Override
                public EntropySource get(int bitsRequired) {
                    return new NativeEntropySource(bitsRequired);
                }
            };
        }
        if (Properties.isOverrideSet("org.bouncycastle.drbg.entropy_thread")) {
            EntropyDaemon entropyDaemon = CryptoServicesRegistrar.entropyDaemon;
            synchronized (entropyDaemon) {
                if (entropyThread == null) {
                    entropyThread = new Thread((Runnable)CryptoServicesRegistrar.entropyDaemon, "BC Entropy Daemon");
                    entropyThread.setDaemon(true);
                    entropyThread.start();
                }
            }
            return new EntropySourceProvider(){

                @Override
                public EntropySource get(int bitsRequired) {
                    return new HybridEntropySource(entropyDaemon, CryptoServicesRegistrar.createBaseEntropySourceProvider(), bitsRequired);
                }
            };
        }
        return new EntropySourceProvider(){

            @Override
            public EntropySource get(int bitsRequired) {
                return new OneShotHybridEntropySource(CryptoServicesRegistrar.createBaseEntropySourceProvider(), bitsRequired);
            }
        };
    }

    public static CryptoServicesConstraints getServicesConstraints() {
        return servicesConstraints.get();
    }

    public static void checkConstraints(CryptoServiceProperties cryptoService) {
        servicesConstraints.get().check(cryptoService);
    }

    public static void setServicesConstraints(CryptoServicesConstraints constraints) {
        CryptoServicesConstraints newConstraints;
        CryptoServicesRegistrar.checkPermission(CanSetConstraints);
        CryptoServicesConstraints cryptoServicesConstraints = newConstraints = constraints == null ? noConstraintsImpl : constraints;
        if (preconfiguredConstraints) {
            if (Properties.isOverrideSet("org.bouncycastle.constraints.allow_override")) {
                servicesConstraints.set(newConstraints);
            } else {
                LOG.warning("attempt to override pre-configured constraints ignored");
            }
        } else {
            servicesConstraints.set(newConstraints);
        }
    }

    public static <T> T getProperty(Property property) {
        Object[] values = CryptoServicesRegistrar.lookupProperty(property);
        if (values != null) {
            return (T)values[0];
        }
        return null;
    }

    private static Object[] lookupProperty(Property property) {
        Map<String, Object[]> properties = threadProperties.get();
        Object[] values = properties == null || !properties.containsKey(property.name) ? globalProperties.get(property.name) : properties.get(property.name);
        return values;
    }

    public static <T> T[] getSizedProperty(Property property) {
        Object[] values = CryptoServicesRegistrar.lookupProperty(property);
        if (values == null) {
            return null;
        }
        return (Object[])values.clone();
    }

    public static <T> T getSizedProperty(Property property, int size) {
        block4: {
            Object[] values;
            block3: {
                values = CryptoServicesRegistrar.lookupProperty(property);
                if (values == null) {
                    return null;
                }
                if (!property.type.isAssignableFrom(DHParameters.class)) break block3;
                for (int i = 0; i != values.length; ++i) {
                    DHParameters params = (DHParameters)values[i];
                    if (params.getP().bitLength() != size) continue;
                    return (T)params;
                }
                break block4;
            }
            if (!property.type.isAssignableFrom(DSAParameters.class)) break block4;
            for (int i = 0; i != values.length; ++i) {
                DSAParameters params = (DSAParameters)values[i];
                if (params.getP().bitLength() != size) continue;
                return (T)params;
            }
        }
        return null;
    }

    public static <T> void setThreadProperty(Property property, T ... propertyValue) {
        CryptoServicesRegistrar.checkPermission(CanSetThreadProperty);
        if (!property.type.isAssignableFrom(propertyValue[0].getClass())) {
            throw new IllegalArgumentException("Bad property value passed");
        }
        CryptoServicesRegistrar.localSetThread(property, (Object[])propertyValue.clone());
    }

    public static <T> void setGlobalProperty(Property property, T ... propertyValue) {
        CryptoServicesRegistrar.checkPermission(CanSetDefaultProperty);
        CryptoServicesRegistrar.localSetGlobalProperty(property, (Object[])propertyValue.clone());
    }

    private static <T> void localSetThread(Property property, T[] propertyValue) {
        Map<String, Object[]> properties = threadProperties.get();
        if (properties == null) {
            properties = new HashMap<String, Object[]>();
            threadProperties.set(properties);
        }
        properties.put(property.name, propertyValue);
    }

    private static <T> void localSetGlobalProperty(Property property, T ... propertyValue) {
        if (!property.type.isAssignableFrom(propertyValue[0].getClass())) {
            throw new IllegalArgumentException("Bad property value passed");
        }
        CryptoServicesRegistrar.localSetThread(property, propertyValue);
        globalProperties.put(property.name, propertyValue);
    }

    public static <T> T[] clearGlobalProperty(Property property) {
        CryptoServicesRegistrar.checkPermission(CanSetDefaultProperty);
        CryptoServicesRegistrar.localClearThreadProperty(property);
        return globalProperties.remove(property.name);
    }

    public static <T> T[] clearThreadProperty(Property property) {
        CryptoServicesRegistrar.checkPermission(CanSetThreadProperty);
        return CryptoServicesRegistrar.localClearThreadProperty(property);
    }

    private static Object[] localClearThreadProperty(Property property) {
        Map<String, Object[]> properties = threadProperties.get();
        if (properties == null) {
            properties = new HashMap<String, Object[]>();
            threadProperties.set(properties);
        }
        return properties.remove(property.name);
    }

    private static void checkPermission(final Permission permission) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    securityManager.checkPermission(permission);
                    return null;
                }
            });
        }
    }

    private static DHParameters toDH(DSAParameters dsaParams) {
        int pSize = dsaParams.getP().bitLength();
        int m = CryptoServicesRegistrar.chooseLowerBound(pSize);
        return new DHParameters(dsaParams.getP(), dsaParams.getG(), dsaParams.getQ(), m, 0, null, new DHValidationParameters(dsaParams.getValidationParameters().getSeed(), dsaParams.getValidationParameters().getCounter()));
    }

    private static int chooseLowerBound(int pSize) {
        int m = 160;
        if (pSize > 1024) {
            m = pSize <= 2048 ? 224 : (pSize <= 3072 ? 256 : (pSize <= 7680 ? 384 : 512));
        }
        return m;
    }

    private static CryptoServicesConstraints getDefaultConstraints() {
        return noConstraintsImpl;
    }

    private static final Object[] findSource() {
        for (int t = 0; t < initialEntropySourceNames.length; ++t) {
            String[] pair = initialEntropySourceNames[t];
            try {
                Object[] r = new Object[]{Class.forName(pair[0]).newInstance(), Class.forName(pair[1]).newInstance()};
                return r;
            }
            catch (Throwable ex) {
                continue;
            }
        }
        return null;
    }

    private static EntropySourceProvider createBaseEntropySourceProvider() {
        String source = AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                return Security.getProperty("securerandom.source");
            }
        });
        if (source == null) {
            return CryptoServicesRegistrar.createInternalEntropySourceProvider();
        }
        try {
            return new URLSeededEntropySourceProvider(new URL(source));
        }
        catch (Exception e) {
            return CryptoServicesRegistrar.createInternalEntropySourceProvider();
        }
    }

    private static IncrementalEntropySourceProvider createInternalEntropySourceProvider() {
        boolean hasGetInstanceStrong = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                try {
                    Class<SecureRandom> def = SecureRandom.class;
                    return def.getMethod("getInstanceStrong", new Class[0]) != null;
                }
                catch (Exception e) {
                    return false;
                }
            }
        });
        if (hasGetInstanceStrong) {
            SecureRandom strong = AccessController.doPrivileged(new PrivilegedAction<SecureRandom>(){

                @Override
                public SecureRandom run() {
                    try {
                        return (SecureRandom)SecureRandom.class.getMethod("getInstanceStrong", new Class[0]).invoke(null, new Object[0]);
                    }
                    catch (Exception e) {
                        return new CoreSecureRandom(CryptoServicesRegistrar.findSource());
                    }
                }
            });
            return new IncrementalEntropySourceProvider(strong, true);
        }
        return new IncrementalEntropySourceProvider(AccessController.doPrivileged(new PrivilegedAction<SecureRandom>(){

            @Override
            public SecureRandom run() {
                return new CoreSecureRandom(CryptoServicesRegistrar.findSource());
            }
        }), true);
    }

    private static void sleep(long ms) throws InterruptedException {
        if (ms != 0L) {
            Thread.sleep(ms);
        }
    }

    static {
        servicesConstraints = new AtomicReference();
        DSAParameters def512Params = new DSAParameters(new BigInteger("fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151bdc43ee737592e17", 16), new BigInteger("962eddcc369cba8ebb260ee6b6a126d9346e38c5", 16), new BigInteger("678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a6c416e50be794ca4", 16), new DSAValidationParameters(Hex.decodeStrict("b869c82b35d70e1b1ff91b28e37a62ecdc34409b"), 123));
        DSAParameters def768Params = new DSAParameters(new BigInteger("e9e642599d355f37c97ffd3567120b8e25c9cd43e927b3a9670fbec5d890141922d2c3b3ad2480093799869d1e846aab49fab0ad26d2ce6a22219d470bce7d777d4a21fbe9c270b57f607002f3cef8393694cf45ee3688c11a8c56ab127a3daf", 16), new BigInteger("9cdbd84c9f1ac2f38d0f80f42ab952e7338bf511", 16), new BigInteger("30470ad5a005fb14ce2d9dcd87e38bc7d1b1c5facbaecbe95f190aa7a31d23c4dbbcbe06174544401a5b2c020965d8c2bd2171d3668445771f74ba084d2029d83c1c158547f3a9f1a2715be23d51ae4d3e5a1f6a7064f316933a346d3f529252", 16), new DSAValidationParameters(Hex.decodeStrict("77d0f8c4dad15eb8c4f2f8d6726cefd96d5bb399"), 263));
        DSAParameters def1024Params = new DSAParameters(new BigInteger("fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c7", 16), new BigInteger("9760508f15230bccb292b982a2eb840bf0581cf5", 16), new BigInteger("f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a", 16), new DSAValidationParameters(Hex.decodeStrict("8d5155894229d5e689ee01e6018a237e2cae64cd"), 92));
        DSAParameters def2048Params = new DSAParameters(new BigInteger("95475cf5d93e596c3fcd1d902add02f427f5f3c7210313bb45fb4d5bb2e5fe1cbd678cd4bbdd84c9836be1f31c0777725aeb6c2fc38b85f48076fa76bcd8146cc89a6fb2f706dd719898c2083dc8d896f84062e2c9c94d137b054a8d8096adb8d51952398eeca852a0af12df83e475aa65d4ec0c38a9560d5661186ff98b9fc9eb60eee8b030376b236bc73be3acdbd74fd61c1d2475fa3077b8f080467881ff7e1ca56fee066d79506ade51edbb5443a563927dbc4ba520086746175c8885925ebc64c6147906773496990cb714ec667304e261faee33b3cbdf008e0c3fa90650d97d3909c9275bf4ac86ffcb3d03e6dfc8ada5934242dd6d3bcca2a406cb0b", 16), new BigInteger("f8183668ba5fc5bb06b5981e6d8b795d30b8978d43ca0ec572e37e09939a9773", 16), new BigInteger("42debb9da5b3d88cc956e08787ec3f3a09bba5f48b889a74aaf53174aa0fbe7e3c5b8fcd7a53bef563b0e98560328960a9517f4014d3325fc7962bf1e049370d76d1314a76137e792f3f0db859d095e4a5b932024f079ecf2ef09c797452b0770e1350782ed57ddf794979dcef23cb96f183061965c4ebc93c9c71c56b925955a75f94cccf1449ac43d586d0beee43251b0b2287349d68de0d144403f13e802f4146d882e057af19b6f6275c6676c8fa0e3ca2713a3257fd1b27d0639f695e347d8d1cf9ac819a26ca9b04cb0eb9b7b035988d15bbac65212a55239cfc7e58fae38d7250ab9991ffbc97134025fe8ce04c4399ad96569be91a546f4978693c7a", 16), new DSAValidationParameters(Hex.decodeStrict("b0b4417601b59cbc9d8ac8f935cadaec4f5fbb2f23785609ae466748d9b5a536"), 497));
        CryptoServicesRegistrar.localSetGlobalProperty(Property.DSA_DEFAULT_PARAMS, def512Params, def768Params, def1024Params, def2048Params);
        CryptoServicesRegistrar.localSetGlobalProperty(Property.DH_DEFAULT_PARAMS, CryptoServicesRegistrar.toDH(def512Params), CryptoServicesRegistrar.toDH(def768Params), CryptoServicesRegistrar.toDH(def1024Params), CryptoServicesRegistrar.toDH(def2048Params));
        servicesConstraints.set(CryptoServicesRegistrar.getDefaultConstraints());
        preconfiguredConstraints = servicesConstraints.get() != noConstraintsImpl;
        NativeLoader.loadDriver();
        nativeServices = new DefaultNativeServices();
        initialEntropySourceNames = new String[][]{{"sun.security.provider.Sun", "sun.security.provider.SecureRandom"}, {"org.apache.harmony.security.provider.crypto.CryptoProvider", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl"}, {"com.android.org.conscrypt.OpenSSLProvider", "com.android.org.conscrypt.OpenSSLRandom"}, {"org.conscrypt.OpenSSLProvider", "org.conscrypt.OpenSSLRandom"}};
        entropyDaemon = null;
        entropyThread = null;
        entropyDaemon = new EntropyDaemon();
        entropyThread = new Thread((Runnable)entropyDaemon, "BC Entropy Daemon");
        entropyThread.setDaemon(true);
        entropyThread.start();
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class ThreadLocalSecureRandomProvider
    implements SecureRandomProvider {
        final ThreadLocal<SecureRandom> defaultRandoms = new ThreadLocal();

        private ThreadLocalSecureRandomProvider() {
        }

        @Override
        public SecureRandom get() {
            if (this.defaultRandoms.get() == null) {
                this.defaultRandoms.set(new SecureRandom());
            }
            return this.defaultRandoms.get();
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class OneShotHybridEntropySource
    implements EntropySource {
        private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
        private final AtomicInteger samples = new AtomicInteger(0);
        private final SP80090DRBG drbg;
        private final SignallingEntropySource entropySource;
        private final int bytesRequired;
        private final byte[] additionalInput = Pack.longToBigEndian(System.currentTimeMillis());

        OneShotHybridEntropySource(EntropySourceProvider entropyProvider, int bitsRequired) {
            this.bytesRequired = (bitsRequired + 7) / 8;
            this.entropySource = new SignallingEntropySource(this.seedAvailable, entropyProvider, 256);
            this.drbg = new HMacSP800DRBG(new HMac(new SHA512Digest()), 256, this.entropySource, Strings.toByteArray("Bouncy Castle One Shot Entropy Source"), this.entropySource.getEntropy());
        }

        @Override
        public boolean isPredictionResistant() {
            return true;
        }

        @Override
        public byte[] getEntropy() {
            byte[] entropy = new byte[this.bytesRequired];
            if (this.samples.getAndIncrement() > 1024) {
                if (this.seedAvailable.getAndSet(false)) {
                    this.samples.set(0);
                    this.drbg.reseed(this.additionalInput);
                } else {
                    this.entropySource.schedule();
                }
            }
            if (this.drbg.generate(entropy, null, false) < 0) {
                this.drbg.reseed(this.additionalInput);
                this.drbg.generate(entropy, null, false);
            }
            return entropy;
        }

        @Override
        public int entropySize() {
            return this.bytesRequired * 8;
        }

        /*
         * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
         */
        private class SignallingEntropySource
        implements IncrementalEntropySource {
            private final AtomicBoolean seedAvailable;
            private final IncrementalEntropySource entropySource;
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            SignallingEntropySource(AtomicBoolean seedAvailable, EntropySourceProvider baseRandom, int bitsRequired) {
                this.seedAvailable = seedAvailable;
                this.entropySource = (IncrementalEntropySource)baseRandom.get(bitsRequired);
                this.byteLength = (bitsRequired + 7) / 8;
            }

            @Override
            public boolean isPredictionResistant() {
                return true;
            }

            @Override
            public byte[] getEntropy() {
                try {
                    return this.getEntropy(0L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override
            public byte[] getEntropy(long pause) throws InterruptedException {
                byte[] seed = this.entropy.getAndSet(null);
                if (seed == null || seed.length != this.byteLength) {
                    seed = this.entropySource.getEntropy(pause);
                } else {
                    this.scheduled.set(false);
                }
                return seed;
            }

            void schedule() {
                if (!this.scheduled.getAndSet(true)) {
                    Thread thread = new Thread(new EntropyGatherer(this.entropySource, this.seedAvailable, this.entropy));
                    thread.setDaemon(true);
                    thread.start();
                }
            }

            @Override
            public int entropySize() {
                return this.byteLength * 8;
            }
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class URLSeededEntropySourceProvider
    implements EntropySourceProvider {
        private final InputStream seedStream;

        URLSeededEntropySourceProvider(final URL url) {
            this.seedStream = AccessController.doPrivileged(new PrivilegedAction<InputStream>(){

                @Override
                public InputStream run() {
                    try {
                        return url.openStream();
                    }
                    catch (IOException e) {
                        throw new IllegalStateException("unable to open random source");
                    }
                }
            });
        }

        private int privilegedRead(final byte[] data, final int off, final int len) {
            return AccessController.doPrivileged(new PrivilegedAction<Integer>(){

                @Override
                public Integer run() {
                    try {
                        return seedStream.read(data, off, len);
                    }
                    catch (IOException e) {
                        throw new InternalError("unable to read random source");
                    }
                }
            });
        }

        @Override
        public EntropySource get(final int bitsRequired) {
            return new IncrementalEntropySource(){
                private final int numBytes;
                {
                    this.numBytes = (bitsRequired + 7) / 8;
                }

                @Override
                public byte[] getEntropy(long pause) throws InterruptedException {
                    byte[] seed = new byte[this.numBytes];
                    for (int i = 0; i < this.numBytes / 8; ++i) {
                        CryptoServicesRegistrar.sleep(pause);
                        this.fetchEntropy(seed, i * 8, 8);
                    }
                    int extra = this.numBytes - this.numBytes / 8 * 8;
                    if (extra != 0) {
                        CryptoServicesRegistrar.sleep(pause);
                        this.fetchEntropy(seed, seed.length - extra, extra);
                    }
                    return seed;
                }

                @Override
                public boolean isPredictionResistant() {
                    return true;
                }

                @Override
                public byte[] getEntropy() {
                    try {
                        return this.getEntropy(0L);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("initial entropy fetch interrupted");
                    }
                }

                private void fetchEntropy(byte[] data, int dataOff, int length) {
                    int off;
                    int len;
                    for (off = 0; off != length && (len = this.privilegedRead(data, dataOff + off, length - off)) > -1; off += len) {
                    }
                    if (off != length) {
                        throw new InternalError("unable to fully read random source");
                    }
                }

                @Override
                public int entropySize() {
                    return bitsRequired;
                }
            };
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class HybridEntropySource
    implements EntropySource {
        private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
        private final AtomicInteger samples = new AtomicInteger(0);
        private final SP80090DRBG drbg;
        private final SignallingEntropySource entropySource;
        private final int bytesRequired;
        private final byte[] additionalInput = Pack.longToBigEndian(System.currentTimeMillis());

        HybridEntropySource(EntropyDaemon entropyDaemon, EntropySourceProvider entropyProvider, int bitsRequired) {
            this.bytesRequired = (bitsRequired + 7) / 8;
            this.entropySource = new SignallingEntropySource(entropyDaemon, this.seedAvailable, entropyProvider, 256);
            this.drbg = new HMacSP800DRBG(new HMac(new SHA512Digest()), 256, this.entropySource, Strings.toByteArray("Bouncy Castle Hybrid Entropy Source"), this.entropySource.getEntropy());
        }

        @Override
        public boolean isPredictionResistant() {
            return true;
        }

        @Override
        public byte[] getEntropy() {
            byte[] entropy = new byte[this.bytesRequired];
            if (this.samples.getAndIncrement() > 20) {
                if (this.seedAvailable.getAndSet(false)) {
                    this.samples.set(0);
                    this.drbg.reseed(this.additionalInput);
                } else {
                    this.entropySource.schedule();
                }
            }
            if (this.drbg.generate(entropy, null, false) < 0) {
                this.drbg.reseed(this.additionalInput);
                this.drbg.generate(entropy, null, false);
            }
            return entropy;
        }

        @Override
        public int entropySize() {
            return this.bytesRequired * 8;
        }

        /*
         * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
         */
        private class SignallingEntropySource
        implements EntropySource {
            private final EntropyDaemon entropyDaemon;
            private final AtomicBoolean seedAvailable;
            private final EntropySource entropySource;
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            SignallingEntropySource(EntropyDaemon entropyDaemon, AtomicBoolean seedAvailable, EntropySourceProvider baseRandom, int bitsRequired) {
                this.entropyDaemon = entropyDaemon;
                this.seedAvailable = seedAvailable;
                this.entropySource = baseRandom.get(bitsRequired);
                this.byteLength = (bitsRequired + 7) / 8;
            }

            @Override
            public boolean isPredictionResistant() {
                return true;
            }

            @Override
            public byte[] getEntropy() {
                byte[] seed = this.entropy.getAndSet(null);
                if (seed == null || seed.length != this.byteLength) {
                    seed = this.entropySource.getEntropy();
                } else {
                    this.scheduled.set(false);
                }
                this.schedule();
                return seed;
            }

            void schedule() {
                if (!this.scheduled.getAndSet(true)) {
                    this.entropyDaemon.addTask(new EntropyGatherer(this.entropySource, this.seedAvailable, this.entropy));
                }
            }

            @Override
            public int entropySize() {
                return this.byteLength * 8;
            }
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class CoreSecureRandom
    extends SecureRandom {
        CoreSecureRandom(Object[] initialEntropySourceAndSpi) {
            super((SecureRandomSpi)initialEntropySourceAndSpi[1], (Provider)initialEntropySourceAndSpi[0]);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class Property {
        public static final Property EC_IMPLICITLY_CA = new Property("ecImplicitlyCA", X9ECParameters.class);
        public static final Property DH_DEFAULT_PARAMS = new Property("dhDefaultParams", DHParameters.class);
        public static final Property DSA_DEFAULT_PARAMS = new Property("dsaDefaultParams", DSAParameters.class);
        private final String name;
        private final Class type;

        private Property(String name, Class type) {
            this.name = name;
            this.type = type;
        }
    }
}


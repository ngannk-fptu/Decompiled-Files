/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jce.provider.BouncyCastleProviderConfiguration;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public final class BouncyCastleProvider
extends Provider
implements ConfigurableProvider {
    private static final Logger LOG = Logger.getLogger(BouncyCastleProvider.class.getName());
    private static String info = CryptoServicesRegistrar.getInfo().replace("APIs", "Security Provider");
    public static final String PROVIDER_NAME = "BC";
    public static final ProviderConfiguration CONFIGURATION = new BouncyCastleProviderConfiguration();
    private static final Map keyInfoConverters = new HashMap();
    private static final Class revChkClass = ClassUtil.loadClass(BouncyCastleProvider.class, "java.security.cert.PKIXRevocationChecker");
    private static final String SYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.symmetric.";
    private static final String[] SYMMETRIC_GENERIC = new String[]{"PBEPBKDF1", "PBEPBKDF2", "PBEPKCS12", "TLSKDF", "SCRYPT"};
    private static final String[] SYMMETRIC_MACS = new String[]{"SipHash", "SipHash128", "Poly1305"};
    private static final CryptoServiceProperties[] SYMMETRIC_CIPHERS = new CryptoServiceProperties[]{BouncyCastleProvider.service("AES", 256), BouncyCastleProvider.service("ARC4", 20), BouncyCastleProvider.service("ARIA", 256), BouncyCastleProvider.service("Blowfish", 128), BouncyCastleProvider.service("Camellia", 256), BouncyCastleProvider.service("CAST5", 128), BouncyCastleProvider.service("CAST6", 256), BouncyCastleProvider.service("ChaCha", 128), BouncyCastleProvider.service("DES", 56), BouncyCastleProvider.service("DESede", 112), BouncyCastleProvider.service("GOST28147", 128), BouncyCastleProvider.service("Grainv1", 128), BouncyCastleProvider.service("Grain128", 128), BouncyCastleProvider.service("HC128", 128), BouncyCastleProvider.service("HC256", 256), BouncyCastleProvider.service("IDEA", 128), BouncyCastleProvider.service("Noekeon", 128), BouncyCastleProvider.service("RC2", 128), BouncyCastleProvider.service("RC5", 128), BouncyCastleProvider.service("RC6", 256), BouncyCastleProvider.service("Rijndael", 256), BouncyCastleProvider.service("Salsa20", 128), BouncyCastleProvider.service("SEED", 128), BouncyCastleProvider.service("Serpent", 256), BouncyCastleProvider.service("Shacal2", 128), BouncyCastleProvider.service("Skipjack", 80), BouncyCastleProvider.service("SM4", 128), BouncyCastleProvider.service("TEA", 128), BouncyCastleProvider.service("Twofish", 256), BouncyCastleProvider.service("Threefish", 128), BouncyCastleProvider.service("VMPC", 128), BouncyCastleProvider.service("VMPCKSA3", 128), BouncyCastleProvider.service("XTEA", 128), BouncyCastleProvider.service("XSalsa20", 128), BouncyCastleProvider.service("OpenSSLPBKDF", 128), BouncyCastleProvider.service("DSTU7624", 256), BouncyCastleProvider.service("GOST3412_2015", 256), BouncyCastleProvider.service("Zuc", 128)};
    private static final String ASYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.asymmetric.";
    private static final String[] ASYMMETRIC_GENERIC = new String[]{"X509", "IES", "COMPOSITE", "EXTERNAL"};
    private static final String[] ASYMMETRIC_CIPHERS = new String[]{"DSA", "DH", "EC", "RSA", "GOST", "ECGOST", "ElGamal", "DSTU4145", "GM", "EdEC"};
    private static final String DIGEST_PACKAGE = "org.bouncycastle.jcajce.provider.digest.";
    private static final String[] DIGESTS = new String[]{"GOST3411", "Keccak", "MD2", "MD4", "MD5", "SHA1", "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", "SHA224", "SHA256", "SHA384", "SHA512", "SHA3", "Skein", "SM3", "Tiger", "Whirlpool", "Blake2b", "Blake2s", "DSTU7564", "Haraka", "Blake3"};
    private static final String KEYSTORE_PACKAGE = "org.bouncycastle.jcajce.provider.keystore.";
    private static final String[] KEYSTORES = new String[]{"BC", "BCFKS", "PKCS12"};
    private static final String SECURE_RANDOM_PACKAGE = "org.bouncycastle.jcajce.provider.drbg.";
    private static final String[] SECURE_RANDOMS = new String[]{"DRBG"};
    private Map<String, Provider.Service> serviceMap = new ConcurrentHashMap<String, Provider.Service>();

    public BouncyCastleProvider() {
        super(PROVIDER_NAME, 2.7302, info);
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                BouncyCastleProvider.this.setup();
                return null;
            }
        });
    }

    private void setup() {
        this.loadAlgorithms(DIGEST_PACKAGE, DIGESTS);
        this.loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_GENERIC);
        this.loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_MACS);
        this.loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_CIPHERS);
        this.loadAlgorithms(ASYMMETRIC_PACKAGE, ASYMMETRIC_GENERIC);
        this.loadAlgorithms(ASYMMETRIC_PACKAGE, ASYMMETRIC_CIPHERS);
        this.loadAlgorithms(KEYSTORE_PACKAGE, KEYSTORES);
        this.loadAlgorithms(SECURE_RANDOM_PACKAGE, SECURE_RANDOMS);
        this.loadPQCKeys();
        if (revChkClass != null) {
            this.put("CertPathValidator.RFC5280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
            this.put("CertPathBuilder.RFC5280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
            this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
            this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
            this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
            this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
        } else {
            this.put("CertPathValidator.RFC5280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
            this.put("CertPathBuilder.RFC5280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
            this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
            this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
            this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
            this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
        }
        this.put("CertStore.Collection", "org.bouncycastle.jce.provider.CertStoreCollectionSpi");
        this.put("CertStore.LDAP", "org.bouncycastle.jce.provider.X509LDAPCertStoreSpi");
        this.put("CertStore.Multi", "org.bouncycastle.jce.provider.MultiCertStoreSpi");
        this.put("Alg.Alias.CertStore.X509LDAP", "LDAP");
        this.getService("SecureRandom", "DEFAULT");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Provider.Service getService(final String type, final String algorithm) {
        String upperCaseAlgName = Strings.toUpperCase(algorithm);
        final String key = type + "." + upperCaseAlgName;
        Provider.Service service = this.serviceMap.get(key);
        if (service == null) {
            BouncyCastleProvider bouncyCastleProvider = this;
            synchronized (bouncyCastleProvider) {
                service = !this.serviceMap.containsKey(key) ? AccessController.doPrivileged(new PrivilegedAction<Provider.Service>(){

                    @Override
                    public Provider.Service run() {
                        Provider.Service service = BouncyCastleProvider.super.getService(type, algorithm);
                        if (service == null) {
                            return null;
                        }
                        BouncyCastleProvider.this.serviceMap.put(key, service);
                        BouncyCastleProvider.super.remove(service.getType() + "." + service.getAlgorithm());
                        BouncyCastleProvider.super.putService(service);
                        return service;
                    }
                }) : this.serviceMap.get(key);
            }
        }
        return service;
    }

    private void loadAlgorithms(String packageName, String[] names) {
        for (int i = 0; i != names.length; ++i) {
            this.loadServiceClass(packageName, names[i]);
        }
    }

    private void loadAlgorithms(String packageName, CryptoServiceProperties[] services) {
        for (int i = 0; i != services.length; ++i) {
            CryptoServiceProperties service = services[i];
            try {
                CryptoServicesRegistrar.checkConstraints(service);
                this.loadServiceClass(packageName, service.getServiceName());
                continue;
            }
            catch (CryptoServiceConstraintsException e) {
                if (!LOG.isLoggable(Level.FINE)) continue;
                LOG.fine("service for " + service.getServiceName() + " ignored due to constraints");
            }
        }
    }

    private void loadServiceClass(String packageName, String serviceName) {
        Class clazz = ClassUtil.loadClass(BouncyCastleProvider.class, packageName + serviceName + "$Mappings");
        if (clazz != null) {
            try {
                ((AlgorithmProvider)clazz.newInstance()).configure(this);
            }
            catch (Exception e) {
                throw new InternalError("cannot create instance of " + packageName + serviceName + "$Mappings : " + e);
            }
        }
    }

    private void loadPQCKeys() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setParameter(String parameterName, Object parameter) {
        ProviderConfiguration providerConfiguration = CONFIGURATION;
        synchronized (providerConfiguration) {
            ((BouncyCastleProviderConfiguration)CONFIGURATION).setParameter(parameterName, parameter);
        }
    }

    @Override
    public boolean hasAlgorithm(String type, String name) {
        return this.containsKey(type + "." + name) || this.containsKey("Alg.Alias." + type + "." + name);
    }

    @Override
    public void addAlgorithm(String key, String value) {
        if (this.containsKey(key)) {
            throw new IllegalStateException("duplicate provider key (" + key + ") found");
        }
        this.put(key, value);
    }

    @Override
    public void addAlgorithm(String key, String value, Map<String, String> attributes) {
        this.addAlgorithm(key, value);
        this.addAttributes(key, attributes);
    }

    @Override
    public void addAlgorithm(String type, ASN1ObjectIdentifier oid, String className) {
        this.addAlgorithm(type + "." + oid, className);
        this.addAlgorithm(type + ".OID." + oid, className);
    }

    @Override
    public void addAlgorithm(String type, ASN1ObjectIdentifier oid, String className, Map<String, String> attributes) {
        this.addAlgorithm(type, oid, className);
        this.addAttributes(type + "." + oid, attributes);
        this.addAttributes(type + ".OID." + oid, attributes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addKeyInfoConverter(ASN1ObjectIdentifier oid, AsymmetricKeyInfoConverter keyInfoConverter) {
        Map map = keyInfoConverters;
        synchronized (map) {
            keyInfoConverters.put(oid, keyInfoConverter);
        }
    }

    @Override
    public AsymmetricKeyInfoConverter getKeyInfoConverter(ASN1ObjectIdentifier oid) {
        return (AsymmetricKeyInfoConverter)keyInfoConverters.get(oid);
    }

    @Override
    public void addAttributes(String key, Map<String, String> attributeMap) {
        this.put(key + " ImplementedIn", "Software");
        for (String attributeName : attributeMap.keySet()) {
            String attributeKey = key + " " + attributeName;
            if (this.containsKey(attributeKey)) {
                throw new IllegalStateException("duplicate provider attribute key (" + attributeKey + ") found");
            }
            this.put(attributeKey, attributeMap.get(attributeName));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier algorithm) {
        Map map = keyInfoConverters;
        synchronized (map) {
            return (AsymmetricKeyInfoConverter)keyInfoConverters.get(algorithm);
        }
    }

    public static PublicKey getPublicKey(SubjectPublicKeyInfo publicKeyInfo) throws IOException {
        AsymmetricKeyInfoConverter converter = BouncyCastleProvider.getAsymmetricKeyInfoConverter(publicKeyInfo.getAlgorithm().getAlgorithm());
        if (converter == null) {
            return null;
        }
        return converter.generatePublic(publicKeyInfo);
    }

    public static PrivateKey getPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        AsymmetricKeyInfoConverter converter = BouncyCastleProvider.getAsymmetricKeyInfoConverter(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
        if (converter == null) {
            return null;
        }
        return converter.generatePrivate(privateKeyInfo);
    }

    private static CryptoServiceProperties service(String name, int bitsOfSecurity) {
        return new JcaCryptoService(name, bitsOfSecurity);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class JcaCryptoService
    implements CryptoServiceProperties {
        private final String name;
        private final int bitsOfSecurity;

        JcaCryptoService(String name, int bitsOfSecurity) {
            this.name = name;
            this.bitsOfSecurity = bitsOfSecurity;
        }

        @Override
        public int bitsOfSecurity() {
            return this.bitsOfSecurity;
        }

        @Override
        public String getServiceName() {
            return this.name;
        }

        @Override
        public CryptoServicePurpose getPurpose() {
            return CryptoServicePurpose.ANY;
        }

        @Override
        public Object getParams() {
            return null;
        }
    }
}


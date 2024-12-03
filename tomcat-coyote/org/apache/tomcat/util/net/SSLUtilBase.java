/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.KeyStoreUtil
 */
package org.apache.tomcat.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.DomainLoadStoreParameter;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.KeyStoreUtil;

public abstract class SSLUtilBase
implements SSLUtil {
    private static final Log log = LogFactory.getLog(SSLUtilBase.class);
    private static final StringManager sm = StringManager.getManager(SSLUtilBase.class);
    public static final String DEFAULT_KEY_ALIAS = "tomcat";
    protected final SSLHostConfig sslHostConfig;
    protected final SSLHostConfigCertificate certificate;
    private final String[] enabledProtocols;
    private final String[] enabledCiphers;

    protected SSLUtilBase(SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }

    protected SSLUtilBase(SSLHostConfigCertificate certificate, boolean warnTls13) {
        List<String> enabledProtocols;
        this.certificate = certificate;
        this.sslHostConfig = certificate.getSSLHostConfig();
        Set<String> configuredProtocols = this.sslHostConfig.getProtocols();
        Set<String> implementedProtocols = this.getImplementedProtocols();
        if (!implementedProtocols.contains("TLSv1.3") && !this.sslHostConfig.isExplicitlyRequestedProtocol("TLSv1.3")) {
            configuredProtocols.remove("TLSv1.3");
        }
        if (!implementedProtocols.contains("SSLv2Hello") && !this.sslHostConfig.isExplicitlyRequestedProtocol("SSLv2Hello")) {
            configuredProtocols.remove("SSLv2Hello");
        }
        if ((enabledProtocols = SSLUtilBase.getEnabled("protocols", this.getLog(), warnTls13, configuredProtocols, implementedProtocols)).contains("SSLv3")) {
            log.warn((Object)sm.getString("sslUtilBase.ssl3"));
        }
        this.enabledProtocols = enabledProtocols.toArray(new String[0]);
        if (enabledProtocols.contains("TLSv1.3") && this.sslHostConfig.getCertificateVerification().isOptional() && !this.isTls13RenegAuthAvailable() && warnTls13) {
            log.warn((Object)sm.getString("sslUtilBase.tls13.auth"));
        }
        this.sslHostConfig.setTls13RenegotiationAvailable(this.isTls13RenegAuthAvailable());
        if (this.sslHostConfig.getCiphers().startsWith("PROFILE=")) {
            this.enabledCiphers = new String[0];
        } else {
            boolean warnOnSkip = !this.sslHostConfig.getCiphers().equals("HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA");
            List<String> configuredCiphers = this.sslHostConfig.getJsseCipherNames();
            Set<String> implementedCiphers = this.getImplementedCiphers();
            List<String> enabledCiphers = SSLUtilBase.getEnabled("ciphers", this.getLog(), warnOnSkip, configuredCiphers, implementedCiphers);
            this.enabledCiphers = enabledCiphers.toArray(new String[0]);
        }
    }

    static <T> List<T> getEnabled(String name, Log log, boolean warnOnSkip, Collection<T> configured, Collection<T> implemented) {
        ArrayList<T> enabled = new ArrayList<T>();
        if (implemented.size() == 0) {
            enabled.addAll(configured);
        } else {
            enabled.addAll(configured);
            enabled.retainAll(implemented);
            if (enabled.isEmpty()) {
                throw new IllegalArgumentException(sm.getString("sslUtilBase.noneSupported", new Object[]{name, configured}));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("sslUtilBase.active", new Object[]{name, enabled}));
            }
            if ((log.isDebugEnabled() || warnOnSkip) && enabled.size() != configured.size()) {
                ArrayList<T> skipped = new ArrayList<T>(configured);
                skipped.removeAll(enabled);
                String msg = sm.getString("sslUtilBase.skipped", new Object[]{name, skipped});
                if (warnOnSkip) {
                    log.warn((Object)msg);
                } else {
                    log.debug((Object)msg);
                }
            }
        }
        return enabled;
    }

    static KeyStore getStore(String type, String provider, String path, String pass, String passFile) throws IOException {
        KeyStore ks = null;
        InputStream istream = null;
        try {
            ks = provider == null ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
            if ("DKS".equalsIgnoreCase(type)) {
                URI uri = ConfigFileLoader.getSource().getURI(path);
                ks.load(new DomainLoadStoreParameter(uri, Collections.emptyMap()));
            } else {
                if (!("PKCS11".equalsIgnoreCase(type) || path.isEmpty() || "NONE".equalsIgnoreCase(path))) {
                    istream = ConfigFileLoader.getSource().getResource(path).getInputStream();
                }
                char[] storePass = null;
                String passToUse = null;
                if (passFile != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(ConfigFileLoader.getSource().getResource(passFile).getInputStream(), StandardCharsets.UTF_8));){
                        passToUse = reader.readLine();
                    }
                } else {
                    passToUse = pass;
                }
                if (passToUse != null && (!"".equals(passToUse) || "JKS".equalsIgnoreCase(type) || "PKCS12".equalsIgnoreCase(type))) {
                    storePass = passToUse.toCharArray();
                }
                KeyStoreUtil.load((KeyStore)ks, (InputStream)istream, storePass);
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception ex) {
            String msg = sm.getString("sslUtilBase.keystore_load_failed", new Object[]{type, path, ex.getMessage()});
            log.error((Object)msg, (Throwable)ex);
            throw new IOException(msg);
        }
        finally {
            if (istream != null) {
                try {
                    istream.close();
                }
                catch (IOException iOException) {}
            }
        }
        return ks;
    }

    @Override
    public final SSLContext createSSLContext(List<String> negotiableProtocols) throws Exception {
        SSLContext sslContext = this.createSSLContextInternal(negotiableProtocols);
        sslContext.init(this.getKeyManagers(), this.getTrustManagers(), null);
        SSLSessionContext sessionContext = sslContext.getServerSessionContext();
        if (sessionContext != null) {
            this.configureSessionContext(sessionContext);
        }
        return sslContext;
    }

    @Override
    public void configureSessionContext(SSLSessionContext sslSessionContext) {
        if (this.sslHostConfig.getSessionCacheSize() >= 0) {
            sslSessionContext.setSessionCacheSize(this.sslHostConfig.getSessionCacheSize());
        }
        if (this.sslHostConfig.getSessionTimeout() >= 0) {
            sslSessionContext.setSessionTimeout(this.sslHostConfig.getSessionTimeout());
        }
    }

    @Override
    public KeyManager[] getKeyManagers() throws Exception {
        KeyManagerFactory kmf;
        KeyStore ks;
        String keyAlias = this.certificate.getCertificateKeyAlias();
        String algorithm = this.sslHostConfig.getKeyManagerAlgorithm();
        String keyPassFile = this.certificate.getCertificateKeyPasswordFile();
        String keyPass = this.certificate.getCertificateKeyPassword();
        if (keyPassFile == null) {
            keyPassFile = this.certificate.getCertificateKeystorePasswordFile();
        }
        if (keyPass == null) {
            keyPass = this.certificate.getCertificateKeystorePassword();
        }
        KeyStore ksUsed = ks = this.certificate.getCertificateKeystore();
        char[] keyPassArray = null;
        String keyPassToUse = null;
        if (keyPassFile != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ConfigFileLoader.getSource().getResource(keyPassFile).getInputStream(), StandardCharsets.UTF_8));){
                keyPassToUse = reader.readLine();
            }
        } else {
            keyPassToUse = keyPass;
        }
        if (keyPassToUse != null) {
            keyPassArray = keyPassToUse.toCharArray();
        }
        if ((kmf = KeyManagerFactory.getInstance(algorithm)).getProvider().getInfo().contains("FIPS")) {
            if (keyAlias != null) {
                log.warn((Object)sm.getString("sslUtilBase.aliasIgnored", new Object[]{keyAlias}));
            }
            kmf.init(ksUsed, keyPassArray);
            return kmf.getKeyManagers();
        }
        if (ks == null) {
            if (this.certificate.getCertificateFile() == null) {
                throw new IOException(sm.getString("sslUtilBase.noCertFile"));
            }
            PEMFile privateKeyFile = new PEMFile(this.certificate.getCertificateKeyFile() != null ? this.certificate.getCertificateKeyFile() : this.certificate.getCertificateFile(), keyPass, keyPassFile, null);
            PEMFile certificateFile = new PEMFile(this.certificate.getCertificateFile());
            ArrayList<X509Certificate> chain = new ArrayList<X509Certificate>(certificateFile.getCertificates());
            if (this.certificate.getCertificateChainFile() != null) {
                PEMFile certificateChainFile = new PEMFile(this.certificate.getCertificateChainFile());
                chain.addAll(certificateChainFile.getCertificates());
            }
            if (keyAlias == null) {
                keyAlias = DEFAULT_KEY_ALIAS;
            }
            ksUsed = KeyStore.getInstance("JKS");
            ksUsed.load(null, null);
            ksUsed.setKeyEntry(keyAlias, privateKeyFile.getPrivateKey(), keyPassArray, chain.toArray(new Certificate[0]));
        } else {
            Key k;
            if (keyAlias != null && !ks.isKeyEntry(keyAlias)) {
                throw new IOException(sm.getString("sslUtilBase.alias_no_key_entry", new Object[]{keyAlias}));
            }
            if (keyAlias == null) {
                Enumeration<String> aliases = ks.aliases();
                if (!aliases.hasMoreElements()) {
                    throw new IOException(sm.getString("sslUtilBase.noKeys"));
                }
                while (aliases.hasMoreElements() && keyAlias == null) {
                    keyAlias = aliases.nextElement();
                    if (ks.isKeyEntry(keyAlias)) continue;
                    keyAlias = null;
                }
                if (keyAlias == null) {
                    throw new IOException(sm.getString("sslUtilBase.alias_no_key_entry", new Object[]{null}));
                }
            }
            if ((k = ks.getKey(keyAlias, keyPassArray)) != null && !"DKS".equalsIgnoreCase(this.certificate.getCertificateKeystoreType()) && "PKCS#8".equalsIgnoreCase(k.getFormat())) {
                String provider = this.certificate.getCertificateKeystoreProvider();
                ksUsed = provider == null ? KeyStore.getInstance(this.certificate.getCertificateKeystoreType()) : KeyStore.getInstance(this.certificate.getCertificateKeystoreType(), provider);
                ksUsed.load(null, null);
                ksUsed.setKeyEntry(keyAlias, k, keyPassArray, ks.getCertificateChain(keyAlias));
            }
        }
        kmf.init(ksUsed, keyPassArray);
        KeyManager[] kms = kmf.getKeyManagers();
        if (kms != null && ksUsed == ks) {
            String alias = keyAlias;
            if ("JKS".equals(this.certificate.getCertificateKeystoreType())) {
                alias = alias.toLowerCase(Locale.ENGLISH);
            }
            for (int i = 0; i < kms.length; ++i) {
                kms[i] = new JSSEKeyManager((X509KeyManager)kms[i], alias);
            }
        }
        return kms;
    }

    @Override
    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    @Override
    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }

    @Override
    public TrustManager[] getTrustManagers() throws Exception {
        String className = this.sslHostConfig.getTrustManagerClassName();
        if (className != null && className.length() > 0) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> clazz = classLoader.loadClass(className);
            if (!TrustManager.class.isAssignableFrom(clazz)) {
                throw new InstantiationException(sm.getString("sslUtilBase.invalidTrustManagerClassName", new Object[]{className}));
            }
            Object trustManagerObject = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            TrustManager trustManager = (TrustManager)trustManagerObject;
            return new TrustManager[]{trustManager};
        }
        TrustManager[] tms = null;
        KeyStore trustStore = this.sslHostConfig.getTruststore();
        if (trustStore != null) {
            this.checkTrustStoreEntries(trustStore);
            String algorithm = this.sslHostConfig.getTruststoreAlgorithm();
            String crlf = this.sslHostConfig.getCertificateRevocationListFile();
            boolean revocationEnabled = this.sslHostConfig.getRevocationEnabled();
            if ("PKIX".equalsIgnoreCase(algorithm)) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                CertPathParameters params = this.getParameters(crlf, trustStore, revocationEnabled);
                CertPathTrustManagerParameters mfp = new CertPathTrustManagerParameters(params);
                tmf.init(mfp);
                tms = tmf.getTrustManagers();
            } else {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                tmf.init(trustStore);
                tms = tmf.getTrustManagers();
                if (crlf != null && crlf.length() > 0) {
                    throw new CRLException(sm.getString("sslUtilBase.noCrlSupport", new Object[]{algorithm}));
                }
                if (this.sslHostConfig.isCertificateVerificationDepthConfigured()) {
                    log.warn((Object)sm.getString("sslUtilBase.noVerificationDepth", new Object[]{algorithm}));
                }
            }
        }
        return tms;
    }

    private void checkTrustStoreEntries(KeyStore trustStore) throws Exception {
        Enumeration<String> aliases = trustStore.aliases();
        if (aliases != null) {
            Date now = new Date();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (!trustStore.isCertificateEntry(alias)) continue;
                Certificate cert = trustStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    try {
                        ((X509Certificate)cert).checkValidity(now);
                    }
                    catch (CertificateExpiredException | CertificateNotYetValidException e) {
                        String msg = sm.getString("sslUtilBase.trustedCertNotValid", new Object[]{alias, ((X509Certificate)cert).getSubjectX500Principal(), e.getMessage()});
                        if (log.isDebugEnabled()) {
                            log.warn((Object)msg, (Throwable)e);
                            continue;
                        }
                        log.warn((Object)msg);
                    }
                    continue;
                }
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("sslUtilBase.trustedCertNotChecked", new Object[]{alias}));
            }
        }
    }

    protected CertPathParameters getParameters(String crlf, KeyStore trustStore, boolean revocationEnabled) throws Exception {
        PKIXBuilderParameters xparams = new PKIXBuilderParameters(trustStore, (CertSelector)new X509CertSelector());
        if (crlf != null && crlf.length() > 0) {
            Collection<? extends CRL> crls = this.getCRLs(crlf);
            CollectionCertStoreParameters csp = new CollectionCertStoreParameters(crls);
            CertStore store = CertStore.getInstance("Collection", csp);
            xparams.addCertStore(store);
            xparams.setRevocationEnabled(true);
        } else {
            xparams.setRevocationEnabled(revocationEnabled);
        }
        xparams.setMaxPathLength(this.sslHostConfig.getCertificateVerificationDepth());
        return xparams;
    }

    protected Collection<? extends CRL> getCRLs(String crlf) throws IOException, CRLException, CertificateException {
        Collection<? extends CRL> crls = null;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream is = ConfigFileLoader.getSource().getResource(crlf).getInputStream();){
            crls = cf.generateCRLs(is);
        }
        return crls;
    }

    protected abstract Set<String> getImplementedProtocols();

    protected abstract Set<String> getImplementedCiphers();

    protected abstract Log getLog();

    protected abstract boolean isTls13RenegAuthAvailable();

    protected abstract SSLContext createSSLContextInternal(List<String> var1) throws Exception;
}


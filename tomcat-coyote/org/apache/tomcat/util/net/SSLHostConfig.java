/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.management.ObjectName;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import org.apache.tomcat.util.res.StringManager;

public class SSLHostConfig
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SSLHostConfig.class);
    private static final StringManager sm = StringManager.getManager(SSLHostConfig.class);
    protected static final String DEFAULT_SSL_HOST_NAME = "_default_";
    protected static final Set<String> SSL_PROTO_ALL_SET = new HashSet<String>();
    public static final String DEFAULT_TLS_CIPHERS = "HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA";
    private Type configType = null;
    private String hostName = "_default_";
    private volatile transient Long openSslConfContext = 0L;
    private volatile transient Long openSslContext = 0L;
    private boolean tls13RenegotiationAvailable = false;
    private String[] enabledCiphers;
    private String[] enabledProtocols;
    private ObjectName oname;
    private Set<String> explicitlyRequestedProtocols = new HashSet<String>();
    private SSLHostConfigCertificate defaultCertificate = null;
    private Set<SSLHostConfigCertificate> certificates = new LinkedHashSet<SSLHostConfigCertificate>(4);
    private String certificateRevocationListFile;
    private CertificateVerification certificateVerification = CertificateVerification.NONE;
    private int certificateVerificationDepth = 10;
    private boolean certificateVerificationDepthConfigured = false;
    private String ciphers = "HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA";
    private LinkedHashSet<Cipher> cipherList = null;
    private List<String> jsseCipherNames = null;
    private boolean honorCipherOrder = false;
    private Set<String> protocols = new HashSet<String>();
    private int sessionCacheSize = -1;
    private int sessionTimeout = 86400;
    private String keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
    private boolean revocationEnabled = false;
    private String sslProtocol = "TLS";
    private String trustManagerClassName;
    private String truststoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    private String truststoreFile = System.getProperty("javax.net.ssl.trustStore");
    private String truststorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    private String truststoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider");
    private String truststoreType = System.getProperty("javax.net.ssl.trustStoreType");
    private transient KeyStore truststore = null;
    private String certificateRevocationListPath;
    private String caCertificateFile;
    private String caCertificatePath;
    private boolean disableCompression = true;
    private boolean disableSessionTickets = false;
    private boolean insecureRenegotiation = false;
    private OpenSSLConf openSslConf = null;

    public SSLHostConfig() {
        this.setProtocols("all");
    }

    public boolean isTls13RenegotiationAvailable() {
        return this.tls13RenegotiationAvailable;
    }

    public void setTls13RenegotiationAvailable(boolean tls13RenegotiationAvailable) {
        this.tls13RenegotiationAvailable = tls13RenegotiationAvailable;
    }

    public Long getOpenSslConfContext() {
        return this.openSslConfContext;
    }

    public void setOpenSslConfContext(Long openSslConfContext) {
        this.openSslConfContext = openSslConfContext;
    }

    public Long getOpenSslContext() {
        return this.openSslContext;
    }

    public void setOpenSslContext(Long openSslContext) {
        this.openSslContext = openSslContext;
    }

    public String getConfigType() {
        return this.configType.name();
    }

    boolean setProperty(String name, Type configType) {
        if (this.configType == null) {
            this.configType = configType;
        } else if (configType != this.configType) {
            log.warn((Object)sm.getString("sslHostConfig.mismatch", new Object[]{name, this.getHostName(), configType, this.configType}));
            return false;
        }
        return true;
    }

    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    public void setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }

    public void setEnabledCiphers(String[] enabledCiphers) {
        this.enabledCiphers = enabledCiphers;
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    public void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    private void registerDefaultCertificate() {
        if (this.defaultCertificate == null) {
            SSLHostConfigCertificate defaultCertificate = new SSLHostConfigCertificate(this, SSLHostConfigCertificate.Type.UNDEFINED);
            this.addCertificate(defaultCertificate);
            this.defaultCertificate = defaultCertificate;
        }
    }

    public void addCertificate(SSLHostConfigCertificate certificate) {
        if (this.certificates.size() == 0) {
            this.certificates.add(certificate);
            return;
        }
        if (this.certificates.size() == 1 && this.certificates.iterator().next().getType() == SSLHostConfigCertificate.Type.UNDEFINED || certificate.getType() == SSLHostConfigCertificate.Type.UNDEFINED) {
            throw new IllegalArgumentException(sm.getString("sslHostConfig.certificate.notype"));
        }
        this.certificates.add(certificate);
    }

    public OpenSSLConf getOpenSslConf() {
        return this.openSslConf;
    }

    public void setOpenSslConf(OpenSSLConf conf) {
        if (conf == null) {
            throw new IllegalArgumentException(sm.getString("sslHostConfig.opensslconf.null"));
        }
        if (this.openSslConf != null) {
            throw new IllegalArgumentException(sm.getString("sslHostConfig.opensslconf.alreadySet"));
        }
        this.setProperty("<OpenSSLConf>", Type.OPENSSL);
        this.openSslConf = conf;
    }

    public Set<SSLHostConfigCertificate> getCertificates() {
        return this.getCertificates(false);
    }

    public Set<SSLHostConfigCertificate> getCertificates(boolean createDefaultIfEmpty) {
        if (this.certificates.size() == 0 && createDefaultIfEmpty) {
            this.registerDefaultCertificate();
        }
        return this.certificates;
    }

    public String getCertificateKeyPassword() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyPassword();
    }

    public void setCertificateKeyPassword(String certificateKeyPassword) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyPassword(certificateKeyPassword);
    }

    public String getCertificateKeyPasswordFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyPasswordFile();
    }

    public void setCertificateKeyPasswordFile(String certificateKeyPasswordFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyPasswordFile(certificateKeyPasswordFile);
    }

    public void setCertificateRevocationListFile(String certificateRevocationListFile) {
        this.certificateRevocationListFile = certificateRevocationListFile;
    }

    public String getCertificateRevocationListFile() {
        return this.certificateRevocationListFile;
    }

    public void setCertificateVerification(String certificateVerification) {
        try {
            this.certificateVerification = CertificateVerification.fromString(certificateVerification);
        }
        catch (IllegalArgumentException iae) {
            this.certificateVerification = CertificateVerification.REQUIRED;
            throw iae;
        }
    }

    public CertificateVerification getCertificateVerification() {
        return this.certificateVerification;
    }

    public void setCertificateVerificationAsString(String certificateVerification) {
        this.setCertificateVerification(certificateVerification);
    }

    public String getCertificateVerificationAsString() {
        return this.certificateVerification.toString();
    }

    public void setCertificateVerificationDepth(int certificateVerificationDepth) {
        this.certificateVerificationDepth = certificateVerificationDepth;
        this.certificateVerificationDepthConfigured = true;
    }

    public int getCertificateVerificationDepth() {
        return this.certificateVerificationDepth;
    }

    public boolean isCertificateVerificationDepthConfigured() {
        return this.certificateVerificationDepthConfigured;
    }

    public void setCiphers(String ciphersList) {
        if (ciphersList != null && !ciphersList.contains(":")) {
            String[] ciphers;
            StringBuilder sb = new StringBuilder();
            for (String cipher : ciphers = ciphersList.split(",")) {
                String trimmed = cipher.trim();
                if (trimmed.length() <= 0) continue;
                String openSSLName = OpenSSLCipherConfigurationParser.jsseToOpenSSL(trimmed);
                if (openSSLName == null) {
                    openSSLName = trimmed;
                }
                if (sb.length() > 0) {
                    sb.append(':');
                }
                sb.append(openSSLName);
            }
            this.ciphers = sb.toString();
        } else {
            this.ciphers = ciphersList;
        }
        this.cipherList = null;
        this.jsseCipherNames = null;
    }

    public String getCiphers() {
        return this.ciphers;
    }

    public LinkedHashSet<Cipher> getCipherList() {
        if (this.cipherList == null) {
            this.cipherList = OpenSSLCipherConfigurationParser.parse(this.getCiphers());
        }
        return this.cipherList;
    }

    public List<String> getJsseCipherNames() {
        if (this.jsseCipherNames == null) {
            this.jsseCipherNames = OpenSSLCipherConfigurationParser.convertForJSSE(this.getCipherList());
        }
        return this.jsseCipherNames;
    }

    public void setHonorCipherOrder(boolean honorCipherOrder) {
        this.honorCipherOrder = honorCipherOrder;
    }

    public boolean getHonorCipherOrder() {
        return this.honorCipherOrder;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName.toLowerCase(Locale.ENGLISH);
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setProtocols(String input) {
        this.protocols.clear();
        this.explicitlyRequestedProtocols.clear();
        for (String value : input.split("(?=[-+,])")) {
            String trimmed = value.trim();
            if (trimmed.length() <= 1) continue;
            if (trimmed.charAt(0) == '+') {
                if ((trimmed = trimmed.substring(1).trim()).equalsIgnoreCase("all")) {
                    this.protocols.addAll(SSL_PROTO_ALL_SET);
                    continue;
                }
                this.protocols.add(trimmed);
                this.explicitlyRequestedProtocols.add(trimmed);
                continue;
            }
            if (trimmed.charAt(0) == '-') {
                if ((trimmed = trimmed.substring(1).trim()).equalsIgnoreCase("all")) {
                    this.protocols.removeAll(SSL_PROTO_ALL_SET);
                    continue;
                }
                this.protocols.remove(trimmed);
                this.explicitlyRequestedProtocols.remove(trimmed);
                continue;
            }
            if (trimmed.charAt(0) == ',') {
                trimmed = trimmed.substring(1).trim();
            }
            if (!this.protocols.isEmpty()) {
                log.warn((Object)sm.getString("sslHostConfig.prefix_missing", new Object[]{trimmed, this.getHostName()}));
            }
            if (trimmed.equalsIgnoreCase("all")) {
                this.protocols.addAll(SSL_PROTO_ALL_SET);
                continue;
            }
            this.protocols.add(trimmed);
            this.explicitlyRequestedProtocols.add(trimmed);
        }
    }

    public Set<String> getProtocols() {
        return this.protocols;
    }

    boolean isExplicitlyRequestedProtocol(String protocol) {
        return this.explicitlyRequestedProtocols.contains(protocol);
    }

    public void setSessionCacheSize(int sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
    }

    public int getSessionCacheSize() {
        return this.sessionCacheSize;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    public String getCertificateKeyAlias() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyAlias();
    }

    public void setCertificateKeyAlias(String certificateKeyAlias) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyAlias(certificateKeyAlias);
    }

    public String getCertificateKeystoreFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystoreFile();
    }

    public void setCertificateKeystoreFile(String certificateKeystoreFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystoreFile(certificateKeystoreFile);
    }

    public String getCertificateKeystorePassword() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystorePassword();
    }

    public void setCertificateKeystorePassword(String certificateKeystorePassword) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystorePassword(certificateKeystorePassword);
    }

    public String getCertificateKeystorePasswordFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystorePasswordFile();
    }

    public void setCertificateKeystorePasswordFile(String certificateKeystorePasswordFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystorePasswordFile(certificateKeystorePasswordFile);
    }

    public String getCertificateKeystoreProvider() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystoreProvider();
    }

    public void setCertificateKeystoreProvider(String certificateKeystoreProvider) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystoreProvider(certificateKeystoreProvider);
    }

    public String getCertificateKeystoreType() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystoreType();
    }

    public void setCertificateKeystoreType(String certificateKeystoreType) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystoreType(certificateKeystoreType);
    }

    public void setKeyManagerAlgorithm(String keyManagerAlgorithm) {
        this.setProperty("keyManagerAlgorithm", Type.JSSE);
        this.keyManagerAlgorithm = keyManagerAlgorithm;
    }

    public String getKeyManagerAlgorithm() {
        return this.keyManagerAlgorithm;
    }

    public void setRevocationEnabled(boolean revocationEnabled) {
        this.setProperty("revocationEnabled", Type.JSSE);
        this.revocationEnabled = revocationEnabled;
    }

    public boolean getRevocationEnabled() {
        return this.revocationEnabled;
    }

    public void setSslProtocol(String sslProtocol) {
        this.setProperty("sslProtocol", Type.JSSE);
        this.sslProtocol = sslProtocol;
    }

    public String getSslProtocol() {
        return this.sslProtocol;
    }

    public void setTrustManagerClassName(String trustManagerClassName) {
        this.setProperty("trustManagerClassName", Type.JSSE);
        this.trustManagerClassName = trustManagerClassName;
    }

    public String getTrustManagerClassName() {
        return this.trustManagerClassName;
    }

    public void setTruststoreAlgorithm(String truststoreAlgorithm) {
        this.setProperty("truststoreAlgorithm", Type.JSSE);
        this.truststoreAlgorithm = truststoreAlgorithm;
    }

    public String getTruststoreAlgorithm() {
        return this.truststoreAlgorithm;
    }

    public void setTruststoreFile(String truststoreFile) {
        this.setProperty("truststoreFile", Type.JSSE);
        this.truststoreFile = truststoreFile;
    }

    public String getTruststoreFile() {
        return this.truststoreFile;
    }

    public void setTruststorePassword(String truststorePassword) {
        this.setProperty("truststorePassword", Type.JSSE);
        this.truststorePassword = truststorePassword;
    }

    public String getTruststorePassword() {
        return this.truststorePassword;
    }

    public void setTruststoreProvider(String truststoreProvider) {
        this.setProperty("truststoreProvider", Type.JSSE);
        this.truststoreProvider = truststoreProvider;
    }

    public String getTruststoreProvider() {
        if (this.truststoreProvider == null) {
            Set<SSLHostConfigCertificate> certificates = this.getCertificates();
            if (certificates.size() == 1) {
                return certificates.iterator().next().getCertificateKeystoreProvider();
            }
            return SSLHostConfigCertificate.DEFAULT_KEYSTORE_PROVIDER;
        }
        return this.truststoreProvider;
    }

    public void setTruststoreType(String truststoreType) {
        this.setProperty("truststoreType", Type.JSSE);
        this.truststoreType = truststoreType;
    }

    public String getTruststoreType() {
        if (this.truststoreType == null) {
            String keystoreType;
            Set<SSLHostConfigCertificate> certificates = this.getCertificates();
            if (certificates.size() == 1 && !"PKCS12".equalsIgnoreCase(keystoreType = certificates.iterator().next().getCertificateKeystoreType())) {
                return keystoreType;
            }
            return SSLHostConfigCertificate.DEFAULT_KEYSTORE_TYPE;
        }
        return this.truststoreType;
    }

    public void setTrustStore(KeyStore truststore) {
        this.truststore = truststore;
    }

    public KeyStore getTruststore() throws IOException {
        KeyStore result = this.truststore;
        if (result == null && this.truststoreFile != null) {
            try {
                result = SSLUtilBase.getStore(this.getTruststoreType(), this.getTruststoreProvider(), this.getTruststoreFile(), this.getTruststorePassword(), null);
            }
            catch (IOException ioe) {
                Throwable cause = ioe.getCause();
                if (cause instanceof UnrecoverableKeyException) {
                    log.warn((Object)sm.getString("sslHostConfig.invalid_truststore_password"), cause);
                    result = SSLUtilBase.getStore(this.getTruststoreType(), this.getTruststoreProvider(), this.getTruststoreFile(), null, null);
                }
                throw ioe;
            }
        }
        return result;
    }

    public String getCertificateChainFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateChainFile();
    }

    public void setCertificateChainFile(String certificateChainFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateChainFile(certificateChainFile);
    }

    public String getCertificateFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateFile();
    }

    public void setCertificateFile(String certificateFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateFile(certificateFile);
    }

    public String getCertificateKeyFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyFile();
    }

    public void setCertificateKeyFile(String certificateKeyFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyFile(certificateKeyFile);
    }

    public void setCertificateRevocationListPath(String certificateRevocationListPath) {
        this.setProperty("certificateRevocationListPath", Type.OPENSSL);
        this.certificateRevocationListPath = certificateRevocationListPath;
    }

    public String getCertificateRevocationListPath() {
        return this.certificateRevocationListPath;
    }

    public void setCaCertificateFile(String caCertificateFile) {
        if (this.setProperty("caCertificateFile", Type.OPENSSL) && this.truststoreFile != null) {
            this.truststoreFile = null;
        }
        this.caCertificateFile = caCertificateFile;
    }

    public String getCaCertificateFile() {
        return this.caCertificateFile;
    }

    public void setCaCertificatePath(String caCertificatePath) {
        if (this.setProperty("caCertificatePath", Type.OPENSSL) && this.truststoreFile != null) {
            this.truststoreFile = null;
        }
        this.caCertificatePath = caCertificatePath;
    }

    public String getCaCertificatePath() {
        return this.caCertificatePath;
    }

    public void setDisableCompression(boolean disableCompression) {
        this.setProperty("disableCompression", Type.OPENSSL);
        this.disableCompression = disableCompression;
    }

    public boolean getDisableCompression() {
        return this.disableCompression;
    }

    public void setDisableSessionTickets(boolean disableSessionTickets) {
        this.setProperty("disableSessionTickets", Type.OPENSSL);
        this.disableSessionTickets = disableSessionTickets;
    }

    public boolean getDisableSessionTickets() {
        return this.disableSessionTickets;
    }

    public void setInsecureRenegotiation(boolean insecureRenegotiation) {
        this.setProperty("insecureRenegotiation", Type.OPENSSL);
        this.insecureRenegotiation = insecureRenegotiation;
    }

    public boolean getInsecureRenegotiation() {
        return this.insecureRenegotiation;
    }

    public Set<X509Certificate> certificatesExpiringBefore(Date date) {
        HashSet<X509Certificate> result = new HashSet<X509Certificate>();
        Set<SSLHostConfigCertificate> sslHostConfigCertificates = this.getCertificates();
        for (SSLHostConfigCertificate sslHostConfigCertificate : sslHostConfigCertificates) {
            X509Certificate certificate;
            Date expirationDate;
            X509Certificate[] certificates;
            SSLContext sslContext = sslHostConfigCertificate.getSslContext();
            if (sslContext == null) continue;
            String alias = sslHostConfigCertificate.getCertificateKeyAlias();
            if (alias == null) {
                alias = "tomcat";
            }
            if ((certificates = sslContext.getCertificateChain(alias)) == null || certificates.length <= 0 || !date.after(expirationDate = (certificate = certificates[0]).getNotAfter())) continue;
            result.add(certificate);
        }
        return result;
    }

    public static String adjustRelativePath(String path) throws FileNotFoundException {
        if (path == null || path.length() == 0) {
            return path;
        }
        String newPath = path;
        File f = new File(newPath);
        if (!f.isAbsolute()) {
            newPath = System.getProperty("catalina.base") + File.separator + newPath;
            f = new File(newPath);
        }
        if (!f.exists()) {
            throw new FileNotFoundException(sm.getString("sslHostConfig.fileNotFound", new Object[]{newPath}));
        }
        return newPath;
    }

    static {
        SSL_PROTO_ALL_SET.add("SSLv2Hello");
        SSL_PROTO_ALL_SET.add("TLSv1");
        SSL_PROTO_ALL_SET.add("TLSv1.1");
        SSL_PROTO_ALL_SET.add("TLSv1.2");
        SSL_PROTO_ALL_SET.add("TLSv1.3");
    }

    public static enum Type {
        JSSE,
        OPENSSL;

    }

    public static enum CertificateVerification {
        NONE(false),
        OPTIONAL_NO_CA(true),
        OPTIONAL(true),
        REQUIRED(false);

        private final boolean optional;

        private CertificateVerification(boolean optional) {
            this.optional = optional;
        }

        public boolean isOptional() {
            return this.optional;
        }

        public static CertificateVerification fromString(String value) {
            if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "require".equalsIgnoreCase(value) || "required".equalsIgnoreCase(value)) {
                return REQUIRED;
            }
            if ("optional".equalsIgnoreCase(value) || "want".equalsIgnoreCase(value)) {
                return OPTIONAL;
            }
            if ("optionalNoCA".equalsIgnoreCase(value) || "optional_no_ca".equalsIgnoreCase(value)) {
                return OPTIONAL_NO_CA;
            }
            if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "none".equalsIgnoreCase(value)) {
                return NONE;
            }
            throw new IllegalArgumentException(sm.getString("sslHostConfig.certificateVerificationInvalid", new Object[]{value}));
        }
    }
}


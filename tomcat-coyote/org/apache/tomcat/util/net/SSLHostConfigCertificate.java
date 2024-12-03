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
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.management.ObjectName;
import javax.net.ssl.X509KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.apache.tomcat.util.res.StringManager;

public class SSLHostConfigCertificate
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SSLHostConfigCertificate.class);
    private static final StringManager sm = StringManager.getManager(SSLHostConfigCertificate.class);
    public static final Type DEFAULT_TYPE = Type.UNDEFINED;
    static final String DEFAULT_KEYSTORE_PROVIDER = System.getProperty("javax.net.ssl.keyStoreProvider");
    static final String DEFAULT_KEYSTORE_TYPE = System.getProperty("javax.net.ssl.keyStoreType", "JKS");
    private static final String DEFAULT_KEYSTORE_FILE = System.getProperty("user.home") + File.separator + ".keystore";
    private static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";
    private ObjectName oname;
    private volatile transient SSLContext sslContext;
    private final SSLHostConfig sslHostConfig;
    private final Type type;
    private String certificateKeyPassword = null;
    private String certificateKeyPasswordFile = null;
    private String certificateKeyAlias;
    private String certificateKeystorePassword = "changeit";
    private String certificateKeystorePasswordFile = null;
    private String certificateKeystoreFile = DEFAULT_KEYSTORE_FILE;
    private String certificateKeystoreProvider = DEFAULT_KEYSTORE_PROVIDER;
    private String certificateKeystoreType = DEFAULT_KEYSTORE_TYPE;
    private transient KeyStore certificateKeystore = null;
    private transient X509KeyManager certificateKeyManager = null;
    private String certificateChainFile;
    private String certificateFile;
    private String certificateKeyFile;
    private StoreType storeType = null;

    public SSLHostConfigCertificate() {
        this(null, Type.UNDEFINED);
    }

    public SSLHostConfigCertificate(SSLHostConfig sslHostConfig, Type type) {
        this.sslHostConfig = sslHostConfig;
        this.type = type;
    }

    public SSLContext getSslContext() {
        return this.sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public SSLHostConfig getSSLHostConfig() {
        return this.sslHostConfig;
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    public void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    public Type getType() {
        return this.type;
    }

    public String getCertificateKeyPassword() {
        return this.certificateKeyPassword;
    }

    public void setCertificateKeyPassword(String certificateKeyPassword) {
        this.certificateKeyPassword = certificateKeyPassword;
    }

    public String getCertificateKeyPasswordFile() {
        return this.certificateKeyPasswordFile;
    }

    public void setCertificateKeyPasswordFile(String certificateKeyPasswordFile) {
        this.certificateKeyPasswordFile = certificateKeyPasswordFile;
    }

    public void setCertificateKeyAlias(String certificateKeyAlias) {
        this.sslHostConfig.setProperty("Certificate.certificateKeyAlias", SSLHostConfig.Type.JSSE);
        this.certificateKeyAlias = certificateKeyAlias;
    }

    public String getCertificateKeyAlias() {
        return this.certificateKeyAlias;
    }

    public void setCertificateKeystoreFile(String certificateKeystoreFile) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystoreFile", SSLHostConfig.Type.JSSE);
        this.setStoreType("Certificate.certificateKeystoreFile", StoreType.KEYSTORE);
        this.certificateKeystoreFile = certificateKeystoreFile;
    }

    public String getCertificateKeystoreFile() {
        return this.certificateKeystoreFile;
    }

    public void setCertificateKeystorePassword(String certificateKeystorePassword) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystorePassword", SSLHostConfig.Type.JSSE);
        this.setStoreType("Certificate.certificateKeystorePassword", StoreType.KEYSTORE);
        this.certificateKeystorePassword = certificateKeystorePassword;
    }

    public String getCertificateKeystorePassword() {
        return this.certificateKeystorePassword;
    }

    public void setCertificateKeystorePasswordFile(String certificateKeystorePasswordFile) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystorePasswordFile", SSLHostConfig.Type.JSSE);
        this.setStoreType("Certificate.certificateKeystorePasswordFile", StoreType.KEYSTORE);
        this.certificateKeystorePasswordFile = certificateKeystorePasswordFile;
    }

    public String getCertificateKeystorePasswordFile() {
        return this.certificateKeystorePasswordFile;
    }

    public void setCertificateKeystoreProvider(String certificateKeystoreProvider) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystoreProvider", SSLHostConfig.Type.JSSE);
        this.setStoreType("Certificate.certificateKeystoreProvider", StoreType.KEYSTORE);
        this.certificateKeystoreProvider = certificateKeystoreProvider;
    }

    public String getCertificateKeystoreProvider() {
        return this.certificateKeystoreProvider;
    }

    public void setCertificateKeystoreType(String certificateKeystoreType) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystoreType", SSLHostConfig.Type.JSSE);
        this.setStoreType("Certificate.certificateKeystoreType", StoreType.KEYSTORE);
        this.certificateKeystoreType = certificateKeystoreType;
    }

    public String getCertificateKeystoreType() {
        return this.certificateKeystoreType;
    }

    public void setCertificateKeystore(KeyStore certificateKeystore) {
        this.certificateKeystore = certificateKeystore;
        if (certificateKeystore != null) {
            this.setCertificateKeystoreType(certificateKeystore.getType());
        }
    }

    public KeyStore getCertificateKeystore() throws IOException {
        KeyStore result = this.certificateKeystore;
        if (result == null && this.storeType == StoreType.KEYSTORE) {
            result = SSLUtilBase.getStore(this.getCertificateKeystoreType(), this.getCertificateKeystoreProvider(), this.getCertificateKeystoreFile(), this.getCertificateKeystorePassword(), this.getCertificateKeystorePasswordFile());
        }
        return result;
    }

    public void setCertificateKeyManager(X509KeyManager certificateKeyManager) {
        this.certificateKeyManager = certificateKeyManager;
    }

    public X509KeyManager getCertificateKeyManager() {
        return this.certificateKeyManager;
    }

    public void setCertificateChainFile(String certificateChainFile) {
        this.setStoreType("Certificate.certificateChainFile", StoreType.PEM);
        this.certificateChainFile = certificateChainFile;
    }

    public String getCertificateChainFile() {
        return this.certificateChainFile;
    }

    public void setCertificateFile(String certificateFile) {
        this.setStoreType("Certificate.certificateFile", StoreType.PEM);
        this.certificateFile = certificateFile;
    }

    public String getCertificateFile() {
        return this.certificateFile;
    }

    public void setCertificateKeyFile(String certificateKeyFile) {
        this.setStoreType("Certificate.certificateKeyFile", StoreType.PEM);
        this.certificateKeyFile = certificateKeyFile;
    }

    public String getCertificateKeyFile() {
        return this.certificateKeyFile;
    }

    private void setStoreType(String name, StoreType type) {
        if (this.storeType == null) {
            this.storeType = type;
        } else if (this.storeType != type) {
            log.warn((Object)sm.getString("sslHostConfigCertificate.mismatch", new Object[]{name, this.sslHostConfig.getHostName(), type, this.storeType}));
        }
    }

    StoreType getStoreType() {
        return this.storeType;
    }

    public static enum Type {
        UNDEFINED(new Authentication[0]),
        RSA(Authentication.RSA),
        DSA(Authentication.DSS),
        EC(Authentication.ECDH, Authentication.ECDSA);

        private final Set<Authentication> compatibleAuthentications = new HashSet<Authentication>();

        private Type(Authentication ... authentications) {
            if (authentications != null) {
                this.compatibleAuthentications.addAll(Arrays.asList(authentications));
            }
        }

        public boolean isCompatibleWith(Authentication au) {
            return this.compatibleAuthentications.contains((Object)au);
        }
    }

    static enum StoreType {
        KEYSTORE,
        PEM;

    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.openssl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.util.List;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import org.apache.tomcat.util.net.openssl.OpenSSLContext;
import org.apache.tomcat.util.net.openssl.OpenSSLEngine;
import org.apache.tomcat.util.res.StringManager;

public class OpenSSLUtil
extends SSLUtilBase {
    private static final Log log = LogFactory.getLog(OpenSSLUtil.class);
    private static final StringManager sm = StringManager.getManager(OpenSSLUtil.class);

    public OpenSSLUtil(SSLHostConfigCertificate certificate) {
        super(certificate);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    protected Set<String> getImplementedProtocols() {
        return OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET;
    }

    @Override
    protected Set<String> getImplementedCiphers() {
        return OpenSSLEngine.AVAILABLE_CIPHER_SUITES;
    }

    @Override
    protected boolean isTls13RenegAuthAvailable() {
        return true;
    }

    @Override
    public SSLContext createSSLContextInternal(List<String> negotiableProtocols) throws Exception {
        return new OpenSSLContext(this.certificate, negotiableProtocols);
    }

    @Deprecated
    public static X509KeyManager chooseKeyManager(KeyManager[] managers) throws Exception {
        return OpenSSLUtil.chooseKeyManager(managers, true);
    }

    public static X509KeyManager chooseKeyManager(KeyManager[] managers, boolean throwOnMissing) throws Exception {
        if (managers == null) {
            return null;
        }
        for (KeyManager manager : managers) {
            if (!(manager instanceof JSSEKeyManager)) continue;
            return (JSSEKeyManager)manager;
        }
        for (KeyManager manager : managers) {
            if (!(manager instanceof X509KeyManager)) continue;
            return (X509KeyManager)manager;
        }
        if (throwOnMissing) {
            throw new IllegalStateException(sm.getString("openssl.keyManagerMissing"));
        }
        log.warn((Object)sm.getString("openssl.keyManagerMissing.warn"));
        return null;
    }

    @Override
    public KeyManager[] getKeyManagers() throws Exception {
        try {
            return super.getKeyManagers();
        }
        catch (IllegalArgumentException e) {
            String msg = sm.getString("openssl.nonJsseChain", new Object[]{this.certificate.getCertificateChainFile()});
            if (log.isDebugEnabled()) {
                log.info((Object)msg, (Throwable)e);
            } else {
                log.info((Object)msg);
            }
            return null;
        }
        catch (IOException | KeyStoreException e) {
            if (this.certificate.getCertificateFile() != null) {
                String msg = sm.getString("openssl.nonJsseCertificate", new Object[]{this.certificate.getCertificateFile(), this.certificate.getCertificateKeyFile()});
                if (log.isDebugEnabled()) {
                    log.info((Object)msg, (Throwable)e);
                } else {
                    log.info((Object)msg);
                }
                return null;
            }
            throw e;
        }
    }
}


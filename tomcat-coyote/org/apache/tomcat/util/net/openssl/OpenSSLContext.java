/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.jni.CertificateVerifier
 *  org.apache.tomcat.jni.Pool
 *  org.apache.tomcat.jni.SSL
 *  org.apache.tomcat.jni.SSLConf
 *  org.apache.tomcat.jni.SSLContext
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.openssl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.CertificateVerifier;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLConf;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import org.apache.tomcat.util.net.openssl.OpenSSLConfCmd;
import org.apache.tomcat.util.net.openssl.OpenSSLEngine;
import org.apache.tomcat.util.net.openssl.OpenSSLSessionContext;
import org.apache.tomcat.util.net.openssl.OpenSSLUtil;
import org.apache.tomcat.util.net.openssl.OpenSSLX509Certificate;
import org.apache.tomcat.util.res.StringManager;

public class OpenSSLContext
implements SSLContext {
    private static final Log log = LogFactory.getLog(OpenSSLContext.class);
    private static final StringManager netSm = StringManager.getManager(AbstractEndpoint.class);
    private static final StringManager sm = StringManager.getManager(OpenSSLContext.class);
    private static final String defaultProtocol = "TLS";
    private static final String BEGIN_KEY = "-----BEGIN PRIVATE KEY-----\n";
    private static final Object END_KEY = "\n-----END PRIVATE KEY-----";
    static final CertificateFactory X509_CERT_FACTORY;
    private final SSLHostConfig sslHostConfig;
    private final SSLHostConfigCertificate certificate;
    private final List<String> negotiableProtocols;
    private final long aprPool;
    private final AtomicInteger aprPoolDestroyed = new AtomicInteger(0);
    protected final long cctx;
    protected final long ctx;
    private OpenSSLSessionContext sessionContext;
    private X509TrustManager x509TrustManager;
    private String enabledProtocol;
    private boolean initialized = false;

    public OpenSSLContext(SSLHostConfigCertificate certificate, List<String> negotiableProtocols) throws SSLException {
        this.sslHostConfig = certificate.getSSLHostConfig();
        this.certificate = certificate;
        this.aprPool = Pool.create((long)0L);
        boolean success = false;
        try {
            OpenSSLConf openSslConf = this.sslHostConfig.getOpenSslConf();
            if (openSslConf != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("openssl.makeConf"));
                    }
                    this.cctx = SSLConf.make((long)this.aprPool, (int)58);
                }
                catch (Exception e) {
                    throw new SSLException(sm.getString("openssl.errMakeConf"), e);
                }
            } else {
                this.cctx = 0L;
            }
            this.sslHostConfig.setOpenSslConfContext(this.cctx);
            int value = 0;
            for (String protocol : this.sslHostConfig.getEnabledProtocols()) {
                if ("SSLv2Hello".equalsIgnoreCase(protocol)) continue;
                if ("SSLv2".equalsIgnoreCase(protocol)) {
                    value |= 1;
                    continue;
                }
                if ("SSLv3".equalsIgnoreCase(protocol)) {
                    value |= 2;
                    continue;
                }
                if ("TLSv1".equalsIgnoreCase(protocol)) {
                    value |= 4;
                    continue;
                }
                if ("TLSv1.1".equalsIgnoreCase(protocol)) {
                    value |= 8;
                    continue;
                }
                if ("TLSv1.2".equalsIgnoreCase(protocol)) {
                    value |= 0x10;
                    continue;
                }
                if ("TLSv1.3".equalsIgnoreCase(protocol)) {
                    value |= 0x20;
                    continue;
                }
                if ("all".equalsIgnoreCase(protocol)) {
                    value |= SSL.SSL_PROTOCOL_ALL;
                    continue;
                }
                throw new Exception(netSm.getString("endpoint.apr.invalidSslProtocol", new Object[]{protocol}));
            }
            try {
                this.ctx = org.apache.tomcat.jni.SSLContext.make((long)this.aprPool, (int)value, (int)1);
            }
            catch (Exception e) {
                throw new Exception(netSm.getString("endpoint.apr.failSslContextMake"), e);
            }
            this.negotiableProtocols = negotiableProtocols;
            success = true;
        }
        catch (Exception e) {
            throw new SSLException(sm.getString("openssl.errorSSLCtxInit"), e);
        }
        finally {
            if (!success) {
                this.destroy();
            }
        }
    }

    public String getEnabledProtocol() {
        return this.enabledProtocol;
    }

    public void setEnabledProtocol(String protocol) {
        this.enabledProtocol = protocol == null ? defaultProtocol : protocol;
    }

    @Override
    public synchronized void destroy() {
        if (this.aprPoolDestroyed.compareAndSet(0, 1)) {
            if (this.ctx != 0L) {
                org.apache.tomcat.jni.SSLContext.free((long)this.ctx);
            }
            if (this.cctx != 0L) {
                SSLConf.free((long)this.cctx);
            }
            if (this.aprPool != 0L) {
                Pool.destroy((long)this.aprPool);
            }
        }
    }

    protected static boolean checkConf(OpenSSLConf conf, long cctx) throws Exception {
        boolean result = true;
        Iterator<OpenSSLConfCmd> iterator = conf.getCommands().iterator();
        while (iterator.hasNext()) {
            int rc;
            OpenSSLConfCmd command;
            OpenSSLConfCmd cmd = command = iterator.next();
            String name = cmd.getName();
            String value = cmd.getValue();
            if (name == null) {
                log.error((Object)sm.getString("opensslconf.noCommandName", new Object[]{value}));
                result = false;
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("opensslconf.checkCommand", new Object[]{name, value}));
            }
            try {
                rc = SSLConf.check((long)cctx, (String)name, (String)value);
            }
            catch (Exception e) {
                log.error((Object)sm.getString("opensslconf.checkFailed"));
                return false;
            }
            if (rc <= 0) {
                log.error((Object)sm.getString("opensslconf.failedCommand", new Object[]{name, value, Integer.toString(rc)}));
                result = false;
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)sm.getString("opensslconf.resultCommand", new Object[]{name, value, Integer.toString(rc)}));
        }
        if (!result) {
            log.error((Object)sm.getString("opensslconf.checkFailed"));
        }
        return result;
    }

    protected static boolean applyConf(OpenSSLConf conf, long cctx, long ctx) throws Exception {
        int rc;
        boolean result = true;
        SSLConf.assign((long)cctx, (long)ctx);
        Iterator<OpenSSLConfCmd> iterator = conf.getCommands().iterator();
        while (iterator.hasNext()) {
            OpenSSLConfCmd command;
            OpenSSLConfCmd cmd = command = iterator.next();
            String name = cmd.getName();
            String value = cmd.getValue();
            if (name == null) {
                log.error((Object)sm.getString("opensslconf.noCommandName", new Object[]{value}));
                result = false;
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("opensslconf.applyCommand", new Object[]{name, value}));
            }
            try {
                rc = SSLConf.apply((long)cctx, (String)name, (String)value);
            }
            catch (Exception e) {
                log.error((Object)sm.getString("opensslconf.applyFailed"));
                return false;
            }
            if (rc <= 0) {
                log.error((Object)sm.getString("opensslconf.failedCommand", new Object[]{name, value, Integer.toString(rc)}));
                result = false;
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)sm.getString("opensslconf.resultCommand", new Object[]{name, value, Integer.toString(rc)}));
        }
        rc = SSLConf.finish((long)cctx);
        if (rc <= 0) {
            log.error((Object)sm.getString("opensslconf.finishFailed", new Object[]{Integer.toString(rc)}));
            result = false;
        }
        if (!result) {
            log.error((Object)sm.getString("opensslconf.applyFailed"));
        }
        return result;
    }

    @Override
    public synchronized void init(KeyManager[] kms, TrustManager[] tms, SecureRandom sr) {
        if (this.initialized) {
            log.warn((Object)sm.getString("openssl.doubleInit"));
            return;
        }
        try {
            OpenSSLConf openSslConf;
            if (this.sslHostConfig.getInsecureRenegotiation()) {
                org.apache.tomcat.jni.SSLContext.setOptions((long)this.ctx, (int)262144);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions((long)this.ctx, (int)262144);
            }
            if (this.sslHostConfig.getHonorCipherOrder()) {
                org.apache.tomcat.jni.SSLContext.setOptions((long)this.ctx, (int)0x400000);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions((long)this.ctx, (int)0x400000);
            }
            if (this.sslHostConfig.getDisableCompression()) {
                org.apache.tomcat.jni.SSLContext.setOptions((long)this.ctx, (int)131072);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions((long)this.ctx, (int)131072);
            }
            if (this.sslHostConfig.getDisableSessionTickets()) {
                org.apache.tomcat.jni.SSLContext.setOptions((long)this.ctx, (int)16384);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions((long)this.ctx, (int)16384);
            }
            org.apache.tomcat.jni.SSLContext.setCipherSuite((long)this.ctx, (String)this.sslHostConfig.getCiphers());
            this.certificate.setCertificateKeyManager(OpenSSLUtil.chooseKeyManager(kms, this.certificate.getCertificateFile() == null));
            this.addCertificate(this.certificate);
            int value = 0;
            switch (this.sslHostConfig.getCertificateVerification()) {
                case NONE: {
                    value = 0;
                    break;
                }
                case OPTIONAL: {
                    value = 1;
                    break;
                }
                case OPTIONAL_NO_CA: {
                    value = 3;
                    break;
                }
                case REQUIRED: {
                    value = 2;
                }
            }
            org.apache.tomcat.jni.SSLContext.setVerify((long)this.ctx, (int)value, (int)this.sslHostConfig.getCertificateVerificationDepth());
            if (tms != null) {
                this.x509TrustManager = OpenSSLContext.chooseTrustManager(tms);
                org.apache.tomcat.jni.SSLContext.setCertVerifyCallback((long)this.ctx, (CertificateVerifier)new CertificateVerifier(){

                    public boolean verify(long ssl, byte[][] chain, String auth) {
                        X509Certificate[] peerCerts = OpenSSLContext.certificates(chain);
                        try {
                            OpenSSLContext.this.x509TrustManager.checkClientTrusted(peerCerts, auth);
                            return true;
                        }
                        catch (Exception e) {
                            log.debug((Object)sm.getString("openssl.certificateVerificationFailed"), (Throwable)e);
                            return false;
                        }
                    }
                });
                for (X509Certificate caCert : this.x509TrustManager.getAcceptedIssuers()) {
                    org.apache.tomcat.jni.SSLContext.addClientCACertificateRaw((long)this.ctx, (byte[])caCert.getEncoded());
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)sm.getString("openssl.addedClientCaCert", new Object[]{caCert.toString()}));
                }
            } else {
                org.apache.tomcat.jni.SSLContext.setCACertificate((long)this.ctx, (String)SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificateFile()), (String)SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificatePath()));
            }
            if (this.negotiableProtocols != null && this.negotiableProtocols.size() > 0) {
                ArrayList<String> protocols = new ArrayList<String>(this.negotiableProtocols);
                protocols.add("http/1.1");
                String[] protocolsArray = protocols.toArray(new String[0]);
                org.apache.tomcat.jni.SSLContext.setAlpnProtos((long)this.ctx, (String[])protocolsArray, (int)0);
            }
            if ((openSslConf = this.sslHostConfig.getOpenSslConf()) != null && this.cctx != 0L) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("openssl.checkConf"));
                }
                try {
                    if (!OpenSSLContext.checkConf(openSslConf, this.cctx)) {
                        log.error((Object)sm.getString("openssl.errCheckConf"));
                        throw new Exception(sm.getString("openssl.errCheckConf"));
                    }
                }
                catch (Exception e) {
                    throw new Exception(sm.getString("openssl.errCheckConf"), e);
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("openssl.applyConf"));
                }
                try {
                    if (!OpenSSLContext.applyConf(openSslConf, this.cctx, this.ctx)) {
                        log.error((Object)sm.getString("openssl.errApplyConf"));
                        throw new SSLException(sm.getString("openssl.errApplyConf"));
                    }
                }
                catch (Exception e) {
                    throw new SSLException(sm.getString("openssl.errApplyConf"), e);
                }
                int opts = org.apache.tomcat.jni.SSLContext.getOptions((long)this.ctx);
                ArrayList<String> enabled = new ArrayList<String>();
                enabled.add("SSLv2Hello");
                if ((opts & 0x4000000) == 0) {
                    enabled.add("TLSv1");
                }
                if ((opts & 0x10000000) == 0) {
                    enabled.add("TLSv1.1");
                }
                if ((opts & 0x8000000) == 0) {
                    enabled.add("TLSv1.2");
                }
                if ((opts & 0x1000000) == 0) {
                    enabled.add("SSLv2");
                }
                if ((opts & 0x2000000) == 0) {
                    enabled.add("SSLv3");
                }
                this.sslHostConfig.setEnabledProtocols(enabled.toArray(new String[0]));
                this.sslHostConfig.setEnabledCiphers(org.apache.tomcat.jni.SSLContext.getCiphers((long)this.ctx));
            }
            this.sessionContext = new OpenSSLSessionContext(this);
            this.sessionContext.setSessionIdContext(org.apache.tomcat.jni.SSLContext.DEFAULT_SESSION_ID_CONTEXT);
            this.sslHostConfig.setOpenSslContext(this.ctx);
            this.initialized = true;
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("openssl.errorSSLCtxInit"), (Throwable)e);
            this.destroy();
        }
    }

    public void addCertificate(SSLHostConfigCertificate certificate) throws Exception {
        if (certificate.getCertificateFile() != null) {
            String passwordToUse = null;
            if (certificate.getCertificateKeyPasswordFile() != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(SSLHostConfig.adjustRelativePath(certificate.getCertificateKeyPasswordFile())), StandardCharsets.UTF_8));){
                    passwordToUse = reader.readLine();
                }
            } else {
                passwordToUse = certificate.getCertificateKeyPassword();
            }
            org.apache.tomcat.jni.SSLContext.setCertificate((long)this.ctx, (String)SSLHostConfig.adjustRelativePath(certificate.getCertificateFile()), (String)SSLHostConfig.adjustRelativePath(certificate.getCertificateKeyFile()), (String)passwordToUse, (int)OpenSSLContext.getCertificateIndex(certificate));
            org.apache.tomcat.jni.SSLContext.setCertificateChainFile((long)this.ctx, (String)SSLHostConfig.adjustRelativePath(certificate.getCertificateChainFile()), (boolean)false);
            org.apache.tomcat.jni.SSLContext.setCARevocation((long)this.ctx, (String)SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListFile()), (String)SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListPath()));
        } else {
            X509Certificate[] chain;
            String alias = certificate.getCertificateKeyAlias();
            X509KeyManager x509KeyManager = certificate.getCertificateKeyManager();
            if (alias == null) {
                alias = "tomcat";
            }
            if ((chain = x509KeyManager.getCertificateChain(alias)) == null) {
                alias = OpenSSLContext.findAlias(x509KeyManager, certificate);
                chain = x509KeyManager.getCertificateChain(alias);
            }
            PrivateKey key = x509KeyManager.getPrivateKey(alias);
            StringBuilder sb = new StringBuilder(BEGIN_KEY);
            sb.append(Base64.getMimeEncoder(64, new byte[]{10}).encodeToString(key.getEncoded()));
            sb.append(END_KEY);
            org.apache.tomcat.jni.SSLContext.setCertificateRaw((long)this.ctx, (byte[])chain[0].getEncoded(), (byte[])sb.toString().getBytes(StandardCharsets.US_ASCII), (int)OpenSSLContext.getCertificateIndex(certificate));
            for (int i = 1; i < chain.length; ++i) {
                org.apache.tomcat.jni.SSLContext.addChainCertificateRaw((long)this.ctx, (byte[])chain[i].getEncoded());
            }
        }
    }

    private static int getCertificateIndex(SSLHostConfigCertificate certificate) {
        int result = certificate.getType() == SSLHostConfigCertificate.Type.RSA || certificate.getType() == SSLHostConfigCertificate.Type.UNDEFINED ? 0 : (certificate.getType() == SSLHostConfigCertificate.Type.EC ? 3 : (certificate.getType() == SSLHostConfigCertificate.Type.DSA ? 1 : 4));
        return result;
    }

    private static String findAlias(X509KeyManager keyManager, SSLHostConfigCertificate certificate) {
        SSLHostConfigCertificate.Type type = certificate.getType();
        String result = null;
        ArrayList<SSLHostConfigCertificate.Type> candidateTypes = new ArrayList<SSLHostConfigCertificate.Type>();
        if (SSLHostConfigCertificate.Type.UNDEFINED.equals((Object)type)) {
            candidateTypes.addAll(Arrays.asList(SSLHostConfigCertificate.Type.values()));
            candidateTypes.remove((Object)SSLHostConfigCertificate.Type.UNDEFINED);
        } else {
            candidateTypes.add(type);
        }
        Iterator iter = candidateTypes.iterator();
        while (result == null && iter.hasNext()) {
            result = keyManager.chooseServerAlias(((SSLHostConfigCertificate.Type)((Object)iter.next())).toString(), null, null);
        }
        return result;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] managers) {
        for (TrustManager m : managers) {
            if (!(m instanceof X509TrustManager)) continue;
            return (X509TrustManager)m;
        }
        throw new IllegalStateException(sm.getString("openssl.trustManagerMissing"));
    }

    private static X509Certificate[] certificates(byte[][] chain) {
        X509Certificate[] peerCerts = new X509Certificate[chain.length];
        for (int i = 0; i < peerCerts.length; ++i) {
            peerCerts[i] = new OpenSSLX509Certificate(chain[i]);
        }
        return peerCerts;
    }

    long getSSLContextID() {
        return this.ctx;
    }

    @Override
    public SSLSessionContext getServerSessionContext() {
        return this.sessionContext;
    }

    @Override
    public SSLEngine createSSLEngine() {
        return new OpenSSLEngine(this.ctx, defaultProtocol, false, this.sessionContext, this.negotiableProtocols != null && this.negotiableProtocols.size() > 0, this.initialized, this.sslHostConfig.getCertificateVerificationDepth(), this.sslHostConfig.getCertificateVerification() == SSLHostConfig.CertificateVerification.OPTIONAL_NO_CA);
    }

    @Override
    public SSLServerSocketFactory getServerSocketFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLParameters getSupportedSSLParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] chain = null;
        X509KeyManager x509KeyManager = this.certificate.getCertificateKeyManager();
        if (x509KeyManager != null) {
            if (alias == null) {
                alias = "tomcat";
            }
            if ((chain = x509KeyManager.getCertificateChain(alias)) == null) {
                alias = OpenSSLContext.findAlias(x509KeyManager, this.certificate);
                chain = x509KeyManager.getCertificateChain(alias);
            }
        }
        return chain;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] acceptedCerts = null;
        if (this.x509TrustManager != null) {
            acceptedCerts = this.x509TrustManager.getAcceptedIssuers();
        }
        return acceptedCerts;
    }

    protected void finalize() throws Throwable {
        try {
            this.destroy();
        }
        finally {
            super.finalize();
        }
    }

    static {
        try {
            X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
        }
        catch (CertificateException e) {
            throw new IllegalStateException(sm.getString("openssl.X509FactoryError"), e);
        }
    }
}


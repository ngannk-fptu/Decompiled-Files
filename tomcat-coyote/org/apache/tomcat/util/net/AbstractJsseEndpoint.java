/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;

public abstract class AbstractJsseEndpoint<S, U>
extends AbstractEndpoint<S, U> {
    private String sslImplementationName = null;
    private int sniParseLimit = 65536;
    private SSLImplementation sslImplementation = null;

    public String getSslImplementationName() {
        return this.sslImplementationName;
    }

    public void setSslImplementationName(String s) {
        this.sslImplementationName = s;
    }

    public SSLImplementation getSslImplementation() {
        return this.sslImplementation;
    }

    public int getSniParseLimit() {
        return this.sniParseLimit;
    }

    public void setSniParseLimit(int sniParseLimit) {
        this.sniParseLimit = sniParseLimit;
    }

    protected void initialiseSsl() throws Exception {
        if (this.isSSLEnabled()) {
            this.sslImplementation = SSLImplementation.getInstance(this.getSslImplementationName());
            for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                this.createSSLContext(sslHostConfig);
            }
            if (this.sslHostConfigs.get(this.getDefaultSSLHostConfigName()) == null) {
                throw new IllegalArgumentException(sm.getString("endpoint.noSslHostConfig", new Object[]{this.getDefaultSSLHostConfigName(), this.getName()}));
            }
        }
    }

    @Override
    protected void createSSLContext(SSLHostConfig sslHostConfig) throws IllegalArgumentException {
        if (sslHostConfig.getCertificateVerification().isOptional() && this.negotiableProtocols.contains("h2")) {
            this.getLog().warn((Object)sm.getString("sslHostConfig.certificateVerificationWithHttp2", new Object[]{sslHostConfig.getHostName()}));
        }
        boolean firstCertificate = true;
        for (SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
            SSLContext sslContext;
            SSLUtil sslUtil = this.sslImplementation.getSSLUtil(certificate);
            if (firstCertificate) {
                firstCertificate = false;
                sslHostConfig.setEnabledProtocols(sslUtil.getEnabledProtocols());
                sslHostConfig.setEnabledCiphers(sslUtil.getEnabledCiphers());
            }
            try {
                sslContext = sslUtil.createSSLContext(this.negotiableProtocols);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            certificate.setSslContext(sslContext);
            this.logCertificate(certificate);
        }
    }

    protected SSLEngine createSSLEngine(String sniHostName, List<Cipher> clientRequestedCiphers, List<String> clientRequestedApplicationProtocols) {
        SSLHostConfig sslHostConfig = this.getSSLHostConfig(sniHostName);
        SSLHostConfigCertificate certificate = this.selectCertificate(sslHostConfig, clientRequestedCiphers);
        SSLContext sslContext = certificate.getSslContext();
        if (sslContext == null) {
            throw new IllegalStateException(sm.getString("endpoint.jsse.noSslContext", new Object[]{sniHostName}));
        }
        SSLEngine engine = sslContext.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setEnabledCipherSuites(sslHostConfig.getEnabledCiphers());
        engine.setEnabledProtocols(sslHostConfig.getEnabledProtocols());
        SSLParameters sslParameters = engine.getSSLParameters();
        sslParameters.setUseCipherSuitesOrder(sslHostConfig.getHonorCipherOrder());
        if (JreCompat.isAlpnSupported() && clientRequestedApplicationProtocols != null && clientRequestedApplicationProtocols.size() > 0 && this.negotiableProtocols.size() > 0) {
            ArrayList commonProtocols = new ArrayList(this.negotiableProtocols);
            commonProtocols.retainAll(clientRequestedApplicationProtocols);
            if (commonProtocols.size() > 0) {
                String[] commonProtocolsArray = commonProtocols.toArray(new String[0]);
                JreCompat.getInstance().setApplicationProtocols(sslParameters, commonProtocolsArray);
            }
        }
        switch (sslHostConfig.getCertificateVerification()) {
            case NONE: {
                sslParameters.setNeedClientAuth(false);
                sslParameters.setWantClientAuth(false);
                break;
            }
            case OPTIONAL: 
            case OPTIONAL_NO_CA: {
                sslParameters.setWantClientAuth(true);
                break;
            }
            case REQUIRED: {
                sslParameters.setNeedClientAuth(true);
            }
        }
        engine.setSSLParameters(sslParameters);
        return engine;
    }

    private SSLHostConfigCertificate selectCertificate(SSLHostConfig sslHostConfig, List<Cipher> clientCiphers) {
        Set<SSLHostConfigCertificate> certificates = sslHostConfig.getCertificates(true);
        if (certificates.size() == 1) {
            return certificates.iterator().next();
        }
        LinkedHashSet<Cipher> serverCiphers = sslHostConfig.getCipherList();
        ArrayList<Cipher> candidateCiphers = new ArrayList<Cipher>();
        if (sslHostConfig.getHonorCipherOrder()) {
            candidateCiphers.addAll(serverCiphers);
            candidateCiphers.retainAll(clientCiphers);
        } else {
            candidateCiphers.addAll(clientCiphers);
            candidateCiphers.retainAll(serverCiphers);
        }
        for (Cipher candidate : candidateCiphers) {
            for (SSLHostConfigCertificate certificate : certificates) {
                if (!certificate.getType().isCompatibleWith(candidate.getAu())) continue;
                return certificate;
            }
        }
        return certificates.iterator().next();
    }

    @Override
    public boolean isAlpnSupported() {
        SSLImplementation sslImplementation;
        if (!this.isSSLEnabled()) {
            return false;
        }
        try {
            sslImplementation = SSLImplementation.getInstance(this.getSslImplementationName());
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return sslImplementation.isAlpnSupported();
    }

    @Override
    public void unbind() throws Exception {
        for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
            for (SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
                certificate.setSslContext(null);
            }
        }
    }

    protected abstract NetworkChannel getServerSocket();

    @Override
    protected final InetSocketAddress getLocalAddress() throws IOException {
        NetworkChannel serverSock = this.getServerSocket();
        if (serverSock == null) {
            return null;
        }
        SocketAddress sa = serverSock.getLocalAddress();
        if (sa instanceof InetSocketAddress) {
            return (InetSocketAddress)sa;
        }
        return null;
    }

    @Override
    protected void setDefaultSslHostConfig(SSLHostConfig sslHostConfig) {
    }
}


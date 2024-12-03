/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapHostnameVerificationSSLSocketFactory
extends SocketFactory
implements Comparator<String> {
    private static final Logger log = LoggerFactory.getLogger(LdapHostnameVerificationSSLSocketFactory.class);
    private final SSLSocketFactory sf = SSLContext.getDefault().getSocketFactory();

    private LdapHostnameVerificationSSLSocketFactory() throws NoSuchAlgorithmException {
    }

    public static synchronized SocketFactory getDefault() {
        log.debug("Name checking SSLSocketFactory created");
        try {
            return new LdapHostnameVerificationSSLSocketFactory();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static void makeUseLdapVerification(Socket s) {
        if (!(s instanceof SSLSocket)) {
            throw new IllegalArgumentException("Unexpected SSLSocket implementation: " + s.getClass().getName());
        }
        SSLSocket ssls = (SSLSocket)s;
        SSLParameters param = ssls.getSSLParameters();
        param.setEndpointIdentificationAlgorithm("LDAPS");
        ssls.setSSLParameters(param);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        log.warn("Creating socket to " + address);
        Socket s = this.sf.createSocket(address, port, localAddress, localPort);
        LdapHostnameVerificationSSLSocketFactory.makeUseLdapVerification(s);
        return s;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        log.debug("Creating socket to " + host);
        Socket s = this.sf.createSocket(host, port);
        LdapHostnameVerificationSSLSocketFactory.makeUseLdapVerification(s);
        return s;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        log.debug("Creating socket to " + host);
        Socket s = this.sf.createSocket(host, port);
        LdapHostnameVerificationSSLSocketFactory.makeUseLdapVerification(s);
        return s;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        log.debug("Creating socket to " + host);
        Socket s = this.sf.createSocket(host, port, localHost, localPort);
        LdapHostnameVerificationSSLSocketFactory.makeUseLdapVerification(s);
        return s;
    }

    @Override
    public Socket createSocket() throws IOException {
        log.debug("Creating disconnected socket");
        Socket s = this.sf.createSocket();
        LdapHostnameVerificationSSLSocketFactory.makeUseLdapVerification(s);
        return s;
    }

    @Override
    public int compare(String socketFactory1, String socketFactory2) {
        return socketFactory1.compareTo(socketFactory2);
    }
}


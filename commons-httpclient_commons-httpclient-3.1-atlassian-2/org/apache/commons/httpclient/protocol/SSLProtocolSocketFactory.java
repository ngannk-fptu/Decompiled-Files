/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.net.SocketFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import sun.security.util.HostnameChecker;

public class SSLProtocolSocketFactory
implements SecureProtocolSocketFactory {
    private static final SSLProtocolSocketFactory factory = new SSLProtocolSocketFactory();
    private static final String[] BAD_COUNTRY_2LDS = new String[]{"ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org"};
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    static SSLProtocolSocketFactory getSocketFactory() {
        return factory;
    }

    protected void setSSLProtocols(SSLSocket sslSocket) {
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket)sf.createSocket(host, port, clientHost, clientPort);
        this.setSSLProtocols(sslSocket);
        SSLProtocolSocketFactory.verifyHostName(host, sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        if (timeout == 0) {
            return this.createSocket(host, port, localAddress, localPort);
        }
        Socket sock = SocketFactory.getDefault().createSocket();
        if (localAddress != null) {
            sock.bind(new InetSocketAddress(localAddress, localPort));
        }
        if (timeout > 0) {
            sock.setSoTimeout(timeout);
        }
        sock.connect(new InetSocketAddress(host, port), timeout);
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket)sf.createSocket(sock, host, port, true);
        this.setSSLProtocols(sslSocket);
        sslSocket.startHandshake();
        SSLProtocolSocketFactory.verifyHostName(host, sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket)sf.createSocket(host, port);
        this.setSSLProtocols(sslSocket);
        SSLProtocolSocketFactory.verifyHostName(host, sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket)sf.createSocket(socket, host, port, autoClose);
        this.setSSLProtocols(sslSocket);
        SSLProtocolSocketFactory.verifyHostName(host, sslSocket);
        return sslSocket;
    }

    private static void verifyHostName(String host, SSLSocket ssl) throws IOException {
        if (host == null) {
            throw new IllegalArgumentException("host to verify was null");
        }
        SSLSession session = ssl.getSession();
        if (session == null) {
            InputStream in = ssl.getInputStream();
            in.available();
            session = ssl.getSession();
            if (session == null) {
                ssl.startHandshake();
                session = ssl.getSession();
            }
        }
        Certificate[] certs = session.getPeerCertificates();
        SSLProtocolSocketFactory.verifyHostName(host.toLowerCase(Locale.US), (X509Certificate)certs[0]);
    }

    private static void verifyHostName(String host, X509Certificate cert) throws SSLException {
        HostnameChecker hostnameChecker = HostnameChecker.getInstance((byte)1);
        try {
            hostnameChecker.match(host, cert);
        }
        catch (CertificateException e) {
            throw new SSLException(e);
        }
    }

    private static String[] getDNSSubjectAlts(X509Certificate cert) {
        LinkedList<String> subjectAltList = new LinkedList<String>();
        Collection<List<?>> c = null;
        try {
            c = cert.getSubjectAlternativeNames();
        }
        catch (CertificateParsingException cpe) {
            cpe.printStackTrace();
        }
        if (c != null) {
            for (List<?> list : c) {
                int type = (Integer)list.get(0);
                if (type != 2) continue;
                String s = (String)list.get(1);
                subjectAltList.add(s);
            }
        }
        if (!subjectAltList.isEmpty()) {
            String[] subjectAlts = new String[subjectAltList.size()];
            subjectAltList.toArray(subjectAlts);
            return subjectAlts;
        }
        return new String[0];
    }

    private static void verifyHostName(String host, String cn, String[] subjectAlts) throws SSLException {
        StringBuffer cnTested = new StringBuffer();
        for (int i = 0; i < subjectAlts.length; ++i) {
            String name = subjectAlts[i];
            if (name == null) continue;
            if (SSLProtocolSocketFactory.verifyHostName(host, name = name.toLowerCase())) {
                return;
            }
            cnTested.append("/").append(name);
        }
        if (cn != null && SSLProtocolSocketFactory.verifyHostName(host, cn)) {
            return;
        }
        cnTested.append("/").append(cn);
        throw new SSLException("hostname in certificate didn't match: <" + host + "> != <" + cnTested + ">");
    }

    private static boolean verifyHostName(String host, String cn) {
        if (SSLProtocolSocketFactory.doWildCard(cn) && !SSLProtocolSocketFactory.isIPAddress(host)) {
            return SSLProtocolSocketFactory.matchesWildCard(cn, host);
        }
        return host.equalsIgnoreCase(cn);
    }

    private static boolean doWildCard(String cn) {
        String[] parts = cn.split("\\.");
        return parts.length >= 3 && parts[0].endsWith("*") && SSLProtocolSocketFactory.acceptableCountryWildcard(cn) && !SSLProtocolSocketFactory.isIPAddress(cn);
    }

    private static boolean isIPAddress(String hostname) {
        return hostname != null && (IPV4_PATTERN.matcher(hostname).matches() || IPV6_STD_PATTERN.matcher(hostname).matches() || IPV6_HEX_COMPRESSED_PATTERN.matcher(hostname).matches());
    }

    private static boolean acceptableCountryWildcard(String cn) {
        String[] parts = cn.split("\\.");
        if (parts.length > 3 || parts[parts.length - 1].length() != 2) {
            return true;
        }
        String countryCode = parts[parts.length - 2];
        return Arrays.binarySearch(BAD_COUNTRY_2LDS, countryCode) < 0;
    }

    private static boolean matchesWildCard(String cn, String hostName) {
        String[] parts = cn.split("\\.");
        boolean match = false;
        String firstpart = parts[0];
        if (firstpart.length() > 1) {
            String prefix = firstpart.substring(0, firstpart.length() - 1);
            String suffix = cn.substring(firstpart.length());
            String hostSuffix = hostName.substring(prefix.length());
            match = hostName.startsWith(prefix) && hostSuffix.endsWith(suffix);
        } else {
            match = hostName.endsWith(cn.substring(1));
        }
        if (match) {
            match = SSLProtocolSocketFactory.countDots(hostName) == SSLProtocolSocketFactory.countDots(cn);
        }
        return match;
    }

    private static int countDots(String data) {
        int dots = 0;
        for (int i = 0; i < data.length(); ++i) {
            if (data.charAt(i) != '.') continue;
            ++dots;
        }
        return dots;
    }

    private static String getCN(X509Certificate cert) {
        String subjectPrincipal = cert.getSubjectX500Principal().toString();
        return SSLProtocolSocketFactory.getCN(subjectPrincipal);
    }

    private static String getCN(String subjectPrincipal) {
        StringTokenizer st = new StringTokenizer(subjectPrincipal, ",");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken().trim();
            if (tok.length() <= 3 || !tok.substring(0, 3).equalsIgnoreCase("CN=")) continue;
            return tok.substring(3);
        }
        return null;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}


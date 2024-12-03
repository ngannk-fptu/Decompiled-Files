/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.sourceforge.jtds.ssl.TdsTlsSocket;
import net.sourceforge.jtds.util.Logger;

public class SocketFactories {
    public static SocketFactory getSocketFactory(String ssl, Socket socket) {
        return new TdsTlsSocketFactory(ssl, socket);
    }

    private static class TdsTlsSocketFactory
    extends SocketFactory {
        private static SSLSocketFactory factorySingleton;
        private final String ssl;
        private final Socket socket;

        public TdsTlsSocketFactory(String ssl, Socket socket) {
            this.ssl = ssl;
            this.socket = socket;
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            SSLSocket sslSocket = (SSLSocket)this.getFactory().createSocket(new TdsTlsSocket(this.socket), host, port, true);
            sslSocket.startHandshake();
            sslSocket.getSession().invalidate();
            return sslSocket;
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort) throws IOException {
            return null;
        }

        private SSLSocketFactory getFactory() throws IOException {
            try {
                if ("authenticate".equals(this.ssl)) {
                    return (SSLSocketFactory)SSLSocketFactory.getDefault();
                }
                return TdsTlsSocketFactory.factory();
            }
            catch (GeneralSecurityException e) {
                Logger.logException(e);
                throw new IOException(e.getMessage());
            }
        }

        private static SSLSocketFactory factory() throws NoSuchAlgorithmException, KeyManagementException {
            if (factorySingleton == null) {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, TdsTlsSocketFactory.trustManagers(), null);
                factorySingleton = ctx.getSocketFactory();
            }
            return factorySingleton;
        }

        private static TrustManager[] trustManagers() {
            X509TrustManager tm = new X509TrustManager(){

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String x) {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String x) {
                }
            };
            return new X509TrustManager[]{tm};
        }
    }
}


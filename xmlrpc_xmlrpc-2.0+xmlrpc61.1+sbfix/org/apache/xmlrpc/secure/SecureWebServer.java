/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.net.ssl.KeyManagerFactory
 *  com.sun.net.ssl.SSLContext
 */
package org.apache.xmlrpc.secure;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcServer;
import org.apache.xmlrpc.secure.SecurityConstants;
import org.apache.xmlrpc.secure.SecurityTool;

public class SecureWebServer
extends WebServer
implements SecurityConstants {
    public SecureWebServer(int port) {
        this(port, null);
    }

    public SecureWebServer(int port, InetAddress addr) {
        super(port, addr);
    }

    public SecureWebServer(int port, InetAddress addr, XmlRpcServer xmlrpc) {
        super(port, addr, xmlrpc);
    }

    protected ServerSocket createServerSocket(int port, int backlog, InetAddress add) throws Exception {
        SecurityTool.setup();
        SSLContext context = SSLContext.getInstance((String)SecurityTool.getSecurityProtocol());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance((String)SecurityTool.getKeyManagerType());
        KeyStore keyStore = KeyStore.getInstance(SecurityTool.getKeyStoreType());
        keyStore.load(new FileInputStream(SecurityTool.getKeyStore()), SecurityTool.getKeyStorePassword().toCharArray());
        keyManagerFactory.init(keyStore, SecurityTool.getKeyStorePassword().toCharArray());
        context.init(keyManagerFactory.getKeyManagers(), null, null);
        SSLServerSocketFactory sslSrvFact = context.getServerSocketFactory();
        return (SSLServerSocket)sslSrvFact.createServerSocket(port);
    }

    public static void main(String[] argv) {
        int p = WebServer.determinePort(argv, 10000);
        XmlRpc.setKeepAlive(true);
        SecureWebServer webserver = new SecureWebServer(p);
        try {
            webserver.addDefaultHandlers();
            webserver.start();
        }
        catch (Exception e) {
            System.err.println("Error running secure web server");
            e.printStackTrace();
            System.exit(1);
        }
    }
}


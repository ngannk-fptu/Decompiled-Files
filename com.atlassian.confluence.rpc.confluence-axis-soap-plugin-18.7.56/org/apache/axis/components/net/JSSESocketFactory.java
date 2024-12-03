/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.DefaultSocketFactory;
import org.apache.axis.components.net.SecureSocketFactory;
import org.apache.axis.components.net.TransportClientProperties;
import org.apache.axis.components.net.TransportClientPropertiesFactory;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.StringUtils;
import org.apache.axis.utils.XMLUtils;

public class JSSESocketFactory
extends DefaultSocketFactory
implements SecureSocketFactory {
    protected SSLSocketFactory sslFactory = null;

    public JSSESocketFactory(Hashtable attributes) {
        super(attributes);
    }

    protected void initFactory() throws IOException {
        this.sslFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    }

    public Socket create(String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL) throws Exception {
        if (this.sslFactory == null) {
            this.initFactory();
        }
        if (port == -1) {
            port = 443;
        }
        TransportClientProperties tcp = TransportClientPropertiesFactory.create("https");
        boolean hostInNonProxyList = this.isHostInNonProxyList(host, tcp.getNonProxyHosts());
        Socket sslSocket = null;
        if (tcp.getProxyHost().length() == 0 || hostInNonProxyList) {
            sslSocket = this.sslFactory.createSocket(host, port);
        } else {
            int tunnelPort;
            int n = tunnelPort = tcp.getProxyPort().length() != 0 ? Integer.parseInt(tcp.getProxyPort()) : 80;
            if (tunnelPort < 0) {
                tunnelPort = 80;
            }
            Socket tunnel = new Socket(tcp.getProxyHost(), tunnelPort);
            OutputStream tunnelOutputStream = tunnel.getOutputStream();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tunnelOutputStream)));
            out.print("CONNECT " + host + ":" + port + " HTTP/1.0\r\n" + "User-Agent: AxisClient");
            if (tcp.getProxyUser().length() != 0 && tcp.getProxyPassword().length() != 0) {
                String encodedPassword = XMLUtils.base64encode((tcp.getProxyUser() + ":" + tcp.getProxyPassword()).getBytes());
                out.print("\nProxy-Authorization: Basic " + encodedPassword);
            }
            out.print("\nContent-Length: 0");
            out.print("\nPragma: no-cache");
            out.print("\r\n\r\n");
            out.flush();
            InputStream tunnelInputStream = tunnel.getInputStream();
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("isNull00", "tunnelInputStream", "" + (tunnelInputStream == null)));
            }
            String replyStr = "";
            int newlinesSeen = 0;
            boolean headerDone = false;
            while (newlinesSeen < 2) {
                int i = tunnelInputStream.read();
                if (i < 0) {
                    throw new IOException("Unexpected EOF from proxy");
                }
                if (i == 10) {
                    headerDone = true;
                    ++newlinesSeen;
                    continue;
                }
                if (i == 13) continue;
                newlinesSeen = 0;
                if (headerDone) continue;
                replyStr = replyStr + String.valueOf((char)i);
            }
            if (StringUtils.startsWithIgnoreWhitespaces("HTTP/1.0 200", replyStr) && StringUtils.startsWithIgnoreWhitespaces("HTTP/1.1 200", replyStr)) {
                throw new IOException(Messages.getMessage("cantTunnel00", new String[]{tcp.getProxyHost(), "" + tunnelPort, replyStr}));
            }
            sslSocket = this.sslFactory.createSocket(tunnel, host, port, true);
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("setupTunnel00", tcp.getProxyHost(), "" + tunnelPort));
            }
        }
        ((SSLSocket)sslSocket).startHandshake();
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("createdSSL00"));
        }
        return sslSocket;
    }
}


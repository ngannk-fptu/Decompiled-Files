/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import org.apache.catalina.ha.backend.HeartbeatListener;
import org.apache.catalina.ha.backend.Proxy;
import org.apache.catalina.ha.backend.Sender;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class TcpSender
implements Sender {
    private static final Log log = LogFactory.getLog(HeartbeatListener.class);
    private static final StringManager sm = StringManager.getManager(TcpSender.class);
    HeartbeatListener config = null;
    protected Proxy[] proxies = null;
    protected Socket[] connections = null;
    protected BufferedReader[] connectionReaders = null;
    protected BufferedWriter[] connectionWriters = null;

    @Override
    public void init(HeartbeatListener config) throws Exception {
        this.config = config;
        StringTokenizer tok = new StringTokenizer(config.getProxyList(), ",");
        this.proxies = new Proxy[tok.countTokens()];
        int i = 0;
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();
            int pos = token.indexOf(58);
            if (pos <= 0) {
                throw new Exception(sm.getString("tcpSender.invalidProxyList"));
            }
            this.proxies[i] = new Proxy();
            this.proxies[i].port = Integer.parseInt(token.substring(pos + 1));
            try {
                this.proxies[i].address = InetAddress.getByName(token.substring(0, pos));
            }
            catch (Exception e) {
                throw new Exception(sm.getString("tcpSender.invalidProxyList"));
            }
            ++i;
        }
        this.connections = new Socket[this.proxies.length];
        this.connectionReaders = new BufferedReader[this.proxies.length];
        this.connectionWriters = new BufferedWriter[this.proxies.length];
    }

    @Override
    public int send(String mess) throws Exception {
        if (this.connections == null) {
            log.error((Object)sm.getString("tcpSender.notInitialized"));
            return -1;
        }
        String requestLine = "POST " + this.config.getProxyURL() + " HTTP/1.0";
        block4: for (int i = 0; i < this.connections.length; ++i) {
            if (this.connections[i] == null) {
                try {
                    if (this.config.getHost() != null) {
                        this.connections[i] = new Socket();
                        InetAddress addr = InetAddress.getByName(this.config.getHost());
                        InetSocketAddress addrs = new InetSocketAddress(addr, 0);
                        this.connections[i].setReuseAddress(true);
                        this.connections[i].bind(addrs);
                        addrs = new InetSocketAddress(this.proxies[i].address, this.proxies[i].port);
                        this.connections[i].connect(addrs);
                    } else {
                        this.connections[i] = new Socket(this.proxies[i].address, this.proxies[i].port);
                    }
                    this.connectionReaders[i] = new BufferedReader(new InputStreamReader(this.connections[i].getInputStream()));
                    this.connectionWriters[i] = new BufferedWriter(new OutputStreamWriter(this.connections[i].getOutputStream()));
                }
                catch (Exception ex) {
                    log.error((Object)sm.getString("tcpSender.connectionFailed"), (Throwable)ex);
                    this.close(i);
                }
            }
            if (this.connections[i] == null) continue;
            BufferedWriter writer = this.connectionWriters[i];
            try {
                writer.write(requestLine);
                writer.write("\r\n");
                writer.write("Content-Length: " + mess.length() + "\r\n");
                writer.write("User-Agent: HeartbeatListener/1.0\r\n");
                writer.write("Connection: Keep-Alive\r\n");
                writer.write("\r\n");
                writer.write(mess);
                writer.write("\r\n");
                writer.flush();
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("tcpSender.sendFailed"), (Throwable)ex);
                this.close(i);
            }
            if (this.connections[i] == null) continue;
            String responseStatus = this.connectionReaders[i].readLine();
            if (responseStatus == null) {
                log.error((Object)sm.getString("tcpSender.responseError"));
                this.close(i);
                continue;
            }
            int status = Integer.parseInt(responseStatus = responseStatus.substring(responseStatus.indexOf(32) + 1, responseStatus.indexOf(32, responseStatus.indexOf(32) + 1)));
            if (status != 200) {
                log.error((Object)sm.getString("tcpSender.responseErrorCode", new Object[]{status}));
                this.close(i);
                continue;
            }
            String header = this.connectionReaders[i].readLine();
            int contentLength = 0;
            while (header != null && !header.isEmpty()) {
                int colon = header.indexOf(58);
                String headerName = header.substring(0, colon).trim();
                String headerValue = header.substring(colon + 1).trim();
                if ("content-length".equalsIgnoreCase(headerName)) {
                    contentLength = Integer.parseInt(headerValue);
                }
                header = this.connectionReaders[i].readLine();
            }
            if (contentLength <= 0) continue;
            char[] buf = new char[512];
            while (contentLength > 0) {
                int thisTime = contentLength > buf.length ? buf.length : contentLength;
                int n = this.connectionReaders[i].read(buf, 0, thisTime);
                if (n <= 0) {
                    log.error((Object)sm.getString("tcpSender.readError"));
                    this.close(i);
                    continue block4;
                }
                contentLength -= n;
            }
        }
        return 0;
    }

    protected void close(int i) {
        try {
            if (this.connectionReaders[i] != null) {
                this.connectionReaders[i].close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.connectionReaders[i] = null;
        try {
            if (this.connectionWriters[i] != null) {
                this.connectionWriters[i].close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.connectionWriters[i] = null;
        try {
            if (this.connections[i] != null) {
                this.connections[i].close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.connections[i] = null;
    }
}


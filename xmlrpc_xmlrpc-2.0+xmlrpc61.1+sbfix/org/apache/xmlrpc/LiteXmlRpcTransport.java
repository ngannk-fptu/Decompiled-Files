/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.StringTokenizer;
import org.apache.xmlrpc.ServerInputStream;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.util.HttpUtil;

class LiteXmlRpcTransport
implements XmlRpcTransport {
    String hostname;
    String host;
    protected String auth = null;
    int port;
    String uri;
    Socket socket = null;
    BufferedOutputStream output;
    BufferedInputStream input;
    boolean keepalive;
    byte[] buffer;

    public LiteXmlRpcTransport(URL url) {
        this.hostname = url.getHost();
        this.port = url.getPort();
        if (this.port < 1) {
            this.port = 80;
        }
        this.uri = url.getFile();
        if (this.uri == null || "".equals(this.uri)) {
            this.uri = "/";
        }
        this.host = this.port == 80 ? this.hostname : this.hostname + ":" + this.port;
    }

    public InputStream sendXmlRpc(byte[] request) throws IOException {
        try {
            if (this.socket == null) {
                this.initConnection();
            }
            InputStream in = null;
            try {
                in = this.sendRequest(request);
            }
            catch (IOException iox) {
                if (this.keepalive) {
                    this.closeConnection();
                    this.initConnection();
                    in = this.sendRequest(request);
                }
                throw iox;
            }
            return in;
        }
        catch (IOException iox) {
            throw iox;
        }
        catch (Exception x) {
            String msg;
            if (XmlRpc.debug) {
                x.printStackTrace();
            }
            if ((msg = x.getMessage()) == null || msg.length() == 0) {
                msg = x.toString();
            }
            throw new IOException(msg);
        }
    }

    protected void initConnection() throws IOException {
        int retries = 3;
        int delayMillis = 100;
        int tries = 0;
        this.socket = null;
        while (this.socket == null) {
            try {
                this.socket = new Socket(this.hostname, this.port);
            }
            catch (ConnectException e) {
                if (tries >= 3) {
                    throw e;
                }
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException ignore) {}
            }
        }
        this.output = new BufferedOutputStream(this.socket.getOutputStream());
        this.input = new BufferedInputStream(this.socket.getInputStream());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeConnection() {
        try {
            this.socket.close();
        }
        catch (Exception exception) {
        }
        finally {
            this.socket = null;
        }
    }

    public InputStream sendRequest(byte[] request) throws IOException {
        this.output.write(("POST " + this.uri + " HTTP/1.0\r\n").getBytes());
        this.output.write("User-Agent: Apache XML-RPC 2.0\r\n".getBytes());
        this.output.write(("Host: " + this.host + "\r\n").getBytes());
        if (XmlRpc.getKeepAlive()) {
            this.output.write("Connection: Keep-Alive\r\n".getBytes());
        }
        this.output.write("Content-Type: text/xml\r\n".getBytes());
        if (this.auth != null) {
            this.output.write(("Authorization: Basic " + this.auth + "\r\n").getBytes());
        }
        this.output.write(("Content-Length: " + request.length).getBytes());
        this.output.write("\r\n\r\n".getBytes());
        this.output.write(request);
        this.output.flush();
        String line = this.readLine();
        if (XmlRpc.debug) {
            System.out.println(line);
        }
        int contentLength = -1;
        try {
            StringTokenizer tokens = new StringTokenizer(line);
            String httpversion = tokens.nextToken();
            String statusCode = tokens.nextToken();
            String statusMsg = tokens.nextToken("\n\r");
            boolean bl = this.keepalive = XmlRpc.getKeepAlive() && "HTTP/1.1".equals(httpversion);
            if (!"200".equals(statusCode)) {
                throw new IOException("Unexpected Response from Server: " + statusMsg);
            }
        }
        catch (IOException iox) {
            throw iox;
        }
        catch (Exception x) {
            throw new IOException("Server returned invalid Response.");
        }
        do {
            if ((line = this.readLine()) == null) continue;
            if (XmlRpc.debug) {
                System.out.println(line);
            }
            if ((line = line.toLowerCase()).startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring(15).trim());
            }
            if (!line.startsWith("connection:")) continue;
            boolean bl = this.keepalive = XmlRpc.getKeepAlive() && line.indexOf("keep-alive") > -1;
        } while (line != null && !line.equals(""));
        return new ServerInputStream(this.input, contentLength);
    }

    public void setBasicAuthentication(String user, String password) {
        this.auth = HttpUtil.encodeBasicAuthentication(user, password);
    }

    public void endClientRequest() {
        if (!this.keepalive) {
            this.closeConnection();
        }
    }

    private String readLine() throws IOException {
        int next;
        if (this.buffer == null) {
            this.buffer = new byte[2048];
        }
        int count = 0;
        while ((next = this.input.read()) >= 0 && next != 10) {
            if (next != 13) {
                this.buffer[count++] = (byte)next;
            }
            if (count < this.buffer.length) continue;
            throw new IOException("HTTP Header too long");
        }
        return new String(this.buffer, 0, count);
    }

    protected void finalize() throws Throwable {
        this.closeConnection();
    }
}


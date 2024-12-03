/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package org.apache.xmlrpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.AuthDemo;
import org.apache.xmlrpc.AuthenticationFailed;
import org.apache.xmlrpc.Echo;
import org.apache.xmlrpc.ServerInputStream;
import org.apache.xmlrpc.SystemHandler;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcServer;

public class WebServer
implements Runnable {
    protected XmlRpcServer xmlrpc;
    protected ServerSocket serverSocket;
    protected Thread listener;
    protected Vector accept;
    protected Vector deny;
    protected Stack threadpool;
    protected ThreadGroup runners;
    private InetAddress address;
    private int port;
    private boolean paranoid;
    protected static final byte[] ctype = WebServer.toHTTPBytes("Content-Type: text/xml\r\n");
    protected static final byte[] clength = WebServer.toHTTPBytes("Content-Length: ");
    protected static final byte[] newline = WebServer.toHTTPBytes("\r\n");
    protected static final byte[] doubleNewline = WebServer.toHTTPBytes("\r\n\r\n");
    protected static final byte[] conkeep = WebServer.toHTTPBytes("Connection: Keep-Alive\r\n");
    protected static final byte[] conclose = WebServer.toHTTPBytes("Connection: close\r\n");
    protected static final byte[] ok = WebServer.toHTTPBytes(" 200 OK\r\n");
    protected static final byte[] server = WebServer.toHTTPBytes("Server: Apache XML-RPC 1.0\r\n");
    protected static final byte[] wwwAuthenticate = WebServer.toHTTPBytes("WWW-Authenticate: Basic realm=XML-RPC\r\n");
    private static final String HTTP_11 = "HTTP/1.1";
    private static final String STAR = "*";
    static /* synthetic */ Class class$org$apache$xmlrpc$WebServer;
    static /* synthetic */ Class class$java$lang$Math;

    public static void main(String[] argv) {
        int p = WebServer.determinePort(argv, 8080);
        XmlRpc.setKeepAlive(true);
        WebServer webserver = new WebServer(p);
        try {
            webserver.addDefaultHandlers();
            webserver.start();
        }
        catch (Exception e) {
            System.err.println("Error running web server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected static int determinePort(String[] argv, int defaultPort) {
        int port = defaultPort;
        if (argv.length > 0) {
            try {
                port = Integer.parseInt(argv[0]);
            }
            catch (NumberFormatException nfx) {
                System.err.println("Error parsing port number: " + argv[0]);
                System.err.println("Usage: java " + (class$org$apache$xmlrpc$WebServer == null ? (class$org$apache$xmlrpc$WebServer = WebServer.class$("org.apache.xmlrpc.WebServer")) : class$org$apache$xmlrpc$WebServer).getName() + " [port]");
                System.exit(1);
            }
        }
        return port;
    }

    public WebServer(int port) {
        this(port, null);
    }

    public WebServer(int port, InetAddress addr) {
        this(port, addr, new XmlRpcServer());
    }

    public WebServer(int port, InetAddress addr, XmlRpcServer xmlrpc) {
        this.address = addr;
        this.port = port;
        this.xmlrpc = xmlrpc;
        this.accept = new Vector();
        this.deny = new Vector();
        this.threadpool = new Stack();
        this.runners = new ThreadGroup("XML-RPC Runner");
    }

    protected static final byte[] toHTTPBytes(String text) {
        try {
            return text.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new Error(e.getMessage() + ": HTTP requires US-ASCII encoding");
        }
    }

    protected ServerSocket createServerSocket(int port, int backlog, InetAddress addr) throws Exception {
        return new ServerSocket(port, backlog, addr);
    }

    private synchronized void setupServerSocket(int backlog) throws Exception {
        int attempt = 1;
        while (this.serverSocket == null) {
            try {
                this.serverSocket = this.createServerSocket(this.port, backlog, this.address);
            }
            catch (BindException e) {
                if (attempt == 10) {
                    throw e;
                }
                ++attempt;
                Thread.sleep(1000L);
            }
        }
        if (XmlRpc.debug) {
            StringBuffer msg = new StringBuffer();
            msg.append("Opened XML-RPC server socket for ");
            msg.append(this.address != null ? this.address.getHostName() : "localhost");
            msg.append(':').append(this.port);
            if (attempt > 1) {
                msg.append(" after ").append(attempt).append(" tries");
            }
            System.out.println(msg.toString());
        }
        if (this.serverSocket.getSoTimeout() <= 0) {
            this.serverSocket.setSoTimeout(4096);
        }
    }

    public void start() {
        try {
            this.setupServerSocket(50);
        }
        catch (Exception e) {
            this.listener = null;
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        if (this.listener == null) {
            this.listener = new Thread((Runnable)this, "XML-RPC Weblistener");
            this.listener.start();
        }
    }

    public void addHandler(String name, Object target) {
        this.xmlrpc.addHandler(name, target);
    }

    protected void addDefaultHandlers() throws Exception {
        this.addHandler("string", "Welcome to XML-RPC!");
        this.addHandler("math", class$java$lang$Math == null ? (class$java$lang$Math = WebServer.class$("java.lang.Math")) : class$java$lang$Math);
        this.addHandler("auth", new AuthDemo());
        this.addHandler("$default", new Echo());
        String url = "http://www.mailtothefuture.com:80/RPC2";
        this.addHandler("mttf", new XmlRpcClient(url));
        SystemHandler system = new SystemHandler();
        system.addDefaultSystemHandlers();
        this.addHandler("system", system);
    }

    public void removeHandler(String name) {
        this.xmlrpc.removeHandler(name);
    }

    public void setParanoid(boolean p) {
        this.paranoid = p;
    }

    public void acceptClient(String address) throws IllegalArgumentException {
        try {
            AddressMatcher m = new AddressMatcher(address);
            this.accept.addElement(m);
        }
        catch (Exception x) {
            throw new IllegalArgumentException("\"" + address + "\" does not represent a valid IP address");
        }
    }

    public void denyClient(String address) throws IllegalArgumentException {
        try {
            AddressMatcher m = new AddressMatcher(address);
            this.deny.addElement(m);
        }
        catch (Exception x) {
            throw new IllegalArgumentException("\"" + address + "\" does not represent a valid IP address");
        }
    }

    protected boolean allowConnection(Socket s) {
        AddressMatcher match;
        int i;
        if (!this.paranoid) {
            return true;
        }
        int l = this.deny.size();
        byte[] address = s.getInetAddress().getAddress();
        for (i = 0; i < l; ++i) {
            match = (AddressMatcher)this.deny.elementAt(i);
            if (!match.matches(address)) continue;
            return false;
        }
        l = this.accept.size();
        for (i = 0; i < l; ++i) {
            match = (AddressMatcher)this.accept.elementAt(i);
            if (!match.matches(address)) continue;
            return true;
        }
        return false;
    }

    protected boolean checkSocket(Socket s) {
        return this.allowConnection(s);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        block31: {
            block18: while (true) {
                try {
                    try {}
                    catch (Exception exception) {
                        System.err.println("Error accepting XML-RPC connections (" + exception + ").");
                        if (XmlRpc.debug) {
                            exception.printStackTrace();
                        }
                        var4_9 = null;
                        if (this.serverSocket != null) {
                            try {
                                this.serverSocket.close();
                                if (XmlRpc.debug) {
                                    System.out.print("Closed XML-RPC server socket");
                                }
                                this.serverSocket = null;
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (this.runners == null) return;
                        g = this.runners;
                        this.runners = null;
                        try {
                            g.interrupt();
                            return;
                        }
                        catch (Exception e) {
                            System.err.println(e);
                            e.printStackTrace();
                            return;
                        }
                    }
                }
                catch (Throwable var3_17) {
                    block30: {
                        var4_10 = null;
                        if (this.serverSocket != null) {
                            ** try [egrp 4[TRYBLOCK] [8 : 213->242)] { 
lbl36:
                            // 1 sources

                            this.serverSocket.close();
                            if (XmlRpc.debug) {
                                System.out.print("Closed XML-RPC server socket");
                            }
                            this.serverSocket = null;
                            break block30;
lbl41:
                            // 1 sources

                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (this.runners == null) throw var3_17;
                    g = this.runners;
                    this.runners = null;
                    ** try [egrp 5[TRYBLOCK] [9 : 267->275)] { 
lbl48:
                    // 1 sources

                    g.interrupt();
                    throw var3_17;
lbl50:
                    // 1 sources

                    catch (Exception e) {
                        System.err.println(e);
                        e.printStackTrace();
                    }
                    throw var3_17;
                }
                while (this.listener != null) {
                    try {
                        socket = this.serverSocket.accept();
                        try {
                            socket.setTcpNoDelay(true);
                        }
                        catch (SocketException socketOptEx) {
                            System.err.println(socketOptEx);
                        }
                        if (this.allowConnection(socket)) {
                            runner = this.getRunner();
                            runner.handle(socket);
                            continue block18;
                        }
                        socket.close();
                    }
                    catch (InterruptedIOException checkState) {
                    }
                    catch (Exception ex) {
                        System.err.println("Exception in XML-RPC listener loop (" + ex + ").");
                        if (!XmlRpc.debug) continue;
                        ex.printStackTrace();
                    }
                    catch (Error err) {
                        System.err.println("Error in XML-RPC listener loop (" + err + ").");
                        err.printStackTrace();
                    }
                }
                break;
            }
            var4_8 = null;
            if (this.serverSocket != null) {
                ** try [egrp 4[TRYBLOCK] [8 : 213->242)] { 
lbl83:
                // 1 sources

                this.serverSocket.close();
                if (XmlRpc.debug) {
                    System.out.print("Closed XML-RPC server socket");
                }
                this.serverSocket = null;
                break block31;
lbl88:
                // 1 sources

                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.runners == null) return;
        g = this.runners;
        this.runners = null;
        try {}
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            return;
        }
        g.interrupt();
    }

    public synchronized void shutdown() {
        if (this.listener != null) {
            Thread l = this.listener;
            this.listener = null;
            l.interrupt();
        }
    }

    protected Runner getRunner() {
        try {
            return (Runner)this.threadpool.pop();
        }
        catch (EmptyStackException empty) {
            int maxRequests = XmlRpc.getMaxThreads();
            if (this.runners.activeCount() > XmlRpc.getMaxThreads()) {
                throw new RuntimeException("System overload: Maximum number of concurrent requests (" + maxRequests + ") exceeded");
            }
            return new Runner();
        }
    }

    void repoolRunner(Runner runner) {
        this.threadpool.push(runner);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    class AddressMatcher {
        int[] pattern = new int[4];

        public AddressMatcher(String address) throws Exception {
            StringTokenizer st = new StringTokenizer(address, ".");
            if (st.countTokens() != 4) {
                throw new Exception("\"" + address + "\" does not represent a valid IP address");
            }
            for (int i = 0; i < 4; ++i) {
                String next = st.nextToken();
                this.pattern[i] = WebServer.STAR.equals(next) ? 256 : (int)((byte)Integer.parseInt(next));
            }
        }

        public boolean matches(byte[] address) {
            for (int i = 0; i < 4; ++i) {
                if (this.pattern[i] > 255 || this.pattern[i] == address[i]) continue;
                return false;
            }
            return true;
        }
    }

    class Connection
    implements Runnable {
        private Socket socket;
        private BufferedInputStream input;
        private BufferedOutputStream output;
        private String user;
        private String password;
        private Base64 base64Codec;
        byte[] buffer;

        public Connection(Socket socket) throws IOException {
            socket.setSoTimeout(30000);
            this.socket = socket;
            this.input = new BufferedInputStream(socket.getInputStream());
            this.output = new BufferedOutputStream(socket.getOutputStream());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Loose catch block
         */
        public void run() {
            block24: {
                boolean keepAlive = false;
                do {
                    this.user = null;
                    this.password = null;
                    String line = this.readLine();
                    if (line != null && line.length() == 0) {
                        line = this.readLine();
                    }
                    if (XmlRpc.debug) {
                        System.out.println(line);
                    }
                    int contentLength = -1;
                    StringTokenizer tokens = new StringTokenizer(line);
                    String method = tokens.nextToken();
                    String uri = tokens.nextToken();
                    String httpVersion = tokens.nextToken();
                    boolean bl = keepAlive = XmlRpc.getKeepAlive() && WebServer.HTTP_11.equals(httpVersion);
                    do {
                        String lineLower;
                        if ((line = this.readLine()) == null) continue;
                        if (XmlRpc.debug) {
                            System.out.println(line);
                        }
                        if ((lineLower = line.toLowerCase()).startsWith("content-length:")) {
                            contentLength = Integer.parseInt(line.substring(15).trim());
                        }
                        if (lineLower.startsWith("connection:")) {
                            boolean bl2 = keepAlive = XmlRpc.getKeepAlive() && lineLower.indexOf("keep-alive") > -1;
                        }
                        if (!lineLower.startsWith("authorization: basic ")) continue;
                        this.parseAuth(line);
                    } while (line != null && line.length() != 0);
                    if ("POST".equalsIgnoreCase(method)) {
                        ServerInputStream sin = new ServerInputStream(this.input, contentLength);
                        try {
                            byte[] result = WebServer.this.xmlrpc.execute(sin, this.user, this.password);
                            this.writeResponse(result, httpVersion, keepAlive);
                        }
                        catch (AuthenticationFailed unauthorized) {
                            keepAlive = false;
                            this.writeUnauthorized(httpVersion, method);
                        }
                    } else {
                        keepAlive = false;
                        this.writeBadRequest(httpVersion, method);
                    }
                    this.output.flush();
                } while (keepAlive);
                Object var11_12 = null;
                try {
                    if (this.socket != null) {
                        this.socket.close();
                    }
                    break block24;
                }
                catch (IOException ignore) {}
                break block24;
                {
                    catch (Exception exception) {
                        if (XmlRpc.debug) {
                            exception.printStackTrace();
                        } else {
                            System.err.println(exception);
                        }
                        Object var11_13 = null;
                        try {
                            if (this.socket != null) {
                                this.socket.close();
                            }
                            break block24;
                        }
                        catch (IOException ignore) {}
                    }
                }
                catch (Throwable throwable) {
                    Object var11_14 = null;
                    try {
                        if (this.socket != null) {
                            this.socket.close();
                        }
                    }
                    catch (IOException ignore) {
                        // empty catch block
                    }
                    throw throwable;
                }
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

        private void parseAuth(String line) {
            try {
                byte[] c = this.base64Codec.decode(WebServer.toHTTPBytes(line.substring(21)));
                String str = new String(c);
                int col = str.indexOf(58);
                this.user = str.substring(0, col);
                this.password = str.substring(col + 1);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        private void writeResponse(byte[] payload, String httpVersion, boolean keepAlive) throws IOException {
            this.output.write(WebServer.toHTTPBytes(httpVersion));
            this.output.write(ok);
            this.output.write(server);
            this.output.write(keepAlive ? conkeep : conclose);
            this.output.write(ctype);
            this.output.write(clength);
            this.output.write(WebServer.toHTTPBytes(Integer.toString(payload.length)));
            this.output.write(doubleNewline);
            this.output.write(payload);
        }

        private void writeBadRequest(String httpVersion, String httpMethod) throws IOException {
            this.output.write(WebServer.toHTTPBytes(httpVersion));
            this.output.write(WebServer.toHTTPBytes(" 400 Bad Request"));
            this.output.write(newline);
            this.output.write(server);
            this.output.write(newline);
            this.output.write(WebServer.toHTTPBytes("Method " + httpMethod + " not implemented (try POST)"));
        }

        private void writeUnauthorized(String httpVersion, String httpMethod) throws IOException {
            this.output.write(WebServer.toHTTPBytes(httpVersion));
            this.output.write(WebServer.toHTTPBytes(" 401 Unauthorized"));
            this.output.write(newline);
            this.output.write(server);
            this.output.write(wwwAuthenticate);
            this.output.write(newline);
            this.output.write(WebServer.toHTTPBytes("Method " + httpMethod + " requires a " + "valid user name and password"));
        }
    }

    class Runner
    implements Runnable {
        Thread thread;
        Connection con;
        int count;

        Runner() {
        }

        public synchronized void handle(Socket socket) throws IOException {
            this.con = new Connection(socket);
            this.count = 0;
            if (this.thread == null || !this.thread.isAlive()) {
                this.thread = new Thread(WebServer.this.runners, this);
                this.thread.start();
            } else {
                this.notify();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            while (this.con != null && Thread.currentThread() == this.thread) {
                this.con.run();
                ++this.count;
                this.con = null;
                if (this.count > 200 || WebServer.this.threadpool.size() > 20) {
                    return;
                }
                Runner runner = this;
                synchronized (runner) {
                    WebServer.this.repoolRunner(this);
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ir) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}


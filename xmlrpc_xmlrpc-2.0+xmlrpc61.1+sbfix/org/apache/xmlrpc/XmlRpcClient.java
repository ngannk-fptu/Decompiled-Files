/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;
import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.DefaultXmlRpcTransport;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcClientWorker;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;

public class XmlRpcClient
implements XmlRpcHandler {
    protected URL url;
    private String storedUser;
    private String storedPassword;
    protected Stack pool = new Stack();
    protected int workers = 0;
    protected int asyncWorkers = 0;
    protected XmlRpcTransportFactory transportFactory;
    private CallData first;
    private CallData last;
    private int maxThreads = -1;

    public XmlRpcClient(URL url, XmlRpcTransportFactory transportFactory) {
        this.url = url;
        this.transportFactory = transportFactory;
    }

    public XmlRpcClient(URL url) {
        this.url = url;
        if (XmlRpc.debug) {
            System.out.println("Created client to url space " + url);
        }
    }

    public XmlRpcClient(String url) throws MalformedURLException {
        this(new URL(url));
    }

    public XmlRpcClient(String hostname, int port) throws MalformedURLException {
        this(new URL("http://" + hostname + ':' + port + "/RPC2"));
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getMaxThreads() {
        if (this.maxThreads == -1) {
            return XmlRpc.getMaxThreads();
        }
        return this.maxThreads;
    }

    public URL getURL() {
        return this.url;
    }

    public void setBasicAuthentication(String user, String password) {
        this.storedUser = user;
        this.storedPassword = password;
    }

    public Object execute(String method, Vector params) throws XmlRpcException, IOException {
        if (this.storedUser != null && this.storedPassword != null && this.transportFactory == null) {
            DefaultXmlRpcTransport transport = this.createDefaultTransport();
            transport.setBasicAuthentication(this.storedUser, this.storedPassword);
            return this.execute(new XmlRpcRequest(method, params), transport);
        }
        return this.execute(new XmlRpcRequest(method, params));
    }

    public Object execute(XmlRpcClientRequest request) throws XmlRpcException, IOException {
        return this.execute(request, this.createTransport());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object execute(XmlRpcClientRequest request, XmlRpcTransport transport) throws XmlRpcException, IOException {
        XmlRpcClientWorker worker = this.getWorker(false);
        try {
            Object retval;
            Object object = retval = worker.execute(request, transport);
            return object;
        }
        finally {
            this.releaseWorker(worker, false);
        }
    }

    public void executeAsync(String method, Vector params, AsyncCallback callback) {
        XmlRpcRequest request = new XmlRpcRequest(method, params);
        if (this.storedUser != null && this.storedPassword != null && this.transportFactory == null) {
            DefaultXmlRpcTransport transport = this.createDefaultTransport();
            transport.setBasicAuthentication(this.storedUser, this.storedPassword);
            this.executeAsync(request, callback, transport);
        } else {
            this.executeAsync(request, callback);
        }
    }

    public void executeAsync(XmlRpcClientRequest request, AsyncCallback callback) {
        this.executeAsync(request, callback, null);
    }

    public void executeAsync(XmlRpcClientRequest request, AsyncCallback callback, XmlRpcTransport transport) {
        CallData call = new CallData(request, callback, transport);
        if (this.asyncWorkers >= 4) {
            this.enqueue(call);
            return;
        }
        Object worker = null;
        try {
            new XmlRpcClientAsyncThread(this.getWorker(true), call).start();
        }
        catch (IOException iox) {
            this.enqueue(call);
        }
    }

    synchronized XmlRpcClientWorker getWorker(boolean async) throws IOException {
        try {
            XmlRpcClientWorker w = (XmlRpcClientWorker)this.pool.pop();
            if (async) {
                ++this.asyncWorkers;
            } else {
                ++this.workers;
            }
            return w;
        }
        catch (EmptyStackException x) {
            if (this.workers < this.getMaxThreads()) {
                if (async) {
                    ++this.asyncWorkers;
                } else {
                    ++this.workers;
                }
                return new XmlRpcClientWorker();
            }
            throw new IOException("XML-RPC System overload");
        }
    }

    synchronized void releaseWorker(XmlRpcClientWorker w, boolean async) {
        if (this.pool.size() < 20) {
            this.pool.push(w);
        }
        if (async) {
            --this.asyncWorkers;
        } else {
            --this.workers;
        }
    }

    synchronized void enqueue(CallData call) {
        if (this.last == null) {
            this.first = this.last = call;
        } else {
            this.last.next = call;
            this.last = call;
        }
    }

    synchronized CallData dequeue() {
        if (this.first == null) {
            return null;
        }
        CallData call = this.first;
        if (this.first == this.last) {
            this.last = null;
            this.first = null;
        } else {
            this.first = this.first.next;
        }
        return call;
    }

    protected XmlRpcTransport createTransport() throws XmlRpcClientException {
        if (this.transportFactory == null) {
            return this.createDefaultTransport();
        }
        return this.transportFactory.createTransport();
    }

    private DefaultXmlRpcTransport createDefaultTransport() {
        return new DefaultXmlRpcTransport(this.url);
    }

    public static void main(String[] args) throws Exception {
        try {
            String url = args[0];
            String method = args[1];
            Vector<Object> v = new Vector<Object>();
            for (int i = 2; i < args.length; ++i) {
                try {
                    v.addElement(new Integer(Integer.parseInt(args[i])));
                    continue;
                }
                catch (NumberFormatException nfx) {
                    v.addElement(args[i]);
                }
            }
            XmlRpcClient client = new XmlRpcClient(url);
            try {
                System.out.println(client.execute(method, v));
            }
            catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
        catch (Exception x) {
            System.err.println(x);
            System.err.println("Usage: java org.apache.xmlrpc.XmlRpcClient <url> <method> <arg> ....");
            System.err.println("Arguments are sent as integers or strings.");
        }
    }

    class CallData {
        XmlRpcClientRequest request;
        XmlRpcTransport transport;
        AsyncCallback callback;
        CallData next;

        public CallData(XmlRpcClientRequest request, AsyncCallback callback, XmlRpcTransport transport) {
            this.request = request;
            this.callback = callback;
            this.transport = transport;
            this.next = null;
        }
    }

    class XmlRpcClientAsyncThread
    extends Thread {
        protected XmlRpcClientWorker worker;
        protected CallData call;

        protected XmlRpcClientAsyncThread(XmlRpcClientWorker worker, CallData initialCall) {
            this.worker = worker;
            this.call = initialCall;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                while (this.call != null) {
                    this.call = XmlRpcClient.this.dequeue();
                    this.executeAsync(this.call.request, this.call.callback, this.call.transport);
                }
            }
            finally {
                XmlRpcClient.this.releaseWorker(this.worker, true);
            }
        }

        void executeAsync(XmlRpcClientRequest request, AsyncCallback callback, XmlRpcTransport transport) {
            block6: {
                Object res = null;
                try {
                    if (transport == null) {
                        transport = XmlRpcClient.this.createTransport();
                    }
                    res = this.worker.execute(request, transport);
                    if (callback != null) {
                        callback.handleResult(res, XmlRpcClient.this.url, request.getMethodName());
                    }
                }
                catch (Exception x) {
                    if (callback == null) break block6;
                    try {
                        callback.handleError(x, XmlRpcClient.this.url, request.getMethodName());
                    }
                    catch (Exception ignore) {
                        // empty catch block
                    }
                }
            }
        }
    }
}


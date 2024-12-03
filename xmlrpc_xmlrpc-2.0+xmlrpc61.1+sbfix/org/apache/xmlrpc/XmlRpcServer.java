/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.InputStream;
import java.util.EmptyStackException;
import java.util.Stack;
import org.apache.xmlrpc.DefaultHandlerMapping;
import org.apache.xmlrpc.DefaultXmlRpcContext;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcContext;
import org.apache.xmlrpc.XmlRpcHandlerMapping;
import org.apache.xmlrpc.XmlRpcWorker;

public class XmlRpcServer {
    private Stack pool = new Stack();
    private int nbrWorkers = 0;
    private int maxThreads = -1;
    private DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping();

    public void addHandler(String handlerName, Object handler) {
        this.handlerMapping.addHandler(handlerName, handler);
    }

    public void removeHandler(String handlerName) {
        this.handlerMapping.removeHandler(handlerName);
    }

    public XmlRpcHandlerMapping getHandlerMapping() {
        return this.handlerMapping;
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

    public byte[] execute(InputStream is) {
        return this.execute(is, new DefaultXmlRpcContext(null, null, this.getHandlerMapping()));
    }

    public byte[] execute(InputStream is, String user, String password) {
        return this.execute(is, new DefaultXmlRpcContext(user, password, this.getHandlerMapping()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] execute(InputStream is, XmlRpcContext context) {
        XmlRpcWorker worker = this.getWorker();
        try {
            byte[] byArray = worker.execute(is, context);
            return byArray;
        }
        finally {
            this.pool.push(worker);
        }
    }

    protected XmlRpcWorker getWorker() {
        try {
            return (XmlRpcWorker)this.pool.pop();
        }
        catch (EmptyStackException x) {
            int maxThreads = this.getMaxThreads();
            if (this.nbrWorkers < maxThreads) {
                ++this.nbrWorkers;
                if ((double)this.nbrWorkers >= (double)maxThreads * 0.95) {
                    System.out.println("95% of XML-RPC server threads in use");
                }
                return this.createWorker();
            }
            throw new RuntimeException("System overload: Maximum number of concurrent requests (" + maxThreads + ") exceeded");
        }
    }

    protected XmlRpcWorker createWorker() {
        return new XmlRpcWorker(this.handlerMapping);
    }
}


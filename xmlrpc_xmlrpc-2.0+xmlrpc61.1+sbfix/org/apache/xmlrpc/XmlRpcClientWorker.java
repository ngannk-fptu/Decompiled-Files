/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcClientRequestProcessor;
import org.apache.xmlrpc.XmlRpcClientResponseProcessor;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcTransport;

public class XmlRpcClientWorker {
    protected XmlRpcClientRequestProcessor requestProcessor;
    protected XmlRpcClientResponseProcessor responseProcessor;
    private static final Object PROCESSING_ERROR_FLAG = new Object();

    public XmlRpcClientWorker() {
        this(new XmlRpcClientRequestProcessor(), new XmlRpcClientResponseProcessor());
    }

    public XmlRpcClientWorker(XmlRpcClientRequestProcessor requestProcessor, XmlRpcClientResponseProcessor responseProcessor) {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Object execute(XmlRpcClientRequest xmlRpcRequest, XmlRpcTransport transport) throws XmlRpcException, XmlRpcClientException, IOException {
        Object object;
        long now = 0L;
        if (XmlRpc.debug) {
            now = System.currentTimeMillis();
        }
        boolean endClientRequestDone = false;
        try {
            block13: {
                try {
                    byte[] request = this.requestProcessor.encodeRequestBytes(xmlRpcRequest, this.responseProcessor.getEncoding());
                    InputStream is = transport.sendXmlRpc(request);
                    Object response = this.responseProcessor.decodeResponse(is);
                    endClientRequestDone = true;
                    transport.endClientRequest();
                    if (response != null && response instanceof XmlRpcException) {
                        throw (XmlRpcException)response;
                    }
                    object = response;
                    Object var11_12 = null;
                    if (!XmlRpc.debug) break block13;
                }
                catch (IOException ioe) {
                    throw ioe;
                }
                catch (XmlRpcClientException xrce) {
                    throw xrce;
                }
                catch (RuntimeException x) {
                    if (!XmlRpc.debug) throw new XmlRpcClientException("Unexpected exception in client processing", x);
                    x.printStackTrace();
                    throw new XmlRpcClientException("Unexpected exception in client processing", x);
                }
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis in request/process/response");
            }
            if (endClientRequestDone) return object;
        }
        catch (Throwable throwable) {
            Object var11_13 = null;
            if (XmlRpc.debug) {
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis in request/process/response");
            }
            if (endClientRequestDone) throw throwable;
            try {
                transport.endClientRequest();
                throw throwable;
            }
            catch (Throwable ignore) {
                throw throwable;
            }
        }
        try {}
        catch (Throwable ignore) {
            // empty catch block
            return object;
        }
        transport.endClientRequest();
        return object;
    }

    protected boolean canReUse() {
        return this.responseProcessor.canReUse() && this.requestProcessor.canReUse();
    }
}


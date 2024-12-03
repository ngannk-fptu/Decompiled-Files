/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.InputStream;
import java.util.Vector;
import org.apache.xmlrpc.ParseFailed;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcServerRequest;

public class XmlRpcRequestProcessor
extends XmlRpc {
    private Vector requestParams = new Vector();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XmlRpcServerRequest decodeRequest(InputStream is) {
        long now = 0L;
        if (XmlRpc.debug) {
            now = System.currentTimeMillis();
        }
        try {
            try {
                this.parse(is);
            }
            catch (Exception e) {
                throw new ParseFailed(e);
            }
            if (XmlRpc.debug) {
                System.out.println("XML-RPC method name: " + this.methodName);
                System.out.println("Request parameters: " + this.requestParams);
            }
            if (this.errorLevel > 0) {
                throw new ParseFailed(this.errorMsg);
            }
            XmlRpcRequest xmlRpcRequest = new XmlRpcRequest(this.methodName, (Vector)this.requestParams.clone());
            Object var6_5 = null;
            this.requestParams.removeAllElements();
            if (XmlRpc.debug) {
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis decoding request");
            }
            return xmlRpcRequest;
        }
        catch (Throwable throwable) {
            block8: {
                Object var6_6 = null;
                this.requestParams.removeAllElements();
                if (!XmlRpc.debug) break block8;
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis decoding request");
            }
            throw throwable;
        }
    }

    protected void objectParsed(Object what) {
        this.requestParams.addElement(what);
    }
}


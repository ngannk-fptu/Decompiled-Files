/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Hashtable;
import java.util.Vector;
import org.apache.xmlrpc.ContextXmlRpcHandler;
import org.apache.xmlrpc.XmlRpcContext;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcWorker;

public class MultiCall
implements ContextXmlRpcHandler {
    public Object execute(String method, Vector params, XmlRpcContext context) throws Exception {
        if ("multicall".equals(method)) {
            return this.multicall(params, context);
        }
        throw new NoSuchMethodException("No method '" + method + "' in " + this.getClass().getName());
    }

    public Vector multicall(Vector requests, XmlRpcContext context) {
        requests = (Vector)requests.elementAt(0);
        Vector<Cloneable> response = new Vector<Cloneable>();
        for (int i = 0; i < requests.size(); ++i) {
            try {
                Hashtable call = (Hashtable)requests.elementAt(i);
                XmlRpcRequest request = new XmlRpcRequest((String)call.get("methodName"), (Vector)call.get("params"));
                Object handler = context.getHandlerMapping().getHandler(request.getMethodName());
                Vector<Object> v = new Vector<Object>();
                v.addElement(XmlRpcWorker.invokeHandler(handler, request, context));
                response.addElement(v);
                continue;
            }
            catch (Exception x) {
                String message = x.toString();
                int code = x instanceof XmlRpcException ? ((XmlRpcException)x).code : 0;
                Hashtable<String, Object> h = new Hashtable<String, Object>();
                h.put("faultString", message);
                h.put("faultCode", new Integer(code));
                response.addElement(h);
            }
        }
        return response;
    }
}


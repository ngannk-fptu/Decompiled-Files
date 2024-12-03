/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.InputStream;
import org.apache.xmlrpc.AuthenticatedXmlRpcHandler;
import org.apache.xmlrpc.AuthenticationFailed;
import org.apache.xmlrpc.ContextXmlRpcHandler;
import org.apache.xmlrpc.DefaultXmlRpcContext;
import org.apache.xmlrpc.ParseFailed;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcContext;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcHandlerMapping;
import org.apache.xmlrpc.XmlRpcRequestProcessor;
import org.apache.xmlrpc.XmlRpcResponseProcessor;
import org.apache.xmlrpc.XmlRpcServerRequest;

public class XmlRpcWorker {
    protected XmlRpcRequestProcessor requestProcessor = new XmlRpcRequestProcessor();
    protected XmlRpcResponseProcessor responseProcessor = new XmlRpcResponseProcessor();
    protected XmlRpcHandlerMapping handlerMapping;

    public XmlRpcWorker(XmlRpcHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    protected static Object invokeHandler(Object handler, XmlRpcServerRequest request, XmlRpcContext context) throws Exception {
        block12: {
            long now;
            block11: {
                block10: {
                    now = 0L;
                    try {
                        if (XmlRpc.debug) {
                            now = System.currentTimeMillis();
                        }
                        if (handler == null) {
                            throw new NullPointerException("Null handler passed to XmlRpcWorker.invokeHandler");
                        }
                        if (!(handler instanceof ContextXmlRpcHandler)) break block10;
                        Object object = ((ContextXmlRpcHandler)handler).execute(request.getMethodName(), request.getParameters(), context);
                        Object var7_7 = null;
                        if (XmlRpc.debug) {
                            System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis processing request");
                        }
                        return object;
                    }
                    catch (Throwable throwable) {
                        block13: {
                            Object var7_10 = null;
                            if (!XmlRpc.debug) break block13;
                            System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis processing request");
                        }
                        throw throwable;
                    }
                }
                if (!(handler instanceof XmlRpcHandler)) break block11;
                Object object = ((XmlRpcHandler)handler).execute(request.getMethodName(), request.getParameters());
                Object var7_8 = null;
                if (XmlRpc.debug) {
                    System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis processing request");
                }
                return object;
            }
            if (!(handler instanceof AuthenticatedXmlRpcHandler)) break block12;
            Object object = ((AuthenticatedXmlRpcHandler)handler).execute(request.getMethodName(), request.getParameters(), context.getUserName(), context.getPassword());
            Object var7_9 = null;
            if (XmlRpc.debug) {
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis processing request");
            }
            return object;
        }
        throw new ClassCastException("Handler class " + handler.getClass().getName() + " is not a valid XML-RPC handler");
    }

    public byte[] execute(InputStream is, String user, String password) {
        return this.execute(is, this.defaultContext(user, password));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public byte[] execute(InputStream is, XmlRpcContext context) {
        long now = 0L;
        if (XmlRpc.debug) {
            now = System.currentTimeMillis();
        }
        try {
            byte[] byArray;
            try {
                XmlRpcServerRequest request = this.requestProcessor.decodeRequest(is);
                Object handler = this.handlerMapping.getHandler(request.getMethodName());
                Object response = XmlRpcWorker.invokeHandler(handler, request, context);
                byArray = this.responseProcessor.encodeResponse(response, this.requestProcessor.getEncoding());
                Object var10_12 = null;
                if (!XmlRpc.debug) return byArray;
            }
            catch (AuthenticationFailed alertCallerAuth) {
                throw alertCallerAuth;
            }
            catch (ParseFailed alertCallerParse) {
                throw alertCallerParse;
            }
            catch (Exception x) {
                if (XmlRpc.debug) {
                    x.printStackTrace();
                }
                byte[] byArray2 = this.responseProcessor.encodeException(x, this.requestProcessor.getEncoding());
                Object var10_13 = null;
                if (!XmlRpc.debug) return byArray2;
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis in request/process/response");
                return byArray2;
            }
            System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis in request/process/response");
            return byArray;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            if (!XmlRpc.debug) throw throwable;
            System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis in request/process/response");
            throw throwable;
        }
    }

    protected XmlRpcContext defaultContext(String user, String password) {
        return new DefaultXmlRpcContext(user, password, this.handlerMapping);
    }
}


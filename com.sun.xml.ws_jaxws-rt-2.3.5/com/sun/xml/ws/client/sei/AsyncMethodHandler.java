/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.AsyncHandler
 *  javax.xml.ws.Response
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.client.AsyncInvoker;
import com.sun.xml.ws.client.AsyncResponseImpl;
import com.sun.xml.ws.client.RequestContext;
import com.sun.xml.ws.client.ResponseContext;
import com.sun.xml.ws.client.sei.MethodHandler;
import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.client.sei.ValueGetterFactory;
import java.lang.reflect.Method;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

abstract class AsyncMethodHandler
extends MethodHandler {
    AsyncMethodHandler(SEIStub owner, Method m) {
        super(owner, m);
    }

    protected final Response<Object> doInvoke(Object proxy, Object[] args, AsyncHandler handler) {
        SEIAsyncInvoker invoker = new SEIAsyncInvoker(proxy, args);
        invoker.setNonNullAsyncHandlerGiven(handler != null);
        AsyncResponseImpl<Object> ft = new AsyncResponseImpl<Object>((Runnable)invoker, handler);
        invoker.setReceiver(ft);
        ft.run();
        return ft;
    }

    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.ASYNC;
    }

    private class SEIAsyncInvoker
    extends AsyncInvoker {
        private final RequestContext rc;
        private final Object[] args;

        SEIAsyncInvoker(Object proxy, Object[] args) {
            this.rc = AsyncMethodHandler.this.owner.requestContext.copy();
            this.args = args;
        }

        @Override
        public void do_run() {
            JavaCallInfo call = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, this.args);
            Packet req = (Packet)AsyncMethodHandler.this.owner.databinding.serializeRequest(call);
            Fiber.CompletionCallback callback = new Fiber.CompletionCallback(){

                @Override
                public void onCompletion(@NotNull Packet response) {
                    SEIAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(response));
                    Message msg = response.getMessage();
                    if (msg == null) {
                        return;
                    }
                    try {
                        Object[] rargs = new Object[1];
                        JavaCallInfo call = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, rargs);
                        call = AsyncMethodHandler.this.owner.databinding.deserializeResponse(response, call);
                        if (call.getException() != null) {
                            throw call.getException();
                        }
                        SEIAsyncInvoker.this.responseImpl.set(rargs[0], null);
                    }
                    catch (Throwable t) {
                        if (t instanceof RuntimeException) {
                            if (t instanceof WebServiceException) {
                                SEIAsyncInvoker.this.responseImpl.set(null, t);
                                return;
                            }
                        } else if (t instanceof Exception) {
                            SEIAsyncInvoker.this.responseImpl.set(null, t);
                            return;
                        }
                        SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(t));
                    }
                }

                @Override
                public void onCompletion(@NotNull Throwable error) {
                    if (error instanceof WebServiceException) {
                        SEIAsyncInvoker.this.responseImpl.set(null, error);
                    } else {
                        SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(error));
                    }
                }
            };
            AsyncMethodHandler.this.owner.doProcessAsync(this.responseImpl, req, this.rc, callback);
        }
    }
}


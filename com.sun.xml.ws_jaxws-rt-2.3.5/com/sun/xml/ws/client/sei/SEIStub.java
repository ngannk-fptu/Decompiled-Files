/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.client.sei;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.databinding.Databinding;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.MEP;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.AsyncResponseImpl;
import com.sun.xml.ws.client.RequestContext;
import com.sun.xml.ws.client.ResponseContextReceiver;
import com.sun.xml.ws.client.Stub;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.sei.AsyncMethodHandler;
import com.sun.xml.ws.client.sei.CallbackMethodHandler;
import com.sun.xml.ws.client.sei.MethodHandler;
import com.sun.xml.ws.client.sei.PollingMethodHandler;
import com.sun.xml.ws.client.sei.SyncMethodHandler;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.SOAPSEIModel;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public final class SEIStub
extends Stub
implements InvocationHandler {
    Databinding databinding;
    public final SOAPSEIModel seiModel;
    public final SOAPVersion soapVersion;
    private final Map<Method, MethodHandler> methodHandlers = new HashMap<Method, MethodHandler>();

    @Deprecated
    public SEIStub(WSServiceDelegate owner, BindingImpl binding, SOAPSEIModel seiModel, Tube master, WSEndpointReference epr) {
        super(owner, master, binding, seiModel.getPort(), seiModel.getPort().getAddress(), epr);
        this.seiModel = seiModel;
        this.soapVersion = binding.getSOAPVersion();
        this.databinding = seiModel.getDatabinding();
        this.initMethodHandlers();
    }

    public SEIStub(WSPortInfo portInfo, BindingImpl binding, SOAPSEIModel seiModel, WSEndpointReference epr) {
        super(portInfo, binding, seiModel.getPort().getAddress(), epr);
        this.seiModel = seiModel;
        this.soapVersion = binding.getSOAPVersion();
        this.databinding = seiModel.getDatabinding();
        this.initMethodHandlers();
    }

    private void initMethodHandlers() {
        HashMap<WSDLBoundOperation, JavaMethodImpl> syncs = new HashMap<WSDLBoundOperation, JavaMethodImpl>();
        for (JavaMethodImpl m : this.seiModel.getJavaMethods()) {
            if (m.getMEP().isAsync) continue;
            SyncMethodHandler handler = new SyncMethodHandler(this, m);
            syncs.put(m.getOperation(), m);
            this.methodHandlers.put(m.getMethod(), handler);
        }
        for (JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
            AsyncMethodHandler handler;
            Method m;
            JavaMethodImpl sync = (JavaMethodImpl)syncs.get(jm.getOperation());
            if (jm.getMEP() == MEP.ASYNC_CALLBACK) {
                m = jm.getMethod();
                handler = new CallbackMethodHandler(this, m, m.getParameterTypes().length - 1);
                this.methodHandlers.put(m, handler);
            }
            if (jm.getMEP() != MEP.ASYNC_POLL) continue;
            m = jm.getMethod();
            handler = new PollingMethodHandler(this, m);
            this.methodHandlers.put(m, handler);
        }
    }

    @Override
    @Nullable
    public OperationDispatcher getOperationDispatcher() {
        if (this.operationDispatcher == null && this.wsdlPort != null) {
            this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, this.seiModel);
        }
        return this.operationDispatcher;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.validateInputs(proxy, method);
        Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            MethodHandler handler = this.methodHandlers.get(method);
            if (handler != null) {
                Object object = handler.invoke(proxy, args);
                return object;
            }
            Object object = method.invoke((Object)this, args);
            return object;
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    private void validateInputs(Object proxy, Method method) {
        if (proxy == null || !Proxy.isProxyClass(proxy.getClass())) {
            throw new IllegalStateException("Passed object is not proxy!");
        }
        if (method == null || method.getDeclaringClass() == null || Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Invoking static method is not allowed!");
        }
    }

    public final Packet doProcess(Packet request, RequestContext rc, ResponseContextReceiver receiver) {
        return super.process(request, rc, receiver);
    }

    public final void doProcessAsync(AsyncResponseImpl<?> receiver, Packet request, RequestContext rc, Fiber.CompletionCallback callback) {
        super.processAsync(receiver, request, rc, callback);
    }

    @Override
    @NotNull
    protected final QName getPortName() {
        return this.wsdlPort.getName();
    }

    @Override
    public void setOutboundHeaders(Object ... headers) {
        if (headers == null) {
            throw new IllegalArgumentException();
        }
        Header[] hl = new Header[headers.length];
        for (int i = 0; i < hl.length; ++i) {
            if (headers[i] == null) {
                throw new IllegalArgumentException();
            }
            hl[i] = Headers.create(this.seiModel.getBindingContext(), headers[i]);
        }
        super.setOutboundHeaders(hl);
    }
}


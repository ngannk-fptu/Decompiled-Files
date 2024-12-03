/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.db;

import com.oracle.webservices.api.message.MessageContext;
import com.sun.xml.ws.api.databinding.ClientCallBridge;
import com.sun.xml.ws.api.databinding.Databinding;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.ws.api.databinding.JavaCallInfo;
import com.sun.xml.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageContextFactory;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.MEP;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.sei.StubAsyncHandler;
import com.sun.xml.ws.client.sei.StubHandler;
import com.sun.xml.ws.db.DatabindingProviderImpl;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.RuntimeModeler;
import com.sun.xml.ws.server.sei.TieHandler;
import com.sun.xml.ws.wsdl.ActionBasedOperationSignature;
import com.sun.xml.ws.wsdl.DispatchException;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import com.sun.xml.ws.wsdl.writer.WSDLGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public final class DatabindingImpl
implements Databinding {
    AbstractSEIModelImpl seiModel;
    Map<Method, StubHandler> stubHandlers;
    Map<JavaMethodImpl, TieHandler> wsdlOpMap = new HashMap<JavaMethodImpl, TieHandler>();
    Map<Method, TieHandler> tieHandlers = new HashMap<Method, TieHandler>();
    OperationDispatcher operationDispatcher;
    OperationDispatcher operationDispatcherNoWsdl;
    boolean clientConfig = false;
    Codec codec;
    MessageContextFactory packetFactory = null;

    public DatabindingImpl(DatabindingProviderImpl p, DatabindingConfig config) {
        RuntimeModeler modeler = new RuntimeModeler(config);
        modeler.setClassLoader(config.getClassLoader());
        this.seiModel = modeler.buildRuntimeModel();
        WSDLPort wsdlport = config.getWsdlPort();
        Object facProp = config.properties().get("com.sun.xml.ws.api.message.MessageContextFactory");
        this.packetFactory = facProp != null && facProp instanceof MessageContextFactory ? (MessageContextFactory)facProp : new MessageContextFactory(this.seiModel.getWSBinding().getFeatures());
        this.clientConfig = this.isClientConfig(config);
        if (this.clientConfig) {
            this.initStubHandlers();
        }
        this.seiModel.setDatabinding(this);
        if (wsdlport != null) {
            this.freeze(wsdlport);
        }
        if (this.operationDispatcher == null) {
            this.operationDispatcherNoWsdl = new OperationDispatcher(null, this.seiModel.getWSBinding(), this.seiModel);
        }
        for (JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
            if (jm.isAsync()) continue;
            TieHandler th = new TieHandler(jm, this.seiModel.getWSBinding(), this.packetFactory);
            this.wsdlOpMap.put(jm, th);
            this.tieHandlers.put(th.getMethod(), th);
        }
    }

    private boolean isClientConfig(DatabindingConfig config) {
        if (config.getContractClass() == null) {
            return false;
        }
        if (!config.getContractClass().isInterface()) {
            return false;
        }
        return config.getEndpointClass() == null || config.getEndpointClass().isInterface();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void freeze(WSDLPort port) {
        if (this.clientConfig) {
            return;
        }
        DatabindingImpl databindingImpl = this;
        synchronized (databindingImpl) {
            if (this.operationDispatcher == null) {
                this.operationDispatcher = port == null ? null : new OperationDispatcher(port, this.seiModel.getWSBinding(), this.seiModel);
            }
        }
    }

    public SEIModel getModel() {
        return this.seiModel;
    }

    private void initStubHandlers() {
        this.stubHandlers = new HashMap<Method, StubHandler>();
        HashMap<ActionBasedOperationSignature, JavaMethodImpl> syncs = new HashMap<ActionBasedOperationSignature, JavaMethodImpl>();
        for (JavaMethodImpl m : this.seiModel.getJavaMethods()) {
            if (m.getMEP().isAsync) continue;
            StubHandler handler = new StubHandler(m, this.packetFactory);
            syncs.put(m.getOperationSignature(), m);
            this.stubHandlers.put(m.getMethod(), handler);
        }
        for (JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
            JavaMethodImpl sync = (JavaMethodImpl)syncs.get(jm.getOperationSignature());
            if (jm.getMEP() != MEP.ASYNC_CALLBACK && jm.getMEP() != MEP.ASYNC_POLL) continue;
            Method m = jm.getMethod();
            StubAsyncHandler handler = new StubAsyncHandler(jm, sync, this.packetFactory);
            this.stubHandlers.put(m, handler);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    JavaMethodImpl resolveJavaMethod(Packet req) throws DispatchException {
        WSDLOperationMapping m = req.getWSDLOperationMapping();
        if (m == null) {
            DatabindingImpl databindingImpl = this;
            synchronized (databindingImpl) {
                m = this.operationDispatcher != null ? this.operationDispatcher.getWSDLOperationMapping(req) : this.operationDispatcherNoWsdl.getWSDLOperationMapping(req);
            }
        }
        return (JavaMethodImpl)m.getJavaMethod();
    }

    public com.oracle.webservices.api.databinding.JavaCallInfo deserializeRequest(Packet req) {
        JavaCallInfo call = new JavaCallInfo();
        try {
            JavaMethodImpl wsdlOp = this.resolveJavaMethod(req);
            TieHandler tie = this.wsdlOpMap.get(wsdlOp);
            call.setMethod(tie.getMethod());
            Object[] args = tie.readRequest(req.getMessage());
            call.setParameters(args);
        }
        catch (DispatchException e) {
            call.setException(e);
        }
        return call;
    }

    public com.oracle.webservices.api.databinding.JavaCallInfo deserializeResponse(Packet res, com.oracle.webservices.api.databinding.JavaCallInfo call) {
        StubHandler stubHandler = this.stubHandlers.get(call.getMethod());
        try {
            return stubHandler.readResponse(res, call);
        }
        catch (Throwable e) {
            call.setException(e);
            return call;
        }
    }

    public WebServiceFeature[] getFeatures() {
        return null;
    }

    @Override
    public Packet serializeRequest(com.oracle.webservices.api.databinding.JavaCallInfo call) {
        StubHandler stubHandler = this.stubHandlers.get(call.getMethod());
        Packet p = stubHandler.createRequestPacket(call);
        p.setState(Packet.State.ClientRequest);
        return p;
    }

    @Override
    public Packet serializeResponse(com.oracle.webservices.api.databinding.JavaCallInfo call) {
        TieHandler th;
        Method method = call.getMethod();
        Message message = null;
        if (method != null && (th = this.tieHandlers.get(method)) != null) {
            return th.serializeResponse(call);
        }
        if (call.getException() instanceof DispatchException) {
            message = ((DispatchException)call.getException()).fault;
        }
        Packet p = (Packet)this.packetFactory.createContext(message);
        p.setState(Packet.State.ServerResponse);
        return p;
    }

    @Override
    public ClientCallBridge getClientBridge(Method method) {
        return this.stubHandlers.get(method);
    }

    @Override
    public void generateWSDL(WSDLGenInfo info) {
        WSDLGenerator wsdlGen = new WSDLGenerator(this.seiModel, info.getWsdlResolver(), this.seiModel.getWSBinding(), info.getContainer(), this.seiModel.getEndpointClass(), info.isInlineSchemas(), info.isSecureXmlProcessingDisabled(), info.getExtensions());
        wsdlGen.doGeneration();
    }

    @Override
    public EndpointCallBridge getEndpointBridge(Packet req) throws DispatchException {
        JavaMethodImpl wsdlOp = this.resolveJavaMethod(req);
        return this.wsdlOpMap.get(wsdlOp);
    }

    Codec getCodec() {
        if (this.codec == null) {
            this.codec = ((BindingImpl)this.seiModel.getWSBinding()).createCodec();
        }
        return this.codec;
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) throws IOException {
        return this.getCodec().encode(packet, out);
    }

    @Override
    public void decode(InputStream in, String ct, Packet p) throws IOException {
        this.getCodec().decode(in, ct, p);
    }

    @Override
    public com.oracle.webservices.api.databinding.JavaCallInfo createJavaCallInfo(Method method, Object[] args) {
        return new JavaCallInfo(method, args);
    }

    @Override
    public com.oracle.webservices.api.databinding.JavaCallInfo deserializeResponse(MessageContext message, com.oracle.webservices.api.databinding.JavaCallInfo call) {
        return this.deserializeResponse((Packet)message, call);
    }

    @Override
    public com.oracle.webservices.api.databinding.JavaCallInfo deserializeRequest(MessageContext message) {
        return this.deserializeRequest((Packet)message);
    }

    @Override
    public MessageContextFactory getMessageContextFactory() {
        return this.packetFactory;
    }
}


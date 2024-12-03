/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.RespectBindingFeature
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 *  org.glassfish.gmbal.ManagedObjectManager
 */
package com.sun.xml.ws.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.addressing.WSEPRExtension;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentFeature;
import com.sun.xml.ws.api.ComponentRegistry;
import com.sun.xml.ws.api.ComponentsFeature;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Engine;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptorFactory;
import com.sun.xml.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.AsyncResponseImpl;
import com.sun.xml.ws.client.MonitorRootClient;
import com.sun.xml.ws.client.RequestContext;
import com.sun.xml.ws.client.ResponseContext;
import com.sun.xml.ws.client.ResponseContextReceiver;
import com.sun.xml.ws.client.SEIPortInfo;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.ws.model.SOAPSEIModel;
import com.sun.xml.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.ws.model.wsdl.WSDLProperties;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.util.Pool;
import com.sun.xml.ws.util.RuntimeVersion;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.glassfish.gmbal.ManagedObjectManager;

public abstract class Stub
implements WSBindingProvider,
ResponseContextReceiver,
ComponentRegistry {
    public static final String PREVENT_SYNC_START_FOR_ASYNC_INVOKE = "com.sun.xml.ws.client.StubRequestSyncStartForAsyncInvoke";
    private Pool<Tube> tubes;
    private final Engine engine;
    protected final WSServiceDelegate owner;
    @Nullable
    protected WSEndpointReference endpointReference;
    protected final BindingImpl binding;
    protected final WSPortInfo portInfo;
    protected AddressingVersion addrVersion;
    public RequestContext requestContext = new RequestContext();
    private final RequestContext cleanRequestContext;
    private ResponseContext responseContext;
    @Nullable
    protected final WSDLPort wsdlPort;
    protected QName portname;
    @Nullable
    private volatile Header[] userOutboundHeaders;
    @NotNull
    private final WSDLProperties wsdlProperties;
    protected OperationDispatcher operationDispatcher = null;
    @NotNull
    private final ManagedObjectManager managedObjectManager;
    private boolean managedObjectManagerClosed = false;
    private final Set<Component> components = new CopyOnWriteArraySet<Component>();
    private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.ws.monitoring");

    @Deprecated
    protected Stub(WSServiceDelegate owner, Tube master, BindingImpl binding, WSDLPort wsdlPort, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
        this(owner, master, null, null, binding, wsdlPort, defaultEndPointAddress, epr);
    }

    @Deprecated
    protected Stub(QName portname, WSServiceDelegate owner, Tube master, BindingImpl binding, WSDLPort wsdlPort, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
        this(owner, master, null, portname, binding, wsdlPort, defaultEndPointAddress, epr);
    }

    protected Stub(WSPortInfo portInfo, BindingImpl binding, Tube master, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
        this((WSServiceDelegate)portInfo.getOwner(), master, portInfo, null, binding, portInfo.getPort(), defaultEndPointAddress, epr);
    }

    protected Stub(WSPortInfo portInfo, BindingImpl binding, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
        this(portInfo, binding, null, defaultEndPointAddress, epr);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Stub(WSServiceDelegate owner, @Nullable Tube master, @Nullable WSPortInfo portInfo, QName portname, BindingImpl binding, @Nullable WSDLPort wsdlPort, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
        Container old = ContainerResolver.getDefault().enterContainer(owner.getContainer());
        try {
            ComponentsFeature csf;
            this.owner = owner;
            this.portInfo = portInfo;
            this.wsdlPort = wsdlPort != null ? wsdlPort : (portInfo != null ? portInfo.getPort() : null);
            this.portname = portname;
            if (portname == null) {
                if (portInfo != null) {
                    this.portname = portInfo.getPortName();
                } else if (wsdlPort != null) {
                    this.portname = wsdlPort.getName();
                }
            }
            this.binding = binding;
            ComponentFeature cf = binding.getFeature(ComponentFeature.class);
            if (cf != null && ComponentFeature.Target.STUB.equals((Object)cf.getTarget())) {
                this.components.add(cf.getComponent());
            }
            if ((csf = binding.getFeature(ComponentsFeature.class)) != null) {
                for (ComponentFeature cfi : csf.getComponentFeatures()) {
                    if (!ComponentFeature.Target.STUB.equals((Object)cfi.getTarget())) continue;
                    this.components.add(cfi.getComponent());
                }
            }
            if (epr != null) {
                this.requestContext.setEndPointAddressString(epr.getAddress());
            } else {
                this.requestContext.setEndpointAddress(defaultEndPointAddress);
            }
            this.engine = new Engine(this.getStringId(), owner.getContainer(), owner.getExecutor());
            this.endpointReference = epr;
            this.wsdlProperties = wsdlPort == null ? new WSDLDirectProperties(owner.getServiceName(), portname) : new WSDLPortProperties(wsdlPort);
            this.cleanRequestContext = this.requestContext.copy();
            this.managedObjectManager = new MonitorRootClient(this).createManagedObjectManager(this);
            this.tubes = master != null ? new Pool.TubePool(master) : new Pool.TubePool(this.createPipeline(portInfo, binding));
            this.addrVersion = binding.getAddressingVersion();
            this.managedObjectManager.resumeJMXRegistration();
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    private Tube createPipeline(WSPortInfo portInfo, WSBinding binding) {
        Stub.checkAllWSDLExtensionsUnderstood(portInfo, binding);
        SOAPSEIModel seiModel = null;
        Class sei = null;
        if (portInfo instanceof SEIPortInfo) {
            SEIPortInfo sp = (SEIPortInfo)portInfo;
            seiModel = sp.model;
            sei = sp.sei;
        }
        BindingID bindingId = portInfo.getBindingId();
        TubelineAssembler assembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), bindingId, this.owner.getContainer());
        if (assembler == null) {
            throw new WebServiceException("Unable to process bindingID=" + bindingId);
        }
        return assembler.createClient(new ClientTubeAssemblerContext(portInfo.getEndpointAddress(), portInfo.getPort(), this, binding, this.owner.getContainer(), ((BindingImpl)binding).createCodec(), (SEIModel)seiModel, sei));
    }

    public WSDLPort getWSDLPort() {
        return this.wsdlPort;
    }

    public WSService getService() {
        return this.owner;
    }

    public Pool<Tube> getTubes() {
        return this.tubes;
    }

    private static void checkAllWSDLExtensionsUnderstood(WSPortInfo port, WSBinding binding) {
        if (port.getPort() != null && binding.isFeatureEnabled(RespectBindingFeature.class)) {
            port.getPort().areRequiredExtensionsUnderstood();
        }
    }

    @Override
    public WSPortInfo getPortInfo() {
        return this.portInfo;
    }

    @Nullable
    public OperationDispatcher getOperationDispatcher() {
        if (this.operationDispatcher == null && this.wsdlPort != null) {
            this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, null);
        }
        return this.operationDispatcher;
    }

    @NotNull
    protected abstract QName getPortName();

    @NotNull
    protected final QName getServiceName() {
        return this.owner.getServiceName();
    }

    public final Executor getExecutor() {
        return this.owner.getExecutor();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Packet process(Packet packet, RequestContext requestContext, ResponseContextReceiver receiver) {
        Packet reply;
        Packet packet2;
        packet.isSynchronousMEP = true;
        packet.component = this;
        this.configureRequestPacket(packet, requestContext);
        Pool<Tube> pool = this.tubes;
        if (pool == null) {
            throw new WebServiceException("close method has already been invoked");
        }
        Fiber fiber = this.engine.createFiber();
        this.configureFiber(fiber);
        Tube tube = pool.take();
        try {
            packet2 = fiber.runSync(tube, packet);
            reply = fiber.getPacket() == null ? packet : fiber.getPacket();
        }
        catch (Throwable throwable) {
            Packet reply2 = fiber.getPacket() == null ? packet : fiber.getPacket();
            receiver.setResponseContext(new ResponseContext(reply2));
            pool.recycle(tube);
            throw throwable;
        }
        receiver.setResponseContext(new ResponseContext(reply));
        pool.recycle(tube);
        return packet2;
    }

    private void configureRequestPacket(Packet packet, RequestContext requestContext) {
        packet.proxy = this;
        packet.handlerConfig = this.binding.getHandlerConfig();
        Header[] hl = this.userOutboundHeaders;
        if (hl != null) {
            MessageHeaders mh = packet.getMessage().getHeaders();
            for (Header h : hl) {
                mh.add(h);
            }
        }
        requestContext.fill(packet, this.binding.getAddressingVersion() != null);
        packet.addSatellite(this.wsdlProperties);
        if (this.addrVersion != null) {
            MessageHeaders headerList = packet.getMessage().getHeaders();
            AddressingUtils.fillRequestAddressingHeaders(headerList, this.wsdlPort, this.binding, packet);
            if (this.endpointReference != null) {
                this.endpointReference.addReferenceParametersToList(packet.getMessage().getHeaders());
            }
        }
    }

    protected final void processAsync(AsyncResponseImpl<?> receiver, Packet request, RequestContext requestContext, final Fiber.CompletionCallback completionCallback) {
        request.component = this;
        this.configureRequestPacket(request, requestContext);
        final Pool<Tube> pool = this.tubes;
        if (pool == null) {
            throw new WebServiceException("close method has already been invoked");
        }
        Fiber fiber = this.engine.createFiber();
        this.configureFiber(fiber);
        receiver.setCancelable(fiber);
        if (receiver.isCancelled()) {
            return;
        }
        FiberContextSwitchInterceptorFactory fcsif = this.owner.getSPI(FiberContextSwitchInterceptorFactory.class);
        if (fcsif != null) {
            fiber.addInterceptor(fcsif.create());
        }
        final Tube tube = pool.take();
        Fiber.CompletionCallback fiberCallback = new Fiber.CompletionCallback(){

            @Override
            public void onCompletion(@NotNull Packet response) {
                pool.recycle(tube);
                completionCallback.onCompletion(response);
            }

            @Override
            public void onCompletion(@NotNull Throwable error) {
                completionCallback.onCompletion(error);
            }
        };
        fiber.start(tube, request, fiberCallback, this.getBinding().isFeatureEnabled(SyncStartForAsyncFeature.class) && !requestContext.containsKey(PREVENT_SYNC_START_FOR_ASYNC_INVOKE));
    }

    protected void configureFiber(Fiber fiber) {
    }

    @Override
    public void close() {
        Pool.TubePool tp = (Pool.TubePool)this.tubes;
        if (tp != null) {
            Tube p = tp.takeMaster();
            p.preDestroy();
            this.tubes = null;
        }
        if (!this.managedObjectManagerClosed) {
            try {
                ObjectName name = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
                if (name != null) {
                    monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", name);
                }
                this.managedObjectManager.close();
            }
            catch (IOException e) {
                monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", e);
            }
            this.managedObjectManagerClosed = true;
        }
    }

    public final WSBinding getBinding() {
        return this.binding;
    }

    public final Map<String, Object> getRequestContext() {
        return this.requestContext.asMap();
    }

    public void resetRequestContext() {
        this.requestContext = this.cleanRequestContext.copy();
    }

    public final ResponseContext getResponseContext() {
        return this.responseContext;
    }

    @Override
    public void setResponseContext(ResponseContext rc) {
        this.responseContext = rc;
    }

    private String getStringId() {
        return RuntimeVersion.VERSION + ": Stub for " + this.getRequestContext().get("javax.xml.ws.service.endpoint.address");
    }

    public String toString() {
        return this.getStringId();
    }

    @Override
    public final WSEndpointReference getWSEndpointReference() {
        if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
            throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference(Class<T> class)", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
        }
        if (this.endpointReference != null) {
            return this.endpointReference;
        }
        String eprAddress = this.requestContext.getEndpointAddress().toString();
        QName portTypeName = null;
        String wsdlAddress = null;
        ArrayList<WSEndpointReference.EPRExtension> wsdlEPRExtensions = new ArrayList<WSEndpointReference.EPRExtension>();
        if (this.wsdlPort != null) {
            portTypeName = this.wsdlPort.getBinding().getPortTypeName();
            wsdlAddress = eprAddress + "?wsdl";
            try {
                WSEndpointReference wsdlEpr = this.wsdlPort.getEPR();
                if (wsdlEpr != null) {
                    for (WSEndpointReference.EPRExtension extnEl : wsdlEpr.getEPRExtensions()) {
                        wsdlEPRExtensions.add(new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader((XMLStreamReader)extnEl.readAsXMLStreamReader()), extnEl.getQName()));
                    }
                }
            }
            catch (XMLStreamException ex) {
                throw new WebServiceException((Throwable)ex);
            }
        }
        AddressingVersion av = AddressingVersion.W3C;
        this.endpointReference = new WSEndpointReference(av, eprAddress, this.getServiceName(), this.getPortName(), portTypeName, null, wsdlAddress, null, wsdlEPRExtensions, null);
        return this.endpointReference;
    }

    public final W3CEndpointReference getEndpointReference() {
        if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
            throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference()", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
        }
        return this.getEndpointReference(W3CEndpointReference.class);
    }

    public final <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
        return this.getWSEndpointReference().toSpec(clazz);
    }

    @Override
    @NotNull
    public ManagedObjectManager getManagedObjectManager() {
        return this.managedObjectManager;
    }

    @Override
    public final void setOutboundHeaders(List<Header> headers) {
        if (headers == null) {
            this.userOutboundHeaders = null;
        } else {
            for (Header h : headers) {
                if (h != null) continue;
                throw new IllegalArgumentException();
            }
            this.userOutboundHeaders = headers.toArray(new Header[headers.size()]);
        }
    }

    @Override
    public final void setOutboundHeaders(Header ... headers) {
        if (headers == null) {
            this.userOutboundHeaders = null;
        } else {
            for (Header h : headers) {
                if (h != null) continue;
                throw new IllegalArgumentException();
            }
            Header[] hl = new Header[headers.length];
            System.arraycopy(headers, 0, hl, 0, headers.length);
            this.userOutboundHeaders = hl;
        }
    }

    @Override
    public final List<Header> getInboundHeaders() {
        return Collections.unmodifiableList(((MessageHeaders)this.responseContext.get("com.sun.xml.ws.api.message.HeaderList")).asList());
    }

    @Override
    public final void setAddress(String address) {
        this.requestContext.put("javax.xml.ws.service.endpoint.address", address);
    }

    @Override
    public <S> S getSPI(Class<S> spiType) {
        for (Component c : this.components) {
            S s = c.getSPI(spiType);
            if (s == null) continue;
            return s;
        }
        return this.owner.getSPI(spiType);
    }

    @Override
    public Set<Component> getComponents() {
        return this.components;
    }
}


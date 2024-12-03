/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.ws.policy.PolicyMap
 *  javax.annotation.PreDestroy
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.handler.Handler
 *  org.glassfish.gmbal.ManagedObjectManager
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.addressing.EPRSDDocumentFilter;
import com.sun.xml.ws.addressing.WSEPRExtension;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentFeature;
import com.sun.xml.ws.api.ComponentsFeature;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.Engine;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.api.server.EndpointAwareCodec;
import com.sun.xml.ws.api.server.EndpointReferenceExtensionContributor;
import com.sun.xml.ws.api.server.LazyMOMProvider;
import com.sun.xml.ws.api.server.TransportBackChannel;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.ws.model.wsdl.WSDLProperties;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.resources.HandlerMessages;
import com.sun.xml.ws.server.EndpointAwareTube;
import com.sun.xml.ws.server.MonitorRootService;
import com.sun.xml.ws.server.ServiceDefinitionImpl;
import com.sun.xml.ws.server.WSEndpointMOMProxy;
import com.sun.xml.ws.util.Pool;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import org.glassfish.gmbal.ManagedObjectManager;
import org.w3c.dom.Element;

public class WSEndpointImpl<T>
extends WSEndpoint<T>
implements LazyMOMProvider.WSEndpointScopeChangeListener {
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server.endpoint");
    @NotNull
    private final QName serviceName;
    @NotNull
    private final QName portName;
    protected final WSBinding binding;
    private final SEIModel seiModel;
    @NotNull
    private final Container container;
    private final WSDLPort port;
    protected final Tube masterTubeline;
    private final ServiceDefinitionImpl serviceDef;
    private final SOAPVersion soapVersion;
    private final Engine engine;
    @NotNull
    private final Codec masterCodec;
    @NotNull
    private final PolicyMap endpointPolicy;
    private final Pool<Tube> tubePool;
    private final OperationDispatcher operationDispatcher;
    @NotNull
    private ManagedObjectManager managedObjectManager;
    private boolean managedObjectManagerClosed = false;
    private final Object managedObjectManagerLock = new Object();
    private LazyMOMProvider.Scope lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
    @NotNull
    private final ServerTubeAssemblerContext context;
    private Map<QName, WSEndpointReference.EPRExtension> endpointReferenceExtensions = new HashMap<QName, WSEndpointReference.EPRExtension>();
    private boolean disposed;
    private final Class<T> implementationClass;
    @NotNull
    private final WSDLProperties wsdlProperties;
    private final Set<Component> componentRegistry = new CopyOnWriteArraySet<Component>();
    private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.ws.monitoring");

    protected WSEndpointImpl(@NotNull QName serviceName, @NotNull QName portName, WSBinding binding, Container container, SEIModel seiModel, WSDLPort port, Class<T> implementationClass, @Nullable ServiceDefinitionImpl serviceDef, EndpointAwareTube terminalTube, boolean isSynchronous, PolicyMap endpointPolicy) {
        ComponentsFeature csf;
        ComponentFeature cf;
        this.serviceName = serviceName;
        this.portName = portName;
        this.binding = binding;
        this.soapVersion = binding.getSOAPVersion();
        this.container = container;
        this.port = port;
        this.implementationClass = implementationClass;
        this.serviceDef = serviceDef;
        this.seiModel = seiModel;
        this.endpointPolicy = endpointPolicy;
        LazyMOMProvider.INSTANCE.registerEndpoint(this);
        this.initManagedObjectManager();
        if (serviceDef != null) {
            serviceDef.setOwner(this);
        }
        if ((cf = binding.getFeature(ComponentFeature.class)) != null) {
            switch (cf.getTarget()) {
                case ENDPOINT: {
                    this.componentRegistry.add(cf.getComponent());
                    break;
                }
                case CONTAINER: {
                    container.getComponents().add(cf.getComponent());
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        if ((csf = binding.getFeature(ComponentsFeature.class)) != null) {
            block10: for (ComponentFeature cfi : csf.getComponentFeatures()) {
                switch (cfi.getTarget()) {
                    case ENDPOINT: {
                        this.componentRegistry.add(cfi.getComponent());
                        continue block10;
                    }
                    case CONTAINER: {
                        container.getComponents().add(cfi.getComponent());
                        continue block10;
                    }
                }
                throw new IllegalArgumentException();
            }
        }
        TubelineAssembler assembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), binding.getBindingId(), container);
        assert (assembler != null);
        this.operationDispatcher = port == null ? null : new OperationDispatcher(port, binding, seiModel);
        this.context = this.createServerTubeAssemblerContext(terminalTube, isSynchronous);
        this.masterTubeline = assembler.createServer(this.context);
        Codec c = this.context.getCodec();
        if (c instanceof EndpointAwareCodec) {
            c = c.copy();
            ((EndpointAwareCodec)c).setEndpoint(this);
        }
        this.masterCodec = c;
        this.tubePool = new Pool.TubePool(this.masterTubeline);
        terminalTube.setEndpoint(this);
        this.engine = new Engine(this.toString(), container);
        this.wsdlProperties = port == null ? new WSDLDirectProperties(serviceName, portName, seiModel) : new WSDLPortProperties(port, seiModel);
        HashMap<QName, WSEndpointReference.EPRExtension> eprExtensions = new HashMap<QName, WSEndpointReference.EPRExtension>();
        try {
            WSEndpointReference wsdlEpr;
            if (port != null && (wsdlEpr = port.getEPR()) != null) {
                for (WSEndpointReference.EPRExtension extnEl : wsdlEpr.getEPRExtensions()) {
                    eprExtensions.put(extnEl.getQName(), extnEl);
                }
            }
            EndpointReferenceExtensionContributor[] eprExtnContributors = ServiceFinder.find(EndpointReferenceExtensionContributor.class).toArray();
            for (EndpointReferenceExtensionContributor eprExtnContributor : eprExtnContributors) {
                WSEndpointReference.EPRExtension wsdlEPRExtn = (WSEndpointReference.EPRExtension)eprExtensions.remove(eprExtnContributor.getQName());
                WSEndpointReference.EPRExtension endpointEprExtn = eprExtnContributor.getEPRExtension(this, wsdlEPRExtn);
                if (endpointEprExtn == null) continue;
                eprExtensions.put(endpointEprExtn.getQName(), endpointEprExtn);
            }
            for (WSEndpointReference.EPRExtension extn : eprExtensions.values()) {
                this.endpointReferenceExtensions.put(extn.getQName(), new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader((XMLStreamReader)extn.readAsXMLStreamReader()), extn.getQName()));
            }
        }
        catch (XMLStreamException ex) {
            throw new WebServiceException((Throwable)ex);
        }
        if (!eprExtensions.isEmpty()) {
            serviceDef.addFilter(new EPRSDDocumentFilter(this));
        }
    }

    protected ServerTubeAssemblerContext createServerTubeAssemblerContext(EndpointAwareTube terminalTube, boolean isSynchronous) {
        ServerPipeAssemblerContext ctx = new ServerPipeAssemblerContext(this.seiModel, this.port, this, terminalTube, isSynchronous);
        return ctx;
    }

    protected WSEndpointImpl(@NotNull QName serviceName, @NotNull QName portName, WSBinding binding, Container container, SEIModel seiModel, WSDLPort port, Tube masterTubeline) {
        this.serviceName = serviceName;
        this.portName = portName;
        this.binding = binding;
        this.soapVersion = binding.getSOAPVersion();
        this.container = container;
        this.endpointPolicy = null;
        this.port = port;
        this.seiModel = seiModel;
        this.serviceDef = null;
        this.implementationClass = null;
        this.masterTubeline = masterTubeline;
        this.masterCodec = ((BindingImpl)this.binding).createCodec();
        LazyMOMProvider.INSTANCE.registerEndpoint(this);
        this.initManagedObjectManager();
        this.operationDispatcher = port == null ? null : new OperationDispatcher(port, binding, seiModel);
        this.context = new ServerPipeAssemblerContext(seiModel, port, this, null, false);
        this.tubePool = new Pool.TubePool(masterTubeline);
        this.engine = new Engine(this.toString(), container);
        this.wsdlProperties = port == null ? new WSDLDirectProperties(serviceName, portName, seiModel) : new WSDLPortProperties(port, seiModel);
    }

    public Collection<WSEndpointReference.EPRExtension> getEndpointReferenceExtensions() {
        return this.endpointReferenceExtensions.values();
    }

    @Override
    @Nullable
    public OperationDispatcher getOperationDispatcher() {
        return this.operationDispatcher;
    }

    @Override
    public PolicyMap getPolicyMap() {
        return this.endpointPolicy;
    }

    @Override
    @NotNull
    public Class<T> getImplementationClass() {
        return this.implementationClass;
    }

    @Override
    @NotNull
    public WSBinding getBinding() {
        return this.binding;
    }

    @Override
    @NotNull
    public Container getContainer() {
        return this.container;
    }

    @Override
    public WSDLPort getPort() {
        return this.port;
    }

    @Override
    @Nullable
    public SEIModel getSEIModel() {
        return this.seiModel;
    }

    @Override
    public void setExecutor(Executor exec) {
        this.engine.setExecutor(exec);
    }

    @Override
    public Engine getEngine() {
        return this.engine;
    }

    @Override
    public void schedule(Packet request, WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        this.processAsync(request, callback, interceptor, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processAsync(final Packet request, final WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor, boolean schedule) {
        Container old = ContainerResolver.getDefault().enterContainer(this.container);
        try {
            request.endpoint = this;
            request.addSatellite(this.wsdlProperties);
            Fiber fiber = this.engine.createFiber();
            fiber.setDeliverThrowableInPacket(true);
            if (interceptor != null) {
                fiber.addInterceptor(interceptor);
            }
            final Tube tube = this.tubePool.take();
            Fiber.CompletionCallback cbak = new Fiber.CompletionCallback(){

                @Override
                public void onCompletion(@NotNull Packet response) {
                    ThrowableContainerPropertySet tc = response.getSatellite(ThrowableContainerPropertySet.class);
                    if (tc == null) {
                        WSEndpointImpl.this.tubePool.recycle(tube);
                    }
                    if (callback != null) {
                        if (tc != null) {
                            response = WSEndpointImpl.this.createServiceResponseForException(tc, response, WSEndpointImpl.this.soapVersion, request.endpoint.getPort(), null, request.endpoint.getBinding());
                        }
                        callback.onCompletion(response);
                    }
                }

                @Override
                public void onCompletion(@NotNull Throwable error) {
                    throw new IllegalStateException();
                }
            };
            fiber.start(tube, request, cbak, this.binding.isFeatureEnabled(SyncStartForAsyncFeature.class) || !schedule);
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    @Override
    public Packet createServiceResponseForException(ThrowableContainerPropertySet tc, Packet responsePacket, SOAPVersion soapVersion, WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
        if (tc.isFaultCreated()) {
            return responsePacket;
        }
        Message faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, tc.getThrowable());
        Packet result = responsePacket.createServerResponse(faultMessage, wsdlPort, seiModel, binding);
        tc.setFaultMessage(faultMessage);
        tc.setResponsePacket(responsePacket);
        tc.setFaultCreated(true);
        return result;
    }

    @Override
    public void process(Packet request, WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        this.processAsync(request, callback, interceptor, false);
    }

    @Override
    @NotNull
    public WSEndpoint.PipeHead createPipeHead() {
        return new WSEndpoint.PipeHead(){
            private final Tube tube;
            {
                this.tube = TubeCloner.clone(WSEndpointImpl.this.masterTubeline);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            @NotNull
            public Packet process(Packet request, WebServiceContextDelegate wscd, TransportBackChannel tbc) {
                Container old = ContainerResolver.getDefault().enterContainer(WSEndpointImpl.this.container);
                try {
                    Packet response;
                    request.webServiceContextDelegate = wscd;
                    request.transportBackChannel = tbc;
                    request.endpoint = WSEndpointImpl.this;
                    request.addSatellite(WSEndpointImpl.this.wsdlProperties);
                    Fiber fiber = WSEndpointImpl.this.engine.createFiber();
                    try {
                        response = fiber.runSync(this.tube, request);
                    }
                    catch (RuntimeException re) {
                        Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(WSEndpointImpl.this.soapVersion, null, re);
                        response = request.createServerResponse(faultMsg, request.endpoint.getPort(), null, request.endpoint.getBinding());
                    }
                    Packet packet = response;
                    return packet;
                }
                finally {
                    ContainerResolver.getDefault().exitContainer(old);
                }
            }
        };
    }

    @Override
    public synchronized void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        this.masterTubeline.preDestroy();
        block2: for (Handler handler : this.binding.getHandlerChain()) {
            for (Method method : handler.getClass().getMethods()) {
                if (method.getAnnotation(PreDestroy.class) == null) continue;
                try {
                    method.invoke((Object)handler, new Object[0]);
                }
                catch (Exception e) {
                    logger.log(Level.WARNING, HandlerMessages.HANDLER_PREDESTROY_IGNORE(e.getMessage()), e);
                }
                continue block2;
            }
        }
        this.closeManagedObjectManager();
        LazyMOMProvider.INSTANCE.unregisterEndpoint(this);
    }

    @Override
    public ServiceDefinitionImpl getServiceDefinition() {
        return this.serviceDef;
    }

    @Override
    @NotNull
    public Set<Component> getComponents() {
        return this.componentRegistry;
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, Element ... referenceParameters) {
        List<Element> refParams = null;
        if (referenceParameters != null) {
            refParams = Arrays.asList(referenceParameters);
        }
        return this.getEndpointReference(clazz, address, wsdlAddress, null, refParams);
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, List<Element> metadata, List<Element> referenceParameters) {
        QName portType = null;
        if (this.port != null) {
            portType = this.port.getBinding().getPortTypeName();
        }
        AddressingVersion av = AddressingVersion.fromSpecClass(clazz);
        return new WSEndpointReference(av, address, this.serviceName, this.portName, portType, metadata, wsdlAddress, referenceParameters, this.endpointReferenceExtensions.values(), null).toSpec(clazz);
    }

    @Override
    @NotNull
    public QName getPortName() {
        return this.portName;
    }

    @Override
    @NotNull
    public Codec createCodec() {
        return this.masterCodec.copy();
    }

    @Override
    @NotNull
    public QName getServiceName() {
        return this.serviceName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initManagedObjectManager() {
        Object object = this.managedObjectManagerLock;
        synchronized (object) {
            if (this.managedObjectManager == null) {
                switch (this.lazyMOMProviderScope) {
                    case GLASSFISH_NO_JMX: {
                        this.managedObjectManager = new WSEndpointMOMProxy(this);
                        break;
                    }
                    default: {
                        this.managedObjectManager = this.obtainManagedObjectManager();
                    }
                }
            }
        }
    }

    @Override
    @NotNull
    public ManagedObjectManager getManagedObjectManager() {
        return this.managedObjectManager;
    }

    @NotNull
    ManagedObjectManager obtainManagedObjectManager() {
        MonitorRootService monitorRootService = new MonitorRootService(this);
        ManagedObjectManager mOM = monitorRootService.createManagedObjectManager(this);
        mOM.resumeJMXRegistration();
        return mOM;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void scopeChanged(LazyMOMProvider.Scope scope) {
        Object object = this.managedObjectManagerLock;
        synchronized (object) {
            if (this.managedObjectManagerClosed) {
                return;
            }
            this.lazyMOMProviderScope = scope;
            if (this.managedObjectManager == null) {
                this.managedObjectManager = scope != LazyMOMProvider.Scope.GLASSFISH_NO_JMX ? this.obtainManagedObjectManager() : new WSEndpointMOMProxy(this);
            } else if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
                ((WSEndpointMOMProxy)this.managedObjectManager).setManagedObjectManager(this.obtainManagedObjectManager());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void closeManagedObjectManager() {
        Object object = this.managedObjectManagerLock;
        synchronized (object) {
            if (this.managedObjectManagerClosed) {
                return;
            }
            if (this.managedObjectManager != null) {
                boolean close = true;
                if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
                    close = false;
                }
                if (close) {
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
                }
            }
            this.managedObjectManagerClosed = true;
        }
    }

    @Override
    @NotNull
    public ServerTubeAssemblerContext getAssemblerContext() {
        return this.context;
    }
}


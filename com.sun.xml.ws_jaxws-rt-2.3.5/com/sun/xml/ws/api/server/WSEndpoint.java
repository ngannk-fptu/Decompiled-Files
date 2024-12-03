/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.ws.policy.PolicyMap
 *  javax.xml.ws.EndpointReference
 *  org.glassfish.gmbal.ManagedObjectManager
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentRegistry;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.ws.api.config.management.ManagedEndpointFactory;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.Engine;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.EndpointComponent;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.TransportBackChannel;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.server.EndpointAwareTube;
import com.sun.xml.ws.server.EndpointFactory;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.glassfish.gmbal.ManagedObjectManager;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public abstract class WSEndpoint<T>
implements ComponentRegistry {
    @NotNull
    public abstract Codec createCodec();

    @NotNull
    public abstract QName getServiceName();

    @NotNull
    public abstract QName getPortName();

    @NotNull
    public abstract Class<T> getImplementationClass();

    @NotNull
    public abstract WSBinding getBinding();

    @NotNull
    public abstract Container getContainer();

    @Nullable
    public abstract WSDLPort getPort();

    public abstract void setExecutor(@NotNull Executor var1);

    public final void schedule(@NotNull Packet request, @NotNull CompletionCallback callback) {
        this.schedule(request, callback, null);
    }

    public abstract void schedule(@NotNull Packet var1, @NotNull CompletionCallback var2, @Nullable FiberContextSwitchInterceptor var3);

    public void process(@NotNull Packet request, @NotNull CompletionCallback callback, @Nullable FiberContextSwitchInterceptor interceptor) {
        this.schedule(request, callback, interceptor);
    }

    public Engine getEngine() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public abstract PipeHead createPipeHead();

    public abstract void dispose();

    @Nullable
    public abstract ServiceDefinition getServiceDefinition();

    public List<BoundEndpoint> getBoundEndpoints() {
        Module m = this.getContainer().getSPI(Module.class);
        return m != null ? m.getBoundEndpoints() : null;
    }

    @Deprecated
    @NotNull
    public Set<EndpointComponent> getComponentRegistry() {
        Set<Component> componentRegistry = this.getComponents();
        EndpointComponentSet sec = new EndpointComponentSet(componentRegistry);
        for (Component c : this.getComponents()) {
            sec.add(c instanceof EndpointComponentWrapper ? ((EndpointComponentWrapper)c).component : new ComponentWrapper(c));
        }
        return sec;
    }

    @Override
    @NotNull
    public Set<Component> getComponents() {
        return Collections.emptySet();
    }

    @Override
    @Nullable
    public <S> S getSPI(@NotNull Class<S> spiType) {
        Set<Component> componentRegistry = this.getComponents();
        if (componentRegistry != null) {
            for (Component c : componentRegistry) {
                S s = c.getSPI(spiType);
                if (s == null) continue;
                return s;
            }
        }
        return this.getContainer().getSPI(spiType);
    }

    @Nullable
    public abstract SEIModel getSEIModel();

    public abstract PolicyMap getPolicyMap();

    @NotNull
    public abstract ManagedObjectManager getManagedObjectManager();

    public abstract void closeManagedObjectManager();

    @NotNull
    public abstract ServerTubeAssemblerContext getAssemblerContext();

    public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable EntityResolver resolver, boolean isTransportSynchronous) {
        return WSEndpoint.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
    }

    public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable EntityResolver resolver, boolean isTransportSynchronous, boolean isStandard) {
        WSEndpoint<T> endpoint = EndpointFactory.createEndpoint(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, isStandard);
        Iterator<ManagedEndpointFactory> managementFactories = ServiceFinder.find(ManagedEndpointFactory.class).iterator();
        if (managementFactories.hasNext()) {
            ManagedEndpointFactory managementFactory = managementFactories.next();
            EndpointCreationAttributes attributes = new EndpointCreationAttributes(processHandlerAnnotation, invoker, resolver, isTransportSynchronous);
            WSEndpoint<T> managedEndpoint = managementFactory.createEndpoint(endpoint, attributes);
            if (endpoint.getAssemblerContext().getTerminalTube() instanceof EndpointAwareTube) {
                ((EndpointAwareTube)endpoint.getAssemblerContext().getTerminalTube()).setEndpoint(managedEndpoint);
            }
            return managedEndpoint;
        }
        return endpoint;
    }

    @Deprecated
    public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable EntityResolver resolver) {
        return WSEndpoint.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, false);
    }

    public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable URL catalogUrl) {
        return WSEndpoint.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, XmlUtil.createEntityResolver(catalogUrl), false);
    }

    @NotNull
    public static QName getDefaultServiceName(Class endpointClass) {
        return WSEndpoint.getDefaultServiceName(endpointClass, true, null);
    }

    @NotNull
    public static QName getDefaultServiceName(Class endpointClass, MetadataReader metadataReader) {
        return WSEndpoint.getDefaultServiceName(endpointClass, true, metadataReader);
    }

    @NotNull
    public static QName getDefaultServiceName(Class endpointClass, boolean isStandard) {
        return WSEndpoint.getDefaultServiceName(endpointClass, isStandard, null);
    }

    @NotNull
    public static QName getDefaultServiceName(Class endpointClass, boolean isStandard, MetadataReader metadataReader) {
        return EndpointFactory.getDefaultServiceName(endpointClass, isStandard, metadataReader);
    }

    @NotNull
    public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass) {
        return WSEndpoint.getDefaultPortName(serviceName, endpointClass, null);
    }

    @NotNull
    public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass, MetadataReader metadataReader) {
        return WSEndpoint.getDefaultPortName(serviceName, endpointClass, true, metadataReader);
    }

    @NotNull
    public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass, boolean isStandard) {
        return WSEndpoint.getDefaultPortName(serviceName, endpointClass, isStandard, null);
    }

    @NotNull
    public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass, boolean isStandard, MetadataReader metadataReader) {
        return EndpointFactory.getDefaultPortName(serviceName, endpointClass, isStandard, metadataReader);
    }

    public abstract <T extends EndpointReference> T getEndpointReference(Class<T> var1, String var2, String var3, Element ... var4);

    public abstract <T extends EndpointReference> T getEndpointReference(Class<T> var1, String var2, String var3, List<Element> var4, List<Element> var5);

    public boolean equalsProxiedInstance(WSEndpoint endpoint) {
        if (endpoint == null) {
            return false;
        }
        return this.equals(endpoint);
    }

    @Nullable
    public abstract OperationDispatcher getOperationDispatcher();

    public abstract Packet createServiceResponseForException(ThrowableContainerPropertySet var1, Packet var2, SOAPVersion var3, WSDLPort var4, SEIModel var5, WSBinding var6);

    private static class EndpointComponentWrapper
    implements Component {
        private final EndpointComponent component;

        public EndpointComponentWrapper(EndpointComponent component) {
            this.component = component;
        }

        @Override
        public <S> S getSPI(Class<S> spiType) {
            return this.component.getSPI(spiType);
        }

        public int hashCode() {
            return this.component.hashCode();
        }

        public boolean equals(Object obj) {
            return this.component.equals(obj);
        }
    }

    private static class ComponentWrapper
    implements EndpointComponent {
        private final Component component;

        public ComponentWrapper(Component component) {
            this.component = component;
        }

        public <S> S getSPI(Class<S> spiType) {
            return this.component.getSPI(spiType);
        }

        public int hashCode() {
            return this.component.hashCode();
        }

        public boolean equals(Object obj) {
            return this.component.equals(obj);
        }
    }

    private class EndpointComponentSet
    extends HashSet<EndpointComponent> {
        private final Set<Component> componentRegistry;

        public EndpointComponentSet(Set<Component> componentRegistry) {
            this.componentRegistry = componentRegistry;
        }

        @Override
        public Iterator<EndpointComponent> iterator() {
            final Iterator it = super.iterator();
            return new Iterator<EndpointComponent>(){
                private EndpointComponent last = null;

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public EndpointComponent next() {
                    this.last = (EndpointComponent)it.next();
                    return this.last;
                }

                @Override
                public void remove() {
                    it.remove();
                    if (this.last != null) {
                        EndpointComponentSet.this.componentRegistry.remove(this.last instanceof ComponentWrapper ? ((ComponentWrapper)this.last).component : new EndpointComponentWrapper(this.last));
                    }
                    this.last = null;
                }
            };
        }

        @Override
        public boolean add(EndpointComponent e) {
            boolean result = super.add(e);
            if (result) {
                this.componentRegistry.add(new EndpointComponentWrapper(e));
            }
            return result;
        }

        @Override
        public boolean remove(Object o) {
            boolean result = super.remove(o);
            if (result) {
                this.componentRegistry.remove(o instanceof ComponentWrapper ? ((ComponentWrapper)o).component : new EndpointComponentWrapper((EndpointComponent)o));
            }
            return result;
        }
    }

    public static interface PipeHead {
        @NotNull
        public Packet process(@NotNull Packet var1, @Nullable WebServiceContextDelegate var2, @Nullable TransportBackChannel var3);
    }

    public static interface CompletionCallback {
        public void onCompletion(@NotNull Packet var1);
    }
}


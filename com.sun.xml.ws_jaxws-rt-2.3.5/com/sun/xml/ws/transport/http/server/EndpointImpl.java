/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferResult
 *  javax.xml.ws.Binding
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.EndpointContext
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.WebServiceContext
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.WebServicePermission
 *  javax.xml.ws.spi.Invoker
 *  javax.xml.ws.spi.http.HttpContext
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 */
package com.sun.xml.ws.transport.http.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.net.httpserver.HttpContext;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.InstanceResolver;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.server.EndpointFactory;
import com.sun.xml.ws.server.ServerRtException;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.HttpAdapterList;
import com.sun.xml.ws.transport.http.server.HttpEndpoint;
import com.sun.xml.ws.transport.http.server.ServerAdapterList;
import com.sun.xml.ws.transport.http.server.ServerContainer;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointContext;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServicePermission;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class EndpointImpl
extends Endpoint {
    private static final WebServicePermission ENDPOINT_PUBLISH_PERMISSION = new WebServicePermission("publishEndpoint");
    private Object actualEndpoint;
    private final WSBinding binding;
    @Nullable
    private final Object implementor;
    private List<Source> metadata;
    private Executor executor;
    private Map<String, Object> properties = Collections.emptyMap();
    private boolean stopped;
    @Nullable
    private EndpointContext endpointContext;
    @NotNull
    private final Class<?> implClass;
    private final Invoker invoker;
    private Container container;

    public EndpointImpl(@NotNull BindingID bindingId, @NotNull Object impl, WebServiceFeature ... features) {
        this(bindingId, impl, impl.getClass(), InstanceResolver.createSingleton(impl).createInvoker(), features);
    }

    public EndpointImpl(@NotNull BindingID bindingId, @NotNull Class implClass, javax.xml.ws.spi.Invoker invoker, WebServiceFeature ... features) {
        this(bindingId, null, implClass, new InvokerImpl(invoker), features);
    }

    private EndpointImpl(@NotNull BindingID bindingId, Object impl, @NotNull Class implClass, Invoker invoker, WebServiceFeature ... features) {
        this.binding = BindingImpl.create(bindingId, features);
        this.implClass = implClass;
        this.invoker = invoker;
        this.implementor = impl;
    }

    public EndpointImpl(WSEndpoint wse, Object serverContext) {
        this(wse, serverContext, null);
    }

    public EndpointImpl(WSEndpoint wse, Object serverContext, EndpointContext ctxt) {
        this.endpointContext = ctxt;
        this.actualEndpoint = new HttpEndpoint(null, this.getAdapter(wse, ""));
        ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
        this.binding = wse.getBinding();
        this.implementor = null;
        this.implClass = null;
        this.invoker = null;
    }

    public EndpointImpl(WSEndpoint wse, String address) {
        this(wse, address, null);
    }

    public EndpointImpl(WSEndpoint wse, String address, EndpointContext ctxt) {
        URL url;
        try {
            url = new URL(address);
        }
        catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Cannot create URL for this address " + address);
        }
        if (!url.getProtocol().equals("http")) {
            throw new IllegalArgumentException(url.getProtocol() + " protocol based address is not supported");
        }
        if (!url.getPath().startsWith("/")) {
            throw new IllegalArgumentException("Incorrect WebService address=" + address + ". The address's path should start with /");
        }
        this.endpointContext = ctxt;
        this.actualEndpoint = new HttpEndpoint(null, this.getAdapter(wse, url.getPath()));
        ((HttpEndpoint)this.actualEndpoint).publish(address);
        this.binding = wse.getBinding();
        this.implementor = null;
        this.implClass = null;
        this.invoker = null;
    }

    public Binding getBinding() {
        return this.binding;
    }

    public Object getImplementor() {
        return this.implementor;
    }

    public void publish(String address) {
        URL url;
        this.canPublish();
        try {
            url = new URL(address);
        }
        catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Cannot create URL for this address " + address);
        }
        if (!url.getProtocol().equals("http")) {
            throw new IllegalArgumentException(url.getProtocol() + " protocol based address is not supported");
        }
        if (!url.getPath().startsWith("/")) {
            throw new IllegalArgumentException("Incorrect WebService address=" + address + ". The address's path should start with /");
        }
        this.createEndpoint(url.getPath());
        ((HttpEndpoint)this.actualEndpoint).publish(address);
    }

    public void publish(Object serverContext) {
        this.canPublish();
        if (!HttpContext.class.isAssignableFrom(serverContext.getClass())) {
            throw new IllegalArgumentException(serverContext.getClass() + " is not a supported context.");
        }
        this.createEndpoint(((HttpContext)serverContext).getPath());
        ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
    }

    public void publish(javax.xml.ws.spi.http.HttpContext serverContext) {
        this.canPublish();
        this.createEndpoint(serverContext.getPath());
        ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
    }

    public void stop() {
        if (this.isPublished()) {
            ((HttpEndpoint)this.actualEndpoint).stop();
            this.actualEndpoint = null;
            this.stopped = true;
        }
    }

    public boolean isPublished() {
        return this.actualEndpoint != null;
    }

    public List<Source> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(List<Source> metadata) {
        if (this.isPublished()) {
            throw new IllegalStateException("Cannot set Metadata. Endpoint is already published");
        }
        this.metadata = metadata;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Map<String, Object> getProperties() {
        return new HashMap<String, Object>(this.properties);
    }

    public void setProperties(Map<String, Object> map) {
        this.properties = new HashMap<String, Object>(map);
    }

    private void createEndpoint(String urlPattern) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission((Permission)ENDPOINT_PUBLISH_PERMISSION);
        }
        try {
            Class.forName("com.sun.net.httpserver.HttpServer");
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("Couldn't load light weight http server", e);
        }
        this.container = this.getContainer();
        MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(this.implClass, this.binding);
        WSEndpoint<?> wse = WSEndpoint.create(this.implClass, true, this.invoker, this.getProperty(QName.class, "javax.xml.ws.wsdl.service"), this.getProperty(QName.class, "javax.xml.ws.wsdl.port"), this.container, this.binding, this.getPrimaryWsdl(metadataReader), this.buildDocList(), null, false);
        this.actualEndpoint = new HttpEndpoint(this.executor, this.getAdapter(wse, urlPattern));
    }

    private <T> T getProperty(Class<T> type, String key) {
        Object o = this.properties.get(key);
        if (o == null) {
            return null;
        }
        if (type.isInstance(o)) {
            return type.cast(o);
        }
        throw new IllegalArgumentException("Property " + key + " has to be of type " + type);
    }

    private List<SDDocumentSource> buildDocList() {
        ArrayList<SDDocumentSource> r = new ArrayList<SDDocumentSource>();
        if (this.metadata != null) {
            for (Source source : this.metadata) {
                try {
                    XMLStreamBufferResult xsbr = XmlUtil.identityTransform(source, new XMLStreamBufferResult());
                    String systemId = source.getSystemId();
                    r.add(SDDocumentSource.create(new URL(systemId), (XMLStreamBuffer)xsbr.getXMLStreamBuffer()));
                }
                catch (IOException | ParserConfigurationException | TransformerException | SAXException te) {
                    throw new ServerRtException("server.rt.err", te);
                }
            }
        }
        return r;
    }

    @Nullable
    private SDDocumentSource getPrimaryWsdl(MetadataReader metadataReader) {
        EndpointFactory.verifyImplementorClass(this.implClass, metadataReader);
        String wsdlLocation = EndpointFactory.getWsdlLocation(this.implClass, metadataReader);
        if (wsdlLocation != null) {
            return SDDocumentSource.create(this.implClass, wsdlLocation);
        }
        return null;
    }

    private void canPublish() {
        if (this.isPublished()) {
            throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already published.");
        }
        if (this.stopped) {
            throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already stopped.");
        }
    }

    public EndpointReference getEndpointReference(Element ... referenceParameters) {
        return this.getEndpointReference(W3CEndpointReference.class, referenceParameters);
    }

    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element ... referenceParameters) {
        if (!this.isPublished()) {
            throw new WebServiceException("Endpoint is not published yet");
        }
        return ((HttpEndpoint)this.actualEndpoint).getEndpointReference(clazz, referenceParameters);
    }

    public void setEndpointContext(EndpointContext ctxt) {
        this.endpointContext = ctxt;
    }

    private HttpAdapter getAdapter(WSEndpoint endpoint, String urlPattern) {
        HttpAdapterList adapterList = null;
        if (this.endpointContext != null) {
            if (this.endpointContext instanceof Component) {
                adapterList = ((Component)this.endpointContext).getSPI(HttpAdapterList.class);
            }
            if (adapterList == null) {
                for (Endpoint e : this.endpointContext.getEndpoints()) {
                    if (!e.isPublished() || e == this) continue;
                    adapterList = ((HttpEndpoint)((EndpointImpl)e).actualEndpoint).getAdapterOwner();
                    assert (adapterList != null);
                    break;
                }
            }
        }
        if (adapterList == null) {
            adapterList = new ServerAdapterList();
        }
        return adapterList.createAdapter("", urlPattern, endpoint);
    }

    private Container getContainer() {
        if (this.endpointContext != null) {
            Container c;
            if (this.endpointContext instanceof Component && (c = ((Component)this.endpointContext).getSPI(Container.class)) != null) {
                return c;
            }
            for (Endpoint e : this.endpointContext.getEndpoints()) {
                if (!e.isPublished() || e == this) continue;
                return ((EndpointImpl)e).container;
            }
        }
        return new ServerContainer();
    }

    private static class InvokerImpl
    extends Invoker {
        private javax.xml.ws.spi.Invoker spiInvoker;

        InvokerImpl(javax.xml.ws.spi.Invoker spiInvoker) {
            this.spiInvoker = spiInvoker;
        }

        @Override
        public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
            try {
                this.spiInvoker.inject((WebServiceContext)wsc);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Object invoke(@NotNull Packet p, @NotNull Method m, Object ... args) throws InvocationTargetException, IllegalAccessException {
            return this.spiInvoker.invoke(m, args);
        }
    }
}


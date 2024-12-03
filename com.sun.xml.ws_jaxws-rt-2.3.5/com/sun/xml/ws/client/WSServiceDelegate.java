/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.jws.HandlerChain
 *  javax.jws.WebService
 *  javax.xml.bind.JAXBContext
 *  javax.xml.ws.Dispatch
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.Service
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceClient
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.handler.HandlerResolver
 *  javax.xml.ws.soap.AddressingFeature
 */
package com.sun.xml.ws.client;

import com.oracle.webservices.api.databinding.ExternalMetadataFeature;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.Closeable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.ComponentFeature;
import com.sun.xml.ws.api.ComponentsFeature;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.ServiceInterceptor;
import com.sun.xml.ws.api.client.ServiceInterceptorFactory;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.api.databinding.DatabindingFactory;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.pipe.Stubs;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.client.ClientContainer;
import com.sun.xml.ws.client.HandlerConfigurator;
import com.sun.xml.ws.client.PortInfo;
import com.sun.xml.ws.client.SEIPortInfo;
import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.db.DatabindingImpl;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.ws.model.RuntimeModeler;
import com.sun.xml.ws.model.SOAPSEIModel;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.resources.DispatchMessages;
import com.sun.xml.ws.resources.ProviderApiMessages;
import com.sun.xml.ws.util.JAXWSUtils;
import com.sun.xml.ws.util.ServiceConfigurationError;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.AddressingFeature;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class WSServiceDelegate
extends WSService {
    private final Map<QName, PortInfo> ports = new HashMap<QName, PortInfo>();
    @NotNull
    private HandlerConfigurator handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(null);
    private final Class<? extends Service> serviceClass;
    private final WebServiceFeatureList features;
    @NotNull
    private final QName serviceName;
    private final Map<QName, SEIPortInfo> seiContext = new HashMap<QName, SEIPortInfo>();
    private volatile Executor executor;
    @Nullable
    private WSDLService wsdlService;
    private final Container container;
    @NotNull
    final ServiceInterceptor serviceInterceptor;
    private URL wsdlURL;
    protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];

    protected Map<QName, PortInfo> getQNameToPortInfoMap() {
        return this.ports;
    }

    public WSServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass, WebServiceFeature ... features) {
        this(wsdlDocumentLocation, serviceName, serviceClass, new WebServiceFeatureList(features));
    }

    protected WSServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass, WebServiceFeatureList features) {
        this((Source)(wsdlDocumentLocation == null ? null : new StreamSource(wsdlDocumentLocation.toExternalForm())), serviceName, serviceClass, features);
        this.wsdlURL = wsdlDocumentLocation;
    }

    public WSServiceDelegate(@Nullable Source wsdl, @NotNull QName serviceName, @NotNull Class<? extends Service> serviceClass, WebServiceFeature ... features) {
        this(wsdl, serviceName, serviceClass, new WebServiceFeatureList(features));
    }

    protected WSServiceDelegate(@Nullable Source wsdl, @NotNull QName serviceName, @NotNull Class<? extends Service> serviceClass, WebServiceFeatureList features) {
        this(wsdl, null, serviceName, serviceClass, features);
    }

    public WSServiceDelegate(@Nullable Source wsdl, @Nullable WSDLService service, @NotNull QName serviceName, @NotNull Class<? extends Service> serviceClass, WebServiceFeature ... features) {
        this(wsdl, service, serviceName, serviceClass, new WebServiceFeatureList(features));
    }

    public WSServiceDelegate(@Nullable Source wsdl, @Nullable WSDLService service, @NotNull QName serviceName, final @NotNull Class<? extends Service> serviceClass, WebServiceFeatureList features) {
        HandlerChain handlerChain;
        ComponentsFeature csf;
        Container tContainer;
        if (serviceName == null) {
            throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME_NULL(null));
        }
        this.features = features;
        WSService.InitParams initParams = (WSService.InitParams)INIT_PARAMS.get();
        INIT_PARAMS.set(null);
        if (initParams == null) {
            initParams = EMPTY_PARAMS;
        }
        this.serviceName = serviceName;
        this.serviceClass = serviceClass;
        Container container = tContainer = initParams.getContainer() != null ? initParams.getContainer() : ContainerResolver.getInstance().getContainer();
        if (tContainer == Container.NONE) {
            tContainer = new ClientContainer();
        }
        this.container = tContainer;
        ComponentFeature cf = this.features.get(ComponentFeature.class);
        if (cf != null) {
            switch (cf.getTarget()) {
                case SERVICE: {
                    this.getComponents().add(cf.getComponent());
                    break;
                }
                case CONTAINER: {
                    this.container.getComponents().add(cf.getComponent());
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        if ((csf = this.features.get(ComponentsFeature.class)) != null) {
            block10: for (ComponentFeature cfi : csf.getComponentFeatures()) {
                switch (cfi.getTarget()) {
                    case SERVICE: {
                        this.getComponents().add(cfi.getComponent());
                        continue block10;
                    }
                    case CONTAINER: {
                        this.container.getComponents().add(cfi.getComponent());
                        continue block10;
                    }
                }
                throw new IllegalArgumentException();
            }
        }
        ServiceInterceptor interceptor = ServiceInterceptorFactory.load(this, Thread.currentThread().getContextClassLoader());
        ServiceInterceptor si = this.container.getSPI(ServiceInterceptor.class);
        if (si != null) {
            interceptor = ServiceInterceptor.aggregate(interceptor, si);
        }
        this.serviceInterceptor = interceptor;
        if (service == null) {
            if (wsdl == null && serviceClass != Service.class) {
                WebServiceClient wsClient = AccessController.doPrivileged(new PrivilegedAction<WebServiceClient>(){

                    @Override
                    public WebServiceClient run() {
                        return serviceClass.getAnnotation(WebServiceClient.class);
                    }
                });
                String string2 = wsClient.wsdlLocation();
                string2 = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(string2));
                wsdl = new StreamSource(string2);
            }
            if (wsdl != null) {
                try {
                    URL url = wsdl.getSystemId() == null ? null : JAXWSUtils.getEncodedURL(wsdl.getSystemId());
                    WSDLModel wSDLModel = this.parseWSDL(url, wsdl, serviceClass);
                    service = wSDLModel.getService(this.serviceName);
                    if (service == null) {
                        throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(wSDLModel.getServices().keySet())));
                    }
                    for (WSDLPort wSDLPort : service.getPorts()) {
                        this.ports.put(wSDLPort.getName(), new PortInfo(this, wSDLPort));
                    }
                }
                catch (MalformedURLException e) {
                    throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
                }
            }
        } else {
            for (WSDLPort wSDLPort : service.getPorts()) {
                this.ports.put(wSDLPort.getName(), new PortInfo(this, wSDLPort));
            }
        }
        this.wsdlService = service;
        if (serviceClass != Service.class && (handlerChain = AccessController.doPrivileged(new PrivilegedAction<HandlerChain>(){

            @Override
            public HandlerChain run() {
                return serviceClass.getAnnotation(HandlerChain.class);
            }
        })) != null) {
            this.handlerConfigurator = new HandlerConfigurator.AnnotationConfigurator(this);
        }
    }

    private WSDLModel parseWSDL(URL wsdlDocumentLocation, Source wsdlSource, Class serviceClass) {
        try {
            return RuntimeWSDLParser.parse(wsdlDocumentLocation, wsdlSource, this.createCatalogResolver(), true, this.getContainer(), serviceClass, ServiceFinder.find(WSDLParserExtension.class).toArray());
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (SAXException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (ServiceConfigurationError e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    protected EntityResolver createCatalogResolver() {
        return XmlUtil.createDefaultCatalogResolver();
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public HandlerResolver getHandlerResolver() {
        return this.handlerConfigurator.getResolver();
    }

    final HandlerConfigurator getHandlerConfigurator() {
        return this.handlerConfigurator;
    }

    public void setHandlerResolver(HandlerResolver resolver) {
        this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(resolver);
    }

    public <T> T getPort(QName portName, Class<T> portInterface) throws WebServiceException {
        return this.getPort(portName, portInterface, EMPTY_FEATURES);
    }

    public <T> T getPort(QName portName, Class<T> portInterface, WebServiceFeature ... features) {
        if (portName == null || portInterface == null) {
            throw new IllegalArgumentException();
        }
        WSDLService tWsdlService = this.wsdlService;
        if (tWsdlService == null && (tWsdlService = this.getWSDLModelfromSEI(portInterface)) == null) {
            throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
        }
        WSDLPort portModel = this.getPortModel(tWsdlService, portName);
        return this.getPort(portModel.getEPR(), portName, portInterface, new WebServiceFeatureList(features));
    }

    public <T> T getPort(EndpointReference epr, Class<T> portInterface, WebServiceFeature ... features) {
        return this.getPort(WSEndpointReference.create(epr), portInterface, features);
    }

    @Override
    public <T> T getPort(WSEndpointReference wsepr, Class<T> portInterface, WebServiceFeature ... features) {
        WebServiceFeatureList featureList = new WebServiceFeatureList(features);
        QName portTypeName = RuntimeModeler.getPortTypeName(portInterface, this.getMetadadaReader(featureList, portInterface.getClassLoader()));
        QName portName = this.getPortNameFromEPR(wsepr, portTypeName);
        return this.getPort(wsepr, portName, portInterface, featureList);
    }

    protected <T> T getPort(WSEndpointReference wsepr, QName portName, Class<T> portInterface, WebServiceFeatureList features) {
        ComponentFeature cf = features.get(ComponentFeature.class);
        if (cf != null && !ComponentFeature.Target.STUB.equals((Object)cf.getTarget())) {
            throw new IllegalArgumentException();
        }
        ComponentsFeature csf = features.get(ComponentsFeature.class);
        if (csf != null) {
            for (ComponentFeature cfi : csf.getComponentFeatures()) {
                if (ComponentFeature.Target.STUB.equals((Object)cfi.getTarget())) continue;
                throw new IllegalArgumentException();
            }
        }
        features.addAll(this.features);
        SEIPortInfo spi = this.addSEI(portName, portInterface, features);
        return this.createEndpointIFBaseProxy(wsepr, portName, portInterface, features, spi);
    }

    public <T> T getPort(Class<T> portInterface, WebServiceFeature ... features) {
        QName portTypeName = RuntimeModeler.getPortTypeName(portInterface, this.getMetadadaReader(new WebServiceFeatureList(features), portInterface.getClassLoader()));
        WSDLService tmpWsdlService = this.wsdlService;
        if (tmpWsdlService == null && (tmpWsdlService = this.getWSDLModelfromSEI(portInterface)) == null) {
            throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
        }
        WSDLPort port = tmpWsdlService.getMatchingPort(portTypeName);
        if (port == null) {
            throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
        }
        QName portName = port.getName();
        return this.getPort(portName, portInterface, features);
    }

    public <T> T getPort(Class<T> portInterface) throws WebServiceException {
        return this.getPort(portInterface, EMPTY_FEATURES);
    }

    public void addPort(QName portName, String bindingId, String endpointAddress) throws WebServiceException {
        if (this.ports.containsKey(portName)) {
            throw new WebServiceException(DispatchMessages.DUPLICATE_PORT(portName.toString()));
        }
        BindingID bid = bindingId == null ? BindingID.SOAP11_HTTP : BindingID.parse(bindingId);
        this.ports.put(portName, new PortInfo(this, endpointAddress == null ? null : EndpointAddress.create(endpointAddress), portName, bid));
    }

    public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode) throws WebServiceException {
        return this.createDispatch(portName, aClass, mode, EMPTY_FEATURES);
    }

    @Override
    public <T> Dispatch<T> createDispatch(QName portName, WSEndpointReference wsepr, Class<T> aClass, Service.Mode mode, WebServiceFeature ... features) {
        return this.createDispatch(portName, wsepr, aClass, mode, new WebServiceFeatureList(features));
    }

    public <T> Dispatch<T> createDispatch(QName portName, WSEndpointReference wsepr, Class<T> aClass, Service.Mode mode, WebServiceFeatureList features) {
        PortInfo port = this.safeGetPort(portName);
        ComponentFeature cf = features.get(ComponentFeature.class);
        if (cf != null && !ComponentFeature.Target.STUB.equals((Object)cf.getTarget())) {
            throw new IllegalArgumentException();
        }
        ComponentsFeature csf = features.get(ComponentsFeature.class);
        if (csf != null) {
            for (ComponentFeature cfi : csf.getComponentFeatures()) {
                if (ComponentFeature.Target.STUB.equals((Object)cfi.getTarget())) continue;
                throw new IllegalArgumentException();
            }
        }
        features.addAll(this.features);
        BindingImpl binding = port.createBinding(features, null, null);
        binding.setMode(mode);
        Dispatch<T> dispatch = Stubs.createDispatch(port, this, binding, aClass, mode, wsepr);
        this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
        return dispatch;
    }

    public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode, WebServiceFeature ... features) {
        return this.createDispatch(portName, aClass, mode, new WebServiceFeatureList(features));
    }

    public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode, WebServiceFeatureList features) {
        MemberSubmissionAddressingFeature msa;
        WSEndpointReference wsepr = null;
        boolean isAddressingEnabled = false;
        AddressingFeature af = features.get(AddressingFeature.class);
        if (af == null) {
            af = this.features.get(AddressingFeature.class);
        }
        if (af != null && af.isEnabled()) {
            isAddressingEnabled = true;
        }
        if ((msa = features.get(MemberSubmissionAddressingFeature.class)) == null) {
            msa = this.features.get(MemberSubmissionAddressingFeature.class);
        }
        if (msa != null && msa.isEnabled()) {
            isAddressingEnabled = true;
        }
        if (isAddressingEnabled && this.wsdlService != null && this.wsdlService.get(portName) != null) {
            wsepr = this.wsdlService.get(portName).getEPR();
        }
        return this.createDispatch(portName, wsepr, aClass, mode, features);
    }

    public <T> Dispatch<T> createDispatch(EndpointReference endpointReference, Class<T> type, Service.Mode mode, WebServiceFeature ... features) {
        WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
        QName portName = this.addPortEpr(wsepr);
        return this.createDispatch(portName, wsepr, type, mode, features);
    }

    @NotNull
    public PortInfo safeGetPort(QName portName) {
        PortInfo port = this.ports.get(portName);
        if (port == null) {
            throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildNameList(this.ports.keySet())));
        }
        return port;
    }

    private StringBuilder buildNameList(Collection<QName> names) {
        StringBuilder sb = new StringBuilder();
        for (QName qn : names) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(qn);
        }
        return sb;
    }

    public EndpointAddress getEndpointAddress(QName qName) {
        PortInfo p = this.ports.get(qName);
        return p != null ? p.targetEndpoint : null;
    }

    public Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode) throws WebServiceException {
        return this.createDispatch(portName, jaxbContext, mode, EMPTY_FEATURES);
    }

    @Override
    public Dispatch<Object> createDispatch(QName portName, WSEndpointReference wsepr, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeature ... features) {
        return this.createDispatch(portName, wsepr, jaxbContext, mode, new WebServiceFeatureList(features));
    }

    protected Dispatch<Object> createDispatch(QName portName, WSEndpointReference wsepr, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeatureList features) {
        PortInfo port = this.safeGetPort(portName);
        ComponentFeature cf = features.get(ComponentFeature.class);
        if (cf != null && !ComponentFeature.Target.STUB.equals((Object)cf.getTarget())) {
            throw new IllegalArgumentException();
        }
        ComponentsFeature csf = features.get(ComponentsFeature.class);
        if (csf != null) {
            for (ComponentFeature cfi : csf.getComponentFeatures()) {
                if (ComponentFeature.Target.STUB.equals((Object)cfi.getTarget())) continue;
                throw new IllegalArgumentException();
            }
        }
        features.addAll(this.features);
        BindingImpl binding = port.createBinding(features, null, null);
        binding.setMode(mode);
        Dispatch<Object> dispatch = Stubs.createJAXBDispatch(port, binding, jaxbContext, mode, wsepr);
        this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
        return dispatch;
    }

    @Override
    @NotNull
    public Container getContainer() {
        return this.container;
    }

    public Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeature ... webServiceFeatures) {
        return this.createDispatch(portName, jaxbContext, mode, new WebServiceFeatureList(webServiceFeatures));
    }

    protected Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeatureList features) {
        MemberSubmissionAddressingFeature msa;
        WSEndpointReference wsepr = null;
        boolean isAddressingEnabled = false;
        AddressingFeature af = features.get(AddressingFeature.class);
        if (af == null) {
            af = this.features.get(AddressingFeature.class);
        }
        if (af != null && af.isEnabled()) {
            isAddressingEnabled = true;
        }
        if ((msa = features.get(MemberSubmissionAddressingFeature.class)) == null) {
            msa = this.features.get(MemberSubmissionAddressingFeature.class);
        }
        if (msa != null && msa.isEnabled()) {
            isAddressingEnabled = true;
        }
        if (isAddressingEnabled && this.wsdlService != null && this.wsdlService.get(portName) != null) {
            wsepr = this.wsdlService.get(portName).getEPR();
        }
        return this.createDispatch(portName, wsepr, jaxbContext, mode, features);
    }

    public Dispatch<Object> createDispatch(EndpointReference endpointReference, JAXBContext context, Service.Mode mode, WebServiceFeature ... features) {
        WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
        QName portName = this.addPortEpr(wsepr);
        return this.createDispatch(portName, wsepr, context, mode, features);
    }

    private QName addPortEpr(WSEndpointReference wsepr) {
        if (wsepr == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_EPR());
        }
        QName eprPortName = this.getPortNameFromEPR(wsepr, null);
        PortInfo portInfo = new PortInfo(this, wsepr.getAddress() == null ? null : EndpointAddress.create(wsepr.getAddress()), eprPortName, this.getPortModel(this.wsdlService, eprPortName).getBinding().getBindingId());
        if (!this.ports.containsKey(eprPortName)) {
            this.ports.put(eprPortName, portInfo);
        }
        return eprPortName;
    }

    private QName getPortNameFromEPR(@NotNull WSEndpointReference wsepr, @Nullable QName portTypeName) {
        QName portName;
        WSEndpointReference.Metadata metadata = wsepr.getMetaData();
        QName eprServiceName = metadata.getServiceName();
        QName eprPortName = metadata.getPortName();
        if (eprServiceName != null && !eprServiceName.equals(this.serviceName)) {
            throw new WebServiceException("EndpointReference WSDL ServiceName differs from Service Instance WSDL Service QName.\n The two Service QNames must match");
        }
        if (this.wsdlService == null) {
            Source eprWsdlSource = metadata.getWsdlSource();
            if (eprWsdlSource == null) {
                throw new WebServiceException(ProviderApiMessages.NULL_WSDL());
            }
            try {
                WSDLModel eprWsdlMdl = this.parseWSDL(new URL(wsepr.getAddress()), eprWsdlSource, null);
                this.wsdlService = eprWsdlMdl.getService(this.serviceName);
                if (this.wsdlService == null) {
                    throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(eprWsdlMdl.getServices().keySet())));
                }
            }
            catch (MalformedURLException e) {
                throw new WebServiceException(ClientMessages.INVALID_ADDRESS(wsepr.getAddress()));
            }
        }
        if ((portName = eprPortName) == null && portTypeName != null) {
            WSDLPort port = this.wsdlService.getMatchingPort(portTypeName);
            if (port == null) {
                throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
            }
            portName = port.getName();
        }
        if (portName == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_PORTNAME());
        }
        if (this.wsdlService.get(portName) == null) {
            throw new WebServiceException(ClientMessages.INVALID_EPR_PORT_NAME(portName, this.buildWsdlPortNames()));
        }
        return portName;
    }

    private <T> T createProxy(final Class<T> portInterface, final InvocationHandler pis) {
        final ClassLoader loader = WSServiceDelegate.getDelegatingLoader(portInterface.getClassLoader(), WSServiceDelegate.class.getClassLoader());
        return AccessController.doPrivileged(new PrivilegedAction<T>(){

            @Override
            public T run() {
                Object proxy = Proxy.newProxyInstance(loader, new Class[]{portInterface, WSBindingProvider.class, Closeable.class}, pis);
                return portInterface.cast(proxy);
            }
        });
    }

    private WSDLService getWSDLModelfromSEI(final Class sei) {
        WebService ws = AccessController.doPrivileged(new PrivilegedAction<WebService>(){

            @Override
            public WebService run() {
                return sei.getAnnotation(WebService.class);
            }
        });
        if (ws == null || ws.wsdlLocation().equals("")) {
            return null;
        }
        String wsdlLocation = ws.wsdlLocation();
        wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
        StreamSource wsdl = new StreamSource(wsdlLocation);
        WSDLService service = null;
        try {
            URL url = wsdl.getSystemId() == null ? null : new URL(wsdl.getSystemId());
            WSDLModel model = this.parseWSDL(url, wsdl, sei);
            service = model.getService(this.serviceName);
            if (service == null) {
                throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(model.getServices().keySet())));
            }
        }
        catch (MalformedURLException e) {
            throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
        }
        return service;
    }

    public QName getServiceName() {
        return this.serviceName;
    }

    public Class getServiceClass() {
        return this.serviceClass;
    }

    public Iterator<QName> getPorts() throws WebServiceException {
        return this.ports.keySet().iterator();
    }

    public URL getWSDLDocumentLocation() {
        if (this.wsdlService == null) {
            return null;
        }
        try {
            return new URL(this.wsdlService.getParent().getLocation().getSystemId());
        }
        catch (MalformedURLException e) {
            throw new AssertionError((Object)e);
        }
    }

    private <T> T createEndpointIFBaseProxy(@Nullable WSEndpointReference epr, QName portName, Class<T> portInterface, WebServiceFeatureList webServiceFeatures, SEIPortInfo eif) {
        if (this.wsdlService == null) {
            throw new WebServiceException(ClientMessages.INVALID_SERVICE_NO_WSDL(this.serviceName));
        }
        if (this.wsdlService.get(portName) == null) {
            throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildWsdlPortNames()));
        }
        BindingImpl binding = eif.createBinding(webServiceFeatures, portInterface);
        InvocationHandler pis = this.getStubHandler(binding, eif, epr);
        T proxy = this.createProxy(portInterface, pis);
        if (this.serviceInterceptor != null) {
            this.serviceInterceptor.postCreateProxy((WSBindingProvider)proxy, portInterface);
        }
        return proxy;
    }

    protected InvocationHandler getStubHandler(BindingImpl binding, SEIPortInfo eif, @Nullable WSEndpointReference epr) {
        return new SEIStub((WSPortInfo)eif, binding, eif.model, epr);
    }

    private StringBuilder buildWsdlPortNames() {
        HashSet<QName> wsdlPortNames = new HashSet<QName>();
        for (WSDLPort wSDLPort : this.wsdlService.getPorts()) {
            wsdlPortNames.add(wSDLPort.getName());
        }
        return this.buildNameList(wsdlPortNames);
    }

    @NotNull
    public WSDLPort getPortModel(WSDLService wsdlService, QName portName) {
        WSDLPort port = wsdlService.get(portName);
        if (port == null) {
            throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildWsdlPortNames()));
        }
        return port;
    }

    private SEIPortInfo addSEI(QName portName, Class portInterface, WebServiceFeatureList features) throws WebServiceException {
        boolean ownModel = this.useOwnSEIModel(features);
        if (ownModel) {
            return this.createSEIPortInfo(portName, portInterface, features);
        }
        SEIPortInfo spi = this.seiContext.get(portName);
        if (spi == null) {
            spi = this.createSEIPortInfo(portName, portInterface, features);
            this.seiContext.put(spi.portName, spi);
            this.ports.put(spi.portName, spi);
        }
        return spi;
    }

    public SEIModel buildRuntimeModel(QName serviceName, QName portName, Class portInterface, WSDLPort wsdlPort, WebServiceFeatureList features) {
        DatabindingFactory fac = DatabindingFactory.newInstance();
        DatabindingConfig config = new DatabindingConfig();
        config.setContractClass(portInterface);
        config.getMappingInfo().setServiceName(serviceName);
        config.setWsdlPort(wsdlPort);
        config.setFeatures(features);
        config.setClassLoader(portInterface.getClassLoader());
        config.getMappingInfo().setPortName(portName);
        config.setWsdlURL(this.wsdlURL);
        config.setMetadataReader(this.getMetadadaReader(features, portInterface.getClassLoader()));
        DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
        return rt.getModel();
    }

    private MetadataReader getMetadadaReader(WebServiceFeatureList features, ClassLoader classLoader) {
        if (features == null) {
            return null;
        }
        ExternalMetadataFeature ef = features.get(ExternalMetadataFeature.class);
        if (ef != null) {
            return ef.getMetadataReader(classLoader, false);
        }
        return null;
    }

    private SEIPortInfo createSEIPortInfo(QName portName, Class portInterface, WebServiceFeatureList features) {
        WSDLPort wsdlPort = this.getPortModel(this.wsdlService, portName);
        SEIModel model = this.buildRuntimeModel(this.serviceName, portName, portInterface, wsdlPort, features);
        return new SEIPortInfo(this, portInterface, (SOAPSEIModel)model, wsdlPort);
    }

    private boolean useOwnSEIModel(WebServiceFeatureList features) {
        return features.contains(UsesJAXBContextFeature.class);
    }

    public WSDLService getWsdlService() {
        return this.wsdlService;
    }

    private static ClassLoader getDelegatingLoader(ClassLoader loader1, ClassLoader loader2) {
        if (loader1 == null) {
            return loader2;
        }
        if (loader2 == null) {
            return loader1;
        }
        for (ClassLoader parent = loader1; parent != null; parent = parent.getParent()) {
            if (parent != loader2) continue;
            return loader1;
        }
        return new DelegatingLoader(loader1, loader2);
    }

    private static final class DelegatingLoader
    extends ClassLoader {
        private final ClassLoader loader;

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.loader == null ? 0 : this.loader.hashCode());
            result = 31 * result + (this.getParent() == null ? 0 : this.getParent().hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            DelegatingLoader other = (DelegatingLoader)obj;
            if (this.loader == null ? other.loader != null : !this.loader.equals(other.loader)) {
                return false;
            }
            return !(this.getParent() == null ? other.getParent() != null : !this.getParent().equals(other.getParent()));
        }

        DelegatingLoader(ClassLoader loader1, ClassLoader loader2) {
            super(loader2);
            this.loader = loader1;
        }

        protected Class findClass(String name) throws ClassNotFoundException {
            return this.loader.loadClass(name);
        }

        @Override
        protected URL findResource(String name) {
            return this.loader.getResource(name);
        }
    }
}


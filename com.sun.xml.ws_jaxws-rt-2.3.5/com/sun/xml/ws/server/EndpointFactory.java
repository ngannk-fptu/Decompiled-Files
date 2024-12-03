/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapMutator
 *  javax.jws.WebService
 *  javax.xml.ws.Provider
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.WebServiceProvider
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.server;

import com.oracle.webservices.api.databinding.ExternalMetadataFeature;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.api.databinding.DatabindingFactory;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.server.AsyncProvider;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.api.server.InstanceResolver;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.db.DatabindingImpl;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.ReflectAnnotationReader;
import com.sun.xml.ws.model.RuntimeModeler;
import com.sun.xml.ws.model.SOAPSEIModel;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.EndpointAwareTube;
import com.sun.xml.ws.server.SDDocumentImpl;
import com.sun.xml.ws.server.ServerRtException;
import com.sun.xml.ws.server.ServiceDefinitionImpl;
import com.sun.xml.ws.server.WSDLGenResolver;
import com.sun.xml.ws.server.WSEndpointImpl;
import com.sun.xml.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.ws.server.sei.SEIInvokerTube;
import com.sun.xml.ws.util.HandlerAnnotationInfo;
import com.sun.xml.ws.util.HandlerAnnotationProcessor;
import com.sun.xml.ws.util.ServiceConfigurationError;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EndpointFactory {
    private static final EndpointFactory instance = new EndpointFactory();
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server.endpoint");

    public static EndpointFactory getInstance() {
        return instance;
    }

    public static <T> WSEndpoint<T> createEndpoint(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous) {
        return EndpointFactory.createEndpoint(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
    }

    public static <T> WSEndpoint<T> createEndpoint(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous, boolean isStandard) {
        EndpointFactory factory;
        EndpointFactory endpointFactory = factory = container != null ? container.getSPI(EndpointFactory.class) : null;
        if (factory == null) {
            factory = EndpointFactory.getInstance();
        }
        return factory.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, isStandard);
    }

    public <T> WSEndpoint<T> create(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous) {
        return this.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
    }

    public <T> WSEndpoint<T> create(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous, boolean isStandard) {
        EndpointAwareTube terminal;
        String portNS;
        String serviceNS;
        if (implType == null) {
            throw new IllegalArgumentException();
        }
        MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(implType, binding);
        if (isStandard) {
            EndpointFactory.verifyImplementorClass(implType, metadataReader);
        }
        if (invoker == null) {
            invoker = InstanceResolver.createDefault(implType).createInvoker();
        }
        CollectionCollection md = new CollectionCollection();
        if (primaryWsdl != null) {
            if (metadata != null) {
                Iterator<? extends SDDocumentSource> it = metadata.iterator();
                if (it.hasNext() && primaryWsdl.equals(it.next())) {
                    md.addAll(metadata);
                } else {
                    md.add(primaryWsdl);
                    md.addAll(metadata);
                }
            } else {
                md.add(primaryWsdl);
            }
        } else if (metadata != null) {
            md.addAll(metadata);
        }
        if (container == null) {
            container = ContainerResolver.getInstance().getContainer();
        }
        if (serviceName == null) {
            serviceName = EndpointFactory.getDefaultServiceName(implType, metadataReader);
        }
        if (portName == null) {
            portName = EndpointFactory.getDefaultPortName(serviceName, implType, metadataReader);
        }
        if (!(serviceNS = serviceName.getNamespaceURI()).equals(portNS = portName.getNamespaceURI())) {
            throw new ServerRtException("wrong.tns.for.port", portNS, serviceNS);
        }
        if (binding == null) {
            binding = BindingImpl.create(BindingID.parse(implType));
        }
        if (isStandard && primaryWsdl != null) {
            EndpointFactory.verifyPrimaryWSDL(primaryWsdl, serviceName);
        }
        QName portTypeName = null;
        if (isStandard && implType.getAnnotation(WebServiceProvider.class) == null) {
            portTypeName = RuntimeModeler.getPortTypeName(implType, metadataReader);
        }
        Collection<SDDocumentImpl> docList = EndpointFactory.categoriseMetadata(md.iterator(), serviceName, portTypeName);
        SDDocumentImpl primaryDoc = primaryWsdl != null ? SDDocumentImpl.create(primaryWsdl, serviceName, portTypeName) : EndpointFactory.findPrimary(docList);
        WSDLPort wsdlPort = null;
        AbstractSEIModelImpl seiModel = null;
        if (primaryDoc != null) {
            wsdlPort = EndpointFactory.getWSDLPort(primaryDoc, docList, serviceName, portName, container, resolver);
        }
        WebServiceFeatureList features = ((BindingImpl)binding).getFeatures();
        if (isStandard) {
            features.parseAnnotations(implType);
        }
        PolicyMap policyMap = null;
        if (this.isUseProviderTube(implType, isStandard)) {
            Iterable<WebServiceFeature> configFtrs;
            if (wsdlPort != null) {
                policyMap = wsdlPort.getOwner().getParent().getPolicyMap();
                configFtrs = wsdlPort.getFeatures();
            } else {
                policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(null, container, implType, false, new PolicyMapMutator[0]));
                configFtrs = PolicyUtil.getPortScopedFeatures(policyMap, serviceName, portName);
            }
            features.mergeFeatures(configFtrs, true);
            terminal = this.createProviderInvokerTube(implType, binding, invoker, container);
        } else {
            seiModel = EndpointFactory.createSEIModel(wsdlPort, implType, serviceName, portName, binding, primaryDoc);
            if (binding instanceof SOAPBindingImpl) {
                ((SOAPBindingImpl)binding).setPortKnownHeaders(((SOAPSEIModel)seiModel).getKnownHeaders());
            }
            if (primaryDoc == null) {
                primaryDoc = EndpointFactory.generateWSDL(binding, seiModel, docList, container, implType);
                wsdlPort = EndpointFactory.getWSDLPort(primaryDoc, docList, serviceName, portName, container, resolver);
                seiModel.freeze(wsdlPort);
            }
            policyMap = wsdlPort.getOwner().getParent().getPolicyMap();
            features.mergeFeatures(wsdlPort.getFeatures(), true);
            terminal = this.createSEIInvokerTube(seiModel, invoker, binding);
        }
        if (processHandlerAnnotation) {
            EndpointFactory.processHandlerAnnotation(binding, implType, serviceName, portName);
        }
        if (primaryDoc != null) {
            docList = EndpointFactory.findMetadataClosure(primaryDoc, docList, resolver);
        }
        ServiceDefinitionImpl serviceDefiniton = primaryDoc != null ? new ServiceDefinitionImpl(docList, primaryDoc) : null;
        return this.create(serviceName, portName, binding, container, seiModel, wsdlPort, implType, serviceDefiniton, terminal, isTransportSynchronous, policyMap);
    }

    protected <T> WSEndpoint<T> create(QName serviceName, QName portName, WSBinding binding, Container container, SEIModel seiModel, WSDLPort wsdlPort, Class<T> implType, ServiceDefinitionImpl serviceDefinition, EndpointAwareTube terminal, boolean isTransportSynchronous, PolicyMap policyMap) {
        return new WSEndpointImpl<T>(serviceName, portName, binding, container, seiModel, wsdlPort, implType, serviceDefinition, terminal, isTransportSynchronous, policyMap);
    }

    protected boolean isUseProviderTube(Class<?> implType, boolean isStandard) {
        return !isStandard || implType.getAnnotation(WebServiceProvider.class) != null;
    }

    protected EndpointAwareTube createSEIInvokerTube(AbstractSEIModelImpl seiModel, Invoker invoker, WSBinding binding) {
        return new SEIInvokerTube(seiModel, invoker, binding);
    }

    protected <T> EndpointAwareTube createProviderInvokerTube(Class<T> implType, WSBinding binding, Invoker invoker, Container container) {
        return ProviderInvokerTube.create(implType, binding, invoker, container);
    }

    private static Collection<SDDocumentImpl> findMetadataClosure(final SDDocumentImpl primaryDoc, final Collection<SDDocumentImpl> docList, final EntityResolver resolver) {
        return new AbstractCollection<SDDocumentImpl>(){

            @Override
            public Iterator<SDDocumentImpl> iterator() {
                HashMap<String, SDDocumentImpl> oldMap = new HashMap<String, SDDocumentImpl>();
                Iterator oldDocs = docList.iterator();
                HashMap<String, SDDocumentImpl> newMap = new HashMap<String, SDDocumentImpl>();
                newMap.put(primaryDoc.getSystemId().toString(), primaryDoc);
                ArrayList<String> remaining = new ArrayList<String>();
                remaining.addAll(primaryDoc.getImports());
                while (!remaining.isEmpty()) {
                    String url = (String)remaining.remove(0);
                    SDDocumentImpl doc = (SDDocumentImpl)oldMap.get(url);
                    if (doc == null) {
                        while (oldDocs.hasNext()) {
                            SDDocumentImpl old = (SDDocumentImpl)oldDocs.next();
                            String id = old.getSystemId().toString();
                            oldMap.put(id, old);
                            if (!id.equals(url)) continue;
                            doc = old;
                            break;
                        }
                        if (doc == null && resolver != null) {
                            try {
                                InputSource source = resolver.resolveEntity(null, url);
                                if (source != null) {
                                    MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
                                    XMLStreamReader reader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(source.getByteStream());
                                    xsb.createFromXMLStreamReader(reader);
                                    SDDocumentSource sdocSource = SDDocumentImpl.create(new URL(url), (XMLStreamBuffer)xsb);
                                    doc = SDDocumentImpl.create(sdocSource, null, null);
                                }
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (doc == null || newMap.containsKey(url)) continue;
                    newMap.put(url, doc);
                    remaining.addAll(doc.getImports());
                }
                return newMap.values().iterator();
            }

            @Override
            public int size() {
                int size = 0;
                Iterator<SDDocumentImpl> it = this.iterator();
                while (it.hasNext()) {
                    it.next();
                    ++size;
                }
                return size;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isEmpty() {
                return docList.isEmpty();
            }
        };
    }

    private static <T> void processHandlerAnnotation(WSBinding binding, Class<T> implType, QName serviceName, QName portName) {
        HandlerAnnotationInfo chainInfo = HandlerAnnotationProcessor.buildHandlerInfo(implType, serviceName, portName, binding);
        if (chainInfo != null) {
            binding.setHandlerChain(chainInfo.getHandlers());
            if (binding instanceof SOAPBinding) {
                ((SOAPBinding)binding).setRoles(chainInfo.getRoles());
            }
        }
    }

    public static boolean verifyImplementorClass(Class<?> clz) {
        return EndpointFactory.verifyImplementorClass(clz, null);
    }

    public static boolean verifyImplementorClass(Class<?> clz, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        WebServiceProvider wsProvider = metadataReader.getAnnotation(WebServiceProvider.class, clz);
        WebService ws = metadataReader.getAnnotation(WebService.class, clz);
        if (wsProvider == null && ws == null) {
            throw new IllegalArgumentException(clz + " has neither @WebService nor @WebServiceProvider annotation");
        }
        if (wsProvider != null && ws != null) {
            throw new IllegalArgumentException(clz + " has both @WebService and @WebServiceProvider annotations");
        }
        if (wsProvider != null) {
            if (Provider.class.isAssignableFrom(clz) || AsyncProvider.class.isAssignableFrom(clz)) {
                return true;
            }
            throw new IllegalArgumentException(clz + " doesn't implement Provider or AsyncProvider interface");
        }
        return false;
    }

    private static AbstractSEIModelImpl createSEIModel(WSDLPort wsdlPort, Class<?> implType, @NotNull QName serviceName, @NotNull QName portName, WSBinding binding, SDDocumentSource primaryWsdl) {
        DatabindingFactory fac = DatabindingFactory.newInstance();
        DatabindingConfig config = new DatabindingConfig();
        config.setEndpointClass(implType);
        config.getMappingInfo().setServiceName(serviceName);
        config.setWsdlPort(wsdlPort);
        config.setWSBinding(binding);
        config.setClassLoader(implType.getClassLoader());
        config.getMappingInfo().setPortName(portName);
        if (primaryWsdl != null) {
            config.setWsdlURL(primaryWsdl.getSystemId());
        }
        config.setMetadataReader(EndpointFactory.getExternalMetadatReader(implType, binding));
        DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
        return (AbstractSEIModelImpl)rt.getModel();
    }

    public static MetadataReader getExternalMetadatReader(Class<?> implType, WSBinding binding) {
        ExternalMetadataFeature ef = binding.getFeature(ExternalMetadataFeature.class);
        if (ef != null) {
            return ef.getMetadataReader(implType.getClassLoader(), false);
        }
        return null;
    }

    @NotNull
    public static QName getDefaultServiceName(Class<?> implType) {
        return EndpointFactory.getDefaultServiceName(implType, null);
    }

    @NotNull
    public static QName getDefaultServiceName(Class<?> implType, MetadataReader metadataReader) {
        return EndpointFactory.getDefaultServiceName(implType, true, metadataReader);
    }

    @NotNull
    public static QName getDefaultServiceName(Class<?> implType, boolean isStandard) {
        return EndpointFactory.getDefaultServiceName(implType, isStandard, null);
    }

    @NotNull
    public static QName getDefaultServiceName(Class<?> implType, boolean isStandard, MetadataReader metadataReader) {
        QName serviceName;
        WebServiceProvider wsProvider;
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        if ((wsProvider = metadataReader.getAnnotation(WebServiceProvider.class, implType)) != null) {
            String tns = wsProvider.targetNamespace();
            String local = wsProvider.serviceName();
            serviceName = new QName(tns, local);
        } else {
            serviceName = RuntimeModeler.getServiceName(implType, metadataReader, isStandard);
        }
        assert (serviceName != null);
        return serviceName;
    }

    @NotNull
    public static QName getDefaultPortName(QName serviceName, Class<?> implType) {
        return EndpointFactory.getDefaultPortName(serviceName, implType, null);
    }

    @NotNull
    public static QName getDefaultPortName(QName serviceName, Class<?> implType, MetadataReader metadataReader) {
        return EndpointFactory.getDefaultPortName(serviceName, implType, true, metadataReader);
    }

    @NotNull
    public static QName getDefaultPortName(QName serviceName, Class<?> implType, boolean isStandard) {
        return EndpointFactory.getDefaultPortName(serviceName, implType, isStandard, null);
    }

    @NotNull
    public static QName getDefaultPortName(QName serviceName, Class<?> implType, boolean isStandard, MetadataReader metadataReader) {
        QName portName;
        WebServiceProvider wsProvider;
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        if ((wsProvider = metadataReader.getAnnotation(WebServiceProvider.class, implType)) != null) {
            String tns = wsProvider.targetNamespace();
            String local = wsProvider.portName();
            portName = new QName(tns, local);
        } else {
            portName = RuntimeModeler.getPortName(implType, metadataReader, serviceName.getNamespaceURI(), isStandard);
        }
        assert (portName != null);
        return portName;
    }

    @Nullable
    public static String getWsdlLocation(Class<?> implType) {
        return EndpointFactory.getWsdlLocation(implType, new ReflectAnnotationReader());
    }

    @Nullable
    public static String getWsdlLocation(Class<?> implType, MetadataReader metadataReader) {
        WebService ws;
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        if ((ws = metadataReader.getAnnotation(WebService.class, implType)) != null) {
            return EndpointFactory.nullIfEmpty(ws.wsdlLocation());
        }
        WebServiceProvider wsProvider = implType.getAnnotation(WebServiceProvider.class);
        assert (wsProvider != null);
        return EndpointFactory.nullIfEmpty(wsProvider.wsdlLocation());
    }

    private static String nullIfEmpty(String string) {
        if (string.length() < 1) {
            string = null;
        }
        return string;
    }

    private static SDDocumentImpl generateWSDL(WSBinding binding, AbstractSEIModelImpl seiModel, Collection<SDDocumentImpl> docs, Container container, Class implType) {
        BindingID bindingId = binding.getBindingId();
        if (!bindingId.canGenerateWSDL()) {
            throw new ServerRtException("can.not.generate.wsdl", bindingId);
        }
        if (bindingId.toString().equals("http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
            String msg = ServerMessages.GENERATE_NON_STANDARD_WSDL();
            logger.warning(msg);
        }
        WSDLGenResolver wsdlResolver = new WSDLGenResolver(docs, seiModel.getServiceQName(), seiModel.getPortTypeName());
        WSDLGenInfo wsdlGenInfo = new WSDLGenInfo();
        wsdlGenInfo.setWsdlResolver(wsdlResolver);
        wsdlGenInfo.setContainer(container);
        wsdlGenInfo.setExtensions(ServiceFinder.find(WSDLGeneratorExtension.class).toArray());
        wsdlGenInfo.setInlineSchemas(false);
        wsdlGenInfo.setSecureXmlProcessingDisabled(EndpointFactory.isSecureXmlProcessingDisabled(binding.getFeatures()));
        seiModel.getDatabinding().generateWSDL(wsdlGenInfo);
        return wsdlResolver.updateDocs();
    }

    private static boolean isSecureXmlProcessingDisabled(WSFeatureList featureList) {
        return false;
    }

    private static Collection<SDDocumentImpl> categoriseMetadata(final Iterator<SDDocumentSource> src, final QName serviceName, final QName portTypeName) {
        return new AbstractCollection<SDDocumentImpl>(){
            private final Collection<SDDocumentImpl> theConverted = new ArrayList<SDDocumentImpl>();

            @Override
            public boolean add(SDDocumentImpl arg0) {
                return this.theConverted.add(arg0);
            }

            @Override
            public Iterator<SDDocumentImpl> iterator() {
                return new Iterator<SDDocumentImpl>(){
                    private Iterator<SDDocumentImpl> convIt;
                    {
                        this.convIt = theConverted.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        if (this.convIt != null && this.convIt.hasNext()) {
                            return true;
                        }
                        return src.hasNext();
                    }

                    @Override
                    public SDDocumentImpl next() {
                        if (this.convIt != null && this.convIt.hasNext()) {
                            return this.convIt.next();
                        }
                        this.convIt = null;
                        if (!src.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        SDDocumentImpl next = SDDocumentImpl.create((SDDocumentSource)src.next(), serviceName, portTypeName);
                        theConverted.add(next);
                        return next;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isEmpty() {
                if (!this.theConverted.isEmpty()) {
                    return false;
                }
                return !src.hasNext();
            }
        };
    }

    private static void verifyPrimaryWSDL(@NotNull SDDocumentSource primaryWsdl, @NotNull QName serviceName) {
        SDDocumentImpl primaryDoc = SDDocumentImpl.create(primaryWsdl, serviceName, null);
        if (!(primaryDoc instanceof SDDocument.WSDL)) {
            throw new WebServiceException(primaryWsdl.getSystemId() + " is not a WSDL. But it is passed as a primary WSDL");
        }
        SDDocument.WSDL wsdlDoc = (SDDocument.WSDL)((Object)primaryDoc);
        if (!wsdlDoc.hasService()) {
            if (wsdlDoc.getAllServices().isEmpty()) {
                throw new WebServiceException("Not a primary WSDL=" + primaryWsdl.getSystemId() + " since it doesn't have Service " + serviceName);
            }
            throw new WebServiceException("WSDL " + primaryDoc.getSystemId() + " has the following services " + wsdlDoc.getAllServices() + " but not " + serviceName + ". Maybe you forgot to specify a serviceName and/or targetNamespace in @WebService/@WebServiceProvider?");
        }
    }

    @Nullable
    private static SDDocumentImpl findPrimary(@NotNull Collection<SDDocumentImpl> docList) {
        SDDocumentImpl primaryDoc = null;
        boolean foundConcrete = false;
        boolean foundAbstract = false;
        for (SDDocumentImpl doc : docList) {
            if (!(doc instanceof SDDocument.WSDL)) continue;
            SDDocument.WSDL wsdlDoc = (SDDocument.WSDL)((Object)doc);
            if (wsdlDoc.hasService()) {
                primaryDoc = doc;
                if (foundConcrete) {
                    throw new ServerRtException("duplicate.primary.wsdl", doc.getSystemId());
                }
                foundConcrete = true;
            }
            if (!wsdlDoc.hasPortType()) continue;
            if (foundAbstract) {
                throw new ServerRtException("duplicate.abstract.wsdl", doc.getSystemId());
            }
            foundAbstract = true;
        }
        return primaryDoc;
    }

    @NotNull
    private static WSDLPort getWSDLPort(SDDocumentSource primaryWsdl, Collection<? extends SDDocumentSource> metadata, @NotNull QName serviceName, @NotNull QName portName, Container container, EntityResolver resolver) {
        URL wsdlUrl = primaryWsdl.getSystemId();
        try {
            WSDLModel wsdlDoc = RuntimeWSDLParser.parse(new XMLEntityResolver.Parser(primaryWsdl), (XMLEntityResolver)new EntityResolverImpl(metadata, resolver), false, container, ServiceFinder.find(WSDLParserExtension.class).toArray());
            if (wsdlDoc.getServices().size() == 0) {
                throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(wsdlUrl));
            }
            WSDLService wsdlService = wsdlDoc.getService(serviceName);
            if (wsdlService == null) {
                throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(serviceName, wsdlUrl));
            }
            WSDLPort wsdlPort = wsdlService.get(portName);
            if (wsdlPort == null) {
                throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(serviceName, portName, wsdlUrl));
            }
            return wsdlPort;
        }
        catch (IOException e) {
            throw new ServerRtException("runtime.parser.wsdl", wsdlUrl, e);
        }
        catch (XMLStreamException e) {
            throw new ServerRtException("runtime.saxparser.exception", e.getMessage(), e.getLocation(), e);
        }
        catch (SAXException e) {
            throw new ServerRtException("runtime.parser.wsdl", wsdlUrl, e);
        }
        catch (ServiceConfigurationError e) {
            throw new ServerRtException("runtime.parser.wsdl", wsdlUrl, e);
        }
    }

    private static class CollectionCollection<T>
    extends AbstractCollection<T> {
        private final Collection<Collection<? extends T>> cols = new ArrayList<Collection<? extends T>>();

        private CollectionCollection() {
        }

        @Override
        public Iterator<T> iterator() {
            final Iterator<Collection<? extends T>> colIt = this.cols.iterator();
            return new Iterator<T>(){
                private Iterator<? extends T> current = null;

                @Override
                public boolean hasNext() {
                    if (this.current == null || !this.current.hasNext()) {
                        do {
                            if (!colIt.hasNext()) {
                                return false;
                            }
                            this.current = ((Collection)colIt.next()).iterator();
                        } while (!this.current.hasNext());
                        return true;
                    }
                    return true;
                }

                @Override
                public T next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return this.current.next();
                }

                @Override
                public void remove() {
                    if (this.current == null) {
                        throw new IllegalStateException();
                    }
                    this.current.remove();
                }
            };
        }

        @Override
        public int size() {
            int size = 0;
            for (Collection<T> c : this.cols) {
                size += c.size();
            }
            return size;
        }

        @Override
        public boolean add(T arg0) {
            return this.cols.add(Collections.singleton(arg0));
        }

        @Override
        public boolean addAll(Collection<? extends T> arg0) {
            return this.cols.add(arg0);
        }

        @Override
        public void clear() {
            this.cols.clear();
        }

        @Override
        public boolean isEmpty() {
            return !this.iterator().hasNext();
        }
    }

    private static final class EntityResolverImpl
    implements XMLEntityResolver {
        private Iterator<? extends SDDocumentSource> origMetadata;
        private Map<String, SDDocumentSource> metadata = new ConcurrentHashMap<String, SDDocumentSource>();
        private EntityResolver resolver;

        public EntityResolverImpl(Collection<? extends SDDocumentSource> metadata, EntityResolver resolver) {
            this.origMetadata = metadata.iterator();
            this.resolver = resolver;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XMLEntityResolver.Parser resolveEntity(String publicId, String systemId) throws IOException, XMLStreamException {
            if (systemId != null) {
                SDDocumentSource doc = this.metadata.get(systemId);
                if (doc != null) {
                    return new XMLEntityResolver.Parser(doc);
                }
                EntityResolverImpl entityResolverImpl = this;
                synchronized (entityResolverImpl) {
                    while (this.origMetadata.hasNext()) {
                        doc = this.origMetadata.next();
                        String extForm = doc.getSystemId().toExternalForm();
                        this.metadata.put(extForm, doc);
                        if (!systemId.equals(extForm)) continue;
                        return new XMLEntityResolver.Parser(doc);
                    }
                }
            }
            if (this.resolver != null) {
                try {
                    InputSource source = this.resolver.resolveEntity(publicId, systemId);
                    if (source != null) {
                        XMLEntityResolver.Parser p = new XMLEntityResolver.Parser(null, XMLStreamReaderFactory.create(source, true));
                        return p;
                    }
                }
                catch (SAXException e) {
                    throw new XMLStreamException(e);
                }
            }
            return null;
        }
    }
}


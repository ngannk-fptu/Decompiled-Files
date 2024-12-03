/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.spi.Invoker
 *  javax.xml.ws.spi.Provider
 *  javax.xml.ws.spi.ServiceDelegate
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 */
package com.sun.xml.ws.spi;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.ServiceSharedFeatureMarker;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.ws.resources.ProviderApiMessages;
import com.sun.xml.ws.spi.ContextClassloaderLocal;
import com.sun.xml.ws.transport.http.server.EndpointImpl;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.parser.RuntimeWSDLParser;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public class ProviderImpl
extends Provider {
    private static final ContextClassloaderLocal<JAXBContext> eprjc = new ContextClassloaderLocal<JAXBContext>(){

        @Override
        protected JAXBContext initialValue() throws Exception {
            return ProviderImpl.getEPRJaxbContext();
        }
    };
    public static final ProviderImpl INSTANCE = new ProviderImpl();

    public Endpoint createEndpoint(String bindingId, Object implementor) {
        return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
    }

    public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass) {
        return new WSServiceDelegate(wsdlDocumentLocation, serviceName, (Class<? extends Service>)serviceClass, new WebServiceFeature[0]);
    }

    public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass, WebServiceFeature ... features) {
        for (WebServiceFeature feature : features) {
            if (feature instanceof ServiceSharedFeatureMarker) continue;
            throw new WebServiceException("Doesn't support any Service specific features");
        }
        return new WSServiceDelegate(wsdlDocumentLocation, serviceName, (Class<? extends Service>)serviceClass, features);
    }

    public ServiceDelegate createServiceDelegate(Source wsdlSource, QName serviceName, Class serviceClass) {
        return new WSServiceDelegate(wsdlSource, serviceName, (Class<? extends Service>)serviceClass, new WebServiceFeature[0]);
    }

    public Endpoint createAndPublishEndpoint(String address, Object implementor) {
        EndpointImpl endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
        endpoint.publish(address);
        return endpoint;
    }

    public Endpoint createEndpoint(String bindingId, Object implementor, WebServiceFeature ... features) {
        return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, features);
    }

    public Endpoint createAndPublishEndpoint(String address, Object implementor, WebServiceFeature ... features) {
        EndpointImpl endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, features);
        endpoint.publish(address);
        return endpoint;
    }

    public Endpoint createEndpoint(String bindingId, Class implementorClass, Invoker invoker, WebServiceFeature ... features) {
        return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementorClass), implementorClass, invoker, features);
    }

    public EndpointReference readEndpointReference(Source eprInfoset) {
        try {
            Unmarshaller unmarshaller = eprjc.get().createUnmarshaller();
            return (EndpointReference)unmarshaller.unmarshal(eprInfoset);
        }
        catch (JAXBException e) {
            throw new WebServiceException("Error creating Marshaller or marshalling.", (Throwable)e);
        }
    }

    public <T> T getPort(EndpointReference endpointReference, Class<T> clazz, WebServiceFeature ... webServiceFeatures) {
        if (endpointReference == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_EPR());
        }
        WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
        WSEndpointReference.Metadata metadata = wsepr.getMetaData();
        if (metadata.getWsdlSource() == null) {
            throw new WebServiceException("WSDL metadata is missing in EPR");
        }
        WSService service = (WSService)this.createServiceDelegate(metadata.getWsdlSource(), metadata.getServiceName(), Service.class);
        return service.getPort(wsepr, clazz, webServiceFeatures);
    }

    public W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters) {
        return this.createW3CEndpointReference(address, null, serviceName, portName, metadata, wsdlDocumentLocation, referenceParameters, null, null);
    }

    public W3CEndpointReference createW3CEndpointReference(String address, QName interfaceName, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters, List<Element> elements, Map<QName, String> attributes) {
        Container container = ContainerResolver.getInstance().getContainer();
        if (address == null) {
            if (serviceName == null || portName == null) {
                throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS_SERVICE_ENDPOINT());
            }
            Module module = container.getSPI(Module.class);
            if (module != null) {
                List<BoundEndpoint> beList = module.getBoundEndpoints();
                for (BoundEndpoint be : beList) {
                    WSEndpoint wse = be.getEndpoint();
                    if (!wse.getServiceName().equals(serviceName) || !wse.getPortName().equals(portName)) continue;
                    try {
                        address = be.getAddress().toString();
                    }
                    catch (WebServiceException webServiceException) {}
                    break;
                }
            }
            if (address == null) {
                throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS());
            }
        }
        if (serviceName == null && portName != null) {
            throw new IllegalStateException(ProviderApiMessages.NULL_SERVICE());
        }
        String wsdlTargetNamespace = null;
        if (wsdlDocumentLocation != null) {
            try {
                EntityResolver er = XmlUtil.createDefaultCatalogResolver();
                URL wsdlLoc = new URL(wsdlDocumentLocation);
                WSDLModel wsdlDoc = RuntimeWSDLParser.parse(wsdlLoc, (Source)new StreamSource(wsdlLoc.toExternalForm()), er, true, container, ServiceFinder.find(WSDLParserExtension.class).toArray());
                if (serviceName != null) {
                    WSDLPort wsdlPort;
                    WSDLService wsdlService = wsdlDoc.getService(serviceName);
                    if (wsdlService == null) {
                        throw new IllegalStateException(ProviderApiMessages.NOTFOUND_SERVICE_IN_WSDL(serviceName, wsdlDocumentLocation));
                    }
                    if (portName != null && (wsdlPort = wsdlService.get(portName)) == null) {
                        throw new IllegalStateException(ProviderApiMessages.NOTFOUND_PORT_IN_WSDL(portName, serviceName, wsdlDocumentLocation));
                    }
                    wsdlTargetNamespace = serviceName.getNamespaceURI();
                } else {
                    QName firstService = wsdlDoc.getFirstServiceName();
                    wsdlTargetNamespace = firstService.getNamespaceURI();
                }
            }
            catch (Exception e) {
                throw new IllegalStateException(ProviderApiMessages.ERROR_WSDL(wsdlDocumentLocation), e);
            }
        }
        if (metadata != null && metadata.size() == 0) {
            metadata = null;
        }
        return new WSEndpointReference(AddressingVersion.fromSpecClass(W3CEndpointReference.class), address, serviceName, portName, interfaceName, metadata, wsdlDocumentLocation, wsdlTargetNamespace, referenceParameters, elements, attributes).toSpec(W3CEndpointReference.class);
    }

    private static JAXBContext getEPRJaxbContext() {
        return AccessController.doPrivileged(new PrivilegedAction<JAXBContext>(){

            @Override
            public JAXBContext run() {
                try {
                    return JAXBContext.newInstance((Class[])new Class[]{MemberSubmissionEndpointReference.class, W3CEndpointReference.class});
                }
                catch (JAXBException e) {
                    throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", (Throwable)e);
                }
            }
        });
    }
}


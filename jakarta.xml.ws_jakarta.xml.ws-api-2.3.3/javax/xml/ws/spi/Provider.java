/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.spi;

import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.FactoryFinder;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public abstract class Provider {
    private static final String DEFAULT_JAXWSPROVIDER = "com.sun.xml.internal.ws.spi.ProviderImpl";

    protected Provider() {
    }

    public static Provider provider() {
        try {
            return FactoryFinder.find(Provider.class, DEFAULT_JAXWSPROVIDER);
        }
        catch (WebServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new WebServiceException("Unable to createEndpointReference Provider", ex);
        }
    }

    public abstract ServiceDelegate createServiceDelegate(URL var1, QName var2, Class<? extends Service> var3);

    public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }

    public abstract Endpoint createEndpoint(String var1, Object var2);

    public abstract Endpoint createAndPublishEndpoint(String var1, Object var2);

    public abstract EndpointReference readEndpointReference(Source var1);

    public abstract <T> T getPort(EndpointReference var1, Class<T> var2, WebServiceFeature ... var3);

    public abstract W3CEndpointReference createW3CEndpointReference(String var1, QName var2, QName var3, List<Element> var4, String var5, List<Element> var6);

    public W3CEndpointReference createW3CEndpointReference(String address, QName interfaceName, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters, List<Element> elements, Map<QName, String> attributes) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }

    public Endpoint createAndPublishEndpoint(String address, Object implementor, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }

    public Endpoint createEndpoint(String bindingId, Object implementor, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }

    public Endpoint createEndpoint(String bindingId, Class<?> implementorClass, Invoker invoker, WebServiceFeature ... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
}


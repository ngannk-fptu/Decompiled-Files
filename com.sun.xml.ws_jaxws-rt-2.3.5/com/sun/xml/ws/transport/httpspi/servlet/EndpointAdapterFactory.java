/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.EndpointContext
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.spi.Invoker
 *  javax.xml.ws.spi.Provider
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.httpspi.servlet.EndpointAdapter;
import com.sun.xml.ws.transport.httpspi.servlet.EndpointContextImpl;
import com.sun.xml.ws.transport.httpspi.servlet.InvokerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointContext;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.Provider;

public final class EndpointAdapterFactory
implements DeploymentDescriptorParser.AdapterFactory<EndpointAdapter> {
    private static final Logger LOGGER = Logger.getLogger(EndpointAdapterFactory.class.getName());
    private final EndpointContextImpl appContext = new EndpointContextImpl();

    @Override
    public EndpointAdapter createAdapter(String name, String urlPattern, Class implType, QName serviceName, QName portName, String bindingId, List<Source> metadata, WebServiceFeature ... features) {
        LOGGER.info("Creating Endpoint using JAX-WS 2.2 HTTP SPI");
        InvokerImpl endpointInvoker = new InvokerImpl(implType);
        Endpoint endpoint = Provider.provider().createEndpoint(bindingId, implType, (Invoker)endpointInvoker, features);
        this.appContext.add(endpoint);
        endpoint.setEndpointContext((EndpointContext)this.appContext);
        if (portName != null || serviceName != null) {
            HashMap<String, QName> props = new HashMap<String, QName>();
            if (portName != null) {
                props.put("javax.xml.ws.wsdl.port", portName);
            }
            if (serviceName != null) {
                props.put("javax.xml.ws.wsdl.service", serviceName);
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Setting Endpoint Properties={0}", props);
            }
            endpoint.setProperties(props);
        }
        if (metadata != null) {
            endpoint.setMetadata(metadata);
            ArrayList<String> docId = new ArrayList<String>();
            for (Source source : metadata) {
                docId.add(source.getSystemId());
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Setting metadata={0}", docId);
            }
        }
        return new EndpointAdapter(endpoint, urlPattern);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebService
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.WebServiceProvider
 */
package org.springframework.remoting.jaxws;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceProvider;
import org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter;

public class SimpleJaxWsServiceExporter
extends AbstractJaxWsServiceExporter {
    public static final String DEFAULT_BASE_ADDRESS = "http://localhost:8080/";
    private String baseAddress = "http://localhost:8080/";

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    @Override
    protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
        endpoint.publish(this.calculateEndpointAddress(endpoint, annotation.serviceName()));
    }

    @Override
    protected void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation) {
        endpoint.publish(this.calculateEndpointAddress(endpoint, annotation.serviceName()));
    }

    protected String calculateEndpointAddress(Endpoint endpoint, String serviceName) {
        String fullAddress = this.baseAddress + serviceName;
        if (endpoint.getClass().getName().startsWith("weblogic.")) {
            fullAddress = fullAddress + "/";
        }
        return fullAddress;
    }
}


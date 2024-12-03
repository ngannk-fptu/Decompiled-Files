/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.handler.HandlerResolver
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.remoting.jaxws;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class LocalJaxWsServiceFactory {
    @Nullable
    private URL wsdlDocumentUrl;
    @Nullable
    private String namespaceUri;
    @Nullable
    private String serviceName;
    @Nullable
    private WebServiceFeature[] serviceFeatures;
    @Nullable
    private Executor executor;
    @Nullable
    private HandlerResolver handlerResolver;

    public void setWsdlDocumentUrl(@Nullable URL wsdlDocumentUrl) {
        this.wsdlDocumentUrl = wsdlDocumentUrl;
    }

    public void setWsdlDocumentResource(Resource wsdlDocumentResource) throws IOException {
        Assert.notNull((Object)wsdlDocumentResource, (String)"WSDL Resource must not be null");
        this.wsdlDocumentUrl = wsdlDocumentResource.getURL();
    }

    @Nullable
    public URL getWsdlDocumentUrl() {
        return this.wsdlDocumentUrl;
    }

    public void setNamespaceUri(@Nullable String namespaceUri) {
        this.namespaceUri = namespaceUri != null ? namespaceUri.trim() : null;
    }

    @Nullable
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public void setServiceName(@Nullable String serviceName) {
        this.serviceName = serviceName;
    }

    @Nullable
    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceFeatures(WebServiceFeature ... serviceFeatures) {
        this.serviceFeatures = serviceFeatures;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setHandlerResolver(HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    public Service createJaxWsService() {
        Service service;
        Assert.notNull((Object)this.serviceName, (String)"No service name specified");
        if (this.serviceFeatures != null) {
            service = this.wsdlDocumentUrl != null ? Service.create((URL)this.wsdlDocumentUrl, (QName)this.getQName(this.serviceName), (WebServiceFeature[])this.serviceFeatures) : Service.create((QName)this.getQName(this.serviceName), (WebServiceFeature[])this.serviceFeatures);
        } else {
            Service service2 = service = this.wsdlDocumentUrl != null ? Service.create((URL)this.wsdlDocumentUrl, (QName)this.getQName(this.serviceName)) : Service.create((QName)this.getQName(this.serviceName));
        }
        if (this.executor != null) {
            service.setExecutor(this.executor);
        }
        if (this.handlerResolver != null) {
            service.setHandlerResolver(this.handlerResolver);
        }
        return service;
    }

    protected QName getQName(String name) {
        return this.getNamespaceUri() != null ? new QName(this.getNamespaceUri(), name) : new QName(name);
    }
}


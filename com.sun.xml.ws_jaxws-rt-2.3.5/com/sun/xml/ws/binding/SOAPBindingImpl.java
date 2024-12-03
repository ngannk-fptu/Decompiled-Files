/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPFactory
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.soap.MTOMFeature
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.binding;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

public final class SOAPBindingImpl
extends BindingImpl
implements SOAPBinding {
    public static final String X_SOAP12HTTP_BINDING = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/";
    private static final String ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
    protected final SOAPVersion soapVersion;
    private Set<QName> portKnownHeaders = Collections.emptySet();
    private Set<QName> bindingUnderstoodHeaders = new HashSet<QName>();
    private final Lock lock = new ReentrantLock();

    SOAPBindingImpl(BindingID bindingId) {
        this(bindingId, EMPTY_FEATURES);
    }

    SOAPBindingImpl(BindingID bindingId, WebServiceFeature ... features) {
        super(bindingId, features);
        this.soapVersion = bindingId.getSOAPVersion();
        this.setRoles(new HashSet<String>());
        this.features.addAll(bindingId.createBuiltinFeatureList());
    }

    public void setPortKnownHeaders(@NotNull Set<QName> headers) {
        try {
            this.lock.lock();
            this.portKnownHeaders = headers;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean understandsHeader(QName header) {
        return this.serviceMode == Service.Mode.MESSAGE || this.portKnownHeaders.contains(header) || this.bindingUnderstoodHeaders.contains(header);
    }

    public void setHandlerChain(List<Handler> chain) {
        this.setHandlerConfig(new HandlerConfiguration(this.getHandlerConfig().getRoles(), chain));
    }

    protected void addRequiredRoles(Set<String> roles) {
        roles.addAll(this.soapVersion.requiredRoles);
    }

    public Set<String> getRoles() {
        return this.getHandlerConfig().getRoles();
    }

    public void setRoles(Set<String> roles) {
        if (roles == null) {
            roles = new HashSet<String>();
        }
        if (roles.contains(ROLE_NONE)) {
            throw new WebServiceException(ClientMessages.INVALID_SOAP_ROLE_NONE());
        }
        this.addRequiredRoles(roles);
        this.setHandlerConfig(new HandlerConfiguration(roles, this.getHandlerConfig()));
    }

    public boolean isMTOMEnabled() {
        return this.isFeatureEnabled(MTOMFeature.class);
    }

    public void setMTOMEnabled(boolean b) {
        this.features.setMTOMEnabled(b);
    }

    public SOAPFactory getSOAPFactory() {
        return this.soapVersion.getSOAPFactory();
    }

    public MessageFactory getMessageFactory() {
        return this.soapVersion.getMessageFactory();
    }
}


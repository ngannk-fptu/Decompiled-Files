/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.naming.StringRefAddr;
import org.apache.naming.AbstractRef;
import org.apache.naming.HandlerRef;

public class ServiceRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.webservices.ServiceRefFactory";
    public static final String SERVICE_INTERFACE = "serviceInterface";
    public static final String SERVICE_NAMESPACE = "service namespace";
    public static final String SERVICE_LOCAL_PART = "service local part";
    public static final String WSDL = "wsdl";
    public static final String JAXRPCMAPPING = "jaxrpcmapping";
    public static final String PORTCOMPONENTLINK = "portcomponentlink";
    public static final String SERVICEENDPOINTINTERFACE = "serviceendpointinterface";
    private final List<HandlerRef> handlers = new CopyOnWriteArrayList<HandlerRef>();

    public ServiceRef(String refname, String serviceInterface, String[] serviceQname, String wsdl, String jaxrpcmapping) {
        this(refname, serviceInterface, serviceQname, wsdl, jaxrpcmapping, null, null);
    }

    public ServiceRef(String refname, String serviceInterface, String[] serviceQname, String wsdl, String jaxrpcmapping, String factory, String factoryLocation) {
        super(serviceInterface, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (serviceInterface != null) {
            refAddr = new StringRefAddr(SERVICE_INTERFACE, serviceInterface);
            this.add(refAddr);
        }
        if (serviceQname[0] != null) {
            refAddr = new StringRefAddr(SERVICE_NAMESPACE, serviceQname[0]);
            this.add(refAddr);
        }
        if (serviceQname[1] != null) {
            refAddr = new StringRefAddr(SERVICE_LOCAL_PART, serviceQname[1]);
            this.add(refAddr);
        }
        if (wsdl != null) {
            refAddr = new StringRefAddr(WSDL, wsdl);
            this.add(refAddr);
        }
        if (jaxrpcmapping != null) {
            refAddr = new StringRefAddr(JAXRPCMAPPING, jaxrpcmapping);
            this.add(refAddr);
        }
    }

    public HandlerRef getHandler() {
        return this.handlers.remove(0);
    }

    public int getHandlersSize() {
        return this.handlers.size();
    }

    public void addHandler(HandlerRef handler) {
        this.handlers.add(handler);
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}


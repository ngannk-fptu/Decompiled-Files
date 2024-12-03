/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.JAXBContext
 *  javax.xml.ws.Dispatch
 *  javax.xml.ws.Service
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.spi.ServiceDelegate
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentRegistry;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.client.WSServiceDelegate;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.ServiceDelegate;

public abstract class WSService
extends ServiceDelegate
implements ComponentRegistry {
    private final Set<Component> components = new CopyOnWriteArraySet<Component>();
    protected static final ThreadLocal<InitParams> INIT_PARAMS = new ThreadLocal();
    protected static final InitParams EMPTY_PARAMS = new InitParams();

    protected WSService() {
    }

    public abstract <T> T getPort(WSEndpointReference var1, Class<T> var2, WebServiceFeature ... var3);

    public abstract <T> Dispatch<T> createDispatch(QName var1, WSEndpointReference var2, Class<T> var3, Service.Mode var4, WebServiceFeature ... var5);

    public abstract Dispatch<Object> createDispatch(QName var1, WSEndpointReference var2, JAXBContext var3, Service.Mode var4, WebServiceFeature ... var5);

    @NotNull
    public abstract Container getContainer();

    @Override
    @Nullable
    public <S> S getSPI(@NotNull Class<S> spiType) {
        for (Component c : this.components) {
            S s = c.getSPI(spiType);
            if (s == null) continue;
            return s;
        }
        return this.getContainer().getSPI(spiType);
    }

    @Override
    @NotNull
    public Set<Component> getComponents() {
        return this.components;
    }

    public static WSService create(URL wsdlDocumentLocation, QName serviceName) {
        return new WSServiceDelegate(wsdlDocumentLocation, serviceName, Service.class, new WebServiceFeature[0]);
    }

    public static WSService create(QName serviceName) {
        return WSService.create(null, serviceName);
    }

    public static WSService create() {
        return WSService.create(null, new QName(WSService.class.getName(), "dummy"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Service create(URL wsdlDocumentLocation, QName serviceName, InitParams properties) {
        if (INIT_PARAMS.get() != null) {
            throw new IllegalStateException("someone left non-null InitParams");
        }
        INIT_PARAMS.set(properties);
        try {
            Service svc = Service.create((URL)wsdlDocumentLocation, (QName)serviceName);
            if (INIT_PARAMS.get() != null) {
                throw new IllegalStateException("Service " + svc + " didn't recognize InitParams");
            }
            Service service = svc;
            return service;
        }
        finally {
            INIT_PARAMS.set(null);
        }
    }

    public static WSService unwrap(final Service svc) {
        return AccessController.doPrivileged(new PrivilegedAction<WSService>(){

            @Override
            public WSService run() {
                try {
                    Field f = svc.getClass().getField("delegate");
                    f.setAccessible(true);
                    Object delegate = f.get(svc);
                    if (!(delegate instanceof WSService)) {
                        throw new IllegalArgumentException();
                    }
                    return (WSService)delegate;
                }
                catch (NoSuchFieldException e) {
                    AssertionError x = new AssertionError((Object)"Unexpected service API implementation");
                    ((Throwable)((Object)x)).initCause(e);
                    throw x;
                }
                catch (IllegalAccessException e) {
                    IllegalAccessError x = new IllegalAccessError(e.getMessage());
                    x.initCause(e);
                    throw x;
                }
            }
        });
    }

    public static final class InitParams {
        private Container container;

        public void setContainer(Container c) {
            this.container = c;
        }

        public Container getContainer() {
            return this.container;
        }
    }
}


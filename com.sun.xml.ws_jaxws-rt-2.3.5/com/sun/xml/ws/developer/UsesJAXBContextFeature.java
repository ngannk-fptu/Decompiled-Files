/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.JAXBRIContext
 *  com.sun.xml.bind.api.TypeReference
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.developer;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.developer.JAXBContextFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class UsesJAXBContextFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.dev.java.net/features/uses-jaxb-context";
    private final JAXBContextFactory factory;

    @FeatureConstructor(value={"value"})
    public UsesJAXBContextFeature(@NotNull Class<? extends JAXBContextFactory> factoryClass) {
        try {
            this.factory = factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (InstantiationException e) {
            InstantiationError x = new InstantiationError(e.getMessage());
            x.initCause(e);
            throw x;
        }
        catch (IllegalAccessException e) {
            IllegalAccessError x = new IllegalAccessError(e.getMessage());
            x.initCause(e);
            throw x;
        }
        catch (InvocationTargetException e) {
            InstantiationError x = new InstantiationError(e.getMessage());
            x.initCause(e);
            throw x;
        }
        catch (NoSuchMethodException e) {
            NoSuchMethodError x = new NoSuchMethodError(e.getMessage());
            x.initCause(e);
            throw x;
        }
    }

    public UsesJAXBContextFeature(@Nullable JAXBContextFactory factory) {
        this.factory = factory;
    }

    public UsesJAXBContextFeature(final @Nullable JAXBRIContext context) {
        this.factory = new JAXBContextFactory(){

            @Override
            @NotNull
            public JAXBRIContext createJAXBContext(@NotNull SEIModel sei, @NotNull List<Class> classesToBind, @NotNull List<TypeReference> typeReferences) throws JAXBException {
                return context;
            }
        };
    }

    @ManagedAttribute
    @Nullable
    public JAXBContextFactory getFactory() {
        return this.factory;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }
}


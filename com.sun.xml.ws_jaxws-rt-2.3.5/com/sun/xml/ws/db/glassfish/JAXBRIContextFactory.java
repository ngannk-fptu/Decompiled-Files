/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.CompositeStructure
 *  com.sun.xml.bind.api.JAXBRIContext
 *  com.sun.xml.bind.api.TypeReference
 *  com.sun.xml.bind.v2.ContextFactory
 *  com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader
 *  com.sun.xml.bind.v2.runtime.MarshallerImpl
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Marshaller
 */
package com.sun.xml.ws.db.glassfish;

import com.sun.xml.bind.api.CompositeStructure;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.bind.v2.ContextFactory;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.bind.v2.runtime.MarshallerImpl;
import com.sun.xml.ws.db.glassfish.JAXBRIContextWrapper;
import com.sun.xml.ws.developer.JAXBContextFactory;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.BindingContextFactory;
import com.sun.xml.ws.spi.db.BindingInfo;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class JAXBRIContextFactory
extends BindingContextFactory {
    @Override
    public BindingContext newContext(JAXBContext context) {
        return new JAXBRIContextWrapper((JAXBRIContext)context, null);
    }

    @Override
    public BindingContext newContext(BindingInfo bi) {
        Class[] classes = bi.contentClasses().toArray(new Class[bi.contentClasses().size()]);
        for (int i = 0; i < classes.length; ++i) {
            if (!WrapperComposite.class.equals((Object)classes[i])) continue;
            classes[i] = CompositeStructure.class;
        }
        Map<TypeInfo, TypeReference> typeInfoMappings = this.typeInfoMappings(bi.typeInfos());
        Map<Class, Class> subclassReplacements = bi.subclassReplacements();
        String defaultNamespaceRemap = bi.getDefaultNamespace();
        Boolean c14nSupport = (Boolean)bi.properties().get("c14nSupport");
        RuntimeAnnotationReader ar = (RuntimeAnnotationReader)bi.properties().get("com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader");
        JAXBContextFactory jaxbContextFactory = (JAXBContextFactory)bi.properties().get(JAXBContextFactory.class.getName());
        try {
            JAXBRIContext context = jaxbContextFactory != null ? jaxbContextFactory.createJAXBContext(bi.getSEIModel(), this.toList(classes), this.toList(typeInfoMappings.values())) : ContextFactory.createContext((Class[])classes, typeInfoMappings.values(), subclassReplacements, (String)defaultNamespaceRemap, (boolean)(c14nSupport != null ? c14nSupport : false), (RuntimeAnnotationReader)ar, (boolean)false, (boolean)false, (boolean)false);
            return new JAXBRIContextWrapper(context, typeInfoMappings);
        }
        catch (Exception e) {
            throw new DatabindingException(e);
        }
    }

    private <T> List<T> toList(T[] a) {
        ArrayList<T> l = new ArrayList<T>();
        l.addAll(Arrays.asList(a));
        return l;
    }

    private <T> List<T> toList(Collection<T> col) {
        if (col instanceof List) {
            return (List)col;
        }
        ArrayList<T> l = new ArrayList<T>();
        l.addAll(col);
        return l;
    }

    private Map<TypeInfo, TypeReference> typeInfoMappings(Collection<TypeInfo> typeInfos) {
        HashMap<TypeInfo, TypeReference> map = new HashMap<TypeInfo, TypeReference>();
        for (TypeInfo ti : typeInfos) {
            Object type = WrapperComposite.class.equals((Object)ti.type) ? CompositeStructure.class : ti.type;
            TypeReference tr = new TypeReference(ti.tagName, (Type)type, ti.annotations);
            map.put(ti, tr);
        }
        return map;
    }

    @Override
    protected BindingContext getContext(Marshaller m) {
        return this.newContext((JAXBContext)((MarshallerImpl)m).getContext());
    }

    @Override
    protected boolean isFor(String str) {
        return str.equals("glassfish.jaxb") || str.equals(this.getClass().getName()) || str.equals("com.sun.xml.bind.v2.runtime");
    }
}


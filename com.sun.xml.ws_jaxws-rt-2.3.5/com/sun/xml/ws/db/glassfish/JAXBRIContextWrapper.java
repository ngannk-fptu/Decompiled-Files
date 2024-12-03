/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.api.JAXBRIContext
 *  com.sun.xml.bind.api.TypeReference
 *  com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfoSet
 *  com.sun.xml.bind.v2.runtime.JAXBContextImpl
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.SchemaOutputResolver
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.xml.ws.db.glassfish;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.ws.db.glassfish.BridgeWrapper;
import com.sun.xml.ws.db.glassfish.MarshallerBridge;
import com.sun.xml.ws.db.glassfish.RawAccessorWrapper;
import com.sun.xml.ws.db.glassfish.WrapperBridge;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

class JAXBRIContextWrapper
implements BindingContext {
    private Map<TypeInfo, TypeReference> typeRefs;
    private Map<TypeReference, TypeInfo> typeInfos;
    private JAXBRIContext context;

    JAXBRIContextWrapper(JAXBRIContext cxt, Map<TypeInfo, TypeReference> refs) {
        this.context = cxt;
        this.typeRefs = refs;
        if (refs != null) {
            this.typeInfos = new HashMap<TypeReference, TypeInfo>();
            for (TypeInfo ti : refs.keySet()) {
                this.typeInfos.put(this.typeRefs.get(ti), ti);
            }
        }
    }

    TypeReference typeReference(TypeInfo ti) {
        return this.typeRefs != null ? this.typeRefs.get(ti) : null;
    }

    TypeInfo typeInfo(TypeReference tr) {
        return this.typeInfos != null ? this.typeInfos.get(tr) : null;
    }

    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return this.context.createMarshaller();
    }

    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        return this.context.createUnmarshaller();
    }

    @Override
    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        this.context.generateSchema(outputResolver);
    }

    @Override
    public String getBuildId() {
        return this.context.getBuildId();
    }

    @Override
    public QName getElementName(Class o) throws JAXBException {
        return this.context.getElementName(o);
    }

    @Override
    public QName getElementName(Object o) throws JAXBException {
        return this.context.getElementName(o);
    }

    @Override
    public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> wrapperBean, String nsUri, String localName) throws JAXBException {
        return new RawAccessorWrapper(this.context.getElementPropertyAccessor(wrapperBean, nsUri, localName));
    }

    @Override
    public List<String> getKnownNamespaceURIs() {
        return this.context.getKnownNamespaceURIs();
    }

    public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
        return this.context.getRuntimeTypeInfoSet();
    }

    public QName getTypeName(TypeReference tr) {
        return this.context.getTypeName(tr);
    }

    public int hashCode() {
        return this.context.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        JAXBRIContextWrapper other = (JAXBRIContextWrapper)obj;
        return this.context == other.context || this.context != null && this.context.equals(other.context);
    }

    @Override
    public boolean hasSwaRef() {
        return this.context.hasSwaRef();
    }

    public String toString() {
        return JAXBRIContextWrapper.class.getName() + " : " + this.context.toString();
    }

    @Override
    public XMLBridge createBridge(TypeInfo ti) {
        TypeReference tr = this.typeRefs.get(ti);
        Bridge b = this.context.createBridge(tr);
        return WrapperComposite.class.equals((Object)ti.type) ? new WrapperBridge(this, b) : new BridgeWrapper(this, b);
    }

    @Override
    public JAXBContext getJAXBContext() {
        return this.context;
    }

    @Override
    public QName getTypeName(TypeInfo ti) {
        TypeReference tr = this.typeRefs.get(ti);
        return this.context.getTypeName(tr);
    }

    @Override
    public XMLBridge createFragmentBridge() {
        return new MarshallerBridge((JAXBContextImpl)this.context);
    }

    @Override
    public Object newWrapperInstace(Class<?> wrapperType) throws InstantiationException, IllegalAccessException {
        return wrapperType.newInstance();
    }
}


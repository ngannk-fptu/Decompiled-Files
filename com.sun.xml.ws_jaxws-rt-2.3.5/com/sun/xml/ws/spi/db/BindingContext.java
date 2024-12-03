/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.SchemaOutputResolver
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.xml.ws.spi.db;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public interface BindingContext {
    public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.bind.defaultNamespaceRemap";
    public static final String TYPE_REFERENCES = "com.sun.xml.bind.typeReferences";
    public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.bind.c14n";
    public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.bind.treatEverythingNillable";
    public static final String ENABLE_XOP = "com.sun.xml.bind.XOP";
    public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.bind.subclassReplacements";
    public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.bind.XmlAccessorFactory";
    public static final String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";

    public Marshaller createMarshaller() throws JAXBException;

    public Unmarshaller createUnmarshaller() throws JAXBException;

    public JAXBContext getJAXBContext();

    public Object newWrapperInstace(Class<?> var1) throws InstantiationException, IllegalAccessException;

    public boolean hasSwaRef();

    @Nullable
    public QName getElementName(@NotNull Object var1) throws JAXBException;

    @Nullable
    public QName getElementName(@NotNull Class var1) throws JAXBException;

    public XMLBridge createBridge(@NotNull TypeInfo var1);

    public XMLBridge createFragmentBridge();

    public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> var1, String var2, String var3) throws JAXBException;

    @NotNull
    public List<String> getKnownNamespaceURIs();

    public void generateSchema(@NotNull SchemaOutputResolver var1) throws IOException;

    public QName getTypeName(@NotNull TypeInfo var1);

    @NotNull
    public String getBuildId();
}


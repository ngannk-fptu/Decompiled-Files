/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ext;

import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.util.Provider;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OptionalHandlerFactory {
    private static final String PACKAGE_PREFIX_JODA_DATETIME = "org.joda.time.";
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JODA_DATETIME = "org.codehaus.jackson.map.ext.JodaSerializers";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "org.codehaus.jackson.map.ext.CoreXMLSerializers";
    private static final String DESERIALIZERS_FOR_JODA_DATETIME = "org.codehaus.jackson.map.ext.JodaDeserializers";
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "org.codehaus.jackson.map.ext.CoreXMLDeserializers";
    private static final String CLASS_NAME_DOM_NODE = "org.w3c.dom.Node";
    private static final String CLASS_NAME_DOM_DOCUMENT = "org.w3c.dom.Node";
    private static final String SERIALIZER_FOR_DOM_NODE = "org.codehaus.jackson.map.ext.DOMSerializer";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "org.codehaus.jackson.map.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "org.codehaus.jackson.map.ext.DOMDeserializer$NodeDeserializer";
    public static final OptionalHandlerFactory instance = new OptionalHandlerFactory();

    protected OptionalHandlerFactory() {
    }

    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type) {
        String factoryName;
        Class<?> rawType = type.getRawClass();
        String className = rawType.getName();
        if (className.startsWith(PACKAGE_PREFIX_JODA_DATETIME)) {
            factoryName = SERIALIZERS_FOR_JODA_DATETIME;
        } else if (className.startsWith(PACKAGE_PREFIX_JAVAX_XML) || this.hasSupertypeStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) {
            factoryName = SERIALIZERS_FOR_JAVAX_XML;
        } else {
            if (this.doesImplement(rawType, "org.w3c.dom.Node")) {
                return (JsonSerializer)this.instantiate(SERIALIZER_FOR_DOM_NODE);
            }
            return null;
        }
        Object ob = this.instantiate(factoryName);
        if (ob == null) {
            return null;
        }
        Provider prov = (Provider)ob;
        Collection entries = prov.provide();
        for (Map.Entry entry : entries) {
            if (rawType != entry.getKey()) continue;
            return (JsonSerializer)entry.getValue();
        }
        for (Map.Entry entry : entries) {
            if (!((Class)entry.getKey()).isAssignableFrom(rawType)) continue;
            return (JsonSerializer)entry.getValue();
        }
        return null;
    }

    public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, DeserializerProvider p) {
        String factoryName;
        Class<?> rawType = type.getRawClass();
        String className = rawType.getName();
        if (className.startsWith(PACKAGE_PREFIX_JODA_DATETIME)) {
            factoryName = DESERIALIZERS_FOR_JODA_DATETIME;
        } else if (className.startsWith(PACKAGE_PREFIX_JAVAX_XML) || this.hasSupertypeStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) {
            factoryName = DESERIALIZERS_FOR_JAVAX_XML;
        } else {
            if (this.doesImplement(rawType, "org.w3c.dom.Node")) {
                return (JsonDeserializer)this.instantiate(DESERIALIZER_FOR_DOM_DOCUMENT);
            }
            if (this.doesImplement(rawType, "org.w3c.dom.Node")) {
                return (JsonDeserializer)this.instantiate(DESERIALIZER_FOR_DOM_NODE);
            }
            return null;
        }
        Object ob = this.instantiate(factoryName);
        if (ob == null) {
            return null;
        }
        Provider prov = (Provider)ob;
        Collection entries = prov.provide();
        for (StdDeserializer deser : entries) {
            if (rawType != deser.getValueClass()) continue;
            return deser;
        }
        for (StdDeserializer deser : entries) {
            if (!deser.getValueClass().isAssignableFrom(rawType)) continue;
            return deser;
        }
        return null;
    }

    private Object instantiate(String className) {
        try {
            return Class.forName(className).newInstance();
        }
        catch (LinkageError linkageError) {
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    private boolean doesImplement(Class<?> actualType, String classNameToImplement) {
        for (Class<?> type = actualType; type != null; type = type.getSuperclass()) {
            if (type.getName().equals(classNameToImplement)) {
                return true;
            }
            if (!this.hasInterface(type, classNameToImplement)) continue;
            return true;
        }
        return false;
    }

    private boolean hasInterface(Class<?> type, String interfaceToImplement) {
        Class<?>[] interfaces;
        for (Class<?> iface : interfaces = type.getInterfaces()) {
            if (!iface.getName().equals(interfaceToImplement)) continue;
            return true;
        }
        for (Class<?> iface : interfaces) {
            if (!this.hasInterface(iface, interfaceToImplement)) continue;
            return true;
        }
        return false;
    }

    private boolean hasSupertypeStartingWith(Class<?> rawType, String prefix) {
        for (Class<?> supertype = rawType.getSuperclass(); supertype != null; supertype = supertype.getSuperclass()) {
            if (!supertype.getName().startsWith(prefix)) continue;
            return true;
        }
        for (Class<?> cls = rawType; cls != null; cls = cls.getSuperclass()) {
            if (!this.hasInterfaceStartingWith(cls, prefix)) continue;
            return true;
        }
        return false;
    }

    private boolean hasInterfaceStartingWith(Class<?> type, String prefix) {
        Class<?>[] interfaces;
        for (Class<?> iface : interfaces = type.getInterfaces()) {
            if (!iface.getName().startsWith(prefix)) continue;
            return true;
        }
        for (Class<?> iface : interfaces) {
            if (!this.hasInterfaceStartingWith(iface, prefix)) continue;
            return true;
        }
        return false;
    }
}


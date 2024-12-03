/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jettison.mapped.DefaultConverter;
import org.codehaus.jettison.mapped.TypeConverter;

public class Configuration {
    private static final String JETTISON_TYPE_CONVERTER_CLASS_KEY = "jettison.mapped.typeconverter.class";
    private static final ConverterFactory converterFactory;
    private Map xmlToJsonNamespaces;
    private List attributesAsElements;
    private List ignoredElements;
    private boolean supressAtAttributes;
    private String attributeKey = "@";
    private boolean ignoreNamespaces;
    private boolean dropRootElement;
    private boolean rootElementArrayWrapper = true;
    private Set primitiveArrayKeys = Collections.EMPTY_SET;
    private boolean writeNullAsString = true;
    private boolean readNullAsString;
    private boolean ignoreEmptyArrayValues;
    private boolean escapeForwardSlashAlways;
    private String jsonNamespaceSeparator;
    private TypeConverter typeConverter = converterFactory.newDefaultConverterInstance();

    public Configuration() {
        this.xmlToJsonNamespaces = new HashMap();
    }

    public Configuration(Map xmlToJsonNamespaces) {
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
    }

    public Configuration(Map xmlToJsonNamespaces, List attributesAsElements, List ignoredElements) {
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
        this.attributesAsElements = attributesAsElements;
        this.ignoredElements = ignoredElements;
    }

    public boolean isIgnoreNamespaces() {
        return this.ignoreNamespaces;
    }

    public void setIgnoreNamespaces(boolean ignoreNamespaces) {
        this.ignoreNamespaces = ignoreNamespaces;
    }

    public List getAttributesAsElements() {
        return this.attributesAsElements;
    }

    public void setAttributesAsElements(List attributesAsElements) {
        this.attributesAsElements = attributesAsElements;
    }

    public List getIgnoredElements() {
        return this.ignoredElements;
    }

    public void setIgnoredElements(List ignoredElements) {
        this.ignoredElements = ignoredElements;
    }

    public Map getXmlToJsonNamespaces() {
        return this.xmlToJsonNamespaces;
    }

    public void setXmlToJsonNamespaces(Map xmlToJsonNamespaces) {
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
    }

    public TypeConverter getTypeConverter() {
        return this.typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    public boolean isSupressAtAttributes() {
        return this.supressAtAttributes;
    }

    public void setSupressAtAttributes(boolean supressAtAttributes) {
        this.supressAtAttributes = supressAtAttributes;
    }

    public String getAttributeKey() {
        return this.attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    static TypeConverter newDefaultConverterInstance() {
        return converterFactory.newDefaultConverterInstance();
    }

    public Set getPrimitiveArrayKeys() {
        return this.primitiveArrayKeys;
    }

    public void setPrimitiveArrayKeys(Set primitiveArrayKeys) {
        this.primitiveArrayKeys = primitiveArrayKeys;
    }

    public boolean isDropRootElement() {
        return this.dropRootElement;
    }

    public void setDropRootElement(boolean dropRootElement) {
        this.dropRootElement = dropRootElement;
    }

    public boolean isRootElementArrayWrapper() {
        return this.rootElementArrayWrapper;
    }

    public void setRootElementArrayWrapper(boolean rootElementArrayWrapper) {
        this.rootElementArrayWrapper = rootElementArrayWrapper;
    }

    public boolean isWriteNullAsString() {
        return this.writeNullAsString;
    }

    public void setWriteNullAsString(boolean writeNullAsString) {
        this.writeNullAsString = writeNullAsString;
    }

    public boolean isReadNullAsString() {
        return this.readNullAsString;
    }

    public void setReadNullAsString(boolean readNullString) {
        this.readNullAsString = readNullString;
    }

    public boolean isIgnoreEmptyArrayValues() {
        return this.ignoreEmptyArrayValues;
    }

    public void setIgnoreEmptyArrayValues(boolean ignoreEmptyArrayValues) {
        this.ignoreEmptyArrayValues = ignoreEmptyArrayValues;
    }

    @Deprecated
    public void setReadNullAsEmptyString(boolean read) {
    }

    public boolean isEscapeForwardSlashAlways() {
        return this.escapeForwardSlashAlways;
    }

    public void setEscapeForwardSlashAlways(boolean escapeForwardSlash) {
        this.escapeForwardSlashAlways = escapeForwardSlash;
    }

    public String getJsonNamespaceSeparator() {
        return this.jsonNamespaceSeparator;
    }

    public void setJsonNamespaceSeparator(String jsonNamespaceSeparator) {
        this.jsonNamespaceSeparator = jsonNamespaceSeparator;
    }

    static {
        ConverterFactory cf = null;
        String userSpecifiedClass = System.getProperty(JETTISON_TYPE_CONVERTER_CLASS_KEY);
        if (userSpecifiedClass != null && userSpecifiedClass.length() > 0) {
            try {
                final Class<TypeConverter> tc = Class.forName(userSpecifiedClass).asSubclass(TypeConverter.class);
                tc.newInstance();
                cf = new ConverterFactory(){

                    @Override
                    public TypeConverter newDefaultConverterInstance() {
                        try {
                            return (TypeConverter)tc.newInstance();
                        }
                        catch (Exception e) {
                            throw new ExceptionInInitializerError(e);
                        }
                    }
                };
            }
            catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        if (cf == null) {
            cf = new ConverterFactory();
        }
        converterFactory = cf;
    }

    private static class ConverterFactory {
        private ConverterFactory() {
        }

        TypeConverter newDefaultConverterInstance() {
            return new DefaultConverter();
        }
    }
}


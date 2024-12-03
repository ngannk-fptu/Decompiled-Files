/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.Validator
 */
package com.sun.jersey.api.json;

import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.api.json.JSONUnmarshaller;
import com.sun.jersey.json.impl.BaseJSONMarshaller;
import com.sun.jersey.json.impl.BaseJSONUnmarshaller;
import com.sun.jersey.json.impl.JSONHelper;
import com.sun.jersey.json.impl.JSONMarshallerImpl;
import com.sun.jersey.json.impl.JSONUnmarshallerImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

public final class JSONJAXBContext
extends JAXBContext
implements JSONConfigurated {
    @Deprecated
    public static final String NAMESPACE = "com.sun.jersey.impl.json.";
    @Deprecated
    public static final String JSON_NOTATION = "com.sun.jersey.impl.json.notation";
    @Deprecated
    public static final String JSON_ENABLED = "com.sun.jersey.impl.json.enabled";
    @Deprecated
    public static final String JSON_ROOT_UNWRAPPING = "com.sun.jersey.impl.json.root.unwrapping";
    @Deprecated
    public static final String JSON_ARRAYS = "com.sun.jersey.impl.json.arrays";
    @Deprecated
    public static final String JSON_NON_STRINGS = "com.sun.jersey.impl.json.non.strings";
    @Deprecated
    public static final String JSON_ATTRS_AS_ELEMS = "com.sun.jersey.impl.json.attrs.as.elems";
    @Deprecated
    public static final String JSON_XML2JSON_NS = "com.sun.jersey.impl.json.xml.to.json.ns";
    private static final Map<String, Object> defaultJsonProperties = new HashMap<String, Object>();
    private JSONConfiguration jsonConfiguration;
    private final JAXBContext jaxbContext;
    static final Map<String, JSONConfiguration.Notation> _notationMap;

    public JSONJAXBContext(Class ... classesToBeBound) throws JAXBException {
        this(JSONConfiguration.DEFAULT, classesToBeBound);
    }

    public JSONJAXBContext(JSONConfiguration config, Class ... classesToBeBound) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        this.jsonConfiguration = config;
        this.jaxbContext = config.getNotation() == JSONConfiguration.Notation.NATURAL ? JAXBContext.newInstance((Class[])classesToBeBound, JSONHelper.createPropertiesForJaxbContext(Collections.emptyMap())) : JAXBContext.newInstance((Class[])classesToBeBound);
    }

    public JSONJAXBContext(Class[] classesToBeBound, Map<String, Object> properties) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance((Class[])classesToBeBound, this.createProperties(properties));
        if (this.jsonConfiguration == null) {
            this.jsonConfiguration = JSONConfiguration.DEFAULT;
        }
    }

    public JSONJAXBContext(JSONConfiguration config, Class[] classesToBeBound, Map<String, Object> properties) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        this.jsonConfiguration = config;
        if (config.getNotation() == JSONConfiguration.Notation.NATURAL) {
            Map<String, Object> myProps = JSONHelper.createPropertiesForJaxbContext(properties);
            this.jaxbContext = JAXBContext.newInstance((Class[])classesToBeBound, myProps);
        } else {
            this.jaxbContext = JAXBContext.newInstance((Class[])classesToBeBound, properties);
        }
    }

    public JSONJAXBContext(String contextPath) throws JAXBException {
        this(JSONConfiguration.DEFAULT, contextPath);
    }

    public JSONJAXBContext(JSONConfiguration config, String contextPath) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        this.jaxbContext = config.getNotation() == JSONConfiguration.Notation.NATURAL ? JAXBContext.newInstance((String)contextPath, (ClassLoader)Thread.currentThread().getContextClassLoader(), this.createProperties(JSONHelper.createPropertiesForJaxbContext(Collections.emptyMap()))) : JAXBContext.newInstance((String)contextPath, (ClassLoader)Thread.currentThread().getContextClassLoader());
        this.jsonConfiguration = config;
    }

    public JSONJAXBContext(String contextPath, ClassLoader classLoader) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance((String)contextPath, (ClassLoader)classLoader);
        this.jsonConfiguration = JSONConfiguration.DEFAULT;
    }

    public JSONJAXBContext(String contextPath, ClassLoader classLoader, Map<String, Object> properties) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance((String)contextPath, (ClassLoader)classLoader, this.createProperties(properties));
        if (this.jsonConfiguration == null) {
            this.jsonConfiguration = JSONConfiguration.DEFAULT;
        }
    }

    public JSONJAXBContext(JSONConfiguration config, String contextPath, ClassLoader classLoader, Map<String, Object> properties) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        if (config.getNotation() == JSONConfiguration.Notation.NATURAL) {
            Map<String, Object> myProps = JSONHelper.createPropertiesForJaxbContext(properties);
            this.jaxbContext = JAXBContext.newInstance((String)contextPath, (ClassLoader)classLoader, myProps);
        } else {
            this.jaxbContext = JAXBContext.newInstance((String)contextPath, (ClassLoader)classLoader, properties);
        }
        this.jsonConfiguration = config;
    }

    public static JSONMarshaller getJSONMarshaller(Marshaller marshaller, JAXBContext jaxbContext) {
        if (marshaller instanceof JSONMarshaller) {
            return (JSONMarshaller)marshaller;
        }
        return new BaseJSONMarshaller(marshaller, jaxbContext, JSONConfiguration.DEFAULT);
    }

    public static JSONUnmarshaller getJSONUnmarshaller(Unmarshaller unmarshaller, JAXBContext jaxbContext) {
        if (unmarshaller instanceof JSONUnmarshaller) {
            return (JSONUnmarshaller)unmarshaller;
        }
        return new BaseJSONUnmarshaller(unmarshaller, jaxbContext, JSONConfiguration.DEFAULT);
    }

    @Override
    public JSONConfiguration getJSONConfiguration() {
        return this.jsonConfiguration;
    }

    public JSONUnmarshaller createJSONUnmarshaller() throws JAXBException {
        return new JSONUnmarshallerImpl(this, this.getJSONConfiguration());
    }

    public JSONMarshaller createJSONMarshaller() throws JAXBException {
        return new JSONMarshallerImpl(this, this.getJSONConfiguration());
    }

    public Unmarshaller createUnmarshaller() throws JAXBException {
        return new JSONUnmarshallerImpl(this.jaxbContext, this.getJSONConfiguration());
    }

    public Marshaller createMarshaller() throws JAXBException {
        return new JSONMarshallerImpl(this.jaxbContext, this.getJSONConfiguration());
    }

    public Validator createValidator() throws JAXBException {
        return this.jaxbContext.createValidator();
    }

    public JAXBContext getOriginalJaxbContext() {
        return this.jaxbContext;
    }

    private Map<String, Object> createProperties(Map<String, Object> properties) {
        Map<String, Object> workProperties = new HashMap<String, Object>();
        workProperties.putAll(defaultJsonProperties);
        workProperties.putAll(properties);
        if (JSONNotation.NATURAL == workProperties.get(JSON_NOTATION)) {
            workProperties = JSONHelper.createPropertiesForJaxbContext(workProperties);
        }
        this.processProperties(workProperties);
        return workProperties;
    }

    private void processProperties(Map<String, Object> properties) {
        HashSet<String> jsonKeys = new HashSet<String>();
        for (String k : Collections.unmodifiableSet(properties.keySet())) {
            if (!k.startsWith(NAMESPACE)) continue;
            jsonKeys.add(k);
        }
        if (!jsonKeys.isEmpty() && this.jsonConfiguration == null) {
            Object nO;
            JSONConfiguration.Notation pNotation = JSONConfiguration.Notation.MAPPED;
            if (properties.containsKey(JSON_NOTATION) && ((nO = properties.get(JSON_NOTATION)) instanceof JSONNotation || nO instanceof String)) {
                pNotation = _notationMap.get(nO.toString());
            }
            this.jsonConfiguration = this.getConfiguration(pNotation, properties);
        }
        for (String k : jsonKeys) {
            properties.remove(k);
        }
    }

    private JSONConfiguration getConfiguration(JSONConfiguration.Notation pNotation, Map<String, Object> properties) {
        String[] a = new String[]{};
        switch (pNotation) {
            case BADGERFISH: {
                return JSONConfiguration.badgerFish().build();
            }
            case MAPPED_JETTISON: {
                JSONConfiguration.MappedJettisonBuilder mappedJettisonBuilder = JSONConfiguration.mappedJettison();
                if (properties.containsKey(JSON_XML2JSON_NS)) {
                    mappedJettisonBuilder.xml2JsonNs((Map)properties.get(JSON_XML2JSON_NS));
                }
                return mappedJettisonBuilder.build();
            }
            case NATURAL: {
                return JSONConfiguration.natural().build();
            }
        }
        JSONConfiguration.MappedBuilder mappedBuilder = JSONConfiguration.mapped();
        if (properties.containsKey(JSON_ARRAYS)) {
            mappedBuilder.arrays(((Collection)properties.get(JSON_ARRAYS)).toArray(a));
        }
        if (properties.containsKey(JSON_ATTRS_AS_ELEMS)) {
            mappedBuilder.attributeAsElement(((Collection)properties.get(JSON_ATTRS_AS_ELEMS)).toArray(a));
        }
        if (properties.containsKey(JSON_NON_STRINGS)) {
            mappedBuilder.nonStrings(((Collection)properties.get(JSON_NON_STRINGS)).toArray(a));
        }
        if (properties.containsKey(JSON_ROOT_UNWRAPPING)) {
            mappedBuilder.rootUnwrapping((Boolean)properties.get(JSON_ROOT_UNWRAPPING));
        }
        return mappedBuilder.build();
    }

    static {
        defaultJsonProperties.put(JSON_NOTATION, (Object)JSONNotation.MAPPED);
        defaultJsonProperties.put(JSON_ROOT_UNWRAPPING, Boolean.TRUE);
        _notationMap = new HashMap<String, JSONConfiguration.Notation>(){
            {
                this.put(JSONNotation.BADGERFISH.toString(), JSONConfiguration.Notation.BADGERFISH);
                this.put(JSONNotation.MAPPED.toString(), JSONConfiguration.Notation.MAPPED);
                this.put(JSONNotation.MAPPED_JETTISON.toString(), JSONConfiguration.Notation.MAPPED_JETTISON);
                this.put(JSONNotation.NATURAL.toString(), JSONConfiguration.Notation.NATURAL);
            }
        };
    }

    @Deprecated
    public static enum JSONNotation {
        MAPPED,
        MAPPED_JETTISON,
        BADGERFISH,
        NATURAL;

    }
}


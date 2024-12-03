/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.jaxrs;

import java.util.ArrayList;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class MapperConfigurator {
    protected ObjectMapper _mapper;
    protected ObjectMapper _defaultMapper;
    protected Annotations[] _defaultAnnotationsToUse;
    protected Class<? extends AnnotationIntrospector> _jaxbIntrospectorClass;

    public MapperConfigurator(ObjectMapper mapper, Annotations[] defAnnotations) {
        this._mapper = mapper;
        this._defaultAnnotationsToUse = defAnnotations;
    }

    public synchronized ObjectMapper getConfiguredMapper() {
        return this._mapper;
    }

    public synchronized ObjectMapper getDefaultMapper() {
        if (this._defaultMapper == null) {
            this._defaultMapper = new ObjectMapper();
            this._setAnnotations(this._defaultMapper, this._defaultAnnotationsToUse);
        }
        return this._defaultMapper;
    }

    public synchronized void setMapper(ObjectMapper m) {
        this._mapper = m;
    }

    public synchronized void setAnnotationsToUse(Annotations[] annotationsToUse) {
        this._setAnnotations(this.mapper(), annotationsToUse);
    }

    public synchronized void configure(DeserializationConfig.Feature f, boolean state) {
        this.mapper().configure(f, state);
    }

    public synchronized void configure(SerializationConfig.Feature f, boolean state) {
        this.mapper().configure(f, state);
    }

    public synchronized void configure(JsonParser.Feature f, boolean state) {
        this.mapper().configure(f, state);
    }

    public synchronized void configure(JsonGenerator.Feature f, boolean state) {
        this.mapper().configure(f, state);
    }

    protected ObjectMapper mapper() {
        if (this._mapper == null) {
            this._mapper = new ObjectMapper();
            this._setAnnotations(this._mapper, this._defaultAnnotationsToUse);
        }
        return this._mapper;
    }

    protected void _setAnnotations(ObjectMapper mapper, Annotations[] annotationsToUse) {
        AnnotationIntrospector intr = annotationsToUse == null || annotationsToUse.length == 0 ? AnnotationIntrospector.nopInstance() : this._resolveIntrospectors(annotationsToUse);
        mapper.getDeserializationConfig().setAnnotationIntrospector(intr);
        mapper.getSerializationConfig().setAnnotationIntrospector(intr);
    }

    protected AnnotationIntrospector _resolveIntrospectors(Annotations[] annotationsToUse) {
        ArrayList<AnnotationIntrospector> intr = new ArrayList<AnnotationIntrospector>();
        for (Annotations a : annotationsToUse) {
            if (a == null) continue;
            intr.add(this._resolveIntrospector(a));
        }
        int count = intr.size();
        if (count == 0) {
            return AnnotationIntrospector.nopInstance();
        }
        AnnotationIntrospector curr = (AnnotationIntrospector)intr.get(0);
        int len = intr.size();
        for (int i = 1; i < len; ++i) {
            curr = AnnotationIntrospector.pair(curr, (AnnotationIntrospector)intr.get(i));
        }
        return curr;
    }

    protected AnnotationIntrospector _resolveIntrospector(Annotations ann) {
        switch (ann) {
            case JACKSON: {
                return new JacksonAnnotationIntrospector();
            }
            case JAXB: {
                try {
                    if (this._jaxbIntrospectorClass == null) {
                        this._jaxbIntrospectorClass = JaxbAnnotationIntrospector.class;
                    }
                    return this._jaxbIntrospectorClass.newInstance();
                }
                catch (Exception e) {
                    throw new IllegalStateException("Failed to instantiate JaxbAnnotationIntrospector: " + e.getMessage(), e);
                }
            }
        }
        throw new IllegalStateException();
    }
}


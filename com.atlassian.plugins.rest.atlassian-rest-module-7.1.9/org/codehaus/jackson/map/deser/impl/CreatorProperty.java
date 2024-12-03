/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CreatorProperty
extends SettableBeanProperty {
    protected final AnnotatedParameter _annotated;
    protected final Object _injectableValueId;

    public CreatorProperty(String name, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, Object injectableValueId) {
        super(name, type, typeDeser, contextAnnotations);
        this._annotated = param;
        this._propertyIndex = index;
        this._injectableValueId = injectableValueId;
    }

    protected CreatorProperty(CreatorProperty src, JsonDeserializer<Object> deser) {
        super(src, deser);
        this._annotated = src._annotated;
        this._injectableValueId = src._injectableValueId;
    }

    @Override
    public CreatorProperty withValueDeserializer(JsonDeserializer<Object> deser) {
        return new CreatorProperty(this, deser);
    }

    public Object findInjectableValue(DeserializationContext context, Object beanInstance) {
        if (this._injectableValueId == null) {
            throw new IllegalStateException("Property '" + this.getName() + "' (type " + this.getClass().getName() + ") has no injectable value id configured");
        }
        return context.findInjectableValue(this._injectableValueId, this, beanInstance);
    }

    public void inject(DeserializationContext context, Object beanInstance) throws IOException {
        this.set(beanInstance, this.findInjectableValue(context, beanInstance));
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> acls) {
        if (this._annotated == null) {
            return null;
        }
        return this._annotated.getAnnotation(acls);
    }

    @Override
    public AnnotatedMember getMember() {
        return this._annotated;
    }

    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object instance) throws IOException, JsonProcessingException {
        this.set(instance, this.deserialize(jp, ctxt));
    }

    @Override
    public void set(Object instance, Object value) throws IOException {
    }

    @Override
    public Object getInjectableValueId() {
        return this._injectableValueId;
    }
}


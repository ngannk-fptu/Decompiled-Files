/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.type.JavaType
 *  org.codehaus.jackson.util.InternCache
 */
package org.codehaus.jackson.map.deser;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.InternCache;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SettableBeanProperty
implements BeanProperty {
    protected final String _propName;
    protected final JavaType _type;
    protected final Annotations _contextAnnotations;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected TypeDeserializer _valueTypeDeserializer;
    protected NullProvider _nullProvider;
    protected String _managedReferenceName;
    protected int _propertyIndex = -1;

    protected SettableBeanProperty(String propName, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations) {
        this._propName = propName == null || propName.length() == 0 ? "" : InternCache.instance.intern(propName);
        this._type = type;
        this._contextAnnotations = contextAnnotations;
        this._valueTypeDeserializer = typeDeser;
    }

    protected SettableBeanProperty(SettableBeanProperty src) {
        this._propName = src._propName;
        this._type = src._type;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._nullProvider = src._nullProvider;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
    }

    protected SettableBeanProperty(SettableBeanProperty src, JsonDeserializer<Object> deser) {
        Object nvl;
        this._propName = src._propName;
        this._type = src._type;
        this._contextAnnotations = src._contextAnnotations;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._valueDeserializer = deser;
        this._nullProvider = deser == null ? null : ((nvl = deser.getNullValue()) == null ? null : new NullProvider(this._type, nvl));
    }

    @Deprecated
    public void setValueDeserializer(JsonDeserializer<Object> deser) {
        if (this._valueDeserializer != null) {
            throw new IllegalStateException("Already had assigned deserializer for property '" + this.getName() + "' (class " + this.getDeclaringClass().getName() + ")");
        }
        this._valueDeserializer = deser;
        Object nvl = this._valueDeserializer.getNullValue();
        this._nullProvider = nvl == null ? null : new NullProvider(this._type, nvl);
    }

    public abstract SettableBeanProperty withValueDeserializer(JsonDeserializer<Object> var1);

    public void setManagedReferenceName(String n) {
        this._managedReferenceName = n;
    }

    public void assignIndex(int index) {
        if (this._propertyIndex != -1) {
            throw new IllegalStateException("Property '" + this.getName() + "' already had index (" + this._propertyIndex + "), trying to assign " + index);
        }
        this._propertyIndex = index;
    }

    @Override
    public final String getName() {
        return this._propName;
    }

    @Override
    public JavaType getType() {
        return this._type;
    }

    @Override
    public abstract <A extends Annotation> A getAnnotation(Class<A> var1);

    @Override
    public abstract AnnotatedMember getMember();

    @Override
    public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
        return this._contextAnnotations.get(acls);
    }

    protected final Class<?> getDeclaringClass() {
        return this.getMember().getDeclaringClass();
    }

    @Deprecated
    public String getPropertyName() {
        return this._propName;
    }

    public String getManagedReferenceName() {
        return this._managedReferenceName;
    }

    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null;
    }

    public boolean hasValueTypeDeserializer() {
        return this._valueTypeDeserializer != null;
    }

    public JsonDeserializer<Object> getValueDeserializer() {
        return this._valueDeserializer;
    }

    public TypeDeserializer getValueTypeDeserializer() {
        return this._valueTypeDeserializer;
    }

    public int getPropertyIndex() {
        return this._propertyIndex;
    }

    @Deprecated
    public int getProperytIndex() {
        return this.getPropertyIndex();
    }

    public Object getInjectableValueId() {
        return null;
    }

    public abstract void deserializeAndSet(JsonParser var1, DeserializationContext var2, Object var3) throws IOException, JsonProcessingException;

    public abstract void set(Object var1, Object var2) throws IOException;

    public final Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return this._nullProvider == null ? null : this._nullProvider.nullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(jp, ctxt);
    }

    protected void _throwAsIOE(Exception e, Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            String actType = value == null ? "[NULL]" : value.getClass().getName();
            StringBuilder msg = new StringBuilder("Problem deserializing property '").append(this.getPropertyName());
            msg.append("' (expected type: ").append(this.getType());
            msg.append("; actual type: ").append(actType).append(")");
            String origMsg = e.getMessage();
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            } else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException(msg.toString(), null, e);
        }
        this._throwAsIOE(e);
    }

    protected IOException _throwAsIOE(Exception e) throws IOException {
        if (e instanceof IOException) {
            throw (IOException)e;
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        Throwable th = e;
        while (th.getCause() != null) {
            th = th.getCause();
        }
        throw new JsonMappingException(th.getMessage(), null, th);
    }

    public String toString() {
        return "[property '" + this.getName() + "']";
    }

    protected static final class NullProvider {
        private final Object _nullValue;
        private final boolean _isPrimitive;
        private final Class<?> _rawType;

        protected NullProvider(JavaType type, Object nullValue) {
            this._nullValue = nullValue;
            this._isPrimitive = type.isPrimitive();
            this._rawType = type.getRawClass();
        }

        public Object nullValue(DeserializationContext ctxt) throws JsonProcessingException {
            if (this._isPrimitive && ctxt.isEnabled(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                throw ctxt.mappingException("Can not map JSON null into type " + this._rawType.getName() + " (set DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)");
            }
            return this._nullValue;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class InnerClassProperty
    extends SettableBeanProperty {
        protected final SettableBeanProperty _delegate;
        protected final Constructor<?> _creator;

        public InnerClassProperty(SettableBeanProperty delegate, Constructor<?> ctor) {
            super(delegate);
            this._delegate = delegate;
            this._creator = ctor;
        }

        protected InnerClassProperty(InnerClassProperty src, JsonDeserializer<Object> deser) {
            super(src, deser);
            this._delegate = src._delegate.withValueDeserializer(deser);
            this._creator = src._creator;
        }

        @Override
        public InnerClassProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new InnerClassProperty(this, deser);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
            return this._delegate.getAnnotation(acls);
        }

        @Override
        public AnnotatedMember getMember() {
            return this._delegate.getMember();
        }

        @Override
        public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
            Object value;
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NULL) {
                value = this._nullProvider == null ? null : this._nullProvider.nullValue(ctxt);
            } else if (this._valueTypeDeserializer != null) {
                value = this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
            } else {
                try {
                    value = this._creator.newInstance(bean);
                }
                catch (Exception e) {
                    ClassUtil.unwrapAndThrowAsIAE(e, "Failed to instantiate class " + this._creator.getDeclaringClass().getName() + ", problem: " + e.getMessage());
                    value = null;
                }
                this._valueDeserializer.deserialize(jp, ctxt, value);
            }
            this.set(bean, value);
        }

        @Override
        public final void set(Object instance, Object value) throws IOException {
            this._delegate.set(instance, value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class ManagedReferenceProperty
    extends SettableBeanProperty {
        protected final String _referenceName;
        protected final boolean _isContainer;
        protected final SettableBeanProperty _managedProperty;
        protected final SettableBeanProperty _backProperty;

        public ManagedReferenceProperty(String refName, SettableBeanProperty forward, SettableBeanProperty backward, Annotations contextAnnotations, boolean isContainer) {
            super(forward.getName(), forward.getType(), forward._valueTypeDeserializer, contextAnnotations);
            this._referenceName = refName;
            this._managedProperty = forward;
            this._backProperty = backward;
            this._isContainer = isContainer;
        }

        protected ManagedReferenceProperty(ManagedReferenceProperty src, JsonDeserializer<Object> deser) {
            super(src, deser);
            this._referenceName = src._referenceName;
            this._isContainer = src._isContainer;
            this._managedProperty = src._managedProperty;
            this._backProperty = src._backProperty;
        }

        @Override
        public ManagedReferenceProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new ManagedReferenceProperty(this, deser);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
            return this._managedProperty.getAnnotation(acls);
        }

        @Override
        public AnnotatedMember getMember() {
            return this._managedProperty.getMember();
        }

        @Override
        public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object instance) throws IOException, JsonProcessingException {
            this.set(instance, this._managedProperty.deserialize(jp, ctxt));
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public final void set(Object instance, Object value) throws IOException {
            this._managedProperty.set(instance, value);
            if (value == null) return;
            if (this._isContainer) {
                if (value instanceof Object[]) {
                    for (Object ob : (Object[])value) {
                        if (ob == null) continue;
                        this._backProperty.set(ob, instance);
                    }
                    return;
                } else if (value instanceof Collection) {
                    for (Object ob : (Collection)value) {
                        if (ob == null) continue;
                        this._backProperty.set(ob, instance);
                    }
                    return;
                } else {
                    if (!(value instanceof Map)) throw new IllegalStateException("Unsupported container type (" + value.getClass().getName() + ") when resolving reference '" + this._referenceName + "'");
                    for (Object ob : ((Map)value).values()) {
                        if (ob == null) continue;
                        this._backProperty.set(ob, instance);
                    }
                }
                return;
            } else {
                this._backProperty.set(value, instance);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class FieldProperty
    extends SettableBeanProperty {
        protected final AnnotatedField _annotated;
        protected final Field _field;

        public FieldProperty(String name, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedField field) {
            super(name, type, typeDeser, contextAnnotations);
            this._annotated = field;
            this._field = field.getAnnotated();
        }

        protected FieldProperty(FieldProperty src, JsonDeserializer<Object> deser) {
            super(src, deser);
            this._annotated = src._annotated;
            this._field = src._field;
        }

        @Override
        public FieldProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new FieldProperty(this, deser);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
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
        public final void set(Object instance, Object value) throws IOException {
            try {
                this._field.set(instance, value);
            }
            catch (Exception e) {
                this._throwAsIOE(e, value);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class SetterlessProperty
    extends SettableBeanProperty {
        protected final AnnotatedMethod _annotated;
        protected final Method _getter;

        public SetterlessProperty(String name, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method) {
            super(name, type, typeDeser, contextAnnotations);
            this._annotated = method;
            this._getter = method.getAnnotated();
        }

        protected SetterlessProperty(SetterlessProperty src, JsonDeserializer<Object> deser) {
            super(src, deser);
            this._annotated = src._annotated;
            this._getter = src._getter;
        }

        @Override
        public SetterlessProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new SetterlessProperty(this, deser);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
            return this._annotated.getAnnotation(acls);
        }

        @Override
        public AnnotatedMember getMember() {
            return this._annotated;
        }

        @Override
        public final void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object instance) throws IOException, JsonProcessingException {
            Object toModify;
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NULL) {
                return;
            }
            try {
                toModify = this._getter.invoke(instance, new Object[0]);
            }
            catch (Exception e) {
                this._throwAsIOE(e);
                return;
            }
            if (toModify == null) {
                throw new JsonMappingException("Problem deserializing 'setterless' property '" + this.getName() + "': get method returned null");
            }
            this._valueDeserializer.deserialize(jp, ctxt, toModify);
        }

        @Override
        public final void set(Object instance, Object value) throws IOException {
            throw new UnsupportedOperationException("Should never call 'set' on setterless property");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class MethodProperty
    extends SettableBeanProperty {
        protected final AnnotatedMethod _annotated;
        protected final Method _setter;

        public MethodProperty(String name, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method) {
            super(name, type, typeDeser, contextAnnotations);
            this._annotated = method;
            this._setter = method.getAnnotated();
        }

        protected MethodProperty(MethodProperty src, JsonDeserializer<Object> deser) {
            super(src, deser);
            this._annotated = src._annotated;
            this._setter = src._setter;
        }

        @Override
        public MethodProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new MethodProperty(this, deser);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
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
        public final void set(Object instance, Object value) throws IOException {
            try {
                this._setter.invoke(instance, value);
            }
            catch (Exception e) {
                this._throwAsIOE(e, value);
            }
        }
    }
}


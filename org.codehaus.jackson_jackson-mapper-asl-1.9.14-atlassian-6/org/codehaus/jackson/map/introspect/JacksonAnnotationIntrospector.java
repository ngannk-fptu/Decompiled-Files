/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JacksonAnnotation
 *  org.codehaus.jackson.annotate.JsonAnyGetter
 *  org.codehaus.jackson.annotate.JsonAnySetter
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonBackReference
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonGetter
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonIgnoreType
 *  org.codehaus.jackson.annotate.JsonManagedReference
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 *  org.codehaus.jackson.annotate.JsonRawValue
 *  org.codehaus.jackson.annotate.JsonSetter
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.annotate.JsonTypeInfo$None
 *  org.codehaus.jackson.annotate.JsonTypeName
 *  org.codehaus.jackson.annotate.JsonUnwrapped
 *  org.codehaus.jackson.annotate.JsonValue
 *  org.codehaus.jackson.annotate.JsonWriteNullProperties
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JacksonAnnotation;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonIgnoreType;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.annotate.JsonUnwrapped;
import org.codehaus.jackson.annotate.JsonValue;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.annotate.JacksonInject;
import org.codehaus.jackson.map.annotate.JsonCachable;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonTypeIdResolver;
import org.codehaus.jackson.map.annotate.JsonTypeResolver;
import org.codehaus.jackson.map.annotate.JsonValueInstantiator;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jackson.map.annotate.NoClass;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.map.ser.std.RawSerializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JacksonAnnotationIntrospector
extends AnnotationIntrospector {
    @Override
    public boolean isHandled(Annotation ann) {
        Class<? extends Annotation> acls = ann.annotationType();
        return acls.getAnnotation(JacksonAnnotation.class) != null;
    }

    @Override
    public String findEnumValue(Enum<?> value) {
        return value.name();
    }

    @Override
    public Boolean findCachability(AnnotatedClass ac) {
        JsonCachable ann = ac.getAnnotation(JsonCachable.class);
        if (ann == null) {
            return null;
        }
        return ann.value() ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String findRootName(AnnotatedClass ac) {
        JsonRootName ann = ac.getAnnotation(JsonRootName.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public String[] findPropertiesToIgnore(AnnotatedClass ac) {
        JsonIgnoreProperties ignore = ac.getAnnotation(JsonIgnoreProperties.class);
        return ignore == null ? null : ignore.value();
    }

    @Override
    public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
        JsonIgnoreProperties ignore = ac.getAnnotation(JsonIgnoreProperties.class);
        return ignore == null ? null : Boolean.valueOf(ignore.ignoreUnknown());
    }

    @Override
    public Boolean isIgnorableType(AnnotatedClass ac) {
        JsonIgnoreType ignore = ac.getAnnotation(JsonIgnoreType.class);
        return ignore == null ? null : Boolean.valueOf(ignore.value());
    }

    @Override
    public Object findFilterId(AnnotatedClass ac) {
        String id;
        JsonFilter ann = ac.getAnnotation(JsonFilter.class);
        if (ann != null && (id = ann.value()).length() > 0) {
            return id;
        }
        return null;
    }

    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        JsonAutoDetect ann = ac.getAnnotation(JsonAutoDetect.class);
        return ann == null ? checker : checker.with(ann);
    }

    @Override
    public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
        JsonManagedReference ref1 = member.getAnnotation(JsonManagedReference.class);
        if (ref1 != null) {
            return AnnotationIntrospector.ReferenceProperty.managed(ref1.value());
        }
        JsonBackReference ref2 = member.getAnnotation(JsonBackReference.class);
        if (ref2 != null) {
            return AnnotationIntrospector.ReferenceProperty.back(ref2.value());
        }
        return null;
    }

    @Override
    public Boolean shouldUnwrapProperty(AnnotatedMember member) {
        JsonUnwrapped ann = member.getAnnotation(JsonUnwrapped.class);
        return ann != null && ann.enabled() ? Boolean.TRUE : null;
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return this._isIgnorable(m);
    }

    @Override
    public Object findInjectableValueId(AnnotatedMember m) {
        JacksonInject ann = m.getAnnotation(JacksonInject.class);
        if (ann == null) {
            return null;
        }
        String id = ann.value();
        if (id.length() == 0) {
            if (!(m instanceof AnnotatedMethod)) {
                return m.getRawType().getName();
            }
            AnnotatedMethod am = (AnnotatedMethod)m;
            if (am.getParameterCount() == 0) {
                return m.getRawType().getName();
            }
            return am.getParameterClass(0).getName();
        }
        return id;
    }

    @Override
    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        return this._findTypeResolver(config, ac, baseType);
    }

    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        if (baseType.isContainerType()) {
            return null;
        }
        return this._findTypeResolver(config, am, baseType);
    }

    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
        if (!containerType.isContainerType()) {
            throw new IllegalArgumentException("Must call method with a container type (got " + containerType + ")");
        }
        return this._findTypeResolver(config, am, containerType);
    }

    @Override
    public List<NamedType> findSubtypes(Annotated a) {
        JsonSubTypes t = a.getAnnotation(JsonSubTypes.class);
        if (t == null) {
            return null;
        }
        JsonSubTypes.Type[] types = t.value();
        ArrayList<NamedType> result = new ArrayList<NamedType>(types.length);
        for (JsonSubTypes.Type type : types) {
            result.add(new NamedType(type.value(), type.name()));
        }
        return result;
    }

    @Override
    public String findTypeName(AnnotatedClass ac) {
        JsonTypeName tn = ac.getAnnotation(JsonTypeName.class);
        return tn == null ? null : tn.value();
    }

    @Override
    public boolean isIgnorableMethod(AnnotatedMethod m) {
        return this._isIgnorable(m);
    }

    @Override
    public boolean isIgnorableConstructor(AnnotatedConstructor c) {
        return this._isIgnorable(c);
    }

    @Override
    public boolean isIgnorableField(AnnotatedField f) {
        return this._isIgnorable(f);
    }

    @Override
    public Object findSerializer(Annotated a) {
        Class<? extends JsonSerializer<?>> serClass;
        JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null && (serClass = ann.using()) != JsonSerializer.None.class) {
            return serClass;
        }
        JsonRawValue annRaw = a.getAnnotation(JsonRawValue.class);
        if (annRaw != null && annRaw.value()) {
            Class<?> cls = a.getRawType();
            return new RawSerializer(cls);
        }
        return null;
    }

    @Override
    public Class<? extends JsonSerializer<?>> findKeySerializer(Annotated a) {
        Class<? extends JsonSerializer<?>> serClass;
        JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null && (serClass = ann.keyUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override
    public Class<? extends JsonSerializer<?>> findContentSerializer(Annotated a) {
        Class<? extends JsonSerializer<?>> serClass;
        JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null && (serClass = ann.contentUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override
    public JsonSerialize.Inclusion findSerializationInclusion(Annotated a, JsonSerialize.Inclusion defValue) {
        JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null) {
            return ann.include();
        }
        JsonWriteNullProperties oldAnn = a.getAnnotation(JsonWriteNullProperties.class);
        if (oldAnn != null) {
            boolean writeNulls = oldAnn.value();
            return writeNulls ? JsonSerialize.Inclusion.ALWAYS : JsonSerialize.Inclusion.NON_NULL;
        }
        return defValue;
    }

    @Override
    public Class<?> findSerializationType(Annotated am) {
        Class<?> cls;
        JsonSerialize ann = am.getAnnotation(JsonSerialize.class);
        if (ann != null && (cls = ann.as()) != NoClass.class) {
            return cls;
        }
        return null;
    }

    @Override
    public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
        Class<?> cls;
        JsonSerialize ann = am.getAnnotation(JsonSerialize.class);
        if (ann != null && (cls = ann.keyAs()) != NoClass.class) {
            return cls;
        }
        return null;
    }

    @Override
    public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
        Class<?> cls;
        JsonSerialize ann = am.getAnnotation(JsonSerialize.class);
        if (ann != null && (cls = ann.contentAs()) != NoClass.class) {
            return cls;
        }
        return null;
    }

    @Override
    public JsonSerialize.Typing findSerializationTyping(Annotated a) {
        JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        return ann == null ? null : ann.typing();
    }

    @Override
    public Class<?>[] findSerializationViews(Annotated a) {
        JsonView ann = a.getAnnotation(JsonView.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        JsonPropertyOrder order = ac.getAnnotation(JsonPropertyOrder.class);
        return order == null ? null : order.value();
    }

    @Override
    public Boolean findSerializationSortAlphabetically(AnnotatedClass ac) {
        JsonPropertyOrder order = ac.getAnnotation(JsonPropertyOrder.class);
        return order == null ? null : Boolean.valueOf(order.alphabetic());
    }

    @Override
    public String findGettablePropertyName(AnnotatedMethod am) {
        JsonProperty pann = am.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        JsonGetter ann = am.getAnnotation(JsonGetter.class);
        if (ann != null) {
            return ann.value();
        }
        if (am.hasAnnotation(JsonSerialize.class) || am.hasAnnotation(JsonView.class)) {
            return "";
        }
        return null;
    }

    @Override
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        JsonValue ann = am.getAnnotation(JsonValue.class);
        return ann != null && ann.value();
    }

    @Override
    public String findSerializablePropertyName(AnnotatedField af) {
        JsonProperty pann = af.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        if (af.hasAnnotation(JsonSerialize.class) || af.hasAnnotation(JsonView.class)) {
            return "";
        }
        return null;
    }

    @Override
    public Class<? extends JsonDeserializer<?>> findDeserializer(Annotated a) {
        Class<? extends JsonDeserializer<?>> deserClass;
        JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        if (ann != null && (deserClass = ann.using()) != JsonDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override
    public Class<? extends KeyDeserializer> findKeyDeserializer(Annotated a) {
        Class<? extends KeyDeserializer> deserClass;
        JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        if (ann != null && (deserClass = ann.keyUsing()) != KeyDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override
    public Class<? extends JsonDeserializer<?>> findContentDeserializer(Annotated a) {
        Class<? extends JsonDeserializer<?>> deserClass;
        JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        if (ann != null && (deserClass = ann.contentUsing()) != JsonDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override
    public Class<?> findDeserializationType(Annotated am, JavaType baseType, String propName) {
        Class<?> cls;
        JsonDeserialize ann = am.getAnnotation(JsonDeserialize.class);
        if (ann != null && (cls = ann.as()) != NoClass.class) {
            return cls;
        }
        return null;
    }

    @Override
    public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType, String propName) {
        Class<?> cls;
        JsonDeserialize ann = am.getAnnotation(JsonDeserialize.class);
        if (ann != null && (cls = ann.keyAs()) != NoClass.class) {
            return cls;
        }
        return null;
    }

    @Override
    public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType, String propName) {
        Class<?> cls;
        JsonDeserialize ann = am.getAnnotation(JsonDeserialize.class);
        if (ann != null && (cls = ann.contentAs()) != NoClass.class) {
            return cls;
        }
        return null;
    }

    @Override
    public Object findValueInstantiator(AnnotatedClass ac) {
        JsonValueInstantiator ann = ac.getAnnotation(JsonValueInstantiator.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public String findSettablePropertyName(AnnotatedMethod am) {
        JsonProperty pann = am.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        JsonSetter ann = am.getAnnotation(JsonSetter.class);
        if (ann != null) {
            return ann.value();
        }
        if (am.hasAnnotation(JsonDeserialize.class) || am.hasAnnotation(JsonView.class) || am.hasAnnotation(JsonBackReference.class) || am.hasAnnotation(JsonManagedReference.class)) {
            return "";
        }
        return null;
    }

    @Override
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return am.hasAnnotation(JsonAnySetter.class);
    }

    @Override
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return am.hasAnnotation(JsonAnyGetter.class);
    }

    @Override
    public boolean hasCreatorAnnotation(Annotated a) {
        return a.hasAnnotation(JsonCreator.class);
    }

    @Override
    public String findDeserializablePropertyName(AnnotatedField af) {
        JsonProperty pann = af.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        if (af.hasAnnotation(JsonDeserialize.class) || af.hasAnnotation(JsonView.class) || af.hasAnnotation(JsonBackReference.class) || af.hasAnnotation(JsonManagedReference.class)) {
            return "";
        }
        return null;
    }

    @Override
    public String findPropertyNameForParam(AnnotatedParameter param) {
        JsonProperty pann;
        if (param != null && (pann = param.getAnnotation(JsonProperty.class)) != null) {
            return pann.value();
        }
        return null;
    }

    protected boolean _isIgnorable(Annotated a) {
        JsonIgnore ann = a.getAnnotation(JsonIgnore.class);
        return ann != null && ann.value();
    }

    protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
        TypeIdResolver idRes;
        StdTypeResolverBuilder b;
        JsonTypeInfo info = ann.getAnnotation(JsonTypeInfo.class);
        JsonTypeResolver resAnn = ann.getAnnotation(JsonTypeResolver.class);
        if (resAnn != null) {
            if (info == null) {
                return null;
            }
            b = config.typeResolverBuilderInstance(ann, resAnn.value());
        } else {
            if (info == null) {
                return null;
            }
            if (info.use() == JsonTypeInfo.Id.NONE) {
                return this._constructNoTypeResolverBuilder();
            }
            b = this._constructStdTypeResolverBuilder();
        }
        JsonTypeIdResolver idResInfo = ann.getAnnotation(JsonTypeIdResolver.class);
        TypeIdResolver typeIdResolver = idRes = idResInfo == null ? null : config.typeIdResolverInstance(ann, idResInfo.value());
        if (idRes != null) {
            idRes.init(baseType);
        }
        b = b.init(info.use(), idRes);
        JsonTypeInfo.As inclusion = info.include();
        if (inclusion == JsonTypeInfo.As.EXTERNAL_PROPERTY && ann instanceof AnnotatedClass) {
            inclusion = JsonTypeInfo.As.PROPERTY;
        }
        b = b.inclusion(inclusion);
        b = b.typeProperty(info.property());
        Class defaultImpl = info.defaultImpl();
        if (defaultImpl != JsonTypeInfo.None.class) {
            b = b.defaultImpl(defaultImpl);
        }
        return b;
    }

    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new StdTypeResolverBuilder();
    }

    protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
        return StdTypeResolverBuilder.noTypeInfoBuilder();
    }
}


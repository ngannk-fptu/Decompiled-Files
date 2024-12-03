/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JacksonAnnotationsInside
 *  com.fasterxml.jackson.annotation.JacksonInject
 *  com.fasterxml.jackson.annotation.JacksonInject$Value
 *  com.fasterxml.jackson.annotation.JsonAlias
 *  com.fasterxml.jackson.annotation.JsonAnyGetter
 *  com.fasterxml.jackson.annotation.JsonAnySetter
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonBackReference
 *  com.fasterxml.jackson.annotation.JsonClassDescription
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonCreator$Mode
 *  com.fasterxml.jackson.annotation.JsonEnumDefaultValue
 *  com.fasterxml.jackson.annotation.JsonFilter
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Value
 *  com.fasterxml.jackson.annotation.JsonGetter
 *  com.fasterxml.jackson.annotation.JsonIdentityInfo
 *  com.fasterxml.jackson.annotation.JsonIdentityReference
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties$Value
 *  com.fasterxml.jackson.annotation.JsonIgnoreType
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonInclude$Value
 *  com.fasterxml.jackson.annotation.JsonIncludeProperties
 *  com.fasterxml.jackson.annotation.JsonIncludeProperties$Value
 *  com.fasterxml.jackson.annotation.JsonKey
 *  com.fasterxml.jackson.annotation.JsonManagedReference
 *  com.fasterxml.jackson.annotation.JsonMerge
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.annotation.JsonProperty$Access
 *  com.fasterxml.jackson.annotation.JsonPropertyDescription
 *  com.fasterxml.jackson.annotation.JsonPropertyOrder
 *  com.fasterxml.jackson.annotation.JsonRawValue
 *  com.fasterxml.jackson.annotation.JsonRootName
 *  com.fasterxml.jackson.annotation.JsonSetter
 *  com.fasterxml.jackson.annotation.JsonSetter$Value
 *  com.fasterxml.jackson.annotation.JsonSubTypes
 *  com.fasterxml.jackson.annotation.JsonSubTypes$Type
 *  com.fasterxml.jackson.annotation.JsonTypeId
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$As
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$None
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Value
 *  com.fasterxml.jackson.annotation.JsonTypeName
 *  com.fasterxml.jackson.annotation.JsonUnwrapped
 *  com.fasterxml.jackson.annotation.JsonValue
 *  com.fasterxml.jackson.annotation.JsonView
 *  com.fasterxml.jackson.annotation.ObjectIdGenerators$None
 *  com.fasterxml.jackson.core.Version
 */
package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.ext.Java7Support;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VirtualAnnotatedMember;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.AttributePropertyWriter;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.ExceptionUtil;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class JacksonAnnotationIntrospector
extends AnnotationIntrospector
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_SER = new Class[]{JsonSerialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class};
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_DESER = new Class[]{JsonDeserialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class, JsonMerge.class};
    private static final Java7Support _java7Helper;
    protected transient LRUMap<String, Boolean> _annotationsInside = new LRUMap(48, 48);
    protected boolean _cfgConstructorPropertiesImpliesCreator = true;

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    protected Object readResolve() {
        if (this._annotationsInside == null) {
            this._annotationsInside = new LRUMap(48, 48);
        }
        return this;
    }

    public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(boolean b) {
        this._cfgConstructorPropertiesImpliesCreator = b;
        return this;
    }

    @Override
    public boolean isAnnotationBundle(Annotation ann) {
        Class<? extends Annotation> type = ann.annotationType();
        String typeName = type.getName();
        Boolean b = this._annotationsInside.get(typeName);
        if (b == null) {
            b = type.getAnnotation(JacksonAnnotationsInside.class) != null;
            this._annotationsInside.putIfAbsent(typeName, b);
        }
        return b;
    }

    @Override
    @Deprecated
    public String findEnumValue(Enum<?> value) {
        try {
            String n;
            JsonProperty prop;
            Field f = value.getDeclaringClass().getField(value.name());
            if (f != null && (prop = f.getAnnotation(JsonProperty.class)) != null && (n = prop.value()) != null && !n.isEmpty()) {
                return n;
            }
        }
        catch (SecurityException securityException) {
        }
        catch (NoSuchFieldException noSuchFieldException) {
            // empty catch block
        }
        return value.name();
    }

    @Override
    @Deprecated
    public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        HashMap<String, String> expl = null;
        for (Field f : enumType.getDeclaredFields()) {
            String n;
            JsonProperty prop;
            if (!f.isEnumConstant() || (prop = f.getAnnotation(JsonProperty.class)) == null || (n = prop.value()).isEmpty()) continue;
            if (expl == null) {
                expl = new HashMap<String, String>();
            }
            expl.put(f.getName(), n);
        }
        if (expl != null) {
            int end = enumValues.length;
            for (int i = 0; i < end; ++i) {
                String defName = enumValues[i].name();
                String explValue = (String)expl.get(defName);
                if (explValue == null) continue;
                names[i] = explValue;
            }
        }
        return names;
    }

    @Override
    public String[] findEnumValues(MapperConfig<?> config, AnnotatedClass annotatedClass, Enum<?>[] enumValues, String[] names) {
        LinkedHashMap<String, String> enumToPropertyMap = new LinkedHashMap<String, String>();
        for (AnnotatedField field : annotatedClass.fields()) {
            String propValue;
            JsonProperty property = field.getAnnotation(JsonProperty.class);
            if (property == null || (propValue = property.value()) == null || propValue.isEmpty()) continue;
            enumToPropertyMap.put(field.getName(), propValue);
        }
        int end = enumValues.length;
        for (int i = 0; i < end; ++i) {
            String defName = enumValues[i].name();
            String explValue = (String)enumToPropertyMap.get(defName);
            if (explValue == null) continue;
            names[i] = explValue;
        }
        return names;
    }

    @Override
    @Deprecated
    public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliasList) {
        for (Field f : enumType.getDeclaredFields()) {
            String[] aliases;
            JsonAlias aliasAnnotation;
            if (!f.isEnumConstant() || (aliasAnnotation = f.getAnnotation(JsonAlias.class)) == null || (aliases = aliasAnnotation.value()).length == 0) continue;
            String name = f.getName();
            int end = enumValues.length;
            for (int i = 0; i < end; ++i) {
                if (!name.equals(enumValues[i].name())) continue;
                aliasList[i] = aliases;
            }
        }
    }

    @Override
    public void findEnumAliases(MapperConfig<?> config, AnnotatedClass annotatedClass, Enum<?>[] enumValues, String[][] aliasList) {
        HashMap<String, String[]> enumToAliasMap = new HashMap<String, String[]>();
        for (AnnotatedField field : annotatedClass.fields()) {
            JsonAlias alias = field.getAnnotation(JsonAlias.class);
            if (alias == null) continue;
            enumToAliasMap.putIfAbsent(field.getName(), alias.value());
        }
        for (Enum<?> enumValue : enumValues) {
            aliasList[i] = enumToAliasMap.getOrDefault(enumValue.name(), new String[0]);
        }
    }

    @Override
    @Deprecated
    public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
        return ClassUtil.findFirstAnnotatedEnumValue(enumCls, JsonEnumDefaultValue.class);
    }

    @Override
    public Enum<?> findDefaultEnumValue(AnnotatedClass annotatedClass, Enum<?>[] enumValues) {
        for (Annotated annotated : annotatedClass.fields()) {
            JsonEnumDefaultValue found;
            if (!annotated.getType().isEnumType() || (found = this._findAnnotation(annotated, JsonEnumDefaultValue.class)) == null) continue;
            for (Enum<?> enumValue : enumValues) {
                if (!enumValue.name().equals(annotated.getName())) continue;
                return enumValue;
            }
        }
        return null;
    }

    @Override
    public PropertyName findRootName(AnnotatedClass ac) {
        JsonRootName ann = this._findAnnotation(ac, JsonRootName.class);
        if (ann == null) {
            return null;
        }
        String ns = ann.namespace();
        if (ns != null && ns.isEmpty()) {
            ns = null;
        }
        return PropertyName.construct(ann.value(), ns);
    }

    @Override
    public Boolean isIgnorableType(AnnotatedClass ac) {
        JsonIgnoreType ignore = this._findAnnotation(ac, JsonIgnoreType.class);
        return ignore == null ? null : Boolean.valueOf(ignore.value());
    }

    @Override
    public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated a) {
        JsonIgnoreProperties v = this._findAnnotation(a, JsonIgnoreProperties.class);
        if (v == null) {
            return JsonIgnoreProperties.Value.empty();
        }
        return JsonIgnoreProperties.Value.from((JsonIgnoreProperties)v);
    }

    @Override
    @Deprecated
    public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated ac) {
        return this.findPropertyIgnoralByName(null, ac);
    }

    @Override
    public JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated a) {
        JsonIncludeProperties v = this._findAnnotation(a, JsonIncludeProperties.class);
        if (v == null) {
            return JsonIncludeProperties.Value.all();
        }
        return JsonIncludeProperties.Value.from((JsonIncludeProperties)v);
    }

    @Override
    public Object findFilterId(Annotated a) {
        String id;
        JsonFilter ann = this._findAnnotation(a, JsonFilter.class);
        if (ann != null && !(id = ann.value()).isEmpty()) {
            return id;
        }
        return null;
    }

    @Override
    public Object findNamingStrategy(AnnotatedClass ac) {
        JsonNaming ann = this._findAnnotation(ac, JsonNaming.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public Object findEnumNamingStrategy(MapperConfig<?> config, AnnotatedClass ac) {
        EnumNaming ann = this._findAnnotation(ac, EnumNaming.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public String findClassDescription(AnnotatedClass ac) {
        JsonClassDescription ann = this._findAnnotation(ac, JsonClassDescription.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        JsonAutoDetect ann = this._findAnnotation(ac, JsonAutoDetect.class);
        return ann == null ? checker : checker.with(ann);
    }

    @Override
    public String findImplicitPropertyName(AnnotatedMember m) {
        PropertyName n = this._findConstructorName(m);
        return n == null ? null : n.getSimpleName();
    }

    @Override
    public List<PropertyName> findPropertyAliases(Annotated m) {
        JsonAlias ann = this._findAnnotation(m, JsonAlias.class);
        if (ann == null) {
            return null;
        }
        String[] strs = ann.value();
        int len = strs.length;
        if (len == 0) {
            return Collections.emptyList();
        }
        ArrayList<PropertyName> result = new ArrayList<PropertyName>(len);
        for (int i = 0; i < len; ++i) {
            result.add(PropertyName.construct(strs[i]));
        }
        return result;
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return this._isIgnorable(m);
    }

    @Override
    public Boolean hasRequiredMarker(AnnotatedMember m) {
        JsonProperty ann = this._findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return ann.required();
        }
        return null;
    }

    @Override
    public JsonProperty.Access findPropertyAccess(Annotated m) {
        JsonProperty ann = this._findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return ann.access();
        }
        return null;
    }

    @Override
    public String findPropertyDescription(Annotated ann) {
        JsonPropertyDescription desc = this._findAnnotation(ann, JsonPropertyDescription.class);
        return desc == null ? null : desc.value();
    }

    @Override
    public Integer findPropertyIndex(Annotated ann) {
        int ix;
        JsonProperty prop = this._findAnnotation(ann, JsonProperty.class);
        if (prop != null && (ix = prop.index()) != -1) {
            return ix;
        }
        return null;
    }

    @Override
    public String findPropertyDefaultValue(Annotated ann) {
        JsonProperty prop = this._findAnnotation(ann, JsonProperty.class);
        if (prop == null) {
            return null;
        }
        String str = prop.defaultValue();
        return str.isEmpty() ? null : str;
    }

    @Override
    public JsonFormat.Value findFormat(Annotated ann) {
        JsonFormat f = this._findAnnotation(ann, JsonFormat.class);
        return f == null ? null : JsonFormat.Value.from((JsonFormat)f);
    }

    @Override
    public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
        JsonManagedReference ref1 = this._findAnnotation(member, JsonManagedReference.class);
        if (ref1 != null) {
            return AnnotationIntrospector.ReferenceProperty.managed(ref1.value());
        }
        JsonBackReference ref2 = this._findAnnotation(member, JsonBackReference.class);
        if (ref2 != null) {
            return AnnotationIntrospector.ReferenceProperty.back(ref2.value());
        }
        return null;
    }

    @Override
    public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
        JsonUnwrapped ann = this._findAnnotation(member, JsonUnwrapped.class);
        if (ann == null || !ann.enabled()) {
            return null;
        }
        String prefix = ann.prefix();
        String suffix = ann.suffix();
        return NameTransformer.simpleTransformer(prefix, suffix);
    }

    @Override
    public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
        JacksonInject ann = this._findAnnotation(m, JacksonInject.class);
        if (ann == null) {
            return null;
        }
        JacksonInject.Value v = JacksonInject.Value.from((JacksonInject)ann);
        if (!v.hasId()) {
            AnnotatedMethod am;
            String id = !(m instanceof AnnotatedMethod) ? m.getRawType().getName() : ((am = (AnnotatedMethod)m).getParameterCount() == 0 ? m.getRawType().getName() : am.getRawParameterType(0).getName());
            v = v.withId((Object)id);
        }
        return v;
    }

    @Override
    @Deprecated
    public Object findInjectableValueId(AnnotatedMember m) {
        JacksonInject.Value v = this.findInjectableValue(m);
        return v == null ? null : v.getId();
    }

    @Override
    public Class<?>[] findViews(Annotated a) {
        JsonView ann = this._findAnnotation(a, JsonView.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
        Class<?> cls1 = setter1.getRawParameterType(0);
        Class<?> cls2 = setter2.getRawParameterType(0);
        if (cls1.isPrimitive()) {
            if (!cls2.isPrimitive()) {
                return setter1;
            }
            return null;
        }
        if (cls2.isPrimitive()) {
            return setter2;
        }
        if (cls1 == String.class) {
            if (cls2 != String.class) {
                return setter1;
            }
        } else if (cls2 == String.class) {
            return setter2;
        }
        return null;
    }

    @Override
    public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
        return null;
    }

    @Override
    public JsonTypeInfo.Value findPolymorphicTypeInfo(MapperConfig<?> config, Annotated ann) {
        JsonTypeInfo t = this._findAnnotation(ann, JsonTypeInfo.class);
        return t == null ? null : JsonTypeInfo.Value.from((JsonTypeInfo)t);
    }

    @Override
    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        return this._findTypeResolver(config, ac, baseType);
    }

    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        if (baseType.isContainerType() || baseType.isReferenceType()) {
            return null;
        }
        return this._findTypeResolver(config, am, baseType);
    }

    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
        if (containerType.getContentType() == null) {
            throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
        }
        return this._findTypeResolver(config, am, containerType);
    }

    @Override
    public List<NamedType> findSubtypes(Annotated a) {
        JsonSubTypes t = this._findAnnotation(a, JsonSubTypes.class);
        if (t == null) {
            return null;
        }
        JsonSubTypes.Type[] types = t.value();
        if (t.failOnRepeatedNames()) {
            return this.findSubtypesCheckRepeatedNames(a.getName(), types);
        }
        ArrayList<NamedType> result = new ArrayList<NamedType>(types.length);
        for (JsonSubTypes.Type type : types) {
            result.add(new NamedType(type.value(), type.name()));
            for (String name : type.names()) {
                result.add(new NamedType(type.value(), name));
            }
        }
        return result;
    }

    private List<NamedType> findSubtypesCheckRepeatedNames(String annotatedTypeName, JsonSubTypes.Type[] types) {
        ArrayList<NamedType> result = new ArrayList<NamedType>(types.length);
        HashSet<String> seenNames = new HashSet<String>();
        for (JsonSubTypes.Type type : types) {
            String typeName = type.name();
            if (!typeName.isEmpty() && seenNames.contains(typeName)) {
                throw new IllegalArgumentException("Annotated type [" + annotatedTypeName + "] got repeated subtype name [" + typeName + "]");
            }
            seenNames.add(typeName);
            result.add(new NamedType(type.value(), typeName));
            for (String altName : type.names()) {
                if (!altName.isEmpty() && seenNames.contains(altName)) {
                    throw new IllegalArgumentException("Annotated type [" + annotatedTypeName + "] got repeated subtype name [" + altName + "]");
                }
                seenNames.add(altName);
                result.add(new NamedType(type.value(), altName));
            }
        }
        return result;
    }

    @Override
    public String findTypeName(AnnotatedClass ac) {
        JsonTypeName tn = this._findAnnotation(ac, JsonTypeName.class);
        return tn == null ? null : tn.value();
    }

    @Override
    public Boolean isTypeId(AnnotatedMember member) {
        return this._hasAnnotation(member, JsonTypeId.class);
    }

    @Override
    public ObjectIdInfo findObjectIdInfo(Annotated ann) {
        JsonIdentityInfo info = this._findAnnotation(ann, JsonIdentityInfo.class);
        if (info == null || info.generator() == ObjectIdGenerators.None.class) {
            return null;
        }
        PropertyName name = PropertyName.construct(info.property());
        return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
    }

    @Override
    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        JsonIdentityReference ref = this._findAnnotation(ann, JsonIdentityReference.class);
        if (ref == null) {
            return objectIdInfo;
        }
        if (objectIdInfo == null) {
            objectIdInfo = ObjectIdInfo.empty();
        }
        return objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
    }

    @Override
    public Object findSerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.using()) != JsonSerializer.None.class) {
            return serClass;
        }
        JsonRawValue annRaw = this._findAnnotation(a, JsonRawValue.class);
        if (annRaw != null && annRaw.value()) {
            Class<?> cls = a.getRawType();
            return new RawSerializer(cls);
        }
        return null;
    }

    @Override
    public Object findKeySerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.keyUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override
    public Object findContentSerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.contentUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override
    public Object findNullSerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.nullsUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override
    public JsonInclude.Value findPropertyInclusion(Annotated a) {
        JsonInclude.Value value;
        JsonInclude inc = this._findAnnotation(a, JsonInclude.class);
        JsonInclude.Value value2 = value = inc == null ? JsonInclude.Value.empty() : JsonInclude.Value.from((JsonInclude)inc);
        if (value.getValueInclusion() == JsonInclude.Include.USE_DEFAULTS) {
            value = this._refinePropertyInclusion(a, value);
        }
        return value;
    }

    private JsonInclude.Value _refinePropertyInclusion(Annotated a, JsonInclude.Value value) {
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            switch (ann.include()) {
                case ALWAYS: {
                    return value.withValueInclusion(JsonInclude.Include.ALWAYS);
                }
                case NON_NULL: {
                    return value.withValueInclusion(JsonInclude.Include.NON_NULL);
                }
                case NON_DEFAULT: {
                    return value.withValueInclusion(JsonInclude.Include.NON_DEFAULT);
                }
                case NON_EMPTY: {
                    return value.withValueInclusion(JsonInclude.Include.NON_EMPTY);
                }
            }
        }
        return value;
    }

    @Override
    public JsonSerialize.Typing findSerializationTyping(Annotated a) {
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        return ann == null ? null : ann.typing();
    }

    @Override
    public Object findSerializationConverter(Annotated a) {
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        return ann == null ? null : this._classIfExplicit(ann.converter(), Converter.None.class);
    }

    @Override
    public Object findSerializationContentConverter(AnnotatedMember a) {
        JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        return ann == null ? null : this._classIfExplicit(ann.contentConverter(), Converter.None.class);
    }

    @Override
    public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType contentType;
        Class<?> currRaw;
        JsonSerialize jsonSer;
        TypeFactory tf;
        JavaType type;
        block26: {
            Class<?> serClass;
            type = baseType;
            tf = config.getTypeFactory();
            jsonSer = this._findAnnotation(a, JsonSerialize.class);
            Class<?> clazz = serClass = jsonSer == null ? null : this._classIfExplicit(jsonSer.as());
            if (serClass != null) {
                if (type.hasRawClass(serClass)) {
                    type = type.withStaticTyping();
                } else {
                    Class<?> currRaw2 = type.getRawClass();
                    try {
                        if (serClass.isAssignableFrom(currRaw2)) {
                            type = tf.constructGeneralizedType(type, serClass);
                            break block26;
                        }
                        if (currRaw2.isAssignableFrom(serClass)) {
                            type = tf.constructSpecializedType(type, serClass);
                            break block26;
                        }
                        if (this._primitiveAndWrapper(currRaw2, serClass)) {
                            type = type.withStaticTyping();
                            break block26;
                        }
                        throw this._databindException(String.format("Cannot refine serialization type %s into %s; types not related", type, serClass.getName()));
                    }
                    catch (IllegalArgumentException iae) {
                        throw this._databindException(iae, String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", type, serClass.getName(), a.getName(), iae.getMessage()));
                    }
                }
            }
        }
        if (type.isMapLikeType()) {
            Class<?> keyClass;
            JavaType keyType = type.getKeyType();
            Class<?> clazz = keyClass = jsonSer == null ? null : this._classIfExplicit(jsonSer.keyAs());
            if (keyClass != null) {
                block27: {
                    if (keyType.hasRawClass(keyClass)) {
                        keyType = keyType.withStaticTyping();
                    } else {
                        currRaw = keyType.getRawClass();
                        try {
                            if (keyClass.isAssignableFrom(currRaw)) {
                                keyType = tf.constructGeneralizedType(keyType, keyClass);
                                break block27;
                            }
                            if (currRaw.isAssignableFrom(keyClass)) {
                                keyType = tf.constructSpecializedType(keyType, keyClass);
                                break block27;
                            }
                            if (this._primitiveAndWrapper(currRaw, keyClass)) {
                                keyType = keyType.withStaticTyping();
                                break block27;
                            }
                            throw this._databindException(String.format("Cannot refine serialization key type %s into %s; types not related", keyType, keyClass.getName()));
                        }
                        catch (IllegalArgumentException iae) {
                            throw this._databindException(iae, String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s", type, keyClass.getName(), a.getName(), iae.getMessage()));
                        }
                    }
                }
                type = ((MapLikeType)type).withKeyType(keyType);
            }
        }
        if ((contentType = type.getContentType()) != null) {
            Class<?> contentClass;
            Class<?> clazz = contentClass = jsonSer == null ? null : this._classIfExplicit(jsonSer.contentAs());
            if (contentClass != null) {
                block28: {
                    if (contentType.hasRawClass(contentClass)) {
                        contentType = contentType.withStaticTyping();
                    } else {
                        currRaw = contentType.getRawClass();
                        try {
                            if (contentClass.isAssignableFrom(currRaw)) {
                                contentType = tf.constructGeneralizedType(contentType, contentClass);
                                break block28;
                            }
                            if (currRaw.isAssignableFrom(contentClass)) {
                                contentType = tf.constructSpecializedType(contentType, contentClass);
                                break block28;
                            }
                            if (this._primitiveAndWrapper(currRaw, contentClass)) {
                                contentType = contentType.withStaticTyping();
                                break block28;
                            }
                            throw this._databindException(String.format("Cannot refine serialization content type %s into %s; types not related", contentType, contentClass.getName()));
                        }
                        catch (IllegalArgumentException iae) {
                            throw this._databindException(iae, String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s", type, contentClass.getName(), a.getName(), iae.getMessage()));
                        }
                    }
                }
                type = type.withContentType(contentType);
            }
        }
        return type;
    }

    @Override
    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        JsonPropertyOrder order = this._findAnnotation(ac, JsonPropertyOrder.class);
        return order == null ? null : order.value();
    }

    @Override
    public Boolean findSerializationSortAlphabetically(Annotated ann) {
        return this._findSortAlpha(ann);
    }

    private final Boolean _findSortAlpha(Annotated ann) {
        JsonPropertyOrder order = this._findAnnotation(ann, JsonPropertyOrder.class);
        if (order != null && order.alphabetic()) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override
    public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
        JsonAppend ann = this._findAnnotation(ac, JsonAppend.class);
        if (ann == null) {
            return;
        }
        boolean prepend = ann.prepend();
        JavaType propType = null;
        JsonAppend.Attr[] attrs = ann.attrs();
        int len = attrs.length;
        for (int i = 0; i < len; ++i) {
            if (propType == null) {
                propType = config.constructType(Object.class);
            }
            BeanPropertyWriter bpw = this._constructVirtualProperty(attrs[i], config, ac, propType);
            if (prepend) {
                properties.add(i, bpw);
                continue;
            }
            properties.add(bpw);
        }
        JsonAppend.Prop[] props = ann.props();
        int len2 = props.length;
        for (int i = 0; i < len2; ++i) {
            BeanPropertyWriter bpw = this._constructVirtualProperty(props[i], config, ac);
            if (prepend) {
                properties.add(i, bpw);
                continue;
            }
            properties.add(bpw);
        }
    }

    protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Attr attr, MapperConfig<?> config, AnnotatedClass ac, JavaType type) {
        PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        String attrName = attr.value();
        PropertyName propName = this._propertyName(attr.propName(), attr.propNamespace());
        if (!propName.hasSimpleName()) {
            propName = PropertyName.construct(attrName);
        }
        VirtualAnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), attrName, type);
        SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, (AnnotatedMember)member, propName, metadata, attr.include());
        return AttributePropertyWriter.construct(attrName, propDef, ac.getAnnotations(), type);
    }

    protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Prop prop, MapperConfig<?> config, AnnotatedClass ac) {
        VirtualBeanPropertyWriter bpw;
        PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        PropertyName propName = this._propertyName(prop.name(), prop.namespace());
        JavaType type = config.constructType(prop.type());
        VirtualAnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), propName.getSimpleName(), type);
        SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, (AnnotatedMember)member, propName, metadata, prop.include());
        Class<? extends VirtualBeanPropertyWriter> implClass = prop.value();
        HandlerInstantiator hi = config.getHandlerInstantiator();
        VirtualBeanPropertyWriter virtualBeanPropertyWriter = bpw = hi == null ? null : hi.virtualPropertyWriterInstance(config, implClass);
        if (bpw == null) {
            bpw = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return bpw.withConfig(config, ac, propDef, type);
    }

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        JsonProperty pann;
        boolean useDefault = false;
        JsonGetter jg = this._findAnnotation(a, JsonGetter.class);
        if (jg != null) {
            String s = jg.value();
            if (!s.isEmpty()) {
                return PropertyName.construct(s);
            }
            useDefault = true;
        }
        if ((pann = this._findAnnotation(a, JsonProperty.class)) != null) {
            String ns = pann.namespace();
            if (ns != null && ns.isEmpty()) {
                ns = null;
            }
            return PropertyName.construct(pann.value(), ns);
        }
        if (useDefault || this._hasOneOf(a, ANNOTATIONS_TO_INFER_SER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    @Override
    public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
        JsonKey ann = this._findAnnotation(a, JsonKey.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    @Override
    public Boolean hasAsValue(Annotated a) {
        JsonValue ann = this._findAnnotation(a, JsonValue.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    @Override
    public Boolean hasAnyGetter(Annotated a) {
        JsonAnyGetter ann = this._findAnnotation(a, JsonAnyGetter.class);
        if (ann == null) {
            return null;
        }
        return ann.enabled();
    }

    @Override
    @Deprecated
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return this._hasAnnotation(am, JsonAnyGetter.class);
    }

    @Override
    @Deprecated
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        JsonValue ann = this._findAnnotation(am, JsonValue.class);
        return ann != null && ann.value();
    }

    @Override
    public Object findDeserializer(Annotated a) {
        Class<? extends JsonDeserializer> deserClass;
        JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        if (ann != null && (deserClass = ann.using()) != JsonDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override
    public Object findKeyDeserializer(Annotated a) {
        Class<? extends KeyDeserializer> deserClass;
        JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        if (ann != null && (deserClass = ann.keyUsing()) != KeyDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override
    public Object findContentDeserializer(Annotated a) {
        Class<? extends JsonDeserializer> deserClass;
        JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        if (ann != null && (deserClass = ann.contentUsing()) != JsonDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override
    public Object findDeserializationConverter(Annotated a) {
        JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        return ann == null ? null : this._classIfExplicit(ann.converter(), Converter.None.class);
    }

    @Override
    public Object findDeserializationContentConverter(AnnotatedMember a) {
        JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        return ann == null ? null : this._classIfExplicit(ann.contentConverter(), Converter.None.class);
    }

    @Override
    public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType contentType;
        Class<?> valueClass;
        JavaType type = baseType;
        TypeFactory tf = config.getTypeFactory();
        JsonDeserialize jsonDeser = this._findAnnotation(a, JsonDeserialize.class);
        Class<?> clazz = valueClass = jsonDeser == null ? null : this._classIfExplicit(jsonDeser.as());
        if (valueClass != null && !type.hasRawClass(valueClass) && !this._primitiveAndWrapper(type, valueClass)) {
            try {
                type = tf.constructSpecializedType(type, valueClass);
            }
            catch (IllegalArgumentException iae) {
                throw this._databindException(iae, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", type, valueClass.getName(), a.getName(), iae.getMessage()));
            }
        }
        if (type.isMapLikeType()) {
            Class<?> keyClass;
            JavaType keyType = type.getKeyType();
            Class<?> clazz2 = keyClass = jsonDeser == null ? null : this._classIfExplicit(jsonDeser.keyAs());
            if (keyClass != null && !this._primitiveAndWrapper(keyType, keyClass)) {
                try {
                    keyType = tf.constructSpecializedType(keyType, keyClass);
                    type = ((MapLikeType)type).withKeyType(keyType);
                }
                catch (IllegalArgumentException iae) {
                    throw this._databindException(iae, String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s", type, keyClass.getName(), a.getName(), iae.getMessage()));
                }
            }
        }
        if ((contentType = type.getContentType()) != null) {
            Class<?> contentClass;
            Class<?> clazz3 = contentClass = jsonDeser == null ? null : this._classIfExplicit(jsonDeser.contentAs());
            if (contentClass != null && !this._primitiveAndWrapper(contentType, contentClass)) {
                try {
                    contentType = tf.constructSpecializedType(contentType, contentClass);
                    type = type.withContentType(contentType);
                }
                catch (IllegalArgumentException iae) {
                    throw this._databindException(iae, String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s", type, contentClass.getName(), a.getName(), iae.getMessage()));
                }
            }
        }
        return type;
    }

    @Override
    public Object findValueInstantiator(AnnotatedClass ac) {
        JsonValueInstantiator ann = this._findAnnotation(ac, JsonValueInstantiator.class);
        return ann == null ? null : ann.value();
    }

    @Override
    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        JsonDeserialize ann = this._findAnnotation(ac, JsonDeserialize.class);
        return ann == null ? null : this._classIfExplicit(ann.builder());
    }

    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        JsonPOJOBuilder ann = this._findAnnotation(ac, JsonPOJOBuilder.class);
        return ann == null ? null : new JsonPOJOBuilder.Value(ann);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        JsonProperty pann;
        boolean useDefault = false;
        JsonSetter js = this._findAnnotation(a, JsonSetter.class);
        if (js != null) {
            String s = js.value();
            if (s.isEmpty()) {
                useDefault = true;
            } else {
                return PropertyName.construct(s);
            }
        }
        if ((pann = this._findAnnotation(a, JsonProperty.class)) != null) {
            String ns = pann.namespace();
            if (ns != null && ns.isEmpty()) {
                ns = null;
            }
            return PropertyName.construct(pann.value(), ns);
        }
        if (useDefault || this._hasOneOf(a, ANNOTATIONS_TO_INFER_DESER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    @Override
    public Boolean hasAnySetter(Annotated a) {
        JsonAnySetter ann = this._findAnnotation(a, JsonAnySetter.class);
        return ann == null ? null : Boolean.valueOf(ann.enabled());
    }

    @Override
    public JsonSetter.Value findSetterInfo(Annotated a) {
        return JsonSetter.Value.from((JsonSetter)this._findAnnotation(a, JsonSetter.class));
    }

    @Override
    public Boolean findMergeInfo(Annotated a) {
        JsonMerge ann = this._findAnnotation(a, JsonMerge.class);
        return ann == null ? null : ann.value().asBoolean();
    }

    @Override
    @Deprecated
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return this._hasAnnotation(am, JsonAnySetter.class);
    }

    @Override
    @Deprecated
    public boolean hasCreatorAnnotation(Annotated a) {
        Boolean b;
        JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return ann.mode() != JsonCreator.Mode.DISABLED;
        }
        if (this._cfgConstructorPropertiesImpliesCreator && a instanceof AnnotatedConstructor && _java7Helper != null && (b = _java7Helper.hasCreatorAnnotation(a)) != null) {
            return b;
        }
        return false;
    }

    @Override
    @Deprecated
    public JsonCreator.Mode findCreatorBinding(Annotated a) {
        JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
        return ann == null ? null : ann.mode();
    }

    @Override
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        Boolean b;
        JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return ann.mode();
        }
        if (this._cfgConstructorPropertiesImpliesCreator && config.isEnabled(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES) && a instanceof AnnotatedConstructor && _java7Helper != null && (b = _java7Helper.hasCreatorAnnotation(a)) != null && b.booleanValue()) {
            return JsonCreator.Mode.PROPERTIES;
        }
        return null;
    }

    protected boolean _isIgnorable(Annotated a) {
        Boolean b;
        JsonIgnore ann = this._findAnnotation(a, JsonIgnore.class);
        if (ann != null) {
            return ann.value();
        }
        if (_java7Helper != null && (b = _java7Helper.findTransient(a)) != null) {
            return b;
        }
        return false;
    }

    protected Class<?> _classIfExplicit(Class<?> cls) {
        if (cls == null || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }

    protected Class<?> _classIfExplicit(Class<?> cls, Class<?> implicit) {
        return (cls = this._classIfExplicit(cls)) == null || cls == implicit ? null : cls;
    }

    protected PropertyName _propertyName(String localName, String namespace) {
        if (localName.isEmpty()) {
            return PropertyName.USE_DEFAULT;
        }
        if (namespace == null || namespace.isEmpty()) {
            return PropertyName.construct(localName);
        }
        return PropertyName.construct(localName, namespace);
    }

    protected PropertyName _findConstructorName(Annotated a) {
        PropertyName name;
        AnnotatedParameter p;
        AnnotatedWithParams ctor;
        if (a instanceof AnnotatedParameter && (ctor = (p = (AnnotatedParameter)a).getOwner()) != null && _java7Helper != null && (name = _java7Helper.findConstructorName(p)) != null) {
            return name;
        }
        return null;
    }

    protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
        Class defaultImpl;
        JsonTypeInfo.As inclusion;
        TypeIdResolver idRes;
        TypeResolverBuilder<?> b;
        JsonTypeInfo.Value typeInfo = this.findPolymorphicTypeInfo(config, ann);
        JsonTypeResolver resAnn = this._findAnnotation(ann, JsonTypeResolver.class);
        if (resAnn != null) {
            if (typeInfo == null) {
                return null;
            }
            b = config.typeResolverBuilderInstance(ann, resAnn.value());
        } else {
            if (typeInfo == null) {
                return null;
            }
            if (typeInfo.getIdType() == JsonTypeInfo.Id.NONE) {
                return this._constructNoTypeResolverBuilder();
            }
            b = this._constructStdTypeResolverBuilder(config, typeInfo, baseType);
        }
        JsonTypeIdResolver idResInfo = this._findAnnotation(ann, JsonTypeIdResolver.class);
        TypeIdResolver typeIdResolver = idRes = idResInfo == null ? null : config.typeIdResolverInstance(ann, idResInfo.value());
        if (idRes != null) {
            idRes.init(baseType);
        }
        if ((inclusion = typeInfo.getInclusionType()) == JsonTypeInfo.As.EXTERNAL_PROPERTY && ann instanceof AnnotatedClass) {
            typeInfo = typeInfo.withInclusionType(JsonTypeInfo.As.PROPERTY);
        }
        if ((defaultImpl = typeInfo.getDefaultImpl()) != null && defaultImpl != JsonTypeInfo.None.class && !defaultImpl.isAnnotation()) {
            typeInfo = typeInfo.withDefaultImpl(defaultImpl);
        }
        b = b.init(typeInfo, idRes);
        return b;
    }

    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new StdTypeResolverBuilder();
    }

    protected TypeResolverBuilder<?> _constructStdTypeResolverBuilder(MapperConfig<?> config, JsonTypeInfo.Value typeInfo, JavaType baseType) {
        return new StdTypeResolverBuilder(typeInfo);
    }

    protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
        return StdTypeResolverBuilder.noTypeInfoBuilder();
    }

    private boolean _primitiveAndWrapper(Class<?> baseType, Class<?> refinement) {
        if (baseType.isPrimitive()) {
            return baseType == ClassUtil.primitiveType(refinement);
        }
        if (refinement.isPrimitive()) {
            return refinement == ClassUtil.primitiveType(baseType);
        }
        return false;
    }

    private boolean _primitiveAndWrapper(JavaType baseType, Class<?> refinement) {
        if (baseType.isPrimitive()) {
            return baseType.hasRawClass(ClassUtil.primitiveType(refinement));
        }
        if (refinement.isPrimitive()) {
            return refinement == ClassUtil.primitiveType(baseType.getRawClass());
        }
        return false;
    }

    private JsonMappingException _databindException(String msg) {
        return new JsonMappingException(null, msg);
    }

    private JsonMappingException _databindException(Throwable t, String msg) {
        return new JsonMappingException(null, msg, t);
    }

    static {
        Java7Support x = null;
        try {
            x = Java7Support.instance();
        }
        catch (Throwable t) {
            ExceptionUtil.rethrowIfFatal(t);
        }
        _java7Helper = x;
    }
}


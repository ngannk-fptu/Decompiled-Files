/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.util.Collection;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.jsontype.impl.AsArrayTypeDeserializer;
import org.codehaus.jackson.map.jsontype.impl.AsArrayTypeSerializer;
import org.codehaus.jackson.map.jsontype.impl.AsExternalTypeDeserializer;
import org.codehaus.jackson.map.jsontype.impl.AsExternalTypeSerializer;
import org.codehaus.jackson.map.jsontype.impl.AsPropertyTypeDeserializer;
import org.codehaus.jackson.map.jsontype.impl.AsPropertyTypeSerializer;
import org.codehaus.jackson.map.jsontype.impl.AsWrapperTypeDeserializer;
import org.codehaus.jackson.map.jsontype.impl.AsWrapperTypeSerializer;
import org.codehaus.jackson.map.jsontype.impl.ClassNameIdResolver;
import org.codehaus.jackson.map.jsontype.impl.MinimalClassNameIdResolver;
import org.codehaus.jackson.map.jsontype.impl.TypeNameIdResolver;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdTypeResolverBuilder
implements TypeResolverBuilder<StdTypeResolverBuilder> {
    protected JsonTypeInfo.Id _idType;
    protected JsonTypeInfo.As _includeAs;
    protected String _typeProperty;
    protected Class<?> _defaultImpl;
    protected TypeIdResolver _customIdResolver;

    @Override
    public Class<?> getDefaultImpl() {
        return this._defaultImpl;
    }

    public static StdTypeResolverBuilder noTypeInfoBuilder() {
        return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, null);
    }

    @Override
    public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes) {
        if (idType == null) {
            throw new IllegalArgumentException("idType can not be null");
        }
        this._idType = idType;
        this._customIdResolver = idRes;
        this._typeProperty = idType.getDefaultPropertyName();
        return this;
    }

    @Override
    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes, BeanProperty property) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        TypeIdResolver idRes = this.idResolver(config, baseType, subtypes, true, false);
        switch (this._includeAs) {
            case WRAPPER_ARRAY: {
                return new AsArrayTypeSerializer(idRes, property);
            }
            case PROPERTY: {
                return new AsPropertyTypeSerializer(idRes, property, this._typeProperty);
            }
            case WRAPPER_OBJECT: {
                return new AsWrapperTypeSerializer(idRes, property);
            }
            case EXTERNAL_PROPERTY: {
                return new AsExternalTypeSerializer(idRes, property, this._typeProperty);
            }
        }
        throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
    }

    @Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes, BeanProperty property) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        TypeIdResolver idRes = this.idResolver(config, baseType, subtypes, false, true);
        switch (this._includeAs) {
            case WRAPPER_ARRAY: {
                return new AsArrayTypeDeserializer(baseType, idRes, property, this._defaultImpl);
            }
            case PROPERTY: {
                return new AsPropertyTypeDeserializer(baseType, idRes, property, this._defaultImpl, this._typeProperty);
            }
            case WRAPPER_OBJECT: {
                return new AsWrapperTypeDeserializer(baseType, idRes, property, this._defaultImpl);
            }
            case EXTERNAL_PROPERTY: {
                return new AsExternalTypeDeserializer(baseType, idRes, property, this._defaultImpl, this._typeProperty);
            }
        }
        throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
    }

    @Override
    public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs) {
        if (includeAs == null) {
            throw new IllegalArgumentException("includeAs can not be null");
        }
        this._includeAs = includeAs;
        return this;
    }

    @Override
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.length() == 0) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = typeIdPropName;
        return this;
    }

    @Override
    public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
        this._defaultImpl = defaultImpl;
        return this;
    }

    public String getTypeProperty() {
        return this._typeProperty;
    }

    protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        if (this._idType == null) {
            throw new IllegalStateException("Can not build, 'init()' not yet called");
        }
        switch (this._idType) {
            case CLASS: {
                return new ClassNameIdResolver(baseType, config.getTypeFactory());
            }
            case MINIMAL_CLASS: {
                return new MinimalClassNameIdResolver(baseType, config.getTypeFactory());
            }
            case NAME: {
                return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            }
            case NONE: {
                return null;
            }
        }
        throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
    }
}


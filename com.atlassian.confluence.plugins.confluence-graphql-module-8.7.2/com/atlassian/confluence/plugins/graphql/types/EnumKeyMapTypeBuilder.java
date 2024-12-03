/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.ApiEnum
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.model.reference.EnrichableMap
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.types.DynamicType
 *  com.atlassian.graphql.types.DynamicTypeBuilder
 *  com.atlassian.graphql.utils.GraphQLSchemaMetadata
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Iterables
 *  graphql.schema.GraphQLOutputType
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.ApiEnum;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.types.DynamicType;
import com.atlassian.graphql.types.DynamicTypeBuilder;
import com.atlassian.graphql.utils.GraphQLSchemaMetadata;
import com.atlassian.graphql.utils.ReflectionUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumKeyMapTypeBuilder
extends DynamicTypeBuilder {
    private static final String BUILT_IN_ENUM_FIELD_NAME = "BUILT_IN";

    public EnumKeyMapTypeBuilder(GraphQLTypeBuilder typeBuilder, GraphQLExtensions extensions) {
        super(typeBuilder, extensions);
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        return (parameterizedType.getRawType() == Map.class || parameterizedType.getRawType() == EnrichableMap.class) && ApiEnum.class.isAssignableFrom(this.getKeyClass(parameterizedType));
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        Class enumType = this.getKeyClass(parameterizedType);
        Type valueType = this.getValueType(parameterizedType);
        HashMap<String, Type> dynamicFieldTypes = new HashMap<String, Type>();
        for (String enumName : EnumKeyMapTypeBuilder.getEnumNames(enumType)) {
            dynamicFieldTypes.put(enumName, valueType);
        }
        DynamicType dynamicEnumType = new DynamicType(typeName, dynamicFieldTypes);
        GraphQLType enumGraphqlType = super.buildType(typeName, (Type)dynamicEnumType, element, context);
        GraphQLSchemaMetadata.markAllFieldsExpandable((GraphQLOutputType)((GraphQLOutputType)enumGraphqlType), (Map)context.getTypes(), (boolean)false);
        return enumGraphqlType;
    }

    public Function<Object, Object> getValueTransformer(Type type, AnnotatedElement element) {
        return obj -> {
            if (obj == null) {
                return null;
            }
            HashMap stringKeyedMap = new HashMap();
            Map map = (Map)obj;
            if (!(map instanceof Collapsed)) {
                for (Map.Entry entry : map.entrySet()) {
                    stringKeyedMap.put(EnumKeyMapTypeBuilder.keyToString(entry.getKey()), entry.getValue());
                }
            }
            return stringKeyedMap;
        };
    }

    private static String keyToString(Object key) {
        return key instanceof ApiEnum ? ((ApiEnum)key).serialise() : (String)key;
    }

    private Class getKeyClass(ParameterizedType type) {
        return ReflectionUtils.getClazz((Type)type.getActualTypeArguments()[0]);
    }

    private Type getValueType(ParameterizedType type) {
        return type.getActualTypeArguments()[1];
    }

    private static Iterable<String> getEnumNames(Class enumType) {
        Object values;
        try {
            Field field = enumType.getDeclaredField(BUILT_IN_ENUM_FIELD_NAME);
            field.setAccessible(true);
            values = field.get(null);
        }
        catch (ReflectiveOperationException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
        return EnumKeyMapTypeBuilder.getEnumNames(values);
    }

    private static Iterable<String> getEnumNames(Object enumValues) {
        if (enumValues instanceof Object[]) {
            return Arrays.stream((Object[])enumValues).map(x -> ((ApiEnum)x).serialise()).collect(Collectors.toList());
        }
        return Iterables.transform((Iterable)((Iterable)enumValues), ApiEnum::serialise);
    }
}


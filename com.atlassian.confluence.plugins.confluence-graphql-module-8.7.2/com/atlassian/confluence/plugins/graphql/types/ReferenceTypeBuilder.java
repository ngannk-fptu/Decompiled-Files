/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  graphql.schema.GraphQLType
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.NoClass
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.utils.ReflectionUtils;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.NoClass;

public class ReferenceTypeBuilder
implements GraphQLTypeBuilder {
    private final GraphQLTypeBuilder typeBuilder;

    public ReferenceTypeBuilder(GraphQLTypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
    }

    public String getTypeName(Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        Type referencedType = ReferenceTypeBuilder.getReferencedType(type, element);
        return this.typeBuilder.getTypeName(referencedType, element, context);
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        return Reference.class.isAssignableFrom(ReflectionUtils.getClazz((Type)type));
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        Type referencedType = ReferenceTypeBuilder.getReferencedType(type, element);
        return (GraphQLType)context.updateFieldType(referencedType, () -> this.typeBuilder.buildType(typeName, referencedType, element, context));
    }

    public Function<Object, Object> getValueTransformer(Type type, AnnotatedElement element) {
        Type referencedType = ReferenceTypeBuilder.getReferencedType(type, element);
        Function transformer = this.typeBuilder.getValueTransformer(referencedType, null);
        return obj -> {
            if (obj != null && !(obj instanceof Reference)) {
                if (referencedType == Boolean.class) {
                    obj = ((Map)obj).get("value");
                }
                return transformer != null ? transformer.apply(obj) : obj;
            }
            Reference reference = (Reference)obj;
            Map<String, Object> referencedObject = reference != null ? (reference.isExpanded() ? reference.get() : ReferenceTypeBuilder.unwrapReferenceId(reference)) : null;
            return transformer != null ? transformer.apply(referencedObject) : referencedObject;
        };
    }

    private static Type getReferencedType(Type type, AnnotatedElement element) {
        JsonDeserialize jsonDeserialize = element != null ? element.getAnnotation(JsonDeserialize.class) : null;
        return jsonDeserialize != null && jsonDeserialize.contentAs() != NoClass.class ? jsonDeserialize.contentAs() : ReflectionUtils.unwrapWildcardType((Type)((ParameterizedType)type).getActualTypeArguments()[0]);
    }

    private static Map<String, Object> unwrapReferenceId(Reference reference) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Iterator iterator = reference.getIdProperties().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry obj;
            Map.Entry entry = obj = iterator.next();
            result.put(entry.getKey().toString(), entry.getValue());
        }
        return result;
    }
}


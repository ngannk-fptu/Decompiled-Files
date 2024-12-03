/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.json.JsonTypeMappingException
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.map.jsontype.TypeIdResolver
 *  org.codehaus.jackson.map.type.SimpleType
 *  org.codehaus.jackson.type.JavaType
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.migration.agent.json;

import com.atlassian.migration.agent.Resources;
import com.atlassian.migration.agent.json.JsonParentType;
import com.atlassian.migration.agent.json.JsonType;
import com.atlassian.migration.json.JsonTypeMappingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.type.JavaType;
import org.springframework.util.ClassUtils;

public class JsonParentTypeIdResolver
implements TypeIdResolver {
    private static final String JSON_TYPES_FILE_PATH = "/META-INF/json-types";
    private static final TypesMapping MAPPING = new TypesMapping();
    private JavaType baseType;

    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    public String idFromValue(Object value) {
        return MAPPING.getId(value.getClass());
    }

    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return MAPPING.getId(suggestedType);
    }

    public JavaType typeFromId(String id) {
        return SimpleType.construct(MAPPING.getType(this.baseType.getRawClass(), id));
    }

    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    private static class TypesMapping {
        final Map<Class<?>, Map<String, Class<?>>> subTypesMap = new HashMap();
        final Map<Class<?>, String> idsMap = new HashMap();

        TypesMapping() {
            try {
                List<String> lines = Resources.readLinesFromResources(JsonParentTypeIdResolver.JSON_TYPES_FILE_PATH);
                for (String line : lines) {
                    Class clazz = ClassUtils.resolveClassName((String)line, (ClassLoader)ClassUtils.getDefaultClassLoader());
                    JsonType jsonType = clazz.getAnnotation(JsonType.class);
                    String typeId = jsonType.value();
                    this.idsMap.put(clazz, typeId);
                    HashSet parentTypes = new HashSet();
                    parentTypes.add(clazz.getSuperclass());
                    parentTypes.addAll(Arrays.asList(clazz.getInterfaces()));
                    parentTypes.forEach(parentType -> {
                        Map idToType;
                        if (parentType.isAnnotationPresent(JsonParentType.class) && (idToType = this.subTypesMap.computeIfAbsent((Class<?>)parentType, type -> new HashMap())).put(typeId, clazz) != null) {
                            throw new JsonTypeMappingException("Duplicated json type id " + typeId + " for parent type " + parentType);
                        }
                    });
                }
            }
            catch (IOException e) {
                throw new JsonTypeMappingException("Failed to read json-types files", (Throwable)e);
            }
        }

        String getId(Class<?> clazz) {
            return this.idsMap.get(clazz);
        }

        Class<?> getType(Class<?> baseType, String id) {
            return this.subTypesMap.get(baseType).get(id);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.map.jsontype.TypeIdResolver
 *  org.codehaus.jackson.map.type.TypeFactory
 *  org.codehaus.jackson.map.util.ClassUtil
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.crowd.directory.rest.resolver;

import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import com.atlassian.crowd.directory.rest.entity.membership.GraphMembershipGroup;
import com.atlassian.crowd.directory.rest.entity.membership.GraphMembershipUser;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

public class DirectoryObjectTypeIdResolver
implements TypeIdResolver {
    public static final String USER_ODATA_TYPE = "#microsoft.graph.user";
    public static final String GROUP_ODATA_TYPE = "#microsoft.graph.group";
    private static final Map<Class<?>, String> CLASSES_TO_IDS = ImmutableMap.of(GraphMembershipUser.class, (Object)"#microsoft.graph.user", GraphMembershipGroup.class, (Object)"#microsoft.graph.group");
    private Map<String, JavaType> idsToTypes;
    private JavaType baseType;
    private JavaType fallbackType;

    public void init(JavaType baseType) {
        this.baseType = baseType;
        this.idsToTypes = new ImmutableMap.Builder().put((Object)USER_ODATA_TYPE, (Object)this.constructJavaType(GraphMembershipUser.class)).put((Object)GROUP_ODATA_TYPE, (Object)this.constructJavaType(GraphMembershipGroup.class)).build();
        this.fallbackType = this.constructJavaType(DirectoryObject.class);
    }

    public String idFromValue(Object value) {
        return this.idFromValueAndType(value, value.getClass());
    }

    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return CLASSES_TO_IDS.get(suggestedType);
    }

    public JavaType typeFromId(String id) {
        return this.idsToTypes.getOrDefault(id, this.fallbackType);
    }

    private JavaType constructJavaType(Class<?> clazz) {
        try {
            Class classToCreate = ClassUtil.findClass((String)clazz.getName());
            return TypeFactory.defaultInstance().constructSpecializedType(this.baseType, classToCreate);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find class " + clazz);
        }
    }

    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}


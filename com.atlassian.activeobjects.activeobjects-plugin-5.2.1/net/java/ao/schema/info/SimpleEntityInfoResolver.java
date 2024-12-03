/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.java.ao.schema.info;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import net.java.ao.AnnotationDelegate;
import net.java.ao.Common;
import net.java.ao.Generator;
import net.java.ao.Polymorphic;
import net.java.ao.RawEntity;
import net.java.ao.Transient;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Default;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.EntityInfoResolver;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.schema.info.ImmutableEntityInfo;
import net.java.ao.schema.info.ImmutableFieldInfo;
import net.java.ao.types.TypeManager;

public class SimpleEntityInfoResolver
implements EntityInfoResolver {
    private final NameConverters nameConverters;
    private final TypeManager typeManager;

    public SimpleEntityInfoResolver(NameConverters nameConverters, TypeManager typeManager) {
        this.nameConverters = nameConverters;
        this.typeManager = typeManager;
    }

    @Override
    public <T extends RawEntity<K>, K> EntityInfo<T, K> resolve(Class<T> type) {
        FieldNameConverter fieldNameConverter = this.nameConverters.getFieldNameConverter();
        HashMap accessorByFieldName = Maps.newHashMap();
        HashMap mutatorByFieldName = Maps.newHashMap();
        for (Method method : type.getMethods()) {
            String name;
            if (method.isAnnotationPresent(Ignore.class)) continue;
            if (Common.isAccessor(method)) {
                name = fieldNameConverter.getName(method);
                if (name == null) continue;
                if (accessorByFieldName.containsKey(name)) {
                    throw new IllegalArgumentException(String.format("Invalid Entity definition. Both %s and %s generate the same table name (%s)", method, accessorByFieldName.get(name), name));
                }
                accessorByFieldName.put(name, method);
                continue;
            }
            if (!Common.isMutator(method) || (name = fieldNameConverter.getName(method)) == null) continue;
            if (mutatorByFieldName.containsKey(name)) {
                throw new IllegalArgumentException(String.format("Invalid Entity definition. Both %s and %s generate the same table name (%s)", method, mutatorByFieldName.get(name), name));
            }
            mutatorByFieldName.put(fieldNameConverter.getName(method), method);
        }
        HashSet fields = Sets.newHashSet();
        for (String fieldName : Sets.union(accessorByFieldName.keySet(), mutatorByFieldName.keySet())) {
            fields.add(this.createFieldInfo(fieldName, (Method)accessorByFieldName.get(fieldName), (Method)mutatorByFieldName.get(fieldName)));
        }
        return new ImmutableEntityInfo(type, this.nameConverters.getTableNameConverter().getName(type), fields);
    }

    private FieldInfo createFieldInfo(String fieldName, Method accessor, Method mutator) {
        Class<?> fieldType = Common.getAttributeTypeFromMethod((Method)MoreObjects.firstNonNull((Object)accessor, (Object)mutator));
        AnnotationDelegate annotations = this.getAnnotations(accessor, mutator);
        Generator generatorAnnotation = annotations.getAnnotation(Generator.class);
        return new ImmutableFieldInfo(fieldName, fieldType.isAnnotationPresent(Polymorphic.class) ? this.nameConverters.getFieldNameConverter().getPolyTypeName((Method)MoreObjects.firstNonNull((Object)accessor, (Object)mutator)) : null, accessor, mutator, fieldType, this.typeManager.getType(fieldType), annotations.isAnnotationPresent(PrimaryKey.class), !annotations.isAnnotationPresent(NotNull.class), annotations.isAnnotationPresent(Transient.class), annotations.isAnnotationPresent(AutoIncrement.class), annotations.isAnnotationPresent(Default.class), generatorAnnotation != null ? generatorAnnotation.value() : null);
    }

    private AnnotationDelegate getAnnotations(Method accessor, Method mutator) {
        return accessor != null ? new AnnotationDelegate(accessor, mutator) : new AnnotationDelegate(mutator, accessor);
    }
}


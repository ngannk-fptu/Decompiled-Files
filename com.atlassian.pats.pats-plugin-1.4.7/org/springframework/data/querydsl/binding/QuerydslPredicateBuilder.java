/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.Property
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.data.querydsl.binding.PathInformation;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslDefaultBinding;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class QuerydslPredicateBuilder {
    private final ConversionService conversionService;
    private final MultiValueBinding<Path<? extends Object>, Object> defaultBinding;
    private final Map<PathInformation, Path<?>> paths;
    private final EntityPathResolver resolver;

    public QuerydslPredicateBuilder(ConversionService conversionService, EntityPathResolver resolver) {
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        this.defaultBinding = new QuerydslDefaultBinding();
        this.conversionService = conversionService;
        this.paths = new ConcurrentHashMap();
        this.resolver = resolver;
    }

    public Predicate getPredicate(TypeInformation<?> type, MultiValueMap<String, String> values, QuerydslBindings bindings) {
        Assert.notNull((Object)bindings, (String)"Context must not be null!");
        BooleanBuilder builder = new BooleanBuilder();
        if (values.isEmpty()) {
            return QuerydslPredicateBuilder.getPredicate(builder);
        }
        for (Map.Entry entry : values.entrySet()) {
            PathInformation propertyPath;
            String path;
            if (QuerydslPredicateBuilder.isSingleElementCollectionWithoutText((List)entry.getValue()) || !bindings.isPathAvailable(path = (String)entry.getKey(), type) || (propertyPath = bindings.getPropertyPath(path, type)) == null) continue;
            Collection<Object> value = this.convertToPropertyPathSpecificType((List)entry.getValue(), propertyPath);
            Optional<Predicate> predicate = this.invokeBinding(propertyPath, bindings, value);
            predicate.ifPresent(builder::and);
        }
        return QuerydslPredicateBuilder.getPredicate(builder);
    }

    public static boolean isEmpty(Predicate predicate) {
        return new BooleanBuilder().equals(predicate);
    }

    private Optional<Predicate> invokeBinding(PathInformation dotPath, QuerydslBindings bindings, Collection<Object> values) {
        Path<?> path = this.getPath(dotPath, bindings);
        return bindings.getBindingForPath(dotPath).orElse(this.defaultBinding).bind(path, values);
    }

    private Path<?> getPath(PathInformation path, QuerydslBindings bindings) {
        Optional<Path<?>> resolvedPath = bindings.getExistingPath(path);
        return resolvedPath.orElseGet(() -> this.paths.computeIfAbsent(path, it -> it.reifyPath(this.resolver)));
    }

    private Collection<Object> convertToPropertyPathSpecificType(List<String> source, PathInformation path) {
        Class<?> targetType = path.getLeafType();
        if (source.isEmpty() || QuerydslPredicateBuilder.isSingleElementCollectionWithoutText(source)) {
            return Collections.emptyList();
        }
        ArrayList<Object> target = new ArrayList<Object>(source.size());
        for (String value : source) {
            target.add(this.conversionService.canConvert(String.class, targetType) ? this.conversionService.convert((Object)value, TypeDescriptor.forObject((Object)value), QuerydslPredicateBuilder.getTargetTypeDescriptor(path)) : value);
        }
        return target;
    }

    private static TypeDescriptor getTargetTypeDescriptor(PathInformation path) {
        TypeDescriptor result;
        PropertyDescriptor descriptor = path.getLeafPropertyDescriptor();
        Class<?> owningType = path.getLeafParentType();
        String leafProperty = path.getLeafProperty();
        TypeDescriptor typeDescriptor = result = descriptor == null ? TypeDescriptor.nested((Field)ReflectionUtils.findRequiredField(owningType, leafProperty), (int)0) : TypeDescriptor.nested((Property)new Property(owningType, descriptor.getReadMethod(), descriptor.getWriteMethod(), leafProperty), (int)0);
        if (result == null) {
            throw new IllegalStateException(String.format("Could not obtain TypeDesciptor for PathInformation %s!", path));
        }
        return result;
    }

    private static boolean isSingleElementCollectionWithoutText(List<String> source) {
        return source.size() == 1 && !StringUtils.hasLength((String)source.get(0));
    }

    private static Predicate getPredicate(BooleanBuilder builder) {
        Predicate predicate = builder.getValue();
        return predicate == null ? new BooleanBuilder() : predicate;
    }
}


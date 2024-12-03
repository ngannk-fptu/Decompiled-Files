/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ClassUtils
 */
package com.atlassian.analytics.client.extractor.nested;

import com.atlassian.analytics.client.extractor.FieldExtractor;
import com.atlassian.analytics.client.extractor.nested.fields.AnnotatedInvocation;
import com.atlassian.analytics.client.extractor.nested.fields.DefaultSingleObjectExtractor;
import com.atlassian.analytics.client.logger.EventAnonymizer;
import io.atlassian.fugue.Option;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ClassUtils;

public class NewFieldExtractor
implements FieldExtractor {
    private static final int MAX_DEPTH = 5;
    private static final DefaultSingleObjectExtractor defaultSingleObjectExtractor = new DefaultSingleObjectExtractor();
    private final EventAnonymizer eventAnonymizer;

    public NewFieldExtractor(EventAnonymizer eventAnonymizer) {
        this.eventAnonymizer = eventAnonymizer;
    }

    @Override
    public Map<String, Object> extractEventProperties(Object event) {
        return this.extractPojo(event, 0, "");
    }

    private Map<String, Object> extractPojo(Object bean, int depth, String keyPrefix) {
        return defaultSingleObjectExtractor.extractSingleObject(bean).entrySet().stream().map(entry -> this.extractWithAnnotationHandling((AnnotatedInvocation)entry.getValue(), depth, (String)entry.getKey())).flatMap(extractedMap -> extractedMap.entrySet().stream()).collect(Collectors.toMap(entry -> keyPrefix + (String)entry.getKey(), Map.Entry::getValue));
    }

    private Map<String, Object> extractWithAnnotationHandling(AnnotatedInvocation invocation, int depth, String keyPrefix) {
        return (Map)Option.none().orElse(() -> this.handleStringWithAnnotations(invocation, keyPrefix)).getOr(() -> this.extractCorrectType(invocation.getInvocationResult(), depth, keyPrefix));
    }

    private Option<Map<String, Object>> handleStringWithAnnotations(AnnotatedInvocation invocation, String keyPrefix) {
        if (!(invocation.getInvocationResult() instanceof String)) {
            return Option.none();
        }
        if (invocation.isHashed()) {
            return Option.some(Collections.singletonMap(keyPrefix, this.eventAnonymizer.hash((String)invocation.getInvocationResult())));
        }
        if (invocation.isException()) {
            return Option.some(Collections.singletonMap(keyPrefix, invocation.getInvocationResult()));
        }
        return Option.none();
    }

    private Map<String, Object> extractCorrectType(@Nullable Object unsafeObject, int depth, String keyPrefix) {
        int currentDepth = depth + 1;
        return (Map)Option.none().orElse(() -> this.handleForbiddenObject(unsafeObject)).orElse(() -> this.handleMaxDepth(currentDepth)).orElse(() -> this.handleOptional(unsafeObject, currentDepth, keyPrefix)).orElse(() -> this.handleLeafNode(unsafeObject, keyPrefix)).orElse(() -> this.handleCollection(unsafeObject, currentDepth, keyPrefix)).getOr(() -> this.handlePojoTerminal(unsafeObject, currentDepth, keyPrefix));
    }

    private Option<Map<String, Object>> handleForbiddenObject(@Nullable Object unsafeObject) {
        if (unsafeObject == null || this.isForbidden(unsafeObject.getClass())) {
            return Option.some(Collections.emptyMap());
        }
        return Option.none();
    }

    private boolean isForbidden(Class<?> type) {
        return CharSequence.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || Class.class.isAssignableFrom(type) || type.isArray();
    }

    private Option<Map<String, Object>> handleMaxDepth(int currentDepth) {
        if (currentDepth > 5) {
            return Option.some(Collections.emptyMap());
        }
        return Option.none();
    }

    private Option<Map<String, Object>> handleOptional(@Nullable Object unsafeObject, int currentDepth, String keyPrefix) {
        if (unsafeObject instanceof Optional) {
            return Option.some(((Optional)unsafeObject).map(content -> this.extractCorrectType(content, currentDepth, keyPrefix)).orElse(Collections.emptyMap()));
        }
        return Option.none();
    }

    private Option<Map<String, Object>> handleLeafNode(@Nullable Object unsafeObject, String keyPrefix) {
        if (this.isLeafNode(unsafeObject.getClass())) {
            return Option.some(Collections.singletonMap(keyPrefix, unsafeObject));
        }
        return Option.none();
    }

    private Option<Map<String, Object>> handleCollection(@Nullable Object unsafeObject, int currentDepth, String keyPrefix) {
        if (Collection.class.isAssignableFrom(unsafeObject.getClass())) {
            Map<String, Object> objects = this.extractCollection((Collection)unsafeObject, currentDepth, keyPrefix);
            return objects.isEmpty() ? Option.some(Collections.emptyMap()) : Option.some(objects);
        }
        return Option.none();
    }

    private Map<String, Object> handlePojoTerminal(@Nullable Object unsafeObject, int currentDepth, String keyPrefix) {
        Map<String, Object> names2Values = this.extractPojo(unsafeObject, currentDepth, keyPrefix + ".");
        return names2Values.isEmpty() ? Collections.emptyMap() : names2Values;
    }

    private Map<String, Object> extractCollection(Collection<Object> collection, int depth, String keyPrefix) {
        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>(collection.size());
        int i = 0;
        for (Object unsafeObject : collection) {
            Map<String, Object> safeObject = this.extractCorrectType(unsafeObject, depth, "[" + i + "]");
            for (Map.Entry<String, Object> subObject : safeObject.entrySet()) {
                output.put(keyPrefix + subObject.getKey(), subObject.getValue());
            }
            ++i;
        }
        return output;
    }

    private boolean isLeafNode(Class<?> type) {
        return Enum.class.isAssignableFrom(type) || ClassUtils.isPrimitiveOrWrapper(type);
    }
}


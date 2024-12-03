/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.auditing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ThreeTenBackPortConverters;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

final class AnnotationAuditingMetadata {
    private static final ReflectionUtils.AnnotationFieldFilter CREATED_BY_FILTER = new ReflectionUtils.AnnotationFieldFilter(CreatedBy.class);
    private static final ReflectionUtils.AnnotationFieldFilter CREATED_DATE_FILTER = new ReflectionUtils.AnnotationFieldFilter(CreatedDate.class);
    private static final ReflectionUtils.AnnotationFieldFilter LAST_MODIFIED_BY_FILTER = new ReflectionUtils.AnnotationFieldFilter(LastModifiedBy.class);
    private static final ReflectionUtils.AnnotationFieldFilter LAST_MODIFIED_DATE_FILTER = new ReflectionUtils.AnnotationFieldFilter(LastModifiedDate.class);
    private static final Map<Class<?>, AnnotationAuditingMetadata> metadataCache = new ConcurrentHashMap();
    public static final boolean IS_JDK_8 = ClassUtils.isPresent((String)"java.time.Clock", (ClassLoader)AnnotationAuditingMetadata.class.getClassLoader());
    static final List<String> SUPPORTED_DATE_TYPES;
    private final Optional<Field> createdByField;
    private final Optional<Field> createdDateField;
    private final Optional<Field> lastModifiedByField;
    private final Optional<Field> lastModifiedDateField;

    private AnnotationAuditingMetadata(Class<?> type) {
        Assert.notNull(type, (String)"Given type must not be null!");
        this.createdByField = Optional.ofNullable(ReflectionUtils.findField(type, CREATED_BY_FILTER));
        this.createdDateField = Optional.ofNullable(ReflectionUtils.findField(type, CREATED_DATE_FILTER));
        this.lastModifiedByField = Optional.ofNullable(ReflectionUtils.findField(type, LAST_MODIFIED_BY_FILTER));
        this.lastModifiedDateField = Optional.ofNullable(ReflectionUtils.findField(type, LAST_MODIFIED_DATE_FILTER));
        this.assertValidDateFieldType(this.createdDateField);
        this.assertValidDateFieldType(this.lastModifiedDateField);
    }

    private void assertValidDateFieldType(Optional<Field> field) {
        field.ifPresent(it -> {
            if (SUPPORTED_DATE_TYPES.contains(it.getType().getName())) {
                return;
            }
            Class<?> type = it.getType();
            if (Jsr310Converters.supports(type) || ThreeTenBackPortConverters.supports(type)) {
                return;
            }
            throw new IllegalStateException(String.format("Found created/modified date field with type %s but only %s as well as java.time types are supported!", type, SUPPORTED_DATE_TYPES));
        });
    }

    public static AnnotationAuditingMetadata getMetadata(Class<?> type) {
        return metadataCache.computeIfAbsent(type, AnnotationAuditingMetadata::new);
    }

    public boolean isAuditable() {
        return Optionals.isAnyPresent(this.createdByField, this.createdDateField, this.lastModifiedByField, this.lastModifiedDateField);
    }

    public Optional<Field> getCreatedByField() {
        return this.createdByField;
    }

    public Optional<Field> getCreatedDateField() {
        return this.createdDateField;
    }

    public Optional<Field> getLastModifiedByField() {
        return this.lastModifiedByField;
    }

    public Optional<Field> getLastModifiedDateField() {
        return this.lastModifiedDateField;
    }

    static {
        ArrayList<String> types = new ArrayList<String>(5);
        types.add("org.joda.time.DateTime");
        types.add("org.joda.time.LocalDateTime");
        types.add(Date.class.getName());
        types.add(Long.class.getName());
        types.add(Long.TYPE.getName());
        SUPPORTED_DATE_TYPES = Collections.unmodifiableList(types);
    }
}


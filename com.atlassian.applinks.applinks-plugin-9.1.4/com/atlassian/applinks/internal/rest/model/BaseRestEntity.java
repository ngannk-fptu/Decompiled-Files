/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.applinks.internal.rest.model;

import com.atlassian.applinks.internal.rest.model.IllegalRestRepresentationStateException;
import com.atlassian.applinks.internal.rest.model.ReadOnlyRestRepresentation;
import com.atlassian.applinks.internal.rest.model.RestRepresentation;
import com.atlassian.applinks.internal.rest.model.RestRepresentations;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class BaseRestEntity
extends LinkedHashMap<String, Object> {
    @Nonnull
    public static BaseRestEntity createSingleFieldEntity(@Nonnull String fieldName, @Nullable Object value) {
        return new BaseRestEntity(Collections.singletonMap(Objects.requireNonNull(fieldName, "fieldName"), value));
    }

    public BaseRestEntity() {
    }

    public BaseRestEntity(@Nullable Map<String, Object> values) {
        if (values != null) {
            this.putAll((Map<? extends String, ?>)values);
        }
    }

    @Nonnull
    public Object getRequired(@Nonnull String key) {
        Objects.requireNonNull(key, "key");
        return BaseRestEntity.requiredValue(key, this.get(key));
    }

    @Nullable
    public Map<String, Object> getJson(@Nonnull String key) {
        Objects.requireNonNull(key, "key");
        return BaseRestEntity.expectedType(key, this.get(key), Map.class);
    }

    @Nonnull
    public Map<String, Object> getRequiredJson(@Nonnull String key) {
        Objects.requireNonNull(key, "key");
        return BaseRestEntity.requiredValue(key, this.getJson(key));
    }

    @Nullable
    public final Boolean getBoolean(@Nonnull String key) {
        Objects.requireNonNull(key, "key");
        return BaseRestEntity.expectedType(key, this.get(key), Boolean.class);
    }

    public final boolean getBooleanValue(@Nonnull String key) {
        return Boolean.TRUE.equals(this.getBoolean(key));
    }

    @Nullable
    public final String getString(@Nonnull String key) {
        Objects.requireNonNull(key, "key");
        return BaseRestEntity.expectedType(key, this.get(key), String.class);
    }

    @Nonnull
    public final String getRequiredString(@Nonnull String key) {
        String value = this.getString(key);
        return BaseRestEntity.validValue(key, value, StringUtils.isNotBlank((CharSequence)value));
    }

    @Nullable
    public final Integer getInt(@Nonnull String key) {
        Number number = BaseRestEntity.expectedType(key, this.get(key), Number.class);
        return number != null ? Integer.valueOf(number.intValue()) : null;
    }

    public final int getRequiredInt(@Nonnull String key) {
        Integer value = this.getInt(key);
        return BaseRestEntity.requiredValue(key, value);
    }

    @Nullable
    public final URI getUri(@Nonnull String key) throws IllegalRestRepresentationStateException {
        return BaseRestEntity.asUri(key, this.getString(key));
    }

    @Nonnull
    public final URI getRequiredUri(@Nonnull String key) throws IllegalRestRepresentationStateException {
        return BaseRestEntity.asUri(key, this.getRequiredString(key));
    }

    @Nullable
    public final <V extends Enum<V>> V getEnum(@Nonnull String key, @Nonnull Class<V> enumType) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(enumType, "enumType");
        Object val = this.get(key);
        if (val == null || enumType.isInstance(val)) {
            return (V)((Enum)enumType.cast(val));
        }
        if (val instanceof String) {
            try {
                return Enum.valueOf(enumType, (String)val);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalRestRepresentationStateException(key, String.format("Failed to convert %s to enum", val), e);
            }
        }
        throw new IllegalRestRepresentationStateException(key, "Unexpected type not convertible to enum: " + val.getClass().getName());
    }

    @Nullable
    public final <V extends RestRepresentation<?>> V getRestEntity(@Nonnull String key, @Nonnull Class<V> entityType) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(entityType, "entityType");
        Object val = this.get(key);
        if (val == null || entityType.isInstance(val)) {
            return (V)((RestRepresentation)entityType.cast(val));
        }
        if (val instanceof Map) {
            return RestRepresentations.fromMap((Map)val, entityType);
        }
        throw new IllegalRestRepresentationStateException(key, String.format("Unexpected type not convertible to %s: %s", entityType.getName(), val.getClass().getName()));
    }

    @Nonnull
    public final <V extends RestRepresentation<?>> V getRequiredRestEntity(@Nonnull String key, @Nonnull Class<V> entityType) {
        return (V)((RestRepresentation)BaseRestEntity.requiredValue(key, this.getRestEntity(key, entityType)));
    }

    @Nullable
    public final <D, V extends RestRepresentation<D>> D getDomain(@Nonnull String key, @Nonnull Class<V> entityType) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(entityType, "entityType");
        V restRepresentation = this.getRestEntity(key, entityType);
        return restRepresentation != null ? (D)restRepresentation.asDomain() : null;
    }

    @Nonnull
    public final <D, V extends RestRepresentation<D>> D getRequiredDomain(@Nonnull String key, @Nonnull Class<V> entityType) {
        return BaseRestEntity.requiredValue(key, this.getDomain(key, entityType));
    }

    public final void putIfNotNull(@Nonnull String key, @Nullable Object value) {
        if (value != null) {
            this.put(key, value);
        }
    }

    public final <T, R extends ReadOnlyRestRepresentation<T>> void putAs(@Nonnull String key, @Nullable T value, @Nonnull Class<R> restRepresentation) {
        this.putIfNotNull(key, RestRepresentations.fromDomainObject(value, restRepresentation));
    }

    public final <E extends Enum<E>> void putEnum(@Nonnull String key, @Nullable E value) {
        if (value != null) {
            this.put(key, (Object)value.name());
        }
    }

    public final void putMap(@Nonnull String key, @Nullable Map<?, ?> value) {
        if (value != null && !value.isEmpty()) {
            this.put(key, (Object)value);
        }
    }

    public final void putIterable(@Nonnull String key, @Nullable Iterable<?> value) {
        if (value != null && value.iterator().hasNext()) {
            this.put(key, (Object)value);
        }
    }

    public final <T, R extends ReadOnlyRestRepresentation<T>> void putIterableOf(@Nonnull String key, @Nullable Iterable<T> value, @Nonnull Class<R> restRepresentation) {
        if (value != null) {
            this.putIterable(key, (Iterable<?>)ImmutableList.copyOf((Iterable)Iterables.transform(value, RestRepresentations.fromDomainFunction(restRepresentation))));
        }
    }

    public final void putAsString(@Nonnull String key, @Nullable Object value) {
        if (value != null) {
            this.put(key, (Object)value.toString());
        }
    }

    @Override
    public Object put(String key, Object value) {
        BaseRestEntity.checkKey(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        for (String string : map.keySet()) {
            BaseRestEntity.checkKey(string);
        }
        super.putAll(map);
    }

    protected static void checkKey(String key) {
        Objects.requireNonNull(key, "key");
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)key), (String)"key was blank", (Object[])new Object[0]);
    }

    @Nullable
    protected static <T> T expectedType(@Nonnull String key, @Nullable Object value, @Nonnull Class<T> expectedType) {
        if (value == null) {
            return null;
        }
        if (!expectedType.isInstance(value)) {
            throw new IllegalRestRepresentationStateException(key, String.format("Value '%s' is not a %s", value, expectedType.getSimpleName()));
        }
        return expectedType.cast(value);
    }

    @Nonnull
    protected static <T> T requiredValue(@Nonnull String key, @Nullable T value) throws IllegalRestRepresentationStateException {
        return BaseRestEntity.validValue(key, value, value != null);
    }

    @Nonnull
    protected static <T> T validValue(@Nonnull String key, @Nullable T value, boolean test) throws IllegalRestRepresentationStateException {
        if (!test || value == null) {
            throw new IllegalRestRepresentationStateException(key);
        }
        return value;
    }

    private static URI asUri(@Nonnull String key, @Nullable String value) {
        try {
            return value != null ? new URI(value) : null;
        }
        catch (URISyntaxException e) {
            throw new IllegalRestRepresentationStateException(key, String.format("Failed to convert '%s' to URI", value), e);
        }
    }

    public static class Builder {
        protected final Map<String, Object> fields = Maps.newLinkedHashMap();

        @Nonnull
        public Builder add(@Nonnull String fieldName, @Nullable Object value) {
            Objects.requireNonNull(fieldName, "fieldName");
            this.fields.put(fieldName, value);
            return this;
        }

        @Nonnull
        public BaseRestEntity build() {
            return new BaseRestEntity(this.fields);
        }
    }
}


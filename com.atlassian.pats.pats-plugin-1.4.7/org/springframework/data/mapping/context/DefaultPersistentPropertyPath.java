/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

class DefaultPersistentPropertyPath<P extends PersistentProperty<P>>
implements PersistentPropertyPath<P> {
    private static final Converter<PersistentProperty<?>, String> DEFAULT_CONVERTER = source -> source.getName();
    private static final String DEFAULT_DELIMITER = ".";
    private final List<P> properties;

    public DefaultPersistentPropertyPath(List<P> properties) {
        Assert.notNull(properties, (String)"Properties must not be null!");
        this.properties = properties;
    }

    public static <T extends PersistentProperty<T>> DefaultPersistentPropertyPath<T> empty() {
        return new DefaultPersistentPropertyPath(Collections.emptyList());
    }

    public DefaultPersistentPropertyPath<P> append(P property) {
        Assert.notNull(property, (String)"Property must not be null!");
        if (this.isEmpty()) {
            return new DefaultPersistentPropertyPath<P>(Collections.singletonList(property));
        }
        Class<?> leafPropertyType = this.getLeafProperty().getActualType();
        Assert.isTrue((boolean)property.getOwner().getType().equals(leafPropertyType), () -> String.format("Cannot append property %s to type %s!", property.getName(), leafPropertyType.getName()));
        ArrayList<P> properties = new ArrayList<P>(this.properties);
        properties.add(property);
        return new DefaultPersistentPropertyPath<P>(properties);
    }

    @Override
    @Nullable
    public String toDotPath() {
        return this.toPath(DEFAULT_DELIMITER, DEFAULT_CONVERTER);
    }

    @Override
    @Nullable
    public String toDotPath(Converter<? super P, String> converter) {
        return this.toPath(DEFAULT_DELIMITER, converter);
    }

    @Override
    @Nullable
    public String toPath(String delimiter) {
        return this.toPath(delimiter, DEFAULT_CONVERTER);
    }

    @Override
    @Nullable
    public String toPath(String delimiter, Converter<? super P, String> converter) {
        Assert.hasText((String)delimiter, (String)"Delimiter must not be null or empty!");
        Assert.notNull(converter, (String)"Converter must not be null!");
        String result = this.properties.stream().map(arg_0 -> converter.convert(arg_0)).filter(StringUtils::hasText).collect(Collectors.joining(delimiter));
        return result.isEmpty() ? null : result;
    }

    @Override
    @Nullable
    public P getLeafProperty() {
        return (P)(this.properties.isEmpty() ? null : (PersistentProperty)this.properties.get(this.properties.size() - 1));
    }

    @Override
    @Nullable
    public P getBaseProperty() {
        return (P)(this.properties.isEmpty() ? null : (PersistentProperty)this.properties.get(0));
    }

    @Override
    public boolean isBasePathOf(PersistentPropertyPath<P> path) {
        Assert.notNull(path, (String)"PersistentPropertyPath must not be null!");
        Iterator iterator = path.iterator();
        for (PersistentProperty property : this) {
            if (!iterator.hasNext()) {
                return false;
            }
            PersistentProperty reference = (PersistentProperty)iterator.next();
            if (property.equals(reference)) continue;
            return false;
        }
        return true;
    }

    @Override
    public PersistentPropertyPath<P> getExtensionForBaseOf(PersistentPropertyPath<P> base) {
        if (!base.isBasePathOf(this)) {
            return this;
        }
        ArrayList<P> result = new ArrayList<P>();
        Iterator<P> iterator = this.iterator();
        for (int i = 0; i < base.getLength(); ++i) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return new DefaultPersistentPropertyPath(result);
    }

    @Override
    public PersistentPropertyPath<P> getParentPath() {
        int size = this.properties.size();
        return size == 0 ? this : new DefaultPersistentPropertyPath<P>(this.properties.subList(0, size - 1));
    }

    @Override
    public int getLength() {
        return this.properties.size();
    }

    @Override
    public Iterator<P> iterator() {
        return this.properties.iterator();
    }

    public boolean containsPropertyOfType(@Nullable TypeInformation<?> type) {
        return type == null ? false : this.properties.stream().anyMatch(property -> type.equals(property.getTypeInformation().getActualType()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultPersistentPropertyPath)) {
            return false;
        }
        DefaultPersistentPropertyPath that = (DefaultPersistentPropertyPath)o;
        return ObjectUtils.nullSafeEquals(this.properties, that.properties);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.properties);
    }

    @Nullable
    public String toString() {
        return this.toDotPath();
    }
}


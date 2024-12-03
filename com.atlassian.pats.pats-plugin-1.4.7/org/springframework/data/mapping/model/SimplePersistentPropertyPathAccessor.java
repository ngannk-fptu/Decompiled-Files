/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.CollectionFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.data.mapping.AccessOptions;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PersistentPropertyPathAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

class SimplePersistentPropertyPathAccessor<T>
implements PersistentPropertyPathAccessor<T> {
    private static final Log logger = LogFactory.getLog(SimplePersistentPropertyPathAccessor.class);
    private static final AccessOptions.GetOptions DEFAULT_GET_OPTIONS = AccessOptions.defaultGetOptions();
    private final PersistentPropertyAccessor<T> delegate;

    public SimplePersistentPropertyPathAccessor(PersistentPropertyAccessor<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T getBean() {
        return this.delegate.getBean();
    }

    @Override
    @Nullable
    public Object getProperty(PersistentProperty<?> property) {
        return this.delegate.getProperty(property);
    }

    @Override
    @Nullable
    public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path) {
        return this.getProperty(path, DEFAULT_GET_OPTIONS);
    }

    @Override
    @Nullable
    public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path, AccessOptions.GetOptions options) {
        T bean;
        Object current = bean = this.getBean();
        if (path.isEmpty()) {
            return bean;
        }
        for (PersistentProperty persistentProperty : path) {
            if (current == null) {
                return this.handleNull(path, options.getNullValues().toNullHandling());
            }
            PersistentEntity entity = persistentProperty.getOwner();
            PersistentPropertyAccessor<T> accessor = entity.getPropertyAccessor(current);
            current = accessor.getProperty(persistentProperty);
        }
        return current;
    }

    @Override
    public void setProperty(PersistentProperty<?> property, @Nullable Object value) {
        this.delegate.setProperty(property, value);
    }

    @Override
    public void setProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path, @Nullable Object value) {
        this.setProperty(path, value, AccessOptions.defaultSetOptions());
    }

    @Override
    public void setProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path, @Nullable Object value, AccessOptions.SetOptions options) {
        Object newValue;
        T parent;
        Assert.notNull(path, (String)"PersistentPropertyPath must not be null!");
        Assert.isTrue((!path.isEmpty() ? 1 : 0) != 0, (String)"PersistentPropertyPath must not be empty!");
        PersistentPropertyPath<PersistentProperty<?>> parentPath = path.getParentPath();
        PersistentProperty<?> leafProperty = path.getRequiredLeafProperty();
        if (!options.propagate(parentPath.getLeafProperty())) {
            return;
        }
        AccessOptions.GetOptions lookupOptions = options.getNullHandling() != AccessOptions.SetOptions.SetNulls.REJECT ? DEFAULT_GET_OPTIONS.withNullValues(AccessOptions.GetOptions.GetNulls.EARLY_RETURN) : DEFAULT_GET_OPTIONS;
        Object object = parent = parentPath.isEmpty() ? this.getBean() : this.getProperty(parentPath, lookupOptions);
        if (parent == null) {
            this.handleNull(path, options.getNullHandling());
            return;
        }
        if (parent == this.getBean()) {
            this.setProperty(leafProperty, value);
            return;
        }
        PersistentProperty<?> parentProperty = parentPath.getRequiredLeafProperty();
        if (parentProperty.isCollectionLike()) {
            Collection source = this.getTypedProperty(parentProperty, Collection.class);
            if (source == null) {
                return;
            }
            newValue = source.stream().map(it -> SimplePersistentPropertyPathAccessor.setValue(it, leafProperty, value)).collect(Collectors.toCollection(() -> CollectionFactory.createApproximateCollection((Object)source, (int)source.size())));
        } else if (Map.class.isInstance(parent)) {
            Map source = this.getTypedProperty(parentProperty, Map.class);
            if (source == null) {
                return;
            }
            Map result = CollectionFactory.createApproximateMap((Object)source, (int)source.size());
            for (Map.Entry entry : source.entrySet()) {
                result.put(entry.getKey(), SimplePersistentPropertyPathAccessor.setValue(entry.getValue(), leafProperty, value));
            }
            newValue = result;
        } else {
            newValue = SimplePersistentPropertyPathAccessor.setValue(parent, leafProperty, value);
        }
        if (newValue != parent) {
            this.setProperty(parentPath, newValue);
        }
    }

    @Nullable
    private Object handleNull(PersistentPropertyPath<? extends PersistentProperty<?>> path, AccessOptions.SetOptions.SetNulls handling) {
        if (AccessOptions.SetOptions.SetNulls.SKIP.equals((Object)handling)) {
            return null;
        }
        String nullIntermediateMessage = "Cannot lookup property %s on null intermediate! Original path was: %s on %s.";
        if (AccessOptions.SetOptions.SetNulls.SKIP_AND_LOG.equals((Object)handling)) {
            logger.info((Object)nullIntermediateMessage);
            return null;
        }
        PersistentPropertyPath<PersistentProperty<?>> parentPath = path.getParentPath();
        throw new MappingException(String.format(nullIntermediateMessage, parentPath.getLeafProperty(), path.toDotPath(), this.getBean().getClass().getName()));
    }

    private static Object setValue(Object parent, PersistentProperty<?> property, @Nullable Object newValue) {
        PersistentPropertyAccessor<Object> accessor = property.getAccessorForOwner(parent);
        accessor.setProperty(property, newValue);
        return accessor.getBean();
    }

    @Nullable
    protected <S> S getTypedProperty(PersistentProperty<?> property, Class<S> type) {
        Assert.notNull(property, (String)"Property must not be null!");
        Assert.notNull(type, (String)"Type must not be null!");
        Object value = this.getProperty(property);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new MappingException(String.format("Invalid property value type! Need %s but got %s!", type.getName(), value.getClass().getName()));
        }
        return type.cast(value);
    }
}


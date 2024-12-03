/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.TraversalContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public interface PersistentPropertyAccessor<T> {
    public void setProperty(PersistentProperty<?> var1, @Nullable Object var2);

    @Deprecated
    default public void setProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path, @Nullable Object value) {
        Object parent;
        PersistentProperty<?> parentProperty;
        Assert.notNull(path, (String)"PersistentPropertyPath must not be null!");
        Assert.isTrue((!path.isEmpty() ? 1 : 0) != 0, (String)"PersistentPropertyPath must not be empty!");
        PersistentPropertyPath<PersistentProperty<?>> parentPath = path.getParentPath();
        PersistentProperty<?> leafProperty = path.getRequiredLeafProperty();
        PersistentProperty<?> persistentProperty = parentProperty = parentPath.isEmpty() ? null : parentPath.getLeafProperty();
        if (parentProperty != null && (parentProperty.isCollectionLike() || parentProperty.isMap())) {
            throw new MappingException(String.format("Cannot traverse collection or map intermediate %s", parentPath.toDotPath()));
        }
        Object object = parent = parentPath.isEmpty() ? this.getBean() : this.getProperty(parentPath);
        if (parent == null) {
            String nullIntermediateMessage = "Cannot lookup property %s on null intermediate! Original path was: %s on %s.";
            throw new MappingException(String.format(nullIntermediateMessage, parentProperty, path.toDotPath(), this.getBean().getClass().getName()));
        }
        PersistentPropertyAccessor<T> accessor = parent == this.getBean() ? this : leafProperty.getOwner().getPropertyAccessor(parent);
        accessor.setProperty(leafProperty, value);
        if (parentPath.isEmpty()) {
            return;
        }
        T bean = accessor.getBean();
        if (bean != parent) {
            this.setProperty(parentPath, bean);
        }
    }

    @Nullable
    public Object getProperty(PersistentProperty<?> var1);

    @Deprecated
    @Nullable
    default public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path) {
        return this.getProperty(path, new TraversalContext());
    }

    @Nullable
    @Deprecated
    default public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path, TraversalContext context) {
        T bean;
        Object current = bean = this.getBean();
        if (path.isEmpty()) {
            return bean;
        }
        for (PersistentProperty persistentProperty : path) {
            if (current == null) {
                String nullIntermediateMessage = "Cannot lookup property %s on null intermediate! Original path was: %s on %s.";
                throw new MappingException(String.format(nullIntermediateMessage, persistentProperty, path.toDotPath(), bean.getClass().getName()));
            }
            PersistentEntity entity = persistentProperty.getOwner();
            PersistentPropertyAccessor<T> accessor = entity.getPropertyAccessor(current);
            current = context.postProcess(persistentProperty, accessor.getProperty(persistentProperty));
        }
        return current;
    }

    public T getBean();
}


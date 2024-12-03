/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public interface PersistentProperty<P extends PersistentProperty<P>> {
    public PersistentEntity<?, P> getOwner();

    public String getName();

    public Class<?> getType();

    public TypeInformation<?> getTypeInformation();

    public Iterable<? extends TypeInformation<?>> getPersistentEntityTypes();

    @Nullable
    public Method getGetter();

    default public Method getRequiredGetter() {
        Method getter = this.getGetter();
        if (getter == null) {
            throw new IllegalArgumentException(String.format("No getter available for persistent property %s!", this));
        }
        return getter;
    }

    @Nullable
    public Method getSetter();

    default public Method getRequiredSetter() {
        Method setter = this.getSetter();
        if (setter == null) {
            throw new IllegalArgumentException(String.format("No setter available for persistent property %s!", this));
        }
        return setter;
    }

    @Nullable
    public Method getWither();

    default public Method getRequiredWither() {
        Method wither = this.getWither();
        if (wither == null) {
            throw new IllegalArgumentException(String.format("No wither available for persistent property %s!", this));
        }
        return wither;
    }

    @Nullable
    public Field getField();

    default public Field getRequiredField() {
        Field field = this.getField();
        if (field == null) {
            throw new IllegalArgumentException(String.format("No field backing persistent property %s!", this));
        }
        return field;
    }

    @Nullable
    public String getSpelExpression();

    @Nullable
    public Association<P> getAssociation();

    default public Association<P> getRequiredAssociation() {
        Association<P> association = this.getAssociation();
        if (association != null) {
            return association;
        }
        throw new IllegalStateException("No association found!");
    }

    public boolean isEntity();

    public boolean isIdProperty();

    public boolean isVersionProperty();

    public boolean isCollectionLike();

    public boolean isMap();

    public boolean isArray();

    public boolean isTransient();

    public boolean isWritable();

    public boolean isImmutable();

    public boolean isAssociation();

    @Nullable
    public Class<?> getComponentType();

    public Class<?> getRawType();

    @Nullable
    public Class<?> getMapValueType();

    public Class<?> getActualType();

    @Nullable
    public <A extends Annotation> A findAnnotation(Class<A> var1);

    default public <A extends Annotation> A getRequiredAnnotation(Class<A> annotationType) throws IllegalStateException {
        A annotation = this.findAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        throw new IllegalStateException(String.format("Required annotation %s not found for %s!", annotationType, this.getName()));
    }

    @Nullable
    public <A extends Annotation> A findPropertyOrOwnerAnnotation(Class<A> var1);

    public boolean isAnnotationPresent(Class<? extends Annotation> var1);

    public boolean usePropertyAccess();

    default public boolean hasActualTypeAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, (String)"Annotation type must not be null!");
        return AnnotatedElementUtils.hasAnnotation(this.getActualType(), annotationType);
    }

    @Nullable
    public Class<?> getAssociationTargetType();

    default public <T> PersistentPropertyAccessor<T> getAccessorForOwner(T owner) {
        Assert.notNull(owner, (String)"Owner must not be null!");
        return this.getOwner().getPropertyAccessor(owner);
    }
}


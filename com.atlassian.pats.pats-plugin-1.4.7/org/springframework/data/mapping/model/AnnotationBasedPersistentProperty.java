/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.AbstractPersistentProperty;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.StreamUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AnnotationBasedPersistentProperty<P extends PersistentProperty<P>>
extends AbstractPersistentProperty<P> {
    private static final String SPRING_DATA_PACKAGE = "org.springframework.data";
    @Nullable
    private final String value;
    private final Map<Class<? extends Annotation>, Optional<? extends Annotation>> annotationCache = new ConcurrentHashMap<Class<? extends Annotation>, Optional<? extends Annotation>>();
    private final Lazy<Boolean> usePropertyAccess = Lazy.of(() -> {
        AccessType accessType = this.findPropertyOrOwnerAnnotation(AccessType.class);
        return accessType != null && AccessType.Type.PROPERTY.equals((Object)accessType.value()) || super.usePropertyAccess();
    });
    private final Lazy<Boolean> isTransient = Lazy.of(() -> super.isTransient() || this.isAnnotationPresent(Transient.class) || this.isAnnotationPresent(Value.class) || this.isAnnotationPresent(Autowired.class));
    private final Lazy<Boolean> isWritable = Lazy.of(() -> !this.isTransient() && !this.isAnnotationPresent(ReadOnlyProperty.class));
    private final Lazy<Boolean> isReference = Lazy.of(() -> !this.isTransient() && (this.isAnnotationPresent(Reference.class) || super.isAssociation()));
    private final Lazy<Boolean> isId = Lazy.of(() -> this.isAnnotationPresent(Id.class));
    private final Lazy<Boolean> isVersion = Lazy.of(() -> this.isAnnotationPresent(Version.class));
    private final Lazy<Class<?>> associationTargetType = Lazy.of(() -> {
        if (!this.isAssociation()) {
            return null;
        }
        return Optional.of(Reference.class).map(this::findAnnotation).map(Reference::to).map(it -> !Class.class.equals(it) ? it : this.getActualType()).orElseGet(() -> super.getAssociationTargetType());
    });

    public AnnotationBasedPersistentProperty(Property property, PersistentEntity<?, P> owner, SimpleTypeHolder simpleTypeHolder) {
        super(property, owner, simpleTypeHolder);
        this.populateAnnotationCache(property);
        Value value = this.findAnnotation(Value.class);
        this.value = value == null ? null : value.value();
    }

    private void populateAnnotationCache(Property property) {
        Optionals.toStream(property.getGetter(), property.getSetter()).forEach(it -> {
            for (Annotation annotation : it.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                Annotation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation((AnnotatedElement)it, annotationType);
                this.validateAnnotation(mergedAnnotation, "Ambiguous mapping! Annotation %s configured multiple times on accessor methods of property %s in class %s!", annotationType.getSimpleName(), this.getName(), this.getOwner().getType().getSimpleName());
                this.annotationCache.put(annotationType, Optional.of(mergedAnnotation));
            }
        });
        property.getField().ifPresent(it -> {
            for (Annotation annotation : it.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                Annotation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation((AnnotatedElement)it, annotationType);
                this.validateAnnotation(mergedAnnotation, "Ambiguous mapping! Annotation %s configured on field %s and one of its accessor methods in class %s!", annotationType.getSimpleName(), it.getName(), this.getOwner().getType().getSimpleName());
                this.annotationCache.put(annotationType, Optional.of(mergedAnnotation));
            }
        });
    }

    private void validateAnnotation(Annotation candidate, String message, Object ... arguments) {
        Class<? extends Annotation> annotationType = candidate.annotationType();
        if (!annotationType.getName().startsWith(SPRING_DATA_PACKAGE)) {
            return;
        }
        if (this.annotationCache.containsKey(annotationType) && !this.annotationCache.get(annotationType).equals(Optional.of(candidate))) {
            throw new MappingException(String.format(message, arguments));
        }
    }

    @Override
    @Nullable
    public String getSpelExpression() {
        return this.value;
    }

    @Override
    public boolean isTransient() {
        return this.isTransient.get();
    }

    @Override
    public boolean isIdProperty() {
        return this.isId.get();
    }

    @Override
    public boolean isVersionProperty() {
        return this.isVersion.get();
    }

    @Override
    public boolean isAssociation() {
        return this.isReference.get();
    }

    @Override
    public boolean isWritable() {
        return this.isWritable.get();
    }

    @Override
    @Nullable
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
        Assert.notNull(annotationType, (String)"Annotation type must not be null!");
        return (A)((Annotation)this.doFindAnnotation(annotationType).orElse(null));
    }

    private <A extends Annotation> Optional<A> doFindAnnotation(Class<A> annotationType) {
        Optional<? extends Annotation> annotation = this.annotationCache.get(annotationType);
        if (annotation != null) {
            return annotation;
        }
        return this.annotationCache.computeIfAbsent(annotationType, type -> this.getAccessors().map(it -> AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)it, (Class)type)).flatMap(StreamUtils::fromNullable).findFirst());
    }

    @Override
    @Nullable
    public <A extends Annotation> A findPropertyOrOwnerAnnotation(Class<A> annotationType) {
        A annotation = this.findAnnotation(annotationType);
        return annotation != null ? annotation : this.getOwner().findAnnotation(annotationType);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.doFindAnnotation(annotationType).isPresent();
    }

    @Override
    public boolean usePropertyAccess() {
        return this.usePropertyAccess.get();
    }

    @Override
    @Nullable
    public Class<?> getAssociationTargetType() {
        return this.associationTargetType.getNullable();
    }

    @Override
    public String toString() {
        if (this.annotationCache.isEmpty()) {
            this.populateAnnotationCache(this.getProperty());
        }
        String builder = this.annotationCache.values().stream().flatMap(xva$0 -> Optionals.toStream(xva$0)).map(Object::toString).collect(Collectors.joining(" "));
        return builder + super.toString();
    }

    private Stream<? extends AnnotatedElement> getAccessors() {
        return Optionals.toStream(Optional.ofNullable(this.getGetter()), Optional.ofNullable(this.getSetter()), Optional.ofNullable(this.getField()));
    }
}


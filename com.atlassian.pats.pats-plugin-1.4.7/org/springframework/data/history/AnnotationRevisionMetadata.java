/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldCallback
 */
package org.springframework.data.history;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.util.AnnotationDetectionFieldCallback;
import org.springframework.data.util.AnnotationDetectionMethodCallback;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class AnnotationRevisionMetadata<N extends Number>
implements RevisionMetadata<N> {
    private final Object entity;
    private final Lazy<Optional<N>> revisionNumber;
    private final Lazy<Optional<Object>> revisionDate;
    private final RevisionMetadata.RevisionType revisionType;

    public AnnotationRevisionMetadata(Object entity, Class<? extends Annotation> revisionNumberAnnotation, Class<? extends Annotation> revisionTimeStampAnnotation) {
        this(entity, revisionNumberAnnotation, revisionTimeStampAnnotation, RevisionMetadata.RevisionType.UNKNOWN);
    }

    public AnnotationRevisionMetadata(Object entity, Class<? extends Annotation> revisionNumberAnnotation, Class<? extends Annotation> revisionTimeStampAnnotation, RevisionMetadata.RevisionType revisionType) {
        Assert.notNull((Object)entity, (String)"Entity must not be null!");
        Assert.notNull(revisionNumberAnnotation, (String)"Revision number annotation must not be null!");
        Assert.notNull(revisionTimeStampAnnotation, (String)"Revision time stamp annotation must not be null!");
        Assert.notNull((Object)((Object)revisionType), (String)"Revision Type must not be null!");
        this.entity = entity;
        this.revisionNumber = AnnotationRevisionMetadata.detectAnnotation(entity, revisionNumberAnnotation);
        this.revisionDate = AnnotationRevisionMetadata.detectAnnotation(entity, revisionTimeStampAnnotation);
        this.revisionType = revisionType;
    }

    @Override
    public Optional<N> getRevisionNumber() {
        return this.revisionNumber.get();
    }

    @Override
    public Optional<Instant> getRevisionInstant() {
        return this.revisionDate.get().map(AnnotationRevisionMetadata::convertToInstant);
    }

    @Override
    public RevisionMetadata.RevisionType getRevisionType() {
        return this.revisionType;
    }

    @Override
    public <T> T getDelegate() {
        return (T)this.entity;
    }

    private static <T> Lazy<Optional<T>> detectAnnotation(Object entity, Class<? extends Annotation> annotationType) {
        return Lazy.of(() -> {
            AnnotationDetectionMethodCallback methodCallback = new AnnotationDetectionMethodCallback(annotationType);
            ReflectionUtils.doWithMethods(entity.getClass(), methodCallback);
            if (methodCallback.getMethod() != null) {
                return Optional.ofNullable(ReflectionUtils.invokeMethod((Method)methodCallback.getRequiredMethod(), (Object)entity));
            }
            AnnotationDetectionFieldCallback callback = new AnnotationDetectionFieldCallback(annotationType);
            ReflectionUtils.doWithFields(entity.getClass(), (ReflectionUtils.FieldCallback)callback);
            return Optional.ofNullable(callback.getValue(entity));
        });
    }

    private static Instant convertToInstant(Object timestamp) {
        if (timestamp instanceof Instant) {
            return (Instant)timestamp;
        }
        if (timestamp instanceof LocalDateTime) {
            return ((LocalDateTime)timestamp).atZone(ZoneOffset.systemDefault()).toInstant();
        }
        if (timestamp instanceof Long) {
            return Instant.ofEpochMilli((Long)timestamp);
        }
        if (Date.class.isInstance(timestamp)) {
            return ((Date)Date.class.cast(timestamp)).toInstant();
        }
        throw new IllegalArgumentException(String.format("Can't convert %s to Instant!", timestamp));
    }
}


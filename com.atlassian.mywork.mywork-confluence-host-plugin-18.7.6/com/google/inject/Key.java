/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.util.$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Key<T> {
    private final AnnotationStrategy annotationStrategy;
    private final TypeLiteral<T> typeLiteral;
    private final int hashCode;

    protected Key(Class<? extends Annotation> annotationType) {
        this.annotationStrategy = Key.strategyFor(annotationType);
        this.typeLiteral = TypeLiteral.fromSuperclassTypeParameter(this.getClass());
        this.hashCode = this.computeHashCode();
    }

    protected Key(Annotation annotation) {
        this.annotationStrategy = Key.strategyFor(annotation);
        this.typeLiteral = TypeLiteral.fromSuperclassTypeParameter(this.getClass());
        this.hashCode = this.computeHashCode();
    }

    protected Key() {
        this.annotationStrategy = NullAnnotationStrategy.INSTANCE;
        this.typeLiteral = TypeLiteral.fromSuperclassTypeParameter(this.getClass());
        this.hashCode = this.computeHashCode();
    }

    private Key(Type type, AnnotationStrategy annotationStrategy) {
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = MoreTypes.canonicalizeForKey(TypeLiteral.get(type));
        this.hashCode = this.computeHashCode();
    }

    private Key(TypeLiteral<T> typeLiteral, AnnotationStrategy annotationStrategy) {
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = MoreTypes.canonicalizeForKey(typeLiteral);
        this.hashCode = this.computeHashCode();
    }

    private int computeHashCode() {
        return this.typeLiteral.hashCode() * 31 + this.annotationStrategy.hashCode();
    }

    public final TypeLiteral<T> getTypeLiteral() {
        return this.typeLiteral;
    }

    public final Class<? extends Annotation> getAnnotationType() {
        return this.annotationStrategy.getAnnotationType();
    }

    public final Annotation getAnnotation() {
        return this.annotationStrategy.getAnnotation();
    }

    boolean hasAnnotationType() {
        return this.annotationStrategy.getAnnotationType() != null;
    }

    String getAnnotationName() {
        Annotation annotation = this.annotationStrategy.getAnnotation();
        if (annotation != null) {
            return ((Object)annotation).toString();
        }
        return this.annotationStrategy.getAnnotationType().toString();
    }

    Class<? super T> getRawType() {
        return this.typeLiteral.getRawType();
    }

    Key<Provider<T>> providerKey() {
        return this.ofType(this.typeLiteral.providerType());
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Key)) {
            return false;
        }
        Key other = (Key)o;
        return this.annotationStrategy.equals(other.annotationStrategy) && this.typeLiteral.equals(other.typeLiteral);
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final String toString() {
        return "Key[type=" + this.typeLiteral + ", annotation=" + this.annotationStrategy + "]";
    }

    static <T> Key<T> get(Class<T> type, AnnotationStrategy annotationStrategy) {
        return new Key<T>(type, annotationStrategy);
    }

    public static <T> Key<T> get(Class<T> type) {
        return new Key<T>(type, (AnnotationStrategy)NullAnnotationStrategy.INSTANCE);
    }

    public static <T> Key<T> get(Class<T> type, Class<? extends Annotation> annotationType) {
        return new Key<T>(type, Key.strategyFor(annotationType));
    }

    public static <T> Key<T> get(Class<T> type, Annotation annotation) {
        return new Key<T>(type, Key.strategyFor(annotation));
    }

    public static Key<?> get(Type type) {
        return new Key(type, (AnnotationStrategy)NullAnnotationStrategy.INSTANCE);
    }

    public static Key<?> get(Type type, Class<? extends Annotation> annotationType) {
        return new Key(type, Key.strategyFor(annotationType));
    }

    public static Key<?> get(Type type, Annotation annotation) {
        return new Key(type, Key.strategyFor(annotation));
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral) {
        return new Key<T>(typeLiteral, (AnnotationStrategy)NullAnnotationStrategy.INSTANCE);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Class<? extends Annotation> annotationType) {
        return new Key<T>(typeLiteral, Key.strategyFor(annotationType));
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Annotation annotation) {
        return new Key<T>(typeLiteral, Key.strategyFor(annotation));
    }

    public <T> Key<T> ofType(Class<T> type) {
        return new Key<T>(type, this.annotationStrategy);
    }

    public Key<?> ofType(Type type) {
        return new Key<T>(type, this.annotationStrategy);
    }

    public <T> Key<T> ofType(TypeLiteral<T> type) {
        return new Key<T>(type, this.annotationStrategy);
    }

    public boolean hasAttributes() {
        return this.annotationStrategy.hasAttributes();
    }

    public Key<T> withoutAttributes() {
        return new Key<T>(this.typeLiteral, this.annotationStrategy.withoutAttributes());
    }

    static AnnotationStrategy strategyFor(Annotation annotation) {
        $Preconditions.checkNotNull(annotation, "annotation");
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Key.ensureRetainedAtRuntime(annotationType);
        Key.ensureIsBindingAnnotation(annotationType);
        if (Annotations.isMarker(annotationType)) {
            return new AnnotationTypeStrategy(annotationType, annotation);
        }
        return new AnnotationInstanceStrategy(Annotations.canonicalizeIfNamed(annotation));
    }

    static AnnotationStrategy strategyFor(Class<? extends Annotation> annotationType) {
        $Preconditions.checkNotNull(annotationType, "annotation type");
        Key.ensureRetainedAtRuntime(annotationType);
        Key.ensureIsBindingAnnotation(annotationType);
        return new AnnotationTypeStrategy(Annotations.canonicalizeIfNamed(annotationType), null);
    }

    private static void ensureRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        $Preconditions.checkArgument(Annotations.isRetainedAtRuntime(annotationType), "%s is not retained at runtime. Please annotate it with @Retention(RUNTIME).", annotationType.getName());
    }

    private static void ensureIsBindingAnnotation(Class<? extends Annotation> annotationType) {
        $Preconditions.checkArgument(Annotations.isBindingAnnotation(annotationType), "%s is not a binding annotation. Please annotate it with @BindingAnnotation.", annotationType.getName());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class AnnotationTypeStrategy
    implements AnnotationStrategy {
        final Class<? extends Annotation> annotationType;
        final Annotation annotation;

        AnnotationTypeStrategy(Class<? extends Annotation> annotationType, Annotation annotation) {
            this.annotationType = $Preconditions.checkNotNull(annotationType, "annotation type");
            this.annotation = annotation;
        }

        @Override
        public boolean hasAttributes() {
            return false;
        }

        @Override
        public AnnotationStrategy withoutAttributes() {
            throw new UnsupportedOperationException("Key already has no attributes.");
        }

        @Override
        public Annotation getAnnotation() {
            return this.annotation;
        }

        @Override
        public Class<? extends Annotation> getAnnotationType() {
            return this.annotationType;
        }

        public boolean equals(Object o) {
            if (!(o instanceof AnnotationTypeStrategy)) {
                return false;
            }
            AnnotationTypeStrategy other = (AnnotationTypeStrategy)o;
            return this.annotationType.equals(other.annotationType);
        }

        public int hashCode() {
            return this.annotationType.hashCode();
        }

        public String toString() {
            return "@" + this.annotationType.getName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class AnnotationInstanceStrategy
    implements AnnotationStrategy {
        final Annotation annotation;

        AnnotationInstanceStrategy(Annotation annotation) {
            this.annotation = $Preconditions.checkNotNull(annotation, "annotation");
        }

        @Override
        public boolean hasAttributes() {
            return true;
        }

        @Override
        public AnnotationStrategy withoutAttributes() {
            return new AnnotationTypeStrategy(this.getAnnotationType(), this.annotation);
        }

        @Override
        public Annotation getAnnotation() {
            return this.annotation;
        }

        @Override
        public Class<? extends Annotation> getAnnotationType() {
            return this.annotation.annotationType();
        }

        public boolean equals(Object o) {
            if (!(o instanceof AnnotationInstanceStrategy)) {
                return false;
            }
            AnnotationInstanceStrategy other = (AnnotationInstanceStrategy)o;
            return ((Object)this.annotation).equals(other.annotation);
        }

        public int hashCode() {
            return ((Object)this.annotation).hashCode();
        }

        public String toString() {
            return ((Object)this.annotation).toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum NullAnnotationStrategy implements AnnotationStrategy
    {
        INSTANCE;


        @Override
        public boolean hasAttributes() {
            return false;
        }

        @Override
        public AnnotationStrategy withoutAttributes() {
            throw new UnsupportedOperationException("Key already has no attributes.");
        }

        @Override
        public Annotation getAnnotation() {
            return null;
        }

        @Override
        public Class<? extends Annotation> getAnnotationType() {
            return null;
        }

        public String toString() {
            return "[none]";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static interface AnnotationStrategy {
        public Annotation getAnnotation();

        public Class<? extends Annotation> getAnnotationType();

        public boolean hasAttributes();

        public AnnotationStrategy withoutAttributes();
    }
}


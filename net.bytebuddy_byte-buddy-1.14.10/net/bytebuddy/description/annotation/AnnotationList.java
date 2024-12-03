/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AnnotationList
extends FilterableList<AnnotationDescription, AnnotationList> {
    public boolean isAnnotationPresent(Class<? extends Annotation> var1);

    public boolean isAnnotationPresent(TypeDescription var1);

    @MaybeNull
    public <T extends Annotation> AnnotationDescription.Loadable<T> ofType(Class<T> var1);

    public AnnotationDescription ofType(TypeDescription var1);

    public AnnotationList inherited(Set<? extends TypeDescription> var1);

    public AnnotationList visibility(ElementMatcher<? super RetentionPolicy> var1);

    public TypeList asTypeList();

    public List<String> asTypeNames();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty
    extends FilterableList.Empty<AnnotationDescription, AnnotationList>
    implements AnnotationList {
        public static List<AnnotationList> asList(int length) {
            ArrayList<AnnotationList> result = new ArrayList<AnnotationList>(length);
            for (int i = 0; i < length; ++i) {
                result.add(new Empty());
            }
            return result;
        }

        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
            return false;
        }

        @Override
        public boolean isAnnotationPresent(TypeDescription annotationType) {
            return false;
        }

        @Override
        @AlwaysNull
        public <T extends Annotation> AnnotationDescription.Loadable<T> ofType(Class<T> annotationType) {
            return AnnotationDescription.UNDEFINED;
        }

        @Override
        @AlwaysNull
        public AnnotationDescription ofType(TypeDescription annotationType) {
            return AnnotationDescription.UNDEFINED;
        }

        @Override
        public AnnotationList inherited(Set<? extends TypeDescription> ignoredTypes) {
            return this;
        }

        @Override
        public AnnotationList visibility(ElementMatcher<? super RetentionPolicy> matcher) {
            return this;
        }

        @Override
        public TypeList asTypeList() {
            return new TypeList.Empty();
        }

        @Override
        public List<String> asTypeNames() {
            return Collections.emptyList();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Explicit
    extends AbstractBase {
        private final List<? extends AnnotationDescription> annotationDescriptions;

        public Explicit(AnnotationDescription ... annotationDescription) {
            this(Arrays.asList(annotationDescription));
        }

        public Explicit(List<? extends AnnotationDescription> annotationDescriptions) {
            this.annotationDescriptions = annotationDescriptions;
        }

        public static List<AnnotationList> asList(List<? extends List<? extends AnnotationDescription>> annotations) {
            ArrayList<AnnotationList> result = new ArrayList<AnnotationList>(annotations.size());
            for (List<? extends AnnotationDescription> list : annotations) {
                result.add(new Explicit(list));
            }
            return result;
        }

        @Override
        public AnnotationDescription get(int index) {
            return this.annotationDescriptions.get(index);
        }

        @Override
        public int size() {
            return this.annotationDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedAnnotations
    extends AbstractBase {
        private final List<? extends Annotation> annotations;

        public ForLoadedAnnotations(Annotation ... annotation) {
            this(Arrays.asList(annotation));
        }

        public ForLoadedAnnotations(List<? extends Annotation> annotations) {
            this.annotations = annotations;
        }

        public static List<AnnotationList> asList(Annotation[][] annotations) {
            ArrayList<AnnotationList> result = new ArrayList<AnnotationList>(annotations.length);
            for (Annotation[] annotation : annotations) {
                result.add(new ForLoadedAnnotations(annotation));
            }
            return result;
        }

        @Override
        public AnnotationDescription get(int index) {
            return AnnotationDescription.ForLoadedAnnotation.of(this.annotations.get(index));
        }

        @Override
        public int size() {
            return this.annotations.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    extends FilterableList.AbstractBase<AnnotationDescription, AnnotationList>
    implements AnnotationList {
        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
            for (AnnotationDescription annotation : this) {
                if (!annotation.getAnnotationType().represents(annotationType)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean isAnnotationPresent(TypeDescription annotationType) {
            for (AnnotationDescription annotation : this) {
                if (!annotation.getAnnotationType().equals(annotationType)) continue;
                return true;
            }
            return false;
        }

        @Override
        @MaybeNull
        public <T extends Annotation> AnnotationDescription.Loadable<T> ofType(Class<T> annotationType) {
            for (AnnotationDescription annotation : this) {
                if (!annotation.getAnnotationType().represents(annotationType)) continue;
                return annotation.prepare(annotationType);
            }
            return AnnotationDescription.UNDEFINED;
        }

        @Override
        @MaybeNull
        public AnnotationDescription ofType(TypeDescription annotationType) {
            for (AnnotationDescription annotation : this) {
                if (!annotation.getAnnotationType().equals(annotationType)) continue;
                return annotation;
            }
            return AnnotationDescription.UNDEFINED;
        }

        @Override
        public AnnotationList inherited(Set<? extends TypeDescription> ignoredTypes) {
            ArrayList<AnnotationDescription> inherited = new ArrayList<AnnotationDescription>(this.size());
            for (AnnotationDescription annotation : this) {
                if (ignoredTypes.contains(annotation.getAnnotationType()) || !annotation.isInherited()) continue;
                inherited.add(annotation);
            }
            return this.wrap(inherited);
        }

        @Override
        public AnnotationList visibility(ElementMatcher<? super RetentionPolicy> matcher) {
            ArrayList<AnnotationDescription> annotationDescriptions = new ArrayList<AnnotationDescription>(this.size());
            for (AnnotationDescription annotation : this) {
                if (!matcher.matches(annotation.getRetention())) continue;
                annotationDescriptions.add(annotation);
            }
            return this.wrap(annotationDescriptions);
        }

        @Override
        public TypeList asTypeList() {
            ArrayList<TypeDescription> annotationTypes = new ArrayList<TypeDescription>(this.size());
            for (AnnotationDescription annotation : this) {
                annotationTypes.add(annotation.getAnnotationType());
            }
            return new TypeList.Explicit(annotationTypes);
        }

        @Override
        public List<String> asTypeNames() {
            ArrayList<String> typeNames = new ArrayList<String>(this.size());
            for (AnnotationDescription annotation : this) {
                typeNames.add(annotation.getAnnotationType().getName());
            }
            return typeNames;
        }

        @Override
        protected AnnotationList wrap(List<AnnotationDescription> values) {
            return new Explicit(values);
        }
    }
}


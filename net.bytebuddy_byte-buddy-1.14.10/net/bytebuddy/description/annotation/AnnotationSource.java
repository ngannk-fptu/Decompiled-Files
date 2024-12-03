/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.annotation;

import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface AnnotationSource {
    public AnnotationList getDeclaredAnnotations();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Explicit
    implements AnnotationSource {
        private final List<? extends AnnotationDescription> annotations;

        public Explicit(AnnotationDescription ... annotation) {
            this(Arrays.asList(annotation));
        }

        public Explicit(List<? extends AnnotationDescription> annotations) {
            this.annotations = annotations;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(this.annotations);
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ((Object)this.annotations).equals(((Explicit)object).annotations);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.annotations).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Empty implements AnnotationSource
    {
        INSTANCE;


        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Empty();
        }
    }
}


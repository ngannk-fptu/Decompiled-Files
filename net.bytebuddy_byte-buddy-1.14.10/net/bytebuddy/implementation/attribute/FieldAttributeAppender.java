/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.attribute.AnnotationAppender;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface FieldAttributeAppender {
    public void apply(FieldVisitor var1, FieldDescription var2, AnnotationValueFilter var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements FieldAttributeAppender {
        private final List<FieldAttributeAppender> fieldAttributeAppenders = new ArrayList<FieldAttributeAppender>();

        public Compound(FieldAttributeAppender ... fieldAttributeAppender) {
            this(Arrays.asList(fieldAttributeAppender));
        }

        public Compound(List<? extends FieldAttributeAppender> fieldAttributeAppenders) {
            for (FieldAttributeAppender fieldAttributeAppender : fieldAttributeAppenders) {
                if (fieldAttributeAppender instanceof Compound) {
                    this.fieldAttributeAppenders.addAll(((Compound)fieldAttributeAppender).fieldAttributeAppenders);
                    continue;
                }
                if (fieldAttributeAppender instanceof NoOp) continue;
                this.fieldAttributeAppenders.add(fieldAttributeAppender);
            }
        }

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            for (FieldAttributeAppender fieldAttributeAppender : this.fieldAttributeAppenders) {
                fieldAttributeAppender.apply(fieldVisitor, fieldDescription, annotationValueFilter);
            }
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
            return ((Object)this.fieldAttributeAppenders).equals(((Compound)object).fieldAttributeAppenders);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.fieldAttributeAppenders).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Explicit
    implements FieldAttributeAppender,
    Factory {
        private final List<? extends AnnotationDescription> annotations;

        public Explicit(List<? extends AnnotationDescription> annotations) {
            this.annotations = annotations;
        }

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender appender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnField(fieldVisitor));
            for (AnnotationDescription annotationDescription : this.annotations) {
                appender = appender.append(annotationDescription, annotationValueFilter);
            }
        }

        @Override
        public FieldAttributeAppender make(TypeDescription typeDescription) {
            return this;
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
    public static enum ForInstrumentedField implements FieldAttributeAppender,
    Factory
    {
        INSTANCE;


        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnField(fieldVisitor));
            annotationAppender = fieldDescription.getType().accept(AnnotationAppender.ForTypeAnnotations.ofFieldType(annotationAppender, annotationValueFilter));
            for (AnnotationDescription annotation : fieldDescription.getDeclaredAnnotations()) {
                annotationAppender = annotationAppender.append(annotation, annotationValueFilter);
            }
        }

        @Override
        public FieldAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }
    }

    public static interface Factory {
        public FieldAttributeAppender make(TypeDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements Factory {
            private final List<Factory> factories = new ArrayList<Factory>();

            public Compound(Factory ... factory) {
                this(Arrays.asList(factory));
            }

            public Compound(List<? extends Factory> factories) {
                for (Factory factory : factories) {
                    if (factory instanceof Compound) {
                        this.factories.addAll(((Compound)factory).factories);
                        continue;
                    }
                    if (factory instanceof NoOp) continue;
                    this.factories.add(factory);
                }
            }

            @Override
            public FieldAttributeAppender make(TypeDescription typeDescription) {
                ArrayList<FieldAttributeAppender> fieldAttributeAppenders = new ArrayList<FieldAttributeAppender>(this.factories.size());
                for (Factory factory : this.factories) {
                    fieldAttributeAppenders.add(factory.make(typeDescription));
                }
                return new net.bytebuddy.implementation.attribute.FieldAttributeAppender$Compound(fieldAttributeAppenders);
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
                return ((Object)this.factories).equals(((Compound)object).factories);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.factories).hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements FieldAttributeAppender,
    Factory
    {
        INSTANCE;


        @Override
        public FieldAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.attribute.AnnotationAppender;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface RecordComponentAttributeAppender {
    public void apply(RecordComponentVisitor var1, RecordComponentDescription var2, AnnotationValueFilter var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements RecordComponentAttributeAppender {
        private final List<RecordComponentAttributeAppender> recordComponentAttributeAppenders = new ArrayList<RecordComponentAttributeAppender>();

        public Compound(RecordComponentAttributeAppender ... recordComponentAttributeAppender) {
            this(Arrays.asList(recordComponentAttributeAppender));
        }

        public Compound(List<? extends RecordComponentAttributeAppender> recordComponentAttributeAppenders) {
            for (RecordComponentAttributeAppender recordComponentAttributeAppender : recordComponentAttributeAppenders) {
                if (recordComponentAttributeAppender instanceof Compound) {
                    this.recordComponentAttributeAppenders.addAll(((Compound)recordComponentAttributeAppender).recordComponentAttributeAppenders);
                    continue;
                }
                if (recordComponentAttributeAppender instanceof NoOp) continue;
                this.recordComponentAttributeAppenders.add(recordComponentAttributeAppender);
            }
        }

        @Override
        public void apply(RecordComponentVisitor recordComponentVisitor, RecordComponentDescription recordComponentDescription, AnnotationValueFilter annotationValueFilter) {
            for (RecordComponentAttributeAppender recordComponentAttributeAppender : this.recordComponentAttributeAppenders) {
                recordComponentAttributeAppender.apply(recordComponentVisitor, recordComponentDescription, annotationValueFilter);
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
            return ((Object)this.recordComponentAttributeAppenders).equals(((Compound)object).recordComponentAttributeAppenders);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.recordComponentAttributeAppenders).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Explicit
    implements RecordComponentAttributeAppender,
    Factory {
        private final List<? extends AnnotationDescription> annotations;

        public Explicit(List<? extends AnnotationDescription> annotations) {
            this.annotations = annotations;
        }

        @Override
        public void apply(RecordComponentVisitor recordComponentVisitor, RecordComponentDescription recordComponentDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender appender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnRecordComponent(recordComponentVisitor));
            for (AnnotationDescription annotationDescription : this.annotations) {
                appender = appender.append(annotationDescription, annotationValueFilter);
            }
        }

        @Override
        public RecordComponentAttributeAppender make(TypeDescription typeDescription) {
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
    public static enum ForInstrumentedRecordComponent implements RecordComponentAttributeAppender,
    Factory
    {
        INSTANCE;


        @Override
        public void apply(RecordComponentVisitor recordComponentVisitor, RecordComponentDescription recordComponentDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnRecordComponent(recordComponentVisitor));
            annotationAppender = recordComponentDescription.getType().accept(AnnotationAppender.ForTypeAnnotations.ofFieldType(annotationAppender, annotationValueFilter));
            for (AnnotationDescription annotation : recordComponentDescription.getDeclaredAnnotations()) {
                annotationAppender = annotationAppender.append(annotation, annotationValueFilter);
            }
        }

        @Override
        public RecordComponentAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }
    }

    public static interface Factory {
        public RecordComponentAttributeAppender make(TypeDescription var1);

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
            public RecordComponentAttributeAppender make(TypeDescription typeDescription) {
                ArrayList<RecordComponentAttributeAppender> recordComponentAttributeAppenders = new ArrayList<RecordComponentAttributeAppender>(this.factories.size());
                for (Factory factory : this.factories) {
                    recordComponentAttributeAppenders.add(factory.make(typeDescription));
                }
                return new net.bytebuddy.implementation.attribute.RecordComponentAttributeAppender$Compound(recordComponentAttributeAppenders);
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
    public static enum NoOp implements RecordComponentAttributeAppender,
    Factory
    {
        INSTANCE;


        @Override
        public RecordComponentAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public void apply(RecordComponentVisitor recordComponentVisitor, RecordComponentDescription recordComponentDescription, AnnotationValueFilter annotationValueFilter) {
        }
    }
}


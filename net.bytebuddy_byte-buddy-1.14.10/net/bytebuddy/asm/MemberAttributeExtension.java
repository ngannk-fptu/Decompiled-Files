/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.asm;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.FieldAttributeAppender;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public abstract class MemberAttributeExtension<T> {
    protected final AnnotationValueFilter.Factory annotationValueFilterFactory;
    protected final T attributeAppenderFactory;

    protected MemberAttributeExtension(AnnotationValueFilter.Factory annotationValueFilterFactory, T attributeAppenderFactory) {
        this.annotationValueFilterFactory = annotationValueFilterFactory;
        this.attributeAppenderFactory = attributeAppenderFactory;
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
        if (!this.annotationValueFilterFactory.equals(((MemberAttributeExtension)object).annotationValueFilterFactory)) {
            return false;
        }
        return this.attributeAppenderFactory.equals(((MemberAttributeExtension)object).attributeAppenderFactory);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.annotationValueFilterFactory.hashCode()) * 31 + this.attributeAppenderFactory.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForMethod
    extends MemberAttributeExtension<MethodAttributeAppender.Factory>
    implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
        public ForMethod() {
            this(AnnotationValueFilter.Default.APPEND_DEFAULTS);
        }

        public ForMethod(AnnotationValueFilter.Factory annotationValueFilterFactory) {
            this(annotationValueFilterFactory, MethodAttributeAppender.NoOp.INSTANCE);
        }

        protected ForMethod(AnnotationValueFilter.Factory annotationValueFilterFactory, MethodAttributeAppender.Factory attributeAppenderFactory) {
            super(annotationValueFilterFactory, attributeAppenderFactory);
        }

        public ForMethod annotateMethod(Annotation ... annotation) {
            return this.annotateMethod(Arrays.asList(annotation));
        }

        public ForMethod annotateMethod(List<? extends Annotation> annotations) {
            return this.annotateMethod(new AnnotationList.ForLoadedAnnotations(annotations));
        }

        public ForMethod annotateMethod(AnnotationDescription ... annotation) {
            return this.annotateMethod((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
        }

        public ForMethod annotateMethod(Collection<? extends AnnotationDescription> annotations) {
            return this.attribute(new MethodAttributeAppender.Explicit(new ArrayList<AnnotationDescription>(annotations)));
        }

        public ForMethod annotateParameter(int index, Annotation ... annotation) {
            return this.annotateParameter(index, Arrays.asList(annotation));
        }

        public ForMethod annotateParameter(int index, List<? extends Annotation> annotations) {
            return this.annotateParameter(index, new AnnotationList.ForLoadedAnnotations(annotations));
        }

        public ForMethod annotateParameter(int index, AnnotationDescription ... annotation) {
            return this.annotateParameter(index, (Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
        }

        public ForMethod annotateParameter(int index, Collection<? extends AnnotationDescription> annotations) {
            if (index < 0) {
                throw new IllegalArgumentException("Parameter index cannot be negative: " + index);
            }
            return this.attribute(new MethodAttributeAppender.Explicit(index, new ArrayList<AnnotationDescription>(annotations)));
        }

        public ForMethod attribute(MethodAttributeAppender.Factory attributeAppenderFactory) {
            return new ForMethod(this.annotationValueFilterFactory, new MethodAttributeAppender.Factory.Compound((MethodAttributeAppender.Factory)this.attributeAppenderFactory, attributeAppenderFactory));
        }

        @Override
        public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
            return new AttributeAppendingMethodVisitor(methodVisitor, instrumentedMethod, ((MethodAttributeAppender.Factory)this.attributeAppenderFactory).make(instrumentedType), this.annotationValueFilterFactory.on(instrumentedMethod));
        }

        public AsmVisitorWrapper on(ElementMatcher<? super MethodDescription> matcher) {
            return new AsmVisitorWrapper.ForDeclaredMethods().invokable(matcher, this);
        }

        private static class AttributeAppendingMethodVisitor
        extends MethodVisitor {
            private final MethodDescription methodDescription;
            private final MethodAttributeAppender methodAttributeAppender;
            private final AnnotationValueFilter annotationValueFilter;
            private boolean applicable;

            private AttributeAppendingMethodVisitor(MethodVisitor methodVisitor, MethodDescription methodDescription, MethodAttributeAppender methodAttributeAppender, AnnotationValueFilter annotationValueFilter) {
                super(OpenedClassReader.ASM_API, methodVisitor);
                this.methodDescription = methodDescription;
                this.methodAttributeAppender = methodAttributeAppender;
                this.annotationValueFilter = annotationValueFilter;
                this.applicable = true;
            }

            public void visitCode() {
                if (this.applicable) {
                    this.methodAttributeAppender.apply(this.mv, this.methodDescription, this.annotationValueFilter);
                    this.applicable = false;
                }
                super.visitCode();
            }

            public void visitEnd() {
                if (this.applicable) {
                    this.methodAttributeAppender.apply(this.mv, this.methodDescription, this.annotationValueFilter);
                    this.applicable = false;
                }
                super.visitEnd();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForField
    extends MemberAttributeExtension<FieldAttributeAppender.Factory>
    implements AsmVisitorWrapper.ForDeclaredFields.FieldVisitorWrapper {
        public ForField() {
            this(AnnotationValueFilter.Default.APPEND_DEFAULTS);
        }

        public ForField(AnnotationValueFilter.Factory annotationValueFilterFactory) {
            this(annotationValueFilterFactory, FieldAttributeAppender.NoOp.INSTANCE);
        }

        protected ForField(AnnotationValueFilter.Factory annotationValueFilterFactory, FieldAttributeAppender.Factory attributeAppenderFactory) {
            super(annotationValueFilterFactory, attributeAppenderFactory);
        }

        public ForField annotate(Annotation ... annotation) {
            return this.annotate(Arrays.asList(annotation));
        }

        public ForField annotate(List<? extends Annotation> annotations) {
            return this.annotate(new AnnotationList.ForLoadedAnnotations(annotations));
        }

        public ForField annotate(AnnotationDescription ... annotation) {
            return this.annotate((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
        }

        public ForField annotate(Collection<? extends AnnotationDescription> annotations) {
            return this.attribute(new FieldAttributeAppender.Explicit(new ArrayList<AnnotationDescription>(annotations)));
        }

        public ForField attribute(FieldAttributeAppender.Factory attributeAppenderFactory) {
            return new ForField(this.annotationValueFilterFactory, new FieldAttributeAppender.Factory.Compound((FieldAttributeAppender.Factory)this.attributeAppenderFactory, attributeAppenderFactory));
        }

        @Override
        public FieldVisitor wrap(TypeDescription instrumentedType, FieldDescription.InDefinedShape fieldDescription, FieldVisitor fieldVisitor) {
            return new FieldAttributeVisitor(fieldVisitor, fieldDescription, ((FieldAttributeAppender.Factory)this.attributeAppenderFactory).make(instrumentedType), this.annotationValueFilterFactory.on(fieldDescription));
        }

        public AsmVisitorWrapper on(ElementMatcher<? super FieldDescription.InDefinedShape> matcher) {
            return new AsmVisitorWrapper.ForDeclaredFields().field(matcher, this);
        }

        private static class FieldAttributeVisitor
        extends FieldVisitor {
            private final FieldDescription fieldDescription;
            private final FieldAttributeAppender fieldAttributeAppender;
            private final AnnotationValueFilter annotationValueFilter;

            private FieldAttributeVisitor(FieldVisitor fieldVisitor, FieldDescription fieldDescription, FieldAttributeAppender fieldAttributeAppender, AnnotationValueFilter annotationValueFilter) {
                super(OpenedClassReader.ASM_API, fieldVisitor);
                this.fieldDescription = fieldDescription;
                this.fieldAttributeAppender = fieldAttributeAppender;
                this.annotationValueFilter = annotationValueFilter;
            }

            public void visitEnd() {
                this.fieldAttributeAppender.apply(this.fv, this.fieldDescription, this.annotationValueFilter);
                super.visitEnd();
            }
        }
    }
}


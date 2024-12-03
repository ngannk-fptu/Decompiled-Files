/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.attribute.AnnotationAppender;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface MethodAttributeAppender {
    public void apply(MethodVisitor var1, MethodDescription var2, AnnotationValueFilter var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements MethodAttributeAppender {
        private final List<MethodAttributeAppender> methodAttributeAppenders = new ArrayList<MethodAttributeAppender>();

        public Compound(MethodAttributeAppender ... methodAttributeAppender) {
            this(Arrays.asList(methodAttributeAppender));
        }

        public Compound(List<? extends MethodAttributeAppender> methodAttributeAppenders) {
            for (MethodAttributeAppender methodAttributeAppender : methodAttributeAppenders) {
                if (methodAttributeAppender instanceof Compound) {
                    this.methodAttributeAppenders.addAll(((Compound)methodAttributeAppender).methodAttributeAppenders);
                    continue;
                }
                if (methodAttributeAppender instanceof NoOp) continue;
                this.methodAttributeAppenders.add(methodAttributeAppender);
            }
        }

        @Override
        public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
            for (MethodAttributeAppender methodAttributeAppender : this.methodAttributeAppenders) {
                methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
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
            return ((Object)this.methodAttributeAppenders).equals(((Compound)object).methodAttributeAppenders);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.methodAttributeAppenders).hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForReceiverType
    implements MethodAttributeAppender,
    Factory {
        private final TypeDescription.Generic receiverType;

        public ForReceiverType(TypeDescription.Generic receiverType) {
            this.receiverType = receiverType;
        }

        public MethodAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
            this.receiverType.accept(AnnotationAppender.ForTypeAnnotations.ofReceiverType(new AnnotationAppender.Default(new AnnotationAppender.Target.OnMethod(methodVisitor)), annotationValueFilter));
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
            return this.receiverType.equals(((ForReceiverType)object).receiverType);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.receiverType.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Explicit
    implements MethodAttributeAppender,
    Factory {
        private final Target target;
        private final List<? extends AnnotationDescription> annotations;

        public Explicit(int parameterIndex, List<? extends AnnotationDescription> annotations) {
            this(new Target.OnMethodParameter(parameterIndex), annotations);
        }

        public Explicit(List<? extends AnnotationDescription> annotations) {
            this(Target.OnMethod.INSTANCE, annotations);
        }

        protected Explicit(Target target, List<? extends AnnotationDescription> annotations) {
            this.target = target;
            this.annotations = annotations;
        }

        public static Factory of(MethodDescription methodDescription) {
            return new Factory.Compound(Explicit.ofMethodAnnotations(methodDescription), Explicit.ofParameterAnnotations(methodDescription));
        }

        public static Factory ofMethodAnnotations(MethodDescription methodDescription) {
            return new Explicit(methodDescription.getDeclaredAnnotations());
        }

        public static Factory ofParameterAnnotations(MethodDescription methodDescription) {
            ParameterList<?> parameters = methodDescription.getParameters();
            ArrayList<Explicit> factories = new ArrayList<Explicit>(parameters.size());
            for (ParameterDescription parameter : parameters) {
                factories.add(new Explicit(parameter.getIndex(), (List<? extends AnnotationDescription>)parameter.getDeclaredAnnotations()));
            }
            return new Factory.Compound(factories);
        }

        @Override
        public MethodAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender appender = new AnnotationAppender.Default(this.target.make(methodVisitor, methodDescription));
            for (AnnotationDescription annotationDescription : this.annotations) {
                appender = appender.append(annotationDescription, annotationValueFilter);
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
            if (!this.target.equals(((Explicit)object).target)) {
                return false;
            }
            return ((Object)this.annotations).equals(((Explicit)object).annotations);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + ((Object)this.annotations).hashCode();
        }

        protected static interface Target {
            public AnnotationAppender.Target make(MethodVisitor var1, MethodDescription var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class OnMethodParameter
            implements Target {
                private final int parameterIndex;

                protected OnMethodParameter(int parameterIndex) {
                    this.parameterIndex = parameterIndex;
                }

                public AnnotationAppender.Target make(MethodVisitor methodVisitor, MethodDescription methodDescription) {
                    if (this.parameterIndex >= methodDescription.getParameters().size()) {
                        throw new IllegalArgumentException("Method " + methodDescription + " has less then " + this.parameterIndex + " parameters");
                    }
                    return new AnnotationAppender.Target.OnMethodParameter(methodVisitor, this.parameterIndex);
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
                    return this.parameterIndex == ((OnMethodParameter)object).parameterIndex;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.parameterIndex;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum OnMethod implements Target
            {
                INSTANCE;


                @Override
                public AnnotationAppender.Target make(MethodVisitor methodVisitor, MethodDescription methodDescription) {
                    return new AnnotationAppender.Target.OnMethod(methodVisitor);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ForInstrumentedMethod implements MethodAttributeAppender,
    Factory
    {
        EXCLUDING_RECEIVER{

            protected AnnotationAppender appendReceiver(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, MethodDescription methodDescription) {
                return annotationAppender;
            }
        }
        ,
        INCLUDING_RECEIVER{

            protected AnnotationAppender appendReceiver(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, MethodDescription methodDescription) {
                TypeDescription.Generic receiverType = methodDescription.getReceiverType();
                return receiverType == null ? annotationAppender : receiverType.accept(AnnotationAppender.ForTypeAnnotations.ofReceiverType(annotationAppender, annotationValueFilter));
            }
        };


        @Override
        public MethodAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnMethod(methodVisitor));
            annotationAppender = methodDescription.getReturnType().accept(AnnotationAppender.ForTypeAnnotations.ofMethodReturnType(annotationAppender, annotationValueFilter));
            annotationAppender = AnnotationAppender.ForTypeAnnotations.ofTypeVariable(annotationAppender, annotationValueFilter, false, methodDescription.getTypeVariables());
            for (AnnotationDescription annotation : (AnnotationList)methodDescription.getDeclaredAnnotations().filter(ElementMatchers.not(ElementMatchers.annotationType(ElementMatchers.nameStartsWith("jdk.internal."))))) {
                annotationAppender = annotationAppender.append(annotation, annotationValueFilter);
            }
            for (ParameterDescription parameterDescription : methodDescription.getParameters()) {
                AnnotationAppender parameterAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnMethodParameter(methodVisitor, parameterDescription.getIndex()));
                parameterAppender = parameterDescription.getType().accept(AnnotationAppender.ForTypeAnnotations.ofMethodParameterType(parameterAppender, annotationValueFilter, parameterDescription.getIndex()));
                for (AnnotationDescription annotation : parameterDescription.getDeclaredAnnotations()) {
                    parameterAppender = parameterAppender.append(annotation, annotationValueFilter);
                }
            }
            annotationAppender = this.appendReceiver(annotationAppender, annotationValueFilter, methodDescription);
            int exceptionTypeIndex = 0;
            for (TypeDescription.Generic exceptionType : methodDescription.getExceptionTypes()) {
                annotationAppender = exceptionType.accept(AnnotationAppender.ForTypeAnnotations.ofExceptionType(annotationAppender, annotationValueFilter, exceptionTypeIndex++));
            }
        }

        protected abstract AnnotationAppender appendReceiver(AnnotationAppender var1, AnnotationValueFilter var2, MethodDescription var3);
    }

    public static interface Factory {
        public MethodAttributeAppender make(TypeDescription var1);

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
            public MethodAttributeAppender make(TypeDescription typeDescription) {
                ArrayList<MethodAttributeAppender> methodAttributeAppenders = new ArrayList<MethodAttributeAppender>(this.factories.size());
                for (Factory factory : this.factories) {
                    methodAttributeAppenders.add(factory.make(typeDescription));
                }
                return new net.bytebuddy.implementation.attribute.MethodAttributeAppender$Compound(methodAttributeAppenders);
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
    public static enum NoOp implements MethodAttributeAppender,
    Factory
    {
        INSTANCE;


        @Override
        public MethodAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
        }
    }
}


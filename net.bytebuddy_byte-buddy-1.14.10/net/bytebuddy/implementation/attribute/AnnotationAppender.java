/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.attribute;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.jar.asm.TypeReference;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface AnnotationAppender {
    @AlwaysNull
    public static final String NO_NAME = null;

    public AnnotationAppender append(AnnotationDescription var1, AnnotationValueFilter var2);

    public AnnotationAppender append(AnnotationDescription var1, AnnotationValueFilter var2, int var3, String var4);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForTypeAnnotations
    implements TypeDescription.Generic.Visitor<AnnotationAppender> {
        public static final boolean VARIABLE_ON_TYPE = true;
        public static final boolean VARIABLE_ON_INVOKEABLE = false;
        private static final String EMPTY_TYPE_PATH = "";
        private static final char COMPONENT_TYPE_PATH = '[';
        private static final char WILDCARD_TYPE_PATH = '*';
        private static final char INNER_CLASS_PATH = '.';
        private static final char INDEXED_TYPE_DELIMITER = ';';
        private static final int SUPER_CLASS_INDEX = -1;
        private final AnnotationAppender annotationAppender;
        private final AnnotationValueFilter annotationValueFilter;
        private final int typeReference;
        private final String typePath;

        protected ForTypeAnnotations(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, TypeReference typeReference) {
            this(annotationAppender, annotationValueFilter, typeReference.getValue(), EMPTY_TYPE_PATH);
        }

        protected ForTypeAnnotations(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, int typeReference, String typePath) {
            this.annotationAppender = annotationAppender;
            this.annotationValueFilter = annotationValueFilter;
            this.typeReference = typeReference;
            this.typePath = typePath;
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofSuperClass(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newSuperTypeReference(-1));
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofInterfaceType(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, int index) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newSuperTypeReference(index));
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofFieldType(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newTypeReference(19));
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofMethodReturnType(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newTypeReference(20));
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofMethodParameterType(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, int index) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newFormalParameterReference(index));
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofExceptionType(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, int index) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newExceptionReference(index));
        }

        public static TypeDescription.Generic.Visitor<AnnotationAppender> ofReceiverType(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter) {
            return new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newTypeReference(21));
        }

        public static AnnotationAppender ofTypeVariable(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, boolean variableOnType, List<? extends TypeDescription.Generic> typeVariables) {
            return ForTypeAnnotations.ofTypeVariable(annotationAppender, annotationValueFilter, variableOnType, 0, typeVariables);
        }

        public static AnnotationAppender ofTypeVariable(AnnotationAppender annotationAppender, AnnotationValueFilter annotationValueFilter, boolean variableOnType, int subListIndex, List<? extends TypeDescription.Generic> typeVariables) {
            int variableBoundBaseBase;
            int variableBaseReference;
            int typeVariableIndex = subListIndex;
            if (variableOnType) {
                variableBaseReference = 0;
                variableBoundBaseBase = 17;
            } else {
                variableBaseReference = 1;
                variableBoundBaseBase = 18;
            }
            for (TypeDescription.Generic generic : typeVariables.subList(subListIndex, typeVariables.size())) {
                int typeReference = TypeReference.newTypeParameterReference(variableBaseReference, typeVariableIndex).getValue();
                for (AnnotationDescription annotationDescription : generic.getDeclaredAnnotations()) {
                    annotationAppender = annotationAppender.append(annotationDescription, annotationValueFilter, typeReference, EMPTY_TYPE_PATH);
                }
                int boundIndex = !((TypeDescription.Generic)generic.getUpperBounds().get(0)).getSort().isTypeVariable() && ((TypeDescription.Generic)generic.getUpperBounds().get(0)).isInterface() ? 1 : 0;
                for (TypeDescription.Generic typeBound : generic.getUpperBounds()) {
                    annotationAppender = typeBound.accept(new ForTypeAnnotations(annotationAppender, annotationValueFilter, TypeReference.newTypeParameterBoundReference(variableBoundBaseBase, typeVariableIndex, boundIndex++)));
                }
                ++typeVariableIndex;
            }
            return annotationAppender;
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public AnnotationAppender onGenericArray(TypeDescription.Generic genericArray) {
            return genericArray.getComponentType().accept(new ForTypeAnnotations(this.apply(genericArray, this.typePath), this.annotationValueFilter, this.typeReference, this.typePath + '['));
        }

        @Override
        public AnnotationAppender onWildcard(TypeDescription.Generic wildcard) {
            TypeList.Generic lowerBounds = wildcard.getLowerBounds();
            return (lowerBounds.isEmpty() ? (TypeDescription.Generic)wildcard.getUpperBounds().getOnly() : (TypeDescription.Generic)lowerBounds.getOnly()).accept(new ForTypeAnnotations(this.apply(wildcard, this.typePath), this.annotationValueFilter, this.typeReference, this.typePath + '*'));
        }

        @Override
        public AnnotationAppender onParameterizedType(TypeDescription.Generic parameterizedType) {
            StringBuilder typePath = new StringBuilder(this.typePath);
            for (int index = 0; index < parameterizedType.asErasure().getInnerClassCount(); ++index) {
                typePath = typePath.append('.');
            }
            AnnotationAppender annotationAppender = this.apply(parameterizedType, typePath.toString());
            TypeDescription.Generic ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
                annotationAppender = ownerType.accept(new ForTypeAnnotations(annotationAppender, this.annotationValueFilter, this.typeReference, this.typePath));
            }
            int index = 0;
            for (TypeDescription.Generic typeArgument : parameterizedType.getTypeArguments()) {
                annotationAppender = typeArgument.accept(new ForTypeAnnotations(annotationAppender, this.annotationValueFilter, this.typeReference, typePath.toString() + index++ + ';'));
            }
            return annotationAppender;
        }

        @Override
        public AnnotationAppender onTypeVariable(TypeDescription.Generic typeVariable) {
            return this.apply(typeVariable, this.typePath);
        }

        @Override
        public AnnotationAppender onNonGenericType(TypeDescription.Generic typeDescription) {
            StringBuilder typePath = new StringBuilder(this.typePath);
            for (int index = 0; index < typeDescription.asErasure().getInnerClassCount(); ++index) {
                typePath = typePath.append('.');
            }
            AnnotationAppender annotationAppender = this.apply(typeDescription, typePath.toString());
            TypeDescription.Generic componentType = typeDescription.getComponentType();
            if (componentType != null) {
                annotationAppender = componentType.accept(new ForTypeAnnotations(annotationAppender, this.annotationValueFilter, this.typeReference, this.typePath + '['));
            }
            return annotationAppender;
        }

        private AnnotationAppender apply(TypeDescription.Generic typeDescription, String typePath) {
            AnnotationAppender annotationAppender = this.annotationAppender;
            for (AnnotationDescription annotationDescription : typeDescription.getDeclaredAnnotations()) {
                annotationAppender = annotationAppender.append(annotationDescription, this.annotationValueFilter, this.typeReference, typePath);
            }
            return annotationAppender;
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
            if (this.typeReference != ((ForTypeAnnotations)object).typeReference) {
                return false;
            }
            if (!this.typePath.equals(((ForTypeAnnotations)object).typePath)) {
                return false;
            }
            if (!this.annotationAppender.equals(((ForTypeAnnotations)object).annotationAppender)) {
                return false;
            }
            return this.annotationValueFilter.equals(((ForTypeAnnotations)object).annotationValueFilter);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.annotationAppender.hashCode()) * 31 + this.annotationValueFilter.hashCode()) * 31 + this.typeReference) * 31 + this.typePath.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements AnnotationAppender {
        private final Target target;

        public Default(Target target) {
            this.target = target;
        }

        private static void handle(AnnotationVisitor annotationVisitor, AnnotationDescription annotation, AnnotationValueFilter annotationValueFilter) {
            for (MethodDescription.InDefinedShape methodDescription : annotation.getAnnotationType().getDeclaredMethods()) {
                if (!annotationValueFilter.isRelevant(annotation, methodDescription)) continue;
                Default.apply(annotationVisitor, methodDescription.getReturnType().asErasure(), methodDescription.getName(), annotation.getValue(methodDescription).resolve());
            }
            annotationVisitor.visitEnd();
        }

        public static void apply(AnnotationVisitor annotationVisitor, TypeDescription valueType, @MaybeNull String name, Object value) {
            if (valueType.isArray()) {
                AnnotationVisitor arrayVisitor = annotationVisitor.visitArray(name);
                int length = Array.getLength(value);
                TypeDescription componentType = valueType.getComponentType();
                for (int index = 0; index < length; ++index) {
                    Default.apply(arrayVisitor, componentType, NO_NAME, Array.get(value, index));
                }
                arrayVisitor.visitEnd();
            } else if (valueType.isAnnotation()) {
                Default.handle(annotationVisitor.visitAnnotation(name, valueType.getDescriptor()), (AnnotationDescription)value, AnnotationValueFilter.Default.APPEND_DEFAULTS);
            } else if (valueType.isEnum()) {
                annotationVisitor.visitEnum(name, valueType.getDescriptor(), ((EnumerationDescription)value).getValue());
            } else if (valueType.represents((Type)((Object)Class.class))) {
                annotationVisitor.visit(name, net.bytebuddy.jar.asm.Type.getType(((TypeDescription)value).getDescriptor()));
            } else {
                annotationVisitor.visit(name, value);
            }
        }

        public AnnotationAppender append(AnnotationDescription annotationDescription, AnnotationValueFilter annotationValueFilter) {
            switch (annotationDescription.getRetention()) {
                case RUNTIME: {
                    this.doAppend(annotationDescription, true, annotationValueFilter);
                    break;
                }
                case CLASS: {
                    this.doAppend(annotationDescription, false, annotationValueFilter);
                    break;
                }
                case SOURCE: {
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected retention policy: " + (Object)((Object)annotationDescription.getRetention()));
                }
            }
            return this;
        }

        private void doAppend(AnnotationDescription annotation, boolean visible, AnnotationValueFilter annotationValueFilter) {
            AnnotationVisitor annotationVisitor = this.target.visit(annotation.getAnnotationType().getDescriptor(), visible);
            if (annotationVisitor != null) {
                Default.handle(annotationVisitor, annotation, annotationValueFilter);
            }
        }

        public AnnotationAppender append(AnnotationDescription annotationDescription, AnnotationValueFilter annotationValueFilter, int typeReference, String typePath) {
            switch (annotationDescription.getRetention()) {
                case RUNTIME: {
                    this.doAppend(annotationDescription, true, annotationValueFilter, typeReference, typePath);
                    break;
                }
                case CLASS: {
                    this.doAppend(annotationDescription, false, annotationValueFilter, typeReference, typePath);
                    break;
                }
                case SOURCE: {
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected retention policy: " + (Object)((Object)annotationDescription.getRetention()));
                }
            }
            return this;
        }

        private void doAppend(AnnotationDescription annotation, boolean visible, AnnotationValueFilter annotationValueFilter, int typeReference, String typePath) {
            AnnotationVisitor annotationVisitor = this.target.visit(annotation.getAnnotationType().getDescriptor(), visible, typeReference, typePath);
            if (annotationVisitor != null) {
                Default.handle(annotationVisitor, annotation, annotationValueFilter);
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
            return this.target.equals(((Default)object).target);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.target.hashCode();
        }
    }

    public static interface Target {
        @MaybeNull
        public AnnotationVisitor visit(String var1, boolean var2);

        @MaybeNull
        public AnnotationVisitor visit(String var1, boolean var2, int var3, String var4);

        @HashCodeAndEqualsPlugin.Enhance
        public static class OnRecordComponent
        implements Target {
            private final RecordComponentVisitor recordComponentVisitor;

            public OnRecordComponent(RecordComponentVisitor recordComponentVisitor) {
                this.recordComponentVisitor = recordComponentVisitor;
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible) {
                return this.recordComponentVisitor.visitAnnotation(annotationTypeDescriptor, visible);
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible, int typeReference, String typePath) {
                return this.recordComponentVisitor.visitTypeAnnotation(typeReference, TypePath.fromString(typePath), annotationTypeDescriptor, visible);
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
                return this.recordComponentVisitor.equals(((OnRecordComponent)object).recordComponentVisitor);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.recordComponentVisitor.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class OnMethodParameter
        implements Target {
            private final MethodVisitor methodVisitor;
            private final int parameterIndex;

            public OnMethodParameter(MethodVisitor methodVisitor, int parameterIndex) {
                this.methodVisitor = methodVisitor;
                this.parameterIndex = parameterIndex;
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible) {
                return this.methodVisitor.visitParameterAnnotation(this.parameterIndex, annotationTypeDescriptor, visible);
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible, int typeReference, String typePath) {
                return this.methodVisitor.visitTypeAnnotation(typeReference, TypePath.fromString(typePath), annotationTypeDescriptor, visible);
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
                if (this.parameterIndex != ((OnMethodParameter)object).parameterIndex) {
                    return false;
                }
                return this.methodVisitor.equals(((OnMethodParameter)object).methodVisitor);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.methodVisitor.hashCode()) * 31 + this.parameterIndex;
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class OnMethod
        implements Target {
            private final MethodVisitor methodVisitor;

            public OnMethod(MethodVisitor methodVisitor) {
                this.methodVisitor = methodVisitor;
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible) {
                return this.methodVisitor.visitAnnotation(annotationTypeDescriptor, visible);
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible, int typeReference, String typePath) {
                return this.methodVisitor.visitTypeAnnotation(typeReference, TypePath.fromString(typePath), annotationTypeDescriptor, visible);
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
                return this.methodVisitor.equals(((OnMethod)object).methodVisitor);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.methodVisitor.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class OnField
        implements Target {
            private final FieldVisitor fieldVisitor;

            public OnField(FieldVisitor fieldVisitor) {
                this.fieldVisitor = fieldVisitor;
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible) {
                return this.fieldVisitor.visitAnnotation(annotationTypeDescriptor, visible);
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible, int typeReference, String typePath) {
                return this.fieldVisitor.visitTypeAnnotation(typeReference, TypePath.fromString(typePath), annotationTypeDescriptor, visible);
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
                return this.fieldVisitor.equals(((OnField)object).fieldVisitor);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldVisitor.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class OnType
        implements Target {
            private final ClassVisitor classVisitor;

            public OnType(ClassVisitor classVisitor) {
                this.classVisitor = classVisitor;
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible) {
                return this.classVisitor.visitAnnotation(annotationTypeDescriptor, visible);
            }

            @MaybeNull
            public AnnotationVisitor visit(String annotationTypeDescriptor, boolean visible, int typeReference, String typePath) {
                return this.classVisitor.visitTypeAnnotation(typeReference, TypePath.fromString(typePath), annotationTypeDescriptor, visible);
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
                return this.classVisitor.equals(((OnType)object).classVisitor);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.classVisitor.hashCode();
            }
        }
    }
}


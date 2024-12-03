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
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.attribute.AnnotationAppender;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface TypeAttributeAppender {
    public void apply(ClassVisitor var1, TypeDescription var2, AnnotationValueFilter var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements TypeAttributeAppender {
        private final List<TypeAttributeAppender> typeAttributeAppenders = new ArrayList<TypeAttributeAppender>();

        public Compound(TypeAttributeAppender ... typeAttributeAppender) {
            this(Arrays.asList(typeAttributeAppender));
        }

        public Compound(List<? extends TypeAttributeAppender> typeAttributeAppenders) {
            for (TypeAttributeAppender typeAttributeAppender : typeAttributeAppenders) {
                if (typeAttributeAppender instanceof Compound) {
                    this.typeAttributeAppenders.addAll(((Compound)typeAttributeAppender).typeAttributeAppenders);
                    continue;
                }
                if (typeAttributeAppender instanceof NoOp) continue;
                this.typeAttributeAppenders.add(typeAttributeAppender);
            }
        }

        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            for (TypeAttributeAppender typeAttributeAppender : this.typeAttributeAppenders) {
                typeAttributeAppender.apply(classVisitor, instrumentedType, annotationValueFilter);
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
            return ((Object)this.typeAttributeAppenders).equals(((Compound)object).typeAttributeAppenders);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.typeAttributeAppenders).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Explicit
    implements TypeAttributeAppender {
        private final List<? extends AnnotationDescription> annotations;

        public Explicit(List<? extends AnnotationDescription> annotations) {
            this.annotations = annotations;
        }

        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender appender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnType(classVisitor));
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
            return ((Object)this.annotations).equals(((Explicit)object).annotations);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.annotations).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ForInstrumentedType implements TypeAttributeAppender
    {
        INSTANCE;


        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnType(classVisitor));
            annotationAppender = AnnotationAppender.ForTypeAnnotations.ofTypeVariable(annotationAppender, annotationValueFilter, true, instrumentedType.getTypeVariables());
            TypeDescription.Generic superClass = instrumentedType.getSuperClass();
            if (superClass != null) {
                annotationAppender = superClass.accept(AnnotationAppender.ForTypeAnnotations.ofSuperClass(annotationAppender, annotationValueFilter));
            }
            int interfaceIndex = 0;
            for (TypeDescription.Generic interfaceType : instrumentedType.getInterfaces()) {
                annotationAppender = interfaceType.accept(AnnotationAppender.ForTypeAnnotations.ofInterfaceType(annotationAppender, annotationValueFilter, interfaceIndex++));
            }
            for (AnnotationDescription annotation : instrumentedType.getDeclaredAnnotations()) {
                annotationAppender = annotationAppender.append(annotation, annotationValueFilter);
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Differentiating
        implements TypeAttributeAppender {
            private final int annotationIndex;
            private final int typeVariableIndex;
            private final int interfaceTypeIndex;

            public Differentiating(TypeDescription typeDescription) {
                this(typeDescription.getDeclaredAnnotations().size(), typeDescription.getTypeVariables().size(), typeDescription.getInterfaces().size());
            }

            protected Differentiating(int annotationIndex, int typeVariableIndex, int interfaceTypeIndex) {
                this.annotationIndex = annotationIndex;
                this.typeVariableIndex = typeVariableIndex;
                this.interfaceTypeIndex = interfaceTypeIndex;
            }

            public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
                AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnType(classVisitor));
                AnnotationAppender.ForTypeAnnotations.ofTypeVariable(annotationAppender, annotationValueFilter, true, this.typeVariableIndex, instrumentedType.getTypeVariables());
                TypeList.Generic interfaceTypes = instrumentedType.getInterfaces();
                int interfaceTypeIndex = this.interfaceTypeIndex;
                for (TypeDescription.Generic interfaceType : (TypeList.Generic)interfaceTypes.subList(this.interfaceTypeIndex, interfaceTypes.size())) {
                    annotationAppender = interfaceType.accept(AnnotationAppender.ForTypeAnnotations.ofInterfaceType(annotationAppender, annotationValueFilter, interfaceTypeIndex++));
                }
                AnnotationList declaredAnnotations = instrumentedType.getDeclaredAnnotations();
                for (AnnotationDescription annotationDescription : (AnnotationList)declaredAnnotations.subList(this.annotationIndex, declaredAnnotations.size())) {
                    annotationAppender = annotationAppender.append(annotationDescription, annotationValueFilter);
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
                if (this.annotationIndex != ((Differentiating)object).annotationIndex) {
                    return false;
                }
                if (this.typeVariableIndex != ((Differentiating)object).typeVariableIndex) {
                    return false;
                }
                return this.interfaceTypeIndex == ((Differentiating)object).interfaceTypeIndex;
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.annotationIndex) * 31 + this.typeVariableIndex) * 31 + this.interfaceTypeIndex;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements TypeAttributeAppender
    {
        INSTANCE;


        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
        }
    }
}


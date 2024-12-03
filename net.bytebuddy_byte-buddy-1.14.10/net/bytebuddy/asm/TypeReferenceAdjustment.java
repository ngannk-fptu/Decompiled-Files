/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.asm;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ConstantDynamic;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class TypeReferenceAdjustment
extends AsmVisitorWrapper.AbstractBase {
    private final boolean strict;
    private final ElementMatcher.Junction<? super TypeDescription> filter;

    protected TypeReferenceAdjustment(boolean strict, ElementMatcher.Junction<? super TypeDescription> filter) {
        this.strict = strict;
        this.filter = filter;
    }

    public static TypeReferenceAdjustment strict() {
        return new TypeReferenceAdjustment(true, ElementMatchers.none());
    }

    public static TypeReferenceAdjustment relaxed() {
        return new TypeReferenceAdjustment(false, ElementMatchers.none());
    }

    public TypeReferenceAdjustment filter(ElementMatcher<? super TypeDescription> filter) {
        return new TypeReferenceAdjustment(this.strict, this.filter.or(filter));
    }

    @Override
    public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
        return new TypeReferenceClassVisitor(classVisitor, this.strict, this.filter, typePool);
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
        if (this.strict != ((TypeReferenceAdjustment)object).strict) {
            return false;
        }
        return this.filter.equals(((TypeReferenceAdjustment)object).filter);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.strict) * 31 + this.filter.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class TypeReferenceClassVisitor
    extends ClassVisitor {
        @AlwaysNull
        private static final AnnotationVisitor IGNORE_ANNOTATION = null;
        @AlwaysNull
        private static final FieldVisitor IGNORE_FIELD = null;
        @AlwaysNull
        private static final MethodVisitor IGNORE_METHOD = null;
        private final boolean strict;
        private final ElementMatcher<? super TypeDescription> filter;
        private final TypePool typePool;
        private final Set<String> observedTypes;
        private final Set<String> visitedInnerTypes;

        protected TypeReferenceClassVisitor(ClassVisitor classVisitor, boolean strict, ElementMatcher<? super TypeDescription> filter, TypePool typePool) {
            super(OpenedClassReader.ASM_API, classVisitor);
            this.typePool = typePool;
            this.strict = strict;
            this.filter = filter;
            this.observedTypes = new HashSet<String>();
            this.visitedInnerTypes = new HashSet<String>();
        }

        @Override
        public void visit(int version, int modifiers, String internalName, @MaybeNull String genericSignature, @MaybeNull String superClassInternalName, @MaybeNull String[] interfaceInternalName) {
            if (superClassInternalName != null) {
                this.observedTypes.add(superClassInternalName);
            }
            if (interfaceInternalName != null) {
                this.observedTypes.addAll(Arrays.asList(interfaceInternalName));
            }
            super.visit(version, modifiers, internalName, genericSignature, superClassInternalName, interfaceInternalName);
        }

        @Override
        public void visitNestHost(String nestHost) {
            this.observedTypes.add(nestHost);
            super.visitNestHost(nestHost);
        }

        @Override
        public void visitOuterClass(String ownerTypeInternalName, String methodName, String methodDescriptor) {
            this.observedTypes.add(ownerTypeInternalName);
            super.visitOuterClass(ownerTypeInternalName, methodName, methodDescriptor);
        }

        @Override
        public void visitNestMember(String nestMember) {
            this.observedTypes.add(nestMember);
            super.visitNestMember(nestMember);
        }

        @Override
        public void visitInnerClass(String internalName, String outerName, String innerName, int modifiers) {
            this.visitedInnerTypes.add(internalName);
            super.visitInnerClass(internalName, outerName, innerName, modifiers);
        }

        @Override
        @MaybeNull
        public RecordComponentVisitor visitRecordComponent(String name, String descriptor, @MaybeNull String signature) {
            this.observedTypes.add(Type.getType(descriptor).getInternalName());
            return super.visitRecordComponent(name, descriptor, signature);
        }

        @Override
        @MaybeNull
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            this.observedTypes.add(Type.getType(descriptor).getInternalName());
            AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
            if (annotationVisitor != null) {
                return new TypeReferenceAnnotationVisitor(annotationVisitor);
            }
            return IGNORE_ANNOTATION;
        }

        @Override
        @MaybeNull
        public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
            this.observedTypes.add(Type.getType(descriptor).getInternalName());
            AnnotationVisitor annotationVisitor = super.visitTypeAnnotation(typeReference, typePath, descriptor, visible);
            if (annotationVisitor != null) {
                return new TypeReferenceAnnotationVisitor(annotationVisitor);
            }
            return IGNORE_ANNOTATION;
        }

        @Override
        @MaybeNull
        public FieldVisitor visitField(int modifiers, String name, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
            FieldVisitor fieldVisitor = super.visitField(modifiers, name, descriptor, signature, value);
            if (fieldVisitor != null) {
                this.resolve(Type.getType(descriptor));
                return new TypeReferenceFieldVisitor(fieldVisitor);
            }
            return IGNORE_FIELD;
        }

        @Override
        @MaybeNull
        public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exceptionInternalName) {
            MethodVisitor methodVisitor = super.visitMethod(modifiers, internalName, descriptor, signature, exceptionInternalName);
            if (methodVisitor != null) {
                this.resolve(Type.getType(descriptor));
                if (exceptionInternalName != null) {
                    this.observedTypes.addAll(Arrays.asList(exceptionInternalName));
                }
                return new TypeReferenceMethodVisitor(methodVisitor);
            }
            return IGNORE_METHOD;
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public void visitEnd() {
            block2: for (String observedType : this.observedTypes) {
                if (!this.visitedInnerTypes.add(observedType)) continue;
                TypePool.Resolution resolution = this.typePool.describe(observedType.replace('/', '.'));
                if (resolution.isResolved()) {
                    TypeDescription typeDescription = resolution.resolve();
                    if (this.filter.matches(typeDescription)) continue;
                    while (typeDescription != null && typeDescription.isNestedClass()) {
                        super.visitInnerClass(typeDescription.getInternalName(), typeDescription.isMemberType() ? typeDescription.getDeclaringType().getInternalName() : null, typeDescription.isAnonymousType() ? null : typeDescription.getSimpleName(), typeDescription.getModifiers());
                        try {
                            while ((typeDescription = typeDescription.getEnclosingType()) != null && !this.visitedInnerTypes.add(typeDescription.getInternalName())) {
                            }
                        }
                        catch (RuntimeException exception) {
                            if (!this.strict) continue block2;
                            throw exception;
                        }
                    }
                    continue;
                }
                if (!this.strict) continue;
                throw new IllegalStateException("Could not locate type for: " + observedType.replace('/', '.'));
            }
            super.visitEnd();
        }

        protected void resolve(Type type) {
            if (type.getSort() == 11) {
                this.resolve(type.getReturnType());
                for (Type argumentType : type.getArgumentTypes()) {
                    this.resolve(argumentType);
                }
            } else {
                while (type.getSort() == 9) {
                    type = type.getElementType();
                }
                if (type.getSort() == 10) {
                    this.observedTypes.add(type.getInternalName());
                }
            }
        }

        protected void resolve(Handle handle) {
            this.observedTypes.add(handle.getOwner());
            Type methodType = Type.getType(handle.getDesc());
            this.resolve(methodType.getReturnType());
            for (Type type : methodType.getArgumentTypes()) {
                this.resolve(type);
            }
        }

        protected void resolve(ConstantDynamic constant) {
            Type methodType = Type.getType(constant.getDescriptor());
            this.resolve(methodType.getReturnType());
            for (Type type : methodType.getArgumentTypes()) {
                this.resolve(type);
            }
            this.resolve(constant.getBootstrapMethod());
            for (int index = 0; index < constant.getBootstrapMethodArgumentCount(); ++index) {
                this.resolve(constant.getBootstrapMethodArgument(index));
            }
        }

        private void observeInternalName(String internalName) {
            int index = internalName.lastIndexOf(91);
            if (index != -1) {
                internalName = internalName.substring(index + 2, internalName.length() - 1);
            }
            this.observedTypes.add(internalName);
        }

        protected void resolve(Object value) {
            if (value instanceof Type) {
                this.resolve((Type)value);
            } else if (value instanceof Handle) {
                this.resolve((Handle)value);
            } else if (value instanceof ConstantDynamic) {
                this.resolve((ConstantDynamic)value);
            }
        }

        protected class TypeReferenceMethodVisitor
        extends MethodVisitor {
            protected TypeReferenceMethodVisitor(MethodVisitor methodVisitor) {
                super(OpenedClassReader.ASM_API, methodVisitor);
            }

            @MaybeNull
            public AnnotationVisitor visitAnnotationDefault() {
                AnnotationVisitor annotationVisitor = super.visitAnnotationDefault();
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitTypeAnnotation(typeReference, typePath, descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitParameterAnnotation(int index, String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitParameterAnnotation(index, descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitInsnAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitInsnAnnotation(typeReference, typePath, descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitTryCatchAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitTryCatchAnnotation(typeReference, typePath, descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitLocalVariableAnnotation(int typeReference, @MaybeNull TypePath typePath, Label[] start, Label[] end, int[] offset, String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitLocalVariableAnnotation(typeReference, typePath, start, end, offset, descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            public void visitTypeInsn(int opcode, String internalName) {
                TypeReferenceClassVisitor.this.observeInternalName(internalName);
                super.visitTypeInsn(opcode, internalName);
            }

            public void visitFieldInsn(int opcode, String ownerInternalName, String name, String descriptor) {
                TypeReferenceClassVisitor.this.observeInternalName(ownerInternalName);
                TypeReferenceClassVisitor.this.resolve(Type.getType(descriptor));
                super.visitFieldInsn(opcode, ownerInternalName, name, descriptor);
            }

            public void visitMethodInsn(int opcode, String ownerInternalName, String name, String descriptor, boolean isInterface) {
                TypeReferenceClassVisitor.this.observeInternalName(ownerInternalName);
                TypeReferenceClassVisitor.this.resolve(Type.getType(descriptor));
                super.visitMethodInsn(opcode, ownerInternalName, name, descriptor, isInterface);
            }

            public void visitInvokeDynamicInsn(String name, String descriptor, Handle handle, Object ... argument) {
                TypeReferenceClassVisitor.this.resolve(Type.getType(descriptor));
                TypeReferenceClassVisitor.this.resolve(handle);
                for (Object anArgument : argument) {
                    TypeReferenceClassVisitor.this.resolve(anArgument);
                }
                super.visitInvokeDynamicInsn(name, descriptor, handle, argument);
            }

            public void visitLdcInsn(Object value) {
                TypeReferenceClassVisitor.this.resolve(value);
                super.visitLdcInsn(value);
            }

            public void visitMultiANewArrayInsn(String descriptor, int dimension) {
                TypeReferenceClassVisitor.this.resolve(Type.getType(descriptor));
                super.visitMultiANewArrayInsn(descriptor, dimension);
            }

            public void visitTryCatchBlock(Label start, Label end, Label handler, @MaybeNull String typeInternalName) {
                if (typeInternalName != null) {
                    TypeReferenceClassVisitor.this.observedTypes.add(typeInternalName);
                }
                super.visitTryCatchBlock(start, end, handler, typeInternalName);
            }
        }

        protected class TypeReferenceFieldVisitor
        extends FieldVisitor {
            protected TypeReferenceFieldVisitor(FieldVisitor fieldVisitor) {
                super(OpenedClassReader.ASM_API, fieldVisitor);
            }

            @MaybeNull
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }
        }

        protected class TypeReferenceAnnotationVisitor
        extends AnnotationVisitor {
            protected TypeReferenceAnnotationVisitor(AnnotationVisitor annotationVisitor) {
                super(OpenedClassReader.ASM_API, annotationVisitor);
            }

            public void visit(String name, Object value) {
                TypeReferenceClassVisitor.this.resolve(value);
                super.visit(name, value);
            }

            public void visitEnum(String name, String descriptor, String value) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                super.visitEnum(name, descriptor, value);
            }

            @MaybeNull
            public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                TypeReferenceClassVisitor.this.observedTypes.add(Type.getType(descriptor).getInternalName());
                AnnotationVisitor annotationVisitor = super.visitAnnotation(name, descriptor);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }

            @MaybeNull
            public AnnotationVisitor visitArray(String name) {
                AnnotationVisitor annotationVisitor = super.visitArray(name);
                if (annotationVisitor != null) {
                    return new TypeReferenceAnnotationVisitor(annotationVisitor);
                }
                return IGNORE_ANNOTATION;
            }
        }
    }
}


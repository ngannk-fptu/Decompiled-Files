/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.ClassFileVersion
 *  net.bytebuddy.description.field.FieldDescription$InDefinedShape
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.implementation.Implementation$Context
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender$Size
 *  net.bytebuddy.jar.asm.Label
 *  net.bytebuddy.jar.asm.MethodVisitor
 *  net.bytebuddy.jar.asm.Type
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.util.Objects;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

abstract class FieldReaderAppender
implements ByteCodeAppender {
    protected final TypeDescription managedCtClass;
    protected final EnhancerImpl.AnnotatedFieldDescription persistentField;
    protected final FieldDescription.InDefinedShape persistentFieldAsDefined;

    private FieldReaderAppender(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
        this.managedCtClass = managedCtClass;
        this.persistentField = persistentField;
        this.persistentFieldAsDefined = persistentField.asDefined();
    }

    static ByteCodeAppender of(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
        if (!persistentField.isVisibleTo(managedCtClass)) {
            return new MethodDispatching(managedCtClass, persistentField);
        }
        return new FieldWriting(managedCtClass, persistentField);
    }

    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
        TypeDescription dispatcherType = this.persistentFieldAsDefined.getType().isPrimitive() ? this.persistentFieldAsDefined.getType().asErasure() : TypeDescription.OBJECT;
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(182, this.managedCtClass.getInternalName(), "$$_hibernate_getInterceptor", Type.getMethodDescriptor((Type)Type.getType(PersistentAttributeInterceptor.class), (Type[])new Type[0]), false);
        Label skip = new Label();
        methodVisitor.visitJumpInsn(198, skip);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(182, this.managedCtClass.getInternalName(), "$$_hibernate_getInterceptor", Type.getMethodDescriptor((Type)Type.getType(PersistentAttributeInterceptor.class), (Type[])new Type[0]), false);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitLdcInsn((Object)this.persistentFieldAsDefined.getName());
        methodVisitor.visitVarInsn(25, 0);
        this.fieldRead(methodVisitor);
        methodVisitor.visitMethodInsn(185, Type.getInternalName(PersistentAttributeInterceptor.class), "read" + EnhancerImpl.capitalize(dispatcherType.getSimpleName()), Type.getMethodDescriptor((Type)Type.getType((String)dispatcherType.getDescriptor()), (Type[])new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType((String)dispatcherType.getDescriptor())}), true);
        if (!dispatcherType.isPrimitive()) {
            methodVisitor.visitTypeInsn(192, this.persistentFieldAsDefined.getType().asErasure().getInternalName());
        }
        this.fieldWrite(methodVisitor);
        methodVisitor.visitLabel(skip);
        if (implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V6)) {
            methodVisitor.visitFrame(3, 0, null, 0, null);
        }
        methodVisitor.visitVarInsn(25, 0);
        this.fieldRead(methodVisitor);
        if (!this.persistentField.getType().isPrimitive() && !this.persistentField.getType().asErasure().getInternalName().equals(this.persistentFieldAsDefined.getType().asErasure().getInternalName())) {
            methodVisitor.visitTypeInsn(192, this.persistentField.getType().asErasure().getInternalName());
        }
        methodVisitor.visitInsn(Type.getType((String)this.persistentFieldAsDefined.getType().asErasure().getDescriptor()).getOpcode(172));
        return new ByteCodeAppender.Size(4 + this.persistentFieldAsDefined.getType().getStackSize().getSize(), instrumentedMethod.getStackSize());
    }

    protected abstract void fieldRead(MethodVisitor var1);

    protected abstract void fieldWrite(MethodVisitor var1);

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FieldReaderAppender that = (FieldReaderAppender)o;
        return Objects.equals(this.managedCtClass, that.managedCtClass) && Objects.equals(this.persistentField, that.persistentField) && Objects.equals(this.persistentFieldAsDefined, that.persistentFieldAsDefined);
    }

    public int hashCode() {
        return Objects.hash(this.managedCtClass, this.persistentField, this.persistentFieldAsDefined);
    }

    private static class MethodDispatching
    extends FieldReaderAppender {
        private MethodDispatching(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
            super(managedCtClass, persistentField);
        }

        @Override
        protected void fieldRead(MethodVisitor methodVisitor) {
            methodVisitor.visitMethodInsn(183, this.managedCtClass.getSuperClass().asErasure().getInternalName(), "$$_hibernate_read_" + this.persistentFieldAsDefined.getName(), Type.getMethodDescriptor((Type)Type.getType((String)this.persistentFieldAsDefined.getType().asErasure().getDescriptor()), (Type[])new Type[0]), false);
        }

        @Override
        protected void fieldWrite(MethodVisitor methodVisitor) {
            methodVisitor.visitMethodInsn(183, this.managedCtClass.getSuperClass().asErasure().getInternalName(), "$$_hibernate_write_" + this.persistentFieldAsDefined.getName(), Type.getMethodDescriptor((Type)Type.getType(Void.TYPE), (Type[])new Type[]{Type.getType((String)this.persistentFieldAsDefined.getType().asErasure().getDescriptor())}), false);
        }
    }

    private static class FieldWriting
    extends FieldReaderAppender {
        private FieldWriting(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
            super(managedCtClass, persistentField);
        }

        @Override
        protected void fieldRead(MethodVisitor methodVisitor) {
            methodVisitor.visitFieldInsn(180, this.persistentFieldAsDefined.getDeclaringType().asErasure().getInternalName(), this.persistentFieldAsDefined.getInternalName(), this.persistentFieldAsDefined.getDescriptor());
        }

        @Override
        protected void fieldWrite(MethodVisitor methodVisitor) {
            methodVisitor.visitFieldInsn(181, this.persistentFieldAsDefined.getDeclaringType().asErasure().getInternalName(), this.persistentFieldAsDefined.getInternalName(), this.persistentFieldAsDefined.getDescriptor());
        }
    }
}


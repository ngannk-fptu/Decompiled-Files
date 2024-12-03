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

abstract class FieldWriterAppender
implements ByteCodeAppender {
    protected final TypeDescription managedCtClass;
    protected final FieldDescription.InDefinedShape persistentFieldAsDefined;

    private FieldWriterAppender(TypeDescription managedCtClass, FieldDescription.InDefinedShape persistentFieldAsDefined) {
        this.managedCtClass = managedCtClass;
        this.persistentFieldAsDefined = persistentFieldAsDefined;
    }

    static ByteCodeAppender of(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
        if (!persistentField.isVisibleTo(managedCtClass)) {
            return new MethodDispatching(managedCtClass, persistentField.asDefined());
        }
        return new FieldWriting(managedCtClass, persistentField.asDefined());
    }

    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
        TypeDescription dispatcherType = this.persistentFieldAsDefined.getType().isPrimitive() ? this.persistentFieldAsDefined.getType().asErasure() : TypeDescription.OBJECT;
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(182, this.managedCtClass.getInternalName(), "$$_hibernate_getInterceptor", Type.getMethodDescriptor((Type)Type.getType(PersistentAttributeInterceptor.class), (Type[])new Type[0]), false);
        Label noInterceptor = new Label();
        methodVisitor.visitJumpInsn(198, noInterceptor);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(182, this.managedCtClass.getInternalName(), "$$_hibernate_getInterceptor", Type.getMethodDescriptor((Type)Type.getType(PersistentAttributeInterceptor.class), (Type[])new Type[0]), false);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitLdcInsn((Object)this.persistentFieldAsDefined.getName());
        methodVisitor.visitVarInsn(25, 0);
        this.fieldRead(methodVisitor);
        methodVisitor.visitVarInsn(Type.getType((String)dispatcherType.getDescriptor()).getOpcode(21), 1);
        methodVisitor.visitMethodInsn(185, Type.getInternalName(PersistentAttributeInterceptor.class), "write" + EnhancerImpl.capitalize(dispatcherType.getSimpleName()), Type.getMethodDescriptor((Type)Type.getType((String)dispatcherType.getDescriptor()), (Type[])new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType((String)dispatcherType.getDescriptor()), Type.getType((String)dispatcherType.getDescriptor())}), true);
        if (!dispatcherType.isPrimitive()) {
            methodVisitor.visitTypeInsn(192, this.persistentFieldAsDefined.getType().asErasure().getInternalName());
        }
        this.fieldWrite(methodVisitor);
        methodVisitor.visitInsn(177);
        methodVisitor.visitLabel(noInterceptor);
        if (implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V6)) {
            methodVisitor.visitFrame(3, 0, null, 0, null);
        }
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(Type.getType((String)dispatcherType.getDescriptor()).getOpcode(21), 1);
        if (!dispatcherType.isPrimitive()) {
            methodVisitor.visitTypeInsn(192, this.persistentFieldAsDefined.getType().asErasure().getInternalName());
        }
        this.fieldWrite(methodVisitor);
        methodVisitor.visitInsn(177);
        return new ByteCodeAppender.Size(4 + 2 * this.persistentFieldAsDefined.getType().getStackSize().getSize(), instrumentedMethod.getStackSize());
    }

    protected abstract void fieldRead(MethodVisitor var1);

    protected abstract void fieldWrite(MethodVisitor var1);

    private static class MethodDispatching
    extends FieldWriterAppender {
        private MethodDispatching(TypeDescription managedCtClass, FieldDescription.InDefinedShape persistentFieldAsDefined) {
            super(managedCtClass, persistentFieldAsDefined);
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
    extends FieldWriterAppender {
        private FieldWriting(TypeDescription managedCtClass, FieldDescription.InDefinedShape persistentFieldAsDefined) {
            super(managedCtClass, persistentFieldAsDefined);
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


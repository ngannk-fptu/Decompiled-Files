/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Embedded
 *  javax.persistence.EmbeddedId
 *  javax.persistence.Id
 *  net.bytebuddy.ClassFileVersion
 *  net.bytebuddy.asm.Advice
 *  net.bytebuddy.asm.Advice$OffsetMapping
 *  net.bytebuddy.asm.Advice$WithCustomMapping
 *  net.bytebuddy.description.field.FieldDescription$InDefinedShape
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.dynamic.scaffold.InstrumentedType
 *  net.bytebuddy.implementation.Implementation
 *  net.bytebuddy.implementation.Implementation$Context
 *  net.bytebuddy.implementation.Implementation$Target
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender$Compound
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender$Size
 *  net.bytebuddy.jar.asm.Label
 *  net.bytebuddy.jar.asm.MethodVisitor
 *  net.bytebuddy.jar.asm.Type
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.util.Collection;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import org.hibernate.bytecode.enhance.internal.bytebuddy.ByteBuddyEnhancementContext;
import org.hibernate.bytecode.enhance.internal.bytebuddy.CodeTemplates;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.bytecode.enhance.internal.bytebuddy.InlineDirtyCheckerEqualsHelper;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;

final class InlineDirtyCheckingHandler
implements Implementation,
ByteCodeAppender {
    private static final String HELPER_TYPE_NAME = Type.getInternalName(InlineDirtyCheckerEqualsHelper.class);
    private static final Type PE_INTERCEPTABLE_TYPE = Type.getType(PersistentAttributeInterceptable.class);
    private static final Type OBJECT_TYPE = Type.getType(Object.class);
    private static final Type STRING_TYPE = Type.getType(String.class);
    private final Implementation delegate;
    private final TypeDescription managedCtClass;
    private final FieldDescription.InDefinedShape persistentField;
    private final boolean applyLazyCheck;

    private InlineDirtyCheckingHandler(Implementation delegate, TypeDescription managedCtClass, FieldDescription.InDefinedShape persistentField, boolean applyLazyCheck) {
        this.delegate = delegate;
        this.managedCtClass = managedCtClass;
        this.persistentField = persistentField;
        this.applyLazyCheck = applyLazyCheck;
    }

    static Implementation wrap(TypeDescription managedCtClass, ByteBuddyEnhancementContext enhancementContext, EnhancerImpl.AnnotatedFieldDescription persistentField, Implementation implementation) {
        if (enhancementContext.doDirtyCheckingInline(managedCtClass)) {
            if (enhancementContext.isCompositeClass(managedCtClass)) {
                implementation = Advice.to(CodeTemplates.CompositeDirtyCheckingHandler.class).wrap(implementation);
            } else if (!(persistentField.hasAnnotation(Id.class) || persistentField.hasAnnotation(EmbeddedId.class) || persistentField.getType().asErasure().isAssignableTo(Collection.class) && enhancementContext.isMappedCollection(persistentField))) {
                implementation = new InlineDirtyCheckingHandler(implementation, managedCtClass, persistentField.asDefined(), enhancementContext.hasLazyLoadableAttributes(managedCtClass));
            }
            if (enhancementContext.isCompositeClass(persistentField.getType().asErasure()) && persistentField.hasAnnotation(Embedded.class)) {
                Advice.WithCustomMapping advice = Advice.withCustomMapping();
                advice = persistentField.isVisibleTo(managedCtClass) ? advice.bind(CodeTemplates.FieldValue.class, persistentField.getFieldDescription()) : advice.bind(CodeTemplates.FieldValue.class, (Advice.OffsetMapping)new CodeTemplates.GetterMapping(persistentField.getFieldDescription()));
                implementation = advice.bind(CodeTemplates.FieldName.class, (Object)persistentField.getName()).to(CodeTemplates.CompositeFieldDirtyCheckingHandler.class).wrap(implementation);
            }
        }
        return implementation;
    }

    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new ByteCodeAppender.Compound(new ByteCodeAppender[]{this, this.delegate.appender(implementationTarget)});
    }

    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return this.delegate.prepare(instrumentedType);
    }

    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
        int branchCode;
        if (this.applyLazyCheck) {
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitLdcInsn((Object)this.persistentField.getName());
        }
        methodVisitor.visitVarInsn(Type.getType((String)this.persistentField.getType().asErasure().getDescriptor()).getOpcode(21), 1);
        methodVisitor.visitVarInsn(25, 0);
        if (this.persistentField.getDeclaringType().asErasure().equals(this.managedCtClass)) {
            methodVisitor.visitFieldInsn(180, this.persistentField.getDeclaringType().asErasure().getInternalName(), this.persistentField.getName(), this.persistentField.getDescriptor());
        } else {
            methodVisitor.visitMethodInsn(182, this.persistentField.getDeclaringType().asErasure().getInternalName(), "$$_hibernate_read_" + this.persistentField.getName(), Type.getMethodDescriptor((Type)Type.getType((String)this.persistentField.getDescriptor()), (Type[])new Type[0]), false);
        }
        if (this.applyLazyCheck) {
            if (this.persistentField.getType().isPrimitive()) {
                Type fieldType = Type.getType((String)this.persistentField.getDescriptor());
                methodVisitor.visitMethodInsn(184, HELPER_TYPE_NAME, "areEquals", Type.getMethodDescriptor((Type)Type.BOOLEAN_TYPE, (Type[])new Type[]{PE_INTERCEPTABLE_TYPE, STRING_TYPE, fieldType, fieldType}), false);
            } else {
                methodVisitor.visitMethodInsn(184, HELPER_TYPE_NAME, "areEquals", Type.getMethodDescriptor((Type)Type.BOOLEAN_TYPE, (Type[])new Type[]{PE_INTERCEPTABLE_TYPE, STRING_TYPE, OBJECT_TYPE, OBJECT_TYPE}), false);
            }
            branchCode = 154;
        } else if (this.persistentField.getType().isPrimitive()) {
            if (this.persistentField.getType().represents(Long.TYPE)) {
                methodVisitor.visitInsn(148);
            } else if (this.persistentField.getType().represents(Float.TYPE)) {
                methodVisitor.visitInsn(149);
            } else if (this.persistentField.getType().represents(Double.TYPE)) {
                methodVisitor.visitInsn(151);
            } else {
                methodVisitor.visitInsn(100);
            }
            branchCode = 153;
        } else {
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Objects.class), "deepEquals", Type.getMethodDescriptor((Type)Type.BOOLEAN_TYPE, (Type[])new Type[]{OBJECT_TYPE, OBJECT_TYPE}), false);
            branchCode = 154;
        }
        Label skip = new Label();
        methodVisitor.visitJumpInsn(branchCode, skip);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitLdcInsn((Object)this.persistentField.getName());
        methodVisitor.visitMethodInsn(182, this.managedCtClass.getInternalName(), "$$_hibernate_trackChange", Type.getMethodDescriptor((Type)Type.VOID_TYPE, (Type[])new Type[]{STRING_TYPE}), false);
        methodVisitor.visitLabel(skip);
        if (implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V6)) {
            methodVisitor.visitFrame(3, 0, null, 0, null);
        }
        return new ByteCodeAppender.Size(3 + 2 * this.persistentField.getType().asErasure().getStackSize().getSize(), instrumentedMethod.getStackSize());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || InlineDirtyCheckingHandler.class != o.getClass()) {
            return false;
        }
        InlineDirtyCheckingHandler that = (InlineDirtyCheckingHandler)o;
        return Objects.equals(this.delegate, that.delegate) && Objects.equals(this.managedCtClass, that.managedCtClass) && Objects.equals(this.persistentField, that.persistentField);
    }

    public int hashCode() {
        return Objects.hash(this.delegate, this.managedCtClass, this.persistentField);
    }
}


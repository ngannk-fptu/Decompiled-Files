/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Id
 *  net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods$MethodVisitorWrapper
 *  net.bytebuddy.description.field.FieldDescription
 *  net.bytebuddy.description.field.FieldList
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.implementation.Implementation$Context
 *  net.bytebuddy.jar.asm.MethodVisitor
 *  net.bytebuddy.jar.asm.Type
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatcher$Junction
 *  net.bytebuddy.matcher.ElementMatchers
 *  net.bytebuddy.pool.TypePool
 *  net.bytebuddy.pool.TypePool$Resolution
 *  net.bytebuddy.utility.OpenedClassReader
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.util.Objects;
import javax.persistence.Id;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.OpenedClassReader;
import org.hibernate.bytecode.enhance.internal.bytebuddy.ByteBuddyEnhancementContext;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.bytecode.enhance.spi.EnhancementException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

final class FieldAccessEnhancer
implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(FieldAccessEnhancer.class);
    private final TypeDescription managedCtClass;
    private final ByteBuddyEnhancementContext enhancementContext;
    private final TypePool classPool;

    FieldAccessEnhancer(TypeDescription managedCtClass, ByteBuddyEnhancementContext enhancementContext, TypePool classPool) {
        this.managedCtClass = managedCtClass;
        this.enhancementContext = enhancementContext;
        this.classPool = classPool;
    }

    public MethodVisitor wrap(final TypeDescription instrumentedType, final MethodDescription instrumentedMethod, final MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
        return new MethodVisitor(OpenedClassReader.ASM_API, methodVisitor){

            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode != 180 && opcode != 181) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    return;
                }
                TypeDescription declaredOwnerType = FieldAccessEnhancer.this.findDeclaredType(owner);
                EnhancerImpl.AnnotatedFieldDescription field = FieldAccessEnhancer.this.findField(declaredOwnerType, name, desc);
                if ((FieldAccessEnhancer.this.enhancementContext.isEntityClass(declaredOwnerType.asErasure()) || FieldAccessEnhancer.this.enhancementContext.isCompositeClass(declaredOwnerType.asErasure())) && !field.getType().asErasure().equals(FieldAccessEnhancer.this.managedCtClass) && FieldAccessEnhancer.this.enhancementContext.isPersistentField(field) && !field.hasAnnotation(Id.class) && !field.getName().equals("this$0")) {
                    log.debugf("Extended enhancement: Transforming access to field [%s#%s] from method [%s#%s()]", new Object[]{declaredOwnerType.getName(), field.getName(), instrumentedType.getName(), instrumentedMethod.getName()});
                    switch (opcode) {
                        case 180: {
                            methodVisitor.visitMethodInsn(182, owner, "$$_hibernate_read_" + name, Type.getMethodDescriptor((Type)Type.getType((String)desc), (Type[])new Type[0]), false);
                            return;
                        }
                        case 181: {
                            if (field.getFieldDescription().isFinal()) break;
                            methodVisitor.visitMethodInsn(182, owner, "$$_hibernate_write_" + name, Type.getMethodDescriptor((Type)Type.getType(Void.TYPE), (Type[])new Type[]{Type.getType((String)desc)}), false);
                            return;
                        }
                        default: {
                            throw new EnhancementException("Unexpected opcode: " + opcode);
                        }
                    }
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        };
    }

    private TypeDescription findDeclaredType(String name) {
        String cleanedName = name.replace('/', '.');
        TypePool.Resolution resolution = this.classPool.describe(cleanedName);
        if (!resolution.isResolved()) {
            String msg = String.format("Unable to perform extended enhancement - Unable to locate [%s]", cleanedName);
            throw new EnhancementException(msg);
        }
        return resolution.resolve();
    }

    private EnhancerImpl.AnnotatedFieldDescription findField(TypeDescription declaredOwnedType, String name, String desc) {
        TypeDescription ownerType = declaredOwnedType;
        ElementMatcher.Junction fieldFilter = ElementMatchers.named((String)name).and((ElementMatcher)ElementMatchers.hasDescriptor((String)desc));
        FieldList fields = (FieldList)ownerType.getDeclaredFields().filter((ElementMatcher)fieldFilter);
        while (fields.isEmpty() && ownerType.getSuperClass() != null) {
            ownerType = ownerType.getSuperClass();
            fields = (FieldList)ownerType.getDeclaredFields().filter((ElementMatcher)fieldFilter);
        }
        if (fields.size() != 1) {
            String msg = String.format("Unable to perform extended enhancement - No unique field [%s] defined by [%s]", name, declaredOwnedType.getName());
            throw new EnhancementException(msg);
        }
        return new EnhancerImpl.AnnotatedFieldDescription(this.enhancementContext, (FieldDescription)fields.getOnly());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || FieldAccessEnhancer.class != o.getClass()) {
            return false;
        }
        FieldAccessEnhancer that = (FieldAccessEnhancer)o;
        return Objects.equals(this.managedCtClass, that.managedCtClass);
    }

    public int hashCode() {
        return this.managedCtClass.hashCode();
    }
}


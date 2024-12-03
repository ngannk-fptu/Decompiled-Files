/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Embedded
 *  net.bytebuddy.asm.Advice
 *  net.bytebuddy.asm.AsmVisitorWrapper
 *  net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods
 *  net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods$MethodVisitorWrapper
 *  net.bytebuddy.asm.ModifierAdjustment
 *  net.bytebuddy.description.field.FieldDescription
 *  net.bytebuddy.description.field.FieldDescription$InDefinedShape
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.modifier.ModifierContributor$ForField
 *  net.bytebuddy.description.modifier.ModifierContributor$ForMethod
 *  net.bytebuddy.description.modifier.Visibility
 *  net.bytebuddy.description.type.TypeDefinition
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.description.type.TypeDescription$Generic
 *  net.bytebuddy.dynamic.DynamicType$Builder
 *  net.bytebuddy.implementation.FieldAccessor
 *  net.bytebuddy.implementation.Implementation
 *  net.bytebuddy.implementation.Implementation$Context
 *  net.bytebuddy.implementation.Implementation$Simple
 *  net.bytebuddy.implementation.StubMethod
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender$Size
 *  net.bytebuddy.jar.asm.MethodVisitor
 *  net.bytebuddy.jar.asm.Type
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatcher$Junction
 *  net.bytebuddy.matcher.ElementMatchers
 *  net.bytebuddy.pool.TypePool
 *  net.bytebuddy.utility.OpenedClassReader
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.persistence.Embedded;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.asm.ModifierAdjustment;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.OpenedClassReader;
import org.hibernate.bytecode.enhance.internal.bytebuddy.BiDirectionalAssociationHandler;
import org.hibernate.bytecode.enhance.internal.bytebuddy.ByteBuddyEnhancementContext;
import org.hibernate.bytecode.enhance.internal.bytebuddy.CodeTemplates;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.bytecode.enhance.internal.bytebuddy.FieldAccessEnhancer;
import org.hibernate.bytecode.enhance.internal.bytebuddy.FieldReaderAppender;
import org.hibernate.bytecode.enhance.internal.bytebuddy.FieldWriterAppender;
import org.hibernate.bytecode.enhance.internal.bytebuddy.InlineDirtyCheckingHandler;
import org.hibernate.engine.spi.CompositeOwner;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

final class PersistentAttributeTransformer
implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(PersistentAttributeTransformer.class);
    private static final ElementMatcher.Junction<MethodDescription> NOT_HIBERNATE_GENERATED = ElementMatchers.not((ElementMatcher)ElementMatchers.nameStartsWith((String)"$$_hibernate_"));
    private static final ModifierContributor.ForField REMOVE_FINAL_MODIFIER = new ModifierContributor.ForField(){

        public int getMask() {
            return 0;
        }

        public int getRange() {
            return 16;
        }

        public boolean isDefault() {
            return false;
        }
    };
    private final TypeDescription managedCtClass;
    private final ByteBuddyEnhancementContext enhancementContext;
    private final TypePool classPool;
    private final EnhancerImpl.AnnotatedFieldDescription[] enhancedFields;

    private PersistentAttributeTransformer(TypeDescription managedCtClass, ByteBuddyEnhancementContext enhancementContext, TypePool classPool, EnhancerImpl.AnnotatedFieldDescription[] enhancedFields) {
        this.managedCtClass = managedCtClass;
        this.enhancementContext = enhancementContext;
        this.classPool = classPool;
        this.enhancedFields = enhancedFields;
    }

    public static PersistentAttributeTransformer collectPersistentFields(TypeDescription managedCtClass, ByteBuddyEnhancementContext enhancementContext, TypePool classPool) {
        ArrayList<EnhancerImpl.AnnotatedFieldDescription> persistentFieldList = new ArrayList<EnhancerImpl.AnnotatedFieldDescription>();
        if (!enhancementContext.isMappedSuperclassClass(managedCtClass)) {
            persistentFieldList.addAll(PersistentAttributeTransformer.collectInheritPersistentFields((TypeDefinition)managedCtClass, enhancementContext));
        }
        for (FieldDescription ctField : managedCtClass.getDeclaredFields()) {
            if (ctField.getName().startsWith("$$_hibernate_") || "this$0".equals(ctField.getName())) continue;
            EnhancerImpl.AnnotatedFieldDescription annotatedField = new EnhancerImpl.AnnotatedFieldDescription(enhancementContext, ctField);
            if (ctField.isStatic() || !enhancementContext.isPersistentField(annotatedField)) continue;
            persistentFieldList.add(annotatedField);
        }
        Object[] orderedFields = enhancementContext.order(persistentFieldList.toArray(new EnhancerImpl.AnnotatedFieldDescription[0]));
        log.debugf("Persistent fields for entity %s: %s", managedCtClass.getName(), Arrays.toString(orderedFields));
        return new PersistentAttributeTransformer(managedCtClass, enhancementContext, classPool, (EnhancerImpl.AnnotatedFieldDescription[])orderedFields);
    }

    private static Collection<EnhancerImpl.AnnotatedFieldDescription> collectInheritPersistentFields(TypeDefinition managedCtClass, ByteBuddyEnhancementContext enhancementContext) {
        if (managedCtClass == null || managedCtClass.represents(Object.class)) {
            return Collections.emptyList();
        }
        TypeDescription.Generic managedCtSuperclass = managedCtClass.getSuperClass();
        if (enhancementContext.isEntityClass(managedCtSuperclass.asErasure())) {
            return Collections.emptyList();
        }
        if (!enhancementContext.isMappedSuperclassClass(managedCtSuperclass.asErasure())) {
            return PersistentAttributeTransformer.collectInheritPersistentFields((TypeDefinition)managedCtSuperclass, enhancementContext);
        }
        log.debugf("Found @MappedSuperclass %s to collectPersistenceFields", managedCtSuperclass);
        ArrayList<EnhancerImpl.AnnotatedFieldDescription> persistentFieldList = new ArrayList<EnhancerImpl.AnnotatedFieldDescription>();
        for (FieldDescription ctField : managedCtSuperclass.getDeclaredFields()) {
            if (ctField.getName().startsWith("$$_hibernate_") || "this$0".equals(ctField.getName())) continue;
            EnhancerImpl.AnnotatedFieldDescription annotatedField = new EnhancerImpl.AnnotatedFieldDescription(enhancementContext, ctField);
            if (ctField.isStatic() || !enhancementContext.isPersistentField(annotatedField)) continue;
            persistentFieldList.add(annotatedField);
        }
        persistentFieldList.addAll(PersistentAttributeTransformer.collectInheritPersistentFields((TypeDefinition)managedCtSuperclass, enhancementContext));
        return persistentFieldList;
    }

    public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, final MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
        return new MethodVisitor(OpenedClassReader.ASM_API, methodVisitor){

            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                EnhancerImpl.AnnotatedFieldDescription enhancedField = PersistentAttributeTransformer.this.getEnhancedField(owner, name, desc);
                if (enhancedField != null) {
                    switch (opcode) {
                        case 180: {
                            methodVisitor.visitMethodInsn(182, owner, "$$_hibernate_read_" + name, "()" + desc, false);
                            return;
                        }
                        case 181: {
                            if (enhancedField.getFieldDescription().isFinal()) break;
                            methodVisitor.visitMethodInsn(182, owner, "$$_hibernate_write_" + name, "(" + desc + ")V", false);
                            return;
                        }
                    }
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        };
    }

    private EnhancerImpl.AnnotatedFieldDescription getEnhancedField(String owner, String name, String desc) {
        for (EnhancerImpl.AnnotatedFieldDescription enhancedField : this.enhancedFields) {
            if (!enhancedField.getName().equals(name) || !enhancedField.getDescriptor().equals(desc) || !enhancedField.getDeclaringType().asErasure().getInternalName().equals(owner)) continue;
            return enhancedField;
        }
        return null;
    }

    DynamicType.Builder<?> applyTo(DynamicType.Builder<?> builder, EnhancerImpl.EnhancementStatus es) {
        builder = es.applySuperInterfaceOptimisations((DynamicType.Builder<?>)builder);
        boolean compositeOwner = false;
        builder = builder.visit((AsmVisitorWrapper)new AsmVisitorWrapper.ForDeclaredMethods().invokable(NOT_HIBERNATE_GENERATED, new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper[]{this}));
        ArrayList<FieldDescription.InDefinedShape> enhancedFieldsAsDefined = new ArrayList<FieldDescription.InDefinedShape>();
        for (EnhancerImpl.AnnotatedFieldDescription f : this.enhancedFields) {
            enhancedFieldsAsDefined.add(f.asDefined());
        }
        builder = builder.visit((AsmVisitorWrapper)new ModifierAdjustment().withFieldModifiers((ElementMatcher)ElementMatchers.anyOf(enhancedFieldsAsDefined), new ModifierContributor.ForField[]{REMOVE_FINAL_MODIFIER}));
        for (EnhancerImpl.AnnotatedFieldDescription enhancedField : this.enhancedFields) {
            builder = builder.defineMethod("$$_hibernate_read_" + enhancedField.getName(), (TypeDefinition)enhancedField.getType().asErasure(), new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.fieldReader(enhancedField));
            if (!enhancedField.getFieldDescription().isFinal()) {
                builder = builder.defineMethod("$$_hibernate_write_" + enhancedField.getName(), (TypeDefinition)TypeDescription.VOID, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new TypeDefinition[]{enhancedField.getType().asErasure()}).intercept(this.fieldWriter(enhancedField));
            }
            if (compositeOwner || this.enhancementContext.isMappedSuperclassClass(this.managedCtClass) || !enhancedField.hasAnnotation(Embedded.class) || !this.enhancementContext.isCompositeClass(enhancedField.getType().asErasure()) || !this.enhancementContext.doDirtyCheckingInline(this.managedCtClass)) continue;
            compositeOwner = true;
        }
        if (compositeOwner) {
            builder = builder.implement(new Type[]{CompositeOwner.class});
            if (this.enhancementContext.isCompositeClass(this.managedCtClass)) {
                builder = builder.defineMethod("$$_hibernate_trackChange", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{String.class}).intercept(Advice.to(CodeTemplates.CompositeOwnerDirtyCheckingHandler.class).wrap((Implementation)StubMethod.INSTANCE));
            }
        }
        if (this.enhancementContext.doExtendedEnhancement(this.managedCtClass)) {
            builder = this.applyExtended((DynamicType.Builder<?>)builder, es);
        }
        return builder;
    }

    private Implementation fieldReader(EnhancerImpl.AnnotatedFieldDescription enhancedField) {
        if (this.enhancementContext.isMappedSuperclassClass(this.managedCtClass)) {
            return FieldAccessor.ofField((String)enhancedField.getName()).in(enhancedField.getDeclaringType().asErasure());
        }
        if (!this.enhancementContext.hasLazyLoadableAttributes(this.managedCtClass) || !this.enhancementContext.isLazyLoadable(enhancedField)) {
            if (enhancedField.getDeclaringType().asErasure().equals(this.managedCtClass)) {
                return FieldAccessor.ofField((String)enhancedField.getName()).in(enhancedField.getDeclaringType().asErasure());
            }
            return new Implementation.Simple(new ByteCodeAppender[]{new FieldMethodReader(this.managedCtClass, enhancedField)});
        }
        return new Implementation.Simple(new ByteCodeAppender[]{FieldReaderAppender.of(this.managedCtClass, enhancedField)});
    }

    private Implementation fieldWriter(EnhancerImpl.AnnotatedFieldDescription enhancedField) {
        Implementation implementation = this.fieldWriterImplementation(enhancedField);
        if (!this.enhancementContext.isMappedSuperclassClass(this.managedCtClass)) {
            implementation = InlineDirtyCheckingHandler.wrap(this.managedCtClass, this.enhancementContext, enhancedField, implementation);
            implementation = BiDirectionalAssociationHandler.wrap(this.managedCtClass, this.enhancementContext, enhancedField, implementation);
        }
        return implementation;
    }

    private Implementation fieldWriterImplementation(EnhancerImpl.AnnotatedFieldDescription enhancedField) {
        if (this.enhancementContext.isMappedSuperclassClass(this.managedCtClass)) {
            return FieldAccessor.ofField((String)enhancedField.getName()).in(enhancedField.getDeclaringType().asErasure());
        }
        if (!this.enhancementContext.hasLazyLoadableAttributes(this.managedCtClass) || !this.enhancementContext.isLazyLoadable(enhancedField)) {
            if (enhancedField.getDeclaringType().asErasure().equals(this.managedCtClass)) {
                return FieldAccessor.ofField((String)enhancedField.getName()).in(enhancedField.getDeclaringType().asErasure());
            }
            return new Implementation.Simple(new ByteCodeAppender[]{new FieldMethodWriter(this.managedCtClass, enhancedField)});
        }
        return new Implementation.Simple(new ByteCodeAppender[]{FieldWriterAppender.of(this.managedCtClass, enhancedField)});
    }

    DynamicType.Builder<?> applyExtended(DynamicType.Builder<?> builder, EnhancerImpl.EnhancementStatus es) {
        FieldAccessEnhancer enhancer = new FieldAccessEnhancer(this.managedCtClass, this.enhancementContext, this.classPool);
        return builder.visit((AsmVisitorWrapper)new AsmVisitorWrapper.ForDeclaredMethods().invokable(NOT_HIBERNATE_GENERATED, new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper[]{enhancer}));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || PersistentAttributeTransformer.class != o.getClass()) {
            return false;
        }
        PersistentAttributeTransformer that = (PersistentAttributeTransformer)o;
        return Objects.equals(this.managedCtClass, that.managedCtClass);
    }

    public int hashCode() {
        return this.managedCtClass.hashCode();
    }

    private static class FieldMethodWriter
    implements ByteCodeAppender {
        private final TypeDescription managedCtClass;
        private final EnhancerImpl.AnnotatedFieldDescription persistentField;

        private FieldMethodWriter(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
            this.managedCtClass = managedCtClass;
            this.persistentField = persistentField;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(net.bytebuddy.jar.asm.Type.getType((String)this.persistentField.getType().asErasure().getDescriptor()).getOpcode(21), 1);
            methodVisitor.visitMethodInsn(183, this.managedCtClass.getSuperClass().asErasure().getInternalName(), "$$_hibernate_write_" + this.persistentField.getName(), net.bytebuddy.jar.asm.Type.getMethodDescriptor((net.bytebuddy.jar.asm.Type)net.bytebuddy.jar.asm.Type.getType(Void.TYPE), (net.bytebuddy.jar.asm.Type[])new net.bytebuddy.jar.asm.Type[]{net.bytebuddy.jar.asm.Type.getType((String)this.persistentField.getType().asErasure().getDescriptor())}), false);
            methodVisitor.visitInsn(177);
            return new ByteCodeAppender.Size(1 + this.persistentField.getType().getStackSize().getSize(), instrumentedMethod.getStackSize());
        }
    }

    private static class FieldMethodReader
    implements ByteCodeAppender {
        private final TypeDescription managedCtClass;
        private final EnhancerImpl.AnnotatedFieldDescription persistentField;

        private FieldMethodReader(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
            this.managedCtClass = managedCtClass;
            this.persistentField = persistentField;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(183, this.managedCtClass.getSuperClass().asErasure().getInternalName(), "$$_hibernate_read_" + this.persistentField.getName(), net.bytebuddy.jar.asm.Type.getMethodDescriptor((net.bytebuddy.jar.asm.Type)net.bytebuddy.jar.asm.Type.getType((String)this.persistentField.getType().asErasure().getDescriptor()), (net.bytebuddy.jar.asm.Type[])new net.bytebuddy.jar.asm.Type[0]), false);
            methodVisitor.visitInsn(net.bytebuddy.jar.asm.Type.getType((String)this.persistentField.getType().asErasure().getDescriptor()).getOpcode(172));
            return new ByteCodeAppender.Size(this.persistentField.getType().getStackSize().getSize(), instrumentedMethod.getStackSize());
        }
    }
}


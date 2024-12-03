/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.AccessType
 *  javax.persistence.ManyToMany
 *  javax.persistence.ManyToOne
 *  javax.persistence.OneToMany
 *  javax.persistence.OneToOne
 *  net.bytebuddy.asm.Advice
 *  net.bytebuddy.description.annotation.AnnotationDescription$Loadable
 *  net.bytebuddy.description.annotation.AnnotationValue
 *  net.bytebuddy.description.field.FieldDescription
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.method.MethodDescription$ForLoadedMethod
 *  net.bytebuddy.description.method.MethodDescription$InDefinedShape
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.description.type.TypeDescription$Generic
 *  net.bytebuddy.dynamic.scaffold.FieldLocator$ForClassHierarchy$Factory
 *  net.bytebuddy.dynamic.scaffold.InstrumentedType
 *  net.bytebuddy.implementation.Implementation
 *  net.bytebuddy.implementation.Implementation$Context
 *  net.bytebuddy.implementation.Implementation$Target
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender$Size
 *  net.bytebuddy.jar.asm.MethodVisitor
 *  net.bytebuddy.jar.asm.Type
 *  net.bytebuddy.utility.OpenedClassReader
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.utility.OpenedClassReader;
import org.hibernate.bytecode.enhance.internal.bytebuddy.ByteBuddyEnhancementContext;
import org.hibernate.bytecode.enhance.internal.bytebuddy.CodeTemplates;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.bytecode.enhance.spi.EnhancementException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

final class BiDirectionalAssociationHandler
implements Implementation {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(BiDirectionalAssociationHandler.class);
    private final Implementation delegate;
    private final TypeDescription targetEntity;
    private final TypeDescription targetType;
    private final String mappedBy;

    static Implementation wrap(TypeDescription managedCtClass, ByteBuddyEnhancementContext enhancementContext, EnhancerImpl.AnnotatedFieldDescription persistentField, Implementation implementation) {
        if (!enhancementContext.doBiDirectionalAssociationManagement(persistentField)) {
            return implementation;
        }
        TypeDescription targetEntity = BiDirectionalAssociationHandler.getTargetEntityClass(managedCtClass, persistentField);
        if (targetEntity == null) {
            return implementation;
        }
        String mappedBy = BiDirectionalAssociationHandler.getMappedBy(persistentField, targetEntity, enhancementContext);
        if (mappedBy == null || mappedBy.isEmpty()) {
            log.infof("Bi-directional association not managed for field [%s#%s]: Could not find target field in [%s]", managedCtClass.getName(), persistentField.getName(), targetEntity.getCanonicalName());
            return implementation;
        }
        TypeDescription targetType = FieldLocator.ForClassHierarchy.Factory.INSTANCE.make(targetEntity).locate(mappedBy).getField().getType().asErasure();
        if (persistentField.hasAnnotation(OneToOne.class)) {
            implementation = Advice.withCustomMapping().bind(CodeTemplates.FieldValue.class, persistentField.getFieldDescription()).bind(CodeTemplates.MappedBy.class, (Object)mappedBy).to(CodeTemplates.OneToOneHandler.class).wrap(implementation);
        }
        if (persistentField.hasAnnotation(OneToMany.class)) {
            implementation = Advice.withCustomMapping().bind(CodeTemplates.FieldValue.class, persistentField.getFieldDescription()).bind(CodeTemplates.MappedBy.class, (Object)mappedBy).to(persistentField.getType().asErasure().isAssignableTo(Map.class) ? CodeTemplates.OneToManyOnMapHandler.class : CodeTemplates.OneToManyOnCollectionHandler.class).wrap(implementation);
        }
        if (persistentField.hasAnnotation(ManyToOne.class)) {
            implementation = Advice.withCustomMapping().bind(CodeTemplates.FieldValue.class, persistentField.getFieldDescription()).bind(CodeTemplates.MappedBy.class, (Object)mappedBy).to(CodeTemplates.ManyToOneHandler.class).wrap(implementation);
        }
        if (persistentField.hasAnnotation(ManyToMany.class)) {
            if (persistentField.getType().asErasure().isAssignableTo(Map.class) || targetType.isAssignableTo(Map.class)) {
                log.infof("Bi-directional association not managed for field [%s#%s]: @ManyToMany in java.util.Map attribute not supported ", managedCtClass.getName(), persistentField.getName());
                return implementation;
            }
            implementation = Advice.withCustomMapping().bind(CodeTemplates.FieldValue.class, persistentField.getFieldDescription()).bind(CodeTemplates.MappedBy.class, (Object)mappedBy).to(CodeTemplates.ManyToManyHandler.class).wrap(implementation);
        }
        return new BiDirectionalAssociationHandler(implementation, targetEntity, targetType, mappedBy);
    }

    public static TypeDescription getTargetEntityClass(TypeDescription managedCtClass, EnhancerImpl.AnnotatedFieldDescription persistentField) {
        try {
            AnnotationDescription.Loadable<OneToOne> oto = persistentField.getAnnotation(OneToOne.class);
            AnnotationDescription.Loadable<OneToMany> otm = persistentField.getAnnotation(OneToMany.class);
            AnnotationDescription.Loadable<ManyToOne> mto = persistentField.getAnnotation(ManyToOne.class);
            AnnotationDescription.Loadable<ManyToMany> mtm = persistentField.getAnnotation(ManyToMany.class);
            if (oto == null && otm == null && mto == null && mtm == null) {
                return null;
            }
            AnnotationValue targetClass = null;
            if (oto != null) {
                targetClass = oto.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(OneToOne.class.getDeclaredMethod("targetEntity", new Class[0])));
            }
            if (otm != null) {
                targetClass = otm.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(OneToMany.class.getDeclaredMethod("targetEntity", new Class[0])));
            }
            if (mto != null) {
                targetClass = mto.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(ManyToOne.class.getDeclaredMethod("targetEntity", new Class[0])));
            }
            if (mtm != null) {
                targetClass = mtm.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(ManyToMany.class.getDeclaredMethod("targetEntity", new Class[0])));
            }
            if (targetClass == null) {
                log.infof("Bi-directional association not managed for field [%s#%s]: Could not find target type", managedCtClass.getName(), persistentField.getName());
                return null;
            }
            if (!((TypeDescription)targetClass.resolve(TypeDescription.class)).represents(Void.TYPE)) {
                return (TypeDescription)targetClass.resolve(TypeDescription.class);
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return BiDirectionalAssociationHandler.entityType(BiDirectionalAssociationHandler.target(persistentField));
    }

    private static TypeDescription.Generic target(EnhancerImpl.AnnotatedFieldDescription persistentField) {
        AnnotationDescription.Loadable access = persistentField.getDeclaringType().asErasure().getDeclaredAnnotations().ofType(Access.class);
        if (access != null && ((Access)access.load()).value() == AccessType.FIELD) {
            return persistentField.getType();
        }
        Optional<MethodDescription> getter = persistentField.getGetter();
        if (getter.isPresent()) {
            return getter.get().getReturnType();
        }
        return persistentField.getType();
    }

    private static String getMappedBy(EnhancerImpl.AnnotatedFieldDescription target, TypeDescription targetEntity, ByteBuddyEnhancementContext context) {
        String mappedBy = BiDirectionalAssociationHandler.getMappedByNotManyToMany(target);
        if (mappedBy == null || mappedBy.isEmpty()) {
            return BiDirectionalAssociationHandler.getMappedByManyToMany(target, targetEntity, context);
        }
        return BiDirectionalAssociationHandler.isValidMappedBy(target, targetEntity, mappedBy, context) ? mappedBy : "";
    }

    private static boolean isValidMappedBy(EnhancerImpl.AnnotatedFieldDescription persistentField, TypeDescription targetEntity, String mappedBy, ByteBuddyEnhancementContext context) {
        try {
            FieldDescription f = FieldLocator.ForClassHierarchy.Factory.INSTANCE.make(targetEntity).locate(mappedBy).getField();
            EnhancerImpl.AnnotatedFieldDescription annotatedF = new EnhancerImpl.AnnotatedFieldDescription(context, f);
            return context.isPersistentField(annotatedF) && persistentField.getDeclaringType().asErasure().isAssignableTo(BiDirectionalAssociationHandler.entityType(f.getType()));
        }
        catch (IllegalStateException e) {
            return false;
        }
    }

    private static String getMappedByNotManyToMany(EnhancerImpl.AnnotatedFieldDescription target) {
        try {
            AnnotationDescription.Loadable<OneToOne> oto = target.getAnnotation(OneToOne.class);
            if (oto != null) {
                return (String)oto.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(OneToOne.class.getDeclaredMethod("mappedBy", new Class[0]))).resolve(String.class);
            }
            AnnotationDescription.Loadable<OneToMany> otm = target.getAnnotation(OneToMany.class);
            if (otm != null) {
                return (String)otm.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(OneToMany.class.getDeclaredMethod("mappedBy", new Class[0]))).resolve(String.class);
            }
            AnnotationDescription.Loadable<ManyToMany> mtm = target.getAnnotation(ManyToMany.class);
            if (mtm != null) {
                return (String)mtm.getValue((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(ManyToMany.class.getDeclaredMethod("mappedBy", new Class[0]))).resolve(String.class);
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return null;
    }

    private static String getMappedByManyToMany(EnhancerImpl.AnnotatedFieldDescription target, TypeDescription targetEntity, ByteBuddyEnhancementContext context) {
        for (FieldDescription f : targetEntity.getDeclaredFields()) {
            EnhancerImpl.AnnotatedFieldDescription annotatedF = new EnhancerImpl.AnnotatedFieldDescription(context, f);
            if (!context.isPersistentField(annotatedF) || !target.getName().equals(BiDirectionalAssociationHandler.getMappedByNotManyToMany(annotatedF)) || !target.getDeclaringType().asErasure().isAssignableTo(BiDirectionalAssociationHandler.entityType(annotatedF.getType()))) continue;
            log.debugf("mappedBy association for field [%s#%s] is [%s#%s]", new Object[]{target.getDeclaringType().asErasure().getName(), target.getName(), targetEntity.getName(), f.getName()});
            return f.getName();
        }
        return null;
    }

    private static TypeDescription entityType(TypeDescription.Generic type) {
        if (type.getSort().isParameterized()) {
            if (type.asErasure().isAssignableTo(Collection.class)) {
                return ((TypeDescription.Generic)type.getTypeArguments().get(0)).asErasure();
            }
            if (type.asErasure().isAssignableTo(Map.class)) {
                return ((TypeDescription.Generic)type.getTypeArguments().get(1)).asErasure();
            }
        }
        return type.asErasure();
    }

    private BiDirectionalAssociationHandler(Implementation delegate, TypeDescription targetEntity, TypeDescription targetType, String mappedBy) {
        this.delegate = delegate;
        this.targetEntity = targetEntity;
        this.targetType = targetType;
        this.mappedBy = mappedBy;
    }

    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new WrappingAppender(this.delegate.appender(implementationTarget));
    }

    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return this.delegate.prepare(instrumentedType);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || BiDirectionalAssociationHandler.class != o.getClass()) {
            return false;
        }
        BiDirectionalAssociationHandler that = (BiDirectionalAssociationHandler)o;
        return Objects.equals(this.delegate, that.delegate) && Objects.equals(this.targetEntity, that.targetEntity) && Objects.equals(this.targetType, that.targetType) && Objects.equals(this.mappedBy, that.mappedBy);
    }

    public int hashCode() {
        return Objects.hash(this.delegate, this.targetEntity, this.targetType, this.mappedBy);
    }

    private class WrappingAppender
    implements ByteCodeAppender {
        private final ByteCodeAppender delegate;

        private WrappingAppender(ByteCodeAppender delegate) {
            this.delegate = delegate;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            return this.delegate.apply(new MethodVisitor(OpenedClassReader.ASM_API, methodVisitor){

                /*
                 * Enabled force condition propagation
                 * Lifted jumps to return sites
                 */
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (owner.startsWith(Type.getInternalName(CodeTemplates.class))) {
                        if (name.equals("getter")) {
                            super.visitTypeInsn(192, BiDirectionalAssociationHandler.this.targetEntity.getInternalName());
                            super.visitMethodInsn(182, BiDirectionalAssociationHandler.this.targetEntity.getInternalName(), "$$_hibernate_read_" + BiDirectionalAssociationHandler.this.mappedBy, Type.getMethodDescriptor((Type)Type.getType((String)BiDirectionalAssociationHandler.this.targetType.getDescriptor()), (Type[])new Type[0]), false);
                            return;
                        } else if (name.equals("setterSelf")) {
                            super.visitInsn(87);
                            super.visitTypeInsn(192, BiDirectionalAssociationHandler.this.targetEntity.getInternalName());
                            super.visitVarInsn(25, 0);
                            super.visitMethodInsn(182, BiDirectionalAssociationHandler.this.targetEntity.getInternalName(), "$$_hibernate_write_" + BiDirectionalAssociationHandler.this.mappedBy, Type.getMethodDescriptor((Type)Type.getType(Void.TYPE), (Type[])new Type[]{Type.getType((String)BiDirectionalAssociationHandler.this.targetType.getDescriptor())}), false);
                            return;
                        } else {
                            if (!name.equals("setterNull")) throw new EnhancementException("Unknown template method: " + name);
                            super.visitInsn(87);
                            super.visitTypeInsn(192, BiDirectionalAssociationHandler.this.targetEntity.getInternalName());
                            super.visitInsn(1);
                            super.visitMethodInsn(182, BiDirectionalAssociationHandler.this.targetEntity.getInternalName(), "$$_hibernate_write_" + BiDirectionalAssociationHandler.this.mappedBy, Type.getMethodDescriptor((Type)Type.getType(Void.TYPE), (Type[])new Type[]{Type.getType((String)BiDirectionalAssociationHandler.this.targetType.getDescriptor())}), false);
                        }
                        return;
                    } else {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }
                }
            }, implementationContext, instrumentedMethod);
        }
    }
}


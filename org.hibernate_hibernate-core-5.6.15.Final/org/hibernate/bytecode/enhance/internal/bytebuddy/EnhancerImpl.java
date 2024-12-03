/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.AccessType
 *  javax.persistence.Transient
 *  net.bytebuddy.asm.Advice
 *  net.bytebuddy.asm.Advice$OffsetMapping
 *  net.bytebuddy.description.annotation.AnnotationDescription
 *  net.bytebuddy.description.annotation.AnnotationDescription$Builder
 *  net.bytebuddy.description.annotation.AnnotationDescription$Loadable
 *  net.bytebuddy.description.annotation.AnnotationList
 *  net.bytebuddy.description.annotation.AnnotationList$Explicit
 *  net.bytebuddy.description.field.FieldDescription
 *  net.bytebuddy.description.field.FieldDescription$InDefinedShape
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.modifier.FieldPersistence
 *  net.bytebuddy.description.modifier.ModifierContributor$ForField
 *  net.bytebuddy.description.modifier.ModifierContributor$ForMethod
 *  net.bytebuddy.description.modifier.Visibility
 *  net.bytebuddy.description.type.TypeDefinition
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.description.type.TypeDescription$Generic
 *  net.bytebuddy.dynamic.ClassFileLocator
 *  net.bytebuddy.dynamic.ClassFileLocator$ForClassLoader
 *  net.bytebuddy.dynamic.ClassFileLocator$Resolution
 *  net.bytebuddy.dynamic.ClassFileLocator$Resolution$Explicit
 *  net.bytebuddy.dynamic.ClassFileLocator$Simple
 *  net.bytebuddy.dynamic.DynamicType$Builder
 *  net.bytebuddy.implementation.FieldAccessor
 *  net.bytebuddy.implementation.FixedValue
 *  net.bytebuddy.implementation.Implementation
 *  net.bytebuddy.implementation.StubMethod
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatchers
 *  net.bytebuddy.pool.TypePool
 *  net.bytebuddy.pool.TypePool$Default$WithLazyResolution
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Transient;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.hibernate.bytecode.enhance.internal.bytebuddy.ByteBuddyEnhancementContext;
import org.hibernate.bytecode.enhance.internal.bytebuddy.CodeTemplates;
import org.hibernate.bytecode.enhance.internal.bytebuddy.PersistentAttributeTransformer;
import org.hibernate.bytecode.enhance.internal.tracker.CompositeOwnerTracker;
import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;
import org.hibernate.bytecode.enhance.spi.CollectionTracker;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.EnhancementException;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.enhance.spi.UnloadedField;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.bytecode.internal.bytebuddy.ByteBuddyState;
import org.hibernate.engine.spi.CompositeOwner;
import org.hibernate.engine.spi.CompositeTracker;
import org.hibernate.engine.spi.EnhancedEntity;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.ExtendedSelfDirtinessTracker;
import org.hibernate.engine.spi.Managed;
import org.hibernate.engine.spi.ManagedComposite;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.ManagedMappedSuperclass;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class EnhancerImpl
implements Enhancer {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(Enhancer.class);
    private static final AnnotationDescription TRANSIENT_ANNOTATION = AnnotationDescription.Builder.ofType(Transient.class).build();
    protected final ByteBuddyEnhancementContext enhancementContext;
    private final ByteBuddyState byteBuddyState;
    private final EnhancerClassFileLocator classFileLocator;
    private final TypePool typePool;
    private final ClassFileLocator adviceLocator = ClassFileLocator.ForClassLoader.of((ClassLoader)CodeTemplates.class.getClassLoader());
    private final Implementation implementationTrackChange = Advice.to(CodeTemplates.TrackChange.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationGetDirtyAttributesWithoutCollections = Advice.to(CodeTemplates.GetDirtyAttributesWithoutCollections.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationAreFieldsDirtyWithoutCollections = Advice.to(CodeTemplates.AreFieldsDirtyWithoutCollections.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationClearDirtyAttributesWithoutCollections = Advice.to(CodeTemplates.ClearDirtyAttributesWithoutCollections.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationSuspendDirtyTracking = Advice.to(CodeTemplates.SuspendDirtyTracking.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationGetDirtyAttributes = Advice.to(CodeTemplates.GetDirtyAttributes.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationAreFieldsDirty = Advice.to(CodeTemplates.AreFieldsDirty.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationGetCollectionTrackerWithoutCollections = Advice.to(CodeTemplates.GetCollectionTrackerWithoutCollections.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationClearDirtyAttributes = Advice.to(CodeTemplates.ClearDirtyAttributes.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Advice adviceInitializeLazyAttributeLoadingInterceptor = Advice.to(CodeTemplates.InitializeLazyAttributeLoadingInterceptor.class, (ClassFileLocator)this.adviceLocator);
    private final Implementation implementationSetOwner = Advice.to(CodeTemplates.SetOwner.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);
    private final Implementation implementationClearOwner = Advice.to(CodeTemplates.ClearOwner.class, (ClassFileLocator)this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE);

    public EnhancerImpl(EnhancementContext enhancementContext, ByteBuddyState byteBuddyState) {
        this.enhancementContext = new ByteBuddyEnhancementContext(enhancementContext);
        this.byteBuddyState = byteBuddyState;
        this.classFileLocator = new EnhancerClassFileLocator(enhancementContext.getLoadingClassLoader());
        this.typePool = this.buildTypePool((ClassFileLocator)this.classFileLocator);
    }

    @Override
    public byte[] enhance(String className, byte[] originalBytes) throws EnhancementException {
        String safeClassName = className.replace('/', '.');
        this.classFileLocator.setClassNameAndBytes(safeClassName, originalBytes);
        try {
            TypeDescription typeDescription = this.typePool.describe(safeClassName).resolve();
            return this.byteBuddyState.rewrite(this.typePool, safeClassName, byteBuddy -> this.doEnhance(byteBuddy.ignore((ElementMatcher)ElementMatchers.isDefaultFinalizer()).redefine(typeDescription, ClassFileLocator.Simple.of((String)safeClassName, (byte[])originalBytes)), typeDescription));
        }
        catch (RuntimeException e) {
            throw new EnhancementException("Failed to enhance class " + className, e);
        }
    }

    private TypePool buildTypePool(ClassFileLocator classFileLocator) {
        return TypePool.Default.WithLazyResolution.of((ClassFileLocator)classFileLocator);
    }

    private DynamicType.Builder<?> doEnhance(DynamicType.Builder<?> builder, TypeDescription managedCtClass) {
        if (managedCtClass.isInterface()) {
            log.debugf("Skipping enhancement of [%s]: it's an interface!", managedCtClass.getName());
            return null;
        }
        if (this.alreadyEnhanced(managedCtClass)) {
            log.debugf("Skipping enhancement of [%s]: already enhanced", managedCtClass.getName());
            return null;
        }
        EnhancementStatus es = new EnhancementStatus(managedCtClass.getName());
        if (this.enhancementContext.isEntityClass(managedCtClass)) {
            log.debugf("Enhancing [%s] as Entity", managedCtClass.getName());
            builder = builder.implement(new Type[]{ManagedEntity.class}).defineMethod("$$_hibernate_getEntityInstance", Object.class, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept((Implementation)FixedValue.self());
            es.enabledInterfaceManagedEntity();
            builder = EnhancerImpl.addFieldWithGetterAndSetter(builder, EntityEntry.class, "$$_hibernate_entityEntryHolder", "$$_hibernate_getEntityEntry", "$$_hibernate_setEntityEntry");
            builder = EnhancerImpl.addFieldWithGetterAndSetter(builder, ManagedEntity.class, "$$_hibernate_previousManagedEntity", "$$_hibernate_getPreviousManagedEntity", "$$_hibernate_setPreviousManagedEntity");
            builder = EnhancerImpl.addFieldWithGetterAndSetter(builder, ManagedEntity.class, "$$_hibernate_nextManagedEntity", "$$_hibernate_getNextManagedEntity", "$$_hibernate_setNextManagedEntity");
            builder = this.addInterceptorHandling((DynamicType.Builder<?>)builder, managedCtClass, es);
            if (this.enhancementContext.doDirtyCheckingInline(managedCtClass)) {
                List<AnnotatedFieldDescription> collectionFields = this.collectCollectionFields(managedCtClass);
                if (collectionFields.isEmpty()) {
                    builder = builder.implement(new Type[]{SelfDirtinessTracker.class}).defineField("$$_hibernate_tracker", DirtyTracker.class, new ModifierContributor.ForField[]{FieldPersistence.TRANSIENT, Visibility.PRIVATE}).annotateField(new AnnotationDescription[]{TRANSIENT_ANNOTATION}).defineMethod("$$_hibernate_trackChange", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{String.class}).intercept(this.implementationTrackChange).defineMethod("$$_hibernate_getDirtyAttributes", String[].class, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationGetDirtyAttributesWithoutCollections).defineMethod("$$_hibernate_hasDirtyAttributes", Boolean.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationAreFieldsDirtyWithoutCollections).defineMethod("$$_hibernate_clearDirtyAttributes", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationClearDirtyAttributesWithoutCollections).defineMethod("$$_hibernate_suspendDirtyTracking", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{Boolean.TYPE}).intercept(this.implementationSuspendDirtyTracking).defineMethod("$$_hibernate_getCollectionTracker", CollectionTracker.class, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationGetCollectionTrackerWithoutCollections);
                    es.enabledInterfaceSelfDirtinessTracker();
                } else {
                    builder = builder.implement(new Type[]{ExtendedSelfDirtinessTracker.class}).defineField("$$_hibernate_tracker", DirtyTracker.class, new ModifierContributor.ForField[]{FieldPersistence.TRANSIENT, Visibility.PRIVATE}).annotateField(new AnnotationDescription[]{TRANSIENT_ANNOTATION}).defineField("$$_hibernate_collectionTracker", CollectionTracker.class, new ModifierContributor.ForField[]{FieldPersistence.TRANSIENT, Visibility.PRIVATE}).annotateField(new AnnotationDescription[]{TRANSIENT_ANNOTATION}).defineMethod("$$_hibernate_trackChange", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{String.class}).intercept(this.implementationTrackChange).defineMethod("$$_hibernate_getDirtyAttributes", String[].class, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationGetDirtyAttributes).defineMethod("$$_hibernate_hasDirtyAttributes", Boolean.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationAreFieldsDirty).defineMethod("$$_hibernate_clearDirtyAttributes", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(this.implementationClearDirtyAttributes).defineMethod("$$_hibernate_suspendDirtyTracking", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{Boolean.TYPE}).intercept(this.implementationSuspendDirtyTracking).defineMethod("$$_hibernate_getCollectionTracker", CollectionTracker.class, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept((Implementation)FieldAccessor.ofField((String)"$$_hibernate_collectionTracker"));
                    StubMethod isDirty = StubMethod.INSTANCE;
                    StubMethod getDirtyNames = StubMethod.INSTANCE;
                    StubMethod clearDirtyNames = StubMethod.INSTANCE;
                    for (AnnotatedFieldDescription collectionField : collectionFields) {
                        Class adviceClearDirtyNames;
                        Class adviceGetDirtyNames;
                        Class adviceIsDirty;
                        String collectionFieldName = collectionField.getName();
                        if (collectionField.getType().asErasure().isAssignableTo(Map.class)) {
                            adviceIsDirty = CodeTemplates.MapAreCollectionFieldsDirty.class;
                            adviceGetDirtyNames = CodeTemplates.MapGetCollectionFieldDirtyNames.class;
                            adviceClearDirtyNames = CodeTemplates.MapGetCollectionClearDirtyNames.class;
                        } else {
                            adviceIsDirty = CodeTemplates.CollectionAreCollectionFieldsDirty.class;
                            adviceGetDirtyNames = CodeTemplates.CollectionGetCollectionFieldDirtyNames.class;
                            adviceClearDirtyNames = CodeTemplates.CollectionGetCollectionClearDirtyNames.class;
                        }
                        if (collectionField.isVisibleTo(managedCtClass)) {
                            FieldDescription fieldDescription = collectionField.getFieldDescription();
                            isDirty = Advice.withCustomMapping().bind(CodeTemplates.FieldName.class, (Object)collectionFieldName).bind(CodeTemplates.FieldValue.class, fieldDescription).to(adviceIsDirty, this.adviceLocator).wrap((Implementation)isDirty);
                            getDirtyNames = Advice.withCustomMapping().bind(CodeTemplates.FieldName.class, (Object)collectionFieldName).bind(CodeTemplates.FieldValue.class, fieldDescription).to(adviceGetDirtyNames, this.adviceLocator).wrap((Implementation)getDirtyNames);
                            clearDirtyNames = Advice.withCustomMapping().bind(CodeTemplates.FieldName.class, (Object)collectionFieldName).bind(CodeTemplates.FieldValue.class, fieldDescription).to(adviceClearDirtyNames, this.adviceLocator).wrap((Implementation)clearDirtyNames);
                            continue;
                        }
                        CodeTemplates.GetterMapping getterMapping = new CodeTemplates.GetterMapping(collectionField.getFieldDescription());
                        isDirty = Advice.withCustomMapping().bind(CodeTemplates.FieldName.class, (Object)collectionFieldName).bind(CodeTemplates.FieldValue.class, (Advice.OffsetMapping)getterMapping).to(adviceIsDirty, this.adviceLocator).wrap((Implementation)isDirty);
                        getDirtyNames = Advice.withCustomMapping().bind(CodeTemplates.FieldName.class, (Object)collectionFieldName).bind(CodeTemplates.FieldValue.class, (Advice.OffsetMapping)getterMapping).to(adviceGetDirtyNames, this.adviceLocator).wrap((Implementation)getDirtyNames);
                        clearDirtyNames = Advice.withCustomMapping().bind(CodeTemplates.FieldName.class, (Object)collectionFieldName).bind(CodeTemplates.FieldValue.class, (Advice.OffsetMapping)getterMapping).to(adviceClearDirtyNames, this.adviceLocator).wrap((Implementation)clearDirtyNames);
                    }
                    if (this.enhancementContext.hasLazyLoadableAttributes(managedCtClass)) {
                        clearDirtyNames = this.adviceInitializeLazyAttributeLoadingInterceptor.wrap((Implementation)clearDirtyNames);
                    }
                    builder = builder.defineMethod("$$_hibernate_areCollectionFieldsDirty", Boolean.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept((Implementation)isDirty).defineMethod("$$_hibernate_getCollectionFieldDirtyNames", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{DirtyTracker.class}).intercept((Implementation)getDirtyNames).defineMethod("$$_hibernate_clearDirtyCollectionNames", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept(Advice.withCustomMapping().to(CodeTemplates.ClearDirtyCollectionNames.class, this.adviceLocator).wrap((Implementation)StubMethod.INSTANCE)).defineMethod("$$_hibernate_removeDirtyFields", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{LazyAttributeLoadingInterceptor.class}).intercept((Implementation)clearDirtyNames);
                }
            }
            return this.createTransformer(managedCtClass).applyTo((DynamicType.Builder<?>)builder, es);
        }
        if (this.enhancementContext.isCompositeClass(managedCtClass)) {
            log.debugf("Enhancing [%s] as Composite", managedCtClass.getName());
            builder = builder.implement(new Type[]{ManagedComposite.class});
            builder = this.addInterceptorHandling((DynamicType.Builder<?>)builder, managedCtClass, es);
            if (this.enhancementContext.doDirtyCheckingInline(managedCtClass)) {
                builder = builder.implement(new Type[]{CompositeTracker.class}).defineField("$$_hibernate_compositeOwners", CompositeOwnerTracker.class, new ModifierContributor.ForField[]{FieldPersistence.TRANSIENT, Visibility.PRIVATE}).annotateField(new AnnotationDescription[]{TRANSIENT_ANNOTATION}).defineMethod("$$_hibernate_setOwner", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{String.class, CompositeOwner.class}).intercept(this.implementationSetOwner).defineMethod("$$_hibernate_clearOwner", Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{String.class}).intercept(this.implementationClearOwner);
            }
            return this.createTransformer(managedCtClass).applyTo((DynamicType.Builder<?>)builder, es);
        }
        if (this.enhancementContext.isMappedSuperclassClass(managedCtClass)) {
            log.debugf("Enhancing [%s] as MappedSuperclass", managedCtClass.getName());
            builder = builder.implement(new Type[]{ManagedMappedSuperclass.class});
            return this.createTransformer(managedCtClass).applyTo((DynamicType.Builder<?>)builder, es);
        }
        if (this.enhancementContext.doExtendedEnhancement(managedCtClass)) {
            log.debugf("Extended enhancement of [%s]", managedCtClass.getName());
            return this.createTransformer(managedCtClass).applyExtended((DynamicType.Builder<?>)builder, es);
        }
        log.debugf("Skipping enhancement of [%s]: not entity or composite", managedCtClass.getName());
        return null;
    }

    private PersistentAttributeTransformer createTransformer(TypeDescription typeDescription) {
        return PersistentAttributeTransformer.collectPersistentFields(typeDescription, this.enhancementContext, this.typePool);
    }

    private boolean alreadyEnhanced(TypeDescription managedCtClass) {
        for (TypeDescription.Generic declaredInterface : managedCtClass.getInterfaces()) {
            if (!declaredInterface.asErasure().isAssignableTo(Managed.class)) continue;
            return true;
        }
        return false;
    }

    private DynamicType.Builder<?> addInterceptorHandling(DynamicType.Builder<?> builder, TypeDescription managedCtClass, EnhancementStatus es) {
        if (this.enhancementContext.hasLazyLoadableAttributes(managedCtClass)) {
            log.debugf("Weaving in PersistentAttributeInterceptable implementation on [%s]", managedCtClass.getName());
            builder = builder.implement(new Type[]{PersistentAttributeInterceptable.class});
            es.enabledInterfacePersistentAttributeInterceptable();
            builder = EnhancerImpl.addFieldWithGetterAndSetter(builder, PersistentAttributeInterceptor.class, "$$_hibernate_attributeInterceptor", "$$_hibernate_getInterceptor", "$$_hibernate_setInterceptor");
        }
        return builder;
    }

    private static DynamicType.Builder<?> addFieldWithGetterAndSetter(DynamicType.Builder<?> builder, Class<?> type, String fieldName, String getterName, String setterName) {
        return builder.defineField(fieldName, type, new ModifierContributor.ForField[]{Visibility.PRIVATE, FieldPersistence.TRANSIENT}).annotateField(new AnnotationDescription[]{TRANSIENT_ANNOTATION}).defineMethod(getterName, type, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).intercept((Implementation)FieldAccessor.ofField((String)fieldName)).defineMethod(setterName, Void.TYPE, new ModifierContributor.ForMethod[]{Visibility.PUBLIC}).withParameters(new Type[]{type}).intercept((Implementation)FieldAccessor.ofField((String)fieldName));
    }

    private List<AnnotatedFieldDescription> collectCollectionFields(TypeDescription managedCtClass) {
        ArrayList<AnnotatedFieldDescription> collectionList = new ArrayList<AnnotatedFieldDescription>();
        for (FieldDescription ctField : managedCtClass.getDeclaredFields()) {
            AnnotatedFieldDescription annotatedField;
            if (Modifier.isStatic(ctField.getModifiers()) || ctField.getName().startsWith("$$_hibernate_") || !this.enhancementContext.isPersistentField(annotatedField = new AnnotatedFieldDescription(this.enhancementContext, ctField)) || !this.enhancementContext.isMappedCollection(annotatedField) || !ctField.getType().asErasure().isAssignableTo(Collection.class) && !ctField.getType().asErasure().isAssignableTo(Map.class)) continue;
            collectionList.add(annotatedField);
        }
        if (!this.enhancementContext.isMappedSuperclassClass(managedCtClass)) {
            collectionList.addAll(this.collectInheritCollectionFields((TypeDefinition)managedCtClass));
        }
        return collectionList;
    }

    private Collection<AnnotatedFieldDescription> collectInheritCollectionFields(TypeDefinition managedCtClass) {
        TypeDescription.Generic managedCtSuperclass = managedCtClass.getSuperClass();
        if (managedCtSuperclass == null || managedCtSuperclass.represents(Object.class)) {
            return Collections.emptyList();
        }
        if (!this.enhancementContext.isMappedSuperclassClass(managedCtSuperclass.asErasure())) {
            return this.collectInheritCollectionFields((TypeDefinition)managedCtSuperclass.asErasure());
        }
        ArrayList<AnnotatedFieldDescription> collectionList = new ArrayList<AnnotatedFieldDescription>();
        for (FieldDescription ctField : managedCtSuperclass.getDeclaredFields()) {
            AnnotatedFieldDescription annotatedField;
            if (Modifier.isStatic(ctField.getModifiers()) || !this.enhancementContext.isPersistentField(annotatedField = new AnnotatedFieldDescription(this.enhancementContext, ctField)) || !this.enhancementContext.isMappedCollection(annotatedField) || !ctField.getType().asErasure().isAssignableTo(Collection.class) && !ctField.getType().asErasure().isAssignableTo(Map.class)) continue;
            collectionList.add(annotatedField);
        }
        collectionList.addAll(this.collectInheritCollectionFields((TypeDefinition)managedCtSuperclass));
        return collectionList;
    }

    static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    static final class EnhancementStatus {
        private final String typeName;
        private boolean managedEntity = false;
        private boolean selfDirtynessTracker = false;
        private boolean persistentAttributeInterceptable = false;
        private boolean applied = false;

        public EnhancementStatus(String typeName) {
            this.typeName = typeName;
        }

        public void enabledInterfaceManagedEntity() {
            this.managedEntity = true;
        }

        public void enabledInterfaceSelfDirtinessTracker() {
            this.selfDirtynessTracker = true;
        }

        public void enabledInterfacePersistentAttributeInterceptable() {
            this.persistentAttributeInterceptable = true;
        }

        public DynamicType.Builder<?> applySuperInterfaceOptimisations(DynamicType.Builder<?> builder) {
            if (this.applied) {
                throw new IllegalStateException("Should not apply super-interface optimisations twice");
            }
            this.applied = true;
            if (this.managedEntity && this.persistentAttributeInterceptable && this.selfDirtynessTracker) {
                log.debugf("Applying Enhancer optimisations for type [%s]; adding EnhancedEntity as additional marker.", this.typeName);
                return builder.implement(new Type[]{EnhancedEntity.class});
            }
            log.debugf("Applying Enhancer optimisations for type [%s]; NOT enabling EnhancedEntity as additional marker.", this.typeName);
            return builder;
        }
    }

    private static class EnhancerClassFileLocator
    extends ClassFileLocator.ForClassLoader {
        private String className;
        private ClassFileLocator.Resolution resolution;

        protected EnhancerClassFileLocator(ClassLoader classLoader) {
            super(classLoader);
        }

        public ClassFileLocator.Resolution locate(String className) throws IOException {
            assert (className != null);
            if (className.equals(this.className)) {
                return this.resolution;
            }
            return super.locate(className);
        }

        void setClassNameAndBytes(String className, byte[] bytes) {
            assert (className != null);
            assert (bytes != null);
            this.className = className;
            this.resolution = new ClassFileLocator.Resolution.Explicit(bytes);
        }
    }

    static class AnnotatedFieldDescription
    implements UnloadedField {
        private final ByteBuddyEnhancementContext context;
        private final FieldDescription fieldDescription;
        private AnnotationList annotations;
        private Optional<MethodDescription> getter;

        AnnotatedFieldDescription(ByteBuddyEnhancementContext context, FieldDescription fieldDescription) {
            this.context = context;
            this.fieldDescription = fieldDescription;
        }

        @Override
        public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
            return this.getAnnotations().isAnnotationPresent(annotationType);
        }

        public String toString() {
            return this.fieldDescription.toString();
        }

        <T extends Annotation> AnnotationDescription.Loadable<T> getAnnotation(Class<T> annotationType) {
            return this.getAnnotations().ofType(annotationType);
        }

        String getName() {
            return this.fieldDescription.getName();
        }

        TypeDefinition getDeclaringType() {
            return this.fieldDescription.getDeclaringType();
        }

        TypeDescription.Generic getType() {
            return this.fieldDescription.getType();
        }

        FieldDescription.InDefinedShape asDefined() {
            return (FieldDescription.InDefinedShape)this.fieldDescription.asDefined();
        }

        String getDescriptor() {
            return this.fieldDescription.getDescriptor();
        }

        boolean isVisibleTo(TypeDescription typeDescription) {
            return this.fieldDescription.isVisibleTo(typeDescription);
        }

        FieldDescription getFieldDescription() {
            return this.fieldDescription;
        }

        Optional<MethodDescription> getGetter() {
            if (this.getter == null) {
                this.getter = this.context.resolveGetter(this.fieldDescription);
            }
            return this.getter;
        }

        private AnnotationList getAnnotations() {
            if (this.annotations == null) {
                this.annotations = this.doGetAnnotations();
            }
            return this.annotations;
        }

        private AnnotationList doGetAnnotations() {
            AnnotationDescription.Loadable access = this.fieldDescription.getDeclaringType().asErasure().getDeclaredAnnotations().ofType(Access.class);
            if (access != null && ((Access)access.load()).value() == AccessType.PROPERTY) {
                Optional<MethodDescription> getter = this.getGetter();
                if (getter.isPresent()) {
                    return getter.get().getDeclaredAnnotations();
                }
                return this.fieldDescription.getDeclaredAnnotations();
            }
            if (access != null && ((Access)access.load()).value() == AccessType.FIELD) {
                return this.fieldDescription.getDeclaredAnnotations();
            }
            Optional<MethodDescription> getter = this.getGetter();
            ArrayList annotationDescriptions = new ArrayList();
            if (getter.isPresent()) {
                annotationDescriptions.addAll(getter.get().getDeclaredAnnotations());
            }
            annotationDescriptions.addAll(this.fieldDescription.getDeclaredAnnotations());
            return new AnnotationList.Explicit(annotationDescriptions);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface Implementation
extends InstrumentedType.Prepareable {
    public ByteCodeAppender appender(Target var1);

    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    implements Implementation {
        private static final int NO_ADDITIONAL_VARIABLES = 0;
        private final ByteCodeAppender byteCodeAppender;

        public Simple(ByteCodeAppender ... byteCodeAppender) {
            this.byteCodeAppender = new ByteCodeAppender.Compound(byteCodeAppender);
        }

        public Simple(StackManipulation ... stackManipulation) {
            this.byteCodeAppender = new ByteCodeAppender.Simple(stackManipulation);
        }

        public static Implementation of(Dispatcher dispatcher) {
            return Simple.of(dispatcher, 0);
        }

        public static Implementation of(Dispatcher dispatcher, int additionalVariableLength) {
            return Simple.of(dispatcher, InstrumentedType.Prepareable.NoOp.INSTANCE, additionalVariableLength);
        }

        public static Implementation of(Dispatcher dispatcher, InstrumentedType.Prepareable prepareable) {
            return Simple.of(dispatcher, prepareable, 0);
        }

        public static Implementation of(Dispatcher dispatcher, InstrumentedType.Prepareable prepareable, int additionalVariableLength) {
            if (additionalVariableLength < 0) {
                throw new IllegalArgumentException("Additional variable length cannot be negative: " + additionalVariableLength);
            }
            return new ForDispatcher(dispatcher, prepareable, additionalVariableLength);
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender appender(Target implementationTarget) {
            return this.byteCodeAppender;
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
            return this.byteCodeAppender.equals(((Simple)object).byteCodeAppender);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.byteCodeAppender.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class ForDispatcher
        implements Implementation {
            private final Dispatcher dispatcher;
            private final InstrumentedType.Prepareable prepareable;
            private final int additionalVariableLength;

            protected ForDispatcher(Dispatcher dispatcher, InstrumentedType.Prepareable prepareable, int additionalVariableLength) {
                this.dispatcher = dispatcher;
                this.prepareable = prepareable;
                this.additionalVariableLength = additionalVariableLength;
            }

            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return this.prepareable.prepare(instrumentedType);
            }

            public ByteCodeAppender appender(Target implementationTarget) {
                return new Appender(implementationTarget);
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
                if (this.additionalVariableLength != ((ForDispatcher)object).additionalVariableLength) {
                    return false;
                }
                if (!this.dispatcher.equals(((ForDispatcher)object).dispatcher)) {
                    return false;
                }
                return this.prepareable.equals(((ForDispatcher)object).prepareable);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.dispatcher.hashCode()) * 31 + this.prepareable.hashCode()) * 31 + this.additionalVariableLength;
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class Appender
            implements ByteCodeAppender {
                private final Target implementationTarget;

                protected Appender(Target implementationTarget) {
                    this.implementationTarget = implementationTarget;
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Context implementationContext, MethodDescription instrumentedMethod) {
                    return new ByteCodeAppender.Size(ForDispatcher.this.dispatcher.apply(this.implementationTarget, instrumentedMethod).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize() + ForDispatcher.this.additionalVariableLength);
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
                    if (!this.implementationTarget.equals(((Appender)object).implementationTarget)) {
                        return false;
                    }
                    return ForDispatcher.this.equals(((Appender)object).ForDispatcher.this);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.implementationTarget.hashCode()) * 31 + ForDispatcher.this.hashCode();
                }
            }
        }

        public static interface Dispatcher {
            public StackManipulation apply(Target var1, MethodDescription var2);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements Implementation {
        private final List<Implementation> implementations = new ArrayList<Implementation>();

        public Compound(Implementation ... implementation) {
            this(Arrays.asList(implementation));
        }

        public Compound(List<? extends Implementation> implementations) {
            for (Implementation implementation : implementations) {
                if (implementation instanceof Composable) {
                    this.implementations.addAll(((Composable)implementation).implementations);
                    this.implementations.add(((Composable)implementation).composable);
                    continue;
                }
                if (implementation instanceof Compound) {
                    this.implementations.addAll(((Compound)implementation).implementations);
                    continue;
                }
                this.implementations.add(implementation);
            }
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            for (Implementation implementation : this.implementations) {
                instrumentedType = implementation.prepare(instrumentedType);
            }
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Target implementationTarget) {
            ByteCodeAppender[] byteCodeAppender = new ByteCodeAppender[this.implementations.size()];
            int index = 0;
            for (Implementation implementation : this.implementations) {
                byteCodeAppender[index++] = implementation.appender(implementationTarget);
            }
            return new ByteCodeAppender.Compound(byteCodeAppender);
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
            return ((Object)this.implementations).equals(((Compound)object).implementations);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.implementations).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Composable
        implements net.bytebuddy.implementation.Implementation$Composable {
            private final net.bytebuddy.implementation.Implementation$Composable composable;
            private final List<Implementation> implementations = new ArrayList<Implementation>();

            public Composable(Implementation implementation, net.bytebuddy.implementation.Implementation$Composable composable) {
                this(Collections.singletonList(implementation), composable);
            }

            public Composable(List<? extends Implementation> implementations, net.bytebuddy.implementation.Implementation$Composable composable) {
                for (Implementation implementation : implementations) {
                    if (implementation instanceof Composable) {
                        this.implementations.addAll(((Composable)implementation).implementations);
                        this.implementations.add(((Composable)implementation).composable);
                        continue;
                    }
                    if (implementation instanceof Compound) {
                        this.implementations.addAll(((Compound)implementation).implementations);
                        continue;
                    }
                    this.implementations.add(implementation);
                }
                if (composable instanceof Composable) {
                    this.implementations.addAll(((Composable)composable).implementations);
                    this.composable = ((Composable)composable).composable;
                } else {
                    this.composable = composable;
                }
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                for (Implementation implementation : this.implementations) {
                    instrumentedType = implementation.prepare(instrumentedType);
                }
                return this.composable.prepare(instrumentedType);
            }

            @Override
            public ByteCodeAppender appender(Target implementationTarget) {
                ByteCodeAppender[] byteCodeAppender = new ByteCodeAppender[this.implementations.size() + 1];
                int index = 0;
                for (Implementation implementation : this.implementations) {
                    byteCodeAppender[index++] = implementation.appender(implementationTarget);
                }
                byteCodeAppender[index] = this.composable.appender(implementationTarget);
                return new ByteCodeAppender.Compound(byteCodeAppender);
            }

            @Override
            public Implementation andThen(Implementation implementation) {
                return new Compound(CompoundList.of(this.implementations, this.composable.andThen(implementation)));
            }

            @Override
            public net.bytebuddy.implementation.Implementation$Composable andThen(net.bytebuddy.implementation.Implementation$Composable implementation) {
                return new Composable(this.implementations, this.composable.andThen(implementation));
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
                if (!this.composable.equals(((Composable)object).composable)) {
                    return false;
                }
                return ((Object)this.implementations).equals(((Composable)object).implementations);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.composable.hashCode()) * 31 + ((Object)this.implementations).hashCode();
            }
        }
    }

    public static interface Context
    extends MethodAccessorFactory {
        public TypeDescription register(AuxiliaryType var1);

        public FieldDescription.InDefinedShape cache(StackManipulation var1, TypeDescription var2);

        public TypeDescription getInstrumentedType();

        public ClassFileVersion getClassFileVersion();

        public FrameGeneration getFrameGeneration();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Default
        extends ExtractableView.AbstractBase {
            public static final String ACCESSOR_METHOD_SUFFIX = "accessor";
            public static final String FIELD_CACHE_PREFIX = "cachedValue";
            private final AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;
            private final TypeInitializer typeInitializer;
            private final ClassFileVersion auxiliaryClassFileVersion;
            private final Map<SpecialMethodInvocation, DelegationRecord> registeredAccessorMethods;
            private final Map<FieldDescription, DelegationRecord> registeredGetters;
            private final Map<FieldDescription, DelegationRecord> registeredSetters;
            private final Map<AuxiliaryType, DynamicType> auxiliaryTypes;
            private final Map<FieldCacheEntry, FieldDescription.InDefinedShape> registeredFieldCacheEntries;
            private final Set<FieldDescription.InDefinedShape> registeredFieldCacheFields;
            private final String suffix;
            private boolean fieldCacheCanAppendEntries;

            protected Default(TypeDescription instrumentedType, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion auxiliaryClassFileVersion, FrameGeneration frameGeneration, String suffix) {
                super(instrumentedType, classFileVersion, frameGeneration);
                this.auxiliaryTypeNamingStrategy = auxiliaryTypeNamingStrategy;
                this.typeInitializer = typeInitializer;
                this.auxiliaryClassFileVersion = auxiliaryClassFileVersion;
                this.suffix = suffix;
                this.registeredAccessorMethods = new HashMap<SpecialMethodInvocation, DelegationRecord>();
                this.registeredGetters = new HashMap<FieldDescription, DelegationRecord>();
                this.registeredSetters = new HashMap<FieldDescription, DelegationRecord>();
                this.auxiliaryTypes = new HashMap<AuxiliaryType, DynamicType>();
                this.registeredFieldCacheEntries = new HashMap<FieldCacheEntry, FieldDescription.InDefinedShape>();
                this.registeredFieldCacheFields = new HashSet<FieldDescription.InDefinedShape>();
                this.fieldCacheCanAppendEntries = true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public MethodDescription.InDefinedShape registerAccessorFor(SpecialMethodInvocation specialMethodInvocation, MethodAccessorFactory.AccessType accessType) {
                DelegationRecord record = this.registeredAccessorMethods.get(specialMethodInvocation);
                record = record == null ? new AccessorMethodDelegation(this.instrumentedType, this.suffix, accessType, specialMethodInvocation) : record.with(accessType);
                this.registeredAccessorMethods.put(specialMethodInvocation, record);
                return record.getMethod();
            }

            @Override
            public MethodDescription.InDefinedShape registerGetterFor(FieldDescription fieldDescription, MethodAccessorFactory.AccessType accessType) {
                DelegationRecord record = this.registeredGetters.get(fieldDescription);
                record = record == null ? new FieldGetterDelegation(this.instrumentedType, this.suffix, accessType, fieldDescription) : record.with(accessType);
                this.registeredGetters.put(fieldDescription, record);
                return record.getMethod();
            }

            @Override
            public MethodDescription.InDefinedShape registerSetterFor(FieldDescription fieldDescription, MethodAccessorFactory.AccessType accessType) {
                DelegationRecord record = this.registeredSetters.get(fieldDescription);
                record = record == null ? new FieldSetterDelegation(this.instrumentedType, this.suffix, accessType, fieldDescription) : record.with(accessType);
                this.registeredSetters.put(fieldDescription, record);
                return record.getMethod();
            }

            @Override
            public TypeDescription register(AuxiliaryType auxiliaryType) {
                DynamicType dynamicType = this.auxiliaryTypes.get(auxiliaryType);
                if (dynamicType == null) {
                    dynamicType = auxiliaryType.make(this.auxiliaryTypeNamingStrategy.name(this.instrumentedType, auxiliaryType), this.auxiliaryClassFileVersion, this);
                    this.auxiliaryTypes.put(auxiliaryType, dynamicType);
                }
                return dynamicType.getTypeDescription();
            }

            @Override
            public List<DynamicType> getAuxiliaryTypes() {
                return new ArrayList<DynamicType>(this.auxiliaryTypes.values());
            }

            @Override
            public FieldDescription.InDefinedShape cache(StackManipulation fieldValue, TypeDescription fieldType) {
                FieldCacheEntry fieldCacheEntry = new FieldCacheEntry(fieldValue, fieldType);
                FieldDescription.InDefinedShape fieldCache = this.registeredFieldCacheEntries.get(fieldCacheEntry);
                if (fieldCache != null) {
                    return fieldCache;
                }
                if (!this.fieldCacheCanAppendEntries) {
                    throw new IllegalStateException("Cached values cannot be registered after defining the type initializer for " + this.instrumentedType);
                }
                int hashCode = fieldValue.hashCode();
                while (!this.registeredFieldCacheFields.add(fieldCache = new CacheValueField(this.instrumentedType, fieldType.asGenericType(), this.suffix, hashCode++))) {
                }
                this.registeredFieldCacheEntries.put(fieldCacheEntry, fieldCache);
                return fieldCache;
            }

            @Override
            public void drain(TypeInitializer.Drain drain, ClassVisitor classVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                this.fieldCacheCanAppendEntries = false;
                TypeInitializer typeInitializer = this.typeInitializer;
                for (Map.Entry<FieldCacheEntry, FieldDescription.InDefinedShape> entry : this.registeredFieldCacheEntries.entrySet()) {
                    FieldVisitor fieldVisitor = classVisitor.visitField(entry.getValue().getModifiers(), entry.getValue().getInternalName(), entry.getValue().getDescriptor(), entry.getValue().getGenericSignature(), FieldDescription.NO_DEFAULT_VALUE);
                    if (fieldVisitor == null) continue;
                    fieldVisitor.visitEnd();
                    typeInitializer = typeInitializer.expandWith(entry.getKey().storeIn(entry.getValue()));
                }
                drain.apply(classVisitor, typeInitializer, this);
                for (TypeWriter.MethodPool.Record record : this.registeredAccessorMethods.values()) {
                    record.apply(classVisitor, this, annotationValueFilterFactory);
                }
                for (TypeWriter.MethodPool.Record record : this.registeredGetters.values()) {
                    record.apply(classVisitor, this, annotationValueFilterFactory);
                }
                for (TypeWriter.MethodPool.Record record : this.registeredSetters.values()) {
                    record.apply(classVisitor, this, annotationValueFilterFactory);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Factory implements net.bytebuddy.implementation.Implementation$Context$Factory
            {
                INSTANCE;


                @Override
                @Deprecated
                public ExtractableView make(TypeDescription instrumentedType, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion classFileVersion, ClassFileVersion auxiliaryClassFileVersion) {
                    return this.make(instrumentedType, auxiliaryTypeNamingStrategy, typeInitializer, classFileVersion, auxiliaryClassFileVersion, classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? FrameGeneration.GENERATE : FrameGeneration.DISABLED);
                }

                @Override
                public ExtractableView make(TypeDescription instrumentedType, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion classFileVersion, ClassFileVersion auxiliaryClassFileVersion, FrameGeneration frameGeneration) {
                    return new Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion, frameGeneration, RandomString.make());
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class WithFixedSuffix
                implements net.bytebuddy.implementation.Implementation$Context$Factory {
                    private final String suffix;

                    public WithFixedSuffix(String suffix) {
                        this.suffix = suffix;
                    }

                    @Deprecated
                    public ExtractableView make(TypeDescription instrumentedType, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion classFileVersion, ClassFileVersion auxiliaryClassFileVersion) {
                        return this.make(instrumentedType, auxiliaryTypeNamingStrategy, typeInitializer, classFileVersion, auxiliaryClassFileVersion, classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? FrameGeneration.GENERATE : FrameGeneration.DISABLED);
                    }

                    public ExtractableView make(TypeDescription instrumentedType, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion classFileVersion, ClassFileVersion auxiliaryClassFileVersion, FrameGeneration frameGeneration) {
                        return new Default(instrumentedType, classFileVersion, auxiliaryTypeNamingStrategy, typeInitializer, auxiliaryClassFileVersion, frameGeneration, this.suffix);
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
                        return this.suffix.equals(((WithFixedSuffix)object).suffix);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.suffix.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class FieldSetterDelegation
            extends DelegationRecord {
                private final FieldDescription fieldDescription;

                protected FieldSetterDelegation(TypeDescription instrumentedType, String suffix, MethodAccessorFactory.AccessType accessType, FieldDescription fieldDescription) {
                    this(new FieldSetter(instrumentedType, fieldDescription, suffix), accessType.getVisibility(), fieldDescription);
                }

                private FieldSetterDelegation(MethodDescription.InDefinedShape methodDescription, Visibility visibility, FieldDescription fieldDescription) {
                    super(methodDescription, visibility);
                    this.fieldDescription = fieldDescription;
                }

                protected DelegationRecord with(MethodAccessorFactory.AccessType accessType) {
                    return new FieldSetterDelegation(this.methodDescription, this.visibility.expandTo(accessType.getVisibility()), this.fieldDescription);
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Context implementationContext, MethodDescription instrumentedMethod) {
                    StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), FieldAccess.forField(this.fieldDescription).write(), MethodReturn.VOID).apply(methodVisitor, implementationContext);
                    return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.fieldDescription.equals(((FieldSetterDelegation)object).fieldDescription);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.fieldDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class FieldGetterDelegation
            extends DelegationRecord {
                private final FieldDescription fieldDescription;

                protected FieldGetterDelegation(TypeDescription instrumentedType, String suffix, MethodAccessorFactory.AccessType accessType, FieldDescription fieldDescription) {
                    this(new FieldGetter(instrumentedType, fieldDescription, suffix), accessType.getVisibility(), fieldDescription);
                }

                private FieldGetterDelegation(MethodDescription.InDefinedShape methodDescription, Visibility visibility, FieldDescription fieldDescription) {
                    super(methodDescription, visibility);
                    this.fieldDescription = fieldDescription;
                }

                protected DelegationRecord with(MethodAccessorFactory.AccessType accessType) {
                    return new FieldGetterDelegation(this.methodDescription, this.visibility.expandTo(accessType.getVisibility()), this.fieldDescription);
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Context implementationContext, MethodDescription instrumentedMethod) {
                    StackManipulation.Size stackSize = new StackManipulation.Compound(this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(this.fieldDescription).read(), MethodReturn.of(this.fieldDescription.getType())).apply(methodVisitor, implementationContext);
                    return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.fieldDescription.equals(((FieldGetterDelegation)object).fieldDescription);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.fieldDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class AccessorMethodDelegation
            extends DelegationRecord {
                private final StackManipulation accessorMethodInvocation;

                protected AccessorMethodDelegation(TypeDescription instrumentedType, String suffix, MethodAccessorFactory.AccessType accessType, SpecialMethodInvocation specialMethodInvocation) {
                    this(new AccessorMethod(instrumentedType, specialMethodInvocation.getMethodDescription(), specialMethodInvocation.getTypeDescription(), suffix), accessType.getVisibility(), specialMethodInvocation);
                }

                private AccessorMethodDelegation(MethodDescription.InDefinedShape methodDescription, Visibility visibility, StackManipulation accessorMethodInvocation) {
                    super(methodDescription, visibility);
                    this.accessorMethodInvocation = accessorMethodInvocation;
                }

                protected DelegationRecord with(MethodAccessorFactory.AccessType accessType) {
                    return new AccessorMethodDelegation(this.methodDescription, this.visibility.expandTo(accessType.getVisibility()), this.accessorMethodInvocation);
                }

                public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Context implementationContext, MethodDescription instrumentedMethod) {
                    StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), this.accessorMethodInvocation, MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext);
                    return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.accessorMethodInvocation.equals(((AccessorMethodDelegation)object).accessorMethodInvocation);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.accessorMethodInvocation.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static abstract class DelegationRecord
            extends TypeWriter.MethodPool.Record.ForDefinedMethod
            implements ByteCodeAppender {
                protected final MethodDescription.InDefinedShape methodDescription;
                protected final Visibility visibility;

                protected DelegationRecord(MethodDescription.InDefinedShape methodDescription, Visibility visibility) {
                    this.methodDescription = methodDescription;
                    this.visibility = visibility;
                }

                protected abstract DelegationRecord with(MethodAccessorFactory.AccessType var1);

                public MethodDescription.InDefinedShape getMethod() {
                    return this.methodDescription;
                }

                public TypeWriter.MethodPool.Record.Sort getSort() {
                    return TypeWriter.MethodPool.Record.Sort.IMPLEMENTED;
                }

                public Visibility getVisibility() {
                    return this.visibility;
                }

                public void applyHead(MethodVisitor methodVisitor) {
                }

                public void applyBody(MethodVisitor methodVisitor, Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    methodVisitor.visitCode();
                    ByteCodeAppender.Size size = this.applyCode(methodVisitor, implementationContext);
                    methodVisitor.visitMaxs(size.getOperandStackSize(), size.getLocalVariableSize());
                }

                public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                }

                public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Context implementationContext) {
                    return this.apply(methodVisitor, implementationContext, this.getMethod());
                }

                public TypeWriter.MethodPool.Record prepend(ByteCodeAppender byteCodeAppender) {
                    throw new UnsupportedOperationException("Cannot prepend code to a delegation for " + this.methodDescription);
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
                    if (!this.visibility.equals(((DelegationRecord)object).visibility)) {
                        return false;
                    }
                    return this.methodDescription.equals(((DelegationRecord)object).methodDescription);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.visibility.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class FieldSetter
            extends AbstractPropertyAccessorMethod {
                private final TypeDescription instrumentedType;
                private final FieldDescription fieldDescription;
                private final String name;

                protected FieldSetter(TypeDescription instrumentedType, FieldDescription fieldDescription, String suffix) {
                    this.instrumentedType = instrumentedType;
                    this.fieldDescription = fieldDescription;
                    this.name = fieldDescription.getName() + "$" + Default.ACCESSOR_METHOD_SUFFIX + "$" + suffix;
                }

                @Override
                public TypeDescription.Generic getReturnType() {
                    return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE);
                }

                @Override
                public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                    return new ParameterList.Explicit.ForTypes((MethodDescription.InDefinedShape)this, Collections.singletonList(this.fieldDescription.getType().asRawType()));
                }

                @Override
                public TypeList.Generic getExceptionTypes() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                @MaybeNull
                public AnnotationValue<?, ?> getDefaultValue() {
                    return AnnotationValue.UNDEFINED;
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return this.instrumentedType;
                }

                @Override
                protected int getBaseModifiers() {
                    return this.fieldDescription.isStatic() ? 8 : 0;
                }

                @Override
                public String getInternalName() {
                    return this.name;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class FieldGetter
            extends AbstractPropertyAccessorMethod {
                private final TypeDescription instrumentedType;
                private final FieldDescription fieldDescription;
                private final String name;

                protected FieldGetter(TypeDescription instrumentedType, FieldDescription fieldDescription, String suffix) {
                    this.instrumentedType = instrumentedType;
                    this.fieldDescription = fieldDescription;
                    this.name = fieldDescription.getName() + "$" + Default.ACCESSOR_METHOD_SUFFIX + "$" + suffix;
                }

                @Override
                public TypeDescription.Generic getReturnType() {
                    return this.fieldDescription.getType().asRawType();
                }

                @Override
                public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                    return new ParameterList.Empty<ParameterDescription.InDefinedShape>();
                }

                @Override
                public TypeList.Generic getExceptionTypes() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                @MaybeNull
                public AnnotationValue<?, ?> getDefaultValue() {
                    return AnnotationValue.UNDEFINED;
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return this.instrumentedType;
                }

                @Override
                protected int getBaseModifiers() {
                    return this.fieldDescription.isStatic() ? 8 : 0;
                }

                @Override
                public String getInternalName() {
                    return this.name;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class AccessorMethod
            extends AbstractPropertyAccessorMethod {
                private final TypeDescription instrumentedType;
                private final MethodDescription methodDescription;
                private final String name;

                protected AccessorMethod(TypeDescription instrumentedType, MethodDescription methodDescription, TypeDescription typeDescription, String suffix) {
                    this.instrumentedType = instrumentedType;
                    this.methodDescription = methodDescription;
                    this.name = methodDescription.getInternalName() + "$" + Default.ACCESSOR_METHOD_SUFFIX + "$" + suffix + (typeDescription.isInterface() ? "$" + RandomString.hashOf(typeDescription.hashCode()) : "");
                }

                @Override
                public TypeDescription.Generic getReturnType() {
                    return this.methodDescription.getReturnType().asRawType();
                }

                @Override
                public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                    return new ParameterList.Explicit.ForTypes((MethodDescription.InDefinedShape)this, this.methodDescription.getParameters().asTypeList().asRawTypes());
                }

                @Override
                public TypeList.Generic getExceptionTypes() {
                    return this.methodDescription.getExceptionTypes().asRawTypes();
                }

                @Override
                @MaybeNull
                public AnnotationValue<?, ?> getDefaultValue() {
                    return AnnotationValue.UNDEFINED;
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return this.instrumentedType;
                }

                @Override
                protected int getBaseModifiers() {
                    return this.methodDescription.isStatic() ? 8 : 0;
                }

                @Override
                public String getInternalName() {
                    return this.name;
                }
            }

            protected static abstract class AbstractPropertyAccessorMethod
            extends MethodDescription.InDefinedShape.AbstractBase {
                protected AbstractPropertyAccessorMethod() {
                }

                public int getModifiers() {
                    return 0x1000 | this.getBaseModifiers() | (this.getDeclaringType().isInterface() ? 1 : 16);
                }

                protected abstract int getBaseModifiers();
            }

            protected static class FieldCacheEntry
            implements StackManipulation {
                private final StackManipulation fieldValue;
                private final TypeDescription fieldType;

                protected FieldCacheEntry(StackManipulation fieldValue, TypeDescription fieldType) {
                    this.fieldValue = fieldValue;
                    this.fieldType = fieldType;
                }

                protected ByteCodeAppender storeIn(FieldDescription fieldDescription) {
                    return new ByteCodeAppender.Simple(this, FieldAccess.forField(fieldDescription).write());
                }

                protected TypeDescription getFieldType() {
                    return this.fieldType;
                }

                public boolean isValid() {
                    return this.fieldValue.isValid();
                }

                public StackManipulation.Size apply(MethodVisitor methodVisitor, Context implementationContext) {
                    return this.fieldValue.apply(methodVisitor, implementationContext);
                }

                public int hashCode() {
                    int result = this.fieldValue.hashCode();
                    result = 31 * result + this.fieldType.hashCode();
                    return result;
                }

                public boolean equals(@MaybeNull Object other) {
                    if (this == other) {
                        return true;
                    }
                    if (other == null || this.getClass() != other.getClass()) {
                        return false;
                    }
                    FieldCacheEntry fieldCacheEntry = (FieldCacheEntry)other;
                    return this.fieldValue.equals(fieldCacheEntry.fieldValue) && this.fieldType.equals(fieldCacheEntry.fieldType);
                }
            }

            protected static class CacheValueField
            extends FieldDescription.InDefinedShape.AbstractBase {
                private final TypeDescription instrumentedType;
                private final TypeDescription.Generic fieldType;
                private final String name;

                protected CacheValueField(TypeDescription instrumentedType, TypeDescription.Generic fieldType, String suffix, int hashCode) {
                    this.instrumentedType = instrumentedType;
                    this.fieldType = fieldType;
                    this.name = "cachedValue$" + suffix + "$" + RandomString.hashOf(hashCode);
                }

                public TypeDescription.Generic getType() {
                    return this.fieldType;
                }

                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }

                @Nonnull
                public TypeDescription getDeclaringType() {
                    return this.instrumentedType;
                }

                public int getModifiers() {
                    return 0x1018 | (this.instrumentedType.isInterface() ? 1 : 2);
                }

                public String getName() {
                    return this.name;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Disabled
        extends ExtractableView.AbstractBase {
            protected Disabled(TypeDescription instrumentedType, ClassFileVersion classFileVersion, FrameGeneration frameGeneration) {
                super(instrumentedType, classFileVersion, frameGeneration);
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public List<DynamicType> getAuxiliaryTypes() {
                return Collections.emptyList();
            }

            @Override
            public void drain(TypeInitializer.Drain drain, ClassVisitor classVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                drain.apply(classVisitor, TypeInitializer.None.INSTANCE, this);
            }

            @Override
            public TypeDescription register(AuxiliaryType auxiliaryType) {
                throw new IllegalStateException("Registration of auxiliary types was disabled: " + auxiliaryType);
            }

            @Override
            public MethodDescription.InDefinedShape registerAccessorFor(SpecialMethodInvocation specialMethodInvocation, MethodAccessorFactory.AccessType accessType) {
                throw new IllegalStateException("Registration of method accessors was disabled: " + specialMethodInvocation.getMethodDescription());
            }

            @Override
            public MethodDescription.InDefinedShape registerGetterFor(FieldDescription fieldDescription, MethodAccessorFactory.AccessType accessType) {
                throw new IllegalStateException("Registration of field accessor was disabled: " + fieldDescription);
            }

            @Override
            public MethodDescription.InDefinedShape registerSetterFor(FieldDescription fieldDescription, MethodAccessorFactory.AccessType accessType) {
                throw new IllegalStateException("Registration of field accessor was disabled: " + fieldDescription);
            }

            @Override
            public FieldDescription.InDefinedShape cache(StackManipulation fieldValue, TypeDescription fieldType) {
                throw new IllegalStateException("Field values caching was disabled: " + fieldType);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Factory implements net.bytebuddy.implementation.Implementation$Context$Factory
            {
                INSTANCE;


                @Override
                @Deprecated
                public ExtractableView make(TypeDescription instrumentedType, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion classFileVersion, ClassFileVersion auxiliaryClassFileVersion) {
                    return this.make(instrumentedType, auxiliaryTypeNamingStrategy, typeInitializer, classFileVersion, auxiliaryClassFileVersion, classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? FrameGeneration.GENERATE : FrameGeneration.DISABLED);
                }

                @Override
                public ExtractableView make(TypeDescription instrumentedType, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, TypeInitializer typeInitializer, ClassFileVersion classFileVersion, ClassFileVersion auxiliaryClassFileVersion, FrameGeneration frameGeneration) {
                    if (typeInitializer.isDefined()) {
                        throw new IllegalStateException("Cannot define type initializer which was explicitly disabled: " + typeInitializer);
                    }
                    return new Disabled(instrumentedType, classFileVersion, frameGeneration);
                }
            }
        }

        public static interface Factory {
            @Deprecated
            public ExtractableView make(TypeDescription var1, AuxiliaryType.NamingStrategy var2, TypeInitializer var3, ClassFileVersion var4, ClassFileVersion var5);

            public ExtractableView make(TypeDescription var1, AuxiliaryType.NamingStrategy var2, TypeInitializer var3, ClassFileVersion var4, ClassFileVersion var5, FrameGeneration var6);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ExtractableView
        extends Context {
            public boolean isEnabled();

            public List<DynamicType> getAuxiliaryTypes();

            public void drain(TypeInitializer.Drain var1, ClassVisitor var2, AnnotationValueFilter.Factory var3);

            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class AbstractBase
            implements ExtractableView {
                protected final TypeDescription instrumentedType;
                protected final ClassFileVersion classFileVersion;
                protected final FrameGeneration frameGeneration;

                protected AbstractBase(TypeDescription instrumentedType, ClassFileVersion classFileVersion, FrameGeneration frameGeneration) {
                    this.instrumentedType = instrumentedType;
                    this.classFileVersion = classFileVersion;
                    this.frameGeneration = frameGeneration;
                }

                public TypeDescription getInstrumentedType() {
                    return this.instrumentedType;
                }

                public ClassFileVersion getClassFileVersion() {
                    return this.classFileVersion;
                }

                public FrameGeneration getFrameGeneration() {
                    return this.frameGeneration;
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
                    if (!this.frameGeneration.equals((Object)((AbstractBase)object).frameGeneration)) {
                        return false;
                    }
                    if (!this.instrumentedType.equals(((AbstractBase)object).instrumentedType)) {
                        return false;
                    }
                    return this.classFileVersion.equals(((AbstractBase)object).classFileVersion);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.classFileVersion.hashCode()) * 31 + this.frameGeneration.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum FrameGeneration {
            GENERATE(true){

                public void generate(MethodVisitor methodVisitor, int type, int stackCount, @MaybeNull Object[] stack, int changedLocalVariableCount, @MaybeNull Object[] changedLocalVariable, int fullLocalVariableCount, @MaybeNull Object[] fullLocalVariable) {
                    methodVisitor.visitFrame(type, changedLocalVariableCount, changedLocalVariable, stackCount, stack);
                }
            }
            ,
            EXPAND(true){

                public void generate(MethodVisitor methodVisitor, int type, int stackCount, @MaybeNull Object[] stack, int changedLocalVariableCount, @MaybeNull Object[] changedLocalVariable, int fullLocalVariableCount, @MaybeNull Object[] fullLocalVariable) {
                    methodVisitor.visitFrame(-1, fullLocalVariableCount, fullLocalVariable, stackCount, stack);
                }
            }
            ,
            DISABLED(false){

                public void generate(MethodVisitor methodVisitor, int type, int stackCount, @MaybeNull Object[] stack, int changedLocalVariableCount, @MaybeNull Object[] changedLocalVariable, int fullLocalVariableCount, @MaybeNull Object[] fullLocalVariable) {
                }
            };

            private static final Object[] EMPTY;
            private final boolean active;

            private FrameGeneration(boolean active) {
                this.active = active;
            }

            public boolean isActive() {
                return this.active;
            }

            public void same(MethodVisitor methodVisitor, List<? extends TypeDefinition> localVariables) {
                this.generate(methodVisitor, 3, EMPTY.length, EMPTY, EMPTY.length, EMPTY, localVariables.size(), FrameGeneration.toStackMapFrames(localVariables));
            }

            public void same1(MethodVisitor methodVisitor, TypeDefinition stackValue, List<? extends TypeDefinition> localVariables) {
                this.generate(methodVisitor, 4, 1, new Object[]{FrameGeneration.toStackMapFrame(stackValue)}, EMPTY.length, EMPTY, localVariables.size(), FrameGeneration.toStackMapFrames(localVariables));
            }

            public void append(MethodVisitor methodVisitor, List<? extends TypeDefinition> appended, List<? extends TypeDefinition> localVariables) {
                this.generate(methodVisitor, 1, EMPTY.length, EMPTY, appended.size(), FrameGeneration.toStackMapFrames(appended), localVariables.size() + appended.size(), FrameGeneration.toStackMapFrames(CompoundList.of(localVariables, appended)));
            }

            public void chop(MethodVisitor methodVisitor, int chopped, List<? extends TypeDefinition> localVariables) {
                this.generate(methodVisitor, 2, EMPTY.length, EMPTY, chopped, EMPTY, localVariables.size(), FrameGeneration.toStackMapFrames(localVariables));
            }

            public void full(MethodVisitor methodVisitor, List<? extends TypeDefinition> stackValues, List<? extends TypeDefinition> localVariables) {
                this.generate(methodVisitor, 0, stackValues.size(), FrameGeneration.toStackMapFrames(stackValues), localVariables.size(), FrameGeneration.toStackMapFrames(localVariables), localVariables.size(), FrameGeneration.toStackMapFrames(localVariables));
            }

            protected abstract void generate(MethodVisitor var1, int var2, int var3, @MaybeNull Object[] var4, int var5, @MaybeNull Object[] var6, int var7, @MaybeNull Object[] var8);

            private static Object[] toStackMapFrames(List<? extends TypeDefinition> typeDefinitions) {
                Object[] value = typeDefinitions.isEmpty() ? EMPTY : new Object[typeDefinitions.size()];
                for (int index = 0; index < typeDefinitions.size(); ++index) {
                    value[index] = FrameGeneration.toStackMapFrame(typeDefinitions.get(index));
                }
                return value;
            }

            private static Object toStackMapFrame(TypeDefinition typeDefinition) {
                if (typeDefinition.represents(Boolean.TYPE) || typeDefinition.represents(Byte.TYPE) || typeDefinition.represents(Short.TYPE) || typeDefinition.represents(Character.TYPE) || typeDefinition.represents(Integer.TYPE)) {
                    return Opcodes.INTEGER;
                }
                if (typeDefinition.represents(Long.TYPE)) {
                    return Opcodes.LONG;
                }
                if (typeDefinition.represents(Float.TYPE)) {
                    return Opcodes.FLOAT;
                }
                if (typeDefinition.represents(Double.TYPE)) {
                    return Opcodes.DOUBLE;
                }
                return typeDefinition.asErasure().getInternalName();
            }

            static {
                EMPTY = new Object[0];
            }
        }
    }

    public static interface Target {
        public TypeDescription getInstrumentedType();

        public TypeDefinition getOriginType();

        public SpecialMethodInvocation invokeSuper(MethodDescription.SignatureToken var1);

        public SpecialMethodInvocation invokeDefault(MethodDescription.SignatureToken var1);

        public SpecialMethodInvocation invokeDefault(MethodDescription.SignatureToken var1, TypeDescription var2);

        public SpecialMethodInvocation invokeDominant(MethodDescription.SignatureToken var1);

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class AbstractBase
        implements Target {
            protected final TypeDescription instrumentedType;
            protected final MethodGraph.Linked methodGraph;
            protected final DefaultMethodInvocation defaultMethodInvocation;

            protected AbstractBase(TypeDescription instrumentedType, MethodGraph.Linked methodGraph, DefaultMethodInvocation defaultMethodInvocation) {
                this.instrumentedType = instrumentedType;
                this.methodGraph = methodGraph;
                this.defaultMethodInvocation = defaultMethodInvocation;
            }

            public TypeDescription getInstrumentedType() {
                return this.instrumentedType;
            }

            public SpecialMethodInvocation invokeDefault(MethodDescription.SignatureToken token) {
                SpecialMethodInvocation specialMethodInvocation = SpecialMethodInvocation.Illegal.INSTANCE;
                for (TypeDescription interfaceType : this.instrumentedType.getInterfaces().asErasures()) {
                    SpecialMethodInvocation invocation = this.invokeDefault(token, interfaceType).withCheckedCompatibilityTo(token.asTypeToken());
                    if (!invocation.isValid()) continue;
                    if (specialMethodInvocation.isValid()) {
                        return SpecialMethodInvocation.Illegal.INSTANCE;
                    }
                    specialMethodInvocation = invocation;
                }
                return specialMethodInvocation;
            }

            public SpecialMethodInvocation invokeDefault(MethodDescription.SignatureToken token, TypeDescription targetType) {
                return this.defaultMethodInvocation.apply(this.methodGraph.getInterfaceGraph(targetType).locate(token), targetType);
            }

            public SpecialMethodInvocation invokeDominant(MethodDescription.SignatureToken token) {
                SpecialMethodInvocation specialMethodInvocation = this.invokeSuper(token);
                return specialMethodInvocation.isValid() ? specialMethodInvocation : this.invokeDefault(token);
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
                if (!this.defaultMethodInvocation.equals((Object)((AbstractBase)object).defaultMethodInvocation)) {
                    return false;
                }
                if (!this.instrumentedType.equals(((AbstractBase)object).instrumentedType)) {
                    return false;
                }
                return this.methodGraph.equals(((AbstractBase)object).methodGraph);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.methodGraph.hashCode()) * 31 + this.defaultMethodInvocation.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum DefaultMethodInvocation {
                ENABLED{

                    protected SpecialMethodInvocation apply(MethodGraph.Node node, TypeDescription targetType) {
                        return node.getSort().isUnique() ? SpecialMethodInvocation.Simple.of(node.getRepresentative(), targetType) : SpecialMethodInvocation.Illegal.INSTANCE;
                    }
                }
                ,
                DISABLED{

                    protected SpecialMethodInvocation apply(MethodGraph.Node node, TypeDescription targetType) {
                        return SpecialMethodInvocation.Illegal.INSTANCE;
                    }
                };


                public static DefaultMethodInvocation of(ClassFileVersion classFileVersion) {
                    return classFileVersion.isAtLeast(ClassFileVersion.JAVA_V8) ? ENABLED : DISABLED;
                }

                protected abstract SpecialMethodInvocation apply(MethodGraph.Node var1, TypeDescription var2);
            }
        }

        public static interface Factory {
            public Target make(TypeDescription var1, MethodGraph.Linked var2, ClassFileVersion var3);
        }
    }

    public static interface SpecialMethodInvocation
    extends StackManipulation {
        public MethodDescription getMethodDescription();

        public TypeDescription getTypeDescription();

        public SpecialMethodInvocation withCheckedCompatibilityTo(MethodDescription.TypeToken var1);

        public JavaConstant.MethodHandle toMethodHandle();

        public static class Simple
        extends AbstractBase {
            private final MethodDescription methodDescription;
            private final TypeDescription typeDescription;
            private final StackManipulation stackManipulation;

            protected Simple(MethodDescription methodDescription, TypeDescription typeDescription, StackManipulation stackManipulation) {
                this.methodDescription = methodDescription;
                this.typeDescription = typeDescription;
                this.stackManipulation = stackManipulation;
            }

            public static SpecialMethodInvocation of(MethodDescription methodDescription, TypeDescription typeDescription) {
                StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).special(typeDescription);
                return stackManipulation.isValid() ? new Simple(methodDescription, typeDescription, stackManipulation) : Illegal.INSTANCE;
            }

            public MethodDescription getMethodDescription() {
                return this.methodDescription;
            }

            public TypeDescription getTypeDescription() {
                return this.typeDescription;
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Context implementationContext) {
                return this.stackManipulation.apply(methodVisitor, implementationContext);
            }

            public SpecialMethodInvocation withCheckedCompatibilityTo(MethodDescription.TypeToken token) {
                if (this.methodDescription.asTypeToken().equals(token)) {
                    return this;
                }
                return Illegal.INSTANCE;
            }

            public JavaConstant.MethodHandle toMethodHandle() {
                return JavaConstant.MethodHandle.ofSpecial((MethodDescription.InDefinedShape)this.methodDescription.asDefined(), this.typeDescription);
            }
        }

        public static abstract class AbstractBase
        extends StackManipulation.AbstractBase
        implements SpecialMethodInvocation {
            private transient /* synthetic */ int hashCode;

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    AbstractBase abstractBase = this;
                    n2 = n = 31 * abstractBase.getMethodDescription().asSignatureToken().hashCode() + abstractBase.getTypeDescription().hashCode();
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof SpecialMethodInvocation)) {
                    return false;
                }
                SpecialMethodInvocation specialMethodInvocation = (SpecialMethodInvocation)other;
                return this.getMethodDescription().asSignatureToken().equals(specialMethodInvocation.getMethodDescription().asSignatureToken()) && this.getTypeDescription().equals(specialMethodInvocation.getTypeDescription());
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Illegal implements SpecialMethodInvocation
        {
            INSTANCE;


            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Context implementationContext) {
                throw new IllegalStateException("Cannot implement an undefined method");
            }

            @Override
            public MethodDescription getMethodDescription() {
                throw new IllegalStateException("An illegal special method invocation must not be applied");
            }

            @Override
            public TypeDescription getTypeDescription() {
                throw new IllegalStateException("An illegal special method invocation must not be applied");
            }

            @Override
            public SpecialMethodInvocation withCheckedCompatibilityTo(MethodDescription.TypeToken token) {
                return this;
            }

            @Override
            public JavaConstant.MethodHandle toMethodHandle() {
                throw new IllegalStateException("An illegal special method invocation must not be applied");
            }
        }
    }

    public static interface Composable
    extends Implementation {
        public Implementation andThen(Implementation var1);

        public Composable andThen(Composable var1);
    }
}


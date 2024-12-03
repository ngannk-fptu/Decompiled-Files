/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.dynamic.scaffold;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.RecordComponentList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import net.bytebuddy.dynamic.scaffold.ClassWriterStrategy;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver;
import net.bytebuddy.dynamic.scaffold.inline.RebaseImplementationTarget;
import net.bytebuddy.dynamic.scaffold.subclass.SubclassImplementationTarget;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.attribute.AnnotationAppender;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.FieldAttributeAppender;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.implementation.attribute.RecordComponentAttributeAppender;
import net.bytebuddy.implementation.attribute.TypeAttributeAppender;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.ConstantDynamic;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.jar.asm.commons.ClassRemapper;
import net.bytebuddy.jar.asm.commons.Remapper;
import net.bytebuddy.jar.asm.commons.SimpleRemapper;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.nullability.UnknownNull;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;
import net.bytebuddy.utility.visitor.ContextClassVisitor;
import net.bytebuddy.utility.visitor.MetadataAwareClassVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeWriter<T> {
    public static final String DUMP_PROPERTY = "net.bytebuddy.dump";

    public DynamicType.Unloaded<T> make(TypeResolutionStrategy.Resolved var1);

    public ContextClassVisitor wrap(ClassVisitor var1, int var2, int var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class Default<S>
    implements TypeWriter<S> {
        @AlwaysNull
        private static final String NO_REFERENCE;
        @MaybeNull
        protected static final String DUMP_FOLDER;
        protected final TypeDescription instrumentedType;
        protected final ClassFileVersion classFileVersion;
        protected final FieldPool fieldPool;
        protected final RecordComponentPool recordComponentPool;
        protected final List<? extends DynamicType> auxiliaryTypes;
        protected final FieldList<FieldDescription.InDefinedShape> fields;
        protected final MethodList<?> methods;
        protected final MethodList<?> instrumentedMethods;
        protected final RecordComponentList<RecordComponentDescription.InDefinedShape> recordComponents;
        protected final LoadedTypeInitializer loadedTypeInitializer;
        protected final TypeInitializer typeInitializer;
        protected final TypeAttributeAppender typeAttributeAppender;
        protected final AsmVisitorWrapper asmVisitorWrapper;
        protected final AnnotationValueFilter.Factory annotationValueFilterFactory;
        protected final AnnotationRetention annotationRetention;
        protected final AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;
        protected final Implementation.Context.Factory implementationContextFactory;
        protected final TypeValidation typeValidation;
        protected final ClassWriterStrategy classWriterStrategy;
        protected final TypePool typePool;
        private static final boolean ACCESS_CONTROLLER;

        protected Default(TypeDescription instrumentedType, ClassFileVersion classFileVersion, FieldPool fieldPool, RecordComponentPool recordComponentPool, List<? extends DynamicType> auxiliaryTypes, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, MethodList<?> instrumentedMethods, RecordComponentList<RecordComponentDescription.InDefinedShape> recordComponents, LoadedTypeInitializer loadedTypeInitializer, TypeInitializer typeInitializer, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool) {
            this.instrumentedType = instrumentedType;
            this.classFileVersion = classFileVersion;
            this.fieldPool = fieldPool;
            this.recordComponentPool = recordComponentPool;
            this.auxiliaryTypes = auxiliaryTypes;
            this.fields = fields;
            this.methods = methods;
            this.instrumentedMethods = instrumentedMethods;
            this.recordComponents = recordComponents;
            this.loadedTypeInitializer = loadedTypeInitializer;
            this.typeInitializer = typeInitializer;
            this.typeAttributeAppender = typeAttributeAppender;
            this.asmVisitorWrapper = asmVisitorWrapper;
            this.auxiliaryTypeNamingStrategy = auxiliaryTypeNamingStrategy;
            this.annotationValueFilterFactory = annotationValueFilterFactory;
            this.annotationRetention = annotationRetention;
            this.implementationContextFactory = implementationContextFactory;
            this.typeValidation = typeValidation;
            this.classWriterStrategy = classWriterStrategy;
            this.typePool = typePool;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedExceptionAction<T> privilegedExceptionAction) throws Exception {
            PrivilegedExceptionAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedExceptionAction);
            }
            return action.run();
        }

        public static <U> TypeWriter<U> forCreation(MethodRegistry.Compiled methodRegistry, List<? extends DynamicType> auxiliaryTypes, FieldPool fieldPool, RecordComponentPool recordComponentPool, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool) {
            return new ForCreation(methodRegistry.getInstrumentedType(), classFileVersion, fieldPool, methodRegistry, recordComponentPool, auxiliaryTypes, methodRegistry.getInstrumentedType().getDeclaredFields(), methodRegistry.getMethods(), methodRegistry.getInstrumentedMethods(), methodRegistry.getInstrumentedType().getRecordComponents(), methodRegistry.getLoadedTypeInitializer(), methodRegistry.getTypeInitializer(), typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool);
        }

        public static <U> TypeWriter<U> forRedefinition(MethodRegistry.Prepared methodRegistry, List<? extends DynamicType> auxiliaryTypes, FieldPool fieldPool, RecordComponentPool recordComponentPool, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool, TypeDescription originalType, ClassFileLocator classFileLocator) {
            return new ForInlining.WithFullProcessing(methodRegistry.getInstrumentedType(), classFileVersion, fieldPool, recordComponentPool, auxiliaryTypes, methodRegistry.getInstrumentedType().getDeclaredFields(), methodRegistry.getMethods(), methodRegistry.getInstrumentedMethods(), methodRegistry.getInstrumentedType().getRecordComponents(), methodRegistry.getLoadedTypeInitializer(), methodRegistry.getTypeInitializer(), typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool, originalType, classFileLocator, methodRegistry, SubclassImplementationTarget.Factory.LEVEL_TYPE, MethodRebaseResolver.Disabled.INSTANCE);
        }

        public static <U> TypeWriter<U> forRebasing(MethodRegistry.Prepared methodRegistry, List<? extends DynamicType> auxiliaryTypes, FieldPool fieldPool, RecordComponentPool recordComponentPool, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool, TypeDescription originalType, ClassFileLocator classFileLocator, MethodRebaseResolver methodRebaseResolver) {
            return new ForInlining.WithFullProcessing(methodRegistry.getInstrumentedType(), classFileVersion, fieldPool, recordComponentPool, CompoundList.of(auxiliaryTypes, methodRebaseResolver.getAuxiliaryTypes()), methodRegistry.getInstrumentedType().getDeclaredFields(), methodRegistry.getMethods(), methodRegistry.getInstrumentedMethods(), methodRegistry.getInstrumentedType().getRecordComponents(), methodRegistry.getLoadedTypeInitializer(), methodRegistry.getTypeInitializer(), typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool, originalType, classFileLocator, methodRegistry, new RebaseImplementationTarget.Factory(methodRebaseResolver), methodRebaseResolver);
        }

        public static <U> TypeWriter<U> forDecoration(TypeDescription instrumentedType, ClassFileVersion classFileVersion, List<? extends DynamicType> auxiliaryTypes, List<? extends MethodDescription> methods, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool, ClassFileLocator classFileLocator) {
            return new ForInlining.WithDecorationOnly(instrumentedType, classFileVersion, auxiliaryTypes, new MethodList.Explicit<MethodDescription>(methods), typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool, classFileLocator);
        }

        @Override
        @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Setting a debugging property should never change the program outcome.")
        public DynamicType.Unloaded<S> make(TypeResolutionStrategy.Resolved typeResolutionStrategy) {
            ClassDumpAction.Dispatcher dispatcher = DUMP_FOLDER == null ? ClassDumpAction.Dispatcher.Disabled.INSTANCE : new ClassDumpAction.Dispatcher.Enabled(DUMP_FOLDER, System.currentTimeMillis());
            UnresolvedType unresolvedType = this.create(typeResolutionStrategy.injectedInto(this.typeInitializer), dispatcher);
            dispatcher.dump(this.instrumentedType, false, unresolvedType.getBinaryRepresentation());
            return unresolvedType.toDynamicType(typeResolutionStrategy);
        }

        protected abstract UnresolvedType create(TypeInitializer var1, ClassDumpAction.Dispatcher var2);

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            String dumpFolder;
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
            NO_REFERENCE = null;
            try {
                dumpFolder = Default.doPrivileged(new GetSystemPropertyAction(TypeWriter.DUMP_PROPERTY));
            }
            catch (RuntimeException exception) {
                dumpFolder = null;
            }
            DUMP_FOLDER = dumpFolder;
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
            if (!this.annotationRetention.equals((Object)((Default)object).annotationRetention)) {
                return false;
            }
            if (!this.typeValidation.equals((Object)((Default)object).typeValidation)) {
                return false;
            }
            if (!this.instrumentedType.equals(((Default)object).instrumentedType)) {
                return false;
            }
            if (!this.classFileVersion.equals(((Default)object).classFileVersion)) {
                return false;
            }
            if (!this.fieldPool.equals(((Default)object).fieldPool)) {
                return false;
            }
            if (!this.recordComponentPool.equals(((Default)object).recordComponentPool)) {
                return false;
            }
            if (!((Object)this.auxiliaryTypes).equals(((Default)object).auxiliaryTypes)) {
                return false;
            }
            if (!this.fields.equals(((Default)object).fields)) {
                return false;
            }
            if (!this.methods.equals(((Default)object).methods)) {
                return false;
            }
            if (!this.instrumentedMethods.equals(((Default)object).instrumentedMethods)) {
                return false;
            }
            if (!this.recordComponents.equals(((Default)object).recordComponents)) {
                return false;
            }
            if (!this.loadedTypeInitializer.equals(((Default)object).loadedTypeInitializer)) {
                return false;
            }
            if (!this.typeInitializer.equals(((Default)object).typeInitializer)) {
                return false;
            }
            if (!this.typeAttributeAppender.equals(((Default)object).typeAttributeAppender)) {
                return false;
            }
            if (!this.asmVisitorWrapper.equals(((Default)object).asmVisitorWrapper)) {
                return false;
            }
            if (!this.annotationValueFilterFactory.equals(((Default)object).annotationValueFilterFactory)) {
                return false;
            }
            if (!this.auxiliaryTypeNamingStrategy.equals(((Default)object).auxiliaryTypeNamingStrategy)) {
                return false;
            }
            if (!this.implementationContextFactory.equals(((Default)object).implementationContextFactory)) {
                return false;
            }
            if (!this.classWriterStrategy.equals(((Default)object).classWriterStrategy)) {
                return false;
            }
            return this.typePool.equals(((Default)object).typePool);
        }

        public int hashCode() {
            return (((((((((((((((((((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.classFileVersion.hashCode()) * 31 + this.fieldPool.hashCode()) * 31 + this.recordComponentPool.hashCode()) * 31 + ((Object)this.auxiliaryTypes).hashCode()) * 31 + this.fields.hashCode()) * 31 + this.methods.hashCode()) * 31 + this.instrumentedMethods.hashCode()) * 31 + this.recordComponents.hashCode()) * 31 + this.loadedTypeInitializer.hashCode()) * 31 + this.typeInitializer.hashCode()) * 31 + this.typeAttributeAppender.hashCode()) * 31 + this.asmVisitorWrapper.hashCode()) * 31 + this.annotationValueFilterFactory.hashCode()) * 31 + this.annotationRetention.hashCode()) * 31 + this.auxiliaryTypeNamingStrategy.hashCode()) * 31 + this.implementationContextFactory.hashCode()) * 31 + this.typeValidation.hashCode()) * 31 + this.classWriterStrategy.hashCode()) * 31 + this.typePool.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ClassDumpAction
        implements PrivilegedExceptionAction<Void> {
            @AlwaysNull
            private static final Void NOTHING = null;
            private final String target;
            private final TypeDescription instrumentedType;
            private final boolean original;
            private final long suffix;
            private final byte[] binaryRepresentation;

            protected ClassDumpAction(String target, TypeDescription instrumentedType, boolean original, long suffix, byte[] binaryRepresentation) {
                this.target = target;
                this.instrumentedType = instrumentedType;
                this.original = original;
                this.suffix = suffix;
                this.binaryRepresentation = binaryRepresentation;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Void run() throws Exception {
                Void void_;
                FileOutputStream outputStream = new FileOutputStream(new File(this.target, this.instrumentedType.getName() + (this.original ? "-original." : ".") + this.suffix + ".class"));
                try {
                    ((OutputStream)outputStream).write(this.binaryRepresentation);
                    void_ = NOTHING;
                    Object var4_3 = null;
                }
                catch (Throwable throwable) {
                    Object var4_4 = null;
                    ((OutputStream)outputStream).close();
                    throw throwable;
                }
                ((OutputStream)outputStream).close();
                return void_;
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
                if (this.original != ((ClassDumpAction)object).original) {
                    return false;
                }
                if (this.suffix != ((ClassDumpAction)object).suffix) {
                    return false;
                }
                if (!this.target.equals(((ClassDumpAction)object).target)) {
                    return false;
                }
                if (!this.instrumentedType.equals(((ClassDumpAction)object).instrumentedType)) {
                    return false;
                }
                return Arrays.equals(this.binaryRepresentation, ((ClassDumpAction)object).binaryRepresentation);
            }

            public int hashCode() {
                long l = this.suffix;
                return ((((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.instrumentedType.hashCode()) * 31 + this.original) * 31 + (int)(l ^ l >>> 32)) * 31 + Arrays.hashCode(this.binaryRepresentation);
            }

            protected static interface Dispatcher {
                public void dump(TypeDescription var1, boolean var2, byte[] var3);

                @HashCodeAndEqualsPlugin.Enhance
                public static class Enabled
                implements Dispatcher {
                    private final String folder;
                    private final long timestamp;

                    protected Enabled(String folder, long timestamp) {
                        this.folder = folder;
                        this.timestamp = timestamp;
                    }

                    public void dump(TypeDescription instrumentedType, boolean original, byte[] binaryRepresentation) {
                        try {
                            Default.doPrivileged(new ClassDumpAction(this.folder, instrumentedType, original, this.timestamp, binaryRepresentation));
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
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
                        if (this.timestamp != ((Enabled)object).timestamp) {
                            return false;
                        }
                        return this.folder.equals(((Enabled)object).folder);
                    }

                    public int hashCode() {
                        long l = this.timestamp;
                        return (this.getClass().hashCode() * 31 + this.folder.hashCode()) * 31 + (int)(l ^ l >>> 32);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Disabled implements Dispatcher
                {
                    INSTANCE;


                    @Override
                    public void dump(TypeDescription instrumentedType, boolean original, byte[] binaryRepresentation) {
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForCreation<U>
        extends Default<U> {
            private final MethodPool methodPool;

            protected ForCreation(TypeDescription instrumentedType, ClassFileVersion classFileVersion, FieldPool fieldPool, MethodPool methodPool, RecordComponentPool recordComponentPool, List<? extends DynamicType> auxiliaryTypes, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, MethodList<?> instrumentedMethods, RecordComponentList<RecordComponentDescription.InDefinedShape> recordComponents, LoadedTypeInitializer loadedTypeInitializer, TypeInitializer typeInitializer, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool) {
                super(instrumentedType, classFileVersion, fieldPool, recordComponentPool, auxiliaryTypes, fields, methods, instrumentedMethods, recordComponents, loadedTypeInitializer, typeInitializer, typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool);
                this.methodPool = methodPool;
            }

            @Override
            public ContextClassVisitor wrap(ClassVisitor classVisitor, int writerFlags, int readerFlags) {
                Implementation.Context.ExtractableView implementationContext = this.implementationContextFactory.make(this.instrumentedType, this.auxiliaryTypeNamingStrategy, this.typeInitializer, this.classFileVersion, this.classFileVersion, (writerFlags & 2) == 0 && this.classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? ((readerFlags & 8) == 0 ? Implementation.Context.FrameGeneration.GENERATE : Implementation.Context.FrameGeneration.EXPAND) : Implementation.Context.FrameGeneration.DISABLED);
                return new ImplementationContextClassVisitor(new CreationClassVisitor(this.asmVisitorWrapper.wrap(this.instrumentedType, ValidatingClassVisitor.of(classVisitor, this.typeValidation), implementationContext, this.typePool, this.fields, this.methods, this.asmVisitorWrapper.mergeWriter(writerFlags), this.asmVisitorWrapper.mergeReader(readerFlags)), implementationContext), implementationContext);
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Relying on correlated type properties.")
            protected UnresolvedType create(TypeInitializer typeInitializer, ClassDumpAction.Dispatcher dispatcher) {
                MethodDescription.InDefinedShape enclosingMethod;
                int writerFlags = this.asmVisitorWrapper.mergeWriter(0);
                int readerFlags = this.asmVisitorWrapper.mergeReader(0);
                ClassWriter classWriter = this.classWriterStrategy.resolve(writerFlags, this.typePool);
                Implementation.Context.ExtractableView implementationContext = this.implementationContextFactory.make(this.instrumentedType, this.auxiliaryTypeNamingStrategy, typeInitializer, this.classFileVersion, this.classFileVersion, (writerFlags & 2) == 0 && this.classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? ((readerFlags & 8) == 0 ? Implementation.Context.FrameGeneration.GENERATE : Implementation.Context.FrameGeneration.EXPAND) : Implementation.Context.FrameGeneration.DISABLED);
                ClassVisitor classVisitor = this.asmVisitorWrapper.wrap(this.instrumentedType, ValidatingClassVisitor.of(classWriter, this.typeValidation), implementationContext, this.typePool, this.fields, this.methods, writerFlags, readerFlags);
                classVisitor.visit(this.classFileVersion.getMinorMajorVersion(), this.instrumentedType.getActualModifiers(!this.instrumentedType.isInterface()), this.instrumentedType.getInternalName(), this.instrumentedType.getGenericSignature(), (this.instrumentedType.getSuperClass() == null ? TypeDescription.ForLoadedType.of(Object.class) : this.instrumentedType.getSuperClass().asErasure()).getInternalName(), this.instrumentedType.getInterfaces().asErasures().toInternalNames());
                if (!this.instrumentedType.isNestHost()) {
                    classVisitor.visitNestHost(this.instrumentedType.getNestHost().getInternalName());
                }
                if ((enclosingMethod = this.instrumentedType.getEnclosingMethod()) != null) {
                    classVisitor.visitOuterClass(enclosingMethod.getDeclaringType().getInternalName(), enclosingMethod.getInternalName(), enclosingMethod.getDescriptor());
                } else if (this.instrumentedType.isLocalType() || this.instrumentedType.isAnonymousType()) {
                    classVisitor.visitOuterClass(this.instrumentedType.getEnclosingType().getInternalName(), NO_REFERENCE, NO_REFERENCE);
                }
                this.typeAttributeAppender.apply(classVisitor, this.instrumentedType, this.annotationValueFilterFactory.on(this.instrumentedType));
                if (this.instrumentedType.isNestHost()) {
                    for (TypeDescription typeDescription : (TypeList)this.instrumentedType.getNestMembers().filter(ElementMatchers.not(ElementMatchers.is(this.instrumentedType)))) {
                        classVisitor.visitNestMember(typeDescription.getInternalName());
                    }
                }
                for (TypeDescription typeDescription : this.instrumentedType.getPermittedSubtypes()) {
                    classVisitor.visitPermittedSubclass(typeDescription.getInternalName());
                }
                TypeDescription declaringType = this.instrumentedType.getDeclaringType();
                if (declaringType != null) {
                    classVisitor.visitInnerClass(this.instrumentedType.getInternalName(), declaringType.getInternalName(), this.instrumentedType.getSimpleName(), this.instrumentedType.getModifiers());
                } else if (this.instrumentedType.isLocalType()) {
                    classVisitor.visitInnerClass(this.instrumentedType.getInternalName(), NO_REFERENCE, this.instrumentedType.getSimpleName(), this.instrumentedType.getModifiers());
                } else if (this.instrumentedType.isAnonymousType()) {
                    classVisitor.visitInnerClass(this.instrumentedType.getInternalName(), NO_REFERENCE, NO_REFERENCE, this.instrumentedType.getModifiers());
                }
                for (TypeDescription typeDescription : this.instrumentedType.getDeclaredTypes()) {
                    classVisitor.visitInnerClass(typeDescription.getInternalName(), typeDescription.isMemberType() ? this.instrumentedType.getInternalName() : NO_REFERENCE, typeDescription.isAnonymousType() ? NO_REFERENCE : typeDescription.getSimpleName(), typeDescription.getModifiers());
                }
                for (RecordComponentDescription recordComponentDescription : this.recordComponents) {
                    this.recordComponentPool.target(recordComponentDescription).apply(classVisitor, this.annotationValueFilterFactory);
                }
                for (FieldDescription fieldDescription : this.fields) {
                    this.fieldPool.target(fieldDescription).apply(classVisitor, this.annotationValueFilterFactory);
                }
                for (MethodDescription methodDescription : this.instrumentedMethods) {
                    this.methodPool.target(methodDescription).apply(classVisitor, implementationContext, this.annotationValueFilterFactory);
                }
                implementationContext.drain(new TypeInitializer.Drain.Default(this.instrumentedType, this.methodPool, this.annotationValueFilterFactory), classVisitor, this.annotationValueFilterFactory);
                classVisitor.visitEnd();
                return new UnresolvedType(classWriter.toByteArray(), implementationContext.getAuxiliaryTypes());
            }

            @Override
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
                return this.methodPool.equals(((ForCreation)object).methodPool);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.methodPool.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class ImplementationContextClassVisitor
            extends ContextClassVisitor {
                private final Implementation.Context.ExtractableView implementationContext;

                protected ImplementationContextClassVisitor(ClassVisitor classVisitor, Implementation.Context.ExtractableView implementationContext) {
                    super(classVisitor);
                    this.implementationContext = implementationContext;
                }

                @Override
                public List<DynamicType> getAuxiliaryTypes() {
                    return CompoundList.of(ForCreation.this.auxiliaryTypes, this.implementationContext.getAuxiliaryTypes());
                }

                @Override
                public LoadedTypeInitializer getLoadedTypeInitializer() {
                    return ForCreation.this.loadedTypeInitializer;
                }
            }

            protected class CreationClassVisitor
            extends MetadataAwareClassVisitor {
                private final Implementation.Context.ExtractableView implementationContext;
                private final Set<String> declaredTypes;
                private final Set<SignatureKey> visitedFields;
                private final Set<SignatureKey> visitedMethods;

                protected CreationClassVisitor(ClassVisitor classVisitor, Implementation.Context.ExtractableView implementationContext) {
                    super(OpenedClassReader.ASM_API, classVisitor);
                    this.declaredTypes = new HashSet<String>();
                    this.visitedFields = new HashSet<SignatureKey>();
                    this.visitedMethods = new HashSet<SignatureKey>();
                    this.implementationContext = implementationContext;
                }

                protected void onAfterAttributes() {
                    ForCreation.this.typeAttributeAppender.apply(this.cv, ForCreation.this.instrumentedType, ForCreation.this.annotationValueFilterFactory.on(ForCreation.this.instrumentedType));
                }

                protected void onVisitInnerClass(String internalName, @MaybeNull String outerName, @MaybeNull String innerName, int modifiers) {
                    this.declaredTypes.add(internalName);
                    super.onVisitInnerClass(internalName, outerName, innerName, modifiers);
                }

                protected FieldVisitor onVisitField(int modifiers, String name, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
                    this.visitedFields.add(new SignatureKey(name, descriptor));
                    return super.onVisitField(modifiers, name, descriptor, signature, value);
                }

                protected MethodVisitor onVisitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
                    this.visitedMethods.add(new SignatureKey(internalName, descriptor));
                    return super.onVisitMethod(modifiers, internalName, descriptor, signature, exception);
                }

                protected void onVisitEnd() {
                    for (TypeDescription typeDescription : ForCreation.this.instrumentedType.getDeclaredTypes()) {
                        if (this.declaredTypes.contains(typeDescription.getInternalName())) continue;
                        this.cv.visitInnerClass(typeDescription.getInternalName(), typeDescription.isMemberType() ? ForCreation.this.instrumentedType.getInternalName() : NO_REFERENCE, typeDescription.isAnonymousType() ? NO_REFERENCE : typeDescription.getSimpleName(), typeDescription.getModifiers());
                    }
                    for (FieldDescription fieldDescription : ForCreation.this.fields) {
                        if (this.visitedFields.contains(new SignatureKey(fieldDescription.getName(), fieldDescription.getDescriptor()))) continue;
                        ForCreation.this.fieldPool.target(fieldDescription).apply(this.cv, ForCreation.this.annotationValueFilterFactory);
                    }
                    for (MethodDescription methodDescription : ForCreation.this.instrumentedMethods) {
                        if (this.visitedMethods.contains(new SignatureKey(methodDescription.getInternalName(), methodDescription.getDescriptor()))) continue;
                        ForCreation.this.methodPool.target(methodDescription).apply(this.cv, this.implementationContext, ForCreation.this.annotationValueFilterFactory);
                    }
                    this.implementationContext.drain(new TypeInitializer.Drain.Default(ForCreation.this.instrumentedType, ForCreation.this.methodPool, ForCreation.this.annotationValueFilterFactory), this.cv, ForCreation.this.annotationValueFilterFactory);
                    super.onVisitEnd();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class ForInlining<U>
        extends Default<U> {
            @AlwaysNull
            private static final FieldVisitor IGNORE_FIELD = null;
            @AlwaysNull
            private static final MethodVisitor IGNORE_METHOD = null;
            @AlwaysNull
            private static final RecordComponentVisitor IGNORE_RECORD_COMPONENT = null;
            @AlwaysNull
            private static final AnnotationVisitor IGNORE_ANNOTATION = null;
            protected final TypeDescription originalType;
            protected final ClassFileLocator classFileLocator;

            protected ForInlining(TypeDescription instrumentedType, ClassFileVersion classFileVersion, FieldPool fieldPool, RecordComponentPool recordComponentPool, List<? extends DynamicType> auxiliaryTypes, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, MethodList<?> instrumentedMethods, RecordComponentList<RecordComponentDescription.InDefinedShape> recordComponents, LoadedTypeInitializer loadedTypeInitializer, TypeInitializer typeInitializer, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool, TypeDescription originalType, ClassFileLocator classFileLocator) {
                super(instrumentedType, classFileVersion, fieldPool, recordComponentPool, auxiliaryTypes, fields, methods, instrumentedMethods, recordComponents, loadedTypeInitializer, typeInitializer, typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool);
                this.originalType = originalType;
                this.classFileLocator = classFileLocator;
            }

            @Override
            public ContextClassVisitor wrap(ClassVisitor classVisitor, int writerFlags, int readerFlags) {
                ContextRegistry contextRegistry = new ContextRegistry();
                return new RegistryContextClassVisitor(this.writeTo(ValidatingClassVisitor.of(classVisitor, this.typeValidation), this.typeInitializer, contextRegistry, this.asmVisitorWrapper.mergeWriter(writerFlags), this.asmVisitorWrapper.mergeReader(readerFlags)), contextRegistry);
            }

            @Override
            protected UnresolvedType create(TypeInitializer typeInitializer, ClassDumpAction.Dispatcher dispatcher) {
                try {
                    int writerFlags = this.asmVisitorWrapper.mergeWriter(0);
                    int readerFlags = this.asmVisitorWrapper.mergeReader(0);
                    byte[] binaryRepresentation = this.classFileLocator.locate(this.originalType.getName()).resolve();
                    dispatcher.dump(this.instrumentedType, true, binaryRepresentation);
                    ClassReader classReader = OpenedClassReader.of(binaryRepresentation);
                    ClassWriter classWriter = this.classWriterStrategy.resolve(writerFlags, this.typePool, classReader);
                    ContextRegistry contextRegistry = new ContextRegistry();
                    classReader.accept(this.writeTo(ValidatingClassVisitor.of(classWriter, this.typeValidation), typeInitializer, contextRegistry, writerFlags, readerFlags), readerFlags);
                    return new UnresolvedType(classWriter.toByteArray(), contextRegistry.getAuxiliaryTypes());
                }
                catch (IOException exception) {
                    throw new RuntimeException("The class file could not be written", exception);
                }
            }

            protected abstract ClassVisitor writeTo(ClassVisitor var1, TypeInitializer var2, ContextRegistry var3, int var4, int var5);

            @Override
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
                if (!this.originalType.equals(((ForInlining)object).originalType)) {
                    return false;
                }
                return this.classFileLocator.equals(((ForInlining)object).classFileLocator);
            }

            @Override
            public int hashCode() {
                return (super.hashCode() * 31 + this.originalType.hashCode()) * 31 + this.classFileLocator.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class WithDecorationOnly<V>
            extends ForInlining<V> {
                protected WithDecorationOnly(TypeDescription instrumentedType, ClassFileVersion classFileVersion, List<? extends DynamicType> auxiliaryTypes, MethodList<?> methods, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool, ClassFileLocator classFileLocator) {
                    super(instrumentedType, classFileVersion, FieldPool.Disabled.INSTANCE, RecordComponentPool.Disabled.INSTANCE, auxiliaryTypes, new LazyFieldList(instrumentedType), methods, new MethodList.Empty(), new RecordComponentList.Empty<RecordComponentDescription.InDefinedShape>(), LoadedTypeInitializer.NoOp.INSTANCE, TypeInitializer.None.INSTANCE, typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool, instrumentedType, classFileLocator);
                }

                @Override
                protected ClassVisitor writeTo(ClassVisitor classVisitor, TypeInitializer typeInitializer, ContextRegistry contextRegistry, int writerFlags, int readerFlags) {
                    if (typeInitializer.isDefined()) {
                        throw new UnsupportedOperationException("Cannot apply a type initializer for a decoration");
                    }
                    return new DecorationClassVisitor(classVisitor, contextRegistry, writerFlags, readerFlags);
                }

                @SuppressFBWarnings(value={"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"}, justification="Field access order is implied by ASM.")
                protected class DecorationClassVisitor
                extends MetadataAwareClassVisitor
                implements TypeInitializer.Drain {
                    private final ContextRegistry contextRegistry;
                    private final int writerFlags;
                    private final int readerFlags;
                    @UnknownNull
                    private Implementation.Context.ExtractableView implementationContext;

                    protected DecorationClassVisitor(ClassVisitor classVisitor, ContextRegistry contextRegistry, int writerFlags, int readerFlags) {
                        super(OpenedClassReader.ASM_API, classVisitor);
                        this.contextRegistry = contextRegistry;
                        this.writerFlags = writerFlags;
                        this.readerFlags = readerFlags;
                    }

                    public void visit(int classFileVersionNumber, int modifiers, String internalName, String genericSignature, String superClassInternalName, String[] interfaceTypeInternalName) {
                        ClassFileVersion classFileVersion = ClassFileVersion.ofMinorMajor(classFileVersionNumber);
                        this.implementationContext = WithDecorationOnly.this.implementationContextFactory.make(WithDecorationOnly.this.instrumentedType, WithDecorationOnly.this.auxiliaryTypeNamingStrategy, WithDecorationOnly.this.typeInitializer, classFileVersion, WithDecorationOnly.this.classFileVersion, (this.writerFlags & 2) == 0 && classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? ((this.readerFlags & 8) == 0 ? Implementation.Context.FrameGeneration.GENERATE : Implementation.Context.FrameGeneration.EXPAND) : Implementation.Context.FrameGeneration.DISABLED);
                        this.contextRegistry.setImplementationContext(this.implementationContext);
                        this.cv = WithDecorationOnly.this.asmVisitorWrapper.wrap(WithDecorationOnly.this.instrumentedType, this.cv, this.implementationContext, WithDecorationOnly.this.typePool, WithDecorationOnly.this.fields, WithDecorationOnly.this.methods, this.writerFlags, this.readerFlags);
                        this.cv.visit(classFileVersionNumber, modifiers, internalName, genericSignature, superClassInternalName, interfaceTypeInternalName);
                    }

                    @MaybeNull
                    protected AnnotationVisitor onVisitTypeAnnotation(int typeReference, TypePath typePath, String descriptor, boolean visible) {
                        return WithDecorationOnly.this.annotationRetention.isEnabled() ? this.cv.visitTypeAnnotation(typeReference, typePath, descriptor, visible) : IGNORE_ANNOTATION;
                    }

                    @MaybeNull
                    protected AnnotationVisitor onVisitAnnotation(String descriptor, boolean visible) {
                        return WithDecorationOnly.this.annotationRetention.isEnabled() ? this.cv.visitAnnotation(descriptor, visible) : IGNORE_ANNOTATION;
                    }

                    protected void onAfterAttributes() {
                        WithDecorationOnly.this.typeAttributeAppender.apply(this.cv, WithDecorationOnly.this.instrumentedType, WithDecorationOnly.this.annotationValueFilterFactory.on(WithDecorationOnly.this.instrumentedType));
                    }

                    protected void onVisitEnd() {
                        this.implementationContext.drain(this, this.cv, WithDecorationOnly.this.annotationValueFilterFactory);
                        this.cv.visitEnd();
                    }

                    public void apply(ClassVisitor classVisitor, TypeInitializer typeInitializer, Implementation.Context implementationContext) {
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class LazyFieldList
                extends FieldList.AbstractBase<FieldDescription.InDefinedShape> {
                    private final TypeDescription instrumentedType;

                    protected LazyFieldList(TypeDescription instrumentedType) {
                        this.instrumentedType = instrumentedType;
                    }

                    @Override
                    public FieldDescription.InDefinedShape get(int index) {
                        return (FieldDescription.InDefinedShape)this.instrumentedType.getDeclaredFields().get(index);
                    }

                    @Override
                    public int size() {
                        return this.instrumentedType.getDeclaredFields().size();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class WithFullProcessing<V>
            extends ForInlining<V> {
                private static final Object[] EMPTY = new Object[0];
                private final MethodRegistry.Prepared methodRegistry;
                private final Implementation.Target.Factory implementationTargetFactory;
                private final MethodRebaseResolver methodRebaseResolver;

                protected WithFullProcessing(TypeDescription instrumentedType, ClassFileVersion classFileVersion, FieldPool fieldPool, RecordComponentPool recordComponentPool, List<? extends DynamicType> auxiliaryTypes, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, MethodList<?> instrumentedMethods, RecordComponentList<RecordComponentDescription.InDefinedShape> recordComponents, LoadedTypeInitializer loadedTypeInitializer, TypeInitializer typeInitializer, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, Implementation.Context.Factory implementationContextFactory, TypeValidation typeValidation, ClassWriterStrategy classWriterStrategy, TypePool typePool, TypeDescription originalType, ClassFileLocator classFileLocator, MethodRegistry.Prepared methodRegistry, Implementation.Target.Factory implementationTargetFactory, MethodRebaseResolver methodRebaseResolver) {
                    super(instrumentedType, classFileVersion, fieldPool, recordComponentPool, auxiliaryTypes, fields, methods, instrumentedMethods, recordComponents, loadedTypeInitializer, typeInitializer, typeAttributeAppender, asmVisitorWrapper, annotationValueFilterFactory, annotationRetention, auxiliaryTypeNamingStrategy, implementationContextFactory, typeValidation, classWriterStrategy, typePool, originalType, classFileLocator);
                    this.methodRegistry = methodRegistry;
                    this.implementationTargetFactory = implementationTargetFactory;
                    this.methodRebaseResolver = methodRebaseResolver;
                }

                @Override
                protected ClassVisitor writeTo(ClassVisitor classVisitor, TypeInitializer typeInitializer, ContextRegistry contextRegistry, int writerFlags, int readerFlags) {
                    classVisitor = new RedefinitionClassVisitor(classVisitor, typeInitializer, contextRegistry, writerFlags, readerFlags);
                    return this.originalType.getName().equals(this.instrumentedType.getName()) ? classVisitor : new OpenedClassRemapper(classVisitor, new SimpleRemapper(this.originalType.getInternalName(), this.instrumentedType.getInternalName()));
                }

                @Override
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
                    if (!this.methodRegistry.equals(((WithFullProcessing)object).methodRegistry)) {
                        return false;
                    }
                    if (!this.implementationTargetFactory.equals(((WithFullProcessing)object).implementationTargetFactory)) {
                        return false;
                    }
                    return this.methodRebaseResolver.equals(((WithFullProcessing)object).methodRebaseResolver);
                }

                @Override
                public int hashCode() {
                    return ((super.hashCode() * 31 + this.methodRegistry.hashCode()) * 31 + this.implementationTargetFactory.hashCode()) * 31 + this.methodRebaseResolver.hashCode();
                }

                @SuppressFBWarnings(value={"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"}, justification="Field access order is implied by ASM.")
                protected class RedefinitionClassVisitor
                extends MetadataAwareClassVisitor {
                    private final TypeInitializer typeInitializer;
                    private final ContextRegistry contextRegistry;
                    private final int writerFlags;
                    private final int readerFlags;
                    private final LinkedHashMap<SignatureKey, FieldDescription> declarableFields;
                    private final LinkedHashMap<SignatureKey, MethodDescription> declarableMethods;
                    private final LinkedHashMap<String, RecordComponentDescription> declarableRecordComponents;
                    private final Set<String> nestMembers;
                    private final LinkedHashMap<String, TypeDescription> declaredTypes;
                    @MaybeNull
                    private final Set<String> permittedSubclasses;
                    @UnknownNull
                    private MethodPool methodPool;
                    @UnknownNull
                    private InitializationHandler initializationHandler;
                    @UnknownNull
                    private Implementation.Context.ExtractableView implementationContext;
                    private boolean retainDeprecationModifiers;

                    protected RedefinitionClassVisitor(ClassVisitor classVisitor, TypeInitializer typeInitializer, ContextRegistry contextRegistry, int writerFlags, int readerFlags) {
                        super(OpenedClassReader.ASM_API, classVisitor);
                        this.typeInitializer = typeInitializer;
                        this.contextRegistry = contextRegistry;
                        this.writerFlags = writerFlags;
                        this.readerFlags = readerFlags;
                        this.declarableFields = new LinkedHashMap((int)Math.ceil((double)WithFullProcessing.this.fields.size() / 0.75));
                        for (FieldDescription fieldDescription : WithFullProcessing.this.fields) {
                            this.declarableFields.put(new SignatureKey(fieldDescription.getInternalName(), fieldDescription.getDescriptor()), fieldDescription);
                        }
                        this.declarableMethods = new LinkedHashMap((int)Math.ceil((double)WithFullProcessing.this.instrumentedMethods.size() / 0.75));
                        for (MethodDescription methodDescription : WithFullProcessing.this.instrumentedMethods) {
                            this.declarableMethods.put(new SignatureKey(methodDescription.getInternalName(), methodDescription.getDescriptor()), methodDescription);
                        }
                        this.declarableRecordComponents = new LinkedHashMap((int)Math.ceil((double)WithFullProcessing.this.recordComponents.size() / 0.75));
                        for (RecordComponentDescription recordComponentDescription : WithFullProcessing.this.recordComponents) {
                            this.declarableRecordComponents.put(recordComponentDescription.getActualName(), recordComponentDescription);
                        }
                        if (WithFullProcessing.this.instrumentedType.isNestHost()) {
                            this.nestMembers = new LinkedHashSet<String>((int)Math.ceil((double)WithFullProcessing.this.instrumentedType.getNestMembers().size() / 0.75));
                            for (TypeDescription typeDescription : (TypeList)WithFullProcessing.this.instrumentedType.getNestMembers().filter(ElementMatchers.not(ElementMatchers.is(WithFullProcessing.this.instrumentedType)))) {
                                this.nestMembers.add(typeDescription.getInternalName());
                            }
                        } else {
                            this.nestMembers = Collections.emptySet();
                        }
                        this.declaredTypes = new LinkedHashMap((int)Math.ceil((double)WithFullProcessing.this.instrumentedType.getDeclaredTypes().size() / 0.75));
                        for (TypeDescription typeDescription : WithFullProcessing.this.instrumentedType.getDeclaredTypes()) {
                            this.declaredTypes.put(typeDescription.getInternalName(), typeDescription);
                        }
                        if (WithFullProcessing.this.instrumentedType.isSealed()) {
                            this.permittedSubclasses = new LinkedHashSet<String>((int)Math.ceil((double)WithFullProcessing.this.instrumentedType.getPermittedSubtypes().size() / 0.75));
                            for (TypeDescription typeDescription : WithFullProcessing.this.instrumentedType.getPermittedSubtypes()) {
                                this.permittedSubclasses.add(typeDescription.getInternalName());
                            }
                        } else {
                            this.permittedSubclasses = null;
                        }
                    }

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Relying on correlated type properties.")
                    public void visit(int classFileVersionNumber, int modifiers, String internalName, String genericSignature, String superClassInternalName, String[] interfaceTypeInternalName) {
                        ClassFileVersion classFileVersion = ClassFileVersion.ofMinorMajor(classFileVersionNumber);
                        this.methodPool = WithFullProcessing.this.methodRegistry.compile(WithFullProcessing.this.implementationTargetFactory, classFileVersion);
                        this.initializationHandler = new InitializationHandler.Creating(WithFullProcessing.this.instrumentedType, this.methodPool, WithFullProcessing.this.annotationValueFilterFactory);
                        this.implementationContext = WithFullProcessing.this.implementationContextFactory.make(WithFullProcessing.this.instrumentedType, WithFullProcessing.this.auxiliaryTypeNamingStrategy, this.typeInitializer, classFileVersion, WithFullProcessing.this.classFileVersion, (this.writerFlags & 2) == 0 && classFileVersion.isAtLeast(ClassFileVersion.JAVA_V6) ? ((this.readerFlags & 8) == 0 ? Implementation.Context.FrameGeneration.GENERATE : Implementation.Context.FrameGeneration.EXPAND) : Implementation.Context.FrameGeneration.DISABLED);
                        this.retainDeprecationModifiers = classFileVersion.isLessThan(ClassFileVersion.JAVA_V5);
                        this.contextRegistry.setImplementationContext(this.implementationContext);
                        this.cv = WithFullProcessing.this.asmVisitorWrapper.wrap(WithFullProcessing.this.instrumentedType, this.cv, this.implementationContext, WithFullProcessing.this.typePool, WithFullProcessing.this.fields, WithFullProcessing.this.methods, this.writerFlags, this.readerFlags);
                        this.cv.visit(classFileVersionNumber, WithFullProcessing.this.instrumentedType.getActualModifiers((modifiers & 0x20) != 0 && !WithFullProcessing.this.instrumentedType.isInterface()) | this.resolveDeprecationModifiers(modifiers) | ((modifiers & 0x10) != 0 && WithFullProcessing.this.instrumentedType.isAnonymousType() ? 16 : 0), WithFullProcessing.this.instrumentedType.getInternalName(), TypeDescription.AbstractBase.RAW_TYPES ? genericSignature : WithFullProcessing.this.instrumentedType.getGenericSignature(), WithFullProcessing.this.instrumentedType.getSuperClass() == null ? (WithFullProcessing.this.instrumentedType.isInterface() ? TypeDescription.ForLoadedType.of(Object.class).getInternalName() : NO_REFERENCE) : WithFullProcessing.this.instrumentedType.getSuperClass().asErasure().getInternalName(), WithFullProcessing.this.instrumentedType.getInterfaces().asErasures().toInternalNames());
                    }

                    protected void onVisitNestHost(String nestHost) {
                        this.onNestHost();
                    }

                    protected void onNestHost() {
                        if (!WithFullProcessing.this.instrumentedType.isNestHost()) {
                            this.cv.visitNestHost(WithFullProcessing.this.instrumentedType.getNestHost().getInternalName());
                        }
                    }

                    protected void onVisitPermittedSubclass(String permittedSubclass) {
                        if (this.permittedSubclasses != null && this.permittedSubclasses.remove(permittedSubclass)) {
                            this.cv.visitPermittedSubclass(permittedSubclass);
                        }
                    }

                    protected void onVisitOuterClass(String owner, @MaybeNull String name, @MaybeNull String descriptor) {
                        try {
                            this.onOuterType();
                        }
                        catch (Throwable ignored) {
                            this.cv.visitOuterClass(owner, name, descriptor);
                        }
                    }

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH"}, justification="Relying on correlated type properties.")
                    protected void onOuterType() {
                        MethodDescription.InDefinedShape enclosingMethod = WithFullProcessing.this.instrumentedType.getEnclosingMethod();
                        if (enclosingMethod != null) {
                            this.cv.visitOuterClass(enclosingMethod.getDeclaringType().getInternalName(), enclosingMethod.getInternalName(), enclosingMethod.getDescriptor());
                        } else if (WithFullProcessing.this.instrumentedType.isLocalType() || WithFullProcessing.this.instrumentedType.isAnonymousType()) {
                            this.cv.visitOuterClass(WithFullProcessing.this.instrumentedType.getEnclosingType().getInternalName(), NO_REFERENCE, NO_REFERENCE);
                        }
                    }

                    protected void onAfterAttributes() {
                        WithFullProcessing.this.typeAttributeAppender.apply(this.cv, WithFullProcessing.this.instrumentedType, WithFullProcessing.this.annotationValueFilterFactory.on(WithFullProcessing.this.instrumentedType));
                    }

                    @MaybeNull
                    protected AnnotationVisitor onVisitTypeAnnotation(int typeReference, TypePath typePath, String descriptor, boolean visible) {
                        return WithFullProcessing.this.annotationRetention.isEnabled() ? this.cv.visitTypeAnnotation(typeReference, typePath, descriptor, visible) : IGNORE_ANNOTATION;
                    }

                    @MaybeNull
                    protected AnnotationVisitor onVisitAnnotation(String descriptor, boolean visible) {
                        return WithFullProcessing.this.annotationRetention.isEnabled() ? this.cv.visitAnnotation(descriptor, visible) : IGNORE_ANNOTATION;
                    }

                    @MaybeNull
                    protected RecordComponentVisitor onVisitRecordComponent(String name, String descriptor, @MaybeNull String genericSignature) {
                        RecordComponentPool.Record record;
                        RecordComponentDescription recordComponentDescription = (RecordComponentDescription)this.declarableRecordComponents.remove(name);
                        if (recordComponentDescription != null && !(record = WithFullProcessing.this.recordComponentPool.target(recordComponentDescription)).isImplicit()) {
                            return this.redefine(record, genericSignature);
                        }
                        return this.cv.visitRecordComponent(name, descriptor, genericSignature);
                    }

                    @MaybeNull
                    protected RecordComponentVisitor redefine(RecordComponentPool.Record record, @MaybeNull String genericSignature) {
                        RecordComponentDescription recordComponentDescription = record.getRecordComponent();
                        RecordComponentVisitor recordComponentVisitor = this.cv.visitRecordComponent(recordComponentDescription.getActualName(), recordComponentDescription.getDescriptor(), TypeDescription.AbstractBase.RAW_TYPES ? genericSignature : recordComponentDescription.getGenericSignature());
                        return recordComponentVisitor == null ? IGNORE_RECORD_COMPONENT : new AttributeObtainingRecordComponentVisitor(recordComponentVisitor, record);
                    }

                    @MaybeNull
                    protected FieldVisitor onVisitField(int modifiers, String internalName, String descriptor, @MaybeNull String genericSignature, @MaybeNull Object value) {
                        FieldPool.Record record;
                        FieldDescription fieldDescription = (FieldDescription)this.declarableFields.remove(new SignatureKey(internalName, descriptor));
                        if (fieldDescription != null && !(record = WithFullProcessing.this.fieldPool.target(fieldDescription)).isImplicit()) {
                            return this.redefine(record, value, modifiers, genericSignature);
                        }
                        return this.cv.visitField(modifiers, internalName, descriptor, genericSignature, value);
                    }

                    @MaybeNull
                    protected FieldVisitor redefine(FieldPool.Record record, @MaybeNull Object value, int modifiers, @MaybeNull String genericSignature) {
                        FieldDescription instrumentedField = record.getField();
                        FieldVisitor fieldVisitor = this.cv.visitField(instrumentedField.getActualModifiers() | this.resolveDeprecationModifiers(modifiers), instrumentedField.getInternalName(), instrumentedField.getDescriptor(), TypeDescription.AbstractBase.RAW_TYPES ? genericSignature : instrumentedField.getGenericSignature(), record.resolveDefault(value));
                        return fieldVisitor == null ? IGNORE_FIELD : new AttributeObtainingFieldVisitor(fieldVisitor, record);
                    }

                    @MaybeNull
                    protected MethodVisitor onVisitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String genericSignature, @MaybeNull String[] exceptionName) {
                        if (internalName.equals("<clinit>")) {
                            MethodVisitor methodVisitor;
                            MethodVisitor methodVisitor2 = this.cv.visitMethod(modifiers, internalName, descriptor, genericSignature, exceptionName);
                            if (methodVisitor2 == null) {
                                methodVisitor = IGNORE_METHOD;
                            } else {
                                this.initializationHandler = InitializationHandler.Appending.of(this.implementationContext.isEnabled(), methodVisitor2, WithFullProcessing.this.instrumentedType, this.methodPool, WithFullProcessing.this.annotationValueFilterFactory, (this.writerFlags & 2) == 0 && this.implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V6), (this.readerFlags & 8) != 0);
                                methodVisitor = (MethodVisitor)((Object)this.initializationHandler);
                            }
                            return methodVisitor;
                        }
                        MethodDescription methodDescription = (MethodDescription)this.declarableMethods.remove(new SignatureKey(internalName, descriptor));
                        return methodDescription == null ? this.cv.visitMethod(modifiers, internalName, descriptor, genericSignature, exceptionName) : this.redefine(methodDescription, (modifiers & 0x400) != 0, modifiers, genericSignature);
                    }

                    @MaybeNull
                    protected MethodVisitor redefine(MethodDescription methodDescription, boolean abstractOrigin, int modifiers, @MaybeNull String genericSignature) {
                        MethodPool.Record record = this.methodPool.target(methodDescription);
                        if (!record.getSort().isDefined()) {
                            return this.cv.visitMethod(methodDescription.getActualModifiers() | this.resolveDeprecationModifiers(modifiers), methodDescription.getInternalName(), methodDescription.getDescriptor(), TypeDescription.AbstractBase.RAW_TYPES ? genericSignature : methodDescription.getGenericSignature(), methodDescription.getExceptionTypes().asErasures().toInternalNames());
                        }
                        MethodDescription implementedMethod = record.getMethod();
                        MethodVisitor methodVisitor = this.cv.visitMethod(ModifierContributor.Resolver.of(Collections.singleton(record.getVisibility())).resolve(implementedMethod.getActualModifiers(record.getSort().isImplemented())) | this.resolveDeprecationModifiers(modifiers), implementedMethod.getInternalName(), implementedMethod.getDescriptor(), TypeDescription.AbstractBase.RAW_TYPES ? genericSignature : implementedMethod.getGenericSignature(), implementedMethod.getExceptionTypes().asErasures().toInternalNames());
                        if (methodVisitor == null) {
                            return IGNORE_METHOD;
                        }
                        if (abstractOrigin) {
                            return new AttributeObtainingMethodVisitor(methodVisitor, record);
                        }
                        if (methodDescription.isNative()) {
                            MethodVisitor rebasedMethodVisitor;
                            MethodRebaseResolver.Resolution resolution = WithFullProcessing.this.methodRebaseResolver.resolve((MethodDescription.InDefinedShape)implementedMethod.asDefined());
                            if (resolution.isRebased() && (rebasedMethodVisitor = super.visitMethod(resolution.getResolvedMethod().getActualModifiers() | this.resolveDeprecationModifiers(modifiers), resolution.getResolvedMethod().getInternalName(), resolution.getResolvedMethod().getDescriptor(), TypeDescription.AbstractBase.RAW_TYPES ? genericSignature : implementedMethod.getGenericSignature(), resolution.getResolvedMethod().getExceptionTypes().asErasures().toInternalNames())) != null) {
                                rebasedMethodVisitor.visitEnd();
                            }
                            return new AttributeObtainingMethodVisitor(methodVisitor, record);
                        }
                        return new CodePreservingMethodVisitor(methodVisitor, record, WithFullProcessing.this.methodRebaseResolver.resolve((MethodDescription.InDefinedShape)implementedMethod.asDefined()));
                    }

                    protected void onVisitInnerClass(String internalName, @MaybeNull String outerName, @MaybeNull String innerName, int modifiers) {
                        if (!internalName.equals(WithFullProcessing.this.instrumentedType.getInternalName())) {
                            TypeDescription declaredType = (TypeDescription)this.declaredTypes.remove(internalName);
                            if (declaredType == null) {
                                this.cv.visitInnerClass(internalName, outerName, innerName, modifiers);
                            } else {
                                this.cv.visitInnerClass(internalName, declaredType.isMemberType() || outerName != null && innerName == null && declaredType.isAnonymousType() ? WithFullProcessing.this.instrumentedType.getInternalName() : NO_REFERENCE, declaredType.isAnonymousType() ? NO_REFERENCE : declaredType.getSimpleName(), declaredType.getModifiers());
                            }
                        }
                    }

                    protected void onVisitNestMember(String nestMember) {
                        if (WithFullProcessing.this.instrumentedType.isNestHost() && this.nestMembers.remove(nestMember)) {
                            this.cv.visitNestMember(nestMember);
                        }
                    }

                    protected void onVisitEnd() {
                        TypeDescription declaringType;
                        for (String nestMember : this.nestMembers) {
                            this.cv.visitNestMember(nestMember);
                        }
                        if (this.permittedSubclasses != null) {
                            for (String permittedSubclass : this.permittedSubclasses) {
                                this.cv.visitPermittedSubclass(permittedSubclass);
                            }
                        }
                        if ((declaringType = WithFullProcessing.this.instrumentedType.getDeclaringType()) != null) {
                            this.cv.visitInnerClass(WithFullProcessing.this.instrumentedType.getInternalName(), declaringType.getInternalName(), WithFullProcessing.this.instrumentedType.getSimpleName(), WithFullProcessing.this.instrumentedType.getModifiers());
                        } else if (WithFullProcessing.this.instrumentedType.isLocalType()) {
                            this.cv.visitInnerClass(WithFullProcessing.this.instrumentedType.getInternalName(), NO_REFERENCE, WithFullProcessing.this.instrumentedType.getSimpleName(), WithFullProcessing.this.instrumentedType.getModifiers());
                        } else if (WithFullProcessing.this.instrumentedType.isAnonymousType()) {
                            this.cv.visitInnerClass(WithFullProcessing.this.instrumentedType.getInternalName(), NO_REFERENCE, NO_REFERENCE, WithFullProcessing.this.instrumentedType.getModifiers());
                        }
                        for (TypeDescription typeDescription : this.declaredTypes.values()) {
                            this.cv.visitInnerClass(typeDescription.getInternalName(), typeDescription.isMemberType() ? WithFullProcessing.this.instrumentedType.getInternalName() : NO_REFERENCE, typeDescription.isAnonymousType() ? NO_REFERENCE : typeDescription.getSimpleName(), typeDescription.getModifiers());
                        }
                        for (RecordComponentDescription recordComponent : this.declarableRecordComponents.values()) {
                            WithFullProcessing.this.recordComponentPool.target(recordComponent).apply(this.cv, WithFullProcessing.this.annotationValueFilterFactory);
                        }
                        for (FieldDescription fieldDescription : this.declarableFields.values()) {
                            WithFullProcessing.this.fieldPool.target(fieldDescription).apply(this.cv, WithFullProcessing.this.annotationValueFilterFactory);
                        }
                        for (MethodDescription methodDescription : this.declarableMethods.values()) {
                            this.methodPool.target(methodDescription).apply(this.cv, this.implementationContext, WithFullProcessing.this.annotationValueFilterFactory);
                        }
                        this.initializationHandler.complete(this.cv, this.implementationContext);
                        this.cv.visitEnd();
                    }

                    private int resolveDeprecationModifiers(int modifiers) {
                        return this.retainDeprecationModifiers && (modifiers & 0x20000) != 0 ? 131072 : 0;
                    }

                    protected class AttributeObtainingMethodVisitor
                    extends MethodVisitor {
                        private final MethodVisitor actualMethodVisitor;
                        private final MethodPool.Record record;

                        protected AttributeObtainingMethodVisitor(MethodVisitor actualMethodVisitor, MethodPool.Record record) {
                            super(OpenedClassReader.ASM_API, actualMethodVisitor);
                            this.actualMethodVisitor = actualMethodVisitor;
                            this.record = record;
                            record.applyHead(actualMethodVisitor);
                        }

                        @MaybeNull
                        public AnnotationVisitor visitAnnotationDefault() {
                            return IGNORE_ANNOTATION;
                        }

                        @MaybeNull
                        public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitTypeAnnotation(typeReference, typePath, descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        @MaybeNull
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitAnnotation(descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public void visitAnnotableParameterCount(int count, boolean visible) {
                            if (WithFullProcessing.this.annotationRetention.isEnabled()) {
                                super.visitAnnotableParameterCount(count, visible);
                            }
                        }

                        @MaybeNull
                        public AnnotationVisitor visitParameterAnnotation(int index, String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitParameterAnnotation(index, descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public void visitCode() {
                            this.mv = IGNORE_METHOD;
                        }

                        public void visitEnd() {
                            this.record.applyBody(this.actualMethodVisitor, RedefinitionClassVisitor.this.implementationContext, WithFullProcessing.this.annotationValueFilterFactory);
                            this.actualMethodVisitor.visitEnd();
                        }
                    }

                    protected class CodePreservingMethodVisitor
                    extends MethodVisitor {
                        private final MethodVisitor actualMethodVisitor;
                        private final MethodPool.Record record;
                        private final MethodRebaseResolver.Resolution resolution;

                        protected CodePreservingMethodVisitor(MethodVisitor actualMethodVisitor, MethodPool.Record record, MethodRebaseResolver.Resolution resolution) {
                            super(OpenedClassReader.ASM_API, actualMethodVisitor);
                            this.actualMethodVisitor = actualMethodVisitor;
                            this.record = record;
                            this.resolution = resolution;
                            record.applyHead(actualMethodVisitor);
                        }

                        @MaybeNull
                        public AnnotationVisitor visitAnnotationDefault() {
                            return IGNORE_ANNOTATION;
                        }

                        @MaybeNull
                        public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitTypeAnnotation(typeReference, typePath, descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        @MaybeNull
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitAnnotation(descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public void visitAnnotableParameterCount(int count, boolean visible) {
                            if (WithFullProcessing.this.annotationRetention.isEnabled()) {
                                super.visitAnnotableParameterCount(count, visible);
                            }
                        }

                        @MaybeNull
                        public AnnotationVisitor visitParameterAnnotation(int index, String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitParameterAnnotation(index, descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public void visitCode() {
                            this.record.applyBody(this.actualMethodVisitor, RedefinitionClassVisitor.this.implementationContext, WithFullProcessing.this.annotationValueFilterFactory);
                            this.actualMethodVisitor.visitEnd();
                            if (this.resolution.isRebased()) {
                                this.mv = RedefinitionClassVisitor.this.cv.visitMethod(this.resolution.getResolvedMethod().getActualModifiers(), this.resolution.getResolvedMethod().getInternalName(), this.resolution.getResolvedMethod().getDescriptor(), this.resolution.getResolvedMethod().getGenericSignature(), this.resolution.getResolvedMethod().getExceptionTypes().asErasures().toInternalNames());
                                super.visitCode();
                                if (!this.resolution.getAppendedParameters().isEmpty() && RedefinitionClassVisitor.this.implementationContext.getFrameGeneration().isActive()) {
                                    if (RedefinitionClassVisitor.this.implementationContext.getFrameGeneration() == Implementation.Context.FrameGeneration.GENERATE && this.resolution.getAppendedParameters().size() < 4) {
                                        super.visitFrame(2, this.resolution.getAppendedParameters().size(), EMPTY, EMPTY.length, EMPTY);
                                    } else {
                                        Object[] frame = new Object[this.resolution.getResolvedMethod().getParameters().size() - this.resolution.getAppendedParameters().size() + 1];
                                        frame[0] = Opcodes.UNINITIALIZED_THIS;
                                        for (int index = 1; index < frame.length; ++index) {
                                            TypeDescription.Generic typeDefinition = ((ParameterDescription.InDefinedShape)this.resolution.getResolvedMethod().getParameters().get(index - 1)).getType();
                                            frame[index] = typeDefinition.represents(Boolean.TYPE) || typeDefinition.represents(Byte.TYPE) || typeDefinition.represents(Short.TYPE) || typeDefinition.represents(Character.TYPE) || typeDefinition.represents(Integer.TYPE) ? Opcodes.INTEGER : (typeDefinition.represents(Long.TYPE) ? Opcodes.LONG : (typeDefinition.represents(Float.TYPE) ? Opcodes.FLOAT : (typeDefinition.represents(Double.TYPE) ? Opcodes.DOUBLE : typeDefinition.asErasure().getInternalName())));
                                        }
                                        super.visitFrame((RedefinitionClassVisitor.this.readerFlags & 8) == 0 ? 0 : -1, frame.length, frame, EMPTY.length, EMPTY);
                                    }
                                    super.visitInsn(0);
                                }
                            } else {
                                this.mv = IGNORE_METHOD;
                                super.visitCode();
                            }
                        }

                        public void visitMaxs(int stackSize, int localVariableLength) {
                            super.visitMaxs(stackSize, Math.max(localVariableLength, this.resolution.getResolvedMethod().getStackSize()));
                        }
                    }

                    protected class AttributeObtainingRecordComponentVisitor
                    extends RecordComponentVisitor {
                        private final RecordComponentPool.Record record;

                        protected AttributeObtainingRecordComponentVisitor(RecordComponentVisitor recordComponentVisitor, RecordComponentPool.Record record) {
                            super(OpenedClassReader.ASM_API, recordComponentVisitor);
                            this.record = record;
                        }

                        public AnnotationVisitor visitTypeAnnotation(int typeReference, TypePath typePath, String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitTypeAnnotation(typeReference, typePath, descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitAnnotation(descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public void visitEnd() {
                            this.record.apply(this.getDelegate(), WithFullProcessing.this.annotationValueFilterFactory);
                            super.visitEnd();
                        }
                    }

                    protected class AttributeObtainingFieldVisitor
                    extends FieldVisitor {
                        private final FieldPool.Record record;

                        protected AttributeObtainingFieldVisitor(FieldVisitor fieldVisitor, FieldPool.Record record) {
                            super(OpenedClassReader.ASM_API, fieldVisitor);
                            this.record = record;
                        }

                        @MaybeNull
                        public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitTypeAnnotation(typeReference, typePath, descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        @MaybeNull
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            return WithFullProcessing.this.annotationRetention.isEnabled() ? super.visitAnnotation(descriptor, visible) : IGNORE_ANNOTATION;
                        }

                        public void visitEnd() {
                            this.record.apply(this.fv, WithFullProcessing.this.annotationValueFilterFactory);
                            super.visitEnd();
                        }
                    }
                }

                protected static interface InitializationHandler {
                    public void complete(ClassVisitor var1, Implementation.Context.ExtractableView var2);

                    public static abstract class Appending
                    extends MethodVisitor
                    implements InitializationHandler,
                    TypeInitializer.Drain {
                        protected final TypeDescription instrumentedType;
                        protected final MethodPool.Record record;
                        protected final AnnotationValueFilter.Factory annotationValueFilterFactory;
                        protected final FrameWriter frameWriter;
                        protected int stackSize;
                        protected int localVariableLength;

                        protected Appending(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                            super(OpenedClassReader.ASM_API, methodVisitor);
                            this.instrumentedType = instrumentedType;
                            this.record = record;
                            this.annotationValueFilterFactory = annotationValueFilterFactory;
                            this.frameWriter = !requireFrames ? FrameWriter.NoOp.INSTANCE : (expandFrames ? FrameWriter.Expanding.INSTANCE : new FrameWriter.Active());
                        }

                        protected static InitializationHandler of(boolean enabled, MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool methodPool, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                            return enabled ? Appending.withDrain(methodVisitor, instrumentedType, methodPool, annotationValueFilterFactory, requireFrames, expandFrames) : Appending.withoutDrain(methodVisitor, instrumentedType, methodPool, annotationValueFilterFactory, requireFrames, expandFrames);
                        }

                        private static WithDrain withDrain(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool methodPool, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                            MethodPool.Record record = methodPool.target(new MethodDescription.Latent.TypeInitializer(instrumentedType));
                            return record.getSort().isImplemented() ? new WithDrain.WithActiveRecord(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames) : new WithDrain.WithoutActiveRecord(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames);
                        }

                        private static WithoutDrain withoutDrain(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool methodPool, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                            MethodPool.Record record = methodPool.target(new MethodDescription.Latent.TypeInitializer(instrumentedType));
                            return record.getSort().isImplemented() ? new WithoutDrain.WithActiveRecord(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames) : new WithoutDrain.WithoutActiveRecord(methodVisitor, instrumentedType, record, annotationValueFilterFactory);
                        }

                        public void visitCode() {
                            this.record.applyAttributes(this.mv, this.annotationValueFilterFactory);
                            super.visitCode();
                            this.onStart();
                        }

                        protected abstract void onStart();

                        public void visitFrame(int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                            super.visitFrame(type, localVariableLength, localVariable, stackSize, stack);
                            this.frameWriter.onFrame(type, localVariableLength);
                        }

                        public void visitMaxs(int stackSize, int localVariableLength) {
                            this.stackSize = stackSize;
                            this.localVariableLength = localVariableLength;
                        }

                        public abstract void visitEnd();

                        public void apply(ClassVisitor classVisitor, TypeInitializer typeInitializer, Implementation.Context implementationContext) {
                            ByteCodeAppender.Size size = typeInitializer.apply(this.mv, implementationContext, new MethodDescription.Latent.TypeInitializer(this.instrumentedType));
                            this.stackSize = Math.max(this.stackSize, size.getOperandStackSize());
                            this.localVariableLength = Math.max(this.localVariableLength, size.getLocalVariableSize());
                            this.onComplete(implementationContext);
                        }

                        protected abstract void onComplete(Implementation.Context var1);

                        public void complete(ClassVisitor classVisitor, Implementation.Context.ExtractableView implementationContext) {
                            implementationContext.drain(this, classVisitor, this.annotationValueFilterFactory);
                            this.mv.visitMaxs(this.stackSize, this.localVariableLength);
                            this.mv.visitEnd();
                        }

                        protected static abstract class WithDrain
                        extends Appending {
                            protected final Label appended = new Label();
                            protected final Label original = new Label();

                            protected WithDrain(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                                super(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames);
                            }

                            protected void onStart() {
                                this.mv.visitJumpInsn(167, this.appended);
                                this.mv.visitLabel(this.original);
                                this.frameWriter.emitFrame(this.mv);
                            }

                            public void visitEnd() {
                                this.mv.visitLabel(this.appended);
                                this.frameWriter.emitFrame(this.mv);
                            }

                            protected void onComplete(Implementation.Context implementationContext) {
                                this.mv.visitJumpInsn(167, this.original);
                                this.onAfterComplete(implementationContext);
                            }

                            protected abstract void onAfterComplete(Implementation.Context var1);

                            protected static class WithActiveRecord
                            extends WithDrain {
                                private final Label label = new Label();

                                protected WithActiveRecord(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                                    super(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames);
                                }

                                public void visitInsn(int opcode) {
                                    if (opcode == 177) {
                                        this.mv.visitJumpInsn(167, this.label);
                                    } else {
                                        super.visitInsn(opcode);
                                    }
                                }

                                protected void onAfterComplete(Implementation.Context implementationContext) {
                                    this.mv.visitLabel(this.label);
                                    this.frameWriter.emitFrame(this.mv);
                                    ByteCodeAppender.Size size = this.record.applyCode(this.mv, implementationContext);
                                    this.stackSize = Math.max(this.stackSize, size.getOperandStackSize());
                                    this.localVariableLength = Math.max(this.localVariableLength, size.getLocalVariableSize());
                                }
                            }

                            protected static class WithoutActiveRecord
                            extends WithDrain {
                                protected WithoutActiveRecord(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                                    super(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames);
                                }

                                protected void onAfterComplete(Implementation.Context implementationContext) {
                                }
                            }
                        }

                        protected static abstract class WithoutDrain
                        extends Appending {
                            protected WithoutDrain(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                                super(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames);
                            }

                            protected void onStart() {
                            }

                            public void visitEnd() {
                            }

                            protected static class WithActiveRecord
                            extends WithoutDrain {
                                private final Label label = new Label();

                                protected WithActiveRecord(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory, boolean requireFrames, boolean expandFrames) {
                                    super(methodVisitor, instrumentedType, record, annotationValueFilterFactory, requireFrames, expandFrames);
                                }

                                public void visitInsn(int opcode) {
                                    if (opcode == 177) {
                                        this.mv.visitJumpInsn(167, this.label);
                                    } else {
                                        super.visitInsn(opcode);
                                    }
                                }

                                protected void onComplete(Implementation.Context implementationContext) {
                                    this.mv.visitLabel(this.label);
                                    this.frameWriter.emitFrame(this.mv);
                                    ByteCodeAppender.Size size = this.record.applyCode(this.mv, implementationContext);
                                    this.stackSize = Math.max(this.stackSize, size.getOperandStackSize());
                                    this.localVariableLength = Math.max(this.localVariableLength, size.getLocalVariableSize());
                                }
                            }

                            protected static class WithoutActiveRecord
                            extends WithoutDrain {
                                protected WithoutActiveRecord(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodPool.Record record, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                                    super(methodVisitor, instrumentedType, record, annotationValueFilterFactory, false, false);
                                }

                                protected void onComplete(Implementation.Context implementationContext) {
                                }
                            }
                        }

                        protected static interface FrameWriter {
                            public static final Object[] EMPTY = new Object[0];

                            public void onFrame(int var1, int var2);

                            public void emitFrame(MethodVisitor var1);

                            public static class Active
                            implements FrameWriter {
                                private int currentLocalVariableLength;

                                public void onFrame(int type, int localVariableLength) {
                                    switch (type) {
                                        case 3: 
                                        case 4: {
                                            break;
                                        }
                                        case 1: {
                                            this.currentLocalVariableLength += localVariableLength;
                                            break;
                                        }
                                        case 2: {
                                            this.currentLocalVariableLength -= localVariableLength;
                                            break;
                                        }
                                        case -1: 
                                        case 0: {
                                            this.currentLocalVariableLength = localVariableLength;
                                            break;
                                        }
                                        default: {
                                            throw new IllegalStateException("Unexpected frame type: " + type);
                                        }
                                    }
                                }

                                public void emitFrame(MethodVisitor methodVisitor) {
                                    if (this.currentLocalVariableLength == 0) {
                                        methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                                    } else if (this.currentLocalVariableLength > 3) {
                                        methodVisitor.visitFrame(0, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                                    } else {
                                        methodVisitor.visitFrame(2, this.currentLocalVariableLength, EMPTY, EMPTY.length, EMPTY);
                                    }
                                    methodVisitor.visitInsn(0);
                                    this.currentLocalVariableLength = 0;
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            public static enum Expanding implements FrameWriter
                            {
                                INSTANCE;


                                @Override
                                public void onFrame(int type, int localVariableLength) {
                                }

                                @Override
                                public void emitFrame(MethodVisitor methodVisitor) {
                                    methodVisitor.visitFrame(-1, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                                    methodVisitor.visitInsn(0);
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            public static enum NoOp implements FrameWriter
                            {
                                INSTANCE;


                                @Override
                                public void onFrame(int type, int localVariableLength) {
                                }

                                @Override
                                public void emitFrame(MethodVisitor methodVisitor) {
                                }
                            }
                        }
                    }

                    public static class Creating
                    extends TypeInitializer.Drain.Default
                    implements InitializationHandler {
                        protected Creating(TypeDescription instrumentedType, MethodPool methodPool, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                            super(instrumentedType, methodPool, annotationValueFilterFactory);
                        }

                        public void complete(ClassVisitor classVisitor, Implementation.Context.ExtractableView implementationContext) {
                            implementationContext.drain(this, classVisitor, this.annotationValueFilterFactory);
                        }
                    }
                }

                protected static class OpenedClassRemapper
                extends ClassRemapper {
                    protected OpenedClassRemapper(ClassVisitor classVisitor, Remapper remapper) {
                        super(OpenedClassReader.ASM_API, classVisitor, remapper);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ContextRegistry {
                @UnknownNull
                private Implementation.Context.ExtractableView implementationContext;

                protected ContextRegistry() {
                }

                public void setImplementationContext(Implementation.Context.ExtractableView implementationContext) {
                    this.implementationContext = implementationContext;
                }

                @SuppressFBWarnings(value={"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"}, justification="Lazy value definition is intended.")
                public List<DynamicType> getAuxiliaryTypes() {
                    return this.implementationContext.getAuxiliaryTypes();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class RegistryContextClassVisitor
            extends ContextClassVisitor {
                private final ContextRegistry contextRegistry;

                protected RegistryContextClassVisitor(ClassVisitor classVisitor, ContextRegistry contextRegistry) {
                    super(classVisitor);
                    this.contextRegistry = contextRegistry;
                }

                @Override
                public List<DynamicType> getAuxiliaryTypes() {
                    return CompoundList.of(ForInlining.this.auxiliaryTypes, this.contextRegistry.getAuxiliaryTypes());
                }

                @Override
                public LoadedTypeInitializer getLoadedTypeInitializer() {
                    return ForInlining.this.loadedTypeInitializer;
                }
            }
        }

        protected static class ValidatingClassVisitor
        extends ClassVisitor {
            private static final String NO_PARAMETERS = "()";
            private static final String RETURNS_VOID = "V";
            private static final String STRING_DESCRIPTOR = "Ljava/lang/String;";
            @AlwaysNull
            private static final FieldVisitor IGNORE_FIELD = null;
            @AlwaysNull
            private static final MethodVisitor IGNORE_METHOD = null;
            @UnknownNull
            private Constraint constraint;

            protected ValidatingClassVisitor(ClassVisitor classVisitor) {
                super(OpenedClassReader.ASM_API, classVisitor);
            }

            protected static ClassVisitor of(ClassVisitor classVisitor, TypeValidation typeValidation) {
                return typeValidation.isEnabled() ? new ValidatingClassVisitor(classVisitor) : classVisitor;
            }

            public void visit(int version, int modifiers, String name, @MaybeNull String signature, @MaybeNull String superName, @MaybeNull String[] interfaceInternalName) {
                boolean record;
                ClassFileVersion classFileVersion = ClassFileVersion.ofMinorMajor(version);
                ArrayList<Constraint> constraints = new ArrayList<Constraint>();
                constraints.add(new Constraint.ForClassFileVersion(classFileVersion));
                if (name.endsWith("/package-info")) {
                    constraints.add(Constraint.ForPackageType.INSTANCE);
                } else if ((modifiers & 0x2000) != 0) {
                    if (!classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)) {
                        throw new IllegalStateException("Cannot define an annotation type for class file version " + classFileVersion);
                    }
                    constraints.add(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V8) ? Constraint.ForAnnotation.JAVA_8 : Constraint.ForAnnotation.CLASSIC);
                } else if ((modifiers & 0x200) != 0) {
                    constraints.add(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V8) ? Constraint.ForInterface.JAVA_8 : Constraint.ForInterface.CLASSIC);
                } else if ((modifiers & 0x400) != 0) {
                    constraints.add(Constraint.ForClass.ABSTRACT);
                } else {
                    constraints.add(Constraint.ForClass.MANIFEST);
                }
                if ((modifiers & 0x10000) != 0) {
                    constraints.add(Constraint.ForRecord.INSTANCE);
                    record = true;
                } else {
                    record = false;
                }
                this.constraint = new Constraint.Compound(constraints);
                this.constraint.assertType(modifiers, interfaceInternalName != null, signature != null);
                if (record) {
                    this.constraint.assertRecord();
                }
                super.visit(version, modifiers, name, signature, superName, interfaceInternalName);
            }

            public void visitPermittedSubclass(String permittedSubclass) {
                this.constraint.assertPermittedSubclass();
                super.visitPermittedSubclass(permittedSubclass);
            }

            @MaybeNull
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                this.constraint.assertAnnotation();
                return super.visitAnnotation(descriptor, visible);
            }

            @MaybeNull
            public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                this.constraint.assertTypeAnnotation();
                return super.visitTypeAnnotation(typeReference, typePath, descriptor, visible);
            }

            public void visitNestHost(String nestHost) {
                this.constraint.assertNestMate();
                super.visitNestHost(nestHost);
            }

            public void visitNestMember(String nestMember) {
                this.constraint.assertNestMate();
                super.visitNestMember(nestMember);
            }

            @MaybeNull
            public FieldVisitor visitField(int modifiers, String name, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
                if (value != null) {
                    Class type;
                    switch (descriptor.charAt(0)) {
                        case 'B': 
                        case 'C': 
                        case 'I': 
                        case 'S': 
                        case 'Z': {
                            type = Integer.class;
                            break;
                        }
                        case 'J': {
                            type = Long.class;
                            break;
                        }
                        case 'F': {
                            type = Float.class;
                            break;
                        }
                        case 'D': {
                            type = Double.class;
                            break;
                        }
                        default: {
                            if (!descriptor.equals(STRING_DESCRIPTOR)) {
                                throw new IllegalStateException("Cannot define a default value for type of field " + name);
                            }
                            type = String.class;
                        }
                    }
                    if (!type.isInstance(value)) {
                        throw new IllegalStateException("Field " + name + " defines an incompatible default value " + value);
                    }
                    if (type == Integer.class) {
                        int maximum;
                        int minimum;
                        switch (descriptor.charAt(0)) {
                            case 'Z': {
                                minimum = 0;
                                maximum = 1;
                                break;
                            }
                            case 'B': {
                                minimum = -128;
                                maximum = 127;
                                break;
                            }
                            case 'C': {
                                minimum = 0;
                                maximum = 65535;
                                break;
                            }
                            case 'S': {
                                minimum = Short.MIN_VALUE;
                                maximum = Short.MAX_VALUE;
                                break;
                            }
                            default: {
                                minimum = Integer.MIN_VALUE;
                                maximum = Integer.MAX_VALUE;
                            }
                        }
                        if ((Integer)value < minimum || (Integer)value > maximum) {
                            throw new IllegalStateException("Field " + name + " defines an incompatible default value " + value);
                        }
                    }
                }
                this.constraint.assertField(name, (modifiers & 1) != 0, (modifiers & 8) != 0, (modifiers & 0x10) != 0, signature != null);
                FieldVisitor fieldVisitor = super.visitField(modifiers, name, descriptor, signature, value);
                return fieldVisitor == null ? IGNORE_FIELD : new ValidatingFieldVisitor(fieldVisitor);
            }

            @MaybeNull
            public MethodVisitor visitMethod(int modifiers, String name, String descriptor, @MaybeNull String signature, @MaybeNull String[] exceptionInternalName) {
                this.constraint.assertMethod(name, (modifiers & 0x400) != 0, (modifiers & 1) != 0, (modifiers & 2) != 0, (modifiers & 8) != 0, !name.equals("<init>") && !name.equals("<clinit>") && (modifiers & 0xA) == 0, name.equals("<init>"), !descriptor.startsWith(NO_PARAMETERS) || descriptor.endsWith(RETURNS_VOID), signature != null);
                MethodVisitor methodVisitor = super.visitMethod(modifiers, name, descriptor, signature, exceptionInternalName);
                return methodVisitor == null ? IGNORE_METHOD : new ValidatingMethodVisitor(methodVisitor, name);
            }

            protected class ValidatingMethodVisitor
            extends MethodVisitor {
                private final String name;

                protected ValidatingMethodVisitor(MethodVisitor methodVisitor, String name) {
                    super(OpenedClassReader.ASM_API, methodVisitor);
                    this.name = name;
                }

                @MaybeNull
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    ValidatingClassVisitor.this.constraint.assertAnnotation();
                    return super.visitAnnotation(descriptor, visible);
                }

                @MaybeNull
                public AnnotationVisitor visitAnnotationDefault() {
                    ValidatingClassVisitor.this.constraint.assertDefaultValue(this.name);
                    return super.visitAnnotationDefault();
                }

                @SuppressFBWarnings(value={"SF_SWITCH_NO_DEFAULT"}, justification="Fall through to default case is intentional.")
                public void visitLdcInsn(Object value) {
                    if (value instanceof Type) {
                        Type type = (Type)value;
                        switch (type.getSort()) {
                            case 9: 
                            case 10: {
                                ValidatingClassVisitor.this.constraint.assertTypeInConstantPool();
                                break;
                            }
                            case 11: {
                                ValidatingClassVisitor.this.constraint.assertMethodTypeInConstantPool();
                            }
                        }
                    } else if (value instanceof Handle) {
                        ValidatingClassVisitor.this.constraint.assertHandleInConstantPool();
                    } else if (value instanceof ConstantDynamic) {
                        ValidatingClassVisitor.this.constraint.assertDynamicValueInConstantPool();
                    }
                    super.visitLdcInsn(value);
                }

                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    if (isInterface && opcode == 183) {
                        ValidatingClassVisitor.this.constraint.assertDefaultMethodCall();
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }

                public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethod, Object ... bootstrapArgument) {
                    ValidatingClassVisitor.this.constraint.assertInvokeDynamic();
                    for (Object constant : bootstrapArgument) {
                        if (!(constant instanceof ConstantDynamic)) continue;
                        ValidatingClassVisitor.this.constraint.assertDynamicValueInConstantPool();
                    }
                    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethod, bootstrapArgument);
                }

                public void visitJumpInsn(int opcode, Label label) {
                    if (opcode == 168) {
                        ValidatingClassVisitor.this.constraint.assertSubRoutine();
                    }
                    super.visitJumpInsn(opcode, label);
                }
            }

            protected class ValidatingFieldVisitor
            extends FieldVisitor {
                protected ValidatingFieldVisitor(FieldVisitor fieldVisitor) {
                    super(OpenedClassReader.ASM_API, fieldVisitor);
                }

                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    ValidatingClassVisitor.this.constraint.assertAnnotation();
                    return super.visitAnnotation(descriptor, visible);
                }
            }

            protected static interface Constraint {
                public void assertType(int var1, boolean var2, boolean var3);

                public void assertField(String var1, boolean var2, boolean var3, boolean var4, boolean var5);

                public void assertMethod(String var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9);

                public void assertAnnotation();

                public void assertTypeAnnotation();

                public void assertDefaultValue(String var1);

                public void assertDefaultMethodCall();

                public void assertTypeInConstantPool();

                public void assertMethodTypeInConstantPool();

                public void assertHandleInConstantPool();

                public void assertInvokeDynamic();

                public void assertSubRoutine();

                public void assertDynamicValueInConstantPool();

                public void assertNestMate();

                public void assertRecord();

                public void assertPermittedSubclass();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Compound
                implements Constraint {
                    private final List<Constraint> constraints = new ArrayList<Constraint>();

                    public Compound(List<? extends Constraint> constraints) {
                        for (Constraint constraint : constraints) {
                            if (constraint instanceof Compound) {
                                this.constraints.addAll(((Compound)constraint).constraints);
                                continue;
                            }
                            this.constraints.add(constraint);
                        }
                    }

                    @Override
                    public void assertType(int modifier, boolean definesInterfaces, boolean isGeneric) {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertType(modifier, definesInterfaces, isGeneric);
                        }
                    }

                    @Override
                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertField(name, isPublic, isStatic, isFinal, isGeneric);
                        }
                    }

                    @Override
                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isDefaultValueIncompatible, boolean isGeneric) {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertMethod(name, isAbstract, isPublic, isPrivate, isStatic, isVirtual, isConstructor, isDefaultValueIncompatible, isGeneric);
                        }
                    }

                    @Override
                    public void assertDefaultValue(String name) {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertDefaultValue(name);
                        }
                    }

                    @Override
                    public void assertDefaultMethodCall() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertDefaultMethodCall();
                        }
                    }

                    @Override
                    public void assertAnnotation() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertAnnotation();
                        }
                    }

                    @Override
                    public void assertTypeAnnotation() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertTypeAnnotation();
                        }
                    }

                    @Override
                    public void assertTypeInConstantPool() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertTypeInConstantPool();
                        }
                    }

                    @Override
                    public void assertMethodTypeInConstantPool() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertMethodTypeInConstantPool();
                        }
                    }

                    @Override
                    public void assertHandleInConstantPool() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertHandleInConstantPool();
                        }
                    }

                    @Override
                    public void assertInvokeDynamic() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertInvokeDynamic();
                        }
                    }

                    @Override
                    public void assertSubRoutine() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertSubRoutine();
                        }
                    }

                    @Override
                    public void assertDynamicValueInConstantPool() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertDynamicValueInConstantPool();
                        }
                    }

                    @Override
                    public void assertNestMate() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertNestMate();
                        }
                    }

                    @Override
                    public void assertRecord() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertRecord();
                        }
                    }

                    @Override
                    public void assertPermittedSubclass() {
                        for (Constraint constraint : this.constraints) {
                            constraint.assertPermittedSubclass();
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
                        return ((Object)this.constraints).equals(((Compound)object).constraints);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + ((Object)this.constraints).hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForClassFileVersion
                implements Constraint {
                    private final ClassFileVersion classFileVersion;

                    protected ForClassFileVersion(ClassFileVersion classFileVersion) {
                        this.classFileVersion = classFileVersion;
                    }

                    public void assertType(int modifiers, boolean definesInterfaces, boolean isGeneric) {
                        if ((modifiers & 0x2000) != 0 && !this.classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)) {
                            throw new IllegalStateException("Cannot define annotation type for class file version " + this.classFileVersion);
                        }
                        if (isGeneric && !this.classFileVersion.isAtLeast(ClassFileVersion.JAVA_V4)) {
                            throw new IllegalStateException("Cannot define a generic type for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                        if (isGeneric && !this.classFileVersion.isAtLeast(ClassFileVersion.JAVA_V4)) {
                            throw new IllegalStateException("Cannot define generic field '" + name + "' for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isDefaultValueIncompatible, boolean isGeneric) {
                        if (isGeneric && !this.classFileVersion.isAtLeast(ClassFileVersion.JAVA_V4)) {
                            throw new IllegalStateException("Cannot define generic method '" + name + "' for class file version " + this.classFileVersion);
                        }
                        if (!isVirtual && isAbstract) {
                            throw new IllegalStateException("Cannot define static or non-virtual method '" + name + "' to be abstract");
                        }
                    }

                    public void assertAnnotation() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V5)) {
                            throw new IllegalStateException("Cannot write annotations for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertTypeAnnotation() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V5)) {
                            throw new IllegalStateException("Cannot write type annotations for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertDefaultValue(String name) {
                    }

                    public void assertDefaultMethodCall() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V8)) {
                            throw new IllegalStateException("Cannot invoke default method for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertTypeInConstantPool() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V5)) {
                            throw new IllegalStateException("Cannot write type to constant pool for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertMethodTypeInConstantPool() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V7)) {
                            throw new IllegalStateException("Cannot write method type to constant pool for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertHandleInConstantPool() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V7)) {
                            throw new IllegalStateException("Cannot write method handle to constant pool for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertInvokeDynamic() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V7)) {
                            throw new IllegalStateException("Cannot write invoke dynamic instruction for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertSubRoutine() {
                        if (this.classFileVersion.isGreaterThan(ClassFileVersion.JAVA_V5)) {
                            throw new IllegalStateException("Cannot write subroutine for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertDynamicValueInConstantPool() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V11)) {
                            throw new IllegalStateException("Cannot write dynamic constant for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertNestMate() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V11)) {
                            throw new IllegalStateException("Cannot define nest mate for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertRecord() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V14)) {
                            throw new IllegalStateException("Cannot define record for class file version " + this.classFileVersion);
                        }
                    }

                    public void assertPermittedSubclass() {
                        if (this.classFileVersion.isLessThan(ClassFileVersion.JAVA_V17)) {
                            throw new IllegalStateException("Cannot define permitted subclasses for class file version " + this.classFileVersion);
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
                        return this.classFileVersion.equals(((ForClassFileVersion)object).classFileVersion);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.classFileVersion.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForAnnotation implements Constraint
                {
                    CLASSIC(true),
                    JAVA_8(false);

                    private final boolean classic;

                    private ForAnnotation(boolean classic) {
                        this.classic = classic;
                    }

                    @Override
                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                        if (!(isStatic && isPublic && isFinal)) {
                            throw new IllegalStateException("Cannot only define public, static, final field '" + name + "' for interface type");
                        }
                    }

                    @Override
                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isDefaultValueIncompatible, boolean isGeneric) {
                        if (!name.equals("<clinit>")) {
                            if (isConstructor) {
                                throw new IllegalStateException("Cannot define constructor for interface type");
                            }
                            if (this.classic && !isVirtual) {
                                throw new IllegalStateException("Cannot define non-virtual method '" + name + "' for a pre-Java 8 annotation type");
                            }
                            if (!isStatic && isDefaultValueIncompatible) {
                                throw new IllegalStateException("Cannot define method '" + name + "' with the given signature as an annotation type method");
                            }
                        }
                    }

                    @Override
                    public void assertAnnotation() {
                    }

                    @Override
                    public void assertTypeAnnotation() {
                    }

                    @Override
                    public void assertDefaultValue(String name) {
                    }

                    @Override
                    public void assertDefaultMethodCall() {
                    }

                    @Override
                    public void assertType(int modifier, boolean definesInterfaces, boolean isGeneric) {
                        if ((modifier & 0x200) == 0) {
                            throw new IllegalStateException("Cannot define annotation type without interface modifier");
                        }
                    }

                    @Override
                    public void assertTypeInConstantPool() {
                    }

                    @Override
                    public void assertMethodTypeInConstantPool() {
                    }

                    @Override
                    public void assertHandleInConstantPool() {
                    }

                    @Override
                    public void assertInvokeDynamic() {
                    }

                    @Override
                    public void assertSubRoutine() {
                    }

                    @Override
                    public void assertDynamicValueInConstantPool() {
                    }

                    @Override
                    public void assertNestMate() {
                    }

                    @Override
                    public void assertRecord() {
                    }

                    @Override
                    public void assertPermittedSubclass() {
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForRecord implements Constraint
                {
                    INSTANCE;


                    @Override
                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                    }

                    @Override
                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isDefaultValueIncompatible, boolean isGeneric) {
                    }

                    @Override
                    public void assertAnnotation() {
                    }

                    @Override
                    public void assertTypeAnnotation() {
                    }

                    @Override
                    public void assertDefaultValue(String name) {
                    }

                    @Override
                    public void assertDefaultMethodCall() {
                    }

                    @Override
                    public void assertType(int modifier, boolean definesInterfaces, boolean isGeneric) {
                        if ((modifier & 0x400) != 0) {
                            throw new IllegalStateException("Cannot define a record class as abstract");
                        }
                    }

                    @Override
                    public void assertTypeInConstantPool() {
                    }

                    @Override
                    public void assertMethodTypeInConstantPool() {
                    }

                    @Override
                    public void assertHandleInConstantPool() {
                    }

                    @Override
                    public void assertInvokeDynamic() {
                    }

                    @Override
                    public void assertSubRoutine() {
                    }

                    @Override
                    public void assertDynamicValueInConstantPool() {
                    }

                    @Override
                    public void assertNestMate() {
                    }

                    @Override
                    public void assertRecord() {
                    }

                    @Override
                    public void assertPermittedSubclass() {
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForInterface implements Constraint
                {
                    CLASSIC(true),
                    JAVA_8(false);

                    private final boolean classic;

                    private ForInterface(boolean classic) {
                        this.classic = classic;
                    }

                    @Override
                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                        if (!(isStatic && isPublic && isFinal)) {
                            throw new IllegalStateException("Cannot only define public, static, final field '" + name + "' for interface type");
                        }
                    }

                    @Override
                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isDefaultValueIncompatible, boolean isGeneric) {
                        if (!name.equals("<clinit>")) {
                            if (isConstructor) {
                                throw new IllegalStateException("Cannot define constructor for interface type");
                            }
                            if (this.classic && !isPublic) {
                                throw new IllegalStateException("Cannot define non-public method '" + name + "' for interface type");
                            }
                            if (this.classic && !isVirtual) {
                                throw new IllegalStateException("Cannot define non-virtual method '" + name + "' for a pre-Java 8 interface type");
                            }
                            if (this.classic && !isAbstract) {
                                throw new IllegalStateException("Cannot define default method '" + name + "' for pre-Java 8 interface type");
                            }
                        }
                    }

                    @Override
                    public void assertAnnotation() {
                    }

                    @Override
                    public void assertTypeAnnotation() {
                    }

                    @Override
                    public void assertDefaultValue(String name) {
                        throw new IllegalStateException("Cannot define default value for '" + name + "' for non-annotation type");
                    }

                    @Override
                    public void assertDefaultMethodCall() {
                    }

                    @Override
                    public void assertType(int modifier, boolean definesInterfaces, boolean isGeneric) {
                    }

                    @Override
                    public void assertTypeInConstantPool() {
                    }

                    @Override
                    public void assertMethodTypeInConstantPool() {
                    }

                    @Override
                    public void assertHandleInConstantPool() {
                    }

                    @Override
                    public void assertInvokeDynamic() {
                    }

                    @Override
                    public void assertSubRoutine() {
                    }

                    @Override
                    public void assertDynamicValueInConstantPool() {
                    }

                    @Override
                    public void assertNestMate() {
                    }

                    @Override
                    public void assertRecord() {
                    }

                    @Override
                    public void assertPermittedSubclass() {
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForPackageType implements Constraint
                {
                    INSTANCE;


                    @Override
                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                        throw new IllegalStateException("Cannot define a field for a package description type");
                    }

                    @Override
                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isNoDefaultValue, boolean isGeneric) {
                        throw new IllegalStateException("Cannot define a method for a package description type");
                    }

                    @Override
                    public void assertAnnotation() {
                    }

                    @Override
                    public void assertTypeAnnotation() {
                    }

                    @Override
                    public void assertDefaultValue(String name) {
                    }

                    @Override
                    public void assertDefaultMethodCall() {
                    }

                    @Override
                    public void assertTypeInConstantPool() {
                    }

                    @Override
                    public void assertMethodTypeInConstantPool() {
                    }

                    @Override
                    public void assertHandleInConstantPool() {
                    }

                    @Override
                    public void assertInvokeDynamic() {
                    }

                    @Override
                    public void assertSubRoutine() {
                    }

                    @Override
                    public void assertType(int modifier, boolean definesInterfaces, boolean isGeneric) {
                        if (modifier != 5632) {
                            throw new IllegalStateException("A package description type must define 5632 as modifier");
                        }
                        if (definesInterfaces) {
                            throw new IllegalStateException("Cannot implement interface for package type");
                        }
                    }

                    @Override
                    public void assertDynamicValueInConstantPool() {
                    }

                    @Override
                    public void assertNestMate() {
                    }

                    @Override
                    public void assertRecord() {
                    }

                    @Override
                    public void assertPermittedSubclass() {
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForClass implements Constraint
                {
                    MANIFEST(true),
                    ABSTRACT(false);

                    private final boolean manifestType;

                    private ForClass(boolean manifestType) {
                        this.manifestType = manifestType;
                    }

                    @Override
                    public void assertType(int modifier, boolean definesInterfaces, boolean isGeneric) {
                    }

                    @Override
                    public void assertField(String name, boolean isPublic, boolean isStatic, boolean isFinal, boolean isGeneric) {
                    }

                    @Override
                    public void assertMethod(String name, boolean isAbstract, boolean isPublic, boolean isPrivate, boolean isStatic, boolean isVirtual, boolean isConstructor, boolean isDefaultValueIncompatible, boolean isGeneric) {
                        if (isAbstract && this.manifestType) {
                            throw new IllegalStateException("Cannot define abstract method '" + name + "' for non-abstract class");
                        }
                    }

                    @Override
                    public void assertAnnotation() {
                    }

                    @Override
                    public void assertTypeAnnotation() {
                    }

                    @Override
                    public void assertDefaultValue(String name) {
                        throw new IllegalStateException("Cannot define default value for '" + name + "' for non-annotation type");
                    }

                    @Override
                    public void assertDefaultMethodCall() {
                    }

                    @Override
                    public void assertTypeInConstantPool() {
                    }

                    @Override
                    public void assertMethodTypeInConstantPool() {
                    }

                    @Override
                    public void assertHandleInConstantPool() {
                    }

                    @Override
                    public void assertInvokeDynamic() {
                    }

                    @Override
                    public void assertSubRoutine() {
                    }

                    @Override
                    public void assertDynamicValueInConstantPool() {
                    }

                    @Override
                    public void assertNestMate() {
                    }

                    @Override
                    public void assertRecord() {
                    }

                    @Override
                    public void assertPermittedSubclass() {
                    }
                }
            }
        }

        protected static class SignatureKey {
            private final String internalName;
            private final String descriptor;

            public SignatureKey(String internalName, String descriptor) {
                this.internalName = internalName;
                this.descriptor = descriptor;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || this.getClass() != other.getClass()) {
                    return false;
                }
                SignatureKey that = (SignatureKey)other;
                return this.internalName.equals(that.internalName) && this.descriptor.equals(that.descriptor);
            }

            public int hashCode() {
                return 17 + this.internalName.hashCode() + 31 * this.descriptor.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class UnresolvedType {
            private final byte[] binaryRepresentation;
            private final List<? extends DynamicType> auxiliaryTypes;

            protected UnresolvedType(byte[] binaryRepresentation, List<? extends DynamicType> auxiliaryTypes) {
                this.binaryRepresentation = binaryRepresentation;
                this.auxiliaryTypes = auxiliaryTypes;
            }

            protected DynamicType.Unloaded<S> toDynamicType(TypeResolutionStrategy.Resolved typeResolutionStrategy) {
                return new DynamicType.Default.Unloaded(Default.this.instrumentedType, this.binaryRepresentation, Default.this.loadedTypeInitializer, CompoundList.of(Default.this.auxiliaryTypes, this.auxiliaryTypes), typeResolutionStrategy);
            }

            protected byte[] getBinaryRepresentation() {
                return this.binaryRepresentation;
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
                if (!Arrays.equals(this.binaryRepresentation, ((UnresolvedType)object).binaryRepresentation)) {
                    return false;
                }
                if (!((Object)this.auxiliaryTypes).equals(((UnresolvedType)object).auxiliaryTypes)) {
                    return false;
                }
                return Default.this.equals(((UnresolvedType)object).Default.this);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + Arrays.hashCode(this.binaryRepresentation)) * 31 + ((Object)this.auxiliaryTypes).hashCode()) * 31 + Default.this.hashCode();
            }
        }
    }

    public static interface RecordComponentPool {
        public Record target(RecordComponentDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Disabled implements RecordComponentPool
        {
            INSTANCE;


            @Override
            public Record target(RecordComponentDescription recordComponentDescription) {
                throw new IllegalStateException("Cannot look up record component from disabled pool");
            }
        }

        public static interface Record {
            public boolean isImplicit();

            public RecordComponentDescription getRecordComponent();

            public RecordComponentAttributeAppender getRecordComponentAppender();

            public void apply(ClassVisitor var1, AnnotationValueFilter.Factory var2);

            public void apply(RecordComponentVisitor var1, AnnotationValueFilter.Factory var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForExplicitRecordComponent
            implements Record {
                private final RecordComponentAttributeAppender attributeAppender;
                private final RecordComponentDescription recordComponentDescription;

                public ForExplicitRecordComponent(RecordComponentAttributeAppender attributeAppender, RecordComponentDescription recordComponentDescription) {
                    this.attributeAppender = attributeAppender;
                    this.recordComponentDescription = recordComponentDescription;
                }

                public boolean isImplicit() {
                    return false;
                }

                public RecordComponentDescription getRecordComponent() {
                    return this.recordComponentDescription;
                }

                public RecordComponentAttributeAppender getRecordComponentAppender() {
                    return this.attributeAppender;
                }

                public void apply(ClassVisitor classVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(this.recordComponentDescription.getActualName(), this.recordComponentDescription.getDescriptor(), this.recordComponentDescription.getGenericSignature());
                    if (recordComponentVisitor != null) {
                        this.attributeAppender.apply(recordComponentVisitor, this.recordComponentDescription, annotationValueFilterFactory.on(this.recordComponentDescription));
                        recordComponentVisitor.visitEnd();
                    }
                }

                public void apply(RecordComponentVisitor recordComponentVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    this.attributeAppender.apply(recordComponentVisitor, this.recordComponentDescription, annotationValueFilterFactory.on(this.recordComponentDescription));
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
                    if (!this.attributeAppender.equals(((ForExplicitRecordComponent)object).attributeAppender)) {
                        return false;
                    }
                    return this.recordComponentDescription.equals(((ForExplicitRecordComponent)object).recordComponentDescription);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.attributeAppender.hashCode()) * 31 + this.recordComponentDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForImplicitRecordComponent
            implements Record {
                private final RecordComponentDescription recordComponentDescription;

                public ForImplicitRecordComponent(RecordComponentDescription recordComponentDescription) {
                    this.recordComponentDescription = recordComponentDescription;
                }

                public boolean isImplicit() {
                    return true;
                }

                public RecordComponentDescription getRecordComponent() {
                    return this.recordComponentDescription;
                }

                public RecordComponentAttributeAppender getRecordComponentAppender() {
                    throw new IllegalStateException("An implicit field record does not expose a field appender: " + this);
                }

                public void apply(ClassVisitor classVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(this.recordComponentDescription.getActualName(), this.recordComponentDescription.getDescriptor(), this.recordComponentDescription.getGenericSignature());
                    if (recordComponentVisitor != null) {
                        RecordComponentAttributeAppender.ForInstrumentedRecordComponent.INSTANCE.apply(recordComponentVisitor, this.recordComponentDescription, annotationValueFilterFactory.on(this.recordComponentDescription));
                        recordComponentVisitor.visitEnd();
                    }
                }

                public void apply(RecordComponentVisitor recordComponentVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    throw new IllegalStateException("An implicit field record is not intended for partial application: " + this);
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
                    return this.recordComponentDescription.equals(((ForImplicitRecordComponent)object).recordComponentDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.recordComponentDescription.hashCode();
                }
            }
        }
    }

    public static interface MethodPool {
        public Record target(MethodDescription var1);

        public static interface Record {
            public Sort getSort();

            public MethodDescription getMethod();

            public Visibility getVisibility();

            public Record prepend(ByteCodeAppender var1);

            public void apply(ClassVisitor var1, Implementation.Context var2, AnnotationValueFilter.Factory var3);

            public void applyHead(MethodVisitor var1);

            public void applyBody(MethodVisitor var1, Implementation.Context var2, AnnotationValueFilter.Factory var3);

            public void applyAttributes(MethodVisitor var1, AnnotationValueFilter.Factory var2);

            public ByteCodeAppender.Size applyCode(MethodVisitor var1, Implementation.Context var2);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class AccessBridgeWrapper
            implements Record {
                private final Record delegate;
                private final TypeDescription instrumentedType;
                private final MethodDescription bridgeTarget;
                private final Set<MethodDescription.TypeToken> bridgeTypes;
                private final MethodAttributeAppender attributeAppender;

                protected AccessBridgeWrapper(Record delegate, TypeDescription instrumentedType, MethodDescription bridgeTarget, Set<MethodDescription.TypeToken> bridgeTypes, MethodAttributeAppender attributeAppender) {
                    this.delegate = delegate;
                    this.instrumentedType = instrumentedType;
                    this.bridgeTarget = bridgeTarget;
                    this.bridgeTypes = bridgeTypes;
                    this.attributeAppender = attributeAppender;
                }

                public static Record of(Record delegate, TypeDescription instrumentedType, MethodDescription bridgeTarget, Set<MethodDescription.TypeToken> bridgeTypes, MethodAttributeAppender attributeAppender) {
                    HashSet<MethodDescription.TypeToken> compatibleBridgeTypes = new HashSet<MethodDescription.TypeToken>();
                    for (MethodDescription.TypeToken bridgeType : bridgeTypes) {
                        if (!bridgeTarget.isBridgeCompatible(bridgeType)) continue;
                        compatibleBridgeTypes.add(bridgeType);
                    }
                    return compatibleBridgeTypes.isEmpty() || instrumentedType.isInterface() && !delegate.getSort().isImplemented() ? delegate : new AccessBridgeWrapper(delegate, instrumentedType, bridgeTarget, compatibleBridgeTypes, attributeAppender);
                }

                @Override
                public Sort getSort() {
                    return this.delegate.getSort();
                }

                @Override
                public MethodDescription getMethod() {
                    return this.bridgeTarget;
                }

                @Override
                public Visibility getVisibility() {
                    return this.delegate.getVisibility();
                }

                @Override
                public Record prepend(ByteCodeAppender byteCodeAppender) {
                    return new AccessBridgeWrapper(this.delegate.prepend(byteCodeAppender), this.instrumentedType, this.bridgeTarget, this.bridgeTypes, this.attributeAppender);
                }

                @Override
                public void apply(ClassVisitor classVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    this.delegate.apply(classVisitor, implementationContext, annotationValueFilterFactory);
                    for (MethodDescription.TypeToken bridgeType : this.bridgeTypes) {
                        AccessorBridge bridgeMethod = new AccessorBridge(this.bridgeTarget, bridgeType, this.instrumentedType);
                        BridgeTarget bridgeTarget = new BridgeTarget(this.bridgeTarget, this.instrumentedType);
                        MethodVisitor methodVisitor = classVisitor.visitMethod(bridgeMethod.getActualModifiers(true, this.getVisibility()), bridgeMethod.getInternalName(), bridgeMethod.getDescriptor(), MethodDescription.NON_GENERIC_SIGNATURE, bridgeMethod.getExceptionTypes().asErasures().toInternalNames());
                        if (methodVisitor == null) continue;
                        this.attributeAppender.apply(methodVisitor, bridgeMethod, annotationValueFilterFactory.on(this.instrumentedType));
                        methodVisitor.visitCode();
                        ByteCodeAppender.Size size = new ByteCodeAppender.Simple(MethodVariableAccess.allArgumentsOf(bridgeMethod).asBridgeOf(bridgeTarget).prependThisReference(), MethodInvocation.invoke(bridgeTarget).virtual(this.instrumentedType), bridgeTarget.getReturnType().asErasure().isAssignableTo(bridgeMethod.getReturnType().asErasure()) ? StackManipulation.Trivial.INSTANCE : TypeCasting.to(bridgeMethod.getReturnType().asErasure()), MethodReturn.of(bridgeMethod.getReturnType())).apply(methodVisitor, implementationContext, bridgeMethod);
                        methodVisitor.visitMaxs(size.getOperandStackSize(), size.getLocalVariableSize());
                        methodVisitor.visitEnd();
                    }
                }

                @Override
                public void applyHead(MethodVisitor methodVisitor) {
                    this.delegate.applyHead(methodVisitor);
                }

                @Override
                public void applyBody(MethodVisitor methodVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    this.delegate.applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
                }

                @Override
                public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    this.delegate.applyAttributes(methodVisitor, annotationValueFilterFactory);
                }

                @Override
                public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    return this.delegate.applyCode(methodVisitor, implementationContext);
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
                    if (!this.delegate.equals(((AccessBridgeWrapper)object).delegate)) {
                        return false;
                    }
                    if (!this.instrumentedType.equals(((AccessBridgeWrapper)object).instrumentedType)) {
                        return false;
                    }
                    if (!this.bridgeTarget.equals(((AccessBridgeWrapper)object).bridgeTarget)) {
                        return false;
                    }
                    if (!((Object)this.bridgeTypes).equals(((AccessBridgeWrapper)object).bridgeTypes)) {
                        return false;
                    }
                    return this.attributeAppender.equals(((AccessBridgeWrapper)object).attributeAppender);
                }

                public int hashCode() {
                    return ((((this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.instrumentedType.hashCode()) * 31 + this.bridgeTarget.hashCode()) * 31 + ((Object)this.bridgeTypes).hashCode()) * 31 + this.attributeAppender.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class BridgeTarget
                extends MethodDescription.InDefinedShape.AbstractBase {
                    private final MethodDescription bridgeTarget;
                    private final TypeDescription instrumentedType;

                    protected BridgeTarget(MethodDescription bridgeTarget, TypeDescription instrumentedType) {
                        this.bridgeTarget = bridgeTarget;
                        this.instrumentedType = instrumentedType;
                    }

                    @Override
                    @Nonnull
                    public TypeDescription getDeclaringType() {
                        return this.instrumentedType;
                    }

                    @Override
                    public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                        return new ParameterList.ForTokens(this, this.bridgeTarget.getParameters().asTokenList(ElementMatchers.is(this.instrumentedType)));
                    }

                    @Override
                    public TypeDescription.Generic getReturnType() {
                        return this.bridgeTarget.getReturnType();
                    }

                    @Override
                    public TypeList.Generic getExceptionTypes() {
                        return this.bridgeTarget.getExceptionTypes();
                    }

                    @Override
                    @MaybeNull
                    public AnnotationValue<?, ?> getDefaultValue() {
                        return this.bridgeTarget.getDefaultValue();
                    }

                    @Override
                    public TypeList.Generic getTypeVariables() {
                        return this.bridgeTarget.getTypeVariables();
                    }

                    @Override
                    public AnnotationList getDeclaredAnnotations() {
                        return this.bridgeTarget.getDeclaredAnnotations();
                    }

                    @Override
                    public int getModifiers() {
                        return this.bridgeTarget.getModifiers();
                    }

                    @Override
                    public String getInternalName() {
                        return this.bridgeTarget.getInternalName();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class AccessorBridge
                extends MethodDescription.InDefinedShape.AbstractBase {
                    private final MethodDescription bridgeTarget;
                    private final MethodDescription.TypeToken bridgeType;
                    private final TypeDescription instrumentedType;

                    protected AccessorBridge(MethodDescription bridgeTarget, MethodDescription.TypeToken bridgeType, TypeDescription instrumentedType) {
                        this.bridgeTarget = bridgeTarget;
                        this.bridgeType = bridgeType;
                        this.instrumentedType = instrumentedType;
                    }

                    @Override
                    @Nonnull
                    public TypeDescription getDeclaringType() {
                        return this.instrumentedType;
                    }

                    @Override
                    public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                        return new ParameterList.Explicit.ForTypes((MethodDescription.InDefinedShape)this, this.bridgeType.getParameterTypes());
                    }

                    @Override
                    public TypeDescription.Generic getReturnType() {
                        return this.bridgeType.getReturnType().asGenericType();
                    }

                    @Override
                    public TypeList.Generic getExceptionTypes() {
                        return this.bridgeTarget.getExceptionTypes().accept(TypeDescription.Generic.Visitor.TypeErasing.INSTANCE);
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
                    public int getModifiers() {
                        return (this.bridgeTarget.getModifiers() | 0x40 | 0x1000) & 0xFFFFFAFF;
                    }

                    @Override
                    public String getInternalName() {
                        return this.bridgeTarget.getInternalName();
                    }
                }
            }

            public static abstract class ForDefinedMethod
            implements Record {
                public void apply(ClassVisitor classVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    MethodVisitor methodVisitor = classVisitor.visitMethod(this.getMethod().getActualModifiers(this.getSort().isImplemented(), this.getVisibility()), this.getMethod().getInternalName(), this.getMethod().getDescriptor(), this.getMethod().getGenericSignature(), this.getMethod().getExceptionTypes().asErasures().toInternalNames());
                    if (methodVisitor != null) {
                        ParameterList<?> parameterList = this.getMethod().getParameters();
                        if (parameterList.hasExplicitMetaData()) {
                            for (ParameterDescription parameterDescription : parameterList) {
                                methodVisitor.visitParameter(parameterDescription.getName(), parameterDescription.getModifiers());
                            }
                        }
                        this.applyHead(methodVisitor);
                        this.applyBody(methodVisitor, implementationContext, annotationValueFilterFactory);
                        methodVisitor.visitEnd();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class OfVisibilityBridge
                extends ForDefinedMethod
                implements ByteCodeAppender {
                    private final MethodDescription visibilityBridge;
                    private final MethodDescription bridgeTarget;
                    private final TypeDescription bridgeType;
                    private final MethodAttributeAppender attributeAppender;

                    protected OfVisibilityBridge(MethodDescription visibilityBridge, MethodDescription bridgeTarget, TypeDescription bridgeType, MethodAttributeAppender attributeAppender) {
                        this.visibilityBridge = visibilityBridge;
                        this.bridgeTarget = bridgeTarget;
                        this.bridgeType = bridgeType;
                        this.attributeAppender = attributeAppender;
                    }

                    public static Record of(TypeDescription instrumentedType, MethodDescription bridgeTarget, MethodAttributeAppender attributeAppender) {
                        TypeDefinition bridgeType = null;
                        if (bridgeTarget.isDefaultMethod()) {
                            TypeDescription declaringType = bridgeTarget.getDeclaringType().asErasure();
                            for (TypeDescription interfaceType : (TypeList)instrumentedType.getInterfaces().asErasures().filter(ElementMatchers.isSubTypeOf(declaringType))) {
                                if (bridgeType != null && !declaringType.isAssignableTo(bridgeType.asErasure())) continue;
                                bridgeType = interfaceType;
                            }
                        }
                        if (bridgeType == null && (bridgeType = instrumentedType.getSuperClass()) == null) {
                            bridgeType = TypeDescription.ForLoadedType.of(Object.class);
                        }
                        return new OfVisibilityBridge(new VisibilityBridge(instrumentedType, bridgeTarget), bridgeTarget, bridgeType.asErasure(), attributeAppender);
                    }

                    public MethodDescription getMethod() {
                        return this.visibilityBridge;
                    }

                    public Sort getSort() {
                        return Sort.IMPLEMENTED;
                    }

                    public Visibility getVisibility() {
                        return this.bridgeTarget.getVisibility();
                    }

                    public Record prepend(ByteCodeAppender byteCodeAppender) {
                        return new WithBody(this.visibilityBridge, new ByteCodeAppender.Compound(this, byteCodeAppender), this.attributeAppender, this.bridgeTarget.getVisibility());
                    }

                    public void applyHead(MethodVisitor methodVisitor) {
                    }

                    public void applyBody(MethodVisitor methodVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.applyAttributes(methodVisitor, annotationValueFilterFactory);
                        methodVisitor.visitCode();
                        ByteCodeAppender.Size size = this.applyCode(methodVisitor, implementationContext);
                        methodVisitor.visitMaxs(size.getOperandStackSize(), size.getLocalVariableSize());
                    }

                    public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.attributeAppender.apply(methodVisitor, this.visibilityBridge, annotationValueFilterFactory.on(this.visibilityBridge));
                    }

                    public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                        return this.apply(methodVisitor, implementationContext, this.visibilityBridge);
                    }

                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        return new ByteCodeAppender.Simple(MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), MethodInvocation.invoke(this.bridgeTarget).special(this.bridgeType), MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext, instrumentedMethod);
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
                        if (!this.visibilityBridge.equals(((OfVisibilityBridge)object).visibilityBridge)) {
                            return false;
                        }
                        if (!this.bridgeTarget.equals(((OfVisibilityBridge)object).bridgeTarget)) {
                            return false;
                        }
                        if (!this.bridgeType.equals(((OfVisibilityBridge)object).bridgeType)) {
                            return false;
                        }
                        return this.attributeAppender.equals(((OfVisibilityBridge)object).attributeAppender);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.visibilityBridge.hashCode()) * 31 + this.bridgeTarget.hashCode()) * 31 + this.bridgeType.hashCode()) * 31 + this.attributeAppender.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class VisibilityBridge
                    extends MethodDescription.InDefinedShape.AbstractBase {
                        private final TypeDescription instrumentedType;
                        private final MethodDescription bridgeTarget;

                        protected VisibilityBridge(TypeDescription instrumentedType, MethodDescription bridgeTarget) {
                            this.instrumentedType = instrumentedType;
                            this.bridgeTarget = bridgeTarget;
                        }

                        @Override
                        @Nonnull
                        public TypeDescription getDeclaringType() {
                            return this.instrumentedType;
                        }

                        @Override
                        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                            return new ParameterList.Explicit.ForTypes((MethodDescription.InDefinedShape)this, this.bridgeTarget.getParameters().asTypeList().asRawTypes());
                        }

                        @Override
                        public TypeDescription.Generic getReturnType() {
                            return this.bridgeTarget.getReturnType().asRawType();
                        }

                        @Override
                        public TypeList.Generic getExceptionTypes() {
                            return this.bridgeTarget.getExceptionTypes().asRawTypes();
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
                            return this.bridgeTarget.getDeclaredAnnotations();
                        }

                        @Override
                        public int getModifiers() {
                            return (this.bridgeTarget.getModifiers() | 0x1000 | 0x40) & 0xFFFFFEFF;
                        }

                        @Override
                        public String getInternalName() {
                            return this.bridgeTarget.getName();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class WithAnnotationDefaultValue
                extends ForDefinedMethod {
                    private final MethodDescription methodDescription;
                    private final AnnotationValue<?, ?> annotationValue;
                    private final MethodAttributeAppender methodAttributeAppender;

                    public WithAnnotationDefaultValue(MethodDescription methodDescription, AnnotationValue<?, ?> annotationValue, MethodAttributeAppender methodAttributeAppender) {
                        this.methodDescription = methodDescription;
                        this.annotationValue = annotationValue;
                        this.methodAttributeAppender = methodAttributeAppender;
                    }

                    @Override
                    public MethodDescription getMethod() {
                        return this.methodDescription;
                    }

                    @Override
                    public Sort getSort() {
                        return Sort.DEFINED;
                    }

                    @Override
                    public Visibility getVisibility() {
                        return this.methodDescription.getVisibility();
                    }

                    @Override
                    public void applyHead(MethodVisitor methodVisitor) {
                        if (!this.methodDescription.isDefaultValue(this.annotationValue)) {
                            throw new IllegalStateException("Cannot set " + this.annotationValue + " as default for " + this.methodDescription);
                        }
                        AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
                        AnnotationAppender.Default.apply(annotationVisitor, this.methodDescription.getReturnType().asErasure(), AnnotationAppender.NO_NAME, this.annotationValue.resolve());
                        annotationVisitor.visitEnd();
                    }

                    @Override
                    public void applyBody(MethodVisitor methodVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.methodAttributeAppender.apply(methodVisitor, this.methodDescription, annotationValueFilterFactory.on(this.methodDescription));
                    }

                    @Override
                    public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        throw new IllegalStateException("Cannot apply attributes for default value on " + this.methodDescription);
                    }

                    @Override
                    public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                        throw new IllegalStateException("Cannot apply code for default value on " + this.methodDescription);
                    }

                    @Override
                    public Record prepend(ByteCodeAppender byteCodeAppender) {
                        throw new IllegalStateException("Cannot prepend code for default value on " + this.methodDescription);
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
                        if (!this.methodDescription.equals(((WithAnnotationDefaultValue)object).methodDescription)) {
                            return false;
                        }
                        if (!this.annotationValue.equals(((WithAnnotationDefaultValue)object).annotationValue)) {
                            return false;
                        }
                        return this.methodAttributeAppender.equals(((WithAnnotationDefaultValue)object).methodAttributeAppender);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.annotationValue.hashCode()) * 31 + this.methodAttributeAppender.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class WithoutBody
                extends ForDefinedMethod {
                    private final MethodDescription methodDescription;
                    private final MethodAttributeAppender methodAttributeAppender;
                    private final Visibility visibility;

                    public WithoutBody(MethodDescription methodDescription, MethodAttributeAppender methodAttributeAppender, Visibility visibility) {
                        this.methodDescription = methodDescription;
                        this.methodAttributeAppender = methodAttributeAppender;
                        this.visibility = visibility;
                    }

                    public MethodDescription getMethod() {
                        return this.methodDescription;
                    }

                    public Sort getSort() {
                        return Sort.DEFINED;
                    }

                    public Visibility getVisibility() {
                        return this.visibility;
                    }

                    public void applyHead(MethodVisitor methodVisitor) {
                    }

                    public void applyBody(MethodVisitor methodVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.applyAttributes(methodVisitor, annotationValueFilterFactory);
                    }

                    public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.methodAttributeAppender.apply(methodVisitor, this.methodDescription, annotationValueFilterFactory.on(this.methodDescription));
                    }

                    public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                        throw new IllegalStateException("Cannot apply code for abstract method on " + this.methodDescription);
                    }

                    public Record prepend(ByteCodeAppender byteCodeAppender) {
                        throw new IllegalStateException("Cannot prepend code for abstract method on " + this.methodDescription);
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
                        if (!this.visibility.equals(((WithoutBody)object).visibility)) {
                            return false;
                        }
                        if (!this.methodDescription.equals(((WithoutBody)object).methodDescription)) {
                            return false;
                        }
                        return this.methodAttributeAppender.equals(((WithoutBody)object).methodAttributeAppender);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.methodAttributeAppender.hashCode()) * 31 + this.visibility.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class WithBody
                extends ForDefinedMethod {
                    private final MethodDescription methodDescription;
                    private final ByteCodeAppender byteCodeAppender;
                    private final MethodAttributeAppender methodAttributeAppender;
                    private final Visibility visibility;

                    public WithBody(MethodDescription methodDescription, ByteCodeAppender byteCodeAppender) {
                        this(methodDescription, byteCodeAppender, MethodAttributeAppender.NoOp.INSTANCE, methodDescription.getVisibility());
                    }

                    public WithBody(MethodDescription methodDescription, ByteCodeAppender byteCodeAppender, MethodAttributeAppender methodAttributeAppender, Visibility visibility) {
                        this.methodDescription = methodDescription;
                        this.byteCodeAppender = byteCodeAppender;
                        this.methodAttributeAppender = methodAttributeAppender;
                        this.visibility = visibility;
                    }

                    public MethodDescription getMethod() {
                        return this.methodDescription;
                    }

                    public Sort getSort() {
                        return Sort.IMPLEMENTED;
                    }

                    public Visibility getVisibility() {
                        return this.visibility;
                    }

                    public void applyHead(MethodVisitor methodVisitor) {
                    }

                    public void applyBody(MethodVisitor methodVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.applyAttributes(methodVisitor, annotationValueFilterFactory);
                        methodVisitor.visitCode();
                        ByteCodeAppender.Size size = this.applyCode(methodVisitor, implementationContext);
                        methodVisitor.visitMaxs(size.getOperandStackSize(), size.getLocalVariableSize());
                    }

                    public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                        this.methodAttributeAppender.apply(methodVisitor, this.methodDescription, annotationValueFilterFactory.on(this.methodDescription));
                    }

                    public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                        return this.byteCodeAppender.apply(methodVisitor, implementationContext, this.methodDescription);
                    }

                    public Record prepend(ByteCodeAppender byteCodeAppender) {
                        return new WithBody(this.methodDescription, new ByteCodeAppender.Compound(byteCodeAppender, this.byteCodeAppender), this.methodAttributeAppender, this.visibility);
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
                        if (!this.visibility.equals(((WithBody)object).visibility)) {
                            return false;
                        }
                        if (!this.methodDescription.equals(((WithBody)object).methodDescription)) {
                            return false;
                        }
                        if (!this.byteCodeAppender.equals(((WithBody)object).byteCodeAppender)) {
                            return false;
                        }
                        return this.methodAttributeAppender.equals(((WithBody)object).methodAttributeAppender);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.byteCodeAppender.hashCode()) * 31 + this.methodAttributeAppender.hashCode()) * 31 + this.visibility.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForNonImplementedMethod
            implements Record {
                private final MethodDescription methodDescription;

                public ForNonImplementedMethod(MethodDescription methodDescription) {
                    this.methodDescription = methodDescription;
                }

                public void apply(ClassVisitor classVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                }

                public void applyBody(MethodVisitor methodVisitor, Implementation.Context implementationContext, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    throw new IllegalStateException("Cannot apply body for non-implemented method on " + this.methodDescription);
                }

                public void applyAttributes(MethodVisitor methodVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                }

                public ByteCodeAppender.Size applyCode(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    throw new IllegalStateException("Cannot apply code for non-implemented method on " + this.methodDescription);
                }

                public void applyHead(MethodVisitor methodVisitor) {
                    throw new IllegalStateException("Cannot apply head for non-implemented method on " + this.methodDescription);
                }

                public MethodDescription getMethod() {
                    return this.methodDescription;
                }

                public Visibility getVisibility() {
                    return this.methodDescription.getVisibility();
                }

                public Sort getSort() {
                    return Sort.SKIPPED;
                }

                public Record prepend(ByteCodeAppender byteCodeAppender) {
                    return new ForDefinedMethod.WithBody(this.methodDescription, new ByteCodeAppender.Compound(byteCodeAppender, new ByteCodeAppender.Simple(DefaultValue.of(this.methodDescription.getReturnType()), MethodReturn.of(this.methodDescription.getReturnType()))));
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
                    return this.methodDescription.equals(((ForNonImplementedMethod)object).methodDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Sort {
                SKIPPED(false, false),
                DEFINED(true, false),
                IMPLEMENTED(true, true);

                private final boolean define;
                private final boolean implement;

                private Sort(boolean define, boolean implement) {
                    this.define = define;
                    this.implement = implement;
                }

                public boolean isDefined() {
                    return this.define;
                }

                public boolean isImplemented() {
                    return this.implement;
                }
            }
        }
    }

    public static interface FieldPool {
        public Record target(FieldDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Disabled implements FieldPool
        {
            INSTANCE;


            @Override
            public Record target(FieldDescription fieldDescription) {
                throw new IllegalStateException("Cannot look up field from disabled pool");
            }
        }

        public static interface Record {
            public boolean isImplicit();

            public FieldDescription getField();

            public FieldAttributeAppender getFieldAppender();

            @MaybeNull
            public Object resolveDefault(@MaybeNull Object var1);

            public void apply(ClassVisitor var1, AnnotationValueFilter.Factory var2);

            public void apply(FieldVisitor var1, AnnotationValueFilter.Factory var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForExplicitField
            implements Record {
                private final FieldAttributeAppender attributeAppender;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Object defaultValue;
                private final FieldDescription fieldDescription;

                public ForExplicitField(FieldAttributeAppender attributeAppender, @MaybeNull Object defaultValue, FieldDescription fieldDescription) {
                    this.attributeAppender = attributeAppender;
                    this.defaultValue = defaultValue;
                    this.fieldDescription = fieldDescription;
                }

                public boolean isImplicit() {
                    return false;
                }

                public FieldDescription getField() {
                    return this.fieldDescription;
                }

                public FieldAttributeAppender getFieldAppender() {
                    return this.attributeAppender;
                }

                @MaybeNull
                public Object resolveDefault(@MaybeNull Object defaultValue) {
                    return this.defaultValue == null ? defaultValue : this.defaultValue;
                }

                public void apply(ClassVisitor classVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    FieldVisitor fieldVisitor = classVisitor.visitField(this.fieldDescription.getActualModifiers(), this.fieldDescription.getInternalName(), this.fieldDescription.getDescriptor(), this.fieldDescription.getGenericSignature(), this.resolveDefault(FieldDescription.NO_DEFAULT_VALUE));
                    if (fieldVisitor != null) {
                        this.attributeAppender.apply(fieldVisitor, this.fieldDescription, annotationValueFilterFactory.on(this.fieldDescription));
                        fieldVisitor.visitEnd();
                    }
                }

                public void apply(FieldVisitor fieldVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    this.attributeAppender.apply(fieldVisitor, this.fieldDescription, annotationValueFilterFactory.on(this.fieldDescription));
                }

                public boolean equals(@MaybeNull Object object) {
                    block11: {
                        block10: {
                            Object object2;
                            block9: {
                                Object object3;
                                if (this == object) {
                                    return true;
                                }
                                if (object == null) {
                                    return false;
                                }
                                if (this.getClass() != object.getClass()) {
                                    return false;
                                }
                                if (!this.attributeAppender.equals(((ForExplicitField)object).attributeAppender)) {
                                    return false;
                                }
                                Object object4 = ((ForExplicitField)object).defaultValue;
                                object2 = object3 = this.defaultValue;
                                if (object4 == null) break block9;
                                if (object2 == null) break block10;
                                if (!object3.equals(object4)) {
                                    return false;
                                }
                                break block11;
                            }
                            if (object2 == null) break block11;
                        }
                        return false;
                    }
                    return this.fieldDescription.equals(((ForExplicitField)object).fieldDescription);
                }

                public int hashCode() {
                    int n = (this.getClass().hashCode() * 31 + this.attributeAppender.hashCode()) * 31;
                    Object object = this.defaultValue;
                    if (object != null) {
                        n = n + object.hashCode();
                    }
                    return n * 31 + this.fieldDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForImplicitField
            implements Record {
                private final FieldDescription fieldDescription;

                public ForImplicitField(FieldDescription fieldDescription) {
                    this.fieldDescription = fieldDescription;
                }

                public boolean isImplicit() {
                    return true;
                }

                public FieldDescription getField() {
                    return this.fieldDescription;
                }

                public FieldAttributeAppender getFieldAppender() {
                    throw new IllegalStateException("An implicit field record does not expose a field appender: " + this);
                }

                public Object resolveDefault(@MaybeNull Object defaultValue) {
                    throw new IllegalStateException("An implicit field record does not expose a default value: " + this);
                }

                public void apply(ClassVisitor classVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    FieldVisitor fieldVisitor = classVisitor.visitField(this.fieldDescription.getActualModifiers(), this.fieldDescription.getInternalName(), this.fieldDescription.getDescriptor(), this.fieldDescription.getGenericSignature(), FieldDescription.NO_DEFAULT_VALUE);
                    if (fieldVisitor != null) {
                        FieldAttributeAppender.ForInstrumentedField.INSTANCE.apply(fieldVisitor, this.fieldDescription, annotationValueFilterFactory.on(this.fieldDescription));
                        fieldVisitor.visitEnd();
                    }
                }

                public void apply(FieldVisitor fieldVisitor, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                    throw new IllegalStateException("An implicit field record is not intended for partial application: " + this);
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
                    return this.fieldDescription.equals(((ForImplicitField)object).fieldDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                }
            }
        }
    }
}


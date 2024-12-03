/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.scaffold.ClassWriterStrategy;
import net.bytebuddy.dynamic.scaffold.FieldRegistry;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.dynamic.scaffold.RecordComponentRegistry;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.HashCodeMethod;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.ToStringMethod;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.FieldAttributeAppender;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.implementation.attribute.RecordComponentAttributeAppender;
import net.bytebuddy.implementation.attribute.TypeAttributeAppender;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.FileSystem;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.visitor.ContextClassVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface DynamicType
extends ClassFileLocator {
    public TypeDescription getTypeDescription();

    public byte[] getBytes();

    public Map<TypeDescription, byte[]> getAuxiliaryTypes();

    public Map<TypeDescription, byte[]> getAllTypes();

    public Map<TypeDescription, LoadedTypeInitializer> getLoadedTypeInitializers();

    public boolean hasAliveLoadedTypeInitializers();

    public Map<TypeDescription, File> saveIn(File var1) throws IOException;

    public File inject(File var1, File var2) throws IOException;

    public File inject(File var1) throws IOException;

    public File toJar(File var1) throws IOException;

    public File toJar(File var1, Manifest var2) throws IOException;

    @Override
    public void close();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements DynamicType {
        private static final String CLASS_FILE_EXTENSION = ".class";
        private static final String MANIFEST_VERSION = "1.0";
        private static final int BUFFER_SIZE = 1024;
        private static final int FROM_BEGINNING = 0;
        private static final int END_OF_FILE = -1;
        private static final String TEMP_SUFFIX = "tmp";
        protected final TypeDescription typeDescription;
        protected final byte[] binaryRepresentation;
        protected final LoadedTypeInitializer loadedTypeInitializer;
        protected final List<? extends DynamicType> auxiliaryTypes;

        @SuppressFBWarnings(value={"EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
        public Default(TypeDescription typeDescription, byte[] binaryRepresentation, LoadedTypeInitializer loadedTypeInitializer, List<? extends DynamicType> auxiliaryTypes) {
            this.typeDescription = typeDescription;
            this.binaryRepresentation = binaryRepresentation;
            this.loadedTypeInitializer = loadedTypeInitializer;
            this.auxiliaryTypes = auxiliaryTypes;
        }

        @Override
        public ClassFileLocator.Resolution locate(String name) throws IOException {
            if (this.typeDescription.getName().equals(name)) {
                return new ClassFileLocator.Resolution.Explicit(this.binaryRepresentation);
            }
            for (DynamicType dynamicType : this.auxiliaryTypes) {
                ClassFileLocator.Resolution resolution = dynamicType.locate(name);
                if (!resolution.isResolved()) continue;
                return resolution;
            }
            return new ClassFileLocator.Resolution.Illegal(name);
        }

        @Override
        public void close() {
        }

        @Override
        public TypeDescription getTypeDescription() {
            return this.typeDescription;
        }

        @Override
        public Map<TypeDescription, byte[]> getAllTypes() {
            LinkedHashMap<TypeDescription, byte[]> allTypes = new LinkedHashMap<TypeDescription, byte[]>();
            allTypes.put(this.typeDescription, this.binaryRepresentation);
            for (DynamicType dynamicType : this.auxiliaryTypes) {
                allTypes.putAll(dynamicType.getAllTypes());
            }
            return allTypes;
        }

        @Override
        public Map<TypeDescription, LoadedTypeInitializer> getLoadedTypeInitializers() {
            HashMap<TypeDescription, LoadedTypeInitializer> classLoadingCallbacks = new HashMap<TypeDescription, LoadedTypeInitializer>();
            for (DynamicType dynamicType : this.auxiliaryTypes) {
                classLoadingCallbacks.putAll(dynamicType.getLoadedTypeInitializers());
            }
            classLoadingCallbacks.put(this.typeDescription, this.loadedTypeInitializer);
            return classLoadingCallbacks;
        }

        @Override
        public boolean hasAliveLoadedTypeInitializers() {
            for (LoadedTypeInitializer loadedTypeInitializer : this.getLoadedTypeInitializers().values()) {
                if (!loadedTypeInitializer.isAlive()) continue;
                return true;
            }
            return false;
        }

        @Override
        @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="The array is not modified by class contract.")
        public byte[] getBytes() {
            return this.binaryRepresentation;
        }

        @Override
        public Map<TypeDescription, byte[]> getAuxiliaryTypes() {
            HashMap<TypeDescription, byte[]> auxiliaryTypes = new HashMap<TypeDescription, byte[]>();
            for (DynamicType dynamicType : this.auxiliaryTypes) {
                auxiliaryTypes.put(dynamicType.getTypeDescription(), dynamicType.getBytes());
                auxiliaryTypes.putAll(dynamicType.getAuxiliaryTypes());
            }
            return auxiliaryTypes;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<TypeDescription, File> saveIn(File folder) throws IOException {
            HashMap<TypeDescription, File> files = new HashMap<TypeDescription, File>();
            File target = new File(folder, this.typeDescription.getName().replace('.', File.separatorChar) + CLASS_FILE_EXTENSION);
            if (target.getParentFile() != null && !target.getParentFile().isDirectory() && !target.getParentFile().mkdirs()) {
                throw new IllegalArgumentException("Could not create directory: " + target.getParentFile());
            }
            FileOutputStream outputStream = new FileOutputStream(target);
            try {
                ((OutputStream)outputStream).write(this.binaryRepresentation);
                Object var6_5 = null;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                ((OutputStream)outputStream).close();
                throw throwable;
            }
            ((OutputStream)outputStream).close();
            files.put(this.typeDescription, target);
            for (DynamicType dynamicType : this.auxiliaryTypes) {
                files.putAll(dynamicType.saveIn(folder));
            }
            return files;
        }

        @Override
        public File inject(File sourceJar, File targetJar) throws IOException {
            return sourceJar.equals(targetJar) ? this.inject(sourceJar) : this.doInject(sourceJar, targetJar);
        }

        @Override
        public File inject(File jar) throws IOException {
            FileSystem.getInstance().move(this.doInject(jar, File.createTempFile(jar.getName(), TEMP_SUFFIX)), jar);
            return jar;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private File doInject(File sourceJar, File targetJar) throws IOException {
            JarInputStream inputStream = new JarInputStream(new FileInputStream(sourceJar));
            try {
                if (!targetJar.isFile() && !targetJar.createNewFile()) {
                    throw new IllegalArgumentException("Could not create file: " + targetJar);
                }
                Manifest manifest = inputStream.getManifest();
                JarOutputStream outputStream = manifest == null ? new JarOutputStream(new FileOutputStream(targetJar)) : new JarOutputStream((OutputStream)new FileOutputStream(targetJar), manifest);
                try {
                    JarEntry jarEntry;
                    Map<TypeDescription, byte[]> rawAuxiliaryTypes = this.getAuxiliaryTypes();
                    HashMap<String, byte[]> files = new HashMap<String, byte[]>();
                    for (Map.Entry<TypeDescription, byte[]> entry : rawAuxiliaryTypes.entrySet()) {
                        files.put(entry.getKey().getInternalName() + CLASS_FILE_EXTENSION, entry.getValue());
                    }
                    files.put(this.typeDescription.getInternalName() + CLASS_FILE_EXTENSION, this.binaryRepresentation);
                    while ((jarEntry = inputStream.getNextJarEntry()) != null) {
                        byte[] replacement = (byte[])files.remove(jarEntry.getName());
                        if (replacement == null) {
                            int index;
                            outputStream.putNextEntry(jarEntry);
                            byte[] buffer = new byte[1024];
                            while ((index = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, index);
                            }
                        } else {
                            outputStream.putNextEntry(new JarEntry(jarEntry.getName()));
                            outputStream.write(replacement);
                        }
                        inputStream.closeEntry();
                        outputStream.closeEntry();
                    }
                    for (Map.Entry entry : files.entrySet()) {
                        outputStream.putNextEntry(new JarEntry((String)entry.getKey()));
                        outputStream.write((byte[])entry.getValue());
                        outputStream.closeEntry();
                    }
                    Object var13_12 = null;
                }
                catch (Throwable throwable) {
                    Object var13_13 = null;
                    outputStream.close();
                    throw throwable;
                }
                outputStream.close();
                Object var15_15 = null;
            }
            catch (Throwable throwable) {
                Object var15_16 = null;
                inputStream.close();
                throw throwable;
            }
            inputStream.close();
            return targetJar;
        }

        @Override
        public File toJar(File file) throws IOException {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, MANIFEST_VERSION);
            return this.toJar(file, manifest);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public File toJar(File file, Manifest manifest) throws IOException {
            if (!file.isFile() && !file.createNewFile()) {
                throw new IllegalArgumentException("Could not create file: " + file);
            }
            JarOutputStream outputStream = new JarOutputStream((OutputStream)new FileOutputStream(file), manifest);
            try {
                for (Map.Entry<TypeDescription, byte[]> entry : this.getAuxiliaryTypes().entrySet()) {
                    outputStream.putNextEntry(new JarEntry(entry.getKey().getInternalName() + CLASS_FILE_EXTENSION));
                    outputStream.write(entry.getValue());
                    outputStream.closeEntry();
                }
                outputStream.putNextEntry(new JarEntry(this.typeDescription.getInternalName() + CLASS_FILE_EXTENSION));
                outputStream.write(this.binaryRepresentation);
                outputStream.closeEntry();
                Object var7_6 = null;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                outputStream.close();
                throw throwable;
            }
            outputStream.close();
            return file;
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
            if (!this.typeDescription.equals(((Default)object).typeDescription)) {
                return false;
            }
            if (!Arrays.equals(this.binaryRepresentation, ((Default)object).binaryRepresentation)) {
                return false;
            }
            if (!this.loadedTypeInitializer.equals(((Default)object).loadedTypeInitializer)) {
                return false;
            }
            return ((Object)this.auxiliaryTypes).equals(((Default)object).auxiliaryTypes);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + Arrays.hashCode(this.binaryRepresentation)) * 31 + this.loadedTypeInitializer.hashCode()) * 31 + ((Object)this.auxiliaryTypes).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Loaded<T>
        extends Default
        implements net.bytebuddy.dynamic.DynamicType$Loaded<T> {
            private final Map<TypeDescription, Class<?>> loadedTypes;

            protected Loaded(TypeDescription typeDescription, byte[] typeByte, LoadedTypeInitializer loadedTypeInitializer, List<? extends DynamicType> auxiliaryTypes, Map<TypeDescription, Class<?>> loadedTypes) {
                super(typeDescription, typeByte, loadedTypeInitializer, auxiliaryTypes);
                this.loadedTypes = loadedTypes;
            }

            @Override
            public Class<? extends T> getLoaded() {
                return this.loadedTypes.get(this.typeDescription);
            }

            @Override
            public Map<TypeDescription, Class<?>> getLoadedAuxiliaryTypes() {
                HashMap loadedAuxiliaryTypes = new HashMap(this.loadedTypes);
                loadedAuxiliaryTypes.remove(this.typeDescription);
                return loadedAuxiliaryTypes;
            }

            @Override
            public Map<TypeDescription, Class<?>> getAllLoaded() {
                return new HashMap(this.loadedTypes);
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
                return ((Object)this.loadedTypes).equals(((Loaded)object).loadedTypes);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + ((Object)this.loadedTypes).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Unloaded<T>
        extends Default
        implements net.bytebuddy.dynamic.DynamicType$Unloaded<T> {
            private final TypeResolutionStrategy.Resolved typeResolutionStrategy;

            public Unloaded(TypeDescription typeDescription, byte[] binaryRepresentation, LoadedTypeInitializer loadedTypeInitializer, List<? extends DynamicType> auxiliaryTypes, TypeResolutionStrategy.Resolved typeResolutionStrategy) {
                super(typeDescription, binaryRepresentation, loadedTypeInitializer, auxiliaryTypes);
                this.typeResolutionStrategy = typeResolutionStrategy;
            }

            @Override
            public net.bytebuddy.dynamic.DynamicType$Loaded<T> load(@MaybeNull ClassLoader classLoader) {
                if (classLoader instanceof InjectionClassLoader && !((InjectionClassLoader)classLoader).isSealed()) {
                    return this.load((InjectionClassLoader)classLoader, InjectionClassLoader.Strategy.INSTANCE);
                }
                return this.load(classLoader, ClassLoadingStrategy.Default.WRAPPER);
            }

            @Override
            public <S extends ClassLoader> net.bytebuddy.dynamic.DynamicType$Loaded<T> load(@MaybeNull S classLoader, ClassLoadingStrategy<? super S> classLoadingStrategy) {
                return new Loaded(this.typeDescription, this.binaryRepresentation, this.loadedTypeInitializer, this.auxiliaryTypes, this.typeResolutionStrategy.initialize(this, classLoader, classLoadingStrategy));
            }

            @Override
            public net.bytebuddy.dynamic.DynamicType$Unloaded<T> include(DynamicType ... dynamicType) {
                return this.include(Arrays.asList(dynamicType));
            }

            @Override
            public net.bytebuddy.dynamic.DynamicType$Unloaded<T> include(List<? extends DynamicType> dynamicType) {
                return new Unloaded<T>(this.typeDescription, this.binaryRepresentation, this.loadedTypeInitializer, CompoundList.of(this.auxiliaryTypes, dynamicType), this.typeResolutionStrategy);
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
                return this.typeResolutionStrategy.equals(((Unloaded)object).typeResolutionStrategy);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.typeResolutionStrategy.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Loaded<T>
    extends DynamicType {
        public Class<? extends T> getLoaded();

        public Map<TypeDescription, Class<?>> getLoadedAuxiliaryTypes();

        public Map<TypeDescription, Class<?>> getAllLoaded();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Unloaded<T>
    extends DynamicType {
        public Loaded<T> load(@MaybeNull ClassLoader var1);

        public <S extends ClassLoader> Loaded<T> load(@MaybeNull S var1, ClassLoadingStrategy<? super S> var2);

        public Unloaded<T> include(DynamicType ... var1);

        public Unloaded<T> include(List<? extends DynamicType> var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Builder<T> {
        public Builder<T> visit(AsmVisitorWrapper var1);

        public Builder<T> name(String var1);

        public Builder<T> suffix(String var1);

        public Builder<T> modifiers(ModifierContributor.ForType ... var1);

        public Builder<T> modifiers(Collection<? extends ModifierContributor.ForType> var1);

        public Builder<T> modifiers(int var1);

        public Builder<T> merge(ModifierContributor.ForType ... var1);

        public Builder<T> merge(Collection<? extends ModifierContributor.ForType> var1);

        public Builder<T> topLevelType();

        public InnerTypeDefinition.ForType<T> innerTypeOf(Class<?> var1);

        public InnerTypeDefinition.ForType<T> innerTypeOf(TypeDescription var1);

        public InnerTypeDefinition<T> innerTypeOf(Method var1);

        public InnerTypeDefinition<T> innerTypeOf(Constructor<?> var1);

        public InnerTypeDefinition<T> innerTypeOf(MethodDescription.InDefinedShape var1);

        public Builder<T> declaredTypes(Class<?> ... var1);

        public Builder<T> declaredTypes(TypeDescription ... var1);

        public Builder<T> declaredTypes(List<? extends Class<?>> var1);

        public Builder<T> declaredTypes(Collection<? extends TypeDescription> var1);

        public Builder<T> noNestMate();

        public Builder<T> nestHost(Class<?> var1);

        public Builder<T> nestHost(TypeDescription var1);

        public Builder<T> nestMembers(Class<?> ... var1);

        public Builder<T> nestMembers(TypeDescription ... var1);

        public Builder<T> nestMembers(List<? extends Class<?>> var1);

        public Builder<T> nestMembers(Collection<? extends TypeDescription> var1);

        public Builder<T> permittedSubclass(Class<?> ... var1);

        public Builder<T> permittedSubclass(TypeDescription ... var1);

        public Builder<T> permittedSubclass(List<? extends Class<?>> var1);

        public Builder<T> permittedSubclass(Collection<? extends TypeDescription> var1);

        public Builder<T> unsealed();

        public Builder<T> attribute(TypeAttributeAppender var1);

        public Builder<T> annotateType(Annotation ... var1);

        public Builder<T> annotateType(List<? extends Annotation> var1);

        public Builder<T> annotateType(AnnotationDescription ... var1);

        public Builder<T> annotateType(Collection<? extends AnnotationDescription> var1);

        public MethodDefinition.ImplementationDefinition.Optional<T> implement(Type ... var1);

        public MethodDefinition.ImplementationDefinition.Optional<T> implement(List<? extends Type> var1);

        public MethodDefinition.ImplementationDefinition.Optional<T> implement(TypeDefinition ... var1);

        public MethodDefinition.ImplementationDefinition.Optional<T> implement(Collection<? extends TypeDefinition> var1);

        public Builder<T> initializer(ByteCodeAppender var1);

        public Builder<T> initializer(LoadedTypeInitializer var1);

        public Builder<T> require(TypeDescription var1, byte[] var2);

        public Builder<T> require(TypeDescription var1, byte[] var2, LoadedTypeInitializer var3);

        public Builder<T> require(DynamicType ... var1);

        public Builder<T> require(Collection<DynamicType> var1);

        public TypeVariableDefinition<T> typeVariable(String var1);

        public TypeVariableDefinition<T> typeVariable(String var1, Type ... var2);

        public TypeVariableDefinition<T> typeVariable(String var1, List<? extends Type> var2);

        public TypeVariableDefinition<T> typeVariable(String var1, TypeDefinition ... var2);

        public TypeVariableDefinition<T> typeVariable(String var1, Collection<? extends TypeDefinition> var2);

        public Builder<T> transform(ElementMatcher<? super TypeDescription.Generic> var1, Transformer<TypeVariableToken> var2);

        public FieldDefinition.Optional.Valuable<T> defineField(String var1, Type var2, ModifierContributor.ForField ... var3);

        public FieldDefinition.Optional.Valuable<T> defineField(String var1, Type var2, Collection<? extends ModifierContributor.ForField> var3);

        public FieldDefinition.Optional.Valuable<T> defineField(String var1, Type var2, int var3);

        public FieldDefinition.Optional.Valuable<T> defineField(String var1, TypeDefinition var2, ModifierContributor.ForField ... var3);

        public FieldDefinition.Optional.Valuable<T> defineField(String var1, TypeDefinition var2, Collection<? extends ModifierContributor.ForField> var3);

        public FieldDefinition.Optional.Valuable<T> defineField(String var1, TypeDefinition var2, int var3);

        public FieldDefinition.Optional.Valuable<T> define(Field var1);

        public FieldDefinition.Optional.Valuable<T> define(FieldDescription var1);

        public FieldDefinition.Optional<T> serialVersionUid(long var1);

        public FieldDefinition.Valuable<T> field(ElementMatcher<? super FieldDescription> var1);

        public FieldDefinition.Valuable<T> field(LatentMatcher<? super FieldDescription> var1);

        public Builder<T> ignoreAlso(ElementMatcher<? super MethodDescription> var1);

        public Builder<T> ignoreAlso(LatentMatcher<? super MethodDescription> var1);

        public MethodDefinition.ParameterDefinition.Initial<T> defineMethod(String var1, Type var2, ModifierContributor.ForMethod ... var3);

        public MethodDefinition.ParameterDefinition.Initial<T> defineMethod(String var1, Type var2, Collection<? extends ModifierContributor.ForMethod> var3);

        public MethodDefinition.ParameterDefinition.Initial<T> defineMethod(String var1, Type var2, int var3);

        public MethodDefinition.ParameterDefinition.Initial<T> defineMethod(String var1, TypeDefinition var2, ModifierContributor.ForMethod ... var3);

        public MethodDefinition.ParameterDefinition.Initial<T> defineMethod(String var1, TypeDefinition var2, Collection<? extends ModifierContributor.ForMethod> var3);

        public MethodDefinition.ParameterDefinition.Initial<T> defineMethod(String var1, TypeDefinition var2, int var3);

        public MethodDefinition.ParameterDefinition.Initial<T> defineConstructor(ModifierContributor.ForMethod ... var1);

        public MethodDefinition.ParameterDefinition.Initial<T> defineConstructor(Collection<? extends ModifierContributor.ForMethod> var1);

        public MethodDefinition.ParameterDefinition.Initial<T> defineConstructor(int var1);

        public MethodDefinition.ImplementationDefinition<T> define(Method var1);

        public MethodDefinition.ImplementationDefinition<T> define(Constructor<?> var1);

        public MethodDefinition.ImplementationDefinition<T> define(MethodDescription var1);

        public FieldDefinition.Optional<T> defineProperty(String var1, Type var2);

        public FieldDefinition.Optional<T> defineProperty(String var1, Type var2, boolean var3);

        public FieldDefinition.Optional<T> defineProperty(String var1, TypeDefinition var2);

        public FieldDefinition.Optional<T> defineProperty(String var1, TypeDefinition var2, boolean var3);

        public MethodDefinition.ImplementationDefinition<T> method(ElementMatcher<? super MethodDescription> var1);

        public MethodDefinition.ImplementationDefinition<T> constructor(ElementMatcher<? super MethodDescription> var1);

        public MethodDefinition.ImplementationDefinition<T> invokable(ElementMatcher<? super MethodDescription> var1);

        public MethodDefinition.ImplementationDefinition<T> invokable(LatentMatcher<? super MethodDescription> var1);

        public Builder<T> withHashCodeEquals();

        public Builder<T> withToString();

        public RecordComponentDefinition.Optional<T> defineRecordComponent(String var1, Type var2);

        public RecordComponentDefinition.Optional<T> defineRecordComponent(String var1, TypeDefinition var2);

        public RecordComponentDefinition.Optional<T> define(RecordComponentDescription var1);

        public RecordComponentDefinition<T> recordComponent(ElementMatcher<? super RecordComponentDescription> var1);

        public RecordComponentDefinition<T> recordComponent(LatentMatcher<? super RecordComponentDescription> var1);

        public ContextClassVisitor wrap(ClassVisitor var1);

        public ContextClassVisitor wrap(ClassVisitor var1, int var2, int var3);

        public ContextClassVisitor wrap(ClassVisitor var1, TypePool var2);

        public ContextClassVisitor wrap(ClassVisitor var1, TypePool var2, int var3, int var4);

        public Unloaded<T> make();

        public Unloaded<T> make(TypeResolutionStrategy var1);

        public Unloaded<T> make(TypePool var1);

        public Unloaded<T> make(TypeResolutionStrategy var1, TypePool var2);

        public TypeDescription toTypeDescription();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class AbstractBase<S>
        implements Builder<S> {
            @Override
            public InnerTypeDefinition.ForType<S> innerTypeOf(Class<?> type) {
                return this.innerTypeOf(TypeDescription.ForLoadedType.of(type));
            }

            @Override
            public InnerTypeDefinition<S> innerTypeOf(Method method) {
                return this.innerTypeOf(new MethodDescription.ForLoadedMethod(method));
            }

            @Override
            public InnerTypeDefinition<S> innerTypeOf(Constructor<?> constructor) {
                return this.innerTypeOf(new MethodDescription.ForLoadedConstructor(constructor));
            }

            @Override
            public Builder<S> declaredTypes(Class<?> ... type) {
                return this.declaredTypes(Arrays.asList(type));
            }

            @Override
            public Builder<S> declaredTypes(TypeDescription ... type) {
                return this.declaredTypes((Collection<? extends TypeDescription>)Arrays.asList(type));
            }

            @Override
            public Builder<S> declaredTypes(List<? extends Class<?>> type) {
                return this.declaredTypes(new TypeList.ForLoadedTypes(type));
            }

            @Override
            public Builder<S> noNestMate() {
                return this.nestHost(TargetType.DESCRIPTION);
            }

            @Override
            public Builder<S> nestHost(Class<?> type) {
                return this.nestHost(TypeDescription.ForLoadedType.of(type));
            }

            @Override
            public Builder<S> nestMembers(Class<?> ... type) {
                return this.nestMembers(Arrays.asList(type));
            }

            @Override
            public Builder<S> nestMembers(TypeDescription ... type) {
                return this.nestMembers((Collection<? extends TypeDescription>)Arrays.asList(type));
            }

            @Override
            public Builder<S> nestMembers(List<? extends Class<?>> types) {
                return this.nestMembers(new TypeList.ForLoadedTypes(types));
            }

            @Override
            public Builder<S> permittedSubclass(Class<?> ... type) {
                return this.permittedSubclass(Arrays.asList(type));
            }

            @Override
            public Builder<S> permittedSubclass(TypeDescription ... type) {
                return this.permittedSubclass((Collection<? extends TypeDescription>)Arrays.asList(type));
            }

            @Override
            public Builder<S> permittedSubclass(List<? extends Class<?>> types) {
                return this.permittedSubclass(new TypeList.ForLoadedTypes(types));
            }

            @Override
            public Builder<S> annotateType(Annotation ... annotation) {
                return this.annotateType(Arrays.asList(annotation));
            }

            @Override
            public Builder<S> annotateType(List<? extends Annotation> annotations) {
                return this.annotateType(new AnnotationList.ForLoadedAnnotations(annotations));
            }

            @Override
            public Builder<S> annotateType(AnnotationDescription ... annotation) {
                return this.annotateType((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
            }

            @Override
            public Builder<S> modifiers(ModifierContributor.ForType ... modifierContributor) {
                return this.modifiers(Arrays.asList(modifierContributor));
            }

            @Override
            public Builder<S> modifiers(Collection<? extends ModifierContributor.ForType> modifierContributors) {
                return this.modifiers(ModifierContributor.Resolver.of(modifierContributors).resolve());
            }

            @Override
            public Builder<S> merge(ModifierContributor.ForType ... modifierContributor) {
                return this.merge(Arrays.asList(modifierContributor));
            }

            @Override
            public MethodDefinition.ImplementationDefinition.Optional<S> implement(Type ... interfaceType) {
                return this.implement(Arrays.asList(interfaceType));
            }

            @Override
            public MethodDefinition.ImplementationDefinition.Optional<S> implement(List<? extends Type> interfaceTypes) {
                return this.implement(new TypeList.Generic.ForLoadedTypes(interfaceTypes));
            }

            @Override
            public MethodDefinition.ImplementationDefinition.Optional<S> implement(TypeDefinition ... interfaceType) {
                return this.implement((Collection<? extends TypeDefinition>)Arrays.asList(interfaceType));
            }

            @Override
            public TypeVariableDefinition<S> typeVariable(String symbol) {
                return this.typeVariable(symbol, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class));
            }

            @Override
            public TypeVariableDefinition<S> typeVariable(String symbol, Type ... bound) {
                return this.typeVariable(symbol, Arrays.asList(bound));
            }

            @Override
            public TypeVariableDefinition<S> typeVariable(String symbol, List<? extends Type> bounds) {
                return this.typeVariable(symbol, new TypeList.Generic.ForLoadedTypes(bounds));
            }

            @Override
            public TypeVariableDefinition<S> typeVariable(String symbol, TypeDefinition ... bound) {
                return this.typeVariable(symbol, (Collection<? extends TypeDefinition>)Arrays.asList(bound));
            }

            @Override
            public RecordComponentDefinition.Optional<S> defineRecordComponent(String name, Type type) {
                return this.defineRecordComponent(name, TypeDefinition.Sort.describe(type));
            }

            @Override
            public RecordComponentDefinition.Optional<S> define(RecordComponentDescription recordComponentDescription) {
                return this.defineRecordComponent(recordComponentDescription.getActualName(), recordComponentDescription.getType());
            }

            @Override
            public RecordComponentDefinition<S> recordComponent(ElementMatcher<? super RecordComponentDescription> matcher) {
                return this.recordComponent(new LatentMatcher.Resolved<RecordComponentDescription>(matcher));
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> defineField(String name, Type type, ModifierContributor.ForField ... modifierContributor) {
                return this.defineField(name, type, Arrays.asList(modifierContributor));
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> defineField(String name, Type type, Collection<? extends ModifierContributor.ForField> modifierContributors) {
                return this.defineField(name, type, ModifierContributor.Resolver.of(modifierContributors).resolve());
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> defineField(String name, Type type, int modifiers) {
                return this.defineField(name, (TypeDefinition)TypeDefinition.Sort.describe(type), modifiers);
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> defineField(String name, TypeDefinition type, ModifierContributor.ForField ... modifierContributor) {
                return this.defineField(name, type, Arrays.asList(modifierContributor));
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> defineField(String name, TypeDefinition type, Collection<? extends ModifierContributor.ForField> modifierContributors) {
                return this.defineField(name, type, ModifierContributor.Resolver.of(modifierContributors).resolve());
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> define(Field field) {
                return this.define(new FieldDescription.ForLoadedField(field));
            }

            @Override
            public FieldDefinition.Optional.Valuable<S> define(FieldDescription field) {
                return this.defineField(field.getName(), (TypeDefinition)field.getType(), field.getModifiers());
            }

            @Override
            public FieldDefinition.Optional<S> serialVersionUid(long serialVersionUid) {
                return this.defineField("serialVersionUID", Long.TYPE, Visibility.PRIVATE, FieldManifestation.FINAL, Ownership.STATIC).value(serialVersionUid);
            }

            @Override
            public FieldDefinition.Valuable<S> field(ElementMatcher<? super FieldDescription> matcher) {
                return this.field(new LatentMatcher.Resolved<FieldDescription>(matcher));
            }

            @Override
            public Builder<S> ignoreAlso(ElementMatcher<? super MethodDescription> ignoredMethods) {
                return this.ignoreAlso(new LatentMatcher.Resolved<MethodDescription>(ignoredMethods));
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineMethod(String name, Type returnType, ModifierContributor.ForMethod ... modifierContributor) {
                return this.defineMethod(name, returnType, Arrays.asList(modifierContributor));
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineMethod(String name, Type returnType, Collection<? extends ModifierContributor.ForMethod> modifierContributors) {
                return this.defineMethod(name, returnType, ModifierContributor.Resolver.of(modifierContributors).resolve());
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineMethod(String name, Type returnType, int modifiers) {
                return this.defineMethod(name, (TypeDefinition)TypeDefinition.Sort.describe(returnType), modifiers);
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineMethod(String name, TypeDefinition returnType, ModifierContributor.ForMethod ... modifierContributor) {
                return this.defineMethod(name, returnType, Arrays.asList(modifierContributor));
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineMethod(String name, TypeDefinition returnType, Collection<? extends ModifierContributor.ForMethod> modifierContributors) {
                return this.defineMethod(name, returnType, ModifierContributor.Resolver.of(modifierContributors).resolve());
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineConstructor(ModifierContributor.ForMethod ... modifierContributor) {
                return this.defineConstructor(Arrays.asList(modifierContributor));
            }

            @Override
            public MethodDefinition.ParameterDefinition.Initial<S> defineConstructor(Collection<? extends ModifierContributor.ForMethod> modifierContributors) {
                return this.defineConstructor(ModifierContributor.Resolver.of(modifierContributors).resolve());
            }

            @Override
            public MethodDefinition.ImplementationDefinition<S> define(Method method) {
                return this.define(new MethodDescription.ForLoadedMethod(method));
            }

            @Override
            public MethodDefinition.ImplementationDefinition<S> define(Constructor<?> constructor) {
                return this.define(new MethodDescription.ForLoadedConstructor(constructor));
            }

            @Override
            public MethodDefinition.ImplementationDefinition<S> define(MethodDescription methodDescription) {
                MethodDefinition.ExceptionDefinition exceptionDefinition;
                MethodDefinition.ParameterDefinition.Initial initialParameterDefinition = methodDescription.isConstructor() ? this.defineConstructor(methodDescription.getModifiers()) : this.defineMethod(methodDescription.getInternalName(), (TypeDefinition)methodDescription.getReturnType(), methodDescription.getModifiers());
                ParameterList<?> parameterList = methodDescription.getParameters();
                if (parameterList.hasExplicitMetaData()) {
                    MethodDefinition.ParameterDefinition<Object> parameterDefinition = initialParameterDefinition;
                    for (ParameterDescription parameter : parameterList) {
                        parameterDefinition = parameterDefinition.withParameter((TypeDefinition)parameter.getType(), parameter.getName(), parameter.getModifiers());
                    }
                    exceptionDefinition = parameterDefinition;
                } else {
                    exceptionDefinition = initialParameterDefinition.withParameters(parameterList.asTypeList());
                }
                MethodDefinition.TypeVariableDefinition<Object> typeVariableDefinition = exceptionDefinition.throwing(methodDescription.getExceptionTypes());
                for (TypeDescription.Generic typeVariable : methodDescription.getTypeVariables()) {
                    typeVariableDefinition = typeVariableDefinition.typeVariable(typeVariable.getSymbol(), typeVariable.getUpperBounds());
                }
                return typeVariableDefinition;
            }

            @Override
            public FieldDefinition.Optional<S> defineProperty(String name, Type type) {
                return this.defineProperty(name, TypeDefinition.Sort.describe(type));
            }

            @Override
            public FieldDefinition.Optional<S> defineProperty(String name, Type type, boolean readOnly) {
                return this.defineProperty(name, TypeDefinition.Sort.describe(type), readOnly);
            }

            @Override
            public FieldDefinition.Optional<S> defineProperty(String name, TypeDefinition type) {
                return this.defineProperty(name, type, false);
            }

            @Override
            public FieldDefinition.Optional<S> defineProperty(String name, TypeDefinition type, boolean readOnly) {
                FieldManifestation fieldManifestation;
                if (name.length() == 0) {
                    throw new IllegalArgumentException("A bean property cannot have an empty name");
                }
                if (type.represents(Void.TYPE)) {
                    throw new IllegalArgumentException("A bean property cannot have a void type");
                }
                Builder<Object> builder = this;
                if (!readOnly) {
                    builder = builder.defineMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), Void.TYPE, Visibility.PUBLIC).withParameters(type).intercept(FieldAccessor.ofField(name));
                    fieldManifestation = FieldManifestation.PLAIN;
                } else {
                    fieldManifestation = FieldManifestation.FINAL;
                }
                return builder.defineMethod((type.represents(Boolean.TYPE) ? "is" : "get") + Character.toUpperCase(name.charAt(0)) + name.substring(1), type, Visibility.PUBLIC).intercept(FieldAccessor.ofField(name)).defineField(name, type, Visibility.PRIVATE, fieldManifestation);
            }

            @Override
            public MethodDefinition.ImplementationDefinition<S> method(ElementMatcher<? super MethodDescription> matcher) {
                return this.invokable(ElementMatchers.isMethod().and(matcher));
            }

            @Override
            public MethodDefinition.ImplementationDefinition<S> constructor(ElementMatcher<? super MethodDescription> matcher) {
                return this.invokable(ElementMatchers.isConstructor().and(matcher));
            }

            @Override
            public MethodDefinition.ImplementationDefinition<S> invokable(ElementMatcher<? super MethodDescription> matcher) {
                return this.invokable(new LatentMatcher.Resolved<MethodDescription>(matcher));
            }

            @Override
            public Builder<S> withHashCodeEquals() {
                return this.method(ElementMatchers.isHashCode()).intercept(HashCodeMethod.usingDefaultOffset().withIgnoredFields(ElementMatchers.isSynthetic())).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withIgnoredFields(ElementMatchers.isSynthetic()));
            }

            @Override
            public Builder<S> withToString() {
                return this.method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBySimpleClassName());
            }

            @Override
            public Builder<S> require(TypeDescription type, byte[] binaryRepresentation) {
                return this.require(type, binaryRepresentation, LoadedTypeInitializer.NoOp.INSTANCE);
            }

            @Override
            public Builder<S> require(TypeDescription type, byte[] binaryRepresentation, LoadedTypeInitializer typeInitializer) {
                return this.require(new Default(type, binaryRepresentation, typeInitializer, Collections.emptyList()));
            }

            @Override
            public Builder<S> require(DynamicType ... auxiliaryType) {
                return this.require(Arrays.asList(auxiliaryType));
            }

            @Override
            public ContextClassVisitor wrap(ClassVisitor classVisitor) {
                return this.wrap(classVisitor, 0, 0);
            }

            @Override
            public ContextClassVisitor wrap(ClassVisitor classVisitor, TypePool typePool) {
                return this.wrap(classVisitor, typePool, 0, 0);
            }

            @Override
            public Unloaded<S> make(TypePool typePool) {
                return this.make(TypeResolutionStrategy.Passive.INSTANCE, typePool);
            }

            @Override
            public Unloaded<S> make() {
                return this.make(TypeResolutionStrategy.Passive.INSTANCE);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class Adapter<U>
            extends UsingTypeWriter<U> {
                protected final InstrumentedType.WithFlexibleName instrumentedType;
                protected final FieldRegistry fieldRegistry;
                protected final MethodRegistry methodRegistry;
                protected final RecordComponentRegistry recordComponentRegistry;
                protected final TypeAttributeAppender typeAttributeAppender;
                protected final AsmVisitorWrapper asmVisitorWrapper;
                protected final ClassFileVersion classFileVersion;
                protected final AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;
                protected final AnnotationValueFilter.Factory annotationValueFilterFactory;
                protected final AnnotationRetention annotationRetention;
                protected final Implementation.Context.Factory implementationContextFactory;
                protected final MethodGraph.Compiler methodGraphCompiler;
                protected final TypeValidation typeValidation;
                protected final VisibilityBridgeStrategy visibilityBridgeStrategy;
                protected final ClassWriterStrategy classWriterStrategy;
                protected final LatentMatcher<? super MethodDescription> ignoredMethods;
                protected final List<? extends DynamicType> auxiliaryTypes;

                protected Adapter(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes) {
                    this.instrumentedType = instrumentedType;
                    this.fieldRegistry = fieldRegistry;
                    this.methodRegistry = methodRegistry;
                    this.recordComponentRegistry = recordComponentRegistry;
                    this.typeAttributeAppender = typeAttributeAppender;
                    this.asmVisitorWrapper = asmVisitorWrapper;
                    this.classFileVersion = classFileVersion;
                    this.auxiliaryTypeNamingStrategy = auxiliaryTypeNamingStrategy;
                    this.annotationValueFilterFactory = annotationValueFilterFactory;
                    this.annotationRetention = annotationRetention;
                    this.implementationContextFactory = implementationContextFactory;
                    this.methodGraphCompiler = methodGraphCompiler;
                    this.typeValidation = typeValidation;
                    this.visibilityBridgeStrategy = visibilityBridgeStrategy;
                    this.classWriterStrategy = classWriterStrategy;
                    this.ignoredMethods = ignoredMethods;
                    this.auxiliaryTypes = auxiliaryTypes;
                }

                @Override
                public FieldDefinition.Optional.Valuable<U> defineField(String name, TypeDefinition type, int modifiers) {
                    return new FieldDefinitionAdapter(new FieldDescription.Token(name, modifiers, type.asGenericType()));
                }

                @Override
                public FieldDefinition.Valuable<U> field(LatentMatcher<? super FieldDescription> matcher) {
                    return new FieldMatchAdapter(matcher);
                }

                @Override
                public MethodDefinition.ParameterDefinition.Initial<U> defineMethod(String name, TypeDefinition returnType, int modifiers) {
                    return new MethodDefinitionAdapter(new MethodDescription.Token(name, modifiers, returnType.asGenericType()));
                }

                @Override
                public MethodDefinition.ParameterDefinition.Initial<U> defineConstructor(int modifiers) {
                    return new MethodDefinitionAdapter(new MethodDescription.Token(modifiers));
                }

                @Override
                public MethodDefinition.ImplementationDefinition<U> invokable(LatentMatcher<? super MethodDescription> matcher) {
                    return new MethodMatchAdapter(matcher);
                }

                @Override
                public MethodDefinition.ImplementationDefinition.Optional<U> implement(Collection<? extends TypeDefinition> interfaceTypes) {
                    return new OptionalMethodMatchAdapter(new TypeList.Generic.Explicit(new ArrayList<TypeDefinition>(interfaceTypes)));
                }

                @Override
                public Builder<U> ignoreAlso(LatentMatcher<? super MethodDescription> ignoredMethods) {
                    return this.materialize(this.instrumentedType, this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, new LatentMatcher.Disjunction(this.ignoredMethods, ignoredMethods), this.auxiliaryTypes);
                }

                @Override
                public RecordComponentDefinition.Optional<U> defineRecordComponent(String name, TypeDefinition type) {
                    return new RecordComponentDefinitionAdapter(new RecordComponentDescription.Token(name, type.asGenericType()));
                }

                @Override
                public RecordComponentDefinition<U> recordComponent(LatentMatcher<? super RecordComponentDescription> matcher) {
                    return new RecordComponentMatchAdapter(matcher);
                }

                @Override
                public Builder<U> initializer(ByteCodeAppender byteCodeAppender) {
                    return this.materialize(this.instrumentedType.withInitializer(byteCodeAppender), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> initializer(LoadedTypeInitializer loadedTypeInitializer) {
                    return this.materialize(this.instrumentedType.withInitializer(loadedTypeInitializer), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> name(String name) {
                    return this.materialize(this.instrumentedType.withName(name), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> suffix(String suffix) {
                    return this.name(this.instrumentedType.getName() + "$" + suffix);
                }

                @Override
                public Builder<U> modifiers(int modifiers) {
                    return this.materialize(this.instrumentedType.withModifiers(modifiers), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> merge(Collection<? extends ModifierContributor.ForType> modifierContributors) {
                    return this.materialize(this.instrumentedType.withModifiers(ModifierContributor.Resolver.of(modifierContributors).resolve(this.instrumentedType.getModifiers())), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> topLevelType() {
                    return this.materialize(this.instrumentedType.withDeclaringType(TypeDescription.UNDEFINED).withEnclosingType(TypeDescription.UNDEFINED).withLocalClass(false), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public InnerTypeDefinition.ForType<U> innerTypeOf(TypeDescription type) {
                    return new InnerTypeDefinitionForTypeAdapter(type);
                }

                @Override
                public InnerTypeDefinition<U> innerTypeOf(MethodDescription.InDefinedShape methodDescription) {
                    return (InnerTypeDefinition)((Object)(methodDescription.isTypeInitializer() ? new InnerTypeDefinitionForTypeAdapter(methodDescription.getDeclaringType()) : new InnerTypeDefinitionForMethodAdapter(methodDescription)));
                }

                @Override
                public Builder<U> declaredTypes(Collection<? extends TypeDescription> types) {
                    return this.materialize(this.instrumentedType.withDeclaredTypes(new TypeList.Explicit(new ArrayList<TypeDescription>(types))), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> nestHost(TypeDescription type) {
                    return this.materialize(this.instrumentedType.withNestHost(type), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> nestMembers(Collection<? extends TypeDescription> types) {
                    return this.materialize(this.instrumentedType.withNestMembers(new TypeList.Explicit(new ArrayList<TypeDescription>(types))), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> permittedSubclass(Collection<? extends TypeDescription> types) {
                    return this.materialize(this.instrumentedType.withPermittedSubclasses(new TypeList.Explicit(new ArrayList<TypeDescription>(types))), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> unsealed() {
                    return this.materialize(this.instrumentedType.withPermittedSubclasses(TypeList.UNDEFINED), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public TypeVariableDefinition<U> typeVariable(String symbol, Collection<? extends TypeDefinition> bounds) {
                    return new TypeVariableDefinitionAdapter(new TypeVariableToken(symbol, new TypeList.Generic.Explicit(new ArrayList<TypeDefinition>(bounds))));
                }

                @Override
                public Builder<U> transform(ElementMatcher<? super TypeDescription.Generic> matcher, Transformer<TypeVariableToken> transformer) {
                    return this.materialize(this.instrumentedType.withTypeVariables(matcher, transformer), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> attribute(TypeAttributeAppender typeAttributeAppender) {
                    return this.materialize(this.instrumentedType, this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, new TypeAttributeAppender.Compound(this.typeAttributeAppender, typeAttributeAppender), this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> annotateType(Collection<? extends AnnotationDescription> annotations) {
                    return this.materialize(this.instrumentedType.withAnnotations(new ArrayList<AnnotationDescription>(annotations)), this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> visit(AsmVisitorWrapper asmVisitorWrapper) {
                    return this.materialize(this.instrumentedType, this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, new AsmVisitorWrapper.Compound(this.asmVisitorWrapper, asmVisitorWrapper), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, this.auxiliaryTypes);
                }

                @Override
                public Builder<U> require(Collection<DynamicType> auxiliaryTypes) {
                    return this.materialize(this.instrumentedType, this.fieldRegistry, this.methodRegistry, this.recordComponentRegistry, this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, CompoundList.of(this.auxiliaryTypes, new ArrayList<DynamicType>(auxiliaryTypes)));
                }

                @Override
                public TypeDescription toTypeDescription() {
                    return this.instrumentedType;
                }

                protected abstract Builder<U> materialize(InstrumentedType.WithFlexibleName var1, FieldRegistry var2, MethodRegistry var3, RecordComponentRegistry var4, TypeAttributeAppender var5, AsmVisitorWrapper var6, ClassFileVersion var7, AuxiliaryType.NamingStrategy var8, AnnotationValueFilter.Factory var9, AnnotationRetention var10, Implementation.Context.Factory var11, MethodGraph.Compiler var12, TypeValidation var13, VisibilityBridgeStrategy var14, ClassWriterStrategy var15, LatentMatcher<? super MethodDescription> var16, List<? extends DynamicType> var17);

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
                    if (!this.annotationRetention.equals((Object)((Adapter)object).annotationRetention)) {
                        return false;
                    }
                    if (!this.typeValidation.equals((Object)((Adapter)object).typeValidation)) {
                        return false;
                    }
                    if (!this.instrumentedType.equals(((Adapter)object).instrumentedType)) {
                        return false;
                    }
                    if (!this.fieldRegistry.equals(((Adapter)object).fieldRegistry)) {
                        return false;
                    }
                    if (!this.methodRegistry.equals(((Adapter)object).methodRegistry)) {
                        return false;
                    }
                    if (!this.recordComponentRegistry.equals(((Adapter)object).recordComponentRegistry)) {
                        return false;
                    }
                    if (!this.typeAttributeAppender.equals(((Adapter)object).typeAttributeAppender)) {
                        return false;
                    }
                    if (!this.asmVisitorWrapper.equals(((Adapter)object).asmVisitorWrapper)) {
                        return false;
                    }
                    if (!this.classFileVersion.equals(((Adapter)object).classFileVersion)) {
                        return false;
                    }
                    if (!this.auxiliaryTypeNamingStrategy.equals(((Adapter)object).auxiliaryTypeNamingStrategy)) {
                        return false;
                    }
                    if (!this.annotationValueFilterFactory.equals(((Adapter)object).annotationValueFilterFactory)) {
                        return false;
                    }
                    if (!this.implementationContextFactory.equals(((Adapter)object).implementationContextFactory)) {
                        return false;
                    }
                    if (!this.methodGraphCompiler.equals(((Adapter)object).methodGraphCompiler)) {
                        return false;
                    }
                    if (!this.visibilityBridgeStrategy.equals(((Adapter)object).visibilityBridgeStrategy)) {
                        return false;
                    }
                    if (!this.classWriterStrategy.equals(((Adapter)object).classWriterStrategy)) {
                        return false;
                    }
                    if (!this.ignoredMethods.equals(((Adapter)object).ignoredMethods)) {
                        return false;
                    }
                    return ((Object)this.auxiliaryTypes).equals(((Adapter)object).auxiliaryTypes);
                }

                public int hashCode() {
                    return ((((((((((((((((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.fieldRegistry.hashCode()) * 31 + this.methodRegistry.hashCode()) * 31 + this.recordComponentRegistry.hashCode()) * 31 + this.typeAttributeAppender.hashCode()) * 31 + this.asmVisitorWrapper.hashCode()) * 31 + this.classFileVersion.hashCode()) * 31 + this.auxiliaryTypeNamingStrategy.hashCode()) * 31 + this.annotationValueFilterFactory.hashCode()) * 31 + this.annotationRetention.hashCode()) * 31 + this.implementationContextFactory.hashCode()) * 31 + this.methodGraphCompiler.hashCode()) * 31 + this.typeValidation.hashCode()) * 31 + this.visibilityBridgeStrategy.hashCode()) * 31 + this.classWriterStrategy.hashCode()) * 31 + this.ignoredMethods.hashCode()) * 31 + ((Object)this.auxiliaryTypes).hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected class RecordComponentMatchAdapter
                extends RecordComponentDefinition.Optional.AbstractBase<U> {
                    private final LatentMatcher<? super RecordComponentDescription> matcher;
                    private final RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory;
                    private final Transformer<RecordComponentDescription> transformer;

                    protected RecordComponentMatchAdapter(LatentMatcher<? super RecordComponentDescription> matcher) {
                        this(matcher, RecordComponentAttributeAppender.NoOp.INSTANCE, Transformer.NoOp.make());
                    }

                    protected RecordComponentMatchAdapter(LatentMatcher<? super RecordComponentDescription> matcher, RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory, Transformer<RecordComponentDescription> transformer) {
                        this.matcher = matcher;
                        this.recordComponentAttributeAppenderFactory = recordComponentAttributeAppenderFactory;
                        this.transformer = transformer;
                    }

                    @Override
                    public RecordComponentDefinition.Optional<U> annotateRecordComponent(Collection<? extends AnnotationDescription> annotations) {
                        return this.attribute(new RecordComponentAttributeAppender.Explicit(new ArrayList<AnnotationDescription>(annotations)));
                    }

                    @Override
                    public RecordComponentDefinition.Optional<U> attribute(RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory) {
                        return new RecordComponentMatchAdapter(this.matcher, new RecordComponentAttributeAppender.Factory.Compound(this.recordComponentAttributeAppenderFactory, recordComponentAttributeAppenderFactory), this.transformer);
                    }

                    @Override
                    public RecordComponentDefinition.Optional<U> transform(Transformer<RecordComponentDescription> transformer) {
                        return new RecordComponentMatchAdapter(this.matcher, this.recordComponentAttributeAppenderFactory, new Transformer.Compound<RecordComponentDescription>(this.transformer, transformer));
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType, Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry.prepend(this.matcher, this.recordComponentAttributeAppenderFactory, this.transformer), Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class RecordComponentDefinitionAdapter
                extends RecordComponentDefinition.Optional.AbstractBase<U> {
                    private final RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory;
                    private final RecordComponentDescription.Token token;
                    private final Transformer<RecordComponentDescription> transformer;

                    protected RecordComponentDefinitionAdapter(RecordComponentDescription.Token token) {
                        this(RecordComponentAttributeAppender.ForInstrumentedRecordComponent.INSTANCE, Transformer.NoOp.make(), token);
                    }

                    protected RecordComponentDefinitionAdapter(RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory, Transformer<RecordComponentDescription> transformer, RecordComponentDescription.Token token) {
                        this.recordComponentAttributeAppenderFactory = recordComponentAttributeAppenderFactory;
                        this.transformer = transformer;
                        this.token = token;
                    }

                    @Override
                    public RecordComponentDefinition.Optional<U> annotateRecordComponent(Collection<? extends AnnotationDescription> annotations) {
                        return new RecordComponentDefinitionAdapter(this.recordComponentAttributeAppenderFactory, this.transformer, new RecordComponentDescription.Token(this.token.getName(), this.token.getType(), CompoundList.of(this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations))));
                    }

                    @Override
                    public RecordComponentDefinition.Optional<U> attribute(RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory) {
                        return new RecordComponentDefinitionAdapter(new RecordComponentAttributeAppender.Factory.Compound(this.recordComponentAttributeAppenderFactory, recordComponentAttributeAppenderFactory), this.transformer, this.token);
                    }

                    @Override
                    public RecordComponentDefinition.Optional<U> transform(Transformer<RecordComponentDescription> transformer) {
                        return new RecordComponentDefinitionAdapter(this.recordComponentAttributeAppenderFactory, new Transformer.Compound<RecordComponentDescription>(this.transformer, transformer), this.token);
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withRecordComponent(this.token), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry.prepend(new LatentMatcher.ForRecordComponentToken(this.token), this.recordComponentAttributeAppenderFactory, this.transformer), Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
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
                        if (!this.recordComponentAttributeAppenderFactory.equals(((RecordComponentDefinitionAdapter)object).recordComponentAttributeAppenderFactory)) {
                            return false;
                        }
                        if (!this.token.equals(((RecordComponentDefinitionAdapter)object).token)) {
                            return false;
                        }
                        if (!this.transformer.equals(((RecordComponentDefinitionAdapter)object).transformer)) {
                            return false;
                        }
                        return Adapter.this.equals(((RecordComponentDefinitionAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.recordComponentAttributeAppenderFactory.hashCode()) * 31 + this.token.hashCode()) * 31 + this.transformer.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class OptionalMethodMatchAdapter
                extends Delegator<U>
                implements MethodDefinition.ImplementationDefinition.Optional<U> {
                    private final TypeList.Generic interfaces;

                    protected OptionalMethodMatchAdapter(TypeList.Generic interfaces) {
                        this.interfaces = interfaces;
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withInterfaces(this.interfaces), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> intercept(Implementation implementation) {
                        return this.interfaceType().intercept(implementation);
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> withoutCode() {
                        return this.interfaceType().withoutCode();
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> defaultValue(AnnotationValue<?, ?> annotationValue) {
                        return this.interfaceType().defaultValue(annotationValue);
                    }

                    @Override
                    public <V> MethodDefinition.ReceiverTypeDefinition<U> defaultValue(V value, Class<? extends V> type) {
                        return this.interfaceType().defaultValue(value, type);
                    }

                    private MethodDefinition.ImplementationDefinition<U> interfaceType() {
                        ElementMatcher.Junction elementMatcher = ElementMatchers.none();
                        for (TypeDescription typeDescription : this.interfaces.asErasures()) {
                            elementMatcher = elementMatcher.or(ElementMatchers.isSuperTypeOf(typeDescription));
                        }
                        return this.materialize().invokable(ElementMatchers.isDeclaredBy(ElementMatchers.isInterface().and(elementMatcher)));
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
                        if (!this.interfaces.equals(((OptionalMethodMatchAdapter)object).interfaces)) {
                            return false;
                        }
                        return Adapter.this.equals(((OptionalMethodMatchAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.interfaces.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class MethodMatchAdapter
                extends MethodDefinition.ImplementationDefinition.AbstractBase<U> {
                    private final LatentMatcher<? super MethodDescription> matcher;

                    protected MethodMatchAdapter(LatentMatcher<? super MethodDescription> matcher) {
                        this.matcher = matcher;
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> intercept(Implementation implementation) {
                        return this.materialize(new MethodRegistry.Handler.ForImplementation(implementation));
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> withoutCode() {
                        return this.materialize(MethodRegistry.Handler.ForAbstractMethod.INSTANCE);
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> defaultValue(AnnotationValue<?, ?> annotationValue) {
                        return this.materialize(new MethodRegistry.Handler.ForAnnotationValue(annotationValue));
                    }

                    private MethodDefinition.ReceiverTypeDefinition<U> materialize(MethodRegistry.Handler handler) {
                        return new AnnotationAdapter(handler);
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
                        if (!this.matcher.equals(((MethodMatchAdapter)object).matcher)) {
                            return false;
                        }
                        return Adapter.this.equals(((MethodMatchAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + Adapter.this.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class AnnotationAdapter
                    extends MethodDefinition.AbstractBase.Adapter<U> {
                        protected AnnotationAdapter(MethodRegistry.Handler handler) {
                            this(handler, MethodAttributeAppender.NoOp.INSTANCE, Transformer.NoOp.make());
                        }

                        protected AnnotationAdapter(MethodRegistry.Handler handler, MethodAttributeAppender.Factory methodAttributeAppenderFactory, Transformer<MethodDescription> transformer) {
                            super(handler, methodAttributeAppenderFactory, transformer);
                        }

                        @Override
                        public MethodDefinition<U> receiverType(TypeDescription.Generic receiverType) {
                            return new AnnotationAdapter(this.handler, new MethodAttributeAppender.Factory.Compound(this.methodAttributeAppenderFactory, new MethodAttributeAppender.ForReceiverType(receiverType)), this.transformer);
                        }

                        @Override
                        public MethodDefinition<U> annotateMethod(Collection<? extends AnnotationDescription> annotations) {
                            return new AnnotationAdapter(this.handler, new MethodAttributeAppender.Factory.Compound(this.methodAttributeAppenderFactory, new MethodAttributeAppender.Explicit(new ArrayList<AnnotationDescription>(annotations))), this.transformer);
                        }

                        @Override
                        public MethodDefinition<U> annotateParameter(int index, Collection<? extends AnnotationDescription> annotations) {
                            return new AnnotationAdapter(this.handler, new MethodAttributeAppender.Factory.Compound(this.methodAttributeAppenderFactory, new MethodAttributeAppender.Explicit(index, new ArrayList<AnnotationDescription>(annotations))), this.transformer);
                        }

                        @Override
                        protected MethodDefinition<U> materialize(MethodRegistry.Handler handler, MethodAttributeAppender.Factory methodAttributeAppenderFactory, Transformer<MethodDescription> transformer) {
                            return new AnnotationAdapter(handler, methodAttributeAppenderFactory, transformer);
                        }

                        @Override
                        protected Builder<U> materialize() {
                            return Adapter.this.materialize(Adapter.this.instrumentedType, Adapter.this.fieldRegistry, Adapter.this.methodRegistry.prepend(MethodMatchAdapter.this.matcher, this.handler, this.methodAttributeAppenderFactory, this.transformer), Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
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
                            return MethodMatchAdapter.this.equals(((AnnotationAdapter)object).MethodMatchAdapter.this);
                        }

                        @Override
                        public int hashCode() {
                            return super.hashCode() * 31 + MethodMatchAdapter.this.hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class MethodDefinitionAdapter
                extends MethodDefinition.ParameterDefinition.Initial.AbstractBase<U> {
                    private final MethodDescription.Token token;

                    protected MethodDefinitionAdapter(MethodDescription.Token token) {
                        this.token = token;
                    }

                    @Override
                    public MethodDefinition.ParameterDefinition.Annotatable<U> withParameter(TypeDefinition type, String name, int modifiers) {
                        return new ParameterAnnotationAdapter(new ParameterDescription.Token(type.asGenericType(), name, modifiers));
                    }

                    @Override
                    public MethodDefinition.ParameterDefinition.Simple.Annotatable<U> withParameter(TypeDefinition type) {
                        return new SimpleParameterAnnotationAdapter(new ParameterDescription.Token(type.asGenericType()));
                    }

                    @Override
                    public MethodDefinition.ExceptionDefinition<U> throwing(Collection<? extends TypeDefinition> types) {
                        return new MethodDefinitionAdapter(new MethodDescription.Token(this.token.getName(), this.token.getModifiers(), this.token.getTypeVariableTokens(), this.token.getReturnType(), this.token.getParameterTokens(), CompoundList.of(this.token.getExceptionTypes(), new TypeList.Generic.Explicit(new ArrayList<TypeDefinition>(types))), this.token.getAnnotations(), this.token.getDefaultValue(), this.token.getReceiverType()));
                    }

                    @Override
                    public MethodDefinition.TypeVariableDefinition.Annotatable<U> typeVariable(String symbol, Collection<? extends TypeDefinition> bounds) {
                        return new TypeVariableAnnotationAdapter(new TypeVariableToken(symbol, new TypeList.Generic.Explicit(new ArrayList<TypeDefinition>(bounds))));
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> intercept(Implementation implementation) {
                        return this.materialize(new MethodRegistry.Handler.ForImplementation(implementation));
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> withoutCode() {
                        return new MethodDefinitionAdapter(new MethodDescription.Token(this.token.getName(), (this.token.getModifiers() & 0x100) == 0 ? ModifierContributor.Resolver.of(MethodManifestation.ABSTRACT).resolve(this.token.getModifiers()) : this.token.getModifiers(), this.token.getTypeVariableTokens(), this.token.getReturnType(), this.token.getParameterTokens(), this.token.getExceptionTypes(), this.token.getAnnotations(), this.token.getDefaultValue(), this.token.getReceiverType())).materialize(MethodRegistry.Handler.ForAbstractMethod.INSTANCE);
                    }

                    @Override
                    public MethodDefinition.ReceiverTypeDefinition<U> defaultValue(AnnotationValue<?, ?> annotationValue) {
                        return new MethodDefinitionAdapter(new MethodDescription.Token(this.token.getName(), ModifierContributor.Resolver.of(MethodManifestation.ABSTRACT).resolve(this.token.getModifiers()), this.token.getTypeVariableTokens(), this.token.getReturnType(), this.token.getParameterTokens(), this.token.getExceptionTypes(), this.token.getAnnotations(), annotationValue, this.token.getReceiverType())).materialize(new MethodRegistry.Handler.ForAnnotationValue(annotationValue));
                    }

                    private MethodDefinition.ReceiverTypeDefinition<U> materialize(MethodRegistry.Handler handler) {
                        return new AnnotationAdapter(handler);
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
                        if (!this.token.equals(((MethodDefinitionAdapter)object).token)) {
                            return false;
                        }
                        return Adapter.this.equals(((MethodDefinitionAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.token.hashCode()) * 31 + Adapter.this.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class AnnotationAdapter
                    extends MethodDefinition.AbstractBase.Adapter<U> {
                        protected AnnotationAdapter(MethodRegistry.Handler handler) {
                            this(handler, MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER, Transformer.NoOp.make());
                        }

                        protected AnnotationAdapter(MethodRegistry.Handler handler, MethodAttributeAppender.Factory methodAttributeAppenderFactory, Transformer<MethodDescription> transformer) {
                            super(handler, methodAttributeAppenderFactory, transformer);
                        }

                        @Override
                        public MethodDefinition<U> receiverType(TypeDescription.Generic receiverType) {
                            MethodDefinitionAdapter methodDefinitionAdapter = new MethodDefinitionAdapter(new MethodDescription.Token(MethodDefinitionAdapter.this.token.getName(), MethodDefinitionAdapter.this.token.getModifiers(), MethodDefinitionAdapter.this.token.getTypeVariableTokens(), MethodDefinitionAdapter.this.token.getReturnType(), MethodDefinitionAdapter.this.token.getParameterTokens(), MethodDefinitionAdapter.this.token.getExceptionTypes(), MethodDefinitionAdapter.this.token.getAnnotations(), MethodDefinitionAdapter.this.token.getDefaultValue(), receiverType));
                            methodDefinitionAdapter.getClass();
                            return methodDefinitionAdapter.new AnnotationAdapter(this.handler, this.methodAttributeAppenderFactory, this.transformer);
                        }

                        @Override
                        public MethodDefinition<U> annotateMethod(Collection<? extends AnnotationDescription> annotations) {
                            MethodDefinitionAdapter methodDefinitionAdapter = new MethodDefinitionAdapter(new MethodDescription.Token(MethodDefinitionAdapter.this.token.getName(), MethodDefinitionAdapter.this.token.getModifiers(), MethodDefinitionAdapter.this.token.getTypeVariableTokens(), MethodDefinitionAdapter.this.token.getReturnType(), MethodDefinitionAdapter.this.token.getParameterTokens(), MethodDefinitionAdapter.this.token.getExceptionTypes(), CompoundList.of(MethodDefinitionAdapter.this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations)), MethodDefinitionAdapter.this.token.getDefaultValue(), MethodDefinitionAdapter.this.token.getReceiverType()));
                            methodDefinitionAdapter.getClass();
                            return methodDefinitionAdapter.new AnnotationAdapter(this.handler, this.methodAttributeAppenderFactory, this.transformer);
                        }

                        @Override
                        public MethodDefinition<U> annotateParameter(int index, Collection<? extends AnnotationDescription> annotations) {
                            ArrayList<ParameterDescription.Token> parameterTokens = new ArrayList<ParameterDescription.Token>(MethodDefinitionAdapter.this.token.getParameterTokens());
                            parameterTokens.set(index, new ParameterDescription.Token(((ParameterDescription.Token)MethodDefinitionAdapter.this.token.getParameterTokens().get(index)).getType(), CompoundList.of(((ParameterDescription.Token)MethodDefinitionAdapter.this.token.getParameterTokens().get(index)).getAnnotations(), new ArrayList<AnnotationDescription>(annotations)), ((ParameterDescription.Token)MethodDefinitionAdapter.this.token.getParameterTokens().get(index)).getName(), ((ParameterDescription.Token)MethodDefinitionAdapter.this.token.getParameterTokens().get(index)).getModifiers()));
                            MethodDefinitionAdapter methodDefinitionAdapter = new MethodDefinitionAdapter(new MethodDescription.Token(MethodDefinitionAdapter.this.token.getName(), MethodDefinitionAdapter.this.token.getModifiers(), MethodDefinitionAdapter.this.token.getTypeVariableTokens(), MethodDefinitionAdapter.this.token.getReturnType(), parameterTokens, MethodDefinitionAdapter.this.token.getExceptionTypes(), MethodDefinitionAdapter.this.token.getAnnotations(), MethodDefinitionAdapter.this.token.getDefaultValue(), MethodDefinitionAdapter.this.token.getReceiverType()));
                            methodDefinitionAdapter.getClass();
                            return methodDefinitionAdapter.new AnnotationAdapter(this.handler, this.methodAttributeAppenderFactory, this.transformer);
                        }

                        @Override
                        protected MethodDefinition<U> materialize(MethodRegistry.Handler handler, MethodAttributeAppender.Factory methodAttributeAppenderFactory, Transformer<MethodDescription> transformer) {
                            return new AnnotationAdapter(handler, methodAttributeAppenderFactory, transformer);
                        }

                        @Override
                        protected Builder<U> materialize() {
                            return Adapter.this.materialize(Adapter.this.instrumentedType.withMethod(MethodDefinitionAdapter.this.token), Adapter.this.fieldRegistry, Adapter.this.methodRegistry.prepend(new LatentMatcher.ForMethodToken(MethodDefinitionAdapter.this.token), this.handler, this.methodAttributeAppenderFactory, this.transformer), Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
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
                            return MethodDefinitionAdapter.this.equals(((AnnotationAdapter)object).MethodDefinitionAdapter.this);
                        }

                        @Override
                        public int hashCode() {
                            return super.hashCode() * 31 + MethodDefinitionAdapter.this.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class SimpleParameterAnnotationAdapter
                    extends MethodDefinition.ParameterDefinition.Simple.Annotatable.AbstractBase.Adapter<U> {
                        private final ParameterDescription.Token token;

                        protected SimpleParameterAnnotationAdapter(ParameterDescription.Token token) {
                            this.token = token;
                        }

                        @Override
                        public MethodDefinition.ParameterDefinition.Simple.Annotatable<U> annotateParameter(Collection<? extends AnnotationDescription> annotations) {
                            return new SimpleParameterAnnotationAdapter(new ParameterDescription.Token(this.token.getType(), CompoundList.of(this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations)), this.token.getName(), this.token.getModifiers()));
                        }

                        @Override
                        protected MethodDefinition.ParameterDefinition.Simple<U> materialize() {
                            return new MethodDefinitionAdapter(new MethodDescription.Token(MethodDefinitionAdapter.this.token.getName(), MethodDefinitionAdapter.this.token.getModifiers(), MethodDefinitionAdapter.this.token.getTypeVariableTokens(), MethodDefinitionAdapter.this.token.getReturnType(), CompoundList.of(MethodDefinitionAdapter.this.token.getParameterTokens(), this.token), MethodDefinitionAdapter.this.token.getExceptionTypes(), MethodDefinitionAdapter.this.token.getAnnotations(), MethodDefinitionAdapter.this.token.getDefaultValue(), MethodDefinitionAdapter.this.token.getReceiverType()));
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
                            if (!this.token.equals(((SimpleParameterAnnotationAdapter)object).token)) {
                                return false;
                            }
                            return MethodDefinitionAdapter.this.equals(((SimpleParameterAnnotationAdapter)object).MethodDefinitionAdapter.this);
                        }

                        public int hashCode() {
                            return (this.getClass().hashCode() * 31 + this.token.hashCode()) * 31 + MethodDefinitionAdapter.this.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class ParameterAnnotationAdapter
                    extends MethodDefinition.ParameterDefinition.Annotatable.AbstractBase.Adapter<U> {
                        private final ParameterDescription.Token token;

                        protected ParameterAnnotationAdapter(ParameterDescription.Token token) {
                            this.token = token;
                        }

                        @Override
                        public MethodDefinition.ParameterDefinition.Annotatable<U> annotateParameter(Collection<? extends AnnotationDescription> annotations) {
                            return new ParameterAnnotationAdapter(new ParameterDescription.Token(this.token.getType(), CompoundList.of(this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations)), this.token.getName(), this.token.getModifiers()));
                        }

                        @Override
                        protected MethodDefinition.ParameterDefinition<U> materialize() {
                            return new MethodDefinitionAdapter(new MethodDescription.Token(MethodDefinitionAdapter.this.token.getName(), MethodDefinitionAdapter.this.token.getModifiers(), MethodDefinitionAdapter.this.token.getTypeVariableTokens(), MethodDefinitionAdapter.this.token.getReturnType(), CompoundList.of(MethodDefinitionAdapter.this.token.getParameterTokens(), this.token), MethodDefinitionAdapter.this.token.getExceptionTypes(), MethodDefinitionAdapter.this.token.getAnnotations(), MethodDefinitionAdapter.this.token.getDefaultValue(), MethodDefinitionAdapter.this.token.getReceiverType()));
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
                            if (!this.token.equals(((ParameterAnnotationAdapter)object).token)) {
                                return false;
                            }
                            return MethodDefinitionAdapter.this.equals(((ParameterAnnotationAdapter)object).MethodDefinitionAdapter.this);
                        }

                        public int hashCode() {
                            return (this.getClass().hashCode() * 31 + this.token.hashCode()) * 31 + MethodDefinitionAdapter.this.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class TypeVariableAnnotationAdapter
                    extends MethodDefinition.TypeVariableDefinition.Annotatable.AbstractBase.Adapter<U> {
                        private final TypeVariableToken token;

                        protected TypeVariableAnnotationAdapter(TypeVariableToken token) {
                            this.token = token;
                        }

                        @Override
                        protected MethodDefinition.ParameterDefinition<U> materialize() {
                            return new MethodDefinitionAdapter(new MethodDescription.Token(MethodDefinitionAdapter.this.token.getName(), MethodDefinitionAdapter.this.token.getModifiers(), CompoundList.of(MethodDefinitionAdapter.this.token.getTypeVariableTokens(), this.token), MethodDefinitionAdapter.this.token.getReturnType(), MethodDefinitionAdapter.this.token.getParameterTokens(), MethodDefinitionAdapter.this.token.getExceptionTypes(), MethodDefinitionAdapter.this.token.getAnnotations(), MethodDefinitionAdapter.this.token.getDefaultValue(), MethodDefinitionAdapter.this.token.getReceiverType()));
                        }

                        @Override
                        public MethodDefinition.TypeVariableDefinition.Annotatable<U> annotateTypeVariable(Collection<? extends AnnotationDescription> annotations) {
                            return new TypeVariableAnnotationAdapter(new TypeVariableToken(this.token.getSymbol(), this.token.getBounds(), CompoundList.of(this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations))));
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
                            if (!this.token.equals(((TypeVariableAnnotationAdapter)object).token)) {
                                return false;
                            }
                            return MethodDefinitionAdapter.this.equals(((TypeVariableAnnotationAdapter)object).MethodDefinitionAdapter.this);
                        }

                        public int hashCode() {
                            return (this.getClass().hashCode() * 31 + this.token.hashCode()) * 31 + MethodDefinitionAdapter.this.hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class FieldMatchAdapter
                extends FieldDefinition.Optional.Valuable.AbstractBase.Adapter<U> {
                    private final LatentMatcher<? super FieldDescription> matcher;

                    protected FieldMatchAdapter(LatentMatcher<? super FieldDescription> matcher) {
                        this(FieldAttributeAppender.NoOp.INSTANCE, Transformer.NoOp.make(), FieldDescription.NO_DEFAULT_VALUE, matcher);
                    }

                    protected FieldMatchAdapter(FieldAttributeAppender.Factory fieldAttributeAppenderFactory, @MaybeNull Transformer<FieldDescription> transformer, Object defaultValue, LatentMatcher<? super FieldDescription> matcher) {
                        super(fieldAttributeAppenderFactory, transformer, defaultValue);
                        this.matcher = matcher;
                    }

                    @Override
                    public FieldDefinition.Optional<U> annotateField(Collection<? extends AnnotationDescription> annotations) {
                        return this.attribute(new FieldAttributeAppender.Explicit(new ArrayList<AnnotationDescription>(annotations)));
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType, Adapter.this.fieldRegistry.prepend(this.matcher, this.fieldAttributeAppenderFactory, this.defaultValue, this.transformer), Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }

                    @Override
                    protected FieldDefinition.Optional<U> materialize(FieldAttributeAppender.Factory fieldAttributeAppenderFactory, Transformer<FieldDescription> transformer, @MaybeNull Object defaultValue) {
                        return new FieldMatchAdapter(fieldAttributeAppenderFactory, transformer, defaultValue, this.matcher);
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
                        if (!this.matcher.equals(((FieldMatchAdapter)object).matcher)) {
                            return false;
                        }
                        return Adapter.this.equals(((FieldMatchAdapter)object).Adapter.this);
                    }

                    @Override
                    public int hashCode() {
                        return (super.hashCode() * 31 + this.matcher.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class FieldDefinitionAdapter
                extends FieldDefinition.Optional.Valuable.AbstractBase.Adapter<U> {
                    private final FieldDescription.Token token;

                    protected FieldDefinitionAdapter(FieldDescription.Token token) {
                        this(FieldAttributeAppender.ForInstrumentedField.INSTANCE, Transformer.NoOp.make(), FieldDescription.NO_DEFAULT_VALUE, token);
                    }

                    protected FieldDefinitionAdapter(FieldAttributeAppender.Factory fieldAttributeAppenderFactory, @MaybeNull Transformer<FieldDescription> transformer, Object defaultValue, FieldDescription.Token token) {
                        super(fieldAttributeAppenderFactory, transformer, defaultValue);
                        this.token = token;
                    }

                    @Override
                    public FieldDefinition.Optional<U> annotateField(Collection<? extends AnnotationDescription> annotations) {
                        return new FieldDefinitionAdapter(this.fieldAttributeAppenderFactory, this.transformer, this.defaultValue, new FieldDescription.Token(this.token.getName(), this.token.getModifiers(), this.token.getType(), CompoundList.of(this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations))));
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withField(this.token), Adapter.this.fieldRegistry.prepend(new LatentMatcher.ForFieldToken(this.token), this.fieldAttributeAppenderFactory, this.defaultValue, this.transformer), Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }

                    @Override
                    protected FieldDefinition.Optional<U> materialize(FieldAttributeAppender.Factory fieldAttributeAppenderFactory, Transformer<FieldDescription> transformer, @MaybeNull Object defaultValue) {
                        return new FieldDefinitionAdapter(fieldAttributeAppenderFactory, transformer, defaultValue, this.token);
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
                        if (!this.token.equals(((FieldDefinitionAdapter)object).token)) {
                            return false;
                        }
                        return Adapter.this.equals(((FieldDefinitionAdapter)object).Adapter.this);
                    }

                    @Override
                    public int hashCode() {
                        return (super.hashCode() * 31 + this.token.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class TypeVariableDefinitionAdapter
                extends TypeVariableDefinition.AbstractBase<U> {
                    private final TypeVariableToken token;

                    protected TypeVariableDefinitionAdapter(TypeVariableToken token) {
                        this.token = token;
                    }

                    @Override
                    public TypeVariableDefinition<U> annotateTypeVariable(Collection<? extends AnnotationDescription> annotations) {
                        return new TypeVariableDefinitionAdapter(new TypeVariableToken(this.token.getSymbol(), this.token.getBounds(), CompoundList.of(this.token.getAnnotations(), new ArrayList<AnnotationDescription>(annotations))));
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withTypeVariable(this.token), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
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
                        if (!this.token.equals(((TypeVariableDefinitionAdapter)object).token)) {
                            return false;
                        }
                        return Adapter.this.equals(((TypeVariableDefinitionAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.token.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class InnerTypeDefinitionForMethodAdapter
                extends Delegator<U>
                implements InnerTypeDefinition<U> {
                    private final MethodDescription.InDefinedShape methodDescription;

                    protected InnerTypeDefinitionForMethodAdapter(MethodDescription.InDefinedShape methodDescription) {
                        this.methodDescription = methodDescription;
                    }

                    @Override
                    public Builder<U> asAnonymousType() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withDeclaringType(TypeDescription.UNDEFINED).withEnclosingMethod(this.methodDescription).withAnonymousClass(true), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withDeclaringType(TypeDescription.UNDEFINED).withEnclosingMethod(this.methodDescription).withLocalClass(true), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
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
                        if (!this.methodDescription.equals(((InnerTypeDefinitionForMethodAdapter)object).methodDescription)) {
                            return false;
                        }
                        return Adapter.this.equals(((InnerTypeDefinitionForMethodAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class InnerTypeDefinitionForTypeAdapter
                extends Delegator<U>
                implements InnerTypeDefinition.ForType<U> {
                    private final TypeDescription typeDescription;

                    protected InnerTypeDefinitionForTypeAdapter(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    @Override
                    public Builder<U> asAnonymousType() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withDeclaringType(TypeDescription.UNDEFINED).withEnclosingType(this.typeDescription).withAnonymousClass(true), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }

                    @Override
                    public Builder<U> asMemberType() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withDeclaringType(this.typeDescription).withEnclosingType(this.typeDescription).withAnonymousClass(false).withLocalClass(false), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
                    }

                    @Override
                    protected Builder<U> materialize() {
                        return Adapter.this.materialize(Adapter.this.instrumentedType.withDeclaringType(TypeDescription.UNDEFINED).withEnclosingType(this.typeDescription).withLocalClass(true), Adapter.this.fieldRegistry, Adapter.this.methodRegistry, Adapter.this.recordComponentRegistry, Adapter.this.typeAttributeAppender, Adapter.this.asmVisitorWrapper, Adapter.this.classFileVersion, Adapter.this.auxiliaryTypeNamingStrategy, Adapter.this.annotationValueFilterFactory, Adapter.this.annotationRetention, Adapter.this.implementationContextFactory, Adapter.this.methodGraphCompiler, Adapter.this.typeValidation, Adapter.this.visibilityBridgeStrategy, Adapter.this.classWriterStrategy, Adapter.this.ignoredMethods, Adapter.this.auxiliaryTypes);
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
                        if (!this.typeDescription.equals(((InnerTypeDefinitionForTypeAdapter)object).typeDescription)) {
                            return false;
                        }
                        return Adapter.this.equals(((InnerTypeDefinitionForTypeAdapter)object).Adapter.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + Adapter.this.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class UsingTypeWriter<U>
            extends AbstractBase<U> {
                @Override
                public ContextClassVisitor wrap(ClassVisitor classVisitor, int writerFlags, int readerFlags) {
                    return this.toTypeWriter().wrap(classVisitor, writerFlags, readerFlags);
                }

                @Override
                public ContextClassVisitor wrap(ClassVisitor classVisitor, TypePool typePool, int writerFlags, int readerFlags) {
                    return this.toTypeWriter(typePool).wrap(classVisitor, writerFlags, readerFlags);
                }

                @Override
                public Unloaded<U> make(TypeResolutionStrategy typeResolutionStrategy) {
                    return this.toTypeWriter().make(typeResolutionStrategy.resolve());
                }

                @Override
                public Unloaded<U> make(TypeResolutionStrategy typeResolutionStrategy, TypePool typePool) {
                    return this.toTypeWriter(typePool).make(typeResolutionStrategy.resolve());
                }

                protected abstract TypeWriter<U> toTypeWriter();

                protected abstract TypeWriter<U> toTypeWriter(TypePool var1);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class Delegator<U>
            extends AbstractBase<U> {
                @Override
                public Builder<U> visit(AsmVisitorWrapper asmVisitorWrapper) {
                    return this.materialize().visit(asmVisitorWrapper);
                }

                @Override
                public Builder<U> initializer(LoadedTypeInitializer loadedTypeInitializer) {
                    return this.materialize().initializer(loadedTypeInitializer);
                }

                @Override
                public Builder<U> annotateType(Collection<? extends AnnotationDescription> annotations) {
                    return this.materialize().annotateType(annotations);
                }

                @Override
                public Builder<U> attribute(TypeAttributeAppender typeAttributeAppender) {
                    return this.materialize().attribute(typeAttributeAppender);
                }

                @Override
                public Builder<U> modifiers(int modifiers) {
                    return this.materialize().modifiers(modifiers);
                }

                @Override
                public Builder<U> merge(Collection<? extends ModifierContributor.ForType> modifierContributors) {
                    return this.materialize().merge(modifierContributors);
                }

                @Override
                public Builder<U> suffix(String suffix) {
                    return this.materialize().suffix(suffix);
                }

                @Override
                public Builder<U> name(String name) {
                    return this.materialize().name(name);
                }

                @Override
                public Builder<U> topLevelType() {
                    return this.materialize().topLevelType();
                }

                @Override
                public InnerTypeDefinition.ForType<U> innerTypeOf(TypeDescription type) {
                    return this.materialize().innerTypeOf(type);
                }

                @Override
                public InnerTypeDefinition<U> innerTypeOf(MethodDescription.InDefinedShape methodDescription) {
                    return this.materialize().innerTypeOf(methodDescription);
                }

                @Override
                public Builder<U> declaredTypes(Collection<? extends TypeDescription> types) {
                    return this.materialize().declaredTypes(types);
                }

                @Override
                public Builder<U> nestHost(TypeDescription type) {
                    return this.materialize().nestHost(type);
                }

                @Override
                public Builder<U> nestMembers(Collection<? extends TypeDescription> types) {
                    return this.materialize().nestMembers(types);
                }

                @Override
                public Builder<U> permittedSubclass(Collection<? extends TypeDescription> types) {
                    return this.materialize().permittedSubclass(types);
                }

                @Override
                public Builder<U> unsealed() {
                    return this.materialize().unsealed();
                }

                @Override
                public MethodDefinition.ImplementationDefinition.Optional<U> implement(Collection<? extends TypeDefinition> interfaceTypes) {
                    return this.materialize().implement(interfaceTypes);
                }

                @Override
                public Builder<U> initializer(ByteCodeAppender byteCodeAppender) {
                    return this.materialize().initializer(byteCodeAppender);
                }

                @Override
                public Builder<U> ignoreAlso(ElementMatcher<? super MethodDescription> ignoredMethods) {
                    return this.materialize().ignoreAlso(ignoredMethods);
                }

                @Override
                public Builder<U> ignoreAlso(LatentMatcher<? super MethodDescription> ignoredMethods) {
                    return this.materialize().ignoreAlso(ignoredMethods);
                }

                @Override
                public TypeVariableDefinition<U> typeVariable(String symbol, Collection<? extends TypeDefinition> bounds) {
                    return this.materialize().typeVariable(symbol, bounds);
                }

                @Override
                public Builder<U> transform(ElementMatcher<? super TypeDescription.Generic> matcher, Transformer<TypeVariableToken> transformer) {
                    return this.materialize().transform(matcher, transformer);
                }

                @Override
                public FieldDefinition.Optional.Valuable<U> defineField(String name, TypeDefinition type, int modifiers) {
                    return this.materialize().defineField(name, type, modifiers);
                }

                @Override
                public FieldDefinition.Valuable<U> field(LatentMatcher<? super FieldDescription> matcher) {
                    return this.materialize().field(matcher);
                }

                @Override
                public MethodDefinition.ParameterDefinition.Initial<U> defineMethod(String name, TypeDefinition returnType, int modifiers) {
                    return this.materialize().defineMethod(name, returnType, modifiers);
                }

                @Override
                public MethodDefinition.ParameterDefinition.Initial<U> defineConstructor(int modifiers) {
                    return this.materialize().defineConstructor(modifiers);
                }

                @Override
                public MethodDefinition.ImplementationDefinition<U> invokable(LatentMatcher<? super MethodDescription> matcher) {
                    return this.materialize().invokable(matcher);
                }

                @Override
                public Builder<U> require(Collection<DynamicType> auxiliaryTypes) {
                    return this.materialize().require(auxiliaryTypes);
                }

                @Override
                public RecordComponentDefinition.Optional<U> defineRecordComponent(String name, TypeDefinition type) {
                    return this.materialize().defineRecordComponent(name, type);
                }

                @Override
                public RecordComponentDefinition.Optional<U> define(RecordComponentDescription recordComponentDescription) {
                    return this.materialize().define(recordComponentDescription);
                }

                @Override
                public RecordComponentDefinition<U> recordComponent(ElementMatcher<? super RecordComponentDescription> matcher) {
                    return this.materialize().recordComponent(matcher);
                }

                @Override
                public RecordComponentDefinition<U> recordComponent(LatentMatcher<? super RecordComponentDescription> matcher) {
                    return this.materialize().recordComponent(matcher);
                }

                @Override
                public ContextClassVisitor wrap(ClassVisitor classVisitor, int writerFlags, int readerFlags) {
                    return this.materialize().wrap(classVisitor, writerFlags, readerFlags);
                }

                @Override
                public ContextClassVisitor wrap(ClassVisitor classVisitor, TypePool typePool, int writerFlags, int readerFlags) {
                    return this.materialize().wrap(classVisitor, typePool, writerFlags, readerFlags);
                }

                @Override
                public Unloaded<U> make() {
                    return this.materialize().make();
                }

                @Override
                public Unloaded<U> make(TypeResolutionStrategy typeResolutionStrategy) {
                    return this.materialize().make(typeResolutionStrategy);
                }

                @Override
                public Unloaded<U> make(TypePool typePool) {
                    return this.materialize().make(typePool);
                }

                @Override
                public Unloaded<U> make(TypeResolutionStrategy typeResolutionStrategy, TypePool typePool) {
                    return this.materialize().make(typeResolutionStrategy, typePool);
                }

                @Override
                public TypeDescription toTypeDescription() {
                    return this.materialize().toTypeDescription();
                }

                protected abstract Builder<U> materialize();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface RecordComponentDefinition<S> {
            public Optional<S> annotateRecordComponent(Annotation ... var1);

            public Optional<S> annotateRecordComponent(List<? extends Annotation> var1);

            public Optional<S> annotateRecordComponent(AnnotationDescription ... var1);

            public Optional<S> annotateRecordComponent(Collection<? extends AnnotationDescription> var1);

            public Optional<S> attribute(RecordComponentAttributeAppender.Factory var1);

            public Optional<S> transform(Transformer<RecordComponentDescription> var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Optional<U>
            extends RecordComponentDefinition<U>,
            Builder<U> {

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<U>
                extends AbstractBase.Delegator<U>
                implements Optional<U> {
                    @Override
                    public Optional<U> annotateRecordComponent(Annotation ... annotation) {
                        return this.annotateRecordComponent(Arrays.asList(annotation));
                    }

                    @Override
                    public Optional<U> annotateRecordComponent(List<? extends Annotation> annotations) {
                        return this.annotateRecordComponent(new AnnotationList.ForLoadedAnnotations(annotations));
                    }

                    @Override
                    public Optional<U> annotateRecordComponent(AnnotationDescription ... annotation) {
                        return this.annotateRecordComponent((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface MethodDefinition<S>
        extends Builder<S> {
            public MethodDefinition<S> annotateMethod(Annotation ... var1);

            public MethodDefinition<S> annotateMethod(List<? extends Annotation> var1);

            public MethodDefinition<S> annotateMethod(AnnotationDescription ... var1);

            public MethodDefinition<S> annotateMethod(Collection<? extends AnnotationDescription> var1);

            public MethodDefinition<S> annotateParameter(int var1, Annotation ... var2);

            public MethodDefinition<S> annotateParameter(int var1, List<? extends Annotation> var2);

            public MethodDefinition<S> annotateParameter(int var1, AnnotationDescription ... var2);

            public MethodDefinition<S> annotateParameter(int var1, Collection<? extends AnnotationDescription> var2);

            public MethodDefinition<S> attribute(MethodAttributeAppender.Factory var1);

            public MethodDefinition<S> transform(Transformer<MethodDescription> var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class AbstractBase<U>
            extends AbstractBase.Delegator<U>
            implements MethodDefinition<U> {
                @Override
                public MethodDefinition<U> annotateMethod(Annotation ... annotation) {
                    return this.annotateMethod(Arrays.asList(annotation));
                }

                @Override
                public MethodDefinition<U> annotateMethod(List<? extends Annotation> annotations) {
                    return this.annotateMethod(new AnnotationList.ForLoadedAnnotations(annotations));
                }

                @Override
                public MethodDefinition<U> annotateMethod(AnnotationDescription ... annotation) {
                    return this.annotateMethod((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                }

                @Override
                public MethodDefinition<U> annotateParameter(int index, Annotation ... annotation) {
                    return this.annotateParameter(index, Arrays.asList(annotation));
                }

                @Override
                public MethodDefinition<U> annotateParameter(int index, List<? extends Annotation> annotations) {
                    return this.annotateParameter(index, new AnnotationList.ForLoadedAnnotations(annotations));
                }

                @Override
                public MethodDefinition<U> annotateParameter(int index, AnnotationDescription ... annotation) {
                    return this.annotateParameter(index, (Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static abstract class Adapter<V>
                extends ReceiverTypeDefinition.AbstractBase<V> {
                    protected final MethodRegistry.Handler handler;
                    protected final MethodAttributeAppender.Factory methodAttributeAppenderFactory;
                    protected final Transformer<MethodDescription> transformer;

                    protected Adapter(MethodRegistry.Handler handler, MethodAttributeAppender.Factory methodAttributeAppenderFactory, Transformer<MethodDescription> transformer) {
                        this.handler = handler;
                        this.methodAttributeAppenderFactory = methodAttributeAppenderFactory;
                        this.transformer = transformer;
                    }

                    @Override
                    public MethodDefinition<V> attribute(MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                        return this.materialize(this.handler, new MethodAttributeAppender.Factory.Compound(this.methodAttributeAppenderFactory, methodAttributeAppenderFactory), this.transformer);
                    }

                    @Override
                    public MethodDefinition<V> transform(Transformer<MethodDescription> transformer) {
                        return this.materialize(this.handler, this.methodAttributeAppenderFactory, new Transformer.Compound<MethodDescription>(this.transformer, transformer));
                    }

                    protected abstract MethodDefinition<V> materialize(MethodRegistry.Handler var1, MethodAttributeAppender.Factory var2, Transformer<MethodDescription> var3);

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
                        if (!this.handler.equals(((Adapter)object).handler)) {
                            return false;
                        }
                        if (!this.methodAttributeAppenderFactory.equals(((Adapter)object).methodAttributeAppenderFactory)) {
                            return false;
                        }
                        return this.transformer.equals(((Adapter)object).transformer);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.handler.hashCode()) * 31 + this.methodAttributeAppenderFactory.hashCode()) * 31 + this.transformer.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface ParameterDefinition<U>
            extends ExceptionDefinition<U> {
                public Annotatable<U> withParameter(Type var1, String var2, ModifierContributor.ForParameter ... var3);

                public Annotatable<U> withParameter(Type var1, String var2, Collection<? extends ModifierContributor.ForParameter> var3);

                public Annotatable<U> withParameter(Type var1, String var2, int var3);

                public Annotatable<U> withParameter(TypeDefinition var1, String var2, ModifierContributor.ForParameter ... var3);

                public Annotatable<U> withParameter(TypeDefinition var1, String var2, Collection<? extends ModifierContributor.ForParameter> var3);

                public Annotatable<U> withParameter(TypeDefinition var1, String var2, int var3);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<V>
                extends ExceptionDefinition.AbstractBase<V>
                implements ParameterDefinition<V> {
                    @Override
                    public Annotatable<V> withParameter(Type type, String name, ModifierContributor.ForParameter ... modifierContributor) {
                        return this.withParameter(type, name, Arrays.asList(modifierContributor));
                    }

                    @Override
                    public Annotatable<V> withParameter(Type type, String name, Collection<? extends ModifierContributor.ForParameter> modifierContributors) {
                        return this.withParameter(type, name, ModifierContributor.Resolver.of(modifierContributors).resolve());
                    }

                    @Override
                    public Annotatable<V> withParameter(Type type, String name, int modifiers) {
                        return this.withParameter((TypeDefinition)TypeDefinition.Sort.describe(type), name, modifiers);
                    }

                    @Override
                    public Annotatable<V> withParameter(TypeDefinition type, String name, ModifierContributor.ForParameter ... modifierContributor) {
                        return this.withParameter(type, name, Arrays.asList(modifierContributor));
                    }

                    @Override
                    public Annotatable<V> withParameter(TypeDefinition type, String name, Collection<? extends ModifierContributor.ForParameter> modifierContributors) {
                        return this.withParameter(type, name, ModifierContributor.Resolver.of(modifierContributors).resolve());
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Initial<V>
                extends ParameterDefinition<V>,
                Simple<V> {
                    public ExceptionDefinition<V> withParameters(Type ... var1);

                    public ExceptionDefinition<V> withParameters(List<? extends Type> var1);

                    public ExceptionDefinition<V> withParameters(TypeDefinition ... var1);

                    public ExceptionDefinition<V> withParameters(Collection<? extends TypeDefinition> var1);

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static abstract class AbstractBase<W>
                    extends net.bytebuddy.dynamic.DynamicType$Builder$MethodDefinition$ParameterDefinition$AbstractBase<W>
                    implements Initial<W> {
                        @Override
                        public Simple.Annotatable<W> withParameter(Type type) {
                            return this.withParameter(TypeDefinition.Sort.describe(type));
                        }

                        @Override
                        public ExceptionDefinition<W> withParameters(Type ... type) {
                            return this.withParameters(Arrays.asList(type));
                        }

                        @Override
                        public ExceptionDefinition<W> withParameters(List<? extends Type> types) {
                            return this.withParameters(new TypeList.Generic.ForLoadedTypes(types));
                        }

                        @Override
                        public ExceptionDefinition<W> withParameters(TypeDefinition ... type) {
                            return this.withParameters((Collection<? extends TypeDefinition>)Arrays.asList(type));
                        }

                        @Override
                        public ExceptionDefinition<W> withParameters(Collection<? extends TypeDefinition> types) {
                            Simple<Object> parameterDefinition = this;
                            for (TypeDefinition typeDefinition : types) {
                                parameterDefinition = parameterDefinition.withParameter(typeDefinition);
                            }
                            return parameterDefinition;
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Simple<V>
                extends ExceptionDefinition<V> {
                    public Annotatable<V> withParameter(Type var1);

                    public Annotatable<V> withParameter(TypeDefinition var1);

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static abstract class AbstractBase<W>
                    extends ExceptionDefinition.AbstractBase<W>
                    implements Simple<W> {
                        @Override
                        public Annotatable<W> withParameter(Type type) {
                            return this.withParameter(TypeDefinition.Sort.describe(type));
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static interface Annotatable<V>
                    extends Simple<V> {
                        public Annotatable<V> annotateParameter(Annotation ... var1);

                        public Annotatable<V> annotateParameter(List<? extends Annotation> var1);

                        public Annotatable<V> annotateParameter(AnnotationDescription ... var1);

                        public Annotatable<V> annotateParameter(Collection<? extends AnnotationDescription> var1);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        public static abstract class AbstractBase<W>
                        extends net.bytebuddy.dynamic.DynamicType$Builder$MethodDefinition$ParameterDefinition$Simple$AbstractBase<W>
                        implements Annotatable<W> {
                            @Override
                            public Annotatable<W> annotateParameter(Annotation ... annotation) {
                                return this.annotateParameter(Arrays.asList(annotation));
                            }

                            @Override
                            public Annotatable<W> annotateParameter(List<? extends Annotation> annotations) {
                                return this.annotateParameter(new AnnotationList.ForLoadedAnnotations(annotations));
                            }

                            @Override
                            public Annotatable<W> annotateParameter(AnnotationDescription ... annotation) {
                                return this.annotateParameter((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static abstract class Adapter<X>
                            extends AbstractBase<X> {
                                protected Adapter() {
                                }

                                @Override
                                public Annotatable<X> withParameter(TypeDefinition type) {
                                    return this.materialize().withParameter(type);
                                }

                                @Override
                                public ExceptionDefinition<X> throwing(Collection<? extends TypeDefinition> types) {
                                    return this.materialize().throwing(types);
                                }

                                @Override
                                public TypeVariableDefinition.Annotatable<X> typeVariable(String symbol, Collection<? extends TypeDefinition> bounds) {
                                    return this.materialize().typeVariable(symbol, bounds);
                                }

                                @Override
                                public ReceiverTypeDefinition<X> intercept(Implementation implementation) {
                                    return this.materialize().intercept(implementation);
                                }

                                @Override
                                public ReceiverTypeDefinition<X> withoutCode() {
                                    return this.materialize().withoutCode();
                                }

                                @Override
                                public ReceiverTypeDefinition<X> defaultValue(AnnotationValue<?, ?> annotationValue) {
                                    return this.materialize().defaultValue(annotationValue);
                                }

                                @Override
                                public <V> ReceiverTypeDefinition<X> defaultValue(V value, Class<? extends V> type) {
                                    return this.materialize().defaultValue(value, type);
                                }

                                protected abstract Simple<X> materialize();
                            }
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Annotatable<V>
                extends ParameterDefinition<V> {
                    public Annotatable<V> annotateParameter(Annotation ... var1);

                    public Annotatable<V> annotateParameter(List<? extends Annotation> var1);

                    public Annotatable<V> annotateParameter(AnnotationDescription ... var1);

                    public Annotatable<V> annotateParameter(Collection<? extends AnnotationDescription> var1);

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static abstract class AbstractBase<W>
                    extends net.bytebuddy.dynamic.DynamicType$Builder$MethodDefinition$ParameterDefinition$AbstractBase<W>
                    implements Annotatable<W> {
                        @Override
                        public Annotatable<W> annotateParameter(Annotation ... annotation) {
                            return this.annotateParameter(Arrays.asList(annotation));
                        }

                        @Override
                        public Annotatable<W> annotateParameter(List<? extends Annotation> annotations) {
                            return this.annotateParameter(new AnnotationList.ForLoadedAnnotations(annotations));
                        }

                        @Override
                        public Annotatable<W> annotateParameter(AnnotationDescription ... annotation) {
                            return this.annotateParameter((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static abstract class Adapter<X>
                        extends AbstractBase<X> {
                            protected Adapter() {
                            }

                            @Override
                            public Annotatable<X> withParameter(TypeDefinition type, String name, int modifiers) {
                                return this.materialize().withParameter(type, name, modifiers);
                            }

                            @Override
                            public ExceptionDefinition<X> throwing(Collection<? extends TypeDefinition> types) {
                                return this.materialize().throwing(types);
                            }

                            @Override
                            public TypeVariableDefinition.Annotatable<X> typeVariable(String symbol, Collection<? extends TypeDefinition> bounds) {
                                return this.materialize().typeVariable(symbol, bounds);
                            }

                            @Override
                            public ReceiverTypeDefinition<X> intercept(Implementation implementation) {
                                return this.materialize().intercept(implementation);
                            }

                            @Override
                            public ReceiverTypeDefinition<X> withoutCode() {
                                return this.materialize().withoutCode();
                            }

                            @Override
                            public ReceiverTypeDefinition<X> defaultValue(AnnotationValue<?, ?> annotationValue) {
                                return this.materialize().defaultValue(annotationValue);
                            }

                            @Override
                            public <V> ReceiverTypeDefinition<X> defaultValue(V value, Class<? extends V> type) {
                                return this.materialize().defaultValue(value, type);
                            }

                            protected abstract ParameterDefinition<X> materialize();
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface ExceptionDefinition<U>
            extends TypeVariableDefinition<U> {
                public ExceptionDefinition<U> throwing(Type ... var1);

                public ExceptionDefinition<U> throwing(List<? extends Type> var1);

                public ExceptionDefinition<U> throwing(TypeDefinition ... var1);

                public ExceptionDefinition<U> throwing(Collection<? extends TypeDefinition> var1);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<V>
                extends TypeVariableDefinition.AbstractBase<V>
                implements ExceptionDefinition<V> {
                    @Override
                    public ExceptionDefinition<V> throwing(Type ... type) {
                        return this.throwing(Arrays.asList(type));
                    }

                    @Override
                    public ExceptionDefinition<V> throwing(List<? extends Type> types) {
                        return this.throwing(new TypeList.Generic.ForLoadedTypes(types));
                    }

                    @Override
                    public ExceptionDefinition<V> throwing(TypeDefinition ... type) {
                        return this.throwing((Collection<? extends TypeDefinition>)Arrays.asList(type));
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface TypeVariableDefinition<U>
            extends ImplementationDefinition<U> {
                public Annotatable<U> typeVariable(String var1);

                public Annotatable<U> typeVariable(String var1, Type ... var2);

                public Annotatable<U> typeVariable(String var1, List<? extends Type> var2);

                public Annotatable<U> typeVariable(String var1, TypeDefinition ... var2);

                public Annotatable<U> typeVariable(String var1, Collection<? extends TypeDefinition> var2);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<V>
                extends ImplementationDefinition.AbstractBase<V>
                implements TypeVariableDefinition<V> {
                    @Override
                    public Annotatable<V> typeVariable(String symbol) {
                        return this.typeVariable(symbol, Collections.singletonList(Object.class));
                    }

                    @Override
                    public Annotatable<V> typeVariable(String symbol, Type ... bound) {
                        return this.typeVariable(symbol, Arrays.asList(bound));
                    }

                    @Override
                    public Annotatable<V> typeVariable(String symbol, List<? extends Type> bounds) {
                        return this.typeVariable(symbol, new TypeList.Generic.ForLoadedTypes(bounds));
                    }

                    @Override
                    public Annotatable<V> typeVariable(String symbol, TypeDefinition ... bound) {
                        return this.typeVariable(symbol, (Collection<? extends TypeDefinition>)Arrays.asList(bound));
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Annotatable<V>
                extends TypeVariableDefinition<V> {
                    public Annotatable<V> annotateTypeVariable(Annotation ... var1);

                    public Annotatable<V> annotateTypeVariable(List<? extends Annotation> var1);

                    public Annotatable<V> annotateTypeVariable(AnnotationDescription ... var1);

                    public Annotatable<V> annotateTypeVariable(Collection<? extends AnnotationDescription> var1);

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static abstract class AbstractBase<W>
                    extends net.bytebuddy.dynamic.DynamicType$Builder$MethodDefinition$TypeVariableDefinition$AbstractBase<W>
                    implements Annotatable<W> {
                        @Override
                        public Annotatable<W> annotateTypeVariable(Annotation ... annotation) {
                            return this.annotateTypeVariable(Arrays.asList(annotation));
                        }

                        @Override
                        public Annotatable<W> annotateTypeVariable(List<? extends Annotation> annotations) {
                            return this.annotateTypeVariable(new AnnotationList.ForLoadedAnnotations(annotations));
                        }

                        @Override
                        public Annotatable<W> annotateTypeVariable(AnnotationDescription ... annotation) {
                            return this.annotateTypeVariable((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static abstract class Adapter<X>
                        extends AbstractBase<X> {
                            protected Adapter() {
                            }

                            @Override
                            public Annotatable<X> typeVariable(String symbol, Collection<? extends TypeDefinition> bounds) {
                                return this.materialize().typeVariable(symbol, bounds);
                            }

                            @Override
                            public ReceiverTypeDefinition<X> intercept(Implementation implementation) {
                                return this.materialize().intercept(implementation);
                            }

                            @Override
                            public ReceiverTypeDefinition<X> withoutCode() {
                                return this.materialize().withoutCode();
                            }

                            @Override
                            public ReceiverTypeDefinition<X> defaultValue(AnnotationValue<?, ?> annotationValue) {
                                return this.materialize().defaultValue(annotationValue);
                            }

                            @Override
                            public <V> ReceiverTypeDefinition<X> defaultValue(V value, Class<? extends V> type) {
                                return this.materialize().defaultValue(value, type);
                            }

                            protected abstract ParameterDefinition<X> materialize();
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface ImplementationDefinition<U> {
                public ReceiverTypeDefinition<U> intercept(Implementation var1);

                public ReceiverTypeDefinition<U> withoutCode();

                public ReceiverTypeDefinition<U> defaultValue(AnnotationValue<?, ?> var1);

                public <W> ReceiverTypeDefinition<U> defaultValue(W var1, Class<? extends W> var2);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<V>
                implements ImplementationDefinition<V> {
                    @Override
                    public <W> ReceiverTypeDefinition<V> defaultValue(W value, Class<? extends W> type) {
                        return this.defaultValue(AnnotationDescription.ForLoadedAnnotation.asValue(value, type));
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Optional<V>
                extends ImplementationDefinition<V>,
                Builder<V> {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface ReceiverTypeDefinition<U>
            extends MethodDefinition<U> {
                public MethodDefinition<U> receiverType(AnnotatedElement var1);

                public MethodDefinition<U> receiverType(TypeDescription.Generic var1);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<V>
                extends net.bytebuddy.dynamic.DynamicType$Builder$MethodDefinition$AbstractBase<V>
                implements ReceiverTypeDefinition<V> {
                    @Override
                    public MethodDefinition<V> receiverType(AnnotatedElement receiverType) {
                        return this.receiverType(TypeDefinition.Sort.describeAnnotated(receiverType));
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface FieldDefinition<S> {
            public Optional<S> annotateField(Annotation ... var1);

            public Optional<S> annotateField(List<? extends Annotation> var1);

            public Optional<S> annotateField(AnnotationDescription ... var1);

            public Optional<S> annotateField(Collection<? extends AnnotationDescription> var1);

            public Optional<S> attribute(FieldAttributeAppender.Factory var1);

            public Optional<S> transform(Transformer<FieldDescription> var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Optional<U>
            extends FieldDefinition<U>,
            Builder<U> {

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static abstract class AbstractBase<U>
                extends AbstractBase.Delegator<U>
                implements Optional<U> {
                    @Override
                    public Optional<U> annotateField(Annotation ... annotation) {
                        return this.annotateField(Arrays.asList(annotation));
                    }

                    @Override
                    public Optional<U> annotateField(List<? extends Annotation> annotations) {
                        return this.annotateField(new AnnotationList.ForLoadedAnnotations(annotations));
                    }

                    @Override
                    public Optional<U> annotateField(AnnotationDescription ... annotation) {
                        return this.annotateField((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Valuable<V>
                extends net.bytebuddy.dynamic.DynamicType$Builder$FieldDefinition$Valuable<V>,
                Optional<V> {

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static abstract class AbstractBase<U>
                    extends net.bytebuddy.dynamic.DynamicType$Builder$FieldDefinition$Optional$AbstractBase<U>
                    implements Valuable<U> {
                        @Override
                        public Optional<U> value(boolean value) {
                            return this.defaultValue(value ? 1 : 0);
                        }

                        @Override
                        public Optional<U> value(int value) {
                            return this.defaultValue(value);
                        }

                        @Override
                        public Optional<U> value(long value) {
                            return this.defaultValue(value);
                        }

                        @Override
                        public Optional<U> value(float value) {
                            return this.defaultValue(Float.valueOf(value));
                        }

                        @Override
                        public Optional<U> value(double value) {
                            return this.defaultValue(value);
                        }

                        @Override
                        public Optional<U> value(String value) {
                            if (value == null) {
                                throw new IllegalArgumentException("Cannot define 'null' as constant value");
                            }
                            return this.defaultValue(value);
                        }

                        protected abstract Optional<U> defaultValue(Object var1);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        private static abstract class Adapter<V>
                        extends AbstractBase<V> {
                            protected final FieldAttributeAppender.Factory fieldAttributeAppenderFactory;
                            protected final Transformer<FieldDescription> transformer;
                            @MaybeNull
                            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                            protected final Object defaultValue;

                            protected Adapter(FieldAttributeAppender.Factory fieldAttributeAppenderFactory, Transformer<FieldDescription> transformer, @MaybeNull Object defaultValue) {
                                this.fieldAttributeAppenderFactory = fieldAttributeAppenderFactory;
                                this.transformer = transformer;
                                this.defaultValue = defaultValue;
                            }

                            @Override
                            public Optional<V> attribute(FieldAttributeAppender.Factory fieldAttributeAppenderFactory) {
                                return this.materialize(new FieldAttributeAppender.Factory.Compound(this.fieldAttributeAppenderFactory, fieldAttributeAppenderFactory), this.transformer, this.defaultValue);
                            }

                            @Override
                            public Optional<V> transform(Transformer<FieldDescription> transformer) {
                                return this.materialize(this.fieldAttributeAppenderFactory, new Transformer.Compound<FieldDescription>(this.transformer, transformer), this.defaultValue);
                            }

                            @Override
                            protected Optional<V> defaultValue(Object defaultValue) {
                                return this.materialize(this.fieldAttributeAppenderFactory, this.transformer, defaultValue);
                            }

                            protected abstract Optional<V> materialize(FieldAttributeAppender.Factory var1, Transformer<FieldDescription> var2, @MaybeNull Object var3);

                            public boolean equals(@MaybeNull Object object) {
                                block12: {
                                    block11: {
                                        Object object2;
                                        block10: {
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
                                            if (!this.fieldAttributeAppenderFactory.equals(((Adapter)object).fieldAttributeAppenderFactory)) {
                                                return false;
                                            }
                                            if (!this.transformer.equals(((Adapter)object).transformer)) {
                                                return false;
                                            }
                                            Object object4 = ((Adapter)object).defaultValue;
                                            object2 = object3 = this.defaultValue;
                                            if (object4 == null) break block10;
                                            if (object2 == null) break block11;
                                            if (!object3.equals(object4)) {
                                                return false;
                                            }
                                            break block12;
                                        }
                                        if (object2 == null) break block12;
                                    }
                                    return false;
                                }
                                return true;
                            }

                            public int hashCode() {
                                int n = ((this.getClass().hashCode() * 31 + this.fieldAttributeAppenderFactory.hashCode()) * 31 + this.transformer.hashCode()) * 31;
                                Object object = this.defaultValue;
                                if (object != null) {
                                    n = n + object.hashCode();
                                }
                                return n;
                            }
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Valuable<U>
            extends FieldDefinition<U> {
                public Optional<U> value(boolean var1);

                public Optional<U> value(int var1);

                public Optional<U> value(long var1);

                public Optional<U> value(float var1);

                public Optional<U> value(double var1);

                public Optional<U> value(String var1);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface TypeVariableDefinition<S>
        extends Builder<S> {
            public TypeVariableDefinition<S> annotateTypeVariable(Annotation ... var1);

            public TypeVariableDefinition<S> annotateTypeVariable(List<? extends Annotation> var1);

            public TypeVariableDefinition<S> annotateTypeVariable(AnnotationDescription ... var1);

            public TypeVariableDefinition<S> annotateTypeVariable(Collection<? extends AnnotationDescription> var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class AbstractBase<U>
            extends AbstractBase.Delegator<U>
            implements TypeVariableDefinition<U> {
                @Override
                public TypeVariableDefinition<U> annotateTypeVariable(Annotation ... annotation) {
                    return this.annotateTypeVariable(Arrays.asList(annotation));
                }

                @Override
                public TypeVariableDefinition<U> annotateTypeVariable(List<? extends Annotation> annotations) {
                    return this.annotateTypeVariable(new AnnotationList.ForLoadedAnnotations(annotations));
                }

                @Override
                public TypeVariableDefinition<U> annotateTypeVariable(AnnotationDescription ... annotation) {
                    return this.annotateTypeVariable((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface InnerTypeDefinition<S>
        extends Builder<S> {
            public Builder<S> asAnonymousType();

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface ForType<U>
            extends InnerTypeDefinition<U> {
                public Builder<U> asMemberType();
            }
        }
    }
}


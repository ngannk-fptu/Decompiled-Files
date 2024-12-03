/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.pool;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.RecordComponentList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.jar.asm.TypeReference;
import net.bytebuddy.jar.asm.signature.SignatureReader;
import net.bytebuddy.jar.asm.signature.SignatureVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.nullability.UnknownNull;

public interface TypePool {
    public Resolution describe(String var1);

    public void clear();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Explicit
    extends AbstractBase.Hierarchical {
        private final Map<String, TypeDescription> types;

        public Explicit(Map<String, TypeDescription> types) {
            this(Empty.INSTANCE, types);
        }

        public Explicit(TypePool parent, Map<String, TypeDescription> types) {
            super(CacheProvider.NoOp.INSTANCE, parent);
            this.types = types;
        }

        public static TypePool wrap(TypeDescription instrumentedType, List<? extends DynamicType> auxiliaryTypes, TypePool typePool) {
            HashMap<String, TypeDescription> typeDescriptions = new HashMap<String, TypeDescription>();
            typeDescriptions.put(instrumentedType.getName(), instrumentedType);
            for (DynamicType dynamicType : auxiliaryTypes) {
                for (TypeDescription typeDescription : dynamicType.getAllTypes().keySet()) {
                    typeDescriptions.put(typeDescription.getName(), typeDescription);
                }
            }
            return new Explicit(typePool, typeDescriptions);
        }

        @Override
        protected Resolution doDescribe(String name) {
            TypeDescription typeDescription = this.types.get(name);
            return typeDescription == null ? new Resolution.Illegal(name) : new Resolution.Simple(typeDescription);
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
            return ((Object)this.types).equals(((Explicit)object).types);
        }

        @Override
        public int hashCode() {
            return super.hashCode() * 31 + ((Object)this.types).hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ClassLoading
    extends AbstractBase.Hierarchical {
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ClassLoader classLoader;

        public ClassLoading(CacheProvider cacheProvider, TypePool parent, @MaybeNull ClassLoader classLoader) {
            super(cacheProvider, parent);
            this.classLoader = classLoader;
        }

        public static TypePool of(@MaybeNull ClassLoader classLoader) {
            return ClassLoading.of(classLoader, Empty.INSTANCE);
        }

        public static TypePool of(@MaybeNull ClassLoader classLoader, TypePool parent) {
            return new ClassLoading(new CacheProvider.Simple(), parent, classLoader);
        }

        public static TypePool ofSystemLoader() {
            return ClassLoading.of(ClassLoader.getSystemClassLoader());
        }

        public static TypePool ofPlatformLoader() {
            return ClassLoading.of(ClassLoader.getSystemClassLoader().getParent());
        }

        public static TypePool ofBootLoader() {
            return ClassLoading.of(ClassLoadingStrategy.BOOTSTRAP_LOADER);
        }

        protected Resolution doDescribe(String name) {
            try {
                return new Resolution.Simple(TypeDescription.ForLoadedType.of(Class.forName(name, false, this.classLoader)));
            }
            catch (ClassNotFoundException ignored) {
                return new Resolution.Illegal(name);
            }
        }

        public boolean equals(@MaybeNull Object object) {
            block11: {
                block10: {
                    ClassLoader classLoader;
                    block9: {
                        ClassLoader classLoader2;
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
                        ClassLoader classLoader3 = ((ClassLoading)object).classLoader;
                        classLoader = classLoader2 = this.classLoader;
                        if (classLoader3 == null) break block9;
                        if (classLoader == null) break block10;
                        if (!classLoader2.equals(classLoader3)) {
                            return false;
                        }
                        break block11;
                    }
                    if (classLoader == null) break block11;
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            int n = super.hashCode() * 31;
            ClassLoader classLoader = this.classLoader;
            if (classLoader != null) {
                n = n + classLoader.hashCode();
            }
            return n;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class LazyFacade
    extends AbstractBase {
        private final TypePool typePool;

        public LazyFacade(TypePool typePool) {
            super(CacheProvider.NoOp.INSTANCE);
            this.typePool = typePool;
        }

        protected Resolution doDescribe(String name) {
            return new LazyResolution(this.typePool, name);
        }

        public void clear() {
            this.typePool.clear();
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
            return this.typePool.equals(((LazyFacade)object).typePool);
        }

        public int hashCode() {
            return super.hashCode() * 31 + this.typePool.hashCode();
        }

        protected static class LazyTypeDescription
        extends TypeDescription.AbstractBase.OfSimpleType.WithDelegation {
            private final TypePool typePool;
            private final String name;
            private transient /* synthetic */ TypeDescription delegate;

            protected LazyTypeDescription(TypePool typePool, String name) {
                this.typePool = typePool;
                this.name = name;
            }

            public String getName() {
                return this.name;
            }

            @CachedReturnPlugin.Enhance(value="delegate")
            protected TypeDescription delegate() {
                TypeDescription typeDescription;
                TypeDescription typeDescription2;
                TypeDescription typeDescription3 = this.delegate;
                if (typeDescription3 != null) {
                    typeDescription2 = null;
                } else {
                    typeDescription = this;
                    typeDescription2 = typeDescription = typeDescription.typePool.describe(typeDescription.name).resolve();
                }
                if (typeDescription == null) {
                    typeDescription = this.delegate;
                } else {
                    this.delegate = typeDescription;
                }
                return typeDescription;
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class LazyResolution
        implements Resolution {
            private final TypePool typePool;
            private final String name;

            protected LazyResolution(TypePool typePool, String name) {
                this.typePool = typePool;
                this.name = name;
            }

            public boolean isResolved() {
                return this.typePool.describe(this.name).isResolved();
            }

            public TypeDescription resolve() {
                return new LazyTypeDescription(this.typePool, this.name);
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
                if (!this.name.equals(((LazyResolution)object).name)) {
                    return false;
                }
                return this.typePool.equals(((LazyResolution)object).typePool);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.typePool.hashCode()) * 31 + this.name.hashCode();
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    extends AbstractBase.Hierarchical {
        @AlwaysNull
        private static final MethodVisitor IGNORE_METHOD = null;
        protected final ClassFileLocator classFileLocator;
        protected final ReaderMode readerMode;

        public Default(CacheProvider cacheProvider, ClassFileLocator classFileLocator, ReaderMode readerMode) {
            this(cacheProvider, classFileLocator, readerMode, Empty.INSTANCE);
        }

        public Default(CacheProvider cacheProvider, ClassFileLocator classFileLocator, ReaderMode readerMode, TypePool parentPool) {
            super(cacheProvider, parentPool);
            this.classFileLocator = classFileLocator;
            this.readerMode = readerMode;
        }

        public static TypePool ofSystemLoader() {
            return Default.of(ClassFileLocator.ForClassLoader.ofSystemLoader());
        }

        public static TypePool ofPlatformLoader() {
            return Default.of(ClassFileLocator.ForClassLoader.ofPlatformLoader());
        }

        public static TypePool ofBootLoader() {
            return Default.of(ClassFileLocator.ForClassLoader.ofBootLoader());
        }

        public static TypePool of(@MaybeNull ClassLoader classLoader) {
            return Default.of(ClassFileLocator.ForClassLoader.of(classLoader));
        }

        public static TypePool of(ClassFileLocator classFileLocator) {
            return new Default(new CacheProvider.Simple(), classFileLocator, ReaderMode.FAST);
        }

        protected Resolution doDescribe(String name) {
            try {
                ClassFileLocator.Resolution resolution = this.classFileLocator.locate(name);
                return resolution.isResolved() ? new Resolution.Simple(this.parse(resolution.resolve())) : new Resolution.Illegal(name);
            }
            catch (IOException exception) {
                throw new IllegalStateException("Error while reading class file", exception);
            }
        }

        private TypeDescription parse(byte[] binaryRepresentation) {
            ClassReader classReader = OpenedClassReader.of(binaryRepresentation);
            TypeExtractor typeExtractor = new TypeExtractor();
            classReader.accept(typeExtractor, this.readerMode.getFlags());
            return typeExtractor.toTypeDescription();
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
            if (!this.readerMode.equals((Object)((Default)object).readerMode)) {
                return false;
            }
            return this.classFileLocator.equals(((Default)object).classFileLocator);
        }

        public int hashCode() {
            return (super.hashCode() * 31 + this.classFileLocator.hashCode()) * 31 + this.readerMode.hashCode();
        }

        protected class TypeExtractor
        extends ClassVisitor {
            private static final int SUPER_CLASS_INDEX = -1;
            private static final int REAL_MODIFIER_MASK = 65535;
            private final Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> superTypeAnnotationTokens;
            private final Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> typeVariableAnnotationTokens;
            private final Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> typeVariableBoundsAnnotationTokens;
            private final List<LazyTypeDescription.AnnotationToken> annotationTokens;
            private final List<LazyTypeDescription.FieldToken> fieldTokens;
            private final List<LazyTypeDescription.MethodToken> methodTokens;
            private final List<LazyTypeDescription.RecordComponentToken> recordComponentTokens;
            private int actualModifiers;
            private int modifiers;
            @MaybeNull
            private String internalName;
            @MaybeNull
            private String superClassName;
            @MaybeNull
            private String genericSignature;
            @MaybeNull
            private String[] interfaceName;
            private boolean anonymousType;
            @MaybeNull
            private String nestHost;
            private final List<String> nestMembers;
            private LazyTypeDescription.TypeContainment typeContainment;
            @MaybeNull
            private String declaringTypeName;
            private final List<String> declaredTypes;
            private final List<String> permittedSubclasses;
            @MaybeNull
            private ClassFileVersion classFileVersion;

            protected TypeExtractor() {
                super(OpenedClassReader.ASM_API);
                this.superTypeAnnotationTokens = new HashMap<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>();
                this.typeVariableAnnotationTokens = new HashMap<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>();
                this.typeVariableBoundsAnnotationTokens = new HashMap<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>>();
                this.annotationTokens = new ArrayList<LazyTypeDescription.AnnotationToken>();
                this.fieldTokens = new ArrayList<LazyTypeDescription.FieldToken>();
                this.methodTokens = new ArrayList<LazyTypeDescription.MethodToken>();
                this.recordComponentTokens = new ArrayList<LazyTypeDescription.RecordComponentToken>();
                this.anonymousType = false;
                this.typeContainment = LazyTypeDescription.TypeContainment.SelfContained.INSTANCE;
                this.nestMembers = new ArrayList<String>();
                this.declaredTypes = new ArrayList<String>();
                this.permittedSubclasses = new ArrayList<String>();
            }

            @SuppressFBWarnings(value={"EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
            public void visit(int classFileVersion, int modifiers, String internalName, @MaybeNull String genericSignature, @MaybeNull String superClassName, @MaybeNull String[] interfaceName) {
                this.modifiers = modifiers & 0xFFFF;
                this.actualModifiers = modifiers;
                this.internalName = internalName;
                this.genericSignature = genericSignature;
                this.superClassName = superClassName;
                this.interfaceName = interfaceName;
                this.classFileVersion = ClassFileVersion.ofMinorMajor(classFileVersion);
            }

            public void visitOuterClass(@MaybeNull String typeName, @MaybeNull String methodName, String methodDescriptor) {
                if (methodName != null && !methodName.equals("<clinit>")) {
                    this.typeContainment = new LazyTypeDescription.TypeContainment.WithinMethod(typeName, methodName, methodDescriptor);
                } else if (typeName != null) {
                    this.typeContainment = new LazyTypeDescription.TypeContainment.WithinType(typeName, true);
                }
            }

            public void visitInnerClass(String internalName, @MaybeNull String outerName, @MaybeNull String innerName, int modifiers) {
                if (internalName.equals(this.internalName)) {
                    if (outerName != null) {
                        this.declaringTypeName = outerName;
                        if (this.typeContainment.isSelfContained()) {
                            this.typeContainment = new LazyTypeDescription.TypeContainment.WithinType(outerName, false);
                        }
                    }
                    if (innerName == null && !this.typeContainment.isSelfContained()) {
                        this.anonymousType = true;
                    }
                    this.modifiers = modifiers & 0xFFFF;
                } else if (outerName != null && innerName != null && outerName.equals(this.internalName)) {
                    this.declaredTypes.add("L" + internalName + ";");
                }
            }

            public AnnotationVisitor visitTypeAnnotation(int rawTypeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                AnnotationRegistrant.AbstractBase.ForTypeVariable.WithIndex annotationRegistrant;
                TypeReference typeReference = new TypeReference(rawTypeReference);
                switch (typeReference.getSort()) {
                    case 16: {
                        annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex(descriptor, typePath, typeReference.getSuperTypeIndex(), this.superTypeAnnotationTokens);
                        break;
                    }
                    case 0: {
                        annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex(descriptor, typePath, typeReference.getTypeParameterIndex(), this.typeVariableAnnotationTokens);
                        break;
                    }
                    case 17: {
                        annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex.DoubleIndexed(descriptor, typePath, typeReference.getTypeParameterBoundIndex(), typeReference.getTypeParameterIndex(), this.typeVariableBoundsAnnotationTokens);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unexpected type reference: " + typeReference.getSort());
                    }
                }
                return new AnnotationExtractor(annotationRegistrant, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
            }

            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return new AnnotationExtractor(descriptor, this.annotationTokens, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
            }

            public FieldVisitor visitField(int modifiers, String internalName, String descriptor, @MaybeNull String genericSignature, @MaybeNull Object value) {
                return new FieldExtractor(modifiers & 0xFFFF, internalName, descriptor, genericSignature);
            }

            @MaybeNull
            public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String genericSignature, @MaybeNull String[] exceptionName) {
                return internalName.equals("<clinit>") ? IGNORE_METHOD : new MethodExtractor(modifiers & 0xFFFF, internalName, descriptor, genericSignature, exceptionName);
            }

            public void visitNestHost(String nestHost) {
                this.nestHost = nestHost;
            }

            public void visitNestMember(String nestMember) {
                this.nestMembers.add(nestMember);
            }

            public RecordComponentVisitor visitRecordComponent(String name, String descriptor, @MaybeNull String signature) {
                return new RecordComponentExtractor(name, descriptor, signature);
            }

            public void visitPermittedSubclass(String permittedSubclass) {
                this.permittedSubclasses.add(permittedSubclass);
            }

            protected TypeDescription toTypeDescription() {
                if (this.internalName == null || this.classFileVersion == null) {
                    throw new IllegalStateException("Internal name or class file version were not set");
                }
                Map<String, List<LazyTypeDescription.AnnotationToken>> superClassAnnotationTokens = this.superTypeAnnotationTokens.remove(-1);
                return new LazyTypeDescription(Default.this, this.actualModifiers, this.modifiers, this.internalName, this.superClassName, this.interfaceName, this.genericSignature, this.typeContainment, this.declaringTypeName, this.declaredTypes, this.anonymousType, this.nestHost, this.nestMembers, superClassAnnotationTokens == null ? Collections.emptyMap() : superClassAnnotationTokens, this.superTypeAnnotationTokens, this.typeVariableAnnotationTokens, this.typeVariableBoundsAnnotationTokens, this.annotationTokens, this.fieldTokens, this.methodTokens, this.recordComponentTokens, this.permittedSubclasses, this.classFileVersion);
            }

            protected class RecordComponentExtractor
            extends RecordComponentVisitor {
                private final String name;
                private final String descriptor;
                @MaybeNull
                private final String genericSignature;
                private final Map<String, List<LazyTypeDescription.AnnotationToken>> typeAnnotationTokens;
                private final List<LazyTypeDescription.AnnotationToken> annotationTokens;

                protected RecordComponentExtractor(String name, @MaybeNull String descriptor, String genericSignature) {
                    super(OpenedClassReader.ASM_API);
                    this.name = name;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.typeAnnotationTokens = new HashMap<String, List<LazyTypeDescription.AnnotationToken>>();
                    this.annotationTokens = new ArrayList<LazyTypeDescription.AnnotationToken>();
                }

                public AnnotationVisitor visitTypeAnnotation(int rawTypeReference, TypePath typePath, String descriptor, boolean visible) {
                    AnnotationRegistrant.ForTypeVariable annotationRegistrant;
                    TypeReference typeReference = new TypeReference(rawTypeReference);
                    switch (typeReference.getSort()) {
                        case 19: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable(descriptor, typePath, this.typeAnnotationTokens);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected type reference on record component: " + typeReference.getSort());
                        }
                    }
                    return new AnnotationExtractor(annotationRegistrant, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return new AnnotationExtractor(descriptor, this.annotationTokens, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                public void visitEnd() {
                    TypeExtractor.this.recordComponentTokens.add(new LazyTypeDescription.RecordComponentToken(this.name, this.descriptor, this.genericSignature, this.typeAnnotationTokens, this.annotationTokens));
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class MethodExtractor
            extends MethodVisitor
            implements AnnotationRegistrant {
                private final int modifiers;
                private final String internalName;
                private final String descriptor;
                @MaybeNull
                private final String genericSignature;
                @MaybeNull
                private final String[] exceptionName;
                private final Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> typeVariableAnnotationTokens;
                private final Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> typeVariableBoundAnnotationTokens;
                private final Map<String, List<LazyTypeDescription.AnnotationToken>> returnTypeAnnotationTokens;
                private final Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> parameterTypeAnnotationTokens;
                private final Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> exceptionTypeAnnotationTokens;
                private final Map<String, List<LazyTypeDescription.AnnotationToken>> receiverTypeAnnotationTokens;
                private final List<LazyTypeDescription.AnnotationToken> annotationTokens;
                private final Map<Integer, List<LazyTypeDescription.AnnotationToken>> parameterAnnotationTokens;
                private final List<LazyTypeDescription.MethodToken.ParameterToken> parameterTokens;
                private final ParameterBag legacyParameterBag;
                @MaybeNull
                private Label firstLabel;
                private int visibleParameterShift;
                private int invisibleParameterShift;
                @MaybeNull
                private AnnotationValue<?, ?> defaultValue;

                protected MethodExtractor(int modifiers, String internalName, @MaybeNull String descriptor, @MaybeNull String genericSignature, String[] exceptionName) {
                    super(OpenedClassReader.ASM_API);
                    this.modifiers = modifiers;
                    this.internalName = internalName;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.exceptionName = exceptionName;
                    this.typeVariableAnnotationTokens = new HashMap<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>();
                    this.typeVariableBoundAnnotationTokens = new HashMap<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>>();
                    this.returnTypeAnnotationTokens = new HashMap<String, List<LazyTypeDescription.AnnotationToken>>();
                    this.parameterTypeAnnotationTokens = new HashMap<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>();
                    this.exceptionTypeAnnotationTokens = new HashMap<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>();
                    this.receiverTypeAnnotationTokens = new HashMap<String, List<LazyTypeDescription.AnnotationToken>>();
                    this.annotationTokens = new ArrayList<LazyTypeDescription.AnnotationToken>();
                    this.parameterAnnotationTokens = new HashMap<Integer, List<LazyTypeDescription.AnnotationToken>>();
                    this.parameterTokens = new ArrayList<LazyTypeDescription.MethodToken.ParameterToken>();
                    this.legacyParameterBag = new ParameterBag(Type.getMethodType(descriptor).getArgumentTypes());
                }

                @Override
                @MaybeNull
                public AnnotationVisitor visitTypeAnnotation(int rawTypeReference, TypePath typePath, String descriptor, boolean visible) {
                    AnnotationRegistrant.AbstractBase.ForTypeVariable annotationRegistrant;
                    TypeReference typeReference = new TypeReference(rawTypeReference);
                    switch (typeReference.getSort()) {
                        case 1: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex(descriptor, typePath, typeReference.getTypeParameterIndex(), this.typeVariableAnnotationTokens);
                            break;
                        }
                        case 18: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex.DoubleIndexed(descriptor, typePath, typeReference.getTypeParameterBoundIndex(), typeReference.getTypeParameterIndex(), this.typeVariableBoundAnnotationTokens);
                            break;
                        }
                        case 20: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable(descriptor, typePath, this.returnTypeAnnotationTokens);
                            break;
                        }
                        case 22: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex(descriptor, typePath, typeReference.getFormalParameterIndex(), this.parameterTypeAnnotationTokens);
                            break;
                        }
                        case 23: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable.WithIndex(descriptor, typePath, typeReference.getExceptionIndex(), this.exceptionTypeAnnotationTokens);
                            break;
                        }
                        case 21: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable(descriptor, typePath, this.receiverTypeAnnotationTokens);
                            break;
                        }
                        case 19: {
                            return null;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected type reference on method: " + typeReference.getSort());
                        }
                    }
                    return new AnnotationExtractor(annotationRegistrant, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return new AnnotationExtractor(descriptor, this.annotationTokens, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                @Override
                public void visitAnnotableParameterCount(int count, boolean visible) {
                    if (visible) {
                        this.visibleParameterShift = Type.getMethodType(this.descriptor).getArgumentTypes().length - count;
                    } else {
                        this.invisibleParameterShift = Type.getMethodType(this.descriptor).getArgumentTypes().length - count;
                    }
                }

                @Override
                public AnnotationVisitor visitParameterAnnotation(int index, String descriptor, boolean visible) {
                    return new AnnotationExtractor(descriptor, index + (visible ? this.visibleParameterShift : this.invisibleParameterShift), this.parameterAnnotationTokens, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                @Override
                public void visitLabel(Label label) {
                    if (Default.this.readerMode.isExtended() && this.firstLabel == null) {
                        this.firstLabel = label;
                    }
                }

                @Override
                public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int offset) {
                    if (Default.this.readerMode.isExtended() && start == this.firstLabel) {
                        this.legacyParameterBag.register(offset, name);
                    }
                }

                @Override
                public void visitParameter(String name, int modifiers) {
                    this.parameterTokens.add(new LazyTypeDescription.MethodToken.ParameterToken(name, modifiers));
                }

                @Override
                public AnnotationVisitor visitAnnotationDefault() {
                    return new AnnotationExtractor(this, new ComponentTypeLocator.ForArrayType(this.descriptor));
                }

                @Override
                public void register(String ignored, AnnotationValue<?, ?> annotationValue) {
                    this.defaultValue = annotationValue;
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void visitEnd() {
                    TypeExtractor.this.methodTokens.add(new LazyTypeDescription.MethodToken(this.internalName, this.modifiers, this.descriptor, this.genericSignature, this.exceptionName, this.typeVariableAnnotationTokens, this.typeVariableBoundAnnotationTokens, this.returnTypeAnnotationTokens, this.parameterTypeAnnotationTokens, this.exceptionTypeAnnotationTokens, this.receiverTypeAnnotationTokens, this.annotationTokens, this.parameterAnnotationTokens, this.parameterTokens.isEmpty() ? this.legacyParameterBag.resolve((this.modifiers & 8) != 0) : this.parameterTokens, this.defaultValue));
                }
            }

            protected class FieldExtractor
            extends FieldVisitor {
                private final int modifiers;
                private final String internalName;
                private final String descriptor;
                @MaybeNull
                private final String genericSignature;
                private final Map<String, List<LazyTypeDescription.AnnotationToken>> typeAnnotationTokens;
                private final List<LazyTypeDescription.AnnotationToken> annotationTokens;

                protected FieldExtractor(int modifiers, String internalName, @MaybeNull String descriptor, String genericSignature) {
                    super(OpenedClassReader.ASM_API);
                    this.modifiers = modifiers;
                    this.internalName = internalName;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.typeAnnotationTokens = new HashMap<String, List<LazyTypeDescription.AnnotationToken>>();
                    this.annotationTokens = new ArrayList<LazyTypeDescription.AnnotationToken>();
                }

                @MaybeNull
                public AnnotationVisitor visitTypeAnnotation(int rawTypeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                    AnnotationRegistrant.ForTypeVariable annotationRegistrant;
                    TypeReference typeReference = new TypeReference(rawTypeReference);
                    switch (typeReference.getSort()) {
                        case 19: {
                            annotationRegistrant = new AnnotationRegistrant.ForTypeVariable(descriptor, typePath, this.typeAnnotationTokens);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected type reference on field: " + typeReference.getSort());
                        }
                    }
                    return new AnnotationExtractor(annotationRegistrant, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return new AnnotationExtractor(descriptor, this.annotationTokens, new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                public void visitEnd() {
                    TypeExtractor.this.fieldTokens.add(new LazyTypeDescription.FieldToken(this.internalName, this.modifiers, this.descriptor, this.genericSignature, this.typeAnnotationTokens, this.annotationTokens));
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class AnnotationExtractor
            extends AnnotationVisitor {
                private final AnnotationRegistrant annotationRegistrant;
                private final ComponentTypeLocator componentTypeLocator;

                protected AnnotationExtractor(String descriptor, List<LazyTypeDescription.AnnotationToken> annotationTokens, ComponentTypeLocator componentTypeLocator) {
                    this(new AnnotationRegistrant.ForByteCodeElement(descriptor, annotationTokens), componentTypeLocator);
                }

                protected AnnotationExtractor(String descriptor, int index, Map<Integer, List<LazyTypeDescription.AnnotationToken>> annotationTokens, ComponentTypeLocator componentTypeLocator) {
                    this(new AnnotationRegistrant.ForByteCodeElement.WithIndex(descriptor, index, annotationTokens), componentTypeLocator);
                }

                protected AnnotationExtractor(AnnotationRegistrant annotationRegistrant, ComponentTypeLocator componentTypeLocator) {
                    super(OpenedClassReader.ASM_API);
                    this.annotationRegistrant = annotationRegistrant;
                    this.componentTypeLocator = componentTypeLocator;
                }

                @Override
                public void visit(String name, Object value) {
                    if (value instanceof Type) {
                        Type type = (Type)value;
                        this.annotationRegistrant.register(name, new LazyTypeDescription.LazyAnnotationValue.ForTypeValue(Default.this, type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()));
                    } else {
                        this.annotationRegistrant.register(name, AnnotationValue.ForConstant.of(value));
                    }
                }

                @Override
                public void visitEnum(String name, String descriptor, String value) {
                    this.annotationRegistrant.register(name, new LazyTypeDescription.LazyAnnotationValue.ForEnumerationValue(Default.this, descriptor.substring(1, descriptor.length() - 1).replace('/', '.'), value));
                }

                @Override
                public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                    return new AnnotationExtractor(new AnnotationLookup(descriptor, name), new ComponentTypeLocator.ForAnnotationProperty(Default.this, descriptor));
                }

                @Override
                public AnnotationVisitor visitArray(String name) {
                    return new AnnotationExtractor(new ArrayLookup(name, this.componentTypeLocator.bind(name)), ComponentTypeLocator.Illegal.INSTANCE);
                }

                @Override
                public void visitEnd() {
                    this.annotationRegistrant.onComplete();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected class AnnotationLookup
                implements AnnotationRegistrant {
                    private final String descriptor;
                    private final String name;
                    private final Map<String, AnnotationValue<?, ?>> values;

                    protected AnnotationLookup(String descriptor, String name) {
                        this.descriptor = descriptor;
                        this.name = name;
                        this.values = new HashMap();
                    }

                    @Override
                    public void register(String name, AnnotationValue<?, ?> annotationValue) {
                        this.values.put(name, annotationValue);
                    }

                    @Override
                    public void onComplete() {
                        AnnotationExtractor.this.annotationRegistrant.register(this.name, new LazyTypeDescription.LazyAnnotationValue.ForAnnotationValue(Default.this, new LazyTypeDescription.AnnotationToken(this.descriptor, this.values)));
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected class ArrayLookup
                implements AnnotationRegistrant {
                    private final String name;
                    private final AbstractBase.ComponentTypeReference componentTypeReference;
                    private final List<AnnotationValue<?, ?>> values;

                    private ArrayLookup(String name, AbstractBase.ComponentTypeReference componentTypeReference) {
                        this.name = name;
                        this.componentTypeReference = componentTypeReference;
                        this.values = new ArrayList();
                    }

                    @Override
                    public void register(String ignored, AnnotationValue<?, ?> annotationValue) {
                        this.values.add(annotationValue);
                    }

                    @Override
                    public void onComplete() {
                        AnnotationExtractor.this.annotationRegistrant.register(this.name, new LazyTypeDescription.LazyAnnotationValue.ForArray(Default.this, this.componentTypeReference, this.values));
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class LazyTypeDescription
        extends TypeDescription.AbstractBase.OfSimpleType {
            @AlwaysNull
            private static final String NO_TYPE = null;
            private final TypePool typePool;
            private final int actualModifiers;
            private final int modifiers;
            private final String name;
            @MaybeNull
            private final String superClassDescriptor;
            @MaybeNull
            private final String genericSignature;
            private final GenericTypeToken.Resolution.ForType signatureResolution;
            private final List<String> interfaceTypeDescriptors;
            private final TypeContainment typeContainment;
            @MaybeNull
            private final String declaringTypeName;
            private final List<String> declaredTypes;
            private final boolean anonymousType;
            @MaybeNull
            private final String nestHost;
            private final List<String> nestMembers;
            private final Map<String, List<AnnotationToken>> superClassAnnotationTokens;
            private final Map<Integer, Map<String, List<AnnotationToken>>> interfaceAnnotationTokens;
            private final Map<Integer, Map<String, List<AnnotationToken>>> typeVariableAnnotationTokens;
            private final Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> typeVariableBoundsAnnotationTokens;
            private final List<AnnotationToken> annotationTokens;
            private final List<FieldToken> fieldTokens;
            private final List<MethodToken> methodTokens;
            private final List<RecordComponentToken> recordComponentTokens;
            private final List<String> permittedSubclasses;
            private final ClassFileVersion classFileVersion;

            protected LazyTypeDescription(TypePool typePool, int actualModifiers, int modifiers, String name, @MaybeNull String superClassInternalName, @MaybeNull String[] interfaceInternalName, @MaybeNull String genericSignature, TypeContainment typeContainment, @MaybeNull String declaringTypeInternalName, List<String> declaredTypes, boolean anonymousType, @MaybeNull String nestHostInternalName, List<String> nestMemberInternalNames, Map<String, List<AnnotationToken>> superClassAnnotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> interfaceAnnotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> typeVariableAnnotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> typeVariableBoundsAnnotationTokens, List<AnnotationToken> annotationTokens, List<FieldToken> fieldTokens, List<MethodToken> methodTokens, List<RecordComponentToken> recordComponentTokens, List<String> permittedSubclasses, ClassFileVersion classFileVersion) {
                this.typePool = typePool;
                this.actualModifiers = actualModifiers & 0xFFFFFFDF;
                this.modifiers = modifiers & 0xFFFDFFDF;
                this.name = Type.getObjectType(name).getClassName();
                this.superClassDescriptor = superClassInternalName == null ? NO_TYPE : Type.getObjectType(superClassInternalName).getDescriptor();
                this.genericSignature = genericSignature;
                GenericTypeToken.Resolution.ForType forType = this.signatureResolution = RAW_TYPES ? GenericTypeToken.Resolution.Raw.INSTANCE : GenericTypeExtractor.ForSignature.OfType.extract(genericSignature);
                if (interfaceInternalName == null) {
                    this.interfaceTypeDescriptors = Collections.emptyList();
                } else {
                    this.interfaceTypeDescriptors = new ArrayList<String>(interfaceInternalName.length);
                    for (String internalName : interfaceInternalName) {
                        this.interfaceTypeDescriptors.add(Type.getObjectType(internalName).getDescriptor());
                    }
                }
                this.typeContainment = typeContainment;
                this.declaringTypeName = declaringTypeInternalName == null ? NO_TYPE : declaringTypeInternalName.replace('/', '.');
                this.declaredTypes = declaredTypes;
                this.anonymousType = anonymousType;
                this.nestHost = nestHostInternalName == null ? NO_TYPE : Type.getObjectType(nestHostInternalName).getClassName();
                this.nestMembers = new ArrayList<String>(nestMemberInternalNames.size());
                for (String nestMemberInternalName : nestMemberInternalNames) {
                    this.nestMembers.add(Type.getObjectType(nestMemberInternalName).getClassName());
                }
                this.superClassAnnotationTokens = superClassAnnotationTokens;
                this.interfaceAnnotationTokens = interfaceAnnotationTokens;
                this.typeVariableAnnotationTokens = typeVariableAnnotationTokens;
                this.typeVariableBoundsAnnotationTokens = typeVariableBoundsAnnotationTokens;
                this.annotationTokens = annotationTokens;
                this.fieldTokens = fieldTokens;
                this.methodTokens = methodTokens;
                this.recordComponentTokens = recordComponentTokens;
                this.permittedSubclasses = new ArrayList<String>(permittedSubclasses.size());
                for (String internalName : permittedSubclasses) {
                    this.permittedSubclasses.add(Type.getObjectType(internalName).getDescriptor());
                }
                this.classFileVersion = classFileVersion;
            }

            @Override
            @MaybeNull
            public TypeDescription.Generic getSuperClass() {
                return this.superClassDescriptor == null || this.isInterface() ? TypeDescription.Generic.UNDEFINED : this.signatureResolution.resolveSuperClass(this.superClassDescriptor, this.typePool, this.superClassAnnotationTokens, this);
            }

            @Override
            public TypeList.Generic getInterfaces() {
                return this.signatureResolution.resolveInterfaceTypes(this.interfaceTypeDescriptors, this.typePool, this.interfaceAnnotationTokens, this);
            }

            @Override
            @MaybeNull
            public MethodDescription.InDefinedShape getEnclosingMethod() {
                return this.typeContainment.getEnclosingMethod(this.typePool);
            }

            @Override
            @MaybeNull
            public TypeDescription getEnclosingType() {
                return this.typeContainment.getEnclosingType(this.typePool);
            }

            @Override
            public TypeList getDeclaredTypes() {
                return new LazyTypeList(this.typePool, this.declaredTypes);
            }

            @Override
            public boolean isAnonymousType() {
                return this.anonymousType;
            }

            @Override
            public boolean isLocalType() {
                return !this.anonymousType && this.typeContainment.isLocalType();
            }

            @Override
            public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
                return new FieldTokenList();
            }

            @Override
            public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
                return new MethodTokenList();
            }

            @Override
            @MaybeNull
            public PackageDescription getPackage() {
                String name = this.getName();
                int index = name.lastIndexOf(46);
                return new LazyPackageDescription(this.typePool, index == -1 ? "" : name.substring(0, index));
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            @MaybeNull
            public TypeDescription getDeclaringType() {
                return this.declaringTypeName == null ? TypeDescription.UNDEFINED : this.typePool.describe(this.declaringTypeName).resolve();
            }

            @Override
            public int getModifiers() {
                return this.modifiers;
            }

            @Override
            public int getActualModifiers(boolean superFlag) {
                return superFlag ? this.actualModifiers | 0x20 : this.actualModifiers;
            }

            @Override
            public TypeDescription getNestHost() {
                return this.nestHost == null ? this : this.typePool.describe(this.nestHost).resolve();
            }

            @Override
            public TypeList getNestMembers() {
                return this.nestHost == null ? new LazyNestMemberList(this, this.typePool, this.nestMembers) : this.typePool.describe(this.nestHost).resolve().getNestMembers();
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                return LazyAnnotationDescription.asList(this.typePool, this.annotationTokens);
            }

            @Override
            public TypeList.Generic getTypeVariables() {
                return this.signatureResolution.resolveTypeVariables(this.typePool, this, this.typeVariableAnnotationTokens, this.typeVariableBoundsAnnotationTokens);
            }

            @Override
            @MaybeNull
            public String getGenericSignature() {
                return this.genericSignature;
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
                return new RecordComponentTokenList();
            }

            @Override
            public boolean isRecord() {
                return (this.actualModifiers & 0x10000) != 0 && JavaType.RECORD.getTypeStub().getDescriptor().equals(this.superClassDescriptor);
            }

            @Override
            public boolean isSealed() {
                return !this.permittedSubclasses.isEmpty();
            }

            @Override
            public TypeList getPermittedSubtypes() {
                return new LazyTypeList(this.typePool, this.permittedSubclasses);
            }

            @Override
            public ClassFileVersion getClassFileVersion() {
                return this.classFileVersion;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private class LazyRecordComponentDescription
            extends RecordComponentDescription.InDefinedShape.AbstractBase {
                private final String name;
                private final String descriptor;
                @MaybeNull
                private final String genericSignature;
                private final GenericTypeToken.Resolution.ForRecordComponent signatureResolution;
                private final Map<String, List<AnnotationToken>> typeAnnotationTokens;
                private final List<AnnotationToken> annotationTokens;

                private LazyRecordComponentDescription(String name, @MaybeNull String descriptor, String genericSignature, GenericTypeToken.Resolution.ForRecordComponent signatureResolution, Map<String, List<AnnotationToken>> typeAnnotationTokens, List<AnnotationToken> annotationTokens) {
                    this.name = name;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.signatureResolution = signatureResolution;
                    this.typeAnnotationTokens = typeAnnotationTokens;
                    this.annotationTokens = annotationTokens;
                }

                @Override
                public TypeDescription.Generic getType() {
                    return this.signatureResolution.resolveRecordType(this.descriptor, LazyTypeDescription.this.typePool, this.typeAnnotationTokens, this);
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return LazyTypeDescription.this;
                }

                @Override
                public String getActualName() {
                    return this.name;
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return LazyAnnotationDescription.asList(LazyTypeDescription.this.typePool, this.annotationTokens);
                }

                @Override
                @MaybeNull
                public String getGenericSignature() {
                    return this.genericSignature;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private class LazyMethodDescription
            extends MethodDescription.InDefinedShape.AbstractBase {
                private final String internalName;
                private final int modifiers;
                private final String returnTypeDescriptor;
                @MaybeNull
                private final String genericSignature;
                private final GenericTypeToken.Resolution.ForMethod signatureResolution;
                private final List<String> parameterTypeDescriptors;
                private final List<String> exceptionTypeDescriptors;
                private final Map<Integer, Map<String, List<AnnotationToken>>> typeVariableAnnotationTokens;
                private final Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> typeVariableBoundAnnotationTokens;
                private final Map<String, List<AnnotationToken>> returnTypeAnnotationTokens;
                private final Map<Integer, Map<String, List<AnnotationToken>>> parameterTypeAnnotationTokens;
                private final Map<Integer, Map<String, List<AnnotationToken>>> exceptionTypeAnnotationTokens;
                private final Map<String, List<AnnotationToken>> receiverTypeAnnotationTokens;
                private final List<AnnotationToken> annotationTokens;
                private final Map<Integer, List<AnnotationToken>> parameterAnnotationTokens;
                private final String[] parameterNames;
                private final Integer[] parameterModifiers;
                @MaybeNull
                private final AnnotationValue<?, ?> defaultValue;

                private LazyMethodDescription(String internalName, int modifiers, @MaybeNull String descriptor, String genericSignature, @MaybeNull GenericTypeToken.Resolution.ForMethod signatureResolution, String[] exceptionTypeInternalName, Map<Integer, Map<String, List<AnnotationToken>>> typeVariableAnnotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> typeVariableBoundAnnotationTokens, Map<String, List<AnnotationToken>> returnTypeAnnotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> parameterTypeAnnotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> exceptionTypeAnnotationTokens, Map<String, List<AnnotationToken>> receiverTypeAnnotationTokens, List<AnnotationToken> annotationTokens, Map<Integer, List<AnnotationToken>> parameterAnnotationTokens, @MaybeNull List<MethodToken.ParameterToken> parameterTokens, AnnotationValue<?, ?> defaultValue) {
                    this.modifiers = modifiers;
                    this.internalName = internalName;
                    Type methodType = Type.getMethodType(descriptor);
                    Type returnType = methodType.getReturnType();
                    Type[] parameterType = methodType.getArgumentTypes();
                    this.returnTypeDescriptor = returnType.getDescriptor();
                    this.parameterTypeDescriptors = new ArrayList<String>(parameterType.length);
                    for (Type type : parameterType) {
                        this.parameterTypeDescriptors.add(type.getDescriptor());
                    }
                    this.genericSignature = genericSignature;
                    this.signatureResolution = signatureResolution;
                    if (exceptionTypeInternalName == null) {
                        this.exceptionTypeDescriptors = Collections.emptyList();
                    } else {
                        this.exceptionTypeDescriptors = new ArrayList<String>(exceptionTypeInternalName.length);
                        for (String anExceptionTypeInternalName : exceptionTypeInternalName) {
                            this.exceptionTypeDescriptors.add(Type.getObjectType(anExceptionTypeInternalName).getDescriptor());
                        }
                    }
                    this.typeVariableAnnotationTokens = typeVariableAnnotationTokens;
                    this.typeVariableBoundAnnotationTokens = typeVariableBoundAnnotationTokens;
                    this.returnTypeAnnotationTokens = returnTypeAnnotationTokens;
                    this.parameterTypeAnnotationTokens = parameterTypeAnnotationTokens;
                    this.exceptionTypeAnnotationTokens = exceptionTypeAnnotationTokens;
                    this.receiverTypeAnnotationTokens = receiverTypeAnnotationTokens;
                    this.annotationTokens = annotationTokens;
                    this.parameterAnnotationTokens = parameterAnnotationTokens;
                    this.parameterNames = new String[parameterType.length];
                    this.parameterModifiers = new Integer[parameterType.length];
                    if (parameterTokens.size() == parameterType.length) {
                        int index = 0;
                        for (MethodToken.ParameterToken parameterToken : parameterTokens) {
                            this.parameterNames[index] = parameterToken.getName();
                            this.parameterModifiers[index] = parameterToken.getModifiers();
                            ++index;
                        }
                    }
                    this.defaultValue = defaultValue;
                }

                @Override
                public TypeDescription.Generic getReturnType() {
                    return this.signatureResolution.resolveReturnType(this.returnTypeDescriptor, LazyTypeDescription.this.typePool, this.returnTypeAnnotationTokens, this);
                }

                @Override
                public TypeList.Generic getExceptionTypes() {
                    return this.signatureResolution.resolveExceptionTypes(this.exceptionTypeDescriptors, LazyTypeDescription.this.typePool, this.exceptionTypeAnnotationTokens, this);
                }

                @Override
                public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                    return new LazyParameterList();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return LazyAnnotationDescription.asList(LazyTypeDescription.this.typePool, this.annotationTokens);
                }

                @Override
                public String getInternalName() {
                    return this.internalName;
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return LazyTypeDescription.this;
                }

                @Override
                public int getModifiers() {
                    return this.modifiers;
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return this.signatureResolution.resolveTypeVariables(LazyTypeDescription.this.typePool, this, this.typeVariableAnnotationTokens, this.typeVariableBoundAnnotationTokens);
                }

                @Override
                @MaybeNull
                public AnnotationValue<?, ?> getDefaultValue() {
                    return this.defaultValue;
                }

                @Override
                @MaybeNull
                public TypeDescription.Generic getReceiverType() {
                    if (this.isStatic()) {
                        return TypeDescription.Generic.UNDEFINED;
                    }
                    if (this.isConstructor()) {
                        TypeDescription declaringType = this.getDeclaringType();
                        TypeDescription enclosingDeclaringType = declaringType.getEnclosingType();
                        if (enclosingDeclaringType == null) {
                            return declaringType.isGenerified() ? new LazyParameterizedReceiverType(declaringType) : new LazyNonGenericReceiverType(declaringType);
                        }
                        return !declaringType.isStatic() && declaringType.isGenerified() ? new LazyParameterizedReceiverType(enclosingDeclaringType) : new LazyNonGenericReceiverType(enclosingDeclaringType);
                    }
                    return LazyTypeDescription.this.isGenerified() ? new LazyParameterizedReceiverType() : new LazyNonGenericReceiverType();
                }

                @Override
                @MaybeNull
                public String getGenericSignature() {
                    return this.genericSignature;
                }

                protected class LazyNonGenericReceiverType
                extends TypeDescription.Generic.OfNonGenericType {
                    private final TypeDescription typeDescription;

                    protected LazyNonGenericReceiverType() {
                        this(this$1.LazyTypeDescription.this);
                    }

                    protected LazyNonGenericReceiverType(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    @MaybeNull
                    public TypeDescription.Generic getOwnerType() {
                        TypeDescription declaringType = this.typeDescription.getDeclaringType();
                        return declaringType == null ? TypeDescription.Generic.UNDEFINED : new LazyNonGenericReceiverType(declaringType);
                    }

                    @MaybeNull
                    public TypeDescription.Generic getComponentType() {
                        return TypeDescription.Generic.UNDEFINED;
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        StringBuilder typePath = new StringBuilder();
                        for (int index = 0; index < this.typeDescription.getInnerClassCount(); ++index) {
                            typePath = typePath.append('.');
                        }
                        return LazyAnnotationDescription.asListOfNullable(LazyTypeDescription.this.typePool, (List)LazyMethodDescription.this.receiverTypeAnnotationTokens.get(typePath.toString()));
                    }

                    public TypeDescription asErasure() {
                        return this.typeDescription;
                    }
                }

                private class LazyParameterizedReceiverType
                extends TypeDescription.Generic.OfParameterizedType {
                    private final TypeDescription typeDescription;

                    protected LazyParameterizedReceiverType() {
                        this(lazyMethodDescription.LazyTypeDescription.this);
                    }

                    protected LazyParameterizedReceiverType(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    public TypeList.Generic getTypeArguments() {
                        return new TypeArgumentList(this.typeDescription.getTypeVariables());
                    }

                    @MaybeNull
                    public TypeDescription.Generic getOwnerType() {
                        TypeDescription declaringType = this.typeDescription.getDeclaringType();
                        if (declaringType == null) {
                            return TypeDescription.Generic.UNDEFINED;
                        }
                        return !this.typeDescription.isStatic() && declaringType.isGenerified() ? new LazyParameterizedReceiverType(declaringType) : new LazyNonGenericReceiverType(declaringType);
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        return LazyAnnotationDescription.asListOfNullable(LazyTypeDescription.this.typePool, (List)LazyMethodDescription.this.receiverTypeAnnotationTokens.get(this.getTypePath()));
                    }

                    private String getTypePath() {
                        StringBuilder typePath = new StringBuilder();
                        for (int index = 0; index < this.typeDescription.getInnerClassCount(); ++index) {
                            typePath = typePath.append('.');
                        }
                        return typePath.toString();
                    }

                    public TypeDescription asErasure() {
                        return this.typeDescription;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected class TypeArgumentList
                    extends TypeList.Generic.AbstractBase {
                        private final List<? extends TypeDescription.Generic> typeVariables;

                        protected TypeArgumentList(List<? extends TypeDescription.Generic> typeVariables) {
                            this.typeVariables = typeVariables;
                        }

                        @Override
                        public TypeDescription.Generic get(int index) {
                            return new AnnotatedTypeVariable(this.typeVariables.get(index), index);
                        }

                        @Override
                        public int size() {
                            return this.typeVariables.size();
                        }

                        protected class AnnotatedTypeVariable
                        extends TypeDescription.Generic.OfTypeVariable {
                            private final TypeDescription.Generic typeVariable;
                            private final int index;

                            protected AnnotatedTypeVariable(TypeDescription.Generic typeVariable, int index) {
                                this.typeVariable = typeVariable;
                                this.index = index;
                            }

                            public TypeList.Generic getUpperBounds() {
                                return this.typeVariable.getUpperBounds();
                            }

                            public TypeVariableSource getTypeVariableSource() {
                                return this.typeVariable.getTypeVariableSource();
                            }

                            public String getSymbol() {
                                return this.typeVariable.getSymbol();
                            }

                            public AnnotationList getDeclaredAnnotations() {
                                return LazyAnnotationDescription.asListOfNullable(LazyTypeDescription.this.typePool, (List)LazyMethodDescription.this.receiverTypeAnnotationTokens.get(LazyParameterizedReceiverType.this.getTypePath() + this.index + ';'));
                            }
                        }
                    }
                }

                private class LazyParameterDescription
                extends ParameterDescription.InDefinedShape.AbstractBase {
                    private final int index;

                    protected LazyParameterDescription(int index) {
                        this.index = index;
                    }

                    public MethodDescription.InDefinedShape getDeclaringMethod() {
                        return LazyMethodDescription.this;
                    }

                    public int getIndex() {
                        return this.index;
                    }

                    public boolean isNamed() {
                        return LazyMethodDescription.this.parameterNames[this.index] != null;
                    }

                    public boolean hasModifiers() {
                        return LazyMethodDescription.this.parameterModifiers[this.index] != null;
                    }

                    public String getName() {
                        return this.isNamed() ? LazyMethodDescription.this.parameterNames[this.index] : super.getName();
                    }

                    public int getModifiers() {
                        return this.hasModifiers() ? LazyMethodDescription.this.parameterModifiers[this.index].intValue() : super.getModifiers();
                    }

                    public TypeDescription.Generic getType() {
                        return (TypeDescription.Generic)LazyMethodDescription.this.signatureResolution.resolveParameterTypes(LazyMethodDescription.this.parameterTypeDescriptors, LazyTypeDescription.this.typePool, LazyMethodDescription.this.parameterTypeAnnotationTokens, LazyMethodDescription.this).get(this.index);
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        return LazyAnnotationDescription.asListOfNullable(LazyTypeDescription.this.typePool, (List)LazyMethodDescription.this.parameterAnnotationTokens.get(this.index));
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private class LazyParameterList
                extends ParameterList.AbstractBase<ParameterDescription.InDefinedShape> {
                    private LazyParameterList() {
                    }

                    @Override
                    public ParameterDescription.InDefinedShape get(int index) {
                        return new LazyParameterDescription(index);
                    }

                    @Override
                    public boolean hasExplicitMetaData() {
                        for (int i = 0; i < this.size(); ++i) {
                            if (LazyMethodDescription.this.parameterNames[i] != null && LazyMethodDescription.this.parameterModifiers[i] != null) continue;
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public int size() {
                        return LazyMethodDescription.this.parameterTypeDescriptors.size();
                    }

                    @Override
                    public TypeList.Generic asTypeList() {
                        return LazyMethodDescription.this.signatureResolution.resolveParameterTypes(LazyMethodDescription.this.parameterTypeDescriptors, LazyTypeDescription.this.typePool, LazyMethodDescription.this.parameterTypeAnnotationTokens, LazyMethodDescription.this);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private class LazyFieldDescription
            extends FieldDescription.InDefinedShape.AbstractBase {
                private final String name;
                private final int modifiers;
                private final String descriptor;
                @MaybeNull
                private final String genericSignature;
                private final GenericTypeToken.Resolution.ForField signatureResolution;
                private final Map<String, List<AnnotationToken>> typeAnnotationTokens;
                private final List<AnnotationToken> annotationTokens;

                private LazyFieldDescription(String name, int modifiers, @MaybeNull String descriptor, String genericSignature, GenericTypeToken.Resolution.ForField signatureResolution, Map<String, List<AnnotationToken>> typeAnnotationTokens, List<AnnotationToken> annotationTokens) {
                    this.modifiers = modifiers;
                    this.name = name;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.signatureResolution = signatureResolution;
                    this.typeAnnotationTokens = typeAnnotationTokens;
                    this.annotationTokens = annotationTokens;
                }

                @Override
                public TypeDescription.Generic getType() {
                    return this.signatureResolution.resolveFieldType(this.descriptor, LazyTypeDescription.this.typePool, this.typeAnnotationTokens, this);
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return LazyAnnotationDescription.asListOfNullable(LazyTypeDescription.this.typePool, this.annotationTokens);
                }

                @Override
                public String getName() {
                    return this.name;
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return LazyTypeDescription.this;
                }

                @Override
                public int getModifiers() {
                    return this.modifiers;
                }

                @Override
                @MaybeNull
                public String getGenericSignature() {
                    return this.genericSignature;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class TokenizedGenericType
            extends TypeDescription.Generic.LazyProjection.WithEagerNavigation {
                private final TypePool typePool;
                private final GenericTypeToken genericTypeToken;
                private final String rawTypeDescriptor;
                private final Map<String, List<AnnotationToken>> annotationTokens;
                private final TypeVariableSource typeVariableSource;
                private transient /* synthetic */ TypeDescription.Generic resolved;
                private transient /* synthetic */ TypeDescription erasure;

                protected TokenizedGenericType(TypePool typePool, GenericTypeToken genericTypeToken, String rawTypeDescriptor, Map<String, List<AnnotationToken>> annotationTokens, TypeVariableSource typeVariableSource) {
                    this.typePool = typePool;
                    this.genericTypeToken = genericTypeToken;
                    this.rawTypeDescriptor = rawTypeDescriptor;
                    this.annotationTokens = annotationTokens;
                    this.typeVariableSource = typeVariableSource;
                }

                protected static TypeDescription.Generic of(TypePool typePool, GenericTypeToken genericTypeToken, String rawTypeDescriptor, @MaybeNull Map<String, List<AnnotationToken>> annotationTokens, TypeVariableSource typeVariableSource) {
                    return new TokenizedGenericType(typePool, genericTypeToken, rawTypeDescriptor, annotationTokens == null ? Collections.emptyMap() : annotationTokens, typeVariableSource);
                }

                protected static TypeDescription toErasure(TypePool typePool, String descriptor) {
                    Type type = Type.getType(descriptor);
                    return typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve();
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected TypeDescription.Generic resolve() {
                    TypeDescription.Generic generic;
                    TypeDescription.Generic generic2;
                    TypeDescription.Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        generic2 = generic = generic.genericTypeToken.toGenericType(generic.typePool, generic.typeVariableSource, "", generic.annotationTokens);
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                @Override
                @CachedReturnPlugin.Enhance(value="erasure")
                public TypeDescription asErasure() {
                    TypeDefinition typeDefinition;
                    TypeDefinition typeDefinition2;
                    TypeDescription typeDescription = this.erasure;
                    if (typeDescription != null) {
                        typeDefinition2 = null;
                    } else {
                        typeDefinition = this;
                        typeDefinition2 = typeDefinition = TokenizedGenericType.toErasure(typeDefinition.typePool, typeDefinition.rawTypeDescriptor);
                    }
                    if (typeDefinition == null) {
                        typeDefinition = this.erasure;
                    } else {
                        this.erasure = typeDefinition;
                    }
                    return typeDefinition;
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.resolve().getDeclaredAnnotations();
                }

                protected static class Malformed
                extends TypeDescription.Generic.LazyProjection.WithEagerNavigation {
                    private final TypePool typePool;
                    private final String rawTypeDescriptor;

                    protected Malformed(TypePool typePool, String rawTypeDescriptor) {
                        this.typePool = typePool;
                        this.rawTypeDescriptor = rawTypeDescriptor;
                    }

                    protected TypeDescription.Generic resolve() {
                        throw new GenericSignatureFormatError();
                    }

                    public TypeDescription asErasure() {
                        return TokenizedGenericType.toErasure(this.typePool, this.rawTypeDescriptor);
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        throw new GenericSignatureFormatError();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class TokenList
                    extends TypeList.Generic.AbstractBase {
                        private final TypePool typePool;
                        private final List<String> rawTypeDescriptors;

                        protected TokenList(TypePool typePool, List<String> rawTypeDescriptors) {
                            this.typePool = typePool;
                            this.rawTypeDescriptors = rawTypeDescriptors;
                        }

                        @Override
                        public TypeDescription.Generic get(int index) {
                            return new Malformed(this.typePool, this.rawTypeDescriptors.get(index));
                        }

                        @Override
                        public int size() {
                            return this.rawTypeDescriptors.size();
                        }

                        @Override
                        public TypeList asErasures() {
                            return new LazyTypeList(this.typePool, this.rawTypeDescriptors);
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class TypeVariableList
                extends TypeList.Generic.AbstractBase {
                    private final TypePool typePool;
                    private final List<GenericTypeToken.OfFormalTypeVariable> typeVariables;
                    private final TypeVariableSource typeVariableSource;
                    private final Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens;
                    private final Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> boundAnnotationTokens;

                    protected TypeVariableList(TypePool typePool, List<GenericTypeToken.OfFormalTypeVariable> typeVariables, TypeVariableSource typeVariableSource, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> boundAnnotationTokens) {
                        this.typePool = typePool;
                        this.typeVariables = typeVariables;
                        this.typeVariableSource = typeVariableSource;
                        this.annotationTokens = annotationTokens;
                        this.boundAnnotationTokens = boundAnnotationTokens;
                    }

                    @Override
                    public TypeDescription.Generic get(int index) {
                        return this.typeVariables.get(index).toGenericType(this.typePool, this.typeVariableSource, this.annotationTokens.get(index), this.boundAnnotationTokens.get(index));
                    }

                    @Override
                    public int size() {
                        return this.typeVariables.size();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class TokenList
                extends TypeList.Generic.AbstractBase {
                    private final TypePool typePool;
                    private final List<GenericTypeToken> genericTypeTokens;
                    private final List<String> rawTypeDescriptors;
                    private final TypeVariableSource typeVariableSource;
                    private final Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens;

                    private TokenList(TypePool typePool, List<GenericTypeToken> genericTypeTokens, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, List<String> rawTypeDescriptors, TypeVariableSource typeVariableSource) {
                        this.typePool = typePool;
                        this.genericTypeTokens = genericTypeTokens;
                        this.annotationTokens = annotationTokens;
                        this.rawTypeDescriptors = rawTypeDescriptors;
                        this.typeVariableSource = typeVariableSource;
                    }

                    @Override
                    public TypeDescription.Generic get(int index) {
                        return this.rawTypeDescriptors.size() == this.genericTypeTokens.size() ? TokenizedGenericType.of(this.typePool, this.genericTypeTokens.get(index), this.rawTypeDescriptors.get(index), this.annotationTokens.get(index), this.typeVariableSource) : TokenizedGenericType.toErasure(this.typePool, this.rawTypeDescriptors.get(index)).asGenericType();
                    }

                    @Override
                    public int size() {
                        return this.rawTypeDescriptors.size();
                    }

                    @Override
                    public TypeList asErasures() {
                        return new LazyTypeList(this.typePool, this.rawTypeDescriptors);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class LazyNestMemberList
            extends TypeList.AbstractBase {
                private final TypeDescription typeDescription;
                private final TypePool typePool;
                private final List<String> nestMembers;

                protected LazyNestMemberList(TypeDescription typeDescription, TypePool typePool, List<String> nestMembers) {
                    this.typeDescription = typeDescription;
                    this.typePool = typePool;
                    this.nestMembers = nestMembers;
                }

                @Override
                public TypeDescription get(int index) {
                    return index == 0 ? this.typeDescription : this.typePool.describe(this.nestMembers.get(index - 1)).resolve();
                }

                @Override
                public int size() {
                    return this.nestMembers.size() + 1;
                }

                @Override
                public String[] toInternalNames() {
                    String[] internalName = new String[this.nestMembers.size() + 1];
                    internalName[0] = this.typeDescription.getInternalName();
                    int index = 1;
                    for (String name : this.nestMembers) {
                        internalName[index++] = name.replace('.', '/');
                    }
                    return internalName;
                }

                @Override
                public int getStackSize() {
                    return this.nestMembers.size() + 1;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class LazyTypeList
            extends TypeList.AbstractBase {
                private final TypePool typePool;
                private final List<String> descriptors;

                protected LazyTypeList(TypePool typePool, List<String> descriptors) {
                    this.typePool = typePool;
                    this.descriptors = descriptors;
                }

                @Override
                public TypeDescription get(int index) {
                    return TokenizedGenericType.toErasure(this.typePool, this.descriptors.get(index));
                }

                @Override
                public int size() {
                    return this.descriptors.size();
                }

                @Override
                @MaybeNull
                public String[] toInternalNames() {
                    String[] internalName = new String[this.descriptors.size()];
                    int index = 0;
                    for (String descriptor : this.descriptors) {
                        internalName[index++] = Type.getType(descriptor).getInternalName();
                    }
                    return internalName.length == 0 ? NO_INTERFACES : internalName;
                }

                @Override
                public int getStackSize() {
                    int stackSize = 0;
                    for (String descriptor : this.descriptors) {
                        stackSize += Type.getType(descriptor).getSize();
                    }
                    return stackSize;
                }
            }

            private static class LazyPackageDescription
            extends PackageDescription.AbstractBase {
                private final TypePool typePool;
                private final String name;

                private LazyPackageDescription(TypePool typePool, String name) {
                    this.typePool = typePool;
                    this.name = name;
                }

                public AnnotationList getDeclaredAnnotations() {
                    Resolution resolution = this.typePool.describe(this.name + "." + "package-info");
                    return resolution.isResolved() ? resolution.resolve().getDeclaredAnnotations() : new AnnotationList.Empty();
                }

                public String getName() {
                    return this.name;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private static abstract class LazyAnnotationValue<U, V>
            extends AnnotationValue.AbstractBase<U, V> {
                private transient /* synthetic */ int hashCode;

                private LazyAnnotationValue() {
                }

                protected abstract AnnotationValue<U, V> doResolve();

                @Override
                public AnnotationValue.State getState() {
                    return this.doResolve().getState();
                }

                @Override
                public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
                    return this.doResolve().filter(property, typeDefinition);
                }

                @Override
                public U resolve() {
                    return this.doResolve().resolve();
                }

                @Override
                public AnnotationValue.Loaded<V> load(@MaybeNull ClassLoader classLoader) {
                    return this.doResolve().load(classLoader);
                }

                @CachedReturnPlugin.Enhance(value="hashCode")
                public int hashCode() {
                    int n;
                    int n2;
                    int n3 = this.hashCode;
                    if (n3 != 0) {
                        n2 = 0;
                    } else {
                        LazyAnnotationValue lazyAnnotationValue = this;
                        n2 = n = lazyAnnotationValue.doResolve().hashCode();
                    }
                    if (n == 0) {
                        n = this.hashCode;
                    } else {
                        this.hashCode = n;
                    }
                    return n;
                }

                public boolean equals(@MaybeNull Object other) {
                    return this.doResolve().equals(other);
                }

                public String toString() {
                    return this.doResolve().toString();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private static class ForArray
                extends LazyAnnotationValue<Object, Object> {
                    private final TypePool typePool;
                    private final AbstractBase.ComponentTypeReference componentTypeReference;
                    private final List<AnnotationValue<?, ?>> values;
                    private transient /* synthetic */ AnnotationValue resolved;

                    private ForArray(TypePool typePool, AbstractBase.ComponentTypeReference componentTypeReference, List<AnnotationValue<?, ?>> values) {
                        this.typePool = typePool;
                        this.componentTypeReference = componentTypeReference;
                        this.values = values;
                    }

                    @Override
                    public AnnotationValue.Sort getSort() {
                        return AnnotationValue.Sort.ARRAY;
                    }

                    /*
                     * Unable to fully structure code
                     * Could not resolve type clashes
                     */
                    @Override
                    @CachedReturnPlugin.Enhance(value="resolved")
                    protected AnnotationValue<Object, Object> doResolve() {
                        block30: {
                            block29: {
                                var1_1 = this.resolved;
                                if (var1_1 == null) break block29;
                                v0 /* !! */  = null;
                                break block30;
                            }
                            var2_2 /* !! */  = this;
                            typeName = var2_2 /* !! */ .componentTypeReference.resolve();
                            if (typeName == null) ** GOTO lbl-1000
                            resolution = var2_2 /* !! */ .typePool.describe(typeName);
                            if (!resolution.isResolved()) {
                                v0 /* !! */  = new AnnotationValue.ForMissingType<U, V>(typeName);
                            } else if (resolution.resolve().isEnum()) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(EnumerationDescription.class, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().isAnnotation()) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(AnnotationDescription.class, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents((java.lang.reflect.Type)Class.class)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(TypeDescription.class, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents((java.lang.reflect.Type)String.class)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(String.class, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Boolean.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Boolean.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Byte.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Byte.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Short.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Short.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Character.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Character.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Integer.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Integer.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Long.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Long.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Float.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Float.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else if (resolution.resolve().represents(Double.TYPE)) {
                                v0 /* !! */  = new AnnotationValue.ForDescriptionArray<U, V>(Double.TYPE, resolution.resolve(), var2_2 /* !! */ .values);
                            } else lbl-1000:
                            // 2 sources

                            {
                                sort = AnnotationValue.Sort.NONE;
                                iterator = var2_2 /* !! */ .values.listIterator(var2_2 /* !! */ .values.size());
                                while (iterator.hasPrevious() && !sort.isDefined()) {
                                    sort = iterator.previous().getSort();
                                }
                                v0 /* !! */  = var2_2 /* !! */  = new ForMismatchedType<W, X>(AnnotationValue.RenderingDispatcher.CURRENT.toArrayErrorString(sort), sort);
                            }
                        }
                        if (var2_2 /* !! */  == null) {
                            var2_2 /* !! */  = this.resolved;
                        } else {
                            this.resolved = var2_2 /* !! */ ;
                        }
                        return var2_2 /* !! */ ;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private static class ForEnumerationValue
                extends LazyAnnotationValue<EnumerationDescription, Enum<?>> {
                    private final TypePool typePool;
                    private final String typeName;
                    private final String value;
                    private transient /* synthetic */ AnnotationValue resolved;

                    private ForEnumerationValue(TypePool typePool, String typeName, String value) {
                        this.typePool = typePool;
                        this.typeName = typeName;
                        this.value = value;
                    }

                    @Override
                    public AnnotationValue.Sort getSort() {
                        return AnnotationValue.Sort.ENUMERATION;
                    }

                    @Override
                    @CachedReturnPlugin.Enhance(value="resolved")
                    protected AnnotationValue<EnumerationDescription, Enum<?>> doResolve() {
                        AnnotationValue<EnumerationDescription, Enum<?>> annotationValue;
                        AnnotationValue<Object, Object> annotationValue2;
                        AnnotationValue annotationValue3 = this.resolved;
                        if (annotationValue3 != null) {
                            annotationValue2 = null;
                        } else {
                            annotationValue = this;
                            Resolution resolution = annotationValue.typePool.describe(annotationValue.typeName);
                            annotationValue2 = !resolution.isResolved() ? new AnnotationValue.ForMissingType(annotationValue.typeName) : (!resolution.resolve().isEnum() ? new ForMismatchedType(annotationValue.typeName + "." + annotationValue.value, AnnotationValue.Sort.ENUMERATION) : (annotationValue = ((FieldList)resolution.resolve().getDeclaredFields().filter(ElementMatchers.named(annotationValue.value))).isEmpty() ? new AnnotationValue.ForEnumerationDescription.WithUnknownConstant(resolution.resolve(), annotationValue.value) : new AnnotationValue.ForEnumerationDescription(new EnumerationDescription.Latent(resolution.resolve(), annotationValue.value))));
                        }
                        if (annotationValue == null) {
                            annotationValue = this.resolved;
                        } else {
                            this.resolved = annotationValue;
                        }
                        return annotationValue;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private static class ForAnnotationValue
                extends LazyAnnotationValue<AnnotationDescription, Annotation> {
                    private final TypePool typePool;
                    private final AnnotationToken annotationToken;
                    private transient /* synthetic */ AnnotationValue resolved;

                    private ForAnnotationValue(TypePool typePool, AnnotationToken annotationToken) {
                        this.typePool = typePool;
                        this.annotationToken = annotationToken;
                    }

                    @Override
                    public AnnotationValue.Sort getSort() {
                        return AnnotationValue.Sort.ANNOTATION;
                    }

                    @Override
                    @CachedReturnPlugin.Enhance(value="resolved")
                    protected AnnotationValue<AnnotationDescription, Annotation> doResolve() {
                        AnnotationValue<AnnotationDescription, Annotation> annotationValue;
                        AnnotationValue<Object, Object> annotationValue2;
                        AnnotationValue annotationValue3 = this.resolved;
                        if (annotationValue3 != null) {
                            annotationValue2 = null;
                        } else {
                            annotationValue = this;
                            AnnotationToken.Resolution resolution = annotationValue.annotationToken.toAnnotationDescription(annotationValue.typePool);
                            annotationValue2 = !resolution.isResolved() ? new AnnotationValue.ForMissingType(annotationValue.annotationToken.getBinaryName()) : (annotationValue = !resolution.resolve().getAnnotationType().isAnnotation() ? new ForMismatchedType(resolution.resolve().getAnnotationType().getName(), AnnotationValue.Sort.ANNOTATION) : new AnnotationValue.ForAnnotationDescription(resolution.resolve()));
                        }
                        if (annotationValue == null) {
                            annotationValue = this.resolved;
                        } else {
                            this.resolved = annotationValue;
                        }
                        return annotationValue;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private static class ForTypeValue
                extends LazyAnnotationValue<TypeDescription, Class<?>> {
                    private final TypePool typePool;
                    private final String typeName;
                    private transient /* synthetic */ AnnotationValue resolved;

                    private ForTypeValue(TypePool typePool, String typeName) {
                        this.typePool = typePool;
                        this.typeName = typeName;
                    }

                    @Override
                    public AnnotationValue.Sort getSort() {
                        return AnnotationValue.Sort.TYPE;
                    }

                    @Override
                    @CachedReturnPlugin.Enhance(value="resolved")
                    protected AnnotationValue<TypeDescription, Class<?>> doResolve() {
                        AnnotationValue<TypeDescription, Class<?>> annotationValue;
                        AnnotationValue<TypeDescription, Object> annotationValue2;
                        AnnotationValue annotationValue3 = this.resolved;
                        if (annotationValue3 != null) {
                            annotationValue2 = null;
                        } else {
                            annotationValue = this;
                            Resolution resolution = annotationValue.typePool.describe(annotationValue.typeName);
                            annotationValue2 = annotationValue = resolution.isResolved() ? new AnnotationValue.ForTypeDescription(resolution.resolve()) : new AnnotationValue.ForMissingType(annotationValue.typeName);
                        }
                        if (annotationValue == null) {
                            annotationValue = this.resolved;
                        } else {
                            this.resolved = annotationValue;
                        }
                        return annotationValue;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                private static class ForMismatchedType<W, X>
                extends AnnotationValue.AbstractBase<W, X> {
                    private final String value;
                    private final AnnotationValue.Sort sort;

                    private ForMismatchedType(String value, AnnotationValue.Sort sort) {
                        this.value = value;
                        this.sort = sort;
                    }

                    @Override
                    public AnnotationValue.State getState() {
                        return AnnotationValue.State.UNRESOLVED;
                    }

                    @Override
                    public AnnotationValue.Sort getSort() {
                        return AnnotationValue.Sort.NONE;
                    }

                    @Override
                    public AnnotationValue<W, X> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
                        return new AnnotationValue.ForMismatchedType(property, property.getReturnType().isArray() ? AnnotationValue.RenderingDispatcher.CURRENT.toArrayErrorString(this.sort) : this.value);
                    }

                    @Override
                    public W resolve() {
                        throw new IllegalStateException("Expected filtering of this unresolved property");
                    }

                    @Override
                    public AnnotationValue.Loaded<X> load(@MaybeNull ClassLoader classLoader) {
                        throw new IllegalStateException("Expected filtering of this unresolved property");
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
                        if (!this.sort.equals((Object)((ForMismatchedType)object).sort)) {
                            return false;
                        }
                        return this.value.equals(((ForMismatchedType)object).value);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.value.hashCode()) * 31 + this.sort.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private static class LazyAnnotationDescription
            extends AnnotationDescription.AbstractBase {
                protected final TypePool typePool;
                private final TypeDescription annotationType;
                protected final Map<String, AnnotationValue<?, ?>> values;

                private LazyAnnotationDescription(TypePool typePool, TypeDescription annotationType, Map<String, AnnotationValue<?, ?>> values) {
                    this.typePool = typePool;
                    this.annotationType = annotationType;
                    this.values = values;
                }

                protected static AnnotationList asListOfNullable(TypePool typePool, @MaybeNull List<? extends AnnotationToken> tokens) {
                    return tokens == null ? new AnnotationList.Empty() : LazyAnnotationDescription.asList(typePool, tokens);
                }

                protected static AnnotationList asList(TypePool typePool, List<? extends AnnotationToken> tokens) {
                    ArrayList<AnnotationDescription> annotationDescriptions = new ArrayList<AnnotationDescription>(tokens.size());
                    for (AnnotationToken annotationToken : tokens) {
                        AnnotationToken.Resolution resolution = annotationToken.toAnnotationDescription(typePool);
                        if (!resolution.isResolved() || !resolution.resolve().getAnnotationType().isAnnotation()) continue;
                        annotationDescriptions.add(resolution.resolve());
                    }
                    return new UnresolvedAnnotationList(annotationDescriptions, tokens);
                }

                @Override
                public AnnotationValue<?, ?> getValue(MethodDescription.InDefinedShape property) {
                    if (!property.getDeclaringType().asErasure().equals(this.annotationType)) {
                        throw new IllegalArgumentException(property + " is not declared by " + this.getAnnotationType());
                    }
                    AnnotationValue<Object, Object> annotationValue = this.values.get(property.getName());
                    if (annotationValue != null) {
                        return annotationValue.filter(property);
                    }
                    annotationValue = ((MethodDescription.InDefinedShape)((MethodList)this.getAnnotationType().getDeclaredMethods().filter(ElementMatchers.is(property))).getOnly()).getDefaultValue();
                    return annotationValue == null ? new AnnotationValue.ForMissingValue(this.annotationType, property.getName()) : annotationValue;
                }

                @Override
                public TypeDescription getAnnotationType() {
                    return this.annotationType;
                }

                public <T extends Annotation> Loadable<T> prepare(Class<T> annotationType) {
                    if (!this.annotationType.represents(annotationType)) {
                        throw new IllegalArgumentException(annotationType + " does not represent " + this.annotationType);
                    }
                    return new Loadable(this.typePool, annotationType, this.values);
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private static class UnresolvedAnnotationList
                extends AnnotationList.Explicit {
                    private final List<? extends AnnotationToken> tokens;

                    private UnresolvedAnnotationList(List<? extends AnnotationDescription> annotationDescriptions, List<? extends AnnotationToken> tokens) {
                        super(annotationDescriptions);
                        this.tokens = tokens;
                    }

                    @Override
                    public List<String> asTypeNames() {
                        ArrayList<String> typeNames = new ArrayList<String>(this.tokens.size());
                        for (AnnotationToken annotationToken : this.tokens) {
                            typeNames.add(annotationToken.getBinaryName());
                        }
                        return typeNames;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private static class Loadable<S extends Annotation>
                extends LazyAnnotationDescription
                implements AnnotationDescription.Loadable<S> {
                    private final Class<S> annotationType;

                    private Loadable(TypePool typePool, Class<S> annotationType, Map<String, AnnotationValue<?, ?>> values) {
                        super(typePool, TypeDescription.ForLoadedType.of(annotationType), values);
                        this.annotationType = annotationType;
                    }

                    @Override
                    public S load() {
                        return AnnotationDescription.AnnotationInvocationHandler.of(this.annotationType.getClassLoader(), this.annotationType, this.values);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class RecordComponentToken {
                private final String name;
                private final String descriptor;
                @UnknownNull
                private final String genericSignature;
                private final GenericTypeToken.Resolution.ForRecordComponent signatureResolution;
                private final Map<String, List<AnnotationToken>> typeAnnotationTokens;
                private final List<AnnotationToken> annotationTokens;

                protected RecordComponentToken(String name, String descriptor, @MaybeNull String genericSignature, Map<String, List<AnnotationToken>> typeAnnotationTokens, List<AnnotationToken> annotationTokens) {
                    this.name = name;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.signatureResolution = TypeDescription.AbstractBase.RAW_TYPES ? GenericTypeToken.Resolution.Raw.INSTANCE : GenericTypeExtractor.ForSignature.OfRecordComponent.extract(genericSignature);
                    this.typeAnnotationTokens = typeAnnotationTokens;
                    this.annotationTokens = annotationTokens;
                }

                private RecordComponentDescription.InDefinedShape toRecordComponentDescription(LazyTypeDescription lazyTypeDescription) {
                    LazyTypeDescription lazyTypeDescription2 = lazyTypeDescription;
                    lazyTypeDescription2.getClass();
                    return lazyTypeDescription2.new LazyRecordComponentDescription(this.name, this.descriptor, this.genericSignature, this.signatureResolution, this.typeAnnotationTokens, this.annotationTokens);
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
                    if (!this.name.equals(((RecordComponentToken)object).name)) {
                        return false;
                    }
                    if (!this.descriptor.equals(((RecordComponentToken)object).descriptor)) {
                        return false;
                    }
                    if (!this.genericSignature.equals(((RecordComponentToken)object).genericSignature)) {
                        return false;
                    }
                    if (!this.signatureResolution.equals(((RecordComponentToken)object).signatureResolution)) {
                        return false;
                    }
                    if (!((Object)this.typeAnnotationTokens).equals(((RecordComponentToken)object).typeAnnotationTokens)) {
                        return false;
                    }
                    return ((Object)this.annotationTokens).equals(((RecordComponentToken)object).annotationTokens);
                }

                public int hashCode() {
                    return (((((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.descriptor.hashCode()) * 31 + this.genericSignature.hashCode()) * 31 + this.signatureResolution.hashCode()) * 31 + ((Object)this.typeAnnotationTokens).hashCode()) * 31 + ((Object)this.annotationTokens).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class MethodToken {
                private final String name;
                private final int modifiers;
                private final String descriptor;
                @UnknownNull
                private final String genericSignature;
                private final GenericTypeToken.Resolution.ForMethod signatureResolution;
                @MaybeNull
                private final String[] exceptionName;
                private final Map<Integer, Map<String, List<AnnotationToken>>> typeVariableAnnotationTokens;
                private final Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> typeVariableBoundAnnotationTokens;
                private final Map<String, List<AnnotationToken>> returnTypeAnnotationTokens;
                private final Map<Integer, Map<String, List<AnnotationToken>>> parameterTypeAnnotationTokens;
                private final Map<Integer, Map<String, List<AnnotationToken>>> exceptionTypeAnnotationTokens;
                private final Map<String, List<AnnotationToken>> receiverTypeAnnotationTokens;
                private final List<AnnotationToken> annotationTokens;
                private final Map<Integer, List<AnnotationToken>> parameterAnnotationTokens;
                private final List<ParameterToken> parameterTokens;
                @UnknownNull
                private final AnnotationValue<?, ?> defaultValue;

                protected MethodToken(String name, int modifiers, String descriptor, @MaybeNull String genericSignature, @MaybeNull String[] exceptionName, Map<Integer, Map<String, List<AnnotationToken>>> typeVariableAnnotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> typeVariableBoundAnnotationTokens, Map<String, List<AnnotationToken>> returnTypeAnnotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> parameterTypeAnnotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> exceptionTypeAnnotationTokens, Map<String, List<AnnotationToken>> receiverTypeAnnotationTokens, List<AnnotationToken> annotationTokens, Map<Integer, List<AnnotationToken>> parameterAnnotationTokens, List<ParameterToken> parameterTokens, @MaybeNull AnnotationValue<?, ?> defaultValue) {
                    this.modifiers = modifiers & 0xFFFDFFFF;
                    this.name = name;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.signatureResolution = TypeDescription.AbstractBase.RAW_TYPES ? GenericTypeToken.Resolution.Raw.INSTANCE : GenericTypeExtractor.ForSignature.OfMethod.extract(genericSignature);
                    this.exceptionName = exceptionName;
                    this.typeVariableAnnotationTokens = typeVariableAnnotationTokens;
                    this.typeVariableBoundAnnotationTokens = typeVariableBoundAnnotationTokens;
                    this.returnTypeAnnotationTokens = returnTypeAnnotationTokens;
                    this.parameterTypeAnnotationTokens = parameterTypeAnnotationTokens;
                    this.exceptionTypeAnnotationTokens = exceptionTypeAnnotationTokens;
                    this.receiverTypeAnnotationTokens = receiverTypeAnnotationTokens;
                    this.annotationTokens = annotationTokens;
                    this.parameterAnnotationTokens = parameterAnnotationTokens;
                    this.parameterTokens = parameterTokens;
                    this.defaultValue = defaultValue;
                }

                private MethodDescription.InDefinedShape toMethodDescription(LazyTypeDescription lazyTypeDescription) {
                    LazyTypeDescription lazyTypeDescription2 = lazyTypeDescription;
                    lazyTypeDescription2.getClass();
                    return lazyTypeDescription2.new LazyMethodDescription(this.name, this.modifiers, this.descriptor, this.genericSignature, this.signatureResolution, this.exceptionName, this.typeVariableAnnotationTokens, this.typeVariableBoundAnnotationTokens, this.returnTypeAnnotationTokens, this.parameterTypeAnnotationTokens, this.exceptionTypeAnnotationTokens, this.receiverTypeAnnotationTokens, this.annotationTokens, this.parameterAnnotationTokens, this.parameterTokens, this.defaultValue);
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
                    if (this.modifiers != ((MethodToken)object).modifiers) {
                        return false;
                    }
                    if (!this.name.equals(((MethodToken)object).name)) {
                        return false;
                    }
                    if (!this.descriptor.equals(((MethodToken)object).descriptor)) {
                        return false;
                    }
                    if (!this.genericSignature.equals(((MethodToken)object).genericSignature)) {
                        return false;
                    }
                    if (!this.signatureResolution.equals(((MethodToken)object).signatureResolution)) {
                        return false;
                    }
                    if (!Arrays.equals(this.exceptionName, ((MethodToken)object).exceptionName)) {
                        return false;
                    }
                    if (!((Object)this.typeVariableAnnotationTokens).equals(((MethodToken)object).typeVariableAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.typeVariableBoundAnnotationTokens).equals(((MethodToken)object).typeVariableBoundAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.returnTypeAnnotationTokens).equals(((MethodToken)object).returnTypeAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.parameterTypeAnnotationTokens).equals(((MethodToken)object).parameterTypeAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.exceptionTypeAnnotationTokens).equals(((MethodToken)object).exceptionTypeAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.receiverTypeAnnotationTokens).equals(((MethodToken)object).receiverTypeAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.annotationTokens).equals(((MethodToken)object).annotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.parameterAnnotationTokens).equals(((MethodToken)object).parameterAnnotationTokens)) {
                        return false;
                    }
                    if (!((Object)this.parameterTokens).equals(((MethodToken)object).parameterTokens)) {
                        return false;
                    }
                    return this.defaultValue.equals(((MethodToken)object).defaultValue);
                }

                public int hashCode() {
                    return (((((((((((((((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.modifiers) * 31 + this.descriptor.hashCode()) * 31 + this.genericSignature.hashCode()) * 31 + this.signatureResolution.hashCode()) * 31 + Arrays.hashCode(this.exceptionName)) * 31 + ((Object)this.typeVariableAnnotationTokens).hashCode()) * 31 + ((Object)this.typeVariableBoundAnnotationTokens).hashCode()) * 31 + ((Object)this.returnTypeAnnotationTokens).hashCode()) * 31 + ((Object)this.parameterTypeAnnotationTokens).hashCode()) * 31 + ((Object)this.exceptionTypeAnnotationTokens).hashCode()) * 31 + ((Object)this.receiverTypeAnnotationTokens).hashCode()) * 31 + ((Object)this.annotationTokens).hashCode()) * 31 + ((Object)this.parameterAnnotationTokens).hashCode()) * 31 + ((Object)this.parameterTokens).hashCode()) * 31 + this.defaultValue.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class ParameterToken {
                    @AlwaysNull
                    protected static final String NO_NAME = null;
                    @AlwaysNull
                    protected static final Integer NO_MODIFIERS = null;
                    @MaybeNull
                    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                    private final String name;
                    @MaybeNull
                    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                    private final Integer modifiers;

                    protected ParameterToken() {
                        this(NO_NAME);
                    }

                    protected ParameterToken(@MaybeNull String name) {
                        this(name, NO_MODIFIERS);
                    }

                    protected ParameterToken(@MaybeNull String name, @MaybeNull Integer modifiers) {
                        this.name = name;
                        this.modifiers = modifiers;
                    }

                    @MaybeNull
                    protected String getName() {
                        return this.name;
                    }

                    @MaybeNull
                    protected Integer getModifiers() {
                        return this.modifiers;
                    }

                    public boolean equals(@MaybeNull Object object) {
                        block18: {
                            block17: {
                                Object object2;
                                block16: {
                                    Object object3;
                                    Object object4;
                                    block15: {
                                        block14: {
                                            Integer n;
                                            block13: {
                                                if (this == object) {
                                                    return true;
                                                }
                                                if (object == null) {
                                                    return false;
                                                }
                                                if (this.getClass() != object.getClass()) {
                                                    return false;
                                                }
                                                object4 = ((ParameterToken)object).modifiers;
                                                object3 = this.modifiers;
                                                n = object3;
                                                if (object4 == null) break block13;
                                                if (n == null) break block14;
                                                if (!((Integer)object3).equals(object4)) {
                                                    return false;
                                                }
                                                break block15;
                                            }
                                            if (n == null) break block15;
                                        }
                                        return false;
                                    }
                                    object4 = ((ParameterToken)object).name;
                                    object2 = object3 = this.name;
                                    if (object4 == null) break block16;
                                    if (object2 == null) break block17;
                                    if (!((String)object3).equals(object4)) {
                                        return false;
                                    }
                                    break block18;
                                }
                                if (object2 == null) break block18;
                            }
                            return false;
                        }
                        return true;
                    }

                    public int hashCode() {
                        int n = this.getClass().hashCode() * 31;
                        Object object = this.name;
                        if (object != null) {
                            n = n + ((String)object).hashCode();
                        }
                        int n2 = n * 31;
                        object = this.modifiers;
                        if (object != null) {
                            n2 = n2 + ((Integer)object).hashCode();
                        }
                        return n2;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class FieldToken {
                private final String name;
                private final int modifiers;
                private final String descriptor;
                @UnknownNull
                private final String genericSignature;
                private final GenericTypeToken.Resolution.ForField signatureResolution;
                private final Map<String, List<AnnotationToken>> typeAnnotationTokens;
                private final List<AnnotationToken> annotationTokens;

                protected FieldToken(String name, int modifiers, String descriptor, @MaybeNull String genericSignature, Map<String, List<AnnotationToken>> typeAnnotationTokens, List<AnnotationToken> annotationTokens) {
                    this.modifiers = modifiers & 0xFFFDFFFF;
                    this.name = name;
                    this.descriptor = descriptor;
                    this.genericSignature = genericSignature;
                    this.signatureResolution = TypeDescription.AbstractBase.RAW_TYPES ? GenericTypeToken.Resolution.Raw.INSTANCE : GenericTypeExtractor.ForSignature.OfField.extract(genericSignature);
                    this.typeAnnotationTokens = typeAnnotationTokens;
                    this.annotationTokens = annotationTokens;
                }

                private LazyFieldDescription toFieldDescription(LazyTypeDescription lazyTypeDescription) {
                    LazyTypeDescription lazyTypeDescription2 = lazyTypeDescription;
                    lazyTypeDescription2.getClass();
                    return lazyTypeDescription2.new LazyFieldDescription(this.name, this.modifiers, this.descriptor, this.genericSignature, this.signatureResolution, this.typeAnnotationTokens, this.annotationTokens);
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
                    if (this.modifiers != ((FieldToken)object).modifiers) {
                        return false;
                    }
                    if (!this.name.equals(((FieldToken)object).name)) {
                        return false;
                    }
                    if (!this.descriptor.equals(((FieldToken)object).descriptor)) {
                        return false;
                    }
                    if (!this.genericSignature.equals(((FieldToken)object).genericSignature)) {
                        return false;
                    }
                    if (!this.signatureResolution.equals(((FieldToken)object).signatureResolution)) {
                        return false;
                    }
                    if (!((Object)this.typeAnnotationTokens).equals(((FieldToken)object).typeAnnotationTokens)) {
                        return false;
                    }
                    return ((Object)this.annotationTokens).equals(((FieldToken)object).annotationTokens);
                }

                public int hashCode() {
                    return ((((((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.modifiers) * 31 + this.descriptor.hashCode()) * 31 + this.genericSignature.hashCode()) * 31 + this.signatureResolution.hashCode()) * 31 + ((Object)this.typeAnnotationTokens).hashCode()) * 31 + ((Object)this.annotationTokens).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class AnnotationToken {
                private final String descriptor;
                private final Map<String, AnnotationValue<?, ?>> values;

                protected AnnotationToken(String descriptor, Map<String, AnnotationValue<?, ?>> values) {
                    this.descriptor = descriptor;
                    this.values = values;
                }

                protected String getBinaryName() {
                    return this.descriptor.substring(1, this.descriptor.length() - 1).replace('/', '.');
                }

                private Resolution toAnnotationDescription(TypePool typePool) {
                    net.bytebuddy.pool.TypePool$Resolution resolution = typePool.describe(this.getBinaryName());
                    return resolution.isResolved() ? new Resolution.Simple(new LazyAnnotationDescription(typePool, resolution.resolve(), this.values)) : new Resolution.Illegal(this.getBinaryName());
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
                    if (!this.descriptor.equals(((AnnotationToken)object).descriptor)) {
                        return false;
                    }
                    return ((Object)this.values).equals(((AnnotationToken)object).values);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.descriptor.hashCode()) * 31 + ((Object)this.values).hashCode();
                }

                protected static interface Resolution {
                    public boolean isResolved();

                    public AnnotationDescription resolve();

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Illegal
                    implements Resolution {
                        private final String annotationType;

                        public Illegal(String annotationType) {
                            this.annotationType = annotationType;
                        }

                        public boolean isResolved() {
                            return false;
                        }

                        public AnnotationDescription resolve() {
                            throw new IllegalStateException("Annotation type is not available: " + this.annotationType);
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
                            return this.annotationType.equals(((Illegal)object).annotationType);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.annotationType.hashCode();
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Simple
                    implements Resolution {
                        private final AnnotationDescription annotationDescription;

                        protected Simple(AnnotationDescription annotationDescription) {
                            this.annotationDescription = annotationDescription;
                        }

                        public boolean isResolved() {
                            return true;
                        }

                        public AnnotationDescription resolve() {
                            return this.annotationDescription;
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
                            return this.annotationDescription.equals(((Simple)object).annotationDescription);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.annotationDescription.hashCode();
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static interface GenericTypeToken {
                public static final String EMPTY_TYPE_PATH = "";
                public static final char COMPONENT_TYPE_PATH = '[';
                public static final char WILDCARD_TYPE_PATH = '*';
                public static final char INNER_CLASS_PATH = '.';
                public static final char INDEXED_TYPE_DELIMITER = ';';

                public TypeDescription.Generic toGenericType(TypePool var1, TypeVariableSource var2, String var3, Map<String, List<AnnotationToken>> var4);

                public boolean isPrimaryBound(TypePool var1);

                public String getTypePathPrefix();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class LazyTokenList
                extends TypeList.Generic.AbstractBase {
                    private final TypePool typePool;
                    private final TypeVariableSource typeVariableSource;
                    private final String typePath;
                    private final Map<String, List<AnnotationToken>> annotationTokens;
                    private final List<GenericTypeToken> genericTypeTokens;

                    protected LazyTokenList(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, List<GenericTypeToken> genericTypeTokens) {
                        this.typePool = typePool;
                        this.typeVariableSource = typeVariableSource;
                        this.typePath = typePath;
                        this.annotationTokens = annotationTokens;
                        this.genericTypeTokens = genericTypeTokens;
                    }

                    @Override
                    public TypeDescription.Generic get(int index) {
                        return this.genericTypeTokens.get(index).toGenericType(this.typePool, this.typeVariableSource, this.typePath + index + ';', this.annotationTokens);
                    }

                    @Override
                    public int size() {
                        return this.genericTypeTokens.size();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class ForWildcardBound
                    extends TypeList.Generic.AbstractBase {
                        private final TypePool typePool;
                        private final TypeVariableSource typeVariableSource;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;
                        private final GenericTypeToken genericTypeToken;

                        protected ForWildcardBound(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, GenericTypeToken genericTypeToken) {
                            this.typePool = typePool;
                            this.typeVariableSource = typeVariableSource;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                            this.genericTypeToken = genericTypeToken;
                        }

                        @Override
                        public TypeDescription.Generic get(int index) {
                            if (index == 0) {
                                return this.genericTypeToken.toGenericType(this.typePool, this.typeVariableSource, this.typePath + '*', this.annotationTokens);
                            }
                            throw new IndexOutOfBoundsException("index = " + index);
                        }

                        @Override
                        public int size() {
                            return 1;
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForParameterizedType
                implements GenericTypeToken {
                    private final String name;
                    private final List<GenericTypeToken> parameterTypeTokens;

                    protected ForParameterizedType(String name, List<GenericTypeToken> parameterTypeTokens) {
                        this.name = name;
                        this.parameterTypeTokens = parameterTypeTokens;
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        return new LazyParameterizedType(typePool, typeVariableSource, typePath, annotationTokens, this.name, this.parameterTypeTokens);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        return !typePool.describe(this.name).resolve().isInterface();
                    }

                    @Override
                    public String getTypePathPrefix() {
                        return String.valueOf('.');
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
                        if (!this.name.equals(((ForParameterizedType)object).name)) {
                            return false;
                        }
                        return ((Object)this.parameterTypeTokens).equals(((ForParameterizedType)object).parameterTypeTokens);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + ((Object)this.parameterTypeTokens).hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class LazyParameterizedType
                    extends TypeDescription.Generic.OfParameterizedType {
                        private final TypePool typePool;
                        private final TypeVariableSource typeVariableSource;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;
                        private final String name;
                        private final List<GenericTypeToken> parameterTypeTokens;

                        protected LazyParameterizedType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, String name, List<GenericTypeToken> parameterTypeTokens) {
                            this.typePool = typePool;
                            this.typeVariableSource = typeVariableSource;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                            this.name = name;
                            this.parameterTypeTokens = parameterTypeTokens;
                        }

                        @Override
                        public TypeDescription asErasure() {
                            return this.typePool.describe(this.name).resolve();
                        }

                        @Override
                        public TypeList.Generic getTypeArguments() {
                            return new LazyTokenList(this.typePool, this.typeVariableSource, this.typePath, this.annotationTokens, this.parameterTypeTokens);
                        }

                        @Override
                        @MaybeNull
                        public TypeDescription.Generic getOwnerType() {
                            TypeDescription ownerType = this.typePool.describe(this.name).resolve().getEnclosingType();
                            return ownerType == null ? TypeDescription.Generic.UNDEFINED : ownerType.asGenericType();
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath));
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Nested
                    implements GenericTypeToken {
                        private final String name;
                        private final List<GenericTypeToken> parameterTypeTokens;
                        private final GenericTypeToken ownerTypeToken;

                        protected Nested(String name, List<GenericTypeToken> parameterTypeTokens, GenericTypeToken ownerTypeToken) {
                            this.name = name;
                            this.parameterTypeTokens = parameterTypeTokens;
                            this.ownerTypeToken = ownerTypeToken;
                        }

                        @Override
                        public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                            return new LazyParameterizedType(typePool, typeVariableSource, typePath, annotationTokens, this.name, this.parameterTypeTokens, this.ownerTypeToken);
                        }

                        @Override
                        public String getTypePathPrefix() {
                            return this.ownerTypeToken.getTypePathPrefix() + '.';
                        }

                        @Override
                        public boolean isPrimaryBound(TypePool typePool) {
                            return !typePool.describe(this.name).resolve().isInterface();
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
                            if (!this.name.equals(((Nested)object).name)) {
                                return false;
                            }
                            if (!((Object)this.parameterTypeTokens).equals(((Nested)object).parameterTypeTokens)) {
                                return false;
                            }
                            return this.ownerTypeToken.equals(((Nested)object).ownerTypeToken);
                        }

                        public int hashCode() {
                            return ((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + ((Object)this.parameterTypeTokens).hashCode()) * 31 + this.ownerTypeToken.hashCode();
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static class LazyParameterizedType
                        extends TypeDescription.Generic.OfParameterizedType {
                            private final TypePool typePool;
                            private final TypeVariableSource typeVariableSource;
                            private final String typePath;
                            private final Map<String, List<AnnotationToken>> annotationTokens;
                            private final String name;
                            private final List<GenericTypeToken> parameterTypeTokens;
                            private final GenericTypeToken ownerTypeToken;

                            protected LazyParameterizedType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, String name, List<GenericTypeToken> parameterTypeTokens, GenericTypeToken ownerTypeToken) {
                                this.typePool = typePool;
                                this.typeVariableSource = typeVariableSource;
                                this.typePath = typePath;
                                this.annotationTokens = annotationTokens;
                                this.name = name;
                                this.parameterTypeTokens = parameterTypeTokens;
                                this.ownerTypeToken = ownerTypeToken;
                            }

                            @Override
                            public TypeDescription asErasure() {
                                return this.typePool.describe(this.name).resolve();
                            }

                            @Override
                            public TypeList.Generic getTypeArguments() {
                                return new LazyTokenList(this.typePool, this.typeVariableSource, this.typePath + this.ownerTypeToken.getTypePathPrefix(), this.annotationTokens, this.parameterTypeTokens);
                            }

                            @Override
                            @MaybeNull
                            public TypeDescription.Generic getOwnerType() {
                                return this.ownerTypeToken.toGenericType(this.typePool, this.typeVariableSource, this.typePath, this.annotationTokens);
                            }

                            @Override
                            public AnnotationList getDeclaredAnnotations() {
                                return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath + this.ownerTypeToken.getTypePathPrefix()));
                            }
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForUpperBoundWildcard
                implements GenericTypeToken {
                    private final GenericTypeToken boundTypeToken;

                    protected ForUpperBoundWildcard(GenericTypeToken boundTypeToken) {
                        this.boundTypeToken = boundTypeToken;
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        return new LazyUpperBoundWildcard(typePool, typeVariableSource, typePath, annotationTokens, this.boundTypeToken);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        throw new IllegalStateException("A wildcard type cannot be a type variable bound: " + this);
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("An upper bound wildcard cannot be the owner of a nested type: " + this);
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
                        return this.boundTypeToken.equals(((ForUpperBoundWildcard)object).boundTypeToken);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.boundTypeToken.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class LazyUpperBoundWildcard
                    extends TypeDescription.Generic.OfWildcardType {
                        private final TypePool typePool;
                        private final TypeVariableSource typeVariableSource;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;
                        private final GenericTypeToken boundTypeToken;

                        protected LazyUpperBoundWildcard(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, GenericTypeToken boundTypeToken) {
                            this.typePool = typePool;
                            this.typeVariableSource = typeVariableSource;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                            this.boundTypeToken = boundTypeToken;
                        }

                        @Override
                        public TypeList.Generic getUpperBounds() {
                            return new LazyTokenList.ForWildcardBound(this.typePool, this.typeVariableSource, this.typePath, this.annotationTokens, this.boundTypeToken);
                        }

                        @Override
                        public TypeList.Generic getLowerBounds() {
                            return new TypeList.Generic.Empty();
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath));
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForLowerBoundWildcard
                implements GenericTypeToken {
                    private final GenericTypeToken boundTypeToken;

                    protected ForLowerBoundWildcard(GenericTypeToken boundTypeToken) {
                        this.boundTypeToken = boundTypeToken;
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        return new LazyLowerBoundWildcard(typePool, typeVariableSource, typePath, annotationTokens, this.boundTypeToken);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        throw new IllegalStateException("A wildcard type cannot be a type variable bound: " + this);
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("A lower bound wildcard cannot be the owner of a nested type: " + this);
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
                        return this.boundTypeToken.equals(((ForLowerBoundWildcard)object).boundTypeToken);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.boundTypeToken.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class LazyLowerBoundWildcard
                    extends TypeDescription.Generic.OfWildcardType {
                        private final TypePool typePool;
                        private final TypeVariableSource typeVariableSource;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;
                        private final GenericTypeToken boundTypeToken;

                        protected LazyLowerBoundWildcard(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, GenericTypeToken boundTypeToken) {
                            this.typePool = typePool;
                            this.typeVariableSource = typeVariableSource;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                            this.boundTypeToken = boundTypeToken;
                        }

                        @Override
                        public TypeList.Generic getUpperBounds() {
                            return new TypeList.Generic.Explicit(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class));
                        }

                        @Override
                        public TypeList.Generic getLowerBounds() {
                            return new LazyTokenList.ForWildcardBound(this.typePool, this.typeVariableSource, this.typePath, this.annotationTokens, this.boundTypeToken);
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath));
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForGenericArray
                implements GenericTypeToken {
                    private final GenericTypeToken componentTypeToken;

                    protected ForGenericArray(GenericTypeToken componentTypeToken) {
                        this.componentTypeToken = componentTypeToken;
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        return new LazyGenericArray(typePool, typeVariableSource, typePath, annotationTokens, this.componentTypeToken);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        throw new IllegalStateException("A generic array type cannot be a type variable bound: " + this);
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("A generic array type cannot be the owner of a nested type: " + this);
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
                        return this.componentTypeToken.equals(((ForGenericArray)object).componentTypeToken);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.componentTypeToken.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class LazyGenericArray
                    extends TypeDescription.Generic.OfGenericArray {
                        private final TypePool typePool;
                        private final TypeVariableSource typeVariableSource;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;
                        private final GenericTypeToken componentTypeToken;

                        protected LazyGenericArray(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens, GenericTypeToken componentTypeToken) {
                            this.typePool = typePool;
                            this.typeVariableSource = typeVariableSource;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                            this.componentTypeToken = componentTypeToken;
                        }

                        @Override
                        public TypeDescription.Generic getComponentType() {
                            return this.componentTypeToken.toGenericType(this.typePool, this.typeVariableSource, this.typePath + '[', this.annotationTokens);
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath));
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForTypeVariable
                implements GenericTypeToken {
                    private final String symbol;

                    protected ForTypeVariable(String symbol) {
                        this.symbol = symbol;
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        TypeDescription.Generic typeVariable = typeVariableSource.findVariable(this.symbol);
                        return typeVariable == null ? new UnresolvedTypeVariable(typeVariableSource, typePool, this.symbol, annotationTokens.get(typePath)) : new AnnotatedTypeVariable(typePool, annotationTokens.get(typePath), typeVariable);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        return true;
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("A type variable cannot be the owner of a nested type: " + this);
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
                        return this.symbol.equals(((ForTypeVariable)object).symbol);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.symbol.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class Formal
                    implements OfFormalTypeVariable {
                        private final String symbol;
                        private final List<GenericTypeToken> boundTypeTokens;

                        protected Formal(String symbol, List<GenericTypeToken> boundTypeTokens) {
                            this.symbol = symbol;
                            this.boundTypeTokens = boundTypeTokens;
                        }

                        @Override
                        public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, @MaybeNull Map<String, List<AnnotationToken>> annotationTokens, @MaybeNull Map<Integer, Map<String, List<AnnotationToken>>> boundaryAnnotationTokens) {
                            return new LazyTypeVariable(typePool, typeVariableSource, annotationTokens == null ? Collections.emptyMap() : annotationTokens, boundaryAnnotationTokens == null ? Collections.emptyMap() : boundaryAnnotationTokens, this.symbol, this.boundTypeTokens);
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
                            if (!this.symbol.equals(((Formal)object).symbol)) {
                                return false;
                            }
                            return ((Object)this.boundTypeTokens).equals(((Formal)object).boundTypeTokens);
                        }

                        public int hashCode() {
                            return (this.getClass().hashCode() * 31 + this.symbol.hashCode()) * 31 + ((Object)this.boundTypeTokens).hashCode();
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static class LazyTypeVariable
                        extends TypeDescription.Generic.OfTypeVariable {
                            private final TypePool typePool;
                            private final TypeVariableSource typeVariableSource;
                            private final Map<String, List<AnnotationToken>> annotationTokens;
                            private final Map<Integer, Map<String, List<AnnotationToken>>> boundaryAnnotationTokens;
                            private final String symbol;
                            private final List<GenericTypeToken> boundTypeTokens;

                            protected LazyTypeVariable(TypePool typePool, TypeVariableSource typeVariableSource, Map<String, List<AnnotationToken>> annotationTokens, Map<Integer, Map<String, List<AnnotationToken>>> boundaryAnnotationTokens, String symbol, List<GenericTypeToken> boundTypeTokens) {
                                this.typePool = typePool;
                                this.typeVariableSource = typeVariableSource;
                                this.annotationTokens = annotationTokens;
                                this.boundaryAnnotationTokens = boundaryAnnotationTokens;
                                this.symbol = symbol;
                                this.boundTypeTokens = boundTypeTokens;
                            }

                            @Override
                            public TypeList.Generic getUpperBounds() {
                                return new LazyBoundTokenList(this.typePool, this.typeVariableSource, this.boundaryAnnotationTokens, this.boundTypeTokens);
                            }

                            @Override
                            public TypeVariableSource getTypeVariableSource() {
                                return this.typeVariableSource;
                            }

                            @Override
                            public String getSymbol() {
                                return this.symbol;
                            }

                            @Override
                            public AnnotationList getDeclaredAnnotations() {
                                return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(GenericTypeToken.EMPTY_TYPE_PATH));
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static class LazyBoundTokenList
                            extends TypeList.Generic.AbstractBase {
                                private final TypePool typePool;
                                private final TypeVariableSource typeVariableSource;
                                private final Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens;
                                private final List<GenericTypeToken> boundTypeTokens;

                                protected LazyBoundTokenList(TypePool typePool, TypeVariableSource typeVariableSource, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, List<GenericTypeToken> boundTypeTokens) {
                                    this.typePool = typePool;
                                    this.typeVariableSource = typeVariableSource;
                                    this.annotationTokens = annotationTokens;
                                    this.boundTypeTokens = boundTypeTokens;
                                }

                                @Override
                                public TypeDescription.Generic get(int index) {
                                    Map annotationTokens = !this.annotationTokens.containsKey(index) && !this.annotationTokens.containsKey(index + 1) ? Collections.emptyMap() : this.annotationTokens.get(index + (this.boundTypeTokens.get(0).isPrimaryBound(this.typePool) ? 0 : 1));
                                    return this.boundTypeTokens.get(index).toGenericType(this.typePool, this.typeVariableSource, GenericTypeToken.EMPTY_TYPE_PATH, annotationTokens == null ? Collections.emptyMap() : annotationTokens);
                                }

                                @Override
                                public int size() {
                                    return this.boundTypeTokens.size();
                                }
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class UnresolvedTypeVariable
                    extends TypeDescription.Generic.OfTypeVariable {
                        private final TypeVariableSource typeVariableSource;
                        private final TypePool typePool;
                        private final String symbol;
                        private final List<AnnotationToken> annotationTokens;

                        protected UnresolvedTypeVariable(TypeVariableSource typeVariableSource, TypePool typePool, String symbol, List<AnnotationToken> annotationTokens) {
                            this.typeVariableSource = typeVariableSource;
                            this.typePool = typePool;
                            this.symbol = symbol;
                            this.annotationTokens = annotationTokens;
                        }

                        @Override
                        public TypeList.Generic getUpperBounds() {
                            throw new IllegalStateException("Cannot resolve bounds of unresolved type variable " + this + " by " + this.typeVariableSource);
                        }

                        @Override
                        public TypeVariableSource getTypeVariableSource() {
                            return this.typeVariableSource;
                        }

                        @Override
                        public String getSymbol() {
                            return this.symbol;
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens);
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class AnnotatedTypeVariable
                    extends TypeDescription.Generic.OfTypeVariable {
                        private final TypePool typePool;
                        private final List<AnnotationToken> annotationTokens;
                        private final TypeDescription.Generic typeVariable;

                        protected AnnotatedTypeVariable(TypePool typePool, List<AnnotationToken> annotationTokens, TypeDescription.Generic typeVariable) {
                            this.typePool = typePool;
                            this.annotationTokens = annotationTokens;
                            this.typeVariable = typeVariable;
                        }

                        @Override
                        public TypeList.Generic getUpperBounds() {
                            return this.typeVariable.getUpperBounds();
                        }

                        @Override
                        public TypeVariableSource getTypeVariableSource() {
                            return this.typeVariable.getTypeVariableSource();
                        }

                        @Override
                        public String getSymbol() {
                            return this.typeVariable.getSymbol();
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens);
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForRawType
                implements GenericTypeToken {
                    private final String name;

                    protected ForRawType(String name) {
                        this.name = name;
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        return new Resolution.Raw.RawAnnotatedType(typePool, typePath, annotationTokens, typePool.describe(this.name).resolve());
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        return !typePool.describe(this.name).resolve().isInterface();
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("A non-generic type cannot be the owner of a nested type: " + this);
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
                        return this.name.equals(((ForRawType)object).name);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.name.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface Resolution {
                    public TypeList.Generic resolveTypeVariables(TypePool var1, TypeVariableSource var2, Map<Integer, Map<String, List<AnnotationToken>>> var3, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> var4);

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static interface ForRecordComponent {
                        public TypeDescription.Generic resolveRecordType(String var1, TypePool var2, Map<String, List<AnnotationToken>> var3, RecordComponentDescription.InDefinedShape var4);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Tokenized
                        implements ForRecordComponent {
                            private final GenericTypeToken recordComponentTypeToken;

                            protected Tokenized(GenericTypeToken recordComponentTypeToken) {
                                this.recordComponentTypeToken = recordComponentTypeToken;
                            }

                            @Override
                            public TypeDescription.Generic resolveRecordType(String recordTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, RecordComponentDescription.InDefinedShape definingRecordComponent) {
                                return TokenizedGenericType.of(typePool, this.recordComponentTypeToken, recordTypeDescriptor, annotationTokens, definingRecordComponent.getDeclaringType());
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
                                return this.recordComponentTypeToken.equals(((Tokenized)object).recordComponentTypeToken);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.recordComponentTypeToken.hashCode();
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static interface ForMethod
                    extends Resolution {
                        public TypeDescription.Generic resolveReturnType(String var1, TypePool var2, Map<String, List<AnnotationToken>> var3, MethodDescription.InDefinedShape var4);

                        public TypeList.Generic resolveParameterTypes(List<String> var1, TypePool var2, Map<Integer, Map<String, List<AnnotationToken>>> var3, MethodDescription.InDefinedShape var4);

                        public TypeList.Generic resolveExceptionTypes(List<String> var1, TypePool var2, Map<Integer, Map<String, List<AnnotationToken>>> var3, MethodDescription.InDefinedShape var4);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Tokenized
                        implements ForMethod {
                            private final GenericTypeToken returnTypeToken;
                            private final List<GenericTypeToken> parameterTypeTokens;
                            private final List<GenericTypeToken> exceptionTypeTokens;
                            private final List<OfFormalTypeVariable> typeVariableTokens;

                            protected Tokenized(GenericTypeToken returnTypeToken, List<GenericTypeToken> parameterTypeTokens, List<GenericTypeToken> exceptionTypeTokens, List<OfFormalTypeVariable> typeVariableTokens) {
                                this.returnTypeToken = returnTypeToken;
                                this.parameterTypeTokens = parameterTypeTokens;
                                this.exceptionTypeTokens = exceptionTypeTokens;
                                this.typeVariableTokens = typeVariableTokens;
                            }

                            @Override
                            public TypeDescription.Generic resolveReturnType(String returnTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                                return TokenizedGenericType.of(typePool, this.returnTypeToken, returnTypeDescriptor, annotationTokens, definingMethod);
                            }

                            @Override
                            public TypeList.Generic resolveParameterTypes(List<String> parameterTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                                return new TokenizedGenericType.TokenList(typePool, this.parameterTypeTokens, annotationTokens, parameterTypeDescriptors, definingMethod);
                            }

                            @Override
                            public TypeList.Generic resolveExceptionTypes(List<String> exceptionTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                                return this.exceptionTypeTokens.isEmpty() ? Raw.INSTANCE.resolveExceptionTypes(exceptionTypeDescriptors, typePool, annotationTokens, definingMethod) : new TokenizedGenericType.TokenList(typePool, this.exceptionTypeTokens, annotationTokens, exceptionTypeDescriptors, definingMethod);
                            }

                            @Override
                            public TypeList.Generic resolveTypeVariables(TypePool typePool, TypeVariableSource typeVariableSource, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> boundAnnotationTokens) {
                                return new TokenizedGenericType.TypeVariableList(typePool, this.typeVariableTokens, typeVariableSource, annotationTokens, boundAnnotationTokens);
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
                                if (!this.returnTypeToken.equals(((Tokenized)object).returnTypeToken)) {
                                    return false;
                                }
                                if (!((Object)this.parameterTypeTokens).equals(((Tokenized)object).parameterTypeTokens)) {
                                    return false;
                                }
                                if (!((Object)this.exceptionTypeTokens).equals(((Tokenized)object).exceptionTypeTokens)) {
                                    return false;
                                }
                                return ((Object)this.typeVariableTokens).equals(((Tokenized)object).typeVariableTokens);
                            }

                            public int hashCode() {
                                return (((this.getClass().hashCode() * 31 + this.returnTypeToken.hashCode()) * 31 + ((Object)this.parameterTypeTokens).hashCode()) * 31 + ((Object)this.exceptionTypeTokens).hashCode()) * 31 + ((Object)this.typeVariableTokens).hashCode();
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static interface ForField {
                        public TypeDescription.Generic resolveFieldType(String var1, TypePool var2, Map<String, List<AnnotationToken>> var3, FieldDescription.InDefinedShape var4);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Tokenized
                        implements ForField {
                            private final GenericTypeToken fieldTypeToken;

                            protected Tokenized(GenericTypeToken fieldTypeToken) {
                                this.fieldTypeToken = fieldTypeToken;
                            }

                            @Override
                            public TypeDescription.Generic resolveFieldType(String fieldTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, FieldDescription.InDefinedShape definingField) {
                                return TokenizedGenericType.of(typePool, this.fieldTypeToken, fieldTypeDescriptor, annotationTokens, definingField.getDeclaringType());
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
                                return this.fieldTypeToken.equals(((Tokenized)object).fieldTypeToken);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.fieldTypeToken.hashCode();
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static interface ForType
                    extends Resolution {
                        public TypeDescription.Generic resolveSuperClass(String var1, TypePool var2, Map<String, List<AnnotationToken>> var3, TypeDescription var4);

                        public TypeList.Generic resolveInterfaceTypes(List<String> var1, TypePool var2, Map<Integer, Map<String, List<AnnotationToken>>> var3, TypeDescription var4);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Tokenized
                        implements ForType {
                            private final GenericTypeToken superClassToken;
                            private final List<GenericTypeToken> interfaceTypeTokens;
                            private final List<OfFormalTypeVariable> typeVariableTokens;

                            protected Tokenized(GenericTypeToken superClassToken, List<GenericTypeToken> interfaceTypeTokens, List<OfFormalTypeVariable> typeVariableTokens) {
                                this.superClassToken = superClassToken;
                                this.interfaceTypeTokens = interfaceTypeTokens;
                                this.typeVariableTokens = typeVariableTokens;
                            }

                            @Override
                            public TypeDescription.Generic resolveSuperClass(String superClassDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, TypeDescription definingType) {
                                return TokenizedGenericType.of(typePool, this.superClassToken, superClassDescriptor, annotationTokens, definingType);
                            }

                            @Override
                            public TypeList.Generic resolveInterfaceTypes(List<String> interfaceTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, TypeDescription definingType) {
                                return new TokenizedGenericType.TokenList(typePool, this.interfaceTypeTokens, annotationTokens, interfaceTypeDescriptors, definingType);
                            }

                            @Override
                            public TypeList.Generic resolveTypeVariables(TypePool typePool, TypeVariableSource typeVariableSource, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> boundAnnotationTokens) {
                                return new TokenizedGenericType.TypeVariableList(typePool, this.typeVariableTokens, typeVariableSource, annotationTokens, boundAnnotationTokens);
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
                                if (!this.superClassToken.equals(((Tokenized)object).superClassToken)) {
                                    return false;
                                }
                                if (!((Object)this.interfaceTypeTokens).equals(((Tokenized)object).interfaceTypeTokens)) {
                                    return false;
                                }
                                return ((Object)this.typeVariableTokens).equals(((Tokenized)object).typeVariableTokens);
                            }

                            public int hashCode() {
                                return ((this.getClass().hashCode() * 31 + this.superClassToken.hashCode()) * 31 + ((Object)this.interfaceTypeTokens).hashCode()) * 31 + ((Object)this.typeVariableTokens).hashCode();
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static enum Malformed implements ForType,
                    ForField,
                    ForMethod,
                    ForRecordComponent
                    {
                        INSTANCE;


                        @Override
                        public TypeDescription.Generic resolveSuperClass(String superClassDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, TypeDescription definingType) {
                            return new TokenizedGenericType.Malformed(typePool, superClassDescriptor);
                        }

                        @Override
                        public TypeList.Generic resolveInterfaceTypes(List<String> interfaceTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, TypeDescription definingType) {
                            return new TokenizedGenericType.Malformed.TokenList(typePool, interfaceTypeDescriptors);
                        }

                        @Override
                        public TypeList.Generic resolveTypeVariables(TypePool typePool, TypeVariableSource typeVariableSource, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> boundAnnotationTokens) {
                            throw new GenericSignatureFormatError();
                        }

                        @Override
                        public TypeDescription.Generic resolveFieldType(String fieldTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, FieldDescription.InDefinedShape definingField) {
                            return new TokenizedGenericType.Malformed(typePool, fieldTypeDescriptor);
                        }

                        @Override
                        public TypeDescription.Generic resolveReturnType(String returnTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                            return new TokenizedGenericType.Malformed(typePool, returnTypeDescriptor);
                        }

                        @Override
                        public TypeList.Generic resolveParameterTypes(List<String> parameterTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                            return new TokenizedGenericType.Malformed.TokenList(typePool, parameterTypeDescriptors);
                        }

                        @Override
                        public TypeList.Generic resolveExceptionTypes(List<String> exceptionTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                            return new TokenizedGenericType.Malformed.TokenList(typePool, exceptionTypeDescriptors);
                        }

                        @Override
                        public TypeDescription.Generic resolveRecordType(String recordTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, RecordComponentDescription.InDefinedShape definingRecordComponent) {
                            return new TokenizedGenericType.Malformed(typePool, recordTypeDescriptor);
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static enum Raw implements ForType,
                    ForField,
                    ForMethod,
                    ForRecordComponent
                    {
                        INSTANCE;


                        @Override
                        public TypeDescription.Generic resolveSuperClass(String superClassDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, TypeDescription definingType) {
                            return RawAnnotatedType.of(typePool, annotationTokens, superClassDescriptor);
                        }

                        @Override
                        public TypeList.Generic resolveInterfaceTypes(List<String> interfaceTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, TypeDescription definingType) {
                            return RawAnnotatedType.LazyRawAnnotatedTypeList.of(typePool, annotationTokens, interfaceTypeDescriptors);
                        }

                        @Override
                        public TypeList.Generic resolveTypeVariables(TypePool typePool, TypeVariableSource typeVariableSource, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, Map<Integer, Map<Integer, Map<String, List<AnnotationToken>>>> boundAnnotationTokens) {
                            return new TypeList.Generic.Empty();
                        }

                        @Override
                        public TypeDescription.Generic resolveFieldType(String fieldTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, FieldDescription.InDefinedShape definingField) {
                            return RawAnnotatedType.of(typePool, annotationTokens, fieldTypeDescriptor);
                        }

                        @Override
                        public TypeDescription.Generic resolveReturnType(String returnTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                            return RawAnnotatedType.of(typePool, annotationTokens, returnTypeDescriptor);
                        }

                        @Override
                        public TypeList.Generic resolveParameterTypes(List<String> parameterTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                            return RawAnnotatedType.LazyRawAnnotatedTypeList.of(typePool, annotationTokens, parameterTypeDescriptors);
                        }

                        @Override
                        public TypeList.Generic resolveExceptionTypes(List<String> exceptionTypeDescriptors, TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, MethodDescription.InDefinedShape definingMethod) {
                            return RawAnnotatedType.LazyRawAnnotatedTypeList.of(typePool, annotationTokens, exceptionTypeDescriptors);
                        }

                        @Override
                        public TypeDescription.Generic resolveRecordType(String recordTypeDescriptor, TypePool typePool, Map<String, List<AnnotationToken>> annotationTokens, RecordComponentDescription.InDefinedShape definingRecordComponent) {
                            return RawAnnotatedType.of(typePool, annotationTokens, recordTypeDescriptor);
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static class RawAnnotatedType
                        extends TypeDescription.Generic.OfNonGenericType {
                            private final TypePool typePool;
                            private final String typePath;
                            private final Map<String, List<AnnotationToken>> annotationTokens;
                            private final TypeDescription typeDescription;

                            protected RawAnnotatedType(TypePool typePool, String typePath, Map<String, List<AnnotationToken>> annotationTokens, TypeDescription typeDescription) {
                                this.typePool = typePool;
                                this.typePath = typePath;
                                this.annotationTokens = annotationTokens;
                                this.typeDescription = typeDescription;
                            }

                            protected static TypeDescription.Generic of(TypePool typePool, @MaybeNull Map<String, List<AnnotationToken>> annotationTokens, String descriptor) {
                                return new RawAnnotatedType(typePool, GenericTypeToken.EMPTY_TYPE_PATH, annotationTokens == null ? Collections.emptyMap() : annotationTokens, TokenizedGenericType.toErasure(typePool, descriptor));
                            }

                            @Override
                            public TypeDescription asErasure() {
                                return this.typeDescription;
                            }

                            @Override
                            @MaybeNull
                            public TypeDescription.Generic getOwnerType() {
                                TypeDescription declaringType = this.typeDescription.getDeclaringType();
                                return declaringType == null ? TypeDescription.Generic.UNDEFINED : new RawAnnotatedType(this.typePool, this.typePath, this.annotationTokens, declaringType);
                            }

                            @Override
                            @MaybeNull
                            public TypeDescription.Generic getComponentType() {
                                TypeDescription componentType = this.typeDescription.getComponentType();
                                return componentType == null ? TypeDescription.Generic.UNDEFINED : new RawAnnotatedType(this.typePool, this.typePath + '[', this.annotationTokens, componentType);
                            }

                            @Override
                            public AnnotationList getDeclaredAnnotations() {
                                StringBuilder typePath = new StringBuilder(this.typePath);
                                for (int index = 0; index < this.typeDescription.getInnerClassCount(); ++index) {
                                    typePath = typePath.append('.');
                                }
                                return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(typePath.toString()));
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static class LazyRawAnnotatedTypeList
                            extends TypeList.Generic.AbstractBase {
                                private final TypePool typePool;
                                private final Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens;
                                private final List<String> descriptors;

                                protected LazyRawAnnotatedTypeList(TypePool typePool, Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, List<String> descriptors) {
                                    this.typePool = typePool;
                                    this.annotationTokens = annotationTokens;
                                    this.descriptors = descriptors;
                                }

                                protected static TypeList.Generic of(TypePool typePool, @MaybeNull Map<Integer, Map<String, List<AnnotationToken>>> annotationTokens, List<String> descriptors) {
                                    return new LazyRawAnnotatedTypeList(typePool, annotationTokens == null ? Collections.emptyMap() : annotationTokens, descriptors);
                                }

                                @Override
                                public TypeDescription.Generic get(int index) {
                                    return RawAnnotatedType.of(this.typePool, this.annotationTokens.get(index), this.descriptors.get(index));
                                }

                                @Override
                                public int size() {
                                    return this.descriptors.size();
                                }

                                @Override
                                public TypeList asErasures() {
                                    return new LazyTypeList(this.typePool, this.descriptors);
                                }

                                @Override
                                public TypeList.Generic asRawTypes() {
                                    return this;
                                }

                                @Override
                                public int getStackSize() {
                                    int stackSize = 0;
                                    for (String descriptor : this.descriptors) {
                                        stackSize += Type.getType(descriptor).getSize();
                                    }
                                    return stackSize;
                                }
                            }
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForUnboundWildcard implements GenericTypeToken
                {
                    INSTANCE;


                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, @MaybeNull Map<String, List<AnnotationToken>> annotationTokens) {
                        return new LazyUnboundWildcard(typePool, typePath, annotationTokens == null ? Collections.emptyMap() : annotationTokens);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        throw new IllegalStateException("A wildcard type cannot be a type variable bound: " + this);
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("An unbound wildcard cannot be the owner of a nested type: " + this);
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class LazyUnboundWildcard
                    extends TypeDescription.Generic.OfWildcardType {
                        private final TypePool typePool;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;

                        protected LazyUnboundWildcard(TypePool typePool, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                            this.typePool = typePool;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                        }

                        @Override
                        public TypeList.Generic getUpperBounds() {
                            return new TypeList.Generic.Explicit(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class));
                        }

                        @Override
                        public TypeList.Generic getLowerBounds() {
                            return new TypeList.Generic.Empty();
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath));
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForPrimitiveType implements GenericTypeToken
                {
                    BOOLEAN(Boolean.TYPE),
                    BYTE(Byte.TYPE),
                    SHORT(Short.TYPE),
                    CHAR(Character.TYPE),
                    INTEGER(Integer.TYPE),
                    LONG(Long.TYPE),
                    FLOAT(Float.TYPE),
                    DOUBLE(Double.TYPE),
                    VOID(Void.TYPE);

                    private final TypeDescription typeDescription;

                    private ForPrimitiveType(Class<?> type) {
                        this.typeDescription = TypeDescription.ForLoadedType.of(type);
                    }

                    public static GenericTypeToken of(char descriptor) {
                        switch (descriptor) {
                            case 'V': {
                                return VOID;
                            }
                            case 'Z': {
                                return BOOLEAN;
                            }
                            case 'B': {
                                return BYTE;
                            }
                            case 'S': {
                                return SHORT;
                            }
                            case 'C': {
                                return CHAR;
                            }
                            case 'I': {
                                return INTEGER;
                            }
                            case 'J': {
                                return LONG;
                            }
                            case 'F': {
                                return FLOAT;
                            }
                            case 'D': {
                                return DOUBLE;
                            }
                        }
                        throw new IllegalArgumentException("Not a valid primitive type descriptor: " + descriptor);
                    }

                    @Override
                    public TypeDescription.Generic toGenericType(TypePool typePool, TypeVariableSource typeVariableSource, String typePath, Map<String, List<AnnotationToken>> annotationTokens) {
                        return new LazyPrimitiveType(typePool, typePath, annotationTokens, this.typeDescription);
                    }

                    @Override
                    public boolean isPrimaryBound(TypePool typePool) {
                        throw new IllegalStateException("A primitive type cannot be a type variable bound: " + this);
                    }

                    @Override
                    public String getTypePathPrefix() {
                        throw new IllegalStateException("A primitive type cannot be the owner of a nested type: " + this);
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class LazyPrimitiveType
                    extends TypeDescription.Generic.OfNonGenericType {
                        private final TypePool typePool;
                        private final String typePath;
                        private final Map<String, List<AnnotationToken>> annotationTokens;
                        private final TypeDescription typeDescription;

                        protected LazyPrimitiveType(TypePool typePool, String typePath, Map<String, List<AnnotationToken>> annotationTokens, TypeDescription typeDescription) {
                            this.typePool = typePool;
                            this.typePath = typePath;
                            this.annotationTokens = annotationTokens;
                            this.typeDescription = typeDescription;
                        }

                        @Override
                        public TypeDescription asErasure() {
                            return this.typeDescription;
                        }

                        @Override
                        @MaybeNull
                        public TypeDescription.Generic getOwnerType() {
                            return TypeDescription.Generic.UNDEFINED;
                        }

                        @Override
                        @MaybeNull
                        public TypeDescription.Generic getComponentType() {
                            return TypeDescription.Generic.UNDEFINED;
                        }

                        @Override
                        public AnnotationList getDeclaredAnnotations() {
                            return LazyAnnotationDescription.asListOfNullable(this.typePool, this.annotationTokens.get(this.typePath));
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static interface OfFormalTypeVariable {
                    public TypeDescription.Generic toGenericType(TypePool var1, TypeVariableSource var2, Map<String, List<AnnotationToken>> var3, Map<Integer, Map<String, List<AnnotationToken>>> var4);
                }
            }

            protected static interface TypeContainment {
                @MaybeNull
                public MethodDescription.InDefinedShape getEnclosingMethod(TypePool var1);

                @MaybeNull
                public TypeDescription getEnclosingType(TypePool var1);

                public boolean isSelfContained();

                public boolean isLocalType();

                @HashCodeAndEqualsPlugin.Enhance
                public static class WithinMethod
                implements TypeContainment {
                    private final String name;
                    private final String methodName;
                    private final String methodDescriptor;

                    protected WithinMethod(String internalName, String methodName, String methodDescriptor) {
                        this.name = internalName.replace('/', '.');
                        this.methodName = methodName;
                        this.methodDescriptor = methodDescriptor;
                    }

                    public MethodDescription.InDefinedShape getEnclosingMethod(TypePool typePool) {
                        TypeDescription enclosingType = this.getEnclosingType(typePool);
                        if (enclosingType == null) {
                            throw new IllegalStateException("Could not resolve enclosing type " + this.name);
                        }
                        MethodList enclosingMethod = (MethodList)enclosingType.getDeclaredMethods().filter(ElementMatchers.hasMethodName(this.methodName).and(ElementMatchers.hasDescriptor(this.methodDescriptor)));
                        if (enclosingMethod.isEmpty()) {
                            throw new IllegalStateException(this.methodName + this.methodDescriptor + " not declared by " + enclosingType);
                        }
                        return (MethodDescription.InDefinedShape)enclosingMethod.getOnly();
                    }

                    public TypeDescription getEnclosingType(TypePool typePool) {
                        return typePool.describe(this.name).resolve();
                    }

                    public boolean isSelfContained() {
                        return false;
                    }

                    public boolean isLocalType() {
                        return true;
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
                        if (!this.name.equals(((WithinMethod)object).name)) {
                            return false;
                        }
                        if (!this.methodName.equals(((WithinMethod)object).methodName)) {
                            return false;
                        }
                        return this.methodDescriptor.equals(((WithinMethod)object).methodDescriptor);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.methodName.hashCode()) * 31 + this.methodDescriptor.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class WithinType
                implements TypeContainment {
                    private final String name;
                    private final boolean localType;

                    protected WithinType(String internalName, boolean localType) {
                        this.name = internalName.replace('/', '.');
                        this.localType = localType;
                    }

                    @MaybeNull
                    public MethodDescription.InDefinedShape getEnclosingMethod(TypePool typePool) {
                        return MethodDescription.UNDEFINED;
                    }

                    public TypeDescription getEnclosingType(TypePool typePool) {
                        return typePool.describe(this.name).resolve();
                    }

                    public boolean isSelfContained() {
                        return false;
                    }

                    public boolean isLocalType() {
                        return this.localType;
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
                        if (this.localType != ((WithinType)object).localType) {
                            return false;
                        }
                        return this.name.equals(((WithinType)object).name);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.localType;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum SelfContained implements TypeContainment
                {
                    INSTANCE;


                    @Override
                    @MaybeNull
                    public MethodDescription.InDefinedShape getEnclosingMethod(TypePool typePool) {
                        return MethodDescription.UNDEFINED;
                    }

                    @Override
                    @MaybeNull
                    public TypeDescription getEnclosingType(TypePool typePool) {
                        return TypeDescription.UNDEFINED;
                    }

                    @Override
                    public boolean isSelfContained() {
                        return true;
                    }

                    @Override
                    public boolean isLocalType() {
                        return false;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class RecordComponentTokenList
            extends RecordComponentList.AbstractBase<RecordComponentDescription.InDefinedShape> {
                protected RecordComponentTokenList() {
                }

                @Override
                public RecordComponentDescription.InDefinedShape get(int index) {
                    return ((RecordComponentToken)LazyTypeDescription.this.recordComponentTokens.get(index)).toRecordComponentDescription(LazyTypeDescription.this);
                }

                @Override
                public int size() {
                    return LazyTypeDescription.this.recordComponentTokens.size();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class MethodTokenList
            extends MethodList.AbstractBase<MethodDescription.InDefinedShape> {
                protected MethodTokenList() {
                }

                @Override
                public MethodDescription.InDefinedShape get(int index) {
                    return ((MethodToken)LazyTypeDescription.this.methodTokens.get(index)).toMethodDescription(LazyTypeDescription.this);
                }

                @Override
                public int size() {
                    return LazyTypeDescription.this.methodTokens.size();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class FieldTokenList
            extends FieldList.AbstractBase<FieldDescription.InDefinedShape> {
                protected FieldTokenList() {
                }

                @Override
                public FieldDescription.InDefinedShape get(int index) {
                    return ((FieldToken)LazyTypeDescription.this.fieldTokens.get(index)).toFieldDescription(LazyTypeDescription.this);
                }

                @Override
                public int size() {
                    return LazyTypeDescription.this.fieldTokens.size();
                }
            }
        }

        protected static class GenericTypeExtractor
        extends GenericTypeRegistrant.RejectingSignatureVisitor
        implements GenericTypeRegistrant {
            private final GenericTypeRegistrant genericTypeRegistrant;
            @UnknownNull
            private IncompleteToken incompleteToken;

            protected GenericTypeExtractor(GenericTypeRegistrant genericTypeRegistrant) {
                this.genericTypeRegistrant = genericTypeRegistrant;
            }

            public void visitBaseType(char descriptor) {
                this.genericTypeRegistrant.register(LazyTypeDescription.GenericTypeToken.ForPrimitiveType.of(descriptor));
            }

            public void visitTypeVariable(String name) {
                this.genericTypeRegistrant.register(new LazyTypeDescription.GenericTypeToken.ForTypeVariable(name));
            }

            public SignatureVisitor visitArrayType() {
                return new GenericTypeExtractor(this);
            }

            public void register(LazyTypeDescription.GenericTypeToken componentTypeToken) {
                this.genericTypeRegistrant.register(new LazyTypeDescription.GenericTypeToken.ForGenericArray(componentTypeToken));
            }

            public void visitClassType(String name) {
                this.incompleteToken = new IncompleteToken.ForTopLevelType(name);
            }

            public void visitInnerClassType(String name) {
                this.incompleteToken = new IncompleteToken.ForInnerClass(name, this.incompleteToken);
            }

            public void visitTypeArgument() {
                this.incompleteToken.appendPlaceholder();
            }

            public SignatureVisitor visitTypeArgument(char wildcard) {
                switch (wildcard) {
                    case '-': {
                        return this.incompleteToken.appendLowerBound();
                    }
                    case '+': {
                        return this.incompleteToken.appendUpperBound();
                    }
                    case '=': {
                        return this.incompleteToken.appendDirectBound();
                    }
                }
                throw new IllegalArgumentException("Unknown wildcard: " + wildcard);
            }

            public void visitEnd() {
                this.genericTypeRegistrant.register(this.incompleteToken.toToken());
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class ForSignature<T extends LazyTypeDescription.GenericTypeToken.Resolution>
            extends GenericTypeRegistrant.RejectingSignatureVisitor
            implements GenericTypeRegistrant {
                protected final List<LazyTypeDescription.GenericTypeToken.OfFormalTypeVariable> typeVariableTokens = new ArrayList<LazyTypeDescription.GenericTypeToken.OfFormalTypeVariable>();
                @MaybeNull
                protected String currentTypeParameter;
                @UnknownNull
                protected List<LazyTypeDescription.GenericTypeToken> currentBounds;

                protected static <S extends LazyTypeDescription.GenericTypeToken.Resolution> S extract(String genericSignature, ForSignature<S> visitor) {
                    SignatureReader signatureReader = new SignatureReader(genericSignature);
                    signatureReader.accept(visitor);
                    return visitor.resolve();
                }

                @Override
                public void visitFormalTypeParameter(String name) {
                    this.collectTypeParameter();
                    this.currentTypeParameter = name;
                    this.currentBounds = new ArrayList<LazyTypeDescription.GenericTypeToken>();
                }

                @Override
                public SignatureVisitor visitClassBound() {
                    return new GenericTypeExtractor(this);
                }

                @Override
                public SignatureVisitor visitInterfaceBound() {
                    return new GenericTypeExtractor(this);
                }

                @Override
                public void register(LazyTypeDescription.GenericTypeToken token) {
                    if (this.currentBounds == null) {
                        throw new IllegalStateException("Did not expect " + token + " before finding formal parameter");
                    }
                    this.currentBounds.add(token);
                }

                protected void collectTypeParameter() {
                    if (this.currentTypeParameter != null) {
                        this.typeVariableTokens.add(new LazyTypeDescription.GenericTypeToken.ForTypeVariable.Formal(this.currentTypeParameter, this.currentBounds));
                    }
                }

                public abstract T resolve();

                protected static class OfRecordComponent
                implements GenericTypeRegistrant {
                    @UnknownNull
                    private LazyTypeDescription.GenericTypeToken recordComponentType;

                    protected OfRecordComponent() {
                    }

                    public static LazyTypeDescription.GenericTypeToken.Resolution.ForRecordComponent extract(@MaybeNull String genericSignature) {
                        if (genericSignature == null) {
                            return LazyTypeDescription.GenericTypeToken.Resolution.Raw.INSTANCE;
                        }
                        SignatureReader signatureReader = new SignatureReader(genericSignature);
                        OfRecordComponent visitor = new OfRecordComponent();
                        try {
                            signatureReader.acceptType(new GenericTypeExtractor(visitor));
                            return visitor.resolve();
                        }
                        catch (RuntimeException ignored) {
                            return LazyTypeDescription.GenericTypeToken.Resolution.Malformed.INSTANCE;
                        }
                    }

                    public void register(LazyTypeDescription.GenericTypeToken token) {
                        this.recordComponentType = token;
                    }

                    protected LazyTypeDescription.GenericTypeToken.Resolution.ForRecordComponent resolve() {
                        return new LazyTypeDescription.GenericTypeToken.Resolution.ForRecordComponent.Tokenized(this.recordComponentType);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class OfMethod
                extends ForSignature<LazyTypeDescription.GenericTypeToken.Resolution.ForMethod> {
                    private final List<LazyTypeDescription.GenericTypeToken> parameterTypeTokens = new ArrayList<LazyTypeDescription.GenericTypeToken>();
                    private final List<LazyTypeDescription.GenericTypeToken> exceptionTypeTokens = new ArrayList<LazyTypeDescription.GenericTypeToken>();
                    @UnknownNull
                    private LazyTypeDescription.GenericTypeToken returnTypeToken;

                    public static LazyTypeDescription.GenericTypeToken.Resolution.ForMethod extract(@MaybeNull String genericSignature) {
                        try {
                            return genericSignature == null ? LazyTypeDescription.GenericTypeToken.Resolution.Raw.INSTANCE : ForSignature.extract(genericSignature, new OfMethod());
                        }
                        catch (RuntimeException ignored) {
                            return LazyTypeDescription.GenericTypeToken.Resolution.Malformed.INSTANCE;
                        }
                    }

                    @Override
                    public SignatureVisitor visitParameterType() {
                        return new GenericTypeExtractor(new ParameterTypeRegistrant());
                    }

                    @Override
                    public SignatureVisitor visitReturnType() {
                        this.collectTypeParameter();
                        return new GenericTypeExtractor(new ReturnTypeTypeRegistrant());
                    }

                    @Override
                    public SignatureVisitor visitExceptionType() {
                        return new GenericTypeExtractor(new ExceptionTypeRegistrant());
                    }

                    @Override
                    public LazyTypeDescription.GenericTypeToken.Resolution.ForMethod resolve() {
                        return new LazyTypeDescription.GenericTypeToken.Resolution.ForMethod.Tokenized(this.returnTypeToken, this.parameterTypeTokens, this.exceptionTypeTokens, this.typeVariableTokens);
                    }

                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class ExceptionTypeRegistrant
                    implements GenericTypeRegistrant {
                        protected ExceptionTypeRegistrant() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            OfMethod.this.exceptionTypeTokens.add(token);
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
                            return OfMethod.this.equals(((ExceptionTypeRegistrant)object).OfMethod.this);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + OfMethod.this.hashCode();
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class ReturnTypeTypeRegistrant
                    implements GenericTypeRegistrant {
                        protected ReturnTypeTypeRegistrant() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            OfMethod.this.returnTypeToken = token;
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
                            return OfMethod.this.equals(((ReturnTypeTypeRegistrant)object).OfMethod.this);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + OfMethod.this.hashCode();
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class ParameterTypeRegistrant
                    implements GenericTypeRegistrant {
                        protected ParameterTypeRegistrant() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            OfMethod.this.parameterTypeTokens.add(token);
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
                            return OfMethod.this.equals(((ParameterTypeRegistrant)object).OfMethod.this);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + OfMethod.this.hashCode();
                        }
                    }
                }

                protected static class OfField
                implements GenericTypeRegistrant {
                    @UnknownNull
                    private LazyTypeDescription.GenericTypeToken fieldTypeToken;

                    protected OfField() {
                    }

                    public static LazyTypeDescription.GenericTypeToken.Resolution.ForField extract(@MaybeNull String genericSignature) {
                        if (genericSignature == null) {
                            return LazyTypeDescription.GenericTypeToken.Resolution.Raw.INSTANCE;
                        }
                        SignatureReader signatureReader = new SignatureReader(genericSignature);
                        OfField visitor = new OfField();
                        try {
                            signatureReader.acceptType(new GenericTypeExtractor(visitor));
                            return visitor.resolve();
                        }
                        catch (RuntimeException ignored) {
                            return LazyTypeDescription.GenericTypeToken.Resolution.Malformed.INSTANCE;
                        }
                    }

                    public void register(LazyTypeDescription.GenericTypeToken token) {
                        this.fieldTypeToken = token;
                    }

                    protected LazyTypeDescription.GenericTypeToken.Resolution.ForField resolve() {
                        return new LazyTypeDescription.GenericTypeToken.Resolution.ForField.Tokenized(this.fieldTypeToken);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class OfType
                extends ForSignature<LazyTypeDescription.GenericTypeToken.Resolution.ForType> {
                    private final List<LazyTypeDescription.GenericTypeToken> interfaceTypeTokens = new ArrayList<LazyTypeDescription.GenericTypeToken>();
                    @UnknownNull
                    private LazyTypeDescription.GenericTypeToken superClassToken;

                    protected OfType() {
                    }

                    public static LazyTypeDescription.GenericTypeToken.Resolution.ForType extract(@MaybeNull String genericSignature) {
                        try {
                            return genericSignature == null ? LazyTypeDescription.GenericTypeToken.Resolution.Raw.INSTANCE : ForSignature.extract(genericSignature, new OfType());
                        }
                        catch (RuntimeException ignored) {
                            return LazyTypeDescription.GenericTypeToken.Resolution.Malformed.INSTANCE;
                        }
                    }

                    @Override
                    public SignatureVisitor visitSuperclass() {
                        this.collectTypeParameter();
                        return new GenericTypeExtractor(new SuperClassRegistrant());
                    }

                    @Override
                    public SignatureVisitor visitInterface() {
                        return new GenericTypeExtractor(new InterfaceTypeRegistrant());
                    }

                    @Override
                    public LazyTypeDescription.GenericTypeToken.Resolution.ForType resolve() {
                        return new LazyTypeDescription.GenericTypeToken.Resolution.ForType.Tokenized(this.superClassToken, this.interfaceTypeTokens, this.typeVariableTokens);
                    }

                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class InterfaceTypeRegistrant
                    implements GenericTypeRegistrant {
                        protected InterfaceTypeRegistrant() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            OfType.this.interfaceTypeTokens.add(token);
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
                            return OfType.this.equals(((InterfaceTypeRegistrant)object).OfType.this);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + OfType.this.hashCode();
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class SuperClassRegistrant
                    implements GenericTypeRegistrant {
                        protected SuperClassRegistrant() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            OfType.this.superClassToken = token;
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
                            return OfType.this.equals(((SuperClassRegistrant)object).OfType.this);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + OfType.this.hashCode();
                        }
                    }
                }
            }

            protected static interface IncompleteToken {
                public SignatureVisitor appendLowerBound();

                public SignatureVisitor appendUpperBound();

                public SignatureVisitor appendDirectBound();

                public void appendPlaceholder();

                public boolean isParameterized();

                public String getName();

                public LazyTypeDescription.GenericTypeToken toToken();

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForInnerClass
                extends AbstractBase {
                    private static final char INNER_CLASS_SEPARATOR = '$';
                    private final String internalName;
                    private final IncompleteToken outerTypeToken;

                    public ForInnerClass(String internalName, IncompleteToken outerTypeToken) {
                        this.internalName = internalName;
                        this.outerTypeToken = outerTypeToken;
                    }

                    public LazyTypeDescription.GenericTypeToken toToken() {
                        return this.isParameterized() || this.outerTypeToken.isParameterized() ? new LazyTypeDescription.GenericTypeToken.ForParameterizedType.Nested(this.getName(), this.parameters, this.outerTypeToken.toToken()) : new LazyTypeDescription.GenericTypeToken.ForRawType(this.getName());
                    }

                    public boolean isParameterized() {
                        return !this.parameters.isEmpty() || !this.outerTypeToken.isParameterized();
                    }

                    public String getName() {
                        return this.outerTypeToken.getName() + '$' + this.internalName.replace('/', '.');
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
                        if (!this.internalName.equals(((ForInnerClass)object).internalName)) {
                            return false;
                        }
                        return this.outerTypeToken.equals(((ForInnerClass)object).outerTypeToken);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.internalName.hashCode()) * 31 + this.outerTypeToken.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForTopLevelType
                extends AbstractBase {
                    private final String internalName;

                    public ForTopLevelType(String internalName) {
                        this.internalName = internalName;
                    }

                    public LazyTypeDescription.GenericTypeToken toToken() {
                        return this.isParameterized() ? new LazyTypeDescription.GenericTypeToken.ForParameterizedType(this.getName(), this.parameters) : new LazyTypeDescription.GenericTypeToken.ForRawType(this.getName());
                    }

                    public boolean isParameterized() {
                        return !this.parameters.isEmpty();
                    }

                    public String getName() {
                        return this.internalName.replace('/', '.');
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
                        return this.internalName.equals(((ForTopLevelType)object).internalName);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.internalName.hashCode();
                    }
                }

                public static abstract class AbstractBase
                implements IncompleteToken {
                    protected final List<LazyTypeDescription.GenericTypeToken> parameters = new ArrayList<LazyTypeDescription.GenericTypeToken>();

                    public SignatureVisitor appendDirectBound() {
                        return new GenericTypeExtractor(new ForDirectBound());
                    }

                    public SignatureVisitor appendUpperBound() {
                        return new GenericTypeExtractor(new ForUpperBound());
                    }

                    public SignatureVisitor appendLowerBound() {
                        return new GenericTypeExtractor(new ForLowerBound());
                    }

                    public void appendPlaceholder() {
                        this.parameters.add(LazyTypeDescription.GenericTypeToken.ForUnboundWildcard.INSTANCE);
                    }

                    protected class ForLowerBound
                    implements GenericTypeRegistrant {
                        protected ForLowerBound() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            AbstractBase.this.parameters.add(new LazyTypeDescription.GenericTypeToken.ForLowerBoundWildcard(token));
                        }
                    }

                    protected class ForUpperBound
                    implements GenericTypeRegistrant {
                        protected ForUpperBound() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            AbstractBase.this.parameters.add(new LazyTypeDescription.GenericTypeToken.ForUpperBoundWildcard(token));
                        }
                    }

                    protected class ForDirectBound
                    implements GenericTypeRegistrant {
                        protected ForDirectBound() {
                        }

                        public void register(LazyTypeDescription.GenericTypeToken token) {
                            AbstractBase.this.parameters.add(token);
                        }
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class ParameterBag {
            private final Type[] parameterType;
            private final Map<Integer, String> parameterRegistry;

            protected ParameterBag(Type[] parameterType) {
                this.parameterType = parameterType;
                this.parameterRegistry = new HashMap<Integer, String>();
            }

            protected void register(int offset, String name) {
                this.parameterRegistry.put(offset, name);
            }

            protected List<LazyTypeDescription.MethodToken.ParameterToken> resolve(boolean isStatic) {
                ArrayList<LazyTypeDescription.MethodToken.ParameterToken> parameterTokens = new ArrayList<LazyTypeDescription.MethodToken.ParameterToken>(this.parameterType.length);
                int offset = isStatic ? StackSize.ZERO.getSize() : StackSize.SINGLE.getSize();
                for (Type aParameterType : this.parameterType) {
                    String name = this.parameterRegistry.get(offset);
                    parameterTokens.add(name == null ? new LazyTypeDescription.MethodToken.ParameterToken() : new LazyTypeDescription.MethodToken.ParameterToken(name));
                    offset += aParameterType.getSize();
                }
                return parameterTokens;
            }
        }

        protected static interface GenericTypeRegistrant {
            public void register(LazyTypeDescription.GenericTypeToken var1);

            public static class RejectingSignatureVisitor
            extends SignatureVisitor {
                private static final String MESSAGE = "Unexpected token in generic signature";

                public RejectingSignatureVisitor() {
                    super(OpenedClassReader.ASM_API);
                }

                public void visitFormalTypeParameter(String name) {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitClassBound() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitInterfaceBound() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitSuperclass() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitInterface() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitParameterType() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitReturnType() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitExceptionType() {
                    throw new IllegalStateException(MESSAGE);
                }

                public void visitBaseType(char descriptor) {
                    throw new IllegalStateException(MESSAGE);
                }

                public void visitTypeVariable(String name) {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitArrayType() {
                    throw new IllegalStateException(MESSAGE);
                }

                public void visitClassType(String name) {
                    throw new IllegalStateException(MESSAGE);
                }

                public void visitInnerClassType(String name) {
                    throw new IllegalStateException(MESSAGE);
                }

                public void visitTypeArgument() {
                    throw new IllegalStateException(MESSAGE);
                }

                public SignatureVisitor visitTypeArgument(char wildcard) {
                    throw new IllegalStateException(MESSAGE);
                }

                public void visitEnd() {
                    throw new IllegalStateException(MESSAGE);
                }
            }
        }

        protected static interface ComponentTypeLocator {
            public AbstractBase.ComponentTypeReference bind(String var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForArrayType
            implements ComponentTypeLocator,
            AbstractBase.ComponentTypeReference {
                private final String componentType;

                public ForArrayType(String methodDescriptor) {
                    String arrayType = Type.getMethodType(methodDescriptor).getReturnType().getClassName();
                    this.componentType = arrayType.substring(0, arrayType.length() - 2);
                }

                public AbstractBase.ComponentTypeReference bind(String name) {
                    return this;
                }

                public String resolve() {
                    return this.componentType;
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
                    return this.componentType.equals(((ForArrayType)object).componentType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.componentType.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForAnnotationProperty
            implements ComponentTypeLocator {
                private final TypePool typePool;
                private final String annotationName;

                public ForAnnotationProperty(TypePool typePool, String annotationDescriptor) {
                    this.typePool = typePool;
                    this.annotationName = annotationDescriptor.substring(1, annotationDescriptor.length() - 1).replace('/', '.');
                }

                public AbstractBase.ComponentTypeReference bind(String name) {
                    return new Bound(name);
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
                    if (!this.annotationName.equals(((ForAnnotationProperty)object).annotationName)) {
                        return false;
                    }
                    return this.typePool.equals(((ForAnnotationProperty)object).typePool);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.typePool.hashCode()) * 31 + this.annotationName.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class Bound
                implements AbstractBase.ComponentTypeReference {
                    private final String name;

                    protected Bound(String name) {
                        this.name = name;
                    }

                    @MaybeNull
                    public String resolve() {
                        TypeDescription componentType = ((MethodDescription.InDefinedShape)((MethodList)ForAnnotationProperty.this.typePool.describe(ForAnnotationProperty.this.annotationName).resolve().getDeclaredMethods().filter(ElementMatchers.named(this.name))).getOnly()).getReturnType().asErasure().getComponentType();
                        return componentType == null ? NO_ARRAY : componentType.getName();
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
                        if (!this.name.equals(((Bound)object).name)) {
                            return false;
                        }
                        return ForAnnotationProperty.this.equals(((Bound)object).ForAnnotationProperty.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + ForAnnotationProperty.this.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Illegal implements ComponentTypeLocator
            {
                INSTANCE;


                @Override
                public AbstractBase.ComponentTypeReference bind(String name) {
                    throw new IllegalStateException("Unexpected lookup of component type for " + name);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface AnnotationRegistrant {
            public void register(String var1, AnnotationValue<?, ?> var2);

            public void onComplete();

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForTypeVariable
            extends AbstractBase.ForTypeVariable {
                private final Map<String, List<LazyTypeDescription.AnnotationToken>> pathMap;

                protected ForTypeVariable(String descriptor, @MaybeNull TypePath typePath, Map<String, List<LazyTypeDescription.AnnotationToken>> pathMap) {
                    super(descriptor, typePath);
                    this.pathMap = pathMap;
                }

                @Override
                protected Map<String, List<LazyTypeDescription.AnnotationToken>> getPathMap() {
                    return this.pathMap;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class WithIndex
                extends AbstractBase.ForTypeVariable.WithIndex {
                    private final Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> indexedPathMap;

                    protected WithIndex(String descriptor, @MaybeNull TypePath typePath, int index, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> indexedPathMap) {
                        super(descriptor, typePath, index);
                        this.indexedPathMap = indexedPathMap;
                    }

                    @Override
                    protected Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> getIndexedPathMap() {
                        return this.indexedPathMap;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static class DoubleIndexed
                    extends AbstractBase.ForTypeVariable.WithIndex.DoubleIndexed {
                        private final Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> doubleIndexedPathMap;

                        protected DoubleIndexed(String descriptor, @MaybeNull TypePath typePath, int index, int preIndex, Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> doubleIndexedPathMap) {
                            super(descriptor, typePath, index, preIndex);
                            this.doubleIndexedPathMap = doubleIndexedPathMap;
                        }

                        @Override
                        protected Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> getDoubleIndexedPathMap() {
                            return this.doubleIndexedPathMap;
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForByteCodeElement
            extends AbstractBase {
                private final List<LazyTypeDescription.AnnotationToken> annotationTokens;

                protected ForByteCodeElement(String descriptor, List<LazyTypeDescription.AnnotationToken> annotationTokens) {
                    super(descriptor);
                    this.annotationTokens = annotationTokens;
                }

                @Override
                protected List<LazyTypeDescription.AnnotationToken> getTokens() {
                    return this.annotationTokens;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class WithIndex
                extends AbstractBase {
                    private final int index;
                    private final Map<Integer, List<LazyTypeDescription.AnnotationToken>> annotationTokens;

                    protected WithIndex(String descriptor, int index, Map<Integer, List<LazyTypeDescription.AnnotationToken>> annotationTokens) {
                        super(descriptor);
                        this.index = index;
                        this.annotationTokens = annotationTokens;
                    }

                    @Override
                    protected List<LazyTypeDescription.AnnotationToken> getTokens() {
                        List<LazyTypeDescription.AnnotationToken> annotationTokens = this.annotationTokens.get(this.index);
                        if (annotationTokens == null) {
                            annotationTokens = new ArrayList<LazyTypeDescription.AnnotationToken>();
                            this.annotationTokens.put(this.index, annotationTokens);
                        }
                        return annotationTokens;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class AbstractBase
            implements AnnotationRegistrant {
                private final String descriptor;
                private final Map<String, AnnotationValue<?, ?>> values;

                protected AbstractBase(String descriptor) {
                    this.descriptor = descriptor;
                    this.values = new HashMap();
                }

                @Override
                public void register(String name, AnnotationValue<?, ?> annotationValue) {
                    this.values.put(name, annotationValue);
                }

                @Override
                public void onComplete() {
                    this.getTokens().add(new LazyTypeDescription.AnnotationToken(this.descriptor, this.values));
                }

                protected abstract List<LazyTypeDescription.AnnotationToken> getTokens();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static abstract class ForTypeVariable
                extends AbstractBase {
                    private final String typePath;

                    protected ForTypeVariable(String descriptor, @MaybeNull TypePath typePath) {
                        super(descriptor);
                        this.typePath = typePath == null ? "" : typePath.toString();
                    }

                    @Override
                    protected List<LazyTypeDescription.AnnotationToken> getTokens() {
                        Map<String, List<LazyTypeDescription.AnnotationToken>> pathMap = this.getPathMap();
                        List<LazyTypeDescription.AnnotationToken> tokens = pathMap.get(this.typePath);
                        if (tokens == null) {
                            tokens = new ArrayList<LazyTypeDescription.AnnotationToken>();
                            pathMap.put(this.typePath, tokens);
                        }
                        return tokens;
                    }

                    protected abstract Map<String, List<LazyTypeDescription.AnnotationToken>> getPathMap();

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static abstract class WithIndex
                    extends ForTypeVariable {
                        private final int index;

                        protected WithIndex(String descriptor, @MaybeNull TypePath typePath, int index) {
                            super(descriptor, typePath);
                            this.index = index;
                        }

                        @Override
                        protected Map<String, List<LazyTypeDescription.AnnotationToken>> getPathMap() {
                            Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> indexedPathMap = this.getIndexedPathMap();
                            Map<String, List<LazyTypeDescription.AnnotationToken>> pathMap = indexedPathMap.get(this.index);
                            if (pathMap == null) {
                                pathMap = new HashMap<String, List<LazyTypeDescription.AnnotationToken>>();
                                indexedPathMap.put(this.index, pathMap);
                            }
                            return pathMap;
                        }

                        protected abstract Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> getIndexedPathMap();

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static abstract class DoubleIndexed
                        extends WithIndex {
                            private final int preIndex;

                            protected DoubleIndexed(String descriptor, @MaybeNull TypePath typePath, int index, int preIndex) {
                                super(descriptor, typePath, index);
                                this.preIndex = preIndex;
                            }

                            @Override
                            protected Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> getIndexedPathMap() {
                                Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> doubleIndexPathMap = this.getDoubleIndexedPathMap();
                                Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>> indexedPathMap = doubleIndexPathMap.get(this.preIndex);
                                if (indexedPathMap == null) {
                                    indexedPathMap = new HashMap<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>();
                                    doubleIndexPathMap.put(this.preIndex, indexedPathMap);
                                }
                                return indexedPathMap;
                            }

                            protected abstract Map<Integer, Map<Integer, Map<String, List<LazyTypeDescription.AnnotationToken>>>> getDoubleIndexedPathMap();
                        }
                    }
                }
            }
        }

        public static class WithLazyResolution
        extends Default {
            public WithLazyResolution(CacheProvider cacheProvider, ClassFileLocator classFileLocator, ReaderMode readerMode) {
                this(cacheProvider, classFileLocator, readerMode, Empty.INSTANCE);
            }

            public WithLazyResolution(CacheProvider cacheProvider, ClassFileLocator classFileLocator, ReaderMode readerMode, TypePool parentPool) {
                super(cacheProvider, classFileLocator, readerMode, parentPool);
            }

            public static TypePool ofSystemLoader() {
                return WithLazyResolution.of(ClassFileLocator.ForClassLoader.ofSystemLoader());
            }

            public static TypePool ofPlatformLoader() {
                return WithLazyResolution.of(ClassFileLocator.ForClassLoader.ofPlatformLoader());
            }

            public static TypePool ofBootLoader() {
                return WithLazyResolution.of(ClassFileLocator.ForClassLoader.ofBootLoader());
            }

            public static TypePool of(@MaybeNull ClassLoader classLoader) {
                return WithLazyResolution.of(ClassFileLocator.ForClassLoader.of(classLoader));
            }

            public static TypePool of(ClassFileLocator classFileLocator) {
                return new WithLazyResolution(new CacheProvider.Simple(), classFileLocator, ReaderMode.FAST);
            }

            protected Resolution doDescribe(String name) {
                return new LazyResolution(name);
            }

            protected Resolution doCache(String name, Resolution resolution) {
                return resolution;
            }

            protected Resolution doResolve(String name) {
                Resolution resolution = this.cacheProvider.find(name);
                if (resolution == null) {
                    resolution = this.cacheProvider.register(name, WithLazyResolution.super.doDescribe(name));
                }
                return resolution;
            }

            protected class LazyTypeDescription
            extends TypeDescription.AbstractBase.OfSimpleType.WithDelegation {
                private final String name;
                private transient /* synthetic */ TypeDescription delegate;

                protected LazyTypeDescription(String name) {
                    this.name = name;
                }

                public String getName() {
                    return this.name;
                }

                @CachedReturnPlugin.Enhance(value="delegate")
                protected TypeDescription delegate() {
                    TypeDescription typeDescription;
                    TypeDescription typeDescription2;
                    TypeDescription typeDescription3 = this.delegate;
                    if (typeDescription3 != null) {
                        typeDescription2 = null;
                    } else {
                        typeDescription = this;
                        typeDescription2 = typeDescription = typeDescription.WithLazyResolution.this.doResolve(typeDescription.name).resolve();
                    }
                    if (typeDescription == null) {
                        typeDescription = this.delegate;
                    } else {
                        this.delegate = typeDescription;
                    }
                    return typeDescription;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class LazyResolution
            implements Resolution {
                private final String name;

                protected LazyResolution(String name) {
                    this.name = name;
                }

                public boolean isResolved() {
                    return WithLazyResolution.this.doResolve(this.name).isResolved();
                }

                public TypeDescription resolve() {
                    return new LazyTypeDescription(this.name);
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
                    if (!this.name.equals(((LazyResolution)object).name)) {
                        return false;
                    }
                    return WithLazyResolution.this.equals(((LazyResolution)object).WithLazyResolution.this);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + WithLazyResolution.this.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ReaderMode {
            EXTENDED(4),
            FAST(1);

            private final int flags;

            private ReaderMode(int flags) {
                this.flags = flags;
            }

            protected int getFlags() {
                return this.flags;
            }

            public boolean isExtended() {
                return this == EXTENDED;
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class AbstractBase
    implements TypePool {
        protected static final Map<String, TypeDescription> PRIMITIVE_TYPES;
        protected static final Map<String, String> PRIMITIVE_DESCRIPTORS;
        private static final String ARRAY_SYMBOL = "[";
        protected final CacheProvider cacheProvider;

        protected AbstractBase(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        public Resolution describe(String name) {
            TypeDescription typeDescription;
            Resolution resolution;
            if (name.contains("/")) {
                throw new IllegalArgumentException(name + " contains the illegal character '/'");
            }
            int arity = 0;
            while (name.startsWith(ARRAY_SYMBOL)) {
                ++arity;
                name = name.substring(1);
            }
            if (arity > 0) {
                String primitiveName = PRIMITIVE_DESCRIPTORS.get(name);
                name = primitiveName == null ? name.substring(1, name.length() - 1) : primitiveName;
            }
            Resolution resolution2 = resolution = (typeDescription = PRIMITIVE_TYPES.get(name)) == null ? this.cacheProvider.find(name) : new Resolution.Simple(typeDescription);
            if (resolution == null) {
                resolution = this.doCache(name, this.doDescribe(name));
            }
            return ArrayTypeResolution.of(resolution, arity);
        }

        protected Resolution doCache(String name, Resolution resolution) {
            return this.cacheProvider.register(name, resolution);
        }

        public void clear() {
            this.cacheProvider.clear();
        }

        protected abstract Resolution doDescribe(String var1);

        static {
            HashMap<String, TypeDescription> primitiveTypes = new HashMap<String, TypeDescription>();
            HashMap<String, String> primitiveDescriptors = new HashMap<String, String>();
            for (Class type : new Class[]{Boolean.TYPE, Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE}) {
                primitiveTypes.put(type.getName(), TypeDescription.ForLoadedType.of(type));
                primitiveDescriptors.put(Type.getDescriptor(type), type.getName());
            }
            PRIMITIVE_TYPES = Collections.unmodifiableMap(primitiveTypes);
            PRIMITIVE_DESCRIPTORS = Collections.unmodifiableMap(primitiveDescriptors);
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
            return this.cacheProvider.equals(((AbstractBase)object).cacheProvider);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.cacheProvider.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class ArrayTypeResolution
        implements Resolution {
            private final Resolution resolution;
            private final int arity;

            protected ArrayTypeResolution(Resolution resolution, int arity) {
                this.resolution = resolution;
                this.arity = arity;
            }

            protected static Resolution of(Resolution resolution, int arity) {
                return arity == 0 ? resolution : new ArrayTypeResolution(resolution, arity);
            }

            public boolean isResolved() {
                return this.resolution.isResolved();
            }

            public TypeDescription resolve() {
                return TypeDescription.ArrayProjection.of(this.resolution.resolve(), this.arity);
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
                if (this.arity != ((ArrayTypeResolution)object).arity) {
                    return false;
                }
                return this.resolution.equals(((ArrayTypeResolution)object).resolution);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.resolution.hashCode()) * 31 + this.arity;
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class Hierarchical
        extends AbstractBase {
            private final TypePool parent;

            protected Hierarchical(CacheProvider cacheProvider, TypePool parent) {
                super(cacheProvider);
                this.parent = parent;
            }

            public Resolution describe(String name) {
                Resolution resolution = this.parent.describe(name);
                return resolution.isResolved() ? resolution : super.describe(name);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void clear() {
                try {
                    this.parent.clear();
                    Object var2_1 = null;
                    super.clear();
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    super.clear();
                    throw throwable;
                }
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
                return this.parent.equals(((Hierarchical)object).parent);
            }

            public int hashCode() {
                return super.hashCode() * 31 + this.parent.hashCode();
            }
        }

        protected static interface ComponentTypeReference {
            @MaybeNull
            public static final String NO_ARRAY = null;

            @MaybeNull
            public String resolve();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Empty implements TypePool
    {
        INSTANCE;


        @Override
        public Resolution describe(String name) {
            return new Resolution.Illegal(name);
        }

        @Override
        public void clear() {
        }
    }

    public static interface CacheProvider {
        @MaybeNull
        public static final Resolution UNRESOLVED = null;

        @MaybeNull
        public Resolution find(String var1);

        public Resolution register(String var1, Resolution var2);

        public void clear();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Discriminating
        implements CacheProvider {
            private final ElementMatcher<String> matcher;
            private final CacheProvider matched;
            private final CacheProvider unmatched;

            public Discriminating(ElementMatcher<String> matcher, CacheProvider matched, CacheProvider unmatched) {
                this.matcher = matcher;
                this.matched = matched;
                this.unmatched = unmatched;
            }

            @Override
            @MaybeNull
            public Resolution find(String name) {
                return (this.matcher.matches(name) ? this.matched : this.unmatched).find(name);
            }

            @Override
            public Resolution register(String name, Resolution resolution) {
                return (this.matcher.matches(name) ? this.matched : this.unmatched).register(name, resolution);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void clear() {
                try {
                    this.unmatched.clear();
                    Object var2_1 = null;
                    this.matched.clear();
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    this.matched.clear();
                    throw throwable;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Simple
        implements CacheProvider {
            private final ConcurrentMap<String, Resolution> storage;

            public Simple() {
                this(new ConcurrentHashMap<String, Resolution>());
            }

            public Simple(ConcurrentMap<String, Resolution> storage) {
                this.storage = storage;
            }

            public static CacheProvider withObjectType() {
                Simple cacheProvider = new Simple();
                cacheProvider.register(Object.class.getName(), new Resolution.Simple(TypeDescription.ForLoadedType.of(Object.class)));
                return cacheProvider;
            }

            @Override
            @MaybeNull
            public Resolution find(String name) {
                return (Resolution)this.storage.get(name);
            }

            @Override
            public Resolution register(String name, Resolution resolution) {
                Resolution cached = this.storage.putIfAbsent(name, resolution);
                return cached == null ? resolution : cached;
            }

            @Override
            public void clear() {
                this.storage.clear();
            }

            public ConcurrentMap<String, Resolution> getStorage() {
                return this.storage;
            }

            public static class UsingSoftReference
            implements CacheProvider {
                private final AtomicReference<SoftReference<Simple>> delegate = new AtomicReference<SoftReference<Simple>>(new SoftReference<Simple>(new Simple()));

                @MaybeNull
                public Resolution find(String name) {
                    CacheProvider provider = this.delegate.get().get();
                    return provider == null ? UNRESOLVED : provider.find(name);
                }

                public Resolution register(String name, Resolution resolution) {
                    SoftReference<Simple> reference = this.delegate.get();
                    Simple provider = reference.get();
                    if (provider == null) {
                        provider = new Simple();
                        while (!this.delegate.compareAndSet(reference, new SoftReference<Simple>(provider))) {
                            reference = this.delegate.get();
                            Simple previous = reference.get();
                            if (previous == null) continue;
                            provider = previous;
                            break;
                        }
                    }
                    return provider.register(name, resolution);
                }

                public void clear() {
                    CacheProvider provider = this.delegate.get().get();
                    if (provider != null) {
                        provider.clear();
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements CacheProvider
        {
            INSTANCE;


            @Override
            @MaybeNull
            public Resolution find(String name) {
                return UNRESOLVED;
            }

            @Override
            public Resolution register(String name, Resolution resolution) {
                return resolution;
            }

            @Override
            public void clear() {
            }
        }
    }

    public static interface Resolution {
        public boolean isResolved();

        public TypeDescription resolve();

        public static class NoSuchTypeException
        extends IllegalStateException {
            private static final long serialVersionUID = 1L;
            private final String name;

            public NoSuchTypeException(String name) {
                super("Cannot resolve type description for " + name);
                this.name = name;
            }

            public String getName() {
                return this.name;
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Illegal
        implements Resolution {
            private final String name;

            public Illegal(String name) {
                this.name = name;
            }

            public boolean isResolved() {
                return false;
            }

            public TypeDescription resolve() {
                throw new NoSuchTypeException(this.name);
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
                return this.name.equals(((Illegal)object).name);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.name.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements Resolution {
            private final TypeDescription typeDescription;

            public Simple(TypeDescription typeDescription) {
                this.typeDescription = typeDescription;
            }

            public boolean isResolved() {
                return true;
            }

            public TypeDescription resolve() {
                return this.typeDescription;
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
                return this.typeDescription.equals(((Simple)object).typeDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
            }
        }
    }
}


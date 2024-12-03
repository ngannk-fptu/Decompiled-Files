/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.EnumerationState;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.scaffold.ClassWriterStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.inline.DecoratingDynamicTypeBuilder;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.dynamic.scaffold.inline.RebaseDynamicTypeBuilder;
import net.bytebuddy.dynamic.scaffold.inline.RedefinitionDynamicTypeBuilder;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.SubclassDynamicTypeBuilder;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.GraalImageCode;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ByteBuddy {
    public static final String DEFAULT_NAMING_PROPERTY = "net.bytebuddy.naming";
    private static final String BYTE_BUDDY_DEFAULT_PREFIX = "ByteBuddy";
    private static final String BYTE_BUDDY_DEFAULT_SUFFIX = "auxiliary";
    private static final String BYTE_BUDDY_DEFAULT_CONTEXT_NAME = "synthetic";
    @MaybeNull
    private static final NamingStrategy DEFAULT_NAMING_STRATEGY;
    @MaybeNull
    private static final AuxiliaryType.NamingStrategy DEFAULT_AUXILIARY_NAMING_STRATEGY;
    @MaybeNull
    private static final Implementation.Context.Factory DEFAULT_IMPLEMENTATION_CONTEXT_FACTORY;
    protected final ClassFileVersion classFileVersion;
    protected final NamingStrategy namingStrategy;
    protected final AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;
    protected final AnnotationValueFilter.Factory annotationValueFilterFactory;
    protected final AnnotationRetention annotationRetention;
    protected final Implementation.Context.Factory implementationContextFactory;
    protected final MethodGraph.Compiler methodGraphCompiler;
    protected final InstrumentedType.Factory instrumentedTypeFactory;
    protected final LatentMatcher<? super MethodDescription> ignoredMethods;
    protected final TypeValidation typeValidation;
    protected final VisibilityBridgeStrategy visibilityBridgeStrategy;
    protected final ClassWriterStrategy classWriterStrategy;
    private static final boolean ACCESS_CONTROLLER;

    @MaybeNull
    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public ByteBuddy() {
        this(ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5));
    }

    public ByteBuddy(ClassFileVersion classFileVersion) {
        this(classFileVersion, DEFAULT_NAMING_STRATEGY == null ? new NamingStrategy.SuffixingRandom(BYTE_BUDDY_DEFAULT_PREFIX) : DEFAULT_NAMING_STRATEGY, DEFAULT_AUXILIARY_NAMING_STRATEGY == null ? new AuxiliaryType.NamingStrategy.SuffixingRandom(BYTE_BUDDY_DEFAULT_SUFFIX) : DEFAULT_AUXILIARY_NAMING_STRATEGY, AnnotationValueFilter.Default.APPEND_DEFAULTS, AnnotationRetention.ENABLED, DEFAULT_IMPLEMENTATION_CONTEXT_FACTORY == null ? Implementation.Context.Default.Factory.INSTANCE : DEFAULT_IMPLEMENTATION_CONTEXT_FACTORY, MethodGraph.Compiler.DEFAULT, InstrumentedType.Factory.Default.MODIFIABLE, TypeValidation.ENABLED, VisibilityBridgeStrategy.Default.ALWAYS, ClassWriterStrategy.Default.CONSTANT_POOL_RETAINING, new LatentMatcher.Resolved(ElementMatchers.isSynthetic().or(ElementMatchers.isDefaultFinalizer())));
    }

    protected ByteBuddy(ClassFileVersion classFileVersion, NamingStrategy namingStrategy, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, InstrumentedType.Factory instrumentedTypeFactory, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods) {
        this.classFileVersion = classFileVersion;
        this.namingStrategy = namingStrategy;
        this.auxiliaryTypeNamingStrategy = auxiliaryTypeNamingStrategy;
        this.annotationValueFilterFactory = annotationValueFilterFactory;
        this.annotationRetention = annotationRetention;
        this.implementationContextFactory = implementationContextFactory;
        this.methodGraphCompiler = methodGraphCompiler;
        this.instrumentedTypeFactory = instrumentedTypeFactory;
        this.typeValidation = typeValidation;
        this.visibilityBridgeStrategy = visibilityBridgeStrategy;
        this.classWriterStrategy = classWriterStrategy;
        this.ignoredMethods = ignoredMethods;
    }

    public <T> DynamicType.Builder<T> subclass(Class<T> superType) {
        return this.subclass(TypeDescription.ForLoadedType.of(superType));
    }

    public <T> DynamicType.Builder<T> subclass(Class<T> superType, ConstructorStrategy constructorStrategy) {
        return this.subclass(TypeDescription.ForLoadedType.of(superType), constructorStrategy);
    }

    public DynamicType.Builder<?> subclass(Type superType) {
        return this.subclass(TypeDefinition.Sort.describe(superType));
    }

    public DynamicType.Builder<?> subclass(Type superType, ConstructorStrategy constructorStrategy) {
        return this.subclass(TypeDefinition.Sort.describe(superType), constructorStrategy);
    }

    public DynamicType.Builder<?> subclass(TypeDefinition superType) {
        return this.subclass(superType, (ConstructorStrategy)ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING);
    }

    public DynamicType.Builder<?> subclass(TypeDefinition superType, ConstructorStrategy constructorStrategy) {
        AbstractList interfaceTypes;
        TypeDescription.Generic actualSuperType;
        if (superType.isPrimitive() || superType.isArray() || superType.isFinal()) {
            throw new IllegalArgumentException("Cannot subclass primitive, array or final types: " + superType);
        }
        if (superType.isInterface()) {
            actualSuperType = TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class);
            interfaceTypes = new TypeList.Generic.Explicit(superType);
        } else {
            actualSuperType = superType.asGenericType();
            interfaceTypes = new TypeList.Generic.Empty();
        }
        return new SubclassDynamicTypeBuilder(this.instrumentedTypeFactory.subclass(this.namingStrategy.subclass(superType.asGenericType()), ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.PLAIN).resolve(superType.getModifiers()), actualSuperType).withInterfaces((TypeList.Generic)((Object)interfaceTypes)), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, constructorStrategy);
    }

    public DynamicType.Builder<?> makeInterface() {
        return this.makeInterface(Collections.emptyList());
    }

    public <T> DynamicType.Builder<T> makeInterface(Class<T> interfaceType) {
        return this.makeInterface(Collections.singletonList(interfaceType));
    }

    public DynamicType.Builder<?> makeInterface(Type ... interfaceType) {
        return this.makeInterface(Arrays.asList(interfaceType));
    }

    public DynamicType.Builder<?> makeInterface(List<? extends Type> interfaceTypes) {
        return this.makeInterface(new TypeList.Generic.ForLoadedTypes(interfaceTypes));
    }

    public DynamicType.Builder<?> makeInterface(TypeDefinition ... interfaceType) {
        return this.makeInterface((Collection<? extends TypeDefinition>)Arrays.asList(interfaceType));
    }

    public DynamicType.Builder<?> makeInterface(Collection<? extends TypeDefinition> interfaceTypes) {
        return this.subclass(Object.class, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).implement(interfaceTypes).modifiers(TypeManifestation.INTERFACE, Visibility.PUBLIC);
    }

    public DynamicType.Builder<?> makePackage(String name) {
        return new SubclassDynamicTypeBuilder(this.instrumentedTypeFactory.subclass(name + "." + "package-info", 5632, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class)), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, ConstructorStrategy.Default.NO_CONSTRUCTORS);
    }

    public DynamicType.Builder<?> makeRecord() {
        TypeDescription.Generic record = InstrumentedType.Default.of(JavaType.RECORD.getTypeStub().getName(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), Visibility.PUBLIC).withMethod(new MethodDescription.Token(4)).withMethod(new MethodDescription.Token("hashCode", 1025, TypeDescription.ForLoadedType.of(Integer.TYPE).asGenericType())).withMethod(new MethodDescription.Token("equals", 1025, TypeDescription.ForLoadedType.of(Boolean.TYPE).asGenericType(), Collections.singletonList(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class)))).withMethod(new MethodDescription.Token("toString", 1025, TypeDescription.ForLoadedType.of(String.class).asGenericType())).asGenericType();
        return new SubclassDynamicTypeBuilder(this.instrumentedTypeFactory.subclass(this.namingStrategy.subclass(record), 17, record).withRecord(true), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, RecordConstructorStrategy.INSTANCE).method(ElementMatchers.isHashCode()).intercept(RecordObjectMethod.HASH_CODE).method(ElementMatchers.isEquals()).intercept(RecordObjectMethod.EQUALS).method(ElementMatchers.isToString()).intercept(RecordObjectMethod.TO_STRING);
    }

    public DynamicType.Builder<? extends Annotation> makeAnnotation() {
        return new SubclassDynamicTypeBuilder(this.instrumentedTypeFactory.subclass(this.namingStrategy.subclass(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Annotation.class)), ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.ANNOTATION).resolve(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class)).withInterfaces(new TypeList.Generic.Explicit(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Annotation.class))), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, ConstructorStrategy.Default.NO_CONSTRUCTORS);
    }

    public DynamicType.Builder<? extends Enum<?>> makeEnumeration(String ... value) {
        return this.makeEnumeration(Arrays.asList(value));
    }

    public DynamicType.Builder<? extends Enum<?>> makeEnumeration(Collection<? extends String> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Require at least one enumeration constant");
        }
        TypeDescription.Generic enumType = TypeDescription.Generic.Builder.parameterizedType(Enum.class, new Type[]{TargetType.class}).build();
        return new SubclassDynamicTypeBuilder(this.instrumentedTypeFactory.subclass(this.namingStrategy.subclass(enumType), ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.FINAL, EnumerationState.ENUMERATION).resolve(), enumType), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, ConstructorStrategy.Default.NO_CONSTRUCTORS).defineConstructor(Visibility.PRIVATE).withParameters(new Type[]{String.class, Integer.TYPE}).intercept(SuperMethodCall.INSTANCE).defineMethod("valueOf", (Type)((Object)TargetType.class), Visibility.PUBLIC, Ownership.STATIC).withParameters(new Type[]{String.class}).intercept(MethodCall.invoke((MethodDescription)((MethodList)enumType.getDeclaredMethods().filter(ElementMatchers.named("valueOf").and(ElementMatchers.takesArguments(Class.class, String.class)))).getOnly()).withOwnType().withArgument(0).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC)).defineMethod("values", (Type)((Object)TargetType[].class), Visibility.PUBLIC, Ownership.STATIC).intercept(new EnumerationImplementation(new ArrayList<String>(values)));
    }

    public <T> DynamicType.Builder<T> redefine(Class<T> type) {
        return this.redefine(type, ClassFileLocator.ForClassLoader.of(type.getClassLoader()));
    }

    public <T> DynamicType.Builder<T> redefine(Class<T> type, ClassFileLocator classFileLocator) {
        return this.redefine(TypeDescription.ForLoadedType.of(type), classFileLocator);
    }

    public <T> DynamicType.Builder<T> redefine(TypeDescription type, ClassFileLocator classFileLocator) {
        if (type.isArray() || type.isPrimitive()) {
            throw new IllegalArgumentException("Cannot redefine array or primitive type: " + type);
        }
        return new RedefinitionDynamicTypeBuilder(this.instrumentedTypeFactory.represent(type), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, type, classFileLocator);
    }

    public <T> DynamicType.Builder<T> rebase(Class<T> type) {
        return this.rebase(type, ClassFileLocator.ForClassLoader.of(type.getClassLoader()));
    }

    public <T> DynamicType.Builder<T> rebase(Class<T> type, ClassFileLocator classFileLocator) {
        return this.rebase(TypeDescription.ForLoadedType.of(type), classFileLocator);
    }

    public <T> DynamicType.Builder<T> rebase(Class<T> type, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer) {
        return this.rebase(TypeDescription.ForLoadedType.of(type), classFileLocator, methodNameTransformer);
    }

    public <T> DynamicType.Builder<T> rebase(TypeDescription type, ClassFileLocator classFileLocator) {
        return this.rebase(type, classFileLocator, MethodNameTransformer.Suffixing.withRandomSuffix());
    }

    public <T> DynamicType.Builder<T> rebase(TypeDescription type, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer) {
        if (type.isArray() || type.isPrimitive()) {
            throw new IllegalArgumentException("Cannot rebase array or primitive type: " + type);
        }
        return new RebaseDynamicTypeBuilder(this.instrumentedTypeFactory.represent(type), this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods, type, classFileLocator, methodNameTransformer);
    }

    public DynamicType.Builder<?> rebase(Package aPackage, ClassFileLocator classFileLocator) {
        return this.rebase(new PackageDescription.ForLoadedPackage(aPackage), classFileLocator);
    }

    public DynamicType.Builder<?> rebase(PackageDescription aPackage, ClassFileLocator classFileLocator) {
        return this.rebase(new TypeDescription.ForPackageDescription(aPackage), classFileLocator);
    }

    public <T> DynamicType.Builder<T> decorate(Class<T> type) {
        return this.decorate(type, ClassFileLocator.ForClassLoader.of(type.getClassLoader()));
    }

    public <T> DynamicType.Builder<T> decorate(Class<T> type, ClassFileLocator classFileLocator) {
        return this.decorate(TypeDescription.ForLoadedType.of(type), classFileLocator);
    }

    public <T> DynamicType.Builder<T> decorate(TypeDescription type, ClassFileLocator classFileLocator) {
        if (type.isArray() || type.isPrimitive()) {
            throw new IllegalArgumentException("Cannot decorate array or primitive type: " + type);
        }
        return new DecoratingDynamicTypeBuilder(type, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.typeValidation, this.classWriterStrategy, this.ignoredMethods, classFileLocator);
    }

    public ByteBuddy with(ClassFileVersion classFileVersion) {
        return new ByteBuddy(classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(NamingStrategy namingStrategy) {
        return new ByteBuddy(this.classFileVersion, namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(AnnotationValueFilter.Factory annotationValueFilterFactory) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(AnnotationRetention annotationRetention) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(Implementation.Context.Factory implementationContextFactory) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(MethodGraph.Compiler methodGraphCompiler) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(InstrumentedType.Factory instrumentedTypeFactory) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(TypeValidation typeValidation) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(VisibilityBridgeStrategy visibilityBridgeStrategy) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, visibilityBridgeStrategy, this.classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy with(ClassWriterStrategy classWriterStrategy) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, classWriterStrategy, this.ignoredMethods);
    }

    public ByteBuddy ignore(ElementMatcher<? super MethodDescription> ignoredMethods) {
        return this.ignore(new LatentMatcher.Resolved<MethodDescription>(ignoredMethods));
    }

    public ByteBuddy ignore(LatentMatcher<? super MethodDescription> ignoredMethods) {
        return new ByteBuddy(this.classFileVersion, this.namingStrategy, this.auxiliaryTypeNamingStrategy, this.annotationValueFilterFactory, this.annotationRetention, this.implementationContextFactory, this.methodGraphCompiler, this.instrumentedTypeFactory, this.typeValidation, this.visibilityBridgeStrategy, this.classWriterStrategy, ignoredMethods);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
        Implementation.Context.Default.Factory.WithFixedSuffix implementationContextFactory;
        AuxiliaryType.NamingStrategy.Suffixing auxiliaryNamingStrategy;
        NamingStrategy.Suffixing namingStrategy;
        String value;
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
        if ((value = ByteBuddy.doPrivileged(new GetSystemPropertyAction(DEFAULT_NAMING_PROPERTY))) == null) {
            if (GraalImageCode.getCurrent().isDefined()) {
                namingStrategy = new NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_PREFIX, new NamingStrategy.Suffixing.BaseNameResolver.WithCallerSuffix(NamingStrategy.Suffixing.BaseNameResolver.ForUnnamedType.INSTANCE), "net.bytebuddy.renamed");
                auxiliaryNamingStrategy = new AuxiliaryType.NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_SUFFIX);
                implementationContextFactory = new Implementation.Context.Default.Factory.WithFixedSuffix(BYTE_BUDDY_DEFAULT_CONTEXT_NAME);
            } else {
                namingStrategy = null;
                auxiliaryNamingStrategy = null;
                implementationContextFactory = null;
            }
        } else if (value.equalsIgnoreCase("fixed")) {
            namingStrategy = new NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_PREFIX, NamingStrategy.Suffixing.BaseNameResolver.ForUnnamedType.INSTANCE, "net.bytebuddy.renamed");
            auxiliaryNamingStrategy = new AuxiliaryType.NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_SUFFIX);
            implementationContextFactory = new Implementation.Context.Default.Factory.WithFixedSuffix(BYTE_BUDDY_DEFAULT_CONTEXT_NAME);
        } else if (value.equalsIgnoreCase("caller")) {
            namingStrategy = new NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_PREFIX, new NamingStrategy.Suffixing.BaseNameResolver.WithCallerSuffix(NamingStrategy.Suffixing.BaseNameResolver.ForUnnamedType.INSTANCE), "net.bytebuddy.renamed");
            auxiliaryNamingStrategy = new AuxiliaryType.NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_SUFFIX);
            implementationContextFactory = new Implementation.Context.Default.Factory.WithFixedSuffix(BYTE_BUDDY_DEFAULT_CONTEXT_NAME);
        } else {
            long seed;
            try {
                seed = Long.parseLong(value);
            }
            catch (Exception ignored) {
                throw new IllegalStateException("'net.bytebuddy.naming' is set to an unknown, non-numeric value: " + value);
            }
            namingStrategy = new NamingStrategy.SuffixingRandom(BYTE_BUDDY_DEFAULT_PREFIX, NamingStrategy.Suffixing.BaseNameResolver.ForUnnamedType.INSTANCE, "net.bytebuddy.renamed", new RandomString(8, new Random(seed)));
            auxiliaryNamingStrategy = new AuxiliaryType.NamingStrategy.Suffixing(BYTE_BUDDY_DEFAULT_SUFFIX);
            implementationContextFactory = new Implementation.Context.Default.Factory.WithFixedSuffix(BYTE_BUDDY_DEFAULT_CONTEXT_NAME);
        }
        DEFAULT_NAMING_STRATEGY = namingStrategy;
        DEFAULT_AUXILIARY_NAMING_STRATEGY = auxiliaryNamingStrategy;
        DEFAULT_IMPLEMENTATION_CONTEXT_FACTORY = implementationContextFactory;
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
        if (!this.annotationRetention.equals((Object)((ByteBuddy)object).annotationRetention)) {
            return false;
        }
        if (!this.typeValidation.equals((Object)((ByteBuddy)object).typeValidation)) {
            return false;
        }
        if (!this.classFileVersion.equals(((ByteBuddy)object).classFileVersion)) {
            return false;
        }
        if (!this.namingStrategy.equals(((ByteBuddy)object).namingStrategy)) {
            return false;
        }
        if (!this.auxiliaryTypeNamingStrategy.equals(((ByteBuddy)object).auxiliaryTypeNamingStrategy)) {
            return false;
        }
        if (!this.annotationValueFilterFactory.equals(((ByteBuddy)object).annotationValueFilterFactory)) {
            return false;
        }
        if (!this.implementationContextFactory.equals(((ByteBuddy)object).implementationContextFactory)) {
            return false;
        }
        if (!this.methodGraphCompiler.equals(((ByteBuddy)object).methodGraphCompiler)) {
            return false;
        }
        if (!this.instrumentedTypeFactory.equals(((ByteBuddy)object).instrumentedTypeFactory)) {
            return false;
        }
        if (!this.ignoredMethods.equals(((ByteBuddy)object).ignoredMethods)) {
            return false;
        }
        if (!this.visibilityBridgeStrategy.equals(((ByteBuddy)object).visibilityBridgeStrategy)) {
            return false;
        }
        return this.classWriterStrategy.equals(((ByteBuddy)object).classWriterStrategy);
    }

    public int hashCode() {
        return (((((((((((this.getClass().hashCode() * 31 + this.classFileVersion.hashCode()) * 31 + this.namingStrategy.hashCode()) * 31 + this.auxiliaryTypeNamingStrategy.hashCode()) * 31 + this.annotationValueFilterFactory.hashCode()) * 31 + this.annotationRetention.hashCode()) * 31 + this.implementationContextFactory.hashCode()) * 31 + this.methodGraphCompiler.hashCode()) * 31 + this.instrumentedTypeFactory.hashCode()) * 31 + this.ignoredMethods.hashCode()) * 31 + this.typeValidation.hashCode()) * 31 + this.visibilityBridgeStrategy.hashCode()) * 31 + this.classWriterStrategy.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static enum RecordObjectMethod implements Implementation
    {
        HASH_CODE("hashCode", StackManipulation.Trivial.INSTANCE, Integer.TYPE, new Class[0]),
        EQUALS("equals", MethodVariableAccess.REFERENCE.loadFrom(1), Boolean.TYPE, Object.class),
        TO_STRING("toString", StackManipulation.Trivial.INSTANCE, String.class, new Class[0]);

        private final String name;
        private final StackManipulation stackManipulation;
        private final TypeDescription returnType;
        private final List<? extends TypeDescription> arguments;

        private RecordObjectMethod(String name, StackManipulation stackManipulation, Class<?> returnType, Class<?> ... arguments) {
            this.name = name;
            this.stackManipulation = stackManipulation;
            this.returnType = TypeDescription.ForLoadedType.of(returnType);
            this.arguments = new TypeList.ForLoadedTypes(arguments);
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<JavaConstant.MethodHandle> methodHandles = new ArrayList<JavaConstant.MethodHandle>(implementationTarget.getInstrumentedType().getRecordComponents().size());
            for (RecordComponentDescription.InDefinedShape recordComponent : implementationTarget.getInstrumentedType().getRecordComponents()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(";");
                }
                stringBuilder.append(recordComponent.getActualName());
                methodHandles.add(JavaConstant.MethodHandle.ofGetter((FieldDescription.InDefinedShape)((FieldList)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.named(recordComponent.getActualName()))).getOnly()));
            }
            return new ByteCodeAppender.Simple(MethodVariableAccess.loadThis(), this.stackManipulation, MethodInvocation.invoke(new MethodDescription.Latent(JavaType.OBJECT_METHODS.getTypeStub(), new MethodDescription.Token("bootstrap", 9, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().asGenericType(), TypeDescription.ForLoadedType.of(String.class).asGenericType(), JavaType.TYPE_DESCRIPTOR.getTypeStub().asGenericType(), TypeDescription.ForLoadedType.of(Class.class).asGenericType(), TypeDescription.ForLoadedType.of(String.class).asGenericType(), TypeDescription.ArrayProjection.of(JavaType.METHOD_HANDLE.getTypeStub()).asGenericType())))).dynamic(this.name, this.returnType, CompoundList.of(implementationTarget.getInstrumentedType(), this.arguments), CompoundList.of(Arrays.asList(JavaConstant.Simple.of(implementationTarget.getInstrumentedType()), JavaConstant.Simple.ofLoaded(stringBuilder.toString())), methodHandles)), MethodReturn.of(this.returnType));
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static enum RecordConstructorStrategy implements ConstructorStrategy,
    Implementation
    {
        INSTANCE;


        @Override
        public List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType) {
            ArrayList<ParameterDescription.Token> tokens = new ArrayList<ParameterDescription.Token>(instrumentedType.getRecordComponents().size());
            for (RecordComponentDescription.InDefinedShape recordComponent : instrumentedType.getRecordComponents()) {
                tokens.add(new ParameterDescription.Token(recordComponent.getType(), (List<? extends AnnotationDescription>)recordComponent.getDeclaredAnnotations().filter(ElementMatchers.targetsElement(ElementType.CONSTRUCTOR)), recordComponent.getActualName(), 0));
            }
            return Collections.singletonList(new MethodDescription.Token("<init>", 1, Collections.emptyList(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE), tokens, Collections.emptyList(), Collections.emptyList(), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED));
        }

        @Override
        public MethodRegistry inject(TypeDescription instrumentedType, MethodRegistry methodRegistry) {
            return methodRegistry.prepend(new LatentMatcher.Resolved(ElementMatchers.isConstructor().and(ElementMatchers.takesGenericArguments(instrumentedType.getRecordComponents().asTypeList()))), new MethodRegistry.Handler.ForImplementation(this), MethodAttributeAppender.ForInstrumentedMethod.EXCLUDING_RECEIVER, Transformer.NoOp.<MethodDescription>make());
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getInstrumentedType());
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            for (RecordComponentDescription.InDefinedShape recordComponent : instrumentedType.getRecordComponents()) {
                instrumentedType = instrumentedType.withField(new FieldDescription.Token(recordComponent.getActualName(), 18, recordComponent.getType(), (List<? extends AnnotationDescription>)recordComponent.getDeclaredAnnotations().filter(ElementMatchers.targetsElement(ElementType.FIELD)))).withMethod(new MethodDescription.Token(recordComponent.getActualName(), 1, Collections.emptyList(), recordComponent.getType(), Collections.emptyList(), Collections.emptyList(), (List<? extends AnnotationDescription>)recordComponent.getDeclaredAnnotations().filter(ElementMatchers.targetsElement(ElementType.METHOD)), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED));
            }
            return instrumentedType;
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class Appender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;

            protected Appender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                if (instrumentedMethod.isMethod()) {
                    return new ByteCodeAppender.Simple(MethodVariableAccess.loadThis(), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)this.instrumentedType.getDeclaredFields().filter(ElementMatchers.named(instrumentedMethod.getName()))).getOnly()).read(), MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext, instrumentedMethod);
                }
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(this.instrumentedType.getRecordComponents().size() * 3 + 2);
                stackManipulations.add(MethodVariableAccess.loadThis());
                stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.RECORD.getTypeStub(), new MethodDescription.Token(1))));
                int offset = 1;
                for (RecordComponentDescription.InDefinedShape recordComponent : this.instrumentedType.getRecordComponents()) {
                    stackManipulations.add(MethodVariableAccess.loadThis());
                    stackManipulations.add(MethodVariableAccess.of(recordComponent.getType()).loadFrom(offset));
                    stackManipulations.add(FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)this.instrumentedType.getDeclaredFields().filter(ElementMatchers.named(recordComponent.getActualName()))).getOnly()).write());
                    offset += recordComponent.getType().getStackSize().getSize();
                }
                stackManipulations.add(MethodReturn.VOID);
                return new ByteCodeAppender.Simple(stackManipulations).apply(methodVisitor, implementationContext, instrumentedMethod);
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
                return this.instrumentedType.equals(((Appender)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class EnumerationImplementation
    implements Implementation {
        protected static final String CLONE_METHOD_NAME = "clone";
        protected static final String ENUM_VALUE_OF_METHOD_NAME = "valueOf";
        protected static final String ENUM_VALUES_METHOD_NAME = "values";
        private static final int ENUM_FIELD_MODIFIERS = 25;
        private static final String ENUM_VALUES = "$VALUES";
        private final List<String> values;

        protected EnumerationImplementation(List<String> values) {
            this.values = values;
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            for (String value : this.values) {
                instrumentedType = instrumentedType.withField(new FieldDescription.Token(value, 16409, TargetType.DESCRIPTION.asGenericType()));
            }
            return instrumentedType.withField(new FieldDescription.Token(ENUM_VALUES, 4121, TypeDescription.ArrayProjection.of(TargetType.DESCRIPTION).asGenericType())).withInitializer(new InitializationAppender(this.values));
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new ValuesMethodAppender(implementationTarget.getInstrumentedType());
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
            return ((Object)this.values).equals(((EnumerationImplementation)object).values);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.values).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class InitializationAppender
        implements ByteCodeAppender {
            private final List<String> values;

            protected InitializationAppender(List<String> values) {
                this.values = values;
            }

            @Override
            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                TypeDescription instrumentedType = instrumentedMethod.getDeclaringType().asErasure();
                MethodDescription enumConstructor = (MethodDescription)((MethodList)instrumentedType.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(String.class, Integer.TYPE)))).getOnly();
                int ordinal = 0;
                StackManipulation stackManipulation = StackManipulation.Trivial.INSTANCE;
                ArrayList<FieldDescription> enumerationFields = new ArrayList<FieldDescription>(this.values.size());
                for (String value : this.values) {
                    FieldDescription fieldDescription = (FieldDescription)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(value))).getOnly();
                    stackManipulation = new StackManipulation.Compound(stackManipulation, TypeCreation.of(instrumentedType), Duplication.SINGLE, new TextConstant(value), IntegerConstant.forValue(ordinal++), MethodInvocation.invoke(enumConstructor), FieldAccess.forField(fieldDescription).write());
                    enumerationFields.add(fieldDescription);
                }
                ArrayList<StackManipulation> fieldGetters = new ArrayList<StackManipulation>(this.values.size());
                for (FieldDescription fieldDescription : enumerationFields) {
                    fieldGetters.add(FieldAccess.forField(fieldDescription).read());
                }
                stackManipulation = new StackManipulation.Compound(stackManipulation, ArrayFactory.forType(instrumentedType.asGenericType()).withValues(fieldGetters), FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(EnumerationImplementation.ENUM_VALUES))).getOnly()).write());
                return new ByteCodeAppender.Size(stackManipulation.apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
                return ((Object)this.values).equals(((InitializationAppender)object).values);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.values).hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class ValuesMethodAppender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;

            protected ValuesMethodAppender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                FieldDescription valuesField = (FieldDescription)((FieldList)this.instrumentedType.getDeclaredFields().filter(ElementMatchers.named(EnumerationImplementation.ENUM_VALUES))).getOnly();
                MethodDescription cloneMethod = (MethodDescription)((MethodList)TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.named(EnumerationImplementation.CLONE_METHOD_NAME))).getOnly();
                return new ByteCodeAppender.Size(new StackManipulation.Compound(FieldAccess.forField(valuesField).read(), MethodInvocation.invoke(cloneMethod).virtual(valuesField.getType().asErasure()), TypeCasting.to(valuesField.getType().asErasure()), MethodReturn.REFERENCE).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
                return this.instrumentedType.equals(((ValuesMethodAppender)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }
        }
    }
}


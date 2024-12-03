/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.agent.builder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.agent.builder.LambdaFactory;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.NexusAccessor;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.ConstantDynamic;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AgentBuilder {
    public AgentBuilder with(ByteBuddy var1);

    public AgentBuilder with(Listener var1);

    public AgentBuilder with(CircularityLock var1);

    public AgentBuilder with(PoolStrategy var1);

    public AgentBuilder with(LocationStrategy var1);

    public AgentBuilder with(ClassFileLocator var1);

    public AgentBuilder with(TypeStrategy var1);

    public AgentBuilder with(InitializationStrategy var1);

    public RedefinitionListenable.WithoutBatchStrategy with(RedefinitionStrategy var1);

    public AgentBuilder with(LambdaInstrumentationStrategy var1);

    public AgentBuilder with(DescriptionStrategy var1);

    public AgentBuilder with(FallbackStrategy var1);

    public AgentBuilder with(ClassFileBufferStrategy var1);

    public AgentBuilder with(InstallationListener var1);

    public AgentBuilder with(InjectionStrategy var1);

    public AgentBuilder with(TransformerDecorator var1);

    public AgentBuilder enableNativeMethodPrefix(String var1);

    public AgentBuilder disableNativeMethodPrefix();

    public AgentBuilder disableClassFormatChanges();

    public AgentBuilder warmUp(Class<?> ... var1);

    public AgentBuilder warmUp(Collection<Class<?>> var1);

    public AgentBuilder assureReadEdgeTo(Instrumentation var1, Class<?> ... var2);

    public AgentBuilder assureReadEdgeTo(Instrumentation var1, JavaModule ... var2);

    public AgentBuilder assureReadEdgeTo(Instrumentation var1, Collection<? extends JavaModule> var2);

    public AgentBuilder assureReadEdgeFromAndTo(Instrumentation var1, Class<?> ... var2);

    public AgentBuilder assureReadEdgeFromAndTo(Instrumentation var1, JavaModule ... var2);

    public AgentBuilder assureReadEdgeFromAndTo(Instrumentation var1, Collection<? extends JavaModule> var2);

    public Identified.Narrowable type(ElementMatcher<? super TypeDescription> var1);

    public Identified.Narrowable type(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2);

    public Identified.Narrowable type(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2, ElementMatcher<? super JavaModule> var3);

    public Identified.Narrowable type(RawMatcher var1);

    public Ignored ignore(ElementMatcher<? super TypeDescription> var1);

    public Ignored ignore(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2);

    public Ignored ignore(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2, ElementMatcher<? super JavaModule> var3);

    public Ignored ignore(RawMatcher var1);

    public ClassFileTransformer makeRaw();

    public ResettableClassFileTransformer installOn(Instrumentation var1);

    public ResettableClassFileTransformer installOnByteBuddyAgent();

    public ResettableClassFileTransformer patchOn(Instrumentation var1, ResettableClassFileTransformer var2);

    public ResettableClassFileTransformer patchOn(Instrumentation var1, ResettableClassFileTransformer var2, RawMatcher var3);

    public ResettableClassFileTransformer patchOn(Instrumentation var1, ResettableClassFileTransformer var2, PatchMode var3);

    public ResettableClassFileTransformer patchOn(Instrumentation var1, ResettableClassFileTransformer var2, RawMatcher var3, PatchMode var4);

    public ResettableClassFileTransformer patchOnByteBuddyAgent(ResettableClassFileTransformer var1);

    public ResettableClassFileTransformer patchOnByteBuddyAgent(ResettableClassFileTransformer var1, PatchMode var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements AgentBuilder {
        private static final String INSTALLER_TYPE = "net.bytebuddy.agent.Installer";
        private static final String INSTALLER_GETTER = "getInstrumentation";
        @AlwaysNull
        private static final byte[] NO_TRANSFORMATION;
        @AlwaysNull
        private static final Class<?> NOT_PREVIOUSLY_DEFINED;
        private static final Dispatcher DISPATCHER;
        private static final CircularityLock DEFAULT_LOCK;
        protected final ByteBuddy byteBuddy;
        protected final Listener listener;
        protected final CircularityLock circularityLock;
        protected final PoolStrategy poolStrategy;
        protected final TypeStrategy typeStrategy;
        protected final LocationStrategy locationStrategy;
        protected final ClassFileLocator classFileLocator;
        protected final NativeMethodStrategy nativeMethodStrategy;
        protected final WarmupStrategy warmupStrategy;
        protected final TransformerDecorator transformerDecorator;
        protected final InitializationStrategy initializationStrategy;
        protected final RedefinitionStrategy redefinitionStrategy;
        protected final RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy;
        protected final RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator;
        protected final RedefinitionStrategy.Listener redefinitionListener;
        protected final RedefinitionStrategy.ResubmissionStrategy redefinitionResubmissionStrategy;
        protected final InjectionStrategy injectionStrategy;
        protected final LambdaInstrumentationStrategy lambdaInstrumentationStrategy;
        protected final DescriptionStrategy descriptionStrategy;
        protected final FallbackStrategy fallbackStrategy;
        protected final ClassFileBufferStrategy classFileBufferStrategy;
        protected final InstallationListener installationListener;
        protected final RawMatcher ignoreMatcher;
        protected final List<Transformation> transformations;
        private static final boolean ACCESS_CONTROLLER;

        public Default() {
            this(new ByteBuddy());
        }

        public Default(ByteBuddy byteBuddy) {
            this(byteBuddy, Listener.NoOp.INSTANCE, DEFAULT_LOCK, PoolStrategy.Default.FAST, TypeStrategy.Default.REBASE, LocationStrategy.ForClassLoader.STRONG, ClassFileLocator.NoOp.INSTANCE, NativeMethodStrategy.Disabled.INSTANCE, WarmupStrategy.NoOp.INSTANCE, TransformerDecorator.NoOp.INSTANCE, new InitializationStrategy.SelfInjection.Split(), RedefinitionStrategy.DISABLED, RedefinitionStrategy.DiscoveryStrategy.SinglePass.INSTANCE, RedefinitionStrategy.BatchAllocator.ForTotal.INSTANCE, RedefinitionStrategy.Listener.NoOp.INSTANCE, RedefinitionStrategy.ResubmissionStrategy.Disabled.INSTANCE, InjectionStrategy.UsingReflection.INSTANCE, LambdaInstrumentationStrategy.DISABLED, DescriptionStrategy.Default.HYBRID, FallbackStrategy.ByThrowableType.ofOptionalTypes(), ClassFileBufferStrategy.Default.RETAINING, InstallationListener.NoOp.INSTANCE, new RawMatcher.Disjunction(new RawMatcher.ForElementMatchers(ElementMatchers.any(), ElementMatchers.isBootstrapClassLoader().or(ElementMatchers.isExtensionClassLoader())), new RawMatcher.ForElementMatchers(ElementMatchers.nameStartsWith("net.bytebuddy.").and(ElementMatchers.not(ElementMatchers.nameStartsWith("net.bytebuddy.renamed."))).or(ElementMatchers.nameStartsWith("sun.reflect.").or(ElementMatchers.nameStartsWith("jdk.internal.reflect."))).or(ElementMatchers.isSynthetic()))), Collections.emptyList());
        }

        protected Default(ByteBuddy byteBuddy, Listener listener, CircularityLock circularityLock, PoolStrategy poolStrategy, TypeStrategy typeStrategy, LocationStrategy locationStrategy, ClassFileLocator classFileLocator, NativeMethodStrategy nativeMethodStrategy, WarmupStrategy warmupStrategy, TransformerDecorator transformerDecorator, InitializationStrategy initializationStrategy, RedefinitionStrategy redefinitionStrategy, RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, RedefinitionStrategy.Listener redefinitionListener, RedefinitionStrategy.ResubmissionStrategy redefinitionResubmissionStrategy, InjectionStrategy injectionStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, ClassFileBufferStrategy classFileBufferStrategy, InstallationListener installationListener, RawMatcher ignoreMatcher, List<Transformation> transformations) {
            this.byteBuddy = byteBuddy;
            this.listener = listener;
            this.circularityLock = circularityLock;
            this.poolStrategy = poolStrategy;
            this.typeStrategy = typeStrategy;
            this.locationStrategy = locationStrategy;
            this.classFileLocator = classFileLocator;
            this.nativeMethodStrategy = nativeMethodStrategy;
            this.warmupStrategy = warmupStrategy;
            this.transformerDecorator = transformerDecorator;
            this.initializationStrategy = initializationStrategy;
            this.redefinitionStrategy = redefinitionStrategy;
            this.redefinitionDiscoveryStrategy = redefinitionDiscoveryStrategy;
            this.redefinitionBatchAllocator = redefinitionBatchAllocator;
            this.redefinitionListener = redefinitionListener;
            this.redefinitionResubmissionStrategy = redefinitionResubmissionStrategy;
            this.injectionStrategy = injectionStrategy;
            this.lambdaInstrumentationStrategy = lambdaInstrumentationStrategy;
            this.descriptionStrategy = descriptionStrategy;
            this.fallbackStrategy = fallbackStrategy;
            this.classFileBufferStrategy = classFileBufferStrategy;
            this.installationListener = installationListener;
            this.ignoreMatcher = ignoreMatcher;
            this.transformations = transformations;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static AgentBuilder of(Plugin ... plugin) {
            return Default.of(Arrays.asList(plugin));
        }

        public static AgentBuilder of(List<? extends Plugin> plugins) {
            return Default.of((EntryPoint)EntryPoint.Default.REBASE, plugins);
        }

        public static AgentBuilder of(EntryPoint entryPoint, Plugin ... plugin) {
            return Default.of(entryPoint, Arrays.asList(plugin));
        }

        public static AgentBuilder of(EntryPoint entryPoint, List<? extends Plugin> plugins) {
            return Default.of(entryPoint, ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5), plugins);
        }

        public static AgentBuilder of(ClassFileVersion classFileVersion, Plugin ... plugin) {
            return Default.of(classFileVersion, Arrays.asList(plugin));
        }

        public static AgentBuilder of(ClassFileVersion classFileVersion, List<? extends Plugin> plugins) {
            return Default.of((EntryPoint)EntryPoint.Default.REBASE, classFileVersion, plugins);
        }

        public static AgentBuilder of(EntryPoint entryPoint, ClassFileVersion classFileVersion, Plugin ... plugin) {
            return Default.of(entryPoint, classFileVersion, Arrays.asList(plugin));
        }

        public static AgentBuilder of(EntryPoint entryPoint, ClassFileVersion classFileVersion, List<? extends Plugin> plugins) {
            AgentBuilder agentBuilder = new Default(entryPoint.byteBuddy(classFileVersion)).with(new TypeStrategy.ForBuildEntryPoint(entryPoint));
            for (Plugin plugin : plugins) {
                agentBuilder = agentBuilder.type(plugin).transform(new Transformer.ForBuildPlugin(plugin));
            }
            return agentBuilder;
        }

        @Override
        public AgentBuilder with(ByteBuddy byteBuddy) {
            return new Default(byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(Listener listener) {
            return new Default(this.byteBuddy, new Listener.Compound(this.listener, listener), this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(CircularityLock circularityLock) {
            return new Default(this.byteBuddy, this.listener, circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(TypeStrategy typeStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(PoolStrategy poolStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(LocationStrategy locationStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(ClassFileLocator classFileLocator) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, new ClassFileLocator.Compound(this.classFileLocator, classFileLocator), this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder enableNativeMethodPrefix(String prefix) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, NativeMethodStrategy.ForPrefix.of(prefix), this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder disableNativeMethodPrefix() {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, NativeMethodStrategy.Disabled.INSTANCE, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder warmUp(Class<?> ... type) {
            return this.warmUp(Arrays.asList(type));
        }

        @Override
        public AgentBuilder warmUp(Collection<Class<?>> types) {
            if (types.isEmpty()) {
                return this;
            }
            for (Class<?> type : types) {
                if (!type.isPrimitive() && !type.isArray()) continue;
                throw new IllegalArgumentException("Cannot warm up primitive or array type: " + type);
            }
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy.with(types), this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(TransformerDecorator transformerDecorator) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, new TransformerDecorator.Compound(this.transformerDecorator, transformerDecorator), this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public RedefinitionListenable.WithoutBatchStrategy with(RedefinitionStrategy redefinitionStrategy) {
            return new Redefining(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, redefinitionStrategy, RedefinitionStrategy.DiscoveryStrategy.SinglePass.INSTANCE, RedefinitionStrategy.BatchAllocator.ForTotal.INSTANCE, RedefinitionStrategy.Listener.NoOp.INSTANCE, RedefinitionStrategy.ResubmissionStrategy.Disabled.INSTANCE, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(InitializationStrategy initializationStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(LambdaInstrumentationStrategy lambdaInstrumentationStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(DescriptionStrategy descriptionStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(FallbackStrategy fallbackStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(ClassFileBufferStrategy classFileBufferStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(InstallationListener installationListener) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, new InstallationListener.Compound(this.installationListener, installationListener), this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder with(InjectionStrategy injectionStrategy) {
            return new Default(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder disableClassFormatChanges() {
            return new Default(this.byteBuddy.with(Implementation.Context.Disabled.Factory.INSTANCE), this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy == TypeStrategy.Default.DECORATE ? TypeStrategy.Default.DECORATE : TypeStrategy.Default.REDEFINE_FROZEN, this.locationStrategy, this.classFileLocator, NativeMethodStrategy.Disabled.INSTANCE, this.warmupStrategy, this.transformerDecorator, InitializationStrategy.NoOp.INSTANCE, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
        }

        @Override
        public AgentBuilder assureReadEdgeTo(Instrumentation instrumentation, Class<?> ... type) {
            return JavaModule.isSupported() ? this.with(Listener.ModuleReadEdgeCompleting.of(instrumentation, false, type)) : this;
        }

        @Override
        public AgentBuilder assureReadEdgeTo(Instrumentation instrumentation, JavaModule ... module) {
            return this.assureReadEdgeTo(instrumentation, Arrays.asList(module));
        }

        @Override
        public AgentBuilder assureReadEdgeTo(Instrumentation instrumentation, Collection<? extends JavaModule> modules) {
            return this.with(new Listener.ModuleReadEdgeCompleting(instrumentation, false, new HashSet<JavaModule>(modules)));
        }

        @Override
        public AgentBuilder assureReadEdgeFromAndTo(Instrumentation instrumentation, Class<?> ... type) {
            return JavaModule.isSupported() ? this.with(Listener.ModuleReadEdgeCompleting.of(instrumentation, true, type)) : this;
        }

        @Override
        public AgentBuilder assureReadEdgeFromAndTo(Instrumentation instrumentation, JavaModule ... module) {
            return this.assureReadEdgeFromAndTo(instrumentation, Arrays.asList(module));
        }

        @Override
        public AgentBuilder assureReadEdgeFromAndTo(Instrumentation instrumentation, Collection<? extends JavaModule> modules) {
            return this.with(new Listener.ModuleReadEdgeCompleting(instrumentation, true, new HashSet<JavaModule>(modules)));
        }

        @Override
        public Identified.Narrowable type(RawMatcher matcher) {
            return new Transforming(matcher, Collections.<Transformer>emptyList(), false);
        }

        @Override
        public Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher) {
            return this.type(typeMatcher, ElementMatchers.any());
        }

        @Override
        public Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
            return this.type(typeMatcher, classLoaderMatcher, ElementMatchers.any());
        }

        @Override
        public Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
            return this.type(new RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, ElementMatchers.not(ElementMatchers.supportsModules()).or(moduleMatcher)));
        }

        @Override
        public Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher) {
            return this.ignore(typeMatcher, ElementMatchers.any());
        }

        @Override
        public Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
            return this.ignore(typeMatcher, classLoaderMatcher, ElementMatchers.any());
        }

        @Override
        public Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
            return this.ignore(new RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, ElementMatchers.not(ElementMatchers.supportsModules()).or(moduleMatcher)));
        }

        @Override
        public Ignored ignore(RawMatcher rawMatcher) {
            return new Ignoring(rawMatcher);
        }

        @Override
        public ResettableClassFileTransformer makeRaw() {
            return this.makeRaw(this.listener, InstallationListener.NoOp.INSTANCE, RedefinitionStrategy.ResubmissionEnforcer.Disabled.INSTANCE);
        }

        private ResettableClassFileTransformer makeRaw(Listener listener, InstallationListener installationListener, RedefinitionStrategy.ResubmissionEnforcer resubmissionEnforcer) {
            return ExecutingTransformer.FACTORY.make(this.byteBuddy, listener, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.initializationStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, installationListener, this.ignoreMatcher, resubmissionEnforcer, this.transformations, this.circularityLock);
        }

        private static Instrumentation resolveByteBuddyAgentInstrumentation() {
            try {
                Class<?> installer = ClassLoader.getSystemClassLoader().loadClass(INSTALLER_TYPE);
                JavaModule source = JavaModule.ofType(AgentBuilder.class);
                JavaModule target = JavaModule.ofType(installer);
                if (source != null && !source.canRead(target)) {
                    Class<?> module = Class.forName("java.lang.Module");
                    module.getMethod("addReads", module).invoke(source.unwrap(), target.unwrap());
                }
                return (Instrumentation)installer.getMethod(INSTALLER_GETTER, new Class[0]).invoke(null, new Object[0]);
            }
            catch (RuntimeException exception) {
                throw exception;
            }
            catch (Exception exception) {
                throw new IllegalStateException("The Byte Buddy agent is not installed or not accessible", exception);
            }
        }

        @Override
        public ResettableClassFileTransformer installOn(Instrumentation instrumentation) {
            return this.doInstall(instrumentation, new Transformation.SimpleMatcher(this.ignoreMatcher, this.transformations), PatchMode.Handler.NoOp.INSTANCE);
        }

        @Override
        public ResettableClassFileTransformer installOnByteBuddyAgent() {
            return this.installOn(Default.resolveByteBuddyAgentInstrumentation());
        }

        @Override
        public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            return this.patchOn(instrumentation, classFileTransformer, PatchMode.of(classFileTransformer));
        }

        @Override
        public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, RawMatcher differentialMatcher) {
            return this.patchOn(instrumentation, classFileTransformer, differentialMatcher, PatchMode.of(classFileTransformer));
        }

        @Override
        public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, PatchMode patchMode) {
            return this.patchOn(instrumentation, classFileTransformer, new Transformation.DifferentialMatcher(this.ignoreMatcher, this.transformations, classFileTransformer instanceof ResettableClassFileTransformer.Substitutable ? ((ResettableClassFileTransformer.Substitutable)classFileTransformer).unwrap() : classFileTransformer), patchMode);
        }

        @Override
        public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, RawMatcher differentialMatcher, PatchMode patchMode) {
            return this.doInstall(instrumentation, differentialMatcher, patchMode.toHandler(classFileTransformer));
        }

        @Override
        public ResettableClassFileTransformer patchOnByteBuddyAgent(ResettableClassFileTransformer classFileTransformer) {
            return this.patchOn(Default.resolveByteBuddyAgentInstrumentation(), classFileTransformer);
        }

        @Override
        public ResettableClassFileTransformer patchOnByteBuddyAgent(ResettableClassFileTransformer classFileTransformer, PatchMode patchMode) {
            return this.patchOn(Default.resolveByteBuddyAgentInstrumentation(), classFileTransformer, patchMode);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private ResettableClassFileTransformer doInstall(Instrumentation instrumentation, RawMatcher matcher, PatchMode.Handler handler) {
            if (!this.circularityLock.acquire()) {
                throw new IllegalStateException("Could not acquire the circularity lock upon installation.");
            }
            try {
                ResettableClassFileTransformer classFileTransformer;
                RedefinitionStrategy.ResubmissionStrategy.Installation installation;
                block8: {
                    installation = this.redefinitionResubmissionStrategy.apply(instrumentation, this.poolStrategy, this.locationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.listener, this.installationListener, this.circularityLock, new Transformation.SimpleMatcher(this.ignoreMatcher, this.transformations), this.redefinitionStrategy, this.redefinitionBatchAllocator, this.redefinitionListener);
                    classFileTransformer = this.transformerDecorator.decorate(this.makeRaw(installation.getListener(), installation.getInstallationListener(), installation.getResubmissionEnforcer()));
                    installation.getInstallationListener().onBeforeInstall(instrumentation, classFileTransformer);
                    try {
                        this.warmupStrategy.apply(classFileTransformer, this.locationStrategy, this.redefinitionStrategy, this.circularityLock, installation.getInstallationListener());
                        handler.onBeforeRegistration(instrumentation);
                        if (handler.onRegistration(classFileTransformer)) {
                            if (this.redefinitionStrategy.isRetransforming()) {
                                DISPATCHER.addTransformer(instrumentation, classFileTransformer, true);
                            } else {
                                instrumentation.addTransformer(classFileTransformer);
                            }
                        }
                        handler.onAfterRegistration(instrumentation);
                        this.nativeMethodStrategy.apply(instrumentation, classFileTransformer);
                        this.lambdaInstrumentationStrategy.apply(this.byteBuddy, instrumentation, classFileTransformer);
                        this.redefinitionStrategy.apply(instrumentation, this.poolStrategy, this.locationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.redefinitionDiscoveryStrategy, this.lambdaInstrumentationStrategy, installation.getListener(), this.redefinitionListener, matcher, this.redefinitionBatchAllocator, this.circularityLock);
                    }
                    catch (Throwable throwable) {
                        throwable = installation.getInstallationListener().onError(instrumentation, classFileTransformer, throwable);
                        if (throwable == null) break block8;
                        instrumentation.removeTransformer(classFileTransformer);
                        throw new IllegalStateException("Could not install class file transformer", throwable);
                    }
                }
                installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
                ResettableClassFileTransformer resettableClassFileTransformer = classFileTransformer;
                Object var8_8 = null;
                this.circularityLock.release();
                return resettableClassFileTransformer;
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                this.circularityLock.release();
                throw throwable;
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
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
            NO_TRANSFORMATION = null;
            NOT_PREVIOUSLY_DEFINED = null;
            DISPATCHER = Default.doPrivileged(JavaDispatcher.of(Dispatcher.class));
            DEFAULT_LOCK = new CircularityLock.Default();
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
            if (!this.redefinitionStrategy.equals((Object)((Default)object).redefinitionStrategy)) {
                return false;
            }
            if (!this.lambdaInstrumentationStrategy.equals((Object)((Default)object).lambdaInstrumentationStrategy)) {
                return false;
            }
            if (!this.byteBuddy.equals(((Default)object).byteBuddy)) {
                return false;
            }
            if (!this.listener.equals(((Default)object).listener)) {
                return false;
            }
            if (!this.circularityLock.equals(((Default)object).circularityLock)) {
                return false;
            }
            if (!this.poolStrategy.equals(((Default)object).poolStrategy)) {
                return false;
            }
            if (!this.typeStrategy.equals(((Default)object).typeStrategy)) {
                return false;
            }
            if (!this.locationStrategy.equals(((Default)object).locationStrategy)) {
                return false;
            }
            if (!this.classFileLocator.equals(((Default)object).classFileLocator)) {
                return false;
            }
            if (!this.nativeMethodStrategy.equals(((Default)object).nativeMethodStrategy)) {
                return false;
            }
            if (!this.warmupStrategy.equals(((Default)object).warmupStrategy)) {
                return false;
            }
            if (!this.transformerDecorator.equals(((Default)object).transformerDecorator)) {
                return false;
            }
            if (!this.initializationStrategy.equals(((Default)object).initializationStrategy)) {
                return false;
            }
            if (!this.redefinitionDiscoveryStrategy.equals(((Default)object).redefinitionDiscoveryStrategy)) {
                return false;
            }
            if (!this.redefinitionBatchAllocator.equals(((Default)object).redefinitionBatchAllocator)) {
                return false;
            }
            if (!this.redefinitionListener.equals(((Default)object).redefinitionListener)) {
                return false;
            }
            if (!this.redefinitionResubmissionStrategy.equals(((Default)object).redefinitionResubmissionStrategy)) {
                return false;
            }
            if (!this.injectionStrategy.equals(((Default)object).injectionStrategy)) {
                return false;
            }
            if (!this.descriptionStrategy.equals(((Default)object).descriptionStrategy)) {
                return false;
            }
            if (!this.fallbackStrategy.equals(((Default)object).fallbackStrategy)) {
                return false;
            }
            if (!this.classFileBufferStrategy.equals(((Default)object).classFileBufferStrategy)) {
                return false;
            }
            if (!this.installationListener.equals(((Default)object).installationListener)) {
                return false;
            }
            if (!this.ignoreMatcher.equals(((Default)object).ignoreMatcher)) {
                return false;
            }
            return ((Object)this.transformations).equals(((Default)object).transformations);
        }

        public int hashCode() {
            return (((((((((((((((((((((((this.getClass().hashCode() * 31 + this.byteBuddy.hashCode()) * 31 + this.listener.hashCode()) * 31 + this.circularityLock.hashCode()) * 31 + this.poolStrategy.hashCode()) * 31 + this.typeStrategy.hashCode()) * 31 + this.locationStrategy.hashCode()) * 31 + this.classFileLocator.hashCode()) * 31 + this.nativeMethodStrategy.hashCode()) * 31 + this.warmupStrategy.hashCode()) * 31 + this.transformerDecorator.hashCode()) * 31 + this.initializationStrategy.hashCode()) * 31 + this.redefinitionStrategy.hashCode()) * 31 + this.redefinitionDiscoveryStrategy.hashCode()) * 31 + this.redefinitionBatchAllocator.hashCode()) * 31 + this.redefinitionListener.hashCode()) * 31 + this.redefinitionResubmissionStrategy.hashCode()) * 31 + this.injectionStrategy.hashCode()) * 31 + this.lambdaInstrumentationStrategy.hashCode()) * 31 + this.descriptionStrategy.hashCode()) * 31 + this.fallbackStrategy.hashCode()) * 31 + this.classFileBufferStrategy.hashCode()) * 31 + this.installationListener.hashCode()) * 31 + this.ignoreMatcher.hashCode()) * 31 + ((Object)this.transformations).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class Redefining
        extends Default
        implements RedefinitionListenable.WithoutBatchStrategy {
            protected Redefining(ByteBuddy byteBuddy, Listener listener, CircularityLock circularityLock, PoolStrategy poolStrategy, TypeStrategy typeStrategy, LocationStrategy locationStrategy, ClassFileLocator classFileLocator, NativeMethodStrategy nativeMethodStrategy, WarmupStrategy warmupStrategy, TransformerDecorator transformerDecorator, InitializationStrategy initializationStrategy, RedefinitionStrategy redefinitionStrategy, RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, RedefinitionStrategy.Listener redefinitionListener, RedefinitionStrategy.ResubmissionStrategy redefinitionResubmissionStrategy, InjectionStrategy injectionStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, ClassFileBufferStrategy classFileBufferStrategy, InstallationListener installationListener, RawMatcher ignoreMatcher, List<Transformation> transformations) {
                super(byteBuddy, listener, circularityLock, poolStrategy, typeStrategy, locationStrategy, classFileLocator, nativeMethodStrategy, warmupStrategy, transformerDecorator, initializationStrategy, redefinitionStrategy, redefinitionDiscoveryStrategy, redefinitionBatchAllocator, redefinitionListener, redefinitionResubmissionStrategy, injectionStrategy, lambdaInstrumentationStrategy, descriptionStrategy, fallbackStrategy, classFileBufferStrategy, installationListener, ignoreMatcher, transformations);
            }

            @Override
            public RedefinitionListenable.WithImplicitDiscoveryStrategy with(RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator) {
                if (!this.redefinitionStrategy.isEnabled()) {
                    throw new IllegalStateException("Cannot set redefinition batch allocator when redefinition is disabled");
                }
                return new Redefining(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
            }

            @Override
            public RedefinitionListenable redefineOnly(Class<?> ... type) {
                return this.with(new RedefinitionStrategy.DiscoveryStrategy.Explicit(type));
            }

            @Override
            public RedefinitionListenable with(RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy) {
                if (!this.redefinitionStrategy.isEnabled()) {
                    throw new IllegalStateException("Cannot set redefinition discovery strategy when redefinition is disabled");
                }
                return new Redefining(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, this.redefinitionListener, this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
            }

            @Override
            public RedefinitionListenable with(RedefinitionStrategy.Listener redefinitionListener) {
                if (!this.redefinitionStrategy.isEnabled()) {
                    throw new IllegalStateException("Cannot set redefinition listener when redefinition is disabled");
                }
                return new Redefining(this.byteBuddy, this.listener, this.circularityLock, this.poolStrategy, this.typeStrategy, this.locationStrategy, this.classFileLocator, this.nativeMethodStrategy, this.warmupStrategy, this.transformerDecorator, this.initializationStrategy, this.redefinitionStrategy, this.redefinitionDiscoveryStrategy, this.redefinitionBatchAllocator, new RedefinitionStrategy.Listener.Compound(this.redefinitionListener, redefinitionListener), this.redefinitionResubmissionStrategy, this.injectionStrategy, this.lambdaInstrumentationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.classFileBufferStrategy, this.installationListener, this.ignoreMatcher, this.transformations);
            }

            @Override
            public RedefinitionListenable.WithoutResubmissionSpecification withResubmission(RedefinitionStrategy.ResubmissionScheduler resubmissionScheduler) {
                if (!this.redefinitionStrategy.isEnabled()) {
                    throw new IllegalStateException("Cannot enable resubmission when redefinition is disabled");
                }
                return new WithResubmission(resubmissionScheduler, RedefinitionListenable.ResubmissionOnErrorMatcher.Trivial.NON_MATCHING, RedefinitionListenable.ResubmissionImmediateMatcher.Trivial.NON_MATCHING);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class WithResubmission
            extends Delegator
            implements RedefinitionListenable.WithResubmissionSpecification {
                private final RedefinitionStrategy.ResubmissionScheduler resubmissionScheduler;
                private final RedefinitionListenable.ResubmissionOnErrorMatcher resubmissionOnErrorMatcher;
                private final RedefinitionListenable.ResubmissionImmediateMatcher resubmissionImmediateMatcher;

                protected WithResubmission(RedefinitionStrategy.ResubmissionScheduler resubmissionScheduler, RedefinitionListenable.ResubmissionOnErrorMatcher resubmissionOnErrorMatcher, RedefinitionListenable.ResubmissionImmediateMatcher resubmissionImmediateMatcher) {
                    this.resubmissionScheduler = resubmissionScheduler;
                    this.resubmissionOnErrorMatcher = resubmissionOnErrorMatcher;
                    this.resubmissionImmediateMatcher = resubmissionImmediateMatcher;
                }

                @Override
                protected AgentBuilder materialize() {
                    return new Default(Redefining.this.byteBuddy, Redefining.this.listener, Redefining.this.circularityLock, Redefining.this.poolStrategy, Redefining.this.typeStrategy, Redefining.this.locationStrategy, Redefining.this.classFileLocator, Redefining.this.nativeMethodStrategy, Redefining.this.warmupStrategy, Redefining.this.transformerDecorator, Redefining.this.initializationStrategy, Redefining.this.redefinitionStrategy, Redefining.this.redefinitionDiscoveryStrategy, Redefining.this.redefinitionBatchAllocator, Redefining.this.redefinitionListener, new RedefinitionStrategy.ResubmissionStrategy.Enabled(this.resubmissionScheduler, this.resubmissionOnErrorMatcher, this.resubmissionImmediateMatcher), Redefining.this.injectionStrategy, Redefining.this.lambdaInstrumentationStrategy, Redefining.this.descriptionStrategy, Redefining.this.fallbackStrategy, Redefining.this.classFileBufferStrategy, Redefining.this.installationListener, Redefining.this.ignoreMatcher, Redefining.this.transformations);
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitOnError() {
                    return this.resubmitOnError(ElementMatchers.any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> exceptionMatcher) {
                    return this.resubmitOnError(exceptionMatcher, ElementMatchers.<String>any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> exceptionMatcher, ElementMatcher<String> typeNameMatcher) {
                    return this.resubmitOnError(exceptionMatcher, typeNameMatcher, ElementMatchers.any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> exceptionMatcher, ElementMatcher<String> typeNameMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
                    return this.resubmitOnError(exceptionMatcher, typeNameMatcher, classLoaderMatcher, ElementMatchers.any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> exceptionMatcher, ElementMatcher<String> typeNameMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                    return this.resubmitOnError(new RedefinitionListenable.ResubmissionOnErrorMatcher.ForElementMatchers(exceptionMatcher, typeNameMatcher, classLoaderMatcher, moduleMatcher));
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitOnError(RedefinitionListenable.ResubmissionOnErrorMatcher matcher) {
                    return new WithResubmission(this.resubmissionScheduler, new RedefinitionListenable.ResubmissionOnErrorMatcher.Disjunction(this.resubmissionOnErrorMatcher, matcher), this.resubmissionImmediateMatcher);
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitImmediate() {
                    return this.resubmitImmediate(ElementMatchers.<String>any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitImmediate(ElementMatcher<String> typeNameMatcher) {
                    return this.resubmitImmediate(typeNameMatcher, ElementMatchers.any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitImmediate(ElementMatcher<String> typeNameMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
                    return this.resubmitImmediate(typeNameMatcher, classLoaderMatcher, ElementMatchers.any());
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitImmediate(ElementMatcher<String> typeNameMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                    return this.resubmitImmediate(new RedefinitionListenable.ResubmissionImmediateMatcher.ForElementMatchers(typeNameMatcher, classLoaderMatcher, moduleMatcher));
                }

                @Override
                public RedefinitionListenable.WithResubmissionSpecification resubmitImmediate(RedefinitionListenable.ResubmissionImmediateMatcher matcher) {
                    return new WithResubmission(this.resubmissionScheduler, this.resubmissionOnErrorMatcher, new RedefinitionListenable.ResubmissionImmediateMatcher.Disjunction(this.resubmissionImmediateMatcher, matcher));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Transforming
        extends Delegator.Matchable<Identified.Narrowable>
        implements Identified.Extendable,
        Identified.Narrowable {
            private final RawMatcher rawMatcher;
            private final List<Transformer> transformers;
            private final boolean terminal;

            protected Transforming(RawMatcher rawMatcher, List<Transformer> transformers, boolean terminal) {
                this.rawMatcher = rawMatcher;
                this.transformers = transformers;
                this.terminal = terminal;
            }

            @Override
            protected AgentBuilder materialize() {
                return new Default(Default.this.byteBuddy, Default.this.listener, Default.this.circularityLock, Default.this.poolStrategy, Default.this.typeStrategy, Default.this.locationStrategy, Default.this.classFileLocator, Default.this.nativeMethodStrategy, Default.this.warmupStrategy, Default.this.transformerDecorator, Default.this.initializationStrategy, Default.this.redefinitionStrategy, Default.this.redefinitionDiscoveryStrategy, Default.this.redefinitionBatchAllocator, Default.this.redefinitionListener, Default.this.redefinitionResubmissionStrategy, Default.this.injectionStrategy, Default.this.lambdaInstrumentationStrategy, Default.this.descriptionStrategy, Default.this.fallbackStrategy, Default.this.classFileBufferStrategy, Default.this.installationListener, Default.this.ignoreMatcher, CompoundList.of(Default.this.transformations, new Transformation(this.rawMatcher, this.transformers, this.terminal)));
            }

            @Override
            public Identified.Extendable transform(Transformer transformer) {
                return new Transforming(this.rawMatcher, CompoundList.of(this.transformers, transformer), this.terminal);
            }

            @Override
            public AgentBuilder asTerminalTransformation() {
                return new Transforming(this.rawMatcher, this.transformers, true);
            }

            @Override
            public Identified.Narrowable and(RawMatcher rawMatcher) {
                return new Transforming(new RawMatcher.Conjunction(this.rawMatcher, rawMatcher), this.transformers, this.terminal);
            }

            @Override
            public Identified.Narrowable or(RawMatcher rawMatcher) {
                return new Transforming(new RawMatcher.Disjunction(this.rawMatcher, rawMatcher), this.transformers, this.terminal);
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
                if (this.terminal != ((Transforming)object).terminal) {
                    return false;
                }
                if (!this.rawMatcher.equals(((Transforming)object).rawMatcher)) {
                    return false;
                }
                if (!((Object)this.transformers).equals(((Transforming)object).transformers)) {
                    return false;
                }
                return Default.this.equals(((Transforming)object).Default.this);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.rawMatcher.hashCode()) * 31 + ((Object)this.transformers).hashCode()) * 31 + this.terminal) * 31 + Default.this.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Ignoring
        extends Delegator.Matchable<Ignored>
        implements Ignored {
            private final RawMatcher rawMatcher;

            protected Ignoring(RawMatcher rawMatcher) {
                this.rawMatcher = rawMatcher;
            }

            @Override
            protected AgentBuilder materialize() {
                return new Default(Default.this.byteBuddy, Default.this.listener, Default.this.circularityLock, Default.this.poolStrategy, Default.this.typeStrategy, Default.this.locationStrategy, Default.this.classFileLocator, Default.this.nativeMethodStrategy, Default.this.warmupStrategy, Default.this.transformerDecorator, Default.this.initializationStrategy, Default.this.redefinitionStrategy, Default.this.redefinitionDiscoveryStrategy, Default.this.redefinitionBatchAllocator, Default.this.redefinitionListener, Default.this.redefinitionResubmissionStrategy, Default.this.injectionStrategy, Default.this.lambdaInstrumentationStrategy, Default.this.descriptionStrategy, Default.this.fallbackStrategy, Default.this.classFileBufferStrategy, Default.this.installationListener, this.rawMatcher, Default.this.transformations);
            }

            @Override
            public Ignored and(RawMatcher rawMatcher) {
                return new Ignoring(new RawMatcher.Conjunction(this.rawMatcher, rawMatcher));
            }

            @Override
            public Ignored or(RawMatcher rawMatcher) {
                return new Ignoring(new RawMatcher.Disjunction(this.rawMatcher, rawMatcher));
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
                if (!this.rawMatcher.equals(((Ignoring)object).rawMatcher)) {
                    return false;
                }
                return Default.this.equals(((Ignoring)object).Default.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.rawMatcher.hashCode()) * 31 + Default.this.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static abstract class Delegator
        implements AgentBuilder {
            protected Delegator() {
            }

            protected abstract AgentBuilder materialize();

            @Override
            public AgentBuilder with(ByteBuddy byteBuddy) {
                return this.materialize().with(byteBuddy);
            }

            @Override
            public AgentBuilder with(Listener listener) {
                return this.materialize().with(listener);
            }

            @Override
            public AgentBuilder with(CircularityLock circularityLock) {
                return this.materialize().with(circularityLock);
            }

            @Override
            public AgentBuilder with(TypeStrategy typeStrategy) {
                return this.materialize().with(typeStrategy);
            }

            @Override
            public AgentBuilder with(PoolStrategy poolStrategy) {
                return this.materialize().with(poolStrategy);
            }

            @Override
            public AgentBuilder with(LocationStrategy locationStrategy) {
                return this.materialize().with(locationStrategy);
            }

            @Override
            public AgentBuilder with(ClassFileLocator classFileLocator) {
                return this.materialize().with(classFileLocator);
            }

            @Override
            public AgentBuilder with(InitializationStrategy initializationStrategy) {
                return this.materialize().with(initializationStrategy);
            }

            @Override
            public RedefinitionListenable.WithoutBatchStrategy with(RedefinitionStrategy redefinitionStrategy) {
                return this.materialize().with(redefinitionStrategy);
            }

            @Override
            public AgentBuilder with(LambdaInstrumentationStrategy lambdaInstrumentationStrategy) {
                return this.materialize().with(lambdaInstrumentationStrategy);
            }

            @Override
            public AgentBuilder with(DescriptionStrategy descriptionStrategy) {
                return this.materialize().with(descriptionStrategy);
            }

            @Override
            public AgentBuilder with(FallbackStrategy fallbackStrategy) {
                return this.materialize().with(fallbackStrategy);
            }

            @Override
            public AgentBuilder with(ClassFileBufferStrategy classFileBufferStrategy) {
                return this.materialize().with(classFileBufferStrategy);
            }

            @Override
            public AgentBuilder with(InstallationListener installationListener) {
                return this.materialize().with(installationListener);
            }

            @Override
            public AgentBuilder with(InjectionStrategy injectionStrategy) {
                return this.materialize().with(injectionStrategy);
            }

            @Override
            public AgentBuilder with(TransformerDecorator transformerDecorator) {
                return this.materialize().with(transformerDecorator);
            }

            @Override
            public AgentBuilder enableNativeMethodPrefix(String prefix) {
                return this.materialize().enableNativeMethodPrefix(prefix);
            }

            @Override
            public AgentBuilder disableNativeMethodPrefix() {
                return this.materialize().disableNativeMethodPrefix();
            }

            @Override
            public AgentBuilder disableClassFormatChanges() {
                return this.materialize().disableClassFormatChanges();
            }

            @Override
            public AgentBuilder warmUp(Class<?> ... type) {
                return this.materialize().warmUp(type);
            }

            @Override
            public AgentBuilder warmUp(Collection<Class<?>> types) {
                return this.materialize().warmUp(types);
            }

            @Override
            public AgentBuilder assureReadEdgeTo(Instrumentation instrumentation, Class<?> ... type) {
                return this.materialize().assureReadEdgeTo(instrumentation, type);
            }

            @Override
            public AgentBuilder assureReadEdgeTo(Instrumentation instrumentation, JavaModule ... module) {
                return this.materialize().assureReadEdgeTo(instrumentation, module);
            }

            @Override
            public AgentBuilder assureReadEdgeTo(Instrumentation instrumentation, Collection<? extends JavaModule> modules) {
                return this.materialize().assureReadEdgeTo(instrumentation, modules);
            }

            @Override
            public AgentBuilder assureReadEdgeFromAndTo(Instrumentation instrumentation, Class<?> ... type) {
                return this.materialize().assureReadEdgeFromAndTo(instrumentation, type);
            }

            @Override
            public AgentBuilder assureReadEdgeFromAndTo(Instrumentation instrumentation, JavaModule ... module) {
                return this.materialize().assureReadEdgeFromAndTo(instrumentation, module);
            }

            @Override
            public AgentBuilder assureReadEdgeFromAndTo(Instrumentation instrumentation, Collection<? extends JavaModule> modules) {
                return this.materialize().assureReadEdgeFromAndTo(instrumentation, modules);
            }

            @Override
            public Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher) {
                return this.materialize().type(typeMatcher);
            }

            @Override
            public Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
                return this.materialize().type(typeMatcher, classLoaderMatcher);
            }

            @Override
            public Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                return this.materialize().type(typeMatcher, classLoaderMatcher, moduleMatcher);
            }

            @Override
            public Identified.Narrowable type(RawMatcher matcher) {
                return this.materialize().type(matcher);
            }

            @Override
            public Ignored ignore(ElementMatcher<? super TypeDescription> ignoredTypes) {
                return this.materialize().ignore(ignoredTypes);
            }

            @Override
            public Ignored ignore(ElementMatcher<? super TypeDescription> ignoredTypes, ElementMatcher<? super ClassLoader> ignoredClassLoaders) {
                return this.materialize().ignore(ignoredTypes, ignoredClassLoaders);
            }

            @Override
            public Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                return this.materialize().ignore(typeMatcher, classLoaderMatcher, moduleMatcher);
            }

            @Override
            public Ignored ignore(RawMatcher rawMatcher) {
                return this.materialize().ignore(rawMatcher);
            }

            @Override
            public ClassFileTransformer makeRaw() {
                return this.materialize().makeRaw();
            }

            @Override
            public ResettableClassFileTransformer installOn(Instrumentation instrumentation) {
                return this.materialize().installOn(instrumentation);
            }

            @Override
            public ResettableClassFileTransformer installOnByteBuddyAgent() {
                return this.materialize().installOnByteBuddyAgent();
            }

            @Override
            public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                return this.materialize().patchOn(instrumentation, classFileTransformer);
            }

            @Override
            public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, RawMatcher differentialMatcher) {
                return this.materialize().patchOn(instrumentation, classFileTransformer, differentialMatcher);
            }

            @Override
            public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, PatchMode patchMode) {
                return this.materialize().patchOn(instrumentation, classFileTransformer, patchMode);
            }

            @Override
            public ResettableClassFileTransformer patchOn(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, RawMatcher differentialMatcher, PatchMode patchMode) {
                return this.materialize().patchOn(instrumentation, classFileTransformer, differentialMatcher, patchMode);
            }

            @Override
            public ResettableClassFileTransformer patchOnByteBuddyAgent(ResettableClassFileTransformer classFileTransformer) {
                return this.materialize().patchOnByteBuddyAgent(classFileTransformer);
            }

            @Override
            public ResettableClassFileTransformer patchOnByteBuddyAgent(ResettableClassFileTransformer classFileTransformer, PatchMode patchMode) {
                return this.materialize().patchOnByteBuddyAgent(classFileTransformer, patchMode);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class Matchable<S extends net.bytebuddy.agent.builder.AgentBuilder$Matchable<S>>
            extends Delegator
            implements net.bytebuddy.agent.builder.AgentBuilder$Matchable<S> {
                protected Matchable() {
                }

                @Override
                public S and(ElementMatcher<? super TypeDescription> typeMatcher) {
                    return this.and(typeMatcher, ElementMatchers.any());
                }

                @Override
                public S and(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
                    return this.and(typeMatcher, classLoaderMatcher, ElementMatchers.any());
                }

                @Override
                public S and(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                    return (S)this.and(new RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher));
                }

                @Override
                public S or(ElementMatcher<? super TypeDescription> typeMatcher) {
                    return this.or(typeMatcher, ElementMatchers.any());
                }

                @Override
                public S or(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
                    return this.or(typeMatcher, classLoaderMatcher, ElementMatchers.any());
                }

                @Override
                public S or(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                    return (S)this.or(new RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class ExecutingTransformer
        extends ResettableClassFileTransformer.AbstractBase {
            protected static final Factory FACTORY;
            private final ByteBuddy byteBuddy;
            private final PoolStrategy poolStrategy;
            private final TypeStrategy typeStrategy;
            private final Listener listener;
            private final NativeMethodStrategy nativeMethodStrategy;
            private final InitializationStrategy initializationStrategy;
            private final InjectionStrategy injectionStrategy;
            private final LambdaInstrumentationStrategy lambdaInstrumentationStrategy;
            private final DescriptionStrategy descriptionStrategy;
            private final LocationStrategy locationStrategy;
            private final ClassFileLocator classFileLocator;
            private final FallbackStrategy fallbackStrategy;
            private final ClassFileBufferStrategy classFileBufferStrategy;
            private final InstallationListener installationListener;
            private final RawMatcher ignoreMatcher;
            private final RedefinitionStrategy.ResubmissionEnforcer resubmissionEnforcer;
            private final List<Transformation> transformations;
            private final CircularityLock circularityLock;
            @MaybeNull
            private final Object accessControlContext;
            private static final boolean ACCESS_CONTROLLER;

            public ExecutingTransformer(ByteBuddy byteBuddy, Listener listener, PoolStrategy poolStrategy, TypeStrategy typeStrategy, LocationStrategy locationStrategy, ClassFileLocator classFileLocator, NativeMethodStrategy nativeMethodStrategy, InitializationStrategy initializationStrategy, InjectionStrategy injectionStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, ClassFileBufferStrategy classFileBufferStrategy, InstallationListener installationListener, RawMatcher ignoreMatcher, RedefinitionStrategy.ResubmissionEnforcer resubmissionEnforcer, List<Transformation> transformations, CircularityLock circularityLock) {
                this.byteBuddy = byteBuddy;
                this.typeStrategy = typeStrategy;
                this.poolStrategy = poolStrategy;
                this.locationStrategy = locationStrategy;
                this.classFileLocator = classFileLocator;
                this.listener = listener;
                this.nativeMethodStrategy = nativeMethodStrategy;
                this.initializationStrategy = initializationStrategy;
                this.injectionStrategy = injectionStrategy;
                this.lambdaInstrumentationStrategy = lambdaInstrumentationStrategy;
                this.descriptionStrategy = descriptionStrategy;
                this.fallbackStrategy = fallbackStrategy;
                this.classFileBufferStrategy = classFileBufferStrategy;
                this.installationListener = installationListener;
                this.ignoreMatcher = ignoreMatcher;
                this.resubmissionEnforcer = resubmissionEnforcer;
                this.transformations = transformations;
                this.circularityLock = circularityLock;
                this.accessControlContext = ExecutingTransformer.getContext();
            }

            @MaybeNull
            @AccessControllerPlugin.Enhance
            private static Object getContext() {
                if (ACCESS_CONTROLLER) {
                    return AccessController.getContext();
                }
                return null;
            }

            @AccessControllerPlugin.Enhance
            private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction, @MaybeNull Object object) {
                PrivilegedAction<T> action;
                if (ACCESS_CONTROLLER) {
                    return AccessController.doPrivileged(privilegedAction, (AccessControlContext)object);
                }
                return action.run();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            @MaybeNull
            public byte[] transform(@MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
                if (this.circularityLock.acquire()) {
                    try {
                        byte[] byArray = ExecutingTransformer.doPrivileged(new LegacyVmDispatcher(classLoader, internalTypeName, classBeingRedefined, protectionDomain, binaryRepresentation), this.accessControlContext);
                        Object var8_7 = null;
                        this.circularityLock.release();
                        return byArray;
                    }
                    catch (Throwable throwable) {
                        Object var8_8 = null;
                        this.circularityLock.release();
                        throw throwable;
                    }
                }
                return NO_TRANSFORMATION;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @MaybeNull
            protected byte[] transform(Object rawModule, @MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
                if (this.circularityLock.acquire()) {
                    try {
                        byte[] byArray = ExecutingTransformer.doPrivileged(new Java9CapableVmDispatcher(rawModule, classLoader, internalTypeName, classBeingRedefined, protectionDomain, binaryRepresentation), this.accessControlContext);
                        Object var9_8 = null;
                        this.circularityLock.release();
                        return byArray;
                    }
                    catch (Throwable throwable) {
                        Object var9_9 = null;
                        this.circularityLock.release();
                        throw throwable;
                    }
                }
                return NO_TRANSFORMATION;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @MaybeNull
            private byte[] transform(@MaybeNull JavaModule module, @MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
                byte[] byArray;
                if (internalTypeName == null || !this.lambdaInstrumentationStrategy.isInstrumented(classBeingRedefined)) {
                    return NO_TRANSFORMATION;
                }
                String name = internalTypeName.replace('/', '.');
                try {
                    if (this.resubmissionEnforcer.isEnforced(name, classLoader, module, classBeingRedefined)) {
                        return NO_TRANSFORMATION;
                    }
                }
                catch (Throwable throwable) {
                    try {
                        this.listener.onDiscovery(name, classLoader, module, classBeingRedefined != null);
                        Object var10_11 = null;
                        this.listener.onError(name, classLoader, module, classBeingRedefined != null, throwable);
                    }
                    catch (Throwable throwable2) {
                        Object var10_12 = null;
                        this.listener.onError(name, classLoader, module, classBeingRedefined != null, throwable);
                        throw throwable2;
                    }
                    throw new IllegalStateException("Failed transformation of " + name, throwable);
                }
                this.listener.onDiscovery(name, classLoader, module, classBeingRedefined != null);
                ClassFileLocator.Compound classFileLocator = new ClassFileLocator.Compound(this.classFileBufferStrategy.resolve(name, binaryRepresentation, classLoader, module, protectionDomain), this.classFileLocator, this.locationStrategy.classFileLocator(classLoader, module));
                TypePool typePool = this.classFileBufferStrategy.typePool(this.poolStrategy, classFileLocator, classLoader, name);
                try {
                    byArray = this.doTransform(module, classLoader, name, classBeingRedefined, classBeingRedefined != null, protectionDomain, typePool, classFileLocator);
                }
                catch (Throwable throwable) {
                    block14: {
                        if (classBeingRedefined == null || !this.descriptionStrategy.isLoadedFirst() || !this.fallbackStrategy.isFallback(classBeingRedefined, throwable)) break block14;
                        byte[] byArray2 = this.doTransform(module, classLoader, name, NOT_PREVIOUSLY_DEFINED, true, protectionDomain, typePool, classFileLocator);
                        Object var13_18 = null;
                        this.listener.onComplete(name, classLoader, module, classBeingRedefined != null);
                        return byArray2;
                    }
                    try {
                        try {
                            throw throwable;
                        }
                        catch (Throwable throwable3) {
                            this.listener.onError(name, classLoader, module, classBeingRedefined != null, throwable3);
                            throw new IllegalStateException("Failed transformation of " + name, throwable3);
                        }
                    }
                    catch (Throwable throwable4) {
                        Object var13_19 = null;
                        this.listener.onComplete(name, classLoader, module, classBeingRedefined != null);
                        throw throwable4;
                    }
                }
                Object var13_17 = null;
                this.listener.onComplete(name, classLoader, module, classBeingRedefined != null);
                return byArray;
            }

            @MaybeNull
            private byte[] doTransform(@MaybeNull JavaModule module, @MaybeNull ClassLoader classLoader, String name, @MaybeNull Class<?> classBeingRedefined, boolean loaded, ProtectionDomain protectionDomain, TypePool typePool, ClassFileLocator classFileLocator) {
                TypeDescription typeDescription = this.descriptionStrategy.apply(name, classBeingRedefined, typePool, this.circularityLock, classLoader, module);
                ArrayList<Transformer> transformers = new ArrayList<Transformer>();
                if (!this.ignoreMatcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) {
                    for (Transformation transformation : this.transformations) {
                        if (!transformation.getMatcher().matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) continue;
                        transformers.addAll(transformation.getTransformers());
                        if (!transformation.isTerminal()) continue;
                        break;
                    }
                }
                if (transformers.isEmpty()) {
                    this.listener.onIgnored(typeDescription, classLoader, module, loaded);
                    return Transformation.NONE;
                }
                DynamicType.Builder<?> builder = this.typeStrategy.builder(typeDescription, this.byteBuddy, classFileLocator, this.nativeMethodStrategy.resolve(), classLoader, module, protectionDomain);
                InitializationStrategy.Dispatcher dispatcher = this.initializationStrategy.dispatcher();
                for (Transformer transformer : transformers) {
                    builder = transformer.transform(builder, typeDescription, classLoader, module, protectionDomain);
                }
                DynamicType.Unloaded<?> dynamicType = dispatcher.apply(builder).make(TypeResolutionStrategy.Disabled.INSTANCE, typePool);
                dispatcher.register(dynamicType, classLoader, protectionDomain, this.injectionStrategy);
                this.listener.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                return dynamicType.getBytes();
            }

            @Override
            public Iterator<Transformer> iterator(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                return this.ignoreMatcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain) ? Collections.emptySet().iterator() : new Transformation.TransformerIterator(typeDescription, classLoader, module, classBeingRedefined, protectionDomain, this.transformations);
            }

            @Override
            public synchronized boolean reset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, RedefinitionStrategy redefinitionStrategy, RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, RedefinitionStrategy.Listener redefinitionListener) {
                if (instrumentation.removeTransformer(classFileTransformer)) {
                    redefinitionStrategy.apply(instrumentation, this.poolStrategy, this.locationStrategy, this.descriptionStrategy, this.fallbackStrategy, redefinitionDiscoveryStrategy, this.lambdaInstrumentationStrategy, Listener.NoOp.INSTANCE, redefinitionListener, new Transformation.SimpleMatcher(this.ignoreMatcher, this.transformations), redefinitionBatchAllocator, CircularityLock.Inactive.INSTANCE);
                    this.installationListener.onReset(instrumentation, classFileTransformer);
                    return true;
                }
                return false;
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            static {
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
                FACTORY = (Factory)Default.doPrivileged(Factory.CreationAction.INSTANCE);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class Java9CapableVmDispatcher
            implements PrivilegedAction<byte[]> {
                private final Object rawModule;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final ClassLoader classLoader;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final String internalTypeName;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Class<?> classBeingRedefined;
                private final ProtectionDomain protectionDomain;
                private final byte[] binaryRepresentation;

                protected Java9CapableVmDispatcher(@MaybeNull Object rawModule, @MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
                    this.rawModule = rawModule;
                    this.classLoader = classLoader;
                    this.internalTypeName = internalTypeName;
                    this.classBeingRedefined = classBeingRedefined;
                    this.protectionDomain = protectionDomain;
                    this.binaryRepresentation = binaryRepresentation;
                }

                @Override
                @MaybeNull
                public byte[] run() {
                    return ExecutingTransformer.this.transform(JavaModule.of(this.rawModule), this.classLoader, this.internalTypeName, this.classBeingRedefined, this.protectionDomain, this.binaryRepresentation);
                }

                public boolean equals(@MaybeNull Object object) {
                    block29: {
                        block28: {
                            Class<?> clazz;
                            block27: {
                                Class<?> clazz2;
                                Class<?> clazz3;
                                block26: {
                                    block25: {
                                        Class<?> clazz4;
                                        block24: {
                                            block23: {
                                                block22: {
                                                    String string;
                                                    block21: {
                                                        if (this == object) {
                                                            return true;
                                                        }
                                                        if (object == null) {
                                                            return false;
                                                        }
                                                        if (this.getClass() != object.getClass()) {
                                                            return false;
                                                        }
                                                        clazz3 = ((Java9CapableVmDispatcher)object).internalTypeName;
                                                        clazz2 = this.internalTypeName;
                                                        string = clazz2;
                                                        if (clazz3 == null) break block21;
                                                        if (string == null) break block22;
                                                        if (!((String)((Object)clazz2)).equals(clazz3)) {
                                                            return false;
                                                        }
                                                        break block23;
                                                    }
                                                    if (string == null) break block23;
                                                }
                                                return false;
                                            }
                                            if (!this.rawModule.equals(((Java9CapableVmDispatcher)object).rawModule)) {
                                                return false;
                                            }
                                            clazz3 = ((Java9CapableVmDispatcher)object).classLoader;
                                            clazz4 = clazz2 = this.classLoader;
                                            if (clazz3 == null) break block24;
                                            if (clazz4 == null) break block25;
                                            if (!clazz2.equals(clazz3)) {
                                                return false;
                                            }
                                            break block26;
                                        }
                                        if (clazz4 == null) break block26;
                                    }
                                    return false;
                                }
                                clazz3 = ((Java9CapableVmDispatcher)object).classBeingRedefined;
                                clazz = clazz2 = this.classBeingRedefined;
                                if (clazz3 == null) break block27;
                                if (clazz == null) break block28;
                                if (!clazz2.equals(clazz3)) {
                                    return false;
                                }
                                break block29;
                            }
                            if (clazz == null) break block29;
                        }
                        return false;
                    }
                    if (!this.protectionDomain.equals(((Java9CapableVmDispatcher)object).protectionDomain)) {
                        return false;
                    }
                    if (!Arrays.equals(this.binaryRepresentation, ((Java9CapableVmDispatcher)object).binaryRepresentation)) {
                        return false;
                    }
                    return ExecutingTransformer.this.equals(((Java9CapableVmDispatcher)object).ExecutingTransformer.this);
                }

                public int hashCode() {
                    int n = (this.getClass().hashCode() * 31 + this.rawModule.hashCode()) * 31;
                    Class<?> clazz = this.classLoader;
                    if (clazz != null) {
                        n = n + clazz.hashCode();
                    }
                    int n2 = n * 31;
                    clazz = this.internalTypeName;
                    if (clazz != null) {
                        n2 = n2 + ((String)((Object)clazz)).hashCode();
                    }
                    int n3 = n2 * 31;
                    clazz = this.classBeingRedefined;
                    if (clazz != null) {
                        n3 = n3 + clazz.hashCode();
                    }
                    return ((n3 * 31 + this.protectionDomain.hashCode()) * 31 + Arrays.hashCode(this.binaryRepresentation)) * 31 + ExecutingTransformer.this.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class LegacyVmDispatcher
            implements PrivilegedAction<byte[]> {
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final ClassLoader classLoader;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final String internalTypeName;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Class<?> classBeingRedefined;
                private final ProtectionDomain protectionDomain;
                private final byte[] binaryRepresentation;

                protected LegacyVmDispatcher(@MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
                    this.classLoader = classLoader;
                    this.internalTypeName = internalTypeName;
                    this.classBeingRedefined = classBeingRedefined;
                    this.protectionDomain = protectionDomain;
                    this.binaryRepresentation = binaryRepresentation;
                }

                @Override
                @MaybeNull
                public byte[] run() {
                    return ExecutingTransformer.this.transform(JavaModule.UNSUPPORTED, this.classLoader, this.internalTypeName, this.classBeingRedefined, this.protectionDomain, this.binaryRepresentation);
                }

                public boolean equals(@MaybeNull Object object) {
                    block28: {
                        block27: {
                            Class<?> clazz;
                            block26: {
                                Class<?> clazz2;
                                Class<?> clazz3;
                                block25: {
                                    block24: {
                                        Class<?> clazz4;
                                        block23: {
                                            block22: {
                                                block21: {
                                                    String string;
                                                    block20: {
                                                        if (this == object) {
                                                            return true;
                                                        }
                                                        if (object == null) {
                                                            return false;
                                                        }
                                                        if (this.getClass() != object.getClass()) {
                                                            return false;
                                                        }
                                                        clazz3 = ((LegacyVmDispatcher)object).internalTypeName;
                                                        clazz2 = this.internalTypeName;
                                                        string = clazz2;
                                                        if (clazz3 == null) break block20;
                                                        if (string == null) break block21;
                                                        if (!((String)((Object)clazz2)).equals(clazz3)) {
                                                            return false;
                                                        }
                                                        break block22;
                                                    }
                                                    if (string == null) break block22;
                                                }
                                                return false;
                                            }
                                            clazz3 = ((LegacyVmDispatcher)object).classLoader;
                                            clazz4 = clazz2 = this.classLoader;
                                            if (clazz3 == null) break block23;
                                            if (clazz4 == null) break block24;
                                            if (!clazz2.equals(clazz3)) {
                                                return false;
                                            }
                                            break block25;
                                        }
                                        if (clazz4 == null) break block25;
                                    }
                                    return false;
                                }
                                clazz3 = ((LegacyVmDispatcher)object).classBeingRedefined;
                                clazz = clazz2 = this.classBeingRedefined;
                                if (clazz3 == null) break block26;
                                if (clazz == null) break block27;
                                if (!clazz2.equals(clazz3)) {
                                    return false;
                                }
                                break block28;
                            }
                            if (clazz == null) break block28;
                        }
                        return false;
                    }
                    if (!this.protectionDomain.equals(((LegacyVmDispatcher)object).protectionDomain)) {
                        return false;
                    }
                    if (!Arrays.equals(this.binaryRepresentation, ((LegacyVmDispatcher)object).binaryRepresentation)) {
                        return false;
                    }
                    return ExecutingTransformer.this.equals(((LegacyVmDispatcher)object).ExecutingTransformer.this);
                }

                public int hashCode() {
                    int n = this.getClass().hashCode() * 31;
                    Class<?> clazz = this.classLoader;
                    if (clazz != null) {
                        n = n + clazz.hashCode();
                    }
                    int n2 = n * 31;
                    clazz = this.internalTypeName;
                    if (clazz != null) {
                        n2 = n2 + ((String)((Object)clazz)).hashCode();
                    }
                    int n3 = n2 * 31;
                    clazz = this.classBeingRedefined;
                    if (clazz != null) {
                        n3 = n3 + clazz.hashCode();
                    }
                    return ((n3 * 31 + this.protectionDomain.hashCode()) * 31 + Arrays.hashCode(this.binaryRepresentation)) * 31 + ExecutingTransformer.this.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static interface Factory {
                public ResettableClassFileTransformer make(ByteBuddy var1, Listener var2, PoolStrategy var3, TypeStrategy var4, LocationStrategy var5, ClassFileLocator var6, NativeMethodStrategy var7, InitializationStrategy var8, InjectionStrategy var9, LambdaInstrumentationStrategy var10, DescriptionStrategy var11, FallbackStrategy var12, ClassFileBufferStrategy var13, InstallationListener var14, RawMatcher var15, RedefinitionStrategy.ResubmissionEnforcer var16, List<Transformation> var17, CircularityLock var18);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForLegacyVm implements Factory
                {
                    INSTANCE;


                    @Override
                    public ResettableClassFileTransformer make(ByteBuddy byteBuddy, Listener listener, PoolStrategy poolStrategy, TypeStrategy typeStrategy, LocationStrategy locationStrategy, ClassFileLocator classFileLocator, NativeMethodStrategy nativeMethodStrategy, InitializationStrategy initializationStrategy, InjectionStrategy injectionStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, ClassFileBufferStrategy classFileBufferStrategy, InstallationListener installationListener, RawMatcher ignoreMatcher, RedefinitionStrategy.ResubmissionEnforcer resubmissionEnforcer, List<Transformation> transformations, CircularityLock circularityLock) {
                        return new ExecutingTransformer(byteBuddy, listener, poolStrategy, typeStrategy, locationStrategy, classFileLocator, nativeMethodStrategy, initializationStrategy, injectionStrategy, lambdaInstrumentationStrategy, descriptionStrategy, fallbackStrategy, classFileBufferStrategy, installationListener, ignoreMatcher, resubmissionEnforcer, transformations, circularityLock);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForJava9CapableVm
                implements Factory {
                    private final Constructor<? extends ResettableClassFileTransformer> executingTransformer;

                    protected ForJava9CapableVm(Constructor<? extends ResettableClassFileTransformer> executingTransformer) {
                        this.executingTransformer = executingTransformer;
                    }

                    @Override
                    public ResettableClassFileTransformer make(ByteBuddy byteBuddy, Listener listener, PoolStrategy poolStrategy, TypeStrategy typeStrategy, LocationStrategy locationStrategy, ClassFileLocator classFileLocator, NativeMethodStrategy nativeMethodStrategy, InitializationStrategy initializationStrategy, InjectionStrategy injectionStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, ClassFileBufferStrategy classFileBufferStrategy, InstallationListener installationListener, RawMatcher ignoreMatcher, RedefinitionStrategy.ResubmissionEnforcer resubmissionEnforcer, List<Transformation> transformations, CircularityLock circularityLock) {
                        try {
                            return this.executingTransformer.newInstance(new Object[]{byteBuddy, listener, poolStrategy, typeStrategy, locationStrategy, classFileLocator, nativeMethodStrategy, initializationStrategy, injectionStrategy, lambdaInstrumentationStrategy, descriptionStrategy, fallbackStrategy, classFileBufferStrategy, installationListener, ignoreMatcher, resubmissionEnforcer, transformations, circularityLock});
                        }
                        catch (IllegalAccessException exception) {
                            throw new IllegalStateException("Cannot access " + this.executingTransformer, exception);
                        }
                        catch (InstantiationException exception) {
                            throw new IllegalStateException("Cannot instantiate " + this.executingTransformer.getDeclaringClass(), exception);
                        }
                        catch (InvocationTargetException exception) {
                            throw new IllegalStateException("Cannot invoke " + this.executingTransformer, exception.getTargetException());
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
                        return this.executingTransformer.equals(((ForJava9CapableVm)object).executingTransformer);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.executingTransformer.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum CreationAction implements PrivilegedAction<Factory>
                {
                    INSTANCE;


                    @Override
                    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
                    public Factory run() {
                        try {
                            return new ForJava9CapableVm(new ByteBuddy().with(TypeValidation.DISABLED).subclass(ExecutingTransformer.class).name(ExecutingTransformer.class.getName() + "$ByteBuddy$ModuleSupport").method(ElementMatchers.named("transform").and(ElementMatchers.takesArgument(0, JavaType.MODULE.load()))).intercept(MethodCall.invoke(ExecutingTransformer.class.getDeclaredMethod("transform", Object.class, ClassLoader.class, String.class, Class.class, ProtectionDomain.class, byte[].class)).onSuper().withAllArguments()).make().load(ExecutingTransformer.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER_PERSISTENT.with(ExecutingTransformer.class.getProtectionDomain())).getLoaded().getDeclaredConstructor(ByteBuddy.class, Listener.class, PoolStrategy.class, TypeStrategy.class, LocationStrategy.class, ClassFileLocator.class, NativeMethodStrategy.class, InitializationStrategy.class, InjectionStrategy.class, LambdaInstrumentationStrategy.class, DescriptionStrategy.class, FallbackStrategy.class, ClassFileBufferStrategy.class, InstallationListener.class, RawMatcher.class, RedefinitionStrategy.ResubmissionEnforcer.class, List.class, CircularityLock.class));
                        }
                        catch (Exception ignored) {
                            return ForLegacyVm.INSTANCE;
                        }
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Transformation {
            @AlwaysNull
            private static final byte[] NONE = null;
            private final RawMatcher matcher;
            private final List<Transformer> transformers;
            private final boolean terminal;

            protected Transformation(RawMatcher matcher, List<Transformer> transformers, boolean terminal) {
                this.matcher = matcher;
                this.transformers = transformers;
                this.terminal = terminal;
            }

            protected RawMatcher getMatcher() {
                return this.matcher;
            }

            protected List<Transformer> getTransformers() {
                return this.transformers;
            }

            protected boolean isTerminal() {
                return this.terminal;
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
                if (this.terminal != ((Transformation)object).terminal) {
                    return false;
                }
                if (!this.matcher.equals(((Transformation)object).matcher)) {
                    return false;
                }
                return ((Object)this.transformers).equals(((Transformation)object).transformers);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + ((Object)this.transformers).hashCode()) * 31 + this.terminal;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class TransformerIterator
            implements Iterator<Transformer> {
                private final TypeDescription typeDescription;
                @MaybeNull
                private final ClassLoader classLoader;
                @MaybeNull
                private final JavaModule module;
                @MaybeNull
                private final Class<?> classBeingRedefined;
                private final ProtectionDomain protectionDomain;
                private final Iterator<Transformation> transformations;
                private Iterator<Transformer> transformers;

                protected TransformerIterator(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain, List<Transformation> transformations) {
                    this.typeDescription = typeDescription;
                    this.classLoader = classLoader;
                    this.module = module;
                    this.classBeingRedefined = classBeingRedefined;
                    this.protectionDomain = protectionDomain;
                    this.transformations = transformations.iterator();
                    this.transformers = Collections.emptySet().iterator();
                    while (!this.transformers.hasNext() && this.transformations.hasNext()) {
                        Transformation transformation = this.transformations.next();
                        if (!transformation.getMatcher().matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) continue;
                        this.transformers = transformation.getTransformers().iterator();
                    }
                }

                @Override
                public boolean hasNext() {
                    return this.transformers.hasNext();
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Transformer next() {
                    Transformer transformer;
                    try {
                        transformer = this.transformers.next();
                        Object var3_2 = null;
                    }
                    catch (Throwable throwable) {
                        Object var3_3 = null;
                        while (!this.transformers.hasNext() && this.transformations.hasNext()) {
                            Transformation transformation = this.transformations.next();
                            if (!transformation.getMatcher().matches(this.typeDescription, this.classLoader, this.module, this.classBeingRedefined, this.protectionDomain)) continue;
                            this.transformers = transformation.getTransformers().iterator();
                        }
                        throw throwable;
                    }
                    while (!this.transformers.hasNext() && this.transformations.hasNext()) {
                        Transformation transformation = this.transformations.next();
                        if (!transformation.getMatcher().matches(this.typeDescription, this.classLoader, this.module, this.classBeingRedefined, this.protectionDomain)) continue;
                        this.transformers = transformation.getTransformers().iterator();
                    }
                    return transformer;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class DifferentialMatcher
            implements RawMatcher {
                private final RawMatcher ignoreMatcher;
                private final List<Transformation> transformations;
                private final ResettableClassFileTransformer classFileTransformer;

                protected DifferentialMatcher(RawMatcher ignoreMatcher, List<Transformation> transformations, ResettableClassFileTransformer classFileTransformer) {
                    this.ignoreMatcher = ignoreMatcher;
                    this.transformations = transformations;
                    this.classFileTransformer = classFileTransformer;
                }

                @Override
                public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                    Iterator<Transformer> iterator = this.classFileTransformer.iterator(typeDescription, classLoader, module, classBeingRedefined, protectionDomain);
                    if (this.ignoreMatcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) {
                        return iterator.hasNext();
                    }
                    for (Transformation transformation : this.transformations) {
                        if (!transformation.getMatcher().matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) continue;
                        for (Transformer transformer : transformation.getTransformers()) {
                            if (iterator.hasNext() && iterator.next().equals(transformer)) continue;
                            return true;
                        }
                    }
                    return iterator.hasNext();
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
                    if (!this.ignoreMatcher.equals(((DifferentialMatcher)object).ignoreMatcher)) {
                        return false;
                    }
                    if (!((Object)this.transformations).equals(((DifferentialMatcher)object).transformations)) {
                        return false;
                    }
                    return this.classFileTransformer.equals(((DifferentialMatcher)object).classFileTransformer);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.ignoreMatcher.hashCode()) * 31 + ((Object)this.transformations).hashCode()) * 31 + this.classFileTransformer.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class SimpleMatcher
            implements RawMatcher {
                private final RawMatcher ignoreMatcher;
                private final List<Transformation> transformations;

                protected SimpleMatcher(RawMatcher ignoreMatcher, List<Transformation> transformations) {
                    this.ignoreMatcher = ignoreMatcher;
                    this.transformations = transformations;
                }

                @Override
                public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                    if (this.ignoreMatcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) {
                        return false;
                    }
                    for (Transformation transformation : this.transformations) {
                        if (!transformation.getMatcher().matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) continue;
                        return true;
                    }
                    return false;
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
                    if (!this.ignoreMatcher.equals(((SimpleMatcher)object).ignoreMatcher)) {
                        return false;
                    }
                    return ((Object)this.transformations).equals(((SimpleMatcher)object).transformations);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.ignoreMatcher.hashCode()) * 31 + ((Object)this.transformations).hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface WarmupStrategy {
            public void apply(ResettableClassFileTransformer var1, LocationStrategy var2, RedefinitionStrategy var3, CircularityLock var4, InstallationListener var5);

            public WarmupStrategy with(Collection<Class<?>> var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Enabled
            implements WarmupStrategy {
                private static final Dispatcher DISPATCHER = (Dispatcher)Default.access$500(JavaDispatcher.of(Dispatcher.class));
                private final Set<Class<?>> types;

                protected Enabled(Set<Class<?>> types) {
                    this.types = types;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void apply(ResettableClassFileTransformer classFileTransformer, LocationStrategy locationStrategy, RedefinitionStrategy redefinitionStrategy, CircularityLock circularityLock, InstallationListener listener) {
                    listener.onBeforeWarmUp(this.types, classFileTransformer);
                    boolean transformed = false;
                    LinkedHashMap results = new LinkedHashMap();
                    for (Class<?> type : this.types) {
                        try {
                            Object var14_14;
                            JavaModule module = JavaModule.ofType(type);
                            byte[] binaryRepresentation = locationStrategy.classFileLocator(type.getClassLoader(), module).locate(type.getName()).resolve();
                            circularityLock.release();
                            try {
                                byte[] result;
                                if (module == null) {
                                    result = classFileTransformer.transform(type.getClassLoader(), Type.getInternalName(type), NOT_PREVIOUSLY_DEFINED, type.getProtectionDomain(), binaryRepresentation);
                                    transformed |= result != null;
                                    if (redefinitionStrategy.isEnabled()) {
                                        result = classFileTransformer.transform(type.getClassLoader(), Type.getInternalName(type), type, type.getProtectionDomain(), binaryRepresentation);
                                        transformed |= result != null;
                                    }
                                } else {
                                    result = DISPATCHER.transform(classFileTransformer, module.unwrap(), type.getClassLoader(), Type.getInternalName(type), NOT_PREVIOUSLY_DEFINED, type.getProtectionDomain(), binaryRepresentation);
                                    transformed |= result != null;
                                    if (redefinitionStrategy.isEnabled()) {
                                        result = DISPATCHER.transform(classFileTransformer, module.unwrap(), type.getClassLoader(), Type.getInternalName(type), type, type.getProtectionDomain(), binaryRepresentation);
                                        transformed |= result != null;
                                    }
                                }
                                results.put(type, result);
                                var14_14 = null;
                                circularityLock.acquire();
                            }
                            catch (Throwable throwable) {
                                var14_14 = null;
                                circularityLock.acquire();
                                throw throwable;
                            }
                            {
                            }
                        }
                        catch (Throwable throwable) {
                            listener.onWarmUpError(type, classFileTransformer, throwable);
                            results.put(type, NO_TRANSFORMATION);
                        }
                    }
                    listener.onAfterWarmUp(results, classFileTransformer, transformed);
                }

                @Override
                public WarmupStrategy with(Collection<Class<?>> types) {
                    LinkedHashSet combined = new LinkedHashSet(this.types);
                    combined.addAll(types);
                    return new Enabled(combined);
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
                    return ((Object)this.types).equals(((Enabled)object).types);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.types).hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @JavaDispatcher.Proxied(value="java.lang.instrument.ClassFileTransformer")
                protected static interface Dispatcher {
                    @MaybeNull
                    @JavaDispatcher.Proxied(value="transform")
                    public byte[] transform(ClassFileTransformer var1, @MaybeNull @JavaDispatcher.Proxied(value="java.lang.Module") Object var2, @MaybeNull ClassLoader var3, String var4, @MaybeNull Class<?> var5, ProtectionDomain var6, byte[] var7) throws IllegalClassFormatException;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements WarmupStrategy
            {
                INSTANCE;


                @Override
                public void apply(ResettableClassFileTransformer classFileTransformer, LocationStrategy locationStrategy, RedefinitionStrategy redefinitionStrategy, CircularityLock circularityLock, InstallationListener listener) {
                }

                @Override
                public WarmupStrategy with(Collection<Class<?>> types) {
                    return new Enabled(new LinkedHashSet(types));
                }
            }
        }

        protected static interface NativeMethodStrategy {
            public MethodNameTransformer resolve();

            public void apply(Instrumentation var1, ClassFileTransformer var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForPrefix
            implements NativeMethodStrategy {
                private final String prefix;

                protected ForPrefix(String prefix) {
                    this.prefix = prefix;
                }

                protected static NativeMethodStrategy of(String prefix) {
                    if (prefix.length() == 0) {
                        throw new IllegalArgumentException("A method name prefix must not be the empty string");
                    }
                    return new ForPrefix(prefix);
                }

                public MethodNameTransformer resolve() {
                    return new MethodNameTransformer.Prefixing(this.prefix);
                }

                public void apply(Instrumentation instrumentation, ClassFileTransformer classFileTransformer) {
                    if (!DISPATCHER.isNativeMethodPrefixSupported(instrumentation)) {
                        throw new IllegalArgumentException("A prefix for native methods is not supported: " + instrumentation);
                    }
                    DISPATCHER.setNativeMethodPrefix(instrumentation, classFileTransformer, this.prefix);
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
                    return this.prefix.equals(((ForPrefix)object).prefix);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.prefix.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Disabled implements NativeMethodStrategy
            {
                INSTANCE;


                @Override
                public MethodNameTransformer resolve() {
                    return MethodNameTransformer.Suffixing.withRandomSuffix();
                }

                @Override
                public void apply(Instrumentation instrumentation, ClassFileTransformer classFileTransformer) {
                }
            }
        }

        @JavaDispatcher.Proxied(value="java.lang.instrument.Instrumentation")
        protected static interface Dispatcher {
            @JavaDispatcher.Defaults
            @JavaDispatcher.Proxied(value="isNativeMethodPrefixSupported")
            public boolean isNativeMethodPrefixSupported(Instrumentation var1);

            @JavaDispatcher.Proxied(value="setNativeMethodPrefix")
            public void setNativeMethodPrefix(Instrumentation var1, ClassFileTransformer var2, String var3);

            @JavaDispatcher.Proxied(value="addTransformer")
            public void addTransformer(Instrumentation var1, ClassFileTransformer var2, boolean var3);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PatchMode {
        GAP{

            protected Handler toHandler(ResettableClassFileTransformer classFileTransformer) {
                return new Handler.ForPatchWithGap(classFileTransformer);
            }
        }
        ,
        OVERLAP{

            protected Handler toHandler(ResettableClassFileTransformer classFileTransformer) {
                return new Handler.ForPatchWithOverlap(classFileTransformer);
            }
        }
        ,
        SUBSTITUTE{

            protected Handler toHandler(ResettableClassFileTransformer classFileTransformer) {
                if (!(classFileTransformer instanceof ResettableClassFileTransformer.Substitutable)) {
                    throw new IllegalArgumentException("Original class file transformer is not substitutable: " + classFileTransformer);
                }
                return new Handler.ForPatchWithSubstitution((ResettableClassFileTransformer.Substitutable)classFileTransformer);
            }
        };


        protected static PatchMode of(ResettableClassFileTransformer classFileTransformer) {
            return classFileTransformer instanceof ResettableClassFileTransformer.Substitutable ? SUBSTITUTE : OVERLAP;
        }

        protected abstract Handler toHandler(ResettableClassFileTransformer var1);

        protected static interface Handler {
            public void onBeforeRegistration(Instrumentation var1);

            public boolean onRegistration(ResettableClassFileTransformer var1);

            public void onAfterRegistration(Instrumentation var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForPatchWithSubstitution
            implements Handler {
                private final ResettableClassFileTransformer.Substitutable classFileTransformer;

                protected ForPatchWithSubstitution(ResettableClassFileTransformer.Substitutable classFileTransformer) {
                    this.classFileTransformer = classFileTransformer;
                }

                public void onBeforeRegistration(Instrumentation instrumentation) {
                }

                public boolean onRegistration(ResettableClassFileTransformer classFileTransformer) {
                    this.classFileTransformer.substitute(classFileTransformer);
                    return false;
                }

                public void onAfterRegistration(Instrumentation instrumentation) {
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
                    return this.classFileTransformer.equals(((ForPatchWithSubstitution)object).classFileTransformer);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.classFileTransformer.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForPatchWithOverlap
            implements Handler {
                private final ResettableClassFileTransformer classFileTransformer;

                protected ForPatchWithOverlap(ResettableClassFileTransformer classFileTransformer) {
                    this.classFileTransformer = classFileTransformer;
                }

                public void onBeforeRegistration(Instrumentation instrumentation) {
                }

                public boolean onRegistration(ResettableClassFileTransformer classFileTransformer) {
                    return true;
                }

                public void onAfterRegistration(Instrumentation instrumentation) {
                    if (!this.classFileTransformer.reset(instrumentation, RedefinitionStrategy.DISABLED)) {
                        throw new IllegalArgumentException("Failed to deregister patched class file transformer: " + this.classFileTransformer);
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
                    return this.classFileTransformer.equals(((ForPatchWithOverlap)object).classFileTransformer);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.classFileTransformer.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForPatchWithGap
            implements Handler {
                private final ResettableClassFileTransformer classFileTransformer;

                protected ForPatchWithGap(ResettableClassFileTransformer classFileTransformer) {
                    this.classFileTransformer = classFileTransformer;
                }

                public void onBeforeRegistration(Instrumentation instrumentation) {
                    if (!this.classFileTransformer.reset(instrumentation, RedefinitionStrategy.DISABLED)) {
                        throw new IllegalArgumentException("Failed to deregister patched class file transformer: " + this.classFileTransformer);
                    }
                }

                public boolean onRegistration(ResettableClassFileTransformer classFileTransformer) {
                    return true;
                }

                public void onAfterRegistration(Instrumentation instrumentation) {
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
                    return this.classFileTransformer.equals(((ForPatchWithGap)object).classFileTransformer);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.classFileTransformer.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements Handler
            {
                INSTANCE;


                @Override
                public void onBeforeRegistration(Instrumentation instrumentation) {
                }

                @Override
                public boolean onRegistration(ResettableClassFileTransformer classFileTransformer) {
                    return true;
                }

                @Override
                public void onAfterRegistration(Instrumentation instrumentation) {
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum LambdaInstrumentationStrategy {
        ENABLED{

            @Override
            protected void apply(ByteBuddy byteBuddy, Instrumentation instrumentation, ClassFileTransformer classFileTransformer) {
                if (LambdaFactory.register(classFileTransformer, new LambdaInstanceFactory(byteBuddy))) {
                    Class<?> lambdaMetaFactory;
                    try {
                        lambdaMetaFactory = Class.forName("java.lang.invoke.LambdaMetafactory");
                    }
                    catch (ClassNotFoundException ignored) {
                        return;
                    }
                    byteBuddy.with(Implementation.Context.Disabled.Factory.INSTANCE).redefine(lambdaMetaFactory).method(ElementMatchers.isPublic().and(ElementMatchers.named("metafactory"))).intercept(new Implementation.Simple(LambdaMetafactoryFactory.REGULAR)).method(ElementMatchers.isPublic().and(ElementMatchers.named("altMetafactory"))).intercept(new Implementation.Simple(LambdaMetafactoryFactory.ALTERNATIVE)).make().load(lambdaMetaFactory.getClassLoader(), ClassReloadingStrategy.of(instrumentation));
                }
            }

            @Override
            protected boolean isInstrumented(@MaybeNull Class<?> type) {
                return true;
            }
        }
        ,
        DISABLED{

            @Override
            protected void apply(ByteBuddy byteBuddy, Instrumentation instrumentation, ClassFileTransformer classFileTransformer) {
            }

            @Override
            protected boolean isInstrumented(@MaybeNull Class<?> type) {
                return type == null || !type.getName().contains("/");
            }
        };


        public static void release(ClassFileTransformer classFileTransformer, Instrumentation instrumentation) {
            if (LambdaFactory.release(classFileTransformer)) {
                try {
                    ClassReloadingStrategy.of(instrumentation).reset(Class.forName("java.lang.invoke.LambdaMetafactory"));
                }
                catch (Exception exception) {
                    throw new IllegalStateException("Could not release lambda transformer", exception);
                }
            }
        }

        public static LambdaInstrumentationStrategy of(boolean enabled) {
            return enabled ? ENABLED : DISABLED;
        }

        protected abstract void apply(ByteBuddy var1, Instrumentation var2, ClassFileTransformer var3);

        public boolean isEnabled() {
            return this == ENABLED;
        }

        protected abstract boolean isInstrumented(@MaybeNull Class<?> var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class LambdaInstanceFactory {
            private static final String LAMBDA_FACTORY = "get$Lambda";
            private static final String FIELD_PREFIX = "arg$";
            private static final String LAMBDA_TYPE_INFIX = "$$Lambda$ByteBuddy$";
            @AlwaysNull
            private static final Class<?> NOT_PREVIOUSLY_DEFINED = null;
            private static final AtomicInteger LAMBDA_NAME_COUNTER = new AtomicInteger();
            private final ByteBuddy byteBuddy;

            protected LambdaInstanceFactory(ByteBuddy byteBuddy) {
                this.byteBuddy = byteBuddy;
            }

            public byte[] make(Object targetTypeLookup, String lambdaMethodName, Object factoryMethodType, Object lambdaMethodType, Object targetMethodHandle, Object specializedLambdaMethodType, boolean serializable, List<Class<?>> markerInterfaces, List<?> additionalBridges, Collection<? extends ClassFileTransformer> classFileTransformers) {
                JavaConstant.MethodType factoryMethod = JavaConstant.MethodType.ofLoaded(factoryMethodType);
                JavaConstant.MethodType lambdaMethod = JavaConstant.MethodType.ofLoaded(lambdaMethodType);
                JavaConstant.MethodHandle targetMethod = JavaConstant.MethodHandle.ofLoaded(targetMethodHandle, targetTypeLookup);
                JavaConstant.MethodType specializedLambdaMethod = JavaConstant.MethodType.ofLoaded(specializedLambdaMethodType);
                Class<?> targetType = JavaConstant.MethodHandle.lookupType(targetTypeLookup);
                String lambdaClassName = targetType.getName() + LAMBDA_TYPE_INFIX + LAMBDA_NAME_COUNTER.incrementAndGet();
                DynamicType.Builder<Object> builder = this.byteBuddy.subclass(factoryMethod.getReturnType(), (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).modifiers(TypeManifestation.FINAL, Visibility.PUBLIC).implement(markerInterfaces).name(lambdaClassName).defineConstructor(Visibility.PUBLIC).withParameters(factoryMethod.getParameterTypes()).intercept(ConstructorImplementation.INSTANCE).method(ElementMatchers.named(lambdaMethodName).and(ElementMatchers.takesArguments(lambdaMethod.getParameterTypes())).and(ElementMatchers.returns(lambdaMethod.getReturnType()))).intercept(new LambdaMethodImplementation(TypeDescription.ForLoadedType.of(targetType), targetMethod, specializedLambdaMethod));
                int index = 0;
                for (TypeDescription capturedType : factoryMethod.getParameterTypes()) {
                    builder = builder.defineField(FIELD_PREFIX + ++index, (TypeDefinition)capturedType, Visibility.PRIVATE, FieldManifestation.FINAL);
                }
                if (!factoryMethod.getParameterTypes().isEmpty()) {
                    builder = builder.defineMethod(LAMBDA_FACTORY, (TypeDefinition)factoryMethod.getReturnType(), Visibility.PRIVATE, Ownership.STATIC).withParameters(factoryMethod.getParameterTypes()).intercept(FactoryImplementation.INSTANCE);
                }
                if (serializable) {
                    if (!markerInterfaces.contains(Serializable.class)) {
                        builder = builder.implement(new java.lang.reflect.Type[]{Serializable.class});
                    }
                    builder = builder.defineMethod("writeReplace", (java.lang.reflect.Type)((Object)Object.class), Visibility.PRIVATE).intercept(new SerializationImplementation(TypeDescription.ForLoadedType.of(targetType), factoryMethod.getReturnType(), lambdaMethodName, lambdaMethod, targetMethod, JavaConstant.MethodType.ofLoaded(specializedLambdaMethodType)));
                } else if (factoryMethod.getReturnType().isAssignableTo(Serializable.class)) {
                    builder = builder.defineMethod("readObject", Void.TYPE, Visibility.PRIVATE).withParameters(new java.lang.reflect.Type[]{ObjectInputStream.class}).throwing(new java.lang.reflect.Type[]{NotSerializableException.class}).intercept(ExceptionMethod.throwing(NotSerializableException.class, "Non-serializable lambda")).defineMethod("writeObject", Void.TYPE, Visibility.PRIVATE).withParameters(new java.lang.reflect.Type[]{ObjectOutputStream.class}).throwing(new java.lang.reflect.Type[]{NotSerializableException.class}).intercept(ExceptionMethod.throwing(NotSerializableException.class, "Non-serializable lambda"));
                }
                for (Object additionalBridgeType : additionalBridges) {
                    JavaConstant.MethodType methodType = JavaConstant.MethodType.ofLoaded(additionalBridgeType);
                    builder = builder.defineMethod(lambdaMethodName, (TypeDefinition)methodType.getReturnType(), MethodManifestation.BRIDGE, Visibility.PUBLIC).withParameters(methodType.getParameterTypes()).intercept(new BridgeMethodImplementation(lambdaMethodName, lambdaMethod));
                }
                byte[] classFile = builder.make().getBytes();
                for (ClassFileTransformer classFileTransformer : classFileTransformers) {
                    try {
                        byte[] transformedClassFile = classFileTransformer.transform(targetType.getClassLoader(), lambdaClassName.replace('.', '/'), NOT_PREVIOUSLY_DEFINED, targetType.getProtectionDomain(), classFile);
                        classFile = transformedClassFile == null ? classFile : transformedClassFile;
                    }
                    catch (Throwable throwable) {}
                }
                return classFile;
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
                return this.byteBuddy.equals(((LambdaInstanceFactory)object).byteBuddy);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.byteBuddy.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class BridgeMethodImplementation
            implements Implementation {
                private final String lambdaMethodName;
                private final JavaConstant.MethodType lambdaMethod;

                protected BridgeMethodImplementation(String lambdaMethodName, JavaConstant.MethodType lambdaMethod) {
                    this.lambdaMethodName = lambdaMethodName;
                    this.lambdaMethod = lambdaMethod;
                }

                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new Appender(implementationTarget.invokeSuper(new MethodDescription.SignatureToken(this.lambdaMethodName, this.lambdaMethod.getReturnType(), this.lambdaMethod.getParameterTypes())));
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
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
                    if (!this.lambdaMethodName.equals(((BridgeMethodImplementation)object).lambdaMethodName)) {
                        return false;
                    }
                    return this.lambdaMethod.equals(((BridgeMethodImplementation)object).lambdaMethod);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.lambdaMethodName.hashCode()) * 31 + this.lambdaMethod.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class Appender
                implements ByteCodeAppender {
                    private final Implementation.SpecialMethodInvocation bridgeTargetInvocation;

                    protected Appender(Implementation.SpecialMethodInvocation bridgeTargetInvocation) {
                        this.bridgeTargetInvocation = bridgeTargetInvocation;
                    }

                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        return new ByteCodeAppender.Compound(new ByteCodeAppender.Simple(MethodVariableAccess.allArgumentsOf(instrumentedMethod).asBridgeOf(this.bridgeTargetInvocation.getMethodDescription()).prependThisReference(), this.bridgeTargetInvocation, this.bridgeTargetInvocation.getMethodDescription().getReturnType().asErasure().isAssignableTo(instrumentedMethod.getReturnType().asErasure()) ? StackManipulation.Trivial.INSTANCE : TypeCasting.to(instrumentedMethod.getReturnType()), MethodReturn.of(instrumentedMethod.getReturnType()))).apply(methodVisitor, implementationContext, instrumentedMethod);
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
                        return this.bridgeTargetInvocation.equals(((Appender)object).bridgeTargetInvocation);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.bridgeTargetInvocation.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class SerializationImplementation
            implements Implementation {
                private final TypeDescription targetType;
                private final TypeDescription lambdaType;
                private final String lambdaMethodName;
                private final JavaConstant.MethodType lambdaMethod;
                private final JavaConstant.MethodHandle targetMethod;
                private final JavaConstant.MethodType specializedMethod;

                protected SerializationImplementation(TypeDescription targetType, TypeDescription lambdaType, String lambdaMethodName, JavaConstant.MethodType lambdaMethod, JavaConstant.MethodHandle targetMethod, JavaConstant.MethodType specializedMethod) {
                    this.targetType = targetType;
                    this.lambdaType = lambdaType;
                    this.lambdaMethodName = lambdaMethodName;
                    this.lambdaMethod = lambdaMethod;
                    this.targetMethod = targetMethod;
                    this.specializedMethod = specializedMethod;
                }

                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    TypeDescription serializedLambda;
                    try {
                        serializedLambda = TypeDescription.ForLoadedType.of(Class.forName("java.lang.invoke.SerializedLambda"));
                    }
                    catch (ClassNotFoundException exception) {
                        throw new IllegalStateException("Cannot find class for lambda serialization", exception);
                    }
                    ArrayList<StackManipulation.Compound> lambdaArguments = new ArrayList<StackManipulation.Compound>(implementationTarget.getInstrumentedType().getDeclaredFields().size());
                    for (FieldDescription.InDefinedShape fieldDescription : implementationTarget.getInstrumentedType().getDeclaredFields()) {
                        lambdaArguments.add(new StackManipulation.Compound(MethodVariableAccess.loadThis(), FieldAccess.forField(fieldDescription).read(), Assigner.DEFAULT.assign(fieldDescription.getType(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), Assigner.Typing.STATIC)));
                    }
                    return new ByteCodeAppender.Simple(new StackManipulation.Compound(TypeCreation.of(serializedLambda), Duplication.SINGLE, ClassConstant.of(this.targetType), new TextConstant(this.lambdaType.getInternalName()), new TextConstant(this.lambdaMethodName), new TextConstant(this.lambdaMethod.getDescriptor()), IntegerConstant.forValue(this.targetMethod.getHandleType().getIdentifier()), new TextConstant(this.targetMethod.getOwnerType().getInternalName()), new TextConstant(this.targetMethod.getName()), new TextConstant(this.targetMethod.getDescriptor()), new TextConstant(this.specializedMethod.getDescriptor()), ArrayFactory.forType(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class)).withValues(lambdaArguments), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)serializedLambda.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly()), MethodReturn.REFERENCE));
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
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
                    if (!this.lambdaMethodName.equals(((SerializationImplementation)object).lambdaMethodName)) {
                        return false;
                    }
                    if (!this.targetType.equals(((SerializationImplementation)object).targetType)) {
                        return false;
                    }
                    if (!this.lambdaType.equals(((SerializationImplementation)object).lambdaType)) {
                        return false;
                    }
                    if (!this.lambdaMethod.equals(((SerializationImplementation)object).lambdaMethod)) {
                        return false;
                    }
                    if (!this.targetMethod.equals(((SerializationImplementation)object).targetMethod)) {
                        return false;
                    }
                    return this.specializedMethod.equals(((SerializationImplementation)object).specializedMethod);
                }

                public int hashCode() {
                    return (((((this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.lambdaType.hashCode()) * 31 + this.lambdaMethodName.hashCode()) * 31 + this.lambdaMethod.hashCode()) * 31 + this.targetMethod.hashCode()) * 31 + this.specializedMethod.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class LambdaMethodImplementation
            implements Implementation {
                private final TypeDescription targetType;
                private final JavaConstant.MethodHandle targetMethod;
                private final JavaConstant.MethodType specializedLambdaMethod;

                protected LambdaMethodImplementation(TypeDescription targetType, JavaConstant.MethodHandle targetMethod, JavaConstant.MethodType specializedLambdaMethod) {
                    this.targetType = targetType;
                    this.targetMethod = targetMethod;
                    this.specializedLambdaMethod = specializedLambdaMethod;
                }

                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return Appender.of((MethodDescription)((MethodList)this.targetMethod.getOwnerType().getDeclaredMethods().filter(ElementMatchers.hasMethodName(this.targetMethod.getName()).and(ElementMatchers.returns(this.targetMethod.getReturnType())).and(ElementMatchers.takesArguments(this.targetMethod.getParameterTypes())))).getOnly(), this.specializedLambdaMethod, implementationTarget.getInstrumentedType().getDeclaredFields(), this.targetMethod.getHandleType(), this.targetType);
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
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
                    if (!this.targetType.equals(((LambdaMethodImplementation)object).targetType)) {
                        return false;
                    }
                    if (!this.targetMethod.equals(((LambdaMethodImplementation)object).targetMethod)) {
                        return false;
                    }
                    return this.specializedLambdaMethod.equals(((LambdaMethodImplementation)object).specializedLambdaMethod);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.targetMethod.hashCode()) * 31 + this.specializedLambdaMethod.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class Appender
                implements ByteCodeAppender {
                    private static final Dispatcher LOOKUP_DATA_DISPATCHER = Appender.dispatcher();
                    private final MethodDescription targetMethod;
                    private final JavaConstant.MethodType specializedLambdaMethod;
                    private final List<FieldDescription.InDefinedShape> declaredFields;
                    private final Dispatcher dispatcher;

                    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
                    private static Dispatcher dispatcher() {
                        try {
                            Class<?> type = Class.forName("java.lang.invoke.MethodHandles$Lookup", false, null);
                            type.getMethod("classData", type, String.class, Class.class);
                            return new Dispatcher.UsingMethodHandle(new MethodDescription.ForLoadedMethod(Class.forName("java.lang.invoke.MethodHandle", false, null).getMethod("invokeExact", Object[].class)));
                        }
                        catch (Exception ignored) {
                            return Dispatcher.UsingDirectInvocation.INSTANCE;
                        }
                    }

                    protected Appender(MethodDescription targetMethod, JavaConstant.MethodType specializedLambdaMethod, List<FieldDescription.InDefinedShape> declaredFields, Dispatcher dispatcher) {
                        this.targetMethod = targetMethod;
                        this.specializedLambdaMethod = specializedLambdaMethod;
                        this.declaredFields = declaredFields;
                        this.dispatcher = dispatcher;
                    }

                    protected static ByteCodeAppender of(MethodDescription targetMethod, JavaConstant.MethodType specializedLambdaMethod, List<FieldDescription.InDefinedShape> declaredFields, JavaConstant.MethodHandle.HandleType handleType, TypeDescription targetType) {
                        return new Appender(targetMethod, specializedLambdaMethod, declaredFields, handleType == JavaConstant.MethodHandle.HandleType.INVOKE_SPECIAL || !targetMethod.getDeclaringType().asErasure().isVisibleTo(targetType) ? LOOKUP_DATA_DISPATCHER : Dispatcher.UsingDirectInvocation.INSTANCE);
                    }

                    @Override
                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        ArrayList<StackManipulation> fieldAccess = new ArrayList<StackManipulation>(this.declaredFields.size() * 2 + 1);
                        for (FieldDescription.InDefinedShape fieldDescription : this.declaredFields) {
                            fieldAccess.add(MethodVariableAccess.loadThis());
                            fieldAccess.add(FieldAccess.forField(fieldDescription).read());
                        }
                        ArrayList<StackManipulation> parameterAccess = new ArrayList<StackManipulation>(instrumentedMethod.getParameters().size() * 2);
                        for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                            parameterAccess.add(MethodVariableAccess.load(parameterDescription));
                            parameterAccess.add(Assigner.DEFAULT.assign(parameterDescription.getType(), ((TypeDescription)this.specializedLambdaMethod.getParameterTypes().get(parameterDescription.getIndex())).asGenericType(), Assigner.Typing.DYNAMIC));
                        }
                        return new ByteCodeAppender.Size(new StackManipulation.Compound(this.targetMethod.isConstructor() ? new StackManipulation.Compound(TypeCreation.of(this.targetMethod.getDeclaringType().asErasure()), Duplication.SINGLE) : StackManipulation.Trivial.INSTANCE, this.dispatcher.initialize(), new StackManipulation.Compound(fieldAccess), new StackManipulation.Compound(parameterAccess), this.dispatcher.invoke(this.targetMethod), Assigner.DEFAULT.assign(this.targetMethod.isConstructor() ? this.targetMethod.getDeclaringType().asGenericType() : this.targetMethod.getReturnType(), this.specializedLambdaMethod.getReturnType().asGenericType(), Assigner.Typing.DYNAMIC), MethodReturn.of(this.specializedLambdaMethod.getReturnType())).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
                        if (!this.targetMethod.equals(((Appender)object).targetMethod)) {
                            return false;
                        }
                        if (!this.specializedLambdaMethod.equals(((Appender)object).specializedLambdaMethod)) {
                            return false;
                        }
                        if (!((Object)this.declaredFields).equals(((Appender)object).declaredFields)) {
                            return false;
                        }
                        return this.dispatcher.equals(((Appender)object).dispatcher);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.targetMethod.hashCode()) * 31 + this.specializedLambdaMethod.hashCode()) * 31 + ((Object)this.declaredFields).hashCode()) * 31 + this.dispatcher.hashCode();
                    }

                    protected static interface Dispatcher {
                        public StackManipulation initialize();

                        public StackManipulation invoke(MethodDescription var1);

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class UsingMethodHandle
                        extends StackManipulation.AbstractBase
                        implements Dispatcher {
                            private final MethodDescription.InDefinedShape invokeExact;

                            protected UsingMethodHandle(MethodDescription.InDefinedShape invokeExact) {
                                this.invokeExact = invokeExact;
                            }

                            public StackManipulation initialize() {
                                return this;
                            }

                            public StackManipulation invoke(MethodDescription methodDescription) {
                                return MethodInvocation.invoke(this.invokeExact);
                            }

                            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                                methodVisitor.visitLdcInsn(new ConstantDynamic("_", "Ljava/lang/invoke/MethodHandle;", new Handle(6, "java/lang/invoke/MethodHandles", "classData", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;", false), new Object[0]));
                                return new StackManipulation.Size(1, 1);
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
                                return this.invokeExact.equals(((UsingMethodHandle)object).invokeExact);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.invokeExact.hashCode();
                            }
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        public static enum UsingDirectInvocation implements Dispatcher
                        {
                            INSTANCE;


                            @Override
                            public StackManipulation initialize() {
                                return StackManipulation.Trivial.INSTANCE;
                            }

                            @Override
                            public StackManipulation invoke(MethodDescription methodDescription) {
                                return MethodInvocation.invoke(methodDescription);
                            }
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum FactoryImplementation implements Implementation
            {
                INSTANCE;


                @Override
                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new Appender(implementationTarget.getInstrumentedType());
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
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
                        return new ByteCodeAppender.Size(new StackManipulation.Compound(TypeCreation.of(this.instrumentedType), Duplication.SINGLE, MethodVariableAccess.allArgumentsOf(instrumentedMethod), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)this.instrumentedType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly()), MethodReturn.REFERENCE).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
            protected static enum ConstructorImplementation implements Implementation
            {
                INSTANCE;

                private final transient MethodDescription.InDefinedShape objectConstructor = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly();

                @Override
                public ByteCodeAppender appender(Implementation.Target implementationTarget) {
                    return new Appender(implementationTarget.getInstrumentedType().getDeclaredFields());
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class Appender
                implements ByteCodeAppender {
                    private final List<FieldDescription.InDefinedShape> declaredFields;

                    protected Appender(List<FieldDescription.InDefinedShape> declaredFields) {
                        this.declaredFields = declaredFields;
                    }

                    @Override
                    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                        ArrayList<StackManipulation> fieldAssignments = new ArrayList<StackManipulation>(this.declaredFields.size() * 3);
                        for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                            fieldAssignments.add(MethodVariableAccess.loadThis());
                            fieldAssignments.add(MethodVariableAccess.load(parameterDescription));
                            fieldAssignments.add(FieldAccess.forField(this.declaredFields.get(parameterDescription.getIndex())).write());
                        }
                        return new ByteCodeAppender.Size(new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(INSTANCE.objectConstructor), new StackManipulation.Compound(fieldAssignments), MethodReturn.VOID).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
                        return ((Object)this.declaredFields).equals(((Appender)object).declaredFields);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + ((Object)this.declaredFields).hashCode();
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static enum LambdaMetafactoryFactory implements ByteCodeAppender
        {
            REGULAR(6, 11){

                protected void onDispatch(MethodVisitor methodVisitor) {
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitVarInsn(54, 6);
                    methodVisitor.visitMethodInsn(184, "java/util/Collections", "emptyList", "()Ljava/util/List;", false);
                    methodVisitor.visitVarInsn(58, 7);
                    methodVisitor.visitMethodInsn(184, "java/util/Collections", "emptyList", "()Ljava/util/List;", false);
                    methodVisitor.visitVarInsn(58, 8);
                    methodVisitor.visitFrame(1, 3, new Object[]{Opcodes.INTEGER, "java/util/List", "java/util/List"}, 0, null);
                }
            }
            ,
            ALTERNATIVE(6, 16){

                protected void onDispatch(MethodVisitor methodVisitor) {
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitInsn(6);
                    methodVisitor.visitInsn(50);
                    methodVisitor.visitTypeInsn(192, "java/lang/Integer");
                    methodVisitor.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
                    methodVisitor.visitVarInsn(54, 4);
                    methodVisitor.visitInsn(7);
                    methodVisitor.visitVarInsn(54, 5);
                    methodVisitor.visitVarInsn(21, 4);
                    methodVisitor.visitInsn(5);
                    methodVisitor.visitInsn(126);
                    Label first = new Label();
                    methodVisitor.visitJumpInsn(153, first);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitVarInsn(21, 5);
                    methodVisitor.visitIincInsn(5, 1);
                    methodVisitor.visitInsn(50);
                    methodVisitor.visitTypeInsn(192, "java/lang/Integer");
                    methodVisitor.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
                    methodVisitor.visitVarInsn(54, 7);
                    methodVisitor.visitVarInsn(21, 7);
                    methodVisitor.visitTypeInsn(189, "java/lang/Class");
                    methodVisitor.visitVarInsn(58, 6);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitVarInsn(21, 5);
                    methodVisitor.visitVarInsn(25, 6);
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitVarInsn(21, 7);
                    methodVisitor.visitMethodInsn(184, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
                    methodVisitor.visitVarInsn(21, 5);
                    methodVisitor.visitVarInsn(21, 7);
                    methodVisitor.visitInsn(96);
                    methodVisitor.visitVarInsn(54, 5);
                    Label second = new Label();
                    methodVisitor.visitJumpInsn(167, second);
                    methodVisitor.visitLabel(first);
                    methodVisitor.visitFrame(1, 2, new Object[]{Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitTypeInsn(189, "java/lang/Class");
                    methodVisitor.visitVarInsn(58, 6);
                    methodVisitor.visitLabel(second);
                    methodVisitor.visitFrame(1, 1, new Object[]{"[Ljava/lang/Class;"}, 0, null);
                    methodVisitor.visitVarInsn(21, 4);
                    methodVisitor.visitInsn(5);
                    methodVisitor.visitInsn(126);
                    Label third = new Label();
                    methodVisitor.visitJumpInsn(153, third);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitVarInsn(21, 5);
                    methodVisitor.visitIincInsn(5, 1);
                    methodVisitor.visitInsn(50);
                    methodVisitor.visitTypeInsn(192, "java/lang/Integer");
                    methodVisitor.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
                    methodVisitor.visitVarInsn(54, 8);
                    methodVisitor.visitVarInsn(21, 8);
                    methodVisitor.visitTypeInsn(189, "java/lang/invoke/MethodType");
                    methodVisitor.visitVarInsn(58, 7);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitVarInsn(21, 5);
                    methodVisitor.visitVarInsn(25, 7);
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitVarInsn(21, 8);
                    methodVisitor.visitMethodInsn(184, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
                    Label forth = new Label();
                    methodVisitor.visitJumpInsn(167, forth);
                    methodVisitor.visitLabel(third);
                    methodVisitor.visitFrame(3, 0, null, 0, null);
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitTypeInsn(189, "java/lang/invoke/MethodType");
                    methodVisitor.visitVarInsn(58, 7);
                    methodVisitor.visitLabel(forth);
                    methodVisitor.visitFrame(1, 1, new Object[]{"[Ljava/lang/invoke/MethodType;"}, 0, null);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitInsn(50);
                    methodVisitor.visitTypeInsn(192, "java/lang/invoke/MethodType");
                    methodVisitor.visitVarInsn(58, 8);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitInsn(4);
                    methodVisitor.visitInsn(50);
                    methodVisitor.visitTypeInsn(192, "java/lang/invoke/MethodHandle");
                    methodVisitor.visitVarInsn(58, 9);
                    methodVisitor.visitVarInsn(25, 3);
                    methodVisitor.visitInsn(5);
                    methodVisitor.visitInsn(50);
                    methodVisitor.visitTypeInsn(192, "java/lang/invoke/MethodType");
                    methodVisitor.visitVarInsn(58, 10);
                    methodVisitor.visitVarInsn(21, 4);
                    methodVisitor.visitInsn(4);
                    methodVisitor.visitInsn(126);
                    Label fifth = new Label();
                    methodVisitor.visitJumpInsn(153, fifth);
                    methodVisitor.visitInsn(4);
                    Label sixth = new Label();
                    methodVisitor.visitJumpInsn(167, sixth);
                    methodVisitor.visitLabel(fifth);
                    methodVisitor.visitFrame(1, 3, new Object[]{"java/lang/invoke/MethodType", "java/lang/invoke/MethodHandle", "java/lang/invoke/MethodType"}, 0, null);
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitLabel(sixth);
                    methodVisitor.visitFrame(4, 0, null, 1, new Object[]{Opcodes.INTEGER});
                    methodVisitor.visitVarInsn(54, 11);
                    methodVisitor.visitVarInsn(25, 6);
                    methodVisitor.visitMethodInsn(184, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;", false);
                    methodVisitor.visitVarInsn(58, 12);
                    methodVisitor.visitVarInsn(25, 7);
                    methodVisitor.visitMethodInsn(184, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;", false);
                    methodVisitor.visitVarInsn(58, 13);
                    methodVisitor.visitVarInsn(25, 8);
                    methodVisitor.visitVarInsn(58, 3);
                    methodVisitor.visitVarInsn(25, 9);
                    methodVisitor.visitVarInsn(58, 4);
                    methodVisitor.visitVarInsn(25, 10);
                    methodVisitor.visitVarInsn(58, 5);
                    methodVisitor.visitVarInsn(21, 11);
                    methodVisitor.visitVarInsn(54, 6);
                    methodVisitor.visitVarInsn(25, 12);
                    methodVisitor.visitVarInsn(58, 7);
                    methodVisitor.visitVarInsn(25, 13);
                    methodVisitor.visitVarInsn(58, 8);
                    methodVisitor.visitFrame(0, 9, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/invoke/MethodType", "java/lang/invoke/MethodHandle", "java/lang/invoke/MethodType", Opcodes.INTEGER, "java/util/List", "java/util/List"}, 0, null);
                }
            };

            private static final Loader LOADER;
            private final int stackSize;
            private final int localVariableLength;

            @SuppressFBWarnings(value={"DE_MIGHT_IGNORE", "REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
            private static Loader resolve() {
                try {
                    Class<?> type = Class.forName("java.lang.invoke.MethodHandles$Lookup", false, null);
                    type.getMethod("defineHiddenClass", byte[].class, Boolean.TYPE, Class.forName("[Ljava.lang.invoke.MethodHandles$Lookup$ClassOption;", false, null));
                    type.getMethod("defineHiddenClassWithClassData", byte[].class, Object.class, Boolean.TYPE, Class.forName("[Ljava.lang.invoke.MethodHandles$Lookup$ClassOption;", false, null));
                    return Loader.UsingMethodHandleLookup.INSTANCE;
                }
                catch (Exception exception) {
                    for (Loader.UsingUnsafe loader : Loader.UsingUnsafe.values()) {
                        try {
                            Class.forName(loader.getType().replace('/', '.'), false, null).getMethod("defineAnonymousClass", Class.class, byte[].class, Object[].class);
                            return loader;
                        }
                        catch (Exception exception2) {
                        }
                    }
                    return Loader.Unavailable.INSTANCE;
                }
            }

            private LambdaMetafactoryFactory(int stackSize, int localVariableLength) {
                this.stackSize = stackSize;
                this.localVariableLength = localVariableLength;
            }

            @Override
            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                this.onDispatch(methodVisitor);
                methodVisitor.visitMethodInsn(184, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;", false);
                methodVisitor.visitLdcInsn("net.bytebuddy.agent.builder.LambdaFactory");
                methodVisitor.visitMethodInsn(182, "java/lang/ClassLoader", "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;", false);
                methodVisitor.visitLdcInsn("make");
                methodVisitor.visitIntInsn(16, 9);
                methodVisitor.visitTypeInsn(189, "java/lang/Class");
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(3);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(4);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/String;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(5);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(6);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(7);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(8);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitIntInsn(16, 6);
                methodVisitor.visitFieldInsn(178, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitIntInsn(16, 7);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/util/List;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitIntInsn(16, 8);
                methodVisitor.visitLdcInsn(Type.getType("Ljava/util/List;"));
                methodVisitor.visitInsn(83);
                methodVisitor.visitMethodInsn(182, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
                methodVisitor.visitInsn(1);
                methodVisitor.visitIntInsn(16, 9);
                methodVisitor.visitTypeInsn(189, "java/lang/Object");
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(3);
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(4);
                methodVisitor.visitVarInsn(25, 1);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(5);
                methodVisitor.visitVarInsn(25, 2);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(6);
                methodVisitor.visitVarInsn(25, 3);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(7);
                methodVisitor.visitVarInsn(25, 4);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitInsn(8);
                methodVisitor.visitVarInsn(25, 5);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitIntInsn(16, 6);
                methodVisitor.visitVarInsn(21, 6);
                methodVisitor.visitMethodInsn(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitIntInsn(16, 7);
                methodVisitor.visitVarInsn(25, 7);
                methodVisitor.visitInsn(83);
                methodVisitor.visitInsn(89);
                methodVisitor.visitIntInsn(16, 8);
                methodVisitor.visitVarInsn(25, 8);
                methodVisitor.visitInsn(83);
                methodVisitor.visitMethodInsn(182, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
                methodVisitor.visitTypeInsn(192, "[B");
                methodVisitor.visitVarInsn(58, 9);
                LOADER.apply(methodVisitor);
                methodVisitor.visitVarInsn(25, 2);
                methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodType", "parameterCount", "()I", false);
                Label first = new Label();
                methodVisitor.visitJumpInsn(154, first);
                methodVisitor.visitTypeInsn(187, "java/lang/invoke/ConstantCallSite");
                methodVisitor.visitInsn(89);
                methodVisitor.visitVarInsn(25, 2);
                methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodType", "returnType", "()Ljava/lang/Class;", false);
                methodVisitor.visitVarInsn(25, 10);
                methodVisitor.visitMethodInsn(182, "java/lang/Class", "getDeclaredConstructors", "()[Ljava/lang/reflect/Constructor;", false);
                methodVisitor.visitInsn(3);
                methodVisitor.visitInsn(50);
                methodVisitor.visitInsn(3);
                methodVisitor.visitTypeInsn(189, "java/lang/Object");
                methodVisitor.visitMethodInsn(182, "java/lang/reflect/Constructor", "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
                methodVisitor.visitMethodInsn(184, "java/lang/invoke/MethodHandles", "constant", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;", false);
                methodVisitor.visitMethodInsn(183, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
                Label second = new Label();
                methodVisitor.visitJumpInsn(167, second);
                methodVisitor.visitLabel(first);
                methodVisitor.visitFrame(0, 11, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/invoke/MethodType", "java/lang/invoke/MethodHandle", "java/lang/invoke/MethodType", Opcodes.INTEGER, "java/util/List", "java/util/List", "[B", "java/lang/Class"}, 0, new Object[0]);
                methodVisitor.visitTypeInsn(187, "java/lang/invoke/ConstantCallSite");
                methodVisitor.visitInsn(89);
                methodVisitor.visitFieldInsn(178, "java/lang/invoke/MethodHandles$Lookup", "IMPL_LOOKUP", "Ljava/lang/invoke/MethodHandles$Lookup;");
                methodVisitor.visitVarInsn(25, 10);
                methodVisitor.visitLdcInsn("get$Lambda");
                methodVisitor.visitVarInsn(25, 2);
                methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
                methodVisitor.visitMethodInsn(183, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
                methodVisitor.visitLabel(second);
                methodVisitor.visitFrame(4, 0, null, 1, new Object[]{"java/lang/invoke/CallSite"});
                methodVisitor.visitInsn(176);
                return new ByteCodeAppender.Size(Math.max(this.stackSize, LOADER.getStackSize()), Math.max(this.localVariableLength, LOADER.getLocalVariableLength()));
            }

            protected abstract void onDispatch(MethodVisitor var1);

            static {
                LOADER = LambdaMetafactoryFactory.resolve();
            }

            protected static interface Loader {
                public void apply(MethodVisitor var1);

                public int getStackSize();

                public int getLocalVariableLength();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum UsingUnsafe implements Loader
                {
                    JDK_INTERNAL_MISC_UNSAFE("jdk/internal/misc/Unsafe"),
                    SUN_MISC_UNSAFE("sun/misc/Unsafe");

                    private final String type;

                    private UsingUnsafe(String type) {
                        this.type = type;
                    }

                    protected String getType() {
                        return this.type;
                    }

                    @Override
                    public void apply(MethodVisitor methodVisitor) {
                        methodVisitor.visitMethodInsn(184, this.type, "getUnsafe", "()L" + this.type + ";", false);
                        methodVisitor.visitVarInsn(58, 11);
                        methodVisitor.visitVarInsn(25, 11);
                        methodVisitor.visitVarInsn(25, 0);
                        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);
                        methodVisitor.visitVarInsn(25, 9);
                        methodVisitor.visitInsn(1);
                        methodVisitor.visitMethodInsn(182, this.type, "defineAnonymousClass", "(Ljava/lang/Class;[B[Ljava/lang/Object;)Ljava/lang/Class;", false);
                        methodVisitor.visitVarInsn(58, 10);
                        methodVisitor.visitVarInsn(25, 11);
                        methodVisitor.visitVarInsn(25, 10);
                        methodVisitor.visitMethodInsn(182, this.type, "ensureClassInitialized", "(Ljava/lang/Class;)V", false);
                    }

                    @Override
                    public int getStackSize() {
                        return 4;
                    }

                    @Override
                    public int getLocalVariableLength() {
                        return 13;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum UsingMethodHandleLookup implements Loader
                {
                    INSTANCE;


                    @Override
                    public void apply(MethodVisitor methodVisitor) {
                        methodVisitor.visitVarInsn(25, 0);
                        methodVisitor.visitVarInsn(25, 4);
                        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "revealDirect", "(Ljava/lang/invoke/MethodHandle;)Ljava/lang/invoke/MethodHandleInfo;", false);
                        methodVisitor.visitVarInsn(58, 10);
                        methodVisitor.visitVarInsn(25, 10);
                        methodVisitor.visitMethodInsn(185, "java/lang/invoke/MethodHandleInfo", "getModifiers", "()I", true);
                        methodVisitor.visitMethodInsn(184, "java/lang/reflect/Modifier", "isProtected", "(I)Z", false);
                        Label first = new Label();
                        methodVisitor.visitJumpInsn(153, first);
                        methodVisitor.visitVarInsn(25, 0);
                        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);
                        methodVisitor.visitVarInsn(25, 10);
                        methodVisitor.visitMethodInsn(185, "java/lang/invoke/MethodHandleInfo", "getDeclaringClass", "()Ljava/lang/Class;", true);
                        methodVisitor.visitMethodInsn(184, "sun/invoke/util/VerifyAccess", "isSamePackage", "(Ljava/lang/Class;Ljava/lang/Class;)Z", false);
                        Label second = new Label();
                        methodVisitor.visitJumpInsn(153, second);
                        methodVisitor.visitLabel(first);
                        methodVisitor.visitFrame(0, 11, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/invoke/MethodType", "java/lang/invoke/MethodHandle", "java/lang/invoke/MethodType", Opcodes.INTEGER, "java/util/List", "java/util/List", "[B", "java/lang/invoke/MethodHandleInfo"}, 0, new Object[0]);
                        methodVisitor.visitVarInsn(25, 10);
                        methodVisitor.visitMethodInsn(185, "java/lang/invoke/MethodHandleInfo", "getReferenceKind", "()I", true);
                        methodVisitor.visitIntInsn(16, 7);
                        Label third = new Label();
                        methodVisitor.visitJumpInsn(160, third);
                        methodVisitor.visitLabel(second);
                        methodVisitor.visitFrame(3, 0, null, 0, null);
                        methodVisitor.visitInsn(4);
                        Label forth = new Label();
                        methodVisitor.visitJumpInsn(167, forth);
                        methodVisitor.visitLabel(third);
                        methodVisitor.visitFrame(3, 0, null, 0, null);
                        methodVisitor.visitInsn(3);
                        methodVisitor.visitLabel(forth);
                        methodVisitor.visitFrame(4, 0, null, 1, new Object[]{Opcodes.INTEGER});
                        methodVisitor.visitVarInsn(54, 11);
                        methodVisitor.visitVarInsn(21, 11);
                        Label fifth = new Label();
                        methodVisitor.visitJumpInsn(153, fifth);
                        methodVisitor.visitVarInsn(25, 0);
                        methodVisitor.visitVarInsn(25, 9);
                        methodVisitor.visitVarInsn(25, 10);
                        methodVisitor.visitInsn(4);
                        methodVisitor.visitInsn(5);
                        methodVisitor.visitTypeInsn(189, "java/lang/invoke/MethodHandles$Lookup$ClassOption");
                        methodVisitor.visitInsn(89);
                        methodVisitor.visitInsn(3);
                        methodVisitor.visitFieldInsn(178, "java/lang/invoke/MethodHandles$Lookup$ClassOption", "NESTMATE", "Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;");
                        methodVisitor.visitInsn(83);
                        methodVisitor.visitInsn(89);
                        methodVisitor.visitInsn(4);
                        methodVisitor.visitFieldInsn(178, "java/lang/invoke/MethodHandles$Lookup$ClassOption", "STRONG", "Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;");
                        methodVisitor.visitInsn(83);
                        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "defineHiddenClassWithClassData", "([BLjava/lang/Object;Z[Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                        methodVisitor.visitVarInsn(58, 12);
                        Label sixth = new Label();
                        methodVisitor.visitLabel(sixth);
                        Label seventh = new Label();
                        methodVisitor.visitJumpInsn(167, seventh);
                        methodVisitor.visitLabel(fifth);
                        methodVisitor.visitFrame(1, 1, new Object[]{Opcodes.INTEGER}, 0, null);
                        methodVisitor.visitVarInsn(25, 0);
                        methodVisitor.visitVarInsn(25, 9);
                        methodVisitor.visitInsn(4);
                        methodVisitor.visitInsn(5);
                        methodVisitor.visitTypeInsn(189, "java/lang/invoke/MethodHandles$Lookup$ClassOption");
                        methodVisitor.visitInsn(89);
                        methodVisitor.visitInsn(3);
                        methodVisitor.visitFieldInsn(178, "java/lang/invoke/MethodHandles$Lookup$ClassOption", "NESTMATE", "Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;");
                        methodVisitor.visitInsn(83);
                        methodVisitor.visitInsn(89);
                        methodVisitor.visitInsn(4);
                        methodVisitor.visitFieldInsn(178, "java/lang/invoke/MethodHandles$Lookup$ClassOption", "STRONG", "Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;");
                        methodVisitor.visitInsn(83);
                        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "defineHiddenClass", "([BZ[Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                        methodVisitor.visitVarInsn(58, 12);
                        methodVisitor.visitLabel(seventh);
                        methodVisitor.visitFrame(1, 1, new Object[]{"java/lang/invoke/MethodHandles$Lookup"}, 0, null);
                        methodVisitor.visitVarInsn(25, 12);
                        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);
                        methodVisitor.visitVarInsn(58, 10);
                        methodVisitor.visitFrame(0, 10, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/invoke/MethodType", "java/lang/invoke/MethodHandle", "java/lang/invoke/MethodType", Opcodes.INTEGER, "java/util/List", "java/util/List", "java/lang/Class"}, 0, null);
                    }

                    @Override
                    public int getStackSize() {
                        return 8;
                    }

                    @Override
                    public int getLocalVariableLength() {
                        return 15;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Unavailable implements Loader
                {
                    INSTANCE;


                    @Override
                    public void apply(MethodVisitor methodVisitor) {
                        throw new IllegalStateException("No lambda expression loading strategy available on current VM");
                    }

                    @Override
                    public int getStackSize() {
                        throw new IllegalStateException("No lambda expression loading strategy available on current VM");
                    }

                    @Override
                    public int getLocalVariableLength() {
                        throw new IllegalStateException("No lambda expression loading strategy available on current VM");
                    }
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RedefinitionStrategy {
        DISABLED(false, false){

            public void apply(Instrumentation instrumentation, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, DiscoveryStrategy discoveryStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, Listener redefinitionListener, RawMatcher matcher, BatchAllocator redefinitionBatchAllocator, CircularityLock circularityLock) {
            }

            protected void check(Instrumentation instrumentation) {
                throw new IllegalStateException("Cannot apply redefinition on disabled strategy");
            }

            protected Collector make(PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, RawMatcher matcher, CircularityLock circularityLock) {
                throw new IllegalStateException("A disabled redefinition strategy cannot create a collector");
            }
        }
        ,
        REDEFINITION(true, false){

            protected void check(Instrumentation instrumentation) {
                if (!instrumentation.isRedefineClassesSupported()) {
                    throw new IllegalStateException("Cannot apply redefinition on " + instrumentation);
                }
            }

            protected Collector make(PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, RawMatcher matcher, CircularityLock circularityLock) {
                return new Collector.ForRedefinition(matcher, poolStrategy, locationStrategy, descriptionStrategy, listener, fallbackStrategy, circularityLock);
            }
        }
        ,
        RETRANSFORMATION(true, true){

            protected void check(Instrumentation instrumentation) {
                if (!DISPATCHER.isRetransformClassesSupported(instrumentation)) {
                    throw new IllegalStateException("Cannot apply retransformation on " + instrumentation);
                }
            }

            protected Collector make(PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, RawMatcher matcher, CircularityLock circularityLock) {
                return new Collector.ForRetransformation(matcher, poolStrategy, locationStrategy, descriptionStrategy, listener, fallbackStrategy, circularityLock);
            }
        };

        protected static final Dispatcher DISPATCHER;
        private final boolean enabled;
        private final boolean retransforming;

        private RedefinitionStrategy(boolean enabled, boolean retransforming) {
            this.enabled = enabled;
            this.retransforming = retransforming;
        }

        protected boolean isRetransforming() {
            return this.retransforming;
        }

        protected abstract void check(Instrumentation var1);

        protected boolean isEnabled() {
            return this.enabled;
        }

        protected abstract Collector make(PoolStrategy var1, LocationStrategy var2, DescriptionStrategy var3, FallbackStrategy var4, net.bytebuddy.agent.builder.AgentBuilder$Listener var5, RawMatcher var6, CircularityLock var7);

        protected void apply(Instrumentation instrumentation, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, DiscoveryStrategy redefinitionDiscoveryStrategy, LambdaInstrumentationStrategy lambdaInstrumentationStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, Listener redefinitionListener, RawMatcher matcher, BatchAllocator redefinitionBatchAllocator, CircularityLock circularityLock) {
            this.check(instrumentation);
            int batch = 0;
            for (Iterable<Class<?>> types : redefinitionDiscoveryStrategy.resolve(instrumentation)) {
                Collector collector = this.make(poolStrategy, locationStrategy, descriptionStrategy, fallbackStrategy, listener, matcher, circularityLock);
                for (Class<?> type : types) {
                    if (type == null || type.isArray() || type.isPrimitive() || !lambdaInstrumentationStrategy.isInstrumented(type)) continue;
                    collector.consider(type, DISPATCHER.isModifiableClass(instrumentation, type) || ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isAtMost(ClassFileVersion.JAVA_V5));
                }
                batch = collector.apply(instrumentation, redefinitionBatchAllocator, redefinitionListener, batch);
            }
        }

        static {
            DISPATCHER = (Dispatcher)Default.doPrivileged(JavaDispatcher.of(Dispatcher.class));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static abstract class Collector {
            private final RawMatcher matcher;
            private final PoolStrategy poolStrategy;
            protected final LocationStrategy locationStrategy;
            private final DescriptionStrategy descriptionStrategy;
            protected final net.bytebuddy.agent.builder.AgentBuilder$Listener listener;
            private final FallbackStrategy fallbackStrategy;
            protected final CircularityLock circularityLock;
            protected final List<Class<?>> types;

            protected Collector(RawMatcher matcher, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, FallbackStrategy fallbackStrategy, CircularityLock circularityLock) {
                this.matcher = matcher;
                this.poolStrategy = poolStrategy;
                this.locationStrategy = locationStrategy;
                this.descriptionStrategy = descriptionStrategy;
                this.listener = listener;
                this.fallbackStrategy = fallbackStrategy;
                this.circularityLock = circularityLock;
                this.types = new ArrayList();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            protected void consider(Class<?> type, boolean modifiable) {
                block12: {
                    JavaModule module = JavaModule.ofType(type);
                    try {
                        TypePool typePool = this.poolStrategy.typePool(this.locationStrategy.classFileLocator(type.getClassLoader(), module), type.getClassLoader());
                        try {
                            this.doConsider(this.matcher, this.listener, this.descriptionStrategy.apply(TypeDescription.ForLoadedType.getName(type), type, typePool, this.circularityLock, type.getClassLoader(), module), type, type, module, modifiable);
                        }
                        catch (Throwable throwable) {
                            if (this.descriptionStrategy.isLoadedFirst() && this.fallbackStrategy.isFallback(type, throwable)) {
                                this.doConsider(this.matcher, this.listener, typePool.describe(TypeDescription.ForLoadedType.getName(type)).resolve(), type, null, module, true);
                                break block12;
                            }
                            throw throwable;
                        }
                    }
                    catch (Throwable throwable) {
                        try {
                            Object v1;
                            try {
                                this.listener.onDiscovery(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true);
                                Object var7_8 = null;
                            }
                            catch (Throwable throwable2) {
                                Object v0;
                                Object var7_9 = null;
                                try {
                                    this.listener.onError(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true, throwable);
                                    v0 = null;
                                }
                                catch (Throwable throwable3) {
                                    v0 = null;
                                }
                                Object var9_13 = v0;
                                this.listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true);
                                throw throwable2;
                            }
                            try {
                                this.listener.onError(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true, throwable);
                                v1 = null;
                            }
                            catch (Throwable throwable4) {
                                v1 = null;
                            }
                            Object var9_12 = v1;
                            this.listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true);
                            {
                            }
                        }
                        catch (Throwable throwable5) {
                            // empty catch block
                        }
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            private void doConsider(RawMatcher matcher, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, TypeDescription typeDescription, Class<?> type, @MaybeNull Class<?> classBeingRedefined, @MaybeNull JavaModule module, boolean modifiable) {
                if (modifiable && matcher.matches(typeDescription, type.getClassLoader(), module, classBeingRedefined, type.getProtectionDomain())) {
                    this.types.add(type);
                    return;
                }
                try {
                    try {
                        try {
                            listener.onDiscovery(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, classBeingRedefined != null);
                            listener.onIgnored(typeDescription, type.getClassLoader(), module, classBeingRedefined != null);
                        }
                        catch (Throwable throwable) {
                            listener.onError(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, classBeingRedefined != null, throwable);
                            Object var10_9 = null;
                            listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, classBeingRedefined != null);
                            return;
                        }
                        Object var10_8 = null;
                    }
                    catch (Throwable throwable) {
                        Object var10_10 = null;
                        listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, classBeingRedefined != null);
                        throw throwable;
                    }
                    listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, classBeingRedefined != null);
                    return;
                }
                catch (Throwable throwable) {
                    return;
                }
            }

            protected int apply(Instrumentation instrumentation, BatchAllocator redefinitionBatchAllocator, Listener redefinitionListener, int batch) {
                HashMap failures = new HashMap();
                PrependableIterator prependableIterator = new PrependableIterator(redefinitionBatchAllocator.batch(this.types));
                while (prependableIterator.hasNext()) {
                    Object types = prependableIterator.next();
                    redefinitionListener.onBatch(batch, (List<Class<?>>)types, this.types);
                    try {
                        this.doApply(instrumentation, (List<Class<?>>)types);
                    }
                    catch (Throwable throwable) {
                        prependableIterator.prepend(redefinitionListener.onError(batch, (List<Class<?>>)types, throwable, this.types));
                        failures.put((List<Class<?>>)types, throwable);
                    }
                    ++batch;
                }
                redefinitionListener.onComplete(batch, this.types, failures);
                return batch;
            }

            protected abstract void doApply(Instrumentation var1, List<Class<?>> var2) throws UnmodifiableClassException, ClassNotFoundException;

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForRetransformation
            extends Collector {
                protected ForRetransformation(RawMatcher matcher, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, FallbackStrategy fallbackStrategy, CircularityLock circularityLock) {
                    super(matcher, poolStrategy, locationStrategy, descriptionStrategy, listener, fallbackStrategy, circularityLock);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                protected void doApply(Instrumentation instrumentation, List<Class<?>> types) throws UnmodifiableClassException {
                    if (!types.isEmpty()) {
                        this.circularityLock.release();
                        try {
                            DISPATCHER.retransformClasses(instrumentation, types.toArray(new Class[0]));
                            Object var4_3 = null;
                            this.circularityLock.acquire();
                        }
                        catch (Throwable throwable) {
                            Object var4_4 = null;
                            this.circularityLock.acquire();
                            throw throwable;
                        }
                        {
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForRedefinition
            extends Collector {
                protected ForRedefinition(RawMatcher matcher, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, FallbackStrategy fallbackStrategy, CircularityLock circularityLock) {
                    super(matcher, poolStrategy, locationStrategy, descriptionStrategy, listener, fallbackStrategy, circularityLock);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                protected void doApply(Instrumentation instrumentation, List<Class<?>> types) throws UnmodifiableClassException, ClassNotFoundException {
                    ArrayList<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>(types.size());
                    for (Class<?> type : types) {
                        try {
                            try {
                                classDefinitions.add(new ClassDefinition(type, this.locationStrategy.classFileLocator(type.getClassLoader(), JavaModule.ofType(type)).locate(TypeDescription.ForLoadedType.getName(type)).resolve()));
                            }
                            catch (Throwable throwable) {
                                Object v1;
                                Object var11_12;
                                Throwable throwable22;
                                Object var9_10;
                                JavaModule module = JavaModule.ofType(type);
                                try {
                                    this.listener.onDiscovery(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true);
                                    var9_10 = null;
                                }
                                catch (Throwable throwable3) {
                                    Object v0;
                                    var9_10 = null;
                                    try {
                                        this.listener.onError(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true, throwable);
                                        v0 = null;
                                    }
                                    catch (Throwable throwable22) {
                                        v0 = null;
                                    }
                                    var11_12 = v0;
                                    this.listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true);
                                    throw throwable3;
                                }
                                try {
                                    this.listener.onError(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true, throwable);
                                    v1 = null;
                                }
                                catch (Throwable throwable22) {
                                    v1 = null;
                                }
                                var11_12 = v1;
                                this.listener.onComplete(TypeDescription.ForLoadedType.getName(type), type.getClassLoader(), module, true);
                                {
                                }
                            }
                        }
                        catch (Throwable throwable) {
                        }
                    }
                    if (!classDefinitions.isEmpty()) {
                        this.circularityLock.release();
                        try {
                            instrumentation.redefineClasses(classDefinitions.toArray(new ClassDefinition[0]));
                            Object var13_14 = null;
                            this.circularityLock.acquire();
                        }
                        catch (Throwable throwable) {
                            Object var13_15 = null;
                            this.circularityLock.acquire();
                            throw throwable;
                        }
                        {
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class PrependableIterator
            implements Iterator<List<Class<?>>> {
                private Iterator<? extends List<Class<?>>> current;
                private final List<Iterator<? extends List<Class<?>>>> backlog;

                protected PrependableIterator(Iterable<? extends List<Class<?>>> origin) {
                    this.current = origin.iterator();
                    this.backlog = new ArrayList();
                }

                public void prepend(Iterable<? extends List<Class<?>>> iterable) {
                    Iterator<List<Class<?>>> iterator = iterable.iterator();
                    if (iterator.hasNext()) {
                        if (this.current.hasNext()) {
                            this.backlog.add(this.current);
                        }
                        this.current = iterator;
                    }
                }

                @Override
                public boolean hasNext() {
                    return this.current.hasNext();
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public List<Class<?>> next() {
                    List<Class<?>> list;
                    try {
                        list = this.current.next();
                        Object var3_2 = null;
                    }
                    catch (Throwable throwable) {
                        Object var3_3 = null;
                        while (!this.current.hasNext() && !this.backlog.isEmpty()) {
                            this.current = this.backlog.remove(this.backlog.size() - 1);
                        }
                        throw throwable;
                    }
                    while (!this.current.hasNext() && !this.backlog.isEmpty()) {
                        this.current = this.backlog.remove(this.backlog.size() - 1);
                    }
                    return list;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.instrument.Instrumentation")
        protected static interface Dispatcher {
            @JavaDispatcher.Proxied(value="isModifiableClass")
            public boolean isModifiableClass(Instrumentation var1, Class<?> var2);

            @JavaDispatcher.Defaults
            @JavaDispatcher.Proxied(value="isRetransformClassesSupported")
            public boolean isRetransformClassesSupported(Instrumentation var1);

            @JavaDispatcher.Proxied(value="retransformClasses")
            public void retransformClasses(Instrumentation var1, Class<?>[] var2) throws UnmodifiableClassException;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface ResubmissionEnforcer {
            public boolean isEnforced(String var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, @MaybeNull Class<?> var4);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Disabled implements ResubmissionEnforcer
            {
                INSTANCE;


                @Override
                public boolean isEnforced(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined) {
                    return false;
                }
            }
        }

        protected static interface ResubmissionStrategy {
            public Installation apply(Instrumentation var1, PoolStrategy var2, LocationStrategy var3, DescriptionStrategy var4, FallbackStrategy var5, net.bytebuddy.agent.builder.AgentBuilder$Listener var6, InstallationListener var7, CircularityLock var8, RawMatcher var9, RedefinitionStrategy var10, BatchAllocator var11, Listener var12);

            @HashCodeAndEqualsPlugin.Enhance
            public static class Installation {
                private final net.bytebuddy.agent.builder.AgentBuilder$Listener listener;
                private final InstallationListener installationListener;
                private final ResubmissionEnforcer resubmissionEnforcer;

                protected Installation(net.bytebuddy.agent.builder.AgentBuilder$Listener listener, InstallationListener installationListener, ResubmissionEnforcer resubmissionEnforcer) {
                    this.listener = listener;
                    this.installationListener = installationListener;
                    this.resubmissionEnforcer = resubmissionEnforcer;
                }

                protected net.bytebuddy.agent.builder.AgentBuilder$Listener getListener() {
                    return this.listener;
                }

                protected InstallationListener getInstallationListener() {
                    return this.installationListener;
                }

                protected ResubmissionEnforcer getResubmissionEnforcer() {
                    return this.resubmissionEnforcer;
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
                    if (!this.listener.equals(((Installation)object).listener)) {
                        return false;
                    }
                    if (!this.installationListener.equals(((Installation)object).installationListener)) {
                        return false;
                    }
                    return this.resubmissionEnforcer.equals(((Installation)object).resubmissionEnforcer);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.listener.hashCode()) * 31 + this.installationListener.hashCode()) * 31 + this.resubmissionEnforcer.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class Enabled
            implements ResubmissionStrategy {
                private final ResubmissionScheduler resubmissionScheduler;
                private final RedefinitionListenable.ResubmissionOnErrorMatcher resubmissionOnErrorMatcher;
                private final RedefinitionListenable.ResubmissionImmediateMatcher resubmissionImmediateMatcher;

                protected Enabled(ResubmissionScheduler resubmissionScheduler, RedefinitionListenable.ResubmissionOnErrorMatcher resubmissionOnErrorMatcher, RedefinitionListenable.ResubmissionImmediateMatcher resubmissionImmediateMatcher) {
                    this.resubmissionScheduler = resubmissionScheduler;
                    this.resubmissionOnErrorMatcher = resubmissionOnErrorMatcher;
                    this.resubmissionImmediateMatcher = resubmissionImmediateMatcher;
                }

                public Installation apply(Instrumentation instrumentation, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, InstallationListener installationListener, CircularityLock circularityLock, RawMatcher matcher, RedefinitionStrategy redefinitionStrategy, BatchAllocator redefinitionBatchAllocator, Listener redefinitionBatchListener) {
                    if (this.resubmissionScheduler.isAlive()) {
                        ConcurrentHashMap<StorageKey, Set<String>> types = new ConcurrentHashMap<StorageKey, Set<String>>();
                        Resubmitter resubmitter = new Resubmitter(this.resubmissionOnErrorMatcher, this.resubmissionImmediateMatcher, types);
                        return new Installation(new net.bytebuddy.agent.builder.AgentBuilder$Listener$Compound(resubmitter, listener), new InstallationListener.Compound(new ResubmissionInstallationListener(instrumentation, this.resubmissionScheduler, poolStrategy, locationStrategy, descriptionStrategy, fallbackStrategy, listener, circularityLock, matcher, redefinitionStrategy, redefinitionBatchAllocator, redefinitionBatchListener, types), installationListener), resubmitter);
                    }
                    throw new IllegalStateException("Resubmission scheduler " + this.resubmissionScheduler + " is not alive");
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
                    if (!this.resubmissionScheduler.equals(((Enabled)object).resubmissionScheduler)) {
                        return false;
                    }
                    if (!this.resubmissionOnErrorMatcher.equals(((Enabled)object).resubmissionOnErrorMatcher)) {
                        return false;
                    }
                    return this.resubmissionImmediateMatcher.equals(((Enabled)object).resubmissionImmediateMatcher);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.resubmissionScheduler.hashCode()) * 31 + this.resubmissionOnErrorMatcher.hashCode()) * 31 + this.resubmissionImmediateMatcher.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class StorageKey
                extends WeakReference<ClassLoader> {
                    private final int hashCode;

                    protected StorageKey(@MaybeNull ClassLoader classLoader) {
                        super(classLoader);
                        this.hashCode = System.identityHashCode(classLoader);
                    }

                    protected boolean isBootstrapLoader() {
                        return this.hashCode == 0;
                    }

                    public int hashCode() {
                        return this.hashCode;
                    }

                    @SuppressFBWarnings(value={"EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS"}, justification="Cross-comparison is intended.")
                    public boolean equals(@MaybeNull Object other) {
                        if (this == other) {
                            return true;
                        }
                        if (other instanceof LookupKey) {
                            LookupKey lookupKey = (LookupKey)other;
                            return this.hashCode == lookupKey.hashCode && this.get() == lookupKey.classLoader;
                        }
                        if (other instanceof StorageKey) {
                            StorageKey storageKey = (StorageKey)other;
                            return this.hashCode == storageKey.hashCode && this.get() == storageKey.get();
                        }
                        return false;
                    }
                }

                protected static class LookupKey {
                    @MaybeNull
                    private final ClassLoader classLoader;
                    private final int hashCode;

                    protected LookupKey(@MaybeNull ClassLoader classLoader) {
                        this.classLoader = classLoader;
                        this.hashCode = System.identityHashCode(classLoader);
                    }

                    public int hashCode() {
                        return this.hashCode;
                    }

                    @SuppressFBWarnings(value={"EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS"}, justification="Cross-comparison is intended.")
                    public boolean equals(@MaybeNull Object other) {
                        if (this == other) {
                            return true;
                        }
                        if (other instanceof LookupKey) {
                            return this.classLoader == ((LookupKey)other).classLoader;
                        }
                        if (other instanceof StorageKey) {
                            StorageKey storageKey = (StorageKey)other;
                            return this.hashCode == storageKey.hashCode && this.classLoader == storageKey.get();
                        }
                        return false;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class ResubmissionInstallationListener
                extends InstallationListener.Adapter
                implements Runnable {
                    private final Instrumentation instrumentation;
                    private final ResubmissionScheduler resubmissionScheduler;
                    private final LocationStrategy locationStrategy;
                    private final PoolStrategy poolStrategy;
                    private final DescriptionStrategy descriptionStrategy;
                    private final FallbackStrategy fallbackStrategy;
                    private final net.bytebuddy.agent.builder.AgentBuilder$Listener listener;
                    private final CircularityLock circularityLock;
                    private final RawMatcher matcher;
                    private final RedefinitionStrategy redefinitionStrategy;
                    private final BatchAllocator redefinitionBatchAllocator;
                    private final Listener redefinitionBatchListener;
                    private final ConcurrentMap<StorageKey, Set<String>> types;
                    @MaybeNull
                    private volatile ResubmissionScheduler.Cancelable cancelable;

                    protected ResubmissionInstallationListener(Instrumentation instrumentation, ResubmissionScheduler resubmissionScheduler, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, CircularityLock circularityLock, RawMatcher matcher, RedefinitionStrategy redefinitionStrategy, BatchAllocator redefinitionBatchAllocator, Listener redefinitionBatchListener, ConcurrentMap<StorageKey, Set<String>> types) {
                        this.instrumentation = instrumentation;
                        this.resubmissionScheduler = resubmissionScheduler;
                        this.poolStrategy = poolStrategy;
                        this.locationStrategy = locationStrategy;
                        this.descriptionStrategy = descriptionStrategy;
                        this.fallbackStrategy = fallbackStrategy;
                        this.listener = listener;
                        this.circularityLock = circularityLock;
                        this.matcher = matcher;
                        this.redefinitionStrategy = redefinitionStrategy;
                        this.redefinitionBatchAllocator = redefinitionBatchAllocator;
                        this.redefinitionBatchListener = redefinitionBatchListener;
                        this.types = types;
                    }

                    @Override
                    public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                        this.cancelable = this.resubmissionScheduler.schedule(this);
                    }

                    @Override
                    public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                        ResubmissionScheduler.Cancelable cancelable = this.cancelable;
                        if (cancelable != null) {
                            cancelable.cancel();
                        }
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     * Enabled aggressive block sorting
                     * Enabled unnecessary exception pruning
                     * Enabled aggressive exception aggregation
                     */
                    @Override
                    public void run() {
                        boolean release;
                        block13: {
                            block12: {
                                release = this.circularityLock.acquire();
                                try {
                                    Collector collector = this.redefinitionStrategy.make(this.poolStrategy, this.locationStrategy, this.descriptionStrategy, this.fallbackStrategy, this.listener, this.matcher, this.circularityLock);
                                    Iterator entries = this.types.entrySet().iterator();
                                    while (entries.hasNext()) {
                                        if (Thread.interrupted()) {
                                            Object var11_10 = null;
                                            if (!release) return;
                                            this.circularityLock.release();
                                            return;
                                        }
                                        Map.Entry entry = entries.next();
                                        ClassLoader classLoader = (ClassLoader)((StorageKey)entry.getKey()).get();
                                        if (classLoader != null || ((StorageKey)entry.getKey()).isBootstrapLoader()) {
                                            Iterator iterator = ((Set)entry.getValue()).iterator();
                                            if (!iterator.hasNext()) continue;
                                            if (Thread.interrupted()) {
                                                break block12;
                                            } else {
                                                try {
                                                    Class<?> type = Class.forName((String)iterator.next(), false, classLoader);
                                                    collector.consider(type, !type.isArray() && !type.isPrimitive() && (DISPATCHER.isModifiableClass(this.instrumentation, type) || ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isAtMost(ClassFileVersion.JAVA_V5)));
                                                }
                                                catch (Throwable throwable) {
                                                    Object var9_9 = null;
                                                    iterator.remove();
                                                    throw throwable;
                                                }
                                            }
                                        }
                                        entries.remove();
                                    }
                                    collector.apply(this.instrumentation, this.redefinitionBatchAllocator, this.redefinitionBatchListener, 0);
                                    break block13;
                                }
                                catch (Throwable throwable) {
                                    Object var11_13 = null;
                                    if (!release) throw throwable;
                                    this.circularityLock.release();
                                    throw throwable;
                                }
                            }
                            Object var11_11 = null;
                            if (!release) return;
                            this.circularityLock.release();
                            return;
                        }
                        Object var11_12 = null;
                        if (!release) return;
                        this.circularityLock.release();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class Resubmitter
                extends net.bytebuddy.agent.builder.AgentBuilder$Listener$Adapter
                implements ResubmissionEnforcer {
                    private final RedefinitionListenable.ResubmissionOnErrorMatcher resubmissionOnErrorMatcher;
                    private final RedefinitionListenable.ResubmissionImmediateMatcher resubmissionImmediateMatcher;
                    private final ConcurrentMap<StorageKey, Set<String>> types;

                    protected Resubmitter(RedefinitionListenable.ResubmissionOnErrorMatcher resubmissionOnErrorMatcher, RedefinitionListenable.ResubmissionImmediateMatcher resubmissionImmediateMatcher, ConcurrentMap<StorageKey, Set<String>> types) {
                        this.resubmissionOnErrorMatcher = resubmissionOnErrorMatcher;
                        this.resubmissionImmediateMatcher = resubmissionImmediateMatcher;
                        this.types = types;
                    }

                    @Override
                    @SuppressFBWarnings(value={"GC_UNRELATED_TYPES"}, justification="Cross-comparison is intended.")
                    public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
                        if (!loaded && this.resubmissionOnErrorMatcher.matches(throwable, typeName, classLoader, module)) {
                            Set previous;
                            Set types = (ConcurrentHashSet)this.types.get(new LookupKey(classLoader));
                            if (types == null && (previous = (Set)this.types.putIfAbsent(new StorageKey(classLoader), types = new ConcurrentHashSet())) != null) {
                                types = previous;
                            }
                            types.add(typeName);
                        }
                    }

                    @Override
                    @SuppressFBWarnings(value={"GC_UNRELATED_TYPES"}, justification="Cross-comparison is intended.")
                    public boolean isEnforced(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined) {
                        if (classBeingRedefined == null && this.resubmissionImmediateMatcher.matches(typeName, classLoader, module)) {
                            Set previous;
                            Set types = (ConcurrentHashSet)this.types.get(new LookupKey(classLoader));
                            if (types == null && (previous = (Set)this.types.putIfAbsent(new StorageKey(classLoader), types = new ConcurrentHashSet())) != null) {
                                types = previous;
                            }
                            types.add(typeName);
                            return true;
                        }
                        return false;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class ConcurrentHashSet<T>
                    extends AbstractSet<T> {
                        private final ConcurrentMap<T, Boolean> delegate = new ConcurrentHashMap<T, Boolean>();

                        protected ConcurrentHashSet() {
                        }

                        @Override
                        public boolean add(T value) {
                            return this.delegate.put(value, Boolean.TRUE) == null;
                        }

                        @Override
                        public boolean remove(Object value) {
                            return this.delegate.remove(value) != null;
                        }

                        @Override
                        public Iterator<T> iterator() {
                            return this.delegate.keySet().iterator();
                        }

                        @Override
                        public int size() {
                            return this.delegate.size();
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Disabled implements ResubmissionStrategy
            {
                INSTANCE;


                @Override
                public Installation apply(Instrumentation instrumentation, PoolStrategy poolStrategy, LocationStrategy locationStrategy, DescriptionStrategy descriptionStrategy, FallbackStrategy fallbackStrategy, net.bytebuddy.agent.builder.AgentBuilder$Listener listener, InstallationListener installationListener, CircularityLock circularityLock, RawMatcher matcher, RedefinitionStrategy redefinitionStrategy, BatchAllocator redefinitionBatchAllocator, Listener redefinitionBatchListener) {
                    return new Installation(listener, installationListener, ResubmissionEnforcer.Disabled.INSTANCE);
                }
            }
        }

        public static interface ResubmissionScheduler {
            public boolean isAlive();

            public Cancelable schedule(Runnable var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class WithFixedDelay
            implements ResubmissionScheduler {
                private final ScheduledExecutorService scheduledExecutorService;
                private final long time;
                private final TimeUnit timeUnit;

                public WithFixedDelay(ScheduledExecutorService scheduledExecutorService, long time, TimeUnit timeUnit) {
                    this.scheduledExecutorService = scheduledExecutorService;
                    this.time = time;
                    this.timeUnit = timeUnit;
                }

                public boolean isAlive() {
                    return !this.scheduledExecutorService.isShutdown();
                }

                public Cancelable schedule(Runnable job) {
                    return new Cancelable.ForFuture(this.scheduledExecutorService.scheduleWithFixedDelay(job, this.time, this.time, this.timeUnit));
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
                    if (this.time != ((WithFixedDelay)object).time) {
                        return false;
                    }
                    if (!this.timeUnit.equals((Object)((WithFixedDelay)object).timeUnit)) {
                        return false;
                    }
                    return this.scheduledExecutorService.equals(((WithFixedDelay)object).scheduledExecutorService);
                }

                public int hashCode() {
                    long l = this.time;
                    return ((this.getClass().hashCode() * 31 + this.scheduledExecutorService.hashCode()) * 31 + (int)(l ^ l >>> 32)) * 31 + this.timeUnit.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class AtFixedRate
            implements ResubmissionScheduler {
                private final ScheduledExecutorService scheduledExecutorService;
                private final long time;
                private final TimeUnit timeUnit;

                public AtFixedRate(ScheduledExecutorService scheduledExecutorService, long time, TimeUnit timeUnit) {
                    this.scheduledExecutorService = scheduledExecutorService;
                    this.time = time;
                    this.timeUnit = timeUnit;
                }

                public boolean isAlive() {
                    return !this.scheduledExecutorService.isShutdown();
                }

                public Cancelable schedule(Runnable job) {
                    return new Cancelable.ForFuture(this.scheduledExecutorService.scheduleAtFixedRate(job, this.time, this.time, this.timeUnit));
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
                    if (this.time != ((AtFixedRate)object).time) {
                        return false;
                    }
                    if (!this.timeUnit.equals((Object)((AtFixedRate)object).timeUnit)) {
                        return false;
                    }
                    return this.scheduledExecutorService.equals(((AtFixedRate)object).scheduledExecutorService);
                }

                public int hashCode() {
                    long l = this.time;
                    return ((this.getClass().hashCode() * 31 + this.scheduledExecutorService.hashCode()) * 31 + (int)(l ^ l >>> 32)) * 31 + this.timeUnit.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements ResubmissionScheduler
            {
                INSTANCE;


                @Override
                public boolean isAlive() {
                    return false;
                }

                @Override
                public Cancelable schedule(Runnable job) {
                    return Cancelable.NoOp.INSTANCE;
                }
            }

            public static interface Cancelable {
                public void cancel();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForFuture
                implements Cancelable {
                    private final Future<?> future;

                    public ForFuture(Future<?> future) {
                        this.future = future;
                    }

                    @Override
                    public void cancel() {
                        this.future.cancel(true);
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
                        return this.future.equals(((ForFuture)object).future);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.future.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum NoOp implements Cancelable
                {
                    INSTANCE;


                    @Override
                    public void cancel() {
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface DiscoveryStrategy {
            public Iterable<Iterable<Class<?>>> resolve(Instrumentation var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Explicit
            implements DiscoveryStrategy {
                private final Set<Class<?>> types;

                public Explicit(Class<?> ... type) {
                    this(new LinkedHashSet(Arrays.asList(type)));
                }

                public Explicit(Set<Class<?>> types) {
                    this.types = types;
                }

                @Override
                public Iterable<Iterable<Class<?>>> resolve(Instrumentation instrumentation) {
                    return Collections.singleton(this.types);
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
                    return ((Object)this.types).equals(((Explicit)object).types);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.types).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Reiterating implements DiscoveryStrategy
            {
                INSTANCE;


                @Override
                public Iterable<Iterable<Class<?>>> resolve(Instrumentation instrumentation) {
                    return new ReiteratingIterable(instrumentation);
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                static enum WithSortOrderAssumption implements DiscoveryStrategy
                {
                    INSTANCE;


                    @Override
                    public Iterable<Iterable<Class<?>>> resolve(Instrumentation instrumentation) {
                        return new OrderedReiteratingIterable(instrumentation);
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class OrderedReiteratingIterator
                    implements Iterator<Iterable<Class<?>>> {
                        private final Instrumentation instrumentation;
                        private int index;
                        @MaybeNull
                        private List<Class<?>> types;

                        protected OrderedReiteratingIterator(Instrumentation instrumentation) {
                            this.instrumentation = instrumentation;
                            this.index = 0;
                        }

                        @Override
                        public boolean hasNext() {
                            if (this.types == null) {
                                Class[] type = this.instrumentation.getAllLoadedClasses();
                                this.types = new ArrayList<Class>(Arrays.asList(type).subList(this.index, type.length));
                                this.index = type.length;
                            }
                            return !this.types.isEmpty();
                        }

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public Iterable<Class<?>> next() {
                            if (this.hasNext()) {
                                try {
                                    List<Class<?>> list = this.types;
                                    Object var3_2 = null;
                                    this.types = null;
                                    return list;
                                }
                                catch (Throwable throwable) {
                                    Object var3_3 = null;
                                    this.types = null;
                                    throw throwable;
                                }
                            }
                            throw new NoSuchElementException();
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException("remove");
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class OrderedReiteratingIterable
                    implements Iterable<Iterable<Class<?>>> {
                        private final Instrumentation instrumentation;

                        protected OrderedReiteratingIterable(Instrumentation instrumentation) {
                            this.instrumentation = instrumentation;
                        }

                        @Override
                        public Iterator<Iterable<Class<?>>> iterator() {
                            return new OrderedReiteratingIterator(this.instrumentation);
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
                            return this.instrumentation.equals(((OrderedReiteratingIterable)object).instrumentation);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.instrumentation.hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class ReiteratingIterator
                implements Iterator<Iterable<Class<?>>> {
                    private final Instrumentation instrumentation;
                    private final Set<Class<?>> processed;
                    @MaybeNull
                    private List<Class<?>> types;

                    protected ReiteratingIterator(Instrumentation instrumentation) {
                        this.instrumentation = instrumentation;
                        this.processed = new HashSet();
                    }

                    @Override
                    public boolean hasNext() {
                        if (this.types == null) {
                            this.types = new ArrayList();
                            for (Class type : this.instrumentation.getAllLoadedClasses()) {
                                if (type == null || !this.processed.add(type)) continue;
                                this.types.add(type);
                            }
                        }
                        return !this.types.isEmpty();
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public Iterable<Class<?>> next() {
                        if (this.hasNext()) {
                            try {
                                List<Class<?>> list = this.types;
                                Object var3_2 = null;
                                this.types = null;
                                return list;
                            }
                            catch (Throwable throwable) {
                                Object var3_3 = null;
                                this.types = null;
                                throw throwable;
                            }
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class ReiteratingIterable
                implements Iterable<Iterable<Class<?>>> {
                    private final Instrumentation instrumentation;

                    protected ReiteratingIterable(Instrumentation instrumentation) {
                        this.instrumentation = instrumentation;
                    }

                    @Override
                    public Iterator<Iterable<Class<?>>> iterator() {
                        return new ReiteratingIterator(this.instrumentation);
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
                        return this.instrumentation.equals(((ReiteratingIterable)object).instrumentation);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.instrumentation.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum SinglePass implements DiscoveryStrategy
            {
                INSTANCE;


                @Override
                public Iterable<Iterable<Class<?>>> resolve(Instrumentation instrumentation) {
                    return Collections.singleton(Arrays.asList(instrumentation.getAllLoadedClasses()));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Listener {
            public void onBatch(int var1, List<Class<?>> var2, List<Class<?>> var3);

            public Iterable<? extends List<Class<?>>> onError(int var1, List<Class<?>> var2, Throwable var3, List<Class<?>> var4);

            public void onComplete(int var1, List<Class<?>> var2, Map<List<Class<?>>, Throwable> var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Compound
            implements Listener {
                private final List<Listener> listeners = new ArrayList<Listener>();

                public Compound(Listener ... listener) {
                    this(Arrays.asList(listener));
                }

                public Compound(List<? extends Listener> listeners) {
                    for (Listener listener : listeners) {
                        if (listener instanceof Compound) {
                            this.listeners.addAll(((Compound)listener).listeners);
                            continue;
                        }
                        if (listener instanceof NoOp) continue;
                        this.listeners.add(listener);
                    }
                }

                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                    for (Listener listener : this.listeners) {
                        listener.onBatch(index, batch, types);
                    }
                }

                @Override
                public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                    ArrayList reattempts = new ArrayList();
                    for (Listener listener : this.listeners) {
                        reattempts.add(listener.onError(index, batch, throwable, types));
                    }
                    return new CompoundIterable(reattempts);
                }

                @Override
                public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                    for (Listener listener : this.listeners) {
                        listener.onComplete(amount, types, failures);
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
                    return ((Object)this.listeners).equals(((Compound)object).listeners);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.listeners).hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class CompoundIterable
                implements Iterable<List<Class<?>>> {
                    private final List<Iterable<? extends List<Class<?>>>> iterables;

                    protected CompoundIterable(List<Iterable<? extends List<Class<?>>>> iterables) {
                        this.iterables = iterables;
                    }

                    @Override
                    public Iterator<List<Class<?>>> iterator() {
                        return new CompoundIterator(new ArrayList(this.iterables));
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
                        return ((Object)this.iterables).equals(((CompoundIterable)object).iterables);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + ((Object)this.iterables).hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class CompoundIterator
                    implements Iterator<List<Class<?>>> {
                        @MaybeNull
                        private Iterator<? extends List<Class<?>>> current;
                        private final List<Iterable<? extends List<Class<?>>>> backlog;

                        protected CompoundIterator(List<Iterable<? extends List<Class<?>>>> iterables) {
                            this.backlog = iterables;
                            this.forward();
                        }

                        @Override
                        public boolean hasNext() {
                            return this.current != null && this.current.hasNext();
                        }

                        @Override
                        public List<Class<?>> next() {
                            block3: {
                                try {
                                    if (this.current == null) break block3;
                                    List<Class<?>> list = this.current.next();
                                    Object var3_2 = null;
                                    this.forward();
                                    return list;
                                }
                                catch (Throwable throwable) {
                                    Object var3_3 = null;
                                    this.forward();
                                    throw throwable;
                                }
                            }
                            throw new NoSuchElementException();
                        }

                        private void forward() {
                            while (!(this.current != null && this.current.hasNext() || this.backlog.isEmpty())) {
                                this.current = this.backlog.remove(0).iterator();
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException("remove");
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class StreamWriting
            implements Listener {
                private final PrintStream printStream;

                public StreamWriting(PrintStream printStream) {
                    this.printStream = printStream;
                }

                public static Listener toSystemOut() {
                    return new StreamWriting(System.out);
                }

                public static Listener toSystemError() {
                    return new StreamWriting(System.err);
                }

                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                    this.printStream.printf("[Byte Buddy] REDEFINE BATCH #%d [%d of %d type(s)]%n", index, batch.size(), types.size());
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                    PrintStream printStream = this.printStream;
                    synchronized (printStream) {
                        this.printStream.printf("[Byte Buddy] REDEFINE ERROR #%d [%d of %d type(s)]%n", index, batch.size(), types.size());
                        throwable.printStackTrace(this.printStream);
                    }
                    return Collections.emptyList();
                }

                @Override
                public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                    this.printStream.printf("[Byte Buddy] REDEFINE COMPLETE %d batch(es) containing %d types [%d failed batch(es)]%n", amount, types.size(), failures.size());
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
                    return this.printStream.equals(((StreamWriting)object).printStream);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.printStream.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Pausing
            extends Adapter {
                private final long value;

                protected Pausing(long value) {
                    this.value = value;
                }

                public static Listener of(long value, TimeUnit timeUnit) {
                    if (value > 0L) {
                        return new Pausing(timeUnit.toMillis(value));
                    }
                    if (value == 0L) {
                        return NoOp.INSTANCE;
                    }
                    throw new IllegalArgumentException("Cannot sleep for a non-positive amount of time: " + value);
                }

                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                    if (index > 0) {
                        try {
                            Thread.sleep(this.value);
                        }
                        catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException(exception);
                        }
                    }
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
                    return this.value == ((Pausing)object).value;
                }

                @Override
                public int hashCode() {
                    long l = this.value;
                    return super.hashCode() * 31 + (int)(l ^ l >>> 32);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class BatchReallocator
            extends Adapter {
                private final BatchAllocator batchAllocator;

                public BatchReallocator(BatchAllocator batchAllocator) {
                    this.batchAllocator = batchAllocator;
                }

                public static Listener splitting() {
                    return new BatchReallocator(new BatchAllocator.Partitioning(2));
                }

                @Override
                public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                    return batch.size() < 2 ? Collections.emptyList() : this.batchAllocator.batch(batch);
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
                    return this.batchAllocator.equals(((BatchReallocator)object).batchAllocator);
                }

                @Override
                public int hashCode() {
                    return super.hashCode() * 31 + this.batchAllocator.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class Adapter
            implements Listener {
                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                }

                @Override
                public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                    return Collections.emptyList();
                }

                @Override
                public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    return this.getClass() == object.getClass();
                }

                public int hashCode() {
                    return this.getClass().hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ErrorEscalating implements Listener
            {
                FAIL_FAST{

                    @Override
                    public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                        throw new IllegalStateException("Could not transform any of " + batch, throwable);
                    }

                    @Override
                    public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                    }
                }
                ,
                FAIL_LAST{

                    @Override
                    public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                        return Collections.emptyList();
                    }

                    @Override
                    public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                        if (!failures.isEmpty()) {
                            throw new IllegalStateException("Could not transform any of " + failures);
                        }
                    }
                };


                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Yielding implements Listener
            {
                INSTANCE;


                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                    if (index > 0) {
                        Thread.yield();
                    }
                }

                @Override
                public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                    return Collections.emptyList();
                }

                @Override
                public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements Listener
            {
                INSTANCE;


                @Override
                public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                }

                @Override
                public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                    return Collections.emptyList();
                }

                @Override
                public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface BatchAllocator {
            public static final int FIRST_BATCH = 0;

            public Iterable<? extends List<Class<?>>> batch(List<Class<?>> var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Partitioning
            implements BatchAllocator {
                private final int parts;

                protected Partitioning(int parts) {
                    this.parts = parts;
                }

                public static BatchAllocator of(int parts) {
                    if (parts < 1) {
                        throw new IllegalArgumentException("A batch size must be positive: " + parts);
                    }
                    return new Partitioning(parts);
                }

                @Override
                public Iterable<? extends List<Class<?>>> batch(List<Class<?>> types) {
                    int reminder;
                    if (types.isEmpty()) {
                        return Collections.emptyList();
                    }
                    ArrayList batches = new ArrayList();
                    int size = types.size() / this.parts;
                    for (int index = reminder = types.size() % this.parts; index < types.size(); index += size) {
                        batches.add(new ArrayList(types.subList(index, index + size)));
                    }
                    if (batches.isEmpty()) {
                        return Collections.singletonList(types);
                    }
                    ((List)batches.get(0)).addAll(0, types.subList(0, reminder));
                    return batches;
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
                    return this.parts == ((Partitioning)object).parts;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.parts;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Slicing
            implements BatchAllocator {
                private final int minimum;
                private final int maximum;
                private final BatchAllocator batchAllocator;

                protected Slicing(int minimum, int maximum, BatchAllocator batchAllocator) {
                    this.minimum = minimum;
                    this.maximum = maximum;
                    this.batchAllocator = batchAllocator;
                }

                public static BatchAllocator withMinimum(int minimum, BatchAllocator batchAllocator) {
                    return Slicing.withinRange(minimum, Integer.MAX_VALUE, batchAllocator);
                }

                public static BatchAllocator withMaximum(int maximum, BatchAllocator batchAllocator) {
                    return Slicing.withinRange(1, maximum, batchAllocator);
                }

                public static BatchAllocator withinRange(int minimum, int maximum, BatchAllocator batchAllocator) {
                    if (minimum <= 0) {
                        throw new IllegalArgumentException("Minimum must be a positive number: " + minimum);
                    }
                    if (minimum > maximum) {
                        throw new IllegalArgumentException("Minimum must not be bigger than maximum: " + minimum + " >" + maximum);
                    }
                    return new Slicing(minimum, maximum, batchAllocator);
                }

                @Override
                public Iterable<? extends List<Class<?>>> batch(List<Class<?>> types) {
                    return new SlicingIterable(this.minimum, this.maximum, this.batchAllocator.batch(types));
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
                    if (this.minimum != ((Slicing)object).minimum) {
                        return false;
                    }
                    if (this.maximum != ((Slicing)object).maximum) {
                        return false;
                    }
                    return this.batchAllocator.equals(((Slicing)object).batchAllocator);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.minimum) * 31 + this.maximum) * 31 + this.batchAllocator.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class SlicingIterable
                implements Iterable<List<Class<?>>> {
                    private final int minimum;
                    private final int maximum;
                    private final Iterable<? extends List<Class<?>>> iterable;

                    protected SlicingIterable(int minimum, int maximum, Iterable<? extends List<Class<?>>> iterable) {
                        this.minimum = minimum;
                        this.maximum = maximum;
                        this.iterable = iterable;
                    }

                    @Override
                    public Iterator<List<Class<?>>> iterator() {
                        return new SlicingIterator(this.minimum, this.maximum, this.iterable.iterator());
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class SlicingIterator
                    implements Iterator<List<Class<?>>> {
                        private final int minimum;
                        private final int maximum;
                        private final Iterator<? extends List<Class<?>>> iterator;
                        private List<Class<?>> buffer;

                        protected SlicingIterator(int minimum, int maximum, Iterator<? extends List<Class<?>>> iterator) {
                            this.minimum = minimum;
                            this.maximum = maximum;
                            this.iterator = iterator;
                            this.buffer = new ArrayList();
                        }

                        @Override
                        public boolean hasNext() {
                            return !this.buffer.isEmpty() || this.iterator.hasNext();
                        }

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public List<Class<?>> next() {
                            if (this.buffer.isEmpty()) {
                                this.buffer = this.iterator.next();
                            }
                            while (this.buffer.size() < this.minimum && this.iterator.hasNext()) {
                                this.buffer.addAll((Collection)this.iterator.next());
                            }
                            if (this.buffer.size() > this.maximum) {
                                try {
                                    List<Class<?>> list = this.buffer.subList(0, this.maximum);
                                    Object var3_3 = null;
                                    this.buffer = new ArrayList(this.buffer.subList(this.maximum, this.buffer.size()));
                                    return list;
                                }
                                catch (Throwable throwable) {
                                    Object var3_4 = null;
                                    this.buffer = new ArrayList(this.buffer.subList(this.maximum, this.buffer.size()));
                                    throw throwable;
                                }
                            }
                            try {
                                List<Class<?>> list = this.buffer;
                                Object var5_6 = null;
                                this.buffer = new ArrayList();
                                return list;
                            }
                            catch (Throwable throwable) {
                                Object var5_7 = null;
                                this.buffer = new ArrayList();
                                throw throwable;
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException("remove");
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForMatchedGrouping
            implements BatchAllocator {
                private final Collection<? extends ElementMatcher<? super TypeDescription>> matchers;

                public ForMatchedGrouping(ElementMatcher<? super TypeDescription> ... matcher) {
                    this(new LinkedHashSet<ElementMatcher<? super TypeDescription>>(Arrays.asList(matcher)));
                }

                public ForMatchedGrouping(Collection<? extends ElementMatcher<? super TypeDescription>> matchers) {
                    this.matchers = matchers;
                }

                public BatchAllocator withMinimum(int threshold) {
                    return Slicing.withMinimum(threshold, this);
                }

                public BatchAllocator withMaximum(int threshold) {
                    return Slicing.withMaximum(threshold, this);
                }

                public BatchAllocator withinRange(int minimum, int maximum) {
                    return Slicing.withinRange(minimum, maximum, this);
                }

                @Override
                public Iterable<? extends List<Class<?>>> batch(List<Class<?>> types) {
                    LinkedHashMap matched = new LinkedHashMap();
                    ArrayList<Class> unmatched = new ArrayList<Class>();
                    for (ElementMatcher<? super TypeDescription> elementMatcher : this.matchers) {
                        matched.put(elementMatcher, new ArrayList());
                    }
                    block1: for (Class clazz : types) {
                        for (ElementMatcher<? super TypeDescription> elementMatcher : this.matchers) {
                            if (!elementMatcher.matches(TypeDescription.ForLoadedType.of(clazz))) continue;
                            ((List)matched.get(elementMatcher)).add(clazz);
                            continue block1;
                        }
                        unmatched.add(clazz);
                    }
                    ArrayList<List> batches = new ArrayList<List>(this.matchers.size() + 1);
                    for (List batch : matched.values()) {
                        if (batch.isEmpty()) continue;
                        batches.add(batch);
                    }
                    if (!unmatched.isEmpty()) {
                        batches.add(unmatched);
                    }
                    return batches;
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
                    return ((Object)this.matchers).equals(((ForMatchedGrouping)object).matchers);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFixedSize
            implements BatchAllocator {
                private final int size;

                protected ForFixedSize(int size) {
                    this.size = size;
                }

                public static BatchAllocator ofSize(int size) {
                    if (size > 0) {
                        return new ForFixedSize(size);
                    }
                    if (size == 0) {
                        return ForTotal.INSTANCE;
                    }
                    throw new IllegalArgumentException("Cannot define a batch with a negative size: " + size);
                }

                @Override
                public Iterable<? extends List<Class<?>>> batch(List<Class<?>> types) {
                    ArrayList batches = new ArrayList();
                    for (int index = 0; index < types.size(); index += this.size) {
                        batches.add(new ArrayList(types.subList(index, Math.min(types.size(), index + this.size))));
                    }
                    return batches;
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
                    return this.size == ((ForFixedSize)object).size;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.size;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForTotal implements BatchAllocator
            {
                INSTANCE;


                @Override
                public Iterable<? extends List<Class<?>>> batch(List<Class<?>> types) {
                    return types.isEmpty() ? Collections.emptySet() : Collections.singleton(types);
                }
            }
        }
    }

    public static interface TransformerDecorator {
        public ResettableClassFileTransformer decorate(ResettableClassFileTransformer var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements TransformerDecorator {
            private final List<TransformerDecorator> transformerDecorators = new ArrayList<TransformerDecorator>();

            public Compound(TransformerDecorator ... transformerDecorator) {
                this(Arrays.asList(transformerDecorator));
            }

            public Compound(List<? extends TransformerDecorator> transformerDecorators) {
                for (TransformerDecorator transformerDecorator : transformerDecorators) {
                    if (transformerDecorator instanceof Compound) {
                        this.transformerDecorators.addAll(((Compound)transformerDecorator).transformerDecorators);
                        continue;
                    }
                    if (transformerDecorator instanceof NoOp) continue;
                    this.transformerDecorators.add(transformerDecorator);
                }
            }

            @Override
            public ResettableClassFileTransformer decorate(ResettableClassFileTransformer classFileTransformer) {
                for (TransformerDecorator transformerDecorator : this.transformerDecorators) {
                    classFileTransformer = transformerDecorator.decorate(classFileTransformer);
                }
                return classFileTransformer;
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
                return ((Object)this.transformerDecorators).equals(((Compound)object).transformerDecorators);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.transformerDecorators).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForSubstitution implements TransformerDecorator
        {
            INSTANCE;


            @Override
            public ResettableClassFileTransformer decorate(ResettableClassFileTransformer classFileTransformer) {
                return ResettableClassFileTransformer.WithDelegation.Substitutable.of(classFileTransformer);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements TransformerDecorator
        {
            INSTANCE;


            @Override
            public ResettableClassFileTransformer decorate(ResettableClassFileTransformer classFileTransformer) {
                return classFileTransformer;
            }
        }
    }

    public static interface ClassFileBufferStrategy {
        public ClassFileLocator resolve(String var1, byte[] var2, @MaybeNull ClassLoader var3, @MaybeNull JavaModule var4, ProtectionDomain var5);

        public TypePool typePool(PoolStrategy var1, ClassFileLocator var2, @MaybeNull ClassLoader var3, String var4);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements ClassFileBufferStrategy
        {
            RETAINING{

                public ClassFileLocator resolve(String name, byte[] binaryRepresentation, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, ProtectionDomain protectionDomain) {
                    return ClassFileLocator.Simple.of(name, binaryRepresentation);
                }

                public TypePool typePool(PoolStrategy poolStrategy, ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader, String name) {
                    return poolStrategy.typePool(classFileLocator, classLoader, name);
                }
            }
            ,
            DISCARDING{

                public ClassFileLocator resolve(String name, byte[] binaryRepresentation, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, ProtectionDomain protectionDomain) {
                    return ClassFileLocator.NoOp.INSTANCE;
                }

                public TypePool typePool(PoolStrategy poolStrategy, ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader, String name) {
                    return poolStrategy.typePool(classFileLocator, classLoader);
                }
            };

        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface InstallationListener {
        @AlwaysNull
        public static final Throwable SUPPRESS_ERROR = null;

        public void onBeforeInstall(Instrumentation var1, ResettableClassFileTransformer var2);

        public void onInstall(Instrumentation var1, ResettableClassFileTransformer var2);

        @MaybeNull
        public Throwable onError(Instrumentation var1, ResettableClassFileTransformer var2, Throwable var3);

        public void onReset(Instrumentation var1, ResettableClassFileTransformer var2);

        public void onBeforeWarmUp(Set<Class<?>> var1, ResettableClassFileTransformer var2);

        public void onWarmUpError(Class<?> var1, ResettableClassFileTransformer var2, Throwable var3);

        public void onAfterWarmUp(Map<Class<?>, byte[]> var1, ResettableClassFileTransformer var2, boolean var3);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements InstallationListener {
            private final List<InstallationListener> installationListeners = new ArrayList<InstallationListener>();

            public Compound(InstallationListener ... installationListener) {
                this(Arrays.asList(installationListener));
            }

            public Compound(List<? extends InstallationListener> installationListeners) {
                for (InstallationListener installationListener : installationListeners) {
                    if (installationListener instanceof Compound) {
                        this.installationListeners.addAll(((Compound)installationListener).installationListeners);
                        continue;
                    }
                    if (installationListener instanceof NoOp) continue;
                    this.installationListeners.add(installationListener);
                }
            }

            @Override
            public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                for (InstallationListener installationListener : this.installationListeners) {
                    installationListener.onBeforeInstall(instrumentation, classFileTransformer);
                }
            }

            @Override
            public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                for (InstallationListener installationListener : this.installationListeners) {
                    installationListener.onInstall(instrumentation, classFileTransformer);
                }
            }

            @Override
            @MaybeNull
            public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                for (InstallationListener installationListener : this.installationListeners) {
                    if (throwable == SUPPRESS_ERROR) {
                        return SUPPRESS_ERROR;
                    }
                    throwable = installationListener.onError(instrumentation, classFileTransformer, throwable);
                }
                return throwable;
            }

            @Override
            public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                for (InstallationListener installationListener : this.installationListeners) {
                    installationListener.onReset(instrumentation, classFileTransformer);
                }
            }

            @Override
            public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {
                for (InstallationListener installationListener : this.installationListeners) {
                    installationListener.onBeforeWarmUp(types, classFileTransformer);
                }
            }

            @Override
            public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                for (InstallationListener installationListener : this.installationListeners) {
                    installationListener.onWarmUpError(type, classFileTransformer, throwable);
                }
            }

            @Override
            public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {
                for (InstallationListener installationListener : this.installationListeners) {
                    installationListener.onAfterWarmUp(types, classFileTransformer, transformed);
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
                return ((Object)this.installationListeners).equals(((Compound)object).installationListeners);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.installationListeners).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class StreamWriting
        implements InstallationListener {
            protected static final String PREFIX = "[Byte Buddy]";
            private final PrintStream printStream;

            public StreamWriting(PrintStream printStream) {
                this.printStream = printStream;
            }

            public static InstallationListener toSystemOut() {
                return new StreamWriting(System.out);
            }

            public static InstallationListener toSystemError() {
                return new StreamWriting(System.err);
            }

            @Override
            public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                this.printStream.printf("[Byte Buddy] BEFORE_INSTALL %s on %s%n", classFileTransformer, instrumentation);
            }

            @Override
            public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                this.printStream.printf("[Byte Buddy] INSTALL %s on %s%n", classFileTransformer, instrumentation);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                PrintStream printStream = this.printStream;
                synchronized (printStream) {
                    this.printStream.printf("[Byte Buddy] ERROR %s on %s%n", classFileTransformer, instrumentation);
                    throwable.printStackTrace(this.printStream);
                }
                return throwable;
            }

            @Override
            public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
                this.printStream.printf("[Byte Buddy] RESET %s on %s%n", classFileTransformer, instrumentation);
            }

            @Override
            public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {
                this.printStream.printf("[Byte Buddy] BEFORE_WARMUP %s on %s%n", classFileTransformer, types);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                PrintStream printStream = this.printStream;
                synchronized (printStream) {
                    this.printStream.printf("[Byte Buddy] ERROR_WARMUP %s on %s%n", classFileTransformer, type);
                    throwable.printStackTrace(this.printStream);
                }
            }

            @Override
            public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {
                this.printStream.printf("[Byte Buddy] AFTER_WARMUP %s %s on %s%n", transformed ? "transformed" : "not transformed", classFileTransformer, types.keySet());
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
                return this.printStream.equals(((StreamWriting)object).printStream);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.printStream.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class Adapter
        implements InstallationListener {
            @Override
            public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                return throwable;
            }

            @Override
            public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
            }

            @Override
            public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ErrorSuppressing implements InstallationListener
        {
            INSTANCE;


            @Override
            public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            @MaybeNull
            public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                return SUPPRESS_ERROR;
            }

            @Override
            public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
            }

            @Override
            public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements InstallationListener
        {
            INSTANCE;


            @Override
            public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
                return throwable;
            }

            @Override
            public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {
            }

            @Override
            public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
            }

            @Override
            public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface FallbackStrategy {
        public boolean isFallback(Class<?> var1, Throwable var2);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ByThrowableType
        implements FallbackStrategy {
            private final Set<? extends Class<? extends Throwable>> types;

            public ByThrowableType(Class<? extends Throwable> ... type) {
                this(new HashSet<Class<? extends Throwable>>(Arrays.asList(type)));
            }

            public ByThrowableType(Set<? extends Class<? extends Throwable>> types) {
                this.types = types;
            }

            public static FallbackStrategy ofOptionalTypes() {
                return new ByThrowableType(LinkageError.class, TypeNotPresentException.class);
            }

            @Override
            public boolean isFallback(Class<?> type, Throwable throwable) {
                for (Class<? extends Throwable> clazz : this.types) {
                    if (!clazz.isInstance(throwable)) continue;
                    return true;
                }
                return false;
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
                return ((Object)this.types).equals(((ByThrowableType)object).types);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.types).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Simple implements FallbackStrategy
        {
            ENABLED(true),
            DISABLED(false);

            private final boolean enabled;

            private Simple(boolean enabled) {
                this.enabled = enabled;
            }

            @Override
            public boolean isFallback(Class<?> type, Throwable throwable) {
                return this.enabled;
            }
        }
    }

    public static interface LocationStrategy {
        public ClassFileLocator classFileLocator(@MaybeNull ClassLoader var1, @MaybeNull JavaModule var2);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements LocationStrategy {
            private final List<LocationStrategy> locationStrategies = new ArrayList<LocationStrategy>();

            public Compound(LocationStrategy ... locationStrategy) {
                this(Arrays.asList(locationStrategy));
            }

            public Compound(List<? extends LocationStrategy> locationStrategies) {
                for (LocationStrategy locationStrategy : locationStrategies) {
                    if (locationStrategy instanceof Compound) {
                        this.locationStrategies.addAll(((Compound)locationStrategy).locationStrategies);
                        continue;
                    }
                    if (locationStrategy instanceof NoOp) continue;
                    this.locationStrategies.add(locationStrategy);
                }
            }

            @Override
            public ClassFileLocator classFileLocator(@MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                ArrayList<ClassFileLocator> classFileLocators = new ArrayList<ClassFileLocator>(this.locationStrategies.size());
                for (LocationStrategy locationStrategy : this.locationStrategies) {
                    classFileLocators.add(locationStrategy.classFileLocator(classLoader, module));
                }
                return new ClassFileLocator.Compound(classFileLocators);
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
                return ((Object)this.locationStrategies).equals(((Compound)object).locationStrategies);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.locationStrategies).hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements LocationStrategy {
            private final ClassFileLocator classFileLocator;

            public Simple(ClassFileLocator classFileLocator) {
                this.classFileLocator = classFileLocator;
            }

            public ClassFileLocator classFileLocator(@MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                return this.classFileLocator;
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
                return this.classFileLocator.equals(((Simple)object).classFileLocator);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.classFileLocator.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForClassLoader implements LocationStrategy
        {
            STRONG{

                public ClassFileLocator classFileLocator(@MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return ClassFileLocator.ForClassLoader.of(classLoader);
                }
            }
            ,
            WEAK{

                public ClassFileLocator classFileLocator(@MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return ClassFileLocator.ForClassLoader.WeaklyReferenced.of(classLoader);
                }
            };


            public LocationStrategy withFallbackTo(ClassFileLocator ... classFileLocator) {
                return this.withFallbackTo((Collection<? extends ClassFileLocator>)Arrays.asList(classFileLocator));
            }

            public LocationStrategy withFallbackTo(Collection<? extends ClassFileLocator> classFileLocators) {
                ArrayList<Simple> locationStrategies = new ArrayList<Simple>(classFileLocators.size());
                for (ClassFileLocator classFileLocator : classFileLocators) {
                    locationStrategies.add(new Simple(classFileLocator));
                }
                return this.withFallbackTo((List<? extends LocationStrategy>)locationStrategies);
            }

            public LocationStrategy withFallbackTo(LocationStrategy ... locationStrategy) {
                return this.withFallbackTo(Arrays.asList(locationStrategy));
            }

            public LocationStrategy withFallbackTo(List<? extends LocationStrategy> locationStrategies) {
                ArrayList<? extends LocationStrategy> allLocationStrategies = new ArrayList<LocationStrategy>(locationStrategies.size() + 1);
                allLocationStrategies.add(this);
                allLocationStrategies.addAll(locationStrategies);
                return new Compound(allLocationStrategies);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements LocationStrategy
        {
            INSTANCE;


            @Override
            public ClassFileLocator classFileLocator(@MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                return ClassFileLocator.NoOp.INSTANCE;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface DescriptionStrategy {
        public boolean isLoadedFirst();

        public TypeDescription apply(String var1, @MaybeNull Class<?> var2, TypePool var3, CircularityLock var4, @MaybeNull ClassLoader var5, @MaybeNull JavaModule var6);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class SuperTypeLoading
        implements DescriptionStrategy {
            private final DescriptionStrategy delegate;

            public SuperTypeLoading(DescriptionStrategy delegate) {
                this.delegate = delegate;
            }

            @Override
            public boolean isLoadedFirst() {
                return this.delegate.isLoadedFirst();
            }

            @Override
            public TypeDescription apply(String name, @MaybeNull Class<?> type, TypePool typePool, CircularityLock circularityLock, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                TypeDescription typeDescription = this.delegate.apply(name, type, typePool, circularityLock, classLoader, module);
                return typeDescription instanceof TypeDescription.ForLoadedType ? typeDescription : new TypeDescription.SuperTypeLoading(typeDescription, classLoader, new UnlockingClassLoadingDelegate(circularityLock));
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
                return this.delegate.equals(((SuperTypeLoading)object).delegate);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.delegate.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Asynchronous
            implements DescriptionStrategy {
                private final DescriptionStrategy delegate;
                private final ExecutorService executorService;

                public Asynchronous(DescriptionStrategy delegate, ExecutorService executorService) {
                    this.delegate = delegate;
                    this.executorService = executorService;
                }

                @Override
                public boolean isLoadedFirst() {
                    return this.delegate.isLoadedFirst();
                }

                @Override
                public TypeDescription apply(String name, @MaybeNull Class<?> type, TypePool typePool, CircularityLock circularityLock, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    TypeDescription typeDescription = this.delegate.apply(name, type, typePool, circularityLock, classLoader, module);
                    return typeDescription instanceof TypeDescription.ForLoadedType ? typeDescription : new TypeDescription.SuperTypeLoading(typeDescription, classLoader, new ThreadSwitchingClassLoadingDelegate(this.executorService));
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
                    if (!this.delegate.equals(((Asynchronous)object).delegate)) {
                        return false;
                    }
                    return this.executorService.equals(((Asynchronous)object).executorService);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.executorService.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class ThreadSwitchingClassLoadingDelegate
                implements TypeDescription.SuperTypeLoading.ClassLoadingDelegate {
                    private final ExecutorService executorService;

                    protected ThreadSwitchingClassLoadingDelegate(ExecutorService executorService) {
                        this.executorService = executorService;
                    }

                    @Override
                    public Class<?> load(String name, @MaybeNull ClassLoader classLoader) {
                        boolean holdsLock = classLoader != null && Thread.holdsLock(classLoader);
                        AtomicBoolean signal = new AtomicBoolean(holdsLock);
                        Future future = this.executorService.submit(holdsLock ? new NotifyingClassLoadingAction(name, classLoader, signal) : new SimpleClassLoadingAction(name, classLoader));
                        try {
                            while (holdsLock && signal.get()) {
                                classLoader.wait();
                            }
                            return (Class)future.get();
                        }
                        catch (ExecutionException exception) {
                            throw new IllegalStateException("Could not load " + name + " asynchronously", exception.getCause());
                        }
                        catch (Exception exception) {
                            throw new IllegalStateException("Could not load " + name + " asynchronously", exception);
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
                        return this.executorService.equals(((ThreadSwitchingClassLoadingDelegate)object).executorService);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.executorService.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class NotifyingClassLoadingAction
                    implements Callable<Class<?>> {
                        private final String name;
                        private final ClassLoader classLoader;
                        private final AtomicBoolean signal;

                        protected NotifyingClassLoadingAction(String name, ClassLoader classLoader, AtomicBoolean signal) {
                            this.name = name;
                            this.classLoader = classLoader;
                            this.signal = signal;
                        }

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public Class<?> call() throws ClassNotFoundException {
                            ClassLoader classLoader = this.classLoader;
                            synchronized (classLoader) {
                                Class<?> clazz;
                                try {
                                    clazz = Class.forName(this.name, false, this.classLoader);
                                    {
                                        Object var4_3 = null;
                                        this.signal.set(false);
                                        this.classLoader.notifyAll();
                                    }
                                }
                                catch (Throwable throwable) {
                                    Object var4_4 = null;
                                    this.signal.set(false);
                                    this.classLoader.notifyAll();
                                    throw throwable;
                                }
                                return clazz;
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
                            if (!this.name.equals(((NotifyingClassLoadingAction)object).name)) {
                                return false;
                            }
                            if (!this.classLoader.equals(((NotifyingClassLoadingAction)object).classLoader)) {
                                return false;
                            }
                            return this.signal.equals(((NotifyingClassLoadingAction)object).signal);
                        }

                        public int hashCode() {
                            return ((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.classLoader.hashCode()) * 31 + this.signal.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class SimpleClassLoadingAction
                    implements Callable<Class<?>> {
                        private final String name;
                        @MaybeNull
                        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                        private final ClassLoader classLoader;

                        protected SimpleClassLoadingAction(String name, @MaybeNull ClassLoader classLoader) {
                            this.name = name;
                            this.classLoader = classLoader;
                        }

                        @Override
                        public Class<?> call() throws ClassNotFoundException {
                            return Class.forName(this.name, false, this.classLoader);
                        }

                        public boolean equals(@MaybeNull Object object) {
                            block11: {
                                block10: {
                                    ClassLoader classLoader;
                                    block9: {
                                        ClassLoader classLoader2;
                                        if (this == object) {
                                            return true;
                                        }
                                        if (object == null) {
                                            return false;
                                        }
                                        if (this.getClass() != object.getClass()) {
                                            return false;
                                        }
                                        if (!this.name.equals(((SimpleClassLoadingAction)object).name)) {
                                            return false;
                                        }
                                        ClassLoader classLoader3 = ((SimpleClassLoadingAction)object).classLoader;
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
                            int n = (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31;
                            ClassLoader classLoader = this.classLoader;
                            if (classLoader != null) {
                                n = n + classLoader.hashCode();
                            }
                            return n;
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class UnlockingClassLoadingDelegate
            implements TypeDescription.SuperTypeLoading.ClassLoadingDelegate {
                private final CircularityLock circularityLock;

                protected UnlockingClassLoadingDelegate(CircularityLock circularityLock) {
                    this.circularityLock = circularityLock;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Class<?> load(String name, @MaybeNull ClassLoader classLoader) throws ClassNotFoundException {
                    Class<?> clazz;
                    this.circularityLock.release();
                    try {
                        clazz = Class.forName(name, false, classLoader);
                        Object var5_4 = null;
                        this.circularityLock.acquire();
                    }
                    catch (Throwable throwable) {
                        Object var5_5 = null;
                        this.circularityLock.acquire();
                        throw throwable;
                    }
                    return clazz;
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
                    return this.circularityLock.equals(((UnlockingClassLoadingDelegate)object).circularityLock);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.circularityLock.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements DescriptionStrategy
        {
            HYBRID(true){

                @Override
                public TypeDescription apply(String name, @MaybeNull Class<?> type, TypePool typePool, CircularityLock circularityLock, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return type == null ? typePool.describe(name).resolve() : TypeDescription.ForLoadedType.of(type);
                }
            }
            ,
            POOL_ONLY(false){

                @Override
                public TypeDescription apply(String name, @MaybeNull Class<?> type, TypePool typePool, CircularityLock circularityLock, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return typePool.describe(name).resolve();
                }
            }
            ,
            POOL_FIRST(false){

                @Override
                public TypeDescription apply(String name, @MaybeNull Class<?> type, TypePool typePool, CircularityLock circularityLock, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    TypePool.Resolution resolution = typePool.describe(name);
                    return resolution.isResolved() || type == null ? resolution.resolve() : TypeDescription.ForLoadedType.of(type);
                }
            };

            private final boolean loadedFirst;

            private Default(boolean loadedFirst) {
                this.loadedFirst = loadedFirst;
            }

            public DescriptionStrategy withSuperTypeLoading() {
                return new SuperTypeLoading(this);
            }

            @Override
            public boolean isLoadedFirst() {
                return this.loadedFirst;
            }

            public DescriptionStrategy withSuperTypeLoading(ExecutorService executorService) {
                return new SuperTypeLoading.Asynchronous(this, executorService);
            }
        }
    }

    public static interface InjectionStrategy {
        public ClassInjector resolve(@MaybeNull ClassLoader var1, @MaybeNull ProtectionDomain var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class UsingInstrumentation
        implements InjectionStrategy {
            private final Instrumentation instrumentation;
            private final File folder;

            public UsingInstrumentation(Instrumentation instrumentation, File folder) {
                this.instrumentation = instrumentation;
                this.folder = folder;
            }

            public ClassInjector resolve(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                return classLoader == null ? ClassInjector.UsingInstrumentation.of(this.folder, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, this.instrumentation) : UsingReflection.INSTANCE.resolve(classLoader, protectionDomain);
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
                if (!this.instrumentation.equals(((UsingInstrumentation)object).instrumentation)) {
                    return false;
                }
                return this.folder.equals(((UsingInstrumentation)object).folder);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentation.hashCode()) * 31 + this.folder.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum UsingJna implements InjectionStrategy
        {
            INSTANCE;


            @Override
            public ClassInjector resolve(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                if (ClassInjector.UsingJna.isAvailable()) {
                    return new ClassInjector.UsingJna(classLoader, protectionDomain);
                }
                throw new IllegalStateException("JNA-based injection is not available on the current VM");
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum UsingUnsafe implements InjectionStrategy
        {
            INSTANCE;


            @Override
            public ClassInjector resolve(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                if (ClassInjector.UsingUnsafe.isAvailable()) {
                    return new ClassInjector.UsingUnsafe(classLoader, protectionDomain);
                }
                throw new IllegalStateException("Unsafe-based injection is not available on the current VM");
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class OfFactory
            implements InjectionStrategy {
                private final ClassInjector.UsingUnsafe.Factory factory;

                public OfFactory(ClassInjector.UsingUnsafe.Factory factory) {
                    this.factory = factory;
                }

                public ClassInjector resolve(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                    return this.factory.make(classLoader, protectionDomain);
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
                    return this.factory.equals(((OfFactory)object).factory);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.factory.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum UsingReflection implements InjectionStrategy
        {
            INSTANCE;


            @Override
            public ClassInjector resolve(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                if (classLoader == null) {
                    throw new IllegalStateException("Cannot inject auxiliary class into bootstrap loader using reflection");
                }
                if (ClassInjector.UsingReflection.isAvailable()) {
                    return new ClassInjector.UsingReflection(classLoader, protectionDomain);
                }
                throw new IllegalStateException("Reflection-based injection is not available on the current VM");
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Disabled implements InjectionStrategy
        {
            INSTANCE;


            @Override
            public ClassInjector resolve(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                throw new IllegalStateException("Class injection is disabled");
            }
        }
    }

    public static interface InitializationStrategy {
        public Dispatcher dispatcher();

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class SelfInjection
        implements InitializationStrategy {
            protected final NexusAccessor nexusAccessor;

            protected SelfInjection(NexusAccessor nexusAccessor) {
                this.nexusAccessor = nexusAccessor;
            }

            @SuppressFBWarnings(value={"DMI_RANDOM_USED_ONLY_ONCE"}, justification="Avoids thread-contention.")
            public net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$Dispatcher dispatcher() {
                return this.dispatcher(new Random().nextInt());
            }

            protected abstract net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$Dispatcher dispatcher(int var1);

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
                return this.nexusAccessor.equals(((SelfInjection)object).nexusAccessor);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.nexusAccessor.hashCode();
            }

            public static class Eager
            extends SelfInjection {
                public Eager() {
                    this(new NexusAccessor());
                }

                public Eager(NexusAccessor nexusAccessor) {
                    super(nexusAccessor);
                }

                protected net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$Dispatcher dispatcher(int identification) {
                    return new Dispatcher(this.nexusAccessor, identification);
                }

                protected static class Dispatcher
                extends net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$SelfInjection$Dispatcher {
                    protected Dispatcher(NexusAccessor nexusAccessor, int identification) {
                        super(nexusAccessor, identification);
                    }

                    public void register(DynamicType dynamicType, @MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, InjectionStrategy injectionStrategy) {
                        Map<TypeDescription, byte[]> auxiliaryTypes = dynamicType.getAuxiliaryTypes();
                        Map<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers = dynamicType.getLoadedTypeInitializers();
                        if (!auxiliaryTypes.isEmpty()) {
                            for (Map.Entry<TypeDescription, Class<?>> entry : injectionStrategy.resolve(classLoader, protectionDomain).inject(auxiliaryTypes).entrySet()) {
                                loadedTypeInitializers.get(entry.getKey()).onLoad(entry.getValue());
                            }
                        }
                        LoadedTypeInitializer loadedTypeInitializer = loadedTypeInitializers.get(dynamicType.getTypeDescription());
                        this.nexusAccessor.register(dynamicType.getTypeDescription().getName(), classLoader, this.identification, loadedTypeInitializer);
                    }
                }
            }

            public static class Lazy
            extends SelfInjection {
                public Lazy() {
                    this(new NexusAccessor());
                }

                public Lazy(NexusAccessor nexusAccessor) {
                    super(nexusAccessor);
                }

                protected net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$Dispatcher dispatcher(int identification) {
                    return new Dispatcher(this.nexusAccessor, identification);
                }

                protected static class Dispatcher
                extends net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$SelfInjection$Dispatcher {
                    protected Dispatcher(NexusAccessor nexusAccessor, int identification) {
                        super(nexusAccessor, identification);
                    }

                    public void register(DynamicType dynamicType, @MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, InjectionStrategy injectionStrategy) {
                        Map<TypeDescription, byte[]> auxiliaryTypes = dynamicType.getAuxiliaryTypes();
                        LoadedTypeInitializer loadedTypeInitializer = auxiliaryTypes.isEmpty() ? dynamicType.getLoadedTypeInitializers().get(dynamicType.getTypeDescription()) : new Dispatcher.InjectingInitializer(dynamicType.getTypeDescription(), auxiliaryTypes, dynamicType.getLoadedTypeInitializers(), injectionStrategy.resolve(classLoader, protectionDomain));
                        this.nexusAccessor.register(dynamicType.getTypeDescription().getName(), classLoader, this.identification, loadedTypeInitializer);
                    }
                }
            }

            public static class Split
            extends SelfInjection {
                public Split() {
                    this(new NexusAccessor());
                }

                public Split(NexusAccessor nexusAccessor) {
                    super(nexusAccessor);
                }

                protected net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$Dispatcher dispatcher(int identification) {
                    return new Dispatcher(this.nexusAccessor, identification);
                }

                protected static class Dispatcher
                extends net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$SelfInjection$Dispatcher {
                    protected Dispatcher(NexusAccessor nexusAccessor, int identification) {
                        super(nexusAccessor, identification);
                    }

                    public void register(DynamicType dynamicType, @MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, InjectionStrategy injectionStrategy) {
                        LoadedTypeInitializer loadedTypeInitializer;
                        Map<TypeDescription, byte[]> auxiliaryTypes = dynamicType.getAuxiliaryTypes();
                        if (!auxiliaryTypes.isEmpty()) {
                            TypeDescription instrumentedType = dynamicType.getTypeDescription();
                            ClassInjector classInjector = injectionStrategy.resolve(classLoader, protectionDomain);
                            LinkedHashMap<TypeDescription, byte[]> independentTypes = new LinkedHashMap<TypeDescription, byte[]>(auxiliaryTypes);
                            LinkedHashMap<TypeDescription, byte[]> dependentTypes = new LinkedHashMap<TypeDescription, byte[]>(auxiliaryTypes);
                            for (TypeDescription typeDescription : auxiliaryTypes.keySet()) {
                                (typeDescription.getDeclaredAnnotations().isAnnotationPresent(AuxiliaryType.SignatureRelevant.class) ? dependentTypes : independentTypes).remove(typeDescription);
                            }
                            Map<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers = dynamicType.getLoadedTypeInitializers();
                            if (!independentTypes.isEmpty()) {
                                for (Map.Entry<TypeDescription, Class<?>> entry : classInjector.inject(independentTypes).entrySet()) {
                                    loadedTypeInitializers.get(entry.getKey()).onLoad(entry.getValue());
                                }
                            }
                            HashMap<TypeDescription, LoadedTypeInitializer> hashMap = new HashMap<TypeDescription, LoadedTypeInitializer>(loadedTypeInitializers);
                            loadedTypeInitializers.keySet().removeAll(independentTypes.keySet());
                            loadedTypeInitializer = hashMap.size() > 1 ? new Dispatcher.InjectingInitializer(instrumentedType, dependentTypes, hashMap, classInjector) : (LoadedTypeInitializer)hashMap.get(instrumentedType);
                        } else {
                            loadedTypeInitializer = dynamicType.getLoadedTypeInitializers().get(dynamicType.getTypeDescription());
                        }
                        this.nexusAccessor.register(dynamicType.getTypeDescription().getName(), classLoader, this.identification, loadedTypeInitializer);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static abstract class Dispatcher
            implements net.bytebuddy.agent.builder.AgentBuilder$InitializationStrategy$Dispatcher {
                protected final NexusAccessor nexusAccessor;
                protected final int identification;

                protected Dispatcher(NexusAccessor nexusAccessor, int identification) {
                    this.nexusAccessor = nexusAccessor;
                    this.identification = identification;
                }

                @Override
                public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder) {
                    return builder.initializer(new NexusAccessor.InitializationAppender(this.identification));
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
                    if (this.identification != ((Dispatcher)object).identification) {
                        return false;
                    }
                    return this.nexusAccessor.equals(((Dispatcher)object).nexusAccessor);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.nexusAccessor.hashCode()) * 31 + this.identification;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class InjectingInitializer
                implements LoadedTypeInitializer {
                    private final TypeDescription instrumentedType;
                    private final Map<TypeDescription, byte[]> rawAuxiliaryTypes;
                    private final Map<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers;
                    private final ClassInjector classInjector;

                    protected InjectingInitializer(TypeDescription instrumentedType, Map<TypeDescription, byte[]> rawAuxiliaryTypes, Map<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers, ClassInjector classInjector) {
                        this.instrumentedType = instrumentedType;
                        this.rawAuxiliaryTypes = rawAuxiliaryTypes;
                        this.loadedTypeInitializers = loadedTypeInitializers;
                        this.classInjector = classInjector;
                    }

                    @Override
                    public void onLoad(Class<?> type) {
                        for (Map.Entry<TypeDescription, Class<?>> auxiliary : this.classInjector.inject(this.rawAuxiliaryTypes).entrySet()) {
                            this.loadedTypeInitializers.get(auxiliary.getKey()).onLoad(auxiliary.getValue());
                        }
                        this.loadedTypeInitializers.get(this.instrumentedType).onLoad(type);
                    }

                    @Override
                    public boolean isAlive() {
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
                        if (!this.instrumentedType.equals(((InjectingInitializer)object).instrumentedType)) {
                            return false;
                        }
                        if (!((Object)this.rawAuxiliaryTypes).equals(((InjectingInitializer)object).rawAuxiliaryTypes)) {
                            return false;
                        }
                        if (!((Object)this.loadedTypeInitializers).equals(((InjectingInitializer)object).loadedTypeInitializers)) {
                            return false;
                        }
                        return this.classInjector.equals(((InjectingInitializer)object).classInjector);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + ((Object)this.rawAuxiliaryTypes).hashCode()) * 31 + ((Object)this.loadedTypeInitializers).hashCode()) * 31 + this.classInjector.hashCode();
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Minimal implements InitializationStrategy,
        Dispatcher
        {
            INSTANCE;


            @Override
            public Dispatcher dispatcher() {
                return this;
            }

            @Override
            public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder) {
                return builder;
            }

            @Override
            public void register(DynamicType dynamicType, @MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, InjectionStrategy injectionStrategy) {
                Map<TypeDescription, byte[]> auxiliaryTypes = dynamicType.getAuxiliaryTypes();
                LinkedHashMap<TypeDescription, byte[]> independentTypes = new LinkedHashMap<TypeDescription, byte[]>(auxiliaryTypes);
                for (TypeDescription auxiliaryType : auxiliaryTypes.keySet()) {
                    if (auxiliaryType.getDeclaredAnnotations().isAnnotationPresent(AuxiliaryType.SignatureRelevant.class)) continue;
                    independentTypes.remove(auxiliaryType);
                }
                if (!independentTypes.isEmpty()) {
                    ClassInjector classInjector = injectionStrategy.resolve(classLoader, protectionDomain);
                    Map<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers = dynamicType.getLoadedTypeInitializers();
                    for (Map.Entry<TypeDescription, Class<?>> entry : classInjector.inject(independentTypes).entrySet()) {
                        loadedTypeInitializers.get(entry.getKey()).onLoad(entry.getValue());
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements InitializationStrategy,
        Dispatcher
        {
            INSTANCE;


            @Override
            public Dispatcher dispatcher() {
                return this;
            }

            @Override
            public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder) {
                return builder;
            }

            @Override
            public void register(DynamicType dynamicType, @MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, InjectionStrategy injectionStrategy) {
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Dispatcher {
            public DynamicType.Builder<?> apply(DynamicType.Builder<?> var1);

            public void register(DynamicType var1, @MaybeNull ClassLoader var2, @MaybeNull ProtectionDomain var3, InjectionStrategy var4);
        }
    }

    public static interface PoolStrategy {
        public TypePool typePool(ClassFileLocator var1, @MaybeNull ClassLoader var2);

        public TypePool typePool(ClassFileLocator var1, @MaybeNull ClassLoader var2, String var3);

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class WithTypePoolCache
        implements PoolStrategy {
            protected final TypePool.Default.ReaderMode readerMode;

            protected WithTypePoolCache(TypePool.Default.ReaderMode readerMode) {
                this.readerMode = readerMode;
            }

            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader) {
                return new TypePool.LazyFacade(new TypePool.Default.WithLazyResolution(this.locate(classLoader), classFileLocator, this.readerMode));
            }

            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader, String name) {
                return new TypePool.LazyFacade(new TypePool.Default.WithLazyResolution(new TypePool.CacheProvider.Discriminating(ElementMatchers.is(name), new TypePool.CacheProvider.Simple(), this.locate(classLoader)), classFileLocator, this.readerMode));
            }

            protected abstract TypePool.CacheProvider locate(@MaybeNull ClassLoader var1);

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
                return this.readerMode.equals((Object)((WithTypePoolCache)object).readerMode);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.readerMode.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Simple
            extends WithTypePoolCache {
                private static final ClassLoader BOOTSTRAP_MARKER;
                private final ConcurrentMap<? super ClassLoader, TypePool.CacheProvider> cacheProviders;
                private static final boolean ACCESS_CONTROLLER;

                public Simple(ConcurrentMap<? super ClassLoader, TypePool.CacheProvider> cacheProviders) {
                    this(TypePool.Default.ReaderMode.FAST, cacheProviders);
                }

                public Simple(TypePool.Default.ReaderMode readerMode, ConcurrentMap<? super ClassLoader, TypePool.CacheProvider> cacheProviders) {
                    super(readerMode);
                    this.cacheProviders = cacheProviders;
                }

                @AccessControllerPlugin.Enhance
                private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
                    PrivilegedAction<T> action;
                    if (ACCESS_CONTROLLER) {
                        return AccessController.doPrivileged(privilegedAction);
                    }
                    return action.run();
                }

                @Override
                protected TypePool.CacheProvider locate(@MaybeNull ClassLoader classLoader) {
                    classLoader = classLoader == null ? this.getBootstrapMarkerLoader() : classLoader;
                    TypePool.CacheProvider cacheProvider = (TypePool.CacheProvider)this.cacheProviders.get(classLoader);
                    while (cacheProvider == null) {
                        cacheProvider = TypePool.CacheProvider.Simple.withObjectType();
                        TypePool.CacheProvider previous = this.cacheProviders.putIfAbsent(classLoader, cacheProvider);
                        if (previous == null) continue;
                        cacheProvider = previous;
                    }
                    return cacheProvider;
                }

                protected ClassLoader getBootstrapMarkerLoader() {
                    return BOOTSTRAP_MARKER;
                }

                /*
                 * Enabled aggressive block sorting
                 * Enabled unnecessary exception pruning
                 * Enabled aggressive exception aggregation
                 */
                static {
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
                    BOOTSTRAP_MARKER = Simple.doPrivileged(BootstrapMarkerAction.INSTANCE);
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
                    return this.cacheProviders.equals(((Simple)object).cacheProviders);
                }

                @Override
                public int hashCode() {
                    return super.hashCode() * 31 + this.cacheProviders.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static enum BootstrapMarkerAction implements PrivilegedAction<ClassLoader>
                {
                    INSTANCE;


                    @Override
                    public ClassLoader run() {
                        return new URLClassLoader(new URL[0], ClassLoadingStrategy.BOOTSTRAP_LOADER);
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ClassLoading implements PoolStrategy
        {
            EXTENDED(TypePool.Default.ReaderMode.EXTENDED),
            FAST(TypePool.Default.ReaderMode.FAST);

            private final TypePool.Default.ReaderMode readerMode;

            private ClassLoading(TypePool.Default.ReaderMode readerMode) {
                this.readerMode = readerMode;
            }

            @Override
            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader) {
                return TypePool.ClassLoading.of(classLoader, new TypePool.Default.WithLazyResolution(TypePool.CacheProvider.Simple.withObjectType(), classFileLocator, this.readerMode));
            }

            @Override
            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader, String name) {
                return this.typePool(classFileLocator, classLoader);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Eager implements PoolStrategy
        {
            EXTENDED(TypePool.Default.ReaderMode.EXTENDED),
            FAST(TypePool.Default.ReaderMode.FAST);

            private final TypePool.Default.ReaderMode readerMode;

            private Eager(TypePool.Default.ReaderMode readerMode) {
                this.readerMode = readerMode;
            }

            @Override
            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader) {
                return new TypePool.Default(TypePool.CacheProvider.Simple.withObjectType(), classFileLocator, this.readerMode);
            }

            @Override
            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader, String name) {
                return this.typePool(classFileLocator, classLoader);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements PoolStrategy
        {
            EXTENDED(TypePool.Default.ReaderMode.EXTENDED),
            FAST(TypePool.Default.ReaderMode.FAST);

            private final TypePool.Default.ReaderMode readerMode;

            private Default(TypePool.Default.ReaderMode readerMode) {
                this.readerMode = readerMode;
            }

            @Override
            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader) {
                return new TypePool.LazyFacade(new TypePool.Default.WithLazyResolution(TypePool.CacheProvider.Simple.withObjectType(), classFileLocator, this.readerMode));
            }

            @Override
            public TypePool typePool(ClassFileLocator classFileLocator, @MaybeNull ClassLoader classLoader, String name) {
                return this.typePool(classFileLocator, classLoader);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> var1, TypeDescription var2, @MaybeNull ClassLoader var3, @MaybeNull JavaModule var4, ProtectionDomain var5);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForAdvice
        implements Transformer {
            private final Advice.WithCustomMapping advice;
            private final Advice.ExceptionHandler exceptionHandler;
            private final Assigner assigner;
            private final ClassFileLocator classFileLocator;
            private final PoolStrategy poolStrategy;
            private final LocationStrategy locationStrategy;
            private final List<Entry> entries;

            public ForAdvice() {
                this(Advice.withCustomMapping());
            }

            public ForAdvice(Advice.WithCustomMapping advice) {
                this(advice, Advice.ExceptionHandler.Default.SUPPRESSING, Assigner.DEFAULT, ClassFileLocator.NoOp.INSTANCE, PoolStrategy.Default.FAST, LocationStrategy.ForClassLoader.STRONG, Collections.emptyList());
            }

            protected ForAdvice(Advice.WithCustomMapping advice, Advice.ExceptionHandler exceptionHandler, Assigner assigner, ClassFileLocator classFileLocator, PoolStrategy poolStrategy, LocationStrategy locationStrategy, List<Entry> entries) {
                this.advice = advice;
                this.exceptionHandler = exceptionHandler;
                this.assigner = assigner;
                this.classFileLocator = classFileLocator;
                this.poolStrategy = poolStrategy;
                this.locationStrategy = locationStrategy;
                this.entries = entries;
            }

            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, ProtectionDomain protectionDomain) {
                ClassFileLocator.Compound classFileLocator = new ClassFileLocator.Compound(this.classFileLocator, this.locationStrategy.classFileLocator(classLoader, module));
                TypePool typePool = this.poolStrategy.typePool(classFileLocator, classLoader);
                AsmVisitorWrapper.ForDeclaredMethods asmVisitorWrapper = new AsmVisitorWrapper.ForDeclaredMethods();
                for (Entry entry : this.entries) {
                    asmVisitorWrapper = asmVisitorWrapper.invokable(entry.getMatcher().resolve(typeDescription), this.wrap(typeDescription, classLoader, module, protectionDomain, entry.resolve(this.advice, typePool, classFileLocator).withAssigner(this.assigner).withExceptionHandler(this.exceptionHandler)));
                }
                return builder.visit(asmVisitorWrapper);
            }

            protected AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper wrap(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, ProtectionDomain protectionDomain, Advice advice) {
                return advice;
            }

            protected ForAdvice make(Advice.WithCustomMapping advice, Advice.ExceptionHandler exceptionHandler, Assigner assigner, ClassFileLocator classFileLocator, PoolStrategy poolStrategy, LocationStrategy locationStrategy, List<Entry> entries) {
                return new ForAdvice(advice, exceptionHandler, assigner, classFileLocator, poolStrategy, locationStrategy, entries);
            }

            public ForAdvice with(PoolStrategy poolStrategy) {
                return this.make(this.advice, this.exceptionHandler, this.assigner, this.classFileLocator, poolStrategy, this.locationStrategy, this.entries);
            }

            public ForAdvice with(LocationStrategy locationStrategy) {
                return this.make(this.advice, this.exceptionHandler, this.assigner, this.classFileLocator, this.poolStrategy, locationStrategy, this.entries);
            }

            public ForAdvice withExceptionHandler(Advice.ExceptionHandler exceptionHandler) {
                return this.make(this.advice, exceptionHandler, this.assigner, this.classFileLocator, this.poolStrategy, this.locationStrategy, this.entries);
            }

            public ForAdvice with(Assigner assigner) {
                return this.make(this.advice, this.exceptionHandler, assigner, this.classFileLocator, this.poolStrategy, this.locationStrategy, this.entries);
            }

            public ForAdvice include(ClassLoader ... classLoader) {
                LinkedHashSet<ClassFileLocator> classFileLocators = new LinkedHashSet<ClassFileLocator>();
                for (ClassLoader aClassLoader : classLoader) {
                    classFileLocators.add(ClassFileLocator.ForClassLoader.of(aClassLoader));
                }
                return this.include(new ArrayList(classFileLocators));
            }

            public ForAdvice include(ClassFileLocator ... classFileLocator) {
                return this.include(Arrays.asList(classFileLocator));
            }

            public ForAdvice include(List<? extends ClassFileLocator> classFileLocators) {
                return this.make(this.advice, this.exceptionHandler, this.assigner, new ClassFileLocator.Compound(CompoundList.of(this.classFileLocator, classFileLocators)), this.poolStrategy, this.locationStrategy, this.entries);
            }

            public ForAdvice advice(ElementMatcher<? super MethodDescription> matcher, String name) {
                return this.advice(new LatentMatcher.Resolved<MethodDescription>(matcher), name);
            }

            public ForAdvice advice(LatentMatcher<? super MethodDescription> matcher, String name) {
                return this.make(this.advice, this.exceptionHandler, this.assigner, this.classFileLocator, this.poolStrategy, this.locationStrategy, CompoundList.of(this.entries, new Entry.ForUnifiedAdvice(matcher, name)));
            }

            public ForAdvice advice(ElementMatcher<? super MethodDescription> matcher, String enter, String exit) {
                return this.advice(new LatentMatcher.Resolved<MethodDescription>(matcher), enter, exit);
            }

            public ForAdvice advice(LatentMatcher<? super MethodDescription> matcher, String enter, String exit) {
                return this.make(this.advice, this.exceptionHandler, this.assigner, this.classFileLocator, this.poolStrategy, this.locationStrategy, CompoundList.of(this.entries, new Entry.ForSplitAdvice(matcher, enter, exit)));
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
                if (!this.advice.equals(((ForAdvice)object).advice)) {
                    return false;
                }
                if (!this.exceptionHandler.equals(((ForAdvice)object).exceptionHandler)) {
                    return false;
                }
                if (!this.assigner.equals(((ForAdvice)object).assigner)) {
                    return false;
                }
                if (!this.classFileLocator.equals(((ForAdvice)object).classFileLocator)) {
                    return false;
                }
                if (!this.poolStrategy.equals(((ForAdvice)object).poolStrategy)) {
                    return false;
                }
                if (!this.locationStrategy.equals(((ForAdvice)object).locationStrategy)) {
                    return false;
                }
                return ((Object)this.entries).equals(((ForAdvice)object).entries);
            }

            public int hashCode() {
                return ((((((this.getClass().hashCode() * 31 + this.advice.hashCode()) * 31 + this.exceptionHandler.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.classFileLocator.hashCode()) * 31 + this.poolStrategy.hashCode()) * 31 + this.locationStrategy.hashCode()) * 31 + ((Object)this.entries).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static abstract class Entry {
                private final LatentMatcher<? super MethodDescription> matcher;

                protected Entry(LatentMatcher<? super MethodDescription> matcher) {
                    this.matcher = matcher;
                }

                protected LatentMatcher<? super MethodDescription> getMatcher() {
                    return this.matcher;
                }

                protected abstract Advice resolve(Advice.WithCustomMapping var1, TypePool var2, ClassFileLocator var3);

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
                    return this.matcher.equals(((Entry)object).matcher);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.matcher.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class ForSplitAdvice
                extends Entry {
                    private final String enter;
                    private final String exit;

                    protected ForSplitAdvice(LatentMatcher<? super MethodDescription> matcher, String enter, String exit) {
                        super(matcher);
                        this.enter = enter;
                        this.exit = exit;
                    }

                    @Override
                    protected Advice resolve(Advice.WithCustomMapping advice, TypePool typePool, ClassFileLocator classFileLocator) {
                        return advice.to(typePool.describe(this.enter).resolve(), typePool.describe(this.exit).resolve(), classFileLocator);
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
                        if (!this.enter.equals(((ForSplitAdvice)object).enter)) {
                            return false;
                        }
                        return this.exit.equals(((ForSplitAdvice)object).exit);
                    }

                    @Override
                    public int hashCode() {
                        return (super.hashCode() * 31 + this.enter.hashCode()) * 31 + this.exit.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class ForUnifiedAdvice
                extends Entry {
                    protected final String name;

                    protected ForUnifiedAdvice(LatentMatcher<? super MethodDescription> matcher, String name) {
                        super(matcher);
                        this.name = name;
                    }

                    @Override
                    protected Advice resolve(Advice.WithCustomMapping advice, TypePool typePool, ClassFileLocator classFileLocator) {
                        return advice.to(typePool.describe(this.name).resolve(), classFileLocator);
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
                        return this.name.equals(((ForUnifiedAdvice)object).name);
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode() * 31 + this.name.hashCode();
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForBuildPlugin
        implements Transformer {
            private final Plugin plugin;

            public ForBuildPlugin(Plugin plugin) {
                this.plugin = plugin;
            }

            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, ProtectionDomain protectionDomain) {
                return this.plugin.apply(builder, typeDescription, ClassFileLocator.ForClassLoader.of(classLoader));
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
                return this.plugin.equals(((ForBuildPlugin)object).plugin);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.plugin.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface TypeStrategy {
        public DynamicType.Builder<?> builder(TypeDescription var1, ByteBuddy var2, ClassFileLocator var3, MethodNameTransformer var4, @MaybeNull ClassLoader var5, @MaybeNull JavaModule var6, @MaybeNull ProtectionDomain var7);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForBuildEntryPoint
        implements TypeStrategy {
            private final EntryPoint entryPoint;

            public ForBuildEntryPoint(EntryPoint entryPoint) {
                this.entryPoint = entryPoint;
            }

            @Override
            public DynamicType.Builder<?> builder(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull ProtectionDomain protectionDomain) {
                return this.entryPoint.transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer);
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
                return this.entryPoint.equals(((ForBuildEntryPoint)object).entryPoint);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.entryPoint.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements TypeStrategy
        {
            REBASE{

                @Override
                public DynamicType.Builder<?> builder(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull ProtectionDomain protectionDomain) {
                    return byteBuddy.rebase(typeDescription, classFileLocator, methodNameTransformer);
                }
            }
            ,
            REDEFINE{

                @Override
                public DynamicType.Builder<?> builder(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull ProtectionDomain protectionDomain) {
                    return byteBuddy.redefine(typeDescription, classFileLocator);
                }
            }
            ,
            REDEFINE_FROZEN{

                @Override
                public DynamicType.Builder<?> builder(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull ProtectionDomain protectionDomain) {
                    return byteBuddy.with(InstrumentedType.Factory.Default.FROZEN).with(VisibilityBridgeStrategy.Default.NEVER).redefine(typeDescription, classFileLocator).ignoreAlso(LatentMatcher.ForSelfDeclaredMethod.NOT_DECLARED);
                }
            }
            ,
            DECORATE{

                @Override
                public DynamicType.Builder<?> builder(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull ProtectionDomain protectionDomain) {
                    return byteBuddy.decorate(typeDescription, classFileLocator);
                }
            };

        }
    }

    public static interface CircularityLock {
        public boolean acquire();

        public void release();

        @HashCodeAndEqualsPlugin.Enhance
        public static class Global
        implements CircularityLock {
            private final Lock lock = new ReentrantLock();
            private final long time;
            private final TimeUnit timeUnit;

            public Global() {
                this(0L, TimeUnit.MILLISECONDS);
            }

            public Global(long time, TimeUnit timeUnit) {
                this.time = time;
                this.timeUnit = timeUnit;
            }

            public boolean acquire() {
                try {
                    return this.time == 0L ? this.lock.tryLock() : this.lock.tryLock(this.time, this.timeUnit);
                }
                catch (InterruptedException ignored) {
                    return false;
                }
            }

            public void release() {
                this.lock.unlock();
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
                if (this.time != ((Global)object).time) {
                    return false;
                }
                if (!this.timeUnit.equals((Object)((Global)object).timeUnit)) {
                    return false;
                }
                return this.lock.equals(((Global)object).lock);
            }

            public int hashCode() {
                long l = this.time;
                return ((this.getClass().hashCode() * 31 + this.lock.hashCode()) * 31 + (int)(l ^ l >>> 32)) * 31 + this.timeUnit.hashCode();
            }
        }

        public static class Default
        implements CircularityLock {
            private final ConcurrentMap<Thread, Boolean> threads = new ConcurrentHashMap<Thread, Boolean>();

            public boolean acquire() {
                return this.threads.putIfAbsent(Thread.currentThread(), true) == null;
            }

            public void release() {
                this.threads.remove(Thread.currentThread());
            }

            protected boolean isLocked() {
                return this.threads.containsKey(Thread.currentThread());
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Inactive implements CircularityLock
        {
            INSTANCE;


            @Override
            public boolean acquire() {
                return true;
            }

            @Override
            public void release() {
            }
        }
    }

    public static interface Listener {
        public static final boolean LOADED = true;

        public void onDiscovery(String var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, boolean var4);

        public void onTransformation(TypeDescription var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, boolean var4, DynamicType var5);

        public void onIgnored(TypeDescription var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, boolean var4);

        public void onError(String var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, boolean var4, Throwable var5);

        public void onComplete(String var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, boolean var4);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements Listener {
            private final List<Listener> listeners = new ArrayList<Listener>();

            public Compound(Listener ... listener) {
                this(Arrays.asList(listener));
            }

            public Compound(List<? extends Listener> listeners) {
                for (Listener listener : listeners) {
                    if (listener instanceof Compound) {
                        this.listeners.addAll(((Compound)listener).listeners);
                        continue;
                    }
                    if (listener instanceof NoOp) continue;
                    this.listeners.add(listener);
                }
            }

            @Override
            public void onDiscovery(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                for (Listener listener : this.listeners) {
                    listener.onDiscovery(typeName, classLoader, module, loaded);
                }
            }

            @Override
            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                for (Listener listener : this.listeners) {
                    listener.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                }
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                for (Listener listener : this.listeners) {
                    listener.onIgnored(typeDescription, classLoader, module, loaded);
                }
            }

            @Override
            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
                for (Listener listener : this.listeners) {
                    listener.onError(typeName, classLoader, module, loaded, throwable);
                }
            }

            @Override
            public void onComplete(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                for (Listener listener : this.listeners) {
                    listener.onComplete(typeName, classLoader, module, loaded);
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
                return ((Object)this.listeners).equals(((Compound)object).listeners);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.listeners).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ModuleReadEdgeCompleting
        extends Adapter {
            private final Instrumentation instrumentation;
            private final boolean addTargetEdge;
            private final Set<? extends JavaModule> modules;

            public ModuleReadEdgeCompleting(Instrumentation instrumentation, boolean addTargetEdge, Set<? extends JavaModule> modules) {
                this.instrumentation = instrumentation;
                this.addTargetEdge = addTargetEdge;
                this.modules = modules;
            }

            public static Listener of(Instrumentation instrumentation, boolean addTargetEdge, Class<?> ... type) {
                HashSet<JavaModule> modules = new HashSet<JavaModule>();
                for (Class<?> aType : type) {
                    modules.add(JavaModule.ofType(aType));
                }
                return modules.isEmpty() ? NoOp.INSTANCE : new ModuleReadEdgeCompleting(instrumentation, addTargetEdge, modules);
            }

            @Override
            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                if (module != JavaModule.UNSUPPORTED && module.isNamed()) {
                    for (JavaModule javaModule : this.modules) {
                        if (!module.canRead(javaModule) || this.addTargetEdge && !module.isOpened(typeDescription.getPackage(), javaModule)) {
                            PackageDescription location = typeDescription.getPackage();
                            ClassInjector.UsingInstrumentation.redefineModule(this.instrumentation, module, Collections.singleton(javaModule), Collections.<String, Set<JavaModule>>emptyMap(), !this.addTargetEdge || location == null || location.isDefault() ? Collections.emptyMap() : Collections.singletonMap(location.getName(), Collections.singleton(javaModule)), Collections.<Class<?>>emptySet(), Collections.<Class<?>, List<Class<?>>>emptyMap());
                        }
                        if (!this.addTargetEdge || javaModule.canRead(module)) continue;
                        ClassInjector.UsingInstrumentation.redefineModule(this.instrumentation, javaModule, Collections.singleton(module), Collections.<String, Set<JavaModule>>emptyMap(), Collections.<String, Set<JavaModule>>emptyMap(), Collections.<Class<?>>emptySet(), Collections.<Class<?>, List<Class<?>>>emptyMap());
                    }
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
                if (this.addTargetEdge != ((ModuleReadEdgeCompleting)object).addTargetEdge) {
                    return false;
                }
                if (!this.instrumentation.equals(((ModuleReadEdgeCompleting)object).instrumentation)) {
                    return false;
                }
                return ((Object)this.modules).equals(((ModuleReadEdgeCompleting)object).modules);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.instrumentation.hashCode()) * 31 + this.addTargetEdge) * 31 + ((Object)this.modules).hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class WithErrorsOnly
        extends Adapter {
            private final Listener delegate;

            public WithErrorsOnly(Listener delegate) {
                this.delegate = delegate;
            }

            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
                this.delegate.onError(typeName, classLoader, module, loaded, throwable);
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
                return this.delegate.equals(((WithErrorsOnly)object).delegate);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.delegate.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class WithTransformationsOnly
        extends Adapter {
            private final Listener delegate;

            public WithTransformationsOnly(Listener delegate) {
                this.delegate = delegate;
            }

            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                this.delegate.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
            }

            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
                this.delegate.onError(typeName, classLoader, module, loaded, throwable);
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
                return this.delegate.equals(((WithTransformationsOnly)object).delegate);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.delegate.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Filtering
        implements Listener {
            private final ElementMatcher<? super String> matcher;
            private final Listener delegate;

            public Filtering(ElementMatcher<? super String> matcher, Listener delegate) {
                this.matcher = matcher;
                this.delegate = delegate;
            }

            @Override
            public void onDiscovery(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                if (this.matcher.matches(typeName)) {
                    this.delegate.onDiscovery(typeName, classLoader, module, loaded);
                }
            }

            @Override
            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                if (this.matcher.matches(typeDescription.getName())) {
                    this.delegate.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                }
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                if (this.matcher.matches(typeDescription.getName())) {
                    this.delegate.onIgnored(typeDescription, classLoader, module, loaded);
                }
            }

            @Override
            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
                if (this.matcher.matches(typeName)) {
                    this.delegate.onError(typeName, classLoader, module, loaded, throwable);
                }
            }

            @Override
            public void onComplete(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                if (this.matcher.matches(typeName)) {
                    this.delegate.onComplete(typeName, classLoader, module, loaded);
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
                if (!this.matcher.equals(((Filtering)object).matcher)) {
                    return false;
                }
                return this.delegate.equals(((Filtering)object).delegate);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.delegate.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class StreamWriting
        implements Listener {
            protected static final String PREFIX = "[Byte Buddy]";
            private final PrintStream printStream;

            public StreamWriting(PrintStream printStream) {
                this.printStream = printStream;
            }

            public static StreamWriting toSystemOut() {
                return new StreamWriting(System.out);
            }

            public static StreamWriting toSystemError() {
                return new StreamWriting(System.err);
            }

            public Listener withTransformationsOnly() {
                return new WithTransformationsOnly(this);
            }

            public Listener withErrorsOnly() {
                return new WithErrorsOnly(this);
            }

            public void onDiscovery(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                this.printStream.printf("[Byte Buddy] DISCOVERY %s [%s, %s, %s, loaded=%b]%n", typeName, classLoader, module, Thread.currentThread(), loaded);
            }

            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                this.printStream.printf("[Byte Buddy] TRANSFORM %s [%s, %s, %s, loaded=%b]%n", typeDescription.getName(), classLoader, module, Thread.currentThread(), loaded);
            }

            public void onIgnored(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                this.printStream.printf("[Byte Buddy] IGNORE %s [%s, %s, %s, loaded=%b]%n", typeDescription.getName(), classLoader, module, Thread.currentThread(), loaded);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
                PrintStream printStream = this.printStream;
                synchronized (printStream) {
                    this.printStream.printf("[Byte Buddy] ERROR %s [%s, %s, %s, loaded=%b]%n", typeName, classLoader, module, Thread.currentThread(), loaded);
                    throwable.printStackTrace(this.printStream);
                }
            }

            public void onComplete(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
                this.printStream.printf("[Byte Buddy] COMPLETE %s [%s, %s, %s, loaded=%b]%n", typeName, classLoader, module, Thread.currentThread(), loaded);
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
                return this.printStream.equals(((StreamWriting)object).printStream);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.printStream.hashCode();
            }
        }

        public static abstract class Adapter
        implements Listener {
            public void onDiscovery(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
            }

            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
            }

            public void onIgnored(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
            }

            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
            }

            public void onComplete(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Listener
        {
            INSTANCE;


            @Override
            public void onDiscovery(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
            }

            @Override
            public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
            }

            @Override
            public void onError(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, Throwable throwable) {
            }

            @Override
            public void onComplete(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded) {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface RawMatcher {
        public boolean matches(TypeDescription var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, @MaybeNull Class<?> var4, ProtectionDomain var5);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForElementMatchers
        implements RawMatcher {
            private final ElementMatcher<? super TypeDescription> typeMatcher;
            private final ElementMatcher<? super ClassLoader> classLoaderMatcher;
            private final ElementMatcher<? super JavaModule> moduleMatcher;

            public ForElementMatchers(ElementMatcher<? super TypeDescription> typeMatcher) {
                this(typeMatcher, ElementMatchers.any());
            }

            public ForElementMatchers(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
                this(typeMatcher, classLoaderMatcher, ElementMatchers.any());
            }

            public ForElementMatchers(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                this.typeMatcher = typeMatcher;
                this.classLoaderMatcher = classLoaderMatcher;
                this.moduleMatcher = moduleMatcher;
            }

            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                return this.moduleMatcher.matches(module) && this.classLoaderMatcher.matches(classLoader) && this.typeMatcher.matches(typeDescription);
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
                if (!this.typeMatcher.equals(((ForElementMatchers)object).typeMatcher)) {
                    return false;
                }
                if (!this.classLoaderMatcher.equals(((ForElementMatchers)object).classLoaderMatcher)) {
                    return false;
                }
                return this.moduleMatcher.equals(((ForElementMatchers)object).moduleMatcher);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.typeMatcher.hashCode()) * 31 + this.classLoaderMatcher.hashCode()) * 31 + this.moduleMatcher.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Inversion
        implements RawMatcher {
            private final RawMatcher matcher;

            public Inversion(RawMatcher matcher) {
                this.matcher = matcher;
            }

            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                return !this.matcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain);
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
                return this.matcher.equals(((Inversion)object).matcher);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.matcher.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Disjunction
        implements RawMatcher {
            private final List<RawMatcher> matchers;

            protected Disjunction(RawMatcher ... matcher) {
                this(Arrays.asList(matcher));
            }

            protected Disjunction(List<? extends RawMatcher> matchers) {
                this.matchers = new ArrayList<RawMatcher>(matchers.size());
                for (RawMatcher rawMatcher : matchers) {
                    if (rawMatcher instanceof Disjunction) {
                        this.matchers.addAll(((Disjunction)rawMatcher).matchers);
                        continue;
                    }
                    if (rawMatcher == Trivial.NON_MATCHING) continue;
                    this.matchers.add(rawMatcher);
                }
            }

            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                for (RawMatcher matcher : this.matchers) {
                    if (!matcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) continue;
                    return true;
                }
                return false;
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
                return ((Object)this.matchers).equals(((Disjunction)object).matchers);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Conjunction
        implements RawMatcher {
            private final List<RawMatcher> matchers;

            protected Conjunction(RawMatcher ... matcher) {
                this(Arrays.asList(matcher));
            }

            protected Conjunction(List<? extends RawMatcher> matchers) {
                this.matchers = new ArrayList<RawMatcher>(matchers.size());
                for (RawMatcher rawMatcher : matchers) {
                    if (rawMatcher instanceof Conjunction) {
                        this.matchers.addAll(((Conjunction)rawMatcher).matchers);
                        continue;
                    }
                    if (rawMatcher == Trivial.MATCHING) continue;
                    this.matchers.add(rawMatcher);
                }
            }

            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                for (RawMatcher matcher : this.matchers) {
                    if (matcher.matches(typeDescription, classLoader, module, classBeingRedefined, protectionDomain)) continue;
                    return false;
                }
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
                return ((Object)this.matchers).equals(((Conjunction)object).matchers);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForResolvableTypes implements RawMatcher
        {
            INSTANCE;


            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                if (classBeingRedefined != null) {
                    try {
                        return Class.forName(classBeingRedefined.getName(), true, classLoader) == classBeingRedefined;
                    }
                    catch (Throwable ignored) {
                        return false;
                    }
                }
                return true;
            }

            public RawMatcher inverted() {
                return new Inversion(this);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForLoadState implements RawMatcher
        {
            LOADED(false),
            UNLOADED(true);

            private final boolean unloaded;

            private ForLoadState(boolean unloaded) {
                this.unloaded = unloaded;
            }

            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                return classBeingRedefined == null == this.unloaded;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Trivial implements RawMatcher
        {
            MATCHING(true),
            NON_MATCHING(false);

            private final boolean matches;

            private Trivial(boolean matches) {
                this.matches = matches;
            }

            @Override
            public boolean matches(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                return this.matches;
            }
        }
    }

    public static interface Identified {
        public Extendable transform(Transformer var1);

        public static interface Extendable
        extends AgentBuilder,
        Identified {
            public AgentBuilder asTerminalTransformation();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Narrowable
        extends Matchable<Narrowable>,
        Identified {
        }
    }

    public static interface RedefinitionListenable
    extends AgentBuilder {
        public RedefinitionListenable with(RedefinitionStrategy.Listener var1);

        public WithoutResubmissionSpecification withResubmission(RedefinitionStrategy.ResubmissionScheduler var1);

        public static interface WithoutBatchStrategy
        extends WithImplicitDiscoveryStrategy {
            public WithImplicitDiscoveryStrategy with(RedefinitionStrategy.BatchAllocator var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface WithImplicitDiscoveryStrategy
        extends RedefinitionListenable {
            public RedefinitionListenable redefineOnly(Class<?> ... var1);

            public RedefinitionListenable with(RedefinitionStrategy.DiscoveryStrategy var1);
        }

        public static interface WithResubmissionSpecification
        extends WithoutResubmissionSpecification,
        AgentBuilder {
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface WithoutResubmissionSpecification {
            public WithResubmissionSpecification resubmitOnError();

            public WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> var1);

            public WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> var1, ElementMatcher<String> var2);

            public WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> var1, ElementMatcher<String> var2, ElementMatcher<? super ClassLoader> var3);

            public WithResubmissionSpecification resubmitOnError(ElementMatcher<? super Throwable> var1, ElementMatcher<String> var2, ElementMatcher<? super ClassLoader> var3, ElementMatcher<? super JavaModule> var4);

            public WithResubmissionSpecification resubmitOnError(ResubmissionOnErrorMatcher var1);

            public WithResubmissionSpecification resubmitImmediate();

            public WithResubmissionSpecification resubmitImmediate(ElementMatcher<String> var1);

            public WithResubmissionSpecification resubmitImmediate(ElementMatcher<String> var1, ElementMatcher<? super ClassLoader> var2);

            public WithResubmissionSpecification resubmitImmediate(ElementMatcher<String> var1, ElementMatcher<? super ClassLoader> var2, ElementMatcher<? super JavaModule> var3);

            public WithResubmissionSpecification resubmitImmediate(ResubmissionImmediateMatcher var1);
        }

        public static interface ResubmissionImmediateMatcher {
            public boolean matches(String var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForElementMatchers
            implements ResubmissionImmediateMatcher {
                private final ElementMatcher<String> typeNameMatcher;
                private final ElementMatcher<? super ClassLoader> classLoaderMatcher;
                private final ElementMatcher<? super JavaModule> moduleMatcher;

                public ForElementMatchers(ElementMatcher<String> typeNameMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                    this.typeNameMatcher = typeNameMatcher;
                    this.classLoaderMatcher = classLoaderMatcher;
                    this.moduleMatcher = moduleMatcher;
                }

                @Override
                public boolean matches(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return this.typeNameMatcher.matches(typeName) && this.classLoaderMatcher.matches(classLoader) && this.moduleMatcher.matches(module);
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
                    if (!this.typeNameMatcher.equals(((ForElementMatchers)object).typeNameMatcher)) {
                        return false;
                    }
                    if (!this.classLoaderMatcher.equals(((ForElementMatchers)object).classLoaderMatcher)) {
                        return false;
                    }
                    return this.moduleMatcher.equals(((ForElementMatchers)object).moduleMatcher);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.typeNameMatcher.hashCode()) * 31 + this.classLoaderMatcher.hashCode()) * 31 + this.moduleMatcher.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Disjunction
            implements ResubmissionImmediateMatcher {
                private final List<ResubmissionImmediateMatcher> matchers;

                public Disjunction(ResubmissionImmediateMatcher ... matcher) {
                    this(Arrays.asList(matcher));
                }

                public Disjunction(List<? extends ResubmissionImmediateMatcher> matchers) {
                    this.matchers = new ArrayList<ResubmissionImmediateMatcher>(matchers.size());
                    for (ResubmissionImmediateMatcher resubmissionImmediateMatcher : matchers) {
                        if (resubmissionImmediateMatcher instanceof Disjunction) {
                            this.matchers.addAll(((Disjunction)resubmissionImmediateMatcher).matchers);
                            continue;
                        }
                        if (resubmissionImmediateMatcher == Trivial.NON_MATCHING) continue;
                        this.matchers.add(resubmissionImmediateMatcher);
                    }
                }

                @Override
                public boolean matches(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    for (ResubmissionImmediateMatcher matcher : this.matchers) {
                        if (!matcher.matches(typeName, classLoader, module)) continue;
                        return true;
                    }
                    return false;
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
                    return ((Object)this.matchers).equals(((Disjunction)object).matchers);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Conjunction
            implements ResubmissionImmediateMatcher {
                private final List<ResubmissionImmediateMatcher> matchers;

                public Conjunction(ResubmissionImmediateMatcher ... matcher) {
                    this(Arrays.asList(matcher));
                }

                public Conjunction(List<? extends ResubmissionImmediateMatcher> matchers) {
                    this.matchers = new ArrayList<ResubmissionImmediateMatcher>(matchers.size());
                    for (ResubmissionImmediateMatcher resubmissionImmediateMatcher : matchers) {
                        if (resubmissionImmediateMatcher instanceof Conjunction) {
                            this.matchers.addAll(((Conjunction)resubmissionImmediateMatcher).matchers);
                            continue;
                        }
                        if (resubmissionImmediateMatcher == Trivial.NON_MATCHING) continue;
                        this.matchers.add(resubmissionImmediateMatcher);
                    }
                }

                @Override
                public boolean matches(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    for (ResubmissionImmediateMatcher matcher : this.matchers) {
                        if (matcher.matches(typeName, classLoader, module)) continue;
                        return false;
                    }
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
                    return ((Object)this.matchers).equals(((Conjunction)object).matchers);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Trivial implements ResubmissionImmediateMatcher
            {
                MATCHING(true),
                NON_MATCHING(false);

                private final boolean matching;

                private Trivial(boolean matching) {
                    this.matching = matching;
                }

                @Override
                public boolean matches(String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return this.matching;
                }
            }
        }

        public static interface ResubmissionOnErrorMatcher {
            public boolean matches(Throwable var1, String var2, @MaybeNull ClassLoader var3, @MaybeNull JavaModule var4);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForElementMatchers
            implements ResubmissionOnErrorMatcher {
                private final ElementMatcher<? super Throwable> exceptionMatcher;
                private final ElementMatcher<String> typeNameMatcher;
                private final ElementMatcher<? super ClassLoader> classLoaderMatcher;
                private final ElementMatcher<? super JavaModule> moduleMatcher;

                public ForElementMatchers(ElementMatcher<? super Throwable> exceptionMatcher, ElementMatcher<String> typeNameMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
                    this.exceptionMatcher = exceptionMatcher;
                    this.typeNameMatcher = typeNameMatcher;
                    this.classLoaderMatcher = classLoaderMatcher;
                    this.moduleMatcher = moduleMatcher;
                }

                @Override
                public boolean matches(Throwable throwable, String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return this.exceptionMatcher.matches(throwable) && this.typeNameMatcher.matches(typeName) && this.classLoaderMatcher.matches(classLoader) && this.moduleMatcher.matches(module);
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
                    if (!this.exceptionMatcher.equals(((ForElementMatchers)object).exceptionMatcher)) {
                        return false;
                    }
                    if (!this.typeNameMatcher.equals(((ForElementMatchers)object).typeNameMatcher)) {
                        return false;
                    }
                    if (!this.classLoaderMatcher.equals(((ForElementMatchers)object).classLoaderMatcher)) {
                        return false;
                    }
                    return this.moduleMatcher.equals(((ForElementMatchers)object).moduleMatcher);
                }

                public int hashCode() {
                    return (((this.getClass().hashCode() * 31 + this.exceptionMatcher.hashCode()) * 31 + this.typeNameMatcher.hashCode()) * 31 + this.classLoaderMatcher.hashCode()) * 31 + this.moduleMatcher.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Disjunction
            implements ResubmissionOnErrorMatcher {
                private final List<ResubmissionOnErrorMatcher> matchers;

                public Disjunction(ResubmissionOnErrorMatcher ... matcher) {
                    this(Arrays.asList(matcher));
                }

                public Disjunction(List<? extends ResubmissionOnErrorMatcher> matchers) {
                    this.matchers = new ArrayList<ResubmissionOnErrorMatcher>(matchers.size());
                    for (ResubmissionOnErrorMatcher resubmissionOnErrorMatcher : matchers) {
                        if (resubmissionOnErrorMatcher instanceof Disjunction) {
                            this.matchers.addAll(((Disjunction)resubmissionOnErrorMatcher).matchers);
                            continue;
                        }
                        if (resubmissionOnErrorMatcher == Trivial.NON_MATCHING) continue;
                        this.matchers.add(resubmissionOnErrorMatcher);
                    }
                }

                @Override
                public boolean matches(Throwable throwable, String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    for (ResubmissionOnErrorMatcher matcher : this.matchers) {
                        if (!matcher.matches(throwable, typeName, classLoader, module)) continue;
                        return true;
                    }
                    return false;
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
                    return ((Object)this.matchers).equals(((Disjunction)object).matchers);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Conjunction
            implements ResubmissionOnErrorMatcher {
                private final List<ResubmissionOnErrorMatcher> matchers;

                public Conjunction(ResubmissionOnErrorMatcher ... matcher) {
                    this(Arrays.asList(matcher));
                }

                public Conjunction(List<? extends ResubmissionOnErrorMatcher> matchers) {
                    this.matchers = new ArrayList<ResubmissionOnErrorMatcher>(matchers.size());
                    for (ResubmissionOnErrorMatcher resubmissionOnErrorMatcher : matchers) {
                        if (resubmissionOnErrorMatcher instanceof Conjunction) {
                            this.matchers.addAll(((Conjunction)resubmissionOnErrorMatcher).matchers);
                            continue;
                        }
                        if (resubmissionOnErrorMatcher == Trivial.MATCHING) continue;
                        this.matchers.add(resubmissionOnErrorMatcher);
                    }
                }

                @Override
                public boolean matches(Throwable throwable, String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    for (ResubmissionOnErrorMatcher matcher : this.matchers) {
                        if (matcher.matches(throwable, typeName, classLoader, module)) continue;
                        return false;
                    }
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
                    return ((Object)this.matchers).equals(((Conjunction)object).matchers);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Trivial implements ResubmissionOnErrorMatcher
            {
                MATCHING(true),
                NON_MATCHING(false);

                private final boolean matching;

                private Trivial(boolean matching) {
                    this.matching = matching;
                }

                @Override
                public boolean matches(Throwable throwable, String typeName, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module) {
                    return this.matching;
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Ignored
    extends Matchable<Ignored>,
    AgentBuilder {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Matchable<T extends Matchable<T>> {
        public T and(ElementMatcher<? super TypeDescription> var1);

        public T and(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2);

        public T and(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2, ElementMatcher<? super JavaModule> var3);

        public T and(RawMatcher var1);

        public T or(ElementMatcher<? super TypeDescription> var1);

        public T or(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2);

        public T or(ElementMatcher<? super TypeDescription> var1, ElementMatcher<? super ClassLoader> var2, ElementMatcher<? super JavaModule> var3);

        public T or(RawMatcher var1);
    }
}


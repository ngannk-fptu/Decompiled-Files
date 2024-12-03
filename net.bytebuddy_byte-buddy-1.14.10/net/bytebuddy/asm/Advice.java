/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.asm;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.RepeatedAnnotationPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bytecode.Addition;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.Throw;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.SerializedConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.Attribute;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.visitor.ExceptionTableSensitiveMethodVisitor;
import net.bytebuddy.utility.visitor.LineNumberPrependingMethodVisitor;
import net.bytebuddy.utility.visitor.StackAwareMethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class Advice
implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper,
Implementation {
    @AlwaysNull
    private static final ClassReader UNDEFINED = null;
    private static final MethodDescription.InDefinedShape SKIP_ON;
    private static final MethodDescription.InDefinedShape SKIP_ON_INDEX;
    private static final MethodDescription.InDefinedShape PREPEND_LINE_NUMBER;
    private static final MethodDescription.InDefinedShape INLINE_ENTER;
    private static final MethodDescription.InDefinedShape SUPPRESS_ENTER;
    private static final MethodDescription.InDefinedShape REPEAT_ON;
    private static final MethodDescription.InDefinedShape REPEAT_ON_INDEX;
    private static final MethodDescription.InDefinedShape ON_THROWABLE;
    private static final MethodDescription.InDefinedShape BACKUP_ARGUMENTS;
    private static final MethodDescription.InDefinedShape INLINE_EXIT;
    private static final MethodDescription.InDefinedShape SUPPRESS_EXIT;
    private final Dispatcher.Resolved.ForMethodEnter methodEnter;
    private final Dispatcher.Resolved.ForMethodExit methodExit;
    private final Assigner assigner;
    private final ExceptionHandler exceptionHandler;
    private final Implementation delegate;

    protected Advice(Dispatcher.Resolved.ForMethodEnter methodEnter, Dispatcher.Resolved.ForMethodExit methodExit) {
        this(methodEnter, methodExit, Assigner.DEFAULT, ExceptionHandler.Default.SUPPRESSING, SuperMethodCall.INSTANCE);
    }

    private Advice(Dispatcher.Resolved.ForMethodEnter methodEnter, Dispatcher.Resolved.ForMethodExit methodExit, Assigner assigner, ExceptionHandler exceptionHandler, Implementation delegate) {
        this.methodEnter = methodEnter;
        this.methodExit = methodExit;
        this.assigner = assigner;
        this.exceptionHandler = exceptionHandler;
        this.delegate = delegate;
    }

    public static Advice to(Class<?> advice) {
        return Advice.to(advice, ClassFileLocator.ForClassLoader.of(advice.getClassLoader()));
    }

    public static Advice to(Class<?> advice, ClassFileLocator classFileLocator) {
        return Advice.to(TypeDescription.ForLoadedType.of(advice), classFileLocator);
    }

    public static Advice to(TypeDescription advice) {
        return Advice.to(advice, (ClassFileLocator)ClassFileLocator.NoOp.INSTANCE);
    }

    public static Advice to(TypeDescription advice, ClassFileLocator classFileLocator) {
        return Advice.to(advice, PostProcessor.NoOp.INSTANCE, classFileLocator, Collections.emptyList(), Delegator.ForRegularInvocation.Factory.INSTANCE);
    }

    protected static Advice to(TypeDescription advice, PostProcessor.Factory postProcessorFactory, ClassFileLocator classFileLocator, List<? extends OffsetMapping.Factory<?>> userFactories, Delegator.Factory delegatorFactory) {
        Dispatcher.Unresolved methodEnter = Dispatcher.Inactive.INSTANCE;
        Dispatcher.Unresolved methodExit = Dispatcher.Inactive.INSTANCE;
        for (MethodDescription.InDefinedShape methodDescription : advice.getDeclaredMethods()) {
            methodEnter = Advice.locate(OnMethodEnter.class, INLINE_ENTER, methodEnter, methodDescription, delegatorFactory);
            methodExit = Advice.locate(OnMethodExit.class, INLINE_EXIT, methodExit, methodDescription, delegatorFactory);
        }
        if (!methodEnter.isAlive() && !methodExit.isAlive()) {
            throw new IllegalArgumentException("No advice defined by " + advice);
        }
        try {
            ClassReader classReader = methodEnter.isBinary() || methodExit.isBinary() ? OpenedClassReader.of(classFileLocator.locate(advice.getName()).resolve()) : UNDEFINED;
            return new Advice(methodEnter.asMethodEnter(userFactories, classReader, methodExit, postProcessorFactory), methodExit.asMethodExit(userFactories, classReader, methodEnter, postProcessorFactory));
        }
        catch (IOException exception) {
            throw new IllegalStateException("Error reading class file of " + advice, exception);
        }
    }

    public static Advice to(Class<?> enterAdvice, Class<?> exitAdvice) {
        ClassLoader exitLoader;
        ClassLoader enterLoader = enterAdvice.getClassLoader();
        return Advice.to(enterAdvice, exitAdvice, enterLoader == (exitLoader = exitAdvice.getClassLoader()) ? ClassFileLocator.ForClassLoader.of(enterLoader) : new ClassFileLocator.Compound(ClassFileLocator.ForClassLoader.of(enterLoader), ClassFileLocator.ForClassLoader.of(exitLoader)));
    }

    public static Advice to(Class<?> enterAdvice, Class<?> exitAdvice, ClassFileLocator classFileLocator) {
        return Advice.to(TypeDescription.ForLoadedType.of(enterAdvice), TypeDescription.ForLoadedType.of(exitAdvice), classFileLocator);
    }

    public static Advice to(TypeDescription enterAdvice, TypeDescription exitAdvice) {
        return Advice.to(enterAdvice, exitAdvice, (ClassFileLocator)ClassFileLocator.NoOp.INSTANCE);
    }

    public static Advice to(TypeDescription enterAdvice, TypeDescription exitAdvice, ClassFileLocator classFileLocator) {
        return Advice.to(enterAdvice, exitAdvice, PostProcessor.NoOp.INSTANCE, classFileLocator, Collections.emptyList(), Delegator.ForRegularInvocation.Factory.INSTANCE);
    }

    protected static Advice to(TypeDescription enterAdvice, TypeDescription exitAdvice, PostProcessor.Factory postProcessorFactory, ClassFileLocator classFileLocator, List<? extends OffsetMapping.Factory<?>> userFactories, Delegator.Factory delegatorFactory) {
        Dispatcher.Unresolved methodEnter = Dispatcher.Inactive.INSTANCE;
        Dispatcher.Unresolved methodExit = Dispatcher.Inactive.INSTANCE;
        for (MethodDescription.InDefinedShape methodDescription : enterAdvice.getDeclaredMethods()) {
            methodEnter = Advice.locate(OnMethodEnter.class, INLINE_ENTER, methodEnter, methodDescription, delegatorFactory);
        }
        if (!methodEnter.isAlive()) {
            throw new IllegalArgumentException("No enter advice defined by " + enterAdvice);
        }
        for (MethodDescription.InDefinedShape methodDescription : exitAdvice.getDeclaredMethods()) {
            methodExit = Advice.locate(OnMethodExit.class, INLINE_EXIT, methodExit, methodDescription, delegatorFactory);
        }
        if (!methodExit.isAlive()) {
            throw new IllegalArgumentException("No exit advice defined by " + exitAdvice);
        }
        try {
            return new Advice(methodEnter.asMethodEnter(userFactories, methodEnter.isBinary() ? OpenedClassReader.of(classFileLocator.locate(enterAdvice.getName()).resolve()) : UNDEFINED, methodExit, postProcessorFactory), methodExit.asMethodExit(userFactories, methodExit.isBinary() ? OpenedClassReader.of(classFileLocator.locate(exitAdvice.getName()).resolve()) : UNDEFINED, methodEnter, postProcessorFactory));
        }
        catch (IOException exception) {
            throw new IllegalStateException("Error reading class file of " + enterAdvice + " or " + exitAdvice, exception);
        }
    }

    private static Dispatcher.Unresolved locate(Class<? extends Annotation> type, MethodDescription.InDefinedShape property, Dispatcher.Unresolved dispatcher, MethodDescription.InDefinedShape methodDescription, Delegator.Factory delegatorFactory) {
        AnnotationDescription.Loadable<? extends Annotation> annotation = methodDescription.getDeclaredAnnotations().ofType(type);
        if (annotation == null) {
            return dispatcher;
        }
        if (dispatcher.isAlive()) {
            throw new IllegalStateException("Duplicate advice for " + dispatcher + " and " + methodDescription);
        }
        if (!methodDescription.isStatic()) {
            throw new IllegalStateException("Advice for " + methodDescription + " is not static");
        }
        return annotation.getValue(property).resolve(Boolean.class) != false ? new Dispatcher.Inlining(methodDescription) : new Dispatcher.Delegating(methodDescription, delegatorFactory);
    }

    public static WithCustomMapping withCustomMapping() {
        return new WithCustomMapping();
    }

    public AsmVisitorWrapper.ForDeclaredMethods on(ElementMatcher<? super MethodDescription> matcher) {
        return new AsmVisitorWrapper.ForDeclaredMethods().invokable(matcher, this);
    }

    @Override
    public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
        return instrumentedMethod.isAbstract() || instrumentedMethod.isNative() ? methodVisitor : this.doWrap(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, writerFlags, readerFlags);
    }

    protected MethodVisitor doWrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, int writerFlags, int readerFlags) {
        if (this.methodEnter.isPrependLineNumber()) {
            methodVisitor = new LineNumberPrependingMethodVisitor(methodVisitor);
        }
        if (!this.methodExit.isAlive()) {
            return new AdviceVisitor.WithoutExitAdvice(methodVisitor, implementationContext, this.assigner, this.exceptionHandler.resolve(instrumentedMethod, instrumentedType), instrumentedType, instrumentedMethod, this.methodEnter, writerFlags, readerFlags);
        }
        if (this.methodExit.getThrowable().represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class))) {
            return new AdviceVisitor.WithExitAdvice.WithoutExceptionHandling(methodVisitor, implementationContext, this.assigner, this.exceptionHandler.resolve(instrumentedMethod, instrumentedType), instrumentedType, instrumentedMethod, this.methodEnter, this.methodExit, writerFlags, readerFlags);
        }
        if (instrumentedMethod.isConstructor()) {
            throw new IllegalStateException("Cannot catch exception during constructor call for " + instrumentedMethod);
        }
        return new AdviceVisitor.WithExitAdvice.WithExceptionHandling(methodVisitor, implementationContext, this.assigner, this.exceptionHandler.resolve(instrumentedMethod, instrumentedType), instrumentedType, instrumentedMethod, this.methodEnter, this.methodExit, writerFlags, readerFlags, this.methodExit.getThrowable());
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return this.delegate.prepare(instrumentedType);
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new Appender(this, implementationTarget, this.delegate.appender(implementationTarget));
    }

    public Advice withAssigner(Assigner assigner) {
        return new Advice(this.methodEnter, this.methodExit, assigner, this.exceptionHandler, this.delegate);
    }

    public Advice withExceptionPrinting() {
        return this.withExceptionHandler(ExceptionHandler.Default.PRINTING);
    }

    public Advice withExceptionHandler(StackManipulation exceptionHandler) {
        return this.withExceptionHandler(new ExceptionHandler.Simple(exceptionHandler));
    }

    public Advice withExceptionHandler(ExceptionHandler exceptionHandler) {
        return new Advice(this.methodEnter, this.methodExit, this.assigner, exceptionHandler, this.delegate);
    }

    public Implementation wrap(Implementation implementation) {
        return new Advice(this.methodEnter, this.methodExit, this.assigner, this.exceptionHandler, implementation);
    }

    static {
        MethodList<MethodDescription.InDefinedShape> enter = TypeDescription.ForLoadedType.of(OnMethodEnter.class).getDeclaredMethods();
        SKIP_ON = (MethodDescription.InDefinedShape)((MethodList)enter.filter(ElementMatchers.named("skipOn"))).getOnly();
        SKIP_ON_INDEX = (MethodDescription.InDefinedShape)((MethodList)enter.filter(ElementMatchers.named("skipOnIndex"))).getOnly();
        PREPEND_LINE_NUMBER = (MethodDescription.InDefinedShape)((MethodList)enter.filter(ElementMatchers.named("prependLineNumber"))).getOnly();
        INLINE_ENTER = (MethodDescription.InDefinedShape)((MethodList)enter.filter(ElementMatchers.named("inline"))).getOnly();
        SUPPRESS_ENTER = (MethodDescription.InDefinedShape)((MethodList)enter.filter(ElementMatchers.named("suppress"))).getOnly();
        MethodList<MethodDescription.InDefinedShape> exit = TypeDescription.ForLoadedType.of(OnMethodExit.class).getDeclaredMethods();
        REPEAT_ON = (MethodDescription.InDefinedShape)((MethodList)exit.filter(ElementMatchers.named("repeatOn"))).getOnly();
        REPEAT_ON_INDEX = (MethodDescription.InDefinedShape)((MethodList)exit.filter(ElementMatchers.named("repeatOnIndex"))).getOnly();
        ON_THROWABLE = (MethodDescription.InDefinedShape)((MethodList)exit.filter(ElementMatchers.named("onThrowable"))).getOnly();
        BACKUP_ARGUMENTS = (MethodDescription.InDefinedShape)((MethodList)exit.filter(ElementMatchers.named("backupArguments"))).getOnly();
        INLINE_EXIT = (MethodDescription.InDefinedShape)((MethodList)exit.filter(ElementMatchers.named("inline"))).getOnly();
        SUPPRESS_EXIT = (MethodDescription.InDefinedShape)((MethodList)exit.filter(ElementMatchers.named("suppress"))).getOnly();
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
        if (!this.methodEnter.equals(((Advice)object).methodEnter)) {
            return false;
        }
        if (!this.methodExit.equals(((Advice)object).methodExit)) {
            return false;
        }
        if (!this.assigner.equals(((Advice)object).assigner)) {
            return false;
        }
        if (!this.exceptionHandler.equals(((Advice)object).exceptionHandler)) {
            return false;
        }
        return this.delegate.equals(((Advice)object).delegate);
    }

    public int hashCode() {
        return ((((this.getClass().hashCode() * 31 + this.methodEnter.hashCode()) * 31 + this.methodExit.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.exceptionHandler.hashCode()) * 31 + this.delegate.hashCode();
    }

    public static final class OnNonDefaultValue {
        private OnNonDefaultValue() {
            throw new UnsupportedOperationException("This class only serves as a marker type and should not be instantiated");
        }
    }

    public static final class OnDefaultValue {
        private OnDefaultValue() {
            throw new UnsupportedOperationException("This class only serves as a marker type and should not be instantiated");
        }
    }

    private static class NoExceptionHandler
    extends Throwable {
        private static final long serialVersionUID = 1L;
        private static final TypeDescription DESCRIPTION = TypeDescription.ForLoadedType.of(NoExceptionHandler.class);

        private NoExceptionHandler() {
            throw new UnsupportedOperationException("This class only serves as a marker type and should not be instantiated");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class WithCustomMapping {
        private final PostProcessor.Factory postProcessorFactory;
        private final Delegator.Factory delegatorFactory;
        private final Map<Class<? extends Annotation>, OffsetMapping.Factory<?>> offsetMappings;

        protected WithCustomMapping() {
            this(PostProcessor.NoOp.INSTANCE, Collections.emptyMap(), Delegator.ForRegularInvocation.Factory.INSTANCE);
        }

        protected WithCustomMapping(PostProcessor.Factory postProcessorFactory, Map<Class<? extends Annotation>, OffsetMapping.Factory<?>> offsetMappings, Delegator.Factory delegatorFactory) {
            this.postProcessorFactory = postProcessorFactory;
            this.offsetMappings = offsetMappings;
            this.delegatorFactory = delegatorFactory;
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, @MaybeNull Object value) {
            return this.bind(OffsetMapping.ForStackManipulation.Factory.of(type, value));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Field field) {
            return this.bind(type, new FieldDescription.ForLoadedField(field));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, FieldDescription fieldDescription) {
            return this.bind(new OffsetMapping.ForField.Resolved.Factory<T>(type, fieldDescription));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Method method, int index) {
            if (index < 0) {
                throw new IllegalArgumentException("A parameter cannot be negative: " + index);
            }
            if (method.getParameterTypes().length <= index) {
                throw new IllegalArgumentException(method + " does not declare a parameter with index " + index);
            }
            return this.bind(type, (ParameterDescription)new MethodDescription.ForLoadedMethod(method).getParameters().get(index));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Constructor<?> constructor, int index) {
            if (index < 0) {
                throw new IllegalArgumentException("A parameter cannot be negative: " + index);
            }
            if (constructor.getParameterTypes().length <= index) {
                throw new IllegalArgumentException(constructor + " does not declare a parameter with index " + index);
            }
            return this.bind(type, (ParameterDescription)new MethodDescription.ForLoadedConstructor(constructor).getParameters().get(index));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, ParameterDescription parameterDescription) {
            return this.bind(new OffsetMapping.ForArgument.Resolved.Factory<T>(type, parameterDescription));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Class<?> value) {
            return this.bind(type, TypeDescription.ForLoadedType.of(value));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, TypeDescription value) {
            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, value));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Enum<?> value) {
            return this.bind(type, new EnumerationDescription.ForLoadedEnumeration(value));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, EnumerationDescription value) {
            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, value));
        }

        public <T extends Annotation> WithCustomMapping bindSerialized(Class<T> type, Serializable value) {
            return this.bindSerialized(type, value, value.getClass());
        }

        public <T extends Annotation, S extends Serializable> WithCustomMapping bindSerialized(Class<T> type, S value, Class<? super S> targetType) {
            return this.bind(OffsetMapping.ForSerializedValue.Factory.of(type, value, targetType));
        }

        public <T extends Annotation> WithCustomMapping bindProperty(Class<T> type, String property) {
            return this.bind(OffsetMapping.ForStackManipulation.OfAnnotationProperty.of(type, property));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, JavaConstant constant) {
            return this.bind(type, (ConstantValue)constant);
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, ConstantValue constant) {
            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, constant.toStackManipulation(), constant.getTypeDescription().asGenericType()));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, StackManipulation stackManipulation, java.lang.reflect.Type targetType) {
            return this.bind(type, stackManipulation, TypeDefinition.Sort.describe(targetType));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, StackManipulation stackManipulation, TypeDescription.Generic targetType) {
            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, stackManipulation, targetType));
        }

        public <T extends Annotation> WithCustomMapping bindLambda(Class<T> type, Constructor<?> constructor, Class<?> functionalInterface) {
            return this.bindLambda(type, new MethodDescription.ForLoadedConstructor(constructor), TypeDescription.ForLoadedType.of(functionalInterface));
        }

        public <T extends Annotation> WithCustomMapping bindLambda(Class<T> type, Constructor<?> constructor, Class<?> functionalInterface, MethodGraph.Compiler methodGraphCompiler) {
            return this.bindLambda(type, new MethodDescription.ForLoadedConstructor(constructor), TypeDescription.ForLoadedType.of(functionalInterface), methodGraphCompiler);
        }

        public <T extends Annotation> WithCustomMapping bindLambda(Class<T> type, Method method, Class<?> functionalInterface) {
            return this.bindLambda(type, new MethodDescription.ForLoadedMethod(method), TypeDescription.ForLoadedType.of(functionalInterface));
        }

        public <T extends Annotation> WithCustomMapping bindLambda(Class<T> type, Method method, Class<?> functionalInterface, MethodGraph.Compiler methodGraphCompiler) {
            return this.bindLambda(type, new MethodDescription.ForLoadedMethod(method), TypeDescription.ForLoadedType.of(functionalInterface), methodGraphCompiler);
        }

        public <T extends Annotation> WithCustomMapping bindLambda(Class<T> type, MethodDescription.InDefinedShape methodDescription, TypeDescription functionalInterface) {
            return this.bindLambda(type, methodDescription, functionalInterface, MethodGraph.Compiler.DEFAULT);
        }

        public <T extends Annotation> WithCustomMapping bindLambda(Class<T> type, MethodDescription.InDefinedShape methodDescription, TypeDescription functionalInterface, MethodGraph.Compiler methodGraphCompiler) {
            if (!functionalInterface.isInterface()) {
                throw new IllegalArgumentException(functionalInterface + " is not an interface type");
            }
            MethodList methods = (MethodList)methodGraphCompiler.compile((TypeDefinition)functionalInterface).listNodes().asMethodList().filter(ElementMatchers.isAbstract());
            if (methods.size() != 1) {
                throw new IllegalArgumentException(functionalInterface + " does not define exactly one abstract method: " + methods);
            }
            return this.bindDynamic(type, (MethodDescription.InDefinedShape)new MethodDescription.Latent(new TypeDescription.Latent("java.lang.invoke.LambdaMetafactory", 1, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), new TypeDescription.Generic[0]), "metafactory", 9, Collections.emptyList(), JavaType.CALL_SITE.getTypeStub().asGenericType(), Arrays.asList(new ParameterDescription.Token(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().asGenericType()), new ParameterDescription.Token(TypeDescription.ForLoadedType.of(String.class).asGenericType()), new ParameterDescription.Token(JavaType.METHOD_TYPE.getTypeStub().asGenericType()), new ParameterDescription.Token(JavaType.METHOD_TYPE.getTypeStub().asGenericType()), new ParameterDescription.Token(JavaType.METHOD_HANDLE.getTypeStub().asGenericType()), new ParameterDescription.Token(JavaType.METHOD_TYPE.getTypeStub().asGenericType())), Collections.emptyList(), Collections.emptyList(), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED), JavaConstant.MethodType.ofSignature((MethodDescription)methods.asDefined().getOnly()), JavaConstant.MethodHandle.of(methodDescription), JavaConstant.MethodType.ofSignature((MethodDescription)methods.asDefined().getOnly()));
        }

        public <T extends Annotation> WithCustomMapping bindDynamic(Class<T> type, Method bootstrapMethod, Object ... constant) {
            return this.bindDynamic(type, bootstrapMethod, Arrays.asList(constant));
        }

        public <T extends Annotation> WithCustomMapping bindDynamic(Class<T> type, Method bootstrapMethod, List<?> constants) {
            return this.bindDynamic(type, (MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(bootstrapMethod), constants);
        }

        public <T extends Annotation> WithCustomMapping bindDynamic(Class<T> type, Constructor<?> bootstrapMethod, Object ... constant) {
            return this.bindDynamic(type, bootstrapMethod, Arrays.asList(constant));
        }

        public <T extends Annotation> WithCustomMapping bindDynamic(Class<T> type, Constructor<?> bootstrapMethod, List<?> constants) {
            return this.bindDynamic(type, (MethodDescription.InDefinedShape)new MethodDescription.ForLoadedConstructor(bootstrapMethod), constants);
        }

        public <T extends Annotation> WithCustomMapping bindDynamic(Class<T> type, MethodDescription.InDefinedShape bootstrapMethod, Object ... constant) {
            return this.bindDynamic(type, bootstrapMethod, Arrays.asList(constant));
        }

        public <T extends Annotation> WithCustomMapping bindDynamic(Class<T> type, MethodDescription.InDefinedShape bootstrapMethod, List<?> constants) {
            List<JavaConstant> arguments = JavaConstant.Simple.wrap(constants);
            if (!bootstrapMethod.isInvokeBootstrap(TypeList.Explicit.of(arguments))) {
                throw new IllegalArgumentException("Not a valid bootstrap method " + bootstrapMethod + " for " + arguments);
            }
            return this.bind(new OffsetMapping.ForStackManipulation.OfDynamicInvocation<T>(type, bootstrapMethod, arguments));
        }

        public <T extends Annotation> WithCustomMapping bind(Class<T> type, OffsetMapping offsetMapping) {
            return this.bind(new OffsetMapping.Factory.Simple<T>(type, offsetMapping));
        }

        public WithCustomMapping bind(OffsetMapping.Factory<?> offsetMapping) {
            LinkedHashMap offsetMappings = new LinkedHashMap(this.offsetMappings);
            if (!offsetMapping.getAnnotationType().isAnnotation()) {
                throw new IllegalArgumentException("Not an annotation type: " + offsetMapping.getAnnotationType());
            }
            if (offsetMappings.put(offsetMapping.getAnnotationType(), offsetMapping) != null) {
                throw new IllegalArgumentException("Annotation type already mapped: " + offsetMapping.getAnnotationType());
            }
            return new WithCustomMapping(this.postProcessorFactory, offsetMappings, this.delegatorFactory);
        }

        public WithCustomMapping bootstrap(Constructor<?> constructor) {
            return this.bootstrap(new MethodDescription.ForLoadedConstructor(constructor));
        }

        public WithCustomMapping bootstrap(Constructor<?> constructor, BootstrapArgumentResolver.Factory resolverFactory) {
            return this.bootstrap(new MethodDescription.ForLoadedConstructor(constructor), resolverFactory);
        }

        public WithCustomMapping bootstrap(Method method) {
            return this.bootstrap(new MethodDescription.ForLoadedMethod(method));
        }

        public WithCustomMapping bootstrap(Method method, BootstrapArgumentResolver.Factory resolver) {
            return this.bootstrap(new MethodDescription.ForLoadedMethod(method), resolver);
        }

        public WithCustomMapping bootstrap(MethodDescription.InDefinedShape bootstrap) {
            return this.bootstrap(bootstrap, (BootstrapArgumentResolver.Factory)BootstrapArgumentResolver.ForDefaultValues.Factory.INSTANCE);
        }

        public WithCustomMapping bootstrap(MethodDescription.InDefinedShape bootstrap, BootstrapArgumentResolver.Factory resolverFactory) {
            return new WithCustomMapping(this.postProcessorFactory, this.offsetMappings, Delegator.ForDynamicInvocation.of(bootstrap, resolverFactory));
        }

        public WithCustomMapping with(PostProcessor.Factory postProcessorFactory) {
            return new WithCustomMapping(new PostProcessor.Factory.Compound(this.postProcessorFactory, postProcessorFactory), this.offsetMappings, this.delegatorFactory);
        }

        public Advice to(Class<?> advice) {
            return this.to(advice, ClassFileLocator.ForClassLoader.of(advice.getClassLoader()));
        }

        public Advice to(Class<?> advice, ClassFileLocator classFileLocator) {
            return this.to(TypeDescription.ForLoadedType.of(advice), classFileLocator);
        }

        public Advice to(TypeDescription advice, ClassFileLocator classFileLocator) {
            return Advice.to(advice, this.postProcessorFactory, classFileLocator, new ArrayList(this.offsetMappings.values()), this.delegatorFactory);
        }

        public Advice to(Class<?> enterAdvice, Class<?> exitAdvice) {
            ClassLoader exitLoader;
            ClassLoader enterLoader = enterAdvice.getClassLoader();
            return this.to(enterAdvice, exitAdvice, enterLoader == (exitLoader = exitAdvice.getClassLoader()) ? ClassFileLocator.ForClassLoader.of(enterLoader) : new ClassFileLocator.Compound(ClassFileLocator.ForClassLoader.of(enterLoader), ClassFileLocator.ForClassLoader.of(exitLoader)));
        }

        public Advice to(Class<?> enterAdvice, Class<?> exitAdvice, ClassFileLocator classFileLocator) {
            return this.to(TypeDescription.ForLoadedType.of(enterAdvice), TypeDescription.ForLoadedType.of(exitAdvice), classFileLocator);
        }

        public Advice to(TypeDescription enterAdvice, TypeDescription exitAdvice) {
            return this.to(enterAdvice, exitAdvice, (ClassFileLocator)ClassFileLocator.NoOp.INSTANCE);
        }

        public Advice to(TypeDescription enterAdvice, TypeDescription exitAdvice, ClassFileLocator classFileLocator) {
            return Advice.to(enterAdvice, exitAdvice, this.postProcessorFactory, classFileLocator, new ArrayList(this.offsetMappings.values()), this.delegatorFactory);
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
            if (!this.postProcessorFactory.equals(((WithCustomMapping)object).postProcessorFactory)) {
                return false;
            }
            if (!this.delegatorFactory.equals(((WithCustomMapping)object).delegatorFactory)) {
                return false;
            }
            return ((Object)this.offsetMappings).equals(((WithCustomMapping)object).offsetMappings);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.postProcessorFactory.hashCode()) * 31 + this.delegatorFactory.hashCode()) * 31 + ((Object)this.offsetMappings).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class AssignReturned
    implements PostProcessor {
        public static final int NO_INDEX = -1;
        protected final TypeDescription.Generic type;
        protected final ExceptionHandler.Factory exceptionHandlerFactory;
        protected final boolean exit;
        protected final boolean skipOnDefaultValue;

        protected AssignReturned(TypeDescription.Generic type, ExceptionHandler.Factory exceptionHandlerFactory, boolean exit, boolean skipOnDefaultValue) {
            this.type = type;
            this.exceptionHandlerFactory = exceptionHandlerFactory;
            this.exit = exit;
            this.skipOnDefaultValue = skipOnDefaultValue;
        }

        @Override
        public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler, StackManipulation exceptionHandler) {
            ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(this.getHandlers().size());
            for (Handler handler : this.getHandlers()) {
                stackManipulations.add(handler.resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, this.getType(), this.toLoadInstruction(handler, this.exit ? argumentHandler.exit() : argumentHandler.enter())));
            }
            StackManipulation stackManipulation = this.exceptionHandlerFactory.wrap(new StackManipulation.Compound(stackManipulations), exceptionHandler, stackMapFrameHandler);
            return this.skipOnDefaultValue ? DefaultValueSkip.of(stackManipulation, stackMapFrameHandler, this.exit ? argumentHandler.exit() : argumentHandler.enter(), this.type) : stackManipulation;
        }

        protected abstract TypeDescription.Generic getType();

        protected abstract Collection<Handler> getHandlers();

        protected abstract StackManipulation toLoadInstruction(Handler var1, int var2);

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
            if (this.exit != ((AssignReturned)object).exit) {
                return false;
            }
            if (this.skipOnDefaultValue != ((AssignReturned)object).skipOnDefaultValue) {
                return false;
            }
            if (!this.type.equals(((AssignReturned)object).type)) {
                return false;
            }
            return this.exceptionHandlerFactory.equals(((AssignReturned)object).exceptionHandlerFactory);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.type.hashCode()) * 31 + this.exceptionHandlerFactory.hashCode()) * 31 + this.exit) * 31 + this.skipOnDefaultValue;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Factory
        implements PostProcessor.Factory {
            private static final MethodDescription.InDefinedShape SKIP_ON_DEFAULT_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(AsScalar.class).getDeclaredMethods().filter(ElementMatchers.named("skipOnDefaultValue"))).getOnly();
            private final List<? extends Handler.Factory<?>> factories;
            private final ExceptionHandler.Factory exceptionHandlerFactory;

            public Factory() {
                this(Arrays.asList(ToArguments.Handler.Factory.INSTANCE, ToAllArguments.Handler.Factory.INSTANCE, ToThis.Handler.Factory.INSTANCE, ToFields.Handler.Factory.INSTANCE, ToReturned.Handler.Factory.INSTANCE, ToThrown.Handler.Factory.INSTANCE), ExceptionHandler.Factory.NoOp.INSTANCE);
            }

            protected Factory(List<? extends Handler.Factory<?>> factories, ExceptionHandler.Factory exceptionHandlerFactory) {
                this.factories = factories;
                this.exceptionHandlerFactory = exceptionHandlerFactory;
            }

            public Factory with(Class<? extends Annotation> type, Handler ... handler) {
                return this.with(type, Arrays.asList(handler));
            }

            public Factory with(Class<? extends Annotation> type, List<Handler> handlers) {
                return this.with(new Handler.Factory.Simple<Annotation>(type, handlers));
            }

            public Factory with(Handler.Factory<?> factory) {
                return new Factory(CompoundList.of(this.factories, factory), this.exceptionHandlerFactory);
            }

            public PostProcessor.Factory withSuppressed(Class<? extends Throwable> exceptionType) {
                return this.withSuppressed(TypeDescription.ForLoadedType.of(exceptionType));
            }

            public PostProcessor.Factory withSuppressed(TypeDescription exceptionType) {
                if (!exceptionType.isAssignableTo(Throwable.class)) {
                    throw new IllegalArgumentException(exceptionType + " is not a throwable type");
                }
                return new Factory(this.factories, new ExceptionHandler.Factory.Enabled(exceptionType));
            }

            @Override
            public PostProcessor make(MethodDescription.InDefinedShape advice, boolean exit) {
                if (advice.getReturnType().represents(Void.TYPE)) {
                    return PostProcessor.NoOp.INSTANCE;
                }
                HashMap factories = new HashMap();
                for (Handler.Factory<?> factory : this.factories) {
                    if (factories.put(factory.getAnnotationType().getName(), factory) == null) continue;
                    throw new IllegalStateException("Duplicate registration of handler for " + factory.getAnnotationType());
                }
                LinkedHashMap handlers = new LinkedHashMap();
                boolean scalar = false;
                boolean skipOnDefaultValue = true;
                for (AnnotationDescription annotation : advice.getDeclaredAnnotations()) {
                    if (annotation.getAnnotationType().represents((java.lang.reflect.Type)((Object)AsScalar.class))) {
                        scalar = true;
                        skipOnDefaultValue = annotation.getValue(SKIP_ON_DEFAULT_VALUE).resolve(Boolean.class);
                        continue;
                    }
                    Handler.Factory factory = (Handler.Factory)factories.get(annotation.getAnnotationType().getName());
                    if (factory == null || handlers.put(factory.getAnnotationType(), factory.make(advice, exit, annotation.prepare(factory.getAnnotationType()))) == null) continue;
                    throw new IllegalStateException("Duplicate handler registration for " + annotation.getAnnotationType());
                }
                if (handlers.isEmpty()) {
                    return PostProcessor.NoOp.INSTANCE;
                }
                return !scalar && advice.getReturnType().isArray() ? new ForArray(advice.getReturnType(), this.exceptionHandlerFactory, exit, handlers.values()) : new ForScalar(advice.getReturnType(), this.exceptionHandlerFactory, exit, skipOnDefaultValue, handlers.values());
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
                if (!((Object)this.factories).equals(((Factory)object).factories)) {
                    return false;
                }
                return this.exceptionHandlerFactory.equals(((Factory)object).exceptionHandlerFactory);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + ((Object)this.factories).hashCode()) * 31 + this.exceptionHandlerFactory.hashCode();
            }
        }

        public static interface Handler {
            public int getIndex();

            public StackManipulation resolve(TypeDescription var1, MethodDescription var2, Assigner var3, ArgumentHandler var4, TypeDescription.Generic var5, StackManipulation var6);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Factory<T extends Annotation> {
                public Class<T> getAnnotationType();

                public List<Handler> make(MethodDescription.InDefinedShape var1, boolean var2, AnnotationDescription.Loadable<? extends T> var3);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple<S extends Annotation>
                implements Factory<S> {
                    private final Class<S> type;
                    private final List<Handler> handlers;

                    public Simple(Class<S> type, List<Handler> handlers) {
                        this.type = type;
                        this.handlers = handlers;
                    }

                    @Override
                    public Class<S> getAnnotationType() {
                        return this.type;
                    }

                    @Override
                    public List<Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends S> annotation) {
                        return this.handlers;
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
                        if (!this.type.equals(((Simple)object).type)) {
                            return false;
                        }
                        return ((Object)this.handlers).equals(((Simple)object).handlers);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.type.hashCode()) * 31 + ((Object)this.handlers).hashCode();
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class ExceptionHandler
        implements StackManipulation {
            private final StackManipulation stackManipulation;
            private final StackManipulation exceptionHandler;
            private final TypeDescription exceptionType;
            private final StackMapFrameHandler.ForPostProcessor stackMapFrameHandler;

            protected ExceptionHandler(StackManipulation stackManipulation, StackManipulation exceptionHandler, TypeDescription exceptionType, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler) {
                this.stackManipulation = stackManipulation;
                this.exceptionHandler = exceptionHandler;
                this.exceptionType = exceptionType;
                this.stackMapFrameHandler = stackMapFrameHandler;
            }

            public boolean isValid() {
                return this.stackManipulation.isValid() && this.exceptionHandler.isValid();
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                Label start = new Label();
                Label handler = new Label();
                Label end = new Label();
                methodVisitor.visitTryCatchBlock(start, handler, handler, this.exceptionType.getInternalName());
                methodVisitor.visitLabel(start);
                StackManipulation.Size size = this.stackManipulation.apply(methodVisitor, implementationContext);
                methodVisitor.visitJumpInsn(167, end);
                methodVisitor.visitLabel(handler);
                this.stackMapFrameHandler.injectIntermediateFrame(methodVisitor, Collections.singletonList(this.exceptionType));
                size = this.exceptionHandler.apply(methodVisitor, implementationContext).aggregate(size);
                methodVisitor.visitLabel(end);
                this.stackMapFrameHandler.injectIntermediateFrame(methodVisitor, Collections.emptyList());
                return size;
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
                if (!this.stackManipulation.equals(((ExceptionHandler)object).stackManipulation)) {
                    return false;
                }
                if (!this.exceptionHandler.equals(((ExceptionHandler)object).exceptionHandler)) {
                    return false;
                }
                if (!this.exceptionType.equals(((ExceptionHandler)object).exceptionType)) {
                    return false;
                }
                return this.stackMapFrameHandler.equals(((ExceptionHandler)object).stackMapFrameHandler);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.exceptionHandler.hashCode()) * 31 + this.exceptionType.hashCode()) * 31 + this.stackMapFrameHandler.hashCode();
            }

            public static interface Factory {
                public StackManipulation wrap(StackManipulation var1, StackManipulation var2, StackMapFrameHandler.ForPostProcessor var3);

                @HashCodeAndEqualsPlugin.Enhance
                public static class Enabled
                implements Factory {
                    private final TypeDescription exceptionType;

                    protected Enabled(TypeDescription exceptionType) {
                        this.exceptionType = exceptionType;
                    }

                    public StackManipulation wrap(StackManipulation stackManipulation, StackManipulation exceptionHandler, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler) {
                        return new ExceptionHandler(stackManipulation, exceptionHandler, this.exceptionType, stackMapFrameHandler);
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
                        return this.exceptionType.equals(((Enabled)object).exceptionType);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.exceptionType.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum NoOp implements Factory
                {
                    INSTANCE;


                    @Override
                    public StackManipulation wrap(StackManipulation stackManipulation, StackManipulation exceptionHandler, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler) {
                        return stackManipulation;
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class DefaultValueSkip
        implements StackManipulation {
            private final StackManipulation stackManipulation;
            private final StackMapFrameHandler.ForPostProcessor stackMapFrameHandler;
            private final int offset;
            private final Dispatcher dispatcher;

            protected DefaultValueSkip(StackManipulation stackManipulation, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler, int offset, Dispatcher dispatcher) {
                this.stackManipulation = stackManipulation;
                this.stackMapFrameHandler = stackMapFrameHandler;
                this.offset = offset;
                this.dispatcher = dispatcher;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            protected static StackManipulation of(StackManipulation stackManipulation, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler, int offset, TypeDefinition typeDefinition) {
                Dispatcher dispatcher;
                if (typeDefinition.isPrimitive()) {
                    if (typeDefinition.represents(Boolean.TYPE) || typeDefinition.represents(Byte.TYPE) || typeDefinition.represents(Short.TYPE) || typeDefinition.represents(Character.TYPE) || typeDefinition.represents(Integer.TYPE)) {
                        dispatcher = Dispatcher.INTEGER;
                        return new DefaultValueSkip(stackManipulation, stackMapFrameHandler, offset, dispatcher);
                    } else if (typeDefinition.represents(Long.TYPE)) {
                        dispatcher = Dispatcher.LONG;
                        return new DefaultValueSkip(stackManipulation, stackMapFrameHandler, offset, dispatcher);
                    } else if (typeDefinition.represents(Float.TYPE)) {
                        dispatcher = Dispatcher.FLOAT;
                        return new DefaultValueSkip(stackManipulation, stackMapFrameHandler, offset, dispatcher);
                    } else {
                        if (!typeDefinition.represents(Double.TYPE)) throw new IllegalArgumentException("Cannot apply skip for " + typeDefinition);
                        dispatcher = Dispatcher.DOUBLE;
                    }
                    return new DefaultValueSkip(stackManipulation, stackMapFrameHandler, offset, dispatcher);
                } else {
                    dispatcher = Dispatcher.REFERENCE;
                }
                return new DefaultValueSkip(stackManipulation, stackMapFrameHandler, offset, dispatcher);
            }

            public boolean isValid() {
                return this.stackManipulation.isValid();
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                Label label = new Label();
                StackManipulation.Size size = this.dispatcher.apply(methodVisitor, this.offset, label).aggregate(this.stackManipulation.apply(methodVisitor, implementationContext));
                methodVisitor.visitLabel(label);
                this.stackMapFrameHandler.injectIntermediateFrame(methodVisitor, Collections.emptyList());
                methodVisitor.visitInsn(0);
                return size;
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
                if (this.offset != ((DefaultValueSkip)object).offset) {
                    return false;
                }
                if (!this.dispatcher.equals((Object)((DefaultValueSkip)object).dispatcher)) {
                    return false;
                }
                if (!this.stackManipulation.equals(((DefaultValueSkip)object).stackManipulation)) {
                    return false;
                }
                return this.stackMapFrameHandler.equals(((DefaultValueSkip)object).stackMapFrameHandler);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.stackMapFrameHandler.hashCode()) * 31 + this.offset) * 31 + this.dispatcher.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Dispatcher {
                INTEGER{

                    protected StackManipulation.Size apply(MethodVisitor methodVisitor, int offset, Label label) {
                        methodVisitor.visitVarInsn(21, offset);
                        methodVisitor.visitJumpInsn(153, label);
                        return new StackManipulation.Size(0, 1);
                    }
                }
                ,
                LONG{

                    protected StackManipulation.Size apply(MethodVisitor methodVisitor, int offset, Label label) {
                        methodVisitor.visitVarInsn(22, offset);
                        methodVisitor.visitInsn(9);
                        methodVisitor.visitInsn(148);
                        methodVisitor.visitJumpInsn(153, label);
                        return new StackManipulation.Size(0, 4);
                    }
                }
                ,
                FLOAT{

                    protected StackManipulation.Size apply(MethodVisitor methodVisitor, int offset, Label label) {
                        methodVisitor.visitVarInsn(23, offset);
                        methodVisitor.visitInsn(11);
                        methodVisitor.visitInsn(149);
                        methodVisitor.visitJumpInsn(153, label);
                        return new StackManipulation.Size(0, 2);
                    }
                }
                ,
                DOUBLE{

                    protected StackManipulation.Size apply(MethodVisitor methodVisitor, int offset, Label label) {
                        methodVisitor.visitVarInsn(24, offset);
                        methodVisitor.visitInsn(14);
                        methodVisitor.visitInsn(151);
                        methodVisitor.visitJumpInsn(153, label);
                        return new StackManipulation.Size(0, 4);
                    }
                }
                ,
                REFERENCE{

                    protected StackManipulation.Size apply(MethodVisitor methodVisitor, int offset, Label label) {
                        methodVisitor.visitVarInsn(25, offset);
                        methodVisitor.visitJumpInsn(198, label);
                        return new StackManipulation.Size(0, 2);
                    }
                };


                protected abstract StackManipulation.Size apply(MethodVisitor var1, int var2, Label var3);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ForScalar
        extends AssignReturned {
            private final List<Handler> handlers = new ArrayList<Handler>();

            protected ForScalar(TypeDescription.Generic type, ExceptionHandler.Factory exceptionHandlerFactory, boolean exit, boolean skipOnDefaultValue, Collection<List<Handler>> handlers) {
                super(type, exceptionHandlerFactory, exit, skipOnDefaultValue);
                for (List<Handler> collection : handlers) {
                    for (Handler handler : collection) {
                        int index = handler.getIndex();
                        if (index > -1) {
                            throw new IllegalStateException("Handler on array requires negative index for " + handler);
                        }
                        this.handlers.add(handler);
                    }
                }
            }

            @Override
            protected TypeDescription.Generic getType() {
                return this.type;
            }

            @Override
            protected Collection<Handler> getHandlers() {
                return this.handlers;
            }

            @Override
            protected StackManipulation toLoadInstruction(Handler handler, int offset) {
                return MethodVariableAccess.of(this.type).loadFrom(offset);
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
                return ((Object)this.handlers).equals(((ForScalar)object).handlers);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + ((Object)this.handlers).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ForArray
        extends AssignReturned {
            private final Map<Handler, Integer> handlers = new LinkedHashMap<Handler, Integer>();

            protected ForArray(TypeDescription.Generic type, ExceptionHandler.Factory exceptionHandlerFactory, boolean exit, Collection<List<Handler>> handlers) {
                super(type, exceptionHandlerFactory, exit, true);
                for (List<Handler> collection : handlers) {
                    for (Handler handler : collection) {
                        int index = handler.getIndex();
                        if (index <= -1) {
                            throw new IllegalStateException("Handler on array requires positive index for " + handler);
                        }
                        this.handlers.put(handler, index);
                    }
                }
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            protected TypeDescription.Generic getType() {
                return this.type.getComponentType();
            }

            @Override
            protected Collection<Handler> getHandlers() {
                return this.handlers.keySet();
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            protected StackManipulation toLoadInstruction(Handler handler, int offset) {
                return new StackManipulation.Compound(MethodVariableAccess.REFERENCE.loadFrom(offset), IntegerConstant.forValue(this.handlers.get(handler)), ArrayAccess.of(this.type.getComponentType()).load());
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
                return ((Object)this.handlers).equals(((ForArray)object).handlers);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + ((Object)this.handlers).hashCode();
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface ToThrown {
            public int index() default -1;

            public Assigner.Typing typing() default Assigner.Typing.STATIC;

            @HashCodeAndEqualsPlugin.Enhance
            public static class Handler
            implements net.bytebuddy.asm.Advice$AssignReturned$Handler {
                private final int index;
                private final Assigner.Typing typing;

                protected Handler(int index, Assigner.Typing typing) {
                    this.index = index;
                    this.typing = typing;
                }

                public int getIndex() {
                    return this.index;
                }

                public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, TypeDescription.Generic type, StackManipulation value) {
                    StackManipulation assignment = assigner.assign(type, TypeDefinition.Sort.describe(Throwable.class), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + type + " to " + Throwable.class.getName());
                    }
                    return new StackManipulation.Compound(value, assignment, MethodVariableAccess.REFERENCE.storeAt(argumentHandler.thrown()));
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
                    if (this.index != ((Handler)object).index) {
                        return false;
                    }
                    return this.typing.equals((Object)((Handler)object).typing);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.index) * 31 + this.typing.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.asm.Advice$AssignReturned$Handler$Factory<ToThrown>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape TO_THROWN_INDEX;
                    private static final MethodDescription.InDefinedShape TO_THROWN_TYPING;

                    @Override
                    public Class<ToThrown> getAnnotationType() {
                        return ToThrown.class;
                    }

                    @Override
                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    public List<net.bytebuddy.asm.Advice$AssignReturned$Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends ToThrown> annotation) {
                        if (!exit) {
                            throw new IllegalStateException("Cannot assign thrown value from enter advice " + advice);
                        }
                        if (advice.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(ON_THROWABLE).resolve(TypeDescription.class).represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class))) {
                            throw new IllegalStateException("Cannot assign thrown value for non-catching exit advice " + advice);
                        }
                        return Collections.singletonList(new Handler(annotation.getValue(TO_THROWN_INDEX).resolve(Integer.class), annotation.getValue(TO_THROWN_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class)));
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(ToThrown.class).getDeclaredMethods();
                        TO_THROWN_INDEX = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("index"))).getOnly();
                        TO_THROWN_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    }
                }
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface ToReturned {
            public int index() default -1;

            public Assigner.Typing typing() default Assigner.Typing.STATIC;

            @HashCodeAndEqualsPlugin.Enhance
            public static class Handler
            implements net.bytebuddy.asm.Advice$AssignReturned$Handler {
                private final int index;
                private final Assigner.Typing typing;

                protected Handler(int index, Assigner.Typing typing) {
                    this.index = index;
                    this.typing = typing;
                }

                public int getIndex() {
                    return this.index;
                }

                public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, TypeDescription.Generic type, StackManipulation value) {
                    if (instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                        return StackManipulation.Trivial.INSTANCE;
                    }
                    StackManipulation assignment = assigner.assign(type, instrumentedMethod.getReturnType(), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + type + " to " + instrumentedMethod.getReturnType());
                    }
                    return new StackManipulation.Compound(value, assignment, MethodVariableAccess.of(instrumentedMethod.getReturnType()).storeAt(argumentHandler.returned()));
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
                    if (this.index != ((Handler)object).index) {
                        return false;
                    }
                    return this.typing.equals((Object)((Handler)object).typing);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.index) * 31 + this.typing.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.asm.Advice$AssignReturned$Handler$Factory<ToReturned>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape TO_RETURNED_INDEX;
                    private static final MethodDescription.InDefinedShape TO_RETURNED_TYPING;

                    @Override
                    public Class<ToReturned> getAnnotationType() {
                        return ToReturned.class;
                    }

                    @Override
                    public List<net.bytebuddy.asm.Advice$AssignReturned$Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends ToReturned> annotation) {
                        if (!exit) {
                            throw new IllegalStateException("Cannot write returned value from enter advice " + advice);
                        }
                        return Collections.singletonList(new Handler(annotation.getValue(TO_RETURNED_INDEX).resolve(Integer.class), annotation.getValue(TO_RETURNED_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class)));
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(ToReturned.class).getDeclaredMethods();
                        TO_RETURNED_INDEX = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("index"))).getOnly();
                        TO_RETURNED_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    }
                }
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface ToFields {
            public ToField[] value();

            @HashCodeAndEqualsPlugin.Enhance
            public static class Handler
            implements net.bytebuddy.asm.Advice$AssignReturned$Handler {
                private final int index;
                private final String name;
                private final TypeDescription declaringType;
                private final Assigner.Typing typing;

                protected Handler(int index, String name, TypeDescription declaringType, Assigner.Typing typing) {
                    this.index = index;
                    this.name = name;
                    this.declaringType = declaringType;
                    this.typing = typing;
                }

                public int getIndex() {
                    return this.index;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, TypeDescription.Generic type, StackManipulation value) {
                    StackManipulation stackManipulation;
                    FieldLocator.Resolution resolution;
                    FieldLocator.AbstractBase locator = this.declaringType.represents(Void.TYPE) ? new FieldLocator.ForClassHierarchy(instrumentedType) : new FieldLocator.ForExactType(this.declaringType);
                    FieldLocator.Resolution resolution2 = resolution = this.name.equals("") ? FieldLocator.Resolution.Simple.ofBeanAccessor(locator, instrumentedMethod) : locator.locate(this.name);
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Cannot resolve field " + this.name + " for " + instrumentedType);
                    }
                    if (!resolution.getField().isVisibleTo(instrumentedType)) {
                        throw new IllegalStateException(resolution.getField() + " is not visible to " + instrumentedType);
                    }
                    if (resolution.getField().isStatic()) {
                        stackManipulation = StackManipulation.Trivial.INSTANCE;
                    } else {
                        if (instrumentedMethod.isStatic()) {
                            throw new IllegalStateException("Cannot access member field " + resolution.getField() + " from static " + instrumentedMethod);
                        }
                        if (!instrumentedType.isAssignableTo(resolution.getField().getDeclaringType().asErasure())) {
                            throw new IllegalStateException(instrumentedType + " does not define " + resolution.getField());
                        }
                        stackManipulation = MethodVariableAccess.loadThis();
                    }
                    StackManipulation assignment = assigner.assign(type, resolution.getField().getType(), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + type + " to " + resolution.getField());
                    }
                    return new StackManipulation.Compound(stackManipulation, value, assignment, FieldAccess.forField(resolution.getField()).write());
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
                    if (this.index != ((Handler)object).index) {
                        return false;
                    }
                    if (!this.typing.equals((Object)((Handler)object).typing)) {
                        return false;
                    }
                    if (!this.name.equals(((Handler)object).name)) {
                        return false;
                    }
                    return this.declaringType.equals(((Handler)object).declaringType);
                }

                public int hashCode() {
                    return (((this.getClass().hashCode() * 31 + this.index) * 31 + this.name.hashCode()) * 31 + this.declaringType.hashCode()) * 31 + this.typing.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.asm.Advice$AssignReturned$Handler$Factory<ToFields>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape TO_FIELDS_VALUE;
                    private static final MethodDescription.InDefinedShape TO_FIELD_VALUE;
                    private static final MethodDescription.InDefinedShape TO_FIELD_INDEX;
                    private static final MethodDescription.InDefinedShape TO_FIELD_DECLARING_TYPE;
                    private static final MethodDescription.InDefinedShape TO_FIELD_TYPING;

                    @Override
                    public Class<ToFields> getAnnotationType() {
                        return ToFields.class;
                    }

                    @Override
                    public List<net.bytebuddy.asm.Advice$AssignReturned$Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends ToFields> annotation) {
                        ArrayList<net.bytebuddy.asm.Advice$AssignReturned$Handler> handlers = new ArrayList<net.bytebuddy.asm.Advice$AssignReturned$Handler>();
                        for (AnnotationDescription field : annotation.getValue(TO_FIELDS_VALUE).resolve(AnnotationDescription[].class)) {
                            handlers.add(new Handler(field.getValue(TO_FIELD_INDEX).resolve(Integer.class), field.getValue(TO_FIELD_VALUE).resolve(String.class), field.getValue(TO_FIELD_DECLARING_TYPE).resolve(TypeDescription.class), field.getValue(TO_FIELD_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class)));
                        }
                        return handlers;
                    }

                    static {
                        TO_FIELDS_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(ToFields.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(ToField.class).getDeclaredMethods();
                        TO_FIELD_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                        TO_FIELD_INDEX = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("index"))).getOnly();
                        TO_FIELD_DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("declaringType"))).getOnly();
                        TO_FIELD_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @Target(value={})
            @RepeatedAnnotationPlugin.Enhance(value=ToFields.class)
            @Repeatable(value=ToFields.class)
            public static @interface ToField {
                public String value() default "";

                public Class<?> declaringType() default void.class;

                public int index() default -1;

                public Assigner.Typing typing() default Assigner.Typing.STATIC;
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface ToThis {
            public int index() default -1;

            public Assigner.Typing typing() default Assigner.Typing.STATIC;

            @HashCodeAndEqualsPlugin.Enhance
            public static class Handler
            implements net.bytebuddy.asm.Advice$AssignReturned$Handler {
                private final int index;
                private final Assigner.Typing typing;
                private final boolean exit;

                protected Handler(int index, Assigner.Typing typing, boolean exit) {
                    this.index = index;
                    this.typing = typing;
                    this.exit = exit;
                }

                public int getIndex() {
                    return this.index;
                }

                public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, TypeDescription.Generic type, StackManipulation value) {
                    if (instrumentedMethod.isStatic()) {
                        throw new IllegalStateException("Cannot assign this reference for static method " + instrumentedMethod);
                    }
                    if (!this.exit && instrumentedMethod.isConstructor()) {
                        throw new IllegalStateException("Cannot assign this reference in constructor prior to initialization for " + instrumentedMethod);
                    }
                    StackManipulation assignment = assigner.assign(type, instrumentedType.asGenericType(), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + type + " to " + instrumentedType);
                    }
                    return new StackManipulation.Compound(value, assignment, MethodVariableAccess.REFERENCE.storeAt(argumentHandler.argument(0)));
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
                    if (this.index != ((Handler)object).index) {
                        return false;
                    }
                    if (this.exit != ((Handler)object).exit) {
                        return false;
                    }
                    return this.typing.equals((Object)((Handler)object).typing);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.index) * 31 + this.typing.hashCode()) * 31 + this.exit;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.asm.Advice$AssignReturned$Handler$Factory<ToThis>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape TO_THIS_INDEX;
                    private static final MethodDescription.InDefinedShape TO_THIS_TYPING;

                    @Override
                    public Class<ToThis> getAnnotationType() {
                        return ToThis.class;
                    }

                    @Override
                    public List<net.bytebuddy.asm.Advice$AssignReturned$Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends ToThis> annotation) {
                        return Collections.singletonList(new Handler(annotation.getValue(TO_THIS_INDEX).resolve(Integer.class), annotation.getValue(TO_THIS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), exit));
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(ToThis.class).getDeclaredMethods();
                        TO_THIS_INDEX = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("index"))).getOnly();
                        TO_THIS_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    }
                }
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface ToAllArguments {
            public int index() default -1;

            public Assigner.Typing typing() default Assigner.Typing.STATIC;

            @HashCodeAndEqualsPlugin.Enhance
            public static class Handler
            implements net.bytebuddy.asm.Advice$AssignReturned$Handler {
                private final int index;
                private final Assigner.Typing typing;

                protected Handler(int index, Assigner.Typing typing) {
                    this.index = index;
                    this.typing = typing;
                }

                public int getIndex() {
                    return this.index;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, TypeDescription.Generic type, StackManipulation value) {
                    ArrayList<StackManipulation.Compound> stackManipulations = new ArrayList<StackManipulation.Compound>(instrumentedMethod.getParameters().size());
                    if (!type.isArray()) {
                        StackManipulation assignment = assigner.assign(type, TypeDefinition.Sort.describe(Object[].class), this.typing);
                        if (!assignment.isValid()) {
                            throw new IllegalStateException("Cannot assign " + type + " to " + Object[].class);
                        }
                        type = TypeDefinition.Sort.describe(Object[].class);
                        value = new StackManipulation.Compound(value, assignment);
                    }
                    for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                        StackManipulation assignment = assigner.assign(type.getComponentType(), parameterDescription.getType(), this.typing);
                        if (!assignment.isValid()) {
                            throw new IllegalStateException("Cannot assign " + type.getComponentType() + " to " + parameterDescription);
                        }
                        stackManipulations.add(new StackManipulation.Compound(assignment, MethodVariableAccess.of(parameterDescription.getType()).storeAt(argumentHandler.argument(parameterDescription.getOffset()))));
                    }
                    return new StackManipulation.Compound(value, ArrayAccess.of(type.getComponentType()).forEach(stackManipulations), Removal.SINGLE);
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
                    if (this.index != ((Handler)object).index) {
                        return false;
                    }
                    return this.typing.equals((Object)((Handler)object).typing);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.index) * 31 + this.typing.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.asm.Advice$AssignReturned$Handler$Factory<ToAllArguments>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape TO_ALL_ARGUMENTS_INDEX;
                    private static final MethodDescription.InDefinedShape TO_ALL_ARGUMENTS_TYPING;

                    @Override
                    public Class<ToAllArguments> getAnnotationType() {
                        return ToAllArguments.class;
                    }

                    @Override
                    public List<net.bytebuddy.asm.Advice$AssignReturned$Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends ToAllArguments> annotation) {
                        return Collections.singletonList(new Handler(annotation.getValue(TO_ALL_ARGUMENTS_INDEX).resolve(Integer.class), annotation.getValue(TO_ALL_ARGUMENTS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class)));
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(ToAllArguments.class).getDeclaredMethods();
                        TO_ALL_ARGUMENTS_INDEX = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("index"))).getOnly();
                        TO_ALL_ARGUMENTS_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    }
                }
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface ToArguments {
            public ToArgument[] value();

            @HashCodeAndEqualsPlugin.Enhance
            public static class Handler
            implements net.bytebuddy.asm.Advice$AssignReturned$Handler {
                private final int value;
                private final int index;
                private final Assigner.Typing typing;

                protected Handler(int value, int index, Assigner.Typing typing) {
                    this.value = value;
                    this.index = index;
                    this.typing = typing;
                }

                public int getIndex() {
                    return this.index;
                }

                public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, TypeDescription.Generic type, StackManipulation value) {
                    if (instrumentedMethod.getParameters().size() < this.value) {
                        throw new IllegalStateException(instrumentedMethod + " declares less then " + this.value + " parameters");
                    }
                    StackManipulation assignment = assigner.assign(type, ((ParameterDescription)instrumentedMethod.getParameters().get(this.value)).getType(), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + type + " to " + ((ParameterDescription)instrumentedMethod.getParameters().get(this.value)).getType());
                    }
                    return new StackManipulation.Compound(value, assignment, MethodVariableAccess.of(((ParameterDescription)instrumentedMethod.getParameters().get(this.value)).getType()).storeAt(argumentHandler.argument(((ParameterDescription)instrumentedMethod.getParameters().get(this.value)).getOffset())));
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
                    if (this.value != ((Handler)object).value) {
                        return false;
                    }
                    if (this.index != ((Handler)object).index) {
                        return false;
                    }
                    return this.typing.equals((Object)((Handler)object).typing);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.value) * 31 + this.index) * 31 + this.typing.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.asm.Advice$AssignReturned$Handler$Factory<ToArguments>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape TO_ARGUMENTS_VALUE;
                    private static final MethodDescription.InDefinedShape TO_ARGUMENT_VALUE;
                    private static final MethodDescription.InDefinedShape TO_ARGUMENT_INDEX;
                    private static final MethodDescription.InDefinedShape TO_ARGUMENT_TYPING;

                    @Override
                    public Class<ToArguments> getAnnotationType() {
                        return ToArguments.class;
                    }

                    @Override
                    public List<net.bytebuddy.asm.Advice$AssignReturned$Handler> make(MethodDescription.InDefinedShape advice, boolean exit, AnnotationDescription.Loadable<? extends ToArguments> annotation) {
                        ArrayList<net.bytebuddy.asm.Advice$AssignReturned$Handler> handlers = new ArrayList<net.bytebuddy.asm.Advice$AssignReturned$Handler>();
                        for (AnnotationDescription argument : annotation.getValue(TO_ARGUMENTS_VALUE).resolve(AnnotationDescription[].class)) {
                            int value = argument.getValue(TO_ARGUMENT_VALUE).resolve(Integer.class);
                            if (value < 0) {
                                throw new IllegalStateException("An argument cannot have a negative index for " + advice);
                            }
                            handlers.add(new Handler(value, argument.getValue(TO_ARGUMENT_INDEX).resolve(Integer.class), argument.getValue(TO_ARGUMENT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class)));
                        }
                        return handlers;
                    }

                    static {
                        TO_ARGUMENTS_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(ToArguments.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(ToArgument.class).getDeclaredMethods();
                        TO_ARGUMENT_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                        TO_ARGUMENT_INDEX = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("index"))).getOnly();
                        TO_ARGUMENT_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    }
                }
            }

            @Target(value={})
            @RepeatedAnnotationPlugin.Enhance(value=ToArguments.class)
            @Repeatable(value=ToArguments.class)
            public static @interface ToArgument {
                public int value();

                public int index() default -1;

                public Assigner.Typing typing() default Assigner.Typing.STATIC;
            }
        }

        @Documented
        @Retention(value=RetentionPolicy.RUNTIME)
        @Target(value={ElementType.METHOD})
        public static @interface AsScalar {
            public boolean skipOnDefaultValue() default true;
        }
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Unused {
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface StubValue {
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Local {
        public String value();
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Exit {
        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Enter {
        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Origin {
        public static final String DEFAULT = "";

        public String value() default "";
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface FieldSetterHandle {
        public String value() default "";

        public Class<?> declaringType() default void.class;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface FieldGetterHandle {
        public String value() default "";

        public Class<?> declaringType() default void.class;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface FieldValue {
        public String value() default "";

        public Class<?> declaringType() default void.class;

        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface SelfCallHandle {
        public boolean bound() default true;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Thrown {
        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.DYNAMIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Return {
        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface AllArguments {
        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;

        public boolean includeSelf() default false;

        public boolean nullIfEmpty() default false;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Argument {
        public int value();

        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;

        public boolean optional() default false;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface This {
        public boolean readOnly() default true;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;

        public boolean optional() default false;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    public static @interface OnMethodExit {
        public Class<?> repeatOn() default void.class;

        public int repeatOnIndex() default -1;

        public Class<? extends Throwable> onThrowable() default NoExceptionHandler.class;

        public boolean backupArguments() default true;

        public boolean inline() default true;

        public Class<? extends Throwable> suppress() default NoExceptionHandler.class;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    public static @interface OnMethodEnter {
        public Class<?> skipOn() default void.class;

        public int skipOnIndex() default -1;

        public boolean prependLineNumber() default true;

        public boolean inline() default true;

        public Class<? extends Throwable> suppress() default NoExceptionHandler.class;
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final Advice advice;
        private final Implementation.Target implementationTarget;
        private final ByteCodeAppender delegate;

        protected Appender(Advice advice, Implementation.Target implementationTarget, ByteCodeAppender delegate) {
            this.advice = advice;
            this.implementationTarget = implementationTarget;
            this.delegate = delegate;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            EmulatingMethodVisitor emulatingMethodVisitor = new EmulatingMethodVisitor(methodVisitor, this.delegate);
            methodVisitor = this.advice.doWrap(this.implementationTarget.getInstrumentedType(), instrumentedMethod, emulatingMethodVisitor, implementationContext, 0, 0);
            return emulatingMethodVisitor.resolve(methodVisitor, implementationContext, instrumentedMethod);
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
            if (!this.advice.equals(((Appender)object).advice)) {
                return false;
            }
            if (!this.implementationTarget.equals(((Appender)object).implementationTarget)) {
                return false;
            }
            return this.delegate.equals(((Appender)object).delegate);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.advice.hashCode()) * 31 + this.implementationTarget.hashCode()) * 31 + this.delegate.hashCode();
        }

        protected static class EmulatingMethodVisitor
        extends MethodVisitor {
            private final ByteCodeAppender delegate;
            private int stackSize;
            private int localVariableLength;

            protected EmulatingMethodVisitor(MethodVisitor methodVisitor, ByteCodeAppender delegate) {
                super(OpenedClassReader.ASM_API, methodVisitor);
                this.delegate = delegate;
            }

            protected ByteCodeAppender.Size resolve(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                methodVisitor.visitCode();
                ByteCodeAppender.Size size = this.delegate.apply(methodVisitor, implementationContext, instrumentedMethod);
                methodVisitor.visitMaxs(size.getOperandStackSize(), size.getLocalVariableSize());
                methodVisitor.visitEnd();
                return new ByteCodeAppender.Size(this.stackSize, this.localVariableLength);
            }

            public void visitCode() {
            }

            public void visitMaxs(int stackSize, int localVariableLength) {
                this.stackSize = stackSize;
                this.localVariableLength = localVariableLength;
            }

            public void visitEnd() {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static abstract class AdviceVisitor
    extends ExceptionTableSensitiveMethodVisitor
    implements Dispatcher.RelocationHandler.Relocation {
        private static final int THIS_VARIABLE_INDEX = 0;
        private static final String THIS_VARIABLE_NAME = "this";
        protected final MethodDescription instrumentedMethod;
        private final Label preparationStart;
        private final Dispatcher.Bound methodEnter;
        protected final Dispatcher.Bound methodExit;
        protected final ArgumentHandler.ForInstrumentedMethod argumentHandler;
        protected final MethodSizeHandler.ForInstrumentedMethod methodSizeHandler;
        protected final StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler;

        @SuppressFBWarnings(value={"MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR"}, justification="Self reference is not used before constructor completion.")
        protected AdviceVisitor(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, StackManipulation exceptionHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Dispatcher.Resolved.ForMethodEnter methodEnter, Dispatcher.Resolved.ForMethodExit methodExit, List<? extends TypeDescription> postMethodTypes, int writerFlags, int readerFlags) {
            super(OpenedClassReader.ASM_API, methodVisitor);
            this.instrumentedMethod = instrumentedMethod;
            this.preparationStart = new Label();
            TreeMap<String, TypeDefinition> namedTypes = new TreeMap<String, TypeDefinition>();
            namedTypes.putAll(methodEnter.getNamedTypes());
            namedTypes.putAll(methodExit.getNamedTypes());
            this.argumentHandler = methodExit.getArgumentHandlerFactory().resolve(instrumentedMethod, methodEnter.getAdviceType(), methodExit.getAdviceType(), namedTypes);
            List<TypeDescription> initialTypes = CompoundList.of(methodExit.getAdviceType().represents(Void.TYPE) ? Collections.emptyList() : Collections.singletonList(methodExit.getAdviceType().asErasure()), this.argumentHandler.getNamedTypes());
            List latentTypes = methodEnter.getActualAdviceType().represents(Void.TYPE) ? Collections.emptyList() : Collections.singletonList(methodEnter.getActualAdviceType().asErasure());
            List preMethodTypes = methodEnter.getAdviceType().represents(Void.TYPE) ? Collections.emptyList() : Collections.singletonList(methodEnter.getAdviceType().asErasure());
            this.methodSizeHandler = MethodSizeHandler.Default.of(instrumentedMethod, initialTypes, preMethodTypes, postMethodTypes, this.argumentHandler.isCopyingArguments(), writerFlags);
            this.stackMapFrameHandler = StackMapFrameHandler.Default.of(instrumentedType, instrumentedMethod, initialTypes, latentTypes, preMethodTypes, postMethodTypes, methodExit.isAlive(), this.argumentHandler.isCopyingArguments(), implementationContext.getClassFileVersion(), writerFlags, readerFlags);
            this.methodEnter = methodEnter.bind(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, this.argumentHandler, this.methodSizeHandler, this.stackMapFrameHandler, exceptionHandler, this);
            this.methodExit = methodExit.bind(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, this.argumentHandler, this.methodSizeHandler, this.stackMapFrameHandler, exceptionHandler, new Dispatcher.RelocationHandler.Relocation.ForLabel(this.preparationStart));
        }

        @Override
        protected void onAfterExceptionTable() {
            this.methodEnter.prepare();
            this.onUserPrepare();
            this.methodExit.prepare();
            this.methodEnter.initialize();
            this.methodExit.initialize();
            this.stackMapFrameHandler.injectInitializationFrame(this.mv);
            this.methodEnter.apply();
            this.mv.visitLabel(this.preparationStart);
            this.methodSizeHandler.requireStackSize(this.argumentHandler.prepare(this.mv));
            this.stackMapFrameHandler.injectStartFrame(this.mv);
            this.mv.visitInsn(0);
            this.onUserStart();
        }

        protected abstract void onUserPrepare();

        protected abstract void onUserStart();

        @Override
        protected void onVisitVarInsn(int opcode, int offset) {
            this.mv.visitVarInsn(opcode, this.argumentHandler.argument(offset));
        }

        @Override
        protected void onVisitIincInsn(int offset, int increment) {
            this.mv.visitIincInsn(this.argumentHandler.argument(offset), increment);
        }

        @Override
        public void onVisitFrame(int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
            this.stackMapFrameHandler.translateFrame(this.mv, type, localVariableLength, localVariable, stackSize, stack);
        }

        @Override
        public void visitMaxs(int stackSize, int localVariableLength) {
            this.onUserEnd();
            this.mv.visitMaxs(this.methodSizeHandler.compoundStackSize(stackSize), this.methodSizeHandler.compoundLocalVariableLength(localVariableLength));
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int offset) {
            this.mv.visitLocalVariable(name, descriptor, signature, start, end, offset == 0 && THIS_VARIABLE_NAME.equals(name) ? offset : this.argumentHandler.argument(offset));
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeReference, TypePath typePath, Label[] start, Label[] end, int[] offset, String descriptor, boolean visible) {
            int[] translated = new int[offset.length];
            for (int index = 0; index < offset.length; ++index) {
                translated[index] = this.argumentHandler.argument(offset[index]);
            }
            return this.mv.visitLocalVariableAnnotation(typeReference, typePath, start, end, translated, descriptor, visible);
        }

        protected abstract void onUserEnd();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static abstract class WithExitAdvice
        extends AdviceVisitor {
            protected final Label returnHandler = new Label();

            protected WithExitAdvice(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, StackManipulation exceptionHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Dispatcher.Resolved.ForMethodEnter methodEnter, Dispatcher.Resolved.ForMethodExit methodExit, List<? extends TypeDescription> postMethodTypes, int writerFlags, int readerFlags) {
                super(StackAwareMethodVisitor.of(methodVisitor, instrumentedMethod), implementationContext, assigner, exceptionHandler, instrumentedType, instrumentedMethod, methodEnter, methodExit, postMethodTypes, writerFlags, readerFlags);
            }

            @Override
            public void apply(MethodVisitor methodVisitor) {
                if (this.instrumentedMethod.getReturnType().represents(Boolean.TYPE) || this.instrumentedMethod.getReturnType().represents(Byte.TYPE) || this.instrumentedMethod.getReturnType().represents(Short.TYPE) || this.instrumentedMethod.getReturnType().represents(Character.TYPE) || this.instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                    methodVisitor.visitInsn(3);
                } else if (this.instrumentedMethod.getReturnType().represents(Long.TYPE)) {
                    methodVisitor.visitInsn(9);
                } else if (this.instrumentedMethod.getReturnType().represents(Float.TYPE)) {
                    methodVisitor.visitInsn(11);
                } else if (this.instrumentedMethod.getReturnType().represents(Double.TYPE)) {
                    methodVisitor.visitInsn(14);
                } else if (!this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                    methodVisitor.visitInsn(1);
                }
                methodVisitor.visitJumpInsn(167, this.returnHandler);
            }

            @Override
            protected void onVisitInsn(int opcode) {
                switch (opcode) {
                    case 177: {
                        ((StackAwareMethodVisitor)this.mv).drainStack();
                        break;
                    }
                    case 172: {
                        this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(54, 21, StackSize.SINGLE));
                        break;
                    }
                    case 174: {
                        this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(56, 23, StackSize.SINGLE));
                        break;
                    }
                    case 175: {
                        this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(57, 24, StackSize.DOUBLE));
                        break;
                    }
                    case 173: {
                        this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(55, 22, StackSize.DOUBLE));
                        break;
                    }
                    case 176: {
                        this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(58, 25, StackSize.SINGLE));
                        break;
                    }
                    default: {
                        this.mv.visitInsn(opcode);
                        return;
                    }
                }
                this.mv.visitJumpInsn(167, this.returnHandler);
            }

            @Override
            protected void onUserEnd() {
                this.mv.visitLabel(this.returnHandler);
                this.onUserReturn();
                this.stackMapFrameHandler.injectCompletionFrame(this.mv);
                this.methodExit.apply();
                this.onExitAdviceReturn();
                if (this.instrumentedMethod.getReturnType().represents(Boolean.TYPE) || this.instrumentedMethod.getReturnType().represents(Byte.TYPE) || this.instrumentedMethod.getReturnType().represents(Short.TYPE) || this.instrumentedMethod.getReturnType().represents(Character.TYPE) || this.instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                    this.mv.visitVarInsn(21, this.argumentHandler.returned());
                    this.mv.visitInsn(172);
                } else if (this.instrumentedMethod.getReturnType().represents(Long.TYPE)) {
                    this.mv.visitVarInsn(22, this.argumentHandler.returned());
                    this.mv.visitInsn(173);
                } else if (this.instrumentedMethod.getReturnType().represents(Float.TYPE)) {
                    this.mv.visitVarInsn(23, this.argumentHandler.returned());
                    this.mv.visitInsn(174);
                } else if (this.instrumentedMethod.getReturnType().represents(Double.TYPE)) {
                    this.mv.visitVarInsn(24, this.argumentHandler.returned());
                    this.mv.visitInsn(175);
                } else if (!this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                    this.mv.visitVarInsn(25, this.argumentHandler.returned());
                    this.mv.visitInsn(176);
                } else {
                    this.mv.visitInsn(177);
                }
                this.methodSizeHandler.requireStackSize(this.instrumentedMethod.getReturnType().getStackSize().getSize());
            }

            protected abstract void onUserReturn();

            protected abstract void onExitAdviceReturn();

            protected static class WithExceptionHandling
            extends WithExitAdvice {
                private final TypeDescription throwable;
                private final Label exceptionHandler;
                protected final Label userStart;

                protected WithExceptionHandling(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, StackManipulation exceptionHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Dispatcher.Resolved.ForMethodEnter methodEnter, Dispatcher.Resolved.ForMethodExit methodExit, int writerFlags, int readerFlags, TypeDescription throwable) {
                    super(methodVisitor, implementationContext, assigner, exceptionHandler, instrumentedType, instrumentedMethod, methodEnter, methodExit, instrumentedMethod.getReturnType().represents(Void.TYPE) ? Collections.singletonList(TypeDescription.ForLoadedType.of(Throwable.class)) : Arrays.asList(instrumentedMethod.getReturnType().asErasure(), TypeDescription.ForLoadedType.of(Throwable.class)), writerFlags, readerFlags);
                    this.throwable = throwable;
                    this.exceptionHandler = new Label();
                    this.userStart = new Label();
                }

                protected void onUserPrepare() {
                    this.mv.visitTryCatchBlock(this.userStart, this.returnHandler, this.exceptionHandler, this.throwable.getInternalName());
                }

                protected void onUserStart() {
                    this.mv.visitLabel(this.userStart);
                }

                protected void onUserReturn() {
                    this.stackMapFrameHandler.injectReturnFrame(this.mv);
                    if (this.instrumentedMethod.getReturnType().represents(Boolean.TYPE) || this.instrumentedMethod.getReturnType().represents(Byte.TYPE) || this.instrumentedMethod.getReturnType().represents(Short.TYPE) || this.instrumentedMethod.getReturnType().represents(Character.TYPE) || this.instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                        this.mv.visitVarInsn(54, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Long.TYPE)) {
                        this.mv.visitVarInsn(55, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Float.TYPE)) {
                        this.mv.visitVarInsn(56, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Double.TYPE)) {
                        this.mv.visitVarInsn(57, this.argumentHandler.returned());
                    } else if (!this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                        this.mv.visitVarInsn(58, this.argumentHandler.returned());
                    }
                    this.mv.visitInsn(1);
                    this.mv.visitVarInsn(58, this.argumentHandler.thrown());
                    Label endOfHandler = new Label();
                    this.mv.visitJumpInsn(167, endOfHandler);
                    this.mv.visitLabel(this.exceptionHandler);
                    this.stackMapFrameHandler.injectExceptionFrame(this.mv);
                    this.mv.visitVarInsn(58, this.argumentHandler.thrown());
                    if (this.instrumentedMethod.getReturnType().represents(Boolean.TYPE) || this.instrumentedMethod.getReturnType().represents(Byte.TYPE) || this.instrumentedMethod.getReturnType().represents(Short.TYPE) || this.instrumentedMethod.getReturnType().represents(Character.TYPE) || this.instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                        this.mv.visitInsn(3);
                        this.mv.visitVarInsn(54, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Long.TYPE)) {
                        this.mv.visitInsn(9);
                        this.mv.visitVarInsn(55, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Float.TYPE)) {
                        this.mv.visitInsn(11);
                        this.mv.visitVarInsn(56, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Double.TYPE)) {
                        this.mv.visitInsn(14);
                        this.mv.visitVarInsn(57, this.argumentHandler.returned());
                    } else if (!this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                        this.mv.visitInsn(1);
                        this.mv.visitVarInsn(58, this.argumentHandler.returned());
                    }
                    this.mv.visitLabel(endOfHandler);
                    this.methodSizeHandler.requireStackSize(StackSize.SINGLE.getSize());
                }

                protected void onExitAdviceReturn() {
                    this.mv.visitVarInsn(25, this.argumentHandler.thrown());
                    Label endOfHandler = new Label();
                    this.mv.visitJumpInsn(198, endOfHandler);
                    this.mv.visitVarInsn(25, this.argumentHandler.thrown());
                    this.mv.visitInsn(191);
                    this.mv.visitLabel(endOfHandler);
                    this.stackMapFrameHandler.injectPostCompletionFrame(this.mv);
                }
            }

            protected static class WithoutExceptionHandling
            extends WithExitAdvice {
                protected WithoutExceptionHandling(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, StackManipulation exceptionHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Dispatcher.Resolved.ForMethodEnter methodEnter, Dispatcher.Resolved.ForMethodExit methodExit, int writerFlags, int readerFlags) {
                    super(methodVisitor, implementationContext, assigner, exceptionHandler, instrumentedType, instrumentedMethod, methodEnter, methodExit, instrumentedMethod.getReturnType().represents(Void.TYPE) ? Collections.emptyList() : Collections.singletonList(instrumentedMethod.getReturnType().asErasure()), writerFlags, readerFlags);
                }

                protected void onUserPrepare() {
                }

                protected void onUserStart() {
                }

                protected void onUserReturn() {
                    if (this.instrumentedMethod.getReturnType().represents(Boolean.TYPE) || this.instrumentedMethod.getReturnType().represents(Byte.TYPE) || this.instrumentedMethod.getReturnType().represents(Short.TYPE) || this.instrumentedMethod.getReturnType().represents(Character.TYPE) || this.instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.mv);
                        this.mv.visitVarInsn(54, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Long.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.mv);
                        this.mv.visitVarInsn(55, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Float.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.mv);
                        this.mv.visitVarInsn(56, this.argumentHandler.returned());
                    } else if (this.instrumentedMethod.getReturnType().represents(Double.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.mv);
                        this.mv.visitVarInsn(57, this.argumentHandler.returned());
                    } else if (!this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.mv);
                        this.mv.visitVarInsn(58, this.argumentHandler.returned());
                    }
                }

                protected void onExitAdviceReturn() {
                }
            }
        }

        protected static class WithoutExitAdvice
        extends AdviceVisitor {
            protected WithoutExitAdvice(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, StackManipulation exceptionHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Dispatcher.Resolved.ForMethodEnter methodEnter, int writerFlags, int readerFlags) {
                super(methodVisitor, implementationContext, assigner, exceptionHandler, instrumentedType, instrumentedMethod, methodEnter, Dispatcher.Inactive.INSTANCE, Collections.emptyList(), writerFlags, readerFlags);
            }

            public void apply(MethodVisitor methodVisitor) {
                if (this.instrumentedMethod.getReturnType().represents(Boolean.TYPE) || this.instrumentedMethod.getReturnType().represents(Byte.TYPE) || this.instrumentedMethod.getReturnType().represents(Short.TYPE) || this.instrumentedMethod.getReturnType().represents(Character.TYPE) || this.instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitInsn(172);
                } else if (this.instrumentedMethod.getReturnType().represents(Long.TYPE)) {
                    methodVisitor.visitInsn(9);
                    methodVisitor.visitInsn(173);
                } else if (this.instrumentedMethod.getReturnType().represents(Float.TYPE)) {
                    methodVisitor.visitInsn(11);
                    methodVisitor.visitInsn(174);
                } else if (this.instrumentedMethod.getReturnType().represents(Double.TYPE)) {
                    methodVisitor.visitInsn(14);
                    methodVisitor.visitInsn(175);
                } else if (this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                    methodVisitor.visitInsn(177);
                } else {
                    methodVisitor.visitInsn(1);
                    methodVisitor.visitInsn(176);
                }
            }

            protected void onUserPrepare() {
            }

            protected void onUserStart() {
            }

            protected void onUserEnd() {
            }
        }
    }

    protected static interface Dispatcher {
        @AlwaysNull
        public static final MethodVisitor IGNORE_METHOD = null;
        @AlwaysNull
        public static final AnnotationVisitor IGNORE_ANNOTATION = null;

        public boolean isAlive();

        public TypeDefinition getAdviceType();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Delegating
        implements Unresolved {
            protected final MethodDescription.InDefinedShape adviceMethod;
            protected final Delegator.Factory delegatorFactory;

            protected Delegating(MethodDescription.InDefinedShape adviceMethod, Delegator.Factory delegatorFactory) {
                this.adviceMethod = adviceMethod;
                this.delegatorFactory = delegatorFactory;
            }

            @Override
            public boolean isAlive() {
                return true;
            }

            @Override
            public boolean isBinary() {
                return false;
            }

            @Override
            public TypeDescription getAdviceType() {
                return this.adviceMethod.getReturnType().asErasure();
            }

            @Override
            public Map<String, TypeDefinition> getNamedTypes() {
                return Collections.emptyMap();
            }

            @Override
            public net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodEnter asMethodEnter(List<? extends OffsetMapping.Factory<?>> userFactories, @MaybeNull ClassReader classReader, Unresolved methodExit, PostProcessor.Factory postProcessorFactory) {
                return Resolved.ForMethodEnter.of(this.adviceMethod, postProcessorFactory.make(this.adviceMethod, false), this.delegatorFactory.make(this.adviceMethod, false), userFactories, methodExit.getAdviceType(), methodExit.isAlive());
            }

            @Override
            public net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodExit asMethodExit(List<? extends OffsetMapping.Factory<?>> userFactories, @MaybeNull ClassReader classReader, Unresolved methodEnter, PostProcessor.Factory postProcessorFactory) {
                Map<String, TypeDefinition> namedTypes = methodEnter.getNamedTypes();
                for (ParameterDescription parameterDescription : this.adviceMethod.getParameters()) {
                    AnnotationDescription.Loadable<Local> annotationDescription = parameterDescription.getDeclaredAnnotations().ofType(Local.class);
                    if (annotationDescription == null) continue;
                    String name = annotationDescription.getValue(OffsetMapping.ForLocalValue.Factory.LOCAL_VALUE).resolve(String.class);
                    TypeDefinition typeDefinition = namedTypes.get(name);
                    if (typeDefinition == null) {
                        throw new IllegalStateException(this.adviceMethod + " attempts use of undeclared local variable " + name);
                    }
                    if (typeDefinition.equals(parameterDescription.getType())) continue;
                    throw new IllegalStateException(this.adviceMethod + " does not read variable " + name + " as " + typeDefinition);
                }
                return Resolved.ForMethodExit.of(this.adviceMethod, postProcessorFactory.make(this.adviceMethod, true), this.delegatorFactory.make(this.adviceMethod, true), namedTypes, userFactories, methodEnter.getAdviceType());
            }

            public String toString() {
                return "Delegate to " + this.adviceMethod;
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
                if (!this.adviceMethod.equals(((Delegating)object).adviceMethod)) {
                    return false;
                }
                return this.delegatorFactory.equals(((Delegating)object).delegatorFactory);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.adviceMethod.hashCode()) * 31 + this.delegatorFactory.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class Resolved
            extends Resolved.AbstractBase {
                protected final Delegator delegator;

                protected Resolved(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, List<? extends OffsetMapping.Factory<?>> factories, TypeDescription throwableType, TypeDescription relocatableType, int relocatableIndex, Delegator delegator) {
                    super(adviceMethod, postProcessor, factories, throwableType, relocatableType, relocatableIndex, OffsetMapping.Factory.AdviceType.DELEGATION);
                    this.delegator = delegator;
                }

                @Override
                public Map<String, TypeDefinition> getNamedTypes() {
                    return Collections.emptyMap();
                }

                @Override
                public Bound bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, StackManipulation exceptionHandler, RelocationHandler.Relocation relocation) {
                    if (!this.adviceMethod.isVisibleTo(instrumentedType)) {
                        throw new IllegalStateException(this.adviceMethod + " is not visible to " + instrumentedMethod.getDeclaringType());
                    }
                    return this.resolve(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, argumentHandler, methodSizeHandler, stackMapFrameHandler, exceptionHandler, relocation);
                }

                protected abstract Bound resolve(TypeDescription var1, MethodDescription var2, MethodVisitor var3, Implementation.Context var4, Assigner var5, ArgumentHandler.ForInstrumentedMethod var6, MethodSizeHandler.ForInstrumentedMethod var7, StackMapFrameHandler.ForInstrumentedMethod var8, StackManipulation var9, RelocationHandler.Relocation var10);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static abstract class ForMethodExit
                extends Resolved
                implements net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodExit {
                    private final boolean backupArguments;

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    protected ForMethodExit(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition enterType, Delegator delegator) {
                        super(adviceMethod, postProcessor, CompoundList.of(Arrays.asList(OffsetMapping.ForArgument.Unresolved.Factory.INSTANCE, OffsetMapping.ForAllArguments.Factory.INSTANCE, OffsetMapping.ForThisReference.Factory.INSTANCE, OffsetMapping.ForField.Unresolved.Factory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.ReaderFactory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.WriterFactory.INSTANCE, OffsetMapping.ForOrigin.Factory.INSTANCE, OffsetMapping.ForSelfCallHandle.Factory.INSTANCE, OffsetMapping.ForUnusedValue.Factory.INSTANCE, OffsetMapping.ForStubValue.INSTANCE, OffsetMapping.ForEnterValue.Factory.of(enterType), OffsetMapping.ForExitValue.Factory.of(adviceMethod.getReturnType()), new OffsetMapping.ForLocalValue.Factory(namedTypes), OffsetMapping.ForReturnValue.Factory.INSTANCE, OffsetMapping.ForThrowable.Factory.of(adviceMethod)), userFactories), adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(SUPPRESS_EXIT).resolve(TypeDescription.class), adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(REPEAT_ON).resolve(TypeDescription.class), (int)adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(REPEAT_ON_INDEX).resolve(Integer.class), delegator);
                        this.backupArguments = adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(BACKUP_ARGUMENTS).resolve(Boolean.class);
                    }

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    protected static net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodExit of(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Delegator delegator, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition enterType) {
                        TypeDescription throwable = adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(ON_THROWABLE).resolve(TypeDescription.class);
                        return throwable.represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class)) ? new WithoutExceptionHandler(adviceMethod, postProcessor, namedTypes, userFactories, enterType, delegator) : new WithExceptionHandler(adviceMethod, postProcessor, namedTypes, userFactories, enterType, throwable, delegator);
                    }

                    @Override
                    protected Bound resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, StackManipulation exceptionHandler, RelocationHandler.Relocation relocation) {
                        return this.doResolve(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, argumentHandler.bindExit(this.adviceMethod, this.getThrowable().represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class))), methodSizeHandler.bindExit(this.adviceMethod), stackMapFrameHandler.bindExit(this.adviceMethod), this.suppressionHandler.bind(exceptionHandler), this.relocationHandler.bind(instrumentedMethod, relocation), exceptionHandler);
                    }

                    private Bound doResolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                        ArrayList<OffsetMapping.Target> offsetMappings = new ArrayList<OffsetMapping.Target>(this.offsetMappings.size());
                        for (OffsetMapping offsetMapping : this.offsetMappings.values()) {
                            offsetMappings.add(offsetMapping.resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, OffsetMapping.Sort.EXIT));
                        }
                        return new AdviceMethodWriter.ForMethodExit(this.adviceMethod, instrumentedType, instrumentedMethod, assigner, this.postProcessor, offsetMappings, methodVisitor, implementationContext, argumentHandler, methodSizeHandler, stackMapFrameHandler, suppressionHandler, relocationHandler, exceptionHandler, this.delegator);
                    }

                    @Override
                    public ArgumentHandler.Factory getArgumentHandlerFactory() {
                        return this.backupArguments ? ArgumentHandler.Factory.COPYING : ArgumentHandler.Factory.SIMPLE;
                    }

                    @Override
                    public TypeDefinition getAdviceType() {
                        return this.adviceMethod.getReturnType();
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
                        return this.backupArguments == ((ForMethodExit)object).backupArguments;
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode() * 31 + this.backupArguments;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class WithoutExceptionHandler
                    extends ForMethodExit {
                        protected WithoutExceptionHandler(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition enterType, Delegator delegator) {
                            super(adviceMethod, postProcessor, namedTypes, userFactories, enterType, delegator);
                        }

                        @Override
                        public TypeDescription getThrowable() {
                            return NoExceptionHandler.DESCRIPTION;
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class WithExceptionHandler
                    extends ForMethodExit {
                        private final TypeDescription throwable;

                        protected WithExceptionHandler(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition enterType, TypeDescription throwable, Delegator delegator) {
                            super(adviceMethod, postProcessor, namedTypes, userFactories, enterType, delegator);
                            this.throwable = throwable;
                        }

                        @Override
                        public TypeDescription getThrowable() {
                            return this.throwable;
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
                            return this.throwable.equals(((WithExceptionHandler)object).throwable);
                        }

                        @Override
                        public int hashCode() {
                            return super.hashCode() * 31 + this.throwable.hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static abstract class ForMethodEnter
                extends Resolved
                implements net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodEnter {
                    private final boolean prependLineNumber;

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    protected ForMethodEnter(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, Delegator delegator) {
                        super(adviceMethod, postProcessor, CompoundList.of(Arrays.asList(OffsetMapping.ForArgument.Unresolved.Factory.INSTANCE, OffsetMapping.ForAllArguments.Factory.INSTANCE, OffsetMapping.ForThisReference.Factory.INSTANCE, OffsetMapping.ForField.Unresolved.Factory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.ReaderFactory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.WriterFactory.INSTANCE, OffsetMapping.ForOrigin.Factory.INSTANCE, OffsetMapping.ForSelfCallHandle.Factory.INSTANCE, OffsetMapping.ForUnusedValue.Factory.INSTANCE, OffsetMapping.ForStubValue.INSTANCE, OffsetMapping.ForExitValue.Factory.of(exitType), new OffsetMapping.Factory.Illegal<Thrown>(Thrown.class), new OffsetMapping.Factory.Illegal<Enter>(Enter.class), new OffsetMapping.Factory.Illegal<Local>(Local.class), new OffsetMapping.Factory.Illegal<Return>(Return.class)), userFactories), adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(SUPPRESS_ENTER).resolve(TypeDescription.class), adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(SKIP_ON).resolve(TypeDescription.class), (int)adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(SKIP_ON_INDEX).resolve(Integer.class), delegator);
                        this.prependLineNumber = adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(PREPEND_LINE_NUMBER).resolve(Boolean.class);
                    }

                    protected static net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodEnter of(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Delegator delegator, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, boolean methodExit) {
                        return methodExit ? new WithRetainedEnterType(adviceMethod, postProcessor, userFactories, exitType, delegator) : new WithDiscardedEnterType(adviceMethod, postProcessor, userFactories, exitType, delegator);
                    }

                    @Override
                    public boolean isPrependLineNumber() {
                        return this.prependLineNumber;
                    }

                    @Override
                    public TypeDefinition getActualAdviceType() {
                        return this.adviceMethod.getReturnType();
                    }

                    @Override
                    protected Bound resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, StackManipulation exceptionHandler, RelocationHandler.Relocation relocation) {
                        return this.doResolve(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, argumentHandler.bindEnter(this.adviceMethod), methodSizeHandler.bindEnter(this.adviceMethod), stackMapFrameHandler.bindEnter(this.adviceMethod), this.suppressionHandler.bind(exceptionHandler), this.relocationHandler.bind(instrumentedMethod, relocation), exceptionHandler);
                    }

                    protected Bound doResolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                        ArrayList<OffsetMapping.Target> offsetMappings = new ArrayList<OffsetMapping.Target>(this.offsetMappings.size());
                        for (OffsetMapping offsetMapping : this.offsetMappings.values()) {
                            offsetMappings.add(offsetMapping.resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, OffsetMapping.Sort.ENTER));
                        }
                        return new AdviceMethodWriter.ForMethodEnter(this.adviceMethod, instrumentedType, instrumentedMethod, assigner, this.postProcessor, offsetMappings, methodVisitor, implementationContext, argumentHandler, methodSizeHandler, stackMapFrameHandler, suppressionHandler, relocationHandler, exceptionHandler, this.delegator);
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
                        return this.prependLineNumber == ((ForMethodEnter)object).prependLineNumber;
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode() * 31 + this.prependLineNumber;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class WithDiscardedEnterType
                    extends ForMethodEnter {
                        protected WithDiscardedEnterType(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, Delegator delegator) {
                            super(adviceMethod, postProcessor, userFactories, exitType, delegator);
                        }

                        @Override
                        public TypeDefinition getAdviceType() {
                            return TypeDescription.ForLoadedType.of(Void.TYPE);
                        }

                        @Override
                        protected Bound doResolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                            methodSizeHandler.requireLocalVariableLengthPadding(this.adviceMethod.getReturnType().getStackSize().getSize());
                            return super.doResolve(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, argumentHandler, methodSizeHandler, stackMapFrameHandler, suppressionHandler, relocationHandler, exceptionHandler);
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class WithRetainedEnterType
                    extends ForMethodEnter {
                        protected WithRetainedEnterType(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, Delegator delegator) {
                            super(adviceMethod, postProcessor, userFactories, exitType, delegator);
                        }

                        @Override
                        public TypeDefinition getAdviceType() {
                            return this.adviceMethod.getReturnType();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static abstract class AdviceMethodWriter
                implements Bound {
                    protected final MethodDescription.InDefinedShape adviceMethod;
                    private final TypeDescription instrumentedType;
                    private final MethodDescription instrumentedMethod;
                    private final Assigner assigner;
                    private final List<OffsetMapping.Target> offsetMappings;
                    protected final MethodVisitor methodVisitor;
                    protected final Implementation.Context implementationContext;
                    protected final ArgumentHandler.ForAdvice argumentHandler;
                    protected final MethodSizeHandler.ForAdvice methodSizeHandler;
                    protected final StackMapFrameHandler.ForAdvice stackMapFrameHandler;
                    private final SuppressionHandler.Bound suppressionHandler;
                    private final RelocationHandler.Bound relocationHandler;
                    private final StackManipulation exceptionHandler;
                    private final PostProcessor postProcessor;
                    private final Delegator delegator;

                    protected AdviceMethodWriter(MethodDescription.InDefinedShape adviceMethod, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, PostProcessor postProcessor, List<OffsetMapping.Target> offsetMappings, MethodVisitor methodVisitor, Implementation.Context implementationContext, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler, Delegator delegator) {
                        this.adviceMethod = adviceMethod;
                        this.instrumentedType = instrumentedType;
                        this.instrumentedMethod = instrumentedMethod;
                        this.assigner = assigner;
                        this.postProcessor = postProcessor;
                        this.offsetMappings = offsetMappings;
                        this.methodVisitor = methodVisitor;
                        this.implementationContext = implementationContext;
                        this.argumentHandler = argumentHandler;
                        this.methodSizeHandler = methodSizeHandler;
                        this.stackMapFrameHandler = stackMapFrameHandler;
                        this.suppressionHandler = suppressionHandler;
                        this.relocationHandler = relocationHandler;
                        this.exceptionHandler = exceptionHandler;
                        this.delegator = delegator;
                    }

                    @Override
                    public void prepare() {
                        this.suppressionHandler.onPrepare(this.methodVisitor);
                    }

                    @Override
                    public void apply() {
                        this.suppressionHandler.onStart(this.methodVisitor);
                        int index = 0;
                        int currentStackSize = 0;
                        int maximumStackSize = 0;
                        for (OffsetMapping.Target offsetMapping : this.offsetMappings) {
                            maximumStackSize = Math.max(maximumStackSize, (currentStackSize += ((ParameterDescription.InDefinedShape)this.adviceMethod.getParameters().get(index++)).getType().getStackSize().getSize()) + offsetMapping.resolveRead().apply(this.methodVisitor, this.implementationContext).getMaximalSize());
                        }
                        maximumStackSize = Math.max(maximumStackSize, this.delegator.apply(this.instrumentedType, this.instrumentedMethod).apply(this.methodVisitor, this.implementationContext).getMaximalSize());
                        this.suppressionHandler.onEndWithSkip(this.methodVisitor, this.implementationContext, this.methodSizeHandler, this.stackMapFrameHandler, this.adviceMethod.getReturnType());
                        if (this.adviceMethod.getReturnType().represents(Boolean.TYPE) || this.adviceMethod.getReturnType().represents(Byte.TYPE) || this.adviceMethod.getReturnType().represents(Short.TYPE) || this.adviceMethod.getReturnType().represents(Character.TYPE) || this.adviceMethod.getReturnType().represents(Integer.TYPE)) {
                            this.methodVisitor.visitVarInsn(54, this.isExitAdvice() ? this.argumentHandler.exit() : this.argumentHandler.enter());
                        } else if (this.adviceMethod.getReturnType().represents(Long.TYPE)) {
                            this.methodVisitor.visitVarInsn(55, this.isExitAdvice() ? this.argumentHandler.exit() : this.argumentHandler.enter());
                        } else if (this.adviceMethod.getReturnType().represents(Float.TYPE)) {
                            this.methodVisitor.visitVarInsn(56, this.isExitAdvice() ? this.argumentHandler.exit() : this.argumentHandler.enter());
                        } else if (this.adviceMethod.getReturnType().represents(Double.TYPE)) {
                            this.methodVisitor.visitVarInsn(57, this.isExitAdvice() ? this.argumentHandler.exit() : this.argumentHandler.enter());
                        } else if (!this.adviceMethod.getReturnType().represents(Void.TYPE)) {
                            this.methodVisitor.visitVarInsn(58, this.isExitAdvice() ? this.argumentHandler.exit() : this.argumentHandler.enter());
                        }
                        this.methodSizeHandler.requireStackSize(this.postProcessor.resolve(this.instrumentedType, this.instrumentedMethod, this.assigner, this.argumentHandler, this.stackMapFrameHandler, this.exceptionHandler).apply(this.methodVisitor, this.implementationContext).getMaximalSize());
                        this.methodSizeHandler.requireStackSize(this.relocationHandler.apply(this.methodVisitor, this.implementationContext, this.isExitAdvice() ? this.argumentHandler.exit() : this.argumentHandler.enter()));
                        this.stackMapFrameHandler.injectCompletionFrame(this.methodVisitor);
                        this.methodSizeHandler.requireStackSize(Math.max(maximumStackSize, this.adviceMethod.getReturnType().getStackSize().getSize()));
                        this.methodSizeHandler.requireLocalVariableLength(this.instrumentedMethod.getStackSize() + this.adviceMethod.getReturnType().getStackSize().getSize());
                    }

                    protected abstract boolean isExitAdvice();

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class ForMethodExit
                    extends AdviceMethodWriter {
                        protected ForMethodExit(MethodDescription.InDefinedShape adviceMethod, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, PostProcessor postProcessor, List<OffsetMapping.Target> offsetMappings, MethodVisitor methodVisitor, Implementation.Context implementationContext, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler, Delegator delegator) {
                            super(adviceMethod, instrumentedType, instrumentedMethod, assigner, postProcessor, offsetMappings, methodVisitor, implementationContext, argumentHandler, methodSizeHandler, stackMapFrameHandler, suppressionHandler, relocationHandler, exceptionHandler, delegator);
                        }

                        @Override
                        public void initialize() {
                            if (this.adviceMethod.getReturnType().represents(Boolean.TYPE) || this.adviceMethod.getReturnType().represents(Byte.TYPE) || this.adviceMethod.getReturnType().represents(Short.TYPE) || this.adviceMethod.getReturnType().represents(Character.TYPE) || this.adviceMethod.getReturnType().represents(Integer.TYPE)) {
                                this.methodVisitor.visitInsn(3);
                                this.methodVisitor.visitVarInsn(54, this.argumentHandler.exit());
                            } else if (this.adviceMethod.getReturnType().represents(Long.TYPE)) {
                                this.methodVisitor.visitInsn(9);
                                this.methodVisitor.visitVarInsn(55, this.argumentHandler.exit());
                            } else if (this.adviceMethod.getReturnType().represents(Float.TYPE)) {
                                this.methodVisitor.visitInsn(11);
                                this.methodVisitor.visitVarInsn(56, this.argumentHandler.exit());
                            } else if (this.adviceMethod.getReturnType().represents(Double.TYPE)) {
                                this.methodVisitor.visitInsn(14);
                                this.methodVisitor.visitVarInsn(57, this.argumentHandler.exit());
                            } else if (!this.adviceMethod.getReturnType().represents(Void.TYPE)) {
                                this.methodVisitor.visitInsn(1);
                                this.methodVisitor.visitVarInsn(58, this.argumentHandler.exit());
                            }
                            this.methodSizeHandler.requireStackSize(this.adviceMethod.getReturnType().getStackSize().getSize());
                        }

                        @Override
                        protected boolean isExitAdvice() {
                            return true;
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class ForMethodEnter
                    extends AdviceMethodWriter {
                        protected ForMethodEnter(MethodDescription.InDefinedShape adviceMethod, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, PostProcessor postProcessor, List<OffsetMapping.Target> offsetMappings, MethodVisitor methodVisitor, Implementation.Context implementationContext, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler, Delegator delegator) {
                            super(adviceMethod, instrumentedType, instrumentedMethod, assigner, postProcessor, offsetMappings, methodVisitor, implementationContext, argumentHandler, methodSizeHandler, stackMapFrameHandler, suppressionHandler, relocationHandler, exceptionHandler, delegator);
                        }

                        @Override
                        public void initialize() {
                        }

                        @Override
                        protected boolean isExitAdvice() {
                            return false;
                        }
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Inlining
        implements Unresolved {
            protected final MethodDescription.InDefinedShape adviceMethod;
            private final Map<String, TypeDefinition> namedTypes;

            protected Inlining(MethodDescription.InDefinedShape adviceMethod) {
                this.adviceMethod = adviceMethod;
                this.namedTypes = new HashMap<String, TypeDefinition>();
                for (ParameterDescription parameterDescription : adviceMethod.getParameters()) {
                    String name;
                    TypeDefinition previous;
                    AnnotationDescription.Loadable<Local> annotationDescription = parameterDescription.getDeclaredAnnotations().ofType(Local.class);
                    if (annotationDescription == null || (previous = this.namedTypes.put(name = annotationDescription.getValue(OffsetMapping.ForLocalValue.Factory.LOCAL_VALUE).resolve(String.class), parameterDescription.getType())) == null || previous.equals(parameterDescription.getType())) continue;
                    throw new IllegalStateException("Local variable for " + name + " is defined with inconsistent types");
                }
            }

            @Override
            public boolean isAlive() {
                return true;
            }

            @Override
            public boolean isBinary() {
                return true;
            }

            @Override
            public TypeDescription getAdviceType() {
                return this.adviceMethod.getReturnType().asErasure();
            }

            @Override
            public Map<String, TypeDefinition> getNamedTypes() {
                return this.namedTypes;
            }

            @Override
            public net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodEnter asMethodEnter(List<? extends OffsetMapping.Factory<?>> userFactories, @MaybeNull ClassReader classReader, Unresolved methodExit, PostProcessor.Factory postProcessorFactory) {
                if (classReader == null) {
                    throw new IllegalStateException("Class reader not expected null");
                }
                return Resolved.ForMethodEnter.of(this.adviceMethod, postProcessorFactory.make(this.adviceMethod, false), this.namedTypes, userFactories, methodExit.getAdviceType(), classReader, methodExit.isAlive());
            }

            @Override
            public net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodExit asMethodExit(List<? extends OffsetMapping.Factory<?>> userFactories, @MaybeNull ClassReader classReader, Unresolved methodEnter, PostProcessor.Factory postProcessorFactory) {
                HashMap<String, TypeDefinition> namedTypes = new HashMap<String, TypeDefinition>(methodEnter.getNamedTypes());
                HashMap<String, TypeDefinition> uninitializedNamedTypes = new HashMap<String, TypeDefinition>();
                for (Map.Entry<String, TypeDefinition> entry : this.namedTypes.entrySet()) {
                    TypeDefinition typeDefinition = (TypeDefinition)namedTypes.get(entry.getKey());
                    TypeDefinition uninitializedTypeDefinition = (TypeDefinition)uninitializedNamedTypes.get(entry.getKey());
                    if (typeDefinition == null && uninitializedTypeDefinition == null) {
                        namedTypes.put(entry.getKey(), entry.getValue());
                        uninitializedNamedTypes.put(entry.getKey(), entry.getValue());
                        continue;
                    }
                    if ((typeDefinition == null ? uninitializedTypeDefinition : typeDefinition).equals(entry.getValue())) continue;
                    throw new IllegalStateException("Local variable for " + entry.getKey() + " is defined with inconsistent types");
                }
                return Resolved.ForMethodExit.of(this.adviceMethod, postProcessorFactory.make(this.adviceMethod, true), namedTypes, uninitializedNamedTypes, userFactories, classReader, methodEnter.getAdviceType());
            }

            public String toString() {
                return "Delegate to " + this.adviceMethod;
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
                if (!this.adviceMethod.equals(((Inlining)object).adviceMethod)) {
                    return false;
                }
                return ((Object)this.namedTypes).equals(((Inlining)object).namedTypes);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.adviceMethod.hashCode()) * 31 + ((Object)this.namedTypes).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class CodeTranslationVisitor
            extends MethodVisitor {
                protected final MethodVisitor methodVisitor;
                protected final Implementation.Context implementationContext;
                protected final ArgumentHandler.ForAdvice argumentHandler;
                protected final MethodSizeHandler.ForAdvice methodSizeHandler;
                protected final StackMapFrameHandler.ForAdvice stackMapFrameHandler;
                private final TypeDescription instrumentedType;
                private final MethodDescription instrumentedMethod;
                private final Assigner assigner;
                protected final MethodDescription.InDefinedShape adviceMethod;
                private final Map<Integer, OffsetMapping.Target> offsetMappings;
                private final SuppressionHandler.Bound suppressionHandler;
                private final RelocationHandler.Bound relocationHandler;
                private final StackManipulation exceptionHandler;
                private final PostProcessor postProcessor;
                private final boolean exit;
                protected final Label endOfMethod;

                protected CodeTranslationVisitor(MethodVisitor methodVisitor, Implementation.Context implementationContext, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, MethodDescription.InDefinedShape adviceMethod, Map<Integer, OffsetMapping.Target> offsetMappings, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler, PostProcessor postProcessor, boolean exit) {
                    super(OpenedClassReader.ASM_API, StackAwareMethodVisitor.of(methodVisitor, instrumentedMethod));
                    this.methodVisitor = methodVisitor;
                    this.implementationContext = implementationContext;
                    this.argumentHandler = argumentHandler;
                    this.methodSizeHandler = methodSizeHandler;
                    this.stackMapFrameHandler = stackMapFrameHandler;
                    this.instrumentedType = instrumentedType;
                    this.instrumentedMethod = instrumentedMethod;
                    this.assigner = assigner;
                    this.adviceMethod = adviceMethod;
                    this.offsetMappings = offsetMappings;
                    this.suppressionHandler = suppressionHandler;
                    this.relocationHandler = relocationHandler;
                    this.exceptionHandler = exceptionHandler;
                    this.postProcessor = postProcessor;
                    this.exit = exit;
                    this.endOfMethod = new Label();
                }

                protected void propagateHandler(Label label) {
                    ((StackAwareMethodVisitor)this.mv).register(label, Collections.singletonList(StackSize.SINGLE));
                }

                @Override
                public void visitParameter(String name, int modifiers) {
                }

                @Override
                public void visitAnnotableParameterCount(int count, boolean visible) {
                }

                @Override
                @MaybeNull
                public AnnotationVisitor visitAnnotationDefault() {
                    return IGNORE_ANNOTATION;
                }

                @Override
                @MaybeNull
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return IGNORE_ANNOTATION;
                }

                @Override
                @MaybeNull
                public AnnotationVisitor visitTypeAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                    return IGNORE_ANNOTATION;
                }

                @Override
                @MaybeNull
                public AnnotationVisitor visitParameterAnnotation(int index, String descriptor, boolean visible) {
                    return IGNORE_ANNOTATION;
                }

                @Override
                public void visitAttribute(Attribute attribute) {
                }

                @Override
                public void visitCode() {
                    this.suppressionHandler.onStart(this.methodVisitor);
                }

                @Override
                public void visitFrame(int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                    this.stackMapFrameHandler.translateFrame(this.methodVisitor, type, localVariableLength, localVariable, stackSize, stack);
                }

                @Override
                public void visitVarInsn(int opcode, int offset) {
                    OffsetMapping.Target target = this.offsetMappings.get(offset);
                    if (target != null) {
                        StackSize expectedGrowth;
                        StackManipulation stackManipulation;
                        switch (opcode) {
                            case 21: 
                            case 23: 
                            case 25: {
                                stackManipulation = target.resolveRead();
                                expectedGrowth = StackSize.SINGLE;
                                break;
                            }
                            case 22: 
                            case 24: {
                                stackManipulation = target.resolveRead();
                                expectedGrowth = StackSize.DOUBLE;
                                break;
                            }
                            case 54: 
                            case 55: 
                            case 56: 
                            case 57: 
                            case 58: {
                                stackManipulation = target.resolveWrite();
                                expectedGrowth = StackSize.ZERO;
                                break;
                            }
                            default: {
                                throw new IllegalStateException("Unexpected opcode: " + opcode);
                            }
                        }
                        this.methodSizeHandler.requireStackSizePadding(stackManipulation.apply(this.mv, this.implementationContext).getMaximalSize() - expectedGrowth.getSize());
                    } else {
                        this.mv.visitVarInsn(opcode, this.argumentHandler.mapped(offset));
                    }
                }

                @Override
                public void visitIincInsn(int offset, int value) {
                    OffsetMapping.Target target = this.offsetMappings.get(offset);
                    if (target != null) {
                        this.methodSizeHandler.requireStackSizePadding(target.resolveIncrement(value).apply(this.mv, this.implementationContext).getMaximalSize());
                    } else {
                        this.mv.visitIincInsn(this.argumentHandler.mapped(offset), value);
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    switch (opcode) {
                        case 177: {
                            ((StackAwareMethodVisitor)this.mv).drainStack();
                            break;
                        }
                        case 172: {
                            this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(54, 21, StackSize.SINGLE));
                            break;
                        }
                        case 176: {
                            this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(58, 25, StackSize.SINGLE));
                            break;
                        }
                        case 174: {
                            this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(56, 23, StackSize.SINGLE));
                            break;
                        }
                        case 173: {
                            this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(55, 22, StackSize.DOUBLE));
                            break;
                        }
                        case 175: {
                            this.methodSizeHandler.requireLocalVariableLength(((StackAwareMethodVisitor)this.mv).drainStack(57, 24, StackSize.DOUBLE));
                            break;
                        }
                        default: {
                            this.mv.visitInsn(opcode);
                            return;
                        }
                    }
                    this.mv.visitJumpInsn(167, this.endOfMethod);
                }

                @Override
                public void visitEnd() {
                    this.suppressionHandler.onEnd(this.methodVisitor, this.implementationContext, this.methodSizeHandler, this.stackMapFrameHandler, this.adviceMethod.getReturnType());
                    this.methodVisitor.visitLabel(this.endOfMethod);
                    if (this.adviceMethod.getReturnType().represents(Boolean.TYPE) || this.adviceMethod.getReturnType().represents(Byte.TYPE) || this.adviceMethod.getReturnType().represents(Short.TYPE) || this.adviceMethod.getReturnType().represents(Character.TYPE) || this.adviceMethod.getReturnType().represents(Integer.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.methodVisitor);
                        this.methodVisitor.visitVarInsn(54, this.exit ? this.argumentHandler.exit() : this.argumentHandler.enter());
                    } else if (this.adviceMethod.getReturnType().represents(Long.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.methodVisitor);
                        this.methodVisitor.visitVarInsn(55, this.exit ? this.argumentHandler.exit() : this.argumentHandler.enter());
                    } else if (this.adviceMethod.getReturnType().represents(Float.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.methodVisitor);
                        this.methodVisitor.visitVarInsn(56, this.exit ? this.argumentHandler.exit() : this.argumentHandler.enter());
                    } else if (this.adviceMethod.getReturnType().represents(Double.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.methodVisitor);
                        this.methodVisitor.visitVarInsn(57, this.exit ? this.argumentHandler.exit() : this.argumentHandler.enter());
                    } else if (!this.adviceMethod.getReturnType().represents(Void.TYPE)) {
                        this.stackMapFrameHandler.injectReturnFrame(this.methodVisitor);
                        this.methodVisitor.visitVarInsn(58, this.exit ? this.argumentHandler.exit() : this.argumentHandler.enter());
                    }
                    this.methodSizeHandler.requireStackSize(this.postProcessor.resolve(this.instrumentedType, this.instrumentedMethod, this.assigner, this.argumentHandler, this.stackMapFrameHandler, this.exceptionHandler).apply(this.methodVisitor, this.implementationContext).getMaximalSize());
                    this.methodSizeHandler.requireStackSize(this.relocationHandler.apply(this.methodVisitor, this.implementationContext, this.exit ? this.argumentHandler.exit() : this.argumentHandler.enter()));
                    this.stackMapFrameHandler.injectCompletionFrame(this.methodVisitor);
                }

                @Override
                public void visitMaxs(int stackSize, int localVariableLength) {
                    this.methodSizeHandler.recordMaxima(stackSize, localVariableLength);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class Resolved
            extends Resolved.AbstractBase {
                protected final ClassReader classReader;

                protected Resolved(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, List<? extends OffsetMapping.Factory<?>> factories, TypeDescription throwableType, TypeDescription relocatableType, int relocatableIndex, ClassReader classReader) {
                    super(adviceMethod, postProcessor, factories, throwableType, relocatableType, relocatableIndex, OffsetMapping.Factory.AdviceType.INLINING);
                    this.classReader = classReader;
                }

                protected abstract Map<Integer, TypeDefinition> resolveInitializationTypes(ArgumentHandler var1);

                protected abstract MethodVisitor apply(MethodVisitor var1, Implementation.Context var2, Assigner var3, ArgumentHandler.ForInstrumentedMethod var4, MethodSizeHandler.ForInstrumentedMethod var5, StackMapFrameHandler.ForInstrumentedMethod var6, TypeDescription var7, MethodDescription var8, SuppressionHandler.Bound var9, RelocationHandler.Bound var10, StackManipulation var11);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static abstract class ForMethodExit
                extends Resolved
                implements net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodExit {
                    private final Map<String, TypeDefinition> uninitializedNamedTypes;
                    private final boolean backupArguments;

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    protected ForMethodExit(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, Map<String, TypeDefinition> uninitializedNamedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, ClassReader classReader, TypeDefinition enterType) {
                        super(adviceMethod, postProcessor, CompoundList.of(Arrays.asList(OffsetMapping.ForArgument.Unresolved.Factory.INSTANCE, OffsetMapping.ForAllArguments.Factory.INSTANCE, OffsetMapping.ForThisReference.Factory.INSTANCE, OffsetMapping.ForField.Unresolved.Factory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.ReaderFactory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.WriterFactory.INSTANCE, OffsetMapping.ForOrigin.Factory.INSTANCE, OffsetMapping.ForSelfCallHandle.Factory.INSTANCE, OffsetMapping.ForUnusedValue.Factory.INSTANCE, OffsetMapping.ForStubValue.INSTANCE, OffsetMapping.ForEnterValue.Factory.of(enterType), OffsetMapping.ForExitValue.Factory.of(adviceMethod.getReturnType()), new OffsetMapping.ForLocalValue.Factory(namedTypes), OffsetMapping.ForReturnValue.Factory.INSTANCE, OffsetMapping.ForThrowable.Factory.of(adviceMethod)), userFactories), adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(SUPPRESS_EXIT).resolve(TypeDescription.class), adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(REPEAT_ON).resolve(TypeDescription.class), (int)adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(REPEAT_ON_INDEX).resolve(Integer.class), classReader);
                        this.uninitializedNamedTypes = uninitializedNamedTypes;
                        this.backupArguments = adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(BACKUP_ARGUMENTS).resolve(Boolean.class);
                    }

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    protected static net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodExit of(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, Map<String, TypeDefinition> uninitializedNamedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, ClassReader classReader, TypeDefinition enterType) {
                        TypeDescription throwable = adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(ON_THROWABLE).resolve(TypeDescription.class);
                        return throwable.represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class)) ? new WithoutExceptionHandler(adviceMethod, postProcessor, namedTypes, uninitializedNamedTypes, userFactories, classReader, enterType) : new WithExceptionHandler(adviceMethod, postProcessor, namedTypes, uninitializedNamedTypes, userFactories, classReader, enterType, throwable);
                    }

                    @Override
                    public Map<String, TypeDefinition> getNamedTypes() {
                        return this.uninitializedNamedTypes;
                    }

                    @Override
                    protected Map<Integer, TypeDefinition> resolveInitializationTypes(ArgumentHandler argumentHandler) {
                        TreeMap<Integer, TypeDefinition> resolved = new TreeMap<Integer, TypeDefinition>();
                        for (Map.Entry<String, TypeDefinition> entry : this.uninitializedNamedTypes.entrySet()) {
                            resolved.put(argumentHandler.named(entry.getKey()), entry.getValue());
                        }
                        if (!this.adviceMethod.getReturnType().represents(Void.TYPE)) {
                            resolved.put(argumentHandler.exit(), this.adviceMethod.getReturnType());
                        }
                        return resolved;
                    }

                    @Override
                    protected MethodVisitor apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                        return this.doApply(methodVisitor, implementationContext, assigner, argumentHandler.bindExit(this.adviceMethod, this.getThrowable().represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class))), methodSizeHandler.bindExit(this.adviceMethod), stackMapFrameHandler.bindExit(this.adviceMethod), instrumentedType, instrumentedMethod, suppressionHandler, relocationHandler, exceptionHandler);
                    }

                    private MethodVisitor doApply(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                        HashMap<Integer, OffsetMapping.Target> offsetMappings = new HashMap<Integer, OffsetMapping.Target>();
                        for (Map.Entry entry : this.offsetMappings.entrySet()) {
                            offsetMappings.put((Integer)entry.getKey(), ((OffsetMapping)entry.getValue()).resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, OffsetMapping.Sort.EXIT));
                        }
                        return new CodeTranslationVisitor(methodVisitor, implementationContext, argumentHandler, methodSizeHandler, stackMapFrameHandler, instrumentedType, instrumentedMethod, assigner, this.adviceMethod, offsetMappings, suppressionHandler, relocationHandler, exceptionHandler, this.postProcessor, true);
                    }

                    @Override
                    public ArgumentHandler.Factory getArgumentHandlerFactory() {
                        return this.backupArguments ? ArgumentHandler.Factory.COPYING : ArgumentHandler.Factory.SIMPLE;
                    }

                    @Override
                    public TypeDefinition getAdviceType() {
                        return this.adviceMethod.getReturnType();
                    }

                    @Override
                    public Bound bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, StackManipulation exceptionHandler, RelocationHandler.Relocation relocation) {
                        return new AdviceMethodInliner(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, argumentHandler, methodSizeHandler, stackMapFrameHandler, this.suppressionHandler.bind(exceptionHandler), this.relocationHandler.bind(instrumentedMethod, relocation), exceptionHandler, this.classReader);
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
                        if (this.backupArguments != ((ForMethodExit)object).backupArguments) {
                            return false;
                        }
                        return ((Object)this.uninitializedNamedTypes).equals(((ForMethodExit)object).uninitializedNamedTypes);
                    }

                    @Override
                    public int hashCode() {
                        return (super.hashCode() * 31 + ((Object)this.uninitializedNamedTypes).hashCode()) * 31 + this.backupArguments;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class WithoutExceptionHandler
                    extends ForMethodExit {
                        protected WithoutExceptionHandler(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, Map<String, TypeDefinition> uninitializedNamedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, ClassReader classReader, TypeDefinition enterType) {
                            super(adviceMethod, postProcessor, namedTypes, uninitializedNamedTypes, userFactories, classReader, enterType);
                        }

                        @Override
                        public TypeDescription getThrowable() {
                            return NoExceptionHandler.DESCRIPTION;
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class WithExceptionHandler
                    extends ForMethodExit {
                        private final TypeDescription throwable;

                        protected WithExceptionHandler(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, Map<String, TypeDefinition> uninitializedNamedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, ClassReader classReader, TypeDefinition enterType, TypeDescription throwable) {
                            super(adviceMethod, postProcessor, namedTypes, uninitializedNamedTypes, userFactories, classReader, enterType);
                            this.throwable = throwable;
                        }

                        @Override
                        public TypeDescription getThrowable() {
                            return this.throwable;
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
                            return this.throwable.equals(((WithExceptionHandler)object).throwable);
                        }

                        @Override
                        public int hashCode() {
                            return super.hashCode() * 31 + this.throwable.hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static abstract class ForMethodEnter
                extends Resolved
                implements net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodEnter {
                    private final Map<String, TypeDefinition> namedTypes;
                    private final boolean prependLineNumber;

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                    protected ForMethodEnter(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, ClassReader classReader) {
                        super(adviceMethod, postProcessor, CompoundList.of(Arrays.asList(OffsetMapping.ForArgument.Unresolved.Factory.INSTANCE, OffsetMapping.ForAllArguments.Factory.INSTANCE, OffsetMapping.ForThisReference.Factory.INSTANCE, OffsetMapping.ForField.Unresolved.Factory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.ReaderFactory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.WriterFactory.INSTANCE, OffsetMapping.ForOrigin.Factory.INSTANCE, OffsetMapping.ForSelfCallHandle.Factory.INSTANCE, OffsetMapping.ForUnusedValue.Factory.INSTANCE, OffsetMapping.ForStubValue.INSTANCE, OffsetMapping.ForThrowable.Factory.INSTANCE, OffsetMapping.ForExitValue.Factory.of(exitType), new OffsetMapping.ForLocalValue.Factory(namedTypes), new OffsetMapping.Factory.Illegal<Thrown>(Thrown.class), new OffsetMapping.Factory.Illegal<Enter>(Enter.class), new OffsetMapping.Factory.Illegal<Return>(Return.class)), userFactories), adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(SUPPRESS_ENTER).resolve(TypeDescription.class), adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(SKIP_ON).resolve(TypeDescription.class), (int)adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(SKIP_ON_INDEX).resolve(Integer.class), classReader);
                        this.namedTypes = namedTypes;
                        this.prependLineNumber = adviceMethod.getDeclaredAnnotations().ofType(OnMethodEnter.class).getValue(PREPEND_LINE_NUMBER).resolve(Boolean.class);
                    }

                    protected static net.bytebuddy.asm.Advice$Dispatcher$Resolved$ForMethodEnter of(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, ClassReader classReader, boolean methodExit) {
                        return methodExit ? new WithRetainedEnterType(adviceMethod, postProcessor, namedTypes, userFactories, exitType, classReader) : new WithDiscardedEnterType(adviceMethod, postProcessor, namedTypes, userFactories, exitType, classReader);
                    }

                    @Override
                    protected Map<Integer, TypeDefinition> resolveInitializationTypes(ArgumentHandler argumentHandler) {
                        TreeMap<Integer, TypeDefinition> resolved = new TreeMap<Integer, TypeDefinition>();
                        for (Map.Entry<String, TypeDefinition> entry : this.namedTypes.entrySet()) {
                            resolved.put(argumentHandler.named(entry.getKey()), entry.getValue());
                        }
                        return resolved;
                    }

                    @Override
                    public Bound bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, StackManipulation exceptionHandler, RelocationHandler.Relocation relocation) {
                        return new AdviceMethodInliner(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, assigner, argumentHandler, methodSizeHandler, stackMapFrameHandler, this.suppressionHandler.bind(exceptionHandler), this.relocationHandler.bind(instrumentedMethod, relocation), exceptionHandler, this.classReader);
                    }

                    @Override
                    public boolean isPrependLineNumber() {
                        return this.prependLineNumber;
                    }

                    @Override
                    public TypeDefinition getActualAdviceType() {
                        return this.adviceMethod.getReturnType();
                    }

                    @Override
                    public Map<String, TypeDefinition> getNamedTypes() {
                        return this.namedTypes;
                    }

                    @Override
                    protected MethodVisitor apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                        return this.doApply(methodVisitor, implementationContext, assigner, argumentHandler.bindEnter(this.adviceMethod), methodSizeHandler.bindEnter(this.adviceMethod), stackMapFrameHandler.bindEnter(this.adviceMethod), instrumentedType, instrumentedMethod, suppressionHandler, relocationHandler, exceptionHandler);
                    }

                    protected MethodVisitor doApply(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                        HashMap<Integer, OffsetMapping.Target> offsetMappings = new HashMap<Integer, OffsetMapping.Target>();
                        for (Map.Entry entry : this.offsetMappings.entrySet()) {
                            offsetMappings.put((Integer)entry.getKey(), ((OffsetMapping)entry.getValue()).resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, OffsetMapping.Sort.ENTER));
                        }
                        return new CodeTranslationVisitor(methodVisitor, implementationContext, argumentHandler, methodSizeHandler, stackMapFrameHandler, instrumentedType, instrumentedMethod, assigner, this.adviceMethod, offsetMappings, suppressionHandler, relocationHandler, exceptionHandler, this.postProcessor, false);
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
                        if (this.prependLineNumber != ((ForMethodEnter)object).prependLineNumber) {
                            return false;
                        }
                        return ((Object)this.namedTypes).equals(((ForMethodEnter)object).namedTypes);
                    }

                    @Override
                    public int hashCode() {
                        return (super.hashCode() * 31 + ((Object)this.namedTypes).hashCode()) * 31 + this.prependLineNumber;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class WithDiscardedEnterType
                    extends ForMethodEnter {
                        protected WithDiscardedEnterType(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, ClassReader classReader) {
                            super(adviceMethod, postProcessor, namedTypes, userFactories, exitType, classReader);
                        }

                        @Override
                        public TypeDefinition getAdviceType() {
                            return TypeDescription.ForLoadedType.of(Void.TYPE);
                        }

                        @Override
                        protected MethodVisitor doApply(MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForAdvice argumentHandler, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDescription instrumentedType, MethodDescription instrumentedMethod, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler) {
                            methodSizeHandler.requireLocalVariableLengthPadding(this.adviceMethod.getReturnType().getStackSize().getSize());
                            return super.doApply(methodVisitor, implementationContext, assigner, argumentHandler, methodSizeHandler, stackMapFrameHandler, instrumentedType, instrumentedMethod, suppressionHandler, relocationHandler, exceptionHandler);
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class WithRetainedEnterType
                    extends ForMethodEnter {
                        protected WithRetainedEnterType(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, Map<String, TypeDefinition> namedTypes, List<? extends OffsetMapping.Factory<?>> userFactories, TypeDefinition exitType, ClassReader classReader) {
                            super(adviceMethod, postProcessor, namedTypes, userFactories, exitType, classReader);
                        }

                        @Override
                        public TypeDefinition getAdviceType() {
                            return this.adviceMethod.getReturnType();
                        }
                    }
                }

                protected class AdviceMethodInliner
                extends ClassVisitor
                implements Bound {
                    protected final TypeDescription instrumentedType;
                    protected final MethodDescription instrumentedMethod;
                    protected final MethodVisitor methodVisitor;
                    protected final Implementation.Context implementationContext;
                    protected final Assigner assigner;
                    protected final ArgumentHandler.ForInstrumentedMethod argumentHandler;
                    protected final MethodSizeHandler.ForInstrumentedMethod methodSizeHandler;
                    protected final StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler;
                    protected final SuppressionHandler.Bound suppressionHandler;
                    protected final RelocationHandler.Bound relocationHandler;
                    protected final StackManipulation exceptionHandler;
                    protected final ClassReader classReader;
                    protected final List<Label> labels;

                    protected AdviceMethodInliner(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, SuppressionHandler.Bound suppressionHandler, RelocationHandler.Bound relocationHandler, StackManipulation exceptionHandler, ClassReader classReader) {
                        super(OpenedClassReader.ASM_API);
                        this.instrumentedType = instrumentedType;
                        this.instrumentedMethod = instrumentedMethod;
                        this.methodVisitor = methodVisitor;
                        this.implementationContext = implementationContext;
                        this.assigner = assigner;
                        this.argumentHandler = argumentHandler;
                        this.methodSizeHandler = methodSizeHandler;
                        this.stackMapFrameHandler = stackMapFrameHandler;
                        this.suppressionHandler = suppressionHandler;
                        this.relocationHandler = relocationHandler;
                        this.exceptionHandler = exceptionHandler;
                        this.classReader = classReader;
                        this.labels = new ArrayList<Label>();
                    }

                    public void prepare() {
                        this.classReader.accept(new ExceptionTableExtractor(), 6);
                        this.suppressionHandler.onPrepare(this.methodVisitor);
                    }

                    public void initialize() {
                        for (Map.Entry<Integer, TypeDefinition> typeDefinition : Resolved.this.resolveInitializationTypes(this.argumentHandler).entrySet()) {
                            if (typeDefinition.getValue().represents(Boolean.TYPE) || typeDefinition.getValue().represents(Byte.TYPE) || typeDefinition.getValue().represents(Short.TYPE) || typeDefinition.getValue().represents(Character.TYPE) || typeDefinition.getValue().represents(Integer.TYPE)) {
                                this.methodVisitor.visitInsn(3);
                                this.methodVisitor.visitVarInsn(54, typeDefinition.getKey());
                            } else if (typeDefinition.getValue().represents(Long.TYPE)) {
                                this.methodVisitor.visitInsn(9);
                                this.methodVisitor.visitVarInsn(55, typeDefinition.getKey());
                            } else if (typeDefinition.getValue().represents(Float.TYPE)) {
                                this.methodVisitor.visitInsn(11);
                                this.methodVisitor.visitVarInsn(56, typeDefinition.getKey());
                            } else if (typeDefinition.getValue().represents(Double.TYPE)) {
                                this.methodVisitor.visitInsn(14);
                                this.methodVisitor.visitVarInsn(57, typeDefinition.getKey());
                            } else {
                                this.methodVisitor.visitInsn(1);
                                this.methodVisitor.visitVarInsn(58, typeDefinition.getKey());
                            }
                            this.methodSizeHandler.requireStackSize(typeDefinition.getValue().getStackSize().getSize());
                        }
                    }

                    public void apply() {
                        this.classReader.accept(this, 2 | this.stackMapFrameHandler.getReaderHint());
                    }

                    @MaybeNull
                    public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
                        return Resolved.this.adviceMethod.getInternalName().equals(internalName) && Resolved.this.adviceMethod.getDescriptor().equals(descriptor) ? new ExceptionTableSubstitutor(Resolved.this.apply(this.methodVisitor, this.implementationContext, this.assigner, this.argumentHandler, this.methodSizeHandler, this.stackMapFrameHandler, this.instrumentedType, this.instrumentedMethod, this.suppressionHandler, this.relocationHandler, this.exceptionHandler)) : IGNORE_METHOD;
                    }

                    protected class ExceptionTableSubstitutor
                    extends MethodVisitor {
                        private final Map<Label, Label> substitutions;
                        private int index;

                        protected ExceptionTableSubstitutor(MethodVisitor methodVisitor) {
                            super(OpenedClassReader.ASM_API, methodVisitor);
                            this.substitutions = new IdentityHashMap<Label, Label>();
                        }

                        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                            this.substitutions.put(start, AdviceMethodInliner.this.labels.get(this.index++));
                            this.substitutions.put(end, AdviceMethodInliner.this.labels.get(this.index++));
                            Label actualHandler = AdviceMethodInliner.this.labels.get(this.index++);
                            this.substitutions.put(handler, actualHandler);
                            ((CodeTranslationVisitor)this.mv).propagateHandler(actualHandler);
                        }

                        @MaybeNull
                        public AnnotationVisitor visitTryCatchAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                            return IGNORE_ANNOTATION;
                        }

                        public void visitLabel(Label label) {
                            super.visitLabel(this.resolve(label));
                        }

                        public void visitJumpInsn(int opcode, Label label) {
                            super.visitJumpInsn(opcode, this.resolve(label));
                        }

                        public void visitTableSwitchInsn(int minimum, int maximum, Label defaultOption, Label ... label) {
                            super.visitTableSwitchInsn(minimum, maximum, defaultOption, this.resolve(label));
                        }

                        public void visitLookupSwitchInsn(Label defaultOption, int[] keys, Label[] label) {
                            super.visitLookupSwitchInsn(this.resolve(defaultOption), keys, this.resolve(label));
                        }

                        private Label[] resolve(Label[] label) {
                            Label[] resolved = new Label[label.length];
                            int index = 0;
                            for (Label aLabel : label) {
                                resolved[index++] = this.resolve(aLabel);
                            }
                            return resolved;
                        }

                        private Label resolve(Label label) {
                            Label substitution = this.substitutions.get(label);
                            return substitution == null ? label : substitution;
                        }
                    }

                    protected class ExceptionTableCollector
                    extends MethodVisitor {
                        private final MethodVisitor methodVisitor;

                        protected ExceptionTableCollector(MethodVisitor methodVisitor) {
                            super(OpenedClassReader.ASM_API);
                            this.methodVisitor = methodVisitor;
                        }

                        public void visitTryCatchBlock(Label start, Label end, Label handler, @MaybeNull String type) {
                            this.methodVisitor.visitTryCatchBlock(start, end, handler, type);
                            AdviceMethodInliner.this.labels.addAll(Arrays.asList(start, end, handler));
                        }

                        @MaybeNull
                        public AnnotationVisitor visitTryCatchAnnotation(int typeReference, @MaybeNull TypePath typePath, String descriptor, boolean visible) {
                            return this.methodVisitor.visitTryCatchAnnotation(typeReference, typePath, descriptor, visible);
                        }
                    }

                    protected class ExceptionTableExtractor
                    extends ClassVisitor {
                        protected ExceptionTableExtractor() {
                            super(OpenedClassReader.ASM_API);
                        }

                        @MaybeNull
                        public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
                            return Resolved.this.adviceMethod.getInternalName().equals(internalName) && Resolved.this.adviceMethod.getDescriptor().equals(descriptor) ? new ExceptionTableCollector(AdviceMethodInliner.this.methodVisitor) : IGNORE_METHOD;
                        }
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Inactive implements Unresolved,
        Resolved.ForMethodEnter,
        Resolved.ForMethodExit,
        Bound
        {
            INSTANCE;


            @Override
            public boolean isAlive() {
                return false;
            }

            @Override
            public boolean isBinary() {
                return false;
            }

            @Override
            public TypeDescription getAdviceType() {
                return TypeDescription.ForLoadedType.of(Void.TYPE);
            }

            @Override
            public boolean isPrependLineNumber() {
                return false;
            }

            @Override
            public TypeDefinition getActualAdviceType() {
                return TypeDescription.ForLoadedType.of(Void.TYPE);
            }

            @Override
            public Map<String, TypeDefinition> getNamedTypes() {
                return Collections.emptyMap();
            }

            @Override
            public TypeDescription getThrowable() {
                return NoExceptionHandler.DESCRIPTION;
            }

            @Override
            public ArgumentHandler.Factory getArgumentHandlerFactory() {
                return ArgumentHandler.Factory.SIMPLE;
            }

            @Override
            public Resolved.ForMethodEnter asMethodEnter(List<? extends OffsetMapping.Factory<?>> userFactories, @MaybeNull ClassReader classReader, Unresolved methodExit, PostProcessor.Factory postProcessorFactory) {
                return this;
            }

            @Override
            public Resolved.ForMethodExit asMethodExit(List<? extends OffsetMapping.Factory<?>> userFactories, @MaybeNull ClassReader classReader, Unresolved methodEnter, PostProcessor.Factory postProcessorFactory) {
                return this;
            }

            @Override
            public void prepare() {
            }

            @Override
            public void initialize() {
            }

            @Override
            public void apply() {
            }

            @Override
            public Bound bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, Assigner assigner, ArgumentHandler.ForInstrumentedMethod argumentHandler, MethodSizeHandler.ForInstrumentedMethod methodSizeHandler, StackMapFrameHandler.ForInstrumentedMethod stackMapFrameHandler, StackManipulation exceptionHandler, RelocationHandler.Relocation relocation) {
                return this;
            }
        }

        public static interface Bound {
            public void prepare();

            public void initialize();

            public void apply();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Resolved
        extends Dispatcher {
            public Map<String, TypeDefinition> getNamedTypes();

            public Bound bind(TypeDescription var1, MethodDescription var2, MethodVisitor var3, Implementation.Context var4, Assigner var5, ArgumentHandler.ForInstrumentedMethod var6, MethodSizeHandler.ForInstrumentedMethod var7, StackMapFrameHandler.ForInstrumentedMethod var8, StackManipulation var9, RelocationHandler.Relocation var10);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class AbstractBase
            implements Resolved {
                protected final MethodDescription.InDefinedShape adviceMethod;
                protected final PostProcessor postProcessor;
                protected final Map<Integer, OffsetMapping> offsetMappings;
                protected final SuppressionHandler suppressionHandler;
                protected final RelocationHandler relocationHandler;

                protected AbstractBase(MethodDescription.InDefinedShape adviceMethod, PostProcessor postProcessor, List<? extends OffsetMapping.Factory<?>> factories, TypeDescription throwableType, TypeDescription relocatableType, int relocatableIndex, OffsetMapping.Factory.AdviceType adviceType) {
                    this.adviceMethod = adviceMethod;
                    this.postProcessor = postProcessor;
                    HashMap offsetMappings = new HashMap();
                    for (OffsetMapping.Factory<?> factory : factories) {
                        offsetMappings.put(TypeDescription.ForLoadedType.of(factory.getAnnotationType()), factory);
                    }
                    this.offsetMappings = new LinkedHashMap<Integer, OffsetMapping>();
                    for (ParameterDescription.InDefinedShape parameterDescription : adviceMethod.getParameters()) {
                        OffsetMapping offsetMapping = null;
                        for (AnnotationDescription annotationDescription : parameterDescription.getDeclaredAnnotations()) {
                            OffsetMapping.Factory factory = (OffsetMapping.Factory)offsetMappings.get(annotationDescription.getAnnotationType());
                            if (factory == null) continue;
                            OffsetMapping current = factory.make(parameterDescription, annotationDescription.prepare(factory.getAnnotationType()), adviceType);
                            if (offsetMapping == null) {
                                offsetMapping = current;
                                continue;
                            }
                            throw new IllegalStateException(parameterDescription + " is bound to both " + current + " and " + offsetMapping);
                        }
                        this.offsetMappings.put(parameterDescription.getOffset(), offsetMapping == null ? new OffsetMapping.ForArgument.Unresolved(parameterDescription) : offsetMapping);
                    }
                    this.suppressionHandler = SuppressionHandler.Suppressing.of(throwableType);
                    this.relocationHandler = RelocationHandler.ForType.of(relocatableType, relocatableIndex, adviceMethod.getReturnType());
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
                    if (!this.adviceMethod.equals(((AbstractBase)object).adviceMethod)) {
                        return false;
                    }
                    if (!this.postProcessor.equals(((AbstractBase)object).postProcessor)) {
                        return false;
                    }
                    if (!((Object)this.offsetMappings).equals(((AbstractBase)object).offsetMappings)) {
                        return false;
                    }
                    if (!this.suppressionHandler.equals(((AbstractBase)object).suppressionHandler)) {
                        return false;
                    }
                    return this.relocationHandler.equals(((AbstractBase)object).relocationHandler);
                }

                public int hashCode() {
                    return ((((this.getClass().hashCode() * 31 + this.adviceMethod.hashCode()) * 31 + this.postProcessor.hashCode()) * 31 + ((Object)this.offsetMappings).hashCode()) * 31 + this.suppressionHandler.hashCode()) * 31 + this.relocationHandler.hashCode();
                }
            }

            public static interface ForMethodExit
            extends Resolved {
                public TypeDescription getThrowable();

                public ArgumentHandler.Factory getArgumentHandlerFactory();
            }

            public static interface ForMethodEnter
            extends Resolved {
                public boolean isPrependLineNumber();

                public TypeDefinition getActualAdviceType();
            }
        }

        public static interface RelocationHandler {
            public Bound bind(MethodDescription var1, Relocation var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForType
            implements RelocationHandler {
                private final TypeDescription typeDescription;
                private final int index;

                protected ForType(TypeDescription typeDescription, int index) {
                    this.typeDescription = typeDescription;
                    this.index = index;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                protected static RelocationHandler of(TypeDescription typeDescription, int index, TypeDefinition returnedType) {
                    TypeDefinition targetType;
                    if (index < 0) {
                        targetType = returnedType;
                    } else if (returnedType.isArray()) {
                        targetType = returnedType.getComponentType();
                    } else {
                        throw new IllegalStateException(returnedType + " is not an array type but an index for a relocation is defined");
                    }
                    if (typeDescription.represents(Void.TYPE)) {
                        return Disabled.INSTANCE;
                    }
                    if (typeDescription.represents((java.lang.reflect.Type)((Object)OnDefaultValue.class))) {
                        return ForValue.of(targetType, index, false);
                    }
                    if (typeDescription.represents((java.lang.reflect.Type)((Object)OnNonDefaultValue.class))) {
                        return ForValue.of(targetType, index, true);
                    }
                    if (typeDescription.isPrimitive() || targetType.isPrimitive()) {
                        throw new IllegalStateException("Cannot relocate execution by instance type for primitive type");
                    }
                    return new ForType(typeDescription, index);
                }

                public net.bytebuddy.asm.Advice$Dispatcher$RelocationHandler$Bound bind(MethodDescription instrumentedMethod, Relocation relocation) {
                    return new Bound(instrumentedMethod, relocation);
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
                    if (this.index != ((ForType)object).index) {
                        return false;
                    }
                    return this.typeDescription.equals(((ForType)object).typeDescription);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.index;
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class Bound
                implements net.bytebuddy.asm.Advice$Dispatcher$RelocationHandler$Bound {
                    private final MethodDescription instrumentedMethod;
                    private final Relocation relocation;

                    protected Bound(MethodDescription instrumentedMethod, Relocation relocation) {
                        this.instrumentedMethod = instrumentedMethod;
                        this.relocation = relocation;
                    }

                    public int apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, int offset) {
                        int size;
                        if (this.instrumentedMethod.isConstructor()) {
                            throw new IllegalStateException("Cannot skip code execution from constructor: " + this.instrumentedMethod);
                        }
                        methodVisitor.visitVarInsn(25, offset);
                        Label noSkip = new Label();
                        if (ForType.this.index < 0) {
                            size = 0;
                        } else {
                            methodVisitor.visitJumpInsn(198, noSkip);
                            methodVisitor.visitVarInsn(25, offset);
                            size = IntegerConstant.forValue(ForType.this.index).apply(methodVisitor, implementationContext).getMaximalSize() + 1;
                            methodVisitor.visitInsn(50);
                        }
                        methodVisitor.visitTypeInsn(193, ForType.this.typeDescription.getInternalName());
                        methodVisitor.visitJumpInsn(153, noSkip);
                        this.relocation.apply(methodVisitor);
                        methodVisitor.visitLabel(noSkip);
                        return size;
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
                        if (!this.instrumentedMethod.equals(((Bound)object).instrumentedMethod)) {
                            return false;
                        }
                        if (!this.relocation.equals(((Bound)object).relocation)) {
                            return false;
                        }
                        return ForType.this.equals(((Bound)object).ForType.this);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.instrumentedMethod.hashCode()) * 31 + this.relocation.hashCode()) * 31 + ForType.this.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForValue {
                BOOLEAN(21, 51, 154, 153, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                    }
                }
                ,
                BYTE(21, 51, 154, 153, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                    }
                }
                ,
                SHORT(21, 53, 154, 153, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                    }
                }
                ,
                CHARACTER(21, 52, 154, 153, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                    }
                }
                ,
                INTEGER(21, 46, 154, 153, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                    }
                }
                ,
                LONG(22, 47, 154, 153, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                        methodVisitor.visitInsn(136);
                    }
                }
                ,
                FLOAT(23, 48, 154, 153, 2){

                    protected void convertValue(MethodVisitor methodVisitor) {
                        methodVisitor.visitInsn(11);
                        methodVisitor.visitInsn(149);
                    }
                }
                ,
                DOUBLE(24, 49, 154, 153, 4){

                    protected void convertValue(MethodVisitor methodVisitor) {
                        methodVisitor.visitInsn(14);
                        methodVisitor.visitInsn(151);
                    }
                }
                ,
                REFERENCE(25, 50, 199, 198, 0){

                    protected void convertValue(MethodVisitor methodVisitor) {
                    }
                };

                private final int load;
                private final int arrayLoad;
                private final int defaultJump;
                private final int nonDefaultJump;
                private final int requiredSize;

                private ForValue(int load, int arrayLoad, int defaultJump, int nonDefaultJump, int requiredSize) {
                    this.load = load;
                    this.arrayLoad = arrayLoad;
                    this.defaultJump = defaultJump;
                    this.nonDefaultJump = nonDefaultJump;
                    this.requiredSize = requiredSize;
                }

                protected static RelocationHandler of(TypeDefinition typeDefinition, int index, boolean inverted) {
                    RelocationHandler relocationHandler;
                    ForValue skipDispatcher;
                    if (typeDefinition.represents(Boolean.TYPE)) {
                        skipDispatcher = BOOLEAN;
                    } else if (typeDefinition.represents(Byte.TYPE)) {
                        skipDispatcher = BYTE;
                    } else if (typeDefinition.represents(Short.TYPE)) {
                        skipDispatcher = SHORT;
                    } else if (typeDefinition.represents(Character.TYPE)) {
                        skipDispatcher = CHARACTER;
                    } else if (typeDefinition.represents(Integer.TYPE)) {
                        skipDispatcher = INTEGER;
                    } else if (typeDefinition.represents(Long.TYPE)) {
                        skipDispatcher = LONG;
                    } else if (typeDefinition.represents(Float.TYPE)) {
                        skipDispatcher = FLOAT;
                    } else if (typeDefinition.represents(Double.TYPE)) {
                        skipDispatcher = DOUBLE;
                    } else {
                        if (typeDefinition.represents(Void.TYPE)) {
                            throw new IllegalStateException("Cannot skip on default value for void return type");
                        }
                        skipDispatcher = REFERENCE;
                    }
                    if (inverted) {
                        ForValue forValue = skipDispatcher;
                        ((Object)((Object)forValue)).getClass();
                        relocationHandler = forValue.new OfNonDefault(index);
                    } else {
                        ForValue forValue = skipDispatcher;
                        ((Object)((Object)forValue)).getClass();
                        relocationHandler = forValue.new OfDefault(index);
                    }
                    return relocationHandler;
                }

                protected abstract void convertValue(MethodVisitor var1);

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class Bound
                implements net.bytebuddy.asm.Advice$Dispatcher$RelocationHandler$Bound {
                    private final MethodDescription instrumentedMethod;
                    private final Relocation relocation;
                    private final int index;
                    private final boolean inverted;

                    protected Bound(MethodDescription instrumentedMethod, Relocation relocation, int index, boolean inverted) {
                        this.instrumentedMethod = instrumentedMethod;
                        this.relocation = relocation;
                        this.index = index;
                        this.inverted = inverted;
                    }

                    public int apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, int offset) {
                        int size;
                        if (this.instrumentedMethod.isConstructor()) {
                            throw new IllegalStateException("Cannot skip code execution from constructor: " + this.instrumentedMethod);
                        }
                        Label noSkip = new Label();
                        if (this.index < 0) {
                            size = ForValue.this.requiredSize;
                            methodVisitor.visitVarInsn(ForValue.this.load, offset);
                        } else {
                            methodVisitor.visitVarInsn(25, offset);
                            methodVisitor.visitJumpInsn(198, noSkip);
                            methodVisitor.visitVarInsn(25, offset);
                            size = Math.max(ForValue.this.requiredSize, IntegerConstant.forValue(this.index).apply(methodVisitor, implementationContext).getMaximalSize() + 1);
                            methodVisitor.visitInsn(ForValue.this.arrayLoad);
                        }
                        ForValue.this.convertValue(methodVisitor);
                        methodVisitor.visitJumpInsn(this.inverted ? ForValue.this.nonDefaultJump : ForValue.this.defaultJump, noSkip);
                        this.relocation.apply(methodVisitor);
                        methodVisitor.visitLabel(noSkip);
                        return size;
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
                        if (this.index != ((Bound)object).index) {
                            return false;
                        }
                        if (this.inverted != ((Bound)object).inverted) {
                            return false;
                        }
                        if (!ForValue.this.equals((Object)((Bound)object).ForValue.this)) {
                            return false;
                        }
                        if (!this.instrumentedMethod.equals(((Bound)object).instrumentedMethod)) {
                            return false;
                        }
                        return this.relocation.equals(((Bound)object).relocation);
                    }

                    public int hashCode() {
                        return ((((this.getClass().hashCode() * 31 + this.instrumentedMethod.hashCode()) * 31 + this.relocation.hashCode()) * 31 + this.index) * 31 + this.inverted) * 31 + ForValue.this.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class OfNonDefault
                implements RelocationHandler {
                    private final int index;

                    protected OfNonDefault(int index) {
                        this.index = index;
                    }

                    public net.bytebuddy.asm.Advice$Dispatcher$RelocationHandler$Bound bind(MethodDescription instrumentedMethod, Relocation relocation) {
                        return new Bound(instrumentedMethod, relocation, this.index, true);
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
                        if (this.index != ((OfNonDefault)object).index) {
                            return false;
                        }
                        return ForValue.this.equals((Object)((OfNonDefault)object).ForValue.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.index) * 31 + ForValue.this.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class OfDefault
                implements RelocationHandler {
                    private final int index;

                    public OfDefault(int index) {
                        this.index = index;
                    }

                    public net.bytebuddy.asm.Advice$Dispatcher$RelocationHandler$Bound bind(MethodDescription instrumentedMethod, Relocation relocation) {
                        return new Bound(instrumentedMethod, relocation, this.index, false);
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
                        if (this.index != ((OfDefault)object).index) {
                            return false;
                        }
                        return ForValue.this.equals((Object)((OfDefault)object).ForValue.this);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.index) * 31 + ForValue.this.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Disabled implements RelocationHandler,
            Bound
            {
                INSTANCE;


                @Override
                public Bound bind(MethodDescription instrumentedMethod, Relocation relocation) {
                    return this;
                }

                @Override
                public int apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, int offset) {
                    return 0;
                }
            }

            public static interface Bound {
                public static final int NO_REQUIRED_SIZE = 0;

                public int apply(MethodVisitor var1, Implementation.Context var2, int var3);
            }

            public static interface Relocation {
                public void apply(MethodVisitor var1);

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForLabel
                implements Relocation {
                    private final Label label;

                    public ForLabel(Label label) {
                        this.label = label;
                    }

                    public void apply(MethodVisitor methodVisitor) {
                        methodVisitor.visitJumpInsn(167, this.label);
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
                        return this.label.equals(((ForLabel)object).label);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.label.hashCode();
                    }
                }
            }
        }

        public static interface SuppressionHandler {
            public Bound bind(StackManipulation var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class Suppressing
            implements SuppressionHandler {
                private final TypeDescription suppressedType;

                protected Suppressing(TypeDescription suppressedType) {
                    this.suppressedType = suppressedType;
                }

                protected static SuppressionHandler of(TypeDescription suppressedType) {
                    return suppressedType.represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class)) ? NoOp.INSTANCE : new Suppressing(suppressedType);
                }

                public net.bytebuddy.asm.Advice$Dispatcher$SuppressionHandler$Bound bind(StackManipulation exceptionHandler) {
                    return new Bound(this.suppressedType, exceptionHandler);
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
                    return this.suppressedType.equals(((Suppressing)object).suppressedType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.suppressedType.hashCode();
                }

                protected static class Bound
                implements net.bytebuddy.asm.Advice$Dispatcher$SuppressionHandler$Bound {
                    private final TypeDescription suppressedType;
                    private final StackManipulation exceptionHandler;
                    private final Label startOfMethod;
                    private final Label endOfMethod;

                    protected Bound(TypeDescription suppressedType, StackManipulation exceptionHandler) {
                        this.suppressedType = suppressedType;
                        this.exceptionHandler = exceptionHandler;
                        this.startOfMethod = new Label();
                        this.endOfMethod = new Label();
                    }

                    public void onPrepare(MethodVisitor methodVisitor) {
                        methodVisitor.visitTryCatchBlock(this.startOfMethod, this.endOfMethod, this.endOfMethod, this.suppressedType.getInternalName());
                    }

                    public void onStart(MethodVisitor methodVisitor) {
                        methodVisitor.visitLabel(this.startOfMethod);
                    }

                    public void onEnd(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDefinition returnType) {
                        methodVisitor.visitLabel(this.endOfMethod);
                        stackMapFrameHandler.injectExceptionFrame(methodVisitor);
                        methodSizeHandler.requireStackSize(1 + this.exceptionHandler.apply(methodVisitor, implementationContext).getMaximalSize());
                        if (returnType.represents(Boolean.TYPE) || returnType.represents(Byte.TYPE) || returnType.represents(Short.TYPE) || returnType.represents(Character.TYPE) || returnType.represents(Integer.TYPE)) {
                            methodVisitor.visitInsn(3);
                        } else if (returnType.represents(Long.TYPE)) {
                            methodVisitor.visitInsn(9);
                        } else if (returnType.represents(Float.TYPE)) {
                            methodVisitor.visitInsn(11);
                        } else if (returnType.represents(Double.TYPE)) {
                            methodVisitor.visitInsn(14);
                        } else if (!returnType.represents(Void.TYPE)) {
                            methodVisitor.visitInsn(1);
                        }
                    }

                    public void onEndWithSkip(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDefinition returnType) {
                        Label skipExceptionHandler = new Label();
                        methodVisitor.visitJumpInsn(167, skipExceptionHandler);
                        this.onEnd(methodVisitor, implementationContext, methodSizeHandler, stackMapFrameHandler, returnType);
                        methodVisitor.visitLabel(skipExceptionHandler);
                        stackMapFrameHandler.injectReturnFrame(methodVisitor);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements SuppressionHandler,
            Bound
            {
                INSTANCE;


                @Override
                public Bound bind(StackManipulation exceptionHandler) {
                    return this;
                }

                @Override
                public void onPrepare(MethodVisitor methodVisitor) {
                }

                @Override
                public void onStart(MethodVisitor methodVisitor) {
                }

                @Override
                public void onEnd(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDefinition returnType) {
                }

                @Override
                public void onEndWithSkip(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodSizeHandler.ForAdvice methodSizeHandler, StackMapFrameHandler.ForAdvice stackMapFrameHandler, TypeDefinition returnType) {
                }
            }

            public static interface Bound {
                public void onPrepare(MethodVisitor var1);

                public void onStart(MethodVisitor var1);

                public void onEnd(MethodVisitor var1, Implementation.Context var2, MethodSizeHandler.ForAdvice var3, StackMapFrameHandler.ForAdvice var4, TypeDefinition var5);

                public void onEndWithSkip(MethodVisitor var1, Implementation.Context var2, MethodSizeHandler.ForAdvice var3, StackMapFrameHandler.ForAdvice var4, TypeDefinition var5);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Unresolved
        extends Dispatcher {
            public boolean isBinary();

            public Map<String, TypeDefinition> getNamedTypes();

            public Resolved.ForMethodEnter asMethodEnter(List<? extends OffsetMapping.Factory<?>> var1, @MaybeNull ClassReader var2, Unresolved var3, PostProcessor.Factory var4);

            public Resolved.ForMethodExit asMethodExit(List<? extends OffsetMapping.Factory<?>> var1, @MaybeNull ClassReader var2, Unresolved var3, PostProcessor.Factory var4);
        }
    }

    public static interface ExceptionHandler {
        public StackManipulation resolve(MethodDescription var1, TypeDescription var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements ExceptionHandler {
            private final StackManipulation stackManipulation;

            public Simple(StackManipulation stackManipulation) {
                this.stackManipulation = stackManipulation;
            }

            public StackManipulation resolve(MethodDescription instrumentedMethod, TypeDescription instrumentedType) {
                return this.stackManipulation;
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
                return this.stackManipulation.equals(((Simple)object).stackManipulation);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.stackManipulation.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements ExceptionHandler
        {
            SUPPRESSING{

                public StackManipulation resolve(MethodDescription instrumentedMethod, TypeDescription instrumentedType) {
                    return Removal.SINGLE;
                }
            }
            ,
            PRINTING{

                public StackManipulation resolve(MethodDescription instrumentedMethod, TypeDescription instrumentedType) {
                    try {
                        return MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(Throwable.class.getMethod("printStackTrace", new Class[0])));
                    }
                    catch (NoSuchMethodException exception) {
                        throw new IllegalStateException("Cannot locate Throwable::printStackTrace");
                    }
                }
            }
            ,
            RETHROWING{

                public StackManipulation resolve(MethodDescription instrumentedMethod, TypeDescription instrumentedType) {
                    return Throw.INSTANCE;
                }
            };

        }
    }

    public static interface StackMapFrameHandler {
        public void translateFrame(MethodVisitor var1, int var2, int var3, @MaybeNull Object[] var4, int var5, @MaybeNull Object[] var6);

        public void injectReturnFrame(MethodVisitor var1);

        public void injectExceptionFrame(MethodVisitor var1);

        public void injectCompletionFrame(MethodVisitor var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class Default
        implements ForInstrumentedMethod {
            protected static final Object[] EMPTY = new Object[0];
            protected final TypeDescription instrumentedType;
            protected final MethodDescription instrumentedMethod;
            protected final List<? extends TypeDescription> initialTypes;
            protected final List<? extends TypeDescription> latentTypes;
            protected final List<? extends TypeDescription> preMethodTypes;
            protected final List<? extends TypeDescription> postMethodTypes;
            protected final boolean expandFrames;
            protected int currentFrameDivergence;

            protected Default(TypeDescription instrumentedType, MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> latentTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes, boolean expandFrames) {
                this.instrumentedType = instrumentedType;
                this.instrumentedMethod = instrumentedMethod;
                this.initialTypes = initialTypes;
                this.latentTypes = latentTypes;
                this.preMethodTypes = preMethodTypes;
                this.postMethodTypes = postMethodTypes;
                this.expandFrames = expandFrames;
            }

            protected static ForInstrumentedMethod of(TypeDescription instrumentedType, MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> latentTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes, boolean exitAdvice, boolean copyArguments, ClassFileVersion classFileVersion, int writerFlags, int readerFlags) {
                if ((writerFlags & 2) != 0 || classFileVersion.isLessThan(ClassFileVersion.JAVA_V6)) {
                    return NoOp.INSTANCE;
                }
                if (!exitAdvice && initialTypes.isEmpty()) {
                    return new Trivial(instrumentedType, instrumentedMethod, latentTypes, (readerFlags & 8) != 0);
                }
                if (copyArguments) {
                    return new WithPreservedArguments.WithArgumentCopy(instrumentedType, instrumentedMethod, initialTypes, latentTypes, preMethodTypes, postMethodTypes, (readerFlags & 8) != 0);
                }
                return new WithPreservedArguments.WithoutArgumentCopy(instrumentedType, instrumentedMethod, initialTypes, latentTypes, preMethodTypes, postMethodTypes, (readerFlags & 8) != 0, !instrumentedMethod.isConstructor());
            }

            @Override
            public net.bytebuddy.asm.Advice$StackMapFrameHandler$ForAdvice bindEnter(MethodDescription.InDefinedShape adviceMethod) {
                return new ForAdvice(adviceMethod, this.initialTypes, this.latentTypes, this.preMethodTypes, TranslationMode.ENTER, this.instrumentedMethod.isConstructor() ? Initialization.UNITIALIZED : Initialization.INITIALIZED);
            }

            @Override
            public int getReaderHint() {
                return this.expandFrames ? 8 : 0;
            }

            protected void translateFrame(MethodVisitor methodVisitor, TranslationMode translationMode, MethodDescription methodDescription, List<? extends TypeDescription> additionalTypes, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                switch (type) {
                    case 3: 
                    case 4: {
                        break;
                    }
                    case 1: {
                        this.currentFrameDivergence += localVariableLength;
                        break;
                    }
                    case 2: {
                        this.currentFrameDivergence -= localVariableLength;
                        if (this.currentFrameDivergence >= 0) break;
                        throw new IllegalStateException(methodDescription + " dropped " + Math.abs(this.currentFrameDivergence) + " implicit frames");
                    }
                    case -1: 
                    case 0: {
                        int offset;
                        if (methodDescription.getParameters().size() + (methodDescription.isStatic() ? 0 : 1) > localVariableLength) {
                            throw new IllegalStateException("Inconsistent frame length for " + methodDescription + ": " + localVariableLength);
                        }
                        if (methodDescription.isStatic()) {
                            offset = 0;
                        } else {
                            if (!translationMode.isPossibleThisFrameValue(this.instrumentedType, this.instrumentedMethod, localVariable[0])) {
                                throw new IllegalStateException(methodDescription + " is inconsistent for 'this' reference: " + localVariable[0]);
                            }
                            offset = 1;
                        }
                        for (int index = 0; index < methodDescription.getParameters().size(); ++index) {
                            if (Initialization.INITIALIZED.toFrame(((ParameterDescription)methodDescription.getParameters().get(index)).getType().asErasure()).equals(localVariable[index + offset])) continue;
                            throw new IllegalStateException(methodDescription + " is inconsistent at " + index + ": " + localVariable[index + offset]);
                        }
                        Object[] translated = new Object[localVariableLength - (methodDescription.isStatic() ? 0 : 1) - methodDescription.getParameters().size() + (this.instrumentedMethod.isStatic() ? 0 : 1) + this.instrumentedMethod.getParameters().size() + additionalTypes.size()];
                        int index = translationMode.copy(this.instrumentedType, this.instrumentedMethod, methodDescription, localVariable, translated);
                        for (TypeDescription typeDescription : additionalTypes) {
                            translated[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                        }
                        System.arraycopy(localVariable, methodDescription.getParameters().size() + (methodDescription.isStatic() ? 0 : 1), translated, index, translated.length - index);
                        localVariableLength = translated.length;
                        localVariable = translated;
                        this.currentFrameDivergence = translated.length - index;
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unexpected frame type: " + type);
                    }
                }
                methodVisitor.visitFrame(type, localVariableLength, localVariable, stackSize, stack);
            }

            protected void injectFullFrame(MethodVisitor methodVisitor, Initialization initialization, List<? extends TypeDescription> typesInArray, List<? extends TypeDescription> typesOnStack) {
                Object[] localVariable = new Object[this.instrumentedMethod.getParameters().size() + (this.instrumentedMethod.isStatic() ? 0 : 1) + typesInArray.size()];
                int index = 0;
                if (!this.instrumentedMethod.isStatic()) {
                    localVariable[index++] = initialization.toFrame(this.instrumentedType);
                }
                for (TypeDescription typeDescription : this.instrumentedMethod.getParameters().asTypeList().asErasures()) {
                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                }
                for (TypeDescription typeDescription : typesInArray) {
                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                }
                index = 0;
                Object[] stackType = new Object[typesOnStack.size()];
                for (TypeDescription typeDescription : typesOnStack) {
                    stackType[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                }
                methodVisitor.visitFrame(this.expandFrames ? -1 : 0, localVariable.length, localVariable, stackType.length, stackType);
                this.currentFrameDivergence = 0;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class ForAdvice
            implements net.bytebuddy.asm.Advice$StackMapFrameHandler$ForAdvice {
                protected final MethodDescription.InDefinedShape adviceMethod;
                protected final List<? extends TypeDescription> startTypes;
                private final List<? extends TypeDescription> intermediateTypes;
                protected final List<? extends TypeDescription> endTypes;
                protected final TranslationMode translationMode;
                private final Initialization initialization;
                private boolean intermedate;

                protected ForAdvice(MethodDescription.InDefinedShape adviceMethod, List<? extends TypeDescription> startTypes, List<? extends TypeDescription> intermediateTypes, List<? extends TypeDescription> endTypes, TranslationMode translationMode, Initialization initialization) {
                    this.adviceMethod = adviceMethod;
                    this.startTypes = startTypes;
                    this.intermediateTypes = intermediateTypes;
                    this.endTypes = endTypes;
                    this.translationMode = translationMode;
                    this.initialization = initialization;
                    this.intermedate = false;
                }

                @Override
                public void translateFrame(MethodVisitor methodVisitor, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                    Default.this.translateFrame(methodVisitor, this.translationMode, this.adviceMethod, this.startTypes, type, localVariableLength, localVariable, stackSize, stack);
                }

                @Override
                public void injectReturnFrame(MethodVisitor methodVisitor) {
                    if (!Default.this.expandFrames && Default.this.currentFrameDivergence == 0) {
                        if (this.adviceMethod.getReturnType().represents(Void.TYPE)) {
                            methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                        } else {
                            methodVisitor.visitFrame(4, EMPTY.length, EMPTY, 1, new Object[]{Initialization.INITIALIZED.toFrame(this.adviceMethod.getReturnType().asErasure())});
                        }
                    } else {
                        Default.this.injectFullFrame(methodVisitor, this.initialization, this.startTypes, this.adviceMethod.getReturnType().represents(Void.TYPE) ? Collections.emptyList() : Collections.singletonList(this.adviceMethod.getReturnType().asErasure()));
                    }
                }

                @Override
                public void injectExceptionFrame(MethodVisitor methodVisitor) {
                    if (!Default.this.expandFrames && Default.this.currentFrameDivergence == 0) {
                        methodVisitor.visitFrame(4, EMPTY.length, EMPTY, 1, new Object[]{Type.getInternalName(Throwable.class)});
                    } else {
                        Default.this.injectFullFrame(methodVisitor, this.initialization, this.startTypes, Collections.singletonList(TypeDescription.ForLoadedType.of(Throwable.class)));
                    }
                }

                @Override
                public void injectCompletionFrame(MethodVisitor methodVisitor) {
                    if (Default.this.expandFrames) {
                        Default.this.injectFullFrame(methodVisitor, this.initialization, CompoundList.of(this.startTypes, this.endTypes), Collections.emptyList());
                    } else if (Default.this.currentFrameDivergence == 0 && (this.intermedate || this.endTypes.size() < 4)) {
                        if (this.intermedate || this.endTypes.isEmpty()) {
                            methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                        } else {
                            Object[] local = new Object[this.endTypes.size()];
                            int index = 0;
                            for (TypeDescription typeDescription : this.endTypes) {
                                local[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                            }
                            methodVisitor.visitFrame(1, local.length, local, EMPTY.length, EMPTY);
                        }
                    } else if (Default.this.currentFrameDivergence < 3 && this.endTypes.isEmpty()) {
                        methodVisitor.visitFrame(2, Default.this.currentFrameDivergence, EMPTY, EMPTY.length, EMPTY);
                        Default.this.currentFrameDivergence = 0;
                    } else {
                        Default.this.injectFullFrame(methodVisitor, this.initialization, CompoundList.of(this.startTypes, this.endTypes), Collections.emptyList());
                    }
                }

                @Override
                public void injectIntermediateFrame(MethodVisitor methodVisitor, List<? extends TypeDescription> stack) {
                    if (Default.this.expandFrames) {
                        Default.this.injectFullFrame(methodVisitor, this.initialization, CompoundList.of(this.startTypes, this.intermediateTypes), stack);
                    } else if (this.intermedate && stack.size() < 2) {
                        if (stack.isEmpty()) {
                            methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                        } else {
                            methodVisitor.visitFrame(4, EMPTY.length, EMPTY, 1, new Object[]{Initialization.INITIALIZED.toFrame(stack.get(0))});
                        }
                    } else if (Default.this.currentFrameDivergence == 0 && this.intermediateTypes.size() < 4 && (stack.isEmpty() || stack.size() < 2 && this.intermediateTypes.isEmpty())) {
                        if (this.intermediateTypes.isEmpty()) {
                            if (stack.isEmpty()) {
                                methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                            } else {
                                methodVisitor.visitFrame(4, EMPTY.length, EMPTY, 1, new Object[]{Initialization.INITIALIZED.toFrame(stack.get(0))});
                            }
                        } else {
                            Object[] local = new Object[this.intermediateTypes.size()];
                            int index = 0;
                            for (TypeDescription typeDescription : this.intermediateTypes) {
                                local[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                            }
                            methodVisitor.visitFrame(1, local.length, local, EMPTY.length, EMPTY);
                        }
                    } else if (Default.this.currentFrameDivergence < 3 && this.intermediateTypes.isEmpty() && stack.isEmpty()) {
                        methodVisitor.visitFrame(2, Default.this.currentFrameDivergence, EMPTY, EMPTY.length, EMPTY);
                    } else {
                        Default.this.injectFullFrame(methodVisitor, this.initialization, CompoundList.of(this.startTypes, this.intermediateTypes), stack);
                    }
                    Default.this.currentFrameDivergence = this.intermediateTypes.size() - this.endTypes.size();
                    this.intermedate = true;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class WithPreservedArguments
            extends Default {
                protected boolean allowCompactCompletionFrame;

                protected WithPreservedArguments(TypeDescription instrumentedType, MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> latentTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes, boolean expandFrames, boolean allowCompactCompletionFrame) {
                    super(instrumentedType, instrumentedMethod, initialTypes, latentTypes, preMethodTypes, postMethodTypes, expandFrames);
                    this.allowCompactCompletionFrame = allowCompactCompletionFrame;
                }

                @Override
                @SuppressFBWarnings(value={"RC_REF_COMPARISON_BAD_PRACTICE"}, justification="ASM models frames by reference identity.")
                protected void translateFrame(MethodVisitor methodVisitor, TranslationMode translationMode, MethodDescription methodDescription, List<? extends TypeDescription> additionalTypes, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                    if (type == 0 && localVariableLength > 0 && localVariable[0] != Opcodes.UNINITIALIZED_THIS) {
                        this.allowCompactCompletionFrame = true;
                    }
                    super.translateFrame(methodVisitor, translationMode, methodDescription, additionalTypes, type, localVariableLength, localVariable, stackSize, stack);
                }

                @Override
                public net.bytebuddy.asm.Advice$StackMapFrameHandler$ForAdvice bindExit(MethodDescription.InDefinedShape adviceMethod) {
                    return new ForAdvice(adviceMethod, CompoundList.of(this.initialTypes, this.preMethodTypes, this.postMethodTypes), Collections.emptyList(), Collections.emptyList(), TranslationMode.EXIT, Initialization.INITIALIZED);
                }

                @Override
                public void injectReturnFrame(MethodVisitor methodVisitor) {
                    if (!this.expandFrames && this.currentFrameDivergence == 0) {
                        if (this.instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                            methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                        } else {
                            methodVisitor.visitFrame(4, EMPTY.length, EMPTY, 1, new Object[]{Initialization.INITIALIZED.toFrame(this.instrumentedMethod.getReturnType().asErasure())});
                        }
                    } else {
                        this.injectFullFrame(methodVisitor, Initialization.INITIALIZED, CompoundList.of(this.initialTypes, this.preMethodTypes), this.instrumentedMethod.getReturnType().represents(Void.TYPE) ? Collections.emptyList() : Collections.singletonList(this.instrumentedMethod.getReturnType().asErasure()));
                    }
                }

                @Override
                public void injectExceptionFrame(MethodVisitor methodVisitor) {
                    if (!this.expandFrames && this.currentFrameDivergence == 0) {
                        methodVisitor.visitFrame(4, EMPTY.length, EMPTY, 1, new Object[]{Type.getInternalName(Throwable.class)});
                    } else {
                        this.injectFullFrame(methodVisitor, Initialization.INITIALIZED, CompoundList.of(this.initialTypes, this.preMethodTypes), Collections.singletonList(TypeDescription.ForLoadedType.of(Throwable.class)));
                    }
                }

                @Override
                public void injectCompletionFrame(MethodVisitor methodVisitor) {
                    if (this.allowCompactCompletionFrame && !this.expandFrames && this.currentFrameDivergence == 0 && this.postMethodTypes.size() < 4) {
                        if (this.postMethodTypes.isEmpty()) {
                            methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                        } else {
                            Object[] local = new Object[this.postMethodTypes.size()];
                            int index = 0;
                            for (TypeDescription typeDescription : this.postMethodTypes) {
                                local[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                            }
                            methodVisitor.visitFrame(1, local.length, local, EMPTY.length, EMPTY);
                        }
                    } else {
                        this.injectFullFrame(methodVisitor, Initialization.INITIALIZED, CompoundList.of(this.initialTypes, this.preMethodTypes, this.postMethodTypes), Collections.emptyList());
                    }
                }

                @Override
                public void injectPostCompletionFrame(MethodVisitor methodVisitor) {
                    if (!this.expandFrames && this.currentFrameDivergence == 0) {
                        methodVisitor.visitFrame(3, EMPTY.length, EMPTY, EMPTY.length, EMPTY);
                    } else {
                        this.injectFullFrame(methodVisitor, Initialization.INITIALIZED, CompoundList.of(this.initialTypes, this.preMethodTypes, this.postMethodTypes), Collections.emptyList());
                    }
                }

                @Override
                public void injectInitializationFrame(MethodVisitor methodVisitor) {
                    if (!this.initialTypes.isEmpty()) {
                        if (!this.expandFrames && this.initialTypes.size() < 4) {
                            Object[] localVariable = new Object[this.initialTypes.size()];
                            int index = 0;
                            for (TypeDescription typeDescription : this.initialTypes) {
                                localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                            }
                            methodVisitor.visitFrame(1, localVariable.length, localVariable, EMPTY.length, EMPTY);
                        } else {
                            Object[] localVariable = new Object[(this.instrumentedMethod.isStatic() ? 0 : 1) + this.instrumentedMethod.getParameters().size() + this.initialTypes.size()];
                            int index = 0;
                            if (this.instrumentedMethod.isConstructor()) {
                                localVariable[index++] = Opcodes.UNINITIALIZED_THIS;
                            } else if (!this.instrumentedMethod.isStatic()) {
                                localVariable[index++] = Initialization.INITIALIZED.toFrame(this.instrumentedType);
                            }
                            for (TypeDescription typeDescription : this.instrumentedMethod.getParameters().asTypeList().asErasures()) {
                                localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                            }
                            for (TypeDescription typeDescription : this.initialTypes) {
                                localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                            }
                            methodVisitor.visitFrame(this.expandFrames ? -1 : 0, localVariable.length, localVariable, EMPTY.length, EMPTY);
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class WithArgumentCopy
                extends WithPreservedArguments {
                    protected WithArgumentCopy(TypeDescription instrumentedType, MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> latentTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes, boolean expandFrames) {
                        super(instrumentedType, instrumentedMethod, initialTypes, latentTypes, preMethodTypes, postMethodTypes, expandFrames, true);
                    }

                    @Override
                    public void injectStartFrame(MethodVisitor methodVisitor) {
                        if (!this.instrumentedMethod.isStatic() || !this.instrumentedMethod.getParameters().isEmpty()) {
                            if (!this.expandFrames && (this.instrumentedMethod.isStatic() ? 0 : 1) + this.instrumentedMethod.getParameters().size() < 4) {
                                Object[] localVariable = new Object[(this.instrumentedMethod.isStatic() ? 0 : 1) + this.instrumentedMethod.getParameters().size()];
                                int index = 0;
                                if (this.instrumentedMethod.isConstructor()) {
                                    localVariable[index++] = Opcodes.UNINITIALIZED_THIS;
                                } else if (!this.instrumentedMethod.isStatic()) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(this.instrumentedType);
                                }
                                for (TypeDescription typeDescription : this.instrumentedMethod.getParameters().asTypeList().asErasures()) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                methodVisitor.visitFrame(1, localVariable.length, localVariable, EMPTY.length, EMPTY);
                            } else {
                                Object[] localVariable = new Object[(this.instrumentedMethod.isStatic() ? 0 : 2) + this.instrumentedMethod.getParameters().size() * 2 + this.initialTypes.size() + this.preMethodTypes.size()];
                                int index = 0;
                                if (this.instrumentedMethod.isConstructor()) {
                                    localVariable[index++] = Opcodes.UNINITIALIZED_THIS;
                                } else if (!this.instrumentedMethod.isStatic()) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(this.instrumentedType);
                                }
                                for (TypeDescription typeDescription : this.instrumentedMethod.getParameters().asTypeList().asErasures()) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                for (TypeDescription typeDescription : this.initialTypes) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                for (TypeDescription typeDescription : this.preMethodTypes) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                if (this.instrumentedMethod.isConstructor()) {
                                    localVariable[index++] = Opcodes.UNINITIALIZED_THIS;
                                } else if (!this.instrumentedMethod.isStatic()) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(this.instrumentedType);
                                }
                                for (TypeDescription typeDescription : this.instrumentedMethod.getParameters().asTypeList().asErasures()) {
                                    localVariable[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                methodVisitor.visitFrame(this.expandFrames ? -1 : 0, localVariable.length, localVariable, EMPTY.length, EMPTY);
                            }
                        }
                        this.currentFrameDivergence = (this.instrumentedMethod.isStatic() ? 0 : 1) + this.instrumentedMethod.getParameters().size();
                    }

                    @Override
                    @SuppressFBWarnings(value={"RC_REF_COMPARISON_BAD_PRACTICE"}, justification="ASM models frames by reference identity.")
                    public void translateFrame(MethodVisitor methodVisitor, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                        switch (type) {
                            case 3: 
                            case 4: {
                                break;
                            }
                            case 1: {
                                this.currentFrameDivergence += localVariableLength;
                                break;
                            }
                            case 2: {
                                this.currentFrameDivergence -= localVariableLength;
                                break;
                            }
                            case -1: 
                            case 0: {
                                Object[] translated = new Object[localVariableLength + (this.instrumentedMethod.isStatic() ? 0 : 1) + this.instrumentedMethod.getParameters().size() + this.initialTypes.size() + this.preMethodTypes.size()];
                                int index = 0;
                                if (this.instrumentedMethod.isConstructor()) {
                                    Initialization initialization = Initialization.INITIALIZED;
                                    for (int variableIndex = 0; variableIndex < localVariableLength; ++variableIndex) {
                                        if (localVariable[variableIndex] != Opcodes.UNINITIALIZED_THIS) continue;
                                        initialization = Initialization.UNITIALIZED;
                                        break;
                                    }
                                    translated[index++] = initialization.toFrame(this.instrumentedType);
                                } else if (!this.instrumentedMethod.isStatic()) {
                                    translated[index++] = Initialization.INITIALIZED.toFrame(this.instrumentedType);
                                }
                                for (TypeDescription typeDescription : this.instrumentedMethod.getParameters().asTypeList().asErasures()) {
                                    translated[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                for (TypeDescription typeDescription : this.initialTypes) {
                                    translated[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                for (TypeDescription typeDescription : this.preMethodTypes) {
                                    translated[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                                }
                                if (localVariableLength > 0) {
                                    System.arraycopy(localVariable, 0, translated, index, localVariableLength);
                                }
                                localVariableLength = translated.length;
                                localVariable = translated;
                                this.currentFrameDivergence = localVariableLength;
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("Unexpected frame type: " + type);
                            }
                        }
                        methodVisitor.visitFrame(type, localVariableLength, localVariable, stackSize, stack);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class WithoutArgumentCopy
                extends WithPreservedArguments {
                    protected WithoutArgumentCopy(TypeDescription instrumentedType, MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> latentTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes, boolean expandFrames, boolean allowCompactCompletionFrame) {
                        super(instrumentedType, instrumentedMethod, initialTypes, latentTypes, preMethodTypes, postMethodTypes, expandFrames, allowCompactCompletionFrame);
                    }

                    @Override
                    public void injectStartFrame(MethodVisitor methodVisitor) {
                    }

                    @Override
                    public void translateFrame(MethodVisitor methodVisitor, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                        this.translateFrame(methodVisitor, TranslationMode.COPY, this.instrumentedMethod, CompoundList.of(this.initialTypes, this.preMethodTypes), type, localVariableLength, localVariable, stackSize, stack);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class Trivial
            extends Default {
                protected Trivial(TypeDescription instrumentedType, MethodDescription instrumentedMethod, List<? extends TypeDescription> latentTypes, boolean expandFrames) {
                    super(instrumentedType, instrumentedMethod, Collections.emptyList(), latentTypes, Collections.emptyList(), Collections.emptyList(), expandFrames);
                }

                @Override
                public void translateFrame(MethodVisitor methodVisitor, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
                    methodVisitor.visitFrame(type, localVariableLength, localVariable, stackSize, stack);
                }

                @Override
                public net.bytebuddy.asm.Advice$StackMapFrameHandler$ForAdvice bindExit(MethodDescription.InDefinedShape adviceMethod) {
                    throw new IllegalStateException("Did not expect exit advice " + adviceMethod + " for " + this.instrumentedMethod);
                }

                @Override
                public void injectReturnFrame(MethodVisitor methodVisitor) {
                    throw new IllegalStateException("Did not expect return frame for " + this.instrumentedMethod);
                }

                @Override
                public void injectExceptionFrame(MethodVisitor methodVisitor) {
                    throw new IllegalStateException("Did not expect exception frame for " + this.instrumentedMethod);
                }

                @Override
                public void injectCompletionFrame(MethodVisitor methodVisitor) {
                    throw new IllegalStateException("Did not expect completion frame for " + this.instrumentedMethod);
                }

                @Override
                public void injectPostCompletionFrame(MethodVisitor methodVisitor) {
                    throw new IllegalStateException("Did not expect post completion frame for " + this.instrumentedMethod);
                }

                @Override
                public void injectInitializationFrame(MethodVisitor methodVisitor) {
                }

                @Override
                public void injectStartFrame(MethodVisitor methodVisitor) {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Initialization {
                UNITIALIZED{

                    protected Object toFrame(TypeDescription typeDescription) {
                        if (typeDescription.isPrimitive()) {
                            throw new IllegalArgumentException("Cannot assume primitive uninitialized value: " + typeDescription);
                        }
                        return Opcodes.UNINITIALIZED_THIS;
                    }
                }
                ,
                INITIALIZED{

                    protected Object toFrame(TypeDescription typeDescription) {
                        if (typeDescription.represents(Boolean.TYPE) || typeDescription.represents(Byte.TYPE) || typeDescription.represents(Short.TYPE) || typeDescription.represents(Character.TYPE) || typeDescription.represents(Integer.TYPE)) {
                            return Opcodes.INTEGER;
                        }
                        if (typeDescription.represents(Long.TYPE)) {
                            return Opcodes.LONG;
                        }
                        if (typeDescription.represents(Float.TYPE)) {
                            return Opcodes.FLOAT;
                        }
                        if (typeDescription.represents(Double.TYPE)) {
                            return Opcodes.DOUBLE;
                        }
                        return typeDescription.getInternalName();
                    }
                };


                protected abstract Object toFrame(TypeDescription var1);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum TranslationMode {
                COPY{

                    protected int copy(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodDescription methodDescription, Object[] localVariable, Object[] translated) {
                        int length = instrumentedMethod.getParameters().size() + (instrumentedMethod.isStatic() ? 0 : 1);
                        System.arraycopy(localVariable, 0, translated, 0, length);
                        return length;
                    }

                    protected boolean isPossibleThisFrameValue(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Object frame) {
                        return instrumentedMethod.isConstructor() && Opcodes.UNINITIALIZED_THIS.equals(frame) || Initialization.INITIALIZED.toFrame(instrumentedType).equals(frame);
                    }
                }
                ,
                ENTER{

                    protected int copy(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodDescription methodDescription, Object[] localVariable, Object[] translated) {
                        int index = 0;
                        if (!instrumentedMethod.isStatic()) {
                            translated[index++] = instrumentedMethod.isConstructor() ? Opcodes.UNINITIALIZED_THIS : Initialization.INITIALIZED.toFrame(instrumentedType);
                        }
                        for (TypeDescription typeDescription : instrumentedMethod.getParameters().asTypeList().asErasures()) {
                            translated[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                        }
                        return index;
                    }

                    protected boolean isPossibleThisFrameValue(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Object frame) {
                        return instrumentedMethod.isConstructor() ? Opcodes.UNINITIALIZED_THIS.equals(frame) : Initialization.INITIALIZED.toFrame(instrumentedType).equals(frame);
                    }
                }
                ,
                EXIT{

                    protected int copy(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodDescription methodDescription, Object[] localVariable, Object[] translated) {
                        int index = 0;
                        if (!instrumentedMethod.isStatic()) {
                            translated[index++] = Initialization.INITIALIZED.toFrame(instrumentedType);
                        }
                        for (TypeDescription typeDescription : instrumentedMethod.getParameters().asTypeList().asErasures()) {
                            translated[index++] = Initialization.INITIALIZED.toFrame(typeDescription);
                        }
                        return index;
                    }

                    protected boolean isPossibleThisFrameValue(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Object frame) {
                        return Initialization.INITIALIZED.toFrame(instrumentedType).equals(frame);
                    }
                };


                protected abstract int copy(TypeDescription var1, MethodDescription var2, MethodDescription var3, Object[] var4, Object[] var5);

                protected abstract boolean isPossibleThisFrameValue(TypeDescription var1, MethodDescription var2, Object var3);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements ForInstrumentedMethod,
        ForAdvice
        {
            INSTANCE;


            @Override
            public ForAdvice bindEnter(MethodDescription.InDefinedShape adviceMethod) {
                return this;
            }

            @Override
            public ForAdvice bindExit(MethodDescription.InDefinedShape adviceMethod) {
                return this;
            }

            @Override
            public int getReaderHint() {
                return 4;
            }

            @Override
            public void translateFrame(MethodVisitor methodVisitor, int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
            }

            @Override
            public void injectReturnFrame(MethodVisitor methodVisitor) {
            }

            @Override
            public void injectExceptionFrame(MethodVisitor methodVisitor) {
            }

            @Override
            public void injectCompletionFrame(MethodVisitor methodVisitor) {
            }

            @Override
            public void injectInitializationFrame(MethodVisitor methodVisitor) {
            }

            @Override
            public void injectStartFrame(MethodVisitor methodVisitor) {
            }

            @Override
            public void injectPostCompletionFrame(MethodVisitor methodVisitor) {
            }

            @Override
            public void injectIntermediateFrame(MethodVisitor methodVisitor, List<? extends TypeDescription> stack) {
            }
        }

        public static interface ForAdvice
        extends StackMapFrameHandler,
        ForPostProcessor {
        }

        public static interface ForInstrumentedMethod
        extends StackMapFrameHandler {
            public ForAdvice bindEnter(MethodDescription.InDefinedShape var1);

            public ForAdvice bindExit(MethodDescription.InDefinedShape var1);

            public int getReaderHint();

            public void injectInitializationFrame(MethodVisitor var1);

            public void injectStartFrame(MethodVisitor var1);

            public void injectPostCompletionFrame(MethodVisitor var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ForPostProcessor {
            public void injectIntermediateFrame(MethodVisitor var1, List<? extends TypeDescription> var2);
        }
    }

    protected static interface MethodSizeHandler {
        public static final int UNDEFINED_SIZE = Short.MAX_VALUE;

        public void requireStackSize(int var1);

        public void requireLocalVariableLength(int var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class Default
        implements ForInstrumentedMethod {
            protected final MethodDescription instrumentedMethod;
            protected final List<? extends TypeDescription> initialTypes;
            protected final List<? extends TypeDescription> preMethodTypes;
            protected final List<? extends TypeDescription> postMethodTypes;
            protected int stackSize;
            protected int localVariableLength;

            protected Default(MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes) {
                this.instrumentedMethod = instrumentedMethod;
                this.initialTypes = initialTypes;
                this.preMethodTypes = preMethodTypes;
                this.postMethodTypes = postMethodTypes;
            }

            protected static ForInstrumentedMethod of(MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes, boolean copyArguments, int writerFlags) {
                if ((writerFlags & 3) != 0) {
                    return NoOp.INSTANCE;
                }
                if (copyArguments) {
                    return new WithCopiedArguments(instrumentedMethod, initialTypes, preMethodTypes, postMethodTypes);
                }
                return new WithRetainedArguments(instrumentedMethod, initialTypes, preMethodTypes, postMethodTypes);
            }

            @Override
            public net.bytebuddy.asm.Advice$MethodSizeHandler$ForAdvice bindEnter(MethodDescription.InDefinedShape adviceMethod) {
                return new ForAdvice(adviceMethod, this.instrumentedMethod.getStackSize() + StackSize.of(this.initialTypes));
            }

            @Override
            public void requireStackSize(int stackSize) {
                this.stackSize = Math.max(this.stackSize, stackSize);
            }

            @Override
            public void requireLocalVariableLength(int localVariableLength) {
                this.localVariableLength = Math.max(this.localVariableLength, localVariableLength);
            }

            @Override
            public int compoundStackSize(int stackSize) {
                return Math.max(this.stackSize, stackSize);
            }

            @Override
            public int compoundLocalVariableLength(int localVariableLength) {
                return Math.max(this.localVariableLength, localVariableLength + StackSize.of(this.postMethodTypes) + StackSize.of(this.initialTypes) + StackSize.of(this.preMethodTypes));
            }

            protected class ForAdvice
            implements net.bytebuddy.asm.Advice$MethodSizeHandler$ForAdvice {
                private final MethodDescription.InDefinedShape adviceMethod;
                private final int baseLocalVariableLength;
                private int stackSizePadding;
                private int localVariableLengthPadding;

                protected ForAdvice(MethodDescription.InDefinedShape adviceMethod, int baseLocalVariableLength) {
                    this.adviceMethod = adviceMethod;
                    this.baseLocalVariableLength = baseLocalVariableLength;
                }

                public void requireStackSize(int stackSize) {
                    Default.this.requireStackSize(stackSize);
                }

                public void requireLocalVariableLength(int localVariableLength) {
                    Default.this.requireLocalVariableLength(localVariableLength);
                }

                public void requireStackSizePadding(int stackSizePadding) {
                    this.stackSizePadding = Math.max(this.stackSizePadding, stackSizePadding);
                }

                public void requireLocalVariableLengthPadding(int localVariableLengthPadding) {
                    this.localVariableLengthPadding = Math.max(this.localVariableLengthPadding, localVariableLengthPadding);
                }

                public void recordMaxima(int stackSize, int localVariableLength) {
                    Default.this.requireStackSize(stackSize + this.stackSizePadding);
                    Default.this.requireLocalVariableLength(localVariableLength - this.adviceMethod.getStackSize() + this.baseLocalVariableLength + this.localVariableLengthPadding);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class WithCopiedArguments
            extends Default {
                protected WithCopiedArguments(MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes) {
                    super(instrumentedMethod, initialTypes, preMethodTypes, postMethodTypes);
                }

                @Override
                public net.bytebuddy.asm.Advice$MethodSizeHandler$ForAdvice bindExit(MethodDescription.InDefinedShape adviceMethod) {
                    return new ForAdvice(adviceMethod, 2 * this.instrumentedMethod.getStackSize() + StackSize.of(this.initialTypes) + StackSize.of(this.preMethodTypes) + StackSize.of(this.postMethodTypes));
                }

                @Override
                public int compoundLocalVariableLength(int localVariableLength) {
                    return Math.max(this.localVariableLength, localVariableLength + this.instrumentedMethod.getStackSize() + StackSize.of(this.postMethodTypes) + StackSize.of(this.initialTypes) + StackSize.of(this.preMethodTypes));
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class WithRetainedArguments
            extends Default {
                protected WithRetainedArguments(MethodDescription instrumentedMethod, List<? extends TypeDescription> initialTypes, List<? extends TypeDescription> preMethodTypes, List<? extends TypeDescription> postMethodTypes) {
                    super(instrumentedMethod, initialTypes, preMethodTypes, postMethodTypes);
                }

                @Override
                public net.bytebuddy.asm.Advice$MethodSizeHandler$ForAdvice bindExit(MethodDescription.InDefinedShape adviceMethod) {
                    return new ForAdvice(adviceMethod, this.instrumentedMethod.getStackSize() + StackSize.of(this.postMethodTypes) + StackSize.of(this.initialTypes) + StackSize.of(this.preMethodTypes));
                }

                @Override
                public int compoundLocalVariableLength(int localVariableLength) {
                    return Math.max(this.localVariableLength, localVariableLength + StackSize.of(this.postMethodTypes) + StackSize.of(this.initialTypes) + StackSize.of(this.preMethodTypes));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements ForInstrumentedMethod,
        ForAdvice
        {
            INSTANCE;


            @Override
            public ForAdvice bindEnter(MethodDescription.InDefinedShape adviceMethod) {
                return this;
            }

            @Override
            public ForAdvice bindExit(MethodDescription.InDefinedShape adviceMethod) {
                return this;
            }

            @Override
            public int compoundStackSize(int stackSize) {
                return Short.MAX_VALUE;
            }

            @Override
            public int compoundLocalVariableLength(int localVariableLength) {
                return Short.MAX_VALUE;
            }

            @Override
            public void requireStackSize(int stackSize) {
            }

            @Override
            public void requireLocalVariableLength(int localVariableLength) {
            }

            @Override
            public void requireStackSizePadding(int stackSizePadding) {
            }

            @Override
            public void requireLocalVariableLengthPadding(int localVariableLengthPadding) {
            }

            @Override
            public void recordMaxima(int stackSize, int localVariableLength) {
            }
        }

        public static interface ForAdvice
        extends MethodSizeHandler {
            public void requireStackSizePadding(int var1);

            public void requireLocalVariableLengthPadding(int var1);

            public void recordMaxima(int var1, int var2);
        }

        public static interface ForInstrumentedMethod
        extends MethodSizeHandler {
            public ForAdvice bindEnter(MethodDescription.InDefinedShape var1);

            public ForAdvice bindExit(MethodDescription.InDefinedShape var1);

            public int compoundStackSize(int var1);

            public int compoundLocalVariableLength(int var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface BootstrapArgumentResolver {
        public List<JavaConstant> resolve(TypeDescription var1, MethodDescription var2);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForDefaultValues
        implements BootstrapArgumentResolver {
            private final MethodDescription.InDefinedShape adviceMethod;
            private final boolean exit;

            protected ForDefaultValues(MethodDescription.InDefinedShape adviceMethod, boolean exit) {
                this.adviceMethod = adviceMethod;
                this.exit = exit;
            }

            @Override
            public List<JavaConstant> resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                if (instrumentedMethod.isTypeInitializer()) {
                    return Arrays.asList(JavaConstant.Simple.ofLoaded(this.adviceMethod.getDeclaringType().getName()), JavaConstant.Simple.ofLoaded(this.exit ? 1 : 0), JavaConstant.Simple.of(instrumentedType), JavaConstant.Simple.ofLoaded(instrumentedMethod.getInternalName()));
                }
                return Arrays.asList(JavaConstant.Simple.ofLoaded(this.adviceMethod.getDeclaringType().getName()), JavaConstant.Simple.ofLoaded(this.exit ? 1 : 0), JavaConstant.Simple.of(instrumentedType), JavaConstant.Simple.ofLoaded(instrumentedMethod.getInternalName()), JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)instrumentedMethod.asDefined()));
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
                if (this.exit != ((ForDefaultValues)object).exit) {
                    return false;
                }
                return this.adviceMethod.equals(((ForDefaultValues)object).adviceMethod);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.adviceMethod.hashCode()) * 31 + this.exit;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Factory implements net.bytebuddy.asm.Advice$BootstrapArgumentResolver$Factory
            {
                INSTANCE;


                @Override
                public BootstrapArgumentResolver resolve(MethodDescription.InDefinedShape adviceMethod, boolean exit) {
                    return new ForDefaultValues(adviceMethod, exit);
                }
            }
        }

        public static interface Factory {
            public BootstrapArgumentResolver resolve(MethodDescription.InDefinedShape var1, boolean var2);
        }
    }

    protected static interface Delegator {
        public StackManipulation apply(TypeDescription var1, MethodDescription var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForDynamicInvocation
        implements Delegator {
            private final MethodDescription.InDefinedShape bootstrapMethod;
            private final MethodDescription.InDefinedShape adviceMethod;
            private final BootstrapArgumentResolver resolver;

            protected ForDynamicInvocation(MethodDescription.InDefinedShape bootstrapMethod, MethodDescription.InDefinedShape adviceMethod, BootstrapArgumentResolver resolver) {
                this.bootstrapMethod = bootstrapMethod;
                this.adviceMethod = adviceMethod;
                this.resolver = resolver;
            }

            protected static net.bytebuddy.asm.Advice$Delegator$Factory of(MethodDescription.InDefinedShape bootstrapMethod, BootstrapArgumentResolver.Factory resolverFactory) {
                if (!bootstrapMethod.isInvokeBootstrap()) {
                    throw new IllegalArgumentException("Not a suitable bootstrap target: " + bootstrapMethod);
                }
                return new Factory(bootstrapMethod, resolverFactory);
            }

            public StackManipulation apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                List<JavaConstant> constants = this.resolver.resolve(instrumentedType, instrumentedMethod);
                if (!this.bootstrapMethod.isInvokeBootstrap(TypeList.Explicit.of(constants))) {
                    throw new IllegalStateException("Cannot invoke " + this.bootstrapMethod + " with arguments: " + constants);
                }
                return MethodInvocation.invoke(this.bootstrapMethod).dynamic(this.adviceMethod.getInternalName(), this.adviceMethod.getReturnType().asErasure(), this.adviceMethod.getParameters().asTypeList().asErasures(), constants);
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
                if (!this.bootstrapMethod.equals(((ForDynamicInvocation)object).bootstrapMethod)) {
                    return false;
                }
                if (!this.adviceMethod.equals(((ForDynamicInvocation)object).adviceMethod)) {
                    return false;
                }
                return this.resolver.equals(((ForDynamicInvocation)object).resolver);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.bootstrapMethod.hashCode()) * 31 + this.adviceMethod.hashCode()) * 31 + this.resolver.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.asm.Advice$Delegator$Factory {
                private final MethodDescription.InDefinedShape bootstrapMethod;
                private final BootstrapArgumentResolver.Factory resolverFactory;

                protected Factory(MethodDescription.InDefinedShape bootstrapMethod, BootstrapArgumentResolver.Factory resolverFactory) {
                    this.bootstrapMethod = bootstrapMethod;
                    this.resolverFactory = resolverFactory;
                }

                public Delegator make(MethodDescription.InDefinedShape adviceMethod, boolean exit) {
                    return new ForDynamicInvocation(this.bootstrapMethod, adviceMethod, this.resolverFactory.resolve(adviceMethod, exit));
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
                    if (!this.bootstrapMethod.equals(((Factory)object).bootstrapMethod)) {
                        return false;
                    }
                    return this.resolverFactory.equals(((Factory)object).resolverFactory);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.bootstrapMethod.hashCode()) * 31 + this.resolverFactory.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForRegularInvocation
        implements Delegator {
            private final MethodDescription.InDefinedShape adviceMethod;

            protected ForRegularInvocation(MethodDescription.InDefinedShape adviceMethod) {
                this.adviceMethod = adviceMethod;
            }

            public StackManipulation apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                return MethodInvocation.invoke(this.adviceMethod);
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
                return this.adviceMethod.equals(((ForRegularInvocation)object).adviceMethod);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.adviceMethod.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$Delegator$Factory
            {
                INSTANCE;


                @Override
                public Delegator make(MethodDescription.InDefinedShape adviceMethod, boolean exit) {
                    return new ForRegularInvocation(adviceMethod);
                }
            }
        }

        public static interface Factory {
            public Delegator make(MethodDescription.InDefinedShape var1, boolean var2);
        }
    }

    public static interface PostProcessor {
        public StackManipulation resolve(TypeDescription var1, MethodDescription var2, Assigner var3, ArgumentHandler var4, StackMapFrameHandler.ForPostProcessor var5, StackManipulation var6);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements PostProcessor {
            private final List<PostProcessor> postProcessors;

            protected Compound(List<PostProcessor> postProcessors) {
                this.postProcessors = postProcessors;
            }

            @Override
            public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler, StackManipulation exceptionHandler) {
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(this.postProcessors.size());
                for (PostProcessor postProcessor : this.postProcessors) {
                    stackManipulations.add(postProcessor.resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, stackMapFrameHandler, exceptionHandler));
                }
                return new StackManipulation.Compound(stackManipulations);
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
                return ((Object)this.postProcessors).equals(((Compound)object).postProcessors);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.postProcessors).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements PostProcessor,
        Factory
        {
            INSTANCE;


            @Override
            public StackManipulation resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, StackMapFrameHandler.ForPostProcessor stackMapFrameHandler, StackManipulation exceptionHandler) {
                return StackManipulation.Trivial.INSTANCE;
            }

            @Override
            public PostProcessor make(MethodDescription.InDefinedShape advice, boolean exit) {
                return this;
            }
        }

        public static interface Factory {
            public PostProcessor make(MethodDescription.InDefinedShape var1, boolean var2);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Compound
            implements Factory {
                private final List<Factory> factories = new ArrayList<Factory>();

                public Compound(Factory ... factory) {
                    this(Arrays.asList(factory));
                }

                public Compound(List<? extends Factory> factories) {
                    for (Factory factory : factories) {
                        if (factory instanceof Compound) {
                            this.factories.addAll(((Compound)factory).factories);
                            continue;
                        }
                        if (factory instanceof NoOp) continue;
                        this.factories.add(factory);
                    }
                }

                @Override
                public PostProcessor make(MethodDescription.InDefinedShape advice, boolean exit) {
                    ArrayList<PostProcessor> postProcessors = new ArrayList<PostProcessor>(this.factories.size());
                    for (Factory factory : this.factories) {
                        postProcessors.add(factory.make(advice, exit));
                    }
                    return new net.bytebuddy.asm.Advice$PostProcessor$Compound(postProcessors);
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
                    return ((Object)this.factories).equals(((Compound)object).factories);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.factories).hashCode();
                }
            }
        }
    }

    public static interface ArgumentHandler {
        public static final int THIS_REFERENCE = 0;

        public int argument(int var1);

        public int exit();

        public int enter();

        public int named(String var1);

        public int returned();

        public int thrown();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Factory {
            SIMPLE{

                @Override
                protected ForInstrumentedMethod resolve(MethodDescription instrumentedMethod, TypeDefinition enterType, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes) {
                    return new ForInstrumentedMethod.Default.Simple(instrumentedMethod, exitType, namedTypes, enterType);
                }
            }
            ,
            COPYING{

                @Override
                protected ForInstrumentedMethod resolve(MethodDescription instrumentedMethod, TypeDefinition enterType, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes) {
                    return new ForInstrumentedMethod.Default.Copying(instrumentedMethod, exitType, namedTypes, enterType);
                }
            };


            protected abstract ForInstrumentedMethod resolve(MethodDescription var1, TypeDefinition var2, TypeDefinition var3, SortedMap<String, TypeDefinition> var4);
        }

        public static interface ForAdvice
        extends ArgumentHandler {
            public int mapped(int var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class Default
            implements ForAdvice {
                protected final MethodDescription instrumentedMethod;
                protected final MethodDescription adviceMethod;
                protected final TypeDefinition exitType;
                protected final SortedMap<String, TypeDefinition> namedTypes;

                protected Default(MethodDescription instrumentedMethod, MethodDescription adviceMethod, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes) {
                    this.instrumentedMethod = instrumentedMethod;
                    this.adviceMethod = adviceMethod;
                    this.exitType = exitType;
                    this.namedTypes = namedTypes;
                }

                @Override
                public int argument(int offset) {
                    return offset;
                }

                @Override
                public int exit() {
                    return this.instrumentedMethod.getStackSize();
                }

                @Override
                public int named(String name) {
                    return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.headMap(name).values());
                }

                @Override
                public int enter() {
                    return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values());
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class ForMethodExit
                extends Default {
                    private final TypeDefinition enterType;
                    private final StackSize throwableSize;

                    protected ForMethodExit(MethodDescription instrumentedMethod, MethodDescription adviceMethod, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes, TypeDefinition enterType, StackSize throwableSize) {
                        super(instrumentedMethod, adviceMethod, exitType, namedTypes);
                        this.enterType = enterType;
                        this.throwableSize = throwableSize;
                    }

                    @Override
                    public int returned() {
                        return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize();
                    }

                    @Override
                    public int thrown() {
                        return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize() + this.instrumentedMethod.getReturnType().getStackSize().getSize();
                    }

                    @Override
                    public int mapped(int offset) {
                        return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize() + this.instrumentedMethod.getReturnType().getStackSize().getSize() + this.throwableSize.getSize() - this.adviceMethod.getStackSize() + offset;
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
                        if (!this.throwableSize.equals((Object)((ForMethodExit)object).throwableSize)) {
                            return false;
                        }
                        return this.enterType.equals(((ForMethodExit)object).enterType);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.enterType.hashCode()) * 31 + this.throwableSize.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class ForMethodEnter
                extends Default {
                    protected ForMethodEnter(MethodDescription instrumentedMethod, MethodDescription adviceMethod, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes) {
                        super(instrumentedMethod, adviceMethod, exitType, namedTypes);
                    }

                    @Override
                    public int returned() {
                        throw new IllegalStateException("Cannot resolve the return value offset during enter advice");
                    }

                    @Override
                    public int thrown() {
                        throw new IllegalStateException("Cannot resolve the thrown value offset during enter advice");
                    }

                    @Override
                    public int mapped(int offset) {
                        return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) - this.adviceMethod.getStackSize() + offset;
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
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ForInstrumentedMethod
        extends ArgumentHandler {
            public int prepare(MethodVisitor var1);

            public ForAdvice bindEnter(MethodDescription var1);

            public ForAdvice bindExit(MethodDescription var1, boolean var2);

            public boolean isCopyingArguments();

            public List<TypeDescription> getNamedTypes();

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class Default
            implements ForInstrumentedMethod {
                protected final MethodDescription instrumentedMethod;
                protected final TypeDefinition exitType;
                protected final SortedMap<String, TypeDefinition> namedTypes;
                protected final TypeDefinition enterType;

                protected Default(MethodDescription instrumentedMethod, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes, TypeDefinition enterType) {
                    this.instrumentedMethod = instrumentedMethod;
                    this.namedTypes = namedTypes;
                    this.exitType = exitType;
                    this.enterType = enterType;
                }

                @Override
                public int exit() {
                    return this.instrumentedMethod.getStackSize();
                }

                @Override
                public int named(String name) {
                    return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.headMap(name).values());
                }

                @Override
                public int enter() {
                    return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values());
                }

                @Override
                public int returned() {
                    return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize();
                }

                @Override
                public int thrown() {
                    return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize() + this.instrumentedMethod.getReturnType().getStackSize().getSize();
                }

                @Override
                public ForAdvice bindEnter(MethodDescription adviceMethod) {
                    return new ForAdvice.Default.ForMethodEnter(this.instrumentedMethod, adviceMethod, this.exitType, this.namedTypes);
                }

                @Override
                public ForAdvice bindExit(MethodDescription adviceMethod, boolean skipThrowable) {
                    return new ForAdvice.Default.ForMethodExit(this.instrumentedMethod, adviceMethod, this.exitType, this.namedTypes, this.enterType, skipThrowable ? StackSize.ZERO : StackSize.SINGLE);
                }

                @Override
                public List<TypeDescription> getNamedTypes() {
                    ArrayList<TypeDescription> namedTypes = new ArrayList<TypeDescription>(this.namedTypes.size());
                    for (TypeDefinition typeDefinition : this.namedTypes.values()) {
                        namedTypes.add(typeDefinition.asErasure());
                    }
                    return namedTypes;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class Copying
                extends Default {
                    protected Copying(MethodDescription instrumentedMethod, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes, TypeDefinition enterType) {
                        super(instrumentedMethod, exitType, namedTypes, enterType);
                    }

                    @Override
                    public int argument(int offset) {
                        return this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize() + offset;
                    }

                    @Override
                    public boolean isCopyingArguments() {
                        return true;
                    }

                    @Override
                    public int prepare(MethodVisitor methodVisitor) {
                        StackSize stackSize;
                        if (!this.instrumentedMethod.isStatic()) {
                            methodVisitor.visitVarInsn(25, 0);
                            methodVisitor.visitVarInsn(58, this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize());
                            stackSize = StackSize.SINGLE;
                        } else {
                            stackSize = StackSize.ZERO;
                        }
                        for (ParameterDescription parameterDescription : this.instrumentedMethod.getParameters()) {
                            Type type = Type.getType(parameterDescription.getType().asErasure().getDescriptor());
                            methodVisitor.visitVarInsn(type.getOpcode(21), parameterDescription.getOffset());
                            methodVisitor.visitVarInsn(type.getOpcode(54), this.instrumentedMethod.getStackSize() + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize() + parameterDescription.getOffset());
                            stackSize = stackSize.maximum(parameterDescription.getType().getStackSize());
                        }
                        return stackSize.getSize();
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
                @HashCodeAndEqualsPlugin.Enhance
                protected static class Simple
                extends Default {
                    protected Simple(MethodDescription instrumentedMethod, TypeDefinition exitType, SortedMap<String, TypeDefinition> namedTypes, TypeDefinition enterType) {
                        super(instrumentedMethod, exitType, namedTypes, enterType);
                    }

                    @Override
                    public int argument(int offset) {
                        return offset < this.instrumentedMethod.getStackSize() ? offset : offset + this.exitType.getStackSize().getSize() + StackSize.of(this.namedTypes.values()) + this.enterType.getStackSize().getSize();
                    }

                    @Override
                    public boolean isCopyingArguments() {
                        return false;
                    }

                    @Override
                    public int prepare(MethodVisitor methodVisitor) {
                        return 0;
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
            }
        }
    }

    public static interface OffsetMapping {
        public Target resolve(TypeDescription var1, MethodDescription var2, Assigner var3, ArgumentHandler var4, Sort var5);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForSerializedValue
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final TypeDescription typeDescription;
            private final StackManipulation deserialization;

            public ForSerializedValue(TypeDescription.Generic target, TypeDescription typeDescription, StackManipulation deserialization) {
                this.target = target;
                this.typeDescription = typeDescription;
                this.deserialization = deserialization;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation assignment = assigner.assign(this.typeDescription.asGenericType(), this.target, Assigner.Typing.DYNAMIC);
                if (!assignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.typeDescription + " to " + this.target);
                }
                return new Target.ForStackManipulation(new StackManipulation.Compound(this.deserialization, assignment));
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
                if (!this.target.equals(((ForSerializedValue)object).target)) {
                    return false;
                }
                if (!this.typeDescription.equals(((ForSerializedValue)object).typeDescription)) {
                    return false;
                }
                return this.deserialization.equals(((ForSerializedValue)object).deserialization);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.deserialization.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Factory<T extends Annotation>
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                private final Class<T> annotationType;
                private final TypeDescription typeDescription;
                private final StackManipulation deserialization;

                protected Factory(Class<T> annotationType, TypeDescription typeDescription, StackManipulation deserialization) {
                    this.annotationType = annotationType;
                    this.typeDescription = typeDescription;
                    this.deserialization = deserialization;
                }

                public static <S extends Annotation, U extends Serializable> net.bytebuddy.asm.Advice$OffsetMapping$Factory<S> of(Class<S> annotationType, U target, Class<? super U> targetType) {
                    if (!targetType.isInstance(target)) {
                        throw new IllegalArgumentException(target + " is no instance of " + targetType);
                    }
                    return new Factory<S>(annotationType, TypeDescription.ForLoadedType.of(targetType), SerializedConstant.of(target));
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                    return new ForSerializedValue(target.getType(), this.typeDescription, this.deserialization);
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
                    if (!this.annotationType.equals(((Factory)object).annotationType)) {
                        return false;
                    }
                    if (!this.typeDescription.equals(((Factory)object).typeDescription)) {
                        return false;
                    }
                    return this.deserialization.equals(((Factory)object).deserialization);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.deserialization.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForStackManipulation
        implements OffsetMapping {
            private final StackManipulation stackManipulation;
            private final TypeDescription.Generic typeDescription;
            private final TypeDescription.Generic targetType;
            private final Assigner.Typing typing;

            public ForStackManipulation(StackManipulation stackManipulation, TypeDescription.Generic typeDescription, TypeDescription.Generic targetType, Assigner.Typing typing) {
                this.stackManipulation = stackManipulation;
                this.typeDescription = typeDescription;
                this.targetType = targetType;
                this.typing = typing;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation assignment = assigner.assign(this.typeDescription, this.targetType, this.typing);
                if (!assignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.typeDescription + " to " + this.targetType);
                }
                return new Target.ForStackManipulation(new StackManipulation.Compound(this.stackManipulation, assignment));
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
                if (!this.typing.equals((Object)((ForStackManipulation)object).typing)) {
                    return false;
                }
                if (!this.stackManipulation.equals(((ForStackManipulation)object).stackManipulation)) {
                    return false;
                }
                if (!this.typeDescription.equals(((ForStackManipulation)object).typeDescription)) {
                    return false;
                }
                return this.targetType.equals(((ForStackManipulation)object).targetType);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.targetType.hashCode()) * 31 + this.typing.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfDynamicInvocation<T extends Annotation>
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                private final Class<T> annotationType;
                private final MethodDescription.InDefinedShape bootstrapMethod;
                private final List<? extends JavaConstant> arguments;

                public OfDynamicInvocation(Class<T> annotationType, MethodDescription.InDefinedShape bootstrapMethod, List<? extends JavaConstant> arguments) {
                    this.annotationType = annotationType;
                    this.bootstrapMethod = bootstrapMethod;
                    this.arguments = arguments;
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                    if (!target.getType().isInterface()) {
                        throw new IllegalArgumentException(target.getType() + " is not an interface");
                    }
                    if (!target.getType().getInterfaces().isEmpty()) {
                        throw new IllegalArgumentException(target.getType() + " must not extend other interfaces");
                    }
                    if (!target.getType().isPublic()) {
                        throw new IllegalArgumentException(target.getType() + " is mot public");
                    }
                    MethodList methodCandidates = (MethodList)target.getType().getDeclaredMethods().filter(ElementMatchers.isAbstract());
                    if (methodCandidates.size() != 1) {
                        throw new IllegalArgumentException(target.getType() + " must declare exactly one abstract method");
                    }
                    return new ForStackManipulation(MethodInvocation.invoke(this.bootstrapMethod).dynamic(((MethodDescription)methodCandidates.getOnly()).getInternalName(), target.getType().asErasure(), Collections.emptyList(), this.arguments), target.getType(), target.getType(), Assigner.Typing.STATIC);
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
                    if (!this.annotationType.equals(((OfDynamicInvocation)object).annotationType)) {
                        return false;
                    }
                    if (!this.bootstrapMethod.equals(((OfDynamicInvocation)object).bootstrapMethod)) {
                        return false;
                    }
                    return ((Object)this.arguments).equals(((OfDynamicInvocation)object).arguments);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.bootstrapMethod.hashCode()) * 31 + ((Object)this.arguments).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfAnnotationProperty<T extends Annotation>
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                private final Class<T> annotationType;
                private final MethodDescription.InDefinedShape property;

                protected OfAnnotationProperty(Class<T> annotationType, MethodDescription.InDefinedShape property) {
                    this.annotationType = annotationType;
                    this.property = property;
                }

                public static <S extends Annotation> net.bytebuddy.asm.Advice$OffsetMapping$Factory<S> of(Class<S> annotationType, String property) {
                    if (!annotationType.isAnnotation()) {
                        throw new IllegalArgumentException("Not an annotation type: " + annotationType);
                    }
                    try {
                        return new OfAnnotationProperty<S>(annotationType, new MethodDescription.ForLoadedMethod(annotationType.getMethod(property, new Class[0])));
                    }
                    catch (NoSuchMethodException exception) {
                        throw new IllegalArgumentException("Cannot find a property " + property + " on " + annotationType, exception);
                    }
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                    ConstantValue value = ConstantValue.Simple.wrapOrNull(annotation.getValue(this.property).resolve());
                    if (value == null) {
                        throw new IllegalStateException("Property does not represent a constant value: " + this.property);
                    }
                    return new ForStackManipulation(value.toStackManipulation(), value.getTypeDescription().asGenericType(), target.getType(), Assigner.Typing.STATIC);
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
                    if (!this.annotationType.equals(((OfAnnotationProperty)object).annotationType)) {
                        return false;
                    }
                    return this.property.equals(((OfAnnotationProperty)object).property);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.property.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfDefaultValue<T extends Annotation>
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                private final Class<T> annotationType;

                public OfDefaultValue(Class<T> annotationType) {
                    this.annotationType = annotationType;
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                    return new ForStackManipulation(DefaultValue.of(target.getType()), target.getType(), target.getType(), Assigner.Typing.STATIC);
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
                    return this.annotationType.equals(((OfDefaultValue)object).annotationType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.annotationType.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Factory<T extends Annotation>
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                private final Class<T> annotationType;
                private final StackManipulation stackManipulation;
                private final TypeDescription.Generic typeDescription;

                public Factory(Class<T> annotationType, TypeDescription typeDescription) {
                    this(annotationType, ClassConstant.of(typeDescription), TypeDescription.ForLoadedType.of(Class.class).asGenericType());
                }

                public Factory(Class<T> annotationType, EnumerationDescription enumerationDescription) {
                    this(annotationType, FieldAccess.forEnumeration(enumerationDescription), enumerationDescription.getEnumerationType().asGenericType());
                }

                public Factory(Class<T> annotationType, ConstantValue constant) {
                    this(annotationType, constant.toStackManipulation(), constant.getTypeDescription().asGenericType());
                }

                public Factory(Class<T> annotationType, StackManipulation stackManipulation, TypeDescription.Generic typeDescription) {
                    this.annotationType = annotationType;
                    this.stackManipulation = stackManipulation;
                    this.typeDescription = typeDescription;
                }

                public static <S extends Annotation> net.bytebuddy.asm.Advice$OffsetMapping$Factory<S> of(Class<S> annotationType, @MaybeNull Object value) {
                    return value == null ? new OfDefaultValue<S>(annotationType) : new Factory<S>(annotationType, ConstantValue.Simple.wrap(value));
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                    return new ForStackManipulation(this.stackManipulation, this.typeDescription, target.getType(), Assigner.Typing.STATIC);
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
                    if (!this.annotationType.equals(((Factory)object).annotationType)) {
                        return false;
                    }
                    if (!this.stackManipulation.equals(((Factory)object).stackManipulation)) {
                        return false;
                    }
                    return this.typeDescription.equals(((Factory)object).typeDescription);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForThrowable
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final boolean readOnly;
            private final Assigner.Typing typing;

            protected ForThrowable(TypeDescription.Generic target, AnnotationDescription.Loadable<Thrown> annotation) {
                this(target, annotation.getValue(Factory.THROWN_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.THROWN_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class));
            }

            public ForThrowable(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing) {
                this.target = target;
                this.readOnly = readOnly;
                this.typing = typing;
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation readAssignment = assigner.assign(TypeDescription.ForLoadedType.of(Throwable.class).asGenericType(), this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign Throwable to " + this.target);
                }
                if (this.readOnly) {
                    return new Target.ForVariable.ReadOnly(TypeDescription.ForLoadedType.of(Throwable.class), argumentHandler.thrown(), readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, TypeDescription.ForLoadedType.of(Throwable.class).asGenericType(), this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.target + " to Throwable");
                }
                return new Target.ForVariable.ReadWrite(TypeDescription.ForLoadedType.of(Throwable.class), argumentHandler.thrown(), readAssignment, writeAssignment);
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
                if (this.readOnly != ((ForThrowable)object).readOnly) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForThrowable)object).typing)) {
                    return false;
                }
                return this.target.equals(((ForThrowable)object).target);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Thrown>
            {
                INSTANCE;

                private static final MethodDescription.InDefinedShape THROWN_READ_ONLY;
                private static final MethodDescription.InDefinedShape THROWN_TYPING;

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming annotation for exit advice.")
                protected static net.bytebuddy.asm.Advice$OffsetMapping$Factory<?> of(MethodDescription.InDefinedShape adviceMethod) {
                    return adviceMethod.getDeclaredAnnotations().ofType(OnMethodExit.class).getValue(ON_THROWABLE).resolve(TypeDescription.class).represents((java.lang.reflect.Type)((Object)NoExceptionHandler.class)) ? new Factory.Illegal<Thrown>(Thrown.class) : INSTANCE;
                }

                @Override
                public Class<Thrown> getAnnotationType() {
                    return Thrown.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Thrown> annotation, Factory.AdviceType adviceType) {
                    if (adviceType.isDelegation() && !annotation.getValue(THROWN_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                        throw new IllegalStateException("Cannot use writable " + target + " on read-only parameter");
                    }
                    return new ForThrowable(target.getType(), annotation);
                }

                static {
                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Thrown.class).getDeclaredMethods();
                    THROWN_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                    THROWN_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForReturnValue
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final boolean readOnly;
            private final Assigner.Typing typing;

            protected ForReturnValue(TypeDescription.Generic target, AnnotationDescription.Loadable<Return> annotation) {
                this(target, annotation.getValue(Factory.RETURN_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.RETURN_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class));
            }

            public ForReturnValue(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing) {
                this.target = target;
                this.readOnly = readOnly;
                this.typing = typing;
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation readAssignment = assigner.assign(instrumentedMethod.getReturnType(), this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + instrumentedMethod.getReturnType() + " to " + this.target);
                }
                if (this.readOnly) {
                    return instrumentedMethod.getReturnType().represents(Void.TYPE) ? new Target.ForDefaultValue.ReadOnly(this.target) : new Target.ForVariable.ReadOnly(instrumentedMethod.getReturnType(), argumentHandler.returned(), readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, instrumentedMethod.getReturnType(), this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.target + " to " + instrumentedMethod.getReturnType());
                }
                return instrumentedMethod.getReturnType().represents(Void.TYPE) ? new Target.ForDefaultValue.ReadWrite(this.target) : new Target.ForVariable.ReadWrite(instrumentedMethod.getReturnType(), argumentHandler.returned(), readAssignment, writeAssignment);
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
                if (this.readOnly != ((ForReturnValue)object).readOnly) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForReturnValue)object).typing)) {
                    return false;
                }
                return this.target.equals(((ForReturnValue)object).target);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Return>
            {
                INSTANCE;

                private static final MethodDescription.InDefinedShape RETURN_READ_ONLY;
                private static final MethodDescription.InDefinedShape RETURN_TYPING;

                @Override
                public Class<Return> getAnnotationType() {
                    return Return.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Return> annotation, Factory.AdviceType adviceType) {
                    if (adviceType.isDelegation() && !annotation.getValue(RETURN_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                        throw new IllegalStateException("Cannot write return value for " + target + " in read-only context");
                    }
                    return new ForReturnValue(target.getType(), annotation);
                }

                static {
                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Return.class).getDeclaredMethods();
                    RETURN_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                    RETURN_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForLocalValue
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final TypeDescription.Generic localType;
            private final String name;

            public ForLocalValue(TypeDescription.Generic target, TypeDescription.Generic localType, String name) {
                this.target = target;
                this.localType = localType;
                this.name = name;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation readAssignment = assigner.assign(this.localType, this.target, Assigner.Typing.STATIC);
                StackManipulation writeAssignment = assigner.assign(this.target, this.localType, Assigner.Typing.STATIC);
                if (!readAssignment.isValid() || !writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.localType + " to " + this.target);
                }
                return new Target.ForVariable.ReadWrite(this.target, argumentHandler.named(this.name), readAssignment, writeAssignment);
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
                if (!this.name.equals(((ForLocalValue)object).name)) {
                    return false;
                }
                if (!this.target.equals(((ForLocalValue)object).target)) {
                    return false;
                }
                return this.localType.equals(((ForLocalValue)object).localType);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.localType.hashCode()) * 31 + this.name.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Local> {
                protected static final MethodDescription.InDefinedShape LOCAL_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Local.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
                private final Map<String, TypeDefinition> namedTypes;

                protected Factory(Map<String, TypeDefinition> namedTypes) {
                    this.namedTypes = namedTypes;
                }

                @Override
                public Class<Local> getAnnotationType() {
                    return Local.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Local> annotation, Factory.AdviceType adviceType) {
                    String name = annotation.getValue(LOCAL_VALUE).resolve(String.class);
                    TypeDefinition namedType = this.namedTypes.get(name);
                    if (namedType == null) {
                        throw new IllegalStateException("Named local variable is unknown: " + name);
                    }
                    return new ForLocalValue(target.getType(), namedType.asGenericType(), name);
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
                    return ((Object)this.namedTypes).equals(((Factory)object).namedTypes);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.namedTypes).hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForExitValue
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final TypeDescription.Generic exitType;
            private final boolean readOnly;
            private final Assigner.Typing typing;

            protected ForExitValue(TypeDescription.Generic target, TypeDescription.Generic exitType, AnnotationDescription.Loadable<Exit> annotation) {
                this(target, exitType, annotation.getValue(Factory.EXIT_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.EXIT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class));
            }

            public ForExitValue(TypeDescription.Generic target, TypeDescription.Generic exitType, boolean readOnly, Assigner.Typing typing) {
                this.target = target;
                this.exitType = exitType;
                this.readOnly = readOnly;
                this.typing = typing;
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation readAssignment = assigner.assign(this.exitType, this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.exitType + " to " + this.target);
                }
                if (this.readOnly) {
                    return new Target.ForVariable.ReadOnly(this.target, argumentHandler.exit(), readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, this.exitType, this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.target + " to " + this.exitType);
                }
                return new Target.ForVariable.ReadWrite(this.target, argumentHandler.exit(), readAssignment, writeAssignment);
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
                if (this.readOnly != ((ForExitValue)object).readOnly) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForExitValue)object).typing)) {
                    return false;
                }
                if (!this.target.equals(((ForExitValue)object).target)) {
                    return false;
                }
                return this.exitType.equals(((ForExitValue)object).exitType);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.exitType.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Exit> {
                private static final MethodDescription.InDefinedShape EXIT_READ_ONLY;
                private static final MethodDescription.InDefinedShape EXIT_TYPING;
                private final TypeDefinition exitType;

                protected Factory(TypeDefinition exitType) {
                    this.exitType = exitType;
                }

                protected static net.bytebuddy.asm.Advice$OffsetMapping$Factory<Exit> of(TypeDefinition typeDefinition) {
                    return typeDefinition.represents(Void.TYPE) ? new Factory.Illegal<Exit>(Exit.class) : new Factory(typeDefinition);
                }

                @Override
                public Class<Exit> getAnnotationType() {
                    return Exit.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Exit> annotation, Factory.AdviceType adviceType) {
                    if (adviceType.isDelegation() && !annotation.getValue(EXIT_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                        throw new IllegalStateException("Cannot use writable " + target + " on read-only parameter");
                    }
                    return new ForExitValue(target.getType(), this.exitType.asGenericType(), annotation);
                }

                static {
                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Exit.class).getDeclaredMethods();
                    EXIT_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                    EXIT_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
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
                    return this.exitType.equals(((Factory)object).exitType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.exitType.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForEnterValue
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final TypeDescription.Generic enterType;
            private final boolean readOnly;
            private final Assigner.Typing typing;

            protected ForEnterValue(TypeDescription.Generic target, TypeDescription.Generic enterType, AnnotationDescription.Loadable<Enter> annotation) {
                this(target, enterType, annotation.getValue(Factory.ENTER_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.ENTER_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class));
            }

            public ForEnterValue(TypeDescription.Generic target, TypeDescription.Generic enterType, boolean readOnly, Assigner.Typing typing) {
                this.target = target;
                this.enterType = enterType;
                this.readOnly = readOnly;
                this.typing = typing;
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StackManipulation readAssignment = assigner.assign(this.enterType, this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.enterType + " to " + this.target);
                }
                if (this.readOnly) {
                    return new Target.ForVariable.ReadOnly(this.target, argumentHandler.enter(), readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, this.enterType, this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.target + " to " + this.enterType);
                }
                return new Target.ForVariable.ReadWrite(this.target, argumentHandler.enter(), readAssignment, writeAssignment);
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
                if (this.readOnly != ((ForEnterValue)object).readOnly) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForEnterValue)object).typing)) {
                    return false;
                }
                if (!this.target.equals(((ForEnterValue)object).target)) {
                    return false;
                }
                return this.enterType.equals(((ForEnterValue)object).enterType);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.enterType.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Enter> {
                private static final MethodDescription.InDefinedShape ENTER_READ_ONLY;
                private static final MethodDescription.InDefinedShape ENTER_TYPING;
                private final TypeDefinition enterType;

                protected Factory(TypeDefinition enterType) {
                    this.enterType = enterType;
                }

                protected static net.bytebuddy.asm.Advice$OffsetMapping$Factory<Enter> of(TypeDefinition typeDefinition) {
                    return typeDefinition.represents(Void.TYPE) ? new Factory.Illegal<Enter>(Enter.class) : new Factory(typeDefinition);
                }

                @Override
                public Class<Enter> getAnnotationType() {
                    return Enter.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Enter> annotation, Factory.AdviceType adviceType) {
                    if (adviceType.isDelegation() && !annotation.getValue(ENTER_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                        throw new IllegalStateException("Cannot use writable " + target + " on read-only parameter");
                    }
                    return new ForEnterValue(target.getType(), this.enterType.asGenericType(), annotation);
                }

                static {
                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Enter.class).getDeclaredMethods();
                    ENTER_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                    ENTER_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
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
                    return this.enterType.equals(((Factory)object).enterType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.enterType.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForStubValue implements OffsetMapping,
        Factory<StubValue>
        {
            INSTANCE;


            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                return new Target.ForDefaultValue.ReadOnly(instrumentedMethod.getReturnType(), assigner.assign(instrumentedMethod.getReturnType(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), Assigner.Typing.DYNAMIC));
            }

            @Override
            public Class<StubValue> getAnnotationType() {
                return StubValue.class;
            }

            @Override
            public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<StubValue> annotation, Factory.AdviceType adviceType) {
                if (!target.getType().represents((java.lang.reflect.Type)((Object)Object.class))) {
                    throw new IllegalStateException("Cannot use StubValue on non-Object parameter type " + target);
                }
                return this;
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForUnusedValue
        implements OffsetMapping {
            private final TypeDefinition target;

            public ForUnusedValue(TypeDefinition target) {
                this.target = target;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                return new Target.ForDefaultValue.ReadWrite(this.target);
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
                return this.target.equals(((ForUnusedValue)object).target);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.target.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Unused>
            {
                INSTANCE;


                @Override
                public Class<Unused> getAnnotationType() {
                    return Unused.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Unused> annotation, Factory.AdviceType adviceType) {
                    return new ForUnusedValue(target.getType());
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForSelfCallHandle implements OffsetMapping
        {
            BOUND{

                protected StackManipulation decorate(MethodDescription methodDescription, StackManipulation stackManipulation) {
                    ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(1 + (methodDescription.isStatic() ? 0 : 2) + methodDescription.getParameters().size() * 3);
                    stackManipulations.add(stackManipulation);
                    if (!methodDescription.isStatic()) {
                        stackManipulations.add(MethodVariableAccess.loadThis());
                        stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLE.getTypeStub(), new MethodDescription.Token("bindTo", 1, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(TypeDefinition.Sort.describe(Object.class))))));
                    }
                    if (!methodDescription.getParameters().isEmpty()) {
                        ArrayList<StackManipulation.Compound> values = new ArrayList<StackManipulation.Compound>(methodDescription.getParameters().size());
                        for (ParameterDescription parameterDescription : methodDescription.getParameters()) {
                            values.add((StackManipulation.Compound)(parameterDescription.getType().isPrimitive() ? new StackManipulation.Compound(MethodVariableAccess.load(parameterDescription), Assigner.DEFAULT.assign(parameterDescription.getType(), parameterDescription.getType().asErasure().asBoxed().asGenericType(), Assigner.Typing.STATIC)) : MethodVariableAccess.load(parameterDescription)));
                        }
                        stackManipulations.add(IntegerConstant.forValue(0));
                        stackManipulations.add(ArrayFactory.forType(TypeDescription.ForLoadedType.of(Object.class).asGenericType()).withValues(values));
                        stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLES.getTypeStub(), new MethodDescription.Token("insertArguments", 9, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(JavaType.METHOD_HANDLE.getTypeStub(), TypeDefinition.Sort.describe(Integer.TYPE), TypeDefinition.Sort.describe(Object[].class))))));
                    }
                    return new StackManipulation.Compound(stackManipulations);
                }
            }
            ,
            UNBOUND{

                protected StackManipulation decorate(MethodDescription methodDescription, StackManipulation stackManipulation) {
                    return stackManipulation;
                }
            };


            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                if (!instrumentedMethod.isMethod()) {
                    throw new IllegalStateException();
                }
                StackManipulation stackManipulation = (instrumentedMethod.isStatic() ? JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)instrumentedMethod.asDefined()) : JavaConstant.MethodHandle.ofSpecial((MethodDescription.InDefinedShape)instrumentedMethod.asDefined(), instrumentedType)).toStackManipulation();
                return new Target.ForStackManipulation(this.decorate(instrumentedMethod, stackManipulation));
            }

            protected abstract StackManipulation decorate(MethodDescription var1, StackManipulation var2);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<SelfCallHandle>
            {
                INSTANCE;

                private static final MethodDescription.InDefinedShape SELF_CALL_HANDLE_BOUND;

                @Override
                public Class<SelfCallHandle> getAnnotationType() {
                    return SelfCallHandle.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<SelfCallHandle> annotation, Factory.AdviceType adviceType) {
                    if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                        throw new IllegalStateException("Cannot assign a MethodHandle to " + target);
                    }
                    return annotation.getValue(SELF_CALL_HANDLE_BOUND).resolve(Boolean.class) != false ? BOUND : UNBOUND;
                }

                static {
                    SELF_CALL_HANDLE_BOUND = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(SelfCallHandle.class).getDeclaredMethods().filter(ElementMatchers.named("bound"))).getOnly();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForOrigin
        implements OffsetMapping {
            private static final char DELIMITER = '#';
            private static final char ESCAPE = '\\';
            private final List<Renderer> renderers;

            public ForOrigin(List<Renderer> renderers) {
                this.renderers = renderers;
            }

            public static OffsetMapping parse(String pattern) {
                if (pattern.equals("")) {
                    return new ForOrigin(Collections.singletonList(Renderer.ForStringRepresentation.INSTANCE));
                }
                ArrayList<Renderer> renderers = new ArrayList<Renderer>(pattern.length());
                int from = 0;
                int to = pattern.indexOf(35);
                while (to != -1) {
                    if (to != 0 && pattern.charAt(to - 1) == '\\' && (to == 1 || pattern.charAt(to - 2) != '\\')) {
                        renderers.add(new Renderer.ForConstantValue(pattern.substring(from, Math.max(0, to - 1)) + '#'));
                        from = to + 1;
                    } else {
                        if (pattern.length() == to + 1) {
                            throw new IllegalStateException("Missing sort descriptor for " + pattern + " at index " + to);
                        }
                        renderers.add(new Renderer.ForConstantValue(pattern.substring(from, to).replace("\\\\", "\\")));
                        switch (pattern.charAt(to + 1)) {
                            case 'm': {
                                renderers.add(Renderer.ForMethodName.INSTANCE);
                                break;
                            }
                            case 't': {
                                renderers.add(Renderer.ForTypeName.INSTANCE);
                                break;
                            }
                            case 'd': {
                                renderers.add(Renderer.ForDescriptor.INSTANCE);
                                break;
                            }
                            case 'r': {
                                renderers.add(Renderer.ForReturnTypeName.INSTANCE);
                                break;
                            }
                            case 's': {
                                renderers.add(Renderer.ForJavaSignature.INSTANCE);
                                break;
                            }
                            case 'p': {
                                renderers.add(Renderer.ForPropertyName.INSTANCE);
                                break;
                            }
                            default: {
                                throw new IllegalStateException("Illegal sort descriptor " + pattern.charAt(to + 1) + " for " + pattern);
                            }
                        }
                        from = to + 2;
                    }
                    to = pattern.indexOf(35, from);
                }
                renderers.add(new Renderer.ForConstantValue(pattern.substring(from)));
                return new ForOrigin(renderers);
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Renderer renderer : this.renderers) {
                    stringBuilder.append(renderer.apply(instrumentedType, instrumentedMethod));
                }
                return Target.ForStackManipulation.of(stringBuilder.toString());
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
                return ((Object)this.renderers).equals(((ForOrigin)object).renderers);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.renderers).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Origin>
            {
                INSTANCE;

                private static final MethodDescription.InDefinedShape ORIGIN_VALUE;

                @Override
                public Class<Origin> getAnnotationType() {
                    return Origin.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Origin> annotation, Factory.AdviceType adviceType) {
                    if (target.getType().asErasure().represents((java.lang.reflect.Type)((Object)Class.class))) {
                        return ForInstrumentedType.INSTANCE;
                    }
                    if (target.getType().asErasure().represents((java.lang.reflect.Type)((Object)Method.class))) {
                        return ForInstrumentedMethod.METHOD;
                    }
                    if (target.getType().asErasure().represents((java.lang.reflect.Type)((Object)Constructor.class))) {
                        return ForInstrumentedMethod.CONSTRUCTOR;
                    }
                    if (JavaType.EXECUTABLE.getTypeStub().equals(target.getType().asErasure())) {
                        return ForInstrumentedMethod.EXECUTABLE;
                    }
                    if (JavaType.METHOD_HANDLE.getTypeStub().equals(target.getType().asErasure())) {
                        return ForInstrumentedMethod.METHOD_HANDLE;
                    }
                    if (JavaType.METHOD_TYPE.getTypeStub().equals(target.getType().asErasure())) {
                        return ForInstrumentedMethod.METHOD_TYPE;
                    }
                    if (JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().equals(target.getType().asErasure())) {
                        return new ForStackManipulation(MethodInvocation.lookup(), JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().asGenericType(), target.getType(), Assigner.Typing.STATIC);
                    }
                    if (target.getType().asErasure().isAssignableFrom(String.class)) {
                        return ForOrigin.parse(annotation.getValue(ORIGIN_VALUE).resolve(String.class));
                    }
                    throw new IllegalStateException("Non-supported type " + target.getType() + " for @Origin annotation");
                }

                static {
                    ORIGIN_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Origin.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
                }
            }

            public static interface Renderer {
                public String apply(TypeDescription var1, MethodDescription var2);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForPropertyName implements Renderer
                {
                    INSTANCE;

                    public static final char SYMBOL = 'p';

                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return FieldAccessor.FieldNameExtractor.ForBeanProperty.INSTANCE.resolve(instrumentedMethod);
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForConstantValue
                implements Renderer {
                    private final String value;

                    public ForConstantValue(String value) {
                        this.value = value;
                    }

                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return this.value;
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
                        return this.value.equals(((ForConstantValue)object).value);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.value.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForStringRepresentation implements Renderer
                {
                    INSTANCE;


                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return instrumentedMethod.toString();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForReturnTypeName implements Renderer
                {
                    INSTANCE;

                    public static final char SYMBOL = 'r';

                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return instrumentedMethod.getReturnType().asErasure().getName();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForJavaSignature implements Renderer
                {
                    INSTANCE;

                    public static final char SYMBOL = 's';

                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        StringBuilder stringBuilder = new StringBuilder("(");
                        boolean comma = false;
                        for (TypeDescription typeDescription : instrumentedMethod.getParameters().asTypeList().asErasures()) {
                            if (comma) {
                                stringBuilder.append(',');
                            } else {
                                comma = true;
                            }
                            stringBuilder.append(typeDescription.getName());
                        }
                        return stringBuilder.append(')').toString();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForDescriptor implements Renderer
                {
                    INSTANCE;

                    public static final char SYMBOL = 'd';

                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return instrumentedMethod.getDescriptor();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForTypeName implements Renderer
                {
                    INSTANCE;

                    public static final char SYMBOL = 't';

                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return instrumentedType.getName();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForMethodName implements Renderer
                {
                    INSTANCE;

                    public static final char SYMBOL = 'm';

                    @Override
                    public String apply(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return instrumentedMethod.getInternalName();
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class ForFieldHandle
        implements OffsetMapping {
            private final Access access;

            protected ForFieldHandle(Access access) {
                this.access = access;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                FieldDescription fieldDescription = this.resolve(instrumentedType, instrumentedMethod);
                if (!fieldDescription.isStatic() && instrumentedMethod.isStatic()) {
                    throw new IllegalStateException("Cannot access non-static field " + fieldDescription + " from static method " + instrumentedMethod);
                }
                if (sort.isPremature(instrumentedMethod) && !fieldDescription.isStatic()) {
                    throw new IllegalStateException("Cannot access " + fieldDescription + " before super constructor call");
                }
                if (fieldDescription.isStatic()) {
                    return new Target.ForStackManipulation(this.access.resolve((FieldDescription.InDefinedShape)fieldDescription.asDefined()).toStackManipulation());
                }
                return new Target.ForStackManipulation(new StackManipulation.Compound(this.access.resolve((FieldDescription.InDefinedShape)fieldDescription.asDefined()).toStackManipulation(), MethodVariableAccess.REFERENCE.loadFrom(argumentHandler.argument(0)), MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLE.getTypeStub(), new MethodDescription.Token("bindTo", 1, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(TypeDefinition.Sort.describe(Object.class)))))));
            }

            protected abstract FieldDescription resolve(TypeDescription var1, MethodDescription var2);

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
                return this.access.equals((Object)((ForFieldHandle)object).access);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.access.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class Resolved
            extends ForFieldHandle {
                private final FieldDescription fieldDescription;

                public Resolved(Access access, FieldDescription fieldDescription) {
                    super(access);
                    this.fieldDescription = fieldDescription;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
                protected FieldDescription resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                    if (!this.fieldDescription.isStatic() && !this.fieldDescription.getDeclaringType().asErasure().isAssignableFrom(instrumentedType)) {
                        throw new IllegalStateException(this.fieldDescription + " is no member of " + instrumentedType);
                    }
                    if (!this.fieldDescription.isVisibleTo(instrumentedType)) {
                        throw new IllegalStateException("Cannot access " + this.fieldDescription + " from " + instrumentedType);
                    }
                    return this.fieldDescription;
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
                    return this.fieldDescription.equals(((Resolved)object).fieldDescription);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.fieldDescription.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Factory<T extends Annotation>
                implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                    private final Class<T> annotationType;
                    private final FieldDescription fieldDescription;
                    private final Access access;

                    public Factory(Class<T> annotationType, FieldDescription fieldDescription, Access access) {
                        this.annotationType = annotationType;
                        this.fieldDescription = fieldDescription;
                        this.access = access;
                    }

                    @Override
                    public Class<T> getAnnotationType() {
                        return this.annotationType;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                        if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                            throw new IllegalStateException("Cannot assign method handle to " + target);
                        }
                        return new Resolved(this.access, this.fieldDescription);
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
                        if (!this.access.equals((Object)((Factory)object).access)) {
                            return false;
                        }
                        if (!this.annotationType.equals(((Factory)object).annotationType)) {
                            return false;
                        }
                        return this.fieldDescription.equals(((Factory)object).fieldDescription);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.fieldDescription.hashCode()) * 31 + this.access.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class Unresolved
            extends ForFieldHandle {
                protected static final String BEAN_PROPERTY = "";
                private final String name;

                public Unresolved(Access access, String name) {
                    super(access);
                    this.name = name;
                }

                protected FieldDescription resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                    FieldLocator.Resolution resolution;
                    FieldLocator locator = this.fieldLocator(instrumentedType);
                    FieldLocator.Resolution resolution2 = resolution = this.name.equals(BEAN_PROPERTY) ? FieldLocator.Resolution.Simple.ofBeanAccessor(locator, instrumentedMethod) : locator.locate(this.name);
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Cannot locate field named " + this.name + " for " + instrumentedType);
                    }
                    return resolution.getField();
                }

                protected abstract FieldLocator fieldLocator(TypeDescription var1);

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
                    return this.name.equals(((Unresolved)object).name);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.name.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static enum WriterFactory implements Factory<FieldSetterHandle>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape FIELD_SETTER_HANDLE_VALUE;
                    private static final MethodDescription.InDefinedShape FIELD_SETTER_HANDLE_DECLARING_TYPE;

                    @Override
                    public Class<FieldSetterHandle> getAnnotationType() {
                        return FieldSetterHandle.class;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldSetterHandle> annotation, Factory.AdviceType adviceType) {
                        if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                            throw new IllegalStateException("Cannot assign method handle to " + target);
                        }
                        TypeDescription declaringType = annotation.getValue(FIELD_SETTER_HANDLE_DECLARING_TYPE).resolve(TypeDescription.class);
                        return declaringType.represents(Void.TYPE) ? new WithImplicitType(Access.SETTER, annotation.getValue(FIELD_SETTER_HANDLE_VALUE).resolve(String.class)) : new WithExplicitType(Access.SETTER, annotation.getValue(FIELD_SETTER_HANDLE_VALUE).resolve(String.class), declaringType);
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(FieldSetterHandle.class).getDeclaredMethods();
                        FIELD_SETTER_HANDLE_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                        FIELD_SETTER_HANDLE_DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("declaringType"))).getOnly();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static enum ReaderFactory implements Factory<FieldGetterHandle>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape FIELD_GETTER_HANDLE_VALUE;
                    private static final MethodDescription.InDefinedShape FIELD_GETTER_HANDLE_DECLARING_TYPE;

                    @Override
                    public Class<FieldGetterHandle> getAnnotationType() {
                        return FieldGetterHandle.class;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldGetterHandle> annotation, Factory.AdviceType adviceType) {
                        if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                            throw new IllegalStateException("Cannot assign method handle to " + target);
                        }
                        TypeDescription declaringType = annotation.getValue(FIELD_GETTER_HANDLE_DECLARING_TYPE).resolve(TypeDescription.class);
                        return declaringType.represents(Void.TYPE) ? new WithImplicitType(Access.GETTER, annotation.getValue(FIELD_GETTER_HANDLE_VALUE).resolve(String.class)) : new WithExplicitType(Access.GETTER, annotation.getValue(FIELD_GETTER_HANDLE_VALUE).resolve(String.class), declaringType);
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(FieldGetterHandle.class).getDeclaredMethods();
                        FIELD_GETTER_HANDLE_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                        FIELD_GETTER_HANDLE_DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("declaringType"))).getOnly();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class WithExplicitType
                extends Unresolved {
                    private final TypeDescription declaringType;

                    public WithExplicitType(Access access, String name, TypeDescription declaringType) {
                        super(access, name);
                        this.declaringType = declaringType;
                    }

                    protected FieldLocator fieldLocator(TypeDescription instrumentedType) {
                        if (!this.declaringType.represents((java.lang.reflect.Type)((Object)TargetType.class)) && !instrumentedType.isAssignableTo(this.declaringType)) {
                            throw new IllegalStateException(this.declaringType + " is no super type of " + instrumentedType);
                        }
                        return new FieldLocator.ForExactType(TargetType.resolve(this.declaringType, instrumentedType));
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
                        return this.declaringType.equals(((WithExplicitType)object).declaringType);
                    }

                    public int hashCode() {
                        return super.hashCode() * 31 + this.declaringType.hashCode();
                    }
                }

                public static class WithImplicitType
                extends Unresolved {
                    public WithImplicitType(Access access, String name) {
                        super(access, name);
                    }

                    protected FieldLocator fieldLocator(TypeDescription instrumentedType) {
                        return new FieldLocator.ForClassHierarchy(instrumentedType);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Access {
                GETTER{

                    protected JavaConstant.MethodHandle resolve(FieldDescription.InDefinedShape fieldDescription) {
                        return JavaConstant.MethodHandle.ofGetter(fieldDescription);
                    }
                }
                ,
                SETTER{

                    protected JavaConstant.MethodHandle resolve(FieldDescription.InDefinedShape fieldDescription) {
                        return JavaConstant.MethodHandle.ofSetter(fieldDescription);
                    }
                };


                protected abstract JavaConstant.MethodHandle resolve(FieldDescription.InDefinedShape var1);
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class ForField
        implements OffsetMapping {
            private static final MethodDescription.InDefinedShape FIELD_VALUE;
            private static final MethodDescription.InDefinedShape FIELD_DECLARING_TYPE;
            private static final MethodDescription.InDefinedShape FIELD_READ_ONLY;
            private static final MethodDescription.InDefinedShape FIELD_TYPING;
            private final TypeDescription.Generic target;
            private final boolean readOnly;
            private final Assigner.Typing typing;

            protected ForField(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing) {
                this.target = target;
                this.readOnly = readOnly;
                this.typing = typing;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                FieldDescription fieldDescription = this.resolve(instrumentedType, instrumentedMethod);
                if (!fieldDescription.isStatic() && instrumentedMethod.isStatic()) {
                    throw new IllegalStateException("Cannot access non-static field " + fieldDescription + " from static method " + instrumentedMethod);
                }
                if (sort.isPremature(instrumentedMethod) && !fieldDescription.isStatic()) {
                    if (this.readOnly) {
                        throw new IllegalStateException("Cannot read " + fieldDescription + " before super constructor call");
                    }
                    StackManipulation writeAssignment = assigner.assign(this.target, fieldDescription.getType(), this.typing);
                    if (!writeAssignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + this.target + " to " + fieldDescription);
                    }
                    return new Target.ForField.WriteOnly((FieldDescription)fieldDescription.asDefined(), writeAssignment);
                }
                StackManipulation readAssignment = assigner.assign(fieldDescription.getType(), this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + fieldDescription + " to " + this.target);
                }
                if (this.readOnly) {
                    return new Target.ForField.ReadOnly(fieldDescription, readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, fieldDescription.getType(), this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.target + " to " + fieldDescription);
                }
                return new Target.ForField.ReadWrite((FieldDescription)fieldDescription.asDefined(), readAssignment, writeAssignment);
            }

            protected abstract FieldDescription resolve(TypeDescription var1, MethodDescription var2);

            static {
                MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(FieldValue.class).getDeclaredMethods();
                FIELD_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                FIELD_DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("declaringType"))).getOnly();
                FIELD_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                FIELD_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
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
                if (this.readOnly != ((ForField)object).readOnly) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForField)object).typing)) {
                    return false;
                }
                return this.target.equals(((ForField)object).target);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class Resolved
            extends ForField {
                private final FieldDescription fieldDescription;

                public Resolved(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, FieldDescription fieldDescription) {
                    super(target, readOnly, typing);
                    this.fieldDescription = fieldDescription;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
                protected FieldDescription resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                    if (!this.fieldDescription.isStatic() && !this.fieldDescription.getDeclaringType().asErasure().isAssignableFrom(instrumentedType)) {
                        throw new IllegalStateException(this.fieldDescription + " is no member of " + instrumentedType);
                    }
                    if (!this.fieldDescription.isVisibleTo(instrumentedType)) {
                        throw new IllegalStateException("Cannot access " + this.fieldDescription + " from " + instrumentedType);
                    }
                    return this.fieldDescription;
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
                    return this.fieldDescription.equals(((Resolved)object).fieldDescription);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.fieldDescription.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Factory<T extends Annotation>
                implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                    private final Class<T> annotationType;
                    private final FieldDescription fieldDescription;
                    private final boolean readOnly;
                    private final Assigner.Typing typing;

                    public Factory(Class<T> annotationType, FieldDescription fieldDescription) {
                        this(annotationType, fieldDescription, true, Assigner.Typing.STATIC);
                    }

                    public Factory(Class<T> annotationType, FieldDescription fieldDescription, boolean readOnly, Assigner.Typing typing) {
                        this.annotationType = annotationType;
                        this.fieldDescription = fieldDescription;
                        this.readOnly = readOnly;
                        this.typing = typing;
                    }

                    @Override
                    public Class<T> getAnnotationType() {
                        return this.annotationType;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                        return new Resolved(target.getType(), this.readOnly, this.typing, this.fieldDescription);
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
                        if (this.readOnly != ((Factory)object).readOnly) {
                            return false;
                        }
                        if (!this.typing.equals((Object)((Factory)object).typing)) {
                            return false;
                        }
                        if (!this.annotationType.equals(((Factory)object).annotationType)) {
                            return false;
                        }
                        return this.fieldDescription.equals(((Factory)object).fieldDescription);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.fieldDescription.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class Unresolved
            extends ForField {
                protected static final String BEAN_PROPERTY = "";
                private final String name;

                protected Unresolved(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, String name) {
                    super(target, readOnly, typing);
                    this.name = name;
                }

                protected FieldDescription resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                    FieldLocator.Resolution resolution;
                    FieldLocator locator = this.fieldLocator(instrumentedType);
                    FieldLocator.Resolution resolution2 = resolution = this.name.equals(BEAN_PROPERTY) ? FieldLocator.Resolution.Simple.ofBeanAccessor(locator, instrumentedMethod) : locator.locate(this.name);
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Cannot locate field named " + this.name + " for " + instrumentedType);
                    }
                    return resolution.getField();
                }

                protected abstract FieldLocator fieldLocator(TypeDescription var1);

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
                    return this.name.equals(((Unresolved)object).name);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.name.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<FieldValue>
                {
                    INSTANCE;


                    @Override
                    public Class<FieldValue> getAnnotationType() {
                        return FieldValue.class;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldValue> annotation, Factory.AdviceType adviceType) {
                        if (adviceType.isDelegation() && !annotation.getValue(FIELD_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                            throw new IllegalStateException("Cannot write to field for " + target + " in read-only context");
                        }
                        TypeDescription declaringType = annotation.getValue(FIELD_DECLARING_TYPE).resolve(TypeDescription.class);
                        return declaringType.represents(Void.TYPE) ? new WithImplicitType(target.getType(), annotation) : new WithExplicitType(target.getType(), annotation, declaringType);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class WithExplicitType
                extends Unresolved {
                    private final TypeDescription declaringType;

                    protected WithExplicitType(TypeDescription.Generic target, AnnotationDescription.Loadable<FieldValue> annotation, TypeDescription declaringType) {
                        this(target, annotation.getValue(FIELD_READ_ONLY).resolve(Boolean.class), annotation.getValue(FIELD_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(FIELD_VALUE).resolve(String.class), declaringType);
                    }

                    public WithExplicitType(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, String name, TypeDescription declaringType) {
                        super(target, readOnly, typing, name);
                        this.declaringType = declaringType;
                    }

                    @Override
                    protected FieldLocator fieldLocator(TypeDescription instrumentedType) {
                        if (!this.declaringType.represents((java.lang.reflect.Type)((Object)TargetType.class)) && !instrumentedType.isAssignableTo(this.declaringType)) {
                            throw new IllegalStateException(this.declaringType + " is no super type of " + instrumentedType);
                        }
                        return new FieldLocator.ForExactType(TargetType.resolve(this.declaringType, instrumentedType));
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
                        return this.declaringType.equals(((WithExplicitType)object).declaringType);
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode() * 31 + this.declaringType.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class WithImplicitType
                extends Unresolved {
                    protected WithImplicitType(TypeDescription.Generic target, AnnotationDescription.Loadable<FieldValue> annotation) {
                        this(target, annotation.getValue(FIELD_READ_ONLY).resolve(Boolean.class), annotation.getValue(FIELD_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(FIELD_VALUE).resolve(String.class));
                    }

                    public WithImplicitType(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, String name) {
                        super(target, readOnly, typing, name);
                    }

                    @Override
                    protected FieldLocator fieldLocator(TypeDescription instrumentedType) {
                        return new FieldLocator.ForClassHierarchy(instrumentedType);
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForInstrumentedMethod implements OffsetMapping
        {
            METHOD{

                protected boolean isRepresentable(MethodDescription instrumentedMethod) {
                    return instrumentedMethod.isMethod();
                }

                protected Target resolve(MethodDescription.InDefinedShape methodDescription) {
                    return Target.ForStackManipulation.of(methodDescription);
                }
            }
            ,
            CONSTRUCTOR{

                protected boolean isRepresentable(MethodDescription instrumentedMethod) {
                    return instrumentedMethod.isConstructor();
                }

                protected Target resolve(MethodDescription.InDefinedShape methodDescription) {
                    return Target.ForStackManipulation.of(methodDescription);
                }
            }
            ,
            EXECUTABLE{

                protected boolean isRepresentable(MethodDescription instrumentedMethod) {
                    return true;
                }

                protected Target resolve(MethodDescription.InDefinedShape methodDescription) {
                    return Target.ForStackManipulation.of(methodDescription);
                }
            }
            ,
            METHOD_HANDLE{

                protected boolean isRepresentable(MethodDescription instrumentedMethod) {
                    return true;
                }

                protected Target resolve(MethodDescription.InDefinedShape methodDescription) {
                    return new Target.ForStackManipulation(JavaConstant.MethodHandle.of(methodDescription).toStackManipulation());
                }
            }
            ,
            METHOD_TYPE{

                protected boolean isRepresentable(MethodDescription instrumentedMethod) {
                    return true;
                }

                protected Target resolve(MethodDescription.InDefinedShape methodDescription) {
                    return new Target.ForStackManipulation(JavaConstant.MethodType.of(methodDescription).toStackManipulation());
                }
            };


            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                if (!this.isRepresentable(instrumentedMethod)) {
                    throw new IllegalStateException("Cannot represent " + instrumentedMethod + " as the specified constant");
                }
                return this.resolve((MethodDescription.InDefinedShape)instrumentedMethod.asDefined());
            }

            protected abstract boolean isRepresentable(MethodDescription var1);

            protected abstract Target resolve(MethodDescription.InDefinedShape var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForInstrumentedType implements OffsetMapping
        {
            INSTANCE;


            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                return Target.ForStackManipulation.of(instrumentedType);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForAllArguments
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final boolean readOnly;
            private final Assigner.Typing typing;
            private final boolean includeSelf;
            private final boolean nullIfEmpty;

            protected ForAllArguments(TypeDescription.Generic target, AnnotationDescription.Loadable<AllArguments> annotation) {
                this(target, annotation.getValue(Factory.ALL_ARGUMENTS_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.ALL_ARGUMENTS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(Factory.ALL_ARGUMENTS_INCLUDE_SELF).resolve(Boolean.class), annotation.getValue(Factory.ALL_ARGUMENTS_NULL_IF_EMPTY).resolve(Boolean.class));
            }

            public ForAllArguments(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, boolean includeSelf, boolean nullIfEmpty) {
                this.target = target;
                this.readOnly = readOnly;
                this.typing = typing;
                this.includeSelf = includeSelf;
                this.nullIfEmpty = nullIfEmpty;
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                if (this.nullIfEmpty && instrumentedMethod.getParameters().isEmpty() && (!this.includeSelf || instrumentedMethod.isStatic())) {
                    return this.readOnly ? new Target.ForStackManipulation(NullConstant.INSTANCE) : new Target.ForStackManipulation.Writable(NullConstant.INSTANCE, Removal.SINGLE);
                }
                ArrayList<StackManipulation.Compound> reads = new ArrayList<StackManipulation.Compound>((this.includeSelf && !instrumentedMethod.isStatic() ? 1 : 0) + instrumentedMethod.getParameters().size());
                if (this.includeSelf && !instrumentedMethod.isStatic()) {
                    if (sort.isPremature(instrumentedMethod) && instrumentedMethod.isConstructor()) {
                        throw new IllegalStateException("Cannot include self in all arguments array from " + instrumentedMethod);
                    }
                    StackManipulation assignment = assigner.assign(instrumentedMethod.getDeclaringType().asGenericType(), this.target, this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + instrumentedMethod.getDeclaringType() + " to " + this.target);
                    }
                    reads.add(new StackManipulation.Compound(MethodVariableAccess.REFERENCE.loadFrom(argumentHandler.argument(0)), assignment));
                }
                for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                    StackManipulation assignment = assigner.assign(parameterDescription.getType(), this.target, this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + parameterDescription + " to " + this.target);
                    }
                    reads.add(new StackManipulation.Compound(MethodVariableAccess.of(parameterDescription.getType()).loadFrom(argumentHandler.argument(parameterDescription.getOffset())), assignment));
                }
                if (this.readOnly) {
                    return new Target.ForArray.ReadOnly(this.target, reads);
                }
                ArrayList<StackManipulation.Compound> writes = new ArrayList<StackManipulation.Compound>(2 * ((this.includeSelf && !instrumentedMethod.isStatic() ? 1 : 0) + instrumentedMethod.getParameters().size()));
                if (this.includeSelf && !instrumentedMethod.isStatic()) {
                    StackManipulation assignment = assigner.assign(this.target, instrumentedMethod.getDeclaringType().asGenericType(), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + this.target + " to " + instrumentedMethod.getDeclaringType());
                    }
                    writes.add(new StackManipulation.Compound(assignment, MethodVariableAccess.REFERENCE.storeAt(argumentHandler.argument(0))));
                }
                for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                    StackManipulation assignment = assigner.assign(this.target, parameterDescription.getType(), this.typing);
                    if (!assignment.isValid()) {
                        throw new IllegalStateException("Cannot assign " + this.target + " to " + parameterDescription);
                    }
                    writes.add(new StackManipulation.Compound(assignment, MethodVariableAccess.of(parameterDescription.getType()).storeAt(argumentHandler.argument(parameterDescription.getOffset()))));
                }
                return new Target.ForArray.ReadWrite(this.target, reads, writes);
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
                if (this.readOnly != ((ForAllArguments)object).readOnly) {
                    return false;
                }
                if (this.includeSelf != ((ForAllArguments)object).includeSelf) {
                    return false;
                }
                if (this.nullIfEmpty != ((ForAllArguments)object).nullIfEmpty) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForAllArguments)object).typing)) {
                    return false;
                }
                return this.target.equals(((ForAllArguments)object).target);
            }

            public int hashCode() {
                return ((((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode()) * 31 + this.includeSelf) * 31 + this.nullIfEmpty;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<AllArguments>
            {
                INSTANCE;

                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_READ_ONLY;
                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_TYPING;
                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_INCLUDE_SELF;
                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_NULL_IF_EMPTY;

                @Override
                public Class<AllArguments> getAnnotationType() {
                    return AllArguments.class;
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<AllArguments> annotation, Factory.AdviceType adviceType) {
                    if (!target.getType().represents((java.lang.reflect.Type)((Object)Object.class)) && !target.getType().isArray()) {
                        throw new IllegalStateException("Cannot use AllArguments annotation on a non-array type");
                    }
                    if (adviceType.isDelegation() && !annotation.getValue(ALL_ARGUMENTS_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                        throw new IllegalStateException("Cannot define writable field access for " + target);
                    }
                    return new ForAllArguments(target.getType().represents((java.lang.reflect.Type)((Object)Object.class)) ? TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class) : target.getType().getComponentType(), annotation);
                }

                static {
                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(AllArguments.class).getDeclaredMethods();
                    ALL_ARGUMENTS_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                    ALL_ARGUMENTS_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    ALL_ARGUMENTS_INCLUDE_SELF = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("includeSelf"))).getOnly();
                    ALL_ARGUMENTS_NULL_IF_EMPTY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfEmpty"))).getOnly();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForThisReference
        implements OffsetMapping {
            private final TypeDescription.Generic target;
            private final boolean readOnly;
            private final Assigner.Typing typing;
            private final boolean optional;

            protected ForThisReference(TypeDescription.Generic target, AnnotationDescription.Loadable<This> annotation) {
                this(target, annotation.getValue(Factory.THIS_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.THIS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(Factory.THIS_OPTIONAL).resolve(Boolean.class));
            }

            public ForThisReference(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, boolean optional) {
                this.target = target;
                this.readOnly = readOnly;
                this.typing = typing;
                this.optional = optional;
            }

            @Override
            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                if (instrumentedMethod.isStatic() || sort.isPremature(instrumentedMethod)) {
                    if (this.optional) {
                        return this.readOnly ? new Target.ForDefaultValue.ReadOnly(instrumentedType) : new Target.ForDefaultValue.ReadWrite(instrumentedType);
                    }
                    throw new IllegalStateException("Cannot map this reference for static method or constructor start: " + instrumentedMethod);
                }
                StackManipulation readAssignment = assigner.assign(instrumentedType.asGenericType(), this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + instrumentedType + " to " + this.target);
                }
                if (this.readOnly) {
                    return new Target.ForVariable.ReadOnly(instrumentedType.asGenericType(), argumentHandler.argument(0), readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, instrumentedType.asGenericType(), this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.target + " to " + instrumentedType);
                }
                return new Target.ForVariable.ReadWrite(instrumentedType.asGenericType(), argumentHandler.argument(0), readAssignment, writeAssignment);
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
                if (this.readOnly != ((ForThisReference)object).readOnly) {
                    return false;
                }
                if (this.optional != ((ForThisReference)object).optional) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForThisReference)object).typing)) {
                    return false;
                }
                return this.target.equals(((ForThisReference)object).target);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode()) * 31 + this.optional;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<This>
            {
                INSTANCE;

                private static final MethodDescription.InDefinedShape THIS_READ_ONLY;
                private static final MethodDescription.InDefinedShape THIS_TYPING;
                private static final MethodDescription.InDefinedShape THIS_OPTIONAL;

                @Override
                public Class<This> getAnnotationType() {
                    return This.class;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<This> annotation, Factory.AdviceType adviceType) {
                    if (adviceType.isDelegation() && !annotation.getValue(THIS_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                        throw new IllegalStateException("Cannot write to this reference for " + target + " in read-only context");
                    }
                    return new ForThisReference(target.getType(), annotation);
                }

                static {
                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(This.class).getDeclaredMethods();
                    THIS_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                    THIS_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                    THIS_OPTIONAL = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("optional"))).getOnly();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class ForArgument
        implements OffsetMapping {
            protected final TypeDescription.Generic target;
            protected final boolean readOnly;
            private final Assigner.Typing typing;

            protected ForArgument(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing) {
                this.target = target;
                this.readOnly = readOnly;
                this.typing = typing;
            }

            public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                ParameterDescription parameterDescription = this.resolve(instrumentedMethod);
                StackManipulation readAssignment = assigner.assign(parameterDescription.getType(), this.target, this.typing);
                if (!readAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + parameterDescription + " to " + this.target);
                }
                if (this.readOnly) {
                    return new Target.ForVariable.ReadOnly(parameterDescription.getType(), argumentHandler.argument(parameterDescription.getOffset()), readAssignment);
                }
                StackManipulation writeAssignment = assigner.assign(this.target, parameterDescription.getType(), this.typing);
                if (!writeAssignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + parameterDescription + " to " + this.target);
                }
                return new Target.ForVariable.ReadWrite(parameterDescription.getType(), argumentHandler.argument(parameterDescription.getOffset()), readAssignment, writeAssignment);
            }

            protected abstract ParameterDescription resolve(MethodDescription var1);

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
                if (this.readOnly != ((ForArgument)object).readOnly) {
                    return false;
                }
                if (!this.typing.equals((Object)((ForArgument)object).typing)) {
                    return false;
                }
                return this.target.equals(((ForArgument)object).target);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class Resolved
            extends ForArgument {
                private final ParameterDescription parameterDescription;

                public Resolved(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, ParameterDescription parameterDescription) {
                    super(target, readOnly, typing);
                    this.parameterDescription = parameterDescription;
                }

                protected ParameterDescription resolve(MethodDescription instrumentedMethod) {
                    if (!this.parameterDescription.getDeclaringMethod().equals(instrumentedMethod)) {
                        throw new IllegalStateException(this.parameterDescription + " is not a parameter of " + instrumentedMethod);
                    }
                    return this.parameterDescription;
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
                    return this.parameterDescription.equals(((Resolved)object).parameterDescription);
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.parameterDescription.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Factory<T extends Annotation>
                implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<T> {
                    private final Class<T> annotationType;
                    private final ParameterDescription parameterDescription;
                    private final boolean readOnly;
                    private final Assigner.Typing typing;

                    public Factory(Class<T> annotationType, ParameterDescription parameterDescription) {
                        this(annotationType, parameterDescription, true, Assigner.Typing.STATIC);
                    }

                    public Factory(Class<T> annotationType, ParameterDescription parameterDescription, boolean readOnly, Assigner.Typing typing) {
                        this.annotationType = annotationType;
                        this.parameterDescription = parameterDescription;
                        this.readOnly = readOnly;
                        this.typing = typing;
                    }

                    @Override
                    public Class<T> getAnnotationType() {
                        return this.annotationType;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, Factory.AdviceType adviceType) {
                        return new Resolved(target.getType(), this.readOnly, this.typing, this.parameterDescription);
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
                        if (this.readOnly != ((Factory)object).readOnly) {
                            return false;
                        }
                        if (!this.typing.equals((Object)((Factory)object).typing)) {
                            return false;
                        }
                        if (!this.annotationType.equals(((Factory)object).annotationType)) {
                            return false;
                        }
                        return this.parameterDescription.equals(((Factory)object).parameterDescription);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.parameterDescription.hashCode()) * 31 + this.readOnly) * 31 + this.typing.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Unresolved
            extends ForArgument {
                private final int index;
                private final boolean optional;

                protected Unresolved(TypeDescription.Generic target, AnnotationDescription.Loadable<Argument> annotation) {
                    this(target, annotation.getValue(Factory.ARGUMENT_READ_ONLY).resolve(Boolean.class), annotation.getValue(Factory.ARGUMENT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(Factory.ARGUMENT_VALUE).resolve(Integer.class), annotation.getValue(Factory.ARGUMENT_OPTIONAL).resolve(Boolean.class));
                }

                protected Unresolved(ParameterDescription parameterDescription) {
                    this(parameterDescription.getType(), true, Assigner.Typing.STATIC, parameterDescription.getIndex());
                }

                public Unresolved(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, int index) {
                    this(target, readOnly, typing, index, false);
                }

                public Unresolved(TypeDescription.Generic target, boolean readOnly, Assigner.Typing typing, int index, boolean optional) {
                    super(target, readOnly, typing);
                    this.index = index;
                    this.optional = optional;
                }

                @Override
                protected ParameterDescription resolve(MethodDescription instrumentedMethod) {
                    ParameterList<?> parameters = instrumentedMethod.getParameters();
                    if (parameters.size() <= this.index) {
                        throw new IllegalStateException(instrumentedMethod + " does not define an index " + this.index);
                    }
                    return (ParameterDescription)parameters.get(this.index);
                }

                @Override
                public Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, ArgumentHandler argumentHandler, Sort sort) {
                    if (this.optional && instrumentedMethod.getParameters().size() <= this.index) {
                        return this.readOnly ? new Target.ForDefaultValue.ReadOnly(this.target) : new Target.ForDefaultValue.ReadWrite(this.target);
                    }
                    return super.resolve(instrumentedType, instrumentedMethod, assigner, argumentHandler, sort);
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
                    if (this.index != ((Unresolved)object).index) {
                        return false;
                    }
                    return this.optional == ((Unresolved)object).optional;
                }

                @Override
                public int hashCode() {
                    return (super.hashCode() * 31 + this.index) * 31 + this.optional;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static enum Factory implements net.bytebuddy.asm.Advice$OffsetMapping$Factory<Argument>
                {
                    INSTANCE;

                    private static final MethodDescription.InDefinedShape ARGUMENT_VALUE;
                    private static final MethodDescription.InDefinedShape ARGUMENT_READ_ONLY;
                    private static final MethodDescription.InDefinedShape ARGUMENT_TYPING;
                    private static final MethodDescription.InDefinedShape ARGUMENT_OPTIONAL;

                    @Override
                    public Class<Argument> getAnnotationType() {
                        return Argument.class;
                    }

                    @Override
                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Argument> annotation, Factory.AdviceType adviceType) {
                        if (adviceType.isDelegation() && !annotation.getValue(ARGUMENT_READ_ONLY).resolve(Boolean.class).booleanValue()) {
                            throw new IllegalStateException("Cannot define writable field access for " + target + " when using delegation");
                        }
                        return new Unresolved(target.getType(), annotation);
                    }

                    static {
                        MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Argument.class).getDeclaredMethods();
                        ARGUMENT_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                        ARGUMENT_READ_ONLY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("readOnly"))).getOnly();
                        ARGUMENT_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                        ARGUMENT_OPTIONAL = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("optional"))).getOnly();
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Sort {
            ENTER{

                public boolean isPremature(MethodDescription methodDescription) {
                    return methodDescription.isConstructor();
                }
            }
            ,
            EXIT{

                public boolean isPremature(MethodDescription methodDescription) {
                    return false;
                }
            };


            public abstract boolean isPremature(MethodDescription var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Factory<T extends Annotation> {
            public Class<T> getAnnotationType();

            public OffsetMapping make(ParameterDescription.InDefinedShape var1, AnnotationDescription.Loadable<T> var2, AdviceType var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Illegal<T extends Annotation>
            implements Factory<T> {
                private final Class<T> annotationType;

                public Illegal(Class<T> annotationType) {
                    this.annotationType = annotationType;
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, AdviceType adviceType) {
                    throw new IllegalStateException("Usage of " + this.annotationType + " is not allowed on " + target);
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

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Simple<T extends Annotation>
            implements Factory<T> {
                private final Class<T> annotationType;
                private final OffsetMapping offsetMapping;

                public Simple(Class<T> annotationType, OffsetMapping offsetMapping) {
                    this.annotationType = annotationType;
                    this.offsetMapping = offsetMapping;
                }

                @Override
                public Class<T> getAnnotationType() {
                    return this.annotationType;
                }

                @Override
                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation, AdviceType adviceType) {
                    return this.offsetMapping;
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
                    if (!this.annotationType.equals(((Simple)object).annotationType)) {
                        return false;
                    }
                    return this.offsetMapping.equals(((Simple)object).offsetMapping);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.offsetMapping.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum AdviceType {
                DELEGATION(true),
                INLINING(false);

                private final boolean delegation;

                private AdviceType(boolean delegation) {
                    this.delegation = delegation;
                }

                public boolean isDelegation() {
                    return this.delegation;
                }
            }
        }

        public static interface Target {
            public StackManipulation resolveRead();

            public StackManipulation resolveWrite();

            public StackManipulation resolveIncrement(int var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForStackManipulation
            implements Target {
                private final StackManipulation stackManipulation;

                public ForStackManipulation(StackManipulation stackManipulation) {
                    this.stackManipulation = stackManipulation;
                }

                public static Target of(MethodDescription.InDefinedShape methodDescription) {
                    return new ForStackManipulation(MethodConstant.of(methodDescription));
                }

                public static Target of(TypeDescription typeDescription) {
                    return new ForStackManipulation(ClassConstant.of(typeDescription));
                }

                public static Target of(@MaybeNull Object value) {
                    return new ForStackManipulation(value == null ? NullConstant.INSTANCE : ConstantValue.Simple.wrap(value).toStackManipulation());
                }

                public StackManipulation resolveRead() {
                    return this.stackManipulation;
                }

                public StackManipulation resolveWrite() {
                    throw new IllegalStateException("Cannot write to constant value: " + this.stackManipulation);
                }

                public StackManipulation resolveIncrement(int value) {
                    throw new IllegalStateException("Cannot write to constant value: " + this.stackManipulation);
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
                    return this.stackManipulation.equals(((ForStackManipulation)object).stackManipulation);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.stackManipulation.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class Writable
                implements Target {
                    private final StackManipulation read;
                    private final StackManipulation write;

                    public Writable(StackManipulation read, StackManipulation write) {
                        this.read = read;
                        this.write = write;
                    }

                    public StackManipulation resolveRead() {
                        return this.read;
                    }

                    public StackManipulation resolveWrite() {
                        return this.write;
                    }

                    public StackManipulation resolveIncrement(int value) {
                        throw new IllegalStateException("Cannot increment mutable constant value: " + this.write);
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
                        if (!this.read.equals(((Writable)object).read)) {
                            return false;
                        }
                        return this.write.equals(((Writable)object).write);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.read.hashCode()) * 31 + this.write.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class ForField
            implements Target {
                protected final FieldDescription fieldDescription;
                protected final StackManipulation readAssignment;

                protected ForField(FieldDescription fieldDescription, StackManipulation readAssignment) {
                    this.fieldDescription = fieldDescription;
                    this.readAssignment = readAssignment;
                }

                public StackManipulation resolveRead() {
                    return new StackManipulation.Compound(this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(this.fieldDescription).read(), this.readAssignment);
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
                    if (!this.fieldDescription.equals(((ForField)object).fieldDescription)) {
                        return false;
                    }
                    return this.readAssignment.equals(((ForField)object).readAssignment);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.readAssignment.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ReadWrite
                extends ForField {
                    private final StackManipulation writeAssignment;

                    public ReadWrite(FieldDescription fieldDescription) {
                        this(fieldDescription, StackManipulation.Trivial.INSTANCE, StackManipulation.Trivial.INSTANCE);
                    }

                    public ReadWrite(FieldDescription fieldDescription, StackManipulation readAssignment, StackManipulation writeAssignment) {
                        super(fieldDescription, readAssignment);
                        this.writeAssignment = writeAssignment;
                    }

                    public StackManipulation resolveWrite() {
                        StackManipulation preparation = this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : new StackManipulation.Compound(MethodVariableAccess.loadThis(), Duplication.SINGLE.flipOver(this.fieldDescription.getType()), Removal.SINGLE);
                        return new StackManipulation.Compound(this.writeAssignment, preparation, FieldAccess.forField(this.fieldDescription).write());
                    }

                    public StackManipulation resolveIncrement(int value) {
                        return new StackManipulation.Compound(this.resolveRead(), IntegerConstant.forValue(value), Addition.INTEGER, this.resolveWrite());
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
                        return this.writeAssignment.equals(((ReadWrite)object).writeAssignment);
                    }

                    public int hashCode() {
                        return super.hashCode() * 31 + this.writeAssignment.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class WriteOnly
                implements Target {
                    private final FieldDescription fieldDescription;
                    private final StackManipulation writeAssignment;

                    protected WriteOnly(FieldDescription fieldDescription, StackManipulation writeAssignment) {
                        this.fieldDescription = fieldDescription;
                        this.writeAssignment = writeAssignment;
                    }

                    public StackManipulation resolveRead() {
                        throw new IllegalStateException("Cannot read write-only field value");
                    }

                    public StackManipulation resolveWrite() {
                        StackManipulation preparation = this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : new StackManipulation.Compound(MethodVariableAccess.loadThis(), Duplication.SINGLE.flipOver(this.fieldDescription.getType()), Removal.SINGLE);
                        return new StackManipulation.Compound(this.writeAssignment, preparation, FieldAccess.forField(this.fieldDescription).write());
                    }

                    public StackManipulation resolveIncrement(int value) {
                        throw new IllegalStateException("Cannot increment write-only field value");
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
                        if (!this.fieldDescription.equals(((WriteOnly)object).fieldDescription)) {
                            return false;
                        }
                        return this.writeAssignment.equals(((WriteOnly)object).writeAssignment);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.writeAssignment.hashCode();
                    }
                }

                public static class ReadOnly
                extends ForField {
                    public ReadOnly(FieldDescription fieldDescription) {
                        this(fieldDescription, StackManipulation.Trivial.INSTANCE);
                    }

                    public ReadOnly(FieldDescription fieldDescription, StackManipulation readAssignment) {
                        super(fieldDescription, readAssignment);
                    }

                    public StackManipulation resolveWrite() {
                        throw new IllegalStateException("Cannot write to read-only field value");
                    }

                    public StackManipulation resolveIncrement(int value) {
                        throw new IllegalStateException("Cannot write to read-only field value");
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class ForArray
            implements Target {
                protected final TypeDescription.Generic target;
                protected final List<? extends StackManipulation> valueReads;

                protected ForArray(TypeDescription.Generic target, List<? extends StackManipulation> valueReads) {
                    this.target = target;
                    this.valueReads = valueReads;
                }

                @Override
                public StackManipulation resolveRead() {
                    return ArrayFactory.forType(this.target).withValues(this.valueReads);
                }

                @Override
                public StackManipulation resolveIncrement(int value) {
                    throw new IllegalStateException("Cannot increment read-only array value");
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
                    if (!this.target.equals(((ForArray)object).target)) {
                        return false;
                    }
                    return ((Object)this.valueReads).equals(((ForArray)object).valueReads);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + ((Object)this.valueReads).hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ReadWrite
                extends ForArray {
                    private final List<? extends StackManipulation> valueWrites;

                    public ReadWrite(TypeDescription.Generic target, List<? extends StackManipulation> valueReads, List<? extends StackManipulation> valueWrites) {
                        super(target, valueReads);
                        this.valueWrites = valueWrites;
                    }

                    @Override
                    public StackManipulation resolveWrite() {
                        return new StackManipulation.Compound(ArrayAccess.of(this.target).forEach(this.valueWrites), Removal.SINGLE);
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
                        return ((Object)this.valueWrites).equals(((ReadWrite)object).valueWrites);
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode() * 31 + ((Object)this.valueWrites).hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ReadOnly
                extends ForArray {
                    public ReadOnly(TypeDescription.Generic target, List<? extends StackManipulation> valueReads) {
                        super(target, valueReads);
                    }

                    @Override
                    public StackManipulation resolveWrite() {
                        throw new IllegalStateException("Cannot write to read-only array value");
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class ForVariable
            implements Target {
                protected final TypeDefinition typeDefinition;
                protected final int offset;
                protected final StackManipulation readAssignment;

                protected ForVariable(TypeDefinition typeDefinition, int offset, StackManipulation readAssignment) {
                    this.typeDefinition = typeDefinition;
                    this.offset = offset;
                    this.readAssignment = readAssignment;
                }

                public StackManipulation resolveRead() {
                    return new StackManipulation.Compound(MethodVariableAccess.of(this.typeDefinition).loadFrom(this.offset), this.readAssignment);
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
                    if (this.offset != ((ForVariable)object).offset) {
                        return false;
                    }
                    if (!this.typeDefinition.equals(((ForVariable)object).typeDefinition)) {
                        return false;
                    }
                    return this.readAssignment.equals(((ForVariable)object).readAssignment);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.typeDefinition.hashCode()) * 31 + this.offset) * 31 + this.readAssignment.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ReadWrite
                extends ForVariable {
                    private final StackManipulation writeAssignment;

                    public ReadWrite(TypeDefinition typeDefinition, int offset) {
                        this(typeDefinition, offset, StackManipulation.Trivial.INSTANCE, StackManipulation.Trivial.INSTANCE);
                    }

                    public ReadWrite(TypeDefinition typeDefinition, int offset, StackManipulation readAssignment, StackManipulation writeAssignment) {
                        super(typeDefinition, offset, readAssignment);
                        this.writeAssignment = writeAssignment;
                    }

                    public StackManipulation resolveWrite() {
                        return new StackManipulation.Compound(this.writeAssignment, MethodVariableAccess.of(this.typeDefinition).storeAt(this.offset));
                    }

                    public StackManipulation resolveIncrement(int value) {
                        return this.typeDefinition.represents(Integer.TYPE) ? MethodVariableAccess.of(this.typeDefinition).increment(this.offset, value) : new StackManipulation.Compound(this.resolveRead(), IntegerConstant.forValue(1), Addition.INTEGER, this.resolveWrite());
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
                        return this.writeAssignment.equals(((ReadWrite)object).writeAssignment);
                    }

                    public int hashCode() {
                        return super.hashCode() * 31 + this.writeAssignment.hashCode();
                    }
                }

                public static class ReadOnly
                extends ForVariable {
                    public ReadOnly(TypeDefinition typeDefinition, int offset) {
                        this(typeDefinition, offset, StackManipulation.Trivial.INSTANCE);
                    }

                    public ReadOnly(TypeDefinition typeDefinition, int offset, StackManipulation readAssignment) {
                        super(typeDefinition, offset, readAssignment);
                    }

                    public StackManipulation resolveWrite() {
                        throw new IllegalStateException("Cannot write to read-only parameter " + this.typeDefinition + " at " + this.offset);
                    }

                    public StackManipulation resolveIncrement(int value) {
                        throw new IllegalStateException("Cannot write to read-only variable " + this.typeDefinition + " at " + this.offset);
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class ForDefaultValue
            implements Target {
                protected final TypeDefinition typeDefinition;
                protected final StackManipulation readAssignment;

                protected ForDefaultValue(TypeDefinition typeDefinition, StackManipulation readAssignment) {
                    this.typeDefinition = typeDefinition;
                    this.readAssignment = readAssignment;
                }

                public StackManipulation resolveRead() {
                    return new StackManipulation.Compound(DefaultValue.of(this.typeDefinition), this.readAssignment);
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
                    if (!this.typeDefinition.equals(((ForDefaultValue)object).typeDefinition)) {
                        return false;
                    }
                    return this.readAssignment.equals(((ForDefaultValue)object).readAssignment);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.typeDefinition.hashCode()) * 31 + this.readAssignment.hashCode();
                }

                public static class ReadWrite
                extends ForDefaultValue {
                    public ReadWrite(TypeDefinition typeDefinition) {
                        this(typeDefinition, StackManipulation.Trivial.INSTANCE);
                    }

                    public ReadWrite(TypeDefinition typeDefinition, StackManipulation readAssignment) {
                        super(typeDefinition, readAssignment);
                    }

                    public StackManipulation resolveWrite() {
                        return Removal.of(this.typeDefinition);
                    }

                    public StackManipulation resolveIncrement(int value) {
                        return StackManipulation.Trivial.INSTANCE;
                    }
                }

                public static class ReadOnly
                extends ForDefaultValue {
                    public ReadOnly(TypeDefinition typeDefinition) {
                        this(typeDefinition, StackManipulation.Trivial.INSTANCE);
                    }

                    public ReadOnly(TypeDefinition typeDefinition, StackManipulation readAssignment) {
                        super(typeDefinition, readAssignment);
                    }

                    public StackManipulation resolveWrite() {
                        throw new IllegalStateException("Cannot write to read-only default value");
                    }

                    public StackManipulation resolveIncrement(int value) {
                        throw new IllegalStateException("Cannot write to read-only default value");
                    }
                }
            }

            public static abstract class AbstractReadOnlyAdapter
            implements Target {
                public StackManipulation resolveWrite() {
                    throw new IllegalStateException("Cannot write to read-only value");
                }

                public StackManipulation resolveIncrement(int value) {
                    throw new IllegalStateException("Cannot write to read-only value");
                }
            }
        }
    }
}


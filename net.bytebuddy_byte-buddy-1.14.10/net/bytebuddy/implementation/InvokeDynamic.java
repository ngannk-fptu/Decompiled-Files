/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.DoubleConstant;
import net.bytebuddy.implementation.bytecode.constant.FloatConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.LongConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class InvokeDynamic
implements Implementation.Composable {
    protected final MethodDescription.InDefinedShape bootstrap;
    protected final List<? extends JavaConstant> arguments;
    protected final InvocationProvider invocationProvider;
    protected final TerminationHandler terminationHandler;
    protected final Assigner assigner;
    protected final Assigner.Typing typing;

    protected InvokeDynamic(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing) {
        this.bootstrap = bootstrap;
        this.arguments = arguments;
        this.invocationProvider = invocationProvider;
        this.terminationHandler = terminationHandler;
        this.assigner = assigner;
        this.typing = typing;
    }

    public static WithImplicitTarget bootstrap(Method method, Object ... constant) {
        return InvokeDynamic.bootstrap((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(method), constant);
    }

    public static WithImplicitTarget bootstrap(Method method, List<?> constants) {
        return InvokeDynamic.bootstrap((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(method), constants);
    }

    public static WithImplicitTarget bootstrap(Constructor<?> constructor, Object ... constant) {
        return InvokeDynamic.bootstrap((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedConstructor(constructor), constant);
    }

    public static WithImplicitTarget bootstrap(Constructor<?> constructor, List<?> constants) {
        return InvokeDynamic.bootstrap((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedConstructor(constructor), constants);
    }

    public static WithImplicitTarget bootstrap(MethodDescription.InDefinedShape bootstrap, Object ... constant) {
        return InvokeDynamic.bootstrap(bootstrap, Arrays.asList(constant));
    }

    public static WithImplicitTarget bootstrap(MethodDescription.InDefinedShape bootstrap, List<?> constants) {
        List<JavaConstant> arguments = JavaConstant.Simple.wrap(constants);
        if (!bootstrap.isInvokeBootstrap(TypeList.Explicit.of(arguments))) {
            throw new IllegalArgumentException("Not a valid bootstrap method " + bootstrap + " for " + arguments);
        }
        return new WithImplicitTarget(bootstrap, arguments, new InvocationProvider.Default(), TerminationHandler.RETURNING, Assigner.DEFAULT, Assigner.Typing.STATIC);
    }

    public static WithImplicitArguments lambda(Method method, Type functionalInterface) {
        return InvokeDynamic.lambda(new MethodDescription.ForLoadedMethod(method), TypeDefinition.Sort.describe(functionalInterface));
    }

    public static WithImplicitArguments lambda(Method method, Type functionalInterface, MethodGraph.Compiler methodGraphCompiler) {
        return InvokeDynamic.lambda(new MethodDescription.ForLoadedMethod(method), TypeDefinition.Sort.describe(functionalInterface), methodGraphCompiler);
    }

    public static WithImplicitArguments lambda(MethodDescription.InDefinedShape methodDescription, TypeDefinition functionalInterface) {
        return InvokeDynamic.lambda(methodDescription, functionalInterface, MethodGraph.Compiler.Default.forJavaHierarchy());
    }

    public static WithImplicitArguments lambda(MethodDescription.InDefinedShape methodDescription, TypeDefinition functionalInterface, MethodGraph.Compiler methodGraphCompiler) {
        if (!functionalInterface.isInterface()) {
            throw new IllegalArgumentException(functionalInterface + " is not an interface type");
        }
        MethodList methods = (MethodList)methodGraphCompiler.compile(functionalInterface).listNodes().asMethodList().filter(ElementMatchers.isAbstract());
        if (methods.size() != 1) {
            throw new IllegalArgumentException(functionalInterface + " does not define exactly one abstract method: " + methods);
        }
        return InvokeDynamic.bootstrap((MethodDescription.InDefinedShape)new MethodDescription.Latent(new TypeDescription.Latent("java.lang.invoke.LambdaMetafactory", 1, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), new TypeDescription.Generic[0]), "metafactory", 9, Collections.emptyList(), JavaType.CALL_SITE.getTypeStub().asGenericType(), Arrays.asList(new ParameterDescription.Token(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().asGenericType()), new ParameterDescription.Token(TypeDescription.ForLoadedType.of(String.class).asGenericType()), new ParameterDescription.Token(JavaType.METHOD_TYPE.getTypeStub().asGenericType()), new ParameterDescription.Token(JavaType.METHOD_TYPE.getTypeStub().asGenericType()), new ParameterDescription.Token(JavaType.METHOD_HANDLE.getTypeStub().asGenericType()), new ParameterDescription.Token(JavaType.METHOD_TYPE.getTypeStub().asGenericType())), Collections.emptyList(), Collections.emptyList(), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED), JavaConstant.MethodType.ofSignature((MethodDescription)methods.asDefined().getOnly()), JavaConstant.MethodHandle.of(methodDescription), JavaConstant.MethodType.ofSignature((MethodDescription)methods.getOnly())).invoke(((MethodDescription.InDefinedShape)methods.asDefined().getOnly()).getInternalName());
    }

    public InvokeDynamic withBooleanValue(boolean ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (boolean aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForBooleanConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withByteValue(byte ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (byte aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForByteConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withShortValue(short ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (short aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForShortConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withCharacterValue(char ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (char aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForCharacterConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withIntegerValue(int ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (int aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForIntegerConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withLongValue(long ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (long aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForLongConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withFloatValue(float ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (float aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForFloatConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withDoubleValue(double ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (double aValue : value) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForDoubleConstant(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withValue(Object ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (Object aValue : value) {
            argumentProviders.add(InvocationProvider.ArgumentProvider.ConstantPoolWrapper.of(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public WithImplicitType withReference(Object value) {
        return new WithImplicitType.OfInstance(this.bootstrap, this.arguments, this.invocationProvider, this.terminationHandler, this.assigner, this.typing, value);
    }

    public InvokeDynamic withReference(Object ... value) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(value.length);
        for (Object aValue : value) {
            argumentProviders.add(InvocationProvider.ArgumentProvider.ForInstance.of(aValue));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withType(TypeDescription ... typeDescription) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(typeDescription.length);
        for (TypeDescription aTypeDescription : typeDescription) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForClassConstant(aTypeDescription));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withEnumeration(EnumerationDescription ... enumerationDescription) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(enumerationDescription.length);
        for (EnumerationDescription anEnumerationDescription : enumerationDescription) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForEnumerationValue(anEnumerationDescription));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withInstance(ConstantValue ... constant) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(constant.length);
        for (ConstantValue aConstant : constant) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForJavaConstant(aConstant));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withInstance(JavaConstant ... constant) {
        return this.withInstance((ConstantValue[])constant);
    }

    public InvokeDynamic withNullValue(Class<?> ... type) {
        return this.withNullValue(new TypeList.ForLoadedTypes(type).toArray(new TypeDescription[0]));
    }

    public InvokeDynamic withNullValue(TypeDescription ... typeDescription) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(typeDescription.length);
        for (TypeDescription aTypeDescription : typeDescription) {
            if (aTypeDescription.isPrimitive()) {
                throw new IllegalArgumentException("Cannot assign null to primitive type: " + aTypeDescription);
            }
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForNullValue(aTypeDescription));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withArgument(int ... index) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(index.length);
        for (int anIndex : index) {
            if (anIndex < 0) {
                throw new IllegalArgumentException("Method parameter indices cannot be negative: " + anIndex);
            }
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForMethodParameter(anIndex));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public WithImplicitType withArgument(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Method parameter indices cannot be negative: " + index);
        }
        return new WithImplicitType.OfArgument(this.bootstrap, this.arguments, this.invocationProvider, this.terminationHandler, this.assigner, this.typing, index);
    }

    public InvokeDynamic withThis(Class<?> ... type) {
        return this.withThis(new TypeList.ForLoadedTypes(type).toArray(new TypeDescription[0]));
    }

    public InvokeDynamic withThis(TypeDescription ... typeDescription) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(typeDescription.length);
        for (TypeDescription aTypeDescription : typeDescription) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForThisInstance(aTypeDescription));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withMethodArguments() {
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(InvocationProvider.ArgumentProvider.ForInterceptedMethodParameters.INSTANCE), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withImplicitAndMethodArguments() {
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(InvocationProvider.ArgumentProvider.ForInterceptedMethodInstanceAndParameters.INSTANCE), this.terminationHandler, this.assigner, this.typing);
    }

    public InvokeDynamic withField(String ... name) {
        return this.withField(FieldLocator.ForClassHierarchy.Factory.INSTANCE, name);
    }

    public InvokeDynamic withField(FieldLocator.Factory fieldLocatorFactory, String ... name) {
        ArrayList<InvocationProvider.ArgumentProvider> argumentProviders = new ArrayList<InvocationProvider.ArgumentProvider>(name.length);
        for (String aName : name) {
            argumentProviders.add(new InvocationProvider.ArgumentProvider.ForField(aName, fieldLocatorFactory));
        }
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArguments(argumentProviders), this.terminationHandler, this.assigner, this.typing);
    }

    public WithImplicitType withField(String name) {
        return this.withField(name, FieldLocator.ForClassHierarchy.Factory.INSTANCE);
    }

    public WithImplicitType withField(String name, FieldLocator.Factory fieldLocatorFactory) {
        return new WithImplicitType.OfField(this.bootstrap, this.arguments, this.invocationProvider, this.terminationHandler, this.assigner, this.typing, name, fieldLocatorFactory);
    }

    public Implementation.Composable withAssigner(Assigner assigner, Assigner.Typing typing) {
        return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider, this.terminationHandler, assigner, typing);
    }

    @Override
    public Implementation andThen(Implementation implementation) {
        return new Implementation.Compound(new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider, TerminationHandler.DROPPING, this.assigner, this.typing), implementation);
    }

    @Override
    public Implementation.Composable andThen(Implementation.Composable implementation) {
        return new Implementation.Compound.Composable(new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider, TerminationHandler.DROPPING, this.assigner, this.typing), implementation);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return this.invocationProvider.prepare(instrumentedType);
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new Appender(implementationTarget.getInstrumentedType());
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
        if (!this.terminationHandler.equals((Object)((InvokeDynamic)object).terminationHandler)) {
            return false;
        }
        if (!this.typing.equals((Object)((InvokeDynamic)object).typing)) {
            return false;
        }
        if (!this.bootstrap.equals(((InvokeDynamic)object).bootstrap)) {
            return false;
        }
        if (!((Object)this.arguments).equals(((InvokeDynamic)object).arguments)) {
            return false;
        }
        if (!this.invocationProvider.equals(((InvokeDynamic)object).invocationProvider)) {
            return false;
        }
        return this.assigner.equals(((InvokeDynamic)object).assigner);
    }

    public int hashCode() {
        return (((((this.getClass().hashCode() * 31 + this.bootstrap.hashCode()) * 31 + ((Object)this.arguments).hashCode()) * 31 + this.invocationProvider.hashCode()) * 31 + this.terminationHandler.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class Appender
    implements ByteCodeAppender {
        private final TypeDescription instrumentedType;

        public Appender(TypeDescription instrumentedType) {
            this.instrumentedType = instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            InvocationProvider.Target.Resolved target = InvokeDynamic.this.invocationProvider.make(instrumentedMethod).resolve(this.instrumentedType, InvokeDynamic.this.assigner, InvokeDynamic.this.typing);
            StackManipulation.Size size = new StackManipulation.Compound(target.getStackManipulation(), MethodInvocation.invoke(InvokeDynamic.this.bootstrap).dynamic(target.getInternalName(), target.getReturnType(), target.getParameterTypes(), InvokeDynamic.this.arguments), InvokeDynamic.this.terminationHandler.resolve(instrumentedMethod, target.getReturnType(), InvokeDynamic.this.assigner, InvokeDynamic.this.typing)).apply(methodVisitor, implementationContext);
            return new ByteCodeAppender.Size(size.getMaximalSize(), instrumentedMethod.getStackSize());
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
            if (!this.instrumentedType.equals(((Appender)object).instrumentedType)) {
                return false;
            }
            return InvokeDynamic.this.equals(((Appender)object).InvokeDynamic.this);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + InvokeDynamic.this.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class WithImplicitType
    extends AbstractDelegator {
        protected WithImplicitType(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing) {
            super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
        }

        public InvokeDynamic as(Class<?> type) {
            return this.as(TypeDescription.ForLoadedType.of(type));
        }

        public abstract InvokeDynamic as(TypeDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"}, justification="Super type implementation covers use case")
        protected static class OfField
        extends WithImplicitType {
            private final String fieldName;
            private final FieldLocator.Factory fieldLocatorFactory;

            protected OfField(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing, String fieldName, FieldLocator.Factory fieldLocatorFactory) {
                super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
                this.fieldName = fieldName;
                this.fieldLocatorFactory = fieldLocatorFactory;
            }

            @Override
            public InvokeDynamic as(TypeDescription typeDescription) {
                return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(new InvocationProvider.ArgumentProvider.ForField.WithExplicitType(this.fieldName, this.fieldLocatorFactory, typeDescription)), this.terminationHandler, this.assigner, this.typing);
            }

            @Override
            protected InvokeDynamic materialize() {
                return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(new InvocationProvider.ArgumentProvider.ForField(this.fieldName, this.fieldLocatorFactory)), this.terminationHandler, this.assigner, this.typing);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"}, justification="Super type implementation covers use case")
        protected static class OfArgument
        extends WithImplicitType {
            private final int index;

            protected OfArgument(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing, int index) {
                super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
                this.index = index;
            }

            @Override
            public InvokeDynamic as(TypeDescription typeDescription) {
                return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(new InvocationProvider.ArgumentProvider.ForMethodParameter.WithExplicitType(this.index, typeDescription)), this.terminationHandler, this.assigner, this.typing);
            }

            @Override
            protected InvokeDynamic materialize() {
                return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(new InvocationProvider.ArgumentProvider.ForMethodParameter(this.index)), this.terminationHandler, this.assigner, this.typing);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"}, justification="Super type implementation covers use case")
        protected static class OfInstance
        extends WithImplicitType {
            private final Object value;
            private final InvocationProvider.ArgumentProvider argumentProvider;

            protected OfInstance(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing, Object value) {
                super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
                this.value = value;
                this.argumentProvider = InvocationProvider.ArgumentProvider.ForInstance.of(value);
            }

            @Override
            public InvokeDynamic as(TypeDescription typeDescription) {
                if (!typeDescription.asBoxed().isInstance(this.value)) {
                    throw new IllegalArgumentException(this.value + " is not of type " + typeDescription);
                }
                return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(new InvocationProvider.ArgumentProvider.ForInstance(this.value, typeDescription)), this.terminationHandler, this.assigner, this.typing);
            }

            @Override
            protected InvokeDynamic materialize() {
                return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.appendArgument(this.argumentProvider), this.terminationHandler, this.assigner, this.typing);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class WithImplicitTarget
    extends WithImplicitArguments {
        protected WithImplicitTarget(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing) {
            super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
        }

        public WithImplicitArguments invoke(Class<?> returnType) {
            return this.invoke(TypeDescription.ForLoadedType.of(returnType));
        }

        public WithImplicitArguments invoke(TypeDescription returnType) {
            return new WithImplicitArguments(this.bootstrap, this.arguments, this.invocationProvider.withReturnTypeProvider(new InvocationProvider.ReturnTypeProvider.ForExplicitType(returnType)), this.terminationHandler, this.assigner, this.typing);
        }

        public WithImplicitArguments invoke(String methodName) {
            return new WithImplicitArguments(this.bootstrap, this.arguments, this.invocationProvider.withNameProvider(new InvocationProvider.NameProvider.ForExplicitName(methodName)), this.terminationHandler, this.assigner, this.typing);
        }

        public WithImplicitArguments invoke(String methodName, Class<?> returnType) {
            return this.invoke(methodName, TypeDescription.ForLoadedType.of(returnType));
        }

        public WithImplicitArguments invoke(String methodName, TypeDescription returnType) {
            return new WithImplicitArguments(this.bootstrap, this.arguments, this.invocationProvider.withNameProvider(new InvocationProvider.NameProvider.ForExplicitName(methodName)).withReturnTypeProvider(new InvocationProvider.ReturnTypeProvider.ForExplicitType(returnType)), this.terminationHandler, this.assigner, this.typing);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class WithImplicitArguments
    extends AbstractDelegator {
        protected WithImplicitArguments(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing) {
            super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
        }

        public InvokeDynamic withoutArguments() {
            return new InvokeDynamic(this.bootstrap, this.arguments, this.invocationProvider.withoutArguments(), this.terminationHandler, this.assigner, this.typing);
        }

        @Override
        protected InvokeDynamic materialize() {
            return this.withoutArguments();
        }

        @Override
        public WithImplicitArguments withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new WithImplicitArguments(this.bootstrap, this.arguments, this.invocationProvider, this.terminationHandler, assigner, typing);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static abstract class AbstractDelegator
    extends InvokeDynamic {
        protected AbstractDelegator(MethodDescription.InDefinedShape bootstrap, List<? extends JavaConstant> arguments, InvocationProvider invocationProvider, TerminationHandler terminationHandler, Assigner assigner, Assigner.Typing typing) {
            super(bootstrap, arguments, invocationProvider, terminationHandler, assigner, typing);
        }

        protected abstract InvokeDynamic materialize();

        @Override
        public InvokeDynamic withBooleanValue(boolean ... value) {
            return this.materialize().withBooleanValue(value);
        }

        @Override
        public InvokeDynamic withByteValue(byte ... value) {
            return this.materialize().withByteValue(value);
        }

        @Override
        public InvokeDynamic withShortValue(short ... value) {
            return this.materialize().withShortValue(value);
        }

        @Override
        public InvokeDynamic withCharacterValue(char ... value) {
            return this.materialize().withCharacterValue(value);
        }

        @Override
        public InvokeDynamic withIntegerValue(int ... value) {
            return this.materialize().withIntegerValue(value);
        }

        @Override
        public InvokeDynamic withLongValue(long ... value) {
            return this.materialize().withLongValue(value);
        }

        @Override
        public InvokeDynamic withFloatValue(float ... value) {
            return this.materialize().withFloatValue(value);
        }

        @Override
        public InvokeDynamic withDoubleValue(double ... value) {
            return this.materialize().withDoubleValue(value);
        }

        @Override
        public InvokeDynamic withValue(Object ... value) {
            return this.materialize().withValue(value);
        }

        @Override
        public WithImplicitType withReference(Object value) {
            return this.materialize().withReference(value);
        }

        @Override
        public InvokeDynamic withReference(Object ... value) {
            return this.materialize().withReference(value);
        }

        @Override
        public InvokeDynamic withType(TypeDescription ... typeDescription) {
            return this.materialize().withType(typeDescription);
        }

        @Override
        public InvokeDynamic withInstance(JavaConstant ... javaConstant) {
            return this.materialize().withInstance(javaConstant);
        }

        @Override
        public InvokeDynamic withNullValue(Class<?> ... type) {
            return this.materialize().withNullValue(type);
        }

        @Override
        public InvokeDynamic withNullValue(TypeDescription ... typeDescription) {
            return this.materialize().withNullValue(typeDescription);
        }

        @Override
        public InvokeDynamic withArgument(int ... index) {
            return this.materialize().withArgument(index);
        }

        @Override
        public WithImplicitType withArgument(int index) {
            return this.materialize().withArgument(index);
        }

        @Override
        public InvokeDynamic withThis(Class<?> ... type) {
            return this.materialize().withThis(type);
        }

        @Override
        public InvokeDynamic withThis(TypeDescription ... typeDescription) {
            return this.materialize().withThis(typeDescription);
        }

        @Override
        public InvokeDynamic withMethodArguments() {
            return this.materialize().withMethodArguments();
        }

        @Override
        public InvokeDynamic withImplicitAndMethodArguments() {
            return this.materialize().withImplicitAndMethodArguments();
        }

        @Override
        public InvokeDynamic withField(String ... fieldName) {
            return this.materialize().withField(fieldName);
        }

        @Override
        public InvokeDynamic withEnumeration(EnumerationDescription ... enumerationDescription) {
            return this.materialize().withEnumeration(enumerationDescription);
        }

        @Override
        public InvokeDynamic withField(FieldLocator.Factory fieldLocatorFactory, String ... name) {
            return this.materialize().withField(fieldLocatorFactory, name);
        }

        @Override
        public WithImplicitType withField(String name) {
            return this.materialize().withField(name);
        }

        @Override
        public WithImplicitType withField(String name, FieldLocator.Factory fieldLocatorFactory) {
            return this.materialize().withField(name, fieldLocatorFactory);
        }

        @Override
        public Implementation.Composable withAssigner(Assigner assigner, Assigner.Typing typing) {
            return this.materialize().withAssigner(assigner, typing);
        }

        @Override
        public Implementation andThen(Implementation implementation) {
            return this.materialize().andThen(implementation);
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return this.materialize().prepare(instrumentedType);
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this.materialize().appender(implementationTarget);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum TerminationHandler {
        RETURNING{

            protected StackManipulation resolve(MethodDescription interceptedMethod, TypeDescription returnType, Assigner assigner, Assigner.Typing typing) {
                StackManipulation stackManipulation = assigner.assign(returnType.asGenericType(), interceptedMethod.getReturnType(), typing);
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot return " + returnType + " from " + interceptedMethod);
                }
                return new StackManipulation.Compound(stackManipulation, MethodReturn.of(interceptedMethod.getReturnType()));
            }
        }
        ,
        DROPPING{

            protected StackManipulation resolve(MethodDescription interceptedMethod, TypeDescription returnType, Assigner assigner, Assigner.Typing typing) {
                return Removal.of(returnType);
            }
        };


        protected abstract StackManipulation resolve(MethodDescription var1, TypeDescription var2, Assigner var3, Assigner.Typing var4);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static interface InvocationProvider {
        public Target make(MethodDescription var1);

        public InvocationProvider appendArguments(List<ArgumentProvider> var1);

        public InvocationProvider appendArgument(ArgumentProvider var1);

        public InvocationProvider withoutArguments();

        public InvocationProvider withNameProvider(NameProvider var1);

        public InvocationProvider withReturnTypeProvider(ReturnTypeProvider var1);

        public InstrumentedType prepare(InstrumentedType var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Default
        implements InvocationProvider {
            private final NameProvider nameProvider;
            private final ReturnTypeProvider returnTypeProvider;
            private final List<ArgumentProvider> argumentProviders;

            protected Default() {
                this(NameProvider.ForInterceptedMethod.INSTANCE, ReturnTypeProvider.ForInterceptedMethod.INSTANCE, Collections.singletonList(ArgumentProvider.ForInterceptedMethodInstanceAndParameters.INSTANCE));
            }

            protected Default(NameProvider nameProvider, ReturnTypeProvider returnTypeProvider, List<ArgumentProvider> argumentProviders) {
                this.nameProvider = nameProvider;
                this.returnTypeProvider = returnTypeProvider;
                this.argumentProviders = argumentProviders;
            }

            @Override
            public Target make(MethodDescription methodDescription) {
                return new Target(this.nameProvider.resolve(methodDescription), this.returnTypeProvider.resolve(methodDescription), this.argumentProviders, methodDescription);
            }

            @Override
            public InvocationProvider appendArguments(List<ArgumentProvider> argumentProviders) {
                return new Default(this.nameProvider, this.returnTypeProvider, CompoundList.of(this.argumentProviders, argumentProviders));
            }

            @Override
            public InvocationProvider appendArgument(ArgumentProvider argumentProvider) {
                return new Default(this.nameProvider, this.returnTypeProvider, CompoundList.of(this.argumentProviders, argumentProvider));
            }

            @Override
            public InvocationProvider withoutArguments() {
                return new Default(this.nameProvider, this.returnTypeProvider, Collections.<ArgumentProvider>emptyList());
            }

            @Override
            public InvocationProvider withNameProvider(NameProvider nameProvider) {
                return new Default(nameProvider, this.returnTypeProvider, this.argumentProviders);
            }

            @Override
            public InvocationProvider withReturnTypeProvider(ReturnTypeProvider returnTypeProvider) {
                return new Default(this.nameProvider, returnTypeProvider, this.argumentProviders);
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                for (ArgumentProvider argumentProvider : this.argumentProviders) {
                    instrumentedType = argumentProvider.prepare(instrumentedType);
                }
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
                if (!this.nameProvider.equals(((Default)object).nameProvider)) {
                    return false;
                }
                if (!this.returnTypeProvider.equals(((Default)object).returnTypeProvider)) {
                    return false;
                }
                return ((Object)this.argumentProviders).equals(((Default)object).argumentProviders);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.nameProvider.hashCode()) * 31 + this.returnTypeProvider.hashCode()) * 31 + ((Object)this.argumentProviders).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Target
            implements net.bytebuddy.implementation.InvokeDynamic$InvocationProvider$Target {
                private final String internalName;
                private final TypeDescription returnType;
                private final List<ArgumentProvider> argumentProviders;
                private final MethodDescription instrumentedMethod;

                protected Target(String internalName, TypeDescription returnType, List<ArgumentProvider> argumentProviders, MethodDescription instrumentedMethod) {
                    this.internalName = internalName;
                    this.returnType = returnType;
                    this.argumentProviders = argumentProviders;
                    this.instrumentedMethod = instrumentedMethod;
                }

                @Override
                public Target.Resolved resolve(TypeDescription instrumentedType, Assigner assigner, Assigner.Typing typing) {
                    StackManipulation[] stackManipulation = new StackManipulation[this.argumentProviders.size()];
                    ArrayList<TypeDescription> parameterTypes = new ArrayList<TypeDescription>();
                    int index = 0;
                    for (ArgumentProvider argumentProvider : this.argumentProviders) {
                        ArgumentProvider.Resolved resolved = argumentProvider.resolve(instrumentedType, this.instrumentedMethod, assigner, typing);
                        parameterTypes.addAll(resolved.getLoadedTypes());
                        stackManipulation[index++] = resolved.getLoadInstruction();
                    }
                    return new Target.Resolved.Simple(new StackManipulation.Compound(stackManipulation), this.internalName, this.returnType, parameterTypes);
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
                    if (!this.internalName.equals(((Target)object).internalName)) {
                        return false;
                    }
                    if (!this.returnType.equals(((Target)object).returnType)) {
                        return false;
                    }
                    if (!((Object)this.argumentProviders).equals(((Target)object).argumentProviders)) {
                        return false;
                    }
                    return this.instrumentedMethod.equals(((Target)object).instrumentedMethod);
                }

                public int hashCode() {
                    return (((this.getClass().hashCode() * 31 + this.internalName.hashCode()) * 31 + this.returnType.hashCode()) * 31 + ((Object)this.argumentProviders).hashCode()) * 31 + this.instrumentedMethod.hashCode();
                }
            }
        }

        public static interface ReturnTypeProvider {
            public TypeDescription resolve(MethodDescription var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForExplicitType
            implements ReturnTypeProvider {
                private final TypeDescription typeDescription;

                protected ForExplicitType(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public TypeDescription resolve(MethodDescription methodDescription) {
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
                    return this.typeDescription.equals(((ForExplicitType)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForInterceptedMethod implements ReturnTypeProvider
            {
                INSTANCE;


                @Override
                public TypeDescription resolve(MethodDescription methodDescription) {
                    return methodDescription.getReturnType().asErasure();
                }
            }
        }

        public static interface NameProvider {
            public String resolve(MethodDescription var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForExplicitName
            implements NameProvider {
                private final String internalName;

                protected ForExplicitName(String internalName) {
                    this.internalName = internalName;
                }

                public String resolve(MethodDescription methodDescription) {
                    return this.internalName;
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
                    return this.internalName.equals(((ForExplicitName)object).internalName);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.internalName.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForInterceptedMethod implements NameProvider
            {
                INSTANCE;


                @Override
                public String resolve(MethodDescription methodDescription) {
                    return methodDescription.getInternalName();
                }
            }
        }

        public static interface ArgumentProvider {
            public Resolved resolve(TypeDescription var1, MethodDescription var2, Assigner var3, Assigner.Typing var4);

            public InstrumentedType prepare(InstrumentedType var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForJavaConstant
            implements ArgumentProvider {
                private final ConstantValue constant;

                protected ForJavaConstant(ConstantValue constant) {
                    this.constant = constant;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(this.constant.toStackManipulation(), this.constant.getTypeDescription());
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
                    return this.constant.equals(((ForJavaConstant)object).constant);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.constant.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForNullValue
            implements ArgumentProvider {
                private final TypeDescription typeDescription;

                protected ForNullValue(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple((StackManipulation)NullConstant.INSTANCE, this.typeDescription);
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
                    return this.typeDescription.equals(((ForNullValue)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForEnumerationValue
            implements ArgumentProvider {
                private final EnumerationDescription enumerationDescription;

                protected ForEnumerationValue(EnumerationDescription enumerationDescription) {
                    this.enumerationDescription = enumerationDescription;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(FieldAccess.forEnumeration(this.enumerationDescription), this.enumerationDescription.getEnumerationType());
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
                    return this.enumerationDescription.equals(((ForEnumerationValue)object).enumerationDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.enumerationDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForClassConstant
            implements ArgumentProvider {
                private final TypeDescription typeDescription;

                protected ForClassConstant(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(ClassConstant.of(this.typeDescription), TypeDescription.ForLoadedType.of(Class.class));
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
                    return this.typeDescription.equals(((ForClassConstant)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForStringConstant
            implements ArgumentProvider {
                private final String value;

                protected ForStringConstant(String value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple((StackManipulation)new TextConstant(this.value), TypeDescription.ForLoadedType.of(String.class));
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
                    return this.value.equals(((ForStringConstant)object).value);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForDoubleConstant
            implements ArgumentProvider {
                private final double value;

                protected ForDoubleConstant(double value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(DoubleConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Double.TYPE));
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
                    return Double.compare(this.value, ((ForDoubleConstant)object).value) == 0;
                }

                public int hashCode() {
                    long l = Double.doubleToLongBits(this.value);
                    return this.getClass().hashCode() * 31 + (int)(l ^ l >>> 32);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFloatConstant
            implements ArgumentProvider {
                private final float value;

                protected ForFloatConstant(float value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(FloatConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Float.TYPE));
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
                    return Float.compare(this.value, ((ForFloatConstant)object).value) == 0;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + Float.floatToIntBits(this.value);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForLongConstant
            implements ArgumentProvider {
                private final long value;

                protected ForLongConstant(long value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(LongConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Long.TYPE));
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
                    return this.value == ((ForLongConstant)object).value;
                }

                public int hashCode() {
                    long l = this.value;
                    return this.getClass().hashCode() * 31 + (int)(l ^ l >>> 32);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForIntegerConstant
            implements ArgumentProvider {
                private final int value;

                protected ForIntegerConstant(int value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(IntegerConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Integer.TYPE));
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
                    return this.value == ((ForIntegerConstant)object).value;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForCharacterConstant
            implements ArgumentProvider {
                private final char value;

                protected ForCharacterConstant(char value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(IntegerConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Character.TYPE));
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
                    return this.value == ((ForCharacterConstant)object).value;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForShortConstant
            implements ArgumentProvider {
                private final short value;

                protected ForShortConstant(short value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(IntegerConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Short.TYPE));
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
                    return this.value == ((ForShortConstant)object).value;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForByteConstant
            implements ArgumentProvider {
                private final byte value;

                protected ForByteConstant(byte value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(IntegerConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Byte.TYPE));
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
                    return this.value == ((ForByteConstant)object).value;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForBooleanConstant
            implements ArgumentProvider {
                private final boolean value;

                protected ForBooleanConstant(boolean value) {
                    this.value = value;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(IntegerConstant.forValue(this.value), TypeDescription.ForLoadedType.of(Boolean.TYPE));
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
                    return this.value == ((ForBooleanConstant)object).value;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForMethodParameter
            implements ArgumentProvider {
                protected final int index;

                protected ForMethodParameter(int index) {
                    this.index = index;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    ParameterList<?> parameters = instrumentedMethod.getParameters();
                    if (this.index >= parameters.size()) {
                        throw new IllegalStateException("No parameter " + this.index + " for " + instrumentedMethod);
                    }
                    return this.doResolve(MethodVariableAccess.load((ParameterDescription)parameters.get(this.index)), ((ParameterDescription)parameters.get(this.index)).getType(), assigner, typing);
                }

                protected Resolved doResolve(StackManipulation access, TypeDescription.Generic type, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(access, type.asErasure());
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
                    return this.index == ((ForMethodParameter)object).index;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.index;
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class WithExplicitType
                extends ForMethodParameter {
                    private final TypeDescription typeDescription;

                    protected WithExplicitType(int index, TypeDescription typeDescription) {
                        super(index);
                        this.typeDescription = typeDescription;
                    }

                    protected Resolved doResolve(StackManipulation access, TypeDescription.Generic type, Assigner assigner, Assigner.Typing typing) {
                        StackManipulation stackManipulation = assigner.assign(type, this.typeDescription.asGenericType(), typing);
                        if (!stackManipulation.isValid()) {
                            throw new IllegalStateException("Cannot assign " + type + " to " + this.typeDescription);
                        }
                        return new Resolved.Simple((StackManipulation)new StackManipulation.Compound(access, stackManipulation), this.typeDescription);
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
                        return this.typeDescription.equals(((WithExplicitType)object).typeDescription);
                    }

                    public int hashCode() {
                        return super.hashCode() * 31 + this.typeDescription.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForField
            implements ArgumentProvider {
                protected final String fieldName;
                protected final FieldLocator.Factory fieldLocatorFactory;

                protected ForField(String fieldName, FieldLocator.Factory fieldLocatorFactory) {
                    this.fieldName = fieldName;
                    this.fieldLocatorFactory = fieldLocatorFactory;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    FieldLocator.Resolution resolution = this.fieldLocatorFactory.make(instrumentedType).locate(this.fieldName);
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Cannot find a field " + this.fieldName + " for " + instrumentedType);
                    }
                    if (!resolution.getField().isStatic() && instrumentedMethod.isStatic()) {
                        throw new IllegalStateException("Cannot access non-static " + resolution.getField() + " from " + instrumentedMethod);
                    }
                    return this.doResolve(new StackManipulation.Compound(resolution.getField().isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(resolution.getField()).read()), resolution.getField().getType(), assigner, typing);
                }

                protected Resolved doResolve(StackManipulation access, TypeDescription.Generic type, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(access, type.asErasure());
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
                    if (!this.fieldName.equals(((ForField)object).fieldName)) {
                        return false;
                    }
                    return this.fieldLocatorFactory.equals(((ForField)object).fieldLocatorFactory);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.fieldName.hashCode()) * 31 + this.fieldLocatorFactory.hashCode();
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class WithExplicitType
                extends ForField {
                    private final TypeDescription typeDescription;

                    protected WithExplicitType(String fieldName, FieldLocator.Factory fieldLocatorFactory, TypeDescription typeDescription) {
                        super(fieldName, fieldLocatorFactory);
                        this.typeDescription = typeDescription;
                    }

                    protected Resolved doResolve(StackManipulation access, TypeDescription.Generic typeDescription, Assigner assigner, Assigner.Typing typing) {
                        StackManipulation stackManipulation = assigner.assign(typeDescription, this.typeDescription.asGenericType(), typing);
                        if (!stackManipulation.isValid()) {
                            throw new IllegalStateException("Cannot assign " + typeDescription + " to " + this.typeDescription);
                        }
                        return new Resolved.Simple((StackManipulation)new StackManipulation.Compound(access, stackManipulation), this.typeDescription);
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
                        return this.typeDescription.equals(((WithExplicitType)object).typeDescription);
                    }

                    public int hashCode() {
                        return super.hashCode() * 31 + this.typeDescription.hashCode();
                    }
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForInstance
            implements ArgumentProvider {
                private static final String FIELD_PREFIX = "invokeDynamic";
                private final Object value;
                private final TypeDescription fieldType;
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
                private final String name;

                protected ForInstance(Object value, TypeDescription fieldType) {
                    this.value = value;
                    this.fieldType = fieldType;
                    this.name = "invokeDynamic$" + RandomString.hashOf(value);
                }

                protected static ArgumentProvider of(Object value) {
                    return new ForInstance(value, TypeDescription.ForLoadedType.of(value.getClass()));
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    FieldDescription fieldDescription = (FieldDescription)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(this.name))).getOnly();
                    StackManipulation stackManipulation = assigner.assign(fieldDescription.getType(), this.fieldType.asGenericType(), typing);
                    if (!stackManipulation.isValid()) {
                        throw new IllegalStateException("Cannot assign " + fieldDescription + " to " + this.fieldType);
                    }
                    return new Resolved.Simple((StackManipulation)new StackManipulation.Compound(FieldAccess.forField(fieldDescription).read(), stackManipulation), fieldDescription.getType().asErasure());
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType.withAuxiliaryField(new FieldDescription.Token(this.name, 4169, this.fieldType.asGenericType()), this.value);
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
                    if (!this.value.equals(((ForInstance)object).value)) {
                        return false;
                    }
                    return this.fieldType.equals(((ForInstance)object).fieldType);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.value.hashCode()) * 31 + this.fieldType.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForThisInstance
            implements ArgumentProvider {
                private final TypeDescription typeDescription;

                protected ForThisInstance(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    if (instrumentedMethod.isStatic()) {
                        throw new IllegalStateException("Cannot get this instance from static method: " + instrumentedMethod);
                    }
                    if (!instrumentedType.isAssignableTo(this.typeDescription)) {
                        throw new IllegalStateException(instrumentedType + " is not assignable to " + instrumentedType);
                    }
                    return new Resolved.Simple(MethodVariableAccess.loadThis(), this.typeDescription);
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
                    return this.typeDescription.equals(((ForThisInstance)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Resolved {
                public StackManipulation getLoadInstruction();

                public List<TypeDescription> getLoadedTypes();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple
                implements Resolved {
                    private final StackManipulation stackManipulation;
                    private final List<TypeDescription> loadedTypes;

                    public Simple(StackManipulation stackManipulation, TypeDescription loadedType) {
                        this(stackManipulation, Collections.singletonList(loadedType));
                    }

                    public Simple(StackManipulation stackManipulation, List<TypeDescription> loadedTypes) {
                        this.stackManipulation = stackManipulation;
                        this.loadedTypes = loadedTypes;
                    }

                    @Override
                    public StackManipulation getLoadInstruction() {
                        return this.stackManipulation;
                    }

                    @Override
                    public List<TypeDescription> getLoadedTypes() {
                        return this.loadedTypes;
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
                        if (!this.stackManipulation.equals(((Simple)object).stackManipulation)) {
                            return false;
                        }
                        return ((Object)this.loadedTypes).equals(((Simple)object).loadedTypes);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + ((Object)this.loadedTypes).hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ConstantPoolWrapper {
                BOOLEAN((Class)Boolean.TYPE, (Class)Boolean.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(IntegerConstant.forValue((Boolean)value));
                    }
                }
                ,
                BYTE((Class)Byte.TYPE, (Class)Byte.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(IntegerConstant.forValue(((Byte)value).byteValue()));
                    }
                }
                ,
                SHORT((Class)Short.TYPE, (Class)Short.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(IntegerConstant.forValue(((Short)value).shortValue()));
                    }
                }
                ,
                CHARACTER((Class)Character.TYPE, (Class)Character.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(IntegerConstant.forValue(((Character)value).charValue()));
                    }
                }
                ,
                INTEGER((Class)Integer.TYPE, (Class)Integer.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(IntegerConstant.forValue((Integer)value));
                    }
                }
                ,
                LONG((Class)Long.TYPE, (Class)Long.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(LongConstant.forValue((Long)value));
                    }
                }
                ,
                FLOAT((Class)Float.TYPE, (Class)Float.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(FloatConstant.forValue(((Float)value).floatValue()));
                    }
                }
                ,
                DOUBLE((Class)Double.TYPE, (Class)Double.class){

                    protected ArgumentProvider make(Object value) {
                        return new WrappingArgumentProvider(DoubleConstant.forValue((Double)value));
                    }
                };

                private final TypeDescription primitiveType;
                private final TypeDescription wrapperType;

                private ConstantPoolWrapper(Class<?> primitiveType, Class<?> wrapperType) {
                    this.primitiveType = TypeDescription.ForLoadedType.of(primitiveType);
                    this.wrapperType = TypeDescription.ForLoadedType.of(wrapperType);
                }

                public static ArgumentProvider of(Object value) {
                    if (value instanceof Boolean) {
                        return BOOLEAN.make(value);
                    }
                    if (value instanceof Byte) {
                        return BYTE.make(value);
                    }
                    if (value instanceof Short) {
                        return SHORT.make(value);
                    }
                    if (value instanceof Character) {
                        return CHARACTER.make(value);
                    }
                    if (value instanceof Integer) {
                        return INTEGER.make(value);
                    }
                    if (value instanceof Long) {
                        return LONG.make(value);
                    }
                    if (value instanceof Float) {
                        return FLOAT.make(value);
                    }
                    if (value instanceof Double) {
                        return DOUBLE.make(value);
                    }
                    if (value instanceof String) {
                        return new ForStringConstant((String)value);
                    }
                    if (value instanceof Class) {
                        return new ForClassConstant(TypeDescription.ForLoadedType.of((Class)value));
                    }
                    if (value instanceof TypeDescription) {
                        return new ForClassConstant((TypeDescription)value);
                    }
                    if (value instanceof Enum) {
                        return new ForEnumerationValue(new EnumerationDescription.ForLoadedEnumeration((Enum)value));
                    }
                    if (value instanceof EnumerationDescription) {
                        return new ForEnumerationValue((EnumerationDescription)value);
                    }
                    if (JavaType.METHOD_HANDLE.isInstance(value)) {
                        return new ForJavaConstant(JavaConstant.MethodHandle.ofLoaded(value));
                    }
                    if (JavaType.METHOD_TYPE.isInstance(value)) {
                        return new ForJavaConstant(JavaConstant.MethodType.ofLoaded(value));
                    }
                    if (value instanceof JavaConstant) {
                        return new ForJavaConstant((JavaConstant)value);
                    }
                    return ForInstance.of(value);
                }

                protected abstract ArgumentProvider make(Object var1);

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                protected class WrappingArgumentProvider
                implements ArgumentProvider {
                    private final StackManipulation stackManipulation;

                    protected WrappingArgumentProvider(StackManipulation stackManipulation) {
                        this.stackManipulation = stackManipulation;
                    }

                    public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                        return new Resolved.Simple((StackManipulation)new StackManipulation.Compound(this.stackManipulation, assigner.assign(ConstantPoolWrapper.this.primitiveType.asGenericType(), ConstantPoolWrapper.this.wrapperType.asGenericType(), typing)), ConstantPoolWrapper.this.wrapperType);
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
                        if (!ConstantPoolWrapper.this.equals((Object)((WrappingArgumentProvider)object).ConstantPoolWrapper.this)) {
                            return false;
                        }
                        return this.stackManipulation.equals(((WrappingArgumentProvider)object).stackManipulation);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + ConstantPoolWrapper.this.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForInterceptedMethodParameters implements ArgumentProvider
            {
                INSTANCE;


                @Override
                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple((StackManipulation)MethodVariableAccess.allArgumentsOf(instrumentedMethod), instrumentedMethod.getParameters().asTypeList().asErasures());
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForInterceptedMethodInstanceAndParameters implements ArgumentProvider
            {
                INSTANCE;


                @Override
                public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return new Resolved.Simple(MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), instrumentedMethod.isStatic() ? instrumentedMethod.getParameters().asTypeList().asErasures() : CompoundList.of(instrumentedMethod.getDeclaringType().asErasure(), instrumentedMethod.getParameters().asTypeList().asErasures()));
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }
            }
        }

        public static interface Target {
            public Resolved resolve(TypeDescription var1, Assigner var2, Assigner.Typing var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Resolved {
                public StackManipulation getStackManipulation();

                public TypeDescription getReturnType();

                public String getInternalName();

                public List<TypeDescription> getParameterTypes();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple
                implements Resolved {
                    private final StackManipulation stackManipulation;
                    private final String internalName;
                    private final TypeDescription returnType;
                    private final List<TypeDescription> parameterTypes;

                    public Simple(StackManipulation stackManipulation, String internalName, TypeDescription returnType, List<TypeDescription> parameterTypes) {
                        this.stackManipulation = stackManipulation;
                        this.internalName = internalName;
                        this.returnType = returnType;
                        this.parameterTypes = parameterTypes;
                    }

                    @Override
                    public StackManipulation getStackManipulation() {
                        return this.stackManipulation;
                    }

                    @Override
                    public TypeDescription getReturnType() {
                        return this.returnType;
                    }

                    @Override
                    public String getInternalName() {
                        return this.internalName;
                    }

                    @Override
                    public List<TypeDescription> getParameterTypes() {
                        return this.parameterTypes;
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
                        if (!this.internalName.equals(((Simple)object).internalName)) {
                            return false;
                        }
                        if (!this.stackManipulation.equals(((Simple)object).stackManipulation)) {
                            return false;
                        }
                        if (!this.returnType.equals(((Simple)object).returnType)) {
                            return false;
                        }
                        return ((Object)this.parameterTypes).equals(((Simple)object).parameterTypes);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.internalName.hashCode()) * 31 + this.returnType.hashCode()) * 31 + ((Object)this.parameterTypes).hashCode();
                    }
                }
            }
        }
    }
}


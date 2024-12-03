/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.auxiliary;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MethodCallProxy
implements AuxiliaryType {
    private static final String FIELD_NAME_PREFIX = "argument";
    private final Implementation.SpecialMethodInvocation specialMethodInvocation;
    private final boolean serializableProxy;
    private final Assigner assigner;

    public MethodCallProxy(Implementation.SpecialMethodInvocation specialMethodInvocation, boolean serializableProxy) {
        this(specialMethodInvocation, serializableProxy, Assigner.DEFAULT);
    }

    public MethodCallProxy(Implementation.SpecialMethodInvocation specialMethodInvocation, boolean serializableProxy, Assigner assigner) {
        this.specialMethodInvocation = specialMethodInvocation;
        this.serializableProxy = serializableProxy;
        this.assigner = assigner;
    }

    private static LinkedHashMap<String, TypeDescription> extractFields(MethodDescription methodDescription) {
        LinkedHashMap<String, TypeDescription> typeDescriptions = new LinkedHashMap<String, TypeDescription>();
        int currentIndex = 0;
        if (!methodDescription.isStatic()) {
            typeDescriptions.put(MethodCallProxy.fieldName(currentIndex++), methodDescription.getDeclaringType().asErasure());
        }
        for (ParameterDescription parameterDescription : methodDescription.getParameters()) {
            typeDescriptions.put(MethodCallProxy.fieldName(currentIndex++), parameterDescription.getType().asErasure());
        }
        return typeDescriptions;
    }

    private static String fieldName(int index) {
        return FIELD_NAME_PREFIX + index;
    }

    @Override
    public String getSuffix() {
        return RandomString.hashOf(this.specialMethodInvocation.getMethodDescription().hashCode()) + (this.serializableProxy ? "S" : "0");
    }

    @Override
    public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
        Type[] typeArray;
        MethodDescription.InDefinedShape accessorMethod = methodAccessorFactory.registerAccessorFor(this.specialMethodInvocation, MethodAccessorFactory.AccessType.DEFAULT);
        LinkedHashMap<String, TypeDescription> parameterFields = MethodCallProxy.extractFields(accessorMethod);
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition receiverTypeDefinition = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).with(PrecomputedMethodGraph.INSTANCE).subclass(Object.class, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER).implement(new Type[]{Runnable.class, Callable.class}).intercept(new MethodCall(accessorMethod, this.assigner));
        if (this.serializableProxy) {
            Class[] classArray = new Class[1];
            typeArray = classArray;
            classArray[0] = Serializable.class;
        } else {
            typeArray = new Class[]{};
        }
        DynamicType.Builder<Object> builder = receiverTypeDefinition.implement(typeArray).defineConstructor(new ModifierContributor.ForMethod[0]).withParameters(parameterFields.values()).intercept(ConstructorCall.INSTANCE);
        for (Map.Entry<String, TypeDescription> field : parameterFields.entrySet()) {
            builder = builder.defineField(field.getKey(), (TypeDefinition)field.getValue(), Visibility.PRIVATE);
        }
        return builder.make();
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
        if (this.serializableProxy != ((MethodCallProxy)object).serializableProxy) {
            return false;
        }
        if (!this.specialMethodInvocation.equals(((MethodCallProxy)object).specialMethodInvocation)) {
            return false;
        }
        return this.assigner.equals(((MethodCallProxy)object).assigner);
    }

    public int hashCode() {
        return ((this.getClass().hashCode() * 31 + this.specialMethodInvocation.hashCode()) * 31 + this.serializableProxy) * 31 + this.assigner.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class AssignableSignatureCall
    extends StackManipulation.AbstractBase {
        private final Implementation.SpecialMethodInvocation specialMethodInvocation;
        private final boolean serializable;

        public AssignableSignatureCall(Implementation.SpecialMethodInvocation specialMethodInvocation, boolean serializable) {
            this.specialMethodInvocation = specialMethodInvocation;
            this.serializable = serializable;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            TypeDescription auxiliaryType = implementationContext.register(new MethodCallProxy(this.specialMethodInvocation, this.serializable));
            return new StackManipulation.Compound(TypeCreation.of(auxiliaryType), Duplication.SINGLE, MethodVariableAccess.allArgumentsOf(this.specialMethodInvocation.getMethodDescription()).prependThisReference(), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)auxiliaryType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly())).apply(methodVisitor, implementationContext);
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
            if (this.serializable != ((AssignableSignatureCall)object).serializable) {
                return false;
            }
            return this.specialMethodInvocation.equals(((AssignableSignatureCall)object).specialMethodInvocation);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.specialMethodInvocation.hashCode()) * 31 + this.serializable;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class MethodCall
    implements Implementation {
        private final MethodDescription accessorMethod;
        private final Assigner assigner;

        protected MethodCall(MethodDescription accessorMethod, Assigner assigner) {
            this.accessorMethod = accessorMethod;
            this.assigner = assigner;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

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
            if (!this.accessorMethod.equals(((MethodCall)object).accessorMethod)) {
                return false;
            }
            return this.assigner.equals(((MethodCall)object).assigner);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.accessorMethod.hashCode()) * 31 + this.assigner.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;

            private Appender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                FieldList<FieldDescription.InDefinedShape> fieldList = this.instrumentedType.getDeclaredFields();
                ArrayList<StackManipulation.Compound> fieldLoadings = new ArrayList<StackManipulation.Compound>(fieldList.size());
                for (FieldDescription fieldDescription : fieldList) {
                    fieldLoadings.add(new StackManipulation.Compound(MethodVariableAccess.loadThis(), FieldAccess.forField(fieldDescription).read()));
                }
                StackManipulation.Size stackSize = new StackManipulation.Compound(new StackManipulation.Compound(fieldLoadings), MethodInvocation.invoke(MethodCall.this.accessorMethod), MethodCall.this.assigner.assign(MethodCall.this.accessorMethod.getReturnType(), instrumentedMethod.getReturnType(), Assigner.Typing.DYNAMIC), MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext);
                return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
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
                return MethodCall.this.equals(((Appender)object).MethodCall.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + MethodCall.this.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ConstructorCall implements Implementation
    {
        INSTANCE;

        private final MethodDescription objectTypeDefaultConstructor = (MethodDescription)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly();

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getInstrumentedType());
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class Appender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;

            private Appender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                FieldList<FieldDescription.InDefinedShape> fieldList = this.instrumentedType.getDeclaredFields();
                StackManipulation[] fieldLoading = new StackManipulation[fieldList.size()];
                int index = 0;
                for (FieldDescription fieldDescription : fieldList) {
                    fieldLoading[index] = new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodVariableAccess.load((ParameterDescription)instrumentedMethod.getParameters().get(index)), FieldAccess.forField(fieldDescription).write());
                    ++index;
                }
                StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(INSTANCE.objectTypeDefaultConstructor), new StackManipulation.Compound(fieldLoading), MethodReturn.VOID).apply(methodVisitor, implementationContext);
                return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
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
    protected static enum PrecomputedMethodGraph implements MethodGraph.Compiler
    {
        INSTANCE;

        private final transient MethodGraph.Linked methodGraph;

        private PrecomputedMethodGraph() {
            LinkedHashMap<MethodDescription.SignatureToken, MethodGraph.Node> nodes = new LinkedHashMap<MethodDescription.SignatureToken, MethodGraph.Node>();
            MethodDescription.Latent callMethod = new MethodDescription.Latent(TypeDescription.ForLoadedType.of(Callable.class), "call", 1025, Collections.emptyList(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), Collections.emptyList(), Collections.singletonList(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Exception.class)), Collections.emptyList(), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED);
            nodes.put(callMethod.asSignatureToken(), new MethodGraph.Node.Simple(callMethod));
            MethodDescription.Latent runMethod = new MethodDescription.Latent(TypeDescription.ForLoadedType.of(Runnable.class), "run", 1025, Collections.emptyList(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED);
            nodes.put(runMethod.asSignatureToken(), new MethodGraph.Node.Simple(runMethod));
            MethodGraph.Simple methodGraph = new MethodGraph.Simple(nodes);
            this.methodGraph = new MethodGraph.Linked.Delegation(methodGraph, methodGraph, Collections.<TypeDescription, MethodGraph>emptyMap());
        }

        @Override
        public MethodGraph.Linked compile(TypeDefinition typeDefinition) {
            return this.methodGraph;
        }

        @Override
        @Deprecated
        public MethodGraph.Linked compile(TypeDescription typeDescription) {
            return this.methodGraph;
        }

        @Override
        public MethodGraph.Linked compile(TypeDefinition typeDefinition, TypeDescription viewPoint) {
            return this.methodGraph;
        }

        @Override
        @Deprecated
        public MethodGraph.Linked compile(TypeDescription typeDefinition, TypeDescription viewPoint) {
            return this.methodGraph;
        }
    }
}


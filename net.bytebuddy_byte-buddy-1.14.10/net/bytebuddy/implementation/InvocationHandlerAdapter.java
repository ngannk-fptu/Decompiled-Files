/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
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
public abstract class InvocationHandlerAdapter
implements Implementation.Composable {
    private static final TypeDescription.Generic INVOCATION_HANDLER_TYPE = TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(InvocationHandler.class);
    private static final boolean UNCACHED = false;
    private static final boolean CACHED = true;
    private static final boolean UNPRIVILEGED = false;
    private static final boolean PRIVILEGED = true;
    private static final boolean RETURNING = true;
    private static final boolean DROPPING = false;
    protected final String fieldName;
    protected final boolean cached;
    protected final boolean privileged;
    protected final boolean returning;
    protected final Assigner assigner;

    protected InvocationHandlerAdapter(String fieldName, boolean cached, boolean privileged, boolean returning, Assigner assigner) {
        this.fieldName = fieldName;
        this.cached = cached;
        this.privileged = privileged;
        this.returning = returning;
        this.assigner = assigner;
    }

    public static InvocationHandlerAdapter of(InvocationHandler invocationHandler) {
        return InvocationHandlerAdapter.of(invocationHandler, "invocationHandler$" + RandomString.hashOf(invocationHandler));
    }

    public static InvocationHandlerAdapter of(InvocationHandler invocationHandler, String fieldName) {
        return new ForInstance(fieldName, true, false, true, Assigner.DEFAULT, invocationHandler);
    }

    public static InvocationHandlerAdapter toField(String name) {
        return InvocationHandlerAdapter.toField(name, FieldLocator.ForClassHierarchy.Factory.INSTANCE);
    }

    public static InvocationHandlerAdapter toField(String name, FieldLocator.Factory fieldLocatorFactory) {
        return new ForField(name, true, false, true, Assigner.DEFAULT, fieldLocatorFactory);
    }

    private List<StackManipulation> argumentValuesOf(MethodDescription instrumentedMethod) {
        TypeList.Generic parameterTypes = instrumentedMethod.getParameters().asTypeList();
        ArrayList<StackManipulation> instruction = new ArrayList<StackManipulation>(parameterTypes.size());
        int currentIndex = 1;
        for (TypeDescription.Generic parameterType : parameterTypes) {
            instruction.add(new StackManipulation.Compound(MethodVariableAccess.of(parameterType).loadFrom(currentIndex), this.assigner.assign(parameterType, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), Assigner.Typing.STATIC)));
            currentIndex += parameterType.getStackSize().getSize();
        }
        return instruction;
    }

    public abstract WithoutPrivilegeConfiguration withoutMethodCache();

    public abstract Implementation withAssigner(Assigner var1);

    public abstract AssignerConfigurable withPrivilegedLookup();

    protected ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod, StackManipulation preparingManipulation, FieldDescription fieldDescription) {
        if (instrumentedMethod.isStatic() || instrumentedMethod.isConstructor()) {
            throw new IllegalStateException("It is not possible to apply an invocation handler onto the static method or constructor " + instrumentedMethod);
        }
        MethodConstant.CanCache methodConstant = this.privileged ? MethodConstant.ofPrivileged((MethodDescription.InDefinedShape)instrumentedMethod.asDefined()) : MethodConstant.of((MethodDescription.InDefinedShape)instrumentedMethod.asDefined());
        StackManipulation.Size stackSize = new StackManipulation.Compound(preparingManipulation, FieldAccess.forField(fieldDescription).read(), MethodVariableAccess.loadThis(), this.cached ? methodConstant.cached() : methodConstant, instrumentedMethod.getParameters().isEmpty() ? NullConstant.INSTANCE : ArrayFactory.forType(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class)).withValues(this.argumentValuesOf(instrumentedMethod)), MethodInvocation.invoke((MethodDescription)((MethodList)INVOCATION_HANDLER_TYPE.getDeclaredMethods().filter(ElementMatchers.isAbstract())).getOnly()), this.returning ? new StackManipulation.Compound(this.assigner.assign(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), instrumentedMethod.getReturnType(), Assigner.Typing.DYNAMIC), MethodReturn.of(instrumentedMethod.getReturnType())) : Removal.SINGLE).apply(methodVisitor, implementationContext);
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
        if (this.cached != ((InvocationHandlerAdapter)object).cached) {
            return false;
        }
        if (this.privileged != ((InvocationHandlerAdapter)object).privileged) {
            return false;
        }
        if (this.returning != ((InvocationHandlerAdapter)object).returning) {
            return false;
        }
        if (!this.fieldName.equals(((InvocationHandlerAdapter)object).fieldName)) {
            return false;
        }
        return this.assigner.equals(((InvocationHandlerAdapter)object).assigner);
    }

    public int hashCode() {
        return ((((this.getClass().hashCode() * 31 + this.fieldName.hashCode()) * 31 + this.cached) * 31 + this.privileged) * 31 + this.returning) * 31 + this.assigner.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForField
    extends InvocationHandlerAdapter
    implements WithoutPrivilegeConfiguration {
        private final FieldLocator.Factory fieldLocatorFactory;

        protected ForField(String fieldName, boolean cached, boolean privileged, boolean returning, Assigner assigner, FieldLocator.Factory fieldLocatorFactory) {
            super(fieldName, cached, privileged, returning, assigner);
            this.fieldLocatorFactory = fieldLocatorFactory;
        }

        public WithoutPrivilegeConfiguration withoutMethodCache() {
            return new ForField(this.fieldName, false, this.privileged, this.returning, this.assigner, this.fieldLocatorFactory);
        }

        public Implementation.Composable withAssigner(Assigner assigner) {
            return new ForField(this.fieldName, this.cached, this.privileged, this.returning, assigner, this.fieldLocatorFactory);
        }

        public AssignerConfigurable withPrivilegedLookup() {
            return new ForField(this.fieldName, this.cached, true, this.returning, this.assigner, this.fieldLocatorFactory);
        }

        public Implementation andThen(Implementation implementation) {
            return new Implementation.Compound(new ForField(this.fieldName, this.cached, this.privileged, false, this.assigner, this.fieldLocatorFactory), implementation);
        }

        public Implementation.Composable andThen(Implementation.Composable implementation) {
            return new Implementation.Compound.Composable(new ForField(this.fieldName, this.cached, this.privileged, false, this.assigner, this.fieldLocatorFactory), implementation);
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            FieldLocator.Resolution resolution = this.fieldLocatorFactory.make(implementationTarget.getInstrumentedType()).locate(this.fieldName);
            if (!resolution.isResolved()) {
                throw new IllegalStateException("Could not find a field named '" + this.fieldName + "' for " + implementationTarget.getInstrumentedType());
            }
            if (!resolution.getField().getType().asErasure().isAssignableTo(InvocationHandler.class)) {
                throw new IllegalStateException("Field " + resolution.getField() + " does not declare a type that is assignable to invocation handler");
            }
            return new Appender(resolution.getField());
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
            return this.fieldLocatorFactory.equals(((ForField)object).fieldLocatorFactory);
        }

        public int hashCode() {
            return super.hashCode() * 31 + this.fieldLocatorFactory.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final FieldDescription fieldDescription;

            protected Appender(FieldDescription fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                return ForField.this.apply(methodVisitor, implementationContext, instrumentedMethod, this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), this.fieldDescription);
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
                if (!this.fieldDescription.equals(((Appender)object).fieldDescription)) {
                    return false;
                }
                return ForField.this.equals(((Appender)object).ForField.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + ForField.this.hashCode();
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForInstance
    extends InvocationHandlerAdapter
    implements WithoutPrivilegeConfiguration {
        private static final String PREFIX = "invocationHandler";
        protected final InvocationHandler invocationHandler;

        protected ForInstance(String fieldName, boolean cached, boolean privileged, boolean returning, Assigner assigner, InvocationHandler invocationHandler) {
            super(fieldName, cached, privileged, returning, assigner);
            this.invocationHandler = invocationHandler;
        }

        public WithoutPrivilegeConfiguration withoutMethodCache() {
            return new ForInstance(this.fieldName, false, this.privileged, this.returning, this.assigner, this.invocationHandler);
        }

        public Implementation.Composable withAssigner(Assigner assigner) {
            return new ForInstance(this.fieldName, this.cached, this.privileged, this.returning, assigner, this.invocationHandler);
        }

        public AssignerConfigurable withPrivilegedLookup() {
            return new ForInstance(this.fieldName, this.cached, true, this.returning, this.assigner, this.invocationHandler);
        }

        public Implementation andThen(Implementation implementation) {
            return new Implementation.Compound(new ForInstance(this.fieldName, this.cached, this.privileged, false, this.assigner, this.invocationHandler), implementation);
        }

        public Implementation.Composable andThen(Implementation.Composable implementation) {
            return new Implementation.Compound.Composable(new ForInstance(this.fieldName, this.cached, this.privileged, false, this.assigner, this.invocationHandler), implementation);
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            if (!((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(this.fieldName).and(ElementMatchers.fieldType(INVOCATION_HANDLER_TYPE.asErasure())))).isEmpty()) {
                throw new IllegalStateException("Field with name " + this.fieldName + " and type " + INVOCATION_HANDLER_TYPE.asErasure() + " already declared by " + instrumentedType);
            }
            return instrumentedType.withField(new FieldDescription.Token(this.fieldName, 4169, INVOCATION_HANDLER_TYPE)).withInitializer(new LoadedTypeInitializer.ForStaticField(this.fieldName, this.invocationHandler));
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getInstrumentedType());
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
            return this.invocationHandler.equals(((ForInstance)object).invocationHandler);
        }

        public int hashCode() {
            return super.hashCode() * 31 + this.invocationHandler.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;

            protected Appender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                return ForInstance.this.apply(methodVisitor, implementationContext, instrumentedMethod, StackManipulation.Trivial.INSTANCE, (FieldDescription)((FieldList)this.instrumentedType.getDeclaredFields().filter(ElementMatchers.named(ForInstance.this.fieldName).and(ElementMatchers.genericFieldType(INVOCATION_HANDLER_TYPE)))).getOnly());
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
                return ForInstance.this.equals(((Appender)object).ForInstance.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + ForInstance.this.hashCode();
            }
        }
    }

    public static interface WithoutPrivilegeConfiguration
    extends AssignerConfigurable {
        public AssignerConfigurable withPrivilegedLookup();
    }

    public static interface AssignerConfigurable
    extends Implementation.Composable {
        public Implementation.Composable withAssigner(Assigner var1);
    }
}


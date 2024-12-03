/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public abstract class FixedValue
implements Implementation {
    protected final Assigner assigner;
    protected final Assigner.Typing typing;

    protected FixedValue(Assigner assigner, Assigner.Typing typing) {
        this.assigner = assigner;
        this.typing = typing;
    }

    public static AssignerConfigurable value(Object value) {
        ConstantValue constant = ConstantValue.Simple.wrapOrNull(value);
        return constant == null ? FixedValue.reference(value) : new ForConstantValue(constant.toStackManipulation(), constant.getTypeDescription());
    }

    public static AssignerConfigurable reference(Object value) {
        return FixedValue.reference(value, "value$" + RandomString.hashOf(value));
    }

    public static AssignerConfigurable reference(Object value, String name) {
        return new ForValue(value, name);
    }

    public static AssignerConfigurable value(TypeDescription type) {
        return new ForConstantValue(ClassConstant.of(type), TypeDescription.ForLoadedType.of(Class.class));
    }

    public static AssignerConfigurable value(ConstantValue constant) {
        return new ForConstantValue(constant.toStackManipulation(), constant.getTypeDescription());
    }

    public static AssignerConfigurable value(JavaConstant constant) {
        return FixedValue.value((ConstantValue)constant);
    }

    public static AssignerConfigurable argument(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Argument index cannot be negative: " + index);
        }
        return new ForArgument(index);
    }

    public static AssignerConfigurable self() {
        return new ForThisValue();
    }

    public static Implementation nullValue() {
        return ForNullValue.INSTANCE;
    }

    public static AssignerConfigurable originType() {
        return new ForOriginType();
    }

    protected ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod, TypeDescription.Generic typeDescription, StackManipulation stackManipulation) {
        StackManipulation assignment = this.assigner.assign(typeDescription, instrumentedMethod.getReturnType(), this.typing);
        if (!assignment.isValid()) {
            throw new IllegalArgumentException("Cannot return value of type " + typeDescription + " for " + instrumentedMethod);
        }
        StackManipulation.Size stackSize = new StackManipulation.Compound(stackManipulation, assignment, MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext);
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
        if (!this.typing.equals((Object)((FixedValue)object).typing)) {
            return false;
        }
        return this.assigner.equals(((FixedValue)object).assigner);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForValue
    extends FixedValue
    implements AssignerConfigurable {
        private static final String PREFIX = "value";
        private final String name;
        private final Object value;

        protected ForValue(Object value, String name) {
            this(Assigner.DEFAULT, Assigner.Typing.STATIC, value, name);
        }

        private ForValue(Assigner assigner, Assigner.Typing typing, Object value, String name) {
            super(assigner, typing);
            this.name = name;
            this.value = value;
        }

        public Implementation withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new ForValue(assigner, typing, this.value, this.name);
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType.withAuxiliaryField(new FieldDescription.Token(this.name, 4169, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(this.value.getClass())), this.value);
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new StaticFieldByteCodeAppender(implementationTarget.getInstrumentedType());
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
            if (!this.name.equals(((ForValue)object).name)) {
                return false;
            }
            return this.value.equals(((ForValue)object).value);
        }

        public int hashCode() {
            return (super.hashCode() * 31 + this.name.hashCode()) * 31 + this.value.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance
        private class StaticFieldByteCodeAppender
        implements ByteCodeAppender {
            private final StackManipulation fieldGetAccess;

            private StaticFieldByteCodeAppender(TypeDescription instrumentedType) {
                this.fieldGetAccess = FieldAccess.forField((FieldDescription.InDefinedShape)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(ForValue.this.name))).getOnly()).read();
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                return ForValue.this.apply(methodVisitor, implementationContext, instrumentedMethod, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(ForValue.this.value.getClass()), this.fieldGetAccess);
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
                return this.fieldGetAccess.equals(((StaticFieldByteCodeAppender)object).fieldGetAccess);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldGetAccess.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForConstantValue
    extends FixedValue
    implements AssignerConfigurable,
    ByteCodeAppender {
        private final StackManipulation valueLoadInstruction;
        private final TypeDescription loadedType;

        protected ForConstantValue(StackManipulation valueLoadInstruction, Class<?> loadedType) {
            this(valueLoadInstruction, TypeDescription.ForLoadedType.of(loadedType));
        }

        protected ForConstantValue(StackManipulation valueLoadInstruction, TypeDescription loadedType) {
            this(Assigner.DEFAULT, Assigner.Typing.STATIC, valueLoadInstruction, loadedType);
        }

        private ForConstantValue(Assigner assigner, Assigner.Typing typing, StackManipulation valueLoadInstruction, TypeDescription loadedType) {
            super(assigner, typing);
            this.valueLoadInstruction = valueLoadInstruction;
            this.loadedType = loadedType;
        }

        @Override
        public Implementation withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new ForConstantValue(assigner, typing, this.valueLoadInstruction, this.loadedType);
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            return this.apply(methodVisitor, implementationContext, instrumentedMethod, this.loadedType.asGenericType(), this.valueLoadInstruction);
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
            if (!this.valueLoadInstruction.equals(((ForConstantValue)object).valueLoadInstruction)) {
                return false;
            }
            return this.loadedType.equals(((ForConstantValue)object).loadedType);
        }

        @Override
        public int hashCode() {
            return (super.hashCode() * 31 + this.valueLoadInstruction.hashCode()) * 31 + this.loadedType.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForArgument
    extends FixedValue
    implements AssignerConfigurable,
    ByteCodeAppender {
        private final int index;

        protected ForArgument(int index) {
            this(Assigner.DEFAULT, Assigner.Typing.STATIC, index);
        }

        private ForArgument(Assigner assigner, Assigner.Typing typing, int index) {
            super(assigner, typing);
            this.index = index;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if (instrumentedMethod.getParameters().size() <= this.index) {
                throw new IllegalStateException(instrumentedMethod + " does not define a parameter with index " + this.index);
            }
            ParameterDescription parameterDescription = (ParameterDescription)instrumentedMethod.getParameters().get(this.index);
            StackManipulation.Compound stackManipulation = new StackManipulation.Compound(MethodVariableAccess.load(parameterDescription), this.assigner.assign(parameterDescription.getType(), instrumentedMethod.getReturnType(), this.typing), MethodReturn.of(instrumentedMethod.getReturnType()));
            if (!stackManipulation.isValid()) {
                throw new IllegalStateException("Cannot assign " + instrumentedMethod.getReturnType() + " to " + parameterDescription);
            }
            return new ByteCodeAppender.Size(stackManipulation.apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public Implementation withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new ForArgument(assigner, typing, this.index);
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
            return this.index == ((ForArgument)object).index;
        }

        public int hashCode() {
            return super.hashCode() * 31 + this.index;
        }
    }

    protected static class ForThisValue
    extends FixedValue
    implements AssignerConfigurable {
        protected ForThisValue() {
            super(Assigner.DEFAULT, Assigner.Typing.STATIC);
        }

        private ForThisValue(Assigner assigner, Assigner.Typing typing) {
            super(assigner, typing);
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getInstrumentedType());
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public Implementation withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new ForThisValue(assigner, typing);
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class Appender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;

            protected Appender(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                if (instrumentedMethod.isStatic() || !this.instrumentedType.isAssignableTo(instrumentedMethod.getReturnType().asErasure())) {
                    throw new IllegalStateException("Cannot return 'this' from " + instrumentedMethod);
                }
                return new ByteCodeAppender.Simple(MethodVariableAccess.loadThis(), MethodReturn.REFERENCE).apply(methodVisitor, implementationContext, instrumentedMethod);
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

    protected static class ForOriginType
    extends FixedValue
    implements AssignerConfigurable {
        protected ForOriginType() {
            this(Assigner.DEFAULT, Assigner.Typing.STATIC);
        }

        private ForOriginType(Assigner assigner, Assigner.Typing typing) {
            super(assigner, typing);
        }

        public Implementation withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new ForOriginType(assigner, typing);
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getOriginType().asErasure());
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final TypeDescription originType;

            protected Appender(TypeDescription originType) {
                this.originType = originType;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                return ForOriginType.this.apply(methodVisitor, implementationContext, instrumentedMethod, TypeDescription.ForLoadedType.of(Class.class).asGenericType(), ClassConstant.of(this.originType));
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
                if (!this.originType.equals(((Appender)object).originType)) {
                    return false;
                }
                return ForOriginType.this.equals(((Appender)object).ForOriginType.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.originType.hashCode()) * 31 + ForOriginType.this.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ForNullValue implements Implementation,
    ByteCodeAppender
    {
        INSTANCE;


        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if (instrumentedMethod.getReturnType().isPrimitive()) {
                throw new IllegalStateException("Cannot return null from " + instrumentedMethod);
            }
            return new ByteCodeAppender.Simple(NullConstant.INSTANCE, MethodReturn.REFERENCE).apply(methodVisitor, implementationContext, instrumentedMethod);
        }
    }

    public static interface AssignerConfigurable
    extends Implementation {
        public Implementation withAssigner(Assigner var1, Assigner.Typing var2);
    }
}


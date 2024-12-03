/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public abstract class FieldAccessor
implements Implementation {
    protected final FieldLocation fieldLocation;
    protected final Assigner assigner;
    protected final Assigner.Typing typing;

    protected FieldAccessor(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing) {
        this.fieldLocation = fieldLocation;
        this.assigner = assigner;
        this.typing = typing;
    }

    public static OwnerTypeLocatable ofField(String name) {
        return FieldAccessor.of((FieldNameExtractor)new FieldNameExtractor.ForFixedValue(name));
    }

    public static OwnerTypeLocatable ofBeanProperty() {
        return FieldAccessor.of(FieldNameExtractor.ForBeanProperty.INSTANCE, FieldNameExtractor.ForBeanProperty.CAPITALIZED);
    }

    public static OwnerTypeLocatable of(FieldNameExtractor fieldNameExtractor) {
        return FieldAccessor.of(Collections.singletonList(fieldNameExtractor));
    }

    public static OwnerTypeLocatable of(FieldNameExtractor ... fieldNameExtractor) {
        return FieldAccessor.of(Arrays.asList(fieldNameExtractor));
    }

    public static OwnerTypeLocatable of(List<? extends FieldNameExtractor> fieldNameExtractors) {
        return new ForImplicitProperty(new FieldLocation.Relative(fieldNameExtractors));
    }

    public static AssignerConfigurable of(Field field) {
        return FieldAccessor.of(new FieldDescription.ForLoadedField(field));
    }

    public static AssignerConfigurable of(FieldDescription fieldDescription) {
        return new ForImplicitProperty(new FieldLocation.Absolute(fieldDescription));
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
        if (!this.typing.equals((Object)((FieldAccessor)object).typing)) {
            return false;
        }
        if (!this.fieldLocation.equals(((FieldAccessor)object).fieldLocation)) {
            return false;
        }
        return this.assigner.equals(((FieldAccessor)object).assigner);
    }

    public int hashCode() {
        return ((this.getClass().hashCode() * 31 + this.fieldLocation.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static abstract class ForSetter<T>
    extends FieldAccessor
    implements Implementation.Composable {
        private final TerminationHandler terminationHandler;

        protected ForSetter(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing, TerminationHandler terminationHandler) {
            super(fieldLocation, assigner, typing);
            this.terminationHandler = terminationHandler;
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget.getInstrumentedType(), this.initialize(implementationTarget.getInstrumentedType()), this.fieldLocation.prepare(implementationTarget.getInstrumentedType()));
        }

        @MaybeNull
        protected abstract T initialize(TypeDescription var1);

        protected abstract StackManipulation resolve(@MaybeNull T var1, FieldDescription var2, TypeDescription var3, MethodDescription var4);

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
            return this.terminationHandler.equals((Object)((ForSetter)object).terminationHandler);
        }

        @Override
        public int hashCode() {
            return super.hashCode() * 31 + this.terminationHandler.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final TypeDescription instrumentedType;
            @MaybeNull
            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
            private final T initialized;
            private final FieldLocation.Prepared fieldLocation;

            protected Appender(@MaybeNull TypeDescription instrumentedType, T initialized, FieldLocation.Prepared fieldLocation) {
                this.instrumentedType = instrumentedType;
                this.initialized = initialized;
                this.fieldLocation = fieldLocation;
            }

            @Override
            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                FieldDescription fieldDescription = this.fieldLocation.resolve(instrumentedMethod);
                if (!fieldDescription.isStatic() && instrumentedMethod.isStatic()) {
                    throw new IllegalStateException("Cannot set instance field " + fieldDescription + " from " + instrumentedMethod);
                }
                if (fieldDescription.isFinal() && instrumentedMethod.isMethod()) {
                    throw new IllegalStateException("Cannot set final field " + fieldDescription + " from " + instrumentedMethod);
                }
                StackManipulation stackManipulation = ForSetter.this.resolve(this.initialized, fieldDescription, this.instrumentedType, instrumentedMethod);
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Set value cannot be assigned to " + fieldDescription);
                }
                return new ByteCodeAppender.Size(new StackManipulation.Compound(instrumentedMethod.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), stackManipulation, FieldAccess.forField(fieldDescription).write(), ForSetter.this.terminationHandler.resolve(instrumentedMethod)).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
            }

            public boolean equals(@MaybeNull Object object) {
                block12: {
                    block11: {
                        Object t;
                        block10: {
                            Object t2;
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
                            Object t3 = ((Appender)object).initialized;
                            t = t2 = this.initialized;
                            if (t3 == null) break block10;
                            if (t == null) break block11;
                            if (!t2.equals(t3)) {
                                return false;
                            }
                            break block12;
                        }
                        if (t == null) break block12;
                    }
                    return false;
                }
                if (!this.fieldLocation.equals(((Appender)object).fieldLocation)) {
                    return false;
                }
                return ForSetter.this.equals(((Appender)object).ForSetter.this);
            }

            public int hashCode() {
                int n = (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31;
                Object t = this.initialized;
                if (t != null) {
                    n = n + t.hashCode();
                }
                return (n * 31 + this.fieldLocation.hashCode()) * 31 + ForSetter.this.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class OfFieldValue
        extends ForSetter<FieldLocation.Prepared> {
            private final FieldLocation target;

            protected OfFieldValue(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing, TerminationHandler terminationHandler, FieldLocation target) {
                super(fieldLocation, assigner, typing, terminationHandler);
                this.target = target;
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            protected FieldLocation.Prepared initialize(TypeDescription instrumentedType) {
                return this.target.prepare(instrumentedType);
            }

            @Override
            @SuppressFBWarnings(value={"NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE"}, justification="Expects its own initialized value as argument")
            protected StackManipulation resolve(@MaybeNull FieldLocation.Prepared target, FieldDescription fieldDescription, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                FieldDescription resolved = target.resolve(instrumentedMethod);
                if (!resolved.isStatic() && instrumentedMethod.isStatic()) {
                    throw new IllegalStateException("Cannot set instance field " + fieldDescription + " from " + instrumentedMethod);
                }
                return new StackManipulation.Compound(resolved.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(resolved).read(), this.assigner.assign(resolved.getType(), fieldDescription.getType(), this.typing));
            }

            @Override
            public Implementation andThen(Implementation implementation) {
                return new Implementation.Compound(new OfFieldValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.target), implementation);
            }

            @Override
            public Implementation.Composable andThen(Implementation.Composable implementation) {
                return new Implementation.Compound.Composable(new OfFieldValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.target), implementation);
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
                return this.target.equals(((OfFieldValue)object).target);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.target.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class OfReferenceValue
        extends ForSetter<FieldDescription.InDefinedShape> {
            protected static final String PREFIX = "fixedFieldValue";
            private final Object value;
            private final String name;

            protected OfReferenceValue(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing, TerminationHandler terminationHandler, Object value, String name) {
                super(fieldLocation, assigner, typing, terminationHandler);
                this.value = value;
                this.name = name;
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType.withAuxiliaryField(new FieldDescription.Token(this.name, 4105, TypeDescription.ForLoadedType.of(this.value.getClass()).asGenericType()), this.value);
            }

            @Override
            protected FieldDescription.InDefinedShape initialize(TypeDescription instrumentedType) {
                return (FieldDescription.InDefinedShape)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(this.name))).getOnly();
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Expects its own initialized value as argument")
            protected StackManipulation resolve(@MaybeNull FieldDescription.InDefinedShape target, FieldDescription fieldDescription, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                if (fieldDescription.isFinal() && instrumentedMethod.isMethod()) {
                    throw new IllegalArgumentException("Cannot set final field " + fieldDescription + " from " + instrumentedMethod);
                }
                return new StackManipulation.Compound(FieldAccess.forField(target).read(), this.assigner.assign(TypeDescription.ForLoadedType.of(this.value.getClass()).asGenericType(), fieldDescription.getType(), this.typing));
            }

            @Override
            public Implementation andThen(Implementation implementation) {
                return new Implementation.Compound(new OfReferenceValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.value, this.name), implementation);
            }

            @Override
            public Implementation.Composable andThen(Implementation.Composable implementation) {
                return new Implementation.Compound.Composable(new OfReferenceValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.value, this.name), implementation);
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
                if (!this.name.equals(((OfReferenceValue)object).name)) {
                    return false;
                }
                return this.value.equals(((OfReferenceValue)object).value);
            }

            @Override
            public int hashCode() {
                return (super.hashCode() * 31 + this.value.hashCode()) * 31 + this.name.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class OfConstantValue
        extends ForSetter<Void> {
            private final TypeDescription.Generic typeDescription;
            private final StackManipulation stackManipulation;

            protected OfConstantValue(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing, TerminationHandler terminationHandler, TypeDescription.Generic typeDescription, StackManipulation stackManipulation) {
                super(fieldLocation, assigner, typing, terminationHandler);
                this.typeDescription = typeDescription;
                this.stackManipulation = stackManipulation;
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            @AlwaysNull
            protected Void initialize(TypeDescription instrumentedType) {
                return null;
            }

            @Override
            protected StackManipulation resolve(@MaybeNull Void unused, FieldDescription fieldDescription, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                return new StackManipulation.Compound(this.stackManipulation, this.assigner.assign(this.typeDescription, fieldDescription.getType(), this.typing));
            }

            @Override
            public Implementation andThen(Implementation implementation) {
                return new Implementation.Compound(new OfConstantValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.typeDescription, this.stackManipulation), implementation);
            }

            @Override
            public Implementation.Composable andThen(Implementation.Composable implementation) {
                return new Implementation.Compound.Composable(new OfConstantValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.typeDescription, this.stackManipulation), implementation);
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
                if (!this.typeDescription.equals(((OfConstantValue)object).typeDescription)) {
                    return false;
                }
                return this.stackManipulation.equals(((OfConstantValue)object).stackManipulation);
            }

            @Override
            public int hashCode() {
                return (super.hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.stackManipulation.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfDefaultValue
        extends ForSetter<Void> {
            protected OfDefaultValue(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing, TerminationHandler terminationHandler) {
                super(fieldLocation, assigner, typing, terminationHandler);
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            @AlwaysNull
            protected Void initialize(TypeDescription instrumentedType) {
                return null;
            }

            @Override
            protected StackManipulation resolve(@MaybeNull Void initialized, FieldDescription fieldDescription, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                return DefaultValue.of(fieldDescription.getType());
            }

            @Override
            public Implementation andThen(Implementation implementation) {
                return new Implementation.Compound(new OfDefaultValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL), implementation);
            }

            @Override
            public Implementation.Composable andThen(Implementation.Composable implementation) {
                return new Implementation.Compound.Composable(new OfDefaultValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL), implementation);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class OfParameterValue
        extends ForSetter<Void> {
            private final int index;

            protected OfParameterValue(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing, TerminationHandler terminationHandler, int index) {
                super(fieldLocation, assigner, typing, terminationHandler);
                this.index = index;
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            @AlwaysNull
            protected Void initialize(TypeDescription instrumentedType) {
                return null;
            }

            @Override
            protected StackManipulation resolve(@MaybeNull Void unused, FieldDescription fieldDescription, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                if (instrumentedMethod.getParameters().size() <= this.index) {
                    throw new IllegalStateException(instrumentedMethod + " does not define a parameter with index " + this.index);
                }
                return new StackManipulation.Compound(MethodVariableAccess.load((ParameterDescription)instrumentedMethod.getParameters().get(this.index)), this.assigner.assign(((ParameterDescription)instrumentedMethod.getParameters().get(this.index)).getType(), fieldDescription.getType(), this.typing));
            }

            @Override
            public Implementation andThen(Implementation implementation) {
                return new Implementation.Compound(new OfParameterValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.index), implementation);
            }

            @Override
            public Implementation.Composable andThen(Implementation.Composable implementation) {
                return new Implementation.Compound.Composable(new OfParameterValue(this.fieldLocation, this.assigner, this.typing, TerminationHandler.NON_OPERATIONAL, this.index), implementation);
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
                return this.index == ((OfParameterValue)object).index;
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.index;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static enum TerminationHandler {
            RETURNING{

                protected StackManipulation resolve(MethodDescription instrumentedMethod) {
                    if (!instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                        throw new IllegalStateException("Cannot implement setter with return value for " + instrumentedMethod);
                    }
                    return MethodReturn.VOID;
                }
            }
            ,
            NON_OPERATIONAL{

                protected StackManipulation resolve(MethodDescription instrumentedMethod) {
                    return StackManipulation.Trivial.INSTANCE;
                }
            };


            protected abstract StackManipulation resolve(MethodDescription var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class ForImplicitProperty
    extends FieldAccessor
    implements OwnerTypeLocatable {
        protected ForImplicitProperty(FieldLocation fieldLocation) {
            this(fieldLocation, Assigner.DEFAULT, Assigner.Typing.STATIC);
        }

        private ForImplicitProperty(FieldLocation fieldLocation, Assigner assigner, Assigner.Typing typing) {
            super(fieldLocation, assigner, typing);
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(this.fieldLocation.prepare(implementationTarget.getInstrumentedType()));
        }

        @Override
        public Implementation.Composable setsArgumentAt(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("A parameter index cannot be negative: " + index);
            }
            return new ForSetter.OfParameterValue(this.fieldLocation, this.assigner, this.typing, ForSetter.TerminationHandler.RETURNING, index);
        }

        @Override
        public Implementation.Composable setsDefaultValue() {
            return new ForSetter.OfDefaultValue(this.fieldLocation, this.assigner, this.typing, ForSetter.TerminationHandler.RETURNING);
        }

        @Override
        public Implementation.Composable setsValue(@MaybeNull Object value) {
            if (value == null) {
                return this.setsDefaultValue();
            }
            ConstantValue constant = ConstantValue.Simple.wrapOrNull(value);
            return constant == null ? this.setsReference(value) : this.setsValue(constant.toStackManipulation(), constant.getTypeDescription().asGenericType());
        }

        @Override
        public Implementation.Composable setsValue(TypeDescription typeDescription) {
            return this.setsValue(ClassConstant.of(typeDescription), (Type)((Object)Class.class));
        }

        @Override
        public Implementation.Composable setsValue(ConstantValue constant) {
            return this.setsValue(constant.toStackManipulation(), constant.getTypeDescription().asGenericType());
        }

        @Override
        public Implementation.Composable setsValue(JavaConstant constant) {
            return this.setsValue((ConstantValue)constant);
        }

        @Override
        public Implementation.Composable setsValue(StackManipulation stackManipulation, Type type) {
            return this.setsValue(stackManipulation, TypeDefinition.Sort.describe(type));
        }

        @Override
        public Implementation.Composable setsValue(StackManipulation stackManipulation, TypeDescription.Generic typeDescription) {
            return new ForSetter.OfConstantValue(this.fieldLocation, this.assigner, this.typing, ForSetter.TerminationHandler.RETURNING, typeDescription, stackManipulation);
        }

        @Override
        public Implementation.Composable setsReference(Object value) {
            return this.setsReference(value, "fixedFieldValue$" + RandomString.hashOf(value));
        }

        @Override
        public Implementation.Composable setsReference(Object value, String name) {
            return new ForSetter.OfReferenceValue(this.fieldLocation, this.assigner, this.typing, ForSetter.TerminationHandler.RETURNING, value, name);
        }

        @Override
        public Implementation.Composable setsFieldValueOf(Field field) {
            return this.setsFieldValueOf(new FieldDescription.ForLoadedField(field));
        }

        @Override
        public Implementation.Composable setsFieldValueOf(FieldDescription fieldDescription) {
            return new ForSetter.OfFieldValue(this.fieldLocation, this.assigner, this.typing, ForSetter.TerminationHandler.RETURNING, new FieldLocation.Absolute(fieldDescription));
        }

        @Override
        public Implementation.Composable setsFieldValueOf(String fieldName) {
            return this.setsFieldValueOf(new FieldNameExtractor.ForFixedValue(fieldName));
        }

        @Override
        public Implementation.Composable setsFieldValueOf(FieldNameExtractor fieldNameExtractor) {
            return new ForSetter.OfFieldValue(this.fieldLocation, this.assigner, this.typing, ForSetter.TerminationHandler.RETURNING, new FieldLocation.Relative(Collections.singletonList(fieldNameExtractor)));
        }

        @Override
        public PropertyConfigurable withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new ForImplicitProperty(this.fieldLocation, assigner, typing);
        }

        @Override
        public AssignerConfigurable in(Class<?> type) {
            return this.in(TypeDescription.ForLoadedType.of(type));
        }

        @Override
        public AssignerConfigurable in(TypeDescription typeDescription) {
            return this.in(new FieldLocator.ForExactType.Factory(typeDescription));
        }

        @Override
        public AssignerConfigurable in(FieldLocator.Factory fieldLocatorFactory) {
            return new ForImplicitProperty(this.fieldLocation.with(fieldLocatorFactory), this.assigner, this.typing);
        }

        @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
        protected class Appender
        implements ByteCodeAppender {
            private final FieldLocation.Prepared fieldLocation;

            protected Appender(FieldLocation.Prepared fieldLocation) {
                this.fieldLocation = fieldLocation;
            }

            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                StackManipulation.Compound implementation;
                StackManipulation initialization;
                if (!instrumentedMethod.isMethod()) {
                    throw new IllegalArgumentException(instrumentedMethod + " does not describe a field getter or setter");
                }
                FieldDescription fieldDescription = this.fieldLocation.resolve(instrumentedMethod);
                if (!fieldDescription.isStatic() && instrumentedMethod.isStatic()) {
                    throw new IllegalStateException("Cannot set instance field " + fieldDescription + " from " + instrumentedMethod);
                }
                StackManipulation stackManipulation = initialization = fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis();
                if (!instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                    implementation = new StackManipulation.Compound(initialization, FieldAccess.forField(fieldDescription).read(), ForImplicitProperty.this.assigner.assign(fieldDescription.getType(), instrumentedMethod.getReturnType(), ForImplicitProperty.this.typing), MethodReturn.of(instrumentedMethod.getReturnType()));
                } else if (instrumentedMethod.getReturnType().represents(Void.TYPE) && instrumentedMethod.getParameters().size() == 1) {
                    if (fieldDescription.isFinal() && instrumentedMethod.isMethod()) {
                        throw new IllegalStateException("Cannot set final field " + fieldDescription + " from " + instrumentedMethod);
                    }
                    implementation = new StackManipulation.Compound(initialization, MethodVariableAccess.load((ParameterDescription)instrumentedMethod.getParameters().get(0)), ForImplicitProperty.this.assigner.assign(((ParameterDescription)instrumentedMethod.getParameters().get(0)).getType(), fieldDescription.getType(), ForImplicitProperty.this.typing), FieldAccess.forField(fieldDescription).write(), MethodReturn.VOID);
                } else {
                    throw new IllegalArgumentException("Method " + instrumentedMethod + " is no bean accessor");
                }
                if (!implementation.isValid()) {
                    throw new IllegalStateException("Cannot set or get value of " + instrumentedMethod + " using " + fieldDescription);
                }
                return new ByteCodeAppender.Size(implementation.apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
                if (!this.fieldLocation.equals(((Appender)object).fieldLocation)) {
                    return false;
                }
                return ForImplicitProperty.this.equals(((Appender)object).ForImplicitProperty.this);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.fieldLocation.hashCode()) * 31 + ForImplicitProperty.this.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface OwnerTypeLocatable
    extends AssignerConfigurable {
        public AssignerConfigurable in(Class<?> var1);

        public AssignerConfigurable in(TypeDescription var1);

        public AssignerConfigurable in(FieldLocator.Factory var1);
    }

    public static interface AssignerConfigurable
    extends PropertyConfigurable {
        public PropertyConfigurable withAssigner(Assigner var1, Assigner.Typing var2);
    }

    public static interface PropertyConfigurable
    extends Implementation {
        public Implementation.Composable setsArgumentAt(int var1);

        public Implementation.Composable setsDefaultValue();

        public Implementation.Composable setsValue(Object var1);

        public Implementation.Composable setsValue(TypeDescription var1);

        public Implementation.Composable setsValue(ConstantValue var1);

        public Implementation.Composable setsValue(JavaConstant var1);

        public Implementation.Composable setsValue(StackManipulation var1, Type var2);

        public Implementation.Composable setsValue(StackManipulation var1, TypeDescription.Generic var2);

        public Implementation.Composable setsReference(Object var1);

        public Implementation.Composable setsReference(Object var1, String var2);

        public Implementation.Composable setsFieldValueOf(Field var1);

        public Implementation.Composable setsFieldValueOf(FieldDescription var1);

        public Implementation.Composable setsFieldValueOf(String var1);

        public Implementation.Composable setsFieldValueOf(FieldNameExtractor var1);
    }

    public static interface FieldNameExtractor {
        public String resolve(MethodDescription var1);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForFixedValue
        implements FieldNameExtractor {
            private final String name;

            protected ForFixedValue(String name) {
                this.name = name;
            }

            public String resolve(MethodDescription methodDescription) {
                return this.name;
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
                return this.name.equals(((ForFixedValue)object).name);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.name.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForBeanProperty implements FieldNameExtractor
        {
            INSTANCE{

                protected char resolve(char character) {
                    return Character.toLowerCase(character);
                }
            }
            ,
            CAPITALIZED{

                protected char resolve(char character) {
                    return Character.toUpperCase(character);
                }
            };


            @Override
            public String resolve(MethodDescription methodDescription) {
                int crop;
                String name = methodDescription.getInternalName();
                if (name.startsWith("get") || name.startsWith("set")) {
                    crop = 3;
                } else if (name.startsWith("is")) {
                    crop = 2;
                } else {
                    throw new IllegalArgumentException(methodDescription + " does not follow Java bean naming conventions");
                }
                name = name.substring(crop);
                if (name.length() == 0) {
                    throw new IllegalArgumentException(methodDescription + " does not specify a bean name");
                }
                return this.resolve(name.charAt(0)) + name.substring(1);
            }

            protected abstract char resolve(char var1);
        }
    }

    protected static interface FieldLocation {
        public FieldLocation with(FieldLocator.Factory var1);

        public Prepared prepare(TypeDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Relative
        implements FieldLocation {
            private final List<? extends FieldNameExtractor> fieldNameExtractors;
            private final FieldLocator.Factory fieldLocatorFactory;

            protected Relative(List<? extends FieldNameExtractor> fieldNameExtractors) {
                this(fieldNameExtractors, FieldLocator.ForClassHierarchy.Factory.INSTANCE);
            }

            private Relative(List<? extends FieldNameExtractor> fieldNameExtractors, FieldLocator.Factory fieldLocatorFactory) {
                this.fieldNameExtractors = fieldNameExtractors;
                this.fieldLocatorFactory = fieldLocatorFactory;
            }

            @Override
            public FieldLocation with(FieldLocator.Factory fieldLocatorFactory) {
                return new Relative(this.fieldNameExtractors, fieldLocatorFactory);
            }

            @Override
            public net.bytebuddy.implementation.FieldAccessor$FieldLocation$Prepared prepare(TypeDescription instrumentedType) {
                return new Prepared(this.fieldNameExtractors, this.fieldLocatorFactory.make(instrumentedType));
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
                if (!((Object)this.fieldNameExtractors).equals(((Relative)object).fieldNameExtractors)) {
                    return false;
                }
                return this.fieldLocatorFactory.equals(((Relative)object).fieldLocatorFactory);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + ((Object)this.fieldNameExtractors).hashCode()) * 31 + this.fieldLocatorFactory.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Prepared
            implements net.bytebuddy.implementation.FieldAccessor$FieldLocation$Prepared {
                private final List<? extends FieldNameExtractor> fieldNameExtractors;
                private final FieldLocator fieldLocator;

                protected Prepared(List<? extends FieldNameExtractor> fieldNameExtractors, FieldLocator fieldLocator) {
                    this.fieldNameExtractors = fieldNameExtractors;
                    this.fieldLocator = fieldLocator;
                }

                @Override
                public FieldDescription resolve(MethodDescription instrumentedMethod) {
                    FieldLocator.Resolution resolution = FieldLocator.Resolution.Illegal.INSTANCE;
                    Iterator<? extends FieldNameExtractor> iterator = this.fieldNameExtractors.iterator();
                    while (iterator.hasNext() && !resolution.isResolved()) {
                        resolution = this.fieldLocator.locate(iterator.next().resolve(instrumentedMethod));
                    }
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Cannot resolve field for " + instrumentedMethod + " using " + this.fieldLocator);
                    }
                    return resolution.getField();
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
                    if (!((Object)this.fieldNameExtractors).equals(((Prepared)object).fieldNameExtractors)) {
                        return false;
                    }
                    return this.fieldLocator.equals(((Prepared)object).fieldLocator);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + ((Object)this.fieldNameExtractors).hashCode()) * 31 + this.fieldLocator.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Absolute
        implements FieldLocation,
        Prepared {
            private final FieldDescription fieldDescription;

            protected Absolute(FieldDescription fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            public FieldLocation with(FieldLocator.Factory fieldLocatorFactory) {
                throw new IllegalStateException("Cannot specify a field locator factory for an absolute field location");
            }

            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
            public Prepared prepare(TypeDescription instrumentedType) {
                if (!this.fieldDescription.isStatic() && !instrumentedType.isAssignableTo(this.fieldDescription.getDeclaringType().asErasure())) {
                    throw new IllegalStateException(this.fieldDescription + " is not declared by " + instrumentedType);
                }
                if (!this.fieldDescription.isAccessibleTo(instrumentedType)) {
                    throw new IllegalStateException("Cannot access " + this.fieldDescription + " from " + instrumentedType);
                }
                return this;
            }

            public FieldDescription resolve(MethodDescription instrumentedMethod) {
                return this.fieldDescription;
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
                return this.fieldDescription.equals(((Absolute)object).fieldDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
            }
        }

        public static interface Prepared {
            public FieldDescription resolve(MethodDescription var1);
        }
    }
}


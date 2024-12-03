/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.InstanceCheck;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class EqualsMethod
implements Implementation {
    private static final MethodDescription.InDefinedShape EQUALS = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isEquals())).getOnly();
    private final SuperClassCheck superClassCheck;
    private final TypeCompatibilityCheck typeCompatibilityCheck;
    private final ElementMatcher.Junction<? super FieldDescription.InDefinedShape> ignored;
    private final ElementMatcher.Junction<? super FieldDescription.InDefinedShape> nonNullable;
    private final Comparator<? super FieldDescription.InDefinedShape> comparator;

    protected EqualsMethod(SuperClassCheck superClassCheck) {
        this(superClassCheck, TypeCompatibilityCheck.EXACT, ElementMatchers.none(), ElementMatchers.none(), NaturalOrderComparator.INSTANCE);
    }

    private EqualsMethod(SuperClassCheck superClassCheck, TypeCompatibilityCheck typeCompatibilityCheck, ElementMatcher.Junction<? super FieldDescription.InDefinedShape> ignored, ElementMatcher.Junction<? super FieldDescription.InDefinedShape> nonNullable, Comparator<? super FieldDescription.InDefinedShape> comparator) {
        this.superClassCheck = superClassCheck;
        this.typeCompatibilityCheck = typeCompatibilityCheck;
        this.ignored = ignored;
        this.nonNullable = nonNullable;
        this.comparator = comparator;
    }

    public static EqualsMethod requiringSuperClassEquality() {
        return new EqualsMethod(SuperClassCheck.ENABLED);
    }

    public static EqualsMethod isolated() {
        return new EqualsMethod(SuperClassCheck.DISABLED);
    }

    public EqualsMethod withIgnoredFields(ElementMatcher<? super FieldDescription.InDefinedShape> ignored) {
        return new EqualsMethod(this.superClassCheck, this.typeCompatibilityCheck, this.ignored.or(ignored), this.nonNullable, this.comparator);
    }

    public EqualsMethod withNonNullableFields(ElementMatcher<? super FieldDescription.InDefinedShape> nonNullable) {
        return new EqualsMethod(this.superClassCheck, this.typeCompatibilityCheck, this.ignored, this.nonNullable.or(nonNullable), this.comparator);
    }

    public EqualsMethod withPrimitiveTypedFieldsFirst() {
        return this.withFieldOrder(TypePropertyComparator.FOR_PRIMITIVE_TYPES);
    }

    public EqualsMethod withEnumerationTypedFieldsFirst() {
        return this.withFieldOrder(TypePropertyComparator.FOR_ENUMERATION_TYPES);
    }

    public EqualsMethod withPrimitiveWrapperTypedFieldsFirst() {
        return this.withFieldOrder(TypePropertyComparator.FOR_PRIMITIVE_WRAPPER_TYPES);
    }

    public EqualsMethod withStringTypedFieldsFirst() {
        return this.withFieldOrder(TypePropertyComparator.FOR_STRING_TYPES);
    }

    public EqualsMethod withFieldOrder(Comparator<? super FieldDescription.InDefinedShape> comparator) {
        return new EqualsMethod(this.superClassCheck, this.typeCompatibilityCheck, this.ignored, this.nonNullable, new CompoundComparator(this.comparator, comparator));
    }

    public Implementation withSubclassEquality() {
        return new EqualsMethod(this.superClassCheck, TypeCompatibilityCheck.SUBCLASS, this.ignored, this.nonNullable, this.comparator);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        if (implementationTarget.getInstrumentedType().isInterface()) {
            throw new IllegalStateException("Cannot implement meaningful equals method for " + implementationTarget.getInstrumentedType());
        }
        ArrayList<FieldDescription.InDefinedShape> fields = new ArrayList<FieldDescription.InDefinedShape>((Collection<FieldDescription.InDefinedShape>)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.not(ElementMatchers.isStatic().or(this.ignored))));
        Collections.sort(fields, this.comparator);
        return new Appender(implementationTarget.getInstrumentedType(), new StackManipulation.Compound(this.superClassCheck.resolve(implementationTarget.getInstrumentedType()), MethodVariableAccess.loadThis(), MethodVariableAccess.REFERENCE.loadFrom(1), ConditionalReturn.onIdentity().returningTrue(), this.typeCompatibilityCheck.resolve(implementationTarget.getInstrumentedType())), fields, this.nonNullable);
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
        if (!this.superClassCheck.equals((Object)((EqualsMethod)object).superClassCheck)) {
            return false;
        }
        if (!this.typeCompatibilityCheck.equals((Object)((EqualsMethod)object).typeCompatibilityCheck)) {
            return false;
        }
        if (!this.ignored.equals(((EqualsMethod)object).ignored)) {
            return false;
        }
        if (!this.nonNullable.equals(((EqualsMethod)object).nonNullable)) {
            return false;
        }
        return ((Object)this.comparator).equals(((EqualsMethod)object).comparator);
    }

    public int hashCode() {
        return ((((this.getClass().hashCode() * 31 + this.superClassCheck.hashCode()) * 31 + this.typeCompatibilityCheck.hashCode()) * 31 + this.ignored.hashCode()) * 31 + this.nonNullable.hashCode()) * 31 + this.comparator.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    @SuppressFBWarnings(value={"SE_COMPARATOR_SHOULD_BE_SERIALIZABLE"}, justification="Not used within a serializable instance")
    protected static class CompoundComparator
    implements Comparator<FieldDescription.InDefinedShape> {
        private final List<Comparator<? super FieldDescription.InDefinedShape>> comparators = new ArrayList<Comparator<? super FieldDescription.InDefinedShape>>();

        protected CompoundComparator(Comparator<? super FieldDescription.InDefinedShape> ... comparator) {
            this(Arrays.asList(comparator));
        }

        protected CompoundComparator(List<? extends Comparator<? super FieldDescription.InDefinedShape>> comparators) {
            for (Comparator<? super FieldDescription.InDefinedShape> comparator : comparators) {
                if (comparator instanceof CompoundComparator) {
                    this.comparators.addAll(((CompoundComparator)comparator).comparators);
                    continue;
                }
                if (comparator instanceof NaturalOrderComparator) continue;
                this.comparators.add(comparator);
            }
        }

        @Override
        public int compare(FieldDescription.InDefinedShape left, FieldDescription.InDefinedShape right) {
            for (Comparator<? super FieldDescription.InDefinedShape> comparator : this.comparators) {
                int comparison = comparator.compare(left, right);
                if (comparison == 0) continue;
                return comparison;
            }
            return 0;
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.comparators).hashCode();
        }

        @Override
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
            return ((Object)this.comparators).equals(((CompoundComparator)object).comparators);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum TypePropertyComparator implements Comparator<FieldDescription.InDefinedShape>
    {
        FOR_PRIMITIVE_TYPES{

            protected boolean resolve(TypeDefinition typeDefinition) {
                return typeDefinition.isPrimitive();
            }
        }
        ,
        FOR_ENUMERATION_TYPES{

            protected boolean resolve(TypeDefinition typeDefinition) {
                return typeDefinition.isEnum();
            }
        }
        ,
        FOR_STRING_TYPES{

            protected boolean resolve(TypeDefinition typeDefinition) {
                return typeDefinition.represents((Type)((Object)String.class));
            }
        }
        ,
        FOR_PRIMITIVE_WRAPPER_TYPES{

            protected boolean resolve(TypeDefinition typeDefinition) {
                return typeDefinition.asErasure().isPrimitiveWrapper();
            }
        };


        @Override
        public int compare(FieldDescription.InDefinedShape left, FieldDescription.InDefinedShape right) {
            if (this.resolve(left.getType()) && !this.resolve(right.getType())) {
                return -1;
            }
            if (!this.resolve(left.getType()) && this.resolve(right.getType())) {
                return 1;
            }
            return 0;
        }

        protected abstract boolean resolve(TypeDefinition var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum NaturalOrderComparator implements Comparator<FieldDescription.InDefinedShape>
    {
        INSTANCE;


        @Override
        public int compare(FieldDescription.InDefinedShape left, FieldDescription.InDefinedShape right) {
            return 0;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ConditionalReturn
    extends StackManipulation.AbstractBase {
        private static final Object[] EMPTY = new Object[0];
        private final int jumpCondition;
        private final int value;

        protected ConditionalReturn(int jumpCondition) {
            this(jumpCondition, 3);
        }

        private ConditionalReturn(int jumpCondition, int value) {
            this.jumpCondition = jumpCondition;
            this.value = value;
        }

        protected static ConditionalReturn onZeroInteger() {
            return new ConditionalReturn(154);
        }

        protected static ConditionalReturn onNonZeroInteger() {
            return new ConditionalReturn(153);
        }

        protected static ConditionalReturn onNullValue() {
            return new ConditionalReturn(199);
        }

        protected static ConditionalReturn onNonIdentity() {
            return new ConditionalReturn(165);
        }

        protected static ConditionalReturn onIdentity() {
            return new ConditionalReturn(166);
        }

        protected static ConditionalReturn onNonEqualInteger() {
            return new ConditionalReturn(159);
        }

        protected StackManipulation returningTrue() {
            return new ConditionalReturn(this.jumpCondition, 4);
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            Label label = new Label();
            methodVisitor.visitJumpInsn(this.jumpCondition, label);
            methodVisitor.visitInsn(this.value);
            methodVisitor.visitInsn(172);
            methodVisitor.visitLabel(label);
            implementationContext.getFrameGeneration().same(methodVisitor, Arrays.asList(implementationContext.getInstrumentedType(), TypeDescription.ForLoadedType.of(Object.class)));
            return new StackManipulation.Size(-1, 1);
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
            if (this.jumpCondition != ((ConditionalReturn)object).jumpCondition) {
                return false;
            }
            return this.value == ((ConditionalReturn)object).value;
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.jumpCondition) * 31 + this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final TypeDescription instrumentedType;
        private final StackManipulation baseline;
        private final List<FieldDescription.InDefinedShape> fieldDescriptions;
        private final ElementMatcher<? super FieldDescription.InDefinedShape> nonNullable;

        protected Appender(TypeDescription instrumentedType, StackManipulation baseline, List<FieldDescription.InDefinedShape> fieldDescriptions, ElementMatcher<? super FieldDescription.InDefinedShape> nonNullable) {
            this.instrumentedType = instrumentedType;
            this.baseline = baseline;
            this.fieldDescriptions = fieldDescriptions;
            this.nonNullable = nonNullable;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if (instrumentedMethod.isStatic()) {
                throw new IllegalStateException("Hash code method must not be static: " + instrumentedMethod);
            }
            if (instrumentedMethod.getParameters().size() != 1 || ((ParameterDescription)instrumentedMethod.getParameters().getOnly()).getType().isPrimitive()) {
                throw new IllegalStateException();
            }
            if (!instrumentedMethod.getReturnType().represents(Boolean.TYPE)) {
                throw new IllegalStateException("Hash code method does not return primitive boolean: " + instrumentedMethod);
            }
            ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(3 + this.fieldDescriptions.size() * 8);
            stackManipulations.add(this.baseline);
            int padding = 0;
            for (FieldDescription.InDefinedShape fieldDescription : this.fieldDescriptions) {
                stackManipulations.add(MethodVariableAccess.loadThis());
                stackManipulations.add(FieldAccess.forField(fieldDescription).read());
                stackManipulations.add(MethodVariableAccess.REFERENCE.loadFrom(1));
                stackManipulations.add(TypeCasting.to(this.instrumentedType));
                stackManipulations.add(FieldAccess.forField(fieldDescription).read());
                NullValueGuard.NoOp nullValueGuard = fieldDescription.getType().isPrimitive() || fieldDescription.getType().isArray() || this.nonNullable.matches(fieldDescription) ? NullValueGuard.NoOp.INSTANCE : new NullValueGuard.UsingJump(instrumentedMethod);
                stackManipulations.add(nullValueGuard.before());
                stackManipulations.add(ValueComparator.of(fieldDescription.getType()));
                stackManipulations.add(nullValueGuard.after());
                padding = Math.max(padding, nullValueGuard.getRequiredVariablePadding());
            }
            stackManipulations.add(IntegerConstant.forValue(true));
            stackManipulations.add(MethodReturn.INTEGER);
            return new ByteCodeAppender.Size(new StackManipulation.Compound(stackManipulations).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize() + padding);
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
            if (!this.baseline.equals(((Appender)object).baseline)) {
                return false;
            }
            if (!((Object)this.fieldDescriptions).equals(((Appender)object).fieldDescriptions)) {
                return false;
            }
            return this.nonNullable.equals(((Appender)object).nonNullable);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.baseline.hashCode()) * 31 + ((Object)this.fieldDescriptions).hashCode()) * 31 + this.nonNullable.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ValueComparator implements StackManipulation
    {
        LONG{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitInsn(148);
                return new StackManipulation.Size(-2, 0);
            }
        }
        ,
        FLOAT{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/lang/Float", "compare", "(FF)I", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        DOUBLE{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/lang/Double", "compare", "(DD)I", false);
                return new StackManipulation.Size(-2, 0);
            }
        }
        ,
        BOOLEAN_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([Z[Z)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        BYTE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([B[B)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        SHORT_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([S[S)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        CHARACTER_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([C[C)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        INTEGER_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([I[I)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        LONG_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([J[J)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        FLOAT_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([F[F)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        DOUBLE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([D[D)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        REFERENCE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "equals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        NESTED_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "deepEquals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z", false);
                return new StackManipulation.Size(-1, 0);
            }
        };


        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public static StackManipulation of(TypeDefinition typeDefinition) {
            if (typeDefinition.represents(Boolean.TYPE) || typeDefinition.represents(Byte.TYPE) || typeDefinition.represents(Short.TYPE) || typeDefinition.represents(Character.TYPE) || typeDefinition.represents(Integer.TYPE)) {
                return ConditionalReturn.onNonEqualInteger();
            }
            if (typeDefinition.represents(Long.TYPE)) {
                return new StackManipulation.Compound(LONG, ConditionalReturn.onNonZeroInteger());
            }
            if (typeDefinition.represents(Float.TYPE)) {
                return new StackManipulation.Compound(FLOAT, ConditionalReturn.onNonZeroInteger());
            }
            if (typeDefinition.represents(Double.TYPE)) {
                return new StackManipulation.Compound(DOUBLE, ConditionalReturn.onNonZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)boolean[].class))) {
                return new StackManipulation.Compound(BOOLEAN_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)byte[].class))) {
                return new StackManipulation.Compound(BYTE_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)short[].class))) {
                return new StackManipulation.Compound(SHORT_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)char[].class))) {
                return new StackManipulation.Compound(CHARACTER_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)int[].class))) {
                return new StackManipulation.Compound(INTEGER_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)long[].class))) {
                return new StackManipulation.Compound(LONG_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)float[].class))) {
                return new StackManipulation.Compound(FLOAT_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.represents((Type)((Object)double[].class))) {
                return new StackManipulation.Compound(DOUBLE_ARRAY, ConditionalReturn.onZeroInteger());
            }
            if (typeDefinition.isArray()) {
                return new StackManipulation.Compound(typeDefinition.getComponentType().isArray() ? NESTED_ARRAY : REFERENCE_ARRAY, ConditionalReturn.onZeroInteger());
            }
            return new StackManipulation.Compound(MethodInvocation.invoke(EQUALS).virtual(typeDefinition.asErasure()), ConditionalReturn.onZeroInteger());
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    protected static interface NullValueGuard {
        public StackManipulation before();

        public StackManipulation after();

        public int getRequiredVariablePadding();

        @HashCodeAndEqualsPlugin.Enhance
        public static class UsingJump
        implements NullValueGuard {
            private final MethodDescription instrumentedMethod;
            private final Label firstValueNull;
            private final Label secondValueNull;
            private final Label endOfBlock;

            protected UsingJump(MethodDescription instrumentedMethod) {
                this.instrumentedMethod = instrumentedMethod;
                this.firstValueNull = new Label();
                this.secondValueNull = new Label();
                this.endOfBlock = new Label();
            }

            public StackManipulation before() {
                return new BeforeInstruction();
            }

            public StackManipulation after() {
                return new AfterInstruction();
            }

            public int getRequiredVariablePadding() {
                return 2;
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
                if (!this.instrumentedMethod.equals(((UsingJump)object).instrumentedMethod)) {
                    return false;
                }
                if (!this.firstValueNull.equals(((UsingJump)object).firstValueNull)) {
                    return false;
                }
                if (!this.secondValueNull.equals(((UsingJump)object).secondValueNull)) {
                    return false;
                }
                return this.endOfBlock.equals(((UsingJump)object).endOfBlock);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.instrumentedMethod.hashCode()) * 31 + this.firstValueNull.hashCode()) * 31 + this.secondValueNull.hashCode()) * 31 + this.endOfBlock.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class AfterInstruction
            extends StackManipulation.AbstractBase {
                protected AfterInstruction() {
                }

                public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    methodVisitor.visitJumpInsn(167, UsingJump.this.endOfBlock);
                    methodVisitor.visitLabel(UsingJump.this.secondValueNull);
                    implementationContext.getFrameGeneration().same1(methodVisitor, TypeDescription.ForLoadedType.of(Object.class), Arrays.asList(implementationContext.getInstrumentedType(), TypeDescription.ForLoadedType.of(Object.class)));
                    methodVisitor.visitJumpInsn(198, UsingJump.this.endOfBlock);
                    methodVisitor.visitLabel(UsingJump.this.firstValueNull);
                    implementationContext.getFrameGeneration().same(methodVisitor, Arrays.asList(implementationContext.getInstrumentedType(), TypeDescription.ForLoadedType.of(Object.class)));
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitInsn(172);
                    methodVisitor.visitLabel(UsingJump.this.endOfBlock);
                    implementationContext.getFrameGeneration().same(methodVisitor, Arrays.asList(implementationContext.getInstrumentedType(), TypeDescription.ForLoadedType.of(Object.class)));
                    return StackManipulation.Size.ZERO;
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
                    return UsingJump.this.equals(((AfterInstruction)object).UsingJump.this);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + UsingJump.this.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class BeforeInstruction
            extends StackManipulation.AbstractBase {
                protected BeforeInstruction() {
                }

                public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    methodVisitor.visitVarInsn(58, UsingJump.this.instrumentedMethod.getStackSize());
                    methodVisitor.visitVarInsn(58, UsingJump.this.instrumentedMethod.getStackSize() + 1);
                    methodVisitor.visitVarInsn(25, UsingJump.this.instrumentedMethod.getStackSize() + 1);
                    methodVisitor.visitVarInsn(25, UsingJump.this.instrumentedMethod.getStackSize());
                    methodVisitor.visitJumpInsn(198, UsingJump.this.secondValueNull);
                    methodVisitor.visitJumpInsn(198, UsingJump.this.firstValueNull);
                    methodVisitor.visitVarInsn(25, UsingJump.this.instrumentedMethod.getStackSize() + 1);
                    methodVisitor.visitVarInsn(25, UsingJump.this.instrumentedMethod.getStackSize());
                    return StackManipulation.Size.ZERO;
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
                    return UsingJump.this.equals(((BeforeInstruction)object).UsingJump.this);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + UsingJump.this.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements NullValueGuard
        {
            INSTANCE;


            @Override
            public StackManipulation before() {
                return StackManipulation.Trivial.INSTANCE;
            }

            @Override
            public StackManipulation after() {
                return StackManipulation.Trivial.INSTANCE;
            }

            @Override
            public int getRequiredVariablePadding() {
                return StackSize.ZERO.getSize();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum TypeCompatibilityCheck {
        EXACT{

            public StackManipulation resolve(TypeDescription instrumentedType) {
                return new StackManipulation.Compound(MethodVariableAccess.REFERENCE.loadFrom(1), ConditionalReturn.onNullValue(), MethodVariableAccess.REFERENCE.loadFrom(0), MethodInvocation.invoke(GET_CLASS), MethodVariableAccess.REFERENCE.loadFrom(1), MethodInvocation.invoke(GET_CLASS), ConditionalReturn.onNonIdentity());
            }
        }
        ,
        SUBCLASS{

            protected StackManipulation resolve(TypeDescription instrumentedType) {
                return new StackManipulation.Compound(MethodVariableAccess.REFERENCE.loadFrom(1), InstanceCheck.of(instrumentedType), ConditionalReturn.onZeroInteger());
            }
        };

        protected static final MethodDescription.InDefinedShape GET_CLASS;

        protected abstract StackManipulation resolve(TypeDescription var1);

        static {
            GET_CLASS = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.named("getClass"))).getOnly();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum SuperClassCheck {
        DISABLED{

            protected StackManipulation resolve(TypeDescription instrumentedType) {
                return StackManipulation.Trivial.INSTANCE;
            }
        }
        ,
        ENABLED{

            protected StackManipulation resolve(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                if (superClass == null) {
                    throw new IllegalStateException(instrumentedType + " does not declare a super class");
                }
                return new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodVariableAccess.REFERENCE.loadFrom(1), MethodInvocation.invoke(EQUALS).special(superClass.asErasure()), ConditionalReturn.onZeroInteger());
            }
        };


        protected abstract StackManipulation resolve(TypeDescription var1);
    }
}


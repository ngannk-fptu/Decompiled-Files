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
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.Addition;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Multiplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
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
public class HashCodeMethod
implements Implementation {
    private static final int DEFAULT_OFFSET = 17;
    private static final int DEFAULT_MULTIPLIER = 31;
    private static final MethodDescription.InDefinedShape HASH_CODE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isHashCode())).getOnly();
    private static final MethodDescription.InDefinedShape GET_CLASS = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.named("getClass").and(ElementMatchers.takesArguments(0)))).getOnly();
    private final OffsetProvider offsetProvider;
    private final int multiplier;
    private final ElementMatcher.Junction<? super FieldDescription.InDefinedShape> ignored;
    private final ElementMatcher.Junction<? super FieldDescription.InDefinedShape> nonNullable;

    protected HashCodeMethod(OffsetProvider offsetProvider) {
        this(offsetProvider, 31, ElementMatchers.none(), ElementMatchers.none());
    }

    private HashCodeMethod(OffsetProvider offsetProvider, int multiplier, ElementMatcher.Junction<? super FieldDescription.InDefinedShape> ignored, ElementMatcher.Junction<? super FieldDescription.InDefinedShape> nonNullable) {
        this.offsetProvider = offsetProvider;
        this.multiplier = multiplier;
        this.ignored = ignored;
        this.nonNullable = nonNullable;
    }

    public static HashCodeMethod usingSuperClassOffset() {
        return new HashCodeMethod(OffsetProvider.ForSuperMethodCall.INSTANCE);
    }

    public static HashCodeMethod usingTypeHashOffset(boolean dynamic) {
        return new HashCodeMethod((OffsetProvider)((Object)(dynamic ? OffsetProvider.ForDynamicTypeHash.INSTANCE : OffsetProvider.ForStaticTypeHash.INSTANCE)));
    }

    public static HashCodeMethod usingDefaultOffset() {
        return HashCodeMethod.usingOffset(17);
    }

    public static HashCodeMethod usingOffset(int value) {
        return new HashCodeMethod(new OffsetProvider.ForFixedValue(value));
    }

    public HashCodeMethod withIgnoredFields(ElementMatcher<? super FieldDescription.InDefinedShape> ignored) {
        return new HashCodeMethod(this.offsetProvider, this.multiplier, this.ignored.or(ignored), this.nonNullable);
    }

    public HashCodeMethod withNonNullableFields(ElementMatcher<? super FieldDescription.InDefinedShape> nonNullable) {
        return new HashCodeMethod(this.offsetProvider, this.multiplier, this.ignored, this.nonNullable.or(nonNullable));
    }

    public Implementation withMultiplier(int multiplier) {
        if (multiplier == 0) {
            throw new IllegalArgumentException("Hash code multiplier must not be zero");
        }
        return new HashCodeMethod(this.offsetProvider, multiplier, this.ignored, this.nonNullable);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        if (implementationTarget.getInstrumentedType().isInterface()) {
            throw new IllegalStateException("Cannot implement meaningful hash code method for " + implementationTarget.getInstrumentedType());
        }
        return new Appender(this.offsetProvider.resolve(implementationTarget.getInstrumentedType()), this.multiplier, (List<FieldDescription.InDefinedShape>)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.not(ElementMatchers.isStatic().or(this.ignored))), (ElementMatcher<? super FieldDescription.InDefinedShape>)this.nonNullable);
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
        if (this.multiplier != ((HashCodeMethod)object).multiplier) {
            return false;
        }
        if (!this.offsetProvider.equals(((HashCodeMethod)object).offsetProvider)) {
            return false;
        }
        if (!this.ignored.equals(((HashCodeMethod)object).ignored)) {
            return false;
        }
        return this.nonNullable.equals(((HashCodeMethod)object).nonNullable);
    }

    public int hashCode() {
        return (((this.getClass().hashCode() * 31 + this.offsetProvider.hashCode()) * 31 + this.multiplier) * 31 + this.ignored.hashCode()) * 31 + this.nonNullable.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final StackManipulation initialValue;
        private final int multiplier;
        private final List<FieldDescription.InDefinedShape> fieldDescriptions;
        private final ElementMatcher<? super FieldDescription.InDefinedShape> nonNullable;

        protected Appender(StackManipulation initialValue, int multiplier, List<FieldDescription.InDefinedShape> fieldDescriptions, ElementMatcher<? super FieldDescription.InDefinedShape> nonNullable) {
            this.initialValue = initialValue;
            this.multiplier = multiplier;
            this.fieldDescriptions = fieldDescriptions;
            this.nonNullable = nonNullable;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if (instrumentedMethod.isStatic()) {
                throw new IllegalStateException("Hash code method must not be static: " + instrumentedMethod);
            }
            if (!instrumentedMethod.getReturnType().represents(Integer.TYPE)) {
                throw new IllegalStateException("Hash code method does not return primitive integer: " + instrumentedMethod);
            }
            ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(2 + this.fieldDescriptions.size() * 8);
            stackManipulations.add(this.initialValue);
            int padding = 0;
            for (FieldDescription.InDefinedShape fieldDescription : this.fieldDescriptions) {
                stackManipulations.add(IntegerConstant.forValue(this.multiplier));
                stackManipulations.add(Multiplication.INTEGER);
                stackManipulations.add(MethodVariableAccess.loadThis());
                stackManipulations.add(FieldAccess.forField(fieldDescription).read());
                NullValueGuard.NoOp nullValueGuard = fieldDescription.getType().isPrimitive() || fieldDescription.getType().isArray() || this.nonNullable.matches(fieldDescription) ? NullValueGuard.NoOp.INSTANCE : new NullValueGuard.UsingJump(instrumentedMethod);
                stackManipulations.add(nullValueGuard.before());
                stackManipulations.add(ValueTransformer.of(fieldDescription.getType()));
                stackManipulations.add(Addition.INTEGER);
                stackManipulations.add(nullValueGuard.after());
                padding = Math.max(padding, nullValueGuard.getRequiredVariablePadding());
            }
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
            if (this.multiplier != ((Appender)object).multiplier) {
                return false;
            }
            if (!this.initialValue.equals(((Appender)object).initialValue)) {
                return false;
            }
            if (!((Object)this.fieldDescriptions).equals(((Appender)object).fieldDescriptions)) {
                return false;
            }
            return this.nonNullable.equals(((Appender)object).nonNullable);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.initialValue.hashCode()) * 31 + this.multiplier) * 31 + ((Object)this.fieldDescriptions).hashCode()) * 31 + this.nonNullable.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ValueTransformer implements StackManipulation
    {
        LONG{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitInsn(92);
                methodVisitor.visitIntInsn(16, 32);
                methodVisitor.visitInsn(125);
                methodVisitor.visitInsn(131);
                methodVisitor.visitInsn(136);
                return new StackManipulation.Size(-1, 3);
            }
        }
        ,
        FLOAT{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/lang/Float", "floatToIntBits", "(F)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        DOUBLE{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/lang/Double", "doubleToLongBits", "(D)J", false);
                methodVisitor.visitInsn(92);
                methodVisitor.visitIntInsn(16, 32);
                methodVisitor.visitInsn(125);
                methodVisitor.visitInsn(131);
                methodVisitor.visitInsn(136);
                return new StackManipulation.Size(-1, 3);
            }
        }
        ,
        BOOLEAN_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([Z)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        BYTE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([B)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        SHORT_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([S)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        CHARACTER_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([C)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        INTEGER_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([I)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        LONG_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([J)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        FLOAT_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([F)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        DOUBLE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([D)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        REFERENCE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "hashCode", "([Ljava/lang/Object;)I", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        NESTED_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "deepHashCode", "([Ljava/lang/Object;)I", false);
                return StackManipulation.Size.ZERO;
            }
        };


        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public static StackManipulation of(TypeDefinition typeDefinition) {
            if (typeDefinition.represents(Boolean.TYPE) || typeDefinition.represents(Byte.TYPE) || typeDefinition.represents(Short.TYPE) || typeDefinition.represents(Character.TYPE) || typeDefinition.represents(Integer.TYPE)) {
                return StackManipulation.Trivial.INSTANCE;
            }
            if (typeDefinition.represents(Long.TYPE)) {
                return LONG;
            }
            if (typeDefinition.represents(Float.TYPE)) {
                return FLOAT;
            }
            if (typeDefinition.represents(Double.TYPE)) {
                return DOUBLE;
            }
            if (typeDefinition.represents((Type)((Object)boolean[].class))) {
                return BOOLEAN_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)byte[].class))) {
                return BYTE_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)short[].class))) {
                return SHORT_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)char[].class))) {
                return CHARACTER_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)int[].class))) {
                return INTEGER_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)long[].class))) {
                return LONG_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)float[].class))) {
                return FLOAT_ARRAY;
            }
            if (typeDefinition.represents((Type)((Object)double[].class))) {
                return DOUBLE_ARRAY;
            }
            if (typeDefinition.isArray()) {
                return typeDefinition.getComponentType().isArray() ? NESTED_ARRAY : REFERENCE_ARRAY;
            }
            return MethodInvocation.invoke(HASH_CODE).virtual(typeDefinition.asErasure());
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
            private final Label label;

            protected UsingJump(MethodDescription instrumentedMethod) {
                this.instrumentedMethod = instrumentedMethod;
                this.label = new Label();
            }

            public StackManipulation before() {
                return new BeforeInstruction();
            }

            public StackManipulation after() {
                return new AfterInstruction();
            }

            public int getRequiredVariablePadding() {
                return 1;
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
                return this.label.equals(((UsingJump)object).label);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentedMethod.hashCode()) * 31 + this.label.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class AfterInstruction
            extends StackManipulation.AbstractBase {
                protected AfterInstruction() {
                }

                public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    methodVisitor.visitLabel(UsingJump.this.label);
                    implementationContext.getFrameGeneration().same1(methodVisitor, TypeDescription.ForLoadedType.of(Integer.TYPE), Arrays.asList(implementationContext.getInstrumentedType(), TypeDescription.ForLoadedType.of(Object.class)));
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
                    methodVisitor.visitVarInsn(25, UsingJump.this.instrumentedMethod.getStackSize());
                    methodVisitor.visitJumpInsn(198, UsingJump.this.label);
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

    protected static interface OffsetProvider {
        public StackManipulation resolve(TypeDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForDynamicTypeHash implements OffsetProvider
        {
            INSTANCE;


            @Override
            public StackManipulation resolve(TypeDescription instrumentedType) {
                return new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(GET_CLASS).virtual(instrumentedType), MethodInvocation.invoke(HASH_CODE).virtual(TypeDescription.ForLoadedType.of(Class.class)));
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForStaticTypeHash implements OffsetProvider
        {
            INSTANCE;


            @Override
            public StackManipulation resolve(TypeDescription instrumentedType) {
                return new StackManipulation.Compound(ClassConstant.of(instrumentedType), MethodInvocation.invoke(HASH_CODE).virtual(TypeDescription.ForLoadedType.of(Class.class)));
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForSuperMethodCall implements OffsetProvider
        {
            INSTANCE;


            @Override
            public StackManipulation resolve(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                if (superClass == null) {
                    throw new IllegalStateException(instrumentedType + " does not declare a super class");
                }
                return new StackManipulation.Compound(MethodVariableAccess.loadThis(), MethodInvocation.invoke(HASH_CODE).special(superClass.asErasure()));
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForFixedValue
        implements OffsetProvider {
            private final int value;

            protected ForFixedValue(int value) {
                this.value = value;
            }

            public StackManipulation resolve(TypeDescription instrumentedType) {
                return IntegerConstant.forValue(this.value);
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
                return this.value == ((ForFixedValue)object).value;
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.value;
            }
        }
    }
}


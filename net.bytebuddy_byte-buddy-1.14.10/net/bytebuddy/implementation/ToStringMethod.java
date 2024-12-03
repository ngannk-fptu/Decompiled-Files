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
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ToStringMethod
implements Implementation {
    private static final MethodDescription.InDefinedShape STRING_BUILDER_CONSTRUCTOR = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(StringBuilder.class).getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(String.class)))).getOnly();
    private static final MethodDescription.InDefinedShape TO_STRING = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(StringBuilder.class).getDeclaredMethods().filter(ElementMatchers.isToString())).getOnly();
    private final PrefixResolver prefixResolver;
    private final String start;
    private final String end;
    private final String separator;
    private final String definer;
    private final ElementMatcher.Junction<? super FieldDescription.InDefinedShape> ignored;

    protected ToStringMethod(PrefixResolver prefixResolver) {
        this(prefixResolver, "{", "}", ", ", "=", ElementMatchers.none());
    }

    private ToStringMethod(PrefixResolver prefixResolver, String start, String end, String separator, String definer, ElementMatcher.Junction<? super FieldDescription.InDefinedShape> ignored) {
        this.prefixResolver = prefixResolver;
        this.start = start;
        this.end = end;
        this.separator = separator;
        this.definer = definer;
        this.ignored = ignored;
    }

    public static ToStringMethod prefixedByFullyQualifiedClassName() {
        return ToStringMethod.prefixedBy(PrefixResolver.Default.FULLY_QUALIFIED_CLASS_NAME);
    }

    public static ToStringMethod prefixedByCanonicalClassName() {
        return ToStringMethod.prefixedBy(PrefixResolver.Default.CANONICAL_CLASS_NAME);
    }

    public static ToStringMethod prefixedBySimpleClassName() {
        return ToStringMethod.prefixedBy(PrefixResolver.Default.SIMPLE_CLASS_NAME);
    }

    public static ToStringMethod prefixedBy(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        return ToStringMethod.prefixedBy(new PrefixResolver.ForFixedValue(prefix));
    }

    public static ToStringMethod prefixedBy(PrefixResolver prefixResolver) {
        return new ToStringMethod(prefixResolver);
    }

    public ToStringMethod withIgnoredFields(ElementMatcher<? super FieldDescription.InDefinedShape> ignored) {
        return new ToStringMethod(this.prefixResolver, this.start, this.end, this.separator, this.definer, this.ignored.or(ignored));
    }

    public Implementation withTokens(String start, String end, String separator, String definer) {
        if (start == null || end == null || separator == null || definer == null) {
            throw new IllegalArgumentException("Token values cannot be null");
        }
        return new ToStringMethod(this.prefixResolver, start, end, separator, definer, this.ignored);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }

    @Override
    public Appender appender(Implementation.Target implementationTarget) {
        if (implementationTarget.getInstrumentedType().isInterface()) {
            throw new IllegalStateException("Cannot implement meaningful toString method for " + implementationTarget.getInstrumentedType());
        }
        String prefix = this.prefixResolver.resolve(implementationTarget.getInstrumentedType());
        if (prefix == null) {
            throw new IllegalStateException("Prefix for toString method cannot be null");
        }
        return new Appender(prefix, this.start, this.end, this.separator, this.definer, (List<? extends FieldDescription.InDefinedShape>)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.not(ElementMatchers.isStatic().or(this.ignored))));
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
        if (!this.start.equals(((ToStringMethod)object).start)) {
            return false;
        }
        if (!this.end.equals(((ToStringMethod)object).end)) {
            return false;
        }
        if (!this.separator.equals(((ToStringMethod)object).separator)) {
            return false;
        }
        if (!this.definer.equals(((ToStringMethod)object).definer)) {
            return false;
        }
        if (!this.prefixResolver.equals(((ToStringMethod)object).prefixResolver)) {
            return false;
        }
        return this.ignored.equals(((ToStringMethod)object).ignored);
    }

    public int hashCode() {
        return (((((this.getClass().hashCode() * 31 + this.prefixResolver.hashCode()) * 31 + this.start.hashCode()) * 31 + this.end.hashCode()) * 31 + this.separator.hashCode()) * 31 + this.definer.hashCode()) * 31 + this.ignored.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ValueConsumer implements StackManipulation
    {
        BOOLEAN{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        CHARACTER{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        INTEGER{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        LONG{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        FLOAT{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        DOUBLE{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false);
                return new StackManipulation.Size(-1, 0);
            }
        }
        ,
        STRING{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        CHARACTER_SEQUENCE{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        OBJECT{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        BOOLEAN_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([Z)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        BYTE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([B)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        SHORT_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([S)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        CHARACTER_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([C)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        INTEGER_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([I)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        LONG_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([J)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        FLOAT_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([F)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        DOUBLE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([D)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        REFERENCE_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        }
        ,
        NESTED_ARRAY{

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitMethodInsn(184, "java/util/Arrays", "deepToString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                return StackManipulation.Size.ZERO;
            }
        };


        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        protected static StackManipulation of(TypeDescription typeDescription) {
            if (typeDescription.represents(Boolean.TYPE)) {
                return BOOLEAN;
            }
            if (typeDescription.represents(Character.TYPE)) {
                return CHARACTER;
            }
            if (typeDescription.represents(Byte.TYPE) || typeDescription.represents(Short.TYPE) || typeDescription.represents(Integer.TYPE)) {
                return INTEGER;
            }
            if (typeDescription.represents(Long.TYPE)) {
                return LONG;
            }
            if (typeDescription.represents(Float.TYPE)) {
                return FLOAT;
            }
            if (typeDescription.represents(Double.TYPE)) {
                return DOUBLE;
            }
            if (typeDescription.represents((Type)((Object)String.class))) {
                return STRING;
            }
            if (typeDescription.isAssignableTo(CharSequence.class)) {
                return CHARACTER_SEQUENCE;
            }
            if (typeDescription.represents((Type)((Object)boolean[].class))) {
                return BOOLEAN_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)byte[].class))) {
                return BYTE_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)short[].class))) {
                return SHORT_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)char[].class))) {
                return CHARACTER_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)int[].class))) {
                return INTEGER_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)long[].class))) {
                return LONG_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)float[].class))) {
                return FLOAT_ARRAY;
            }
            if (typeDescription.represents((Type)((Object)double[].class))) {
                return DOUBLE_ARRAY;
            }
            if (typeDescription.isArray()) {
                return typeDescription.getComponentType().isArray() ? NESTED_ARRAY : REFERENCE_ARRAY;
            }
            return OBJECT;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    public static interface PrefixResolver {
        @MaybeNull
        public String resolve(TypeDescription var1);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForFixedValue
        implements PrefixResolver {
            private final String prefix;

            protected ForFixedValue(String prefix) {
                this.prefix = prefix;
            }

            public String resolve(TypeDescription instrumentedType) {
                return this.prefix;
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
                return this.prefix.equals(((ForFixedValue)object).prefix);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.prefix.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements PrefixResolver
        {
            FULLY_QUALIFIED_CLASS_NAME{

                public String resolve(TypeDescription instrumentedType) {
                    return instrumentedType.getName();
                }
            }
            ,
            CANONICAL_CLASS_NAME{

                @MaybeNull
                public String resolve(TypeDescription instrumentedType) {
                    return instrumentedType.getCanonicalName();
                }
            }
            ,
            SIMPLE_CLASS_NAME{

                public String resolve(TypeDescription instrumentedType) {
                    return instrumentedType.getSimpleName();
                }
            };

        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final String prefix;
        private final String start;
        private final String end;
        private final String separator;
        private final String definer;
        private final List<? extends FieldDescription.InDefinedShape> fieldDescriptions;

        protected Appender(String prefix, String start, String end, String separator, String definer, List<? extends FieldDescription.InDefinedShape> fieldDescriptions) {
            this.prefix = prefix;
            this.start = start;
            this.end = end;
            this.separator = separator;
            this.definer = definer;
            this.fieldDescriptions = fieldDescriptions;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if (instrumentedMethod.isStatic()) {
                throw new IllegalStateException("toString method must not be static: " + instrumentedMethod);
            }
            if (!instrumentedMethod.getReturnType().asErasure().isAssignableFrom(String.class)) {
                throw new IllegalStateException("toString method does not return String-compatible type: " + instrumentedMethod);
            }
            ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(Math.max(0, this.fieldDescriptions.size() * 7 - 2) + 10);
            stackManipulations.add(TypeCreation.of(TypeDescription.ForLoadedType.of(StringBuilder.class)));
            stackManipulations.add(Duplication.SINGLE);
            stackManipulations.add(new TextConstant(this.prefix));
            stackManipulations.add(MethodInvocation.invoke(STRING_BUILDER_CONSTRUCTOR));
            stackManipulations.add(new TextConstant(this.start));
            stackManipulations.add(ValueConsumer.STRING);
            boolean first = true;
            for (FieldDescription.InDefinedShape inDefinedShape : this.fieldDescriptions) {
                if (first) {
                    first = false;
                } else {
                    stackManipulations.add(new TextConstant(this.separator));
                    stackManipulations.add(ValueConsumer.STRING);
                }
                stackManipulations.add(new TextConstant(inDefinedShape.getName() + this.definer));
                stackManipulations.add(ValueConsumer.STRING);
                stackManipulations.add(MethodVariableAccess.loadThis());
                stackManipulations.add(FieldAccess.forField(inDefinedShape).read());
                stackManipulations.add(ValueConsumer.of(inDefinedShape.getType().asErasure()));
            }
            stackManipulations.add(new TextConstant(this.end));
            stackManipulations.add(ValueConsumer.STRING);
            stackManipulations.add(MethodInvocation.invoke(TO_STRING));
            stackManipulations.add(MethodReturn.REFERENCE);
            return new ByteCodeAppender.Size(new StackManipulation.Compound(stackManipulations).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
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
            if (!this.prefix.equals(((Appender)object).prefix)) {
                return false;
            }
            if (!this.start.equals(((Appender)object).start)) {
                return false;
            }
            if (!this.end.equals(((Appender)object).end)) {
                return false;
            }
            if (!this.separator.equals(((Appender)object).separator)) {
                return false;
            }
            if (!this.definer.equals(((Appender)object).definer)) {
                return false;
            }
            return ((Object)this.fieldDescriptions).equals(((Appender)object).fieldDescriptions);
        }

        public int hashCode() {
            return (((((this.getClass().hashCode() * 31 + this.prefix.hashCode()) * 31 + this.start.hashCode()) * 31 + this.end.hashCode()) * 31 + this.separator.hashCode()) * 31 + this.definer.hashCode()) * 31 + ((Object)this.fieldDescriptions).hashCode();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.asm;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.constant.FieldConstant;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.SerializedConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.visitor.LocalVariableAwareMethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MemberSubstitution
implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
    protected static final int THIS_REFERENCE = 0;
    private final MethodGraph.Compiler methodGraphCompiler;
    private final TypePoolResolver typePoolResolver;
    private final boolean strict;
    private final boolean failIfNoMatch;
    private final Replacement.Factory replacementFactory;

    protected MemberSubstitution(boolean strict) {
        this(MethodGraph.Compiler.DEFAULT, TypePoolResolver.OfImplicitPool.INSTANCE, strict, false, Replacement.NoOp.INSTANCE);
    }

    protected MemberSubstitution(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory) {
        this.methodGraphCompiler = methodGraphCompiler;
        this.typePoolResolver = typePoolResolver;
        this.failIfNoMatch = failIfNoMatch;
        this.strict = strict;
        this.replacementFactory = replacementFactory;
    }

    public static MemberSubstitution strict() {
        return new MemberSubstitution(true);
    }

    public static MemberSubstitution relaxed() {
        return new MemberSubstitution(false);
    }

    public WithoutSpecification element(ElementMatcher<? super ByteCodeElement.Member> matcher) {
        return new WithoutSpecification.ForMatchedByteCodeElement(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, matcher);
    }

    public WithoutSpecification.ForMatchedField field(ElementMatcher<? super FieldDescription> matcher) {
        return new WithoutSpecification.ForMatchedField(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, matcher);
    }

    public WithoutSpecification.ForMatchedMethod method(ElementMatcher<? super MethodDescription> matcher) {
        return new WithoutSpecification.ForMatchedMethod(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, matcher);
    }

    public WithoutSpecification constructor(ElementMatcher<? super MethodDescription> matcher) {
        return this.invokable(ElementMatchers.isConstructor().and(matcher));
    }

    public WithoutSpecification invokable(ElementMatcher<? super MethodDescription> matcher) {
        return new WithoutSpecification.ForMatchedMethod(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, matcher);
    }

    public MemberSubstitution with(MethodGraph.Compiler methodGraphCompiler) {
        return new MemberSubstitution(methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory);
    }

    public MemberSubstitution with(TypePoolResolver typePoolResolver) {
        return new MemberSubstitution(this.methodGraphCompiler, typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory);
    }

    public MemberSubstitution failIfNoMatch(boolean failIfNoMatch) {
        return new MemberSubstitution(this.methodGraphCompiler, this.typePoolResolver, this.strict, failIfNoMatch, this.replacementFactory);
    }

    public AsmVisitorWrapper.ForDeclaredMethods on(ElementMatcher<? super MethodDescription> matcher) {
        return new AsmVisitorWrapper.ForDeclaredMethods().invokable(matcher, this);
    }

    @Override
    public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
        typePool = this.typePoolResolver.resolve(instrumentedType, instrumentedMethod, typePool);
        return new SubstitutingMethodVisitor(methodVisitor, instrumentedType, instrumentedMethod, this.methodGraphCompiler, this.strict, this.failIfNoMatch, this.replacementFactory.make(instrumentedType, instrumentedMethod, typePool), implementationContext, typePool, implementationContext.getClassFileVersion().isAtLeast(ClassFileVersion.JAVA_V11));
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
        if (this.strict != ((MemberSubstitution)object).strict) {
            return false;
        }
        if (this.failIfNoMatch != ((MemberSubstitution)object).failIfNoMatch) {
            return false;
        }
        if (!this.methodGraphCompiler.equals(((MemberSubstitution)object).methodGraphCompiler)) {
            return false;
        }
        if (!this.typePoolResolver.equals(((MemberSubstitution)object).typePoolResolver)) {
            return false;
        }
        return this.replacementFactory.equals(((MemberSubstitution)object).replacementFactory);
    }

    public int hashCode() {
        return ((((this.getClass().hashCode() * 31 + this.methodGraphCompiler.hashCode()) * 31 + this.typePoolResolver.hashCode()) * 31 + this.strict) * 31 + this.failIfNoMatch) * 31 + this.replacementFactory.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Source {
        SUBSTITUTED_ELEMENT{

            @Override
            protected ByteCodeElement.Member element(ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return original;
            }

            @Override
            @MaybeNull
            protected Value self(TypeList.Generic parameters, Map<Integer, Integer> offsets, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return original.isStatic() ? null : new Value((TypeDescription.Generic)parameters.get(0), offsets.get(0));
            }

            @Override
            @MaybeNull
            protected Value argument(int index, TypeList.Generic parameters, Map<Integer, Integer> offsets, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return index < parameters.size() - (original.isStatic() ? 0 : 1) ? new Value((TypeDescription.Generic)parameters.get(index + (original.isStatic() ? 0 : 1)), offsets.get(index + (original.isStatic() ? 0 : 1))) : null;
            }

            @Override
            protected List<Value> arguments(boolean includesSelf, TypeList.Generic parameters, Map<Integer, Integer> offsets, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                int index;
                ArrayList<Value> values = new ArrayList<Value>(parameters.size() - (!includesSelf && !original.isStatic() ? 1 : 0));
                int n = index = original.isStatic() || includesSelf ? 0 : 1;
                while (index < parameters.size()) {
                    values.add(new Value((TypeDescription.Generic)parameters.get(index), offsets.get(index)));
                    ++index;
                }
                return values;
            }

            @Override
            protected JavaConstant.MethodHandle handle(JavaConstant.MethodHandle methodHandle, MethodDescription instrumentedMethod) {
                return methodHandle;
            }

            @Override
            protected boolean isRepresentable(Substitution.Chain.Step.ForDelegation.OffsetMapping.ForOrigin.Sort sort, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return sort.isRepresentable(original);
            }

            @Override
            protected StackManipulation resolve(Substitution.Chain.Step.ForDelegation.OffsetMapping.ForOrigin.Sort sort, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, MethodDescription instrumentedMethod) {
                return sort.resolve(original, parameters.asErasures(), result.asErasure());
            }
        }
        ,
        ENCLOSING_METHOD{

            @Override
            protected ByteCodeElement.Member element(ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return instrumentedMethod;
            }

            @Override
            @MaybeNull
            protected Value self(TypeList.Generic parameters, Map<Integer, Integer> offsets, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return instrumentedMethod.isStatic() ? null : new Value(instrumentedMethod.getDeclaringType().asGenericType(), 0);
            }

            @Override
            @MaybeNull
            protected Value argument(int index, TypeList.Generic parameters, Map<Integer, Integer> offsets, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                if (index < instrumentedMethod.getParameters().size()) {
                    ParameterDescription parameterDescription = (ParameterDescription)instrumentedMethod.getParameters().get(index);
                    return new Value(parameterDescription.getType(), parameterDescription.getOffset());
                }
                return null;
            }

            @Override
            protected List<Value> arguments(boolean includesSelf, TypeList.Generic parameters, Map<Integer, Integer> offsets, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                ArrayList<Value> values;
                if (includesSelf && !instrumentedMethod.isStatic()) {
                    values = new ArrayList(instrumentedMethod.getParameters().size() + 1);
                    values.add(new Value(instrumentedMethod.getDeclaringType().asGenericType(), 0));
                } else {
                    values = new ArrayList<Value>(instrumentedMethod.getParameters().size());
                }
                for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                    values.add(new Value(parameterDescription.getType(), parameterDescription.getOffset()));
                }
                return values;
            }

            @Override
            protected JavaConstant.MethodHandle handle(JavaConstant.MethodHandle methodHandle, MethodDescription instrumentedMethod) {
                return JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)instrumentedMethod.asDefined());
            }

            @Override
            protected boolean isRepresentable(Substitution.Chain.Step.ForDelegation.OffsetMapping.ForOrigin.Sort sort, ByteCodeElement.Member original, MethodDescription instrumentedMethod) {
                return sort.isRepresentable(instrumentedMethod);
            }

            @Override
            protected StackManipulation resolve(Substitution.Chain.Step.ForDelegation.OffsetMapping.ForOrigin.Sort sort, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, MethodDescription instrumentedMethod) {
                return sort.resolve(instrumentedMethod, instrumentedMethod.isStatic() || instrumentedMethod.isConstructor() ? instrumentedMethod.getParameters().asTypeList().asErasures() : CompoundList.of(instrumentedMethod.getDeclaringType().asErasure(), instrumentedMethod.getParameters().asTypeList().asErasures()), instrumentedMethod.isConstructor() ? instrumentedMethod.getDeclaringType().asErasure() : instrumentedMethod.getReturnType().asErasure());
            }
        };


        protected abstract ByteCodeElement.Member element(ByteCodeElement.Member var1, MethodDescription var2);

        @MaybeNull
        protected abstract Value self(TypeList.Generic var1, Map<Integer, Integer> var2, ByteCodeElement.Member var3, MethodDescription var4);

        @MaybeNull
        protected abstract Value argument(int var1, TypeList.Generic var2, Map<Integer, Integer> var3, ByteCodeElement.Member var4, MethodDescription var5);

        protected abstract List<Value> arguments(boolean var1, TypeList.Generic var2, Map<Integer, Integer> var3, ByteCodeElement.Member var4, MethodDescription var5);

        protected abstract JavaConstant.MethodHandle handle(JavaConstant.MethodHandle var1, MethodDescription var2);

        protected abstract boolean isRepresentable(Substitution.Chain.Step.ForDelegation.OffsetMapping.ForOrigin.Sort var1, ByteCodeElement.Member var2, MethodDescription var3);

        protected abstract StackManipulation resolve(Substitution.Chain.Step.ForDelegation.OffsetMapping.ForOrigin.Sort var1, ByteCodeElement.Member var2, TypeList.Generic var3, TypeDescription.Generic var4, MethodDescription var5);

        @HashCodeAndEqualsPlugin.Enhance
        protected static class Value {
            private final TypeDescription.Generic typeDescription;
            private final int offset;

            protected Value(TypeDescription.Generic typeDescription, int offset) {
                this.typeDescription = typeDescription;
                this.offset = offset;
            }

            protected TypeDescription.Generic getTypeDescription() {
                return this.typeDescription;
            }

            protected int getOffset() {
                return this.offset;
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
                if (this.offset != ((Value)object).offset) {
                    return false;
                }
                return this.typeDescription.equals(((Value)object).typeDescription);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.offset;
            }
        }
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface Current {
        public Assigner.Typing typing() default Assigner.Typing.STATIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface StubValue {
        public Source source() default Source.SUBSTITUTED_ELEMENT;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Unused {
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface Origin {
        public Source source() default Source.SUBSTITUTED_ELEMENT;
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
    @Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface FieldValue {
        public String value() default "";

        public Class<?> declaringType() default void.class;

        public Assigner.Typing typing() default Assigner.Typing.STATIC;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface SelfCallHandle {
        public Source source() default Source.SUBSTITUTED_ELEMENT;

        public boolean bound() default true;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface AllArguments {
        public Assigner.Typing typing() default Assigner.Typing.STATIC;

        public Source source() default Source.SUBSTITUTED_ELEMENT;

        public boolean includeSelf() default false;

        public boolean nullIfEmpty() default false;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface Argument {
        public int value();

        public Assigner.Typing typing() default Assigner.Typing.STATIC;

        public Source source() default Source.SUBSTITUTED_ELEMENT;

        public boolean optional() default false;
    }

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface This {
        public Assigner.Typing typing() default Assigner.Typing.STATIC;

        public Source source() default Source.SUBSTITUTED_ELEMENT;

        public boolean optional() default false;
    }

    protected static class SubstitutingMethodVisitor
    extends LocalVariableAwareMethodVisitor {
        private final TypeDescription instrumentedType;
        private final MethodDescription instrumentedMethod;
        private final MethodGraph.Compiler methodGraphCompiler;
        private final boolean strict;
        private final boolean failIfNoMatch;
        private final Replacement replacement;
        private final Implementation.Context implementationContext;
        private final TypePool typePool;
        private final boolean virtualPrivateCalls;
        private int stackSizeBuffer;
        private int localVariableExtension;
        private boolean matched;

        protected SubstitutingMethodVisitor(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodGraph.Compiler methodGraphCompiler, boolean strict, boolean failIfNoMatch, Replacement replacement, Implementation.Context implementationContext, TypePool typePool, boolean virtualPrivateCalls) {
            super(methodVisitor, instrumentedMethod);
            this.instrumentedType = instrumentedType;
            this.instrumentedMethod = instrumentedMethod;
            this.methodGraphCompiler = methodGraphCompiler;
            this.strict = strict;
            this.failIfNoMatch = failIfNoMatch;
            this.replacement = replacement;
            this.implementationContext = implementationContext;
            this.typePool = typePool;
            this.virtualPrivateCalls = virtualPrivateCalls;
            this.stackSizeBuffer = 0;
            this.localVariableExtension = 0;
        }

        public void visitFieldInsn(int opcode, String owner, String internalName, String descriptor) {
            TypePool.Resolution resolution = this.typePool.describe(owner.replace('/', '.'));
            if (resolution.isResolved()) {
                FieldList candidates;
                Iterator iterator = resolution.resolve().iterator();
                do {
                    candidates = (FieldList)((TypeDefinition)iterator.next()).getDeclaredFields().filter(this.strict ? ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor)) : ElementMatchers.failSafe(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor))));
                } while (iterator.hasNext() && candidates.isEmpty());
                if (!candidates.isEmpty()) {
                    Replacement.Binding binding = this.replacement.bind(this.instrumentedType, this.instrumentedMethod, resolution.resolve(), (FieldDescription)candidates.getOnly(), opcode == 181 || opcode == 179);
                    if (binding.isBound()) {
                        boolean read;
                        TypeDescription.Generic result;
                        AbstractList parameters;
                        switch (opcode) {
                            case 181: {
                                parameters = new TypeList.Generic.Explicit(((FieldDescription)candidates.getOnly()).getDeclaringType(), ((FieldDescription)candidates.getOnly()).getType());
                                result = TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE);
                                read = false;
                                break;
                            }
                            case 179: {
                                parameters = new TypeList.Generic.Explicit(((FieldDescription)candidates.getOnly()).getType());
                                result = TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE);
                                read = false;
                                break;
                            }
                            case 180: {
                                parameters = new TypeList.Generic.Explicit(((FieldDescription)candidates.getOnly()).getDeclaringType());
                                result = ((FieldDescription)candidates.getOnly()).getType();
                                read = true;
                                break;
                            }
                            case 178: {
                                parameters = new TypeList.Generic.Empty();
                                result = ((FieldDescription)candidates.getOnly()).getType();
                                read = true;
                                break;
                            }
                            default: {
                                throw new IllegalStateException("Unexpected opcode: " + opcode);
                            }
                        }
                        this.stackSizeBuffer = Math.max(this.stackSizeBuffer, binding.make((TypeList.Generic)((Object)parameters), result, read ? JavaConstant.MethodHandle.ofGetter((FieldDescription.InDefinedShape)((FieldDescription)candidates.getOnly()).asDefined()) : JavaConstant.MethodHandle.ofSetter((FieldDescription.InDefinedShape)((FieldDescription)candidates.getOnly()).asDefined()), read ? FieldAccess.forField((FieldDescription)candidates.getOnly()).read() : FieldAccess.forField((FieldDescription)candidates.getOnly()).write(), this.getFreeOffset()).apply(new LocalVariableTracingMethodVisitor(this.mv), this.implementationContext).getMaximalSize());
                        this.matched = true;
                        return;
                    }
                } else if (this.strict) {
                    throw new IllegalStateException("Could not resolve " + owner.replace('/', '.') + "." + internalName + descriptor + " using " + this.typePool);
                }
            } else if (this.strict) {
                throw new IllegalStateException("Could not resolve " + owner.replace('/', '.') + " using " + this.typePool);
            }
            super.visitFieldInsn(opcode, owner, internalName, descriptor);
        }

        public void visitMethodInsn(int opcode, String owner, String internalName, String descriptor, boolean isInterface) {
            TypePool.Resolution resolution = this.typePool.describe(owner.replace('/', '.'));
            if (resolution.isResolved()) {
                MethodList candidates;
                if (opcode == 183 && internalName.equals("<init>")) {
                    candidates = (MethodList)resolution.resolve().getDeclaredMethods().filter(this.strict ? ElementMatchers.isConstructor().and(ElementMatchers.hasDescriptor(descriptor)) : ElementMatchers.failSafe(ElementMatchers.isConstructor().and(ElementMatchers.hasDescriptor(descriptor))));
                } else if (opcode == 184) {
                    Iterator iterator = resolution.resolve().iterator();
                    do {
                        candidates = (MethodList)((TypeDefinition)iterator.next()).getDeclaredMethods().filter(this.strict ? ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor)) : ElementMatchers.failSafe(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor))));
                    } while (iterator.hasNext() && candidates.isEmpty());
                } else if (opcode == 183) {
                    candidates = (MethodList)resolution.resolve().getDeclaredMethods().filter(this.strict ? ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor)) : ElementMatchers.failSafe(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor))));
                } else if (this.virtualPrivateCalls) {
                    candidates = (MethodList)resolution.resolve().getDeclaredMethods().filter(this.strict ? ElementMatchers.isPrivate().and(ElementMatchers.not(ElementMatchers.isStatic())).and(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor))) : ElementMatchers.failSafe(ElementMatchers.isPrivate().and(ElementMatchers.not(ElementMatchers.isStatic())).and(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor)))));
                    if (candidates.isEmpty()) {
                        candidates = (MethodList)this.methodGraphCompiler.compile((TypeDefinition)resolution.resolve(), this.instrumentedType).listNodes().asMethodList().filter(this.strict ? ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor)) : ElementMatchers.failSafe(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor))));
                    }
                } else {
                    candidates = (MethodList)this.methodGraphCompiler.compile((TypeDefinition)resolution.resolve(), this.instrumentedType).listNodes().asMethodList().filter(this.strict ? ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor)) : ElementMatchers.failSafe(ElementMatchers.named(internalName).and(ElementMatchers.hasDescriptor(descriptor))));
                }
                if (!candidates.isEmpty()) {
                    Replacement.Binding binding = this.replacement.bind(this.instrumentedType, this.instrumentedMethod, resolution.resolve(), (MethodDescription)candidates.getOnly(), Replacement.InvocationType.of(opcode, (MethodDescription)candidates.getOnly()));
                    if (binding.isBound()) {
                        StackManipulation.Size size = binding.make(((MethodDescription)candidates.getOnly()).isStatic() || ((MethodDescription)candidates.getOnly()).isConstructor() ? ((MethodDescription)candidates.getOnly()).getParameters().asTypeList() : new TypeList.Generic.Explicit(CompoundList.of(resolution.resolve(), ((MethodDescription)candidates.getOnly()).getParameters().asTypeList())), ((MethodDescription)candidates.getOnly()).isConstructor() ? ((MethodDescription)candidates.getOnly()).getDeclaringType().asGenericType() : ((MethodDescription)candidates.getOnly()).getReturnType(), opcode == 183 && ((MethodDescription)candidates.getOnly()).isMethod() && !((MethodDescription)candidates.getOnly()).isPrivate() ? JavaConstant.MethodHandle.ofSpecial((MethodDescription.InDefinedShape)((MethodDescription)candidates.getOnly()).asDefined(), resolution.resolve()) : JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)((MethodDescription)candidates.getOnly()).asDefined()), opcode == 183 && ((MethodDescription)candidates.getOnly()).isMethod() && !((MethodDescription)candidates.getOnly()).isPrivate() ? MethodInvocation.invoke((MethodDescription)candidates.getOnly()).special(resolution.resolve()) : MethodInvocation.invoke((MethodDescription)candidates.getOnly()), this.getFreeOffset()).apply(new LocalVariableTracingMethodVisitor(this.mv), this.implementationContext);
                        if (((MethodDescription)candidates.getOnly()).isConstructor()) {
                            this.stackSizeBuffer = Math.max(this.stackSizeBuffer, size.getMaximalSize() + 2);
                            this.stackSizeBuffer = Math.max(this.stackSizeBuffer, new StackManipulation.Compound(Duplication.SINGLE.flipOver(TypeDescription.ForLoadedType.of(Object.class)), Removal.SINGLE, Removal.SINGLE, Duplication.SINGLE.flipOver(TypeDescription.ForLoadedType.of(Object.class)), Removal.SINGLE, Removal.SINGLE).apply(this.mv, this.implementationContext).getMaximalSize() + StackSize.SINGLE.getSize());
                        } else {
                            this.stackSizeBuffer = Math.max(this.stackSizeBuffer, size.getMaximalSize());
                        }
                        this.matched = true;
                        return;
                    }
                } else if (this.strict) {
                    throw new IllegalStateException("Could not resolve " + owner.replace('/', '.') + "." + internalName + descriptor + " using " + this.typePool);
                }
            } else if (this.strict) {
                throw new IllegalStateException("Could not resolve " + owner.replace('/', '.') + " using " + this.typePool);
            }
            super.visitMethodInsn(opcode, owner, internalName, descriptor, isInterface);
        }

        public void visitMaxs(int stackSize, int localVariableLength) {
            if (this.failIfNoMatch && !this.matched) {
                throw new IllegalStateException("No substitution found within " + this.instrumentedMethod + " of " + this.instrumentedType);
            }
            super.visitMaxs(stackSize + this.stackSizeBuffer, Math.max(this.localVariableExtension, localVariableLength));
        }

        private class LocalVariableTracingMethodVisitor
        extends MethodVisitor {
            private LocalVariableTracingMethodVisitor(MethodVisitor methodVisitor) {
                super(OpenedClassReader.ASM_API, methodVisitor);
            }

            @SuppressFBWarnings(value={"SF_SWITCH_NO_DEFAULT"}, justification="No action required on default option.")
            public void visitVarInsn(int opcode, int offset) {
                switch (opcode) {
                    case 54: 
                    case 56: 
                    case 58: {
                        SubstitutingMethodVisitor.this.localVariableExtension = Math.max(SubstitutingMethodVisitor.this.localVariableExtension, offset + 1);
                        break;
                    }
                    case 55: 
                    case 57: {
                        SubstitutingMethodVisitor.this.localVariableExtension = Math.max(SubstitutingMethodVisitor.this.localVariableExtension, offset + 2);
                    }
                }
                super.visitVarInsn(opcode, offset);
            }
        }
    }

    protected static interface Replacement {
        public Binding bind(TypeDescription var1, MethodDescription var2, TypeDescription var3, FieldDescription var4, boolean var5);

        public Binding bind(TypeDescription var1, MethodDescription var2, TypeDescription var3, MethodDescription var4, InvocationType var5);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForFirstBinding
        implements Replacement {
            private final List<? extends Replacement> replacements;

            protected ForFirstBinding(List<? extends Replacement> replacements) {
                this.replacements = replacements;
            }

            @Override
            public Binding bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypeDescription typeDescription, FieldDescription fieldDescription, boolean writeAccess) {
                for (Replacement replacement : this.replacements) {
                    Binding binding = replacement.bind(instrumentedType, instrumentedMethod, typeDescription, fieldDescription, writeAccess);
                    if (!binding.isBound()) continue;
                    return binding;
                }
                return Binding.Unresolved.INSTANCE;
            }

            @Override
            public Binding bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypeDescription typeDescription, MethodDescription methodDescription, InvocationType invocationType) {
                for (Replacement replacement : this.replacements) {
                    Binding binding = replacement.bind(instrumentedType, instrumentedMethod, typeDescription, methodDescription, invocationType);
                    if (!binding.isBound()) continue;
                    return binding;
                }
                return Binding.Unresolved.INSTANCE;
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
                return ((Object)this.replacements).equals(((ForFirstBinding)object).replacements);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.replacements).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForElementMatchers
        implements Replacement {
            private final ElementMatcher<? super FieldDescription> fieldMatcher;
            private final ElementMatcher<? super MethodDescription> methodMatcher;
            private final boolean matchFieldRead;
            private final boolean matchFieldWrite;
            private final boolean includeVirtualCalls;
            private final boolean includeSuperCalls;
            private final Substitution substitution;

            protected ForElementMatchers(ElementMatcher<? super FieldDescription> fieldMatcher, ElementMatcher<? super MethodDescription> methodMatcher, boolean matchFieldRead, boolean matchFieldWrite, boolean includeVirtualCalls, boolean includeSuperCalls, Substitution substitution) {
                this.fieldMatcher = fieldMatcher;
                this.methodMatcher = methodMatcher;
                this.matchFieldRead = matchFieldRead;
                this.matchFieldWrite = matchFieldWrite;
                this.includeVirtualCalls = includeVirtualCalls;
                this.includeSuperCalls = includeSuperCalls;
                this.substitution = substitution;
            }

            @Override
            public Binding bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypeDescription typeDescription, FieldDescription fieldDescription, boolean writeAccess) {
                return (writeAccess ? this.matchFieldWrite : this.matchFieldRead) && this.fieldMatcher.matches(fieldDescription) ? new Binding.Resolved(typeDescription, fieldDescription, this.substitution) : Binding.Unresolved.INSTANCE;
            }

            @Override
            public Binding bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypeDescription typeDescription, MethodDescription methodDescription, InvocationType invocationType) {
                return invocationType.matches(this.includeVirtualCalls, this.includeSuperCalls) && this.methodMatcher.matches(methodDescription) ? new Binding.Resolved(typeDescription, methodDescription, this.substitution) : Binding.Unresolved.INSTANCE;
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
                if (this.matchFieldRead != ((ForElementMatchers)object).matchFieldRead) {
                    return false;
                }
                if (this.matchFieldWrite != ((ForElementMatchers)object).matchFieldWrite) {
                    return false;
                }
                if (this.includeVirtualCalls != ((ForElementMatchers)object).includeVirtualCalls) {
                    return false;
                }
                if (this.includeSuperCalls != ((ForElementMatchers)object).includeSuperCalls) {
                    return false;
                }
                if (!this.fieldMatcher.equals(((ForElementMatchers)object).fieldMatcher)) {
                    return false;
                }
                if (!this.methodMatcher.equals(((ForElementMatchers)object).methodMatcher)) {
                    return false;
                }
                return this.substitution.equals(((ForElementMatchers)object).substitution);
            }

            public int hashCode() {
                return ((((((this.getClass().hashCode() * 31 + this.fieldMatcher.hashCode()) * 31 + this.methodMatcher.hashCode()) * 31 + this.matchFieldRead) * 31 + this.matchFieldWrite) * 31 + this.includeVirtualCalls) * 31 + this.includeSuperCalls) * 31 + this.substitution.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.asm.MemberSubstitution$Replacement$Factory {
                private final ElementMatcher<? super FieldDescription> fieldMatcher;
                private final ElementMatcher<? super MethodDescription> methodMatcher;
                private final boolean matchFieldRead;
                private final boolean matchFieldWrite;
                private final boolean includeVirtualCalls;
                private final boolean includeSuperCalls;
                private final Substitution.Factory substitutionFactory;

                protected Factory(ElementMatcher<? super FieldDescription> fieldMatcher, ElementMatcher<? super MethodDescription> methodMatcher, boolean matchFieldRead, boolean matchFieldWrite, boolean includeVirtualCalls, boolean includeSuperCalls, Substitution.Factory substitutionFactory) {
                    this.fieldMatcher = fieldMatcher;
                    this.methodMatcher = methodMatcher;
                    this.matchFieldRead = matchFieldRead;
                    this.matchFieldWrite = matchFieldWrite;
                    this.includeVirtualCalls = includeVirtualCalls;
                    this.includeSuperCalls = includeSuperCalls;
                    this.substitutionFactory = substitutionFactory;
                }

                protected static net.bytebuddy.asm.MemberSubstitution$Replacement$Factory of(ElementMatcher<? super ByteCodeElement.Member> matcher, Substitution.Factory factory) {
                    return new Factory(matcher, matcher, true, true, true, true, factory);
                }

                protected static net.bytebuddy.asm.MemberSubstitution$Replacement$Factory ofField(ElementMatcher<? super FieldDescription> matcher, boolean matchFieldRead, boolean matchFieldWrite, Substitution.Factory factory) {
                    return new Factory(matcher, ElementMatchers.none(), matchFieldRead, matchFieldWrite, false, false, factory);
                }

                protected static net.bytebuddy.asm.MemberSubstitution$Replacement$Factory ofMethod(ElementMatcher<? super MethodDescription> matcher, boolean includeVirtualCalls, boolean includeSuperCalls, Substitution.Factory factory) {
                    return new Factory(ElementMatchers.none(), matcher, false, false, includeVirtualCalls, includeSuperCalls, factory);
                }

                @Override
                public Replacement make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    return new ForElementMatchers(this.fieldMatcher, this.methodMatcher, this.matchFieldRead, this.matchFieldWrite, this.includeVirtualCalls, this.includeSuperCalls, this.substitutionFactory.make(instrumentedType, instrumentedMethod, typePool));
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
                    if (this.matchFieldRead != ((Factory)object).matchFieldRead) {
                        return false;
                    }
                    if (this.matchFieldWrite != ((Factory)object).matchFieldWrite) {
                        return false;
                    }
                    if (this.includeVirtualCalls != ((Factory)object).includeVirtualCalls) {
                        return false;
                    }
                    if (this.includeSuperCalls != ((Factory)object).includeSuperCalls) {
                        return false;
                    }
                    if (!this.fieldMatcher.equals(((Factory)object).fieldMatcher)) {
                        return false;
                    }
                    if (!this.methodMatcher.equals(((Factory)object).methodMatcher)) {
                        return false;
                    }
                    return this.substitutionFactory.equals(((Factory)object).substitutionFactory);
                }

                public int hashCode() {
                    return ((((((this.getClass().hashCode() * 31 + this.fieldMatcher.hashCode()) * 31 + this.methodMatcher.hashCode()) * 31 + this.matchFieldRead) * 31 + this.matchFieldWrite) * 31 + this.includeVirtualCalls) * 31 + this.includeSuperCalls) * 31 + this.substitutionFactory.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Replacement,
        Factory
        {
            INSTANCE;


            @Override
            public Replacement make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                return this;
            }

            @Override
            public Binding bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypeDescription typeDescription, FieldDescription fieldDescription, boolean writeAccess) {
                return Binding.Unresolved.INSTANCE;
            }

            @Override
            public Binding bind(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypeDescription typeDescription, MethodDescription methodDescription, InvocationType invocationType) {
                return Binding.Unresolved.INSTANCE;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum InvocationType {
            VIRTUAL,
            SUPER,
            OTHER;


            protected static InvocationType of(int opcode, MethodDescription methodDescription) {
                switch (opcode) {
                    case 182: 
                    case 185: {
                        return VIRTUAL;
                    }
                    case 183: {
                        return methodDescription.isVirtual() ? SUPER : OTHER;
                    }
                }
                return OTHER;
            }

            protected boolean matches(boolean includeVirtualCalls, boolean includeSuperCalls) {
                switch (this) {
                    case VIRTUAL: {
                        return includeVirtualCalls;
                    }
                    case SUPER: {
                        return includeSuperCalls;
                    }
                }
                return true;
            }
        }

        public static interface Factory {
            public Replacement make(TypeDescription var1, MethodDescription var2, TypePool var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Compound
            implements Factory {
                private final List<Factory> factories = new ArrayList<Factory>();

                protected Compound(Factory ... factory) {
                    this(Arrays.asList(factory));
                }

                protected Compound(List<? extends Factory> factories) {
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
                public Replacement make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    ArrayList<Replacement> replacements = new ArrayList<Replacement>();
                    for (Factory factory : this.factories) {
                        replacements.add(factory.make(instrumentedType, instrumentedMethod, typePool));
                    }
                    return new ForFirstBinding(replacements);
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

        public static interface Binding {
            public boolean isBound();

            public StackManipulation make(TypeList.Generic var1, TypeDescription.Generic var2, JavaConstant.MethodHandle var3, StackManipulation var4, int var5);

            @HashCodeAndEqualsPlugin.Enhance
            public static class Resolved
            implements Binding {
                private final TypeDescription receiver;
                private final ByteCodeElement.Member original;
                private final Substitution substitution;

                protected Resolved(TypeDescription receiver, ByteCodeElement.Member original, Substitution substitution) {
                    this.receiver = receiver;
                    this.original = original;
                    this.substitution = substitution;
                }

                public boolean isBound() {
                    return true;
                }

                public StackManipulation make(TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                    return this.substitution.resolve(this.receiver, this.original, parameters, result, methodHandle, stackManipulation, freeOffset);
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
                    if (!this.receiver.equals(((Resolved)object).receiver)) {
                        return false;
                    }
                    if (!this.original.equals(((Resolved)object).original)) {
                        return false;
                    }
                    return this.substitution.equals(((Resolved)object).substitution);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.receiver.hashCode()) * 31 + this.original.hashCode()) * 31 + this.substitution.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Unresolved implements Binding
            {
                INSTANCE;


                @Override
                public boolean isBound() {
                    return false;
                }

                @Override
                public StackManipulation make(TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                    throw new IllegalStateException("Cannot resolve unresolved binding");
                }
            }
        }
    }

    public static interface Substitution {
        public StackManipulation resolve(TypeDescription var1, ByteCodeElement.Member var2, TypeList.Generic var3, TypeDescription.Generic var4, JavaConstant.MethodHandle var5, StackManipulation var6, int var7);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Chain
        implements Substitution {
            private final Assigner assigner;
            private final Assigner.Typing typing;
            private final List<Step> steps;

            protected Chain(Assigner assigner, Assigner.Typing typing, List<Step> steps) {
                this.assigner = assigner;
                this.typing = typing;
                this.steps = steps;
            }

            public static Factory withDefaultAssigner() {
                return Chain.with(Assigner.DEFAULT, Assigner.Typing.STATIC);
            }

            public static Factory with(Assigner assigner, Assigner.Typing typing) {
                return new Factory(assigner, typing, Collections.<Step.Factory>emptyList());
            }

            @Override
            public StackManipulation resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(1 + parameters.size() + this.steps.size() * 2 + (result.represents(Void.TYPE) ? 0 : 2));
                HashMap<Integer, Integer> offsets = new HashMap<Integer, Integer>();
                for (int index = parameters.size() - 1; index >= 0; --index) {
                    stackManipulations.add(MethodVariableAccess.of((TypeDefinition)parameters.get(index)).storeAt(freeOffset));
                    offsets.put(index, freeOffset);
                    freeOffset += ((TypeDescription.Generic)parameters.get(index)).getStackSize().getSize();
                }
                stackManipulations.add(DefaultValue.of(result));
                TypeDescription.Generic current = result;
                for (Step step : this.steps) {
                    Step.Resolution resolution = step.resolve(receiver, original, parameters, result, methodHandle, stackManipulation, current, offsets, freeOffset);
                    stackManipulations.add(resolution.getStackManipulation());
                    current = resolution.getResultType();
                }
                StackManipulation assignment = this.assigner.assign(current, result, this.typing);
                if (!assignment.isValid()) {
                    throw new IllegalStateException("Failed to assign " + current + " to " + result);
                }
                stackManipulations.add(assignment);
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
                if (!this.typing.equals((Object)((Chain)object).typing)) {
                    return false;
                }
                if (!this.assigner.equals(((Chain)object).assigner)) {
                    return false;
                }
                return ((Object)this.steps).equals(((Chain)object).steps);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode()) * 31 + ((Object)this.steps).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Factory
            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Factory {
                private final Assigner assigner;
                private final Assigner.Typing typing;
                private final List<Step.Factory> steps;

                protected Factory(Assigner assigner, Assigner.Typing typing, List<Step.Factory> steps) {
                    this.assigner = assigner;
                    this.typing = typing;
                    this.steps = steps;
                }

                @Override
                public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    if (this.steps.isEmpty()) {
                        return Stubbing.INSTANCE;
                    }
                    ArrayList<Step> steps = new ArrayList<Step>(this.steps.size());
                    for (Step.Factory step : this.steps) {
                        steps.add(step.make(this.assigner, this.typing, instrumentedType, instrumentedMethod));
                    }
                    return new Chain(this.assigner, this.typing, steps);
                }

                public Factory executing(Step.Factory ... step) {
                    return this.executing(Arrays.asList(step));
                }

                public Factory executing(List<? extends Step.Factory> steps) {
                    return new Factory(this.assigner, this.typing, CompoundList.of(this.steps, steps));
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
                    if (!this.typing.equals((Object)((Factory)object).typing)) {
                        return false;
                    }
                    if (!this.assigner.equals(((Factory)object).assigner)) {
                        return false;
                    }
                    return ((Object)this.steps).equals(((Factory)object).steps);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode()) * 31 + ((Object)this.steps).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Step {
                public Resolution resolve(TypeDescription var1, ByteCodeElement.Member var2, TypeList.Generic var3, TypeDescription.Generic var4, JavaConstant.MethodHandle var5, StackManipulation var6, TypeDescription.Generic var7, Map<Integer, Integer> var8, int var9);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForDelegation
                implements Step {
                    private final TypeDescription.Generic returned;
                    private final Dispatcher.Resolved dispatcher;
                    private final List<OffsetMapping.Resolved> offsetMappings;

                    protected ForDelegation(TypeDescription.Generic returned, Dispatcher.Resolved dispatcher, List<OffsetMapping.Resolved> offsetMappings) {
                        this.returned = returned;
                        this.dispatcher = dispatcher;
                        this.offsetMappings = offsetMappings;
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(Method method) {
                        return ForDelegation.to(new MethodDescription.ForLoadedMethod(method));
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(Constructor<?> constructor) {
                        return ForDelegation.to(new MethodDescription.ForLoadedConstructor(constructor));
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(MethodDescription.InDefinedShape methodDescription) {
                        if (methodDescription.isTypeInitializer()) {
                            throw new IllegalArgumentException("Cannot delegate to a type initializer: " + methodDescription);
                        }
                        return ForDelegation.to(methodDescription, Dispatcher.ForRegularInvocation.Factory.INSTANCE, Collections.emptyList());
                    }

                    private static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(MethodDescription.InDefinedShape delegate, Dispatcher.Factory dispatcherFactory, List<? extends OffsetMapping.Factory<?>> userFactories) {
                        if (delegate.isTypeInitializer()) {
                            throw new IllegalArgumentException("Cannot delegate to type initializer: " + delegate);
                        }
                        return new Factory(delegate, dispatcherFactory.make(delegate), CompoundList.of(Arrays.asList(OffsetMapping.ForArgument.Factory.INSTANCE, OffsetMapping.ForThisReference.Factory.INSTANCE, OffsetMapping.ForAllArguments.Factory.INSTANCE, OffsetMapping.ForSelfCallHandle.Factory.INSTANCE, OffsetMapping.ForField.Unresolved.Factory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.GetterFactory.INSTANCE, OffsetMapping.ForFieldHandle.Unresolved.SetterFactory.INSTANCE, OffsetMapping.ForOrigin.Factory.INSTANCE, OffsetMapping.ForStubValue.Factory.INSTANCE, new OffsetMapping.ForStackManipulation.OfDefaultValue<Unused>(Unused.class), OffsetMapping.ForCurrent.Factory.INSTANCE), userFactories));
                    }

                    public static WithCustomMapping withCustomMapping() {
                        return new WithCustomMapping(Dispatcher.ForRegularInvocation.Factory.INSTANCE, Collections.<Class<Annotation>, OffsetMapping.Factory<?>>emptyMap());
                    }

                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(this.offsetMappings.size() + 3);
                        stackManipulations.add(current.represents(Void.TYPE) ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.of(current).storeAt(freeOffset));
                        stackManipulations.add(this.dispatcher.initialize());
                        for (OffsetMapping.Resolved offsetMapping : this.offsetMappings) {
                            stackManipulations.add(offsetMapping.apply(receiver, original, parameters, result, current, methodHandle, offsets, freeOffset));
                        }
                        stackManipulations.add(this.dispatcher.apply(receiver, original, methodHandle));
                        return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), this.returned);
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
                        if (!this.returned.equals(((ForDelegation)object).returned)) {
                            return false;
                        }
                        if (!this.dispatcher.equals(((ForDelegation)object).dispatcher)) {
                            return false;
                        }
                        return ((Object)this.offsetMappings).equals(((ForDelegation)object).offsetMappings);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.returned.hashCode()) * 31 + this.dispatcher.hashCode()) * 31 + ((Object)this.offsetMappings).hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static class WithCustomMapping {
                        private final Dispatcher.Factory dispatcherFactory;
                        private final Map<Class<? extends Annotation>, OffsetMapping.Factory<?>> offsetMappings;

                        protected WithCustomMapping(Dispatcher.Factory dispatcherFactory, Map<Class<? extends Annotation>, OffsetMapping.Factory<?>> offsetMappings) {
                            this.dispatcherFactory = dispatcherFactory;
                            this.offsetMappings = offsetMappings;
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, @MaybeNull Object value) {
                            return this.bind(OffsetMapping.ForStackManipulation.of(type, value));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Field field) {
                            return this.bind(type, new FieldDescription.ForLoadedField(field));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, FieldDescription fieldDescription) {
                            return this.bind(new OffsetMapping.ForField.Resolved.Factory<T>(type, fieldDescription));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Class<?> value) {
                            return this.bind(type, TypeDescription.ForLoadedType.of(value));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, TypeDescription value) {
                            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, ConstantValue.Simple.wrap(value)));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, Enum<?> value) {
                            return this.bind(type, new EnumerationDescription.ForLoadedEnumeration(value));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, EnumerationDescription value) {
                            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, ConstantValue.Simple.wrap(value)));
                        }

                        public <T extends Annotation> WithCustomMapping bindSerialized(Class<T> type, Serializable value) {
                            return this.bindSerialized(type, value, value.getClass());
                        }

                        public <T extends Annotation, S extends Serializable> WithCustomMapping bindSerialized(Class<T> type, S value, Class<? super S> targetType) {
                            return this.bind(OffsetMapping.ForStackManipulation.OfSerializedConstant.of(type, value, targetType));
                        }

                        public <T extends Annotation> WithCustomMapping bindProperty(Class<T> type, String property) {
                            return this.bind(OffsetMapping.ForStackManipulation.OfAnnotationProperty.of(type, property));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, ConstantValue constant) {
                            return this.bind(new OffsetMapping.ForStackManipulation.Factory<T>(type, constant.toStackManipulation(), constant.getTypeDescription().asGenericType()));
                        }

                        public <T extends Annotation> WithCustomMapping bind(Class<T> type, StackManipulation stackManipulation, Type targetType) {
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
                            return new WithCustomMapping(this.dispatcherFactory, offsetMappings);
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

                        public WithCustomMapping bootstrap(Method method, BootstrapArgumentResolver.Factory resolverFactory) {
                            return this.bootstrap(new MethodDescription.ForLoadedMethod(method), resolverFactory);
                        }

                        public WithCustomMapping bootstrap(MethodDescription.InDefinedShape bootstrap) {
                            return this.bootstrap(bootstrap, (BootstrapArgumentResolver.Factory)BootstrapArgumentResolver.ForDefaultValues.Factory.INSTANCE);
                        }

                        public WithCustomMapping bootstrap(MethodDescription.InDefinedShape bootstrap, BootstrapArgumentResolver.Factory resolverFactory) {
                            return new WithCustomMapping(Dispatcher.ForDynamicInvocation.of(bootstrap, resolverFactory), this.offsetMappings);
                        }

                        public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(Method method) {
                            return this.to(new MethodDescription.ForLoadedMethod(method));
                        }

                        public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(Constructor<?> constructor) {
                            return this.to(new MethodDescription.ForLoadedConstructor(constructor));
                        }

                        public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory to(MethodDescription.InDefinedShape methodDescription) {
                            return ForDelegation.to(methodDescription, this.dispatcherFactory, new ArrayList(this.offsetMappings.values()));
                        }
                    }

                    public static interface BootstrapArgumentResolver {
                        public Resolved resolve(TypeDescription var1, MethodDescription var2);

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForDefaultValues
                        implements BootstrapArgumentResolver {
                            private final MethodDescription.InDefinedShape delegate;

                            protected ForDefaultValues(MethodDescription.InDefinedShape delegate) {
                                this.delegate = delegate;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$BootstrapArgumentResolver$Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.delegate, instrumentedType, instrumentedMethod);
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
                                return this.delegate.equals(((ForDefaultValues)object).delegate);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.delegate.hashCode();
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            public static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$BootstrapArgumentResolver$Factory
                            {
                                INSTANCE;


                                @Override
                                public BootstrapArgumentResolver make(MethodDescription.InDefinedShape delegate) {
                                    return new ForDefaultValues(delegate);
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$BootstrapArgumentResolver$Resolved {
                                private final MethodDescription.InDefinedShape delegate;
                                private final TypeDescription instrumentedType;
                                private final MethodDescription instrumentedMethod;

                                protected Resolved(MethodDescription.InDefinedShape delegate, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                    this.delegate = delegate;
                                    this.instrumentedType = instrumentedType;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public List<JavaConstant> make(TypeDescription receiver, ByteCodeElement.Member original, JavaConstant.MethodHandle methodHandle) {
                                    if (this.instrumentedMethod.isTypeInitializer()) {
                                        return Arrays.asList(JavaConstant.Simple.ofLoaded(this.delegate.getDeclaringType().getName()), JavaConstant.Simple.of(receiver), JavaConstant.Simple.ofLoaded(original.getInternalName()), methodHandle, JavaConstant.Simple.of(this.instrumentedType), JavaConstant.Simple.ofLoaded(this.instrumentedMethod.getInternalName()));
                                    }
                                    return Arrays.asList(JavaConstant.Simple.ofLoaded(this.delegate.getDeclaringType().getName()), JavaConstant.Simple.of(receiver), JavaConstant.Simple.ofLoaded(original.getInternalName()), methodHandle, JavaConstant.Simple.of(this.instrumentedType), JavaConstant.Simple.ofLoaded(this.instrumentedMethod.getInternalName()), JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)this.instrumentedMethod.asDefined()));
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
                                    if (!this.delegate.equals(((Resolved)object).delegate)) {
                                        return false;
                                    }
                                    if (!this.instrumentedType.equals(((Resolved)object).instrumentedType)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return ((this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.instrumentedType.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }
                        }

                        public static interface Factory {
                            public BootstrapArgumentResolver make(MethodDescription.InDefinedShape var1);
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        public static interface Resolved {
                            public List<JavaConstant> make(TypeDescription var1, ByteCodeElement.Member var2, JavaConstant.MethodHandle var3);
                        }
                    }

                    protected static interface Dispatcher {
                        public Resolved resolve(TypeDescription var1, MethodDescription var2);

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForDynamicInvocation
                        implements Dispatcher {
                            private final MethodDescription.InDefinedShape bootstrapMethod;
                            private final MethodDescription.InDefinedShape delegate;
                            private final BootstrapArgumentResolver resolver;

                            protected ForDynamicInvocation(MethodDescription.InDefinedShape bootstrapMethod, MethodDescription.InDefinedShape delegate, BootstrapArgumentResolver resolver) {
                                this.bootstrapMethod = bootstrapMethod;
                                this.delegate = delegate;
                                this.resolver = resolver;
                            }

                            protected static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$Dispatcher$Factory of(MethodDescription.InDefinedShape bootstrapMethod, BootstrapArgumentResolver.Factory resolverFactory) {
                                if (!bootstrapMethod.isInvokeBootstrap()) {
                                    throw new IllegalStateException("Not a bootstrap method: " + bootstrapMethod);
                                }
                                return new Factory(bootstrapMethod, resolverFactory);
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$Dispatcher$Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.bootstrapMethod, this.delegate, this.resolver.resolve(instrumentedType, instrumentedMethod));
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
                                if (!this.delegate.equals(((ForDynamicInvocation)object).delegate)) {
                                    return false;
                                }
                                return this.resolver.equals(((ForDynamicInvocation)object).resolver);
                            }

                            public int hashCode() {
                                return ((this.getClass().hashCode() * 31 + this.bootstrapMethod.hashCode()) * 31 + this.delegate.hashCode()) * 31 + this.resolver.hashCode();
                            }

                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Factory
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$Dispatcher$Factory {
                                private final MethodDescription.InDefinedShape bootstrapMethod;
                                private final BootstrapArgumentResolver.Factory resolverFactory;

                                protected Factory(MethodDescription.InDefinedShape bootstrapMethod, BootstrapArgumentResolver.Factory resolverFactory) {
                                    this.bootstrapMethod = bootstrapMethod;
                                    this.resolverFactory = resolverFactory;
                                }

                                public Dispatcher make(MethodDescription.InDefinedShape delegate) {
                                    return new ForDynamicInvocation(this.bootstrapMethod, delegate, this.resolverFactory.make(delegate));
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

                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$Dispatcher$Resolved {
                                private final MethodDescription.InDefinedShape bootstrapMethod;
                                private final MethodDescription.InDefinedShape delegate;
                                private final BootstrapArgumentResolver.Resolved resolver;

                                protected Resolved(MethodDescription.InDefinedShape bootstrapMethod, MethodDescription.InDefinedShape delegate, BootstrapArgumentResolver.Resolved resolver) {
                                    this.bootstrapMethod = bootstrapMethod;
                                    this.delegate = delegate;
                                    this.resolver = resolver;
                                }

                                public StackManipulation initialize() {
                                    return StackManipulation.Trivial.INSTANCE;
                                }

                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, JavaConstant.MethodHandle methodHandle) {
                                    List<JavaConstant> constants = this.resolver.make(receiver, original, methodHandle);
                                    if (!this.bootstrapMethod.isInvokeBootstrap(TypeList.Explicit.of(constants))) {
                                        throw new IllegalArgumentException(this.bootstrapMethod + " is not accepting advice bootstrap arguments: " + constants);
                                    }
                                    return MethodInvocation.invoke(this.bootstrapMethod).dynamic(this.delegate.getInternalName(), this.delegate.getReturnType().asErasure(), this.delegate.getParameters().asTypeList().asErasures(), constants);
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
                                    if (!this.bootstrapMethod.equals(((Resolved)object).bootstrapMethod)) {
                                        return false;
                                    }
                                    if (!this.delegate.equals(((Resolved)object).delegate)) {
                                        return false;
                                    }
                                    return this.resolver.equals(((Resolved)object).resolver);
                                }

                                public int hashCode() {
                                    return ((this.getClass().hashCode() * 31 + this.bootstrapMethod.hashCode()) * 31 + this.delegate.hashCode()) * 31 + this.resolver.hashCode();
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForRegularInvocation
                        implements Dispatcher,
                        Resolved {
                            private final MethodDescription delegate;

                            protected ForRegularInvocation(MethodDescription delegate) {
                                this.delegate = delegate;
                            }

                            public Resolved resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return this;
                            }

                            public StackManipulation initialize() {
                                return this.delegate.isConstructor() ? new StackManipulation.Compound(TypeCreation.of(this.delegate.getDeclaringType().asErasure()), Duplication.SINGLE) : StackManipulation.Trivial.INSTANCE;
                            }

                            public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, JavaConstant.MethodHandle methodHandle) {
                                return MethodInvocation.invoke(this.delegate);
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
                                return this.delegate.equals(((ForRegularInvocation)object).delegate);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.delegate.hashCode();
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$Dispatcher$Factory
                            {
                                INSTANCE;


                                @Override
                                public Dispatcher make(MethodDescription.InDefinedShape delegate) {
                                    return new ForRegularInvocation(delegate);
                                }
                            }
                        }

                        public static interface Factory {
                            public Dispatcher make(MethodDescription.InDefinedShape var1);
                        }

                        public static interface Resolved {
                            public StackManipulation initialize();

                            public StackManipulation apply(TypeDescription var1, ByteCodeElement.Member var2, JavaConstant.MethodHandle var3);
                        }
                    }

                    public static interface OffsetMapping {
                        public Resolved resolve(Assigner var1, Assigner.Typing var2, TypeDescription var3, MethodDescription var4);

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForCurrent
                        implements OffsetMapping {
                            private final TypeDescription.Generic targetType;
                            @MaybeNull
                            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                            private final Assigner.Typing typing;

                            public ForCurrent(TypeDescription.Generic targetType, @MaybeNull Assigner.Typing typing) {
                                this.targetType = targetType;
                                this.typing = typing;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.targetType, assigner, this.typing == null ? typing : this.typing);
                            }

                            public boolean equals(@MaybeNull Object object) {
                                block10: {
                                    block9: {
                                        Assigner.Typing typing;
                                        block8: {
                                            Assigner.Typing typing2;
                                            if (this == object) {
                                                return true;
                                            }
                                            if (object == null) {
                                                return false;
                                            }
                                            if (this.getClass() != object.getClass()) {
                                                return false;
                                            }
                                            Assigner.Typing typing3 = ((ForCurrent)object).typing;
                                            typing = typing2 = this.typing;
                                            if (typing3 == null) break block8;
                                            if (typing == null) break block9;
                                            if (!typing2.equals((Object)typing3)) {
                                                return false;
                                            }
                                            break block10;
                                        }
                                        if (typing == null) break block10;
                                    }
                                    return false;
                                }
                                return this.targetType.equals(((ForCurrent)object).targetType);
                            }

                            public int hashCode() {
                                int n = (this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31;
                                Assigner.Typing typing = this.typing;
                                if (typing != null) {
                                    n = n + typing.hashCode();
                                }
                                return n;
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final TypeDescription.Generic targetType;
                                private final Assigner assigner;
                                private final Assigner.Typing typing;

                                public Resolved(TypeDescription.Generic targetType, Assigner assigner, Assigner.Typing typing) {
                                    this.targetType = targetType;
                                    this.assigner = assigner;
                                    this.typing = typing;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    StackManipulation assignment = this.assigner.assign(current, this.targetType, this.typing);
                                    if (!assignment.isValid()) {
                                        throw new IllegalStateException("Cannot assign " + current + " to " + this.targetType);
                                    }
                                    return new StackManipulation.Compound(MethodVariableAccess.of(current).loadFrom(offset), assignment);
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
                                    if (!this.typing.equals((Object)((Resolved)object).typing)) {
                                        return false;
                                    }
                                    if (!this.targetType.equals(((Resolved)object).targetType)) {
                                        return false;
                                    }
                                    return this.assigner.equals(((Resolved)object).assigner);
                                }

                                public int hashCode() {
                                    return ((this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<Current>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape CURRENT_TYPING;

                                @Override
                                public Class<Current> getAnnotationType() {
                                    return Current.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<Current> annotation) {
                                    return new ForCurrent(target.getDeclaringType().asGenericType(), annotation.getValue(CURRENT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class));
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Current> annotation) {
                                    return new ForCurrent(target.getType(), annotation.getValue(CURRENT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class));
                                }

                                static {
                                    CURRENT_TYPING = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Current.class).getDeclaredMethods().filter(ElementMatchers.named("typing"))).getOnly();
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForStubValue
                        implements OffsetMapping {
                            private final Source source;

                            protected ForStubValue(Source source) {
                                this.source = source;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.source, instrumentedMethod);
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
                                return this.source.equals((Object)((ForStubValue)object).source);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.source.hashCode();
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<StubValue>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape STUB_VALUE_SOURCE;

                                @Override
                                public Class<StubValue> getAnnotationType() {
                                    return StubValue.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<StubValue> annotation) {
                                    throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<StubValue> annotation) {
                                    if (!target.getType().represents((Type)((Object)Object.class))) {
                                        throw new IllegalStateException("Expected " + target + " to declare an Object type");
                                    }
                                    return new ForStubValue(annotation.getValue(STUB_VALUE_SOURCE).resolve(EnumerationDescription.class).load(Source.class));
                                }

                                static {
                                    STUB_VALUE_SOURCE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(StubValue.class).getDeclaredMethods().filter(ElementMatchers.named("source"))).getOnly();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final Source source;
                                private final MethodDescription instrumentedMethod;

                                protected Resolved(Source source, MethodDescription instrumentedMethod) {
                                    this.source = source;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    return DefaultValue.of(this.source.handle(methodHandle, this.instrumentedMethod).getReturnType());
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
                                    if (!this.source.equals((Object)((Resolved)object).source)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return (this.getClass().hashCode() * 31 + this.source.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForOrigin
                        implements OffsetMapping {
                            private final Sort sort;
                            private final Source source;

                            protected ForOrigin(Sort sort, Source source) {
                                this.sort = sort;
                                this.source = source;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.sort, this.source, instrumentedMethod);
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
                                if (!this.sort.equals((Object)((ForOrigin)object).sort)) {
                                    return false;
                                }
                                return this.source.equals((Object)((ForOrigin)object).source);
                            }

                            public int hashCode() {
                                return (this.getClass().hashCode() * 31 + this.sort.hashCode()) * 31 + this.source.hashCode();
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final Sort sort;
                                private final Source source;
                                private final MethodDescription instrumentedMethod;

                                protected Resolved(Sort sort, Source source, MethodDescription instrumentedMethod) {
                                    this.sort = sort;
                                    this.source = source;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    if (!this.source.isRepresentable(this.sort, original, this.instrumentedMethod)) {
                                        throw new IllegalStateException("Cannot represent " + (Object)((Object)this.sort) + " for " + (Object)((Object)this.source) + " in " + this.instrumentedMethod);
                                    }
                                    return this.source.resolve(this.sort, original, parameters, result, this.instrumentedMethod);
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
                                    if (!this.sort.equals((Object)((Resolved)object).sort)) {
                                        return false;
                                    }
                                    if (!this.source.equals((Object)((Resolved)object).source)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return ((this.getClass().hashCode() * 31 + this.sort.hashCode()) * 31 + this.source.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<Origin>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape ORIGIN_TYPE;

                                @Override
                                public Class<Origin> getAnnotationType() {
                                    return Origin.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<Origin> annotation) {
                                    throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Origin> annotation) {
                                    Sort sort;
                                    if (target.getType().asErasure().represents((Type)((Object)Class.class))) {
                                        sort = Sort.TYPE;
                                    } else if (target.getType().asErasure().represents((Type)((Object)Method.class))) {
                                        sort = Sort.METHOD;
                                    } else if (target.getType().asErasure().represents((Type)((Object)Constructor.class))) {
                                        sort = Sort.CONSTRUCTOR;
                                    } else if (target.getType().asErasure().represents((Type)((Object)Field.class))) {
                                        sort = Sort.FIELD;
                                    } else if (JavaType.EXECUTABLE.getTypeStub().equals(target.getType().asErasure())) {
                                        sort = Sort.EXECUTABLE;
                                    } else if (JavaType.METHOD_HANDLE.getTypeStub().equals(target.getType().asErasure())) {
                                        sort = Sort.METHOD_HANDLE;
                                    } else if (JavaType.METHOD_TYPE.getTypeStub().equals(target.getType().asErasure())) {
                                        sort = Sort.METHOD_TYPE;
                                    } else if (JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().equals(target.getType().asErasure())) {
                                        sort = Sort.LOOKUP;
                                    } else if (target.getType().asErasure().isAssignableFrom(String.class)) {
                                        sort = Sort.STRING;
                                    } else {
                                        throw new IllegalStateException("Non-supported type " + target.getType() + " for @Origin annotation");
                                    }
                                    return new ForOrigin(sort, annotation.getValue(ORIGIN_TYPE).resolve(EnumerationDescription.class).load(Source.class));
                                }

                                static {
                                    ORIGIN_TYPE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Origin.class).getDeclaredMethods().filter(ElementMatchers.named("source"))).getOnly();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Sort {
                                METHOD{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return original instanceof MethodDescription && ((MethodDescription)original).isMethod();
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return MethodConstant.of((MethodDescription.InDefinedShape)((MethodDescription)original).asDefined());
                                    }
                                }
                                ,
                                CONSTRUCTOR{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return original instanceof MethodDescription && ((MethodDescription)original).isConstructor();
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return MethodConstant.of((MethodDescription.InDefinedShape)((MethodDescription)original).asDefined());
                                    }
                                }
                                ,
                                FIELD{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return original instanceof FieldDescription;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return new FieldConstant((FieldDescription.InDefinedShape)((FieldDescription)original).asDefined());
                                    }
                                }
                                ,
                                EXECUTABLE{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return original instanceof MethodDescription;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return MethodConstant.of((MethodDescription.InDefinedShape)((MethodDescription)original).asDefined());
                                    }
                                }
                                ,
                                TYPE{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return true;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return ClassConstant.of(original.getDeclaringType().asErasure());
                                    }
                                }
                                ,
                                LOOKUP{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return true;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return MethodInvocation.lookup();
                                    }
                                }
                                ,
                                METHOD_HANDLE{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return true;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        JavaConstant.MethodHandle handle;
                                        if (original instanceof MethodDescription) {
                                            handle = JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)((MethodDescription)original).asDefined());
                                        } else if (original instanceof FieldDescription) {
                                            handle = returnType.represents(Void.TYPE) ? JavaConstant.MethodHandle.ofSetter((FieldDescription.InDefinedShape)((FieldDescription)original).asDefined()) : JavaConstant.MethodHandle.ofGetter((FieldDescription.InDefinedShape)((FieldDescription)original).asDefined());
                                        } else {
                                            throw new IllegalStateException("Unexpected byte code element: " + original);
                                        }
                                        return handle.toStackManipulation();
                                    }
                                }
                                ,
                                METHOD_TYPE{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return true;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return JavaConstant.MethodType.of(returnType, parameterTypes).toStackManipulation();
                                    }
                                }
                                ,
                                STRING{

                                    @Override
                                    protected boolean isRepresentable(ByteCodeElement.Member original) {
                                        return true;
                                    }

                                    @Override
                                    protected StackManipulation resolve(ByteCodeElement.Member original, List<TypeDescription> parameterTypes, TypeDescription returnType) {
                                        return new TextConstant(original.toString());
                                    }
                                };


                                protected abstract boolean isRepresentable(ByteCodeElement.Member var1);

                                protected abstract StackManipulation resolve(ByteCodeElement.Member var1, List<TypeDescription> var2, TypeDescription var3);
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static abstract class ForFieldHandle
                        implements OffsetMapping {
                            private final Access access;

                            protected ForFieldHandle(Access access) {
                                this.access = access;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                FieldDescription fieldDescription = this.resolve(instrumentedType, instrumentedMethod);
                                if (!fieldDescription.isStatic() && instrumentedMethod.isStatic()) {
                                    throw new IllegalStateException("Cannot access non-static field " + fieldDescription + " from static method " + instrumentedMethod);
                                }
                                if (fieldDescription.isStatic()) {
                                    return new Resolved.ForStackManipulation(this.access.resolve((FieldDescription.InDefinedShape)fieldDescription.asDefined()).toStackManipulation());
                                }
                                return new Resolved.ForStackManipulation(new StackManipulation.Compound(this.access.resolve((FieldDescription.InDefinedShape)fieldDescription.asDefined()).toStackManipulation(), MethodVariableAccess.REFERENCE.loadFrom(0), MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLE.getTypeStub(), new MethodDescription.Token("bindTo", 1, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(TypeDefinition.Sort.describe(Object.class)))))));
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
                                implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<T> {
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
                                    public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation) {
                                        throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                    }

                                    @Override
                                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation) {
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
                                protected static enum SetterFactory implements Factory<FieldSetterHandle>
                                {
                                    INSTANCE;

                                    private static final MethodDescription.InDefinedShape FIELD_SETTER_HANDLE_VALUE;
                                    private static final MethodDescription.InDefinedShape FIELD_SETTER_HANDLE_DECLARING_TYPE;

                                    @Override
                                    public Class<FieldSetterHandle> getAnnotationType() {
                                        return FieldSetterHandle.class;
                                    }

                                    @Override
                                    public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldSetterHandle> annotation) {
                                        throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                    }

                                    @Override
                                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldSetterHandle> annotation) {
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
                                protected static enum GetterFactory implements Factory<FieldGetterHandle>
                                {
                                    INSTANCE;

                                    private static final MethodDescription.InDefinedShape FIELD_GETTER_HANDLE_VALUE;
                                    private static final MethodDescription.InDefinedShape FIELD_GETTER_HANDLE_DECLARING_TYPE;

                                    @Override
                                    public Class<FieldGetterHandle> getAnnotationType() {
                                        return FieldGetterHandle.class;
                                    }

                                    @Override
                                    public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldGetterHandle> annotation) {
                                        throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                    }

                                    @Override
                                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldGetterHandle> annotation) {
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
                                        if (!this.declaringType.represents((Type)((Object)TargetType.class)) && !instrumentedType.isAssignableTo(this.declaringType)) {
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
                            private static final MethodDescription.InDefinedShape FIELD_VALUE_VALUE;
                            private static final MethodDescription.InDefinedShape FIELD_VALUE_DECLARING_TYPE;
                            private static final MethodDescription.InDefinedShape FIELD_VALUE_TYPING;
                            private final TypeDescription.Generic target;
                            @MaybeNull
                            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                            private final Assigner.Typing typing;

                            protected ForField(TypeDescription.Generic target, @MaybeNull Assigner.Typing typing) {
                                this.target = target;
                                this.typing = typing;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                FieldDescription fieldDescription = this.resolve(instrumentedType, instrumentedMethod);
                                if (!fieldDescription.isStatic() && instrumentedMethod.isStatic()) {
                                    throw new IllegalStateException("Cannot access non-static field " + fieldDescription + " from static method " + instrumentedMethod);
                                }
                                StackManipulation assignment = assigner.assign(fieldDescription.getType(), this.target, this.typing == null ? typing : this.typing);
                                if (!assignment.isValid()) {
                                    throw new IllegalStateException("Cannot assign " + fieldDescription + " to " + this.target);
                                }
                                return new Resolved.ForStackManipulation(new StackManipulation.Compound(fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(fieldDescription).read(), assignment));
                            }

                            protected abstract FieldDescription resolve(TypeDescription var1, MethodDescription var2);

                            static {
                                MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(FieldValue.class).getDeclaredMethods();
                                FIELD_VALUE_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                                FIELD_VALUE_DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("declaringType"))).getOnly();
                                FIELD_VALUE_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                            }

                            public boolean equals(@MaybeNull Object object) {
                                block10: {
                                    block9: {
                                        Assigner.Typing typing;
                                        block8: {
                                            Assigner.Typing typing2;
                                            if (this == object) {
                                                return true;
                                            }
                                            if (object == null) {
                                                return false;
                                            }
                                            if (this.getClass() != object.getClass()) {
                                                return false;
                                            }
                                            Assigner.Typing typing3 = ((ForField)object).typing;
                                            typing = typing2 = this.typing;
                                            if (typing3 == null) break block8;
                                            if (typing == null) break block9;
                                            if (!typing2.equals((Object)typing3)) {
                                                return false;
                                            }
                                            break block10;
                                        }
                                        if (typing == null) break block10;
                                    }
                                    return false;
                                }
                                return this.target.equals(((ForField)object).target);
                            }

                            public int hashCode() {
                                int n = (this.getClass().hashCode() * 31 + this.target.hashCode()) * 31;
                                Assigner.Typing typing = this.typing;
                                if (typing != null) {
                                    n = n + typing.hashCode();
                                }
                                return n;
                            }

                            @HashCodeAndEqualsPlugin.Enhance
                            public static class Resolved
                            extends ForField {
                                private final FieldDescription fieldDescription;

                                public Resolved(TypeDescription.Generic target, Assigner.Typing typing, FieldDescription fieldDescription) {
                                    super(target, typing);
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
                                extends Factory.AbstractBase<T> {
                                    private final Class<T> annotationType;
                                    private final FieldDescription fieldDescription;
                                    private final Assigner.Typing typing;

                                    public Factory(Class<T> annotationType, FieldDescription fieldDescription) {
                                        this(annotationType, fieldDescription, Assigner.Typing.STATIC);
                                    }

                                    public Factory(Class<T> annotationType, FieldDescription fieldDescription, Assigner.Typing typing) {
                                        this.annotationType = annotationType;
                                        this.fieldDescription = fieldDescription;
                                        this.typing = typing;
                                    }

                                    @Override
                                    public Class<T> getAnnotationType() {
                                        return this.annotationType;
                                    }

                                    @Override
                                    protected OffsetMapping make(TypeDescription.Generic target, AnnotationDescription.Loadable<T> annotation) {
                                        return new Resolved(target, this.typing, this.fieldDescription);
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
                                        if (!this.typing.equals((Object)((Factory)object).typing)) {
                                            return false;
                                        }
                                        if (!this.annotationType.equals(((Factory)object).annotationType)) {
                                            return false;
                                        }
                                        return this.fieldDescription.equals(((Factory)object).fieldDescription);
                                    }

                                    public int hashCode() {
                                        return ((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.fieldDescription.hashCode()) * 31 + this.typing.hashCode();
                                    }
                                }
                            }

                            @HashCodeAndEqualsPlugin.Enhance
                            public static abstract class Unresolved
                            extends ForField {
                                protected static final String BEAN_PROPERTY = "";
                                private final String name;

                                protected Unresolved(TypeDescription.Generic target, Assigner.Typing typing, String name) {
                                    super(target, typing);
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
                                protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<FieldValue>
                                {
                                    INSTANCE;


                                    @Override
                                    public Class<FieldValue> getAnnotationType() {
                                        return FieldValue.class;
                                    }

                                    @Override
                                    public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldValue> annotation) {
                                        TypeDescription declaringType = annotation.getValue(FIELD_VALUE_DECLARING_TYPE).resolve(TypeDescription.class);
                                        return declaringType.represents(Void.TYPE) ? new WithImplicitType(target.getDeclaringType().asGenericType(), annotation) : new WithExplicitType(target.getDeclaringType().asGenericType(), annotation, declaringType);
                                    }

                                    @Override
                                    public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<FieldValue> annotation) {
                                        TypeDescription declaringType = annotation.getValue(FIELD_VALUE_DECLARING_TYPE).resolve(TypeDescription.class);
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
                                        this(target, annotation.getValue(FIELD_VALUE_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(FIELD_VALUE_VALUE).resolve(String.class), declaringType);
                                    }

                                    public WithExplicitType(TypeDescription.Generic target, Assigner.Typing typing, String name, TypeDescription declaringType) {
                                        super(target, typing, name);
                                        this.declaringType = declaringType;
                                    }

                                    @Override
                                    protected FieldLocator fieldLocator(TypeDescription instrumentedType) {
                                        if (!this.declaringType.represents((Type)((Object)TargetType.class)) && !instrumentedType.isAssignableTo(this.declaringType)) {
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
                                        this(target, annotation.getValue(FIELD_VALUE_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(FIELD_VALUE_VALUE).resolve(String.class));
                                    }

                                    public WithImplicitType(TypeDescription.Generic target, Assigner.Typing typing, String name) {
                                        super(target, typing, name);
                                    }

                                    @Override
                                    protected FieldLocator fieldLocator(TypeDescription instrumentedType) {
                                        return new FieldLocator.ForClassHierarchy(instrumentedType);
                                    }
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForSelfCallHandle
                        implements OffsetMapping {
                            private final Source source;
                            private final boolean bound;

                            public ForSelfCallHandle(Source source, boolean bound) {
                                this.source = source;
                                this.bound = bound;
                            }

                            public Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return this.bound ? new Bound(this.source, instrumentedMethod) : new Unbound(this.source, instrumentedMethod);
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
                                if (this.bound != ((ForSelfCallHandle)object).bound) {
                                    return false;
                                }
                                return this.source.equals((Object)((ForSelfCallHandle)object).source);
                            }

                            public int hashCode() {
                                return (this.getClass().hashCode() * 31 + this.source.hashCode()) * 31 + this.bound;
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Unbound
                            implements Resolved {
                                private final Source source;
                                private final MethodDescription instrumentedMethod;

                                protected Unbound(Source source, MethodDescription instrumentedMethod) {
                                    this.source = source;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    return this.source.handle(methodHandle, this.instrumentedMethod).toStackManipulation();
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
                                    if (!this.source.equals((Object)((Unbound)object).source)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Unbound)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return (this.getClass().hashCode() * 31 + this.source.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Bound
                            implements Resolved {
                                private final Source source;
                                private final MethodDescription instrumentedMethod;

                                protected Bound(Source source, MethodDescription instrumentedMethod) {
                                    this.source = source;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    Source.Value dispatched = this.source.self(parameters, offsets, original, this.instrumentedMethod);
                                    List<Source.Value> values = this.source.arguments(false, parameters, offsets, original, this.instrumentedMethod);
                                    ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(1 + (values.size() + (dispatched == null ? 0 : 2)) + (values.isEmpty() ? 0 : 1));
                                    stackManipulations.add(this.source.handle(methodHandle, this.instrumentedMethod).toStackManipulation());
                                    if (dispatched != null) {
                                        stackManipulations.add(MethodVariableAccess.of(dispatched.getTypeDescription()).loadFrom(dispatched.getOffset()));
                                        stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLE.getTypeStub(), new MethodDescription.Token("bindTo", 1, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(TypeDefinition.Sort.describe(Object.class))))));
                                    }
                                    if (!values.isEmpty()) {
                                        for (Source.Value value : values) {
                                            stackManipulations.add(MethodVariableAccess.of(value.getTypeDescription()).loadFrom(value.getOffset()));
                                        }
                                        stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLES.getTypeStub(), new MethodDescription.Token("insertArguments", 9, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(JavaType.METHOD_HANDLE.getTypeStub(), TypeDefinition.Sort.describe(Integer.TYPE), TypeDefinition.Sort.describe(Object[].class))))));
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
                                    if (!this.source.equals((Object)((Bound)object).source)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Bound)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return (this.getClass().hashCode() * 31 + this.source.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<SelfCallHandle>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_SOURCE;
                                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_BOUND;

                                @Override
                                public Class<SelfCallHandle> getAnnotationType() {
                                    return SelfCallHandle.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<SelfCallHandle> annotation) {
                                    throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<SelfCallHandle> annotation) {
                                    if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                                        throw new IllegalStateException("Cannot assign method handle to " + target);
                                    }
                                    return new ForSelfCallHandle(annotation.getValue(ALL_ARGUMENTS_SOURCE).resolve(EnumerationDescription.class).load(Source.class), annotation.getValue(ALL_ARGUMENTS_BOUND).resolve(Boolean.class));
                                }

                                static {
                                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(SelfCallHandle.class).getDeclaredMethods();
                                    ALL_ARGUMENTS_SOURCE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("source"))).getOnly();
                                    ALL_ARGUMENTS_BOUND = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("bound"))).getOnly();
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForAllArguments
                        implements OffsetMapping {
                            private final TypeDescription.Generic targetComponentType;
                            @MaybeNull
                            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                            private final Assigner.Typing typing;
                            private final Source source;
                            private final boolean includeSelf;
                            private final boolean nullIfEmpty;

                            public ForAllArguments(TypeDescription.Generic targetComponentType, @MaybeNull Assigner.Typing typing, Source source, boolean includeSelf, boolean nullIfEmpty) {
                                this.targetComponentType = targetComponentType;
                                this.typing = typing;
                                this.source = source;
                                this.includeSelf = includeSelf;
                                this.nullIfEmpty = nullIfEmpty;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.targetComponentType, this.typing == null ? typing : this.typing, this.source, this.includeSelf, this.nullIfEmpty, assigner, instrumentedMethod);
                            }

                            public boolean equals(@MaybeNull Object object) {
                                block13: {
                                    block12: {
                                        Assigner.Typing typing;
                                        block11: {
                                            Assigner.Typing typing2;
                                            if (this == object) {
                                                return true;
                                            }
                                            if (object == null) {
                                                return false;
                                            }
                                            if (this.getClass() != object.getClass()) {
                                                return false;
                                            }
                                            if (this.includeSelf != ((ForAllArguments)object).includeSelf) {
                                                return false;
                                            }
                                            if (this.nullIfEmpty != ((ForAllArguments)object).nullIfEmpty) {
                                                return false;
                                            }
                                            Assigner.Typing typing3 = ((ForAllArguments)object).typing;
                                            typing = typing2 = this.typing;
                                            if (typing3 == null) break block11;
                                            if (typing == null) break block12;
                                            if (!typing2.equals((Object)typing3)) {
                                                return false;
                                            }
                                            break block13;
                                        }
                                        if (typing == null) break block13;
                                    }
                                    return false;
                                }
                                if (!this.source.equals((Object)((ForAllArguments)object).source)) {
                                    return false;
                                }
                                return this.targetComponentType.equals(((ForAllArguments)object).targetComponentType);
                            }

                            public int hashCode() {
                                int n = (this.getClass().hashCode() * 31 + this.targetComponentType.hashCode()) * 31;
                                Assigner.Typing typing = this.typing;
                                if (typing != null) {
                                    n = n + typing.hashCode();
                                }
                                return ((n * 31 + this.source.hashCode()) * 31 + this.includeSelf) * 31 + this.nullIfEmpty;
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final TypeDescription.Generic targetComponentType;
                                private final Assigner.Typing typing;
                                private final Source source;
                                private final boolean includeSelf;
                                private final boolean nullIfEmpty;
                                private final Assigner assigner;
                                private final MethodDescription instrumentedMethod;

                                protected Resolved(TypeDescription.Generic targetComponentType, Assigner.Typing typing, Source source, boolean includeSelf, boolean nullIfEmpty, Assigner assigner, MethodDescription instrumentedMethod) {
                                    this.targetComponentType = targetComponentType;
                                    this.typing = typing;
                                    this.source = source;
                                    this.includeSelf = includeSelf;
                                    this.nullIfEmpty = nullIfEmpty;
                                    this.assigner = assigner;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    List<Source.Value> values = this.source.arguments(this.includeSelf, parameters, offsets, original, this.instrumentedMethod);
                                    if (this.nullIfEmpty && values.isEmpty()) {
                                        return NullConstant.INSTANCE;
                                    }
                                    ArrayList<StackManipulation.Compound> stackManipulations = new ArrayList<StackManipulation.Compound>();
                                    for (Source.Value value : values) {
                                        StackManipulation assignment = this.assigner.assign(value.getTypeDescription(), this.targetComponentType, this.typing);
                                        if (!assignment.isValid()) {
                                            throw new IllegalStateException("Cannot assign " + value.getTypeDescription() + " to " + this.targetComponentType);
                                        }
                                        stackManipulations.add(new StackManipulation.Compound(MethodVariableAccess.of(value.getTypeDescription()).loadFrom(value.getOffset()), assignment));
                                    }
                                    return ArrayFactory.forType(this.targetComponentType).withValues(stackManipulations);
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
                                    if (this.includeSelf != ((Resolved)object).includeSelf) {
                                        return false;
                                    }
                                    if (this.nullIfEmpty != ((Resolved)object).nullIfEmpty) {
                                        return false;
                                    }
                                    if (!this.typing.equals((Object)((Resolved)object).typing)) {
                                        return false;
                                    }
                                    if (!this.source.equals((Object)((Resolved)object).source)) {
                                        return false;
                                    }
                                    if (!this.targetComponentType.equals(((Resolved)object).targetComponentType)) {
                                        return false;
                                    }
                                    if (!this.assigner.equals(((Resolved)object).assigner)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return ((((((this.getClass().hashCode() * 31 + this.targetComponentType.hashCode()) * 31 + this.typing.hashCode()) * 31 + this.source.hashCode()) * 31 + this.includeSelf) * 31 + this.nullIfEmpty) * 31 + this.assigner.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<AllArguments>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_TYPING;
                                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_SOURCE;
                                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_INCLUDE_SELF;
                                private static final MethodDescription.InDefinedShape ALL_ARGUMENTS_NULL_IF_EMPTY;

                                @Override
                                public Class<AllArguments> getAnnotationType() {
                                    return AllArguments.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<AllArguments> annotation) {
                                    throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                }

                                @Override
                                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<AllArguments> annotation) {
                                    if (!target.getType().isArray()) {
                                        throw new IllegalStateException("Expected array as parameter type for " + target);
                                    }
                                    return new ForAllArguments(target.getType().getComponentType(), annotation.getValue(ALL_ARGUMENTS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(ALL_ARGUMENTS_SOURCE).resolve(EnumerationDescription.class).load(Source.class), annotation.getValue(ALL_ARGUMENTS_INCLUDE_SELF).resolve(Boolean.class), annotation.getValue(ALL_ARGUMENTS_NULL_IF_EMPTY).resolve(Boolean.class));
                                }

                                static {
                                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(AllArguments.class).getDeclaredMethods();
                                    ALL_ARGUMENTS_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                                    ALL_ARGUMENTS_SOURCE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("source"))).getOnly();
                                    ALL_ARGUMENTS_INCLUDE_SELF = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("includeSelf"))).getOnly();
                                    ALL_ARGUMENTS_NULL_IF_EMPTY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfEmpty"))).getOnly();
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForThisReference
                        implements OffsetMapping {
                            private final TypeDescription.Generic targetType;
                            @MaybeNull
                            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                            private final Assigner.Typing typing;
                            private final Source source;
                            private final boolean optional;

                            public ForThisReference(TypeDescription.Generic targetType, @MaybeNull Assigner.Typing typing, Source source, boolean optional) {
                                this.targetType = targetType;
                                this.typing = typing;
                                this.source = source;
                                this.optional = optional;
                            }

                            public Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.targetType, this.typing == null ? typing : this.typing, this.source, this.optional, assigner, instrumentedMethod);
                            }

                            public boolean equals(@MaybeNull Object object) {
                                block12: {
                                    block11: {
                                        Assigner.Typing typing;
                                        block10: {
                                            Assigner.Typing typing2;
                                            if (this == object) {
                                                return true;
                                            }
                                            if (object == null) {
                                                return false;
                                            }
                                            if (this.getClass() != object.getClass()) {
                                                return false;
                                            }
                                            if (this.optional != ((ForThisReference)object).optional) {
                                                return false;
                                            }
                                            Assigner.Typing typing3 = ((ForThisReference)object).typing;
                                            typing = typing2 = this.typing;
                                            if (typing3 == null) break block10;
                                            if (typing == null) break block11;
                                            if (!typing2.equals((Object)typing3)) {
                                                return false;
                                            }
                                            break block12;
                                        }
                                        if (typing == null) break block12;
                                    }
                                    return false;
                                }
                                if (!this.source.equals((Object)((ForThisReference)object).source)) {
                                    return false;
                                }
                                return this.targetType.equals(((ForThisReference)object).targetType);
                            }

                            public int hashCode() {
                                int n = (this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31;
                                Assigner.Typing typing = this.typing;
                                if (typing != null) {
                                    n = n + typing.hashCode();
                                }
                                return (n * 31 + this.source.hashCode()) * 31 + this.optional;
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<This>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape THIS_TYPING;
                                private static final MethodDescription.InDefinedShape THIS_SOURCE;
                                private static final MethodDescription.InDefinedShape THIS_OPTIONAL;

                                @Override
                                public Class<This> getAnnotationType() {
                                    return This.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<This> annotation) {
                                    return new ForThisReference(target.getDeclaringType().asGenericType(), annotation.getValue(THIS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(THIS_SOURCE).resolve(EnumerationDescription.class).load(Source.class), annotation.getValue(THIS_OPTIONAL).resolve(Boolean.class));
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<This> annotation) {
                                    return new ForThisReference(target.getType(), annotation.getValue(THIS_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(THIS_SOURCE).resolve(EnumerationDescription.class).load(Source.class), annotation.getValue(THIS_OPTIONAL).resolve(Boolean.class));
                                }

                                static {
                                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(This.class).getDeclaredMethods();
                                    THIS_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                                    THIS_SOURCE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("source"))).getOnly();
                                    THIS_OPTIONAL = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("optional"))).getOnly();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final TypeDescription.Generic targetType;
                                private final Assigner.Typing typing;
                                private final Source source;
                                private final boolean optional;
                                private final Assigner assigner;
                                private final MethodDescription instrumentedMethod;

                                protected Resolved(TypeDescription.Generic targetType, Assigner.Typing typing, Source source, boolean optional, Assigner assigner, MethodDescription instrumentedMethod) {
                                    this.targetType = targetType;
                                    this.typing = typing;
                                    this.source = source;
                                    this.optional = optional;
                                    this.assigner = assigner;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    Source.Value value = this.source.self(parameters, offsets, original, this.instrumentedMethod);
                                    if (value != null) {
                                        StackManipulation assignment = this.assigner.assign(value.getTypeDescription(), this.targetType, this.typing);
                                        if (!assignment.isValid()) {
                                            throw new IllegalStateException("Cannot assign " + value.getTypeDescription() + " to " + this.targetType);
                                        }
                                        return new StackManipulation.Compound(MethodVariableAccess.of(value.getTypeDescription()).loadFrom(value.getOffset()), assignment);
                                    }
                                    if (this.optional) {
                                        return DefaultValue.of(this.targetType);
                                    }
                                    throw new IllegalStateException("No this reference available for " + original);
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
                                    if (this.optional != ((Resolved)object).optional) {
                                        return false;
                                    }
                                    if (!this.typing.equals((Object)((Resolved)object).typing)) {
                                        return false;
                                    }
                                    if (!this.source.equals((Object)((Resolved)object).source)) {
                                        return false;
                                    }
                                    if (!this.targetType.equals(((Resolved)object).targetType)) {
                                        return false;
                                    }
                                    if (!this.assigner.equals(((Resolved)object).assigner)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return (((((this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.typing.hashCode()) * 31 + this.source.hashCode()) * 31 + this.optional) * 31 + this.assigner.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForArgument
                        implements OffsetMapping {
                            private final TypeDescription.Generic targetType;
                            private final int index;
                            @MaybeNull
                            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                            private final Assigner.Typing typing;
                            private final Source source;
                            private final boolean optional;

                            public ForArgument(TypeDescription.Generic targetType, int index, @MaybeNull Assigner.Typing typing, Source source, boolean optional) {
                                this.targetType = targetType;
                                this.index = index;
                                this.typing = typing;
                                this.source = source;
                                this.optional = optional;
                            }

                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(this.targetType, this.index, this.typing == null ? typing : this.typing, this.source, this.optional, assigner, instrumentedMethod);
                            }

                            public boolean equals(@MaybeNull Object object) {
                                block13: {
                                    block12: {
                                        Assigner.Typing typing;
                                        block11: {
                                            Assigner.Typing typing2;
                                            if (this == object) {
                                                return true;
                                            }
                                            if (object == null) {
                                                return false;
                                            }
                                            if (this.getClass() != object.getClass()) {
                                                return false;
                                            }
                                            if (this.index != ((ForArgument)object).index) {
                                                return false;
                                            }
                                            if (this.optional != ((ForArgument)object).optional) {
                                                return false;
                                            }
                                            Assigner.Typing typing3 = ((ForArgument)object).typing;
                                            typing = typing2 = this.typing;
                                            if (typing3 == null) break block11;
                                            if (typing == null) break block12;
                                            if (!typing2.equals((Object)typing3)) {
                                                return false;
                                            }
                                            break block13;
                                        }
                                        if (typing == null) break block13;
                                    }
                                    return false;
                                }
                                if (!this.source.equals((Object)((ForArgument)object).source)) {
                                    return false;
                                }
                                return this.targetType.equals(((ForArgument)object).targetType);
                            }

                            public int hashCode() {
                                int n = ((this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.index) * 31;
                                Assigner.Typing typing = this.typing;
                                if (typing != null) {
                                    n = n + typing.hashCode();
                                }
                                return (n * 31 + this.source.hashCode()) * 31 + this.optional;
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final TypeDescription.Generic targetType;
                                private final int index;
                                private final Assigner.Typing typing;
                                private final Source source;
                                private final boolean optional;
                                private final Assigner assigner;
                                private final MethodDescription instrumentedMethod;

                                protected Resolved(TypeDescription.Generic targetType, int index, Assigner.Typing typing, Source source, boolean optional, Assigner assigner, MethodDescription instrumentedMethod) {
                                    this.targetType = targetType;
                                    this.index = index;
                                    this.typing = typing;
                                    this.source = source;
                                    this.optional = optional;
                                    this.assigner = assigner;
                                    this.instrumentedMethod = instrumentedMethod;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    Source.Value value = this.source.argument(this.index, parameters, offsets, original, this.instrumentedMethod);
                                    if (value != null) {
                                        StackManipulation assignment = this.assigner.assign(value.getTypeDescription(), this.targetType, this.typing);
                                        if (!assignment.isValid()) {
                                            throw new IllegalStateException("Cannot assign " + value.getTypeDescription() + " to " + this.targetType);
                                        }
                                        return new StackManipulation.Compound(MethodVariableAccess.of(value.getTypeDescription()).loadFrom(value.getOffset()), assignment);
                                    }
                                    if (this.optional) {
                                        return DefaultValue.of(this.targetType);
                                    }
                                    throw new IllegalStateException("No argument with index " + this.index + " available for " + original);
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
                                    if (this.index != ((Resolved)object).index) {
                                        return false;
                                    }
                                    if (this.optional != ((Resolved)object).optional) {
                                        return false;
                                    }
                                    if (!this.typing.equals((Object)((Resolved)object).typing)) {
                                        return false;
                                    }
                                    if (!this.source.equals((Object)((Resolved)object).source)) {
                                        return false;
                                    }
                                    if (!this.targetType.equals(((Resolved)object).targetType)) {
                                        return false;
                                    }
                                    if (!this.assigner.equals(((Resolved)object).assigner)) {
                                        return false;
                                    }
                                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                                }

                                public int hashCode() {
                                    return ((((((this.getClass().hashCode() * 31 + this.targetType.hashCode()) * 31 + this.index) * 31 + this.typing.hashCode()) * 31 + this.source.hashCode()) * 31 + this.optional) * 31 + this.assigner.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            protected static enum Factory implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<Argument>
                            {
                                INSTANCE;

                                private static final MethodDescription.InDefinedShape ARGUMENT_VALUE;
                                private static final MethodDescription.InDefinedShape ARGUMENT_TYPING;
                                private static final MethodDescription.InDefinedShape ARGUMENT_SOURCE;
                                private static final MethodDescription.InDefinedShape ARGUMENT_OPTIONAL;

                                @Override
                                public Class<Argument> getAnnotationType() {
                                    return Argument.class;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<Argument> annotation) {
                                    return new ForArgument(target.getDeclaringType().asGenericType(), annotation.getValue(ARGUMENT_VALUE).resolve(Integer.class), annotation.getValue(ARGUMENT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(ARGUMENT_SOURCE).resolve(EnumerationDescription.class).load(Source.class), annotation.getValue(ARGUMENT_OPTIONAL).resolve(Boolean.class));
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<Argument> annotation) {
                                    int index = annotation.getValue(ARGUMENT_VALUE).resolve(Integer.class);
                                    if (index < 0) {
                                        throw new IllegalStateException("Cannot assign negative parameter index " + index + " for " + target);
                                    }
                                    return new ForArgument(target.getType(), index, annotation.getValue(ARGUMENT_TYPING).resolve(EnumerationDescription.class).load(Assigner.Typing.class), annotation.getValue(ARGUMENT_SOURCE).resolve(EnumerationDescription.class).load(Source.class), annotation.getValue(ARGUMENT_OPTIONAL).resolve(Boolean.class));
                                }

                                static {
                                    MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Argument.class).getDeclaredMethods();
                                    ARGUMENT_VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
                                    ARGUMENT_TYPING = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("typing"))).getOnly();
                                    ARGUMENT_SOURCE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("source"))).getOnly();
                                    ARGUMENT_OPTIONAL = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("optional"))).getOnly();
                                }
                            }
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class ForStackManipulation
                        implements OffsetMapping {
                            private final StackManipulation stackManipulation;
                            private final TypeDescription.Generic typeDescription;
                            private final TypeDescription.Generic targetType;

                            public ForStackManipulation(StackManipulation stackManipulation, TypeDescription.Generic typeDescription, TypeDescription.Generic targetType) {
                                this.targetType = targetType;
                                this.stackManipulation = stackManipulation;
                                this.typeDescription = typeDescription;
                            }

                            public static <S extends Annotation> net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<S> of(Class<S> annotationType, @MaybeNull Object value) {
                                return value == null ? new OfDefaultValue<S>(annotationType) : new Factory<S>(annotationType, ConstantValue.Simple.wrap(value));
                            }

                            @Override
                            public net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved resolve(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Resolved(assigner, typing, this.stackManipulation, this.typeDescription, this.targetType);
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
                                if (!this.stackManipulation.equals(((ForStackManipulation)object).stackManipulation)) {
                                    return false;
                                }
                                if (!this.typeDescription.equals(((ForStackManipulation)object).typeDescription)) {
                                    return false;
                                }
                                return this.targetType.equals(((ForStackManipulation)object).targetType);
                            }

                            public int hashCode() {
                                return ((this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.targetType.hashCode();
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            public static class Factory<T extends Annotation>
                            extends Factory.AbstractBase<T> {
                                private final Class<T> annotationType;
                                private final StackManipulation stackManipulation;
                                private final TypeDescription.Generic typeDescription;

                                public Factory(Class<T> annotationType, ConstantValue value) {
                                    this(annotationType, value.toStackManipulation(), value.getTypeDescription().asGenericType());
                                }

                                public Factory(Class<T> annotationType, StackManipulation stackManipulation, TypeDescription.Generic typeDescription) {
                                    this.annotationType = annotationType;
                                    this.stackManipulation = stackManipulation;
                                    this.typeDescription = typeDescription;
                                }

                                @Override
                                public Class<T> getAnnotationType() {
                                    return this.annotationType;
                                }

                                @Override
                                protected OffsetMapping make(TypeDescription.Generic target, AnnotationDescription.Loadable<T> annotation) {
                                    return new ForStackManipulation(this.stackManipulation, this.typeDescription, target);
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

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            public static class OfDynamicInvocation<T extends Annotation>
                            extends Factory.AbstractBase<T> {
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
                                protected OffsetMapping make(TypeDescription.Generic target, AnnotationDescription.Loadable<T> annotation) {
                                    if (!target.isInterface()) {
                                        throw new IllegalArgumentException(target + " is not an interface");
                                    }
                                    if (!target.getInterfaces().isEmpty()) {
                                        throw new IllegalArgumentException(target + " must not extend other interfaces");
                                    }
                                    if (!target.isPublic()) {
                                        throw new IllegalArgumentException(target + " is mot public");
                                    }
                                    MethodList methodCandidates = (MethodList)target.getDeclaredMethods().filter(ElementMatchers.isAbstract());
                                    if (methodCandidates.size() != 1) {
                                        throw new IllegalArgumentException(target + " must declare exactly one abstract method");
                                    }
                                    return new ForStackManipulation(MethodInvocation.invoke(this.bootstrapMethod).dynamic(((MethodDescription)methodCandidates.getOnly()).getInternalName(), target.asErasure(), Collections.emptyList(), this.arguments), target, target);
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
                            public static class OfSerializedConstant<T extends Annotation>
                            extends Factory.AbstractBase<T> {
                                private final Class<T> annotationType;
                                private final StackManipulation deserialization;
                                private final TypeDescription.Generic typeDescription;

                                protected OfSerializedConstant(Class<T> annotationType, StackManipulation deserialization, TypeDescription.Generic typeDescription) {
                                    this.annotationType = annotationType;
                                    this.deserialization = deserialization;
                                    this.typeDescription = typeDescription;
                                }

                                public static <S extends Annotation, U extends Serializable> net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<S> of(Class<S> type, U value, Class<? super U> targetType) {
                                    if (!targetType.isInstance(value)) {
                                        throw new IllegalArgumentException(value + " is no instance of " + targetType);
                                    }
                                    return new OfSerializedConstant<S>(type, SerializedConstant.of(value), TypeDescription.ForLoadedType.of(targetType).asGenericType());
                                }

                                @Override
                                public Class<T> getAnnotationType() {
                                    return this.annotationType;
                                }

                                @Override
                                protected OffsetMapping make(TypeDescription.Generic target, AnnotationDescription.Loadable<T> annotation) {
                                    return new ForStackManipulation(this.deserialization, this.typeDescription, target);
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
                                    if (!this.annotationType.equals(((OfSerializedConstant)object).annotationType)) {
                                        return false;
                                    }
                                    if (!this.deserialization.equals(((OfSerializedConstant)object).deserialization)) {
                                        return false;
                                    }
                                    return this.typeDescription.equals(((OfSerializedConstant)object).typeDescription);
                                }

                                public int hashCode() {
                                    return ((this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + this.deserialization.hashCode()) * 31 + this.typeDescription.hashCode();
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            public static class OfAnnotationProperty<T extends Annotation>
                            extends Factory.WithParameterSupportOnly<T> {
                                private final Class<T> annotationType;
                                private final MethodDescription.InDefinedShape property;

                                protected OfAnnotationProperty(Class<T> annotationType, MethodDescription.InDefinedShape property) {
                                    this.annotationType = annotationType;
                                    this.property = property;
                                }

                                public static <S extends Annotation> net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<S> of(Class<S> annotationType, String property) {
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
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation) {
                                    ConstantValue value = ConstantValue.Simple.wrapOrNull(annotation.getValue(this.property).resolve());
                                    if (value == null) {
                                        throw new IllegalStateException("Not a constant value property: " + this.property);
                                    }
                                    return new ForStackManipulation(value.toStackManipulation(), value.getTypeDescription().asGenericType(), target.getType());
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
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Factory<T> {
                                private final Class<T> annotationType;

                                public OfDefaultValue(Class<T> annotationType) {
                                    this.annotationType = annotationType;
                                }

                                @Override
                                public Class<T> getAnnotationType() {
                                    return this.annotationType;
                                }

                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation) {
                                    throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<T> annotation) {
                                    return new ForStackManipulation(DefaultValue.of(target.getType()), target.getType(), target.getType());
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
                            protected static class Resolved
                            implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$ForDelegation$OffsetMapping$Resolved {
                                private final Assigner assigner;
                                private final Assigner.Typing typing;
                                private final StackManipulation stackManipulation;
                                private final TypeDescription.Generic typeDescription;
                                private final TypeDescription.Generic targetType;

                                protected Resolved(Assigner assigner, Assigner.Typing typing, StackManipulation stackManipulation, TypeDescription.Generic typeDescription, TypeDescription.Generic targetType) {
                                    this.assigner = assigner;
                                    this.typing = typing;
                                    this.stackManipulation = stackManipulation;
                                    this.typeDescription = typeDescription;
                                    this.targetType = targetType;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
                                    StackManipulation assignment = this.assigner.assign(this.typeDescription, this.targetType, this.typing);
                                    if (!assignment.isValid()) {
                                        throw new IllegalStateException("Cannot assign " + this.typeDescription + " to " + this.targetType);
                                    }
                                    return new StackManipulation.Compound(this.stackManipulation, assignment);
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
                                    if (!this.typing.equals((Object)((Resolved)object).typing)) {
                                        return false;
                                    }
                                    if (!this.assigner.equals(((Resolved)object).assigner)) {
                                        return false;
                                    }
                                    if (!this.stackManipulation.equals(((Resolved)object).stackManipulation)) {
                                        return false;
                                    }
                                    if (!this.typeDescription.equals(((Resolved)object).typeDescription)) {
                                        return false;
                                    }
                                    return this.targetType.equals(((Resolved)object).targetType);
                                }

                                public int hashCode() {
                                    return ((((this.getClass().hashCode() * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode()) * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.targetType.hashCode();
                                }
                            }
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        public static interface Factory<T extends Annotation> {
                            public Class<T> getAnnotationType();

                            public OffsetMapping make(MethodDescription.InDefinedShape var1, AnnotationDescription.Loadable<T> var2);

                            public OffsetMapping make(ParameterDescription.InDefinedShape var1, AnnotationDescription.Loadable<T> var2);

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            public static class Simple<S extends Annotation>
                            extends AbstractBase<S> {
                                private final Class<S> annotationType;
                                private final OffsetMapping offsetMapping;

                                public Simple(Class<S> annotationType, OffsetMapping offsetMapping) {
                                    this.annotationType = annotationType;
                                    this.offsetMapping = offsetMapping;
                                }

                                @Override
                                public Class<S> getAnnotationType() {
                                    return this.annotationType;
                                }

                                @Override
                                protected OffsetMapping make(TypeDescription.Generic target, AnnotationDescription.Loadable<S> annotation) {
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
                            public static abstract class WithParameterSupportOnly<S extends Annotation>
                            implements Factory<S> {
                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<S> annotation) {
                                    throw new UnsupportedOperationException("This factory does not support binding a method receiver");
                                }
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            public static abstract class AbstractBase<S extends Annotation>
                            implements Factory<S> {
                                @Override
                                public OffsetMapping make(MethodDescription.InDefinedShape target, AnnotationDescription.Loadable<S> annotation) {
                                    return this.make(target.getDeclaringType().asGenericType(), annotation);
                                }

                                @Override
                                public OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<S> annotation) {
                                    return this.make(target.getType(), annotation);
                                }

                                protected abstract OffsetMapping make(TypeDescription.Generic var1, AnnotationDescription.Loadable<S> var2);
                            }
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        public static interface Resolved {
                            public StackManipulation apply(TypeDescription var1, ByteCodeElement.Member var2, TypeList.Generic var3, TypeDescription.Generic var4, TypeDescription.Generic var5, JavaConstant.MethodHandle var6, Map<Integer, Integer> var7, int var8);

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            public static class ForStackManipulation
                            implements Resolved {
                                private final StackManipulation stackManipulation;

                                public ForStackManipulation(StackManipulation stackManipulation) {
                                    this.stackManipulation = stackManipulation;
                                }

                                @Override
                                public StackManipulation apply(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, TypeDescription.Generic current, JavaConstant.MethodHandle methodHandle, Map<Integer, Integer> offsets, int offset) {
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
                                    return this.stackManipulation.equals(((ForStackManipulation)object).stackManipulation);
                                }

                                public int hashCode() {
                                    return this.getClass().hashCode() * 31 + this.stackManipulation.hashCode();
                                }
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class Factory
                    implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory {
                        private final MethodDescription.InDefinedShape delegate;
                        private final Dispatcher dispatcher;
                        private final List<OffsetMapping> offsetMappings;

                        protected Factory(MethodDescription.InDefinedShape delegate, Dispatcher dispatcher, List<? extends OffsetMapping.Factory<?>> factories) {
                            HashMap offsetMappings = new HashMap();
                            for (OffsetMapping.Factory<?> factory : factories) {
                                offsetMappings.put(TypeDescription.ForLoadedType.of(factory.getAnnotationType()), factory);
                            }
                            this.offsetMappings = new ArrayList<OffsetMapping>(factories.size());
                            if (delegate.isMethod() && !delegate.isStatic()) {
                                OffsetMapping offsetMapping = null;
                                for (AnnotationDescription annotationDescription : delegate.getDeclaredAnnotations()) {
                                    OffsetMapping.Factory factory = (OffsetMapping.Factory)offsetMappings.get(annotationDescription.getAnnotationType());
                                    if (factory == null) continue;
                                    OffsetMapping current = factory.make(delegate, annotationDescription.prepare(factory.getAnnotationType()));
                                    if (offsetMapping == null) {
                                        offsetMapping = current;
                                        continue;
                                    }
                                    throw new IllegalStateException(delegate + " is bound to both " + current + " and " + offsetMapping);
                                }
                                this.offsetMappings.add(offsetMapping == null ? new OffsetMapping.ForThisReference(delegate.getDeclaringType().asGenericType(), null, Source.SUBSTITUTED_ELEMENT, false) : offsetMapping);
                            }
                            for (int index = 0; index < delegate.getParameters().size(); ++index) {
                                ParameterDescription.InDefinedShape inDefinedShape = (ParameterDescription.InDefinedShape)delegate.getParameters().get(index);
                                OffsetMapping offsetMapping = null;
                                for (AnnotationDescription annotationDescription : inDefinedShape.getDeclaredAnnotations()) {
                                    OffsetMapping.Factory factory = (OffsetMapping.Factory)offsetMappings.get(annotationDescription.getAnnotationType());
                                    if (factory == null) continue;
                                    OffsetMapping current = factory.make(inDefinedShape, annotationDescription.prepare(factory.getAnnotationType()));
                                    if (offsetMapping == null) {
                                        offsetMapping = current;
                                        continue;
                                    }
                                    throw new IllegalStateException(inDefinedShape + " is bound to both " + current + " and " + offsetMapping);
                                }
                                this.offsetMappings.add(offsetMapping == null ? new OffsetMapping.ForArgument(inDefinedShape.getType(), index, null, Source.SUBSTITUTED_ELEMENT, false) : offsetMapping);
                            }
                            this.delegate = delegate;
                            this.dispatcher = dispatcher;
                        }

                        @Override
                        public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                            ArrayList<OffsetMapping.Resolved> targets = new ArrayList<OffsetMapping.Resolved>(this.offsetMappings.size());
                            for (OffsetMapping offsetMapping : this.offsetMappings) {
                                targets.add(offsetMapping.resolve(assigner, typing, instrumentedType, instrumentedMethod));
                            }
                            return new ForDelegation(this.delegate.getReturnType(), this.dispatcher.resolve(instrumentedType, instrumentedMethod), targets);
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
                            if (!this.delegate.equals(((Factory)object).delegate)) {
                                return false;
                            }
                            if (!this.dispatcher.equals(((Factory)object).dispatcher)) {
                                return false;
                            }
                            return ((Object)this.offsetMappings).equals(((Factory)object).offsetMappings);
                        }

                        public int hashCode() {
                            return ((this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.dispatcher.hashCode()) * 31 + ((Object)this.offsetMappings).hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForInvocation
                implements Step {
                    private final MethodDescription methodDescription;
                    private final Map<Integer, Integer> substitutions;
                    private final Assigner assigner;
                    private final Assigner.Typing typing;

                    protected ForInvocation(MethodDescription methodDescription, Map<Integer, Integer> substitutions, Assigner assigner, Assigner.Typing typing) {
                        this.methodDescription = methodDescription;
                        this.substitutions = substitutions;
                        this.assigner = assigner;
                        this.typing = typing;
                    }

                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(3 + parameters.size() * 2);
                        if (this.methodDescription.isStatic()) {
                            stackManipulations.add(Removal.of(current));
                        } else if (this.methodDescription.isConstructor()) {
                            stackManipulations.add(Removal.of(current));
                            stackManipulations.add(TypeCreation.of(this.methodDescription.getDeclaringType().asErasure()));
                        } else {
                            StackManipulation assignment = this.assigner.assign(current, this.methodDescription.getDeclaringType().asGenericType(), this.typing);
                            if (!assignment.isValid()) {
                                throw new IllegalStateException("Cannot assign " + current + " to " + this.methodDescription.getDeclaringType());
                            }
                            stackManipulations.add(assignment);
                        }
                        boolean shift = (original.getModifiers() & 8) == 0 && (!(original instanceof MethodDescription) || !((MethodDescription)original).isConstructor());
                        for (int index = 0; index < this.methodDescription.getParameters().size(); ++index) {
                            int substitution;
                            int n = this.substitutions.containsKey(index + (shift ? 1 : 0)) ? this.substitutions.get(index + (shift ? 1 : 0)) : (substitution = index + (shift ? 1 : 0));
                            if (substitution >= parameters.size()) {
                                throw new IllegalStateException(original + " does not support an index " + substitution);
                            }
                            stackManipulations.add(MethodVariableAccess.of((TypeDefinition)parameters.get(substitution)).loadFrom(offsets.get(substitution)));
                            StackManipulation assignment = this.assigner.assign((TypeDescription.Generic)parameters.get(substitution), ((ParameterDescription)this.methodDescription.getParameters().get(index)).getType(), this.typing);
                            if (!assignment.isValid()) {
                                throw new IllegalStateException("Cannot assign parameter with " + index + " of type " + parameters.get(substitution) + " to " + this.methodDescription);
                            }
                            stackManipulations.add(assignment);
                        }
                        stackManipulations.add(MethodInvocation.invoke(this.methodDescription));
                        return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), this.methodDescription.getReturnType());
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
                        if (!this.typing.equals((Object)((ForInvocation)object).typing)) {
                            return false;
                        }
                        if (!this.methodDescription.equals(((ForInvocation)object).methodDescription)) {
                            return false;
                        }
                        if (!((Object)this.substitutions).equals(((ForInvocation)object).substitutions)) {
                            return false;
                        }
                        return this.assigner.equals(((ForInvocation)object).assigner);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + ((Object)this.substitutions).hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Factory
                    implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory {
                        private final MethodDescription methodDescription;
                        private final Map<Integer, Integer> substitutions;

                        public Factory(Method method) {
                            this(new MethodDescription.ForLoadedMethod(method));
                        }

                        public Factory(Constructor<?> constructor) {
                            this(new MethodDescription.ForLoadedConstructor(constructor));
                        }

                        public Factory(MethodDescription methodDescription) {
                            this(methodDescription, Collections.emptyMap());
                        }

                        public Factory(MethodDescription methodDescription, Map<Integer, Integer> substitutions) {
                            this.methodDescription = methodDescription;
                            this.substitutions = substitutions;
                        }

                        @Override
                        public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                            return new ForInvocation(this.methodDescription, this.substitutions, assigner, typing);
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
                            if (!this.methodDescription.equals(((Factory)object).methodDescription)) {
                                return false;
                            }
                            return ((Object)this.substitutions).equals(((Factory)object).substitutions);
                        }

                        public int hashCode() {
                            return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + ((Object)this.substitutions).hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static abstract class ForField
                implements Step {
                    protected final FieldDescription fieldDescription;
                    protected final Assigner assigner;
                    protected final Assigner.Typing typing;

                    protected ForField(FieldDescription fieldDescription, Assigner assigner, Assigner.Typing typing) {
                        this.fieldDescription = fieldDescription;
                        this.assigner = assigner;
                        this.typing = typing;
                    }

                    @Override
                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Field description always has declaring type.")
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(2);
                        if (this.fieldDescription.isStatic()) {
                            stackManipulations.add(Removal.of(current));
                        } else {
                            StackManipulation assignment = this.assigner.assign(current, this.fieldDescription.getDeclaringType().asGenericType(), this.typing);
                            if (!assignment.isValid()) {
                                throw new IllegalStateException("Cannot assign " + current + " to " + this.fieldDescription.getDeclaringType());
                            }
                            stackManipulations.add(assignment);
                        }
                        return this.doResolve(original, parameters, offsets, new StackManipulation.Compound(stackManipulations));
                    }

                    protected abstract Resolution doResolve(ByteCodeElement.Member var1, TypeList.Generic var2, Map<Integer, Integer> var3, StackManipulation var4);

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
                        if (!this.typing.equals((Object)((ForField)object).typing)) {
                            return false;
                        }
                        if (!this.fieldDescription.equals(((ForField)object).fieldDescription)) {
                            return false;
                        }
                        return this.assigner.equals(((ForField)object).assigner);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Write
                    extends ForField {
                        private final int index;

                        protected Write(FieldDescription fieldDescription, Assigner assigner, Assigner.Typing typing, int index) {
                            super(fieldDescription, assigner, typing);
                            this.index = index;
                        }

                        @Override
                        protected Resolution doResolve(ByteCodeElement.Member original, TypeList.Generic parameters, Map<Integer, Integer> offsets, StackManipulation stackManipulation) {
                            int index;
                            int n = index = (original.getModifiers() & 8) == 0 && (!(original instanceof MethodDescription) || !((MethodDescription)original).isConstructor()) ? this.index + 1 : this.index;
                            if (index >= parameters.size()) {
                                throw new IllegalStateException(original + " does not define an argument with index " + index);
                            }
                            StackManipulation assignment = this.assigner.assign((TypeDescription.Generic)parameters.get(index), this.fieldDescription.getType(), this.typing);
                            if (!assignment.isValid()) {
                                throw new IllegalStateException("Cannot write " + parameters.get(index) + " to " + this.fieldDescription);
                            }
                            return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulation, MethodVariableAccess.of((TypeDefinition)parameters.get(index)).loadFrom(offsets.get(index)), assignment, FieldAccess.forField(this.fieldDescription).write()), TypeDefinition.Sort.describe(Void.TYPE));
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
                            return this.index == ((Write)object).index;
                        }

                        @Override
                        public int hashCode() {
                            return super.hashCode() * 31 + this.index;
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Factory
                        implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory {
                            private final FieldDescription fieldDescription;
                            private final int index;

                            public Factory(Field field, int index) {
                                this(new FieldDescription.ForLoadedField(field), index);
                            }

                            public Factory(FieldDescription fieldDescription, int index) {
                                this.fieldDescription = fieldDescription;
                                this.index = index;
                            }

                            public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Write(this.fieldDescription, assigner, typing, this.index);
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
                                if (this.index != ((Factory)object).index) {
                                    return false;
                                }
                                return this.fieldDescription.equals(((Factory)object).fieldDescription);
                            }

                            public int hashCode() {
                                return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.index;
                            }
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Read
                    extends ForField {
                        protected Read(FieldDescription fieldDescription, Assigner assigner, Assigner.Typing typing) {
                            super(fieldDescription, assigner, typing);
                        }

                        @Override
                        protected Resolution doResolve(ByteCodeElement.Member original, TypeList.Generic parameters, Map<Integer, Integer> offsets, StackManipulation stackManipulation) {
                            return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulation, FieldAccess.forField(this.fieldDescription).read()), this.fieldDescription.getType());
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
                            return this.getClass() == object.getClass();
                        }

                        @Override
                        public int hashCode() {
                            return super.hashCode();
                        }

                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Factory
                        implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory {
                            private final FieldDescription fieldDescription;

                            public Factory(Field field) {
                                this(new FieldDescription.ForLoadedField(field));
                            }

                            public Factory(FieldDescription fieldDescription) {
                                this.fieldDescription = fieldDescription;
                            }

                            public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                                return new Read(this.fieldDescription, assigner, typing);
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
                                return this.fieldDescription.equals(((Factory)object).fieldDescription);
                            }

                            public int hashCode() {
                                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                            }
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForArgumentLoading
                implements Step,
                Factory {
                    private final int index;

                    protected ForArgumentLoading(int index) {
                        this.index = index;
                    }

                    @Override
                    public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return this;
                    }

                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        if (this.index >= parameters.size()) {
                            throw new IllegalStateException(original + " has not " + this.index + " arguments");
                        }
                        return new Simple((StackManipulation)new StackManipulation.Compound(Removal.of(current), MethodVariableAccess.of((TypeDefinition)parameters.get(this.index)).loadFrom(offsets.get(this.index))), (TypeDescription.Generic)parameters.get(this.index));
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
                        return this.index == ((ForArgumentLoading)object).index;
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.index;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForArgumentSubstitution
                implements Step {
                    private final StackManipulation substitution;
                    private final TypeDescription.Generic typeDescription;
                    private final int index;
                    private final Assigner assigner;
                    private final Assigner.Typing typing;

                    protected ForArgumentSubstitution(StackManipulation substitution, TypeDescription.Generic typeDescription, int index, Assigner assigner, Assigner.Typing typing) {
                        this.substitution = substitution;
                        this.typeDescription = typeDescription;
                        this.index = index;
                        this.assigner = assigner;
                        this.typing = typing;
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory of(Object value, int index) {
                        if (index < 0) {
                            throw new IllegalArgumentException("Index cannot be negative: " + index);
                        }
                        ConstantValue constant = ConstantValue.Simple.wrap(value);
                        return new Factory(constant.toStackManipulation(), constant.getTypeDescription().asGenericType(), index);
                    }

                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        if (this.index >= parameters.size()) {
                            throw new IllegalStateException(original + " has not " + this.index + " arguments");
                        }
                        StackManipulation assignment = this.assigner.assign(this.typeDescription, (TypeDescription.Generic)parameters.get(this.index), this.typing);
                        if (!assignment.isValid()) {
                            throw new IllegalStateException("Cannot assign " + this.typeDescription + " to " + parameters.get(this.index));
                        }
                        return new Simple((StackManipulation)new StackManipulation.Compound(this.substitution, assignment, MethodVariableAccess.of((TypeDefinition)parameters.get(this.index)).storeAt(offsets.get(this.index))), current);
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
                        if (this.index != ((ForArgumentSubstitution)object).index) {
                            return false;
                        }
                        if (!this.typing.equals((Object)((ForArgumentSubstitution)object).typing)) {
                            return false;
                        }
                        if (!this.substitution.equals(((ForArgumentSubstitution)object).substitution)) {
                            return false;
                        }
                        if (!this.typeDescription.equals(((ForArgumentSubstitution)object).typeDescription)) {
                            return false;
                        }
                        return this.assigner.equals(((ForArgumentSubstitution)object).assigner);
                    }

                    public int hashCode() {
                        return ((((this.getClass().hashCode() * 31 + this.substitution.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.index) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
                    }

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Factory
                    implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory {
                        private final StackManipulation stackManipulation;
                        private final TypeDescription.Generic typeDescription;
                        private final int index;

                        public Factory(StackManipulation stackManipulation, Type type, int index) {
                            this(stackManipulation, TypeDefinition.Sort.describe(type), index);
                        }

                        public Factory(StackManipulation stackManipulation, TypeDescription.Generic typeDescription, int index) {
                            this.stackManipulation = stackManipulation;
                            this.typeDescription = typeDescription;
                            this.index = index;
                        }

                        public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                            return new ForArgumentSubstitution(this.stackManipulation, this.typeDescription, this.index, assigner, typing);
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
                            if (this.index != ((Factory)object).index) {
                                return false;
                            }
                            if (!this.stackManipulation.equals(((Factory)object).stackManipulation)) {
                                return false;
                            }
                            return this.typeDescription.equals(((Factory)object).typeDescription);
                        }

                        public int hashCode() {
                            return ((this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode()) * 31 + this.index;
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForAssignment
                implements Step {
                    @MaybeNull
                    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                    private final TypeDescription.Generic result;
                    private final Assigner assigner;

                    protected ForAssignment(@MaybeNull TypeDescription.Generic result, Assigner assigner) {
                        this.result = result;
                        this.assigner = assigner;
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory castTo(Type type) {
                        return new Factory(TypeDefinition.Sort.describe(type));
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory castTo(TypeDescription.Generic typeDescription) {
                        return new Factory(typeDescription);
                    }

                    public static net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory castToSubstitutionResult() {
                        return new Factory(null);
                    }

                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        StackManipulation assignment = this.assigner.assign(current, this.result == null ? result : this.result, Assigner.Typing.DYNAMIC);
                        if (!assignment.isValid()) {
                            throw new IllegalStateException("Failed to assign " + current + " to " + (this.result == null ? result : this.result));
                        }
                        return new Simple(assignment, this.result == null ? result : this.result);
                    }

                    public boolean equals(@MaybeNull Object object) {
                        block10: {
                            block9: {
                                TypeDescription.Generic generic;
                                block8: {
                                    TypeDescription.Generic generic2;
                                    if (this == object) {
                                        return true;
                                    }
                                    if (object == null) {
                                        return false;
                                    }
                                    if (this.getClass() != object.getClass()) {
                                        return false;
                                    }
                                    TypeDescription.Generic generic3 = ((ForAssignment)object).result;
                                    generic = generic2 = this.result;
                                    if (generic3 == null) break block8;
                                    if (generic == null) break block9;
                                    if (!generic2.equals(generic3)) {
                                        return false;
                                    }
                                    break block10;
                                }
                                if (generic == null) break block10;
                            }
                            return false;
                        }
                        return this.assigner.equals(((ForAssignment)object).assigner);
                    }

                    public int hashCode() {
                        int n = this.getClass().hashCode() * 31;
                        TypeDescription.Generic generic = this.result;
                        if (generic != null) {
                            n = n + generic.hashCode();
                        }
                        return n * 31 + this.assigner.hashCode();
                    }

                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class Factory
                    implements net.bytebuddy.asm.MemberSubstitution$Substitution$Chain$Step$Factory {
                        @MaybeNull
                        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                        private final TypeDescription.Generic result;

                        protected Factory(@MaybeNull TypeDescription.Generic result) {
                            this.result = result;
                        }

                        public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                            return new ForAssignment(this.result, assigner);
                        }

                        public boolean equals(@MaybeNull Object object) {
                            block10: {
                                block9: {
                                    TypeDescription.Generic generic;
                                    block8: {
                                        TypeDescription.Generic generic2;
                                        if (this == object) {
                                            return true;
                                        }
                                        if (object == null) {
                                            return false;
                                        }
                                        if (this.getClass() != object.getClass()) {
                                            return false;
                                        }
                                        TypeDescription.Generic generic3 = ((Factory)object).result;
                                        generic = generic2 = this.result;
                                        if (generic3 == null) break block8;
                                        if (generic == null) break block9;
                                        if (!generic2.equals(generic3)) {
                                            return false;
                                        }
                                        break block10;
                                    }
                                    if (generic == null) break block10;
                                }
                                return false;
                            }
                            return true;
                        }

                        public int hashCode() {
                            int n = this.getClass().hashCode() * 31;
                            TypeDescription.Generic generic = this.result;
                            if (generic != null) {
                                n = n + generic.hashCode();
                            }
                            return n;
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple
                implements Step,
                Resolution,
                Factory {
                    private final StackManipulation stackManipulation;
                    private final TypeDescription.Generic resultType;

                    public Simple(StackManipulation stackManipulation, Type resultType) {
                        this(stackManipulation, TypeDefinition.Sort.describe(resultType));
                    }

                    public Simple(StackManipulation stackManipulation, TypeDescription.Generic resultType) {
                        this.stackManipulation = stackManipulation;
                        this.resultType = resultType;
                    }

                    public static Factory of(Object value) {
                        ConstantValue constant = ConstantValue.Simple.wrap(value);
                        return new Simple(constant.toStackManipulation(), constant.getTypeDescription().asGenericType());
                    }

                    @Override
                    public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return this;
                    }

                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        return receiver.represents(Void.TYPE) ? this : new Simple((StackManipulation)new StackManipulation.Compound(Removal.of(current), this.stackManipulation), this.resultType);
                    }

                    @Override
                    public StackManipulation getStackManipulation() {
                        return this.stackManipulation;
                    }

                    @Override
                    public TypeDescription.Generic getResultType() {
                        return this.resultType;
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
                        return this.resultType.equals(((Simple)object).resultType);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.resultType.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum OfOriginalExpression implements Step,
                Factory
                {
                    INSTANCE;


                    @Override
                    public Resolution resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, TypeDescription.Generic current, Map<Integer, Integer> offsets, int freeOffset) {
                        ArrayList<StackManipulation> stackManipulations;
                        if (original instanceof MethodDescription && ((MethodDescription)original).isConstructor()) {
                            stackManipulations = new ArrayList(parameters.size() + 4);
                            stackManipulations.add(Removal.of(current));
                            stackManipulations.add(TypeCreation.of(original.getDeclaringType().asErasure()));
                            stackManipulations.add(Duplication.SINGLE);
                        } else {
                            stackManipulations = new ArrayList<StackManipulation>(parameters.size() + 4);
                            stackManipulations.add(Removal.of(current));
                        }
                        for (int index = 0; index < parameters.size(); ++index) {
                            stackManipulations.add(MethodVariableAccess.of((TypeDefinition)parameters.get(index)).loadFrom(offsets.get(index)));
                        }
                        if (original instanceof MethodDescription) {
                            stackManipulations.add(stackManipulation);
                            return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), ((MethodDescription)original).isConstructor() ? original.getDeclaringType().asGenericType() : ((MethodDescription)original).getReturnType());
                        }
                        if (original instanceof FieldDescription) {
                            if (original.isStatic()) {
                                if (parameters.isEmpty()) {
                                    stackManipulations.add(stackManipulation);
                                    return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), ((FieldDescription)original).getType());
                                }
                                stackManipulations.add(stackManipulation);
                                return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), TypeDefinition.Sort.describe(Void.TYPE));
                            }
                            if (parameters.size() == 1) {
                                stackManipulations.add(FieldAccess.forField((FieldDescription)original).read());
                                return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), ((FieldDescription)original).getType());
                            }
                            stackManipulations.add(FieldAccess.forField((FieldDescription)original).write());
                            return new Simple((StackManipulation)new StackManipulation.Compound(stackManipulations), TypeDefinition.Sort.describe(Void.TYPE));
                        }
                        throw new IllegalArgumentException("Unexpected target type: " + original);
                    }

                    @Override
                    public Step make(Assigner assigner, Assigner.Typing typing, TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                        return this;
                    }
                }

                public static interface Factory {
                    public Step make(Assigner var1, Assigner.Typing var2, TypeDescription var3, MethodDescription var4);
                }

                public static interface Resolution {
                    public StackManipulation getStackManipulation();

                    public TypeDescription.Generic getResultType();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodInvocation
        implements Substitution {
            private final TypeDescription instrumentedType;
            private final MethodResolver methodResolver;

            public ForMethodInvocation(TypeDescription instrumentedType, MethodResolver methodResolver) {
                this.instrumentedType = instrumentedType;
                this.methodResolver = methodResolver;
            }

            public StackManipulation resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                TypeList.Generic mapped;
                MethodDescription methodDescription = this.methodResolver.resolve(receiver, original, parameters, result);
                if (!methodDescription.isAccessibleTo(this.instrumentedType)) {
                    throw new IllegalStateException(this.instrumentedType + " cannot access " + methodDescription);
                }
                TypeList.Generic generic = mapped = methodDescription.isStatic() ? methodDescription.getParameters().asTypeList() : new TypeList.Generic.Explicit(CompoundList.of(methodDescription.getDeclaringType(), methodDescription.getParameters().asTypeList()));
                if (!methodDescription.getReturnType().asErasure().isAssignableTo(result.asErasure())) {
                    throw new IllegalStateException("Cannot assign return value of " + methodDescription + " to " + result);
                }
                if (mapped.size() != parameters.size()) {
                    throw new IllegalStateException("Cannot invoke " + methodDescription + " on " + parameters.size() + " parameters");
                }
                for (int index = 0; index < mapped.size(); ++index) {
                    if (((TypeDescription.Generic)parameters.get(index)).asErasure().isAssignableTo(((TypeDescription.Generic)mapped.get(index)).asErasure())) continue;
                    throw new IllegalStateException("Cannot invoke " + methodDescription + " on parameter " + index + " of type " + parameters.get(index));
                }
                return methodDescription.isVirtual() ? MethodInvocation.invoke(methodDescription).virtual(((TypeDescription.Generic)mapped.get(0)).asErasure()) : MethodInvocation.invoke(methodDescription);
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
                if (!this.instrumentedType.equals(((ForMethodInvocation)object).instrumentedType)) {
                    return false;
                }
                return this.methodResolver.equals(((ForMethodInvocation)object).methodResolver);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.methodResolver.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfMatchedMethod
            implements Factory {
                private final ElementMatcher<? super MethodDescription> matcher;
                private final MethodGraph.Compiler methodGraphCompiler;

                public OfMatchedMethod(ElementMatcher<? super MethodDescription> matcher, MethodGraph.Compiler methodGraphCompiler) {
                    this.matcher = matcher;
                    this.methodGraphCompiler = methodGraphCompiler;
                }

                @Override
                public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    return new ForMethodInvocation(instrumentedType, new MethodResolver.Matching(instrumentedType, this.methodGraphCompiler, this.matcher));
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
                    if (!this.matcher.equals(((OfMatchedMethod)object).matcher)) {
                        return false;
                    }
                    return this.methodGraphCompiler.equals(((OfMatchedMethod)object).methodGraphCompiler);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.methodGraphCompiler.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class OfGivenMethod
            implements Factory {
                private final MethodDescription methodDescription;

                public OfGivenMethod(MethodDescription methodDescription) {
                    this.methodDescription = methodDescription;
                }

                public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    return new ForMethodInvocation(instrumentedType, new MethodResolver.Simple(this.methodDescription));
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
                    return this.methodDescription.equals(((OfGivenMethod)object).methodDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            static enum OfInstrumentedMethod implements Factory
            {
                INSTANCE;


                @Override
                public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    return new ForMethodInvocation(instrumentedType, new MethodResolver.Simple(instrumentedMethod));
                }
            }

            public static interface MethodResolver {
                public MethodDescription resolve(TypeDescription var1, ByteCodeElement.Member var2, TypeList.Generic var3, TypeDescription.Generic var4);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Matching
                implements MethodResolver {
                    private final TypeDescription instrumentedType;
                    private final MethodGraph.Compiler methodGraphCompiler;
                    private final ElementMatcher<? super MethodDescription> matcher;

                    public Matching(TypeDescription instrumentedType, MethodGraph.Compiler methodGraphCompiler, ElementMatcher<? super MethodDescription> matcher) {
                        this.instrumentedType = instrumentedType;
                        this.methodGraphCompiler = methodGraphCompiler;
                        this.matcher = matcher;
                    }

                    @Override
                    public MethodDescription resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result) {
                        if (parameters.isEmpty()) {
                            throw new IllegalStateException("Cannot substitute parameterless instruction with " + original);
                        }
                        if (((TypeDescription.Generic)parameters.get(0)).isPrimitive() || ((TypeDescription.Generic)parameters.get(0)).isArray()) {
                            throw new IllegalStateException("Cannot invoke method on primitive or array type for " + original);
                        }
                        TypeDefinition typeDefinition = ((TypeDescription.Generic)parameters.get(0)).accept(new TypeDescription.Generic.Visitor.Substitutor.ForReplacement(this.instrumentedType));
                        List candidates = CompoundList.of(this.methodGraphCompiler.compile(typeDefinition, this.instrumentedType).listNodes().asMethodList().filter(this.matcher), typeDefinition.getDeclaredMethods().filter(ElementMatchers.isPrivate().and(ElementMatchers.isVisibleTo(this.instrumentedType)).and(this.matcher)));
                        if (candidates.size() == 1) {
                            return (MethodDescription)candidates.get(0);
                        }
                        throw new IllegalStateException("Not exactly one method that matches " + this.matcher + ": " + candidates);
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
                        if (!this.instrumentedType.equals(((Matching)object).instrumentedType)) {
                            return false;
                        }
                        if (!this.methodGraphCompiler.equals(((Matching)object).methodGraphCompiler)) {
                            return false;
                        }
                        return this.matcher.equals(((Matching)object).matcher);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.methodGraphCompiler.hashCode()) * 31 + this.matcher.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple
                implements MethodResolver {
                    private final MethodDescription methodDescription;

                    public Simple(MethodDescription methodDescription) {
                        this.methodDescription = methodDescription;
                    }

                    public MethodDescription resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result) {
                        return this.methodDescription;
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
                        return this.methodDescription.equals(((Simple)object).methodDescription);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForFieldAccess
        implements Substitution {
            private final TypeDescription instrumentedType;
            private final FieldResolver fieldResolver;

            public ForFieldAccess(TypeDescription instrumentedType, FieldResolver fieldResolver) {
                this.instrumentedType = instrumentedType;
                this.fieldResolver = fieldResolver;
            }

            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
            public StackManipulation resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                FieldDescription fieldDescription = this.fieldResolver.resolve(receiver, original, parameters, result);
                if (!fieldDescription.isAccessibleTo(this.instrumentedType)) {
                    throw new IllegalStateException(this.instrumentedType + " cannot access " + fieldDescription);
                }
                if (result.represents(Void.TYPE)) {
                    if (parameters.size() != (fieldDescription.isStatic() ? 1 : 2)) {
                        throw new IllegalStateException("Cannot set " + fieldDescription + " with " + parameters);
                    }
                    if (!fieldDescription.isStatic() && !((TypeDescription.Generic)parameters.get(0)).asErasure().isAssignableTo(fieldDescription.getDeclaringType().asErasure())) {
                        throw new IllegalStateException("Cannot set " + fieldDescription + " on " + parameters.get(0));
                    }
                    if (!((TypeDescription.Generic)parameters.get(fieldDescription.isStatic() ? 0 : 1)).asErasure().isAssignableTo(fieldDescription.getType().asErasure())) {
                        throw new IllegalStateException("Cannot set " + fieldDescription + " to " + parameters.get(fieldDescription.isStatic() ? 0 : 1));
                    }
                    return FieldAccess.forField(fieldDescription).write();
                }
                if (parameters.size() != (fieldDescription.isStatic() ? 0 : 1)) {
                    throw new IllegalStateException("Cannot set " + fieldDescription + " with " + parameters);
                }
                if (!fieldDescription.isStatic() && !((TypeDescription.Generic)parameters.get(0)).asErasure().isAssignableTo(fieldDescription.getDeclaringType().asErasure())) {
                    throw new IllegalStateException("Cannot get " + fieldDescription + " on " + parameters.get(0));
                }
                if (!fieldDescription.getType().asErasure().isAssignableTo(result.asErasure())) {
                    throw new IllegalStateException("Cannot get " + fieldDescription + " as " + result);
                }
                return FieldAccess.forField(fieldDescription).read();
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
                if (!this.instrumentedType.equals(((ForFieldAccess)object).instrumentedType)) {
                    return false;
                }
                return this.fieldResolver.equals(((ForFieldAccess)object).fieldResolver);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.fieldResolver.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfMatchedField
            implements Factory {
                private final ElementMatcher<? super FieldDescription> matcher;

                public OfMatchedField(ElementMatcher<? super FieldDescription> matcher) {
                    this.matcher = matcher;
                }

                @Override
                public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    return new ForFieldAccess(instrumentedType, new FieldResolver.ForElementMatcher(instrumentedType, this.matcher));
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
                    return this.matcher.equals(((OfMatchedField)object).matcher);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.matcher.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class OfGivenField
            implements Factory {
                private final FieldDescription fieldDescription;

                public OfGivenField(FieldDescription fieldDescription) {
                    this.fieldDescription = fieldDescription;
                }

                public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                    return new ForFieldAccess(instrumentedType, new FieldResolver.Simple(this.fieldDescription));
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
                    return this.fieldDescription.equals(((OfGivenField)object).fieldDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                }
            }

            public static interface FieldResolver {
                public FieldDescription resolve(TypeDescription var1, ByteCodeElement.Member var2, TypeList.Generic var3, TypeDescription.Generic var4);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForElementMatcher
                implements FieldResolver {
                    private final TypeDescription instrumentedType;
                    private final ElementMatcher<? super FieldDescription> matcher;

                    protected ForElementMatcher(TypeDescription instrumentedType, ElementMatcher<? super FieldDescription> matcher) {
                        this.instrumentedType = instrumentedType;
                        this.matcher = matcher;
                    }

                    @Override
                    public FieldDescription resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result) {
                        if (parameters.isEmpty()) {
                            throw new IllegalStateException("Cannot substitute parameterless instruction with " + original);
                        }
                        if (((TypeDescription.Generic)parameters.get(0)).isPrimitive() || ((TypeDescription.Generic)parameters.get(0)).isArray()) {
                            throw new IllegalStateException("Cannot access field on primitive or array type for " + original);
                        }
                        TypeDefinition current = ((TypeDescription.Generic)parameters.get(0)).accept(new TypeDescription.Generic.Visitor.Substitutor.ForReplacement(this.instrumentedType));
                        do {
                            FieldList fields;
                            if ((fields = (FieldList)current.getDeclaredFields().filter(ElementMatchers.not(ElementMatchers.isStatic()).and(ElementMatchers.isVisibleTo(this.instrumentedType)).and(this.matcher))).size() == 1) {
                                return (FieldDescription)fields.getOnly();
                            }
                            if (fields.size() <= 1) continue;
                            throw new IllegalStateException("Ambiguous field location of " + fields);
                        } while ((current = current.getSuperClass()) != null);
                        throw new IllegalStateException("Cannot locate field matching " + this.matcher + " on " + receiver);
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
                        if (!this.instrumentedType.equals(((ForElementMatcher)object).instrumentedType)) {
                            return false;
                        }
                        return this.matcher.equals(((ForElementMatcher)object).matcher);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.matcher.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple
                implements FieldResolver {
                    private final FieldDescription fieldDescription;

                    public Simple(FieldDescription fieldDescription) {
                        this.fieldDescription = fieldDescription;
                    }

                    public FieldDescription resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result) {
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
                        return this.fieldDescription.equals(((Simple)object).fieldDescription);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForValue
        implements Substitution,
        Factory {
            private final StackManipulation stackManipulation;
            private final TypeDescription.Generic typeDescription;

            public ForValue(StackManipulation stackManipulation, TypeDescription.Generic typeDescription) {
                this.stackManipulation = stackManipulation;
                this.typeDescription = typeDescription;
            }

            public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                return this;
            }

            public StackManipulation resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(parameters.size());
                for (int index = parameters.size() - 1; index >= 0; --index) {
                    stackManipulations.add(Removal.of((TypeDefinition)parameters.get(index)));
                }
                if (!this.typeDescription.asErasure().isAssignableTo(result.asErasure())) {
                    throw new IllegalStateException("Cannot assign " + this.typeDescription + " to " + result);
                }
                return new StackManipulation.Compound(CompoundList.of(stackManipulations, this.stackManipulation));
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
                if (!this.stackManipulation.equals(((ForValue)object).stackManipulation)) {
                    return false;
                }
                return this.typeDescription.equals(((ForValue)object).typeDescription);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDescription.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Stubbing implements Substitution,
        Factory
        {
            INSTANCE;


            @Override
            public Substitution make(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                return this;
            }

            @Override
            public StackManipulation resolve(TypeDescription receiver, ByteCodeElement.Member original, TypeList.Generic parameters, TypeDescription.Generic result, JavaConstant.MethodHandle methodHandle, StackManipulation stackManipulation, int freeOffset) {
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(parameters.size());
                for (int index = parameters.size() - 1; index >= 0; --index) {
                    stackManipulations.add(Removal.of((TypeDefinition)parameters.get(index)));
                }
                return new StackManipulation.Compound(CompoundList.of(stackManipulations, DefaultValue.of(result.asErasure())));
            }
        }

        public static interface Factory {
            public Substitution make(TypeDescription var1, MethodDescription var2, TypePool var3);
        }
    }

    public static interface TypePoolResolver {
        public TypePool resolve(TypeDescription var1, MethodDescription var2, TypePool var3);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForClassFileLocator
        implements TypePoolResolver {
            private final ClassFileLocator classFileLocator;
            private final TypePool.Default.ReaderMode readerMode;

            public ForClassFileLocator(ClassFileLocator classFileLocator) {
                this(classFileLocator, TypePool.Default.ReaderMode.FAST);
            }

            public ForClassFileLocator(ClassFileLocator classFileLocator, TypePool.Default.ReaderMode readerMode) {
                this.classFileLocator = classFileLocator;
                this.readerMode = readerMode;
            }

            public static TypePoolResolver of(@MaybeNull ClassLoader classLoader) {
                return new ForClassFileLocator(ClassFileLocator.ForClassLoader.of(classLoader));
            }

            public TypePool resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                return new TypePool.Default(new TypePool.CacheProvider.Simple(), this.classFileLocator, this.readerMode, typePool);
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
                if (!this.readerMode.equals((Object)((ForClassFileLocator)object).readerMode)) {
                    return false;
                }
                return this.classFileLocator.equals(((ForClassFileLocator)object).classFileLocator);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.classFileLocator.hashCode()) * 31 + this.readerMode.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForExplicitPool
        implements TypePoolResolver {
            private final TypePool typePool;

            public ForExplicitPool(TypePool typePool) {
                this.typePool = typePool;
            }

            public TypePool resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                return this.typePool;
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
                return this.typePool.equals(((ForExplicitPool)object).typePool);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.typePool.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum OfImplicitPool implements TypePoolResolver
        {
            INSTANCE;


            @Override
            public TypePool resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, TypePool typePool) {
                return typePool;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class WithoutSpecification {
        protected final MethodGraph.Compiler methodGraphCompiler;
        protected final TypePoolResolver typePoolResolver;
        protected final boolean strict;
        protected final boolean failIfNoMatch;
        protected final Replacement.Factory replacementFactory;

        protected WithoutSpecification(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory) {
            this.methodGraphCompiler = methodGraphCompiler;
            this.typePoolResolver = typePoolResolver;
            this.strict = strict;
            this.failIfNoMatch = failIfNoMatch;
            this.replacementFactory = replacementFactory;
        }

        public MemberSubstitution stub() {
            return this.replaceWith(Substitution.Stubbing.INSTANCE);
        }

        public MemberSubstitution replaceWithConstant(Object value) {
            ConstantValue constant = ConstantValue.Simple.wrap(value);
            return this.replaceWith(new Substitution.ForValue(constant.toStackManipulation(), constant.getTypeDescription().asGenericType()));
        }

        public MemberSubstitution replaceWith(Field field) {
            return this.replaceWith(new FieldDescription.ForLoadedField(field));
        }

        public MemberSubstitution replaceWith(FieldDescription fieldDescription) {
            return this.replaceWith(new Substitution.ForFieldAccess.OfGivenField(fieldDescription));
        }

        public MemberSubstitution replaceWithField(ElementMatcher<? super FieldDescription> matcher) {
            return this.replaceWith(new Substitution.ForFieldAccess.OfMatchedField(matcher));
        }

        public MemberSubstitution replaceWith(Method method) {
            return this.replaceWith(new MethodDescription.ForLoadedMethod(method));
        }

        public MemberSubstitution replaceWith(MethodDescription methodDescription) {
            if (!methodDescription.isMethod()) {
                throw new IllegalArgumentException("Cannot use " + methodDescription + " as a replacement");
            }
            return this.replaceWith(new Substitution.ForMethodInvocation.OfGivenMethod(methodDescription));
        }

        public MemberSubstitution replaceWithMethod(ElementMatcher<? super MethodDescription> matcher) {
            return this.replaceWithMethod(matcher, this.methodGraphCompiler);
        }

        public MemberSubstitution replaceWithMethod(ElementMatcher<? super MethodDescription> matcher, MethodGraph.Compiler methodGraphCompiler) {
            return this.replaceWith(new Substitution.ForMethodInvocation.OfMatchedMethod(matcher, methodGraphCompiler));
        }

        public MemberSubstitution replaceWithInstrumentedMethod() {
            return this.replaceWith(Substitution.ForMethodInvocation.OfInstrumentedMethod.INSTANCE);
        }

        public MemberSubstitution replaceWithChain(Substitution.Chain.Step.Factory ... step) {
            return this.replaceWithChain(Arrays.asList(step));
        }

        public MemberSubstitution replaceWithChain(List<? extends Substitution.Chain.Step.Factory> steps) {
            return this.replaceWith(Substitution.Chain.withDefaultAssigner().executing(steps));
        }

        public abstract MemberSubstitution replaceWith(Substitution.Factory var1);

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
            if (this.strict != ((WithoutSpecification)object).strict) {
                return false;
            }
            if (this.failIfNoMatch != ((WithoutSpecification)object).failIfNoMatch) {
                return false;
            }
            if (!this.methodGraphCompiler.equals(((WithoutSpecification)object).methodGraphCompiler)) {
                return false;
            }
            if (!this.typePoolResolver.equals(((WithoutSpecification)object).typePoolResolver)) {
                return false;
            }
            return this.replacementFactory.equals(((WithoutSpecification)object).replacementFactory);
        }

        public int hashCode() {
            return ((((this.getClass().hashCode() * 31 + this.methodGraphCompiler.hashCode()) * 31 + this.typePoolResolver.hashCode()) * 31 + this.strict) * 31 + this.failIfNoMatch) * 31 + this.replacementFactory.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMatchedMethod
        extends WithoutSpecification {
            private final ElementMatcher<? super MethodDescription> matcher;
            private final boolean includeVirtualCalls;
            private final boolean includeSuperCalls;

            protected ForMatchedMethod(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory, ElementMatcher<? super MethodDescription> matcher) {
                this(methodGraphCompiler, typePoolResolver, strict, failIfNoMatch, replacementFactory, matcher, true, true);
            }

            protected ForMatchedMethod(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory, ElementMatcher<? super MethodDescription> matcher, boolean includeVirtualCalls, boolean includeSuperCalls) {
                super(methodGraphCompiler, typePoolResolver, strict, failIfNoMatch, replacementFactory);
                this.matcher = matcher;
                this.includeVirtualCalls = includeVirtualCalls;
                this.includeSuperCalls = includeSuperCalls;
            }

            public WithoutSpecification onVirtualCall() {
                return new ForMatchedMethod(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, ElementMatchers.isVirtual().and(this.matcher), true, false);
            }

            public WithoutSpecification onSuperCall() {
                return new ForMatchedMethod(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, ElementMatchers.isVirtual().and(this.matcher), false, true);
            }

            @Override
            public MemberSubstitution replaceWith(Substitution.Factory substitutionFactory) {
                return new MemberSubstitution(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, new Replacement.Factory.Compound(this.replacementFactory, Replacement.ForElementMatchers.Factory.ofMethod(this.matcher, this.includeVirtualCalls, this.includeSuperCalls, substitutionFactory)));
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
                if (this.includeVirtualCalls != ((ForMatchedMethod)object).includeVirtualCalls) {
                    return false;
                }
                if (this.includeSuperCalls != ((ForMatchedMethod)object).includeSuperCalls) {
                    return false;
                }
                return this.matcher.equals(((ForMatchedMethod)object).matcher);
            }

            @Override
            public int hashCode() {
                return ((super.hashCode() * 31 + this.matcher.hashCode()) * 31 + this.includeVirtualCalls) * 31 + this.includeSuperCalls;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMatchedField
        extends WithoutSpecification {
            private final ElementMatcher<? super FieldDescription> matcher;
            private final boolean matchRead;
            private final boolean matchWrite;

            protected ForMatchedField(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory, ElementMatcher<? super FieldDescription> matcher) {
                this(methodGraphCompiler, typePoolResolver, strict, failIfNoMatch, replacementFactory, matcher, true, true);
            }

            protected ForMatchedField(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory, ElementMatcher<? super FieldDescription> matcher, boolean matchRead, boolean matchWrite) {
                super(methodGraphCompiler, typePoolResolver, strict, failIfNoMatch, replacementFactory);
                this.matcher = matcher;
                this.matchRead = matchRead;
                this.matchWrite = matchWrite;
            }

            public WithoutSpecification onRead() {
                return new ForMatchedField(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, this.matcher, true, false);
            }

            public WithoutSpecification onWrite() {
                return new ForMatchedField(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, this.replacementFactory, this.matcher, false, true);
            }

            @Override
            public MemberSubstitution replaceWith(Substitution.Factory substitutionFactory) {
                return new MemberSubstitution(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, new Replacement.Factory.Compound(this.replacementFactory, Replacement.ForElementMatchers.Factory.ofField(this.matcher, this.matchRead, this.matchWrite, substitutionFactory)));
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
                if (this.matchRead != ((ForMatchedField)object).matchRead) {
                    return false;
                }
                if (this.matchWrite != ((ForMatchedField)object).matchWrite) {
                    return false;
                }
                return this.matcher.equals(((ForMatchedField)object).matcher);
            }

            @Override
            public int hashCode() {
                return ((super.hashCode() * 31 + this.matcher.hashCode()) * 31 + this.matchRead) * 31 + this.matchWrite;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ForMatchedByteCodeElement
        extends WithoutSpecification {
            private final ElementMatcher<? super ByteCodeElement.Member> matcher;

            protected ForMatchedByteCodeElement(MethodGraph.Compiler methodGraphCompiler, TypePoolResolver typePoolResolver, boolean strict, boolean failIfNoMatch, Replacement.Factory replacementFactory, ElementMatcher<? super ByteCodeElement.Member> matcher) {
                super(methodGraphCompiler, typePoolResolver, strict, failIfNoMatch, replacementFactory);
                this.matcher = matcher;
            }

            @Override
            public MemberSubstitution replaceWith(Substitution.Factory substitutionFactory) {
                return new MemberSubstitution(this.methodGraphCompiler, this.typePoolResolver, this.strict, this.failIfNoMatch, new Replacement.Factory.Compound(this.replacementFactory, Replacement.ForElementMatchers.Factory.of(this.matcher, substitutionFactory)));
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
                return this.matcher.equals(((ForMatchedByteCodeElement)object).matcher);
            }

            @Override
            public int hashCode() {
                return super.hashCode() * 31 + this.matcher.hashCode();
            }
        }
    }
}


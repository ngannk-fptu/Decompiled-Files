/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.build;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class AccessControllerPlugin
extends Plugin.ForElementMatcher
implements Plugin.Factory {
    private static final String ACCESS_CONTROLLER = "java.security.AccessController";
    private static final String NAME = "ACCESS_CONTROLLER";
    private static final Object[] EMPTY = new Object[0];
    private static final Map<MethodDescription.SignatureToken, MethodDescription.SignatureToken> SIGNATURES = new HashMap<MethodDescription.SignatureToken, MethodDescription.SignatureToken>();
    @MaybeNull
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
    private final String property;

    public AccessControllerPlugin() {
        this((String)null);
    }

    @Plugin.Factory.UsingReflection.Priority(value=0x7FFFFFFF)
    public AccessControllerPlugin(@MaybeNull String property) {
        super(ElementMatchers.declaresMethod(ElementMatchers.isAnnotatedWith(Enhance.class)));
        this.property = property;
    }

    @Override
    public Plugin make() {
        return this;
    }

    @Override
    @SuppressFBWarnings(value={"SBSC_USE_STRINGBUFFER_CONCATENATION"}, justification="Collision is unlikely and buffer overhead not justified.")
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        String name = NAME;
        while (!((FieldList)typeDescription.getDeclaredFields().filter(ElementMatchers.named(name))).isEmpty()) {
            name = name + "$";
        }
        return builder.defineField(name, Boolean.TYPE, Visibility.PRIVATE, Ownership.STATIC, FieldManifestation.FINAL).visit(new AsmVisitorWrapper.ForDeclaredMethods().method(ElementMatchers.isAnnotatedWith(Enhance.class), new AccessControlWrapper(name))).initializer(this.property == null ? new Initializer.WithoutProperty(typeDescription, name) : new Initializer.WithProperty(typeDescription, name, this.property));
    }

    @Override
    public void close() {
    }

    static {
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class)), new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class)), new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class), TypeDescription.ForLoadedType.of(Object.class)), new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class), JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub()));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class), TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(Permission[].class)), new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class), JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub(), TypeDescription.ForLoadedType.of(Permission[].class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class), TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(Permission[].class)), new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedAction.class), JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub(), TypeDescription.ForLoadedType.of(Permission[].class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class)), new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class)), new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class), TypeDescription.ForLoadedType.of(Object.class)), new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class), JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub()));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class), TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(Permission[].class)), new MethodDescription.SignatureToken("doPrivileged", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class), JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub(), TypeDescription.ForLoadedType.of(Permission[].class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class), TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(Permission[].class)), new MethodDescription.SignatureToken("doPrivilegedWithCombiner", TypeDescription.ForLoadedType.of(Object.class), TypeDescription.ForLoadedType.of(PrivilegedExceptionAction.class), JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub(), TypeDescription.ForLoadedType.of(Permission[].class)));
        SIGNATURES.put(new MethodDescription.SignatureToken("getContext", TypeDescription.ForLoadedType.of(Object.class), new TypeDescription[0]), new MethodDescription.SignatureToken("getContext", JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub(), new TypeDescription[0]));
        SIGNATURES.put(new MethodDescription.SignatureToken("checkPermission", TypeDescription.ForLoadedType.of(Void.TYPE), TypeDescription.ForLoadedType.of(Permission.class)), new MethodDescription.SignatureToken("checkPermission", TypeDescription.ForLoadedType.of(Void.TYPE), TypeDescription.ForLoadedType.of(Permission.class)));
    }

    @Override
    public boolean equals(@MaybeNull Object object) {
        block11: {
            block10: {
                String string;
                block9: {
                    String string2;
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
                    String string3 = ((AccessControllerPlugin)object).property;
                    string = string2 = this.property;
                    if (string3 == null) break block9;
                    if (string == null) break block10;
                    if (!string2.equals(string3)) {
                        return false;
                    }
                    break block11;
                }
                if (string == null) break block11;
            }
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int n = super.hashCode() * 31;
        String string = this.property;
        if (string != null) {
            n = n + string.hashCode();
        }
        return n;
    }

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Enhance {
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class AccessControlWrapper
    implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
        private final String name;

        protected AccessControlWrapper(String name) {
            this.name = name;
        }

        public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
            MethodDescription.SignatureToken token = (MethodDescription.SignatureToken)SIGNATURES.get(((MethodDescription.InDefinedShape)instrumentedMethod.asDefined()).asSignatureToken());
            if (token == null) {
                throw new IllegalStateException(instrumentedMethod + " does not have a method with a matching signature in " + AccessControllerPlugin.ACCESS_CONTROLLER);
            }
            if (instrumentedMethod.isPublic() || instrumentedMethod.isProtected()) {
                throw new IllegalStateException(instrumentedMethod + " is either public or protected what is not permitted to avoid context leaks");
            }
            return new PrefixingMethodVisitor(methodVisitor, instrumentedType, token, this.name, instrumentedMethod.isStatic() ? 0 : 1, implementationContext.getFrameGeneration());
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
            return this.name.equals(((AccessControlWrapper)object).name);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.name.hashCode();
        }

        protected static class PrefixingMethodVisitor
        extends MethodVisitor {
            private final TypeDescription instrumentedType;
            private final MethodDescription.SignatureToken token;
            private final String name;
            private final int offset;
            private final Implementation.Context.FrameGeneration frameGeneration;

            protected PrefixingMethodVisitor(MethodVisitor methodVisitor, TypeDescription instrumentedType, MethodDescription.SignatureToken token, String name, int offset, Implementation.Context.FrameGeneration frameGeneration) {
                super(OpenedClassReader.ASM_API, methodVisitor);
                this.instrumentedType = instrumentedType;
                this.token = token;
                this.name = name;
                this.offset = offset;
                this.frameGeneration = frameGeneration;
            }

            public void visitCode() {
                this.mv.visitCode();
                this.mv.visitFieldInsn(178, this.instrumentedType.getInternalName(), this.name, Type.getDescriptor(Boolean.TYPE));
                Label label = new Label();
                this.mv.visitJumpInsn(153, label);
                int offset = this.offset;
                for (TypeDescription typeDescription : this.token.getParameterTypes()) {
                    this.mv.visitVarInsn(Type.getType(typeDescription.getDescriptor()).getOpcode(21), offset);
                    if (typeDescription.equals(JavaType.ACCESS_CONTROL_CONTEXT.getTypeStub())) {
                        this.mv.visitTypeInsn(192, typeDescription.getInternalName());
                    }
                    offset += typeDescription.getStackSize().getSize();
                }
                this.mv.visitMethodInsn(184, AccessControllerPlugin.ACCESS_CONTROLLER.replace('.', '/'), this.token.getName(), this.token.getDescriptor(), false);
                this.mv.visitInsn(Type.getType(this.token.getReturnType().getDescriptor()).getOpcode(172));
                this.mv.visitLabel(label);
                this.frameGeneration.same(this.mv, this.token.getParameterTypes());
            }

            public void visitMaxs(int stackSize, int localVariableLength) {
                this.mv.visitMaxs(Math.max(Math.max(StackSize.of(this.token.getParameterTypes()), this.token.getReturnType().getStackSize().getSize()), stackSize), localVariableLength);
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static abstract class Initializer
    implements ByteCodeAppender {
        private final TypeDescription instrumentedType;
        private final String name;

        protected Initializer(TypeDescription instrumentedType, String name) {
            this.instrumentedType = instrumentedType;
            this.name = name;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label start = new Label();
            Label end = new Label();
            Label classNotFound = new Label();
            Label securityException = new Label();
            Label complete = new Label();
            methodVisitor.visitTryCatchBlock(start, end, classNotFound, Type.getInternalName(ClassNotFoundException.class));
            methodVisitor.visitTryCatchBlock(start, end, securityException, Type.getInternalName(SecurityException.class));
            methodVisitor.visitLabel(start);
            methodVisitor.visitLdcInsn(AccessControllerPlugin.ACCESS_CONTROLLER);
            methodVisitor.visitInsn(3);
            methodVisitor.visitInsn(1);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Class.class), "forName", Type.getMethodDescriptor(Type.getType(Class.class), Type.getType(String.class), Type.getType(Boolean.TYPE), Type.getType(ClassLoader.class)), false);
            methodVisitor.visitInsn(87);
            int size = this.onAccessController(methodVisitor);
            methodVisitor.visitFieldInsn(179, this.instrumentedType.getInternalName(), this.name, Type.getDescriptor(Boolean.TYPE));
            methodVisitor.visitLabel(end);
            methodVisitor.visitJumpInsn(167, complete);
            methodVisitor.visitLabel(classNotFound);
            implementationContext.getFrameGeneration().same1(methodVisitor, TypeDescription.ForLoadedType.of(ClassNotFoundException.class), Collections.emptyList());
            methodVisitor.visitInsn(87);
            methodVisitor.visitInsn(3);
            methodVisitor.visitFieldInsn(179, this.instrumentedType.getInternalName(), this.name, Type.getDescriptor(Boolean.TYPE));
            methodVisitor.visitJumpInsn(167, complete);
            methodVisitor.visitLabel(securityException);
            implementationContext.getFrameGeneration().same1(methodVisitor, TypeDescription.ForLoadedType.of(SecurityException.class), Collections.emptyList());
            methodVisitor.visitInsn(87);
            methodVisitor.visitInsn(4);
            methodVisitor.visitFieldInsn(179, this.instrumentedType.getInternalName(), this.name, Type.getDescriptor(Boolean.TYPE));
            methodVisitor.visitLabel(complete);
            implementationContext.getFrameGeneration().same(methodVisitor, Collections.emptyList());
            return new ByteCodeAppender.Size(Math.max(3, size), 0);
        }

        protected abstract int onAccessController(MethodVisitor var1);

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
            if (!this.name.equals(((Initializer)object).name)) {
                return false;
            }
            return this.instrumentedType.equals(((Initializer)object).instrumentedType);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.name.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class WithoutProperty
        extends Initializer {
            protected WithoutProperty(TypeDescription instrumentedType, String name) {
                super(instrumentedType, name);
            }

            protected int onAccessController(MethodVisitor methodVisitor) {
                methodVisitor.visitInsn(4);
                return 1;
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
                return this.getClass() == object.getClass();
            }

            public int hashCode() {
                return super.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class WithProperty
        extends Initializer {
            private final String property;

            protected WithProperty(TypeDescription instrumentedType, String name, String property) {
                super(instrumentedType, name);
                this.property = property;
            }

            protected int onAccessController(MethodVisitor methodVisitor) {
                methodVisitor.visitLdcInsn(this.property);
                methodVisitor.visitLdcInsn("true");
                methodVisitor.visitMethodInsn(184, Type.getInternalName(System.class), "getProperty", Type.getMethodDescriptor(Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)), false);
                methodVisitor.visitMethodInsn(184, Type.getInternalName(Boolean.class), "parseBoolean", Type.getMethodDescriptor(Type.getType(Boolean.TYPE), Type.getType(String.class)), false);
                return 2;
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
                return this.property.equals(((WithProperty)object).property);
            }

            public int hashCode() {
                return super.hashCode() * 31 + this.property.hashCode();
            }
        }
    }
}


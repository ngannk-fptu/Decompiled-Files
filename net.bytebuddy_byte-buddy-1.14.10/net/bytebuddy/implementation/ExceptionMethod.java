/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.Throw;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ExceptionMethod
implements Implementation,
ByteCodeAppender {
    private final ConstructionDelegate constructionDelegate;

    public ExceptionMethod(ConstructionDelegate constructionDelegate) {
        this.constructionDelegate = constructionDelegate;
    }

    public static Implementation throwing(Class<? extends Throwable> throwableType) {
        return ExceptionMethod.throwing(TypeDescription.ForLoadedType.of(throwableType));
    }

    public static Implementation throwing(TypeDescription throwableType) {
        if (!throwableType.isAssignableTo(Throwable.class)) {
            throw new IllegalArgumentException(throwableType + " does not extend throwable");
        }
        return new ExceptionMethod(new ConstructionDelegate.ForDefaultConstructor(throwableType));
    }

    public static Implementation throwing(Class<? extends Throwable> throwableType, String message) {
        return ExceptionMethod.throwing(TypeDescription.ForLoadedType.of(throwableType), message);
    }

    public static Implementation throwing(TypeDescription throwableType, String message) {
        if (!throwableType.isAssignableTo(Throwable.class)) {
            throw new IllegalArgumentException(throwableType + " does not extend throwable");
        }
        return new ExceptionMethod(new ConstructionDelegate.ForStringConstructor(throwableType, message));
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
        StackManipulation.Size stackSize = new StackManipulation.Compound(this.constructionDelegate.make(), Throw.INSTANCE).apply(methodVisitor, implementationContext);
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
        return this.constructionDelegate.equals(((ExceptionMethod)object).constructionDelegate);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.constructionDelegate.hashCode();
    }

    public static interface ConstructionDelegate {
        public StackManipulation make();

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForStringConstructor
        implements ConstructionDelegate {
            private final TypeDescription throwableType;
            private final MethodDescription targetConstructor;
            private final String message;

            public ForStringConstructor(TypeDescription throwableType, String message) {
                this.throwableType = throwableType;
                this.targetConstructor = (MethodDescription)((MethodList)throwableType.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(String.class)))).getOnly();
                this.message = message;
            }

            public StackManipulation make() {
                return new StackManipulation.Compound(TypeCreation.of(this.throwableType), Duplication.SINGLE, new TextConstant(this.message), MethodInvocation.invoke(this.targetConstructor));
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
                if (!this.message.equals(((ForStringConstructor)object).message)) {
                    return false;
                }
                if (!this.throwableType.equals(((ForStringConstructor)object).throwableType)) {
                    return false;
                }
                return this.targetConstructor.equals(((ForStringConstructor)object).targetConstructor);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.throwableType.hashCode()) * 31 + this.targetConstructor.hashCode()) * 31 + this.message.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForDefaultConstructor
        implements ConstructionDelegate {
            private final TypeDescription throwableType;
            private final MethodDescription targetConstructor;

            public ForDefaultConstructor(TypeDescription throwableType) {
                this.throwableType = throwableType;
                this.targetConstructor = (MethodDescription)((MethodList)throwableType.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(0)))).getOnly();
            }

            public StackManipulation make() {
                return new StackManipulation.Compound(TypeCreation.of(this.throwableType), Duplication.SINGLE, MethodInvocation.invoke(this.targetConstructor));
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
                if (!this.throwableType.equals(((ForDefaultConstructor)object).throwableType)) {
                    return false;
                }
                return this.targetConstructor.equals(((ForDefaultConstructor)object).targetConstructor);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.throwableType.hashCode()) * 31 + this.targetConstructor.hashCode();
            }
        }
    }
}


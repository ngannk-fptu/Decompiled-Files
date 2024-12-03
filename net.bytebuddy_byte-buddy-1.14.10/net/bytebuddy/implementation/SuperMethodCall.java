/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum SuperMethodCall implements Implementation.Composable
{
    INSTANCE;


    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new Appender(implementationTarget, Appender.TerminationHandler.RETURNING);
    }

    @Override
    public Implementation andThen(Implementation implementation) {
        return new Implementation.Compound(WithoutReturn.INSTANCE, implementation);
    }

    @Override
    public Implementation.Composable andThen(Implementation.Composable implementation) {
        return new Implementation.Compound.Composable(WithoutReturn.INSTANCE, implementation);
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final Implementation.Target implementationTarget;
        private final TerminationHandler terminationHandler;

        protected Appender(Implementation.Target implementationTarget, TerminationHandler terminationHandler) {
            this.implementationTarget = implementationTarget;
            this.terminationHandler = terminationHandler;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Implementation.SpecialMethodInvocation superMethodCall = this.implementationTarget.invokeDominant(instrumentedMethod.asSignatureToken()).withCheckedCompatibilityTo(instrumentedMethod.asTypeToken());
            if (!superMethodCall.isValid()) {
                throw new IllegalStateException("Cannot call super (or default) method for " + instrumentedMethod);
            }
            StackManipulation.Size size = new StackManipulation.Compound(MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), superMethodCall, this.terminationHandler.of(instrumentedMethod)).apply(methodVisitor, implementationContext);
            return new ByteCodeAppender.Size(size.getMaximalSize(), instrumentedMethod.getStackSize());
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
            if (!this.terminationHandler.equals((Object)((Appender)object).terminationHandler)) {
                return false;
            }
            return this.implementationTarget.equals(((Appender)object).implementationTarget);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.implementationTarget.hashCode()) * 31 + this.terminationHandler.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static enum TerminationHandler {
            RETURNING{

                protected StackManipulation of(MethodDescription methodDescription) {
                    return MethodReturn.of(methodDescription.getReturnType());
                }
            }
            ,
            DROPPING{

                protected StackManipulation of(MethodDescription methodDescription) {
                    return Removal.of(methodDescription.getReturnType());
                }
            };


            protected abstract StackManipulation of(MethodDescription var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum WithoutReturn implements Implementation
    {
        INSTANCE;


        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new Appender(implementationTarget, Appender.TerminationHandler.DROPPING);
        }
    }
}


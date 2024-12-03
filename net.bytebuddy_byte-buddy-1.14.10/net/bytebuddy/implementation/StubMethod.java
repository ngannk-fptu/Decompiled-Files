/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum StubMethod implements Implementation.Composable,
ByteCodeAppender
{
    INSTANCE;


    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return this;
    }

    @Override
    public Implementation andThen(Implementation implementation) {
        return implementation;
    }

    @Override
    public Implementation.Composable andThen(Implementation.Composable implementation) {
        return implementation;
    }

    @Override
    public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
        StackManipulation.Size stackSize = new StackManipulation.Compound(DefaultValue.of(instrumentedMethod.getReturnType()), MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext);
        return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
    }
}


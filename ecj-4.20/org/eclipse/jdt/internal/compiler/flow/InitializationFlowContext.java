/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class InitializationFlowContext
extends ExceptionHandlingFlowContext {
    public int exceptionCount;
    public TypeBinding[] thrownExceptions = new TypeBinding[5];
    public ASTNode[] exceptionThrowers = new ASTNode[5];
    public FlowInfo[] exceptionThrowerFlowInfos = new FlowInfo[5];
    public FlowInfo initsBeforeContext;

    public InitializationFlowContext(FlowContext parent, ASTNode associatedNode, FlowInfo initsBeforeContext, FlowContext initializationParent, BlockScope scope) {
        super(parent, associatedNode, Binding.NO_EXCEPTIONS, initializationParent, scope, FlowInfo.DEAD_END);
        this.initsBeforeContext = initsBeforeContext;
    }

    public void checkInitializerExceptions(BlockScope currentScope, FlowContext initializerContext, FlowInfo flowInfo) {
        int i = 0;
        while (i < this.exceptionCount) {
            initializerContext.checkExceptionHandlers(this.thrownExceptions[i], this.exceptionThrowers[i], this.exceptionThrowerFlowInfos[i], currentScope);
            ++i;
        }
    }

    @Override
    public FlowContext getInitializationContext() {
        return this;
    }

    @Override
    public String individualToString() {
        StringBuffer buffer = new StringBuffer("Initialization flow context");
        int i = 0;
        while (i < this.exceptionCount) {
            buffer.append('[').append(this.thrownExceptions[i].readableName());
            buffer.append('-').append(this.exceptionThrowerFlowInfos[i].toString()).append(']');
            ++i;
        }
        return buffer.toString();
    }

    @Override
    public void recordHandlingException(ReferenceBinding exceptionType, UnconditionalFlowInfo flowInfo, TypeBinding raisedException, TypeBinding caughtException, ASTNode invocationSite, boolean wasMasked) {
        int size = this.thrownExceptions.length;
        if (this.exceptionCount == size) {
            this.thrownExceptions = new TypeBinding[size * 2];
            System.arraycopy(this.thrownExceptions, 0, this.thrownExceptions, 0, size);
            this.exceptionThrowers = new ASTNode[size * 2];
            System.arraycopy(this.exceptionThrowers, 0, this.exceptionThrowers, 0, size);
            this.exceptionThrowerFlowInfos = new FlowInfo[size * 2];
            System.arraycopy(this.exceptionThrowerFlowInfos, 0, this.exceptionThrowerFlowInfos, 0, size);
        }
        this.thrownExceptions[this.exceptionCount] = raisedException;
        this.exceptionThrowers[this.exceptionCount] = invocationSite;
        this.exceptionThrowerFlowInfos[this.exceptionCount++] = flowInfo.copy();
    }
}


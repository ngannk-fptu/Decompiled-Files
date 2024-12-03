/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.TryFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;

public class InsideSubRoutineFlowContext
extends TryFlowContext {
    public UnconditionalFlowInfo initsOnReturn = FlowInfo.DEAD_END;

    public InsideSubRoutineFlowContext(FlowContext parent, ASTNode associatedNode) {
        super(parent, associatedNode);
    }

    @Override
    public String individualToString() {
        StringBuffer buffer = new StringBuffer("Inside SubRoutine flow context");
        buffer.append("[initsOnReturn -").append(this.initsOnReturn.toString()).append(']');
        return buffer.toString();
    }

    @Override
    public UnconditionalFlowInfo initsOnReturn() {
        return this.initsOnReturn;
    }

    @Override
    public boolean isNonReturningContext() {
        return ((SubRoutineStatement)this.associatedNode).isSubRoutineEscaping();
    }

    @Override
    public void recordReturnFrom(UnconditionalFlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            this.initsOnReturn = this.initsOnReturn == FlowInfo.DEAD_END ? (UnconditionalFlowInfo)flowInfo.copy() : this.initsOnReturn.mergedWith(flowInfo);
        }
    }

    @Override
    public SubRoutineStatement subroutine() {
        return (SubRoutineStatement)this.associatedNode;
    }
}


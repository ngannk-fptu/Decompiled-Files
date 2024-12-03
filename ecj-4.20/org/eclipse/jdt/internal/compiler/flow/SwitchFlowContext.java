/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;

public class SwitchFlowContext
extends FlowContext {
    public BranchLabel breakLabel;
    public UnconditionalFlowInfo initsOnBreak = FlowInfo.DEAD_END;
    public boolean isExpression = false;

    public SwitchFlowContext(FlowContext parent, ASTNode associatedNode, BranchLabel breakLabel, boolean isPreTest, boolean inheritNullFieldChecks) {
        super(parent, associatedNode, inheritNullFieldChecks);
        this.breakLabel = breakLabel;
        if (isPreTest && parent.conditionalLevel > -1) {
            ++this.conditionalLevel;
        }
    }

    @Override
    public BranchLabel breakLabel() {
        return this.breakLabel;
    }

    @Override
    public String individualToString() {
        StringBuffer buffer = new StringBuffer("Switch flow context");
        buffer.append("[initsOnBreak -").append(this.initsOnBreak.toString()).append(']');
        return buffer.toString();
    }

    @Override
    public boolean isBreakable() {
        return true;
    }

    @Override
    public void recordBreakFrom(FlowInfo flowInfo) {
        this.initsOnBreak = (this.initsOnBreak.tagBits & 1) == 0 ? this.initsOnBreak.mergedWith(flowInfo.unconditionalInits()) : flowInfo.unconditionalCopy();
    }
}


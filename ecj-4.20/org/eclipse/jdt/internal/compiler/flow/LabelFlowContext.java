/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class LabelFlowContext
extends SwitchFlowContext {
    public char[] labelName;

    public LabelFlowContext(FlowContext parent, ASTNode associatedNode, char[] labelName, BranchLabel breakLabel, BlockScope scope) {
        super(parent, associatedNode, breakLabel, false, true);
        this.labelName = labelName;
        this.checkLabelValidity(scope);
    }

    void checkLabelValidity(BlockScope scope) {
        FlowContext current = this.getLocalParent();
        while (current != null) {
            char[] currentLabelName = current.labelName();
            if (currentLabelName != null && CharOperation.equals(currentLabelName, this.labelName)) {
                scope.problemReporter().alreadyDefinedLabel(this.labelName, this.associatedNode);
            }
            current = current.getLocalParent();
        }
    }

    @Override
    public String individualToString() {
        return "Label flow context [label:" + String.valueOf(this.labelName) + "]";
    }

    @Override
    public char[] labelName() {
        return this.labelName;
    }
}


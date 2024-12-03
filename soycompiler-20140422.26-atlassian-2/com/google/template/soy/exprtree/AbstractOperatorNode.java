/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractParentExprNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.Operator;
import java.util.List;

public abstract class AbstractOperatorNode
extends AbstractParentExprNode
implements ExprNode.OperatorNode {
    private final Operator operator;

    public AbstractOperatorNode(Operator operator) {
        this.operator = operator;
    }

    protected AbstractOperatorNode(AbstractOperatorNode orig) {
        super(orig);
        this.operator = orig.operator;
    }

    @Override
    public Operator getOperator() {
        return this.operator;
    }

    @Override
    public String toSourceString() {
        boolean isLeftAssociative = this.operator.getAssociativity() == Operator.Associativity.LEFT;
        StringBuilder sourceSb = new StringBuilder();
        List<Operator.SyntaxElement> syntax = this.operator.getSyntax();
        int n = syntax.size();
        for (int i = 0; i < n; ++i) {
            Operator.SyntaxElement syntaxEl = syntax.get(i);
            if (syntaxEl instanceof Operator.Operand) {
                Operator.Operand operand = (Operator.Operand)syntaxEl;
                if (i == (isLeftAssociative ? 0 : n - 1)) {
                    sourceSb.append(this.getOperandProtectedForLowerPrec(operand.getIndex()));
                    continue;
                }
                sourceSb.append(this.getOperandProtectedForLowerOrEqualPrec(operand.getIndex()));
                continue;
            }
            if (syntaxEl instanceof Operator.Token) {
                sourceSb.append(((Operator.Token)syntaxEl).getValue());
                continue;
            }
            if (syntaxEl instanceof Operator.Spacer) {
                sourceSb.append(' ');
                continue;
            }
            throw new AssertionError();
        }
        return sourceSb.toString();
    }

    private String getOperandProtectedForLowerPrec(int index) {
        return this.getOperandProtectedForPrecHelper(index, false);
    }

    private String getOperandProtectedForLowerOrEqualPrec(int index) {
        return this.getOperandProtectedForPrecHelper(index, true);
    }

    private String getOperandProtectedForPrecHelper(int index, boolean shouldProtectEqualPrec) {
        boolean shouldProtect;
        int thisOpPrec = this.operator.getPrecedence();
        ExprNode child = this.getChild(index);
        if (child instanceof ExprNode.OperatorNode) {
            int childOpPrec = ((ExprNode.OperatorNode)child).getOperator().getPrecedence();
            shouldProtect = shouldProtectEqualPrec ? childOpPrec <= thisOpPrec : childOpPrec < thisOpPrec;
        } else {
            shouldProtect = false;
        }
        if (shouldProtect) {
            return "(" + child.toSourceString() + ")";
        }
        return child.toSourceString();
    }
}


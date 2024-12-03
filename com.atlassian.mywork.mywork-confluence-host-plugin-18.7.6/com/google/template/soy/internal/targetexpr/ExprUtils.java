/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.internal.targetexpr;

import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.internal.targetexpr.TargetExpr;
import java.util.List;

public class ExprUtils {
    private ExprUtils() {
    }

    public static String genExprWithNewToken(Operator op, List<? extends TargetExpr> operandExprs, String newToken) {
        int opPrec = op.getPrecedence();
        boolean isLeftAssociative = op.getAssociativity() == Operator.Associativity.LEFT;
        StringBuilder exprSb = new StringBuilder();
        List<Operator.SyntaxElement> syntax = op.getSyntax();
        int n = syntax.size();
        for (int i = 0; i < n; ++i) {
            Operator.SyntaxElement syntaxEl = syntax.get(i);
            if (syntaxEl instanceof Operator.Operand) {
                int operandIndex = ((Operator.Operand)syntaxEl).getIndex();
                TargetExpr operandExpr = operandExprs.get(operandIndex);
                boolean needsProtection = i == (isLeftAssociative ? 0 : n - 1) ? operandExpr.getPrecedence() < opPrec : operandExpr.getPrecedence() <= opPrec;
                String subexpr = needsProtection ? "(" + operandExpr.getText() + ")" : operandExpr.getText();
                exprSb.append(subexpr);
                continue;
            }
            if (syntaxEl instanceof Operator.Token) {
                if (newToken != null) {
                    exprSb.append(newToken);
                    continue;
                }
                exprSb.append(((Operator.Token)syntaxEl).getValue());
                continue;
            }
            if (syntaxEl instanceof Operator.Spacer) {
                exprSb.append(' ');
                continue;
            }
            throw new AssertionError();
        }
        return exprSb.toString();
    }
}


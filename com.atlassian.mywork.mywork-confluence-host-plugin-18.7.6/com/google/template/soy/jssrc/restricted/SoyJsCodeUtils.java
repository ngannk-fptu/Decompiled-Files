/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.jssrc.restricted;

import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.internal.targetexpr.ExprUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import java.util.List;

public class SoyJsCodeUtils {
    private SoyJsCodeUtils() {
    }

    public static JsExpr genJsExprUsingSoySyntax(Operator op, List<JsExpr> operandJsExprs) {
        return SoyJsCodeUtils.genJsExprUsingSoySyntaxWithNewToken(op, operandJsExprs, null);
    }

    public static JsExpr genJsExprUsingSoySyntaxWithNewToken(Operator op, List<JsExpr> operandJsExprs, String newToken) {
        return new JsExpr(ExprUtils.genExprWithNewToken(op, operandJsExprs, newToken), op.getPrecedence());
    }
}


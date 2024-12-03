/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.jssrc.internal.TranslateToJsExprVisitor;
import com.google.template.soy.jssrc.internal.V1JsExprTranslator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.internal.NonpluginFunction;
import java.util.Deque;
import java.util.Map;

class JsExprTranslator {
    private final Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap;
    private final TranslateToJsExprVisitor.TranslateToJsExprVisitorFactory translateToJsExprVisitorFactory;

    @Inject
    JsExprTranslator(Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap, TranslateToJsExprVisitor.TranslateToJsExprVisitorFactory translateToJsExprVisitorFactory) {
        this.soyJsSrcFunctionsMap = soyJsSrcFunctionsMap;
        this.translateToJsExprVisitorFactory = translateToJsExprVisitorFactory;
    }

    public JsExpr translateToJsExpr(ExprNode expr, String exprText, Deque<Map<String, JsExpr>> localVarTranslations) {
        if (expr != null && (exprText == null || new CheckAllFunctionsSupportedVisitor(this.soyJsSrcFunctionsMap).exec(expr).booleanValue())) {
            return (JsExpr)this.translateToJsExprVisitorFactory.create(localVarTranslations).exec(expr);
        }
        Preconditions.checkNotNull((Object)exprText);
        return V1JsExprTranslator.translateToJsExpr(exprText, localVarTranslations);
    }

    private static class CheckAllFunctionsSupportedVisitor
    extends AbstractExprNodeVisitor<Boolean> {
        private final Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap;
        private boolean areAllFunctionsSupported;

        public CheckAllFunctionsSupportedVisitor(Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap) {
            this.soyJsSrcFunctionsMap = soyJsSrcFunctionsMap;
        }

        @Override
        public Boolean exec(ExprNode node) {
            this.areAllFunctionsSupported = true;
            this.visit(node);
            return this.areAllFunctionsSupported;
        }

        @Override
        protected void visitFunctionNode(FunctionNode node) {
            String fnName = node.getFunctionName();
            if (NonpluginFunction.forFunctionName(fnName) == null && !this.soyJsSrcFunctionsMap.containsKey(fnName)) {
                this.areAllFunctionsSupported = false;
                return;
            }
            this.visitChildren(node);
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                if (!this.areAllFunctionsSupported) {
                    return;
                }
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}


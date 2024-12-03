/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.google.template.soy.parsepasses;

import com.google.common.collect.ImmutableMap;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.shared.internal.NonpluginFunction;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import java.util.Map;
import java.util.Set;

public class CheckFunctionCallsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final Map<String, SoyFunction> soyFunctionsByName;
    private SyntaxVersion declaredSyntaxVersion;

    @AssistedInject
    public CheckFunctionCallsVisitor(Map<String, SoyFunction> soyFunctionsByName, @Assisted SyntaxVersion declaredSyntaxVersion) {
        this.soyFunctionsByName = ImmutableMap.copyOf(soyFunctionsByName);
        this.declaredSyntaxVersion = declaredSyntaxVersion;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ExprHolderNode) {
            for (ExprUnion exprUnion : ((SoyNode.ExprHolderNode)node).getAllExprUnions()) {
                if (exprUnion.getExpr() == null) continue;
                try {
                    new CheckFunctionCallsExprVisitor((SoyNode.ExprHolderNode)node).exec(exprUnion.getExpr());
                }
                catch (SoySyntaxException ex) {
                    throw SoySyntaxExceptionUtils.associateNode(ex, node);
                }
            }
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private final class CheckFunctionCallsExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SoyNode.ExprHolderNode container;

        CheckFunctionCallsExprVisitor(SoyNode.ExprHolderNode container) {
            this.container = container;
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }

        @Override
        protected void visitFunctionNode(FunctionNode node) {
            block11: {
                int numArgs;
                String fnName;
                block10: {
                    fnName = node.getFunctionName();
                    numArgs = node.numChildren();
                    NonpluginFunction nonpluginFn = NonpluginFunction.forFunctionName(fnName);
                    if (nonpluginFn == null) break block10;
                    if (numArgs != nonpluginFn.getNumArgs()) {
                        throw SoySyntaxException.createWithoutMetaInfo("Function '" + fnName + "' called with the wrong number of arguments (function call \"" + node.toSourceString() + "\").");
                    }
                    switch (nonpluginFn) {
                        case INDEX: 
                        case IS_FIRST: 
                        case IS_LAST: {
                            this.requireLoopVariableInScope(node, node.getChild(0));
                            break block11;
                        }
                        case QUOTE_KEYS_IF_JS: {
                            if (!(node.getChild(0) instanceof MapLiteralNode)) {
                                throw SoySyntaxException.createWithoutMetaInfo("Function quoteKeysIfJs() must have a map literal as its arg (encountered \"" + node.toSourceString() + "\").");
                            }
                            break block11;
                        }
                        default: {
                            throw new AssertionError((Object)("Unrecognized nonplugin fn " + (Object)((Object)nonpluginFn)));
                        }
                    }
                }
                SoyFunction pluginFn = (SoyFunction)CheckFunctionCallsVisitor.this.soyFunctionsByName.get(fnName);
                if (pluginFn != null) {
                    Set<Integer> arities = pluginFn.getValidArgsSizes();
                    if (!arities.contains(numArgs)) {
                        throw SoySyntaxException.createWithoutMetaInfo("Function '" + fnName + "' called with the wrong number of arguments (function call \"" + node.toSourceString() + "\").");
                    }
                } else if (CheckFunctionCallsVisitor.this.declaredSyntaxVersion != SyntaxVersion.V1_0) {
                    throw SoySyntaxException.createWithoutMetaInfo("Unrecognized function '" + fnName + "' (encountered function call \"" + node.toSourceString() + "\").");
                }
            }
            this.visitChildren(node);
        }

        private void requireLoopVariableInScope(FunctionNode fn, ExprNode loopVariable) {
            if (!this.isLoopVariableInScope(loopVariable)) {
                throw SoySyntaxException.createWithoutMetaInfo("Function '" + fn.getFunctionName() + "' must have a foreach loop variable as its argument (encountered \"" + fn.toSourceString() + "\").");
            }
        }

        private boolean isLoopVariableInScope(ExprNode loopVariable) {
            if (!(loopVariable instanceof VarRefNode)) {
                return false;
            }
            VarRefNode loopVariableRef = (VarRefNode)loopVariable;
            if (loopVariableRef.isInjected()) {
                return false;
            }
            String loopVariableName = loopVariableRef.getName();
            for (ParentNode ancestor = this.container.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
                String iteratorVariableName;
                if (!(ancestor instanceof ForeachNonemptyNode) || !loopVariableName.equals(iteratorVariableName = ((ForeachNonemptyNode)ancestor).getVarName())) continue;
                return true;
            }
            return false;
        }
    }

    public static interface CheckFunctionCallsVisitorFactory {
        public CheckFunctionCallsVisitor create(SyntaxVersion var1);
    }
}


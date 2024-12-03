/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.inject.Inject
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.jssrc.restricted.SoyLibraryAssistedJsSrcFunction;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoytreeUtils;
import java.util.Map;
import java.util.SortedSet;

class GenFunctionPluginRequiresVisitor {
    private final Map<String, SoyLibraryAssistedJsSrcFunction> soyLibraryAssistedJsSrcFunctionsMap;
    private SortedSet<String> requiredJsLibNames;

    @Inject
    public GenFunctionPluginRequiresVisitor(Map<String, SoyLibraryAssistedJsSrcFunction> soyLibraryAssistedJsSrcFunctionsMap) {
        this.soyLibraryAssistedJsSrcFunctionsMap = soyLibraryAssistedJsSrcFunctionsMap;
    }

    public SortedSet<String> exec(SoyFileNode soyFile) {
        this.requiredJsLibNames = Sets.newTreeSet();
        GenFunctionPluginRequiresHelperVisitor helperVisitor = new GenFunctionPluginRequiresHelperVisitor();
        SoytreeUtils.execOnAllV2Exprs(soyFile, helperVisitor);
        return this.requiredJsLibNames;
    }

    private class GenFunctionPluginRequiresHelperVisitor
    extends AbstractExprNodeVisitor<SortedSet<String>> {
        private GenFunctionPluginRequiresHelperVisitor() {
        }

        @Override
        protected void visitFunctionNode(FunctionNode node) {
            String functionName = node.getFunctionName();
            if (GenFunctionPluginRequiresVisitor.this.soyLibraryAssistedJsSrcFunctionsMap.containsKey(functionName)) {
                GenFunctionPluginRequiresVisitor.this.requiredJsLibNames.addAll(((SoyLibraryAssistedJsSrcFunction)GenFunctionPluginRequiresVisitor.this.soyLibraryAssistedJsSrcFunctionsMap.get(functionName)).getRequiredJsLibNames());
            }
            this.visitChildren(node);
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}


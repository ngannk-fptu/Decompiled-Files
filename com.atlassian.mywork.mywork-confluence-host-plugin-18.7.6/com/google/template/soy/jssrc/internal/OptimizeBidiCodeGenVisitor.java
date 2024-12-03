/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.jssrc.internal;

import com.google.inject.Inject;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.coredirectives.CoreDirectiveUtils;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.sharedpasses.CombineConsecutiveRawTextNodesVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.Map;

class OptimizeBidiCodeGenVisitor
extends AbstractSoyNodeVisitor<Void> {
    private static final String BIDI_MARK_FN_NAME = "bidiMark";
    private static final String BIDI_START_EDGE_FN_NAME = "bidiStartEdge";
    private static final String BIDI_END_EDGE_FN_NAME = "bidiEndEdge";
    private final Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap;
    private BidiGlobalDir bidiGlobalDir;
    private IdGenerator nodeIdGen;
    boolean madeReplacement;

    @Inject
    public OptimizeBidiCodeGenVisitor(Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap, BidiGlobalDir bidiGlobalDir) {
        this.soyJsSrcFunctionsMap = soyJsSrcFunctionsMap;
        this.bidiGlobalDir = bidiGlobalDir;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        if (!(this.soyJsSrcFunctionsMap.containsKey(BIDI_MARK_FN_NAME) && this.soyJsSrcFunctionsMap.containsKey(BIDI_START_EDGE_FN_NAME) && this.soyJsSrcFunctionsMap.containsKey(BIDI_END_EDGE_FN_NAME))) {
            return;
        }
        this.nodeIdGen = node.getNodeIdGenerator();
        this.madeReplacement = false;
        this.visitChildren(node);
        if (this.madeReplacement) {
            new CombineConsecutiveRawTextNodesVisitor().exec(node);
        }
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        String rawText;
        if (!node.couldHaveSyntaxVersionAtLeast(SyntaxVersion.V2_0) || node.getExprUnion().getExpr() == null) {
            return;
        }
        SoyNode.BlockNode parent = node.getParent();
        if (parent instanceof SoyNode.MsgBlockNode) {
            return;
        }
        Node expr = node.getExprUnion().getExpr().getChild(0);
        if (!(expr instanceof FunctionNode)) {
            return;
        }
        if (!this.bidiGlobalDir.isStaticValue()) {
            return;
        }
        String fnName = ((FunctionNode)expr).getFunctionName();
        if (fnName.equals(BIDI_MARK_FN_NAME)) {
            rawText = this.bidiGlobalDir.getStaticValue() < 0 ? "\\u200F" : "\\u200E";
        } else if (fnName.equals(BIDI_START_EDGE_FN_NAME)) {
            rawText = this.bidiGlobalDir.getStaticValue() < 0 ? "right" : "left";
        } else if (fnName.equals(BIDI_END_EDGE_FN_NAME)) {
            rawText = this.bidiGlobalDir.getStaticValue() < 0 ? "left" : "right";
        } else {
            return;
        }
        for (PrintDirectiveNode directiveNode : node.getChildren()) {
            if (CoreDirectiveUtils.isCoreDirective(directiveNode)) continue;
            return;
        }
        parent.replaceChild(node, new RawTextNode(this.nodeIdGen.genId(), rawText));
        this.madeReplacement = true;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }
}


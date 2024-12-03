/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.inject.Inject
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.shared.internal.ApiCallScope;
import com.google.template.soy.soytree.AbstractReturningSoyNodeVisitor;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamValueNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.DebuggerNode;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetNode;
import com.google.template.soy.soytree.LogNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.XidNode;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import com.google.template.soy.soytree.jssrc.GoogMsgRefNode;
import java.util.Map;

@ApiCallScope
class IsComputableAsJsExprsVisitor
extends AbstractReturningSoyNodeVisitor<Boolean> {
    private final SoyJsSrcOptions jsSrcOptions;
    private final Map<SoyNode, Boolean> memoizedResults;

    @Inject
    IsComputableAsJsExprsVisitor(SoyJsSrcOptions jsSrcOptions) {
        this.jsSrcOptions = jsSrcOptions;
        this.memoizedResults = Maps.newHashMap();
    }

    public Boolean execOnChildren(SoyNode.ParentSoyNode<?> node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visit(SoyNode node) {
        if (this.memoizedResults.containsKey(node)) {
            return this.memoizedResults.get(node);
        }
        Boolean result = (Boolean)super.visit(node);
        this.memoizedResults.put(node, result);
        return result;
    }

    @Override
    protected Boolean visitTemplateNode(TemplateNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitRawTextNode(RawTextNode node) {
        return true;
    }

    @Override
    protected Boolean visitGoogMsgDefNode(GoogMsgDefNode node) {
        return false;
    }

    @Override
    protected Boolean visitMsgPlaceholderNode(MsgPlaceholderNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitGoogMsgRefNode(GoogMsgRefNode node) {
        return true;
    }

    @Override
    protected Boolean visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitPrintNode(PrintNode node) {
        return true;
    }

    @Override
    protected Boolean visitXidNode(XidNode node) {
        return true;
    }

    @Override
    protected Boolean visitCssNode(CssNode node) {
        return true;
    }

    @Override
    protected Boolean visitLetNode(LetNode node) {
        return false;
    }

    @Override
    protected Boolean visitIfNode(IfNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitIfCondNode(IfCondNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitIfElseNode(IfElseNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitSwitchNode(SwitchNode node) {
        return false;
    }

    @Override
    protected Boolean visitForeachNode(ForeachNode node) {
        return false;
    }

    @Override
    protected Boolean visitForNode(ForNode node) {
        return false;
    }

    @Override
    protected Boolean visitCallNode(CallNode node) {
        return this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.CONCAT && this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitCallParamValueNode(CallParamValueNode node) {
        return true;
    }

    @Override
    protected Boolean visitCallParamContentNode(CallParamContentNode node) {
        return this.areChildrenComputableAsJsExprs(node);
    }

    @Override
    protected Boolean visitLogNode(LogNode node) {
        return false;
    }

    @Override
    protected Boolean visitDebuggerNode(DebuggerNode node) {
        return false;
    }

    private boolean areChildrenComputableAsJsExprs(SoyNode.ParentSoyNode<?> node) {
        for (SoyNode child : node.getChildren()) {
            if (child instanceof RawTextNode || child instanceof PrintNode || this.visit(child).booleanValue()) continue;
            return false;
        }
        return true;
    }
}


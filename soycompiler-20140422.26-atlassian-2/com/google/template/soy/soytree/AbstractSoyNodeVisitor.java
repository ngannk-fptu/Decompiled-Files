/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.basetree.AbstractNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CallParamValueNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.DebuggerNode;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachIfemptyNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.LetNode;
import com.google.template.soy.soytree.LetValueNode;
import com.google.template.soy.soytree.LogNode;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.MsgPluralCaseNode;
import com.google.template.soy.soytree.MsgPluralDefaultNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgPluralRemainderNode;
import com.google.template.soy.soytree.MsgSelectCaseNode;
import com.google.template.soy.soytree.MsgSelectDefaultNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SwitchCaseNode;
import com.google.template.soy.soytree.SwitchDefaultNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.XidNode;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import com.google.template.soy.soytree.jssrc.GoogMsgRefNode;

public abstract class AbstractSoyNodeVisitor<R>
extends AbstractNodeVisitor<SoyNode, R> {
    @Override
    protected void visit(SoyNode node) {
        switch (node.getKind()) {
            case SOY_FILE_SET_NODE: {
                this.visitSoyFileSetNode((SoyFileSetNode)node);
                break;
            }
            case SOY_FILE_NODE: {
                this.visitSoyFileNode((SoyFileNode)node);
                break;
            }
            case TEMPLATE_BASIC_NODE: {
                this.visitTemplateBasicNode((TemplateBasicNode)node);
                break;
            }
            case TEMPLATE_DELEGATE_NODE: {
                this.visitTemplateDelegateNode((TemplateDelegateNode)node);
                break;
            }
            case RAW_TEXT_NODE: {
                this.visitRawTextNode((RawTextNode)node);
                break;
            }
            case GOOG_MSG_DEF_NODE: {
                this.visitGoogMsgDefNode((GoogMsgDefNode)node);
                break;
            }
            case GOOG_MSG_REF_NODE: {
                this.visitGoogMsgRefNode((GoogMsgRefNode)node);
                break;
            }
            case MSG_FALLBACK_GROUP_NODE: {
                this.visitMsgFallbackGroupNode((MsgFallbackGroupNode)node);
                break;
            }
            case MSG_NODE: {
                this.visitMsgNode((MsgNode)node);
                break;
            }
            case MSG_PLURAL_NODE: {
                this.visitMsgPluralNode((MsgPluralNode)node);
                break;
            }
            case MSG_PLURAL_CASE_NODE: {
                this.visitMsgPluralCaseNode((MsgPluralCaseNode)node);
                break;
            }
            case MSG_PLURAL_DEFAULT_NODE: {
                this.visitMsgPluralDefaultNode((MsgPluralDefaultNode)node);
                break;
            }
            case MSG_PLURAL_REMAINDER_NODE: {
                this.visitMsgPluralRemainderNode((MsgPluralRemainderNode)node);
                break;
            }
            case MSG_SELECT_NODE: {
                this.visitMsgSelectNode((MsgSelectNode)node);
                break;
            }
            case MSG_SELECT_CASE_NODE: {
                this.visitMsgSelectCaseNode((MsgSelectCaseNode)node);
                break;
            }
            case MSG_SELECT_DEFAULT_NODE: {
                this.visitMsgSelectDefaultNode((MsgSelectDefaultNode)node);
                break;
            }
            case MSG_PLACEHOLDER_NODE: {
                this.visitMsgPlaceholderNode((MsgPlaceholderNode)node);
                break;
            }
            case MSG_HTML_TAG_NODE: {
                this.visitMsgHtmlTagNode((MsgHtmlTagNode)node);
                break;
            }
            case PRINT_NODE: {
                this.visitPrintNode((PrintNode)node);
                break;
            }
            case PRINT_DIRECTIVE_NODE: {
                this.visitPrintDirectiveNode((PrintDirectiveNode)node);
                break;
            }
            case CSS_NODE: {
                this.visitCssNode((CssNode)node);
                break;
            }
            case XID_NODE: {
                this.visitXidNode((XidNode)node);
                break;
            }
            case LET_VALUE_NODE: {
                this.visitLetValueNode((LetValueNode)node);
                break;
            }
            case LET_CONTENT_NODE: {
                this.visitLetContentNode((LetContentNode)node);
                break;
            }
            case IF_NODE: {
                this.visitIfNode((IfNode)node);
                break;
            }
            case IF_COND_NODE: {
                this.visitIfCondNode((IfCondNode)node);
                break;
            }
            case IF_ELSE_NODE: {
                this.visitIfElseNode((IfElseNode)node);
                break;
            }
            case SWITCH_NODE: {
                this.visitSwitchNode((SwitchNode)node);
                break;
            }
            case SWITCH_CASE_NODE: {
                this.visitSwitchCaseNode((SwitchCaseNode)node);
                break;
            }
            case SWITCH_DEFAULT_NODE: {
                this.visitSwitchDefaultNode((SwitchDefaultNode)node);
                break;
            }
            case FOREACH_NODE: {
                this.visitForeachNode((ForeachNode)node);
                break;
            }
            case FOREACH_NONEMPTY_NODE: {
                this.visitForeachNonemptyNode((ForeachNonemptyNode)node);
                break;
            }
            case FOREACH_IFEMPTY_NODE: {
                this.visitForeachIfemptyNode((ForeachIfemptyNode)node);
                break;
            }
            case FOR_NODE: {
                this.visitForNode((ForNode)node);
                break;
            }
            case CALL_BASIC_NODE: {
                this.visitCallBasicNode((CallBasicNode)node);
                break;
            }
            case CALL_DELEGATE_NODE: {
                this.visitCallDelegateNode((CallDelegateNode)node);
                break;
            }
            case CALL_PARAM_VALUE_NODE: {
                this.visitCallParamValueNode((CallParamValueNode)node);
                break;
            }
            case CALL_PARAM_CONTENT_NODE: {
                this.visitCallParamContentNode((CallParamContentNode)node);
                break;
            }
            case LOG_NODE: {
                this.visitLogNode((LogNode)node);
                break;
            }
            case DEBUGGER_NODE: {
                this.visitDebuggerNode((DebuggerNode)node);
                break;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    protected void visitChildren(SoyNode.ParentSoyNode<?> node) {
        ((AbstractNodeVisitor)this).visitChildren(node);
    }

    @Override
    protected void visitChildrenAllowingConcurrentModification(SoyNode.ParentSoyNode<?> node) {
        ((AbstractNodeVisitor)this).visitChildrenAllowingConcurrentModification(node);
    }

    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.visitSoyNode(node);
    }

    protected void visitSoyFileNode(SoyFileNode node) {
        this.visitSoyNode(node);
    }

    protected void visitTemplateBasicNode(TemplateBasicNode node) {
        this.visitTemplateNode(node);
    }

    protected void visitTemplateDelegateNode(TemplateDelegateNode node) {
        this.visitTemplateNode(node);
    }

    protected void visitTemplateNode(TemplateNode node) {
        this.visitSoyNode(node);
    }

    protected void visitRawTextNode(RawTextNode node) {
        this.visitSoyNode(node);
    }

    protected void visitGoogMsgDefNode(GoogMsgDefNode node) {
        this.visitSoyNode(node);
    }

    protected void visitGoogMsgRefNode(GoogMsgRefNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgNode(MsgNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgPluralNode(MsgPluralNode node) {
        this.visitMsgSubstUnitNode(node);
    }

    protected void visitMsgPluralCaseNode(MsgPluralCaseNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgPluralDefaultNode(MsgPluralDefaultNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgPluralRemainderNode(MsgPluralRemainderNode node) {
        this.visitMsgSubstUnitNode(node);
    }

    protected void visitMsgSelectNode(MsgSelectNode node) {
        this.visitMsgSubstUnitNode(node);
    }

    protected void visitMsgSelectCaseNode(MsgSelectCaseNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgSelectDefaultNode(MsgSelectDefaultNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgPlaceholderNode(MsgPlaceholderNode node) {
        this.visitMsgSubstUnitNode(node);
    }

    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        this.visitSoyNode(node);
    }

    protected void visitMsgSubstUnitNode(SoyNode.MsgSubstUnitNode node) {
        this.visitSoyNode(node);
    }

    protected void visitPrintNode(PrintNode node) {
        this.visitSoyNode(node);
    }

    protected void visitPrintDirectiveNode(PrintDirectiveNode node) {
        this.visitSoyNode(node);
    }

    protected void visitCssNode(CssNode node) {
        this.visitSoyNode(node);
    }

    protected void visitXidNode(XidNode node) {
        this.visitSoyNode(node);
    }

    protected void visitLetValueNode(LetValueNode node) {
        this.visitLetNode(node);
    }

    protected void visitLetContentNode(LetContentNode node) {
        this.visitLetNode(node);
    }

    protected void visitLetNode(LetNode node) {
        this.visitSoyNode(node);
    }

    protected void visitIfNode(IfNode node) {
        this.visitSoyNode(node);
    }

    protected void visitIfCondNode(IfCondNode node) {
        this.visitSoyNode(node);
    }

    protected void visitIfElseNode(IfElseNode node) {
        this.visitSoyNode(node);
    }

    protected void visitSwitchNode(SwitchNode node) {
        this.visitSoyNode(node);
    }

    protected void visitSwitchCaseNode(SwitchCaseNode node) {
        this.visitSoyNode(node);
    }

    protected void visitSwitchDefaultNode(SwitchDefaultNode node) {
        this.visitSoyNode(node);
    }

    protected void visitForeachNode(ForeachNode node) {
        this.visitSoyNode(node);
    }

    protected void visitForeachIfemptyNode(ForeachIfemptyNode node) {
        this.visitSoyNode(node);
    }

    protected void visitForeachNonemptyNode(ForeachNonemptyNode node) {
        this.visitLoopNode(node);
    }

    protected void visitForNode(ForNode node) {
        this.visitLoopNode(node);
    }

    protected void visitLoopNode(SoyNode.LoopNode node) {
        this.visitSoyNode(node);
    }

    protected void visitCallBasicNode(CallBasicNode node) {
        this.visitCallNode(node);
    }

    protected void visitCallDelegateNode(CallDelegateNode node) {
        this.visitCallNode(node);
    }

    protected void visitCallNode(CallNode node) {
        this.visitSoyNode(node);
    }

    protected void visitCallParamValueNode(CallParamValueNode node) {
        this.visitCallParamNode(node);
    }

    protected void visitCallParamContentNode(CallParamContentNode node) {
        this.visitCallParamNode(node);
    }

    protected void visitCallParamNode(CallParamNode node) {
        this.visitSoyNode(node);
    }

    protected void visitLogNode(LogNode node) {
        this.visitSoyNode(node);
    }

    protected void visitDebuggerNode(DebuggerNode node) {
        this.visitSoyNode(node);
    }

    protected void visitSoyNode(SoyNode node) {
        throw new UnsupportedOperationException();
    }
}


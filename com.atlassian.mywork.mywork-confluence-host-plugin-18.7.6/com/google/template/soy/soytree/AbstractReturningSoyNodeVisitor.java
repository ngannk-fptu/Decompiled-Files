/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.basetree.AbstractReturningNodeVisitor;
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
import java.util.List;

public abstract class AbstractReturningSoyNodeVisitor<R>
extends AbstractReturningNodeVisitor<SoyNode, R> {
    @Override
    protected R visit(SoyNode node) {
        switch (node.getKind()) {
            case SOY_FILE_SET_NODE: {
                return this.visitSoyFileSetNode((SoyFileSetNode)node);
            }
            case SOY_FILE_NODE: {
                return this.visitSoyFileNode((SoyFileNode)node);
            }
            case TEMPLATE_BASIC_NODE: {
                return this.visitTemplateBasicNode((TemplateBasicNode)node);
            }
            case TEMPLATE_DELEGATE_NODE: {
                return this.visitTemplateDelegateNode((TemplateDelegateNode)node);
            }
            case RAW_TEXT_NODE: {
                return this.visitRawTextNode((RawTextNode)node);
            }
            case GOOG_MSG_DEF_NODE: {
                return this.visitGoogMsgDefNode((GoogMsgDefNode)node);
            }
            case GOOG_MSG_REF_NODE: {
                return this.visitGoogMsgRefNode((GoogMsgRefNode)node);
            }
            case MSG_FALLBACK_GROUP_NODE: {
                return this.visitMsgFallbackGroupNode((MsgFallbackGroupNode)node);
            }
            case MSG_NODE: {
                return this.visitMsgNode((MsgNode)node);
            }
            case MSG_PLURAL_NODE: {
                return this.visitMsgPluralNode((MsgPluralNode)node);
            }
            case MSG_PLURAL_CASE_NODE: {
                return this.visitMsgPluralCaseNode((MsgPluralCaseNode)node);
            }
            case MSG_PLURAL_DEFAULT_NODE: {
                return this.visitMsgPluralDefaultNode((MsgPluralDefaultNode)node);
            }
            case MSG_PLURAL_REMAINDER_NODE: {
                return this.visitMsgPluralRemainderNode((MsgPluralRemainderNode)node);
            }
            case MSG_SELECT_NODE: {
                return this.visitMsgSelectNode((MsgSelectNode)node);
            }
            case MSG_SELECT_CASE_NODE: {
                return this.visitMsgSelectCaseNode((MsgSelectCaseNode)node);
            }
            case MSG_SELECT_DEFAULT_NODE: {
                return this.visitMsgSelectDefaultNode((MsgSelectDefaultNode)node);
            }
            case MSG_PLACEHOLDER_NODE: {
                return this.visitMsgPlaceholderNode((MsgPlaceholderNode)node);
            }
            case MSG_HTML_TAG_NODE: {
                return this.visitMsgHtmlTagNode((MsgHtmlTagNode)node);
            }
            case PRINT_NODE: {
                return this.visitPrintNode((PrintNode)node);
            }
            case PRINT_DIRECTIVE_NODE: {
                return this.visitPrintDirectiveNode((PrintDirectiveNode)node);
            }
            case CSS_NODE: {
                return this.visitCssNode((CssNode)node);
            }
            case XID_NODE: {
                return this.visitXidNode((XidNode)node);
            }
            case LET_VALUE_NODE: {
                return this.visitLetValueNode((LetValueNode)node);
            }
            case LET_CONTENT_NODE: {
                return this.visitLetContentNode((LetContentNode)node);
            }
            case IF_NODE: {
                return this.visitIfNode((IfNode)node);
            }
            case IF_COND_NODE: {
                return this.visitIfCondNode((IfCondNode)node);
            }
            case IF_ELSE_NODE: {
                return this.visitIfElseNode((IfElseNode)node);
            }
            case SWITCH_NODE: {
                return this.visitSwitchNode((SwitchNode)node);
            }
            case SWITCH_CASE_NODE: {
                return this.visitSwitchCaseNode((SwitchCaseNode)node);
            }
            case SWITCH_DEFAULT_NODE: {
                return this.visitSwitchDefaultNode((SwitchDefaultNode)node);
            }
            case FOREACH_NODE: {
                return this.visitForeachNode((ForeachNode)node);
            }
            case FOREACH_NONEMPTY_NODE: {
                return this.visitForeachNonemptyNode((ForeachNonemptyNode)node);
            }
            case FOREACH_IFEMPTY_NODE: {
                return this.visitForeachIfemptyNode((ForeachIfemptyNode)node);
            }
            case FOR_NODE: {
                return this.visitForNode((ForNode)node);
            }
            case CALL_BASIC_NODE: {
                return this.visitCallBasicNode((CallBasicNode)node);
            }
            case CALL_DELEGATE_NODE: {
                return this.visitCallDelegateNode((CallDelegateNode)node);
            }
            case CALL_PARAM_VALUE_NODE: {
                return this.visitCallParamValueNode((CallParamValueNode)node);
            }
            case CALL_PARAM_CONTENT_NODE: {
                return this.visitCallParamContentNode((CallParamContentNode)node);
            }
            case LOG_NODE: {
                return this.visitLogNode((LogNode)node);
            }
            case DEBUGGER_NODE: {
                return this.visitDebuggerNode((DebuggerNode)node);
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<R> visitChildren(SoyNode.ParentSoyNode<?> node) {
        return ((AbstractReturningNodeVisitor)this).visitChildren(node);
    }

    @Override
    protected List<R> visitChildrenAllowingConcurrentModification(SoyNode.ParentSoyNode<?> node) {
        return ((AbstractReturningNodeVisitor)this).visitChildrenAllowingConcurrentModification(node);
    }

    protected R visitSoyFileSetNode(SoyFileSetNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitSoyFileNode(SoyFileNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitTemplateBasicNode(TemplateNode node) {
        return this.visitTemplateNode(node);
    }

    protected R visitTemplateDelegateNode(TemplateNode node) {
        return this.visitTemplateNode(node);
    }

    protected R visitTemplateNode(TemplateNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitRawTextNode(RawTextNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitGoogMsgDefNode(GoogMsgDefNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitGoogMsgRefNode(GoogMsgRefNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgNode(MsgNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgPluralNode(MsgPluralNode node) {
        return this.visitMsgSubstUnitNode(node);
    }

    protected R visitMsgPluralCaseNode(MsgPluralCaseNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgPluralDefaultNode(MsgPluralDefaultNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgPluralRemainderNode(MsgPluralRemainderNode node) {
        return this.visitMsgSubstUnitNode(node);
    }

    protected R visitMsgSelectNode(MsgSelectNode node) {
        return this.visitMsgSubstUnitNode(node);
    }

    protected R visitMsgSelectCaseNode(MsgSelectCaseNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgSelectDefaultNode(MsgSelectDefaultNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgPlaceholderNode(MsgPlaceholderNode node) {
        return this.visitMsgSubstUnitNode(node);
    }

    protected R visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitMsgSubstUnitNode(SoyNode.MsgSubstUnitNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitPrintNode(PrintNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitPrintDirectiveNode(PrintDirectiveNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitCssNode(CssNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitXidNode(XidNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitLetValueNode(LetValueNode node) {
        return this.visitLetNode(node);
    }

    protected R visitLetContentNode(LetContentNode node) {
        return this.visitLetNode(node);
    }

    protected R visitLetNode(LetNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitIfNode(IfNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitIfCondNode(IfCondNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitIfElseNode(IfElseNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitSwitchNode(SwitchNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitSwitchCaseNode(SwitchCaseNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitSwitchDefaultNode(SwitchDefaultNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitForeachNode(ForeachNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitForeachIfemptyNode(ForeachIfemptyNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitForeachNonemptyNode(ForeachNonemptyNode node) {
        return this.visitLoopNode(node);
    }

    protected R visitForNode(ForNode node) {
        return this.visitLoopNode(node);
    }

    protected R visitLoopNode(SoyNode.LoopNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitCallBasicNode(CallBasicNode node) {
        return this.visitCallNode(node);
    }

    protected R visitCallDelegateNode(CallDelegateNode node) {
        return this.visitCallNode(node);
    }

    protected R visitCallNode(CallNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitCallParamValueNode(CallParamValueNode node) {
        return this.visitCallParamNode(node);
    }

    protected R visitCallParamContentNode(CallParamContentNode node) {
        return this.visitCallParamNode(node);
    }

    protected R visitCallParamNode(CallParamNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitLogNode(LogNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitDebuggerNode(DebuggerNode node) {
        return this.visitSoyNode(node);
    }

    protected R visitSoyNode(SoyNode node) {
        throw new UnsupportedOperationException();
    }
}


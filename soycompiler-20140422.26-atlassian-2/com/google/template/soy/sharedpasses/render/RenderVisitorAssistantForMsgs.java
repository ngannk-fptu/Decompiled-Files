/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.ibm.icu.text.PluralRules
 *  com.ibm.icu.util.ULocale
 */
package com.google.template.soy.sharedpasses.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.internal.MsgUtils;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralCaseSpec;
import com.google.template.soy.msgs.restricted.SoyMsgPluralPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralRemainderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import com.google.template.soy.msgs.restricted.SoyMsgSelectPart;
import com.google.template.soy.sharedpasses.render.RenderException;
import com.google.template.soy.sharedpasses.render.RenderVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CaseOrDefaultNode;
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
import com.google.template.soy.soytree.SoyNode;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;
import java.util.Deque;
import java.util.List;
import java.util.Map;

class RenderVisitorAssistantForMsgs
extends AbstractSoyNodeVisitor<Void> {
    private final RenderVisitor master;
    private final Deque<Map<String, SoyValue>> env;
    private final SoyMsgBundle msgBundle;
    private double currPluralRemainderValue;

    RenderVisitorAssistantForMsgs(RenderVisitor master, Deque<Map<String, SoyValue>> env, SoyMsgBundle msgBundle) {
        this.master = master;
        this.env = env;
        this.msgBundle = msgBundle;
        this.currPluralRemainderValue = -1.0;
    }

    @Override
    public Void exec(SoyNode node) {
        throw new AssertionError();
    }

    void visitForUseByMaster(SoyNode node) {
        this.visit(node);
    }

    @Override
    protected void visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        boolean doAddEnvFrame;
        boolean bl = doAddEnvFrame = node.needsEnvFrameDuringInterp() != Boolean.FALSE;
        if (doAddEnvFrame) {
            this.env.push(Maps.newHashMap());
        }
        boolean foundTranslation = false;
        if (this.msgBundle != null) {
            for (MsgNode msg : node.getChildren()) {
                SoyMsg translation = this.msgBundle.getMsg(MsgUtils.computeMsgIdForDualFormat(msg));
                if (translation == null) continue;
                this.renderMsgFromTranslation(msg, translation);
                foundTranslation = true;
                break;
            }
        }
        if (!foundTranslation) {
            this.renderMsgFromSource((MsgNode)node.getChild(0));
        }
        if (doAddEnvFrame) {
            this.env.pop();
        }
    }

    private void renderMsgFromTranslation(MsgNode msg, SoyMsg translation) {
        ImmutableList<SoyMsgPart> msgParts = translation.getParts();
        if (msgParts.size() > 0) {
            SoyMsgPart firstPart = (SoyMsgPart)msgParts.get(0);
            if (firstPart instanceof SoyMsgPluralPart) {
                new PlrselMsgPartsVisitor(msg, new ULocale(translation.getLocaleString())).visitPart((SoyMsgPluralPart)firstPart);
            } else if (firstPart instanceof SoyMsgSelectPart) {
                new PlrselMsgPartsVisitor(msg, new ULocale(translation.getLocaleString())).visitPart((SoyMsgSelectPart)firstPart);
            } else {
                for (SoyMsgPart msgPart : msgParts) {
                    if (msgPart instanceof SoyMsgRawTextPart) {
                        RenderVisitor.append(this.master.getCurrOutputBufForUseByAssistants(), ((SoyMsgRawTextPart)msgPart).getRawText());
                        continue;
                    }
                    if (msgPart instanceof SoyMsgPlaceholderPart) {
                        String placeholderName = ((SoyMsgPlaceholderPart)msgPart).getPlaceholderName();
                        this.visit(msg.getRepPlaceholderNode(placeholderName));
                        continue;
                    }
                    throw new AssertionError();
                }
            }
        }
    }

    private void renderMsgFromSource(MsgNode msg) {
        this.visitChildren(msg);
    }

    @Override
    protected void visitMsgNode(MsgNode node) {
        throw new AssertionError();
    }

    @Override
    protected void visitMsgPluralNode(MsgPluralNode node) {
        double pluralValue;
        ExprRootNode<?> pluralExpr = node.getExpr();
        try {
            pluralValue = this.master.evalForUseByAssistants(pluralExpr, node).numberValue();
        }
        catch (SoyDataException e) {
            throw new RenderException(String.format("Plural expression \"%s\" doesn't evaluate to number.", pluralExpr.toSourceString()), e).addPartialStackTraceElement(node.getSourceLocation());
        }
        this.currPluralRemainderValue = pluralValue - (double)node.getOffset();
        for (CaseOrDefaultNode child : node.getChildren()) {
            if (child instanceof MsgPluralDefaultNode) {
                this.visitChildren(child);
                break;
            }
            if ((double)((MsgPluralCaseNode)child).getCaseNumber() != pluralValue) continue;
            this.visitChildren(child);
            break;
        }
        this.currPluralRemainderValue = -1.0;
    }

    @Override
    protected void visitMsgPluralRemainderNode(MsgPluralRemainderNode node) {
        RenderVisitor.append(this.master.getCurrOutputBufForUseByAssistants(), String.valueOf(this.currPluralRemainderValue));
    }

    @Override
    protected void visitMsgSelectNode(MsgSelectNode node) {
        String selectValue;
        ExprRootNode<?> selectExpr = node.getExpr();
        try {
            selectValue = this.master.evalForUseByAssistants(selectExpr, node).stringValue();
        }
        catch (SoyDataException e) {
            throw new RenderException(String.format("Select expression \"%s\" doesn't evaluate to string.", selectExpr.toSourceString()), e).addPartialStackTraceElement(node.getSourceLocation());
        }
        for (CaseOrDefaultNode child : node.getChildren()) {
            if (child instanceof MsgSelectDefaultNode) {
                this.visitChildren(child);
                continue;
            }
            if (!((MsgSelectCaseNode)child).getCaseValue().equals(selectValue)) continue;
            this.visitChildren(child);
            return;
        }
    }

    @Override
    protected void visitMsgPlaceholderNode(MsgPlaceholderNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        this.master.visitForUseByAssistants(node);
    }

    private class PlrselMsgPartsVisitor {
        private final MsgNode msgNode;
        private final ULocale locale;
        private double currentPluralRemainderValue;

        public PlrselMsgPartsVisitor(MsgNode msgNode, ULocale locale) {
            this.msgNode = msgNode;
            this.locale = locale;
        }

        private void visitPart(SoyMsgSelectPart selectPart) {
            String correctSelectValue;
            String selectVarName = selectPart.getSelectVarName();
            MsgSelectNode repSelectNode = this.msgNode.getRepSelectNode(selectVarName);
            ExprRootNode<?> selectExpr = repSelectNode.getExpr();
            try {
                correctSelectValue = RenderVisitorAssistantForMsgs.this.master.evalForUseByAssistants(selectExpr, repSelectNode).stringValue();
            }
            catch (SoyDataException e) {
                throw new RenderException(String.format("Select expression \"%s\" doesn't evaluate to string.", selectExpr.toSourceString()), e).addPartialStackTraceElement(repSelectNode.getSourceLocation());
            }
            List caseParts = null;
            List defaultParts = null;
            for (Pair case0 : selectPart.getCases()) {
                if (case0.first == null) {
                    defaultParts = (List)case0.second;
                    continue;
                }
                if (!((String)case0.first).equals(correctSelectValue)) continue;
                caseParts = (List)case0.second;
                break;
            }
            if (caseParts == null) {
                caseParts = defaultParts;
            }
            if (caseParts != null) {
                for (SoyMsgPart casePart : caseParts) {
                    if (casePart instanceof SoyMsgSelectPart) {
                        this.visitPart((SoyMsgSelectPart)casePart);
                        continue;
                    }
                    if (casePart instanceof SoyMsgPluralPart) {
                        this.visitPart((SoyMsgPluralPart)casePart);
                        continue;
                    }
                    if (casePart instanceof SoyMsgPlaceholderPart) {
                        this.visitPart((SoyMsgPlaceholderPart)casePart);
                        continue;
                    }
                    if (casePart instanceof SoyMsgRawTextPart) {
                        this.visitPart((SoyMsgRawTextPart)casePart);
                        continue;
                    }
                    throw new RenderException("Unsupported part of type " + casePart.getClass().getName() + " under a select case.").addPartialStackTraceElement(repSelectNode.getSourceLocation());
                }
            }
        }

        private void visitPart(SoyMsgPluralPart pluralPart) {
            double correctPluralValue;
            MsgPluralNode repPluralNode = this.msgNode.getRepPluralNode(pluralPart.getPluralVarName());
            ExprRootNode<?> pluralExpr = repPluralNode.getExpr();
            try {
                correctPluralValue = RenderVisitorAssistantForMsgs.this.master.evalForUseByAssistants(pluralExpr, repPluralNode).numberValue();
            }
            catch (SoyDataException e) {
                throw new RenderException(String.format("Plural expression \"%s\" doesn't evaluate to number.", pluralExpr.toSourceString()), e).addPartialStackTraceElement(repPluralNode.getSourceLocation());
            }
            this.currentPluralRemainderValue = correctPluralValue - (double)repPluralNode.getOffset();
            List caseParts = null;
            boolean hasNonExplicitCases = false;
            List otherCaseParts = null;
            for (Pair case0 : pluralPart.getCases()) {
                SoyMsgPluralCaseSpec pluralCaseSpec = (SoyMsgPluralCaseSpec)case0.first;
                SoyMsgPluralCaseSpec.Type caseType = pluralCaseSpec.getType();
                if (caseType == SoyMsgPluralCaseSpec.Type.EXPLICIT) {
                    if ((double)pluralCaseSpec.getExplicitValue() != correctPluralValue) continue;
                    caseParts = (List)case0.second;
                    break;
                }
                if (caseType == SoyMsgPluralCaseSpec.Type.OTHER) {
                    otherCaseParts = (List)case0.second;
                    continue;
                }
                hasNonExplicitCases = true;
            }
            if (caseParts == null && hasNonExplicitCases) {
                String pluralKeyword = PluralRules.forLocale((ULocale)this.locale).select(this.currentPluralRemainderValue);
                SoyMsgPluralCaseSpec.Type correctCaseType = new SoyMsgPluralCaseSpec(pluralKeyword).getType();
                for (Pair case0 : pluralPart.getCases()) {
                    if (((SoyMsgPluralCaseSpec)case0.first).getType() != correctCaseType) continue;
                    caseParts = (List)case0.second;
                    break;
                }
            }
            if (caseParts == null) {
                caseParts = otherCaseParts;
            }
            for (SoyMsgPart casePart : caseParts) {
                if (casePart instanceof SoyMsgPlaceholderPart) {
                    this.visitPart((SoyMsgPlaceholderPart)casePart);
                    continue;
                }
                if (casePart instanceof SoyMsgRawTextPart) {
                    this.visitPart((SoyMsgRawTextPart)casePart);
                    continue;
                }
                if (casePart instanceof SoyMsgPluralRemainderPart) {
                    this.visitPart((SoyMsgPluralRemainderPart)casePart);
                    continue;
                }
                throw new RenderException("Unsupported part of type " + casePart.getClass().getName() + " under a plural case.").addPartialStackTraceElement(repPluralNode.getSourceLocation());
            }
        }

        private void visitPart(SoyMsgPluralRemainderPart remainderPart) {
            RenderVisitor.append(RenderVisitorAssistantForMsgs.this.master.getCurrOutputBufForUseByAssistants(), String.valueOf(this.currentPluralRemainderValue));
        }

        private void visitPart(SoyMsgPlaceholderPart msgPlaceholderPart) {
            RenderVisitorAssistantForMsgs.this.visit(this.msgNode.getRepPlaceholderNode(msgPlaceholderPart.getPlaceholderName()));
        }

        private void visitPart(SoyMsgRawTextPart rawTextPart) {
            RenderVisitor.append(RenderVisitorAssistantForMsgs.this.master.getCurrOutputBufForUseByAssistants(), rawTextPart.getRawText());
        }
    }
}


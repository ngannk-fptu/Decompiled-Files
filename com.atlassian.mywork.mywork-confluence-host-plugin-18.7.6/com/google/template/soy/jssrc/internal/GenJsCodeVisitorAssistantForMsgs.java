/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.GenCallCodeUtils;
import com.google.template.soy.jssrc.internal.GenJsCodeVisitor;
import com.google.template.soy.jssrc.internal.GenJsExprsVisitor;
import com.google.template.soy.jssrc.internal.IsComputableAsJsExprsVisitor;
import com.google.template.soy.jssrc.internal.JsCodeBuilder;
import com.google.template.soy.jssrc.internal.JsExprTranslator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import com.google.template.soy.msgs.internal.IcuSyntaxUtils;
import com.google.template.soy.msgs.internal.MsgUtils;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgPluralRemainderNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GenJsCodeVisitorAssistantForMsgs
extends AbstractSoyNodeVisitor<Void> {
    private static final Pattern UNDERSCORE_NUMBER_SUFFIX = Pattern.compile("_[0-9]+$");
    private final SoyJsSrcOptions jsSrcOptions;
    private final GenJsCodeVisitor master;
    private final JsExprTranslator jsExprTranslator;
    private final GenCallCodeUtils genCallCodeUtils;
    private final IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor;
    private final GenJsExprsVisitor genJsExprsVisitor;
    private final JsCodeBuilder jsCodeBuilder;
    private final Deque<Map<String, JsExpr>> localVarTranslations;

    GenJsCodeVisitorAssistantForMsgs(GenJsCodeVisitor master, SoyJsSrcOptions jsSrcOptions, JsExprTranslator jsExprTranslator, GenCallCodeUtils genCallCodeUtils, IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor, JsCodeBuilder jsCodeBuilder, Deque<Map<String, JsExpr>> localVarTranslations, GenJsExprsVisitor genJsExprsVisitor) {
        this.master = master;
        this.jsSrcOptions = jsSrcOptions;
        this.jsExprTranslator = jsExprTranslator;
        this.genCallCodeUtils = genCallCodeUtils;
        this.isComputableAsJsExprsVisitor = isComputableAsJsExprsVisitor;
        this.jsCodeBuilder = jsCodeBuilder;
        this.localVarTranslations = localVarTranslations;
        this.genJsExprsVisitor = genJsExprsVisitor;
    }

    @Override
    public Void exec(SoyNode node) {
        throw new AssertionError();
    }

    void visitForUseByMaster(SoyNode node) {
        this.visit(node);
    }

    @Override
    protected void visitGoogMsgDefNode(GoogMsgDefNode node) {
        if (node.numChildren() == 1) {
            MsgNode msgNode = (MsgNode)node.getChild(0);
            String googMsgVarName = this.buildGoogMsgVarNameHelper(node, msgNode);
            GoogMsgCodeGenInfo googMsgCodeGenInfo = this.genGoogGetMsgCallHelper(googMsgVarName, msgNode);
            this.jsCodeBuilder.appendLineStart("var ", node.getRenderedGoogMsgVarName(), " = ");
            if (msgNode.isPlrselMsg()) {
                this.genI18nMessageFormatExprHelper(googMsgCodeGenInfo);
            } else {
                this.jsCodeBuilder.append(googMsgVarName);
            }
            this.jsCodeBuilder.appendLineEnd(";");
        } else {
            ArrayList childGoogMsgCodeGenInfos = Lists.newArrayListWithCapacity((int)node.numChildren());
            for (MsgNode msgNode : node.getChildren()) {
                String googMsgVarName = this.buildGoogMsgVarNameHelper(node, msgNode);
                childGoogMsgCodeGenInfos.add(this.genGoogGetMsgCallHelper(googMsgVarName, msgNode));
            }
            this.jsCodeBuilder.appendLineStart("var ", node.getRenderedGoogMsgVarName(), " = goog.getMsgWithFallback(");
            boolean isFirst = true;
            for (GoogMsgCodeGenInfo childGoogMsgCodeGenInfo : childGoogMsgCodeGenInfos) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    this.jsCodeBuilder.append(", ");
                }
                this.jsCodeBuilder.append(childGoogMsgCodeGenInfo.googMsgVarName);
            }
            this.jsCodeBuilder.appendLineEnd(");");
            for (GoogMsgCodeGenInfo childGoogMsgCodeGenInfo : childGoogMsgCodeGenInfos) {
                if (!childGoogMsgCodeGenInfo.isPlrselMsg) continue;
                this.jsCodeBuilder.appendLine("if (", node.getRenderedGoogMsgVarName(), " == ", childGoogMsgCodeGenInfo.googMsgVarName, ") {");
                this.jsCodeBuilder.increaseIndent();
                this.jsCodeBuilder.appendLineStart(node.getRenderedGoogMsgVarName(), " = ");
                this.genI18nMessageFormatExprHelper(childGoogMsgCodeGenInfo);
                this.jsCodeBuilder.appendLineEnd(";");
                this.jsCodeBuilder.decreaseIndent();
                this.jsCodeBuilder.appendLine("}");
            }
        }
    }

    private String buildGoogMsgVarNameHelper(GoogMsgDefNode googMsgDefNode, MsgNode msgNode) {
        return this.jsSrcOptions.googMsgsAreExternal() ? "MSG_EXTERNAL_" + googMsgDefNode.getChildMsgId(msgNode) : "MSG_UNNAMED_" + msgNode.getId();
    }

    private GoogMsgCodeGenInfo genGoogGetMsgCallHelper(String googMsgVarName, MsgNode msgNode) {
        ImmutableList<SoyMsgPart> msgParts = MsgUtils.buildMsgParts(msgNode);
        String googMsgContentStr = GenJsCodeVisitorAssistantForMsgs.buildGoogMsgContentStr(msgParts, msgNode.isPlrselMsg());
        String googMsgContentStrCode = BaseUtils.escapeToSoyString(googMsgContentStr, true);
        GoogMsgCodeGenInfo googMsgCodeGenInfo = new GoogMsgCodeGenInfo(googMsgVarName, msgNode.isPlrselMsg());
        this.genGoogMsgCodeBitsForChildren(msgNode, msgNode, googMsgCodeGenInfo);
        this.jsCodeBuilder.appendLineStart("/** ");
        if (msgNode.getMeaning() != null) {
            this.jsCodeBuilder.appendLineEnd("@meaning ", msgNode.getMeaning());
            this.jsCodeBuilder.appendLineStart(" *  ");
        }
        this.jsCodeBuilder.append("@desc ", msgNode.getDesc());
        if (msgNode.isHidden()) {
            this.jsCodeBuilder.appendLineEnd(new String[0]);
            this.jsCodeBuilder.appendLineStart(" *  @hidden");
        }
        this.jsCodeBuilder.appendLineEnd(" */");
        this.jsCodeBuilder.appendLineStart("var ", googMsgCodeGenInfo.googMsgVarName, " = goog.getMsg(");
        if (msgNode.isPlrselMsg()) {
            this.jsCodeBuilder.appendLineEnd(googMsgContentStrCode, ");");
        } else if (googMsgCodeGenInfo.placeholderCodeBits.size() == 0) {
            this.jsCodeBuilder.appendLineEnd(googMsgContentStrCode, ");");
        } else {
            this.jsCodeBuilder.appendLineEnd(new String[0]);
            this.jsCodeBuilder.appendLine("    ", googMsgContentStrCode, ",");
            this.appendCodeBits(googMsgCodeGenInfo.placeholderCodeBits);
            this.jsCodeBuilder.appendLineEnd(");");
        }
        return googMsgCodeGenInfo;
    }

    private static String buildGoogMsgContentStr(ImmutableList<SoyMsgPart> msgParts, boolean doUseBracedPhs) {
        msgParts = IcuSyntaxUtils.convertMsgPartsToEmbeddedIcuSyntax(msgParts, false);
        StringBuilder msgStrSb = new StringBuilder();
        for (SoyMsgPart msgPart : msgParts) {
            if (msgPart instanceof SoyMsgRawTextPart) {
                msgStrSb.append(((SoyMsgRawTextPart)msgPart).getRawText());
                continue;
            }
            if (msgPart instanceof SoyMsgPlaceholderPart) {
                String placeholderName = ((SoyMsgPlaceholderPart)msgPart).getPlaceholderName();
                if (doUseBracedPhs) {
                    msgStrSb.append("{").append(placeholderName).append("}");
                    continue;
                }
                String googMsgPlaceholderName = GenJsCodeVisitorAssistantForMsgs.genGoogMsgPlaceholderName(placeholderName);
                msgStrSb.append("{$").append(googMsgPlaceholderName).append("}");
                continue;
            }
            throw new AssertionError();
        }
        return msgStrSb.toString();
    }

    private void genI18nMessageFormatExprHelper(GoogMsgCodeGenInfo googMsgCodeGenInfo) {
        List<String> codeBitsForMfCall = googMsgCodeGenInfo.plrselVarCodeBits;
        codeBitsForMfCall.addAll(googMsgCodeGenInfo.placeholderCodeBits);
        this.jsCodeBuilder.appendLineEnd("(new goog.i18n.MessageFormat(", googMsgCodeGenInfo.googMsgVarName, ")).formatIgnoringPound(");
        this.appendCodeBits(codeBitsForMfCall);
        this.jsCodeBuilder.append(")");
    }

    private void appendCodeBits(List<String> codeBits) {
        boolean isFirst = true;
        for (String codeBit : codeBits) {
            if (isFirst) {
                this.jsCodeBuilder.appendLineStart("    {");
                isFirst = false;
            } else {
                this.jsCodeBuilder.appendLineEnd(",");
                this.jsCodeBuilder.appendLineStart("     ");
            }
            this.jsCodeBuilder.append(codeBit);
        }
        this.jsCodeBuilder.append("}");
    }

    private void genGoogMsgCodeBitsForChildren(SoyNode.BlockNode parentNode, MsgNode msgNode, GoogMsgCodeGenInfo googMsgCodeGenInfo) {
        for (SoyNode.StandaloneNode child : parentNode.getChildren()) {
            if (child instanceof RawTextNode) continue;
            if (child instanceof MsgPlaceholderNode) {
                this.genGoogMsgCodeBitsForPlaceholder((MsgPlaceholderNode)child, msgNode, googMsgCodeGenInfo);
                continue;
            }
            if (child instanceof MsgPluralNode) {
                this.genGoogMsgCodeBitsForPluralNode((MsgPluralNode)child, msgNode, googMsgCodeGenInfo);
                continue;
            }
            if (child instanceof MsgSelectNode) {
                this.genGoogMsgCodeBitsForSelectNode((MsgSelectNode)child, msgNode, googMsgCodeGenInfo);
                continue;
            }
            if (child instanceof MsgPluralRemainderNode) continue;
            String nodeStringForErrorMsg = parentNode instanceof SoyNode.CommandNode ? "Tag " + ((SoyNode.CommandNode)((Object)parentNode)).getTagString() : "Node " + parentNode.toString();
            throw SoySyntaxException.createWithoutMetaInfo(nodeStringForErrorMsg + " is not allowed to be a direct child of a 'msg' tag.");
        }
    }

    private void genGoogMsgCodeBitsForPluralNode(MsgPluralNode pluralNode, MsgNode msgNode, GoogMsgCodeGenInfo googMsgCodeGenInfo) {
        GenJsCodeVisitorAssistantForMsgs.updatePlrselVarCodeBits(googMsgCodeGenInfo, msgNode.getPluralVarName(pluralNode), this.jsExprTranslator.translateToJsExpr(pluralNode.getExpr(), null, this.localVarTranslations).getText());
        for (CaseOrDefaultNode child : pluralNode.getChildren()) {
            this.genGoogMsgCodeBitsForChildren(child, msgNode, googMsgCodeGenInfo);
        }
    }

    private void genGoogMsgCodeBitsForSelectNode(MsgSelectNode selectNode, MsgNode msgNode, GoogMsgCodeGenInfo googMsgCodeGenInfo) {
        GenJsCodeVisitorAssistantForMsgs.updatePlrselVarCodeBits(googMsgCodeGenInfo, msgNode.getSelectVarName(selectNode), this.jsExprTranslator.translateToJsExpr(selectNode.getExpr(), null, this.localVarTranslations).getText());
        for (CaseOrDefaultNode child : selectNode.getChildren()) {
            this.genGoogMsgCodeBitsForChildren(child, msgNode, googMsgCodeGenInfo);
        }
    }

    private static void updatePlrselVarCodeBits(GoogMsgCodeGenInfo googMsgCodeGenInfo, String plrselVarName, String exprText) {
        if (googMsgCodeGenInfo.seenPlrselVarNames.contains(plrselVarName)) {
            return;
        }
        googMsgCodeGenInfo.seenPlrselVarNames.add(plrselVarName);
        String placeholderCodeBit = "'" + plrselVarName + "': " + exprText;
        googMsgCodeGenInfo.plrselVarCodeBits.add(placeholderCodeBit);
    }

    private void genGoogMsgCodeBitsForPlaceholder(MsgPlaceholderNode node, MsgNode msgNode, GoogMsgCodeGenInfo googMsgCodeGenInfo) {
        String placeholderName = msgNode.getPlaceholderName(node);
        if (googMsgCodeGenInfo.seenPlaceholderNames.contains(placeholderName)) {
            return;
        }
        googMsgCodeGenInfo.seenPlaceholderNames.add(placeholderName);
        String googMsgPlaceholderName = googMsgCodeGenInfo.isPlrselMsg ? placeholderName : GenJsCodeVisitorAssistantForMsgs.genGoogMsgPlaceholderName(placeholderName);
        String placeholderCodeBit = "'" + googMsgPlaceholderName + "': " + this.genGoogMsgPlaceholderExpr(node).getText();
        googMsgCodeGenInfo.placeholderCodeBits.add(placeholderCodeBit);
    }

    private static String genGoogMsgPlaceholderName(String placeholderName) {
        Matcher suffixMatcher = UNDERSCORE_NUMBER_SUFFIX.matcher(placeholderName);
        if (suffixMatcher.find()) {
            String base = placeholderName.substring(0, suffixMatcher.start());
            String suffix = suffixMatcher.group();
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, base) + suffix;
        }
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, placeholderName);
    }

    private JsExpr genGoogMsgPlaceholderExpr(MsgPlaceholderNode msgPhNode) {
        ArrayList contentJsExprs = Lists.newArrayList();
        for (SoyNode.StandaloneNode contentNode : msgPhNode.getChildren()) {
            if (contentNode instanceof MsgHtmlTagNode && !((Boolean)this.isComputableAsJsExprsVisitor.exec(contentNode)).booleanValue()) {
                this.visit(contentNode);
                contentJsExprs.add(new JsExpr("htmlTag" + contentNode.getId(), Integer.MAX_VALUE));
                continue;
            }
            if (contentNode instanceof CallNode) {
                CallNode callNode = (CallNode)contentNode;
                for (CallParamNode grandchild : callNode.getChildren()) {
                    if (!(grandchild instanceof CallParamContentNode) || ((Boolean)this.isComputableAsJsExprsVisitor.exec(grandchild)).booleanValue()) continue;
                    this.visit(grandchild);
                }
                contentJsExprs.add(this.genCallCodeUtils.genCallExpr(callNode, this.localVarTranslations));
                continue;
            }
            contentJsExprs.addAll(this.genJsExprsVisitor.exec(contentNode));
        }
        return JsExprUtils.concatJsExprs(contentJsExprs);
    }

    @Override
    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        if (((Boolean)this.isComputableAsJsExprsVisitor.exec(node)).booleanValue()) {
            throw new AssertionError((Object)"Should only define 'htmlTag<n>' when not computable as JS expressions.");
        }
        this.jsCodeBuilder.pushOutputVar("htmlTag" + node.getId());
        this.visitChildren(node);
        this.jsCodeBuilder.popOutputVar();
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        this.master.visitForUseByAssistants(node);
    }

    private static class GoogMsgCodeGenInfo {
        public final String googMsgVarName;
        public final boolean isPlrselMsg;
        public List<String> placeholderCodeBits;
        public Set<String> seenPlaceholderNames;
        public List<String> plrselVarCodeBits;
        public Set<String> seenPlrselVarNames;

        public GoogMsgCodeGenInfo(String googMsgVarName, boolean isPlrselMsg) {
            this.googMsgVarName = googMsgVarName;
            this.isPlrselMsg = isPlrselMsg;
            this.placeholderCodeBits = Lists.newArrayList();
            this.seenPlaceholderNames = Sets.newHashSet();
            this.plrselVarCodeBits = Lists.newArrayList();
            this.seenPlrselVarNames = Sets.newHashSet();
        }
    }
}


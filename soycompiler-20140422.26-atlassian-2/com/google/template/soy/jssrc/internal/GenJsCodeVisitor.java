/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.inject.Inject
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.CanInitOutputVarVisitor;
import com.google.template.soy.jssrc.internal.GenCallCodeUtils;
import com.google.template.soy.jssrc.internal.GenDirectivePluginRequiresVisitor;
import com.google.template.soy.jssrc.internal.GenFunctionPluginRequiresVisitor;
import com.google.template.soy.jssrc.internal.GenJsCodeVisitorAssistantForMsgs;
import com.google.template.soy.jssrc.internal.GenJsExprsVisitor;
import com.google.template.soy.jssrc.internal.IsComputableAsJsExprsVisitor;
import com.google.template.soy.jssrc.internal.JsCodeBuilder;
import com.google.template.soy.jssrc.internal.JsExprTranslator;
import com.google.template.soy.jssrc.internal.JsSrcUtils;
import com.google.template.soy.jssrc.internal.TranslateToJsExprVisitor;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import com.google.template.soy.shared.internal.FindCalleesNotInFileVisitor;
import com.google.template.soy.shared.internal.HasNodeTypesVisitor;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import com.google.template.soy.sharedpasses.FindIndirectParamsVisitor;
import com.google.template.soy.sharedpasses.ShouldEnsureDataIsDefinedVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.DebuggerNode;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.LetValueNode;
import com.google.template.soy.soytree.LogNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.soytree.SwitchCaseNode;
import com.google.template.soy.soytree.SwitchDefaultNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.XidNode;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import com.google.template.soy.types.SoyObjectType;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeOps;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.primitive.NullType;
import com.google.template.soy.types.primitive.SanitizedType;
import com.google.template.soy.types.proto.SoyProtoType;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GenJsCodeVisitor
extends AbstractSoyNodeVisitor<List<String>> {
    private static final Pattern DOT = Pattern.compile("\\.");
    private static final Pattern INTEGER = Pattern.compile("-?\\d+");
    private static final String GOOG_IS_RTL_NAMESPACE = "goog.i18n.bidi";
    private static final String GOOG_MESSAGE_FORMAT_NAMESPACE = "goog.i18n.MessageFormat";
    private final SoyJsSrcOptions jsSrcOptions;
    private final boolean isUsingIjData;
    private final JsExprTranslator jsExprTranslator;
    private final GenCallCodeUtils genCallCodeUtils;
    private final IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor;
    private final CanInitOutputVarVisitor canInitOutputVarVisitor;
    private final GenJsExprsVisitor.GenJsExprsVisitorFactory genJsExprsVisitorFactory;
    private List<String> jsFilesContents;
    @VisibleForTesting
    protected JsCodeBuilder jsCodeBuilder;
    @VisibleForTesting
    protected Deque<Map<String, JsExpr>> localVarTranslations;
    @VisibleForTesting
    protected GenJsExprsVisitor genJsExprsVisitor;
    @VisibleForTesting
    protected GenJsCodeVisitorAssistantForMsgs assistantForMsgs;
    private GenDirectivePluginRequiresVisitor genDirectivePluginRequiresVisitor;
    private GenFunctionPluginRequiresVisitor genFunctionPluginRequiresVisitor;
    private TemplateRegistry templateRegistry;
    private final SoyTypeOps typeOps;

    @Inject
    GenJsCodeVisitor(SoyJsSrcOptions jsSrcOptions, @ApiCallScopeBindingAnnotations.IsUsingIjData boolean isUsingIjData, JsExprTranslator jsExprTranslator, GenCallCodeUtils genCallCodeUtils, IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor, CanInitOutputVarVisitor canInitOutputVarVisitor, GenJsExprsVisitor.GenJsExprsVisitorFactory genJsExprsVisitorFactory, GenDirectivePluginRequiresVisitor genDirectivePluginRequiresVisitor, GenFunctionPluginRequiresVisitor genFunctionPluginRequiresVisitor, SoyTypeOps typeOps) {
        this.jsSrcOptions = jsSrcOptions;
        this.isUsingIjData = isUsingIjData;
        this.jsExprTranslator = jsExprTranslator;
        this.genCallCodeUtils = genCallCodeUtils;
        this.isComputableAsJsExprsVisitor = isComputableAsJsExprsVisitor;
        this.canInitOutputVarVisitor = canInitOutputVarVisitor;
        this.genJsExprsVisitorFactory = genJsExprsVisitorFactory;
        this.genDirectivePluginRequiresVisitor = genDirectivePluginRequiresVisitor;
        this.genFunctionPluginRequiresVisitor = genFunctionPluginRequiresVisitor;
        this.typeOps = typeOps;
    }

    @Override
    public List<String> exec(SoyNode node) {
        this.jsFilesContents = Lists.newArrayList();
        this.jsCodeBuilder = null;
        this.localVarTranslations = null;
        this.genJsExprsVisitor = null;
        this.assistantForMsgs = null;
        this.visit(node);
        return this.jsFilesContents;
    }

    void visitForUseByAssistants(SoyNode node) {
        this.visit(node);
    }

    @Override
    @VisibleForTesting
    protected void visit(SoyNode node) {
        super.visit(node);
    }

    @Override
    protected void visitChildren(SoyNode.ParentSoyNode<?> node) {
        if (node.numChildren() == 0 || !((Boolean)this.canInitOutputVarVisitor.exec(node.getChild(0))).booleanValue()) {
            this.jsCodeBuilder.initOutputVarIfNecessary();
        }
        ArrayList consecChildrenJsExprs = Lists.newArrayList();
        for (SoyNode child : node.getChildren()) {
            if (((Boolean)this.isComputableAsJsExprsVisitor.exec(child)).booleanValue()) {
                consecChildrenJsExprs.addAll(this.genJsExprsVisitor.exec(child));
                continue;
            }
            if (consecChildrenJsExprs.size() > 0) {
                this.jsCodeBuilder.addToOutputVar(consecChildrenJsExprs);
                consecChildrenJsExprs.clear();
            }
            this.visit(child);
        }
        if (consecChildrenJsExprs.size() > 0) {
            this.jsCodeBuilder.addToOutputVar(consecChildrenJsExprs);
            consecChildrenJsExprs.clear();
        }
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.templateRegistry = new TemplateRegistry(node);
        for (SoyFileNode soyFile : node.getChildren()) {
            try {
                this.visit(soyFile);
            }
            catch (SoySyntaxException sse) {
                throw sse.associateMetaInfo(null, soyFile.getFilePath(), null);
            }
        }
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        if (node.getSoyFileKind() != SoyFileKind.SRC) {
            return;
        }
        this.jsCodeBuilder = new JsCodeBuilder(this.jsSrcOptions.getCodeStyle());
        this.jsCodeBuilder.appendLine("// This file was automatically generated from ", node.getFileName(), ".");
        this.jsCodeBuilder.appendLine("// Please don't edit this file by hand.");
        this.jsCodeBuilder.appendLine(new String[0]);
        this.jsCodeBuilder.appendLine("/**");
        if (node.getNamespace() != null) {
            this.jsCodeBuilder.appendLine(" * @fileoverview Templates in namespace ", node.getNamespace(), ".");
        }
        if (node.getDelPackageName() != null) {
            this.jsCodeBuilder.appendLine(" * @modName {", node.getDelPackageName(), "}");
        }
        this.addJsDocToProvideDelTemplates(node);
        this.addJsDocToRequireDelTemplates(node);
        this.addCodeToRequireCss(node);
        this.jsCodeBuilder.appendLine(" */");
        this.jsCodeBuilder.appendLine(new String[0]);
        if (this.jsSrcOptions.shouldProvideRequireSoyNamespaces()) {
            this.addCodeToProvideSoyNamespace(node);
            if (this.jsSrcOptions.shouldProvideBothSoyNamespacesAndJsFunctions()) {
                this.addCodeToProvideJsFunctions(node);
            }
            this.jsCodeBuilder.appendLine(new String[0]);
            this.addCodeToRequireGeneralDeps(node);
            this.addCodeToRequireSoyNamespaces(node);
        } else if (this.jsSrcOptions.shouldProvideRequireJsFunctions()) {
            if (this.jsSrcOptions.shouldProvideBothSoyNamespacesAndJsFunctions()) {
                this.addCodeToProvideSoyNamespace(node);
            }
            this.addCodeToProvideJsFunctions(node);
            this.jsCodeBuilder.appendLine(new String[0]);
            this.addCodeToRequireGeneralDeps(node);
            this.addCodeToRequireJsFunctions(node);
        } else {
            this.addCodeToDefineJsNamespaces(node);
        }
        for (TemplateNode template : node.getChildren()) {
            this.jsCodeBuilder.appendLine(new String[0]).appendLine(new String[0]);
            try {
                this.visit(template);
            }
            catch (SoySyntaxException sse) {
                throw sse.associateMetaInfo(null, null, template.getTemplateNameForUserMsgs());
            }
        }
        this.jsFilesContents.add(this.jsCodeBuilder.getCode());
        this.jsCodeBuilder = null;
    }

    private void addCodeToRequireCss(SoyFileNode soyFile) {
        TreeSet requiredCssNamespaces = Sets.newTreeSet();
        requiredCssNamespaces.addAll(soyFile.getRequiredCssNamespaces());
        for (TemplateNode template : soyFile.getChildren()) {
            requiredCssNamespaces.addAll(template.getRequiredCssNamespaces());
        }
        for (String requiredCssNamespace : requiredCssNamespaces) {
            this.jsCodeBuilder.appendLine(" * @requirecss {", requiredCssNamespace, "}");
        }
    }

    private void addCodeToDefineJsNamespaces(SoyFileNode soyFile) {
        TreeSet jsNamespaces = Sets.newTreeSet();
        for (TemplateNode template : soyFile.getChildren()) {
            String templateName = template.getTemplateName();
            Matcher dotMatcher = DOT.matcher(templateName);
            while (dotMatcher.find()) {
                jsNamespaces.add(templateName.substring(0, dotMatcher.start()));
            }
        }
        for (String jsNamespace : jsNamespaces) {
            boolean hasDot;
            boolean bl = hasDot = jsNamespace.indexOf(46) >= 0;
            if (!this.jsSrcOptions.shouldDeclareTopLevelNamespaces() && !hasDot) continue;
            this.jsCodeBuilder.appendLine("if (typeof ", jsNamespace, " == 'undefined') { ", hasDot ? "" : "var ", jsNamespace, " = {}; }");
        }
    }

    private void addCodeToProvideSoyNamespace(SoyFileNode soyFile) {
        if (soyFile.getNamespace() != null) {
            this.jsCodeBuilder.appendLine("goog.provide('", soyFile.getNamespace(), "');");
        }
    }

    private void addCodeToProvideJsFunctions(SoyFileNode soyFile) {
        TreeSet templateNames = Sets.newTreeSet();
        for (TemplateNode template : soyFile.getChildren()) {
            if (template instanceof TemplateBasicNode && ((TemplateBasicNode)template).isOverride()) continue;
            templateNames.add(template.getTemplateName());
        }
        for (String templateName : templateNames) {
            this.jsCodeBuilder.appendLine("goog.provide('", templateName, "');");
        }
    }

    private void addJsDocToProvideDelTemplates(SoyFileNode soyFile) {
        TreeSet delTemplateNames = Sets.newTreeSet();
        for (TemplateNode template : soyFile.getChildren()) {
            if (!(template instanceof TemplateDelegateNode)) continue;
            delTemplateNames.add(((TemplateDelegateNode)template).getDelTemplateName());
        }
        for (String delTemplateName : delTemplateNames) {
            this.jsCodeBuilder.appendLine(" * @hassoydeltemplate {", delTemplateName, "}");
        }
    }

    private void addJsDocToRequireDelTemplates(SoyFileNode soyFile) {
        TreeSet delTemplateNames = Sets.newTreeSet();
        for (CallDelegateNode delCall : SoytreeUtils.getAllNodesOfType(soyFile, CallDelegateNode.class)) {
            delTemplateNames.add(delCall.getDelCalleeName());
        }
        for (String delTemplateName : delTemplateNames) {
            this.jsCodeBuilder.appendLine(" * @hassoydelcall {", delTemplateName, "}");
        }
    }

    private void addCodeToRequireGeneralDeps(SoyFileNode soyFile) {
        this.jsCodeBuilder.appendLine("goog.require('soy');");
        this.jsCodeBuilder.appendLine("goog.require('soydata');");
        if (this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
            this.jsCodeBuilder.appendLine("goog.require('soy.StringBuilder');");
        }
        Object requiredObjectTypes = ImmutableSortedSet.of();
        if (this.hasStrictParams(soyFile)) {
            requiredObjectTypes = this.getRequiredObjectTypes(soyFile);
            this.jsCodeBuilder.appendLine("goog.require('goog.asserts');");
        }
        if (this.jsSrcOptions.getUseGoogIsRtlForBidiGlobalDir()) {
            this.jsCodeBuilder.appendLine("goog.require('", GOOG_IS_RTL_NAMESPACE, "');");
        }
        if (this.hasNodeTypes(soyFile, MsgPluralNode.class, MsgSelectNode.class)) {
            this.jsCodeBuilder.appendLine("goog.require('", GOOG_MESSAGE_FORMAT_NAMESPACE, "');");
        }
        if (this.hasNodeTypes(soyFile, XidNode.class)) {
            this.jsCodeBuilder.appendLine("goog.require('xid');");
        }
        TreeSet pluginRequiredJsLibNames = Sets.newTreeSet();
        pluginRequiredJsLibNames.addAll(this.genDirectivePluginRequiresVisitor.exec(soyFile));
        pluginRequiredJsLibNames.addAll(this.genFunctionPluginRequiresVisitor.exec(soyFile));
        for (String namespace : pluginRequiredJsLibNames) {
            this.jsCodeBuilder.appendLine("goog.require('" + namespace + "');");
        }
        if (!requiredObjectTypes.isEmpty()) {
            this.jsCodeBuilder.appendLine(new String[0]);
            Iterator iterator = requiredObjectTypes.iterator();
            while (iterator.hasNext()) {
                String requiredType = (String)iterator.next();
                this.jsCodeBuilder.appendLine("goog.require('" + requiredType + "');");
            }
        }
    }

    private void addCodeToRequireSoyNamespaces(SoyFileNode soyFile) {
        String prevCalleeNamespace = null;
        TreeSet calleeNamespaces = Sets.newTreeSet();
        for (String calleeNotInFile : new FindCalleesNotInFileVisitor().exec(soyFile)) {
            int lastDotIndex = calleeNotInFile.lastIndexOf(46);
            if (lastDotIndex == -1) {
                throw SoySyntaxExceptionUtils.createWithNode("When using the option to provide/require Soy namespaces, found a called template \"" + calleeNotInFile + "\" that does not reside in a namespace.", soyFile);
            }
            calleeNamespaces.add(calleeNotInFile.substring(0, lastDotIndex));
        }
        for (String calleeNamespace : calleeNamespaces) {
            if (calleeNamespace.length() <= 0 || calleeNamespace.equals(prevCalleeNamespace)) continue;
            this.jsCodeBuilder.appendLine("goog.require('", calleeNamespace, "');");
            prevCalleeNamespace = calleeNamespace;
        }
    }

    private void addCodeToRequireJsFunctions(SoyFileNode soyFile) {
        for (String calleeNotInFile : new FindCalleesNotInFileVisitor().exec(soyFile)) {
            this.jsCodeBuilder.appendLine("goog.require('", calleeNotInFile, "');");
        }
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        boolean isCodeStyleStringbuilder = this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.STRINGBUILDER;
        boolean useStrongTyping = this.hasStrictParams(node);
        this.localVarTranslations = new ArrayDeque<Map<String, JsExpr>>();
        this.genJsExprsVisitor = this.genJsExprsVisitorFactory.create(this.localVarTranslations);
        this.assistantForMsgs = null;
        if (this.jsSrcOptions.shouldGenerateJsdoc()) {
            this.jsCodeBuilder.appendLine("/**");
            if (useStrongTyping) {
                this.genParamsRecordType(node);
            } else {
                this.jsCodeBuilder.appendLine(" * @param {Object.<string, *>=} opt_data");
            }
            if (isCodeStyleStringbuilder) {
                this.jsCodeBuilder.appendLine(" * @param {soy.StringBuilder=} opt_sb");
            } else {
                this.jsCodeBuilder.appendLine(" * @param {(null|undefined)=} opt_ignored");
            }
            if (this.isUsingIjData) {
                this.jsCodeBuilder.appendLine(" * @param {Object.<string, *>=} opt_ijData");
            }
            String returnType = node.getContentKind() == null ? "string" : "!" + NodeContentKinds.toJsSanitizedContentCtorName(node.getContentKind());
            this.jsCodeBuilder.appendLine(" * @return {", returnType, "}");
            String suppressions = "checkTypes|uselessCode";
            if (node instanceof TemplateBasicNode && ((TemplateBasicNode)node).isOverride()) {
                suppressions = suppressions + "|duplicate";
            }
            this.jsCodeBuilder.appendLine(" * @suppress {" + suppressions + "}");
            this.jsCodeBuilder.appendLine(" */");
        }
        this.jsCodeBuilder.appendLine(node.getTemplateName(), " = function(opt_data", isCodeStyleStringbuilder ? ", opt_sb" : ", opt_ignored", this.isUsingIjData ? ", opt_ijData" : "", ") {");
        this.jsCodeBuilder.increaseIndent();
        this.generateFunctionBody(node);
        this.jsCodeBuilder.decreaseIndent();
        this.jsCodeBuilder.appendLine("};");
        this.jsCodeBuilder.appendLine("if (goog.DEBUG) {");
        this.jsCodeBuilder.increaseIndent();
        this.jsCodeBuilder.appendLine(node.getTemplateName() + ".soyTemplateName = '" + node.getTemplateName() + "';");
        this.jsCodeBuilder.decreaseIndent();
        this.jsCodeBuilder.appendLine("}");
        if (node instanceof TemplateDelegateNode) {
            TemplateDelegateNode nodeAsDelTemplate = (TemplateDelegateNode)node;
            String delTemplateIdExprText = "soy.$$getDelTemplateId('" + nodeAsDelTemplate.getDelTemplateName() + "')";
            String delTemplateVariantExprText = "'" + nodeAsDelTemplate.getDelTemplateVariant() + "'";
            this.jsCodeBuilder.appendLine("soy.$$registerDelegateFn(", delTemplateIdExprText, ", ", delTemplateVariantExprText, ", ", Integer.toString(nodeAsDelTemplate.getDelPriority()), ", ", nodeAsDelTemplate.getTemplateName(), ");");
        }
    }

    private void generateFunctionBody(TemplateNode node) {
        JsExpr resultJsExpr;
        boolean isCodeStyleStringbuilder = this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.STRINGBUILDER;
        this.localVarTranslations.push(Maps.newHashMap());
        if (new ShouldEnsureDataIsDefinedVisitor().exec(node)) {
            this.jsCodeBuilder.appendLine("opt_data = opt_data || {};");
        }
        if (node.getParams() != null) {
            this.genParamTypeChecks(node);
        }
        if (!isCodeStyleStringbuilder && ((Boolean)this.isComputableAsJsExprsVisitor.exec(node)).booleanValue()) {
            List<JsExpr> templateBodyJsExprs = this.genJsExprsVisitor.exec(node);
            resultJsExpr = node.getContentKind() == null ? JsExprUtils.concatJsExprsForceString(templateBodyJsExprs) : JsExprUtils.concatJsExprs(templateBodyJsExprs);
        } else {
            this.jsCodeBuilder.pushOutputVar("output");
            if (isCodeStyleStringbuilder) {
                this.jsCodeBuilder.appendLine("var output = opt_sb || new soy.StringBuilder();");
                this.jsCodeBuilder.setOutputVarInited();
            }
            this.visitChildren(node);
            resultJsExpr = isCodeStyleStringbuilder ? new JsExpr("opt_sb ? '' : output.toString()", Integer.MAX_VALUE) : new JsExpr("output", Integer.MAX_VALUE);
            this.jsCodeBuilder.popOutputVar();
        }
        if (node.getContentKind() != null) {
            if (isCodeStyleStringbuilder) {
                throw SoySyntaxExceptionUtils.createWithNode("Soy's StringBuilder-based code generation mode does not currently support autoescape=\"strict\".", node);
            }
            resultJsExpr = JsExprUtils.maybeWrapAsSanitizedContent(node.getContentKind(), resultJsExpr);
        }
        this.jsCodeBuilder.appendLine("return ", resultJsExpr.getText(), ";");
        this.localVarTranslations.pop();
    }

    @Override
    protected void visitGoogMsgDefNode(GoogMsgDefNode node) {
        if (this.assistantForMsgs == null) {
            this.assistantForMsgs = new GenJsCodeVisitorAssistantForMsgs(this, this.jsSrcOptions, this.jsExprTranslator, this.genCallCodeUtils, this.isComputableAsJsExprsVisitor, this.jsCodeBuilder, this.localVarTranslations, this.genJsExprsVisitor);
        }
        this.assistantForMsgs.visitForUseByMaster(node);
    }

    @Override
    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        throw new AssertionError();
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        this.jsCodeBuilder.addToOutputVar(this.genJsExprsVisitor.exec(node));
    }

    @Override
    protected void visitLetValueNode(LetValueNode node) {
        String generatedVarName = node.getUniqueVarName();
        JsExpr valueJsExpr = this.jsExprTranslator.translateToJsExpr(node.getValueExpr(), null, this.localVarTranslations);
        this.jsCodeBuilder.appendLine("var ", generatedVarName, " = ", valueJsExpr.getText(), ";");
        this.localVarTranslations.peek().put(node.getVarName(), new JsExpr(generatedVarName, Integer.MAX_VALUE));
    }

    @Override
    protected void visitLetContentNode(LetContentNode node) {
        String generatedVarName = node.getUniqueVarName();
        this.localVarTranslations.push(Maps.newHashMap());
        this.jsCodeBuilder.pushOutputVar(generatedVarName);
        this.visitChildren(node);
        this.jsCodeBuilder.popOutputVar();
        this.localVarTranslations.pop();
        if (this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
            this.jsCodeBuilder.appendLine(generatedVarName, " = ", generatedVarName, ".toString();");
        }
        if (node.getContentKind() != null) {
            String sanitizedContentOrdainer = NodeContentKinds.toJsSanitizedContentOrdainerForInternalBlocks(node.getContentKind());
            this.jsCodeBuilder.appendLine(generatedVarName, " = ", sanitizedContentOrdainer, "(", generatedVarName, ");");
        }
        this.localVarTranslations.peek().put(node.getVarName(), new JsExpr(generatedVarName, Integer.MAX_VALUE));
    }

    @Override
    protected void visitIfNode(IfNode node) {
        if (((Boolean)this.isComputableAsJsExprsVisitor.exec(node)).booleanValue()) {
            this.jsCodeBuilder.addToOutputVar(this.genJsExprsVisitor.exec(node));
            return;
        }
        for (SoyNode child : node.getChildren()) {
            if (child instanceof IfCondNode) {
                IfCondNode icn = (IfCondNode)child;
                JsExpr condJsExpr = this.jsExprTranslator.translateToJsExpr(icn.getExprUnion().getExpr(), icn.getExprText(), this.localVarTranslations);
                if (icn.getCommandName().equals("if")) {
                    this.jsCodeBuilder.appendLine("if (", condJsExpr.getText(), ") {");
                } else {
                    this.jsCodeBuilder.appendLine("} else if (", condJsExpr.getText(), ") {");
                }
                this.jsCodeBuilder.increaseIndent();
                this.visit(icn);
                this.jsCodeBuilder.decreaseIndent();
                continue;
            }
            if (child instanceof IfElseNode) {
                IfElseNode ien = (IfElseNode)child;
                this.jsCodeBuilder.appendLine("} else {");
                this.jsCodeBuilder.increaseIndent();
                this.visit(ien);
                this.jsCodeBuilder.decreaseIndent();
                continue;
            }
            throw new AssertionError();
        }
        this.jsCodeBuilder.appendLine("}");
    }

    @Override
    protected void visitSwitchNode(SwitchNode node) {
        JsExpr switchValueJsExpr = this.jsExprTranslator.translateToJsExpr(node.getExpr(), node.getExprText(), this.localVarTranslations);
        this.jsCodeBuilder.appendLine("switch (", switchValueJsExpr.getText(), ") {");
        this.jsCodeBuilder.increaseIndent();
        for (SoyNode child : node.getChildren()) {
            if (child instanceof SwitchCaseNode) {
                SwitchCaseNode scn = (SwitchCaseNode)child;
                for (ExprNode exprNode : scn.getExprList()) {
                    JsExpr caseJsExpr = this.jsExprTranslator.translateToJsExpr(exprNode, null, this.localVarTranslations);
                    this.jsCodeBuilder.appendLine("case ", caseJsExpr.getText(), ":");
                }
                this.jsCodeBuilder.increaseIndent();
                this.visit(scn);
                this.jsCodeBuilder.appendLine("break;");
                this.jsCodeBuilder.decreaseIndent();
                continue;
            }
            if (child instanceof SwitchDefaultNode) {
                SwitchDefaultNode sdn = (SwitchDefaultNode)child;
                this.jsCodeBuilder.appendLine("default:");
                this.jsCodeBuilder.increaseIndent();
                this.visit(sdn);
                this.jsCodeBuilder.decreaseIndent();
                continue;
            }
            throw new AssertionError();
        }
        this.jsCodeBuilder.decreaseIndent();
        this.jsCodeBuilder.appendLine("}");
    }

    @Override
    protected void visitForeachNode(ForeachNode node) {
        boolean hasIfemptyNode;
        String baseVarName = node.getVarName();
        String nodeId = Integer.toString(node.getId());
        String listVarName = baseVarName + "List" + nodeId;
        String listLenVarName = baseVarName + "ListLen" + nodeId;
        JsExpr dataRefJsExpr = this.jsExprTranslator.translateToJsExpr(node.getExpr(), node.getExprText(), this.localVarTranslations);
        this.jsCodeBuilder.appendLine("var ", listVarName, " = ", dataRefJsExpr.getText(), ";");
        this.jsCodeBuilder.appendLine("var ", listLenVarName, " = ", listVarName, ".length;");
        boolean bl = hasIfemptyNode = node.numChildren() == 2;
        if (hasIfemptyNode) {
            this.jsCodeBuilder.appendLine("if (", listLenVarName, " > 0) {");
            this.jsCodeBuilder.increaseIndent();
        }
        this.visit((SoyNode)node.getChild(0));
        if (hasIfemptyNode) {
            this.jsCodeBuilder.decreaseIndent();
            this.jsCodeBuilder.appendLine("} else {");
            this.jsCodeBuilder.increaseIndent();
            this.visit((SoyNode)node.getChild(1));
            this.jsCodeBuilder.decreaseIndent();
            this.jsCodeBuilder.appendLine("}");
        }
    }

    @Override
    protected void visitForeachNonemptyNode(ForeachNonemptyNode node) {
        String baseVarName = node.getVarName();
        String foreachNodeId = Integer.toString(node.getForeachNodeId());
        String listVarName = baseVarName + "List" + foreachNodeId;
        String listLenVarName = baseVarName + "ListLen" + foreachNodeId;
        String indexVarName = baseVarName + "Index" + foreachNodeId;
        String dataVarName = baseVarName + "Data" + foreachNodeId;
        this.jsCodeBuilder.appendLine("for (var ", indexVarName, " = 0; ", indexVarName, " < ", listLenVarName, "; ", indexVarName, "++) {");
        this.jsCodeBuilder.increaseIndent();
        this.jsCodeBuilder.appendLine("var ", dataVarName, " = ", listVarName, "[", indexVarName, "];");
        HashMap newLocalVarTranslationsFrame = Maps.newHashMap();
        newLocalVarTranslationsFrame.put(baseVarName, new JsExpr(dataVarName, Integer.MAX_VALUE));
        newLocalVarTranslationsFrame.put(baseVarName + "__isFirst", new JsExpr(indexVarName + " == 0", Operator.EQUAL.getPrecedence()));
        newLocalVarTranslationsFrame.put(baseVarName + "__isLast", new JsExpr(indexVarName + " == " + listLenVarName + " - 1", Operator.EQUAL.getPrecedence()));
        newLocalVarTranslationsFrame.put(baseVarName + "__index", new JsExpr(indexVarName, Integer.MAX_VALUE));
        this.localVarTranslations.push(newLocalVarTranslationsFrame);
        this.visitChildren(node);
        this.localVarTranslations.pop();
        this.jsCodeBuilder.decreaseIndent();
        this.jsCodeBuilder.appendLine("}");
    }

    @Override
    protected void visitForNode(ForNode node) {
        String incrementCode;
        String limitCode;
        String initCode;
        String varName = node.getVarName();
        String nodeId = Integer.toString(node.getId());
        ArrayList rangeArgs = Lists.newArrayList(node.getRangeArgs());
        String incrementJsExprText = rangeArgs.size() == 3 ? this.jsExprTranslator.translateToJsExpr((ExprNode)rangeArgs.remove(2), null, this.localVarTranslations).getText() : "1";
        String initJsExprText = rangeArgs.size() == 2 ? this.jsExprTranslator.translateToJsExpr((ExprNode)rangeArgs.remove(0), null, this.localVarTranslations).getText() : "0";
        String limitJsExprText = this.jsExprTranslator.translateToJsExpr((ExprNode)rangeArgs.get(0), null, this.localVarTranslations).getText();
        if (INTEGER.matcher(initJsExprText).matches()) {
            initCode = initJsExprText;
        } else {
            initCode = varName + "Init" + nodeId;
            this.jsCodeBuilder.appendLine("var ", initCode, " = ", initJsExprText, ";");
        }
        if (INTEGER.matcher(limitJsExprText).matches()) {
            limitCode = limitJsExprText;
        } else {
            limitCode = varName + "Limit" + nodeId;
            this.jsCodeBuilder.appendLine("var ", limitCode, " = ", limitJsExprText, ";");
        }
        if (INTEGER.matcher(incrementJsExprText).matches()) {
            incrementCode = incrementJsExprText;
        } else {
            incrementCode = varName + "Increment" + nodeId;
            this.jsCodeBuilder.appendLine("var ", incrementCode, " = ", incrementJsExprText, ";");
        }
        String incrementStmt = incrementCode.equals("1") ? varName + nodeId + "++" : varName + nodeId + " += " + incrementCode;
        this.jsCodeBuilder.appendLine("for (var ", varName, nodeId, " = ", initCode, "; ", varName, nodeId, " < ", limitCode, "; ", incrementStmt, ") {");
        this.jsCodeBuilder.increaseIndent();
        HashMap newLocalVarTranslationsFrame = Maps.newHashMap();
        newLocalVarTranslationsFrame.put(varName, new JsExpr(varName + nodeId, Integer.MAX_VALUE));
        this.localVarTranslations.push(newLocalVarTranslationsFrame);
        this.visitChildren(node);
        this.localVarTranslations.pop();
        this.jsCodeBuilder.decreaseIndent();
        this.jsCodeBuilder.appendLine("}");
    }

    @Override
    protected void visitCallNode(CallNode node) {
        for (CallParamNode child : node.getChildren()) {
            if (!(child instanceof CallParamContentNode) || ((Boolean)this.isComputableAsJsExprsVisitor.exec(child)).booleanValue()) continue;
            this.visit(child);
        }
        if (this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
            this.genCallCodeUtils.genAndAppendCallStmt(this.jsCodeBuilder, node, this.localVarTranslations);
        } else {
            JsExpr callExpr = this.genCallCodeUtils.genCallExpr(node, this.localVarTranslations);
            this.jsCodeBuilder.addToOutputVar((List<JsExpr>)ImmutableList.of((Object)callExpr));
        }
    }

    @Override
    protected void visitCallParamContentNode(CallParamContentNode node) {
        if (((Boolean)this.isComputableAsJsExprsVisitor.exec(node)).booleanValue()) {
            throw new AssertionError((Object)"Should only define 'param<n>' when not computable as JS expressions.");
        }
        this.localVarTranslations.push(Maps.newHashMap());
        this.jsCodeBuilder.pushOutputVar("param" + node.getId());
        this.visitChildren(node);
        this.jsCodeBuilder.popOutputVar();
        this.localVarTranslations.pop();
    }

    @Override
    protected void visitLogNode(LogNode node) {
        if (this.isComputableAsJsExprsVisitor.execOnChildren(node).booleanValue()) {
            List<JsExpr> logMsgJsExprs = this.genJsExprsVisitor.execOnChildren(node);
            JsExpr logMsgJsExpr = JsExprUtils.concatJsExprs(logMsgJsExprs);
            this.jsCodeBuilder.appendLine("window.console.log(", logMsgJsExpr.getText(), ");");
        } else {
            this.localVarTranslations.push(Maps.newHashMap());
            this.jsCodeBuilder.pushOutputVar("logMsg_s" + node.getId());
            this.visitChildren(node);
            this.jsCodeBuilder.popOutputVar();
            this.localVarTranslations.pop();
            this.jsCodeBuilder.appendLine("window.console.log(logMsg_s", Integer.toString(node.getId()), ");");
        }
    }

    @Override
    protected void visitDebuggerNode(DebuggerNode node) {
        this.jsCodeBuilder.appendLine("debugger;");
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            if (node instanceof SoyNode.BlockNode) {
                this.localVarTranslations.push(Maps.newHashMap());
                this.visitChildren((SoyNode.BlockNode)node);
                this.localVarTranslations.pop();
            } else {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
            return;
        }
        if (!((Boolean)this.isComputableAsJsExprsVisitor.exec(node)).booleanValue()) {
            throw new UnsupportedOperationException();
        }
        this.jsCodeBuilder.addToOutputVar(this.genJsExprsVisitor.exec(node));
    }

    private void genParamsRecordType(TemplateNode node) {
        HashSet paramNames = Sets.newHashSet();
        StringBuilder sb = new StringBuilder();
        sb.append(" * @param {{");
        boolean first = true;
        for (TemplateParam param : node.getParams()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("\n *    ");
            sb.append(this.genParamAlias(param.name()));
            sb.append(": ");
            String jsType = this.genParamTypeExpr(param.type());
            if (jsType.equals("?")) {
                jsType = "(?)";
            }
            sb.append(jsType);
            paramNames.add(param.name());
        }
        FindIndirectParamsVisitor.IndirectParamsInfo ipi = new FindIndirectParamsVisitor(this.templateRegistry).exec(node);
        if (!ipi.mayHaveIndirectParamsInExternalCalls && !ipi.mayHaveIndirectParamsInExternalDelCalls) {
            for (String indirectParamName : ipi.indirectParamTypes.keySet()) {
                if (paramNames.contains(indirectParamName)) continue;
                Collection paramTypes = ipi.indirectParamTypes.get((Object)indirectParamName);
                SoyType combinedType = this.typeOps.computeLeastCommonType(paramTypes);
                UnionType indirectParamType = this.typeOps.getTypeRegistry().getOrCreateUnionType(combinedType, NullType.getInstance());
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append("\n *    ");
                sb.append(this.genParamAlias(indirectParamName));
                sb.append(": ");
                sb.append(this.genParamTypeExpr(indirectParamType));
            }
        }
        sb.append("\n * }} opt_data");
        this.jsCodeBuilder.appendLine(sb.toString());
    }

    private String genParamTypeExpr(SoyType type) {
        return JsSrcUtils.getJsTypeExpr(type, true, true);
    }

    private void genParamTypeChecks(TemplateNode node) {
        for (TemplateParam param : node.getParams()) {
            if (param.declLoc() != TemplateParam.DeclLoc.HEADER) continue;
            String paramName = ((HeaderParam)param).name();
            String paramVal = "opt_data" + TranslateToJsExprVisitor.genCodeForKeyAccess(paramName);
            String paramAlias = this.genParamAlias(paramName);
            boolean isAliasedLocalVar = false;
            switch (param.type().getKind()) {
                case ANY: 
                case UNKNOWN: {
                    break;
                }
                case STRING: {
                    this.genParamTypeChecksUsingGeneralAssert(paramName, paramAlias, paramVal, "goog.isString({0}) || ({0} instanceof goog.soy.data.SanitizedContent)", "string|goog.soy.data.SanitizedContent");
                    isAliasedLocalVar = true;
                    break;
                }
                case BOOL: 
                case INT: 
                case FLOAT: 
                case LIST: 
                case RECORD: 
                case MAP: {
                    String assertionFunction = null;
                    switch (param.type().getKind()) {
                        case BOOL: {
                            assertionFunction = "goog.asserts.assertBoolean";
                            break;
                        }
                        case INT: 
                        case FLOAT: {
                            assertionFunction = "goog.asserts.assertNumber";
                            break;
                        }
                        case LIST: {
                            assertionFunction = "goog.asserts.assertArray";
                            break;
                        }
                        case RECORD: 
                        case MAP: {
                            assertionFunction = "goog.asserts.assertObject";
                            break;
                        }
                        default: {
                            throw new AssertionError();
                        }
                    }
                    this.jsCodeBuilder.appendLine("var " + paramAlias + " = " + assertionFunction + "(" + paramVal + ", \"expected parameter '" + paramName + "' of type " + param.type().toString() + ".\");");
                    isAliasedLocalVar = true;
                    break;
                }
                case OBJECT: {
                    if (param.type() instanceof SoyProtoType) {
                        paramVal = this.extractProtoFromMap(paramVal);
                    }
                    this.jsCodeBuilder.appendLine("var " + paramName + " = goog.asserts.assertInstanceof(" + paramVal + ", " + JsSrcUtils.getJsTypeName(param.type()) + ", \"expected parameter '" + paramName + "' of type " + JsSrcUtils.getJsTypeName(param.type()) + ".\");");
                    isAliasedLocalVar = true;
                    break;
                }
                case ENUM: {
                    this.jsCodeBuilder.appendLine("var " + paramAlias + " = goog.asserts.assertNumber(" + paramVal + ", \"expected param '" + paramName + "' of type " + param.type().toString() + ".\");");
                    isAliasedLocalVar = true;
                    break;
                }
                case UNION: {
                    UnionType unionType = (UnionType)param.type();
                    if (this.containsProtoObjectType(unionType)) {
                        paramVal = this.extractProtoFromMap(paramVal);
                    }
                    this.genParamTypeChecksUsingGeneralAssert(paramName, paramAlias, paramVal, this.genUnionTypeTests(unionType), JsSrcUtils.getJsTypeExpr(param.type(), false, false));
                    isAliasedLocalVar = true;
                    break;
                }
                default: {
                    if (param.type() instanceof SanitizedType) {
                        String typeName = JsSrcUtils.getJsTypeName(param.type());
                        this.genParamTypeChecksUsingGeneralAssert(paramName, paramAlias, paramVal, "({0} instanceof " + typeName + ") || ({0} instanceof soydata.UnsanitizedText) || goog.isString({0})", typeName);
                        isAliasedLocalVar = true;
                        break;
                    }
                    throw new AssertionError((Object)("Unsupported type: " + param.type()));
                }
            }
            if (!isAliasedLocalVar) continue;
            this.localVarTranslations.peek().put(paramName, new JsExpr(paramAlias, Integer.MAX_VALUE));
        }
    }

    private String genUnionTypeTests(UnionType unionType) {
        TreeSet typeTests = Sets.newTreeSet();
        boolean hasNumber = false;
        block10: for (SoyType memberType : unionType.getMembers()) {
            switch (memberType.getKind()) {
                case ANY: 
                case UNKNOWN: {
                    typeTests.add("{0} != null");
                    continue block10;
                }
                case NULL: {
                    continue block10;
                }
                case BOOL: {
                    typeTests.add("goog.isBoolean({0})");
                    continue block10;
                }
                case STRING: {
                    typeTests.add("goog.isString({0})");
                    typeTests.add("({0} instanceof goog.soy.data.SanitizedContent)");
                    continue block10;
                }
                case INT: 
                case FLOAT: 
                case ENUM: {
                    if (hasNumber) continue block10;
                    typeTests.add("goog.isNumber({0})");
                    hasNumber = true;
                    continue block10;
                }
                case LIST: {
                    typeTests.add("goog.isArray({0})");
                    continue block10;
                }
                case RECORD: 
                case MAP: {
                    typeTests.add("goog.isObject({0})");
                    continue block10;
                }
                case OBJECT: {
                    String jsType = JsSrcUtils.getJsTypeName(memberType);
                    if (memberType instanceof SoyProtoType) {
                        if (unionType.isNullable()) {
                            typeTests.add("(({0}.$jspbMessageInstance || {0}) instanceof " + jsType + ")");
                            continue block10;
                        }
                        typeTests.add(this.extractProtoFromMap("{0}"));
                        continue block10;
                    }
                    typeTests.add("({0} instanceof " + jsType + ")");
                    continue block10;
                }
            }
            if (memberType instanceof SanitizedType) {
                typeTests.add("({0} instanceof " + JsSrcUtils.getJsTypeName(memberType) + ")");
                typeTests.add("({0} instanceof soydata.UnsanitizedText)");
                typeTests.add("goog.isString({0})");
                continue;
            }
            throw new AssertionError((Object)("Unsupported union member type: " + memberType));
        }
        String result = Joiner.on((String)" || ").join((Iterable)typeTests);
        if (unionType.isNullable()) {
            result = "{0} == null || " + result;
        }
        return result;
    }

    private void genParamTypeChecksUsingGeneralAssert(String paramName, String paramAlias, String paramVal, String typePredicate, String jsDocTypeExpr) {
        String paramAccessVal = TranslateToJsExprVisitor.genCodeForParamAccess(paramName);
        this.jsCodeBuilder.appendLine("goog.asserts.assert(" + MessageFormat.format(typePredicate, paramAccessVal) + ", \"expected param '" + paramName + "' of type " + jsDocTypeExpr + ".\");");
        this.jsCodeBuilder.appendLine("var " + paramAlias + " = /** @type {" + jsDocTypeExpr + "} */ (" + paramVal + ");");
    }

    private String genParamAlias(String paramName) {
        return JsSrcUtils.isReservedWord(paramName) ? "param$" + paramName : paramName;
    }

    private String extractProtoFromMap(String mapVal) {
        return "(" + mapVal + " && " + mapVal + ".$jspbMessageInstance || " + mapVal + ")";
    }

    private boolean hasNodeTypes(SoyFileNode soyFile, Class ... nodeTypes) {
        return new HasNodeTypesVisitor(nodeTypes).exec(soyFile);
    }

    private boolean containsProtoObjectType(UnionType unionType) {
        for (SoyType memberType : unionType.getMembers()) {
            if (memberType.getKind() != SoyType.Kind.OBJECT || !(memberType instanceof SoyProtoType)) continue;
            return true;
        }
        return false;
    }

    private boolean hasStrictParams(SoyFileNode soyFile) {
        for (TemplateNode template : soyFile.getChildren()) {
            if (!this.hasStrictParams(template)) continue;
            return true;
        }
        return false;
    }

    private boolean hasStrictParams(TemplateNode template) {
        if (template.getParams() != null) {
            for (TemplateParam param : template.getParams()) {
                if (param.declLoc() != TemplateParam.DeclLoc.HEADER) continue;
                return true;
            }
        }
        return false;
    }

    private SortedSet<String> getRequiredObjectTypes(SoyFileNode soyFile) {
        TreeSet requiredObjectTypes = Sets.newTreeSet();
        FieldImportsVisitor fieldImportsVisitor = new FieldImportsVisitor(requiredObjectTypes);
        for (TemplateNode template : soyFile.getChildren()) {
            SoytreeUtils.execOnAllV2Exprs(template, fieldImportsVisitor);
            if (template.getParams() == null) continue;
            for (TemplateParam param : template.getParams()) {
                if (param.declLoc() != TemplateParam.DeclLoc.HEADER) continue;
                if (param.type().getKind() == SoyType.Kind.OBJECT) {
                    requiredObjectTypes.add(JsSrcUtils.getJsTypeName(param.type()));
                    continue;
                }
                if (param.type().getKind() != SoyType.Kind.UNION) continue;
                UnionType union = (UnionType)param.type();
                for (SoyType memberType : union.getMembers()) {
                    if (memberType.getKind() != SoyType.Kind.OBJECT) continue;
                    requiredObjectTypes.add(JsSrcUtils.getJsTypeName(memberType));
                }
            }
        }
        return requiredObjectTypes;
    }

    private static class FieldImportsVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SortedSet<String> imports;

        public FieldImportsVisitor(SortedSet<String> imports) {
            this.imports = imports;
        }

        @Override
        public Void exec(ExprNode node) {
            this.visit(node);
            return null;
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }

        @Override
        protected void visitFieldAccessNode(FieldAccessNode node) {
            String importSymbol;
            SoyType baseType = node.getBaseExprChild().getType();
            if (baseType instanceof SoyObjectType && (importSymbol = ((SoyObjectType)baseType).getFieldImport(node.getFieldName(), SoyBackendKind.JS_SRC)) != null) {
                this.imports.add(importSymbol);
            }
            this.visit(node.getBaseExprChild());
        }
    }
}


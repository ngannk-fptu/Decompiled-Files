/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.inject.assistedinject.Assisted
 *  com.google.inject.assistedinject.AssistedInject
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.internal.GenCallCodeUtils;
import com.google.template.soy.jssrc.internal.IsComputableAsJsExprsVisitor;
import com.google.template.soy.jssrc.internal.JsExprTranslator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.XidNode;
import com.google.template.soy.soytree.jssrc.GoogMsgRefNode;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class GenJsExprsVisitor
extends AbstractSoyNodeVisitor<List<JsExpr>> {
    Map<String, SoyJsSrcPrintDirective> soyJsSrcDirectivesMap;
    private final JsExprTranslator jsExprTranslator;
    private final GenCallCodeUtils genCallCodeUtils;
    private final IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor;
    private final GenJsExprsVisitorFactory genJsExprsVisitorFactory;
    private final Deque<Map<String, JsExpr>> localVarTranslations;
    private List<JsExpr> jsExprs;

    @AssistedInject
    GenJsExprsVisitor(Map<String, SoyJsSrcPrintDirective> soyJsSrcDirectivesMap, JsExprTranslator jsExprTranslator, GenCallCodeUtils genCallCodeUtils, IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor, GenJsExprsVisitorFactory genJsExprsVisitorFactory, @Assisted Deque<Map<String, JsExpr>> localVarTranslations) {
        this.soyJsSrcDirectivesMap = soyJsSrcDirectivesMap;
        this.jsExprTranslator = jsExprTranslator;
        this.genCallCodeUtils = genCallCodeUtils;
        this.isComputableAsJsExprsVisitor = isComputableAsJsExprsVisitor;
        this.genJsExprsVisitorFactory = genJsExprsVisitorFactory;
        this.localVarTranslations = localVarTranslations;
    }

    @Override
    public List<JsExpr> exec(SoyNode node) {
        Preconditions.checkArgument((boolean)((Boolean)this.isComputableAsJsExprsVisitor.exec(node)));
        this.jsExprs = Lists.newArrayList();
        this.visit(node);
        return this.jsExprs;
    }

    public List<JsExpr> execOnChildren(SoyNode.ParentSoyNode<?> node) {
        Preconditions.checkArgument((boolean)this.isComputableAsJsExprsVisitor.execOnChildren(node));
        this.jsExprs = Lists.newArrayList();
        this.visitChildren(node);
        return this.jsExprs;
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitRawTextNode(RawTextNode node) {
        String exprText = BaseUtils.escapeToSoyString(node.getRawText(), true);
        exprText = exprText.replace("</script>", "<\\/script>");
        this.jsExprs.add(new JsExpr(exprText, Integer.MAX_VALUE));
    }

    @Override
    protected void visitMsgPlaceholderNode(MsgPlaceholderNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitGoogMsgRefNode(GoogMsgRefNode node) {
        this.jsExprs.add(new JsExpr(node.getRenderedGoogMsgVarName(), Integer.MAX_VALUE));
    }

    @Override
    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        JsExpr jsExpr = this.jsExprTranslator.translateToJsExpr(node.getExprUnion().getExpr(), node.getExprText(), this.localVarTranslations);
        for (PrintDirectiveNode directiveNode : node.getChildren()) {
            SoyJsSrcPrintDirective directive = this.soyJsSrcDirectivesMap.get(directiveNode.getName());
            if (directive == null) {
                throw SoySyntaxExceptionUtils.createWithNode("Failed to find SoyJsSrcPrintDirective with name '" + directiveNode.getName() + "' (tag " + node.toSourceString() + ")", directiveNode);
            }
            List<ExprRootNode<?>> args = directiveNode.getArgs();
            if (!directive.getValidArgsSizes().contains(args.size())) {
                throw SoySyntaxExceptionUtils.createWithNode("Print directive '" + directiveNode.getName() + "' used with the wrong number of arguments (tag " + node.toSourceString() + ").", directiveNode);
            }
            ArrayList argsJsExprs = Lists.newArrayListWithCapacity((int)args.size());
            for (ExprRootNode<?> arg : args) {
                argsJsExprs.add(this.jsExprTranslator.translateToJsExpr(arg, null, this.localVarTranslations));
            }
            jsExpr = directive.applyForJsSrc(jsExpr, argsJsExprs);
        }
        this.jsExprs.add(jsExpr);
    }

    @Override
    protected void visitXidNode(XidNode node) {
        this.jsExprs.add(new JsExpr("xid('" + node.getText() + "')", Integer.MAX_VALUE));
    }

    @Override
    protected void visitCssNode(CssNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("goog.getCssName(");
        ExprRootNode<?> componentNameExpr = node.getComponentNameExpr();
        if (componentNameExpr != null) {
            JsExpr baseJsExpr = this.jsExprTranslator.translateToJsExpr(componentNameExpr, node.getComponentNameText(), this.localVarTranslations);
            sb.append(baseJsExpr.getText()).append(", ");
        }
        sb.append('\'').append(node.getSelectorText()).append("')");
        this.jsExprs.add(new JsExpr(sb.toString(), Integer.MAX_VALUE));
    }

    @Override
    protected void visitIfNode(IfNode node) {
        GenJsExprsVisitor genJsExprsVisitor = this.genJsExprsVisitorFactory.create(this.localVarTranslations);
        StringBuilder jsExprTextSb = new StringBuilder();
        boolean hasElse = false;
        for (SoyNode child : node.getChildren()) {
            if (child instanceof IfCondNode) {
                IfCondNode icn = (IfCondNode)child;
                JsExpr condJsExpr = this.jsExprTranslator.translateToJsExpr(icn.getExprUnion().getExpr(), icn.getExprText(), this.localVarTranslations);
                jsExprTextSb.append('(').append(condJsExpr.getText()).append(") ? ");
                List<JsExpr> condBlockJsExprs = genJsExprsVisitor.exec(icn);
                jsExprTextSb.append(JsExprUtils.concatJsExprs(condBlockJsExprs).getText());
                jsExprTextSb.append(" : ");
                continue;
            }
            if (child instanceof IfElseNode) {
                hasElse = true;
                IfElseNode ien = (IfElseNode)child;
                List<JsExpr> elseBlockJsExprs = genJsExprsVisitor.exec(ien);
                jsExprTextSb.append(JsExprUtils.concatJsExprs(elseBlockJsExprs).getText());
                continue;
            }
            throw new AssertionError();
        }
        if (!hasElse) {
            jsExprTextSb.append("''");
        }
        this.jsExprs.add(new JsExpr(jsExprTextSb.toString(), Operator.CONDITIONAL.getPrecedence()));
    }

    @Override
    protected void visitIfCondNode(IfCondNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitIfElseNode(IfElseNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitCallNode(CallNode node) {
        this.jsExprs.add(this.genCallCodeUtils.genCallExpr(node, this.localVarTranslations));
    }

    @Override
    protected void visitCallParamContentNode(CallParamContentNode node) {
        this.visitChildren(node);
    }

    public static interface GenJsExprsVisitorFactory {
        public GenJsExprsVisitor create(Deque<Map<String, JsExpr>> var1);
    }
}


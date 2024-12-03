/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.Inject
 *  javax.annotation.Nullable
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.GenJsExprsVisitor;
import com.google.template.soy.jssrc.internal.IsComputableAsJsExprsVisitor;
import com.google.template.soy.jssrc.internal.JsCodeBuilder;
import com.google.template.soy.jssrc.internal.JsExprTranslator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CallParamValueNode;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

class GenCallCodeUtils {
    private final Map<String, SoyJsSrcPrintDirective> soyJsSrcDirectivesMap;
    private final SoyJsSrcOptions jsSrcOptions;
    private final boolean isUsingIjData;
    private final JsExprTranslator jsExprTranslator;
    private final IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor;
    private final GenJsExprsVisitor.GenJsExprsVisitorFactory genJsExprsVisitorFactory;

    @Inject
    GenCallCodeUtils(Map<String, SoyJsSrcPrintDirective> soyJsSrcDirectivesMap, SoyJsSrcOptions jsSrcOptions, @ApiCallScopeBindingAnnotations.IsUsingIjData boolean isUsingIjData, JsExprTranslator jsExprTranslator, IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor, GenJsExprsVisitor.GenJsExprsVisitorFactory genJsExprsVisitorFactory) {
        this.jsSrcOptions = jsSrcOptions;
        this.isUsingIjData = isUsingIjData;
        this.jsExprTranslator = jsExprTranslator;
        this.isComputableAsJsExprsVisitor = isComputableAsJsExprsVisitor;
        this.genJsExprsVisitorFactory = genJsExprsVisitorFactory;
        this.soyJsSrcDirectivesMap = soyJsSrcDirectivesMap;
    }

    public JsExpr genCallExpr(CallNode callNode, Deque<Map<String, JsExpr>> localVarTranslations) {
        return this.genCallExprHelper(callNode, localVarTranslations, null);
    }

    public void genAndAppendCallStmt(JsCodeBuilder jsCodeBuilder, CallNode callNode, Deque<Map<String, JsExpr>> localVarTranslations) {
        if (this.jsSrcOptions.getCodeStyle() != SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
            throw new AssertionError();
        }
        JsExpr callExpr = this.genCallExprHelper(callNode, localVarTranslations, jsCodeBuilder.getOutputVarName());
        jsCodeBuilder.appendLine(callExpr.getText(), ";");
    }

    private JsExpr genCallExprHelper(CallNode callNode, Deque<Map<String, JsExpr>> localVarTranslations, @Nullable String outputVarNameForStringbuilder) {
        String calleeExprText;
        JsExpr objToPass = this.genObjToPass(callNode, localVarTranslations);
        if (callNode instanceof CallBasicNode) {
            calleeExprText = ((CallBasicNode)callNode).getCalleeName();
        } else {
            String variantJsExprText;
            CallDelegateNode callDelegateNode = (CallDelegateNode)callNode;
            String calleeIdExprText = "soy.$$getDelTemplateId('" + callDelegateNode.getDelCalleeName() + "')";
            ExprRootNode<?> variantSoyExpr = callDelegateNode.getDelCalleeVariantExpr();
            if (variantSoyExpr == null) {
                variantJsExprText = "''";
            } else {
                JsExpr variantJsExpr = this.jsExprTranslator.translateToJsExpr(variantSoyExpr, variantSoyExpr.toSourceString(), localVarTranslations);
                variantJsExprText = variantJsExpr.getText();
            }
            calleeExprText = "soy.$$getDelegateFn(" + calleeIdExprText + ", " + variantJsExprText + ", " + (callDelegateNode.allowsEmptyDefault() ? "true" : "false") + ")";
        }
        String callExprText = outputVarNameForStringbuilder != null ? calleeExprText + "(" + objToPass.getText() + ", " + outputVarNameForStringbuilder + (this.isUsingIjData ? ", opt_ijData" : "") + ")" : calleeExprText + "(" + objToPass.getText() + (this.isUsingIjData ? ", null, opt_ijData" : "") + ")";
        JsExpr result = new JsExpr(callExprText, Integer.MAX_VALUE);
        for (String directiveName : callNode.getEscapingDirectiveNames()) {
            SoyJsSrcPrintDirective directive = this.soyJsSrcDirectivesMap.get(directiveName);
            Preconditions.checkNotNull((Object)directive, (Object)("Contextual autoescaping produced a bogus directive: " + directiveName));
            result = directive.applyForJsSrc(result, (List<JsExpr>)ImmutableList.of());
        }
        return result;
    }

    public JsExpr genObjToPass(CallNode callNode, Deque<Map<String, JsExpr>> localVarTranslations) {
        JsExpr dataToPass = callNode.isPassingAllData() ? new JsExpr("opt_data", Integer.MAX_VALUE) : (callNode.isPassingData() ? this.jsExprTranslator.translateToJsExpr(callNode.getDataExpr(), null, localVarTranslations) : new JsExpr("null", Integer.MAX_VALUE));
        if (callNode.numChildren() == 0) {
            return dataToPass;
        }
        StringBuilder paramsObjSb = new StringBuilder();
        paramsObjSb.append('{');
        boolean isFirst = true;
        for (CallParamNode child : callNode.getChildren()) {
            JsExpr valueJsExpr;
            if (isFirst) {
                isFirst = false;
            } else {
                paramsObjSb.append(", ");
            }
            String key = child.getKey();
            paramsObjSb.append(key).append(": ");
            if (child instanceof CallParamValueNode) {
                CallParamValueNode cpvn = (CallParamValueNode)child;
                valueJsExpr = this.jsExprTranslator.translateToJsExpr(cpvn.getValueExprUnion().getExpr(), cpvn.getValueExprText(), localVarTranslations);
                paramsObjSb.append(valueJsExpr.getText());
                continue;
            }
            CallParamContentNode cpcn = (CallParamContentNode)child;
            if (((Boolean)this.isComputableAsJsExprsVisitor.exec(cpcn)).booleanValue()) {
                valueJsExpr = JsExprUtils.concatJsExprsForceString(this.genJsExprsVisitorFactory.create(localVarTranslations).exec(cpcn));
            } else {
                String paramExpr = "param" + cpcn.getId();
                if (this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.STRINGBUILDER) {
                    paramExpr = paramExpr + ".toString()";
                }
                valueJsExpr = new JsExpr(paramExpr, Integer.MAX_VALUE);
            }
            valueJsExpr = JsExprUtils.maybeWrapAsSanitizedContentForInternalBlocks(cpcn.getContentKind(), valueJsExpr);
            paramsObjSb.append(valueJsExpr.getText());
        }
        paramsObjSb.append('}');
        if (callNode.isPassingData()) {
            return new JsExpr("soy.$$augmentMap(" + dataToPass.getText() + ", " + paramsObjSb.toString() + ")", Integer.MAX_VALUE);
        }
        return new JsExpr(paramsObjSb.toString(), Integer.MAX_VALUE);
    }
}


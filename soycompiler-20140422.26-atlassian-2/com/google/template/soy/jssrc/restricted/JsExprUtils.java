/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.jssrc.restricted;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import java.util.List;
import javax.annotation.Nullable;

public class JsExprUtils {
    private static final JsExpr EMPTY_STRING = new JsExpr("''", Integer.MAX_VALUE);

    private JsExprUtils() {
    }

    public static JsExpr concatJsExprs(List<JsExpr> jsExprs) {
        if (jsExprs.size() == 0) {
            return EMPTY_STRING;
        }
        if (jsExprs.size() == 1) {
            return jsExprs.get(0);
        }
        int plusOpPrec = Operator.PLUS.getPrecedence();
        StringBuilder resultSb = new StringBuilder();
        boolean isFirst = true;
        for (JsExpr jsExpr : jsExprs) {
            boolean needsProtection;
            boolean bl = isFirst ? jsExpr.getPrecedence() < plusOpPrec : (needsProtection = jsExpr.getPrecedence() <= plusOpPrec);
            if (isFirst) {
                isFirst = false;
            } else {
                resultSb.append(" + ");
            }
            if (needsProtection) {
                resultSb.append('(').append(jsExpr.getText()).append(')');
                continue;
            }
            resultSb.append(jsExpr.getText());
        }
        return new JsExpr(resultSb.toString(), plusOpPrec);
    }

    public static JsExpr concatJsExprsForceString(List<JsExpr> jsExprs) {
        if (jsExprs.size() > 0 && JsExprUtils.isStringLiteral(jsExprs.get(0)) || jsExprs.size() > 1 && JsExprUtils.isStringLiteral(jsExprs.get(1))) {
            return JsExprUtils.concatJsExprs(jsExprs);
        }
        return JsExprUtils.concatJsExprs((List<JsExpr>)ImmutableList.builder().add((Object)EMPTY_STRING).addAll(jsExprs).build());
    }

    @VisibleForTesting
    static boolean isStringLiteral(JsExpr jsExpr) {
        String jsExprText = jsExpr.getText();
        int jsExprTextLastIndex = jsExprText.length() - 1;
        if (jsExprTextLastIndex < 1 || jsExprText.charAt(0) != '\'' || jsExprText.charAt(jsExprTextLastIndex) != '\'') {
            return false;
        }
        for (int i = 1; i < jsExprTextLastIndex; ++i) {
            char c = jsExprText.charAt(i);
            if (c == '\'') {
                return false;
            }
            if (c != '\\') continue;
            ++i;
        }
        return true;
    }

    public static JsExpr toString(JsExpr expr) {
        return JsExprUtils.concatJsExprsForceString((List<JsExpr>)ImmutableList.of((Object)expr));
    }

    @VisibleForTesting
    static JsExpr wrapWithFunction(String functionExprText, JsExpr jsExpr) {
        Preconditions.checkNotNull((Object)functionExprText);
        return new JsExpr(functionExprText + "(" + jsExpr.getText() + ")", Integer.MAX_VALUE);
    }

    public static JsExpr maybeWrapAsSanitizedContent(@Nullable SanitizedContent.ContentKind contentKind, JsExpr jsExpr) {
        if (contentKind == null) {
            return jsExpr;
        }
        return JsExprUtils.wrapWithFunction(NodeContentKinds.toJsSanitizedContentOrdainer(contentKind), jsExpr);
    }

    public static JsExpr maybeWrapAsSanitizedContentForInternalBlocks(@Nullable SanitizedContent.ContentKind contentKind, JsExpr jsExpr) {
        if (contentKind == null) {
            return jsExpr;
        }
        return JsExprUtils.wrapWithFunction(NodeContentKinds.toJsSanitizedContentOrdainerForInternalBlocks(contentKind), jsExpr);
    }
}


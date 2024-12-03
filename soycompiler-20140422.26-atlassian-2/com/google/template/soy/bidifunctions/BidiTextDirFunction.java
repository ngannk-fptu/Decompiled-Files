/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 */
package com.google.template.soy.bidifunctions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.internal.i18n.BidiUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
class BidiTextDirFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    BidiTextDirFunction() {
    }

    @Override
    public String getName() {
        return "bidiTextDir";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1, (Object)2);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SanitizedContent sanitizedContent;
        SoyValue value = args.get(0);
        Dir valueDir = null;
        boolean isHtmlForValueDirEstimation = false;
        if (value instanceof SanitizedContent && (valueDir = (sanitizedContent = (SanitizedContent)value).getContentDirection()) == null) {
            boolean bl = isHtmlForValueDirEstimation = sanitizedContent.getContentKind() == SanitizedContent.ContentKind.HTML;
        }
        if (valueDir == null) {
            isHtmlForValueDirEstimation = isHtmlForValueDirEstimation || args.size() == 2 && args.get(1).booleanValue();
            valueDir = BidiUtils.estimateDirection(value.coerceToString(), isHtmlForValueDirEstimation);
        }
        return IntegerData.forValue(valueDir.ord);
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr value = args.get(0);
        JsExpr isHtml = args.size() == 2 ? args.get(1) : null;
        String callText = isHtml != null ? "soy.$$bidiTextDir(" + value.getText() + ", " + isHtml.getText() + ")" : "soy.$$bidiTextDir(" + value.getText() + ")";
        return new JsExpr(callText, Integer.MAX_VALUE);
    }
}


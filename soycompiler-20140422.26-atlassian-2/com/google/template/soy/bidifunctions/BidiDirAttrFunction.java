/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Provider
 *  com.google.inject.Singleton
 */
package com.google.template.soy.bidifunctions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.internal.i18n.BidiFormatter;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.internal.i18n.BidiUtils;
import com.google.template.soy.internal.i18n.SoyBidiUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
class BidiDirAttrFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    private final Provider<BidiGlobalDir> bidiGlobalDirProvider;

    @Inject
    BidiDirAttrFunction(Provider<BidiGlobalDir> bidiGlobalDirProvider) {
        this.bidiGlobalDirProvider = bidiGlobalDirProvider;
    }

    @Override
    public String getName() {
        return "bidiDirAttr";
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
        BidiFormatter bidiFormatter = SoyBidiUtils.getBidiFormatter(((BidiGlobalDir)this.bidiGlobalDirProvider.get()).getStaticValue());
        String dirAttr = bidiFormatter.knownDirAttr(valueDir);
        return UnsafeSanitizedContentOrdainer.ordainAsSafe(dirAttr, SanitizedContent.ContentKind.ATTRIBUTES);
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr value = args.get(0);
        JsExpr isHtml = args.size() == 2 ? args.get(1) : null;
        String callText = "soy.$$bidiDirAttr(" + ((BidiGlobalDir)this.bidiGlobalDirProvider.get()).getCodeSnippet() + ", " + value.getText() + (isHtml != null ? ", " + isHtml.getText() : "") + ")";
        return new JsExpr(callText, Integer.MAX_VALUE);
    }
}


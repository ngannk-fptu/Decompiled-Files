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
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.internal.i18n.SoyBidiUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
class BidiMarkAfterFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    private final Provider<BidiGlobalDir> bidiGlobalDirProvider;

    @Inject
    BidiMarkAfterFunction(Provider<BidiGlobalDir> bidiGlobalDirProvider) {
        this.bidiGlobalDirProvider = bidiGlobalDirProvider;
    }

    @Override
    public String getName() {
        return "bidiMarkAfter";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1, (Object)2);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue value = args.get(0);
        boolean isHtml = args.size() == 2 && args.get(1).booleanValue();
        Dir valueDir = null;
        if (value instanceof SanitizedContent) {
            SanitizedContent sanitizedContent = (SanitizedContent)value;
            valueDir = sanitizedContent.getContentDirection();
            isHtml = isHtml || sanitizedContent.getContentKind() == SanitizedContent.ContentKind.HTML;
        }
        int bidiGlobalDir = ((BidiGlobalDir)this.bidiGlobalDirProvider.get()).getStaticValue();
        return StringData.forValue(SoyBidiUtils.getBidiFormatter(bidiGlobalDir).markAfterKnownDir(valueDir, value.coerceToString(), isHtml));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr value = args.get(0);
        JsExpr isHtml = args.size() == 2 ? args.get(1) : null;
        String callText = "soy.$$bidiMarkAfter(" + ((BidiGlobalDir)this.bidiGlobalDirProvider.get()).getCodeSnippet() + ", " + value.getText() + (isHtml != null ? ", " + isHtml.getText() : "") + ")";
        return new JsExpr(callText, Integer.MAX_VALUE);
    }
}


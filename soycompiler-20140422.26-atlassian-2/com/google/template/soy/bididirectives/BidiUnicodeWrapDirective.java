/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Provider
 *  com.google.inject.Singleton
 */
package com.google.template.soy.bididirectives;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.internal.i18n.BidiFormatter;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.internal.i18n.SoyBidiUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import java.util.List;
import java.util.Set;

@Singleton
public class BidiUnicodeWrapDirective
implements SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    private final Provider<BidiGlobalDir> bidiGlobalDirProvider;

    @Inject
    BidiUnicodeWrapDirective(Provider<BidiGlobalDir> bidiGlobalDirProvider) {
        this.bidiGlobalDirProvider = bidiGlobalDirProvider;
    }

    @Override
    public String getName() {
        return "|bidiUnicodeWrap";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)0);
    }

    @Override
    public boolean shouldCancelAutoescape() {
        return false;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        SanitizedContent.ContentKind valueKind = null;
        Dir valueDir = null;
        if (value instanceof SanitizedContent) {
            SanitizedContent sanitizedContent = (SanitizedContent)value;
            valueKind = sanitizedContent.getContentKind();
            valueDir = sanitizedContent.getContentDirection();
        }
        BidiFormatter bidiFormatter = SoyBidiUtils.getBidiFormatter(((BidiGlobalDir)this.bidiGlobalDirProvider.get()).getStaticValue());
        boolean isHtml = valueKind == SanitizedContent.ContentKind.HTML;
        String wrappedValue = bidiFormatter.unicodeWrapWithKnownDir(valueDir, value.coerceToString(), isHtml);
        Dir wrappedValueDir = bidiFormatter.getContextDir();
        if (valueKind == SanitizedContent.ContentKind.TEXT || valueKind == SanitizedContent.ContentKind.HTML || valueKind == SanitizedContent.ContentKind.JS_STR_CHARS) {
            return UnsafeSanitizedContentOrdainer.ordainAsSafe(wrappedValue, valueKind, wrappedValueDir);
        }
        if (valueKind != null) {
            return StringData.forValue(wrappedValue);
        }
        return StringData.forValue(wrappedValue);
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        String codeSnippet = ((BidiGlobalDir)this.bidiGlobalDirProvider.get()).getCodeSnippet();
        return new JsExpr("soy.$$bidiUnicodeWrap(" + codeSnippet + ", " + value.getText() + ")", Integer.MAX_VALUE);
    }
}


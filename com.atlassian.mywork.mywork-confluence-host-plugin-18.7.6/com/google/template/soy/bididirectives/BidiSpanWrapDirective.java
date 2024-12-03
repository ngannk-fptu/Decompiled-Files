/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.bididirectives;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContentOperator;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.internal.i18n.BidiFormatter;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.internal.i18n.SoyBidiUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

@Singleton
public class BidiSpanWrapDirective
implements SanitizedContentOperator,
SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    private final Provider<BidiGlobalDir> bidiGlobalDirProvider;

    @Inject
    BidiSpanWrapDirective(Provider<BidiGlobalDir> bidiGlobalDirProvider) {
        this.bidiGlobalDirProvider = bidiGlobalDirProvider;
    }

    @Override
    public String getName() {
        return "|bidiSpanWrap";
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
    @Nonnull
    public SanitizedContent.ContentKind getContentKind() {
        return SanitizedContent.ContentKind.HTML;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        Dir valueDir = null;
        if (value instanceof SanitizedContent) {
            valueDir = ((SanitizedContent)value).getContentDirection();
        }
        BidiFormatter bidiFormatter = SoyBidiUtils.getBidiFormatter(this.bidiGlobalDirProvider.get().getStaticValue());
        String wrappedValue = bidiFormatter.spanWrapWithKnownDir(valueDir, value.coerceToString(), true);
        return StringData.forValue(wrappedValue);
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        String codeSnippet = this.bidiGlobalDirProvider.get().getCodeSnippet();
        return new JsExpr("soy.$$bidiSpanWrap(" + codeSnippet + ", " + value.getText() + ")", Integer.MAX_VALUE);
    }
}


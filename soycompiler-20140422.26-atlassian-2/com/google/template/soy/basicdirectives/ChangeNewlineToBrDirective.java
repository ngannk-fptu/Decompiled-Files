/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.basicdirectives;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContentOperator;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

@Singleton
@SoyPurePrintDirective
public class ChangeNewlineToBrDirective
implements SanitizedContentOperator,
SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r\\n|\\r|\\n");

    @Inject
    public ChangeNewlineToBrDirective() {
    }

    @Override
    public String getName() {
        return "|changeNewlineToBr";
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
        SanitizedContent sanitizedContent;
        String result = NEWLINE_PATTERN.matcher(value.coerceToString()).replaceAll("<br>");
        if (value instanceof SanitizedContent && (sanitizedContent = (SanitizedContent)value).getContentKind() == SanitizedContent.ContentKind.HTML) {
            return UnsafeSanitizedContentOrdainer.ordainAsSafe(result, SanitizedContent.ContentKind.HTML, sanitizedContent.getContentDirection());
        }
        return StringData.forValue(result);
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        return new JsExpr("soy.$$changeNewlineToBr(" + value.getText() + ")", Integer.MAX_VALUE);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.basicdirectives;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContentOperator;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

@Singleton
@SoyPurePrintDirective
public class InsertWordBreaksDirective
implements SanitizedContentOperator,
SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    @Inject
    InsertWordBreaksDirective() {
    }

    @Override
    public String getName() {
        return "|insertWordBreaks";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
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
        int codePoint;
        int maxCharsBetweenWordBreaks;
        try {
            maxCharsBetweenWordBreaks = args.get(0).integerValue();
        }
        catch (SoyDataException sde) {
            throw new IllegalArgumentException("Could not parse 'insertWordBreaks' parameter as integer.");
        }
        StringBuilder result = new StringBuilder();
        boolean isInTag = false;
        boolean isMaybeInEntity = false;
        int numCharsWithoutBreak = 0;
        String str = value.coerceToString();
        int n = str.length();
        for (int i = 0; i < n; i += Character.charCount(codePoint)) {
            codePoint = str.codePointAt(i);
            if (numCharsWithoutBreak >= maxCharsBetweenWordBreaks && codePoint != 32) {
                result.append("<wbr>");
                numCharsWithoutBreak = 0;
            }
            if (isInTag) {
                if (codePoint == 62) {
                    isInTag = false;
                }
            } else if (isMaybeInEntity) {
                switch (codePoint) {
                    case 59: {
                        isMaybeInEntity = false;
                        ++numCharsWithoutBreak;
                        break;
                    }
                    case 60: {
                        isMaybeInEntity = false;
                        isInTag = true;
                        break;
                    }
                    case 32: {
                        isMaybeInEntity = false;
                        numCharsWithoutBreak = 0;
                    }
                }
            } else {
                switch (codePoint) {
                    case 60: {
                        isInTag = true;
                        break;
                    }
                    case 38: {
                        isMaybeInEntity = true;
                        break;
                    }
                    case 32: {
                        numCharsWithoutBreak = 0;
                        break;
                    }
                    default: {
                        ++numCharsWithoutBreak;
                    }
                }
            }
            result.appendCodePoint(codePoint);
        }
        if (value instanceof SanitizedContent && (sanitizedContent = (SanitizedContent)value).getContentKind() == SanitizedContent.ContentKind.HTML) {
            return UnsafeSanitizedContentOrdainer.ordainAsSafe(result.toString(), SanitizedContent.ContentKind.HTML, sanitizedContent.getContentDirection());
        }
        return StringData.forValue(result.toString());
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        return new JsExpr("soy.$$insertWordBreaks(" + value.getText() + ", " + args.get(0).getText() + ")", Integer.MAX_VALUE);
    }
}


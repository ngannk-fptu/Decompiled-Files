/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.basicdirectives;

import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SoyPurePrintDirective
public class TruncateDirective
implements SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    @Inject
    public TruncateDirective() {
    }

    @Override
    public String getName() {
        return "|truncate";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1, (Object)2);
    }

    @Override
    public boolean shouldCancelAutoescape() {
        return false;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        boolean doAddEllipsis;
        int maxLen;
        try {
            maxLen = args.get(0).integerValue();
        }
        catch (SoyDataException sde) {
            throw new IllegalArgumentException("Could not parse first parameter of '|truncate' as integer (value was \"" + args.get(0).stringValue() + "\").");
        }
        String str = value.coerceToString();
        if (str.length() <= maxLen) {
            return StringData.forValue(str);
        }
        if (args.size() == 2) {
            try {
                doAddEllipsis = args.get(1).booleanValue();
            }
            catch (SoyDataException sde) {
                throw new IllegalArgumentException("Could not parse second parameter of '|truncate' as boolean.");
            }
        } else {
            doAddEllipsis = true;
        }
        if (doAddEllipsis) {
            if (maxLen > 3) {
                maxLen -= 3;
            } else {
                doAddEllipsis = false;
            }
        }
        if (Character.isHighSurrogate(str.charAt(maxLen - 1)) && Character.isLowSurrogate(str.charAt(maxLen))) {
            --maxLen;
        }
        str = str.substring(0, maxLen);
        if (doAddEllipsis) {
            str = str + "...";
        }
        return StringData.forValue(str);
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        String maxLenExprText = args.get(0).getText();
        String doAddEllipsisExprText = args.size() == 2 ? args.get(1).getText() : "true";
        return new JsExpr("soy.$$truncate(" + value.getText() + ", " + maxLenExprText + ", " + doAddEllipsisExprText + ")", Integer.MAX_VALUE);
    }
}


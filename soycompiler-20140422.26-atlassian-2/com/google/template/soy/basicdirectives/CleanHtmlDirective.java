/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package com.google.template.soy.basicdirectives;

import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.Sanitizers;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SoyPurePrintDirective
public class CleanHtmlDirective
implements SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    private static final Set<Integer> VALID_ARGS_SIZES = ImmutableSet.of((Object)0);

    @Inject
    public CleanHtmlDirective() {
    }

    @Override
    public String getName() {
        return "|cleanHtml";
    }

    @Override
    public final Set<Integer> getValidArgsSizes() {
        return VALID_ARGS_SIZES;
    }

    @Override
    public boolean shouldCancelAutoescape() {
        return false;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        return Sanitizers.cleanHtml(value);
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        return new JsExpr("soy.$$cleanHtml(" + value.getText() + ")", Integer.MAX_VALUE);
    }
}


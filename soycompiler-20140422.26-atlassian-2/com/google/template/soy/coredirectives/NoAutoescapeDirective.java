/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 */
package com.google.template.soy.coredirectives;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.Sanitizers;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPurePrintDirective
public class NoAutoescapeDirective
implements SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    public static final String NAME = "|noAutoescape";

    @Inject
    public NoAutoescapeDirective() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)0);
    }

    @Override
    public boolean shouldCancelAutoescape() {
        return true;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        return Sanitizers.filterNoAutoescape(value);
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        return new JsExpr("soy.$$filterNoAutoescape(" + value.getText() + ")", Integer.MAX_VALUE);
    }
}


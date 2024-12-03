/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.basicdirectives;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SoyPurePrintDirective
public class TextDirective
implements SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    @Inject
    public TextDirective() {
    }

    @Override
    public String getName() {
        return "|text";
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
        return value;
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        return JsExprUtils.concatJsExprs((List<JsExpr>)ImmutableList.of((Object)new JsExpr("''", Integer.MAX_VALUE), (Object)value));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 */
package com.google.template.soy.basicfunctions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsCodeUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
class RandomIntFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    RandomIntFunction() {
    }

    @Override
    public String getName() {
        return "randomInt";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg = args.get(0);
        return IntegerData.forValue((long)Math.floor(Math.random() * (double)arg.longValue()));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg = args.get(0);
        JsExpr random = new JsExpr("Math.random()", Integer.MAX_VALUE);
        JsExpr randomTimesArg = SoyJsCodeUtils.genJsExprUsingSoySyntax(Operator.TIMES, Lists.newArrayList((Object[])new JsExpr[]{random, arg}));
        return new JsExpr("Math.floor(" + randomTimesArg.getText() + ")", Integer.MAX_VALUE);
    }
}


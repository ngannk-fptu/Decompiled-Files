/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 */
package com.google.template.soy.basicfunctions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPureFunction
class FloorFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    FloorFunction() {
    }

    @Override
    public String getName() {
        return "floor";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg = args.get(0);
        if (arg instanceof IntegerData) {
            return arg;
        }
        return IntegerData.forValue((int)Math.floor(arg.floatValue()));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg = args.get(0);
        return new JsExpr("Math.floor(" + arg.getText() + ")", Integer.MAX_VALUE);
    }
}


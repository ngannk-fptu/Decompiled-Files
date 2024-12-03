/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.basicfunctions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPureFunction
class MinFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    MinFunction() {
    }

    @Override
    public String getName() {
        return "min";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)2);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg0 = args.get(0);
        SoyValue arg1 = args.get(1);
        if (arg0 instanceof IntegerData && arg1 instanceof IntegerData) {
            return IntegerData.forValue(Math.min(arg0.longValue(), arg1.longValue()));
        }
        return FloatData.forValue(Math.min(arg0.numberValue(), arg1.numberValue()));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg0 = args.get(0);
        JsExpr arg1 = args.get(1);
        return new JsExpr("Math.min(" + arg0.getText() + ", " + arg1.getText() + ")", Integer.MAX_VALUE);
    }
}


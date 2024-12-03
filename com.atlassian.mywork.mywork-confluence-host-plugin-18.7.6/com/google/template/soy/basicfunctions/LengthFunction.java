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
import com.google.template.soy.data.SoyList;
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
class LengthFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    LengthFunction() {
    }

    @Override
    public String getName() {
        return "length";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg = args.get(0);
        if (arg == null) {
            throw new IllegalArgumentException("Argument to length() function is null.");
        }
        if (!(arg instanceof SoyList)) {
            throw new IllegalArgumentException("Argument to length() function is not a SoyList (found type " + arg.getClass().getName() + ").");
        }
        return IntegerData.forValue(((SoyList)arg).length());
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg = args.get(0);
        String exprText = arg.getPrecedence() == Integer.MAX_VALUE ? arg.getText() + ".length" : "(" + arg.getText() + ").length";
        return new JsExpr(exprText, Integer.MAX_VALUE);
    }
}


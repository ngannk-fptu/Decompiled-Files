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
import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPureFunction
class KeysFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    private final SoyValueHelper valueHelper;

    @Inject
    KeysFunction(SoyValueHelper valueHelper) {
        this.valueHelper = valueHelper;
    }

    @Override
    public String getName() {
        return "keys";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg = args.get(0);
        if (!(arg instanceof SoyMap)) {
            throw new IllegalArgumentException("Argument to keys() function is not SoyMap.");
        }
        return this.valueHelper.newEasyListFromJavaIterable(((SoyMap)arg).getItemKeys());
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg = args.get(0);
        return new JsExpr("soy.$$getMapKeys(" + arg.getText() + ")", Integer.MAX_VALUE);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.basicfunctions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsCodeUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPureFunction
class IsNonnullFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    IsNonnullFunction() {
    }

    @Override
    public String getName() {
        return "isNonnull";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg = args.get(0);
        return BooleanData.forValue(!(arg instanceof UndefinedData) && !(arg instanceof NullData));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg = args.get(0);
        JsExpr nullJsExpr = new JsExpr("null", Integer.MAX_VALUE);
        return SoyJsCodeUtils.genJsExprUsingSoySyntax(Operator.NOT_EQUAL, Lists.newArrayList((Object[])new JsExpr[]{arg, nullJsExpr}));
    }
}


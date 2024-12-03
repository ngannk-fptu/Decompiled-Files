/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.basicfunctions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPureFunction
class StrSubFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    StrSubFunction() {
    }

    @Override
    public String getName() {
        return "strSub";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)2, (Object)3);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg0 = args.get(0);
        SoyValue arg1 = args.get(1);
        SoyValue arg2 = args.size() == 3 ? args.get(2) : null;
        Preconditions.checkArgument((arg0 instanceof StringData || arg0 instanceof SanitizedContent ? 1 : 0) != 0, (String)"First argument to strSub() function is not StringData or SanitizedContent: %s", (Object)arg0);
        Preconditions.checkArgument((boolean)(arg1 instanceof IntegerData), (String)"Second argument to strSub() function is not IntegerData: %s", (Object)arg1);
        if (arg2 != null) {
            Preconditions.checkArgument((boolean)(arg2 instanceof IntegerData), (String)"Third argument to strSub() function is not IntegerData: %s", (Object)arg2);
        }
        String strArg0 = arg0.coerceToString();
        int intArg1 = arg1.integerValue();
        if (arg2 != null) {
            return StringData.forValue(strArg0.substring(intArg1, arg2.integerValue()));
        }
        return StringData.forValue(strArg0.substring(intArg1));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        String arg0 = JsExprUtils.toString(args.get(0)).getText();
        JsExpr arg1 = args.get(1);
        JsExpr arg2 = args.size() == 3 ? args.get(2) : null;
        return new JsExpr("(" + arg0 + ").substring(" + arg1.getText() + (arg2 != null ? "," + arg2.getText() : "") + ")", Integer.MAX_VALUE);
    }
}


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
class StrIndexOfFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    StrIndexOfFunction() {
    }

    @Override
    public String getName() {
        return "strIndexOf";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)2);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg0 = args.get(0);
        SoyValue arg1 = args.get(1);
        Preconditions.checkArgument((arg0 instanceof StringData || arg0 instanceof SanitizedContent ? 1 : 0) != 0, (String)"First argument to strIndexOf() function is not StringData or SanitizedContent: %s", (Object)arg0);
        Preconditions.checkArgument((arg1 instanceof StringData || arg1 instanceof SanitizedContent ? 1 : 0) != 0, (String)"Second argument to strIndexOf() function is not StringData or SanitizedContent: %s", (Object)arg1);
        String strArg0 = arg0.coerceToString();
        String strArg1 = arg1.coerceToString();
        return IntegerData.forValue(strArg0.indexOf(strArg1));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        String arg0 = JsExprUtils.toString(args.get(0)).getText();
        String arg1 = JsExprUtils.toString(args.get(1)).getText();
        return new JsExpr("(" + arg0 + ").indexOf(" + arg1 + ")", Integer.MAX_VALUE);
    }
}


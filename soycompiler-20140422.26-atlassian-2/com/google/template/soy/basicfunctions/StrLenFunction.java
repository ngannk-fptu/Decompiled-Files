/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
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
class StrLenFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    StrLenFunction() {
    }

    @Override
    public String getName() {
        return "strLen";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg0 = args.get(0);
        Preconditions.checkArgument((arg0 instanceof StringData || arg0 instanceof SanitizedContent ? 1 : 0) != 0, (String)"First argument to strLen() function is not StringData or SanitizedContent: %s", (Object)arg0);
        return IntegerData.forValue(arg0.coerceToString().length());
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        String arg0 = JsExprUtils.toString(args.get(0)).getText();
        return new JsExpr("(" + arg0 + ").length", Integer.MAX_VALUE);
    }
}


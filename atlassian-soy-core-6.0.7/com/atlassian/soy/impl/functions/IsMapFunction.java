/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Singleton
 *  com.google.template.soy.data.SoyData
 *  com.google.template.soy.data.SoyDict
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.restricted.BooleanData
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 */
package com.atlassian.soy.impl.functions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
public class IsMapFunction
implements SoyJsSrcFunction,
SoyJavaFunction {
    public static final String FUNCTION_NAME = "isMap";

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1);
    }

    public JsExpr computeForJsSrc(List<JsExpr> jsExprs) {
        return new JsExpr("Object.prototype.toString.call(" + jsExprs.get(0).getText() + ") === '[object Object]'", Integer.MAX_VALUE);
    }

    public SoyData computeForJava(List<SoyValue> soyDatas) {
        return BooleanData.forValue((boolean)(soyDatas.get(0) instanceof SoyDict));
    }
}


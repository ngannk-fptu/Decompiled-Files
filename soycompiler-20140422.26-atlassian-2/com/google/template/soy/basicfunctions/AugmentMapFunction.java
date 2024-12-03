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
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyEasyDict;
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
class AugmentMapFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    private final SoyValueHelper valueHelper;

    @Inject
    AugmentMapFunction(SoyValueHelper valueHelper) {
        this.valueHelper = valueHelper;
    }

    @Override
    public String getName() {
        return "augmentMap";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)2);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue arg0 = args.get(0);
        SoyValue arg1 = args.get(1);
        Preconditions.checkArgument((boolean)(arg0 instanceof SoyMap), (Object)"First argument to augmentMap() function is not SoyMap.");
        Preconditions.checkArgument((boolean)(arg1 instanceof SoyMap), (Object)"Second argument to augmentMap() function is not SoyMap.");
        Preconditions.checkArgument((boolean)(arg0 instanceof SoyDict), (Object)"First argument to augmentMap() function is not SoyDict. Currently, augmentMap() doesn't support maps that are not dicts (it is a todo).");
        Preconditions.checkArgument((boolean)(arg1 instanceof SoyDict), (Object)"Second argument to augmentMap() function is not SoyDict. Currently, augmentMap() doesn't support maps that are not dicts (it is a todo).");
        SoyEasyDict resultDict = this.valueHelper.newEasyDict();
        resultDict.setItemsFromDict((SoyDict)arg0);
        resultDict.setItemsFromDict((SoyDict)arg1);
        return resultDict;
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr arg0 = args.get(0);
        JsExpr arg1 = args.get(1);
        String exprText = "soy.$$augmentMap(" + arg0.getText() + ", " + arg1.getText() + ")";
        return new JsExpr(exprText, Integer.MAX_VALUE);
    }
}


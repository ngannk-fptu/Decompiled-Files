/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Singleton
 *  com.google.template.soy.data.SoyDict
 *  com.google.template.soy.data.SoyEasyDict
 *  com.google.template.soy.data.SoyEasyList
 *  com.google.template.soy.data.SoyList
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.SoyValueHelper
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 */
package com.atlassian.soy.impl.functions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyEasyDict;
import com.google.template.soy.data.SoyEasyList;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

@Singleton
public class ConcatFunction
implements SoyJsSrcFunction,
SoyJavaFunction {
    public static final String FUNCTION_NAME = "concat";
    private final SoyValueHelper soyValueHelper;

    @Inject
    public ConcatFunction(SoyValueHelper soyValueHelper) {
        this.soyValueHelper = soyValueHelper;
    }

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)2);
    }

    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr listA = args.get(0);
        JsExpr listB = args.get(1);
        return new JsExpr("atl_soy.concat(" + listA.getText() + ", " + listB.getText() + ")", Integer.MAX_VALUE);
    }

    public SoyValue computeForJava(List<SoyValue> args) {
        SoyValue first = args.get(0);
        SoyValue second = args.get(1);
        if (first instanceof SoyList && second instanceof SoyList) {
            return this.concatIterables((SoyList)first, (SoyList)second);
        }
        if (first instanceof SoyDict && second instanceof SoyDict) {
            return this.concatMaps((SoyDict)first, (SoyDict)second);
        }
        throw new IllegalArgumentException("concat() accepts two arguments that are either both Maps or both Iterables.");
    }

    private SoyList concatIterables(SoyList first, SoyList second) {
        SoyEasyList list = this.soyValueHelper.newEasyList();
        list.addAllFromList(first);
        list.addAllFromList(second);
        return list.makeImmutable();
    }

    private SoyDict concatMaps(SoyDict first, SoyDict second) {
        SoyEasyDict dict = this.soyValueHelper.newEasyDict();
        dict.setItemsFromDict(first);
        dict.setItemsFromDict(second);
        return dict.makeImmutable();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.tofu.internal;

import com.google.inject.Inject;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import com.google.template.soy.tofu.internal.TofuEvalVisitor;
import com.google.template.soy.tofu.internal.TofuModule;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
class TofuEvalVisitorFactory
implements EvalVisitor.EvalVisitorFactory {
    private final SoyValueHelper valueHelper;
    private final Map<String, SoyJavaFunction> soyJavaFunctionsMap;

    @Inject
    public TofuEvalVisitorFactory(SoyValueHelper valueHelper, @TofuModule.Tofu Map<String, SoyJavaFunction> soyJavaFunctionsMap) {
        this.valueHelper = valueHelper;
        this.soyJavaFunctionsMap = soyJavaFunctionsMap;
    }

    @Override
    public EvalVisitor create(SoyRecord data, @Nullable SoyRecord ijData, Deque<Map<String, SoyValue>> env) {
        return new TofuEvalVisitor(this.valueHelper, this.soyJavaFunctionsMap, data, ijData, env);
    }
}


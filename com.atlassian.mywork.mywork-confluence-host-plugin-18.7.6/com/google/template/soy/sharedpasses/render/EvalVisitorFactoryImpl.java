/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses.render;

import com.google.inject.Inject;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public class EvalVisitorFactoryImpl
implements EvalVisitor.EvalVisitorFactory {
    private final SoyValueHelper valueHelper;
    private final Map<String, SoyJavaFunction> soyJavaFunctionsMap;

    @Inject
    public EvalVisitorFactoryImpl(SoyValueHelper valueHelper, @SharedModule.Shared Map<String, SoyJavaFunction> soyJavaFunctionsMap) {
        this.valueHelper = valueHelper;
        this.soyJavaFunctionsMap = soyJavaFunctionsMap;
    }

    @Override
    public EvalVisitor create(SoyRecord data, @Nullable SoyRecord ijData, Deque<Map<String, SoyValue>> env) {
        return new EvalVisitor(this.valueHelper, this.soyJavaFunctionsMap, data, ijData, env);
    }
}


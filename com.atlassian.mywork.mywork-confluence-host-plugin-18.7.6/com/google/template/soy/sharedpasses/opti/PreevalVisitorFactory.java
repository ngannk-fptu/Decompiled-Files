/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.sharedpasses.opti.PreevalVisitor;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public class PreevalVisitorFactory
implements EvalVisitor.EvalVisitorFactory {
    private final SoyValueHelper valueHelper;
    private final Map<String, SoyJavaFunction> soyJavaFunctionsMap;

    @Inject
    public PreevalVisitorFactory(SoyValueHelper valueHelper, @SharedModule.Shared Map<String, SoyJavaFunction> soyJavaFunctionsMap) {
        this.valueHelper = valueHelper;
        this.soyJavaFunctionsMap = soyJavaFunctionsMap;
    }

    public PreevalVisitor create(SoyRecord data, Deque<Map<String, SoyValue>> env) {
        return new PreevalVisitor(this.valueHelper, this.soyJavaFunctionsMap, data, env);
    }

    @Override
    public PreevalVisitor create(SoyRecord data, @Nullable SoyRecord ijData, Deque<Map<String, SoyValue>> env) {
        Preconditions.checkArgument((ijData == null ? 1 : 0) != 0);
        return new PreevalVisitor(this.valueHelper, this.soyJavaFunctionsMap, data, env);
    }
}


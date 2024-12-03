/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.tofu.internal;

import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;

class TofuEvalVisitor
extends EvalVisitor {
    protected TofuEvalVisitor(SoyValueHelper valueHelper, @Nullable Map<String, SoyJavaFunction> soyJavaFunctionsMap, SoyRecord data, @Nullable SoyRecord ijData, Deque<Map<String, SoyValue>> env) {
        super(valueHelper, soyJavaFunctionsMap, data, ijData, env);
    }
}


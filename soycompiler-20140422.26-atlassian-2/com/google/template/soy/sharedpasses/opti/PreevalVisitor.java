/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import com.google.template.soy.sharedpasses.render.RenderException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

class PreevalVisitor
extends EvalVisitor {
    PreevalVisitor(SoyValueHelper valueHelper, Map<String, SoyJavaFunction> soyJavaFunctionsMap, SoyRecord data, Deque<Map<String, SoyValue>> env) {
        super(valueHelper, soyJavaFunctionsMap, data, null, env);
    }

    @Override
    protected SoyValue visitVarRefNode(VarRefNode node) {
        if (node.isInjected()) {
            throw new RenderException("Cannot preevaluate reference to ijData.");
        }
        SoyValue value = super.visitVarRefNode(node);
        if (value instanceof UndefinedData) {
            throw new RenderException("Encountered undefined reference during preevaluation.");
        }
        return value;
    }

    @Override
    protected SoyValue computeFunctionHelper(SoyJavaFunction fn, List<SoyValue> args, FunctionNode fnNode) {
        if (!fn.getClass().isAnnotationPresent(SoyPureFunction.class)) {
            throw new RenderException("Cannot preevaluate impure function.");
        }
        return super.computeFunctionHelper(fn, args, fnNode);
    }
}


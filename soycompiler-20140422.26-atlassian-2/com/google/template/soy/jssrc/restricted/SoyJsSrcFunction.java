/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.jssrc.restricted;

import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.shared.restricted.SoyFunction;
import java.util.List;

public interface SoyJsSrcFunction
extends SoyFunction {
    public JsExpr computeForJsSrc(List<JsExpr> var1);
}


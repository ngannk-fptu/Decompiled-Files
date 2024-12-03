/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.jssrc.restricted;

import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import java.util.List;

public interface SoyJsSrcPrintDirective
extends SoyPrintDirective {
    public JsExpr applyForJsSrc(JsExpr var1, List<JsExpr> var2);
}


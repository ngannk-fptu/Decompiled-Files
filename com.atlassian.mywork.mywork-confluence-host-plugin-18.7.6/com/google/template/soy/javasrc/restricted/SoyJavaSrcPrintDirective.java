/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.javasrc.restricted;

import com.google.template.soy.javasrc.restricted.JavaExpr;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import java.util.List;

@Deprecated
public interface SoyJavaSrcPrintDirective
extends SoyPrintDirective {
    public JavaExpr applyForJavaSrc(JavaExpr var1, List<JavaExpr> var2);
}


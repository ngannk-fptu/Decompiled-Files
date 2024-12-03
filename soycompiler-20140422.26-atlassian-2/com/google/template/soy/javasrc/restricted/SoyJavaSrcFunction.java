/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.javasrc.restricted;

import com.google.template.soy.javasrc.restricted.JavaExpr;
import com.google.template.soy.shared.restricted.SoyFunction;
import java.util.List;

@Deprecated
public interface SoyJavaSrcFunction
extends SoyFunction {
    public JavaExpr computeForJavaSrc(List<JavaExpr> var1);
}


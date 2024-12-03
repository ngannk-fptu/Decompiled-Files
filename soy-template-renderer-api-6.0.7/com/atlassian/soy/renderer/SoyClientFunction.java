/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyFunction;

@PublicSpi
public interface SoyClientFunction
extends SoyFunction {
    public JsExpression generate(JsExpression ... var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyFunction
 *  com.google.common.base.Supplier
 */
package com.atlassian.soy.spi.functions;

import com.atlassian.soy.renderer.SoyFunction;
import com.google.common.base.Supplier;

public interface SoyFunctionSupplier
extends Supplier<Iterable<SoyFunction>> {
    public Iterable<SoyFunction> get();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyFunction
 *  com.google.common.base.Preconditions
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.renderer.SoyFunction;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.google.common.base.Preconditions;

public class SimpleSoyFunctionSupplier
implements SoyFunctionSupplier {
    private final Iterable<SoyFunction> functions;

    public SimpleSoyFunctionSupplier(Iterable<SoyFunction> functions) {
        this.functions = (Iterable)Preconditions.checkNotNull(functions, (Object)"functions");
    }

    @Override
    public Iterable<SoyFunction> get() {
        return this.functions;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyFunction
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.renderer.SoyFunction;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import java.util.Collections;

public class EmptySoyFunctionSupplier
implements SoyFunctionSupplier {
    @Override
    public Iterable<SoyFunction> get() {
        return Collections.emptySet();
    }
}


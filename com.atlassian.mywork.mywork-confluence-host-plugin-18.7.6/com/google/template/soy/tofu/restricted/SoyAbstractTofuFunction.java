/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyJavaRuntimeFunction;
import com.google.template.soy.tofu.restricted.SoyTofuFunction;
import java.util.List;

public abstract class SoyAbstractTofuFunction
implements SoyJavaRuntimeFunction,
SoyTofuFunction {
    @Override
    public SoyData computeForTofu(List<SoyData> args) {
        return this.compute(args);
    }
}


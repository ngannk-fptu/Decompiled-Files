/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyFunction;
import java.util.List;

public interface SoyTofuFunction
extends SoyFunction {
    public SoyData computeForTofu(List<SoyData> var1);
}


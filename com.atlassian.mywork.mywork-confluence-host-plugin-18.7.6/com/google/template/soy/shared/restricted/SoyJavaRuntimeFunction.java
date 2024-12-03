/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.shared.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyFunction;
import java.util.List;

public interface SoyJavaRuntimeFunction
extends SoyFunction {
    public SoyData compute(List<SoyData> var1);
}


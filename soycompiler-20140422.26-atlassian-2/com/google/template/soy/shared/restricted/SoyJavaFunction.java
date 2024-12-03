/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.shared.restricted;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.shared.restricted.SoyFunction;
import java.util.List;

public interface SoyJavaFunction
extends SoyFunction {
    public SoyValue computeForJava(List<SoyValue> var1);
}


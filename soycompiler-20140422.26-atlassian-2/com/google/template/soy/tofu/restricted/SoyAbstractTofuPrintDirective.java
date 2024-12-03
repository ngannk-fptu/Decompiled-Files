/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyJavaRuntimePrintDirective;
import com.google.template.soy.tofu.restricted.SoyTofuPrintDirective;
import java.util.List;

public abstract class SoyAbstractTofuPrintDirective
implements SoyJavaRuntimePrintDirective,
SoyTofuPrintDirective {
    @Override
    public SoyData applyForTofu(SoyData value, List<SoyData> args) {
        return this.apply(value, args);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import java.util.List;

public interface SoyTofuPrintDirective
extends SoyPrintDirective {
    public SoyData applyForTofu(SoyData var1, List<SoyData> var2);
}


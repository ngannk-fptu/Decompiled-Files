/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.shared.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import java.util.List;

public interface SoyJavaRuntimePrintDirective
extends SoyPrintDirective {
    public SoyData apply(SoyData var1, List<SoyData> var2);
}


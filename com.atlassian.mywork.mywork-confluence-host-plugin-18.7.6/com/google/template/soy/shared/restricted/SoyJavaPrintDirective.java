/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.shared.restricted;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import java.util.List;

public interface SoyJavaPrintDirective
extends SoyPrintDirective {
    public SoyValue applyForJava(SoyValue var1, List<SoyValue> var2);
}


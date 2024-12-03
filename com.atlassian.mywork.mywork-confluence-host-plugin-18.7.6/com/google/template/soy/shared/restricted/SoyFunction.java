/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.shared.restricted;

import java.util.Set;

public interface SoyFunction {
    public String getName();

    public Set<Integer> getValidArgsSizes();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types;

import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeRegistry;

public interface SoyTypeProvider {
    public SoyType getType(String var1, SoyTypeRegistry var2);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

public interface ConditionalConverter {
    public boolean matches(TypeDescriptor var1, TypeDescriptor var2);
}


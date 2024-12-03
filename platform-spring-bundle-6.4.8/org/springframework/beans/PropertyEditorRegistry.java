/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyEditor;
import org.springframework.lang.Nullable;

public interface PropertyEditorRegistry {
    public void registerCustomEditor(Class<?> var1, PropertyEditor var2);

    public void registerCustomEditor(@Nullable Class<?> var1, @Nullable String var2, PropertyEditor var3);

    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> var1, @Nullable String var2);
}


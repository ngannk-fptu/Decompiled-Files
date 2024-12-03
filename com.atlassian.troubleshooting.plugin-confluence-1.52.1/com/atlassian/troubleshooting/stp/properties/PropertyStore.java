/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.properties;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PropertyStore {
    @Nonnull
    public PropertyStore addCategory(@Nonnull String var1);

    public void copyCategoriesFrom(@Nonnull PropertyStore var1);

    @Nonnull
    public PropertyStore addCategory(@Nonnull String var1, @Nonnull PropertyStore var2);

    @Nonnull
    public Map<String, List<PropertyStore>> getCategories();

    @Nonnull
    public Map<String, String> getValues();

    public void putValues(@Nonnull Map<String, String> var1);

    public void setValue(@Nonnull String var1, @Nullable String var2);
}


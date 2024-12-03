/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyEasyList
extends SoyList,
SoyMap {
    public void add(SoyValueProvider var1);

    public void add(@Nullable Object var1);

    public void add(int var1, SoyValueProvider var2);

    public void add(int var1, @Nullable Object var2);

    public void set(int var1, SoyValueProvider var2);

    public void set(int var1, @Nullable Object var2);

    public void del(int var1);

    public void addAllFromList(SoyList var1);

    public void addAllFromJavaIterable(Iterable<?> var1);

    public SoyEasyList makeImmutable();
}


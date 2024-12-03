/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyEasyDict
extends SoyDict {
    public void setField(String var1, SoyValueProvider var2);

    public void delField(String var1);

    public void setItemsFromDict(SoyDict var1);

    public void setFieldsFromJavaStringMap(Map<String, ?> var1);

    public void set(String var1, @Nullable Object var2);

    public void del(String var1);

    public boolean has(String var1);

    public SoyValue get(String var1);

    public SoyValueProvider getProvider(String var1);

    public SoyEasyDict makeImmutable();
}


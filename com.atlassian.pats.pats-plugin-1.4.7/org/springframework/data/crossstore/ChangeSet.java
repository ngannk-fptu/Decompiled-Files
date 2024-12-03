/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.core.convert.ConversionService
 */
package org.springframework.data.crossstore;

import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.core.convert.ConversionService;

public interface ChangeSet {
    @Nullable
    public <T> T get(String var1, Class<T> var2, ConversionService var3);

    public void set(String var1, Object var2);

    public Map<String, Object> getValues();

    @Nullable
    public Object removeProperty(String var1);
}


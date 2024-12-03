/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public interface AnnotatedTypeMetadata {
    public boolean isAnnotated(String var1);

    @Nullable
    public Map<String, Object> getAnnotationAttributes(String var1);

    @Nullable
    public Map<String, Object> getAnnotationAttributes(String var1, boolean var2);

    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String var1);

    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String var1, boolean var2);
}


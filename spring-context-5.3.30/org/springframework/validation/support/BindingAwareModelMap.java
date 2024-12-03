/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation.support;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

public class BindingAwareModelMap
extends ExtendedModelMap {
    @Override
    public Object put(String key, @Nullable Object value) {
        this.removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        map.forEach(this::removeBindingResultIfNecessary);
        super.putAll(map);
    }

    private void removeBindingResultIfNecessary(Object key, @Nullable Object value) {
        String bindingResultKey;
        BindingResult bindingResult;
        String attributeName;
        if (key instanceof String && !(attributeName = (String)key).startsWith(BindingResult.MODEL_KEY_PREFIX) && (bindingResult = (BindingResult)this.get(bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attributeName)) != null && bindingResult.getTarget() != value) {
            this.remove(bindingResultKey);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation.support;

import java.util.Map;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

public class BindingAwareModelMap
extends ExtendedModelMap {
    @Override
    public Object put(String key, Object value) {
        this.removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        map.forEach(this::removeBindingResultIfNecessary);
        super.putAll(map);
    }

    private void removeBindingResultIfNecessary(Object key, Object value) {
        String bindingResultKey;
        BindingResult bindingResult;
        String attributeName;
        if (key instanceof String && !(attributeName = (String)key).startsWith(BindingResult.MODEL_KEY_PREFIX) && (bindingResult = (BindingResult)this.get(bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attributeName)) != null && bindingResult.getTarget() != value) {
            this.remove(bindingResultKey);
        }
    }
}


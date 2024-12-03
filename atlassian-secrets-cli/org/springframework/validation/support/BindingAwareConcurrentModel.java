/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation.support;

import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BindingResult;

public class BindingAwareConcurrentModel
extends ConcurrentModel {
    @Override
    public Object put(String key, Object value) {
        this.removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    private void removeBindingResultIfNecessary(String key, Object value) {
        String resultKey;
        BindingResult result;
        if (!key.startsWith(BindingResult.MODEL_KEY_PREFIX) && (result = (BindingResult)this.get(resultKey = BindingResult.MODEL_KEY_PREFIX + key)) != null && result.getTarget() != value) {
            this.remove(resultKey);
        }
    }
}


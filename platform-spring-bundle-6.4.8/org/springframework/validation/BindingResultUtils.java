/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

public abstract class BindingResultUtils {
    @Nullable
    public static BindingResult getBindingResult(Map<?, ?> model, String name) {
        Assert.notNull(model, "Model map must not be null");
        Assert.notNull((Object)name, "Name must not be null");
        Object attr = model.get(BindingResult.MODEL_KEY_PREFIX + name);
        if (attr != null && !(attr instanceof BindingResult)) {
            throw new IllegalStateException("BindingResult attribute is not of type BindingResult: " + attr);
        }
        return (BindingResult)attr;
    }

    public static BindingResult getRequiredBindingResult(Map<?, ?> model, String name) {
        BindingResult bindingResult = BindingResultUtils.getBindingResult(model, name);
        if (bindingResult == null) {
            throw new IllegalStateException("No BindingResult attribute found for name '" + name + "'- have you exposed the correct model?");
        }
        return bindingResult;
    }
}


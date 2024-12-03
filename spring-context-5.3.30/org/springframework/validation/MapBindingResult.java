/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.validation;

import java.io.Serializable;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractBindingResult;

public class MapBindingResult
extends AbstractBindingResult
implements Serializable {
    private final Map<?, ?> target;

    public MapBindingResult(Map<?, ?> target, String objectName) {
        super(objectName);
        Assert.notNull(target, (String)"Target Map must not be null");
        this.target = target;
    }

    public final Map<?, ?> getTargetMap() {
        return this.target;
    }

    @Override
    @NonNull
    public final Object getTarget() {
        return this.target;
    }

    @Override
    @Nullable
    protected Object getActualFieldValue(String field) {
        return this.target.get(field);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.spel.spi;

import java.util.Collections;
import java.util.Map;
import org.springframework.data.spel.spi.ExtensionIdAware;
import org.springframework.data.spel.spi.Function;
import org.springframework.lang.Nullable;

public interface EvaluationContextExtension
extends ExtensionIdAware {
    default public Map<String, Object> getProperties() {
        return Collections.emptyMap();
    }

    default public Map<String, Function> getFunctions() {
        return Collections.emptyMap();
    }

    @Nullable
    default public Object getRootObject() {
        return null;
    }
}


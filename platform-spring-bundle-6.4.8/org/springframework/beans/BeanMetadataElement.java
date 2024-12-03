/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.lang.Nullable;

public interface BeanMetadataElement {
    @Nullable
    default public Object getSource() {
        return null;
    }
}


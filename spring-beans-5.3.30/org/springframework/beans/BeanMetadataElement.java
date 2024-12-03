/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import org.springframework.lang.Nullable;

public interface BeanMetadataElement {
    @Nullable
    default public Object getSource() {
        return null;
    }
}


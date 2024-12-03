/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor;

import org.hibernate.type.descriptor.WrapperOptions;

@Deprecated
public interface WrapperOptionsContext
extends WrapperOptions {
    @Deprecated
    default public WrapperOptions getWrapperOptions() {
        return this;
    }
}


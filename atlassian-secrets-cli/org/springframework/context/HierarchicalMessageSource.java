/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

public interface HierarchicalMessageSource
extends MessageSource {
    public void setParentMessageSource(@Nullable MessageSource var1);

    @Nullable
    public MessageSource getParentMessageSource();
}


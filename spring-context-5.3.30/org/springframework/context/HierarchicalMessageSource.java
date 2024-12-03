/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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


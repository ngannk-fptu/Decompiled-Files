/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

public interface Component {
    @Nullable
    public <S> S getSPI(@NotNull Class<S> var1);
}


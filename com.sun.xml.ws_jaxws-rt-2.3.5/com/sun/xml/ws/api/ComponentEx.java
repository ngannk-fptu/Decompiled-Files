/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.Component;

public interface ComponentEx
extends Component {
    @NotNull
    public <S> Iterable<S> getIterableSPI(@NotNull Class<S> var1);
}


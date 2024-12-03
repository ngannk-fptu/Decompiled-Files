/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.Component;
import java.util.Set;

public interface ComponentRegistry
extends Component {
    @NotNull
    public Set<Component> getComponents();
}


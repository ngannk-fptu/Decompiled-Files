/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentFilter;

public interface ServiceDefinition
extends Iterable<SDDocument> {
    @NotNull
    public SDDocument getPrimary();

    public void addFilter(@NotNull SDDocumentFilter var1);
}


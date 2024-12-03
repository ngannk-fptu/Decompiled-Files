/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 */
package com.atlassian.webresource.api.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.assembler.WebResource;
import java.util.Optional;

public interface PluginDataResource
extends WebResource {
    public String getKey();

    public Jsonable getJsonable();

    public Optional<Jsonable> getData();
}


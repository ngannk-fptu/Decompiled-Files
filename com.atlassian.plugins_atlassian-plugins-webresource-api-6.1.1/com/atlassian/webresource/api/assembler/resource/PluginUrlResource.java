/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler.resource;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.WebResource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResourceParams;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.List;

@ExperimentalApi
public interface PluginUrlResource<T extends PluginUrlResourceParams>
extends WebResource {
    public String getStaticUrl(UrlMode var1);

    public T getParams();

    public boolean isTainted();

    public List<PrebakeError> getPrebakeErrors();

    public String getKey();

    public BatchType getBatchType();

    public static enum BatchType {
        CONTEXT,
        RESOURCE;

    }
}


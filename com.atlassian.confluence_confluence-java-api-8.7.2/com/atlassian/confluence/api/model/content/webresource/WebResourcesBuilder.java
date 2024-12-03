/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model.content.webresource;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.HtmlString;
import com.atlassian.confluence.api.model.content.webresource.ResourceType;
import java.net.URI;
import java.util.List;
import java.util.Map;

@ExperimentalApi
@Internal
public interface WebResourcesBuilder {
    public WebResourcesBuilder uris(ResourceType var1, List<URI> var2);

    public WebResourcesBuilder uris(Map<ResourceType, List<URI>> var1);

    public WebResourcesBuilder addCollapsedUris(ResourceType var1);

    public WebResourcesBuilder tag(ResourceType var1, HtmlString var2);

    public WebResourcesBuilder tag(Map<ResourceType, HtmlString> var1);

    public WebResourcesBuilder addCollapsedTag(ResourceType var1);
}


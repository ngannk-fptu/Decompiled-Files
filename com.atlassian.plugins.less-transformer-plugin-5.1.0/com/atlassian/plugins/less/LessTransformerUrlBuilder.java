/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 */
package com.atlassian.plugins.less;

import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.plugins.less.LessPrebakeError;
import com.atlassian.plugins.less.PrebakeStateResult;
import com.atlassian.plugins.less.UriStateManager;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import java.net.URI;
import java.util.List;

public class LessTransformerUrlBuilder
implements DimensionAwareTransformerUrlBuilder {
    private final List<URI> resources;
    private final UriStateManager uriStateManager;

    public LessTransformerUrlBuilder(List<URI> resources, UriStateManager uriStateManager) {
        this.resources = resources;
        this.uriStateManager = uriStateManager;
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coord) {
        for (URI uri : this.resources) {
            PrebakeStateResult stateResult = this.uriStateManager.getState(uri, coord);
            urlBuilder.addToHash("LESS-URI-STATE", (Object)stateResult.getState());
            if (stateResult.getPrebakeErrors().isEmpty()) continue;
            LessPrebakeError error = new LessPrebakeError(uri.getPath(), stateResult.getPrebakeErrors());
            urlBuilder.addPrebakeError((PrebakeError)error);
        }
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        for (URI uri : this.resources) {
            urlBuilder.addToHash("LESS-URI-STATE", (Object)this.uriStateManager.getState(uri));
        }
    }
}


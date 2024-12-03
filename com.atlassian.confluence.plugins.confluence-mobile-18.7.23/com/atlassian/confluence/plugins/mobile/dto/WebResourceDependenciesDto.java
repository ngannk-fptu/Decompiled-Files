/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import net.jcip.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonProperty;

@Immutable
public class WebResourceDependenciesDto {
    @JsonProperty
    private String cssResourceTags;
    @JsonProperty
    private String jsResourceTags;

    public WebResourceDependenciesDto(String cssResourceTags, String jsResourceTags) {
        this.cssResourceTags = cssResourceTags;
        this.jsResourceTags = jsResourceTags;
    }

    public String getCssResourceTags() {
        return this.cssResourceTags;
    }

    public String getJsResourceTags() {
        return this.jsResourceTags;
    }
}


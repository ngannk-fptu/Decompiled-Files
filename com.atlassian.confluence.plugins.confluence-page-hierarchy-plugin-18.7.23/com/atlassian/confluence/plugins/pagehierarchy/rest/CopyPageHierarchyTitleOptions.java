/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.pagehierarchy.rest;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CopyPageHierarchyTitleOptions {
    private final String prefix;
    private final String replace;
    private final String search;

    @JsonCreator
    public CopyPageHierarchyTitleOptions(@JsonProperty(value="prefix") String prefix, @JsonProperty(value="replace") String replace, @JsonProperty(value="search") String search) {
        this.prefix = prefix;
        this.replace = replace;
        this.search = search;
    }

    public String getSearch() {
        return this.search;
    }

    public String getReplace() {
        return this.replace;
    }

    public String getPrefix() {
        return this.prefix;
    }
}


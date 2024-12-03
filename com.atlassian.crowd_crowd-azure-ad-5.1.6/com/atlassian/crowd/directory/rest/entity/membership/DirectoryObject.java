/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.map.annotate.JsonTypeIdResolver
 */
package com.atlassian.crowd.directory.rest.entity.membership;

import com.atlassian.crowd.directory.rest.resolver.DirectoryObjectTypeIdResolver;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonTypeIdResolver;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, include=JsonTypeInfo.As.PROPERTY, property="@odata.type")
@JsonTypeIdResolver(value=DirectoryObjectTypeIdResolver.class)
@JsonIgnoreProperties(ignoreUnknown=true)
public class DirectoryObject {
    @JsonProperty(value="displayName")
    protected final String displayName;
    @JsonProperty(value="id")
    protected final String id;

    protected DirectoryObject() {
        this.displayName = null;
        this.id = null;
    }

    public DirectoryObject(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getId() {
        return this.id;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.audit.query;

import org.codehaus.jackson.annotate.JsonProperty;

public class AuditLogEntityRestrictionRestDTO {
    @JsonProperty(value="id")
    private final Long id;
    @JsonProperty(value="name")
    private final String name;

    protected AuditLogEntityRestrictionRestDTO() {
        this.id = null;
        this.name = null;
    }

    public AuditLogEntityRestrictionRestDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}


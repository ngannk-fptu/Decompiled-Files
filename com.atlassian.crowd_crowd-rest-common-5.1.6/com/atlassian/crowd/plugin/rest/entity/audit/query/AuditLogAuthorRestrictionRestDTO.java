/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.audit.query;

import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.plugin.rest.entity.audit.query.AuditLogEntityRestrictionRestDTO;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditLogAuthorRestrictionRestDTO
extends AuditLogEntityRestrictionRestDTO {
    @JsonProperty(value="type")
    private final AuditLogAuthorType type;

    private AuditLogAuthorRestrictionRestDTO() {
        this.type = null;
    }

    public AuditLogAuthorRestrictionRestDTO(Long id, String name, AuditLogAuthorType type) {
        super(id, name);
        this.type = type;
    }

    public AuditLogAuthorType getType() {
        return this.type;
    }
}


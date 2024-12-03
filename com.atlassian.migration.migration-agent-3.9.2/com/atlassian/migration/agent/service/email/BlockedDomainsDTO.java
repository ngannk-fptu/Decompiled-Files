/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.migration.agent.service.email;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
public class BlockedDomainsDTO {
    @JsonProperty
    private final List<String> domains;

    public BlockedDomainsDTO(List<String> domains) {
        this.domains = domains;
    }

    public List<String> getDomains() {
        return this.domains;
    }
}


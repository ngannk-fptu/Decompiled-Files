/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.cmpt.domain.Edition;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CloudEditionCheckResponse {
    public final String status;
    public final Edition edition;
    public final int usersCount;

    @JsonCreator
    public CloudEditionCheckResponse(@JsonProperty(value="status") String status, @JsonProperty(value="edition") Edition edition, @JsonProperty(value="usersCount") int usersCount) {
        this.status = status;
        this.edition = edition;
        this.usersCount = usersCount;
    }
}


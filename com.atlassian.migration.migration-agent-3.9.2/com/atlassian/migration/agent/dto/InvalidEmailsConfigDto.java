/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.InvalidEmailsStrategy;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InvalidEmailsConfigDto {
    @JsonProperty
    private final InvalidEmailsStrategy actionOnMigration;

    @JsonCreator
    public InvalidEmailsConfigDto() {
        this.actionOnMigration = InvalidEmailsStrategy.DO_NOTHING;
    }

    @JsonCreator
    public InvalidEmailsConfigDto(@JsonProperty(value="actionOnMigration") InvalidEmailsStrategy actionOnMigration) {
        this.actionOnMigration = actionOnMigration;
    }

    public InvalidEmailsStrategy getActionOnMigration() {
        return this.actionOnMigration;
    }
}


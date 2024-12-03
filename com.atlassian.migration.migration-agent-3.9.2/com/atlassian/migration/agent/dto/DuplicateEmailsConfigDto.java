/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.DuplicateEmailsStrategy;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class DuplicateEmailsConfigDto {
    @JsonProperty
    private final DuplicateEmailsStrategy actionOnMigration;

    @JsonCreator
    public DuplicateEmailsConfigDto() {
        this.actionOnMigration = DuplicateEmailsStrategy.DO_NOTHING;
    }

    @JsonCreator
    public DuplicateEmailsConfigDto(@JsonProperty(value="actionOnMigration") DuplicateEmailsStrategy actionOnMigration) {
        this.actionOnMigration = actionOnMigration;
    }

    public DuplicateEmailsStrategy getActionOnMigration() {
        return this.actionOnMigration;
    }
}


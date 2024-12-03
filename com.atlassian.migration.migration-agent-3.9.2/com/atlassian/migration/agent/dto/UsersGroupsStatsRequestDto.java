/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.util.UserMigrationType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UsersGroupsStatsRequestDto {
    @JsonProperty
    @Nonnull
    private final UserMigrationType userMigrationType;
    @JsonProperty
    @Nonnull
    private final List<String> spaceKeys;

    @JsonCreator
    public UsersGroupsStatsRequestDto(@JsonProperty(value="userMigrationType") String userMigrationType, @JsonProperty(value="spaceKeys") List<String> spaceKeys) {
        this.userMigrationType = userMigrationType == null ? UserMigrationType.NONE : UserMigrationType.valueOf(userMigrationType.toUpperCase());
        this.spaceKeys = spaceKeys == null ? Collections.emptyList() : spaceKeys.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    @NotNull
    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    @NotNull
    public UserMigrationType getUserMigrationType() {
        return this.userMigrationType;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.SpaceDto;
import java.util.Collection;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public class SpaceSearchResultDto {
    @JsonProperty
    private final int total;
    @JsonProperty
    private final int startIndex;
    @JsonProperty
    private final int pageSize;
    @JsonProperty
    private final Collection<SpaceDto> spaces;
    @JsonProperty
    private final int totalSpaces;
    @JsonProperty
    private final double percentage;
    @JsonProperty
    private boolean isCalculating;

    @JsonCreator
    public SpaceSearchResultDto(@JsonProperty(value="total") int total, @JsonProperty(value="startIndex") int startIndex, @JsonProperty(value="pageSize") int pageSize, @JsonProperty(value="spaces") Collection<SpaceDto> spaces, @JsonProperty(value="totalSpaces") int totalSpaces, @JsonProperty(value="percentage") double percentage, @JsonProperty(value="isCalculating") boolean isCalculating) {
        this.total = total;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.spaces = spaces;
        this.totalSpaces = totalSpaces;
        this.percentage = percentage;
        this.isCalculating = isCalculating;
    }

    public int getTotal() {
        return this.total;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public Collection<SpaceDto> getSpaces() {
        return this.spaces;
    }

    public int getTotalSpaces() {
        return this.totalSpaces;
    }

    public double getPercentage() {
        return this.percentage;
    }

    public boolean isCalculating() {
        return this.isCalculating;
    }
}


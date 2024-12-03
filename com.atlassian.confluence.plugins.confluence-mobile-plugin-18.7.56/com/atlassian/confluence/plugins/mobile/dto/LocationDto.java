/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class LocationDto {
    @JsonProperty
    private SpaceDto space;
    @JsonProperty
    private List<AbstractPageDto> ancestors;

    public LocationDto() {
    }

    public LocationDto(SpaceDto space, List<AbstractPageDto> ancestors) {
        this.space = space;
        this.ancestors = ancestors;
    }

    public SpaceDto getSpace() {
        return this.space;
    }

    public List<AbstractPageDto> getAncestors() {
        return this.ancestors;
    }
}


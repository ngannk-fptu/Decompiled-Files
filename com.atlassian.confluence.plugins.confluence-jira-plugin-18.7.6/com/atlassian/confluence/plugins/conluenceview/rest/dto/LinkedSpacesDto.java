/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.conluenceview.rest.dto;

import com.atlassian.confluence.plugins.conluenceview.rest.dto.GenericResponseDto;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.LinkedSpaceDto;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
public class LinkedSpacesDto
extends GenericResponseDto {
    private List<LinkedSpaceDto> spaces;

    protected LinkedSpacesDto(Builder builder) {
        super(builder);
        this.spaces = builder.spaces;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder
    extends GenericResponseDto.Builder {
        private List<LinkedSpaceDto> spaces;

        public Builder withSpaces(List<LinkedSpaceDto> spaces) {
            this.spaces = spaces;
            return this;
        }

        @Override
        public LinkedSpacesDto build() {
            return new LinkedSpacesDto(this);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.conluenceview.rest.dto;

import com.atlassian.confluence.plugins.conluenceview.rest.dto.ConfluencePageDto;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.GenericResponseDto;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
public class ConfluencePagesDto
extends GenericResponseDto {
    private Collection<ConfluencePageDto> pages;

    private ConfluencePagesDto(Builder builder) {
        super(builder);
        this.pages = builder.pages;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Collection<ConfluencePageDto> getPages() {
        return this.pages;
    }

    public static final class Builder
    extends GenericResponseDto.Builder {
        private Collection<ConfluencePageDto> pages;

        public Builder withPages(Collection<ConfluencePageDto> pages) {
            this.pages = pages;
            return this;
        }

        @Override
        public ConfluencePagesDto build() {
            return new ConfluencePagesDto(this);
        }
    }
}


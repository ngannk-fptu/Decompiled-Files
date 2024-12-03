/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConsentRequestDto {
    @JsonProperty
    private String displayedText;

    @Generated
    public static ConsentRequestDtoBuilder builder() {
        return new ConsentRequestDtoBuilder();
    }

    @Generated
    public String getDisplayedText() {
        return this.displayedText;
    }

    @Generated
    public void setDisplayedText(String displayedText) {
        this.displayedText = displayedText;
    }

    @Generated
    public ConsentRequestDto(String displayedText) {
        this.displayedText = displayedText;
    }

    @Generated
    public ConsentRequestDto() {
    }

    @Generated
    public static class ConsentRequestDtoBuilder {
        @Generated
        private String displayedText;

        @Generated
        ConsentRequestDtoBuilder() {
        }

        @Generated
        public ConsentRequestDtoBuilder displayedText(String displayedText) {
            this.displayedText = displayedText;
            return this;
        }

        @Generated
        public ConsentRequestDto build() {
            return new ConsentRequestDto(this.displayedText);
        }

        @Generated
        public String toString() {
            return "ConsentRequestDto.ConsentRequestDtoBuilder(displayedText=" + this.displayedText + ")";
        }
    }
}


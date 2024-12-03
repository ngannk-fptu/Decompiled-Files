/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.entity;

import com.atlassian.migration.agent.mapi.entity.MapiCheckDetailsDto;
import com.atlassian.migration.agent.mapi.entity.MapiOutcome;
import com.atlassian.migration.agent.mapi.entity.MapiStatus;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MapiStatusDto {
    @JsonProperty
    private List<String> level;
    @JsonProperty
    private MapiStatus status;
    @JsonProperty
    @Nullable
    private MapiOutcome outcome;
    @JsonProperty
    @Nullable
    private String message;
    @JsonProperty
    @Nullable
    private MapiCheckDetailsDto details;

    @Generated
    public static MapiStatusDtoBuilder builder() {
        return new MapiStatusDtoBuilder();
    }

    @Generated
    public MapiStatusDto(List<String> level, MapiStatus status, @Nullable MapiOutcome outcome, @Nullable String message, @Nullable MapiCheckDetailsDto details) {
        this.level = level;
        this.status = status;
        this.outcome = outcome;
        this.message = message;
        this.details = details;
    }

    @Generated
    public List<String> getLevel() {
        return this.level;
    }

    @Generated
    public MapiStatus getStatus() {
        return this.status;
    }

    @Nullable
    @Generated
    public MapiOutcome getOutcome() {
        return this.outcome;
    }

    @Nullable
    @Generated
    public String getMessage() {
        return this.message;
    }

    @Nullable
    @Generated
    public MapiCheckDetailsDto getDetails() {
        return this.details;
    }

    @Generated
    public void setLevel(List<String> level) {
        this.level = level;
    }

    @Generated
    public void setStatus(MapiStatus status) {
        this.status = status;
    }

    @Generated
    public void setOutcome(@Nullable MapiOutcome outcome) {
        this.outcome = outcome;
    }

    @Generated
    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Generated
    public void setDetails(@Nullable MapiCheckDetailsDto details) {
        this.details = details;
    }

    @Generated
    public static class MapiStatusDtoBuilder {
        @Generated
        private List<String> level;
        @Generated
        private MapiStatus status;
        @Generated
        private MapiOutcome outcome;
        @Generated
        private String message;
        @Generated
        private MapiCheckDetailsDto details;

        @Generated
        MapiStatusDtoBuilder() {
        }

        @Generated
        public MapiStatusDtoBuilder level(List<String> level) {
            this.level = level;
            return this;
        }

        @Generated
        public MapiStatusDtoBuilder status(MapiStatus status) {
            this.status = status;
            return this;
        }

        @Generated
        public MapiStatusDtoBuilder outcome(@Nullable MapiOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        @Generated
        public MapiStatusDtoBuilder message(@Nullable String message) {
            this.message = message;
            return this;
        }

        @Generated
        public MapiStatusDtoBuilder details(@Nullable MapiCheckDetailsDto details) {
            this.details = details;
            return this;
        }

        @Generated
        public MapiStatusDto build() {
            return new MapiStatusDto(this.level, this.status, this.outcome, this.message, this.details);
        }

        @Generated
        public String toString() {
            return "MapiStatusDto.MapiStatusDtoBuilder(level=" + this.level + ", status=" + (Object)((Object)this.status) + ", outcome=" + (Object)((Object)this.outcome) + ", message=" + this.message + ", details=" + this.details + ")";
        }
    }
}


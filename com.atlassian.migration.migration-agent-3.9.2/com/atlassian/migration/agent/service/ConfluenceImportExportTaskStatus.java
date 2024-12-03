/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.json.DurationLongDeserializer
 *  com.atlassian.migration.json.DurationLongSerializer
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.json.DurationLongDeserializer;
import com.atlassian.migration.json.DurationLongSerializer;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
@ParametersAreNonnullByDefault
public class ConfluenceImportExportTaskStatus {
    @JsonProperty
    private final String id;
    @JsonSerialize(using=DurationLongSerializer.class)
    @JsonDeserialize(using=DurationLongDeserializer.class)
    @JsonProperty
    private final Duration elapsedTime;
    @JsonProperty
    private final int percentageComplete;
    @JsonProperty
    private final boolean successful;
    @JsonProperty
    private final boolean complete;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final String result;
    @JsonProperty
    private final Integer statusCode;

    @JsonCreator
    public ConfluenceImportExportTaskStatus(@JsonProperty(value="id") String id, @JsonProperty(value="elapsedTime") Duration elapsedTime, @JsonProperty(value="percentageComplete") int percentageComplete, @JsonProperty(value="successful") boolean successful, @JsonProperty(value="complete") boolean complete, @JsonProperty(value="message") @Nullable String message, @JsonProperty(value="result") @Nullable String result, @JsonProperty(value="statusCode") @Nullable Integer statusCode) {
        this.id = Objects.requireNonNull(id);
        this.elapsedTime = Objects.requireNonNull(elapsedTime);
        this.percentageComplete = percentageComplete;
        this.successful = successful;
        this.complete = complete;
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public String getId() {
        return this.id;
    }

    public Duration getElapsedTime() {
        return this.elapsedTime;
    }

    public int getPercentageComplete() {
        return this.percentageComplete;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public boolean isComplete() {
        return this.complete;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

    @Nullable
    public String getResult() {
        return this.result;
    }

    @Nullable
    public Integer getStatusCode() {
        return this.statusCode;
    }

    public String toString() {
        return "ConfluenceImportExportTaskStatus{id='" + this.id + '\'' + ", elapsedTime=" + this.elapsedTime.getSeconds() + "s, percentageComplete=" + this.percentageComplete + ", successful=" + this.successful + ", complete=" + this.complete + ", message='" + this.message + '\'' + ", result='" + this.result + '\'' + ", statusCode='" + this.statusCode + '\'' + '}';
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private Duration elapsedTime;
        private int percentageComplete;
        private boolean successful;
        private boolean complete;
        private String message;
        private String result;
        private Integer statusCode;

        private Builder() {
        }

        @Nonnull
        public Builder id(String id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        @Nonnull
        public Builder elapsedTime(Duration elapsedTime) {
            this.elapsedTime = Objects.requireNonNull(elapsedTime);
            return this;
        }

        @Nonnull
        public Builder percentageComplete(int percentageComplete) {
            this.percentageComplete = percentageComplete;
            return this;
        }

        @Nonnull
        public Builder successful(boolean successful) {
            this.successful = successful;
            return this;
        }

        @Nonnull
        public Builder complete(boolean complete) {
            this.complete = complete;
            return this;
        }

        @Nonnull
        public Builder message(@Nullable String message) {
            this.message = message;
            return this;
        }

        @Nonnull
        public Builder result(@Nullable String result) {
            this.result = result;
            return this;
        }

        @Nonnull
        public Builder statusCode(@Nullable Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        @Nonnull
        public ConfluenceImportExportTaskStatus build() {
            return new ConfluenceImportExportTaskStatus(this.id, this.elapsedTime, this.percentageComplete, this.successful, this.complete, this.message, this.result, this.statusCode);
        }
    }
}


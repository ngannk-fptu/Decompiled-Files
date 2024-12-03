/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import com.atlassian.migration.agent.dto.assessment.AppUsageStatus;
import java.io.Serializable;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppUsageDto
implements Serializable {
    private static final long serialVersionUID = 8742143746873458992L;
    @JsonProperty
    private String key;
    @JsonProperty
    private boolean hasMacros;
    @JsonProperty
    private int users;
    @JsonProperty
    private int pages;
    @JsonProperty
    private AppUsageStatus status;
    @JsonIgnore
    @JsonProperty
    private long timeToCalculate;

    @JsonCreator
    public AppUsageDto(@JsonProperty(value="key") String key, @JsonProperty(value="hasMacros") boolean hasMacros, @JsonProperty(value="users") int users, @JsonProperty(value="pages") int pages, @JsonProperty(value="status") AppUsageStatus status, @JsonProperty(value="timeToCalculate") long timeToCalculate) {
        this.key = key;
        this.hasMacros = hasMacros;
        this.users = users;
        this.pages = pages;
        this.status = status;
        this.timeToCalculate = timeToCalculate;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isHasMacros() {
        return this.hasMacros;
    }

    public int getUsers() {
        return this.users;
    }

    public int getPages() {
        return this.pages;
    }

    public long getTimeToCalculate() {
        return this.timeToCalculate;
    }

    public AppUsageStatus getStatus() {
        return this.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals((Object)this, (Object)o, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this);
    }

    public static final class Builder {
        private String key;
        private boolean hasMacros;
        private int users = -1;
        private int pages = -1;
        private AppUsageStatus status = AppUsageStatus.RUNNING;
        private long timeToCalculate = -1L;

        private Builder() {
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder hasMacros(boolean hasMacros) {
            this.hasMacros = hasMacros;
            return this;
        }

        public Builder users(int users) {
            this.users = users;
            return this;
        }

        public Builder pages(int pages) {
            this.pages = pages;
            return this;
        }

        public Builder status(AppUsageStatus status) {
            this.status = status;
            return this;
        }

        public Builder timeToCalculate(long timeToCalculate) {
            this.timeToCalculate = timeToCalculate;
            return this;
        }

        public AppUsageDto build() {
            return new AppUsageDto(this.key, this.hasMacros, this.users, this.pages, this.status, this.timeToCalculate);
        }
    }
}


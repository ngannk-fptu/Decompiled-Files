/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Advisory {
    private final String name;
    private final String url;

    @JsonCreator
    public Advisory(@JsonProperty(value="name") String name, @JsonProperty(value="url") String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.name).append("url", (Object)this.url).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Advisory advisory = (Advisory)o;
        return new EqualsBuilder().append((Object)this.name, (Object)advisory.name).append((Object)this.url, (Object)advisory.url).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.name).append((Object)this.url).toHashCode();
    }
}


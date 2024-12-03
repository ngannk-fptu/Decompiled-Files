/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DefaultSupportHealthStatus
implements SupportHealthStatus {
    private static final String DEFAULT_DOCUMENTATION = "";
    private final boolean isHealthy;
    private final String failureReason;
    private final Application application;
    private final String nodeId;
    private final long time;
    private final SupportHealthStatus.Severity severity;
    private final String documentation;
    private final Set<SupportHealthStatus.Link> additionalLinks;

    public DefaultSupportHealthStatus(boolean isHealthy, String failureReason, long time, Application application, @Nullable String nodeId, SupportHealthStatus.Severity severity, String documentation) {
        this(isHealthy, failureReason, time, application, nodeId, severity, documentation, Collections.emptySet());
    }

    public DefaultSupportHealthStatus(boolean isHealthy, String failureReason, long time, Application application, @Nullable String nodeId, SupportHealthStatus.Severity severity, String documentation, Set<SupportHealthStatus.Link> additionalLinks) {
        this.isHealthy = isHealthy;
        this.failureReason = failureReason;
        this.time = time;
        this.application = application;
        this.nodeId = nodeId;
        this.severity = severity;
        this.documentation = documentation;
        this.additionalLinks = additionalLinks;
    }

    @Override
    public boolean isHealthy() {
        return this.isHealthy;
    }

    @Override
    public String failureReason() {
        return this.failureReason;
    }

    @Override
    public Application getApplication() {
        return this.application;
    }

    @Override
    public String getNodeId() {
        return this.nodeId;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getDocumentation() {
        return this.documentation;
    }

    @Override
    public SupportHealthStatus.Severity getSeverity() {
        return this.severity;
    }

    @Override
    public Set<SupportHealthStatus.Link> getAdditionalLinks() {
        return this.additionalLinks;
    }

    public static class DefaultLink
    implements SupportHealthStatus.Link {
        private final String displayName;
        private final String url;

        public DefaultLink(String displayName, String url) {
            this.displayName = displayName;
            this.url = url;
        }

        @Override
        public String getDisplayName() {
            return this.displayName;
        }

        @Override
        public String getUrl() {
            return this.url;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            DefaultLink that = (DefaultLink)o;
            return new EqualsBuilder().append((Object)this.displayName, (Object)that.displayName).append((Object)this.url, (Object)that.url).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append((Object)this.displayName).append((Object)this.url).toHashCode();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.Application;
import java.util.Collections;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public interface SupportHealthStatus {
    public boolean isHealthy();

    public String failureReason();

    public Application getApplication();

    default public String getNodeId() {
        return null;
    }

    public long getTime();

    public String getDocumentation();

    public Severity getSeverity();

    default public Set<Link> getAdditionalLinks() {
        return Collections.emptySet();
    }

    public static interface Link {
        public String getDisplayName();

        public String getUrl();
    }

    public static enum Severity {
        UNDEFINED,
        MINOR,
        WARNING,
        MAJOR,
        CRITICAL,
        DISABLED;


        public static Severity valueOf(int severityInt) {
            return Severity.values()[severityInt];
        }

        @JsonCreator
        public static Severity fromString(String name) {
            return Severity.valueOf(name.toUpperCase());
        }

        @JsonValue
        public String stringValue() {
            return this.name().toLowerCase();
        }
    }
}


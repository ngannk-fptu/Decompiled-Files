/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.healthcheck.core.rest;

import com.atlassian.healthcheck.core.HealthStatusExtended;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="healthStatus")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class HealthStatusRepresentation {
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private boolean isHealthy;
    @XmlElement
    private String failureReason;
    @XmlElement
    private String application;
    @XmlElement
    private long time;
    @XmlElement
    private HealthStatusExtended.Severity severity;
    @XmlElement
    private String documentation;

    public HealthStatusRepresentation(String name, String description, boolean isHealthy, String failureReason, String application, long time, HealthStatusExtended.Severity severity, String documentation) {
        this.name = name;
        this.description = description;
        this.isHealthy = isHealthy;
        this.failureReason = failureReason;
        this.application = application;
        this.time = time;
        this.severity = severity;
        this.documentation = documentation;
    }

    public HealthStatusRepresentation() {
    }

    public HealthStatusRepresentation(String name, String description, boolean isHealthy, String failureReason, String application, long time) {
        this(name, description, isHealthy, failureReason, application, time, HealthStatusExtended.Severity.UNDEFINED, "");
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isHealthy() {
        return this.isHealthy;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public String getApplication() {
        return this.application;
    }

    public long getTime() {
        return this.time;
    }

    public HealthStatusExtended.Severity getSeverity() {
        return this.severity;
    }

    public String getDocumentation() {
        return this.documentation;
    }
}


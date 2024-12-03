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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="healthCheckRepresentation")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class HealthCheckRepresentation {
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String completeKey;
    @XmlElement
    private String tag;
    @XmlElement
    private int timeout;

    private HealthCheckRepresentation() {
    }

    public HealthCheckRepresentation(String name, String description, String completeKey, String tag, int timeout) {
        this.name = name;
        this.description = description;
        this.completeKey = completeKey;
        this.tag = tag;
        this.timeout = timeout;
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

    public int getTimeout() {
        return this.timeout;
    }
}


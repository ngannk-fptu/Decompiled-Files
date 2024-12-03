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

import com.atlassian.healthcheck.core.rest.HealthStatusRepresentation;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="healthCheck")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class HealthCheckStatusesRepresentation {
    @XmlElement
    private List<HealthStatusRepresentation> status;

    private HealthCheckStatusesRepresentation() {
    }

    public HealthCheckStatusesRepresentation(List<HealthStatusRepresentation> status) {
        this.status = status;
    }

    public List<HealthStatusRepresentation> getStatus() {
        return this.status;
    }
}


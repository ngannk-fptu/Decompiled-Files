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

import com.atlassian.healthcheck.core.rest.HealthCheckRepresentation;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="healthCheckRepresentations")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class HealthCheckRepresentations {
    @XmlElement
    private Collection<HealthCheckRepresentation> healthCheckRepresentations;

    private HealthCheckRepresentations() {
    }

    public HealthCheckRepresentations(Collection<HealthCheckRepresentation> healthCheckRepresentations) {
        this.healthCheckRepresentations = healthCheckRepresentations;
    }

    public Collection<HealthCheckRepresentation> getHealthCheckRepresentations() {
        return this.healthCheckRepresentations;
    }
}


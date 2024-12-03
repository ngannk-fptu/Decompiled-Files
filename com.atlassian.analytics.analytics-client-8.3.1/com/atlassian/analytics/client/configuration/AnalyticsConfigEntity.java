/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.analytics.client.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="config")
@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class AnalyticsConfigEntity {
    private String destination;

    public AnalyticsConfigEntity() {
    }

    public AnalyticsConfigEntity(String destination) {
        this.destination = destination;
    }

    @XmlElement
    public String getDestination() {
        return this.destination;
    }
}


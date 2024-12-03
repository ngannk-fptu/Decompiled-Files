/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="webhook")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class WebhookEntity {
    @XmlElement(name="id")
    private final Long id = null;
    @XmlElement(name="endpointUrl")
    private String endpointUrl;
    @XmlElement(name="token")
    private String token;

    public WebhookEntity() {
    }

    public WebhookEntity(String endpointUrl, @Nullable String token) {
        this.endpointUrl = endpointUrl;
        this.token = token;
    }

    public long getId() {
        return this.id;
    }

    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    public String getToken() {
        return this.token;
    }
}


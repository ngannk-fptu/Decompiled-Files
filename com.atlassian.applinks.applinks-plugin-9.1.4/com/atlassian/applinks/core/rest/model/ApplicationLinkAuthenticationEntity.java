/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity;
import com.atlassian.applinks.core.rest.model.ConsumerEntity;
import com.atlassian.applinks.core.rest.model.LinkedEntity;
import com.atlassian.plugins.rest.common.Link;
import io.swagger.annotations.ApiModel;
import java.util.List;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement(name="applicationLinkAuthentication")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
@JsonIgnoreProperties(ignoreUnknown=true)
@ApiModel
public class ApplicationLinkAuthenticationEntity
extends LinkedEntity {
    @XmlElement(name="configuredAuthProviders")
    private List<AuthenticationProviderEntity> configuredAuthenticationProviders;
    @XmlElement(name="consumers")
    private List<ConsumerEntity> consumers;

    public ApplicationLinkAuthenticationEntity() {
    }

    public ApplicationLinkAuthenticationEntity(Link self, List<ConsumerEntity> consumers, List<AuthenticationProviderEntity> configuredAuthenticationProviders) {
        this.configuredAuthenticationProviders = configuredAuthenticationProviders;
        this.consumers = consumers;
        this.addLink(self);
    }

    @Nullable
    public List<AuthenticationProviderEntity> getConfiguredAuthProviders() {
        return this.configuredAuthenticationProviders;
    }

    @Nullable
    public List<ConsumerEntity> getConsumers() {
        return this.consumers;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.rest.model.LinkedEntity;
import com.atlassian.applinks.core.rest.model.adapter.ApplicationIdAdapter;
import com.atlassian.applinks.core.rest.model.adapter.OptionalURIAdapter;
import com.atlassian.applinks.core.rest.model.adapter.RequiredBaseURIAdapter;
import com.atlassian.applinks.core.rest.model.adapter.TypeIdAdapter;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.plugins.rest.common.Link;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement(name="applicationLink")
@com.fasterxml.jackson.databind.annotation.JsonSerialize
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
@org.codehaus.jackson.annotate.JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationLinkEntity
extends LinkedEntity {
    @XmlJavaTypeAdapter(value=ApplicationIdAdapter.class)
    private ApplicationId id;
    @XmlJavaTypeAdapter(value=TypeIdAdapter.class)
    private TypeId typeId;
    private String name;
    @XmlJavaTypeAdapter(value=RequiredBaseURIAdapter.class)
    private URI displayUrl;
    @Deprecated
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    private URI iconUrl;
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    private URI iconUri;
    @XmlJavaTypeAdapter(value=RequiredBaseURIAdapter.class)
    private URI rpcUrl;
    @XmlElement(name="isPrimary")
    private Boolean isPrimary;
    @XmlElement(name="isSystem")
    private Boolean isSystem;

    protected ApplicationLinkEntity() {
    }

    protected ApplicationLinkEntity(ApplicationLinkEntity entity) {
        super(entity.getLinks());
        this.id = entity.id;
        this.typeId = entity.typeId;
        this.name = entity.name;
        this.displayUrl = entity.displayUrl;
        this.iconUrl = entity.iconUrl;
        this.iconUri = entity.iconUri;
        this.isPrimary = entity.isPrimary;
        this.isSystem = entity.isSystem;
        this.rpcUrl = entity.rpcUrl;
    }

    public ApplicationLinkEntity(ApplicationLink applicationLink, Link self) {
        this(applicationLink.getId(), TypeId.getTypeId((ApplicationType)applicationLink.getType()), applicationLink.getName(), applicationLink.getDisplayUrl(), applicationLink.getType().getIconUrl(), IconUriResolver.resolveIconUri(applicationLink.getType()), applicationLink.getRpcUrl(), applicationLink.isPrimary(), applicationLink.isSystem(), self);
    }

    public ApplicationLinkEntity(ApplicationId id, TypeId typeId, String name, URI displayUrl, URI iconUrl, URI iconUri, URI rpcUrl, Boolean primary, Boolean isSystem, Link self) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.displayUrl = displayUrl;
        this.iconUrl = iconUrl;
        this.iconUri = iconUri;
        this.isPrimary = primary;
        this.isSystem = isSystem;
        this.iconUri = iconUri;
        if (!this.isSystem().booleanValue()) {
            this.rpcUrl = rpcUrl;
        }
        this.addLink(self);
    }

    public ApplicationLinkEntity(Link ... links) {
        super(links);
    }

    public ApplicationId getId() {
        return this.id;
    }

    public TypeId getTypeId() {
        return this.typeId;
    }

    public String getName() {
        return this.name;
    }

    public URI getDisplayUrl() {
        return this.displayUrl;
    }

    @Deprecated
    public URI getIconUrl() {
        return this.iconUrl;
    }

    public URI getIconUri() {
        return this.iconUri;
    }

    public URI getRpcUrl() {
        return this.isSystem() != false ? null : this.rpcUrl;
    }

    public boolean isPrimary() {
        return this.isPrimary != null && this.isPrimary != false;
    }

    public Boolean isSystem() {
        return this.isSystem != null && this.isSystem != false;
    }

    public ApplicationLinkDetails getDetails() {
        return ApplicationLinkDetails.builder().name(this.getName()).displayUrl(this.getDisplayUrl()).rpcUrl(this.getRpcUrl()).isPrimary(this.isPrimary()).build();
    }
}


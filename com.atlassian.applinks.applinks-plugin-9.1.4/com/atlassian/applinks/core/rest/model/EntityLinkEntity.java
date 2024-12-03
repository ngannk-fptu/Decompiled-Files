/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.rest.model.adapter.ApplicationIdAdapter;
import com.atlassian.applinks.core.rest.model.adapter.OptionalURIAdapter;
import com.atlassian.applinks.core.rest.model.adapter.TypeIdAdapter;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.spi.application.TypeId;
import java.net.URI;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="entityLink")
public class EntityLinkEntity {
    @XmlJavaTypeAdapter(value=ApplicationIdAdapter.class)
    private ApplicationId applicationId;
    @XmlJavaTypeAdapter(value=TypeIdAdapter.class)
    private TypeId typeId;
    private String key;
    private String name;
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    private URI displayUrl;
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    private URI iconUri;
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    private URI iconUrl;
    private Boolean isPrimary;

    private EntityLinkEntity() {
    }

    public EntityLinkEntity(EntityLink entity) {
        this(entity.getApplicationLink().getId(), entity.getKey(), TypeId.getTypeId((EntityType)entity.getType()), entity.getName(), entity.getDisplayUrl(), IconUriResolver.resolveIconUri(entity.getType()), entity.getType().getIconUrl(), entity.isPrimary());
    }

    public EntityLinkEntity(ApplicationId applicationId, String key, TypeId typeId, String name, URI displayUrl, URI iconUri, URI iconUrl, Boolean isPrimary) {
        this.applicationId = Objects.requireNonNull(applicationId, "applicationId can't be null");
        this.typeId = Objects.requireNonNull(typeId, "typeId can't be null");
        this.key = Objects.requireNonNull(key, "key can't be null");
        this.name = name != null ? name : key;
        this.displayUrl = displayUrl;
        this.iconUri = iconUri;
        this.iconUrl = iconUrl;
        this.isPrimary = isPrimary;
    }

    public ApplicationId getApplicationId() {
        return this.applicationId;
    }

    public TypeId getTypeId() {
        return this.typeId;
    }

    public URI getDisplayUrl() {
        return this.displayUrl;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public URI getIconUri() {
        return this.iconUri;
    }

    public URI getIconUrl() {
        return this.iconUrl;
    }

    public Boolean isPrimary() {
        return this.isPrimary;
    }
}


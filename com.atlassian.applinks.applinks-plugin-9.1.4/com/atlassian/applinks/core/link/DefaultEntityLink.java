/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.link;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.applinks.core.util.URIUtil;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEntityLink
implements EntityLink {
    private final PropertyService propertyService;
    private final URI displayUrl;
    private final String key;
    private final EntityType type;
    private final ApplicationLink applicationLink;
    private final boolean primary;
    private final String name;
    private static final Logger LOG = LoggerFactory.getLogger((String)DefaultEntityLink.class.getName());

    DefaultEntityLink(String key, EntityType type, String name, URI displayUrl, ApplicationLink applicationLink, PropertyService propertyService, boolean isPrimary) {
        this.name = name;
        this.propertyService = propertyService;
        this.displayUrl = displayUrl;
        this.key = key;
        this.type = type;
        this.applicationLink = applicationLink;
        this.primary = isPrimary;
    }

    public URI getDisplayUrl() {
        return URIUtil.copyOf(this.displayUrl);
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public EntityType getType() {
        return this.type;
    }

    public ApplicationLink getApplicationLink() {
        return this.applicationLink;
    }

    public Object getProperty(String key) {
        return this.propertyService.getProperties(this).getProperty(key);
    }

    public Object putProperty(String key, Object value) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Putting property for [%s] [%s] as [%s]", this, key, value);
            LOG.debug(message);
        }
        return this.propertyService.getProperties(this).putProperty(key, value);
    }

    public Object removeProperty(String key) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Removing property for [%s] [%s] was [%s]", this, key, this.propertyService.getProperties(this).getProperty(key));
            LOG.debug(message);
        }
        return this.propertyService.getProperties(this).removeProperty(key);
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public String toString() {
        return String.format("%s - %s (%s)", this.getType().getClass().getSimpleName(), this.getKey(), this.getApplicationLink().getId());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultEntityLink that = (DefaultEntityLink)o;
        if (!this.applicationLink.equals(that.applicationLink)) {
            return false;
        }
        if (!this.key.equals(that.key)) {
            return false;
        }
        return this.type.equals(that.type);
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.applicationLink.hashCode();
        return result;
    }
}


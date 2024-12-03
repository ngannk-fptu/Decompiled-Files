/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.link.EntityLinkBuilderFactory
 *  com.atlassian.applinks.spi.link.EntityLinkBuilderFactory$EntityLinkBuilder
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.link;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.link.DefaultEntityLink;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.applinks.spi.link.EntityLinkBuilderFactory;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultEntityLinkBuilderFactory
implements EntityLinkBuilderFactory {
    private final PropertyService propertyService;

    @Autowired
    public DefaultEntityLinkBuilderFactory(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    public EntityLinkBuilderFactory.EntityLinkBuilder builder() {
        return new DefaultEntityLinkBuilder();
    }

    public class DefaultEntityLinkBuilder
    implements EntityLinkBuilderFactory.EntityLinkBuilder {
        private ApplicationLink applicationLink;
        private EntityType type;
        private String key;
        private String name;
        private boolean primary = false;

        public EntityLinkBuilderFactory.EntityLinkBuilder key(String key) {
            this.key = key;
            return this;
        }

        public EntityLinkBuilderFactory.EntityLinkBuilder type(EntityType type) {
            this.type = type;
            return this;
        }

        public EntityLinkBuilderFactory.EntityLinkBuilder applicationLink(ApplicationLink applicationLink) {
            this.applicationLink = applicationLink;
            return this;
        }

        public EntityLinkBuilderFactory.EntityLinkBuilder primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        public EntityLinkBuilderFactory.EntityLinkBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EntityLink build() {
            if (this.name == null) {
                this.name = this.key;
            }
            return new DefaultEntityLink(Objects.requireNonNull(this.key, "key"), Objects.requireNonNull(this.type, "type"), Objects.requireNonNull(this.name, "name"), this.type.getDisplayUrl(Objects.requireNonNull(this.applicationLink), this.key), Objects.requireNonNull(this.applicationLink, "applicationLink"), DefaultEntityLinkBuilderFactory.this.propertyService, this.primary);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.host.spi.DefaultEntityReference
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.model.adapter.OptionalURIAdapter;
import com.atlassian.applinks.core.rest.model.adapter.TypeIdAdapter;
import com.atlassian.applinks.host.spi.DefaultEntityReference;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.spi.application.TypeId;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="entities")
public class ReferenceEntityList {
    private final List<ReferenceEntity> entity = new ArrayList<ReferenceEntity>();

    private ReferenceEntityList() {
    }

    public ReferenceEntityList(Iterable<EntityReference> entities) {
        Iterables.addAll(this.entity, (Iterable)Iterables.transform(entities, (Function)new Function<EntityReference, ReferenceEntity>(){

            public ReferenceEntity apply(EntityReference from) {
                return new ReferenceEntity(from.getKey(), from.getName(), TypeId.getTypeId((EntityType)from.getType()), IconUriResolver.resolveIconUri(from.getType()), from.getType().getIconUrl());
            }
        }));
    }

    public Iterable<EntityReference> getEntities(final InternalTypeAccessor typeAccessor) {
        return Iterables.transform(this.entity, (Function)new Function<ReferenceEntity, EntityReference>(){

            public EntityReference apply(ReferenceEntity from) {
                return new DefaultEntityReference(from.key, from.name, typeAccessor.loadEntityType(from.typeId.get()));
            }
        });
    }

    public static class ReferenceEntity {
        @XmlAttribute
        private String key;
        @XmlAttribute
        private String name;
        @XmlAttribute
        @XmlJavaTypeAdapter(value=TypeIdAdapter.class)
        private TypeId typeId;
        @XmlAttribute
        @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
        private URI iconUri;
        @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
        private URI iconUrl;

        private ReferenceEntity() {
        }

        private ReferenceEntity(String key, String name, TypeId typeId, URI iconUri, URI iconUrl) {
            this.key = key;
            this.name = name;
            this.typeId = typeId;
            this.iconUri = iconUri;
            this.iconUrl = iconUrl;
        }
    }
}


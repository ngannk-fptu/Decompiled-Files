/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.projectcreate.spi.AggregateRoot
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.projectcreate.producer.crud.rest;

import com.atlassian.plugins.projectcreate.spi.AggregateRoot;
import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class SingleAggregateRootRepresentation {
    @XmlElement
    public LinksSection links;
    @XmlElement
    public String label;
    @XmlElement
    public String key;

    public SingleAggregateRootRepresentation(AggregateRoot entity, String baseUrl, String entityType) {
        this.links = new LinksSection(baseUrl, entityType, entity.key(), entity.homeUri());
        this.label = entity.label();
        this.key = entity.key();
    }

    @XmlRootElement
    public static class LinksSection {
        @XmlElement
        public String self;
        @XmlElement
        public String resource;
        @XmlElement
        public String collection;

        public LinksSection() {
        }

        public LinksSection(String baseUrl, String entityType, String entityKey, URI entityHomeUri) {
            this.self = baseUrl + "/rest/capabilities/aggregate-root/" + entityType + "/" + entityKey;
            this.resource = entityHomeUri.toString();
            this.collection = baseUrl + "/rest/capabilities/aggregate-root/" + entityType;
        }
    }
}


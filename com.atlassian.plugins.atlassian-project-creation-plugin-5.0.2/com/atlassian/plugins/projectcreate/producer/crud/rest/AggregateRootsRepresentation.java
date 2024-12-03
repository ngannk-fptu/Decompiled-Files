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
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AggregateRootsRepresentation {
    @XmlElement
    public LinksSection links;
    @XmlElement
    public String type;
    @XmlElement
    public HashMap<String, RootRepresentation> keys = new HashMap();

    public AggregateRootsRepresentation(Iterable<AggregateRoot> rawRoots, String baseUrl, String entityType) {
        for (AggregateRoot root : rawRoots) {
            this.keys.put(root.key(), new RootRepresentation(baseUrl + "/rest/capabilities/aggregate-root/" + entityType + "/" + root.key(), root.label()));
        }
        this.links = new LinksSection(baseUrl, entityType);
        this.type = entityType;
    }

    @XmlRootElement
    public static class RootRepresentation {
        @XmlElement
        public String href;
        @XmlElement
        public String label;

        public RootRepresentation(String href, String label) {
            this.href = href;
            this.label = label;
        }
    }

    @XmlRootElement
    public static class LinksSection {
        @XmlElement
        public String self;
        @XmlElement
        public String collection;

        public LinksSection() {
        }

        public LinksSection(String baseUrl, String entityType) {
            this.self = baseUrl + "/rest/capabilities/aggregate-root/" + entityType;
            this.collection = baseUrl + "/rest/capabilities/aggregate-root/";
        }
    }
}


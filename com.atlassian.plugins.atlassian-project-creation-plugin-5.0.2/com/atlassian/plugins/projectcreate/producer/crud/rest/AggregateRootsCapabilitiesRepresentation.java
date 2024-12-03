/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootSubType
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.plugins.projectcreate.producer.crud.rest;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.projectcreate.spi.AggregateRootSubType;
import com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.beans.factory.annotation.Autowired;

@XmlRootElement
public class AggregateRootsCapabilitiesRepresentation {
    public static final String CAPABILITIES_PATH = "/rest/capabilities";
    @XmlElement
    public LinksSection links;
    @XmlElement
    public HashMap<String, CapabilityType> types;

    public AggregateRootsCapabilitiesRepresentation() {
    }

    @Autowired
    public AggregateRootsCapabilitiesRepresentation(@ComponentImport I18nResolver i18nResolver, Iterable<AggregateRootTypeCapability> projectCreationCapabilities, String baseUrl) {
        this.links = new LinksSection(baseUrl);
        this.types = new HashMap();
        for (AggregateRootTypeCapability aggregateRootTypeCapability : projectCreationCapabilities) {
            if (!aggregateRootTypeCapability.isAvailable()) continue;
            String url = baseUrl + "/rest/capabilities/aggregate-root/" + aggregateRootTypeCapability.getType();
            this.types.put(aggregateRootTypeCapability.getType(), new CapabilityType(i18nResolver, url, i18nResolver.getText(aggregateRootTypeCapability.getLabelI18nKey()), aggregateRootTypeCapability.getSubTypes()));
        }
    }

    @XmlRootElement
    public static class SubType {
        @XmlElement
        public String label;
        @XmlElement(name="default")
        public Boolean isDefault;

        public SubType() {
        }

        public SubType(String labelText) {
            this.label = labelText;
            this.isDefault = null;
        }

        public SubType(String label, Boolean aDefault) {
            this.label = label;
            this.isDefault = aDefault;
        }
    }

    @XmlRootElement
    public static class CapabilityType {
        @XmlElement
        public String href;
        @XmlElement
        public String label;
        @XmlElement
        public HashMap<String, SubType> subtypes;

        public CapabilityType() {
        }

        public CapabilityType(String href, String label) {
            this.href = href;
            this.subtypes = null;
            this.label = label;
        }

        public CapabilityType(I18nResolver i18nResolver, String href, String label, Iterable<AggregateRootSubType> subTypes) {
            this.href = href;
            this.label = label;
            this.subtypes = new HashMap();
            for (AggregateRootSubType subType : subTypes) {
                this.subtypes.put(subType.getKey(), new SubType(i18nResolver.getText(subType.getLabelI18nKey())));
            }
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

        public LinksSection(String baseUrl) {
            this.self = baseUrl + "/rest/capabilities/aggregate-root/";
            this.collection = baseUrl + AggregateRootsCapabilitiesRepresentation.CAPABILITIES_PATH;
        }
    }
}


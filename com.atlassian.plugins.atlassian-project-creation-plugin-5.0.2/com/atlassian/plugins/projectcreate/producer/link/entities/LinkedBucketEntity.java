/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.plugins.projectcreate.producer.link.entities;

import javax.xml.bind.annotation.XmlElement;

public class LinkedBucketEntity {
    @XmlElement
    private String label;
    @XmlElement
    private String key;
    @XmlElement(name="instance-id")
    private String instanceId;
    @XmlElement(name="type")
    private String type;
    @XmlElement(name="url")
    private String url;
    @XmlElement(name="link-url")
    private String linkUrl;

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkUrl() {
        return this.linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.plugins.rest.common.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="password")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class PasswordEntity {
    @XmlElement(name="link")
    private final Link link;
    @XmlElement(name="value")
    private final String value;

    private PasswordEntity() {
        this.value = null;
        this.link = null;
    }

    public PasswordEntity(String value, Link link) {
        this.value = value;
        this.link = link;
    }

    public String getValue() {
        return this.value;
    }

    public Link getLink() {
        return this.link;
    }
}


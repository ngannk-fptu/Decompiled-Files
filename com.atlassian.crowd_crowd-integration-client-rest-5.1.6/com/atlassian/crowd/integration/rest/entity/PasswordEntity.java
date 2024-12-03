/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="password")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class PasswordEntity {
    @XmlElement(name="value")
    private final String value;

    private PasswordEntity() {
        this.value = null;
    }

    public PasswordEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}


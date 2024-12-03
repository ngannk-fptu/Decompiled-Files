/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.authentication.impl.basicauth.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="warning")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class BasicAuthMessageEntity {
    @XmlElement
    String message;

    public BasicAuthMessageEntity() {
    }

    public BasicAuthMessageEntity(String message) {
        this.message = message;
    }
}


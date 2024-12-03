/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="rename")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class RenameEntity {
    @XmlElement(name="new-name")
    private final String newName;

    private RenameEntity() {
        this(null);
    }

    public RenameEntity(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return this.newName;
    }
}


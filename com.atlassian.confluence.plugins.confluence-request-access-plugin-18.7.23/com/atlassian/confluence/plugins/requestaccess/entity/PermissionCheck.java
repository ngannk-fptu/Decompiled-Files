/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.requestaccess.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="permissionCheck")
public class PermissionCheck {
    @XmlElement(name="hasPermission")
    private final boolean hasPermission;

    public PermissionCheck(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public boolean isHasPermission() {
        return this.hasPermission;
    }
}


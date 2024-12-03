/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.user.extras.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="groups-entity")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupsEntity {
    @XmlElementWrapper(name="groups")
    @XmlElement(name="group")
    private List<String> groups;

    public List<String> getGroups() {
        return this.groups;
    }

    public void setGroups(List<String> groupName) {
        this.groups = groupName;
    }
}


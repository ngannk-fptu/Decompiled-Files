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

import com.atlassian.confluence.user.extras.entities.UserEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="users-entity")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UsersEntity {
    @XmlElementWrapper(name="users")
    @XmlElement(name="user")
    private List<UserEntity> users;

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<UserEntity> getUsers() {
        return this.users;
    }
}


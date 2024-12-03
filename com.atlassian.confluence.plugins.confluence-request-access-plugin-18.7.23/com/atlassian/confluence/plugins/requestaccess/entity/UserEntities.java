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

import com.atlassian.confluence.plugins.requestaccess.entity.UserEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="users")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntities {
    @XmlElement
    private final List<UserEntity> users;

    public UserEntities(List<UserEntity> users) {
        this.users = users;
    }

    public List<UserEntity> getUsers() {
        return this.users;
    }
}


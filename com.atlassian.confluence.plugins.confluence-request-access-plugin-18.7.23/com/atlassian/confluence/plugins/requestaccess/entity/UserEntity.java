/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.requestaccess.entity;

import com.atlassian.user.User;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntity {
    @XmlElement
    private final String fullName;
    @XmlElement
    private final String name;
    @XmlElement
    private final String email;

    public UserEntity(User user) {
        this.fullName = user.getFullName();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.Session
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.UserEntity;
import com.atlassian.crowd.model.authentication.Session;
import java.util.Date;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="session")
public class SessionEntity
implements Session {
    @XmlElement
    private final String token;
    @XmlElement
    private final UserEntity user;
    @XmlElement(name="created-date")
    private Date createdDate;
    @XmlElement(name="expiry-date")
    private Date expiryDate;
    @XmlElement(name="unaliased-username")
    private String unaliasedUsername;

    private SessionEntity() {
        this.token = null;
        this.user = null;
        this.createdDate = null;
        this.expiryDate = null;
    }

    public SessionEntity(String token, UserEntity user, Date createdDate, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.createdDate = createdDate;
        this.expiryDate = expiryDate;
    }

    public String getToken() {
        return this.token;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    @Nullable
    public String getUnaliasedUsername() {
        return this.unaliasedUsername;
    }
}


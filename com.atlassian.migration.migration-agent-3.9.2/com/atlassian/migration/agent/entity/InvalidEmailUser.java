/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ReflectionToStringBuilder
 */
package com.atlassian.migration.agent.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name="MIG_INVALID_EMAIL_USER")
public class InvalidEmailUser {
    @Id
    @Column(name="userName", nullable=false)
    private String userName;
    @Column(name="email", nullable=false)
    private String email;
    @Column(name="created", nullable=false)
    private Instant created;

    public InvalidEmailUser() {
    }

    public InvalidEmailUser(String userName, String email) {
        this.userName = userName;
        this.email = email;
        this.created = Instant.now();
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString((Object)this);
    }
}


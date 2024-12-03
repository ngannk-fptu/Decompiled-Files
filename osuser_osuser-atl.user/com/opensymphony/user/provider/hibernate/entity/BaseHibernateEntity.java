/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.entity;

import com.opensymphony.user.provider.hibernate.entity.HibernateEntity;

public abstract class BaseHibernateEntity
implements HibernateEntity {
    private String name;
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.GeneratedValue
 *  javax.persistence.Id
 *  javax.persistence.MappedSuperclass
 *  org.hibernate.annotations.GenericGenerator
 */
package com.atlassian.migration.agent.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class WithId {
    @Id
    @GeneratedValue(generator="uuid2")
    @GenericGenerator(name="uuid2", strategy="uuid2")
    @Column(name="id", unique=true)
    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WithId withId = (WithId)o;
        return Objects.equals(this.id, withId.id);
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }
}


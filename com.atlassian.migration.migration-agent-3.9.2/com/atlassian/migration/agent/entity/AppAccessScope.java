/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.migration.agent.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Table(name="MIG_APP_ACCESS_SCOPE")
@Entity
public class AppAccessScope
implements Serializable {
    @Id
    @Column(name="serverAppKey", nullable=false)
    private String serverAppKey;
    @Id
    @Column(name="accessScope", nullable=false)
    private String accessScope;

    public AppAccessScope() {
    }

    public AppAccessScope(String serverAppKey, String accessScope) {
        this.serverAppKey = serverAppKey;
        this.accessScope = accessScope;
    }

    public String getServerAppKey() {
        return this.serverAppKey;
    }

    public void setServerAppKey(String serverAppKey) {
        this.serverAppKey = serverAppKey;
    }

    public String getAccessScope() {
        return this.accessScope;
    }

    public void setAccessScope(String accessScope) {
        this.accessScope = accessScope;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AppAccessScope other = (AppAccessScope)o;
        return Objects.equals(this.serverAppKey, other.serverAppKey) && Objects.equals(this.accessScope, other.accessScope);
    }

    public int hashCode() {
        return Objects.hash(this.serverAppKey, this.accessScope);
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.NO_CLASS_NAME_STYLE).append("appKey", (Object)this.serverAppKey).append("accessScope", (Object)this.accessScope).build();
    }
}


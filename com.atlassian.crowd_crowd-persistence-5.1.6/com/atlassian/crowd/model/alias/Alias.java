/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.application.Application
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.alias;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.application.Application;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Alias
implements Serializable {
    private Long id;
    private Application application;
    private String name;
    private String lowerName;
    private String alias;
    private String lowerAlias;

    private Alias() {
    }

    public Alias(Long id, Application application, String name, String alias) {
        this(application, name, alias);
        this.id = id;
    }

    public Alias(Application application, String name, String alias) {
        this.application = application;
        this.setName(name);
        this.setAlias(alias);
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
        this.lowerAlias = IdentifierUtils.toLowerCase((String)alias);
    }

    public Application getApplication() {
        return this.application;
    }

    private void setApplication(Application application) {
        this.application = application;
    }

    public String getName() {
        return this.name;
    }

    private void setName(String name) {
        this.name = name;
        this.lowerName = IdentifierUtils.toLowerCase((String)name);
    }

    public String getLowerAlias() {
        return this.lowerAlias;
    }

    private void setLowerAlias(String lowerAlias) {
        this.lowerAlias = lowerAlias;
    }

    public String getLowerName() {
        return this.lowerName;
    }

    private void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alias)) {
            return false;
        }
        Alias alias = (Alias)o;
        if (this.getApplication().getId() != null ? !this.getApplication().getId().equals(alias.getApplication().getId()) : alias.getApplication().getId() != null) {
            return false;
        }
        return !(this.getLowerName() != null ? !this.getLowerName().equals(alias.getLowerName()) : alias.getLowerName() != null);
    }

    public int hashCode() {
        int result = this.getLowerName() != null ? this.getLowerName().hashCode() : 0;
        result = 31 * result + (this.getApplication().getId() != null ? this.getApplication().getId().hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.id).append("application", (Object)this.application).append("name", (Object)this.name).append("lowerName", (Object)this.lowerName).append("alias", (Object)this.alias).append("lowerAlias", (Object)this.lowerAlias).toString();
    }
}


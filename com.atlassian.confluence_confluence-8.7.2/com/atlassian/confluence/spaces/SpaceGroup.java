/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.impl.hibernate.Hibernate;

@Deprecated
public class SpaceGroup
extends ConfluenceEntityObject
implements NotExportable {
    private String name;
    private String key;
    private String licenseKey;

    public SpaceGroup() {
    }

    public SpaceGroup(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass((Object)this) != Hibernate.getClass((Object)o)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpaceGroup that = (SpaceGroup)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        if (this.licenseKey != null ? !this.licenseKey.equals(that.licenseKey) : that.licenseKey != null) {
            return false;
        }
        return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }
}


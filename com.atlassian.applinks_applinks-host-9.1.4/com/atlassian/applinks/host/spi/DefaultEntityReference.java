/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityType
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.host.spi;

import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.host.spi.EntityReference;
import org.apache.commons.lang3.StringUtils;

public class DefaultEntityReference
implements EntityReference {
    private final String key;
    private final String name;
    private final EntityType type;

    public DefaultEntityReference(String key, String name, EntityType type) {
        this.key = key;
        this.name = StringUtils.isEmpty((CharSequence)name) ? key : name;
        this.type = type;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EntityType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultEntityReference that = (DefaultEntityReference)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.type != null ? !this.type.equals(that.type) : that.type != null);
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        return result;
    }

    public String toString() {
        return this.type + ":" + this.key;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.user.provider.ProfileProvider
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.osuser.properties;

import com.atlassian.user.Entity;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.provider.ProfileProvider;
import org.apache.log4j.Logger;

public class OSUPropertySetFactory
implements PropertySetFactory {
    protected final Logger log = Logger.getLogger(this.getClass());
    private final ProfileProvider profileProvider;

    public OSUPropertySetFactory(ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    public PropertySet getPropertySet(Entity entity) {
        PropertySet ps = null;
        String entityName = entity.getName();
        if (this.profileProvider.handles(entityName)) {
            ps = this.profileProvider.getPropertySet(entityName);
        } else {
            String lowercasedEntityName = entityName.toLowerCase();
            this.log.info((Object)("No propertyset for user [" + entityName + "]. Trying lower case form - [" + lowercasedEntityName + "]"));
            if (this.profileProvider.handles(lowercasedEntityName)) {
                ps = this.profileProvider.getPropertySet(lowercasedEntityName);
            }
        }
        return ps;
    }
}


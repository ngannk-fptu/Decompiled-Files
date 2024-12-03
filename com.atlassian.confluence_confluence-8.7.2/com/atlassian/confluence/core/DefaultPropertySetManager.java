/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ConfluencePropertySetManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import java.util.HashMap;
import java.util.Map;

public class DefaultPropertySetManager
implements ConfluencePropertySetManager {
    @Override
    public PropertySet getPropertySet(Object owner) {
        TransientHibernateHandle handle;
        Map<String, Object> args = null;
        if (owner instanceof ContentEntityObject) {
            ContentEntityObject contentEntityObject = (ContentEntityObject)owner;
            args = this.createArgs(contentEntityObject.getId());
        } else if (owner instanceof TransientHibernateHandle && ContentEntityObject.class.isAssignableFrom((handle = (TransientHibernateHandle)owner).getClazz())) {
            args = this.createArgs((Long)handle.getId());
        }
        if (args != null) {
            return PropertySetManager.getInstance((String)"hibernate", args);
        }
        throw new IllegalArgumentException("Unsupported property set owner: " + owner.getClass().getName());
    }

    private Map<String, Object> createArgs(Long id) {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("entityId", id);
        args.put("entityName", "confluence_ContentEntityObject");
        return args;
    }
}


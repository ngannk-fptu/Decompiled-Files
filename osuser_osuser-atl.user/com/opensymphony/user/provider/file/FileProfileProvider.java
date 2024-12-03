/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.file.FilePropertySetCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileProfileProvider
implements ProfileProvider {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$FileProfileProvider == null ? (class$com$opensymphony$user$provider$file$FileProfileProvider = FileProfileProvider.class$("com.opensymphony.user.provider.file.FileProfileProvider")) : class$com$opensymphony$user$provider$file$FileProfileProvider));
    protected FilePropertySetCache propertySetCache;
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$FileProfileProvider;

    public PropertySet getPropertySet(String name) {
        if (!this.propertySetCache.propertySets.containsKey(name)) {
            return null;
        }
        return (PropertySet)this.propertySetCache.propertySets.get(name);
    }

    public boolean create(String name) {
        if (this.propertySetCache.propertySets.containsKey(name)) {
            return false;
        }
        PropertySet propertySet = PropertySetManager.getInstance((String)"serializable", null);
        this.propertySetCache.propertySets.put(name, propertySet);
        return this.propertySetCache.store();
    }

    public void flushCaches() {
        this.propertySetCache.store();
    }

    public boolean handles(String name) {
        return this.propertySetCache.propertySets.containsKey(name);
    }

    public boolean init(Properties properties) {
        return true;
    }

    public List list() {
        return Collections.unmodifiableList(new ArrayList(this.propertySetCache.propertySets.keySet()));
    }

    public boolean load(String name, Entity.Accessor accessor) {
        return true;
    }

    public boolean remove(String name) {
        boolean rv = this.propertySetCache.propertySets.remove(name) != null;
        return rv && this.propertySetCache.store();
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return this.propertySetCache.store();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


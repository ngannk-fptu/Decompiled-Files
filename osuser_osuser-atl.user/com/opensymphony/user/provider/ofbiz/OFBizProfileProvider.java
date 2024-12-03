/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 *  org.apache.log4j.Category
 *  org.ofbiz.core.entity.GenericEntityException
 *  org.ofbiz.core.entity.GenericValue
 */
package com.opensymphony.user.provider.ofbiz;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.ofbiz.OFBizAbstractProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Category;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

public class OFBizProfileProvider
extends OFBizAbstractProvider
implements ProfileProvider {
    private static final Category LOG = Category.getInstance((Class)(class$com$opensymphony$user$provider$ofbiz$OFBizProfileProvider == null ? (class$com$opensymphony$user$provider$ofbiz$OFBizProfileProvider = OFBizProfileProvider.class$("com.opensymphony.user.provider.ofbiz.OFBizProfileProvider")) : class$com$opensymphony$user$provider$ofbiz$OFBizProfileProvider));
    protected Map psCache;
    protected String psEntity;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ofbiz$OFBizProfileProvider;

    public PropertySet getPropertySet(String name) {
        Object cachedValue;
        if (this.exclusiveAccess && (cachedValue = this.psCache.get(name)) != null) {
            return (PropertySet)cachedValue;
        }
        try {
            GenericValue v = this.findUser(name);
            if (v != null) {
                PropertySet ps = null;
                if (this.exclusiveAccess) {
                    HashMap<String, Object> args = new HashMap<String, Object>();
                    args.put("entityId", v.getLong("id"));
                    args.put("entityName", this.psEntity);
                    PropertySet basePs = PropertySetManager.getInstance((String)"ofbiz", args);
                    args = new HashMap();
                    args.put("PropertySet", basePs);
                    ps = PropertySetManager.getInstance((String)"cached", args);
                    this.psCache.put(name, ps);
                } else {
                    HashMap<String, Object> args = new HashMap<String, Object>();
                    args.put("entityId", v.getLong("id"));
                    args.put("entityName", this.psEntity);
                    ps = PropertySetManager.getInstance((String)"ofbiz", args);
                }
                return ps;
            }
        }
        catch (GenericEntityException genericEntityException) {
            // empty catch block
        }
        return null;
    }

    public void flushCaches() {
        super.flushCaches();
        if (this.exclusiveAccess) {
            this.psCache = Collections.synchronizedMap(new HashMap());
        }
    }

    public boolean handles(String name) {
        try {
            GenericValue v = this.findUser(name);
            if (v != null) {
                return true;
            }
        }
        catch (GenericEntityException genericEntityException) {
            // empty catch block
        }
        return false;
    }

    public boolean init(Properties properties) {
        boolean superResult = super.init(properties);
        this.psEntity = properties.getProperty("propertySetEntity", "OSUser");
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("propertySetEntity: " + this.psEntity));
        }
        if (this.exclusiveAccess) {
            this.psCache = Collections.synchronizedMap(new HashMap());
        }
        return superResult;
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


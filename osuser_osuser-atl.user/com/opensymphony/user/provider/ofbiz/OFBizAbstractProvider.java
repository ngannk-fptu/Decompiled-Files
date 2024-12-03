/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 *  org.ofbiz.core.entity.EntityUtil
 *  org.ofbiz.core.entity.GenericDelegator
 *  org.ofbiz.core.entity.GenericEntityException
 *  org.ofbiz.core.entity.GenericValue
 *  org.ofbiz.core.util.UtilMisc
 */
package com.opensymphony.user.provider.ofbiz;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.UserProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Category;
import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.UtilMisc;

public abstract class OFBizAbstractProvider
implements UserProvider {
    protected final Category LOG = Category.getInstance(this.getClass());
    protected Map nameCache;
    protected String delegator;
    protected String userEntity;
    protected boolean exclusiveAccess = false;

    public boolean create(String s) {
        return true;
    }

    public void flushCaches() {
        this.nameCache = Collections.synchronizedMap(new HashMap());
        this.clearAllCache();
    }

    public boolean init(Properties properties) {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug((Object)("init(" + properties.toString() + ")"));
        }
        if (properties.getProperty("exclusive-access") != null && "true".equalsIgnoreCase(properties.getProperty("exclusive-access"))) {
            this.exclusiveAccess = true;
            this.nameCache = Collections.synchronizedMap(new HashMap());
        }
        this.delegator = properties.getProperty("delegator", "default");
        this.userEntity = properties.getProperty("userEntity", "OSUser");
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug((Object)("delegator: " + this.delegator));
            this.LOG.debug((Object)("exclusiveAccess: " + this.exclusiveAccess));
            this.LOG.debug((Object)("userEntity: " + this.userEntity));
        }
        return true;
    }

    public List list() {
        return null;
    }

    public boolean load(String s, Entity.Accessor accessor) {
        return true;
    }

    public boolean remove(String s) {
        return true;
    }

    public boolean store(String s, Entity.Accessor accessor) {
        return true;
    }

    protected GenericDelegator getDelegator() {
        return GenericDelegator.getGenericDelegator((String)this.delegator);
    }

    protected void clearAllCache() {
        if (this.exclusiveAccess) {
            this.getDelegator().clearCacheLine(this.userEntity, null);
        }
    }

    protected void clearUserCache(String name) {
        if (this.exclusiveAccess) {
            this.nameCache.remove(name);
        }
    }

    protected GenericValue findUser(String name) throws GenericEntityException {
        GenericValue user = null;
        if (this.exclusiveAccess) {
            user = (GenericValue)this.nameCache.get(name);
        }
        if (user == null) {
            List userId = this.getDelegator().findByAnd(this.userEntity, UtilMisc.toMap((String)"name", (Object)name));
            if (userId.size() > 1) {
                throw new RuntimeException("Found more than one user with name '" + name + "'; ids " + this.printList(userId));
            }
            user = EntityUtil.getOnly((List)userId);
            if (this.exclusiveAccess && user != null) {
                this.nameCache.put(user.getString("name"), user);
            }
        }
        if (user == null) {
            this.LOG.debug((Object)("user " + name + " not found"));
        }
        return user;
    }

    private final String printList(List list) {
        StringBuffer buf = new StringBuffer();
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            buf.append(iter.next());
            if (!iter.hasNext()) continue;
            buf.append(", ");
        }
        return buf.toString();
    }
}


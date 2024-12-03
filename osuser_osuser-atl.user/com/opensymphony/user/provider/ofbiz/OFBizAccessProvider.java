/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 *  org.ofbiz.core.entity.GenericEntityException
 *  org.ofbiz.core.entity.GenericValue
 *  org.ofbiz.core.util.UtilMisc
 */
package com.opensymphony.user.provider.ofbiz;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.ofbiz.OFBizAbstractProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Category;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.UtilMisc;

public class OFBizAccessProvider
extends OFBizAbstractProvider
implements AccessProvider {
    private static final Category LOG = Category.getInstance((Class)(class$com$opensymphony$user$provider$ofbiz$OFBizAccessProvider == null ? (class$com$opensymphony$user$provider$ofbiz$OFBizAccessProvider = OFBizAccessProvider.class$("com.opensymphony.user.provider.ofbiz.OFBizAccessProvider")) : class$com$opensymphony$user$provider$ofbiz$OFBizAccessProvider));
    protected List groupsCache;
    protected Map groupUsersCache;
    protected Map userGroupsCache;
    protected Object groupsCacheLock = new Object();
    protected String groupEntity;
    protected String groupSequence;
    protected String membershipEntity;
    protected String membershipSequence;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ofbiz$OFBizAccessProvider;

    public boolean addToGroup(String userName, String groupName) {
        try {
            if (!this.inGroup(userName, groupName)) {
                Long id = this.getDelegator().getNextSeqId(this.membershipSequence);
                GenericValue v = this.getDelegator().makeValue(this.membershipEntity, UtilMisc.toMap((String)"id", (Object)id, (String)"userName", (Object)userName, (String)"groupName", (Object)groupName));
                v.create();
                if (this.exclusiveAccess) {
                    this.userGroupsCache.remove(userName);
                    this.groupUsersCache.remove(groupName);
                }
            }
            return true;
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not add user to group", (Throwable)e);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean create(String name) {
        block5: {
            try {
                Long id = this.getDelegator().getNextSeqId(this.groupSequence);
                GenericValue v = this.getDelegator().makeValue(this.groupEntity, UtilMisc.toMap((String)"id", (Object)id, (String)"name", (Object)name));
                v.create();
                if (!this.exclusiveAccess) break block5;
                Object object = this.groupsCacheLock;
                synchronized (object) {
                    this.groupsCache.add(name);
                    Collections.sort(this.groupsCache);
                }
            }
            catch (GenericEntityException e) {
                LOG.error((Object)"Could not create group", (Throwable)e);
                return false;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flushCaches() {
        super.flushCaches();
        if (this.exclusiveAccess) {
            Object object = this.groupsCacheLock;
            synchronized (object) {
                this.groupsCache = null;
            }
            this.userGroupsCache = Collections.synchronizedMap(new HashMap());
            this.groupUsersCache = Collections.synchronizedMap(new HashMap());
        }
    }

    public boolean handles(String name) {
        try {
            if (this.list().contains(name)) {
                return true;
            }
            if (this.findUser(name) != null) {
                return true;
            }
        }
        catch (GenericEntityException e) {
            LOG.error((Object)("GenericEntityException: " + (Object)((Object)e)), (Throwable)e);
        }
        return false;
    }

    public boolean inGroup(String userName, String groupName) {
        if (this.exclusiveAccess) {
            return this.listUsersInGroup(groupName).contains(userName);
        }
        try {
            GenericValue v = this.findByUsernameAndGroup(userName, groupName);
            if (v != null) {
                return true;
            }
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not verify that user is in group", (Throwable)e);
            return false;
        }
        return false;
    }

    public boolean init(Properties properties) {
        boolean superResult = super.init(properties);
        this.groupEntity = properties.getProperty("groupEntity", "OSGroup");
        this.groupSequence = properties.getProperty("groupSequence", "OSGroup");
        this.membershipEntity = properties.getProperty("membershipEntity", "OSMembership");
        this.membershipSequence = properties.getProperty("membershipSequence", "OSMembership");
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("groupEntity: " + this.groupEntity));
            LOG.debug((Object)("groupSequence: " + this.groupSequence));
            LOG.debug((Object)("membershipEntity: " + this.membershipEntity));
            LOG.debug((Object)("membershipSequence: " + this.membershipSequence));
        }
        this.userGroupsCache = Collections.synchronizedMap(new HashMap());
        this.groupUsersCache = Collections.synchronizedMap(new HashMap());
        return superResult;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List list() {
        try {
            if (this.exclusiveAccess) {
                Object object = this.groupsCacheLock;
                synchronized (object) {
                    if (this.groupsCache != null) {
                        return this.groupsCache;
                    }
                }
            }
            List col = this.getDelegator().findAll(this.groupEntity, UtilMisc.toList((Object)"name ASC"));
            ArrayList<String> list = new ArrayList<String>();
            Iterator iterator = col.iterator();
            while (iterator.hasNext()) {
                GenericValue o = (GenericValue)iterator.next();
                list.add(o.getString("name"));
            }
            if (this.exclusiveAccess) {
                Object object = this.groupsCacheLock;
                synchronized (object) {
                    this.groupsCache = list;
                }
            }
            return list;
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not list groups", (Throwable)e);
            return null;
        }
    }

    public List listGroupsContainingUser(String userName) {
        Object cachedValue;
        if (this.exclusiveAccess && (cachedValue = this.userGroupsCache.get(userName)) != null) {
            return (List)cachedValue;
        }
        ArrayList<String> list = new ArrayList<String>();
        try {
            List c = this.getDelegator().findByAnd(this.membershipEntity, UtilMisc.toMap((String)"userName", (Object)userName));
            Iterator iterator = c.iterator();
            while (iterator.hasNext()) {
                GenericValue value = (GenericValue)iterator.next();
                list.add(value.getString("groupName"));
            }
            if (this.exclusiveAccess) {
                this.userGroupsCache.put(userName, list);
            }
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not list groups containing user", (Throwable)e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List listUsersInGroup(String groupName) {
        Object cachedValue;
        if (this.exclusiveAccess && (cachedValue = this.groupUsersCache.get(groupName)) != null) {
            return (List)cachedValue;
        }
        ArrayList<String> list = new ArrayList<String>();
        try {
            List c = this.getDelegator().findByAnd(this.membershipEntity, UtilMisc.toMap((String)"groupName", (Object)groupName));
            Iterator iterator = c.iterator();
            while (iterator.hasNext()) {
                GenericValue value = (GenericValue)iterator.next();
                list.add(value.getString("userName"));
            }
            if (this.exclusiveAccess) {
                this.groupUsersCache.put(groupName, list);
            }
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not list users in group", (Throwable)e);
        }
        return list;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(String name) {
        try {
            this.getDelegator().removeByAnd(this.groupEntity, UtilMisc.toMap((String)"name", (Object)name));
            if (this.exclusiveAccess) {
                Object object = this.groupsCacheLock;
                synchronized (object) {
                    this.groupsCache.remove(name);
                }
            }
            return true;
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not remove group", (Throwable)e);
            return false;
        }
    }

    public boolean removeFromGroup(String userName, String groupName) {
        try {
            this.getDelegator().removeByAnd(this.membershipEntity, UtilMisc.toMap((String)"userName", (Object)userName, (String)"groupName", (Object)groupName));
            if (this.exclusiveAccess) {
                this.userGroupsCache.remove(userName);
                this.groupUsersCache.remove(groupName);
            }
            return true;
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not remove group", (Throwable)e);
            return false;
        }
    }

    private GenericValue findByUsernameAndGroup(String userName, String groupName) throws GenericEntityException {
        List c = this.getDelegator().findByAnd(this.membershipEntity, UtilMisc.toMap((String)"userName", (Object)userName, (String)"groupName", (Object)groupName));
        if (c.size() > 0) {
            return (GenericValue)c.iterator().next();
        }
        return null;
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


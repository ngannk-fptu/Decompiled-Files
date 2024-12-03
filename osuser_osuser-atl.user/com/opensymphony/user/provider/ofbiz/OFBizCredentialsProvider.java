/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.log4j.Category
 *  org.ofbiz.core.entity.GenericEntityException
 *  org.ofbiz.core.entity.GenericTransactionException
 *  org.ofbiz.core.entity.GenericValue
 *  org.ofbiz.core.entity.TransactionUtil
 *  org.ofbiz.core.util.UtilMisc
 */
package com.opensymphony.user.provider.ofbiz;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.Entity;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ejb.util.Base64;
import com.opensymphony.user.provider.ejb.util.PasswordDigester;
import com.opensymphony.user.provider.ofbiz.OFBizAbstractProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Category;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericTransactionException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.TransactionUtil;
import org.ofbiz.core.util.UtilMisc;

public class OFBizCredentialsProvider
extends OFBizAbstractProvider
implements CredentialsProvider {
    private static final Category LOG = Category.getInstance((Class)(class$com$opensymphony$user$provider$ofbiz$OFBizCredentialsProvider == null ? (class$com$opensymphony$user$provider$ofbiz$OFBizCredentialsProvider = OFBizCredentialsProvider.class$("com.opensymphony.user.provider.ofbiz.OFBizCredentialsProvider")) : class$com$opensymphony$user$provider$ofbiz$OFBizCredentialsProvider));
    protected String userSequence;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ofbiz$OFBizCredentialsProvider;

    public boolean authenticate(String name, String password) {
        try {
            GenericValue v = this.findUser(name);
            if (v == null) {
                return false;
            }
            String passwordHash = v.getString("passwordHash");
            if (password == null || passwordHash == null || password.length() == 0) {
                return false;
            }
            return this.compareHash(passwordHash, password);
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not authenticate user", (Throwable)e);
            return false;
        }
    }

    public boolean changePassword(String name, String password) {
        try {
            GenericValue v = this.findUser(name);
            if (v == null) {
                return false;
            }
            this.clearUserCache(name);
            this.clearAllCache();
            v.set("passwordHash", (Object)this.createHash(password));
            v.store();
            return true;
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not change password", (Throwable)e);
            return false;
        }
    }

    public boolean create(String name) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Creating user: " + name));
            }
            Long id = this.getDelegator().getNextSeqId(this.userSequence);
            GenericValue v = this.getDelegator().makeValue(this.userEntity, UtilMisc.toMap((String)"name", (Object)name, (String)"id", (Object)id));
            this.clearAllCache();
            v.create();
        }
        catch (GenericEntityException e) {
            LOG.error((Object)("GenericEntityException creating user : " + name + " : " + (Object)((Object)e)), (Throwable)e);
            return false;
        }
        return true;
    }

    public boolean handles(String name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("OFBizCredentialsProvider.handles(" + name + ")"));
        }
        try {
            if (this.findUser(name) != null) {
                return true;
            }
        }
        catch (GenericEntityException e) {
            LOG.warn((Object)"did not handle user", (Throwable)e);
        }
        return false;
    }

    public boolean init(Properties properties) {
        boolean superResult = super.init(properties);
        this.userSequence = properties.getProperty("userSequence", "OSUser");
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("userSequence: " + this.userSequence));
        }
        return superResult;
    }

    public List list() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            ArrayList<String> order = new ArrayList<String>();
            order.add("name asc");
            List col = null;
            col = this.exclusiveAccess ? this.getDelegator().findAllCache(this.userEntity, order) : this.getDelegator().findAll(this.userEntity, order);
            Iterator iterator = col.iterator();
            while (iterator.hasNext()) {
                GenericValue o = (GenericValue)iterator.next();
                list.add(o.getString("name"));
            }
        }
        catch (GenericEntityException e) {
            LOG.error((Object)"Could not list users", (Throwable)e);
        }
        return list;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("name = " + name));
        }
        try {
            GenericValue user = this.findUser(name);
            accessor.setName(user.getString("name"));
            accessor.setMutable(true);
            return true;
        }
        catch (GenericEntityException e) {
            return false;
        }
    }

    public boolean remove(String name) {
        try {
            List userGroups;
            TransactionUtil.begin();
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("trying to remove properties for: " + name));
            }
            User user = UserManager.getInstance().getUser(name);
            PropertySet props = user.getPropertySet();
            Iterator keyIter = props.getKeys().iterator();
            while (keyIter.hasNext()) {
                props.remove((String)keyIter.next());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("trying to remove groups for: " + name));
            }
            if ((userGroups = user.getGroups()) != null) {
                Iterator iterator = userGroups.iterator();
                while (iterator.hasNext()) {
                    user.getAccessProvider().removeFromGroup(name, (String)iterator.next());
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("trying to remove user: " + name));
            }
            this.clearUserCache(name);
            this.clearAllCache();
            this.getDelegator().removeByAnd(this.userEntity, UtilMisc.toMap((String)"name", (Object)name));
            if (TransactionUtil.getStatus() != 6) {
                TransactionUtil.commit();
            }
            return true;
        }
        catch (GenericEntityException e) {
            LOG.error((Object)("Could not remove user: " + name), (Throwable)e);
            try {
                TransactionUtil.rollback();
            }
            catch (GenericTransactionException e1) {
                LOG.error((Object)("Could not remove user: " + name), (Throwable)e1);
                return false;
            }
            return false;
        }
        catch (EntityNotFoundException e) {
            LOG.error((Object)("Could not remove user: " + name), (Throwable)e);
            try {
                TransactionUtil.rollback();
            }
            catch (GenericTransactionException e1) {
                LOG.error((Object)("Could not remove user: " + name), (Throwable)e1);
                return false;
            }
            return false;
        }
    }

    private boolean compareHash(String hashedValue, String unhashedValue) {
        return hashedValue.equals(this.createHash(unhashedValue));
    }

    private String createHash(String original) {
        byte[] digested = PasswordDigester.digest(original.getBytes());
        byte[] encoded = Base64.encode(digested);
        return new String(encoded);
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


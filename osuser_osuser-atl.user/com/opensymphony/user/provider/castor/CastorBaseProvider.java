/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.exolab.castor.jdo.Database
 *  org.exolab.castor.jdo.OQLQuery
 *  org.exolab.castor.jdo.PersistenceException
 *  org.exolab.castor.jdo.QueryResults
 */
package com.opensymphony.user.provider.castor;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.castor.CastorDataProvider;
import com.opensymphony.user.provider.castor.entity.CastorGroup;
import com.opensymphony.user.provider.castor.entity.CastorUser;
import java.util.Properties;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryResults;

public class CastorBaseProvider {
    protected static final String GROUP_CLASS = "com.opensymphony.user.provider.castor.entity.CastorGroup";
    protected static final String USER_CLASS = "com.opensymphony.user.provider.castor.entity.CastorUser";
    protected static final String groupQueryString = "SELECT distinct g FROM com.opensymphony.user.provider.castor.entity.CastorGroup g WHERE name = $1";
    protected static final String userQueryString = "SELECT distinct u FROM com.opensymphony.user.provider.castor.entity.CastorUser u WHERE name = $1";
    public static final String DatabaseFile = "/META-INF/database.xml";
    public static final String MappingFile = "/META-INF/mapping.xml";
    protected CastorDataProvider _dataProvider = null;

    public void flushCaches() {
    }

    public boolean init(Properties properties) {
        if (this._dataProvider == null) {
            this._dataProvider = CastorDataProvider.getInstance(properties);
        }
        return true;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }

    protected CastorGroup queryGroupsByNameKey(Database db, String name) throws PersistenceException {
        OQLQuery groupOql = null;
        groupOql = db.getOQLQuery(groupQueryString);
        groupOql.bind((Object)name);
        QueryResults result = groupOql.execute();
        if (!result.hasMore()) {
            result.close();
            return null;
        }
        CastorGroup ret = (CastorGroup)result.next();
        result.close();
        return ret;
    }

    protected CastorGroup queryGroupsByNameKey(String name) throws PersistenceException {
        Database db = this._dataProvider.getDatabase();
        db.begin();
        CastorGroup group = this.queryGroupsByNameKey(db, name);
        db.commit();
        db.close();
        return group;
    }

    protected CastorUser queryUsersByNameKey(Database db, String name) throws PersistenceException {
        OQLQuery userOql = null;
        userOql = db.getOQLQuery(userQueryString);
        userOql.bind((Object)name);
        QueryResults result = userOql.execute();
        if (!result.hasMore()) {
            result.close();
            return null;
        }
        CastorUser ret = (CastorUser)result.next();
        result.close();
        return ret;
    }

    protected CastorUser queryUsersByNameKey(String name) throws PersistenceException {
        Database db = this._dataProvider.getDatabase();
        db.begin();
        CastorUser user = this.queryUsersByNameKey(db, name);
        db.commit();
        db.close();
        return user;
    }
}


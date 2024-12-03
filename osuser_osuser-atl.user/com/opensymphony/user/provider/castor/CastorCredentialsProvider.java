/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.exolab.castor.jdo.Database
 *  org.exolab.castor.jdo.OQLQuery
 *  org.exolab.castor.jdo.PersistenceException
 *  org.exolab.castor.jdo.QueryException
 *  org.exolab.castor.jdo.QueryResults
 *  org.exolab.castor.jdo.TransactionAbortedException
 */
package com.opensymphony.user.provider.castor;

import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.castor.CastorBaseProvider;
import com.opensymphony.user.provider.castor.entity.CastorUser;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.TransactionAbortedException;

public class CastorCredentialsProvider
extends CastorBaseProvider
implements CredentialsProvider {
    public boolean authenticate(String name, String password) {
        boolean returnVal = false;
        Database db = null;
        CastorUser user = null;
        OQLQuery userOql = null;
        QueryResults results = null;
        try {
            db = this._dataProvider.getDatabase();
            db.begin();
            userOql = db.getOQLQuery("SELECT distinct u FROM com.opensymphony.user.provider.castor.entity.CastorUser u WHERE name = $1");
            userOql.bind((Object)name);
            results = userOql.execute();
            if (results.hasMore()) {
                user = (CastorUser)results.next();
            }
            if (user != null) {
                returnVal = user.authenticate(password.trim());
            }
            db.commit();
            db.close();
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            returnVal = false;
        }
        return returnVal;
    }

    public boolean changePassword(String name, String password) {
        boolean returnVal = true;
        Database db = null;
        CastorUser user = null;
        try {
            db = this._dataProvider.getDatabase();
            db.begin();
            user = this.queryUsersByNameKey(db, name);
            if (user != null) {
                user.setPassword(password);
            } else {
                returnVal = false;
            }
            db.commit();
            db.close();
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            returnVal = false;
        }
        return returnVal;
    }

    public boolean create(String name) {
        boolean returnVal = true;
        Database db = null;
        CastorUser user = new CastorUser();
        CastorUser checkUser = null;
        try {
            checkUser = this.queryUsersByNameKey(name);
        }
        catch (PersistenceException e) {
            e.printStackTrace();
        }
        if (checkUser == null) {
            try {
                db = this._dataProvider.getDatabase();
                db.begin();
                user.setName(name);
                db.create((Object)user);
                db.commit();
                db.close();
            }
            catch (PersistenceException e) {
                e.printStackTrace();
                returnVal = false;
            }
        } else {
            returnVal = false;
        }
        return returnVal;
    }

    public boolean handles(String name) {
        try {
            if (name != null && this.queryUsersByNameKey(name) != null) {
                return true;
            }
        }
        catch (PersistenceException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List list() {
        ArrayList<String> users = new ArrayList<String>();
        OQLQuery userOql = null;
        Database db = null;
        QueryResults results = null;
        try {
            db = this._dataProvider.getDatabase();
        }
        catch (PersistenceException e) {
            e.printStackTrace();
        }
        if (db != null) {
            try {
                db.begin();
            }
            catch (PersistenceException e) {
                e.printStackTrace();
            }
            try {
                try {
                    userOql = db.getOQLQuery("SELECT distinct u FROM com.opensymphony.user.provider.castor.entity.CastorUser u");
                    results = userOql.execute();
                }
                catch (QueryException e) {
                    e.printStackTrace();
                }
                catch (PersistenceException e) {
                    e.printStackTrace();
                }
                try {
                    while (results.hasMore()) {
                        users.add(((CastorUser)results.next()).getName());
                    }
                }
                catch (PersistenceException e) {
                    e.printStackTrace();
                }
                catch (NoSuchElementException e) {
                    e.printStackTrace();
                }
                Object var7_12 = null;
            }
            catch (Throwable throwable) {
                Object var7_13 = null;
                try {
                    db.commit();
                    db.close();
                }
                catch (TransactionAbortedException e) {
                    e.printStackTrace();
                }
                catch (PersistenceException e) {
                    e.printStackTrace();
                }
                throw throwable;
            }
            try {
                db.commit();
                db.close();
            }
            catch (TransactionAbortedException e) {
                e.printStackTrace();
            }
            catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public boolean remove(String name) {
        boolean returnVal = true;
        Database db = null;
        Object userOql = null;
        Object results = null;
        try {
            db = this._dataProvider.getDatabase();
            db.begin();
            CastorUser user = this.queryUsersByNameKey(db, name);
            if (user != null) {
                db.remove((Object)user);
            } else {
                returnVal = false;
            }
            db.commit();
            db.close();
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            returnVal = false;
        }
        return returnVal;
    }
}


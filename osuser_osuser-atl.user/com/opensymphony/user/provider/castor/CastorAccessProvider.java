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

import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.castor.CastorBaseProvider;
import com.opensymphony.user.provider.castor.entity.CastorGroup;
import com.opensymphony.user.provider.castor.entity.CastorUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.TransactionAbortedException;

public class CastorAccessProvider
extends CastorBaseProvider
implements AccessProvider {
    public boolean addToGroup(String username, String groupname) {
        try {
            Database db = this._dataProvider.getDatabase();
            db.begin();
            CastorGroup group = this.queryGroupsByNameKey(db, groupname);
            CastorUser user = this.queryUsersByNameKey(db, username);
            group.addUser(user);
            db.commit();
            db.close();
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean create(String name) {
        boolean returnVal = true;
        Database db = null;
        CastorGroup group = new CastorGroup();
        CastorGroup checkGroup = null;
        try {
            checkGroup = this.queryGroupsByNameKey(name);
        }
        catch (PersistenceException e) {
            e.printStackTrace();
        }
        if (checkGroup == null) {
            try {
                db = this._dataProvider.getDatabase();
                db.begin();
                group.setName(name);
                db.create((Object)group);
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
        CastorGroup group = null;
        Object user = null;
        try {
            if (name != null) {
                group = this.queryGroupsByNameKey(name);
                if (name != null && group != null) {
                    return true;
                }
                return this.queryUsersByNameKey(name) != null;
            }
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean inGroup(String username, String groupname) {
        CastorGroup group = null;
        try {
            group = this.queryGroupsByNameKey(groupname);
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        }
        if (group != null && group.getUsers() != null) {
            Iterator userIter = group.getUsers().iterator();
            while (userIter.hasNext()) {
                if (!((CastorUser)userIter.next()).getName().equals(username)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List list() {
        ArrayList<String> groups = new ArrayList<String>();
        OQLQuery groupOql = null;
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
                    groupOql = db.getOQLQuery("SELECT distinct g FROM com.opensymphony.user.provider.castor.entity.CastorGroup g");
                    results = groupOql.execute();
                }
                catch (QueryException e) {
                    e.printStackTrace();
                }
                catch (PersistenceException e) {
                    e.printStackTrace();
                }
                try {
                    while (results.hasMore()) {
                        groups.add(((CastorGroup)results.next()).getName());
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
        return groups;
    }

    public List listGroupsContainingUser(String username) {
        ArrayList<String> result = new ArrayList<String>();
        Iterator i = this.list().iterator();
        while (i.hasNext()) {
            String currentGroup = (String)i.next();
            if (!this.inGroup(username, currentGroup)) continue;
            result.add(currentGroup);
        }
        return Collections.unmodifiableList(result);
    }

    public List listUsersInGroup(String groupname) {
        CastorGroup group = null;
        try {
            group = this.queryGroupsByNameKey(groupname);
        }
        catch (PersistenceException e) {
            e.printStackTrace();
        }
        ArrayList<String> returnList = new ArrayList<String>();
        ArrayList users = group.getUsers();
        if (users != null) {
            Iterator userIter = users.iterator();
            while (userIter.hasNext()) {
                returnList.add(((CastorUser)userIter.next()).getName());
            }
        }
        return returnList;
    }

    public boolean remove(String name) {
        boolean returnVal = true;
        Database db = null;
        try {
            db = this._dataProvider.getDatabase();
            db.begin();
            CastorGroup group = this.queryGroupsByNameKey(db, name);
            if (group != null) {
                db.remove((Object)group);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeFromGroup(String username, String groupname) {
        Database db = null;
        CastorGroup returnGroup = null;
        try {
            db = this._dataProvider.getDatabase();
        }
        catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        }
        if (db != null) {
            try {
                db.begin();
            }
            catch (PersistenceException e) {
                e.printStackTrace();
                return false;
            }
            try {
                try {
                    returnGroup = this.queryGroupsByNameKey(db, groupname);
                    returnGroup.removeUser(username);
                }
                catch (PersistenceException e) {
                    e.printStackTrace();
                    boolean bl = false;
                    Object var8_9 = null;
                    try {
                        db.commit();
                        db.close();
                    }
                    catch (TransactionAbortedException e2) {
                        e2.printStackTrace();
                        return false;
                    }
                    catch (PersistenceException e3) {
                        e3.printStackTrace();
                        return false;
                    }
                    return bl;
                }
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    db.commit();
                    db.close();
                }
                catch (TransactionAbortedException e2) {
                    e2.printStackTrace();
                    return false;
                }
                catch (PersistenceException e3) {
                    e3.printStackTrace();
                    return false;
                }
                throw throwable;
            }
            Object var8_10 = null;
            try {
                db.commit();
                db.close();
            }
            catch (TransactionAbortedException e2) {
                e2.printStackTrace();
                return false;
            }
            catch (PersistenceException e3) {
                e3.printStackTrace();
                return false;
            }
        }
        return true;
    }
}


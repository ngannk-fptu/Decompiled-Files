/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 */
package com.opensymphony.module.propertyset.hibernate;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAOUtils;
import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

public class HibernatePropertySetDAOImpl
implements HibernatePropertySetDAO {
    private SessionFactory sessionFactory;

    public HibernatePropertySetDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void setImpl(PropertySetItem item, boolean isUpdate) {
        Session session = null;
        try {
            try {
                session = this.sessionFactory.openSession();
                if (isUpdate) {
                    session.update((Object)item);
                } else {
                    session.save((Object)item);
                }
                session.flush();
            }
            catch (HibernateException he) {
                throw new PropertyException("Could not save key '" + item.getKey() + "':" + he.getMessage());
            }
            Object var6_4 = null;
        }
        catch (Throwable throwable) {
            Object var6_5 = null;
            try {
                if (session == null) throw throwable;
                if (!session.connection().getAutoCommit()) {
                    session.connection().commit();
                }
                session.close();
                throw throwable;
            }
            catch (Exception e) {
                // empty catch block
            }
            throw throwable;
        }
        try {}
        catch (Exception e) {}
        if (session == null) return;
        if (!session.connection().getAutoCommit()) {
            session.connection().commit();
        }
        session.close();
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Collection getKeys(String entityName, Long entityId, String prefix, int type) {
        List list;
        block11: {
            Session session = null;
            list = null;
            session = this.sessionFactory.openSession();
            list = HibernatePropertySetDAOUtils.getKeysImpl(session, entityName, entityId, prefix, type);
            Object var9_7 = null;
            try {
                if (session != null) {
                    session.flush();
                    session.close();
                }
                break block11;
            }
            catch (Exception e2) {}
            break block11;
            {
                catch (HibernateException e) {
                    list = Collections.EMPTY_LIST;
                    Object var9_8 = null;
                    try {
                        if (session != null) {
                            session.flush();
                            session.close();
                        }
                        break block11;
                    }
                    catch (Exception e2) {}
                }
            }
            catch (Throwable throwable) {
                Object var9_9 = null;
                try {
                    if (session != null) {
                        session.flush();
                        session.close();
                    }
                }
                catch (Exception e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public PropertySetItem findByKey(String entityName, Long entityId, String key) {
        PropertySetItem item;
        block11: {
            Session session = null;
            item = null;
            session = this.sessionFactory.openSession();
            item = HibernatePropertySetDAOUtils.getItem(session, entityName, entityId, key);
            session.flush();
            Object var9_6 = null;
            try {
                if (session != null) {
                    session.close();
                }
                break block11;
            }
            catch (Exception e2) {}
            break block11;
            {
                catch (HibernateException e) {
                    PropertySetItem propertySetItem = null;
                    Object var9_7 = null;
                    try {
                        if (session != null) {
                            session.close();
                        }
                    }
                    catch (Exception e2) {
                        // empty catch block
                    }
                    return propertySetItem;
                }
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
                try {
                    if (session != null) {
                        session.close();
                    }
                }
                catch (Exception e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        return item;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void remove(String entityName, Long entityId, String key) {
        Session session = null;
        try {
            try {
                session = this.sessionFactory.openSession();
                session.delete((Object)HibernatePropertySetDAOUtils.getItem(session, entityName, entityId, key));
                session.flush();
            }
            catch (HibernateException e) {
                throw new PropertyException("Could not remove key '" + key + "': " + e.getMessage());
            }
            Object var7_5 = null;
        }
        catch (Throwable throwable) {
            Object var7_6 = null;
            try {
                if (session == null) throw throwable;
                if (!session.connection().getAutoCommit()) {
                    session.connection().commit();
                }
                session.close();
                throw throwable;
            }
            catch (Exception e) {
                // empty catch block
            }
            throw throwable;
        }
        try {}
        catch (Exception e) {}
        if (session == null) return;
        if (!session.connection().getAutoCommit()) {
            session.connection().commit();
        }
        session.close();
        return;
    }
}


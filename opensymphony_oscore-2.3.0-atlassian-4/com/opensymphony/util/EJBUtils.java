/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBHome
 *  javax.ejb.EJBObject
 *  javax.ejb.FinderException
 *  javax.rmi.PortableRemoteObject
 */
package com.opensymphony.util;

import com.opensymphony.util.OrderedMap;
import com.opensymphony.util.TextUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.ejb.FinderException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class EJBUtils {
    private static HashMap finderMethods = new HashMap();
    private static Set ignoreEnvLocations = new HashSet();

    public static final Context getRoot() throws NamingException, RemoteException {
        return new InitialContext();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final EJBObject findEntity(EJBHome home, String id) throws RemoteException, FinderException {
        try {
            Class<?> homeClass = home.getClass();
            OrderedMap params = new OrderedMap();
            params.put(Integer.TYPE, new Integer(TextUtils.parseInt(id)));
            params.put(Long.TYPE, new Long(TextUtils.parseLong(id)));
            params.put("java.lang.Integer", new Integer(TextUtils.parseInt(id)));
            params.put("java.lang.Long", new Long(TextUtils.parseLong(id)));
            params.put("java.lang.String", id);
            Iterator it = params.iterator();
            while (it.hasNext()) {
                Object classType = it.next();
                try {
                    Object[] args;
                    EJBObject result;
                    Method m = null;
                    if (!finderMethods.containsKey(homeClass)) {
                        Class[] theClass = new Class[]{classType instanceof String ? Class.forName((String)classType) : (Class<?>)classType};
                        m = homeClass.getMethod("findByPrimaryKey", theClass);
                        finderMethods.put(homeClass, m);
                    } else {
                        m = (Method)finderMethods.get(homeClass);
                    }
                    if ((result = (EJBObject)m.invoke((Object)home, args = new Object[]{params.get(classType)})) == null) continue;
                    return result;
                }
                catch (ClassCastException m) {
                }
                catch (ClassNotFoundException m) {
                }
                catch (NoSuchMethodException m) {
                }
                catch (IllegalAccessException m) {
                }
                catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    if (t instanceof RemoteException) {
                        throw (RemoteException)t;
                    }
                    if (!(t instanceof FinderException)) continue;
                    throw (FinderException)t;
                    return null;
                }
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return null;
    }

    public static final EJBObject findEntity(EJBHome home, int id) throws RemoteException, FinderException {
        return EJBUtils.findEntity(home, "" + id);
    }

    public static final EJBObject findEntity(EJBHome home, long id) throws RemoteException, FinderException {
        return EJBUtils.findEntity(home, "" + id);
    }

    public static final Object narrow(Object o, Class classType) {
        return PortableRemoteObject.narrow((Object)o, (Class)classType);
    }
}


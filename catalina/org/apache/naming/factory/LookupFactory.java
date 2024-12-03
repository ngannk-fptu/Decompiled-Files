/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.naming.factory;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.LookupRef;
import org.apache.naming.StringManager;

public class LookupFactory
implements ObjectFactory {
    private static final Log log = LogFactory.getLog(LookupFactory.class);
    private static final StringManager sm = StringManager.getManager(LookupFactory.class);
    private static final ThreadLocal<Set<String>> names = ThreadLocal.withInitial(HashSet::new);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Object result;
        block22: {
            String lookupName = null;
            result = null;
            if (obj instanceof LookupRef) {
                Reference ref = (Reference)obj;
                ObjectFactory factory = null;
                RefAddr lookupNameRefAddr = ref.get("lookup-name");
                if (lookupNameRefAddr != null) {
                    lookupName = lookupNameRefAddr.getContent().toString();
                }
                try {
                    if (lookupName != null && !names.get().add(lookupName)) {
                        String msg = sm.getString("lookupFactory.circularReference", lookupName);
                        NamingException ne = new NamingException(msg);
                        log.warn((Object)msg, (Throwable)ne);
                        throw ne;
                    }
                    RefAddr factoryRefAddr = ref.get("factory");
                    if (factoryRefAddr != null) {
                        String factoryClassName = factoryRefAddr.getContent().toString();
                        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                        Class<?> factoryClass = null;
                        if (tcl != null) {
                            try {
                                factoryClass = tcl.loadClass(factoryClassName);
                            }
                            catch (ClassNotFoundException e) {
                                NamingException ex = new NamingException(sm.getString("lookupFactory.loadFailed"));
                                ex.initCause(e);
                                throw ex;
                            }
                        }
                        try {
                            factoryClass = Class.forName(factoryClassName);
                        }
                        catch (ClassNotFoundException e) {
                            NamingException ex = new NamingException(sm.getString("lookupFactory.loadFailed"));
                            ex.initCause(e);
                            throw ex;
                        }
                        if (factoryClass != null) {
                            try {
                                factory = (ObjectFactory)factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                            }
                            catch (Throwable t) {
                                if (t instanceof NamingException) {
                                    throw (NamingException)t;
                                }
                                NamingException ex = new NamingException(sm.getString("lookupFactory.createFailed"));
                                ex.initCause(t);
                                throw ex;
                            }
                        }
                    }
                    if (factory != null) {
                        result = factory.getObjectInstance(obj, name, nameCtx, environment);
                    } else {
                        if (lookupName == null) {
                            throw new NamingException(sm.getString("lookupFactory.createFailed"));
                        }
                        result = new InitialContext().lookup(lookupName);
                    }
                    Class<?> clazz = Class.forName(ref.getClassName());
                    if (result == null || clazz.isAssignableFrom(result.getClass())) break block22;
                    String msg = sm.getString("lookupFactory.typeMismatch", name, ref.getClassName(), lookupName, result.getClass().getName());
                    NamingException ne = new NamingException(msg);
                    log.warn((Object)msg, (Throwable)ne);
                    if (result instanceof AutoCloseable) {
                        try {
                            ((AutoCloseable)result).close();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    throw ne;
                }
                finally {
                    names.get().remove(lookupName);
                }
            }
        }
        return result;
    }
}


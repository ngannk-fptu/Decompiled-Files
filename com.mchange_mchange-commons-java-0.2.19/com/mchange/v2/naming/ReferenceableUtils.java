/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.naming;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

public final class ReferenceableUtils {
    static final MLogger logger = MLog.getLogger(ReferenceableUtils.class);
    static final String REFADDR_VERSION = "version";
    static final String REFADDR_CLASSNAME = "classname";
    static final String REFADDR_FACTORY = "factory";
    static final String REFADDR_FACTORY_CLASS_LOCATION = "factoryClassLocation";
    static final String REFADDR_SIZE = "size";
    static final int CURRENT_REF_VERSION = 1;

    public static String literalNullToNull(String string) {
        if (string == null || "null".equals(string)) {
            return null;
        }
        return string;
    }

    public static Object referenceToObject(Reference reference, Name name, Context context, Hashtable hashtable) throws NamingException {
        try {
            Serializable serializable;
            ClassLoader classLoader;
            String string = reference.getFactoryClassName();
            String string2 = reference.getFactoryClassLocation();
            ClassLoader classLoader2 = Thread.currentThread().getContextClassLoader();
            if (classLoader2 == null) {
                classLoader2 = ReferenceableUtils.class.getClassLoader();
            }
            if (string2 == null) {
                classLoader = classLoader2;
            } else {
                serializable = new URL(string2);
                classLoader = new URLClassLoader(new URL[]{serializable}, classLoader2);
            }
            serializable = Class.forName(string, true, classLoader);
            ObjectFactory objectFactory = (ObjectFactory)((Class)serializable).newInstance();
            return objectFactory.getObjectInstance(reference, name, context, hashtable);
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not resolve Reference to Object!", exception);
            }
            NamingException namingException = new NamingException("Could not resolve Reference to Object!");
            namingException.setRootCause(exception);
            throw namingException;
        }
    }

    public static void appendToReference(Reference reference, Reference reference2) throws NamingException {
        int n = reference2.size();
        reference.add(new StringRefAddr(REFADDR_VERSION, String.valueOf(1)));
        reference.add(new StringRefAddr(REFADDR_CLASSNAME, reference2.getClassName()));
        reference.add(new StringRefAddr(REFADDR_FACTORY, reference2.getFactoryClassName()));
        reference.add(new StringRefAddr(REFADDR_FACTORY_CLASS_LOCATION, reference2.getFactoryClassLocation()));
        reference.add(new StringRefAddr(REFADDR_SIZE, String.valueOf(n)));
        for (int i = 0; i < n; ++i) {
            reference.add(reference2.get(i));
        }
    }

    public static ExtractRec extractNestedReference(Reference reference, int n) throws NamingException {
        try {
            int n2 = Integer.parseInt((String)reference.get(n++).getContent());
            if (n2 == 1) {
                String string = (String)reference.get(n++).getContent();
                String string2 = (String)reference.get(n++).getContent();
                String string3 = (String)reference.get(n++).getContent();
                Reference reference2 = new Reference(string, string2, string3);
                int n3 = Integer.parseInt((String)reference.get(n++).getContent());
                for (int i = 0; i < n3; ++i) {
                    reference2.add(reference.get(n++));
                }
                return new ExtractRec(reference2, n);
            }
            throw new NamingException("Bad version of nested reference!!!");
        }
        catch (NumberFormatException numberFormatException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Version or size nested reference was not a number!!!", numberFormatException);
            }
            throw new NamingException("Version or size nested reference was not a number!!!");
        }
    }

    private ReferenceableUtils() {
    }

    public static class ExtractRec {
        public Reference ref;
        public int index;

        private ExtractRec(Reference reference, int n) {
            this.ref = reference;
            this.index = n;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming.java;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ContextBindings;
import org.apache.naming.NamingContext;
import org.apache.naming.SelectorContext;

public class javaURLContextFactory
implements ObjectFactory,
InitialContextFactory {
    public static final String MAIN = "initialContext";
    protected static volatile Context initialContext = null;

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        if (ContextBindings.isThreadBound() || ContextBindings.isClassLoaderBound()) {
            return new SelectorContext(environment);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        if (ContextBindings.isThreadBound() || ContextBindings.isClassLoaderBound()) {
            return new SelectorContext(environment, true);
        }
        if (initialContext != null) return initialContext;
        Class<javaURLContextFactory> clazz = javaURLContextFactory.class;
        synchronized (javaURLContextFactory.class) {
            if (initialContext != null) return initialContext;
            initialContext = new NamingContext(environment, MAIN);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return initialContext;
        }
    }
}


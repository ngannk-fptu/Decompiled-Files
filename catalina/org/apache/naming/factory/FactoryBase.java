/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming.factory;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.StringManager;

public abstract class FactoryBase
implements ObjectFactory {
    private static final StringManager sm = StringManager.getManager(FactoryBase.class);

    @Override
    public final Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (this.isReferenceTypeSupported(obj)) {
            Reference ref = (Reference)obj;
            Object linked = this.getLinked(ref);
            if (linked != null) {
                return linked;
            }
            ObjectFactory factory = null;
            RefAddr factoryRefAddr = ref.get("factory");
            if (factoryRefAddr != null) {
                String factoryClassName = factoryRefAddr.getContent().toString();
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                Class<?> factoryClass = null;
                try {
                    factoryClass = tcl != null ? tcl.loadClass(factoryClassName) : Class.forName(factoryClassName);
                }
                catch (ClassNotFoundException e) {
                    NamingException ex = new NamingException(sm.getString("factoryBase.factoryClassError"));
                    ex.initCause(e);
                    throw ex;
                }
                try {
                    factory = (ObjectFactory)factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Throwable t) {
                    if (t instanceof NamingException) {
                        throw (NamingException)t;
                    }
                    if (t instanceof ThreadDeath) {
                        throw (ThreadDeath)t;
                    }
                    if (t instanceof VirtualMachineError) {
                        throw (VirtualMachineError)t;
                    }
                    NamingException ex = new NamingException(sm.getString("factoryBase.factoryCreationError"));
                    ex.initCause(t);
                    throw ex;
                }
            }
            factory = this.getDefaultFactory(ref);
            if (factory != null) {
                return factory.getObjectInstance(obj, name, nameCtx, environment);
            }
            throw new NamingException(sm.getString("factoryBase.instanceCreationError"));
        }
        return null;
    }

    protected abstract boolean isReferenceTypeSupported(Object var1);

    protected abstract ObjectFactory getDefaultFactory(Reference var1) throws NamingException;

    protected abstract Object getLinked(Reference var1) throws NamingException;
}


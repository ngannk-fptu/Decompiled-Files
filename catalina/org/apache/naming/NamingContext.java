/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.naming;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextAccessController;
import org.apache.naming.NameParserImpl;
import org.apache.naming.NamingContextBindingsEnumeration;
import org.apache.naming.NamingContextEnumeration;
import org.apache.naming.NamingEntry;
import org.apache.naming.ResourceRef;
import org.apache.naming.StringManager;

public class NamingContext
implements Context {
    protected static final NameParser nameParser = new NameParserImpl();
    private static final Log log = LogFactory.getLog(NamingContext.class);
    protected final Hashtable<String, Object> env = new Hashtable();
    protected static final StringManager sm = StringManager.getManager(NamingContext.class);
    protected final HashMap<String, NamingEntry> bindings;
    protected final String name;
    private boolean exceptionOnFailedWrite = true;
    private static final boolean GRAAL;

    public NamingContext(Hashtable<String, Object> env, String name) {
        this(env, name, new HashMap<String, NamingEntry>());
    }

    public NamingContext(Hashtable<String, Object> env, String name, HashMap<String, NamingEntry> bindings) {
        this.name = name;
        if (env != null) {
            Enumeration<String> envEntries = env.keys();
            while (envEntries.hasMoreElements()) {
                String entryName = envEntries.nextElement();
                this.addToEnvironment(entryName, env.get(entryName));
            }
        }
        this.bindings = bindings;
    }

    public boolean getExceptionOnFailedWrite() {
        return this.exceptionOnFailedWrite;
    }

    public void setExceptionOnFailedWrite(boolean exceptionOnFailedWrite) {
        this.exceptionOnFailedWrite = exceptionOnFailedWrite;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        return this.lookup(name, true);
    }

    @Override
    public Object lookup(String name) throws NamingException {
        return this.lookup(new CompositeName(name), true);
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        this.bind(name, obj, false);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        this.bind(new CompositeName(name), obj);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        this.bind(name, obj, true);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        this.rebind(new CompositeName(name), obj);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void unbind(Name name) throws NamingException {
        if (!this.checkWritable()) {
            return;
        }
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            throw new NamingException(sm.getString("namingContext.invalidName"));
        }
        NamingEntry entry = this.bindings.get(name.get(0));
        if (entry == null) {
            throw new NameNotFoundException(sm.getString("namingContext.nameNotBound", name, name.get(0)));
        }
        if (name.size() > 1) {
            if (entry.type != 10) throw new NamingException(sm.getString("namingContext.contextExpected"));
            ((Context)entry.value).unbind(name.getSuffix(1));
            return;
        } else {
            this.bindings.remove(name.get(0));
        }
    }

    @Override
    public void unbind(String name) throws NamingException {
        this.unbind(new CompositeName(name));
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        Object value = this.lookup(oldName);
        this.bind(newName, value);
        this.unbind(oldName);
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        this.rename(new CompositeName(oldName), new CompositeName(newName));
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            return new NamingContextEnumeration(this.bindings.values().iterator());
        }
        NamingEntry entry = this.bindings.get(name.get(0));
        if (entry == null) {
            throw new NameNotFoundException(sm.getString("namingContext.nameNotBound", name, name.get(0)));
        }
        if (entry.type != 10) {
            throw new NamingException(sm.getString("namingContext.contextExpected"));
        }
        return ((Context)entry.value).list(name.getSuffix(1));
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return this.list(new CompositeName(name));
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            return new NamingContextBindingsEnumeration(this.bindings.values().iterator(), this);
        }
        NamingEntry entry = this.bindings.get(name.get(0));
        if (entry == null) {
            throw new NameNotFoundException(sm.getString("namingContext.nameNotBound", name, name.get(0)));
        }
        if (entry.type != 10) {
            throw new NamingException(sm.getString("namingContext.contextExpected"));
        }
        return ((Context)entry.value).listBindings(name.getSuffix(1));
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return this.listBindings(new CompositeName(name));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void destroySubcontext(Name name) throws NamingException {
        if (!this.checkWritable()) {
            return;
        }
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            throw new NamingException(sm.getString("namingContext.invalidName"));
        }
        NamingEntry entry = this.bindings.get(name.get(0));
        if (entry == null) {
            throw new NameNotFoundException(sm.getString("namingContext.nameNotBound", name, name.get(0)));
        }
        if (name.size() > 1) {
            if (entry.type != 10) throw new NamingException(sm.getString("namingContext.contextExpected"));
            ((Context)entry.value).destroySubcontext(name.getSuffix(1));
            return;
        } else {
            if (entry.type != 10) throw new NotContextException(sm.getString("namingContext.contextExpected"));
            ((Context)entry.value).close();
            this.bindings.remove(name.get(0));
        }
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        this.destroySubcontext(new CompositeName(name));
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        if (!this.checkWritable()) {
            return null;
        }
        NamingContext newContext = new NamingContext(this.env, this.name);
        this.bind(name, (Object)newContext);
        newContext.setExceptionOnFailedWrite(this.getExceptionOnFailedWrite());
        return newContext;
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        return this.createSubcontext(new CompositeName(name));
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        return this.lookup(name, false);
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        return this.lookup(new CompositeName(name), false);
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            return nameParser;
        }
        if (name.size() > 1) {
            NamingEntry obj = this.bindings.get(name.get(0));
            if (obj instanceof Context) {
                return ((Context)((Object)obj)).getNameParser(name.getSuffix(1));
            }
            throw new NotContextException(sm.getString("namingContext.contextExpected"));
        }
        return nameParser;
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        return this.getNameParser(new CompositeName(name));
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        prefix = (Name)prefix.clone();
        return prefix.addAll(name);
    }

    @Override
    public String composeName(String name, String prefix) {
        return prefix + "/" + name;
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) {
        return this.env.put(propName, propVal);
    }

    @Override
    public Object removeFromEnvironment(String propName) {
        return this.env.remove(propName);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() {
        return this.env;
    }

    @Override
    public void close() throws NamingException {
        if (!this.checkWritable()) {
            return;
        }
        this.env.clear();
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        throw new OperationNotSupportedException(sm.getString("namingContext.noAbsoluteName"));
    }

    protected Object lookup(Name name, boolean resolveLinks) throws NamingException {
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            return new NamingContext(this.env, this.name, this.bindings);
        }
        NamingEntry entry = this.bindings.get(name.get(0));
        if (entry == null) {
            throw new NameNotFoundException(sm.getString("namingContext.nameNotBound", name, name.get(0)));
        }
        if (name.size() > 1) {
            if (entry.type != 10) {
                throw new NamingException(sm.getString("namingContext.contextExpected"));
            }
            return ((Context)entry.value).lookup(name.getSuffix(1));
        }
        if (resolveLinks && entry.type == 1) {
            String link = ((LinkRef)entry.value).getLinkName();
            if (link.startsWith(".")) {
                return this.lookup(link.substring(1));
            }
            return new InitialContext(this.env).lookup(link);
        }
        if (entry.type == 2) {
            try {
                boolean singleton;
                Object obj = null;
                if (!GRAAL) {
                    obj = NamingManager.getObjectInstance(entry.value, name, this, this.env);
                } else {
                    Reference reference = (Reference)entry.value;
                    String factoryClassName = reference.getFactoryClassName();
                    if (factoryClassName != null) {
                        Class<?> factoryClass = this.getClass().getClassLoader().loadClass(factoryClassName);
                        ObjectFactory factory = (ObjectFactory)factoryClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                        obj = factory.getObjectInstance(entry.value, name, this, this.env);
                    }
                }
                if (entry.value instanceof ResourceRef && (singleton = Boolean.parseBoolean((String)((ResourceRef)entry.value).get("singleton").getContent()))) {
                    entry.type = 0;
                    entry.value = obj;
                }
                if (obj == null) {
                    throw new NamingException(sm.getString("namingContext.failResolvingReference"));
                }
                return obj;
            }
            catch (NamingException e) {
                throw e;
            }
            catch (Exception e) {
                String msg = sm.getString("namingContext.failResolvingReference");
                log.warn((Object)msg, (Throwable)e);
                NamingException ne = new NamingException(msg);
                ne.initCause(e);
                throw ne;
            }
        }
        return entry.value;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void bind(Name name, Object obj, boolean rebind) throws NamingException {
        if (!this.checkWritable()) {
            return;
        }
        while (!name.isEmpty() && name.get(0).length() == 0) {
            name = name.getSuffix(1);
        }
        if (name.isEmpty()) {
            throw new NamingException(sm.getString("namingContext.invalidName"));
        }
        NamingEntry entry = this.bindings.get(name.get(0));
        if (name.size() > 1) {
            if (entry == null) {
                throw new NameNotFoundException(sm.getString("namingContext.nameNotBound", name, name.get(0)));
            }
            if (entry.type != 10) throw new NamingException(sm.getString("namingContext.contextExpected"));
            if (rebind) {
                ((Context)entry.value).rebind(name.getSuffix(1), obj);
                return;
            } else {
                ((Context)entry.value).bind(name.getSuffix(1), obj);
            }
            return;
        } else {
            if (!rebind && entry != null) {
                throw new NameAlreadyBoundException(sm.getString("namingContext.alreadyBound", name.get(0)));
            }
            Object toBind = NamingManager.getStateToBind(obj, name, this, this.env);
            if (toBind instanceof Context) {
                entry = new NamingEntry(name.get(0), toBind, 10);
            } else if (toBind instanceof LinkRef) {
                entry = new NamingEntry(name.get(0), toBind, 1);
            } else if (toBind instanceof Reference) {
                entry = new NamingEntry(name.get(0), toBind, 2);
            } else if (toBind instanceof Referenceable) {
                toBind = ((Referenceable)toBind).getReference();
                entry = new NamingEntry(name.get(0), toBind, 2);
            } else {
                entry = new NamingEntry(name.get(0), toBind, 0);
            }
            this.bindings.put(name.get(0), entry);
        }
    }

    protected boolean isWritable() {
        return ContextAccessController.isWritable(this.name);
    }

    protected boolean checkWritable() throws NamingException {
        if (this.isWritable()) {
            return true;
        }
        if (this.exceptionOnFailedWrite) {
            throw new OperationNotSupportedException(sm.getString("namingContext.readOnly"));
        }
        return false;
    }

    static {
        boolean result = false;
        try {
            Class<?> nativeImageClazz = Class.forName("org.graalvm.nativeimage.ImageInfo");
            result = Boolean.TRUE.equals(nativeImageClazz.getMethod("inImageCode", new Class[0]).invoke(null, new Object[0]));
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (IllegalArgumentException | ReflectiveOperationException exception) {
            // empty catch block
        }
        GRAAL = result || System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }
}


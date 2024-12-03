/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.naming;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.apache.naming.NamingContext;
import org.apache.naming.StringManager;

public class SelectorContext
implements Context {
    public static final String prefix = "java:";
    public static final int prefixLength = "java:".length();
    public static final String IC_PREFIX = "IC_";
    private static final Log log = LogFactory.getLog(SelectorContext.class);
    protected final Hashtable<String, Object> env;
    protected static final StringManager sm = StringManager.getManager(SelectorContext.class);
    protected final boolean initialContext;

    public SelectorContext(Hashtable<String, Object> env) {
        this.env = env;
        this.initialContext = false;
    }

    public SelectorContext(Hashtable<String, Object> env, boolean initialContext) {
        this.env = env;
        this.initialContext = initialContext;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingName", "lookup", name));
        }
        return this.getBoundContext().lookup(this.parseName(name));
    }

    @Override
    public Object lookup(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingString", "lookup", name));
        }
        return this.getBoundContext().lookup(this.parseName(name));
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        this.getBoundContext().bind(this.parseName(name), obj);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        this.getBoundContext().bind(this.parseName(name), obj);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        this.getBoundContext().rebind(this.parseName(name), obj);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        this.getBoundContext().rebind(this.parseName(name), obj);
    }

    @Override
    public void unbind(Name name) throws NamingException {
        this.getBoundContext().unbind(this.parseName(name));
    }

    @Override
    public void unbind(String name) throws NamingException {
        this.getBoundContext().unbind(this.parseName(name));
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        this.getBoundContext().rename(this.parseName(oldName), this.parseName(newName));
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        this.getBoundContext().rename(this.parseName(oldName), this.parseName(newName));
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingName", "list", name));
        }
        return this.getBoundContext().list(this.parseName(name));
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingString", "list", name));
        }
        return this.getBoundContext().list(this.parseName(name));
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingName", "listBindings", name));
        }
        return this.getBoundContext().listBindings(this.parseName(name));
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingString", "listBindings", name));
        }
        return this.getBoundContext().listBindings(this.parseName(name));
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        this.getBoundContext().destroySubcontext(this.parseName(name));
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        this.getBoundContext().destroySubcontext(this.parseName(name));
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        return this.getBoundContext().createSubcontext(this.parseName(name));
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        return this.getBoundContext().createSubcontext(this.parseName(name));
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingName", "lookupLink", name));
        }
        return this.getBoundContext().lookupLink(this.parseName(name));
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("selectorContext.methodUsingString", "lookupLink", name));
        }
        return this.getBoundContext().lookupLink(this.parseName(name));
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        return this.getBoundContext().getNameParser(this.parseName(name));
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        return this.getBoundContext().getNameParser(this.parseName(name));
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        Name prefixClone = (Name)prefix.clone();
        return prefixClone.addAll(name);
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        return prefix + "/" + name;
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return this.getBoundContext().addToEnvironment(propName, propVal);
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        return this.getBoundContext().removeFromEnvironment(propName);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return this.getBoundContext().getEnvironment();
    }

    @Override
    public void close() throws NamingException {
        this.getBoundContext().close();
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        return prefix;
    }

    protected Context getBoundContext() throws NamingException {
        if (this.initialContext) {
            String ICName = IC_PREFIX;
            if (ContextBindings.isThreadBound()) {
                ICName = ICName + ContextBindings.getThreadName();
            } else if (ContextBindings.isClassLoaderBound()) {
                ICName = ICName + ContextBindings.getClassLoaderName();
            }
            Context initialContext = ContextBindings.getContext(ICName);
            if (initialContext == null) {
                initialContext = new NamingContext(this.env, ICName);
                ContextBindings.bindContext(ICName, initialContext);
            }
            return initialContext;
        }
        if (ContextBindings.isThreadBound()) {
            return ContextBindings.getThread();
        }
        return ContextBindings.getClassLoader();
    }

    protected String parseName(String name) throws NamingException {
        if (!this.initialContext && name.startsWith(prefix)) {
            return name.substring(prefixLength);
        }
        if (this.initialContext) {
            return name;
        }
        throw new NamingException(sm.getString("selectorContext.noJavaUrl"));
    }

    protected Name parseName(Name name) throws NamingException {
        if (!this.initialContext && !name.isEmpty() && name.get(0).startsWith(prefix)) {
            if (name.get(0).equals(prefix)) {
                return name.getSuffix(1);
            }
            Name result = name.getSuffix(1);
            result.add(0, name.get(0).substring(prefixLength));
            return result;
        }
        if (this.initialContext) {
            return name;
        }
        throw new NamingException(sm.getString("selectorContext.noJavaUrl"));
    }
}


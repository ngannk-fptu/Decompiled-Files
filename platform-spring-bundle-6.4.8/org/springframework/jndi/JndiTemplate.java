/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.jndi;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jndi.JndiCallback;
import org.springframework.jndi.TypeMismatchNamingException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

public class JndiTemplate {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Properties environment;

    public JndiTemplate() {
    }

    public JndiTemplate(@Nullable Properties environment2) {
        this.environment = environment2;
    }

    public void setEnvironment(@Nullable Properties environment2) {
        this.environment = environment2;
    }

    @Nullable
    public Properties getEnvironment() {
        return this.environment;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public <T> T execute(JndiCallback<T> contextCallback) throws NamingException {
        Context ctx = this.getContext();
        try {
            T t = contextCallback.doInContext(ctx);
            return t;
        }
        finally {
            this.releaseContext(ctx);
        }
    }

    public Context getContext() throws NamingException {
        return this.createInitialContext();
    }

    public void releaseContext(@Nullable Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            }
            catch (NamingException ex) {
                this.logger.debug((Object)"Could not close JNDI InitialContext", (Throwable)ex);
            }
        }
    }

    protected Context createInitialContext() throws NamingException {
        Hashtable icEnv = null;
        Properties env = this.getEnvironment();
        if (env != null) {
            icEnv = new Hashtable(env.size());
            CollectionUtils.mergePropertiesIntoMap(env, icEnv);
        }
        return new InitialContext(icEnv);
    }

    public Object lookup(String name) throws NamingException {
        Object result;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Looking up JNDI object with name [" + name + "]"));
        }
        if ((result = this.execute(ctx -> ctx.lookup(name))) == null) {
            throw new NameNotFoundException("JNDI object with [" + name + "] not found: JNDI implementation returned null");
        }
        return result;
    }

    public <T> T lookup(String name, @Nullable Class<T> requiredType) throws NamingException {
        Object jndiObject = this.lookup(name);
        if (requiredType != null && !requiredType.isInstance(jndiObject)) {
            throw new TypeMismatchNamingException(name, requiredType, jndiObject.getClass());
        }
        return (T)jndiObject;
    }

    public void bind(String name, Object object) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Binding JNDI object with name [" + name + "]"));
        }
        this.execute(ctx -> {
            ctx.bind(name, object);
            return null;
        });
    }

    public void rebind(String name, Object object) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Rebinding JNDI object with name [" + name + "]"));
        }
        this.execute(ctx -> {
            ctx.rebind(name, object);
            return null;
        });
    }

    public void unbind(String name) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Unbinding JNDI object with name [" + name + "]"));
        }
        this.execute(ctx -> {
            ctx.unbind(name);
            return null;
        });
    }
}


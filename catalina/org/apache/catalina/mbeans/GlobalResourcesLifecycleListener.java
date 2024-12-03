/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import org.apache.catalina.Group;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class GlobalResourcesLifecycleListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(GlobalResourcesLifecycleListener.class);
    protected static final StringManager sm = StringManager.getManager(GlobalResourcesLifecycleListener.class);
    protected Lifecycle component = null;

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if ("start".equals(event.getType())) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn((Object)sm.getString("listener.notServer", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
            }
            this.component = event.getLifecycle();
            this.createMBeans();
        } else if ("stop".equals(event.getType())) {
            this.destroyMBeans();
            this.component = null;
        }
    }

    protected void createMBeans() {
        Context context = null;
        try {
            context = (Context)new InitialContext().lookup("java:/");
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("globalResources.noNamingContext"));
            return;
        }
        try {
            this.createMBeans("", context);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("globalResources.createError"), (Throwable)e);
        }
    }

    protected void createMBeans(String prefix, Context context) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Creating MBeans for Global JNDI Resources in Context '" + prefix + "'"));
        }
        try {
            NamingEnumeration<Binding> bindings = context.listBindings("");
            while (bindings.hasMore()) {
                Binding binding = bindings.next();
                String name = prefix + binding.getName();
                Object value = context.lookup(binding.getName());
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Checking resource " + name));
                }
                if (value instanceof Context) {
                    this.createMBeans(name + "/", (Context)value);
                    continue;
                }
                if (!(value instanceof UserDatabase)) continue;
                try {
                    this.createMBeans(name, (UserDatabase)value);
                }
                catch (Exception e) {
                    log.error((Object)sm.getString("globalResources.userDatabaseCreateError", new Object[]{name}), (Throwable)e);
                }
            }
        }
        catch (RuntimeException ex) {
            log.error((Object)sm.getString("globalResources.createError.runtime"), (Throwable)ex);
        }
        catch (OperationNotSupportedException ex) {
            log.error((Object)sm.getString("globalResources.createError.operation"), (Throwable)ex);
        }
    }

    protected void createMBeans(String name, UserDatabase database) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Creating UserDatabase MBeans for resource " + name));
            log.debug((Object)("Database=" + database));
        }
        try {
            MBeanUtils.createMBean(database);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase", new Object[]{name}), e);
        }
        if (database.isSparse()) {
            return;
        }
        Iterator<Role> roles = database.getRoles();
        while (roles.hasNext()) {
            Role role = roles.next();
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Creating Role MBean for role " + role));
            }
            try {
                MBeanUtils.createMBean(role);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase.role", new Object[]{role}), e);
            }
        }
        Iterator<Group> groups = database.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Creating Group MBean for group " + group));
            }
            try {
                MBeanUtils.createMBean(group);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase.group", new Object[]{group}), e);
            }
        }
        Iterator<User> users = database.getUsers();
        while (users.hasNext()) {
            User user = users.next();
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Creating User MBean for user " + user));
            }
            try {
                MBeanUtils.createMBean(user);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase.user", new Object[]{user}), e);
            }
        }
    }

    protected void destroyMBeans() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Destroying MBeans for Global JNDI Resources");
        }
    }
}


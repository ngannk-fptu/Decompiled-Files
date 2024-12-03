/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.ContextResourceLink
 *  org.apache.tomcat.util.modeler.ManagedBean
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Group;
import org.apache.catalina.Loader;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.util.ContextName;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class MBeanUtils {
    protected static final StringManager sm = StringManager.getManager(MBeanUtils.class);
    private static final String[][] exceptions = new String[][]{{"org.apache.catalina.users.MemoryGroup", "Group"}, {"org.apache.catalina.users.MemoryRole", "Role"}, {"org.apache.catalina.users.MemoryUser", "User"}, {"org.apache.catalina.users.GenericGroup", "Group"}, {"org.apache.catalina.users.GenericRole", "Role"}, {"org.apache.catalina.users.GenericUser", "User"}};
    private static Registry registry = MBeanUtils.createRegistry();
    private static MBeanServer mserver = MBeanUtils.createServer();

    static String createManagedName(Object component) {
        String className = component.getClass().getName();
        for (String[] exception : exceptions) {
            if (!className.equals(exception[0])) continue;
            return exception[1];
        }
        int period = className.lastIndexOf(46);
        if (period >= 0) {
            className = className.substring(period + 1);
        }
        return className;
    }

    public static DynamicMBean createMBean(ContextEnvironment environment) throws Exception {
        String mname = MBeanUtils.createManagedName(environment);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)environment);
        ObjectName oname = MBeanUtils.createObjectName(domain, environment);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    public static DynamicMBean createMBean(ContextResource resource) throws Exception {
        String mname = MBeanUtils.createManagedName(resource);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)resource);
        ObjectName oname = MBeanUtils.createObjectName(domain, resource);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    public static DynamicMBean createMBean(ContextResourceLink resourceLink) throws Exception {
        String mname = MBeanUtils.createManagedName(resourceLink);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)resourceLink);
        ObjectName oname = MBeanUtils.createObjectName(domain, resourceLink);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    static DynamicMBean createMBean(Group group) throws Exception {
        String mname = MBeanUtils.createManagedName(group);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)group);
        ObjectName oname = MBeanUtils.createObjectName(domain, group);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    static DynamicMBean createMBean(Role role) throws Exception {
        String mname = MBeanUtils.createManagedName(role);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)role);
        ObjectName oname = MBeanUtils.createObjectName(domain, role);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    static DynamicMBean createMBean(User user) throws Exception {
        String mname = MBeanUtils.createManagedName(user);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)user);
        ObjectName oname = MBeanUtils.createObjectName(domain, user);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    static DynamicMBean createMBean(UserDatabase userDatabase) throws Exception {
        String mname;
        ManagedBean managed;
        if (userDatabase.isSparse()) {
            ManagedBean managed2 = registry.findManagedBean("SparseUserDatabase");
            if (managed2 == null) {
                Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{"SparseUserDatabase"}));
                throw new MBeanException(e);
            }
            String domain = managed2.getDomain();
            if (domain == null) {
                domain = mserver.getDefaultDomain();
            }
            DynamicMBean mbean = managed2.createMBean((Object)userDatabase);
            ObjectName oname = MBeanUtils.createObjectName(domain, userDatabase);
            if (mserver.isRegistered(oname)) {
                mserver.unregisterMBean(oname);
            }
            mserver.registerMBean(mbean, oname);
        }
        if ((managed = registry.findManagedBean(mname = MBeanUtils.createManagedName(userDatabase))) == null) {
            Exception e = new Exception(sm.getString("mBeanUtils.noManagedBean", new Object[]{mname}));
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean((Object)userDatabase);
        ObjectName oname = MBeanUtils.createObjectName(domain, userDatabase);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    public static ObjectName createObjectName(String domain, ContextEnvironment environment) throws MalformedObjectNameException {
        ObjectName name = null;
        Object container = environment.getNamingResources().getContainer();
        if (container instanceof Server) {
            name = new ObjectName(domain + ":type=Environment,resourcetype=Global,name=" + environment.getName());
        } else if (container instanceof Context) {
            Context context = (Context)container;
            ContextName cn = new ContextName(context.getName(), false);
            Container host = context.getParent();
            name = new ObjectName(domain + ":type=Environment,resourcetype=Context,host=" + host.getName() + ",context=" + cn.getDisplayName() + ",name=" + environment.getName());
        }
        return name;
    }

    public static ObjectName createObjectName(String domain, ContextResource resource) throws MalformedObjectNameException {
        ObjectName name = null;
        String quotedResourceName = ObjectName.quote(resource.getName());
        Object container = resource.getNamingResources().getContainer();
        if (container instanceof Server) {
            name = new ObjectName(domain + ":type=Resource,resourcetype=Global,class=" + resource.getType() + ",name=" + quotedResourceName);
        } else if (container instanceof Context) {
            Context context = (Context)container;
            ContextName cn = new ContextName(context.getName(), false);
            Container host = context.getParent();
            name = new ObjectName(domain + ":type=Resource,resourcetype=Context,host=" + host.getName() + ",context=" + cn.getDisplayName() + ",class=" + resource.getType() + ",name=" + quotedResourceName);
        }
        return name;
    }

    public static ObjectName createObjectName(String domain, ContextResourceLink resourceLink) throws MalformedObjectNameException {
        ObjectName name = null;
        String quotedResourceLinkName = ObjectName.quote(resourceLink.getName());
        Object container = resourceLink.getNamingResources().getContainer();
        if (container instanceof Server) {
            name = new ObjectName(domain + ":type=ResourceLink,resourcetype=Global,name=" + quotedResourceLinkName);
        } else if (container instanceof Context) {
            Context context = (Context)container;
            ContextName cn = new ContextName(context.getName(), false);
            Container host = context.getParent();
            name = new ObjectName(domain + ":type=ResourceLink,resourcetype=Context,host=" + host.getName() + ",context=" + cn.getDisplayName() + ",name=" + quotedResourceLinkName);
        }
        return name;
    }

    static ObjectName createObjectName(String domain, Group group) throws MalformedObjectNameException {
        ObjectName name = null;
        name = new ObjectName(domain + ":type=Group,groupname=" + ObjectName.quote(group.getGroupname()) + ",database=" + group.getUserDatabase().getId());
        return name;
    }

    static ObjectName createObjectName(String domain, Loader loader) throws MalformedObjectNameException {
        ObjectName name = null;
        Context context = loader.getContext();
        ContextName cn = new ContextName(context.getName(), false);
        Container host = context.getParent();
        name = new ObjectName(domain + ":type=Loader,host=" + host.getName() + ",context=" + cn.getDisplayName());
        return name;
    }

    static ObjectName createObjectName(String domain, Role role) throws MalformedObjectNameException {
        ObjectName name = new ObjectName(domain + ":type=Role,rolename=" + ObjectName.quote(role.getRolename()) + ",database=" + role.getUserDatabase().getId());
        return name;
    }

    static ObjectName createObjectName(String domain, User user) throws MalformedObjectNameException {
        ObjectName name = new ObjectName(domain + ":type=User,username=" + ObjectName.quote(user.getUsername()) + ",database=" + user.getUserDatabase().getId());
        return name;
    }

    static ObjectName createObjectName(String domain, UserDatabase userDatabase) throws MalformedObjectNameException {
        ObjectName name = null;
        name = new ObjectName(domain + ":type=UserDatabase,database=" + userDatabase.getId());
        return name;
    }

    public static synchronized Registry createRegistry() {
        if (registry == null) {
            registry = Registry.getRegistry(null, null);
            ClassLoader cl = MBeanUtils.class.getClassLoader();
            registry.loadDescriptors("org.apache.catalina.mbeans", cl);
            registry.loadDescriptors("org.apache.catalina.authenticator", cl);
            registry.loadDescriptors("org.apache.catalina.core", cl);
            registry.loadDescriptors("org.apache.catalina", cl);
            registry.loadDescriptors("org.apache.catalina.deploy", cl);
            registry.loadDescriptors("org.apache.catalina.loader", cl);
            registry.loadDescriptors("org.apache.catalina.realm", cl);
            registry.loadDescriptors("org.apache.catalina.session", cl);
            registry.loadDescriptors("org.apache.catalina.startup", cl);
            registry.loadDescriptors("org.apache.catalina.users", cl);
            registry.loadDescriptors("org.apache.catalina.ha", cl);
            registry.loadDescriptors("org.apache.catalina.connector", cl);
            registry.loadDescriptors("org.apache.catalina.valves", cl);
            registry.loadDescriptors("org.apache.catalina.storeconfig", cl);
            registry.loadDescriptors("org.apache.tomcat.util.descriptor.web", cl);
        }
        return registry;
    }

    public static synchronized MBeanServer createServer() {
        if (mserver == null) {
            mserver = Registry.getRegistry(null, null).getMBeanServer();
        }
        return mserver;
    }

    public static void destroyMBean(ContextEnvironment environment) throws Exception {
        ObjectName oname;
        String mname = MBeanUtils.createManagedName(environment);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        if (mserver.isRegistered(oname = MBeanUtils.createObjectName(domain, environment))) {
            mserver.unregisterMBean(oname);
        }
    }

    public static void destroyMBean(ContextResource resource) throws Exception {
        ObjectName oname;
        String mname;
        ManagedBean managed;
        if ("org.apache.catalina.UserDatabase".equals(resource.getType())) {
            MBeanUtils.destroyMBeanUserDatabase(resource.getName());
        }
        if ((managed = registry.findManagedBean(mname = MBeanUtils.createManagedName(resource))) == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        if (mserver.isRegistered(oname = MBeanUtils.createObjectName(domain, resource))) {
            mserver.unregisterMBean(oname);
        }
    }

    public static void destroyMBean(ContextResourceLink resourceLink) throws Exception {
        ObjectName oname;
        String mname = MBeanUtils.createManagedName(resourceLink);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        if (mserver.isRegistered(oname = MBeanUtils.createObjectName(domain, resourceLink))) {
            mserver.unregisterMBean(oname);
        }
    }

    static void destroyMBean(Group group) throws Exception {
        ObjectName oname;
        String mname = MBeanUtils.createManagedName(group);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        if (mserver.isRegistered(oname = MBeanUtils.createObjectName(domain, group))) {
            mserver.unregisterMBean(oname);
        }
    }

    static void destroyMBean(Role role) throws Exception {
        ObjectName oname;
        String mname = MBeanUtils.createManagedName(role);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        if (mserver.isRegistered(oname = MBeanUtils.createObjectName(domain, role))) {
            mserver.unregisterMBean(oname);
        }
    }

    static void destroyMBean(User user) throws Exception {
        ObjectName oname;
        String mname = MBeanUtils.createManagedName(user);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        if (mserver.isRegistered(oname = MBeanUtils.createObjectName(domain, user))) {
            mserver.unregisterMBean(oname);
        }
    }

    static void destroyMBeanUserDatabase(String userDatabase) throws Exception {
        ObjectName query = null;
        Set<ObjectName> results = null;
        query = new ObjectName("Users:type=Group,database=" + userDatabase + ",*");
        results = mserver.queryNames(query, null);
        for (ObjectName result : results) {
            mserver.unregisterMBean(result);
        }
        query = new ObjectName("Users:type=Role,database=" + userDatabase + ",*");
        results = mserver.queryNames(query, null);
        for (ObjectName result : results) {
            mserver.unregisterMBean(result);
        }
        query = new ObjectName("Users:type=User,database=" + userDatabase + ",*");
        results = mserver.queryNames(query, null);
        for (ObjectName result : results) {
            mserver.unregisterMBean(result);
        }
        ObjectName db = new ObjectName("Users:type=UserDatabase,database=" + userDatabase);
        if (mserver.isRegistered(db)) {
            mserver.unregisterMBean(db);
        }
        if (mserver.isRegistered(db = new ObjectName("Catalina:type=UserDatabase,database=" + userDatabase))) {
            mserver.unregisterMBean(db);
        }
    }
}


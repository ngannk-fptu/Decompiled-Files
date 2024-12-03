/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.ObjectCreationFactory
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.users.GenericGroup;
import org.apache.catalina.users.GenericRole;
import org.apache.catalina.users.GenericUser;
import org.apache.catalina.users.MemoryGroupCreationFactory;
import org.apache.catalina.users.MemoryRoleCreationFactory;
import org.apache.catalina.users.MemoryUserCreationFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.ObjectCreationFactory;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public class MemoryUserDatabase
implements UserDatabase {
    private static final Log log = LogFactory.getLog(MemoryUserDatabase.class);
    private static final StringManager sm = StringManager.getManager(MemoryUserDatabase.class);
    protected final Map<String, Group> groups = new ConcurrentHashMap<String, Group>();
    protected final String id;
    protected String pathname = "conf/tomcat-users.xml";
    protected String pathnameOld = this.pathname + ".old";
    protected String pathnameNew = this.pathname + ".new";
    protected boolean readonly = true;
    protected final Map<String, Role> roles = new ConcurrentHashMap<String, Role>();
    protected final Map<String, User> users = new ConcurrentHashMap<String, User>();
    private final ReentrantReadWriteLock dbLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.dbLock.readLock();
    private final Lock writeLock = this.dbLock.writeLock();
    private volatile long lastModified = 0L;
    private boolean watchSource = true;

    public MemoryUserDatabase() {
        this(null);
    }

    public MemoryUserDatabase(String id) {
        this.id = id;
    }

    @Override
    public Iterator<Group> getGroups() {
        this.readLock.lock();
        try {
            Iterator<Group> iterator = new ArrayList<Group>(this.groups.values()).iterator();
            return iterator;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        this.pathnameOld = pathname + ".old";
        this.pathnameNew = pathname + ".new";
    }

    public boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean getWatchSource() {
        return this.watchSource;
    }

    public void setWatchSource(boolean watchSource) {
        this.watchSource = watchSource;
    }

    @Override
    public Iterator<Role> getRoles() {
        this.readLock.lock();
        try {
            Iterator<Role> iterator = new ArrayList<Role>(this.roles.values()).iterator();
            return iterator;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Iterator<User> getUsers() {
        this.readLock.lock();
        try {
            Iterator<User> iterator = new ArrayList<User>(this.users.values()).iterator();
            return iterator;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        this.writeLock.lock();
        try {
            this.save();
            this.users.clear();
            this.groups.clear();
            this.roles.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Group createGroup(String groupname, String description) {
        if (groupname == null || groupname.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullGroup");
            log.warn((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        GenericGroup<MemoryUserDatabase> group = new GenericGroup<MemoryUserDatabase>(this, groupname, description, null);
        this.readLock.lock();
        try {
            this.groups.put(group.getGroupname(), group);
        }
        finally {
            this.readLock.unlock();
        }
        return group;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Role createRole(String rolename, String description) {
        if (rolename == null || rolename.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullRole");
            log.warn((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        GenericRole<MemoryUserDatabase> role = new GenericRole<MemoryUserDatabase>(this, rolename, description);
        this.readLock.lock();
        try {
            this.roles.put(role.getRolename(), role);
        }
        finally {
            this.readLock.unlock();
        }
        return role;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public User createUser(String username, String password, String fullName) {
        if (username == null || username.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullUser");
            log.warn((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        GenericUser<MemoryUserDatabase> user = new GenericUser<MemoryUserDatabase>(this, username, password, fullName, null, null);
        this.readLock.lock();
        try {
            this.users.put(user.getUsername(), user);
        }
        finally {
            this.readLock.unlock();
        }
        return user;
    }

    @Override
    public Group findGroup(String groupname) {
        this.readLock.lock();
        try {
            Group group = this.groups.get(groupname);
            return group;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Role findRole(String rolename) {
        this.readLock.lock();
        try {
            Role role = this.roles.get(rolename);
            return role;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public User findUser(String username) {
        this.readLock.lock();
        try {
            User user = this.users.get(username);
            return user;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void open() throws Exception {
        this.writeLock.lock();
        try {
            this.users.clear();
            this.groups.clear();
            this.roles.clear();
            String pathName = this.getPathname();
            try (ConfigurationSource.Resource resource = ConfigFileLoader.getSource().getResource(pathName);){
                this.lastModified = resource.getLastModified();
                Digester digester = new Digester();
                try {
                    digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("memoryUserDatabase.xmlFeatureEncoding"), (Throwable)e);
                }
                digester.addFactoryCreate("tomcat-users/group", (ObjectCreationFactory)new MemoryGroupCreationFactory(this), true);
                digester.addFactoryCreate("tomcat-users/role", (ObjectCreationFactory)new MemoryRoleCreationFactory(this), true);
                digester.addFactoryCreate("tomcat-users/user", (ObjectCreationFactory)new MemoryUserCreationFactory(this), true);
                digester.parse(resource.getInputStream());
            }
            catch (IOException ioe) {
                log.error((Object)sm.getString("memoryUserDatabase.fileNotFound", new Object[]{pathName}));
            }
            catch (Exception e) {
                this.users.clear();
                this.groups.clear();
                this.roles.clear();
                throw e;
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGroup(Group group) {
        this.readLock.lock();
        try {
            Iterator<User> users = this.getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeGroup(group);
            }
            this.groups.remove(group.getGroupname());
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRole(Role role) {
        this.readLock.lock();
        try {
            Iterator<Group> groups = this.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                group.removeRole(role);
            }
            Iterator<User> users = this.getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeRole(role);
            }
            this.roles.remove(role.getRolename());
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void removeUser(User user) {
        this.readLock.lock();
        try {
            this.users.remove(user.getUsername());
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Deprecated
    public boolean isWriteable() {
        return this.isWritable();
    }

    public boolean isWritable() {
        File dir;
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("catalina.base"), this.pathname);
        }
        return (dir = file.getParentFile()).exists() && dir.isDirectory() && dir.canWrite();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save() throws Exception {
        if (this.getReadonly()) {
            log.error((Object)sm.getString("memoryUserDatabase.readOnly"));
            return;
        }
        if (!this.isWritable()) {
            log.warn((Object)sm.getString("memoryUserDatabase.notPersistable"));
            return;
        }
        File fileNew = new File(this.pathnameNew);
        if (!fileNew.isAbsolute()) {
            fileNew = new File(System.getProperty("catalina.base"), this.pathnameNew);
        }
        this.writeLock.lock();
        try {
            try (FileOutputStream fos = new FileOutputStream(fileNew);
                 OutputStreamWriter osw = new OutputStreamWriter((OutputStream)fos, StandardCharsets.UTF_8);
                 PrintWriter writer = new PrintWriter(osw);){
                Role role;
                Iterator<Role> roles;
                writer.println("<?xml version='1.0' encoding='utf-8'?>");
                writer.println("<tomcat-users xmlns=\"http://tomcat.apache.org/xml\"");
                writer.print("              ");
                writer.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                writer.print("              ");
                writer.println("xsi:schemaLocation=\"http://tomcat.apache.org/xml tomcat-users.xsd\"");
                writer.println("              version=\"1.0\">");
                Iterator<Principal> values = null;
                values = this.getRoles();
                while (values.hasNext()) {
                    Role role2 = values.next();
                    writer.print("  <role rolename=\"");
                    writer.print(Escape.xml((String)role2.getRolename()));
                    writer.print("\"");
                    if (null != role2.getDescription()) {
                        writer.print(" description=\"");
                        writer.print(Escape.xml((String)role2.getDescription()));
                        writer.print("\"");
                    }
                    writer.println("/>");
                }
                values = this.getGroups();
                while (values.hasNext()) {
                    Group group = (Group)values.next();
                    writer.print("  <group groupname=\"");
                    writer.print(Escape.xml((String)group.getName()));
                    writer.print("\"");
                    if (null != group.getDescription()) {
                        writer.print(" description=\"");
                        writer.print(Escape.xml((String)group.getDescription()));
                        writer.print("\"");
                    }
                    writer.print(" roles=\"");
                    roles = group.getRoles();
                    while (roles.hasNext()) {
                        role = roles.next();
                        writer.print(Escape.xml((String)role.getRolename()));
                        if (!roles.hasNext()) continue;
                        writer.print(',');
                    }
                    writer.println("\"/>");
                }
                values = this.getUsers();
                while (values.hasNext()) {
                    User user = (User)values.next();
                    writer.print("  <user username=\"");
                    writer.print(Escape.xml((String)user.getUsername()));
                    writer.print("\" password=\"");
                    writer.print(Escape.xml((String)user.getPassword()));
                    writer.print("\"");
                    if (null != user.getFullName()) {
                        writer.print(" fullName=\"");
                        writer.print(Escape.xml((String)user.getFullName()));
                        writer.print("\"");
                    }
                    writer.print(" groups=\"");
                    Iterator<Group> groups = user.getGroups();
                    while (groups.hasNext()) {
                        Group group = groups.next();
                        writer.print(Escape.xml((String)group.getGroupname()));
                        if (!groups.hasNext()) continue;
                        writer.print(',');
                    }
                    writer.print("\" roles=\"");
                    roles = user.getRoles();
                    while (roles.hasNext()) {
                        role = roles.next();
                        writer.print(Escape.xml((String)role.getRolename()));
                        if (!roles.hasNext()) continue;
                        writer.print(',');
                    }
                    writer.print("\"/>");
                }
                writer.println("</tomcat-users>");
                if (writer.checkError()) {
                    throw new IOException(sm.getString("memoryUserDatabase.writeException", new Object[]{fileNew.getAbsolutePath()}));
                }
            }
            catch (IOException e) {
                if (fileNew.exists() && !fileNew.delete()) {
                    log.warn((Object)sm.getString("memoryUserDatabase.fileDelete", new Object[]{fileNew}));
                }
                throw e;
            }
            this.lastModified = fileNew.lastModified();
        }
        finally {
            this.writeLock.unlock();
        }
        File fileOld = new File(this.pathnameOld);
        if (!fileOld.isAbsolute()) {
            fileOld = new File(System.getProperty("catalina.base"), this.pathnameOld);
        }
        if (fileOld.exists() && !fileOld.delete()) {
            throw new IOException(sm.getString("memoryUserDatabase.fileDelete", new Object[]{fileOld}));
        }
        File fileOrig = new File(this.pathname);
        if (!fileOrig.isAbsolute()) {
            fileOrig = new File(System.getProperty("catalina.base"), this.pathname);
        }
        if (fileOrig.exists() && !fileOrig.renameTo(fileOld)) {
            throw new IOException(sm.getString("memoryUserDatabase.renameOld", new Object[]{fileOld.getAbsolutePath()}));
        }
        if (!fileNew.renameTo(fileOrig)) {
            if (fileOld.exists() && !fileOld.renameTo(fileOrig)) {
                log.warn((Object)sm.getString("memoryUserDatabase.restoreOrig", new Object[]{fileOld}));
            }
            throw new IOException(sm.getString("memoryUserDatabase.renameNew", new Object[]{fileOrig.getAbsolutePath()}));
        }
        if (fileOld.exists() && !fileOld.delete()) {
            throw new IOException(sm.getString("memoryUserDatabase.fileDelete", new Object[]{fileOld}));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void backgroundProcess() {
        if (!this.watchSource) {
            return;
        }
        URI uri = ConfigFileLoader.getSource().getURI(this.getPathname());
        URLConnection uConn = null;
        URL url = uri.toURL();
        uConn = url.openConnection();
        if (this.lastModified != uConn.getLastModified()) {
            this.writeLock.lock();
            try {
                long detectedLastModified = uConn.getLastModified();
                if (this.lastModified != detectedLastModified && detectedLastModified + 2000L < System.currentTimeMillis()) {
                    log.info((Object)sm.getString("memoryUserDatabase.reload", new Object[]{this.id, uri}));
                    this.open();
                }
            }
            finally {
                this.writeLock.unlock();
            }
        }
        if (uConn == null) return;
        try {
            uConn.getInputStream().close();
            return;
        }
        catch (FileNotFoundException fnfe) {
            this.lastModified = 0L;
            return;
        }
        catch (IOException ioe) {
            log.warn((Object)sm.getString("memoryUserDatabase.fileClose", new Object[]{this.pathname}), (Throwable)ioe);
        }
        return;
        catch (Exception ioe) {
            try {
                log.error((Object)sm.getString("memoryUserDatabase.reloadError", new Object[]{this.id, uri}), (Throwable)ioe);
                if (uConn == null) return;
            }
            catch (Throwable throwable) {
                if (uConn == null) throw throwable;
                try {
                    uConn.getInputStream().close();
                    throw throwable;
                }
                catch (FileNotFoundException fnfe) {
                    this.lastModified = 0L;
                    throw throwable;
                }
                catch (IOException ioe2) {
                    log.warn((Object)sm.getString("memoryUserDatabase.fileClose", new Object[]{this.pathname}), (Throwable)ioe2);
                }
                throw throwable;
            }
            try {
                uConn.getInputStream().close();
                return;
            }
            catch (FileNotFoundException fnfe) {
                this.lastModified = 0L;
                return;
            }
            catch (IOException ioe3) {
                log.warn((Object)sm.getString("memoryUserDatabase.fileClose", new Object[]{this.pathname}), (Throwable)ioe3);
            }
            return;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MemoryUserDatabase[id=");
        sb.append(this.id);
        sb.append(",pathname=");
        sb.append(this.pathname);
        sb.append(",groupCount=");
        sb.append(this.groups.size());
        sb.append(",roleCount=");
        sb.append(this.roles.size());
        sb.append(",userCount=");
        sb.append(this.users.size());
        sb.append(']');
        return sb.toString();
    }
}


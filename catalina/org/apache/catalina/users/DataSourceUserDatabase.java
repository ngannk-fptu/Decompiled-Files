/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sql.DataSource;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.users.GenericGroup;
import org.apache.catalina.users.GenericRole;
import org.apache.catalina.users.GenericUser;
import org.apache.catalina.users.SparseUserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class DataSourceUserDatabase
extends SparseUserDatabase {
    private static final Log log = LogFactory.getLog(DataSourceUserDatabase.class);
    private static final StringManager sm = StringManager.getManager(DataSourceUserDatabase.class);
    protected final DataSource dataSource;
    protected final String id;
    protected final ConcurrentHashMap<String, User> createdUsers = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, User> modifiedUsers = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, User> removedUsers = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Group> createdGroups = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Group> modifiedGroups = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Group> removedGroups = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Role> createdRoles = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Role> modifiedRoles = new ConcurrentHashMap();
    protected final ConcurrentHashMap<String, Role> removedRoles = new ConcurrentHashMap();
    private String preparedAllUsers = null;
    private String preparedAllGroups = null;
    private String preparedAllRoles = null;
    private String preparedGroup = null;
    private String preparedRole = null;
    private String preparedUserRoles = null;
    private String preparedUser = null;
    private String preparedUserGroups = null;
    private String preparedGroupRoles = null;
    protected String dataSourceName = null;
    protected String roleNameCol = null;
    protected String roleAndGroupDescriptionCol = null;
    protected String groupNameCol = null;
    protected String userCredCol = null;
    protected String userFullNameCol = null;
    protected String userNameCol = null;
    protected String userRoleTable = null;
    protected String userGroupTable = null;
    protected String groupRoleTable = null;
    protected String userTable = null;
    protected String groupTable = null;
    protected String roleTable = null;
    private volatile boolean connectionSuccess = true;
    protected boolean readonly = true;
    private final ReentrantReadWriteLock dbLock = new ReentrantReadWriteLock();
    private final Lock dbReadLock = this.dbLock.readLock();
    private final Lock dbWriteLock = this.dbLock.writeLock();
    private final ReentrantReadWriteLock groupsLock = new ReentrantReadWriteLock();
    private final Lock groupsReadLock = this.groupsLock.readLock();
    private final Lock groupsWriteLock = this.groupsLock.writeLock();
    private final ReentrantReadWriteLock usersLock = new ReentrantReadWriteLock();
    private final Lock usersReadLock = this.usersLock.readLock();
    private final Lock usersWriteLock = this.usersLock.writeLock();
    private final ReentrantReadWriteLock rolesLock = new ReentrantReadWriteLock();
    private final Lock rolesReadLock = this.rolesLock.readLock();
    private final Lock rolesWriteLock = this.rolesLock.writeLock();

    public DataSourceUserDatabase(DataSource dataSource, String id) {
        this.dataSource = dataSource;
        this.id = id;
    }

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getRoleNameCol() {
        return this.roleNameCol;
    }

    public void setRoleNameCol(String roleNameCol) {
        this.roleNameCol = roleNameCol;
    }

    public String getUserCredCol() {
        return this.userCredCol;
    }

    public void setUserCredCol(String userCredCol) {
        this.userCredCol = userCredCol;
    }

    public String getUserNameCol() {
        return this.userNameCol;
    }

    public void setUserNameCol(String userNameCol) {
        this.userNameCol = userNameCol;
    }

    public String getUserRoleTable() {
        return this.userRoleTable;
    }

    public void setUserRoleTable(String userRoleTable) {
        this.userRoleTable = userRoleTable;
    }

    public String getUserTable() {
        return this.userTable;
    }

    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }

    public String getRoleAndGroupDescriptionCol() {
        return this.roleAndGroupDescriptionCol;
    }

    public void setRoleAndGroupDescriptionCol(String roleAndGroupDescriptionCol) {
        this.roleAndGroupDescriptionCol = roleAndGroupDescriptionCol;
    }

    public String getGroupNameCol() {
        return this.groupNameCol;
    }

    public void setGroupNameCol(String groupNameCol) {
        this.groupNameCol = groupNameCol;
    }

    public String getUserFullNameCol() {
        return this.userFullNameCol;
    }

    public void setUserFullNameCol(String userFullNameCol) {
        this.userFullNameCol = userFullNameCol;
    }

    public String getUserGroupTable() {
        return this.userGroupTable;
    }

    public void setUserGroupTable(String userGroupTable) {
        this.userGroupTable = userGroupTable;
    }

    public String getGroupRoleTable() {
        return this.groupRoleTable;
    }

    public void setGroupRoleTable(String groupRoleTable) {
        this.groupRoleTable = groupRoleTable;
    }

    public String getGroupTable() {
        return this.groupTable;
    }

    public void setGroupTable(String groupTable) {
        this.groupTable = groupTable;
    }

    public String getRoleTable() {
        return this.roleTable;
    }

    public void setRoleTable(String roleTable) {
        this.roleTable = roleTable;
    }

    public boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<Group> getGroups() {
        this.dbReadLock.lock();
        try {
            this.groupsReadLock.lock();
            try {
                HashMap<String, Group> groups;
                block27: {
                    groups = new HashMap<String, Group>();
                    groups.putAll(this.createdGroups);
                    groups.putAll(this.modifiedGroups);
                    try (Connection dbConnection = this.openConnection();){
                        if (dbConnection == null || this.preparedAllGroups == null) break block27;
                        try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedAllGroups);
                             ResultSet rs = stmt.executeQuery();){
                            while (rs.next()) {
                                Group group;
                                String groupName = rs.getString(1);
                                if (groupName == null || groups.containsKey(groupName) || this.removedGroups.containsKey(groupName) || (group = this.findGroupInternal(dbConnection, groupName)) == null) continue;
                                groups.put(groupName, group);
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                Iterator<Group> iterator = groups.values().iterator();
                this.groupsReadLock.unlock();
                return iterator;
            }
            catch (Throwable throwable) {
                this.groupsReadLock.unlock();
                throw throwable;
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<Role> getRoles() {
        this.dbReadLock.lock();
        try {
            this.rolesReadLock.lock();
            try {
                HashMap<String, Role> roles;
                block27: {
                    roles = new HashMap<String, Role>();
                    roles.putAll(this.createdRoles);
                    roles.putAll(this.modifiedRoles);
                    try (Connection dbConnection = this.openConnection();){
                        if (dbConnection == null || this.preparedAllRoles == null) break block27;
                        try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedAllRoles);
                             ResultSet rs = stmt.executeQuery();){
                            while (rs.next()) {
                                Role role;
                                String roleName = rs.getString(1);
                                if (roleName == null || roles.containsKey(roleName) || this.removedRoles.containsKey(roleName) || (role = this.findRoleInternal(dbConnection, roleName)) == null) continue;
                                roles.put(roleName, role);
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                Iterator<Role> iterator = roles.values().iterator();
                this.rolesReadLock.unlock();
                return iterator;
            }
            catch (Throwable throwable) {
                this.rolesReadLock.unlock();
                throw throwable;
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<User> getUsers() {
        this.dbReadLock.lock();
        try {
            this.usersReadLock.lock();
            try {
                HashMap<String, User> users = new HashMap<String, User>();
                users.putAll(this.createdUsers);
                users.putAll(this.modifiedUsers);
                Connection dbConnection = this.openConnection();
                if (dbConnection != null) {
                    try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedAllUsers);
                         ResultSet rs = stmt.executeQuery();){
                        while (rs.next()) {
                            User user;
                            String userName = rs.getString(1);
                            if (userName == null || users.containsKey(userName) || this.removedUsers.containsKey(userName) || (user = this.findUserInternal(dbConnection, userName)) == null) continue;
                            users.put(userName, user);
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                    finally {
                        this.closeConnection(dbConnection);
                    }
                }
                Iterator<User> iterator = users.values().iterator();
                this.usersReadLock.unlock();
                return iterator;
            }
            catch (Throwable throwable) {
                this.usersReadLock.unlock();
                throw throwable;
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Group createGroup(String groupname, String description) {
        this.dbReadLock.lock();
        try {
            this.groupsWriteLock.lock();
            try {
                GenericGroup<DataSourceUserDatabase> group = new GenericGroup<DataSourceUserDatabase>(this, groupname, description, null);
                this.createdGroups.put(groupname, group);
                this.modifiedGroups.remove(groupname);
                GenericGroup<DataSourceUserDatabase> genericGroup = group;
                this.groupsWriteLock.unlock();
                return genericGroup;
            }
            catch (Throwable throwable) {
                this.groupsWriteLock.unlock();
                throw throwable;
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Role createRole(String rolename, String description) {
        this.dbReadLock.lock();
        try {
            this.rolesWriteLock.lock();
            try {
                GenericRole<DataSourceUserDatabase> role = new GenericRole<DataSourceUserDatabase>(this, rolename, description);
                this.createdRoles.put(rolename, role);
                this.modifiedRoles.remove(rolename);
                GenericRole<DataSourceUserDatabase> genericRole = role;
                this.rolesWriteLock.unlock();
                return genericRole;
            }
            catch (Throwable throwable) {
                this.rolesWriteLock.unlock();
                throw throwable;
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public User createUser(String username, String password, String fullName) {
        this.dbReadLock.lock();
        try {
            this.usersWriteLock.lock();
            try {
                GenericUser<DataSourceUserDatabase> user = new GenericUser<DataSourceUserDatabase>(this, username, password, fullName, null, null);
                this.createdUsers.put(username, user);
                this.modifiedUsers.remove(username);
                GenericUser<DataSourceUserDatabase> genericUser = user;
                this.usersWriteLock.unlock();
                return genericUser;
            }
            catch (Throwable throwable) {
                this.usersWriteLock.unlock();
                throw throwable;
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public Group findGroup(String groupname) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public Group findGroupInternal(Connection dbConnection, String groupName) {
        GenericGroup<DataSourceUserDatabase> group;
        block30: {
            group = null;
            try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedGroup);){
                stmt.setString(1, groupName);
                try (ResultSet rs = stmt.executeQuery();){
                    if (!rs.next() || rs.getString(1) == null) break block30;
                    String description = this.roleAndGroupDescriptionCol != null ? rs.getString(2) : null;
                    ArrayList<Role> groupRoles = new ArrayList<Role>();
                    if (groupName != null) {
                        groupName = groupName.trim();
                        try (PreparedStatement stmt2 = dbConnection.prepareStatement(this.preparedGroupRoles);){
                            stmt2.setString(1, groupName);
                            try (ResultSet rs2 = stmt2.executeQuery();){
                                while (rs2.next()) {
                                    Role groupRole;
                                    String roleName = rs2.getString(1);
                                    if (roleName == null || (groupRole = this.findRoleInternal(dbConnection, roleName)) == null) continue;
                                    groupRoles.add(groupRole);
                                }
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                    group = new GenericGroup<DataSourceUserDatabase>(this, groupName, description, groupRoles);
                }
            }
            catch (SQLException e) {
                log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
            }
        }
        return group;
    }

    /*
     * Exception decompiling
     */
    @Override
    public Role findRole(String rolename) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public Role findRoleInternal(Connection dbConnection, String roleName) {
        GenericRole<DataSourceUserDatabase> role = null;
        try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedRole);){
            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery();){
                if (rs.next() && rs.getString(1) != null) {
                    String description = this.roleAndGroupDescriptionCol != null ? rs.getString(2) : null;
                    role = new GenericRole<DataSourceUserDatabase>(this, roleName, description);
                }
            }
        }
        catch (SQLException e) {
            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
        }
        return role;
    }

    /*
     * Exception decompiling
     */
    @Override
    public User findUser(String username) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 10[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public User findUserInternal(Connection dbConnection, String userName) {
        String dbCredentials = null;
        String fullName = null;
        try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedUser);){
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery();){
                if (rs.next()) {
                    dbCredentials = rs.getString(1);
                    if (this.userFullNameCol != null) {
                        fullName = rs.getString(2);
                    }
                }
                dbCredentials = dbCredentials != null ? dbCredentials.trim() : null;
            }
        }
        catch (SQLException e) {
            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
        }
        ArrayList<Group> groups = new ArrayList<Group>();
        if (this.isGroupStoreDefined()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedUserGroups);){
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery();){
                    while (rs.next()) {
                        Group group;
                        String groupName = rs.getString(1);
                        if (groupName == null || (group = this.findGroupInternal(dbConnection, groupName)) == null) continue;
                        groups.add(group);
                    }
                }
            }
            catch (SQLException e) {
                log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
            }
        }
        ArrayList<Role> roles = new ArrayList<Role>();
        if (this.userRoleTable != null && this.roleNameCol != null) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedUserRoles);){
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery();){
                    while (rs.next()) {
                        Role role;
                        String roleName = rs.getString(1);
                        if (roleName == null || (role = this.findRoleInternal(dbConnection, roleName)) == null) continue;
                        roles.add(role);
                    }
                }
            }
            catch (SQLException e) {
                log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
            }
        }
        GenericUser<DataSourceUserDatabase> user = new GenericUser<DataSourceUserDatabase>(this, userName, dbCredentials, fullName, groups, roles);
        return user;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void modifiedGroup(Group group) {
        this.dbReadLock.lock();
        try {
            this.groupsWriteLock.lock();
            try {
                String name = group.getName();
                if (!this.createdGroups.containsKey(name) && !this.removedGroups.containsKey(name)) {
                    this.modifiedGroups.put(name, group);
                }
            }
            finally {
                this.groupsWriteLock.unlock();
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void modifiedRole(Role role) {
        this.dbReadLock.lock();
        try {
            this.rolesWriteLock.lock();
            try {
                String name = role.getName();
                if (!this.createdRoles.containsKey(name) && !this.removedRoles.containsKey(name)) {
                    this.modifiedRoles.put(name, role);
                }
            }
            finally {
                this.rolesWriteLock.unlock();
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void modifiedUser(User user) {
        this.dbReadLock.lock();
        try {
            this.usersWriteLock.lock();
            try {
                String name = user.getName();
                if (!this.createdUsers.containsKey(name) && !this.removedUsers.containsKey(name)) {
                    this.modifiedUsers.put(name, user);
                }
            }
            finally {
                this.usersWriteLock.unlock();
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    @Override
    public void open() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("DataSource UserDatabase features: User<->Role [" + Boolean.toString(this.userRoleTable != null && this.roleNameCol != null) + "], Roles [" + Boolean.toString(this.isRoleStoreDefined()) + "], Groups [" + Boolean.toString(this.isRoleStoreDefined()) + "]"));
        }
        this.dbWriteLock.lock();
        try {
            StringBuilder temp = new StringBuilder("SELECT ");
            temp.append(this.userCredCol);
            if (this.userFullNameCol != null) {
                temp.append(',').append(this.userFullNameCol);
            }
            temp.append(" FROM ");
            temp.append(this.userTable);
            temp.append(" WHERE ");
            temp.append(this.userNameCol);
            temp.append(" = ?");
            this.preparedUser = temp.toString();
            temp = new StringBuilder("SELECT ");
            temp.append(this.userNameCol);
            temp.append(" FROM ");
            temp.append(this.userTable);
            this.preparedAllUsers = temp.toString();
            temp = new StringBuilder("SELECT ");
            temp.append(this.roleNameCol);
            temp.append(" FROM ");
            temp.append(this.userRoleTable);
            temp.append(" WHERE ");
            temp.append(this.userNameCol);
            temp.append(" = ?");
            this.preparedUserRoles = temp.toString();
            if (this.isGroupStoreDefined()) {
                temp = new StringBuilder("SELECT ");
                temp.append(this.groupNameCol);
                temp.append(" FROM ");
                temp.append(this.userGroupTable);
                temp.append(" WHERE ");
                temp.append(this.userNameCol);
                temp.append(" = ?");
                this.preparedUserGroups = temp.toString();
                temp = new StringBuilder("SELECT ");
                temp.append(this.roleNameCol);
                temp.append(" FROM ");
                temp.append(this.groupRoleTable);
                temp.append(" WHERE ");
                temp.append(this.groupNameCol);
                temp.append(" = ?");
                this.preparedGroupRoles = temp.toString();
                temp = new StringBuilder("SELECT ");
                temp.append(this.groupNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp.append(" FROM ");
                temp.append(this.groupTable);
                temp.append(" WHERE ");
                temp.append(this.groupNameCol);
                temp.append(" = ?");
                this.preparedGroup = temp.toString();
                temp = new StringBuilder("SELECT ");
                temp.append(this.groupNameCol);
                temp.append(" FROM ");
                temp.append(this.groupTable);
                this.preparedAllGroups = temp.toString();
            }
            if (this.isRoleStoreDefined()) {
                temp = new StringBuilder("SELECT ");
                temp.append(this.roleNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp.append(" FROM ");
                temp.append(this.roleTable);
                temp.append(" WHERE ");
                temp.append(this.roleNameCol);
                temp.append(" = ?");
                this.preparedRole = temp.toString();
                temp = new StringBuilder("SELECT ");
                temp.append(this.roleNameCol);
                temp.append(" FROM ");
                temp.append(this.roleTable);
                this.preparedAllRoles = temp.toString();
            } else if (this.userRoleTable != null && this.roleNameCol != null) {
                temp = new StringBuilder("SELECT ");
                temp.append(this.roleNameCol);
                temp.append(" FROM ");
                temp.append(this.userRoleTable);
                temp.append(" WHERE ");
                temp.append(this.roleNameCol);
                temp.append(" = ?");
                this.preparedRole = temp.toString();
            }
        }
        finally {
            this.dbWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGroup(Group group) {
        this.dbReadLock.lock();
        try {
            this.groupsWriteLock.lock();
            try {
                String name = group.getName();
                this.createdGroups.remove(name);
                this.modifiedGroups.remove(name);
                this.removedGroups.put(name, group);
            }
            finally {
                this.groupsWriteLock.unlock();
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRole(Role role) {
        this.dbReadLock.lock();
        try {
            this.rolesWriteLock.lock();
            try {
                String name = role.getName();
                this.createdRoles.remove(name);
                this.modifiedRoles.remove(name);
                this.removedRoles.put(name, role);
            }
            finally {
                this.rolesWriteLock.unlock();
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeUser(User user) {
        this.dbReadLock.lock();
        try {
            this.usersWriteLock.lock();
            try {
                String name = user.getName();
                this.createdUsers.remove(name);
                this.modifiedUsers.remove(name);
                this.removedUsers.put(name, user);
            }
            finally {
                this.usersWriteLock.unlock();
            }
        }
        finally {
            this.dbReadLock.unlock();
        }
    }

    @Override
    public void save() throws Exception {
        if (this.readonly) {
            return;
        }
        Connection dbConnection = this.openConnection();
        if (dbConnection == null) {
            return;
        }
        this.dbWriteLock.lock();
        try {
            try {
                this.saveInternal(dbConnection);
            }
            finally {
                this.closeConnection(dbConnection);
            }
        }
        finally {
            this.dbWriteLock.unlock();
        }
    }

    protected void saveInternal(Connection dbConnection) {
        Group group;
        Iterator<Group> groups;
        PreparedStatement stmt;
        Role role;
        Iterator<Role> roles;
        PreparedStatement stmt2;
        PreparedStatement stmt3;
        StringBuilder temp = null;
        StringBuilder tempRelation = null;
        StringBuilder tempRelationDelete = null;
        if (this.isRoleStoreDefined()) {
            if (!this.removedRoles.isEmpty()) {
                temp = new StringBuilder("DELETE FROM ");
                temp.append(this.roleTable);
                temp.append(" WHERE ").append(this.roleNameCol);
                temp.append(" = ?");
                if (this.groupRoleTable != null) {
                    tempRelationDelete = new StringBuilder("DELETE FROM ");
                    tempRelationDelete.append(this.groupRoleTable);
                    tempRelationDelete.append(" WHERE ");
                    tempRelationDelete.append(this.roleNameCol);
                    tempRelationDelete.append(" = ?");
                }
                Iterator<Role> tempRelationDelete2 = new StringBuilder("DELETE FROM ");
                ((StringBuilder)((Object)tempRelationDelete2)).append(this.userRoleTable);
                ((StringBuilder)((Object)tempRelationDelete2)).append(" WHERE ");
                ((StringBuilder)((Object)tempRelationDelete2)).append(this.roleNameCol);
                ((StringBuilder)((Object)tempRelationDelete2)).append(" = ?");
                for (Role role2 : this.removedRoles.values()) {
                    PreparedStatement stmt4;
                    if (tempRelationDelete != null) {
                        try {
                            stmt4 = dbConnection.prepareStatement(tempRelationDelete.toString());
                            try {
                                stmt4.setString(1, role2.getRolename());
                                stmt4.executeUpdate();
                            }
                            finally {
                                if (stmt4 != null) {
                                    stmt4.close();
                                }
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                    try {
                        stmt4 = dbConnection.prepareStatement(((StringBuilder)((Object)tempRelationDelete2)).toString());
                        try {
                            stmt4.setString(1, role2.getRolename());
                            stmt4.executeUpdate();
                        }
                        finally {
                            if (stmt4 != null) {
                                stmt4.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                    try {
                        stmt4 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt4.setString(1, role2.getRolename());
                            stmt4.executeUpdate();
                        }
                        finally {
                            if (stmt4 == null) continue;
                            stmt4.close();
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                this.removedRoles.clear();
            }
            if (!this.createdRoles.isEmpty()) {
                temp = new StringBuilder("INSERT INTO ");
                temp.append(this.roleTable);
                temp.append('(').append(this.roleNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp.append(") VALUES (?");
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(", ?");
                }
                temp.append(')');
                for (Role role3 : this.createdRoles.values()) {
                    try {
                        stmt3 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt3.setString(1, role3.getRolename());
                            if (this.roleAndGroupDescriptionCol != null) {
                                stmt3.setString(2, role3.getDescription());
                            }
                            stmt3.executeUpdate();
                        }
                        finally {
                            if (stmt3 == null) continue;
                            stmt3.close();
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                this.createdRoles.clear();
            }
            if (!this.modifiedRoles.isEmpty() && this.roleAndGroupDescriptionCol != null) {
                temp = new StringBuilder("UPDATE ");
                temp.append(this.roleTable);
                temp.append(" SET ").append(this.roleAndGroupDescriptionCol);
                temp.append(" = ? WHERE ").append(this.roleNameCol);
                temp.append(" = ?");
                for (Role role3 : this.modifiedRoles.values()) {
                    try {
                        stmt3 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt3.setString(1, role3.getDescription());
                            stmt3.setString(2, role3.getRolename());
                            stmt3.executeUpdate();
                        }
                        finally {
                            if (stmt3 == null) continue;
                            stmt3.close();
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                this.modifiedRoles.clear();
            }
        } else if (this.userRoleTable != null && this.roleNameCol != null) {
            tempRelationDelete = new StringBuilder("DELETE FROM ");
            tempRelationDelete.append(this.userRoleTable);
            tempRelationDelete.append(" WHERE ");
            tempRelationDelete.append(this.roleNameCol);
            tempRelationDelete.append(" = ?");
            for (Role role3 : this.removedRoles.values()) {
                try {
                    stmt3 = dbConnection.prepareStatement(tempRelationDelete.toString());
                    try {
                        stmt3.setString(1, role3.getRolename());
                        stmt3.executeUpdate();
                    }
                    finally {
                        if (stmt3 == null) continue;
                        stmt3.close();
                    }
                }
                catch (SQLException e) {
                    log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                }
            }
            this.removedRoles.clear();
        }
        if (this.isGroupStoreDefined()) {
            Role role4;
            Iterator<Role> roles2;
            PreparedStatement stmt5;
            tempRelation = new StringBuilder("INSERT INTO ");
            tempRelation.append(this.groupRoleTable);
            tempRelation.append('(').append(this.groupNameCol).append(", ");
            tempRelation.append(this.roleNameCol);
            tempRelation.append(") VALUES (?, ?)");
            String groupRoleRelation = tempRelation.toString();
            tempRelationDelete = new StringBuilder("DELETE FROM ");
            tempRelationDelete.append(this.groupRoleTable);
            tempRelationDelete.append(" WHERE ");
            tempRelationDelete.append(this.groupNameCol);
            tempRelationDelete.append(" = ?");
            String groupRoleRelationDelete = tempRelationDelete.toString();
            if (!this.removedGroups.isEmpty()) {
                temp = new StringBuilder("DELETE FROM ");
                temp.append(this.groupTable);
                temp.append(" WHERE ").append(this.groupNameCol);
                temp.append(" = ?");
                Iterator<Group> tempRelationDelete2 = new StringBuilder("DELETE FROM ");
                ((StringBuilder)((Object)tempRelationDelete2)).append(this.userGroupTable);
                ((StringBuilder)((Object)tempRelationDelete2)).append(" WHERE ");
                ((StringBuilder)((Object)tempRelationDelete2)).append(this.groupNameCol);
                ((StringBuilder)((Object)tempRelationDelete2)).append(" = ?");
                for (Group group2 : this.removedGroups.values()) {
                    PreparedStatement stmt6;
                    try {
                        stmt6 = dbConnection.prepareStatement(groupRoleRelationDelete);
                        try {
                            stmt6.setString(1, group2.getGroupname());
                            stmt6.executeUpdate();
                        }
                        finally {
                            if (stmt6 != null) {
                                stmt6.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                    try {
                        stmt6 = dbConnection.prepareStatement(((StringBuilder)((Object)tempRelationDelete2)).toString());
                        try {
                            stmt6.setString(1, group2.getGroupname());
                            stmt6.executeUpdate();
                        }
                        finally {
                            if (stmt6 != null) {
                                stmt6.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                    try {
                        stmt6 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt6.setString(1, group2.getGroupname());
                            stmt6.executeUpdate();
                        }
                        finally {
                            if (stmt6 == null) continue;
                            stmt6.close();
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                this.removedGroups.clear();
            }
            if (!this.createdGroups.isEmpty()) {
                temp = new StringBuilder("INSERT INTO ");
                temp.append(this.groupTable);
                temp.append('(').append(this.groupNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp.append(") VALUES (?");
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(", ?");
                }
                temp.append(')');
                for (Group group3 : this.createdGroups.values()) {
                    try {
                        stmt5 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt5.setString(1, group3.getGroupname());
                            if (this.roleAndGroupDescriptionCol != null) {
                                stmt5.setString(2, group3.getDescription());
                            }
                            stmt5.executeUpdate();
                        }
                        finally {
                            if (stmt5 != null) {
                                stmt5.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                    roles2 = group3.getRoles();
                    while (roles2.hasNext()) {
                        role4 = roles2.next();
                        try {
                            stmt2 = dbConnection.prepareStatement(groupRoleRelation);
                            try {
                                stmt2.setString(1, group3.getGroupname());
                                stmt2.setString(2, role4.getRolename());
                                stmt2.executeUpdate();
                            }
                            finally {
                                if (stmt2 == null) continue;
                                stmt2.close();
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                }
                this.createdGroups.clear();
            }
            if (!this.modifiedGroups.isEmpty()) {
                if (this.roleAndGroupDescriptionCol != null) {
                    temp = new StringBuilder("UPDATE ");
                    temp.append(this.groupTable);
                    temp.append(" SET ").append(this.roleAndGroupDescriptionCol);
                    temp.append(" = ? WHERE ").append(this.groupNameCol);
                    temp.append(" = ?");
                }
                for (Group group3 : this.modifiedGroups.values()) {
                    if (temp != null) {
                        try {
                            stmt5 = dbConnection.prepareStatement(temp.toString());
                            try {
                                stmt5.setString(1, group3.getDescription());
                                stmt5.setString(2, group3.getGroupname());
                                stmt5.executeUpdate();
                            }
                            finally {
                                if (stmt5 != null) {
                                    stmt5.close();
                                }
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                    try {
                        stmt5 = dbConnection.prepareStatement(groupRoleRelationDelete);
                        try {
                            stmt5.setString(1, group3.getGroupname());
                            stmt5.executeUpdate();
                        }
                        finally {
                            if (stmt5 != null) {
                                stmt5.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                    roles2 = group3.getRoles();
                    while (roles2.hasNext()) {
                        role4 = roles2.next();
                        try {
                            stmt2 = dbConnection.prepareStatement(groupRoleRelation);
                            try {
                                stmt2.setString(1, group3.getGroupname());
                                stmt2.setString(2, role4.getRolename());
                                stmt2.executeUpdate();
                            }
                            finally {
                                if (stmt2 == null) continue;
                                stmt2.close();
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                }
                this.modifiedGroups.clear();
            }
        }
        String userRoleRelation = null;
        String userRoleRelationDelete = null;
        if (this.userRoleTable != null && this.roleNameCol != null) {
            tempRelation = new StringBuilder("INSERT INTO ");
            tempRelation.append(this.userRoleTable);
            tempRelation.append('(').append(this.userNameCol).append(", ");
            tempRelation.append(this.roleNameCol);
            tempRelation.append(") VALUES (?, ?)");
            userRoleRelation = tempRelation.toString();
            tempRelationDelete = new StringBuilder("DELETE FROM ");
            tempRelationDelete.append(this.userRoleTable);
            tempRelationDelete.append(" WHERE ");
            tempRelationDelete.append(this.userNameCol);
            tempRelationDelete.append(" = ?");
            userRoleRelationDelete = tempRelationDelete.toString();
        }
        String userGroupRelation = null;
        String userGroupRelationDelete = null;
        if (this.isGroupStoreDefined()) {
            tempRelation = new StringBuilder("INSERT INTO ");
            tempRelation.append(this.userGroupTable);
            tempRelation.append('(').append(this.userNameCol).append(", ");
            tempRelation.append(this.groupNameCol);
            tempRelation.append(") VALUES (?, ?)");
            userGroupRelation = tempRelation.toString();
            tempRelationDelete = new StringBuilder("DELETE FROM ");
            tempRelationDelete.append(this.userGroupTable);
            tempRelationDelete.append(" WHERE ");
            tempRelationDelete.append(this.userNameCol);
            tempRelationDelete.append(" = ?");
            userGroupRelationDelete = tempRelationDelete.toString();
        }
        if (!this.removedUsers.isEmpty()) {
            temp = new StringBuilder("DELETE FROM ");
            temp.append(this.userTable);
            temp.append(" WHERE ").append(this.userNameCol);
            temp.append(" = ?");
            for (User user : this.removedUsers.values()) {
                if (userRoleRelationDelete != null) {
                    try {
                        stmt2 = dbConnection.prepareStatement(userRoleRelationDelete);
                        try {
                            stmt2.setString(1, user.getUsername());
                            stmt2.executeUpdate();
                        }
                        finally {
                            if (stmt2 != null) {
                                stmt2.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                if (userGroupRelationDelete != null) {
                    try {
                        stmt2 = dbConnection.prepareStatement(userGroupRelationDelete);
                        try {
                            stmt2.setString(1, user.getUsername());
                            stmt2.executeUpdate();
                        }
                        finally {
                            if (stmt2 != null) {
                                stmt2.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                try {
                    stmt2 = dbConnection.prepareStatement(temp.toString());
                    try {
                        stmt2.setString(1, user.getUsername());
                        stmt2.executeUpdate();
                    }
                    finally {
                        if (stmt2 == null) continue;
                        stmt2.close();
                    }
                }
                catch (SQLException e) {
                    log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                }
            }
            this.removedUsers.clear();
        }
        if (!this.createdUsers.isEmpty()) {
            temp = new StringBuilder("INSERT INTO ");
            temp.append(this.userTable);
            temp.append('(').append(this.userNameCol);
            temp.append(", ").append(this.userCredCol);
            if (this.userFullNameCol != null) {
                temp.append(',').append(this.userFullNameCol);
            }
            temp.append(") VALUES (?, ?");
            if (this.userFullNameCol != null) {
                temp.append(", ?");
            }
            temp.append(')');
            for (User user : this.createdUsers.values()) {
                try {
                    stmt2 = dbConnection.prepareStatement(temp.toString());
                    try {
                        stmt2.setString(1, user.getUsername());
                        stmt2.setString(2, user.getPassword());
                        if (this.userFullNameCol != null) {
                            stmt2.setString(3, user.getFullName());
                        }
                        stmt2.executeUpdate();
                    }
                    finally {
                        if (stmt2 != null) {
                            stmt2.close();
                        }
                    }
                }
                catch (SQLException e) {
                    log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                }
                if (userRoleRelation != null) {
                    roles = user.getRoles();
                    while (roles.hasNext()) {
                        role = roles.next();
                        try {
                            stmt = dbConnection.prepareStatement(userRoleRelation);
                            try {
                                stmt.setString(1, user.getUsername());
                                stmt.setString(2, role.getRolename());
                                stmt.executeUpdate();
                            }
                            finally {
                                if (stmt != null) {
                                    stmt.close();
                                }
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                }
                if (userGroupRelation == null) continue;
                groups = user.getGroups();
                while (groups.hasNext()) {
                    group = groups.next();
                    try {
                        stmt = dbConnection.prepareStatement(userGroupRelation);
                        try {
                            stmt.setString(1, user.getUsername());
                            stmt.setString(2, group.getGroupname());
                            stmt.executeUpdate();
                        }
                        finally {
                            if (stmt == null) continue;
                            stmt.close();
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
            }
            this.createdUsers.clear();
        }
        if (!this.modifiedUsers.isEmpty()) {
            temp = new StringBuilder("UPDATE ");
            temp.append(this.userTable);
            temp.append(" SET ").append(this.userCredCol);
            temp.append(" = ?");
            if (this.userFullNameCol != null) {
                temp.append(", ").append(this.userFullNameCol).append(" = ?");
            }
            temp.append(" WHERE ").append(this.userNameCol);
            temp.append(" = ?");
            for (User user : this.modifiedUsers.values()) {
                try {
                    stmt2 = dbConnection.prepareStatement(temp.toString());
                    try {
                        stmt2.setString(1, user.getPassword());
                        if (this.userFullNameCol != null) {
                            stmt2.setString(2, user.getFullName());
                            stmt2.setString(3, user.getUsername());
                        } else {
                            stmt2.setString(2, user.getUsername());
                        }
                        stmt2.executeUpdate();
                    }
                    finally {
                        if (stmt2 != null) {
                            stmt2.close();
                        }
                    }
                }
                catch (SQLException e) {
                    log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                }
                if (userRoleRelationDelete != null) {
                    try {
                        stmt2 = dbConnection.prepareStatement(userRoleRelationDelete);
                        try {
                            stmt2.setString(1, user.getUsername());
                            stmt2.executeUpdate();
                        }
                        finally {
                            if (stmt2 != null) {
                                stmt2.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                if (userGroupRelationDelete != null) {
                    try {
                        stmt2 = dbConnection.prepareStatement(userGroupRelationDelete);
                        try {
                            stmt2.setString(1, user.getUsername());
                            stmt2.executeUpdate();
                        }
                        finally {
                            if (stmt2 != null) {
                                stmt2.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
                if (userRoleRelation != null) {
                    roles = user.getRoles();
                    while (roles.hasNext()) {
                        role = roles.next();
                        try {
                            stmt = dbConnection.prepareStatement(userRoleRelation);
                            try {
                                stmt.setString(1, user.getUsername());
                                stmt.setString(2, role.getRolename());
                                stmt.executeUpdate();
                            }
                            finally {
                                if (stmt != null) {
                                    stmt.close();
                                }
                            }
                        }
                        catch (SQLException e) {
                            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                        }
                    }
                }
                if (userGroupRelation == null) continue;
                groups = user.getGroups();
                while (groups.hasNext()) {
                    group = groups.next();
                    try {
                        stmt = dbConnection.prepareStatement(userGroupRelation);
                        try {
                            stmt.setString(1, user.getUsername());
                            stmt.setString(2, group.getGroupname());
                            stmt.executeUpdate();
                        }
                        finally {
                            if (stmt == null) continue;
                            stmt.close();
                        }
                    }
                    catch (SQLException e) {
                        log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
                    }
                }
            }
            this.modifiedGroups.clear();
        }
    }

    @Override
    public boolean isAvailable() {
        return this.connectionSuccess;
    }

    protected boolean isGroupStoreDefined() {
        return this.groupTable != null && this.userGroupTable != null && this.groupNameCol != null && this.groupRoleTable != null && this.isRoleStoreDefined();
    }

    protected boolean isRoleStoreDefined() {
        return this.roleTable != null && this.userRoleTable != null && this.roleNameCol != null;
    }

    protected Connection openConnection() {
        if (this.dataSource == null) {
            return null;
        }
        try {
            Connection connection = this.dataSource.getConnection();
            this.connectionSuccess = true;
            return connection;
        }
        catch (Exception e) {
            this.connectionSuccess = false;
            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
            return null;
        }
    }

    protected void closeConnection(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        }
        catch (SQLException e) {
            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
        }
        try {
            dbConnection.close();
        }
        catch (SQLException e) {
            log.error((Object)sm.getString("dataSourceUserDatabase.exception"), (Throwable)e);
        }
    }
}


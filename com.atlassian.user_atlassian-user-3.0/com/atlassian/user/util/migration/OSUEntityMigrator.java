/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.provider.AccessProvider
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 *  org.apache.log4j.Logger
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowCallbackHandler
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 *  org.springframework.orm.hibernate.SessionFactoryUtils
 */
package com.atlassian.user.util.migration;

import com.atlassian.user.EntityException;
import com.atlassian.user.ExternalEntity;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.DefaultDelegationAccessor;
import com.atlassian.user.configuration.DelegationAccessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.hibernate.DefaultExternalEntityDAO;
import com.atlassian.user.impl.hibernate.DefaultHibernateUser;
import com.atlassian.user.impl.osuser.OSUAccessor;
import com.atlassian.user.impl.osuser.OSUUserManager;
import com.atlassian.user.security.password.Credential;
import com.atlassian.user.util.migration.EntityMigrator;
import com.atlassian.user.util.migration.MigrationProgressListener;
import com.atlassian.user.util.migration.MigratorConfiguration;
import com.atlassian.user.util.migration.OSUserDao;
import com.opensymphony.user.provider.AccessProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate.SessionFactoryUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OSUEntityMigrator
implements EntityMigrator {
    private static final Logger log = Logger.getLogger(OSUEntityMigrator.class);
    private static final String OSUSER_REPOSITORY_KEY = "osuserRepository";
    private UserManager targetUserManager;
    private GroupManager targetGroupManager;
    private AccessProvider osAccessProvider;
    private final SessionFactory sessionFactory;
    private final DefaultExternalEntityDAO externalEntityDAO;
    private Session hibernateSession;

    public OSUEntityMigrator(RepositoryAccessor osuserRepositoryAccessor, RepositoryAccessor repositoryAccessor, SessionFactory sessionFactory) {
        if (osuserRepositoryAccessor == null) {
            throw new IllegalArgumentException("osuserRepositoryAccessor is required.");
        }
        if (repositoryAccessor == null) {
            throw new IllegalArgumentException("targetRepositoryAccessor is required.");
        }
        if (sessionFactory == null) {
            throw new IllegalArgumentException("sessionFactory is required.");
        }
        this.sessionFactory = sessionFactory;
        this.externalEntityDAO = new DefaultExternalEntityDAO(sessionFactory);
        DelegationAccessor targetRepositoryAccessor = this.getNonOSUserRepositoryAccessor(repositoryAccessor);
        if (!targetRepositoryAccessor.getRepositoryAccessors().isEmpty()) {
            UserManager osUserManager = osuserRepositoryAccessor.getUserManager();
            if (osUserManager == null) {
                throw new IllegalArgumentException("osUserManager is required.");
            }
            OSUAccessor osuAccessor = ((OSUUserManager)osUserManager).getAccessor();
            if (osuAccessor == null) {
                throw new IllegalArgumentException("osuAccessor is required.");
            }
            this.osAccessProvider = osuAccessor.getAccessProvider();
            if (this.osAccessProvider == null) {
                throw new IllegalArgumentException("osAccessProvider is required.");
            }
            this.targetUserManager = targetRepositoryAccessor.getUserManager();
            this.targetGroupManager = targetRepositoryAccessor.getGroupManager();
            if (this.targetUserManager == null) {
                throw new IllegalArgumentException("userManager is required.");
            }
            if (this.targetGroupManager == null) {
                throw new IllegalArgumentException("groupManager is required.");
            }
        }
    }

    private DelegationAccessor getNonOSUserRepositoryAccessor(RepositoryAccessor repositoryAccessor) {
        DefaultDelegationAccessor nonOSUserDelegationAccessor = new DefaultDelegationAccessor();
        if (repositoryAccessor instanceof DelegationAccessor) {
            DelegationAccessor delegationAccessor = (DelegationAccessor)repositoryAccessor;
            for (RepositoryAccessor accessor : delegationAccessor.getRepositoryAccessors()) {
                if (OSUSER_REPOSITORY_KEY.equals(accessor.getIdentifier().getKey())) continue;
                nonOSUserDelegationAccessor.addRepositoryAccessor(accessor);
            }
            return nonOSUserDelegationAccessor;
        }
        if (!OSUSER_REPOSITORY_KEY.equals(repositoryAccessor.getIdentifier().getKey())) {
            nonOSUserDelegationAccessor.addRepositoryAccessor(repositoryAccessor);
        }
        return nonOSUserDelegationAccessor;
    }

    @Override
    public boolean hasExistingUsers() {
        try {
            return !this.targetUserManager.getUsers().isEmpty();
        }
        catch (EntityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void migrate(MigratorConfiguration config, MigrationProgressListener progressListener) throws EntityException {
        if (this.targetUserManager == null) {
            throw new RepositoryException("No non OSUser repository configured. Cannot perform migration.");
        }
        this.hibernateSession = SessionFactoryUtils.getSession((SessionFactory)this.sessionFactory, (boolean)true);
        OSUserDao osUserDao = new OSUserDao(this.getDataSource());
        Map<Long, DefaultUser> users = osUserDao.findAllUsers();
        Map<String, List<String>> userGroups = osUserDao.findAllUserGroups(users);
        Map<User, Boolean> migratedUsers = this.migrateUsers(progressListener, users);
        for (Map.Entry<User, Boolean> userEntry : migratedUsers.entrySet()) {
            User user = userEntry.getKey();
            this.migrateUserGroupMembership(user, userGroups.get(user.getName()), userEntry.getValue(), config, progressListener);
        }
        this.migrateGroups(progressListener);
    }

    private Map<User, Boolean> migrateUsers(MigrationProgressListener progressListener, Map<Long, DefaultUser> users) throws EntityException {
        progressListener.userMigrationStarted(users.size());
        HashMap<User, Boolean> migratedUsers = new HashMap<User, Boolean>();
        int i = 0;
        for (Map.Entry<Long, DefaultUser> userEntry : users.entrySet()) {
            Long osUserId = userEntry.getKey();
            User user = userEntry.getValue();
            User existingUser = this.targetUserManager.getUser(user.getName());
            if (existingUser == null) {
                try {
                    User newUser = this.addUser(this.targetUserManager, (DefaultUser)user);
                    migratedUsers.put(newUser, Boolean.TRUE);
                    this.migratePropertySet(osUserId, newUser);
                }
                catch (UnsupportedOperationException e) {
                    log.error((Object)("could add user [ " + user.getName() + " ]"));
                }
                catch (IllegalArgumentException e) {
                    log.error((Object)("could not migrate invalid user [ " + user.getName() + " ]"));
                }
            } else {
                migratedUsers.put(existingUser, Boolean.FALSE);
                this.migratePropertySet(osUserId, user);
            }
            progressListener.userMigrated();
            if (i % 100 == 0) {
                try {
                    this.hibernateSession.flush();
                    this.hibernateSession.clear();
                }
                catch (HibernateException e) {
                    log.error((Object)e);
                }
            }
            ++i;
        }
        progressListener.userMigrationComplete();
        return migratedUsers;
    }

    private void migrateGroups(MigrationProgressListener progressListener) throws EntityException {
        List groupNames = this.osAccessProvider.list();
        progressListener.groupMigrationStarted(groupNames.size());
        for (String groupName : groupNames) {
            this.getOrCreateGroup(groupName);
            progressListener.groupMigrated();
        }
        progressListener.groupMigrationComplete();
    }

    private void migrateUserGroupMembership(User user, List<String> userGroups, boolean isCreatedUser, MigratorConfiguration config, MigrationProgressListener progressListener) throws EntityException {
        if (userGroups != null) {
            for (String groupName : userGroups) {
                Group group = this.getOrCreateGroup(groupName);
                if (!isCreatedUser && !config.isMigrateMembershipsForExistingUsers()) continue;
                if (log.isInfoEnabled()) {
                    log.info((Object)("Adding member <" + user.getName() + "> to group <" + groupName + ">"));
                }
                if (!this.targetGroupManager.isReadOnly(group)) {
                    this.targetGroupManager.addMembership(group, user);
                    continue;
                }
                progressListener.readonlyGroupMembershipNotMigrated(group.getName(), user.getName());
            }
        }
    }

    private void migratePropertySet(Long userId, User user) throws EntityException {
        if (log.isInfoEnabled()) {
            log.info((Object)("Migrating properties for <" + user.getName() + ">"));
        }
        User targetUser = this.targetUserManager.getUser(user.getName());
        final String entityName = this.getEntityName(targetUser);
        final long entityId = this.getEntityId(targetUser);
        final JdbcTemplate template = new JdbcTemplate(this.getDataSource());
        if (template.queryForInt("SELECT count(*) FROM OS_PROPERTYENTRY WHERE entity_name=? AND entity_id=?", new Object[]{entityName, entityId}) == 0) {
            template.query("SELECT * FROM OS_PROPERTYENTRY WHERE entity_name = 'OSUser_user' AND entity_id = ? AND entity_key <> 'fullName' AND entity_key <> 'email'", new Object[]{userId}, new RowCallbackHandler(){

                public void processRow(ResultSet resultSet) throws SQLException {
                    template.update("INSERT INTO OS_PROPERTYENTRY (entity_name,entity_id,entity_key,key_type,boolean_val,double_val,string_val,long_val,int_val,date_val) VALUES (?,?,?,?,?,?,?,?,?,?)", new Object[]{entityName, entityId, resultSet.getString("entity_key"), resultSet.getInt("key_type"), resultSet.getBoolean("boolean_val"), resultSet.getDouble("double_val"), resultSet.getString("string_val"), resultSet.getLong("long_val"), resultSet.getInt("int_val"), resultSet.getTimestamp("date_val")});
                }
            });
        }
    }

    private String getEntityName(User user) {
        if (this.isExternalUser(user)) {
            return "EXT_" + user.getName();
        }
        return "LOC_" + user.getName();
    }

    private long getEntityId(User user) {
        if (!this.isExternalUser(user)) {
            return ((DefaultHibernateUser)user).getId();
        }
        ExternalEntity externalEntity = this.externalEntityDAO.getExternalEntity(user.getName());
        if (externalEntity != null) {
            return externalEntity.getId();
        }
        return this.externalEntityDAO.createExternalEntity(user.getName()).getId();
    }

    private boolean isExternalUser(User user) {
        return !(user instanceof DefaultHibernateUser);
    }

    private User addUser(UserManager userManager, DefaultUser user) throws EntityException {
        Credential credential;
        if (log.isInfoEnabled()) {
            log.info((Object)("Adding user <" + user.getName() + ">"));
        }
        Credential credential2 = credential = user.getPassword() == null ? Credential.NONE : Credential.encrypted(user.getPassword());
        if (user.getFullName() == null) {
            user.setFullName("");
        }
        if (user.getEmail() == null) {
            user.setEmail("");
        }
        return userManager.createUser(user, credential);
    }

    private Group getOrCreateGroup(String groupName) throws EntityException {
        Group group = this.targetGroupManager.getGroup(groupName);
        if (group == null) {
            if (log.isInfoEnabled()) {
                log.info((Object)("Creating group <" + groupName + ">"));
            }
            group = this.targetGroupManager.createGroup(groupName);
        }
        return group;
    }

    private DataSource getDataSource() {
        Connection conn;
        try {
            conn = this.hibernateSession.connection();
        }
        catch (HibernateException e) {
            throw new RuntimeException(e);
        }
        return new SingleConnectionDataSource(conn, true);
    }
}


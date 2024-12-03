/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.core.log.LogMessage
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.jdbc.core.PreparedStatementSetter
 *  org.springframework.util.Assert
 */
package org.springframework.security.provisioning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.log.LogMessage;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

public class JdbcUserDetailsManager
extends JdbcDaoImpl
implements UserDetailsManager,
GroupManager {
    public static final String DEF_CREATE_USER_SQL = "insert into users (username, password, enabled) values (?,?,?)";
    public static final String DEF_DELETE_USER_SQL = "delete from users where username = ?";
    public static final String DEF_UPDATE_USER_SQL = "update users set password = ?, enabled = ? where username = ?";
    public static final String DEF_INSERT_AUTHORITY_SQL = "insert into authorities (username, authority) values (?,?)";
    public static final String DEF_DELETE_USER_AUTHORITIES_SQL = "delete from authorities where username = ?";
    public static final String DEF_USER_EXISTS_SQL = "select username from users where username = ?";
    public static final String DEF_CHANGE_PASSWORD_SQL = "update users set password = ? where username = ?";
    public static final String DEF_FIND_GROUPS_SQL = "select group_name from groups";
    public static final String DEF_FIND_USERS_IN_GROUP_SQL = "select username from group_members gm, groups g where gm.group_id = g.id and g.group_name = ?";
    public static final String DEF_INSERT_GROUP_SQL = "insert into groups (group_name) values (?)";
    public static final String DEF_FIND_GROUP_ID_SQL = "select id from groups where group_name = ?";
    public static final String DEF_INSERT_GROUP_AUTHORITY_SQL = "insert into group_authorities (group_id, authority) values (?,?)";
    public static final String DEF_DELETE_GROUP_SQL = "delete from groups where id = ?";
    public static final String DEF_DELETE_GROUP_AUTHORITIES_SQL = "delete from group_authorities where group_id = ?";
    public static final String DEF_DELETE_GROUP_MEMBERS_SQL = "delete from group_members where group_id = ?";
    public static final String DEF_RENAME_GROUP_SQL = "update groups set group_name = ? where group_name = ?";
    public static final String DEF_INSERT_GROUP_MEMBER_SQL = "insert into group_members (group_id, username) values (?,?)";
    public static final String DEF_DELETE_GROUP_MEMBER_SQL = "delete from group_members where group_id = ? and username = ?";
    public static final String DEF_GROUP_AUTHORITIES_QUERY_SQL = "select g.id, g.group_name, ga.authority from groups g, group_authorities ga where g.group_name = ? and g.id = ga.group_id ";
    public static final String DEF_DELETE_GROUP_AUTHORITY_SQL = "delete from group_authorities where group_id = ? and authority = ?";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private String createUserSql = "insert into users (username, password, enabled) values (?,?,?)";
    private String deleteUserSql = "delete from users where username = ?";
    private String updateUserSql = "update users set password = ?, enabled = ? where username = ?";
    private String createAuthoritySql = "insert into authorities (username, authority) values (?,?)";
    private String deleteUserAuthoritiesSql = "delete from authorities where username = ?";
    private String userExistsSql = "select username from users where username = ?";
    private String changePasswordSql = "update users set password = ? where username = ?";
    private String findAllGroupsSql = "select group_name from groups";
    private String findUsersInGroupSql = "select username from group_members gm, groups g where gm.group_id = g.id and g.group_name = ?";
    private String insertGroupSql = "insert into groups (group_name) values (?)";
    private String findGroupIdSql = "select id from groups where group_name = ?";
    private String insertGroupAuthoritySql = "insert into group_authorities (group_id, authority) values (?,?)";
    private String deleteGroupSql = "delete from groups where id = ?";
    private String deleteGroupAuthoritiesSql = "delete from group_authorities where group_id = ?";
    private String deleteGroupMembersSql = "delete from group_members where group_id = ?";
    private String renameGroupSql = "update groups set group_name = ? where group_name = ?";
    private String insertGroupMemberSql = "insert into group_members (group_id, username) values (?,?)";
    private String deleteGroupMemberSql = "delete from group_members where group_id = ? and username = ?";
    private String groupAuthoritiesSql = "select g.id, g.group_name, ga.authority from groups g, group_authorities ga where g.group_name = ? and g.id = ga.group_id ";
    private String deleteGroupAuthoritySql = "delete from group_authorities where group_id = ? and authority = ?";
    private AuthenticationManager authenticationManager;
    private UserCache userCache = new NullUserCache();

    public JdbcUserDetailsManager() {
    }

    public JdbcUserDetailsManager(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    @Override
    protected void initDao() throws ApplicationContextException {
        if (this.authenticationManager == null) {
            this.logger.info((Object)"No authentication manager set. Reauthentication of users when changing passwords will not be performed.");
        }
        super.initDao();
    }

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        return this.getJdbcTemplate().query(this.getUsersByUsernameQuery(), this::mapToUser, new Object[]{username});
    }

    private UserDetails mapToUser(ResultSet rs, int rowNum) throws SQLException {
        String userName = rs.getString(1);
        String password = rs.getString(2);
        boolean enabled = rs.getBoolean(3);
        boolean accLocked = false;
        boolean accExpired = false;
        boolean credsExpired = false;
        if (rs.getMetaData().getColumnCount() > 3) {
            accLocked = rs.getBoolean(4);
            accExpired = rs.getBoolean(5);
            credsExpired = rs.getBoolean(6);
        }
        return new User(userName, password, enabled, !accExpired, !credsExpired, !accLocked, AuthorityUtils.NO_AUTHORITIES);
    }

    @Override
    public void createUser(UserDetails user) {
        this.validateUserDetails(user);
        this.getJdbcTemplate().update(this.createUserSql, ps -> {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isEnabled());
            int paramCount = ps.getParameterMetaData().getParameterCount();
            if (paramCount > 3) {
                ps.setBoolean(4, !user.isAccountNonLocked());
                ps.setBoolean(5, !user.isAccountNonExpired());
                ps.setBoolean(6, !user.isCredentialsNonExpired());
            }
        });
        if (this.getEnableAuthorities()) {
            this.insertUserAuthorities(user);
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        this.validateUserDetails(user);
        this.getJdbcTemplate().update(this.updateUserSql, ps -> {
            ps.setString(1, user.getPassword());
            ps.setBoolean(2, user.isEnabled());
            int paramCount = ps.getParameterMetaData().getParameterCount();
            if (paramCount == 3) {
                ps.setString(3, user.getUsername());
            } else {
                ps.setBoolean(3, !user.isAccountNonLocked());
                ps.setBoolean(4, !user.isAccountNonExpired());
                ps.setBoolean(5, !user.isCredentialsNonExpired());
                ps.setString(6, user.getUsername());
            }
        });
        if (this.getEnableAuthorities()) {
            this.deleteUserAuthorities(user.getUsername());
            this.insertUserAuthorities(user);
        }
        this.userCache.removeUserFromCache(user.getUsername());
    }

    private void insertUserAuthorities(UserDetails user) {
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            this.getJdbcTemplate().update(this.createAuthoritySql, new Object[]{user.getUsername(), grantedAuthority.getAuthority()});
        }
    }

    @Override
    public void deleteUser(String username) {
        if (this.getEnableAuthorities()) {
            this.deleteUserAuthorities(username);
        }
        this.getJdbcTemplate().update(this.deleteUserSql, new Object[]{username});
        this.userCache.removeUserFromCache(username);
    }

    private void deleteUserAuthorities(String username) {
        this.getJdbcTemplate().update(this.deleteUserAuthoritiesSql, new Object[]{username});
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        }
        String username = currentUser.getName();
        if (this.authenticationManager != null) {
            this.logger.debug((Object)LogMessage.format((String)"Reauthenticating user '%s' for password change request.", (Object)username));
            this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        } else {
            this.logger.debug((Object)"No authentication manager set. Password won't be re-checked.");
        }
        this.logger.debug((Object)("Changing password for user '" + username + "'"));
        this.getJdbcTemplate().update(this.changePasswordSql, new Object[]{newPassword, username});
        Authentication authentication = this.createNewAuthentication(currentUser, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.userCache.removeUserFromCache(username);
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = this.loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        List users = this.getJdbcTemplate().queryForList(this.userExistsSql, (Object[])new String[]{username}, String.class);
        if (users.size() > 1) {
            throw new IncorrectResultSizeDataAccessException("More than one user found with name '" + username + "'", 1);
        }
        return users.size() == 1;
    }

    @Override
    public List<String> findAllGroups() {
        return this.getJdbcTemplate().queryForList(this.findAllGroupsSql, String.class);
    }

    @Override
    public List<String> findUsersInGroup(String groupName) {
        Assert.hasText((String)groupName, (String)"groupName should have text");
        return this.getJdbcTemplate().queryForList(this.findUsersInGroupSql, (Object[])new String[]{groupName}, String.class);
    }

    @Override
    public void createGroup(String groupName, List<GrantedAuthority> authorities) {
        Assert.hasText((String)groupName, (String)"groupName should have text");
        Assert.notNull(authorities, (String)"authorities cannot be null");
        this.logger.debug((Object)("Creating new group '" + groupName + "' with authorities " + AuthorityUtils.authorityListToSet(authorities)));
        this.getJdbcTemplate().update(this.insertGroupSql, new Object[]{groupName});
        int groupId = this.findGroupId(groupName);
        for (GrantedAuthority a : authorities) {
            String authority = a.getAuthority();
            this.getJdbcTemplate().update(this.insertGroupAuthoritySql, ps -> {
                ps.setInt(1, groupId);
                ps.setString(2, authority);
            });
        }
    }

    @Override
    public void deleteGroup(String groupName) {
        this.logger.debug((Object)("Deleting group '" + groupName + "'"));
        Assert.hasText((String)groupName, (String)"groupName should have text");
        int id = this.findGroupId(groupName);
        PreparedStatementSetter groupIdPSS = ps -> ps.setInt(1, id);
        this.getJdbcTemplate().update(this.deleteGroupMembersSql, groupIdPSS);
        this.getJdbcTemplate().update(this.deleteGroupAuthoritiesSql, groupIdPSS);
        this.getJdbcTemplate().update(this.deleteGroupSql, groupIdPSS);
    }

    @Override
    public void renameGroup(String oldName, String newName) {
        this.logger.debug((Object)("Changing group name from '" + oldName + "' to '" + newName + "'"));
        Assert.hasText((String)oldName, (String)"oldName should have text");
        Assert.hasText((String)newName, (String)"newName should have text");
        this.getJdbcTemplate().update(this.renameGroupSql, new Object[]{newName, oldName});
    }

    @Override
    public void addUserToGroup(String username, String groupName) {
        this.logger.debug((Object)("Adding user '" + username + "' to group '" + groupName + "'"));
        Assert.hasText((String)username, (String)"username should have text");
        Assert.hasText((String)groupName, (String)"groupName should have text");
        int id = this.findGroupId(groupName);
        this.getJdbcTemplate().update(this.insertGroupMemberSql, ps -> {
            ps.setInt(1, id);
            ps.setString(2, username);
        });
        this.userCache.removeUserFromCache(username);
    }

    @Override
    public void removeUserFromGroup(String username, String groupName) {
        this.logger.debug((Object)("Removing user '" + username + "' to group '" + groupName + "'"));
        Assert.hasText((String)username, (String)"username should have text");
        Assert.hasText((String)groupName, (String)"groupName should have text");
        int id = this.findGroupId(groupName);
        this.getJdbcTemplate().update(this.deleteGroupMemberSql, ps -> {
            ps.setInt(1, id);
            ps.setString(2, username);
        });
        this.userCache.removeUserFromCache(username);
    }

    @Override
    public List<GrantedAuthority> findGroupAuthorities(String groupName) {
        this.logger.debug((Object)("Loading authorities for group '" + groupName + "'"));
        Assert.hasText((String)groupName, (String)"groupName should have text");
        return this.getJdbcTemplate().query(this.groupAuthoritiesSql, (Object[])new String[]{groupName}, this::mapToGrantedAuthority);
    }

    private GrantedAuthority mapToGrantedAuthority(ResultSet rs, int rowNum) throws SQLException {
        String roleName = this.getRolePrefix() + rs.getString(3);
        return new SimpleGrantedAuthority(roleName);
    }

    @Override
    public void removeGroupAuthority(String groupName, GrantedAuthority authority) {
        this.logger.debug((Object)("Removing authority '" + authority + "' from group '" + groupName + "'"));
        Assert.hasText((String)groupName, (String)"groupName should have text");
        Assert.notNull((Object)authority, (String)"authority cannot be null");
        int id = this.findGroupId(groupName);
        this.getJdbcTemplate().update(this.deleteGroupAuthoritySql, ps -> {
            ps.setInt(1, id);
            ps.setString(2, authority.getAuthority());
        });
    }

    @Override
    public void addGroupAuthority(String groupName, GrantedAuthority authority) {
        this.logger.debug((Object)("Adding authority '" + authority + "' to group '" + groupName + "'"));
        Assert.hasText((String)groupName, (String)"groupName should have text");
        Assert.notNull((Object)authority, (String)"authority cannot be null");
        int id = this.findGroupId(groupName);
        this.getJdbcTemplate().update(this.insertGroupAuthoritySql, ps -> {
            ps.setInt(1, id);
            ps.setString(2, authority.getAuthority());
        });
    }

    private int findGroupId(String group) {
        return (Integer)this.getJdbcTemplate().queryForObject(this.findGroupIdSql, Integer.class, new Object[]{group});
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setCreateUserSql(String createUserSql) {
        Assert.hasText((String)createUserSql, (String)"createUserSql should have text");
        this.createUserSql = createUserSql;
    }

    public void setDeleteUserSql(String deleteUserSql) {
        Assert.hasText((String)deleteUserSql, (String)"deleteUserSql should have text");
        this.deleteUserSql = deleteUserSql;
    }

    public void setUpdateUserSql(String updateUserSql) {
        Assert.hasText((String)updateUserSql, (String)"updateUserSql should have text");
        this.updateUserSql = updateUserSql;
    }

    public void setCreateAuthoritySql(String createAuthoritySql) {
        Assert.hasText((String)createAuthoritySql, (String)"createAuthoritySql should have text");
        this.createAuthoritySql = createAuthoritySql;
    }

    public void setDeleteUserAuthoritiesSql(String deleteUserAuthoritiesSql) {
        Assert.hasText((String)deleteUserAuthoritiesSql, (String)"deleteUserAuthoritiesSql should have text");
        this.deleteUserAuthoritiesSql = deleteUserAuthoritiesSql;
    }

    public void setUserExistsSql(String userExistsSql) {
        Assert.hasText((String)userExistsSql, (String)"userExistsSql should have text");
        this.userExistsSql = userExistsSql;
    }

    public void setChangePasswordSql(String changePasswordSql) {
        Assert.hasText((String)changePasswordSql, (String)"changePasswordSql should have text");
        this.changePasswordSql = changePasswordSql;
    }

    public void setFindAllGroupsSql(String findAllGroupsSql) {
        Assert.hasText((String)findAllGroupsSql, (String)"findAllGroupsSql should have text");
        this.findAllGroupsSql = findAllGroupsSql;
    }

    public void setFindUsersInGroupSql(String findUsersInGroupSql) {
        Assert.hasText((String)findUsersInGroupSql, (String)"findUsersInGroupSql should have text");
        this.findUsersInGroupSql = findUsersInGroupSql;
    }

    public void setInsertGroupSql(String insertGroupSql) {
        Assert.hasText((String)insertGroupSql, (String)"insertGroupSql should have text");
        this.insertGroupSql = insertGroupSql;
    }

    public void setFindGroupIdSql(String findGroupIdSql) {
        Assert.hasText((String)findGroupIdSql, (String)"findGroupIdSql should have text");
        this.findGroupIdSql = findGroupIdSql;
    }

    public void setInsertGroupAuthoritySql(String insertGroupAuthoritySql) {
        Assert.hasText((String)insertGroupAuthoritySql, (String)"insertGroupAuthoritySql should have text");
        this.insertGroupAuthoritySql = insertGroupAuthoritySql;
    }

    public void setDeleteGroupSql(String deleteGroupSql) {
        Assert.hasText((String)deleteGroupSql, (String)"deleteGroupSql should have text");
        this.deleteGroupSql = deleteGroupSql;
    }

    public void setDeleteGroupAuthoritiesSql(String deleteGroupAuthoritiesSql) {
        Assert.hasText((String)deleteGroupAuthoritiesSql, (String)"deleteGroupAuthoritiesSql should have text");
        this.deleteGroupAuthoritiesSql = deleteGroupAuthoritiesSql;
    }

    public void setDeleteGroupMembersSql(String deleteGroupMembersSql) {
        Assert.hasText((String)deleteGroupMembersSql, (String)"deleteGroupMembersSql should have text");
        this.deleteGroupMembersSql = deleteGroupMembersSql;
    }

    public void setRenameGroupSql(String renameGroupSql) {
        Assert.hasText((String)renameGroupSql, (String)"renameGroupSql should have text");
        this.renameGroupSql = renameGroupSql;
    }

    public void setInsertGroupMemberSql(String insertGroupMemberSql) {
        Assert.hasText((String)insertGroupMemberSql, (String)"insertGroupMemberSql should have text");
        this.insertGroupMemberSql = insertGroupMemberSql;
    }

    public void setDeleteGroupMemberSql(String deleteGroupMemberSql) {
        Assert.hasText((String)deleteGroupMemberSql, (String)"deleteGroupMemberSql should have text");
        this.deleteGroupMemberSql = deleteGroupMemberSql;
    }

    public void setGroupAuthoritiesSql(String groupAuthoritiesSql) {
        Assert.hasText((String)groupAuthoritiesSql, (String)"groupAuthoritiesSql should have text");
        this.groupAuthoritiesSql = groupAuthoritiesSql;
    }

    public void setDeleteGroupAuthoritySql(String deleteGroupAuthoritySql) {
        Assert.hasText((String)deleteGroupAuthoritySql, (String)"deleteGroupAuthoritySql should have text");
        this.deleteGroupAuthoritySql = deleteGroupAuthoritySql;
    }

    public void setUserCache(UserCache userCache) {
        Assert.notNull((Object)userCache, (String)"userCache cannot be null");
        this.userCache = userCache;
    }

    private void validateUserDetails(UserDetails user) {
        Assert.hasText((String)user.getUsername(), (String)"Username may not be empty or null");
        this.validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, (String)"Authorities list must not be null");
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull((Object)grantedAuthority, (String)"Authorities list contains a null entry");
            Assert.hasText((String)grantedAuthority.getAuthority(), (String)"getAuthority() method must return a non-empty string");
        }
    }
}


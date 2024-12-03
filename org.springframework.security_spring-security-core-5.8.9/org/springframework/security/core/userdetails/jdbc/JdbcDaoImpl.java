/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.jdbc.core.RowMapper
 *  org.springframework.jdbc.core.support.JdbcDaoSupport
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.userdetails.jdbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class JdbcDaoImpl
extends JdbcDaoSupport
implements UserDetailsService,
MessageSourceAware {
    public static final String DEFAULT_USER_SCHEMA_DDL_LOCATION = "org/springframework/security/core/userdetails/jdbc/users.ddl";
    public static final String DEF_USERS_BY_USERNAME_QUERY = "select username,password,enabled from users where username = ?";
    public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = "select username,authority from authorities where username = ?";
    public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY = "select g.id, g.group_name, ga.authority from groups g, group_members gm, group_authorities ga where gm.username = ? and g.id = ga.group_id and g.id = gm.group_id";
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String authoritiesByUsernameQuery = "select username,authority from authorities where username = ?";
    private String groupAuthoritiesByUsernameQuery = "select g.id, g.group_name, ga.authority from groups g, group_members gm, group_authorities ga where gm.username = ? and g.id = ga.group_id and g.id = gm.group_id";
    private String usersByUsernameQuery = "select username,password,enabled from users where username = ?";
    private String rolePrefix = "";
    private boolean usernameBasedPrimaryKey = true;
    private boolean enableAuthorities = true;
    private boolean enableGroups;

    protected MessageSourceAccessor getMessages() {
        return this.messages;
    }

    protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
    }

    public String getUsersByUsernameQuery() {
        return this.usersByUsernameQuery;
    }

    protected void initDao() throws ApplicationContextException {
        Assert.isTrue((this.enableAuthorities || this.enableGroups ? 1 : 0) != 0, (String)"Use of either authorities or groups must be enabled");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetails> users = this.loadUsersByUsername(username);
        if (users.size() == 0) {
            this.logger.debug((Object)("Query returned no results for user '" + username + "'"));
            throw new UsernameNotFoundException(this.messages.getMessage("JdbcDaoImpl.notFound", new Object[]{username}, "Username {0} not found"));
        }
        UserDetails user = users.get(0);
        HashSet<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();
        if (this.enableAuthorities) {
            dbAuthsSet.addAll(this.loadUserAuthorities(user.getUsername()));
        }
        if (this.enableGroups) {
            dbAuthsSet.addAll(this.loadGroupAuthorities(user.getUsername()));
        }
        ArrayList<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>(dbAuthsSet);
        this.addCustomAuthorities(user.getUsername(), dbAuths);
        if (dbAuths.size() == 0) {
            this.logger.debug((Object)("User '" + username + "' has no authorities and will be treated as 'not found'"));
            throw new UsernameNotFoundException(this.messages.getMessage("JdbcDaoImpl.noAuthority", new Object[]{username}, "User {0} has no GrantedAuthority"));
        }
        return this.createUserDetails(username, user, dbAuths);
    }

    protected List<UserDetails> loadUsersByUsername(String username) {
        RowMapper mapper = (rs, rowNum) -> {
            String username1 = rs.getString(1);
            String password = rs.getString(2);
            boolean enabled = rs.getBoolean(3);
            return new User(username1, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        };
        return this.getJdbcTemplate().query(this.usersByUsernameQuery, mapper, new Object[]{username});
    }

    protected List<GrantedAuthority> loadUserAuthorities(String username) {
        return this.getJdbcTemplate().query(this.authoritiesByUsernameQuery, (Object[])new String[]{username}, (rs, rowNum) -> {
            String roleName = this.rolePrefix + rs.getString(2);
            return new SimpleGrantedAuthority(roleName);
        });
    }

    protected List<GrantedAuthority> loadGroupAuthorities(String username) {
        return this.getJdbcTemplate().query(this.groupAuthoritiesByUsernameQuery, (Object[])new String[]{username}, (rs, rowNum) -> {
            String roleName = this.getRolePrefix() + rs.getString(3);
            return new SimpleGrantedAuthority(roleName);
        });
    }

    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();
        if (!this.usernameBasedPrimaryKey) {
            returnUsername = username;
        }
        return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(), userFromUserQuery.isAccountNonExpired(), userFromUserQuery.isCredentialsNonExpired(), userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
    }

    public void setAuthoritiesByUsernameQuery(String queryString) {
        this.authoritiesByUsernameQuery = queryString;
    }

    protected String getAuthoritiesByUsernameQuery() {
        return this.authoritiesByUsernameQuery;
    }

    public void setGroupAuthoritiesByUsernameQuery(String queryString) {
        this.groupAuthoritiesByUsernameQuery = queryString;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    protected String getRolePrefix() {
        return this.rolePrefix;
    }

    public void setUsernameBasedPrimaryKey(boolean usernameBasedPrimaryKey) {
        this.usernameBasedPrimaryKey = usernameBasedPrimaryKey;
    }

    protected boolean isUsernameBasedPrimaryKey() {
        return this.usernameBasedPrimaryKey;
    }

    public void setUsersByUsernameQuery(String usersByUsernameQueryString) {
        this.usersByUsernameQuery = usersByUsernameQueryString;
    }

    protected boolean getEnableAuthorities() {
        return this.enableAuthorities;
    }

    public void setEnableAuthorities(boolean enableAuthorities) {
        this.enableAuthorities = enableAuthorities;
    }

    protected boolean getEnableGroups() {
        return this.enableGroups;
    }

    public void setEnableGroups(boolean enableGroups) {
        this.enableGroups = enableGroups;
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull((Object)messageSource, (String)"messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }
}


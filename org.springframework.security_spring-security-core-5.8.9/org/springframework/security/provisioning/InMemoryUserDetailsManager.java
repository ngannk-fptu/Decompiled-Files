/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.provisioning;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.security.provisioning.MutableUser;
import org.springframework.security.provisioning.MutableUserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

public class InMemoryUserDetailsManager
implements UserDetailsManager,
UserDetailsPasswordService {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<String, MutableUserDetails> users = new HashMap<String, MutableUserDetails>();
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private AuthenticationManager authenticationManager;

    public InMemoryUserDetailsManager() {
    }

    public InMemoryUserDetailsManager(Collection<UserDetails> users) {
        for (UserDetails user : users) {
            this.createUser(user);
        }
    }

    public InMemoryUserDetailsManager(UserDetails ... users) {
        for (UserDetails user : users) {
            this.createUser(user);
        }
    }

    public InMemoryUserDetailsManager(Properties users) {
        Enumeration<?> names = users.propertyNames();
        UserAttributeEditor editor = new UserAttributeEditor();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            editor.setAsText(users.getProperty(name));
            UserAttribute attr = (UserAttribute)editor.getValue();
            Assert.notNull((Object)attr, () -> "The entry with username '" + name + "' could not be converted to an UserDetails");
            this.createUser(this.createUserDetails(name, attr));
        }
    }

    private User createUserDetails(String name, UserAttribute attr) {
        return new User(name, attr.getPassword(), attr.isEnabled(), true, true, true, attr.getAuthorities());
    }

    @Override
    public void createUser(UserDetails user) {
        Assert.isTrue((!this.userExists(user.getUsername()) ? 1 : 0) != 0, (String)"user should not exist");
        this.users.put(user.getUsername().toLowerCase(), new MutableUser(user));
    }

    @Override
    public void deleteUser(String username) {
        this.users.remove(username.toLowerCase());
    }

    @Override
    public void updateUser(UserDetails user) {
        Assert.isTrue((boolean)this.userExists(user.getUsername()), (String)"user should exist");
        this.users.put(user.getUsername().toLowerCase(), new MutableUser(user));
    }

    @Override
    public boolean userExists(String username) {
        return this.users.containsKey(username.toLowerCase());
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        }
        String username = currentUser.getName();
        this.logger.debug((Object)LogMessage.format((String)"Changing password for user '%s'", (Object)username));
        if (this.authenticationManager != null) {
            this.logger.debug((Object)LogMessage.format((String)"Reauthenticating user '%s' for password change request.", (Object)username));
            this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        } else {
            this.logger.debug((Object)"No authentication manager set. Password won't be re-checked.");
        }
        MutableUserDetails user = this.users.get(username);
        Assert.state((user != null ? 1 : 0) != 0, (String)"Current user doesn't exist in database.");
        user.setPassword(newPassword);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String username = user.getUsername();
        MutableUserDetails mutableUser = this.users.get(username.toLowerCase());
        mutableUser.setPassword(newPassword);
        return mutableUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = this.users.get(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}


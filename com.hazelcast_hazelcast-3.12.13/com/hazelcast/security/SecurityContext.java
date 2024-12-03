/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.config.PermissionConfig;
import com.hazelcast.security.Credentials;
import com.hazelcast.security.ICredentialsFactory;
import com.hazelcast.security.SecureCallable;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public interface SecurityContext {
    public LoginContext createMemberLoginContext(Credentials var1) throws LoginException;

    public LoginContext createClientLoginContext(Credentials var1) throws LoginException;

    public ICredentialsFactory getCredentialsFactory();

    public void checkPermission(Subject var1, Permission var2) throws AccessControlException;

    public void interceptBefore(Credentials var1, String var2, String var3, String var4, Object[] var5) throws AccessControlException;

    public void interceptAfter(Credentials var1, String var2, String var3, String var4);

    public <V> SecureCallable<V> createSecureCallable(Subject var1, Callable<V> var2);

    public void destroy();

    public void refreshPermissions(Set<PermissionConfig> var1);
}


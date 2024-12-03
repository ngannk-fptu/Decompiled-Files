/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api;

import java.util.Map;
import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface JackrabbitRepository
extends Repository {
    public static final String OPTION_USER_MANAGEMENT_SUPPORTED = "option.user.management.supported";
    public static final String OPTION_PRINCIPAL_MANAGEMENT_SUPPORTED = "option.principal.management.supported";
    public static final String OPTION_PRIVILEGE_MANAGEMENT_SUPPORTED = "option.privilege.management.supported";

    public Session login(Credentials var1, String var2, Map<String, Object> var3) throws LoginException, NoSuchWorkspaceException, RepositoryException;

    public void shutdown();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import org.apache.jackrabbit.api.security.authorization.PrivilegeManager;
import org.xml.sax.InputSource;

public interface JackrabbitWorkspace
extends Workspace {
    @Override
    public void createWorkspace(String var1) throws AccessDeniedException, RepositoryException;

    public void createWorkspace(String var1, InputSource var2) throws AccessDeniedException, RepositoryException;

    public PrivilegeManager getPrivilegeManager() throws RepositoryException;
}


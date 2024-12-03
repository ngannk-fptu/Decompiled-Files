/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

public interface CopyMoveContext {
    public boolean isShallowCopy();

    public Session getSession();

    public Workspace getWorkspace() throws RepositoryException;
}


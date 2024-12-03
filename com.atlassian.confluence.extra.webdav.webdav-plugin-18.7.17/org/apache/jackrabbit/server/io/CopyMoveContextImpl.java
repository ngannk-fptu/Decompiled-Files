/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import org.apache.jackrabbit.server.io.CopyMoveContext;

public class CopyMoveContextImpl
implements CopyMoveContext {
    private final boolean isShallow;
    private final Session session;

    public CopyMoveContextImpl(Session session) {
        this(session, false);
    }

    public CopyMoveContextImpl(Session session, boolean isShallowCopy) {
        this.isShallow = isShallowCopy;
        this.session = session;
    }

    @Override
    public boolean isShallowCopy() {
        return this.isShallow;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public Workspace getWorkspace() throws RepositoryException {
        return this.session.getWorkspace();
    }
}


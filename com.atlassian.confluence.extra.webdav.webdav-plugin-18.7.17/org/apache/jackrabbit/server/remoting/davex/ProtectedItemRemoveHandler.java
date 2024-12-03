/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.remoting.davex;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface ProtectedItemRemoveHandler {
    public boolean remove(Session var1, String var2) throws RepositoryException;
}


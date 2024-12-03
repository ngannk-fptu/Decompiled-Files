/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.simple;

import java.util.HashSet;
import javax.jcr.Session;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;

public class DavSessionImpl
extends JcrDavSession {
    private final HashSet<String> lockTokens = new HashSet();

    public DavSessionImpl(Session session) {
        super(session);
    }

    @Override
    public void addReference(Object reference) {
        throw new UnsupportedOperationException("No yet implemented.");
    }

    @Override
    public void removeReference(Object reference) {
        throw new UnsupportedOperationException("No yet implemented.");
    }

    @Override
    public void addLockToken(String token) {
        super.addLockToken(token);
        this.lockTokens.add(token);
    }

    @Override
    public String[] getLockTokens() {
        return this.lockTokens.toArray(new String[this.lockTokens.size()]);
    }

    @Override
    public void removeLockToken(String token) {
        super.removeLockToken(token);
        this.lockTokens.remove(token);
    }
}


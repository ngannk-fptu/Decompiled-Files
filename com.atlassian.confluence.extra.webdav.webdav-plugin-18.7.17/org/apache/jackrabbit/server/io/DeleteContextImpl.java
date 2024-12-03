/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import javax.jcr.Session;
import org.apache.jackrabbit.server.io.DeleteContext;

public class DeleteContextImpl
implements DeleteContext {
    private final Session session;

    public DeleteContextImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return this.session;
    }
}


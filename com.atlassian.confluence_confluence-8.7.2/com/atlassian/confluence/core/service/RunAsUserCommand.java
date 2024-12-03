/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;

public class RunAsUserCommand
implements ServiceCommand {
    private final ServiceCommand delegate;
    private final ConfluenceUser userToRunAs;

    public RunAsUserCommand(ConfluenceUser userToRunAs, ServiceCommand delegate) {
        this.userToRunAs = userToRunAs;
        this.delegate = delegate;
    }

    @Override
    public boolean isValid() {
        ConfluenceUser oldUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(this.userToRunAs);
            boolean bl = this.delegate.isValid();
            return bl;
        }
        finally {
            AuthenticatedUserThreadLocal.set(oldUser);
        }
    }

    public Collection getValidationErrors() {
        return this.delegate.getValidationErrors();
    }

    @Override
    public boolean isAuthorized() {
        ConfluenceUser oldUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(this.userToRunAs);
            boolean bl = this.delegate.isAuthorized();
            return bl;
        }
        finally {
            AuthenticatedUserThreadLocal.set(oldUser);
        }
    }

    @Override
    public void execute() {
        ConfluenceUser oldUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(this.userToRunAs);
            this.delegate.execute();
        }
        finally {
            AuthenticatedUserThreadLocal.set(oldUser);
        }
    }
}


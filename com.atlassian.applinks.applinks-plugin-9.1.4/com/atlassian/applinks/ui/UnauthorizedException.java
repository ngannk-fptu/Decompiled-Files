/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.ui.RequestException;

public class UnauthorizedException
extends RequestException {
    public UnauthorizedException() {
        this((Message)null);
    }

    public UnauthorizedException(Message message) {
        super(401, message);
    }

    public UnauthorizedException(Message message, Throwable cause) {
        super(401, message, cause);
    }

    @Override
    public final String getTemplate() {
        return "com/atlassian/applinks/ui/no_admin_privileges.vm";
    }
}


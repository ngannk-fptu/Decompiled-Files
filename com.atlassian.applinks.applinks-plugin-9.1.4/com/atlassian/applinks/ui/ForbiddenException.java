/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.ui.RequestException;

public class ForbiddenException
extends RequestException {
    public ForbiddenException(Message message) {
        super(403, message);
    }
}


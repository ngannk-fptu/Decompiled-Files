/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ActiveObjectsPluginException;

public class ActiveObjectsInitException
extends ActiveObjectsPluginException {
    public ActiveObjectsInitException(String msg) {
        super(msg);
    }

    public ActiveObjectsInitException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


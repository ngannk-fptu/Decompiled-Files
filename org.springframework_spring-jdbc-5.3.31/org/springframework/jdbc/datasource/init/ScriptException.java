/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.datasource.init;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

public abstract class ScriptException
extends DataAccessException {
    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}


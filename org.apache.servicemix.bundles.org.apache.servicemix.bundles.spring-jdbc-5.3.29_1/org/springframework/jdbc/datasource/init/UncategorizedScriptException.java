/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.init;

import org.springframework.jdbc.datasource.init.ScriptException;

public class UncategorizedScriptException
extends ScriptException {
    public UncategorizedScriptException(String message) {
        super(message);
    }

    public UncategorizedScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.support.EncodedResource
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.datasource.init;

import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.lang.Nullable;

public class ScriptParseException
extends ScriptException {
    public ScriptParseException(String message, @Nullable EncodedResource resource) {
        super(ScriptParseException.buildMessage(message, resource));
    }

    public ScriptParseException(String message, @Nullable EncodedResource resource, @Nullable Throwable cause) {
        super(ScriptParseException.buildMessage(message, resource), cause);
    }

    private static String buildMessage(String message, @Nullable EncodedResource resource) {
        return String.format("Failed to parse SQL script from resource [%s]: %s", resource == null ? "<unknown>" : resource, message);
    }
}


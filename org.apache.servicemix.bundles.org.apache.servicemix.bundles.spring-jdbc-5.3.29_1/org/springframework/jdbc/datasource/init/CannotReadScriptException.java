/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.support.EncodedResource
 */
package org.springframework.jdbc.datasource.init;

import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptException;

public class CannotReadScriptException
extends ScriptException {
    public CannotReadScriptException(EncodedResource resource, Throwable cause) {
        super("Cannot read SQL script from " + resource, cause);
    }
}


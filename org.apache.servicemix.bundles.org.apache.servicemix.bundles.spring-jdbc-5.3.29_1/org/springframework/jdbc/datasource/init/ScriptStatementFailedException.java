/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.support.EncodedResource
 */
package org.springframework.jdbc.datasource.init;

import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptException;

public class ScriptStatementFailedException
extends ScriptException {
    public ScriptStatementFailedException(String stmt, int stmtNumber, EncodedResource encodedResource, Throwable cause) {
        super(ScriptStatementFailedException.buildErrorMessage(stmt, stmtNumber, encodedResource), cause);
    }

    public static String buildErrorMessage(String stmt, int stmtNumber, EncodedResource encodedResource) {
        return String.format("Failed to execute SQL script statement #%s of %s: %s", stmtNumber, encodedResource, stmt);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.plugin;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.plugin.AuthenticationRequestType;
import org.postgresql.util.PSQLException;

public interface AuthenticationPlugin {
    public char @Nullable [] getPassword(AuthenticationRequestType var1) throws PSQLException;
}


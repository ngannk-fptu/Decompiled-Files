/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.api.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import java.sql.Connection;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface DatabaseConnectionConverter {
    public DatabaseConnection convert(Connection var1);

    public DatabaseConnection convertExternallyManaged(Connection var1);
}


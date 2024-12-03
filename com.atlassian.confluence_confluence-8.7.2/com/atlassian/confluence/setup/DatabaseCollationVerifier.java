/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.setup.DatabaseVerificationResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface DatabaseCollationVerifier {
    public Optional<DatabaseVerificationResult> verifyCollationOfDatabase(Connection var1, String var2, String[] var3, String var4) throws SQLException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.hibernate.ScrollMode;

public interface StatementPreparer {
    public Statement createStatement();

    public PreparedStatement prepareStatement(String var1);

    public PreparedStatement prepareStatement(String var1, boolean var2);

    public PreparedStatement prepareStatement(String var1, int var2);

    public PreparedStatement prepareStatement(String var1, String[] var2);

    public PreparedStatement prepareQueryStatement(String var1, boolean var2, ScrollMode var3);
}


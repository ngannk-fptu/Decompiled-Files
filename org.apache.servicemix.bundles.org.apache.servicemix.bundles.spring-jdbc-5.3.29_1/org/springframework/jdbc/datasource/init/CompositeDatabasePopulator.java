/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.util.Assert;

public class CompositeDatabasePopulator
implements DatabasePopulator {
    private final List<DatabasePopulator> populators = new ArrayList<DatabasePopulator>(4);

    public CompositeDatabasePopulator() {
    }

    public CompositeDatabasePopulator(Collection<DatabasePopulator> populators) {
        Assert.notNull(populators, (String)"DatabasePopulators must not be null");
        this.populators.addAll(populators);
    }

    public CompositeDatabasePopulator(DatabasePopulator ... populators) {
        Assert.notNull((Object)populators, (String)"DatabasePopulators must not be null");
        this.populators.addAll(Arrays.asList(populators));
    }

    public void setPopulators(DatabasePopulator ... populators) {
        Assert.notNull((Object)populators, (String)"DatabasePopulators must not be null");
        this.populators.clear();
        this.populators.addAll(Arrays.asList(populators));
    }

    public void addPopulators(DatabasePopulator ... populators) {
        Assert.notNull((Object)populators, (String)"DatabasePopulators must not be null");
        this.populators.addAll(Arrays.asList(populators));
    }

    @Override
    public void populate(Connection connection) throws SQLException, ScriptException {
        Assert.notNull((Object)connection, (String)"Connection must not be null");
        for (DatabasePopulator populator : this.populators) {
            populator.populate(connection);
        }
    }
}


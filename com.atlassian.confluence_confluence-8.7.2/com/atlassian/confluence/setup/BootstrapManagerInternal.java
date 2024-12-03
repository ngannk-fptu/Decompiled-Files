/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.DatabaseDetails
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.setup.BootstrapManager;
import java.util.Optional;

public interface BootstrapManagerInternal
extends BootstrapManager {
    public Optional<DatabaseDetails> getDatabaseDetail(String var1) throws ConfigurationException;

    public boolean performPersistenceUpgrade();
}


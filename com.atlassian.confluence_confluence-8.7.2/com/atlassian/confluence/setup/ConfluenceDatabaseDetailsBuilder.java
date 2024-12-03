/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.DatabaseDetails
 *  org.apache.commons.beanutils.BeanUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

@Internal
public class ConfluenceDatabaseDetailsBuilder {
    private static final String DEFAULT_DATABASE_TYPE = "postgresql";
    private String databaseType = "postgresql";

    public ConfluenceDatabaseDetailsBuilder databaseType(String databaseType) {
        if (!StringUtils.isEmpty((CharSequence)databaseType)) {
            this.databaseType = databaseType;
        }
        return this;
    }

    public ConfluenceDatabaseDetails build() {
        try {
            DatabaseDetails databaseDetails = DatabaseDetails.getDefaults((String)this.databaseType);
            ConfluenceDatabaseDetails confluenceDatabaseDetails = new ConfluenceDatabaseDetails();
            BeanUtils.copyProperties((Object)((Object)confluenceDatabaseDetails), (Object)databaseDetails);
            confluenceDatabaseDetails.setDatabaseType(this.databaseType);
            confluenceDatabaseDetails.setDatabaseUrl("");
            confluenceDatabaseDetails.setupForDatabase(this.databaseType);
            return confluenceDatabaseDetails;
        }
        catch (ConfigurationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to setup database configuration", e);
        }
    }

    public ConfluenceDatabaseDetails build(DatabaseDetails databaseDetails) {
        try {
            ConfluenceDatabaseDetails confluenceDatabaseDetails = new ConfluenceDatabaseDetails();
            BeanUtils.copyProperties((Object)((Object)confluenceDatabaseDetails), (Object)databaseDetails);
            confluenceDatabaseDetails.setSimple(false);
            confluenceDatabaseDetails.setDatabaseType(this.databaseType);
            confluenceDatabaseDetails.setupForDatabase(this.databaseType);
            return confluenceDatabaseDetails;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to setup database configuration", e);
        }
    }
}


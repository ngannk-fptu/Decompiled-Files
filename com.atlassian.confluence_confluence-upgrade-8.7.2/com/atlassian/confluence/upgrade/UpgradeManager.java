/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.JohnsonEventContainer
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.PluginExportCompatibility;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.johnson.JohnsonEventContainer;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface UpgradeManager {
    public static final int MINIMUM_SUPPORTED_UPGRADE_BUILD_NUMBER = 7103;
    public static final String MINIMUM_SUPPORTED_UPGRADE_VERSION = "6.0.5";

    public void upgrade(JohnsonEventContainer var1) throws UpgradeException;

    public List<UpgradeError> getErrors();

    public boolean needUpgrade();

    public boolean isUpgraded();

    public boolean configuredBuildNumberNewerThan(String var1);

    public boolean taskNewerThan(String var1, UpgradeTask var2);

    public void entireUpgradeFinished();

    public String getOldestSpaceImportAllowed();

    public String getExportBuildNumber(boolean var1);

    public @NonNull Map<String, PluginExportCompatibility> getPluginExportCompatibility(boolean var1);

    public void setDatabaseBuildNumber();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import java.text.MessageFormat;

public class SettingsMigrationException
extends MigrationException {
    private static final MessageFormat MESSAGE = new MessageFormat("Failed to migrate the wiki content for the '{0}' setting");
    private final String settingName;

    public SettingsMigrationException(String settingName) {
        super(MESSAGE.format(new String[]{settingName}));
        this.settingName = settingName;
    }

    public SettingsMigrationException(String settingName, Throwable cause) {
        super(MESSAGE.format(new String[]{settingName}), cause);
        this.settingName = settingName;
    }

    public String getSettingName() {
        return this.settingName;
    }
}


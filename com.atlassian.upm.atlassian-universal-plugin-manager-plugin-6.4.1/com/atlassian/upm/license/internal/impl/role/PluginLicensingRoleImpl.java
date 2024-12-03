/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import java.net.URI;

public class PluginLicensingRoleImpl
implements PluginLicensingRole {
    private final String key;
    private final String nameI18nKey;
    private final String descriptionI18nKey;
    private final String singularI18nKey;
    private final String pluralI18nKey;
    private final URI managementPage;
    private final int roleCount;

    public PluginLicensingRoleImpl(String key, String nameI18nKey, String descriptionI18nKey, String singularI18nKey, String pluralI18nKey, URI managementPage, int roleCount) {
        this.key = key;
        this.nameI18nKey = nameI18nKey;
        this.descriptionI18nKey = descriptionI18nKey;
        this.singularI18nKey = singularI18nKey;
        this.pluralI18nKey = pluralI18nKey;
        this.managementPage = managementPage;
        this.roleCount = roleCount;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getNameI18nKey() {
        return this.nameI18nKey;
    }

    @Override
    public String getDescriptionI18nKey() {
        return this.descriptionI18nKey;
    }

    @Override
    public String getSingularI18nKey() {
        return this.singularI18nKey;
    }

    @Override
    public String getPluralI18nKey() {
        return this.pluralI18nKey;
    }

    @Override
    public URI getManagementPage() {
        return this.managementPage;
    }

    @Override
    public int getRoleCount() {
        return this.roleCount;
    }
}


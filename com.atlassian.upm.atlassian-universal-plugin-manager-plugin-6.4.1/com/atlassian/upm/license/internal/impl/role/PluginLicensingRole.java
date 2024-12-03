/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.impl.role;

import java.net.URI;

public interface PluginLicensingRole {
    public String getKey();

    public String getNameI18nKey();

    public String getDescriptionI18nKey();

    public String getSingularI18nKey();

    public String getPluralI18nKey();

    public URI getManagementPage();

    public int getRoleCount();
}


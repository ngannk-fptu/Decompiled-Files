/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.sso;

import com.atlassian.crowd.model.sso.NameIdFormat;

public interface BaseApplicationSamlConfiguration {
    public String getAudienceUrl();

    public String getAssertionConsumerUrl();

    public NameIdFormat getNameIdFormat();

    public boolean isAddUserAttributesEnabled();
}


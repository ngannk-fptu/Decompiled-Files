/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.persistence.dao;

import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import java.util.Collection;

public interface TrustedApplicationDao {
    public ConfluenceTrustedApplication findById(long var1);

    public ConfluenceTrustedApplication findByKeyAlias(String var1);

    public ConfluenceTrustedApplication findByName(String var1);

    public Collection<ConfluenceTrustedApplication> findAll();

    public void saveHibernateTrustedApplication(ConfluenceTrustedApplication var1);

    public void deleteHibernateTrustedApplication(ConfluenceTrustedApplication var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.dao.sso;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.sso.ApplicationSamlConfigurationEntity;
import java.util.List;
import java.util.Optional;

public interface ApplicationSamlConfigurationDAO {
    public Optional<ApplicationSamlConfigurationEntity> loadForApplication(Application var1);

    public Optional<ApplicationSamlConfigurationEntity> findByAssertionConsumerAndAudience(String var1, String var2);

    public void save(ApplicationSamlConfigurationEntity var1) throws ApplicationNotFoundException;

    public List<ApplicationSamlConfigurationEntity> findAll();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.sso;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.sso.InvalidApplicationSamlConfigurationException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.sso.ApplicationSamlConfiguration;
import com.atlassian.crowd.model.sso.BaseApplicationSamlConfiguration;
import java.io.InputStream;
import java.util.Optional;

public interface ApplicationSamlConfigurationService {
    public Optional<ApplicationSamlConfiguration> loadConfigurationForApplication(Application var1);

    public void storeApplicationConfiguration(ApplicationSamlConfiguration var1) throws InvalidApplicationSamlConfigurationException, OperationFailedException;

    public Optional<ApplicationSamlConfiguration> findByAssertionConsumerAndAudience(String var1, String var2);

    public BaseApplicationSamlConfiguration parseApplicationMetadata(InputStream var1);
}


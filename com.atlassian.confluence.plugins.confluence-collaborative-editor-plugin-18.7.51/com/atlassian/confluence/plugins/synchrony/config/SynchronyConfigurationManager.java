/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.synchrony.config;

public interface SynchronyConfigurationManager {
    public static final String SYNCHRONY_SERVICE_URL_SYS_PROP = "synchrony.service.url";
    public static final String SYNCHRONY_LOCAL_SERVICE_URL_SYS_PROP = "synchrony.local.service.url";

    public String getExternalServiceUrl();

    public String getExternalBaseUrl();

    public void setExternalBaseUrl(String var1);

    public String getInternalServiceUrl();

    public void setInternalBaseUrl(String var1);

    public void setInternalPort(int var1);

    public int getInternalPort();

    @Deprecated
    public String getResourcesUrl();

    public String getConfiguredAppID();

    public String getAppID();

    public void setAppId(String var1);

    public void setRegistrationComplete();

    public boolean isRegistrationComplete();

    public String getAppSecret();

    public void setAppSecret(String var1);

    public String generateAppID();

    public String generateAppSecret();

    public boolean isDebug();

    public boolean isUsingLocalSynchrony();

    @Deprecated
    public boolean isSynchronyEnabled();

    public boolean isSharedDraftsEnabled();

    @Deprecated
    public boolean isSynchronyExplicitlyDisabled();

    public boolean isSharedDraftsExplicitlyDisabled();

    public boolean isSynchronyProdOverrideEnabled();

    public boolean isSynchronyEncryptionEnabled();

    @Deprecated
    public void enableSynchrony();

    @Deprecated
    public void disableSynchrony();

    public int postConfigToSynchrony(String var1, String var2) throws Exception;

    public boolean removeSynchronyCredentials();

    public boolean registerWithSynchrony();

    public void setPassphrase(String var1);

    public String getPassphrase();

    public String generatePassphrase();

    public boolean retrievePublicKey();

    public String getSynchronyPublicKey();

    public void setSynchronyPublicKey(String var1);

    public void generateStorePassphraseIfMissing();

    public void disableSharedDrafts();

    public void enableSharedDrafts();
}


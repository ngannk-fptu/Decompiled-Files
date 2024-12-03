/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 */
package com.atlassian.crowd.manager.property;

import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.manager.audit.AuditLogConfiguration;
import com.atlassian.crowd.manager.property.PropertyManagerException;
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.backup.BackupConfiguration;
import com.atlassian.crowd.model.lookandfeel.LookAndFeelConfiguration;
import com.atlassian.crowd.util.ImageInfo;
import com.atlassian.crowd.util.mail.SMTPServer;
import java.net.URI;
import java.security.Key;
import java.util.Optional;

public interface PropertyManager {
    public String getDeploymentTitle() throws PropertyManagerException;

    public void setDeploymentTitle(String var1);

    @Deprecated
    public String getDomain();

    @Deprecated
    public void setDomain(String var1);

    @Deprecated
    public boolean isSecureCookie();

    @Deprecated
    public void setSecureCookie(boolean var1);

    public void setCacheEnabled(boolean var1);

    public boolean isCacheEnabled();

    public long getSessionTime();

    public void setSessionTime(long var1);

    @Deprecated
    public SMTPServer getSMTPServer() throws PropertyManagerException;

    @Deprecated
    public void setSMTPServer(SMTPServer var1);

    public Key getDesEncryptionKey() throws PropertyManagerException;

    public void generateDesEncryptionKey() throws PropertyManagerException;

    @Deprecated
    public void setSMTPTemplate(String var1);

    @Deprecated
    public String getSMTPTemplate() throws PropertyManagerException;

    public void setCurrentLicenseResourceTotal(int var1);

    public int getCurrentLicenseResourceTotal();

    @Deprecated
    public void setNotificationEmail(String var1);

    @Deprecated
    public String getNotificationEmail() throws PropertyManagerException;

    @Deprecated
    public boolean isGzipEnabled() throws PropertyManagerException;

    @Deprecated
    public void setGzipEnabled(boolean var1);

    public Integer getBuildNumber() throws PropertyManagerException;

    public void setBuildNumber(Integer var1);

    public String getTrustedProxyServers() throws PropertyManagerException;

    public void setTrustedProxyServers(String var1);

    public void setAuditLogConfiguration(AuditLogConfiguration var1);

    public AuditLogConfiguration getAuditLogConfiguration();

    public boolean isUsingDatabaseTokenStorage() throws PropertyManagerException;

    public void setUsingDatabaseTokenStorage(boolean var1);

    public void removeProperty(String var1);

    public String getProperty(String var1) throws ObjectNotFoundException;

    public Optional<String> getOptionalProperty(String var1);

    public void setProperty(String var1, String var2);

    public boolean isIncludeIpAddressInValidationFactors();

    public void setIncludeIpAddressInValidationFactors(boolean var1);

    public boolean isUseWebAvatars();

    public void setUseWebAvatars(boolean var1);

    public CookieConfiguration getCookieConfiguration();

    public void setCookieConfiguration(CookieConfiguration var1);

    public String getString(String var1, String var2);

    public boolean getBoolean(String var1, boolean var2);

    public int getInt(String var1, int var2);

    public void setBaseUrl(URI var1);

    public URI getBaseUrl() throws PropertyManagerException;

    public Optional<Long> getPrivateKeyCertificatePairToSign();

    public void setPrivateKeyCertificateToSign(long var1);

    public BackupConfiguration getBackupConfiguration();

    public void saveBackupConfiguration(BackupConfiguration var1);

    public void setLookAndFeelConfiguration(LookAndFeelConfiguration var1, ImageInfo var2) throws PropertyManagerException;

    public Optional<LookAndFeelConfiguration> getLookAndFeelConfiguration() throws PropertyManagerException;

    public void removeLookAndFeelConfiguration() throws PropertyManagerException;

    public Optional<ImageInfo> getLogoImage() throws PropertyManagerException;
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.osuser.config.xml;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.impl.osuser.OSUAccessor;
import com.atlassian.user.impl.osuser.config.xml.DefaultOSUConfigurationHandler;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.xml.sax.helpers.DefaultHandler;

public interface OSUConfigurationLoader {
    public void load(InputStream var1) throws ConfigurationException;

    public DefaultHandler getOSUserConfigurationHandler();

    public void setOSUserConfigurationHandler(DefaultOSUConfigurationHandler var1);

    public void addProvider(String var1, Properties var2) throws ConfigurationException;

    public OSUAccessor getOSUAccessor();

    public List getCredentialProviders();
}


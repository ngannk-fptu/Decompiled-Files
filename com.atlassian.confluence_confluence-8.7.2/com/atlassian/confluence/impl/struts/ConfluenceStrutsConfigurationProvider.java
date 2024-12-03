/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.opensymphony.xwork2.config.Configuration
 *  com.opensymphony.xwork2.config.ConfigurationException
 *  com.opensymphony.xwork2.config.ConfigurationProvider
 *  com.opensymphony.xwork2.inject.ContainerBuilder
 *  com.opensymphony.xwork2.util.location.LocatableProperties
 *  com.opensymphony.xwork2.util.location.Location
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.config.DefaultSettings
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.config.DefaultSettings;

public class ConfluenceStrutsConfigurationProvider
implements ConfigurationProvider {
    private final ConfluenceStrutsSettings confluenceStrutsSettings;

    public ConfluenceStrutsConfigurationProvider(BootstrapManager bootstrapManager) {
        this(new ConfluenceStrutsSettings(bootstrapManager));
    }

    @VisibleForTesting
    ConfluenceStrutsConfigurationProvider(ConfluenceStrutsSettings confluenceStrutsSettings) {
        this.confluenceStrutsSettings = confluenceStrutsSettings;
    }

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void loadPackages() throws ConfigurationException {
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        this.confluenceStrutsSettings.list().forEachRemaining(name -> {
            String nameStr = (String)name;
            props.setProperty(nameStr, this.confluenceStrutsSettings.get(nameStr), (Object)Location.UNKNOWN);
        });
    }

    public static class ConfluenceStrutsSettings
    extends DefaultSettings {
        private static final Set<String> FILE_PATH_KEYS = Set.of("struts.multipart.saveDir");
        public static final Set<String> RUNTIME_KEYS = Set.of("struts.i18n.encoding", "struts.multipart.maxFileSize");
        private final BootstrapManager bootstrapManager;

        public ConfluenceStrutsSettings(BootstrapManager bootstrapManager) {
            this.bootstrapManager = bootstrapManager;
        }

        public String get(String aName) throws IllegalArgumentException {
            String setting;
            switch (aName) {
                case "struts.i18n.encoding": {
                    SettingsManager settingsManager;
                    String defaultEncoding;
                    setting = "UTF-8";
                    if (!ContainerManager.isContainerSetup() || !StringUtils.isNotEmpty((CharSequence)(defaultEncoding = (settingsManager = (SettingsManager)ContainerManager.getComponent((String)"settingsManager")).getGlobalSettings().getDefaultEncoding()))) break;
                    setting = defaultEncoding;
                    break;
                }
                case "struts.multipart.maxFileSize": {
                    setting = String.valueOf(((SettingsService)ContainerManager.getComponent((String)"apiSettingsService")).getGlobalSettings().getAttachmentMaxSizeBytes());
                    break;
                }
                default: {
                    setting = FILE_PATH_KEYS.contains(aName) ? this.bootstrapManager.getFilePathProperty(aName) : this.bootstrapManager.getProperty(aName).toString();
                }
            }
            return setting;
        }

        public Iterator list() {
            HashSet<String> keys = new HashSet<String>(this.bootstrapManager.getPropertyKeys());
            keys.addAll(RUNTIME_KEYS);
            return keys.iterator();
        }

        public Location getLocation(String name) {
            return Location.UNKNOWN;
        }
    }
}


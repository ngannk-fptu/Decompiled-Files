/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.impl.filestore.FileStoreFactory;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.util.Properties;

@Deprecated
public class BootstrapPropertiesProvider {
    private final Properties properties;

    @Deprecated
    public BootstrapPropertiesProvider(BootstrapManager bootstrapManager) {
        this.properties = BootstrapPropertiesProvider.createProperties(bootstrapManager, new FileStoreFactory(bootstrapManager).getConfluenceHome());
    }

    public BootstrapPropertiesProvider(BootstrapManager bootstrapManager, FilesystemPath confluenceHome) {
        this.properties = BootstrapPropertiesProvider.createProperties(bootstrapManager, confluenceHome);
    }

    private static Properties createProperties(BootstrapManager bootstrapManager, FilesystemPath confluenceHome) {
        Properties properties = new Properties();
        File home = confluenceHome.asJavaFile();
        File localHome = bootstrapManager.getLocalHome();
        for (String key : bootstrapManager.getPropertyKeys()) {
            Object propValue = bootstrapManager.getProperty(key);
            String value = propValue != null ? propValue.toString() : "";
            properties.setProperty(key, GeneralUtil.replaceConfluenceConstants(value, home, localHome));
        }
        properties.setProperty("confluence.home", home.getPath());
        return properties;
    }

    public Properties getProperties() {
        return this.properties;
    }
}


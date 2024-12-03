/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.tomcat.util.file.ConfigurationSource;

public class ConfigFileLoader {
    private static ConfigurationSource source;

    public static final ConfigurationSource getSource() {
        if (source == null) {
            return ConfigurationSource.DEFAULT;
        }
        return source;
    }

    public static final void setSource(ConfigurationSource source) {
        if (ConfigFileLoader.source == null) {
            ConfigFileLoader.source = source;
        }
    }

    private ConfigFileLoader() {
    }

    @Deprecated
    public static InputStream getInputStream(String location) throws IOException {
        return ConfigFileLoader.getSource().getResource(location).getInputStream();
    }

    @Deprecated
    public static URI getURI(String location) {
        return ConfigFileLoader.getSource().getURI(location);
    }
}


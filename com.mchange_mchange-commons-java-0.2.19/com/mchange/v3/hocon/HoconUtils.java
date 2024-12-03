/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.typesafe.config.Config
 *  com.typesafe.config.ConfigException$WrongType
 *  com.typesafe.config.ConfigFactory
 *  com.typesafe.config.ConfigMergeable
 */
package com.mchange.v3.hocon;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigMergeable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class HoconUtils {
    private static final String APPLICATION = "application";

    public static PropertiesConversion configToProperties(Config config) {
        Set set = config.entrySet();
        Properties properties = new Properties();
        HashSet<String> hashSet = new HashSet<String>();
        for (Map.Entry entry : set) {
            String string = (String)entry.getKey();
            String string2 = null;
            try {
                string2 = config.getString(string);
            }
            catch (ConfigException.WrongType wrongType) {
                hashSet.add(string);
            }
            if (string2 == null) continue;
            properties.setProperty(string, string2);
        }
        PropertiesConversion propertiesConversion = new PropertiesConversion();
        propertiesConversion.properties = properties;
        propertiesConversion.unrenderable = hashSet;
        return propertiesConversion;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Config applicationOrStandardSubstitute(ClassLoader classLoader) throws SubstituteNotAvailableException {
        String string = APPLICATION;
        Config config = null;
        String string2 = System.getProperty("config.resource");
        if (string2 != null) {
            string = string2;
        } else {
            string2 = System.getProperty("config.file");
            if (string2 != null) {
                File file = new File(string2);
                if (!file.exists()) throw new SubstituteNotAvailableException(String.format("Specified config.file '%s' (specified as a System property) does not exist.", file.getAbsolutePath()));
                if (!file.canRead()) throw new SubstituteNotAvailableException(String.format("config.file '%s' (specified as a System property) is not readable.", file.getAbsolutePath()));
                config = ConfigFactory.parseFile((File)file);
            } else {
                string2 = System.getProperty("config.url");
                if (string2 != null) {
                    try {
                        config = ConfigFactory.parseURL((URL)new URL(string2));
                    }
                    catch (MalformedURLException malformedURLException) {
                        throw new SubstituteNotAvailableException(String.format("Specified config.url '%s' (specified as a System property) could not be parsed.", string2));
                    }
                }
            }
        }
        if (config != null) return config;
        return ConfigFactory.parseResourcesAnySyntax((ClassLoader)classLoader, (String)string);
    }

    public static ConfigWithFallbackMessage applicationOrStandardSubstituteFallbackWithMessage(ClassLoader classLoader) throws SubstituteNotAvailableException {
        try {
            return new ConfigWithFallbackMessage(HoconUtils.applicationOrStandardSubstitute(classLoader), null);
        }
        catch (SubstituteNotAvailableException substituteNotAvailableException) {
            return new ConfigWithFallbackMessage(ConfigFactory.parseResourcesAnySyntax((ClassLoader)classLoader, (String)APPLICATION), substituteNotAvailableException.getMessage() + " Falling back to standard application.(conf|json|properties).");
        }
    }

    public static WarnedConfig customFileOrSpecifiedSourceWins(File file) {
        boolean bl;
        ArrayList<String> arrayList = new ArrayList<String>();
        boolean bl2 = file.exists();
        Properties properties = System.getProperties();
        boolean bl3 = bl = properties.containsKey("config.resource") || properties.containsKey("config.file") || properties.containsKey("config.url");
        if (bl && bl2) {
            arrayList.add(HoconUtils.createSpecifiedSourceWarning(file, properties));
            return new WarnedConfig(ConfigFactory.load(), arrayList);
        }
        if (!bl2) {
            return new WarnedConfig(ConfigFactory.load(), arrayList);
        }
        Config config = ConfigFactory.defaultOverrides().withFallback((ConfigMergeable)ConfigFactory.parseFile((File)file).withFallback((ConfigMergeable)ConfigFactory.defaultApplication().withFallback((ConfigMergeable)ConfigFactory.defaultReference())));
        return new WarnedConfig(config, arrayList);
    }

    private static String createSpecifiedSourceWarning(File file, Properties properties) {
        boolean bl = true;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Config file ");
        stringBuilder.append(file.getAbsolutePath());
        stringBuilder.append(" will be ignored because a location has been explicitly set via System.properties. [");
        if (properties.containsKey("config.resource")) {
            stringBuilder.append("config.resource=" + properties.getProperty("config.resource"));
            bl = false;
        }
        if (properties.containsKey("config.file")) {
            if (!bl) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("config.file=" + properties.getProperty("config.file"));
            bl = false;
        }
        if (properties.containsKey("config.url")) {
            if (!bl) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("config.url=" + properties.getProperty("config.url"));
            bl = false;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private HoconUtils() {
    }

    public static class WarnedConfig {
        public Config config;
        public List<String> warnings;

        WarnedConfig(Config config, List<String> list) {
            this.config = config;
            this.warnings = list;
        }
    }

    public static class ConfigWithFallbackMessage {
        private Config _config;
        private String _message;

        public Config config() {
            return this._config;
        }

        public String message() {
            return this._message;
        }

        private ConfigWithFallbackMessage(Config config, String string) {
            this._config = config;
            this._message = string;
        }
    }

    public static class SubstituteNotAvailableException
    extends Exception {
        SubstituteNotAvailableException(String string) {
            super(string);
        }
    }

    public static class PropertiesConversion {
        Properties properties;
        Set<String> unrenderable;
    }
}


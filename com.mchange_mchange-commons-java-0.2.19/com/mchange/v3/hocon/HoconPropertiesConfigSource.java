/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.typesafe.config.Config
 *  com.typesafe.config.ConfigFactory
 *  com.typesafe.config.ConfigMergeable
 */
package com.mchange.v3.hocon;

import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.PropertiesConfigSource;
import com.mchange.v2.lang.SystemUtils;
import com.mchange.v3.hocon.HoconUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigMergeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class HoconPropertiesConfigSource
implements PropertiesConfigSource {
    private static Config extractConfig(ClassLoader classLoader, String string, List<DelayedLogItem> list) throws FileNotFoundException, Exception {
        int n = string.indexOf(58);
        ArrayList<Config> arrayList = new ArrayList<Config>();
        if (n >= 0 && "hocon".equals(string.substring(0, n).toLowerCase())) {
            Config config;
            String string2 = string.substring(n + 1).trim();
            for (String string3 : config = string2.split("\\s*,\\s*")) {
                String string4;
                String string5;
                int n2 = string3.lastIndexOf(35);
                if (n2 > 0) {
                    string5 = string3.substring(0, n2);
                    string4 = string3.substring(n2 + 1).replace('/', '.').trim();
                } else {
                    string5 = string3;
                    string4 = null;
                }
                Config config2 = null;
                if ("/".equals(string5)) {
                    config2 = ConfigFactory.systemProperties();
                } else {
                    Object object;
                    Object object2;
                    Config config3 = null;
                    if ("application".equals(string5) || "/application".equals(string5)) {
                        object2 = System.getProperty("config.resource");
                        if (object2 != null) {
                            string5 = object2;
                        } else {
                            object2 = System.getProperty("config.file");
                            if (object2 != null) {
                                object = new File((String)object2);
                                if (((File)object).exists()) {
                                    if (((File)object).canRead()) {
                                        config3 = ConfigFactory.parseFile((File)object);
                                    } else {
                                        list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, String.format("Specified config.file '%s' is not readable. Falling back to standard application.(conf|json|properties).}", ((File)object).getAbsolutePath())));
                                    }
                                } else {
                                    list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, String.format("Specified config.file '%s' does not exist. Falling back to standard application.(conf|json|properties).}", ((File)object).getAbsolutePath())));
                                }
                            } else {
                                object2 = System.getProperty("config.url");
                                if (object2 != null) {
                                    config3 = ConfigFactory.parseURL((URL)new URL((String)object2));
                                }
                            }
                        }
                    }
                    if (config3 == null) {
                        object2 = null;
                        if (string5.indexOf(":") >= 0) {
                            try {
                                object = SystemUtils.sysPropsEnvReplace(string5);
                                object2 = new URL((String)object);
                            }
                            catch (MalformedURLException malformedURLException) {
                                list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, String.format("Apparent URL resource path for HOCON '%s' could not be parsed as a URL.", string5), malformedURLException));
                            }
                        }
                        if (object2 != null) {
                            config3 = ConfigFactory.parseURL((URL)object2);
                        } else {
                            if (string5.charAt(0) == '/') {
                                string5 = string5.substring(1);
                            }
                            boolean bl = string5.indexOf(46) >= 0;
                            config3 = bl ? ConfigFactory.parseResources((ClassLoader)classLoader, (String)string5) : ConfigFactory.parseResourcesAnySyntax((ClassLoader)classLoader, (String)string5);
                        }
                    }
                    if (config3.isEmpty()) {
                        list.add(new DelayedLogItem(DelayedLogItem.Level.FINE, String.format("Missing or empty HOCON configuration for resource path '%s'.", string5)));
                    } else {
                        config2 = config3;
                    }
                }
                if (config2 == null) continue;
                if (string4 != null) {
                    config2 = config2.getConfig(string4);
                }
                arrayList.add(config2);
            }
            if (arrayList.size() == 0) {
                throw new FileNotFoundException(String.format("Could not find HOCON configuration at any of the listed resources in '%s'", string));
            }
            Config config4 = ConfigFactory.empty();
            int n3 = arrayList.size();
            while (--n3 >= 0) {
                config4 = config4.withFallback((ConfigMergeable)arrayList.get(n3));
            }
            return config4.resolve();
        }
        throw new IllegalArgumentException(String.format("Invalid resource identifier for hocon config file: '%s'", string));
    }

    public PropertiesConfigSource.Parse propertiesFromSource(ClassLoader classLoader, String string) throws FileNotFoundException, Exception {
        LinkedList<DelayedLogItem> linkedList = new LinkedList<DelayedLogItem>();
        Config config = HoconPropertiesConfigSource.extractConfig(classLoader, string, linkedList);
        HoconUtils.PropertiesConversion propertiesConversion = HoconUtils.configToProperties(config);
        for (String string2 : propertiesConversion.unrenderable) {
            linkedList.add(new DelayedLogItem(DelayedLogItem.Level.FINE, String.format("Value at path '%s' could not be converted to a String. Skipping.", string2)));
        }
        return new PropertiesConfigSource.Parse(propertiesConversion.properties, linkedList);
    }

    @Override
    public PropertiesConfigSource.Parse propertiesFromSource(String string) throws FileNotFoundException, Exception {
        return this.propertiesFromSource(HoconPropertiesConfigSource.class.getClassLoader(), string);
    }
}


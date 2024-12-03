/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.mail.config;

import com.atlassian.mail.Settings;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.auth.AuthenticationContextFactory;
import com.atlassian.mail.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ConfigLoader {
    private static final Logger log = Logger.getLogger(ConfigLoader.class);
    private static final String DEFAULT_CONFIG_FILE = "mail-config.xml";
    private MailServerManager loadedManager;
    private Settings loadedSettings;
    private AuthenticationContextFactory loadedAuthCtxFactory;

    private ConfigLoader() {
    }

    public ConfigLoader(String file) {
        InputStream configurationFileAsStream = ClassLoaderUtils.getResourceAsStream(file, ConfigLoader.class);
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDoc = db.parse(configurationFileAsStream);
            Element root = xmlDoc.getDocumentElement();
            MailServerManager mailServerManager = ConfigLoader.create(MailServerManager.class, root, "manager", Optional.of((instance, element) -> {
                HashMap<String, String> params = new HashMap<String, String>();
                NodeList properties = element.getElementsByTagName("property");
                if (properties.getLength() > 0) {
                    for (int i = 0; i < properties.getLength(); ++i) {
                        Element property = (Element)properties.item(i);
                        String name = ConfigLoader.getContainedText(property, "name");
                        String value = ConfigLoader.getContainedText(property, "value");
                        params.put(name, value);
                    }
                }
                instance.init(params);
                return instance;
            }));
            this.setLoadedManager(mailServerManager);
            Settings settingsInstance = ConfigLoader.create(Settings.class, root, "settings", Optional.empty());
            if (settingsInstance != null) {
                this.setLoadedSettings(settingsInstance);
            } else {
                this.setLoadedSettings(new Settings.Default());
            }
            AuthenticationContextFactory authCtxFactory = ConfigLoader.create(AuthenticationContextFactory.class, root, "auth-ctx-factory", Optional.empty());
            if (authCtxFactory != null) {
                this.setLoadedAuthContextFactory(authCtxFactory);
            }
        }
        catch (Exception e) {
            log.fatal((Object)e, (Throwable)e);
            throw new RuntimeException("Error in mail configuration: " + e.getMessage(), e);
        }
        finally {
            try {
                configurationFileAsStream.close();
            }
            catch (IOException e2) {
                log.error((Object)e2);
            }
        }
    }

    private static <T> T create(Class<T> expectedClass, Element root, String elementName, Optional<BiFunction<T, Element, T>> createHandlerOpt) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class factoryClass;
        Element configurationElement = (Element)root.getElementsByTagName(elementName).item(0);
        if (configurationElement != null && expectedClass.isAssignableFrom(factoryClass = ClassLoaderUtils.loadClass(configurationElement.getAttribute("class"), ConfigLoader.class))) {
            Object instance = expectedClass.cast(factoryClass.newInstance());
            return (T)createHandlerOpt.map(h -> h.apply(instance, configurationElement)).orElse(instance);
        }
        return null;
    }

    public static ConfigLoader getImmutableConfigurationLoader() {
        return ConfigLoader.getImmutableConfigurationLoader(DEFAULT_CONFIG_FILE);
    }

    public static ConfigLoader getImmutableConfigurationLoader(String file) {
        ConfigLoader configLoader = new ConfigLoader(file);
        return new ImmutableConfigLoader(configLoader);
    }

    public static MailServerManager getServerManager() {
        return ConfigLoader.getServerManager(DEFAULT_CONFIG_FILE);
    }

    public static MailServerManager getServerManager(String file) {
        ConfigLoader configLoader = new ConfigLoader(file);
        return configLoader.getLoadedManager();
    }

    public static Settings getSettings(String file) {
        ConfigLoader configLoader = new ConfigLoader(file);
        return configLoader.getLoadedSettings();
    }

    public static Settings getSettings() {
        return ConfigLoader.getSettings(DEFAULT_CONFIG_FILE);
    }

    public static AuthenticationContextFactory getAuthenticationContextFactory(String file) {
        ConfigLoader configLoader = new ConfigLoader(file);
        return configLoader.getLoadedAuthContextFactory();
    }

    public static AuthenticationContextFactory getAuthenticationContextFactory() {
        return ConfigLoader.getAuthenticationContextFactory(DEFAULT_CONFIG_FILE);
    }

    public MailServerManager getLoadedManager() {
        return this.loadedManager;
    }

    public void setLoadedManager(MailServerManager loadedManager) {
        this.loadedManager = loadedManager;
    }

    public void setLoadedSettings(Settings loadedSettings) {
        this.loadedSettings = loadedSettings;
    }

    public Settings getLoadedSettings() {
        return this.loadedSettings;
    }

    public void setLoadedAuthContextFactory(AuthenticationContextFactory factory) {
        this.loadedAuthCtxFactory = factory;
    }

    public AuthenticationContextFactory getLoadedAuthContextFactory() {
        return this.loadedAuthCtxFactory;
    }

    private static String getContainedText(Element parent, String childTagName) {
        try {
            Node tag = parent.getElementsByTagName(childTagName).item(0);
            return ((Text)tag.getFirstChild()).getData();
        }
        catch (Exception e) {
            return null;
        }
    }

    private static final class ImmutableConfigLoader
    extends ConfigLoader {
        private final ConfigLoader loader;

        private ImmutableConfigLoader(ConfigLoader delegate) {
            this.loader = delegate;
        }

        @Override
        public MailServerManager getLoadedManager() {
            return this.loader.getLoadedManager();
        }

        @Override
        public void setLoadedManager(MailServerManager loadedManager) {
        }

        @Override
        public void setLoadedSettings(Settings loadedSettings) {
        }

        @Override
        public Settings getLoadedSettings() {
            return this.loader.getLoadedSettings();
        }

        @Override
        public void setLoadedAuthContextFactory(AuthenticationContextFactory factory) {
        }

        @Override
        public AuthenticationContextFactory getLoadedAuthContextFactory() {
            return this.loader.getLoadedAuthContextFactory();
        }
    }
}


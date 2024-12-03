/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.BeanHandler;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.generator.ConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public final class ConfigurationFactory {
    private static final Logger LOG = LoggerFactory.getLogger((String)ConfigurationFactory.class.getName());
    private static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "/ehcache.xml";
    private static final String FAILSAFE_CLASSPATH_CONFIGURATION_FILE = "/ehcache-failsafe.xml";

    private ConfigurationFactory() {
    }

    public static Configuration parseConfiguration(File file) throws CacheException {
        if (file == null) {
            throw new CacheException("Attempt to configure ehcache from null file.");
        }
        LOG.debug("Configuring ehcache from file: {}", (Object)file);
        Configuration configuration = null;
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            configuration = ConfigurationFactory.parseConfiguration(input);
        }
        catch (Exception e) {
            throw new CacheException("Error configuring from " + file + ". Initial cause was " + e.getMessage(), e);
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException e) {
                LOG.error("IOException while closing configuration input stream. Error was " + e.getMessage());
            }
        }
        configuration.setSource(ConfigurationSource.getConfigurationSource(file));
        return configuration;
    }

    public static Configuration parseConfiguration(URL url) throws CacheException {
        Configuration configuration;
        LOG.debug("Configuring ehcache from URL: {}", (Object)url);
        InputStream input = null;
        try {
            input = url.openStream();
            configuration = ConfigurationFactory.parseConfiguration(input);
        }
        catch (Exception e) {
            throw new CacheException("Error configuring from " + url + ". Initial cause was " + e.getMessage(), e);
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException e) {
                LOG.error("IOException while closing configuration input stream. Error was " + e.getMessage());
            }
        }
        configuration.setSource(ConfigurationSource.getConfigurationSource(url));
        return configuration;
    }

    public static Configuration parseConfiguration() throws CacheException {
        ClassLoader standardClassloader = Thread.currentThread().getContextClassLoader();
        URL url = null;
        if (standardClassloader != null) {
            url = standardClassloader.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url == null) {
            url = ConfigurationFactory.class.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url != null) {
            LOG.debug("Configuring ehcache from ehcache.xml found in the classpath: " + url);
        } else {
            url = ConfigurationFactory.class.getResource(FAILSAFE_CLASSPATH_CONFIGURATION_FILE);
            LOG.warn("No configuration found. Configuring ehcache from ehcache-failsafe.xml  found in the classpath: {}", (Object)url);
        }
        Configuration configuration = ConfigurationFactory.parseConfiguration(url);
        configuration.setSource(ConfigurationSource.getConfigurationSource());
        return configuration;
    }

    public static Configuration parseConfiguration(InputStream inputStream) throws CacheException {
        LOG.debug("Configuring ehcache from InputStream");
        Configuration configuration = new Configuration();
        try {
            InputStream translatedInputStream = ConfigurationFactory.translateSystemProperties(inputStream);
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            BeanHandler handler = new BeanHandler(configuration);
            parser.parse(translatedInputStream, (DefaultHandler)handler);
        }
        catch (Exception e) {
            throw new CacheException("Error configuring from input stream. Initial cause was " + e.getMessage(), e);
        }
        configuration.setSource(ConfigurationSource.getConfigurationSource(inputStream));
        return configuration;
    }

    public static CacheConfiguration parseCacheConfiguration(String xmlString) throws CacheException {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        try {
            InputSource source = new InputSource(new StringReader(xmlString));
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            BeanHandler handler = new BeanHandler(cacheConfiguration);
            parser.parse(source, (DefaultHandler)handler);
        }
        catch (Exception e) {
            throw new CacheException("Error configuring from input stream. Initial cause was " + e.getMessage(), e);
        }
        return cacheConfiguration;
    }

    private static InputStream translateSystemProperties(InputStream inputStream) throws IOException {
        int c;
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
        while ((c = ((Reader)reader).read()) != -1) {
            sb.append((char)c);
        }
        String configuration = sb.toString();
        Set tokens = ConfigurationFactory.extractPropertyTokens(configuration);
        for (Object tokenObject : tokens) {
            String token = (String)tokenObject;
            String leftTrimmed = token.replaceAll("\\$\\{", "");
            String trimmedToken = leftTrimmed.replaceAll("\\}", "");
            String property = System.getProperty(trimmedToken);
            if (property == null) {
                LOG.debug("Did not find a system property for the " + token + " token specified in the configuration.Replacing with \"\"");
                continue;
            }
            String propertyWithQuotesProtected = Matcher.quoteReplacement(property);
            configuration = configuration.replaceAll("\\$\\{" + trimmedToken + "\\}", propertyWithQuotesProtected);
            LOG.debug("Found system property value of " + property + " for the " + token + " token specified in the configuration.");
        }
        return new ByteArrayInputStream(configuration.getBytes("UTF-8"));
    }

    static Set extractPropertyTokens(String sourceDocument) {
        HashSet<String> propertyTokens = new HashSet<String>();
        Pattern pattern = Pattern.compile("\\$\\{.+?\\}");
        Matcher matcher = pattern.matcher(sourceDocument);
        while (matcher.find()) {
            String token = matcher.group();
            propertyTokens.add(token);
        }
        return propertyTokens;
    }
}


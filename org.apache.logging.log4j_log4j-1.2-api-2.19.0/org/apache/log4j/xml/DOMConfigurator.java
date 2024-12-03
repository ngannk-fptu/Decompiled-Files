/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.ConfigurationSource
 *  org.apache.logging.log4j.core.config.Configurator
 *  org.apache.logging.log4j.core.net.UrlConnectionFactory
 *  org.apache.logging.log4j.core.util.IOUtils
 */
package org.apache.log4j.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.log4j.LogManager;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.XMLWatchdog;
import org.apache.log4j.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.util.IOUtils;
import org.w3c.dom.Element;

public class DOMConfigurator {
    public static void configure(Element element) {
    }

    public static void configure(String fileName) throws FactoryConfigurationError {
        Path path = Paths.get(fileName, new String[0]);
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);){
            ConfigurationSource source = new ConfigurationSource(inputStream, path);
            LoggerContext context = (LoggerContext)org.apache.logging.log4j.LogManager.getContext((boolean)false);
            Configuration configuration = new XmlConfigurationFactory().getConfiguration(context, source);
            LogManager.getRootLogger().removeAllAppenders();
            Configurator.reconfigure((Configuration)configuration);
        }
        catch (IOException e) {
            throw new FactoryConfigurationError(e);
        }
    }

    public static void configure(URL url) throws FactoryConfigurationError {
        new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
    }

    public static void configureAndWatch(String fileName) {
        DOMConfigurator.configure(fileName);
    }

    public static void configureAndWatch(String fileName, long delay) {
        XMLWatchdog xdog = new XMLWatchdog(fileName);
        xdog.setDelay(delay);
        xdog.start();
    }

    public static Object parseElement(Element element, Properties props, Class expectedClass) {
        return null;
    }

    public static void setParameter(Element elem, PropertySetter propSetter, Properties props) {
    }

    public static String subst(String value, Properties props) {
        return OptionConverter.substVars(value, props);
    }

    private void doConfigure(ConfigurationSource source) {
        LoggerContext context = (LoggerContext)org.apache.logging.log4j.LogManager.getContext((boolean)false);
        Configuration configuration = new XmlConfigurationFactory().getConfiguration(context, source);
        Configurator.reconfigure((Configuration)configuration);
    }

    public void doConfigure(Element element, LoggerRepository repository) {
    }

    public void doConfigure(InputStream inputStream, LoggerRepository repository) throws FactoryConfigurationError {
        try {
            this.doConfigure(new ConfigurationSource(inputStream));
        }
        catch (IOException e) {
            throw new FactoryConfigurationError(e);
        }
    }

    public void doConfigure(Reader reader, LoggerRepository repository) throws FactoryConfigurationError {
        try {
            StringWriter sw = new StringWriter();
            IOUtils.copy((Reader)reader, (Writer)sw);
            this.doConfigure(new ConfigurationSource((InputStream)new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8))));
        }
        catch (IOException e) {
            throw new FactoryConfigurationError(e);
        }
    }

    public void doConfigure(String fileName, LoggerRepository repository) {
        DOMConfigurator.configure(fileName);
    }

    public void doConfigure(URL url, LoggerRepository repository) {
        try {
            URLConnection connection = UrlConnectionFactory.createConnection((URL)url);
            try (InputStream inputStream = connection.getInputStream();){
                this.doConfigure(new ConfigurationSource(inputStream, url));
            }
        }
        catch (IOException e) {
            throw new FactoryConfigurationError(e);
        }
    }
}


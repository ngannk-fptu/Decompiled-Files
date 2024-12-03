/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.cfgxml.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.hibernate.boot.cfgxml.internal.JaxbCfgProcessor;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgHibernateConfiguration;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.internal.util.ValueHolder;
import org.hibernate.internal.util.config.ConfigurationException;
import org.jboss.logging.Logger;

public class ConfigLoader {
    private static final Logger log = Logger.getLogger(ConfigLoader.class);
    private final BootstrapServiceRegistry bootstrapServiceRegistry;
    private ValueHolder<JaxbCfgProcessor> jaxbProcessorHolder = new ValueHolder<1>(new ValueHolder.DeferredInitializer<JaxbCfgProcessor>(){

        @Override
        public JaxbCfgProcessor initialize() {
            return new JaxbCfgProcessor(ConfigLoader.this.bootstrapServiceRegistry.getService(ClassLoaderService.class));
        }
    });

    public ConfigLoader(BootstrapServiceRegistry bootstrapServiceRegistry) {
        this.bootstrapServiceRegistry = bootstrapServiceRegistry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LoadedConfig loadConfigXmlResource(String cfgXmlResourceName) {
        InputStream stream = this.bootstrapServiceRegistry.getService(ClassLoaderService.class).locateResourceStream(cfgXmlResourceName);
        if (stream == null) {
            throw new ConfigurationException("Could not locate cfg.xml resource [" + cfgXmlResourceName + "]");
        }
        try {
            JaxbCfgHibernateConfiguration jaxbCfg = this.jaxbProcessorHolder.getValue().unmarshal(stream, new Origin(SourceType.RESOURCE, cfgXmlResourceName));
            LoadedConfig loadedConfig = LoadedConfig.consume(jaxbCfg);
            return loadedConfig;
        }
        finally {
            try {
                stream.close();
            }
            catch (IOException e) {
                log.debug((Object)"Unable to close cfg.xml resource stream", (Throwable)e);
            }
        }
    }

    public LoadedConfig loadConfigXmlFile(File cfgXmlFile) {
        try {
            JaxbCfgHibernateConfiguration jaxbCfg = this.jaxbProcessorHolder.getValue().unmarshal(new FileInputStream(cfgXmlFile), new Origin(SourceType.FILE, cfgXmlFile.getAbsolutePath()));
            return LoadedConfig.consume(jaxbCfg);
        }
        catch (FileNotFoundException e) {
            throw new ConfigurationException("Specified cfg.xml file [" + cfgXmlFile.getAbsolutePath() + "] does not exist");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LoadedConfig loadConfigXmlUrl(URL url) {
        LoadedConfig loadedConfig;
        InputStream stream = url.openStream();
        try {
            JaxbCfgHibernateConfiguration jaxbCfg = this.jaxbProcessorHolder.getValue().unmarshal(stream, new Origin(SourceType.URL, url.toExternalForm()));
            loadedConfig = LoadedConfig.consume(jaxbCfg);
        }
        catch (Throwable throwable) {
            try {
                try {
                    stream.close();
                }
                catch (IOException e) {
                    log.debug((Object)"Unable to close cfg.xml URL stream", (Throwable)e);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new ConfigurationException("Could not access given cfg.xml URL input stream", e);
            }
        }
        try {
            stream.close();
        }
        catch (IOException e) {
            log.debug((Object)"Unable to close cfg.xml URL stream", (Throwable)e);
        }
        return loadedConfig;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Properties loadProperties(String resourceName) {
        Properties properties2;
        InputStream stream = this.bootstrapServiceRegistry.getService(ClassLoaderService.class).locateResourceStream(resourceName);
        if (stream == null) {
            throw new ConfigurationException("Unable to apply settings from properties file [" + resourceName + "]");
        }
        try {
            Properties properties = new Properties();
            properties.load(stream);
            properties2 = properties;
        }
        catch (IOException e) {
            try {
                throw new ConfigurationException("Unable to apply settings from properties file [" + resourceName + "]", e);
            }
            catch (Throwable throwable) {
                try {
                    stream.close();
                    throw throwable;
                }
                catch (IOException e2) {
                    log.debug((Object)String.format("Unable to close properties file [%s] stream", resourceName), (Throwable)e2);
                }
                throw throwable;
            }
        }
        try {
            stream.close();
            return properties2;
        }
        catch (IOException e) {
            log.debug((Object)String.format("Unable to close properties file [%s] stream", resourceName), (Throwable)e);
        }
        return properties2;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Properties loadProperties(File file) {
        try {
            Properties properties2;
            FileInputStream stream = new FileInputStream(file);
            try {
                Properties properties = new Properties();
                properties.load(stream);
                properties2 = properties;
            }
            catch (IOException e) {
                try {
                    throw new ConfigurationException("Unable to apply settings from properties file [" + file.getAbsolutePath() + "]", e);
                }
                catch (Throwable throwable) {
                    try {
                        ((InputStream)stream).close();
                        throw throwable;
                    }
                    catch (IOException e2) {
                        log.debug((Object)String.format("Unable to close properties file [%s] stream", file.getAbsolutePath()), (Throwable)e2);
                    }
                    throw throwable;
                }
            }
            try {
                ((InputStream)stream).close();
                return properties2;
            }
            catch (IOException e) {
                log.debug((Object)String.format("Unable to close properties file [%s] stream", file.getAbsolutePath()), (Throwable)e);
            }
            return properties2;
        }
        catch (FileNotFoundException e3) {
            throw new ConfigurationException("Unable locate specified properties file [" + file.getAbsolutePath() + "]", e3);
        }
    }
}


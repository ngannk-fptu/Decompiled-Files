/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 *  org.apache.log4j.Priority
 *  org.apache.log4j.PropertyConfigurator
 */
package com.opensymphony.provider.log;

import com.opensymphony.provider.LogProvider;
import com.opensymphony.provider.ProviderConfigurationException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

public class Log4JProvider
implements LogProvider {
    @Override
    public Object getContext(String name) {
        return Category.getInstance((String)name);
    }

    @Override
    public boolean isEnabled(Object context, int level) {
        Category category = (Category)context;
        return category.isEnabledFor(this.getPriority(level));
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
        block11: {
            try {
                Object configurationMethod = null;
                String configFile = System.getProperty("logger.config");
                if (configFile != null) {
                    PropertyConfigurator.configure((String)configFile);
                    break block11;
                }
                Properties logProps = null;
                InputStream is = null;
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                try {
                    is = classLoader.getResourceAsStream("log4j.properties");
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (is == null) {
                    try {
                        is = classLoader.getResourceAsStream("/log4j.properties");
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (is != null) {
                    logProps = new Properties();
                    logProps.load(is);
                }
                if (logProps != null) {
                    PropertyConfigurator.configure((Properties)logProps);
                    break block11;
                }
                throw new ProviderConfigurationException("Log4J config file not found - specify location in logger.config property");
            }
            catch (ProviderConfigurationException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ProviderConfigurationException("Error configuring Log4J", e);
            }
        }
    }

    @Override
    public void log(Object context, int level, Object msg, Throwable throwable) {
        Category category = (Category)context;
        category.log(this.getPriority(level), msg, throwable);
    }

    private Priority getPriority(int level) {
        switch (level) {
            case 1: {
                return Priority.DEBUG;
            }
            case 2: {
                return Priority.INFO;
            }
            case 3: {
                return Priority.WARN;
            }
            case 4: {
                return Priority.ERROR;
            }
            case 5: {
                return Priority.FATAL;
            }
        }
        return Priority.toPriority((int)level);
    }
}


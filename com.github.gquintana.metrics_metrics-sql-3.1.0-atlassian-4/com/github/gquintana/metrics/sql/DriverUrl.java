/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.github.gquintana.metrics.proxy.AbstractProxyFactory;
import com.github.gquintana.metrics.proxy.CGLibProxyFactory;
import com.github.gquintana.metrics.proxy.CachingProxyFactory;
import com.github.gquintana.metrics.proxy.ReflectProxyFactory;
import com.github.gquintana.metrics.sql.DefaultMetricNamingStrategy;
import com.github.gquintana.metrics.sql.Driver;
import com.github.gquintana.metrics.sql.MetricNamingStrategy;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DriverUrl {
    public static final String URL_PREFIX = "jdbc:metrics:";
    private final String rawUrl;
    private final String cleanUrl;
    private final String databaseType;
    private final Properties properties;
    private static final Pattern PATTERN = Pattern.compile("^jdbc:metrics:(([\\w]+):[^?;]+)(?:([?;])(.*))?$");

    public DriverUrl(String rawUrl, String cleanUrl, String databaseType, Properties properties) {
        this.rawUrl = rawUrl;
        this.cleanUrl = cleanUrl;
        this.databaseType = databaseType;
        this.properties = properties;
    }

    private static Properties parseProperties(String urlProps, String propSep, StringBuilder cleanUrlBuilder) {
        Properties properties = new Properties();
        boolean first = true;
        for (String sProp : urlProps.split(propSep)) {
            if (sProp.startsWith("metrics_")) {
                String[] subProp = sProp.split("=");
                properties.put(subProp[0], subProp[1]);
                continue;
            }
            if (first) {
                first = false;
            } else {
                cleanUrlBuilder.append(propSep);
            }
            cleanUrlBuilder.append(sProp);
        }
        return properties;
    }

    public static DriverUrl parse(String rawUrl) {
        String cleanUrl;
        String dbType;
        Matcher matcher = PATTERN.matcher(rawUrl);
        StringBuilder cleanUrlBuilder = new StringBuilder("jdbc:");
        Properties properties = null;
        if (matcher.matches()) {
            cleanUrlBuilder.append(matcher.group(1));
            dbType = matcher.group(2);
            String sep = matcher.group(3);
            String sProps = matcher.group(4);
            if (sep != null && sProps != null) {
                cleanUrlBuilder.append(sep);
                if (sep.equals("?")) {
                    properties = DriverUrl.parseProperties(sProps, "&", cleanUrlBuilder);
                } else if (sep.equals(";")) {
                    properties = DriverUrl.parseProperties(sProps, ";", cleanUrlBuilder);
                }
                cleanUrl = cleanUrlBuilder.toString();
                if (cleanUrl.endsWith(sep)) {
                    cleanUrl = cleanUrl.substring(0, cleanUrl.length() - sep.length());
                }
            } else {
                cleanUrl = cleanUrlBuilder.toString();
            }
        } else {
            throw new IllegalArgumentException("Missing prefix jdbc:metrics:->" + rawUrl);
        }
        return new DriverUrl(rawUrl, cleanUrl, dbType, properties);
    }

    public String getRawUrl() {
        return this.rawUrl;
    }

    public String getCleanUrl() {
        return this.cleanUrl;
    }

    public String getDatabaseType() {
        return this.databaseType;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public <T> T getProperty(String key, Class<T> type) {
        if (this.properties == null) {
            return null;
        }
        String sVal = this.properties.getProperty(key);
        if (sVal == null) {
            return null;
        }
        if (type.equals(String.class)) {
            return type.cast(sVal);
        }
        if (type.equals(Class.class)) {
            try {
                return type.cast(Class.forName(sVal));
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalArgumentException("Property " + sVal + " is not a valid class", classNotFoundException);
            }
        }
        throw new IllegalArgumentException("Property type " + type + " not supported");
    }

    public <T> T getProperty(String key, Class<T> type, T def) {
        T val = this.getProperty(key, type);
        if (val == null) {
            val = def;
        }
        return val;
    }

    public Class<? extends Driver> getDriverClass() {
        return this.getProperty("metrics_driver", Class.class);
    }

    public Class<? extends AbstractProxyFactory> getProxyFactoryClass() {
        String s = this.getProperty("metrics_proxy_factory", String.class);
        Class factoryClass = s == null || s.equals("reflect") ? ReflectProxyFactory.class : (s.equalsIgnoreCase("cglib") ? CGLibProxyFactory.class : (s.equalsIgnoreCase("caching") ? CachingProxyFactory.class : this.getProperty("metrics_proxy_factory", Class.class)));
        return factoryClass;
    }

    public Class<? extends MetricNamingStrategy> getNamingStrategyClass() {
        return this.getProperty("metrics_naming_strategy", Class.class, DefaultMetricNamingStrategy.class);
    }

    public String getName() {
        return this.getProperty("metrics_name", String.class, this.databaseType + "_driver");
    }
}


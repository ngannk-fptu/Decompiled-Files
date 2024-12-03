/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Constants
 *  org.springframework.core.SpringProperties
 *  org.springframework.lang.Nullable
 *  org.springframework.util.PropertyPlaceholderHelper
 *  org.springframework.util.PropertyPlaceholderHelper$PlaceholderResolver
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.beans.factory.config;

import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.core.Constants;
import org.springframework.core.SpringProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

@Deprecated
public class PropertyPlaceholderConfigurer
extends PlaceholderConfigurerSupport {
    public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
    public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
    public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
    private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);
    private int systemPropertiesMode = 1;
    private boolean searchSystemEnvironment = !SpringProperties.getFlag((String)"spring.getenv.ignore");

    public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
        this.systemPropertiesMode = constants.asNumber(constantName).intValue();
    }

    public void setSystemPropertiesMode(int systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }

    public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
        this.searchSystemEnvironment = searchSystemEnvironment;
    }

    @Nullable
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String propVal = null;
        if (systemPropertiesMode == 2) {
            propVal = this.resolveSystemProperty(placeholder);
        }
        if (propVal == null) {
            propVal = this.resolvePlaceholder(placeholder, props);
        }
        if (propVal == null && systemPropertiesMode == 1) {
            propVal = this.resolveSystemProperty(placeholder);
        }
        return propVal;
    }

    @Nullable
    protected String resolvePlaceholder(String placeholder, Properties props) {
        return props.getProperty(placeholder);
    }

    @Nullable
    protected String resolveSystemProperty(String key) {
        try {
            String value = System.getProperty(key);
            if (value == null && this.searchSystemEnvironment) {
                value = System.getenv(key);
            }
            return value;
        }
        catch (Throwable ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Could not access system property '" + key + "': " + ex));
            }
            return null;
        }
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        PlaceholderResolvingStringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    private final class PropertyPlaceholderConfigurerResolver
    implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final Properties props;

        private PropertyPlaceholderConfigurerResolver(Properties props) {
            this.props = props;
        }

        @Nullable
        public String resolvePlaceholder(String placeholderName) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, this.props, PropertyPlaceholderConfigurer.this.systemPropertiesMode);
        }
    }

    private class PlaceholderResolvingStringValueResolver
    implements StringValueResolver {
        private final PropertyPlaceholderHelper helper;
        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper(PropertyPlaceholderConfigurer.this.placeholderPrefix, PropertyPlaceholderConfigurer.this.placeholderSuffix, PropertyPlaceholderConfigurer.this.valueSeparator, PropertyPlaceholderConfigurer.this.ignoreUnresolvablePlaceholders);
            this.resolver = new PropertyPlaceholderConfigurerResolver(props);
        }

        @Nullable
        public String resolveStringValue(String strVal) throws BeansException {
            String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
            if (PropertyPlaceholderConfigurer.this.trimValues) {
                resolved = resolved.trim();
            }
            return resolved.equals(PropertyPlaceholderConfigurer.this.nullValue) ? null : resolved;
        }
    }
}


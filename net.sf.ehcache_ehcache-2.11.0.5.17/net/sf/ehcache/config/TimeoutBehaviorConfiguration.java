/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.InvalidConfigurationException;

public class TimeoutBehaviorConfiguration
implements Cloneable {
    public static final String EXCEPTION_TYPE_NAME = "exception";
    public static final String LOCAL_READS_TYPE_NAME = "localReads";
    public static final String LOCAL_READS_AND_EXCEPTION_ON_WRITES_TYPE_NAME = "localReadsAndExceptionOnWrite";
    public static final String NOOP_TYPE_NAME = "noop";
    public static final String CUSTOM_TYPE_NAME = "custom";
    public static final String CUSTOM_TYPE_FACTORY_PROPERTY_NAME = "customFactoryClassName";
    public static final TimeoutBehaviorType DEFAULT_TIMEOUT_BEHAVIOR_TYPE = TimeoutBehaviorType.EXCEPTION;
    public static final String DEFAULT_PROPERTIES = "";
    public static final String DEFAULT_PROPERTY_SEPARATOR = ",";
    public static final String DEFAULT_VALUE = DEFAULT_TIMEOUT_BEHAVIOR_TYPE.getTypeName();
    private volatile TimeoutBehaviorType type = DEFAULT_TIMEOUT_BEHAVIOR_TYPE;
    private volatile String properties = "";
    private volatile String propertySeparator = ",";

    public TimeoutBehaviorConfiguration() {
    }

    public TimeoutBehaviorConfiguration(TimeoutBehaviorConfiguration ref) {
        this.propertySeparator = ref.propertySeparator;
        this.properties = ref.properties;
        this.type = ref.type;
    }

    public String getType() {
        return this.type.getTypeName();
    }

    public TimeoutBehaviorType getTimeoutBehaviorType() {
        return this.type;
    }

    public void setType(String type) {
        if (!TimeoutBehaviorType.isValidTimeoutBehaviorType(type)) {
            throw new CacheException("Invalid value for timeoutBehavior type - '" + type + "'. Valid values are: '" + TimeoutBehaviorType.EXCEPTION.getTypeName() + "',  '" + TimeoutBehaviorType.NOOP.getTypeName() + "',  '" + TimeoutBehaviorType.LOCAL_READS.getTypeName());
        }
        this.type = TimeoutBehaviorType.getTimeoutBehaviorTypeFromName(type);
    }

    public TimeoutBehaviorConfiguration type(String type) {
        this.setType(type);
        return this;
    }

    public String getProperties() {
        return this.properties;
    }

    public void setProperties(String properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Properties cannot be null");
        }
        this.properties = properties;
    }

    public TimeoutBehaviorConfiguration properties(String value) {
        this.setProperties(value);
        return this;
    }

    public String getPropertySeparator() {
        return this.propertySeparator;
    }

    public void setPropertySeparator(String propertySeparator) {
        if (propertySeparator == null) {
            throw new IllegalArgumentException("Property Separator cannot be null");
        }
        this.propertySeparator = propertySeparator;
    }

    public TimeoutBehaviorConfiguration propertySeparator(String value) {
        this.setPropertySeparator(value);
        return this;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.properties == null ? 0 : this.properties.hashCode());
        result = 31 * result + (this.propertySeparator == null ? 0 : this.propertySeparator.hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        TimeoutBehaviorConfiguration other = (TimeoutBehaviorConfiguration)obj;
        if (this.properties == null ? other.properties != null : !this.properties.equals(other.properties)) {
            return false;
        }
        if (this.propertySeparator == null ? other.propertySeparator != null : !this.propertySeparator.equals(other.propertySeparator)) {
            return false;
        }
        return this.type == other.type;
    }

    private Properties extractProperties() {
        String[] props;
        Properties rv = new Properties();
        String propertiesString = this.properties;
        String sep = this.propertySeparator;
        for (String prop : props = propertiesString.split(this.propertySeparator)) {
            String[] nvPair = prop.split("=");
            if (nvPair == null || nvPair.length != 2) {
                throw new InvalidConfigurationException("Property not specified correctly. Failed to parse: " + prop);
            }
            rv.setProperty(nvPair[0], nvPair[1]);
        }
        return rv;
    }

    public static enum TimeoutBehaviorType {
        EXCEPTION{

            @Override
            public String getTypeName() {
                return TimeoutBehaviorConfiguration.EXCEPTION_TYPE_NAME;
            }
        }
        ,
        NOOP{

            @Override
            public String getTypeName() {
                return TimeoutBehaviorConfiguration.NOOP_TYPE_NAME;
            }
        }
        ,
        LOCAL_READS{

            @Override
            public String getTypeName() {
                return TimeoutBehaviorConfiguration.LOCAL_READS_TYPE_NAME;
            }
        }
        ,
        LOCAL_READS_AND_EXCEPTION_ON_WRITES{

            @Override
            public String getTypeName() {
                return TimeoutBehaviorConfiguration.LOCAL_READS_AND_EXCEPTION_ON_WRITES_TYPE_NAME;
            }
        }
        ,
        CUSTOM{

            @Override
            public String getTypeName() {
                return TimeoutBehaviorConfiguration.CUSTOM_TYPE_NAME;
            }
        };

        private static final Map<String, TimeoutBehaviorType> TYPE_MAP;

        public abstract String getTypeName();

        public static boolean isValidTimeoutBehaviorType(String type) {
            TimeoutBehaviorType timeoutBehaviorType = TYPE_MAP.get(type);
            return timeoutBehaviorType != null;
        }

        public static TimeoutBehaviorType getTimeoutBehaviorTypeFromName(String typeName) {
            return TYPE_MAP.get(typeName);
        }

        static {
            HashMap<String, TimeoutBehaviorType> validTypes = new HashMap<String, TimeoutBehaviorType>();
            for (TimeoutBehaviorType timeoutBehaviorType : TimeoutBehaviorType.values()) {
                validTypes.put(timeoutBehaviorType.getTypeName(), timeoutBehaviorType);
            }
            TYPE_MAP = Collections.unmodifiableMap(validTypes);
        }
    }
}


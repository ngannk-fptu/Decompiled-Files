/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.StringUtil;
import java.util.Properties;

public class LoginModuleConfig {
    private String className;
    private LoginModuleUsage usage;
    private Properties properties = new Properties();

    public LoginModuleConfig() {
    }

    public LoginModuleConfig(String className, LoginModuleUsage usage) {
        this.className = className;
        this.usage = usage;
    }

    public String getClassName() {
        return this.className;
    }

    @Deprecated
    public Object getImplementation() {
        throw new UnsupportedOperationException("Deprecated operation. Use getClassName instead.");
    }

    public Properties getProperties() {
        return this.properties;
    }

    public LoginModuleUsage getUsage() {
        return this.usage;
    }

    public LoginModuleConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    @Deprecated
    public LoginModuleConfig setImplementation(Object implementation) {
        throw new UnsupportedOperationException("Deprecated operation. Use setClassName instead.");
    }

    public LoginModuleConfig setUsage(LoginModuleUsage usage) {
        this.usage = usage;
        return this;
    }

    public LoginModuleConfig setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public String toString() {
        return "LoginModuleConfig{className='" + this.className + "', usage=" + (Object)((Object)this.usage) + ", properties=" + this.properties + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LoginModuleConfig that = (LoginModuleConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.usage != that.usage) {
            return false;
        }
        return this.properties != null ? this.properties.equals(that.properties) : that.properties == null;
    }

    public int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 31 * result + (this.usage != null ? this.usage.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        return result;
    }

    public static enum LoginModuleUsage {
        REQUIRED,
        REQUISITE,
        SUFFICIENT,
        OPTIONAL;


        public static LoginModuleUsage get(String v) {
            try {
                return LoginModuleUsage.valueOf(v.toUpperCase(StringUtil.LOCALE_INTERNAL));
            }
            catch (Exception ignore) {
                EmptyStatement.ignore(ignore);
                return REQUIRED;
            }
        }
    }
}


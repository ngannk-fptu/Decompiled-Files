/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

public class FactoryConfiguration<T extends FactoryConfiguration>
implements Cloneable {
    protected String fullyQualifiedClassPath;
    protected String properties;
    protected String propertySeparator;

    public T clone() {
        FactoryConfiguration config;
        try {
            config = (FactoryConfiguration)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return (T)config;
    }

    public final void setClass(String fullyQualifiedClassPath) {
        this.fullyQualifiedClassPath = fullyQualifiedClassPath;
    }

    public T className(String fullyQualifiedClassPath) {
        this.setClass(fullyQualifiedClassPath);
        return (T)this;
    }

    public final String getFullyQualifiedClassPath() {
        return this.fullyQualifiedClassPath;
    }

    public final void setProperties(String properties) {
        this.properties = properties;
    }

    public T properties(String properties) {
        this.setProperties(properties);
        return (T)this;
    }

    public final String getProperties() {
        return this.properties;
    }

    public void setPropertySeparator(String propertySeparator) {
        this.propertySeparator = propertySeparator;
    }

    public T propertySeparator(String propertySeparator) {
        this.setPropertySeparator(propertySeparator);
        return (T)this;
    }

    public String getPropertySeparator() {
        return this.propertySeparator;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.fullyQualifiedClassPath == null ? 0 : this.fullyQualifiedClassPath.hashCode());
        result = 31 * result + (this.properties == null ? 0 : this.properties.hashCode());
        result = 31 * result + (this.propertySeparator == null ? 0 : this.propertySeparator.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        FactoryConfiguration other = (FactoryConfiguration)obj;
        if (this.fullyQualifiedClassPath == null ? other.fullyQualifiedClassPath != null : !this.fullyQualifiedClassPath.equals(other.fullyQualifiedClassPath)) {
            return false;
        }
        if (this.properties == null ? other.properties != null : !this.properties.equals(other.properties)) {
            return false;
        }
        return !(this.propertySeparator == null ? other.propertySeparator != null : !this.propertySeparator.equals(other.propertySeparator));
    }
}


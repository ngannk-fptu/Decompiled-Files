/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.lang.reflect.Method;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.OldToolInfo;
import org.apache.velocity.tools.ToolInfo;
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationException;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.config.NullKeyException;
import org.apache.velocity.tools.config.ValidScope;

public class ToolConfiguration
extends Configuration {
    private String key;
    private String classname;
    private String restrictTo;
    private Boolean skipSetters;
    private Status status;
    private Throwable problem;

    public void setKey(String key) {
        this.key = key;
        if (key != null && !key.equals(this.getDefaultKey())) {
            this.setProperty("key", key);
        }
    }

    public void setClass(Class clazz) {
        this.setClassname(clazz.getName());
    }

    public void setClassname(String classname) {
        this.classname = classname;
        this.status = null;
    }

    public void setRestrictTo(String path) {
        this.restrictTo = path;
    }

    public void setSkipSetters(Boolean cfgOnly) {
        this.skipSetters = cfgOnly;
    }

    public String getKey() {
        if (this.key != null) {
            return this.key;
        }
        return this.getDefaultKey();
    }

    public String getDefaultKey() {
        if (this.getClassname() != null) {
            Class clazz = this.getToolClass();
            DefaultKey defaultKey = clazz.getAnnotation(DefaultKey.class);
            if (defaultKey != null) {
                return defaultKey.value();
            }
            String name = clazz.getSimpleName();
            if (name.endsWith("Tool")) {
                int i = name.indexOf("Tool");
                name = name.substring(0, i);
            }
            name = name.length() > 1 ? name.substring(0, 1).toLowerCase() + name.substring(1, name.length()) : name.toLowerCase();
            return name;
        }
        return null;
    }

    public String getClassname() {
        return this.classname;
    }

    public Class getToolClass() {
        try {
            return ClassUtils.getClass(this.getClassname());
        }
        catch (ClassNotFoundException cnfe) {
            throw new ConfigurationException((Configuration)this, (Throwable)cnfe);
        }
    }

    public String[] getInvalidScopes() {
        InvalidScope invalid = this.getToolClass().getAnnotation(InvalidScope.class);
        if (invalid != null) {
            return invalid.value();
        }
        return new String[0];
    }

    public String[] getValidScopes() {
        ValidScope valid = this.getToolClass().getAnnotation(ValidScope.class);
        if (valid != null) {
            return valid.value();
        }
        return new String[0];
    }

    private final Status getStatus() {
        if (this.status == null) {
            if (this.getClassname() == null) {
                this.status = Status.NONE;
            }
            try {
                Class clazz = ClassUtils.getClass(this.getClassname());
                this.digForDependencies(clazz);
                clazz.newInstance();
                Method init = clazz.getMethod("init", Object.class);
                Deprecated bc = init.getAnnotation(Deprecated.class);
                if (bc == null) {
                    this.status = Status.OLD;
                    this.problem = null;
                } else {
                    this.status = Status.VALID;
                    this.problem = null;
                }
            }
            catch (NoSuchMethodException nsme) {
                this.status = Status.VALID;
                this.problem = null;
            }
            catch (ClassNotFoundException cnfe) {
                this.status = Status.MISSING;
                this.problem = cnfe;
            }
            catch (NoClassDefFoundError ncdfe) {
                this.status = Status.UNSUPPORTED;
                this.problem = ncdfe;
            }
            catch (Throwable t) {
                this.status = Status.UNINSTANTIABLE;
                this.problem = t;
            }
        }
        return this.status;
    }

    private void digForDependencies(Class clazz) {
        clazz.getDeclaredMethods();
        clazz.getDeclaredFields();
        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            this.digForDependencies(superClass);
        }
    }

    public String getRestrictTo() {
        return this.restrictTo;
    }

    public Boolean getSkipSetters() {
        return this.skipSetters;
    }

    public ToolInfo createInfo() {
        ToolInfo info = null;
        Status status = this.getStatus();
        switch (status) {
            case VALID: {
                info = new ToolInfo(this.getKey(), this.getToolClass());
                break;
            }
            case OLD: {
                info = new OldToolInfo(this.getKey(), this.getToolClass());
                break;
            }
            default: {
                throw this.problem == null ? new ConfigurationException((Configuration)this, this.getError(status)) : new ConfigurationException(this, this.getError(status), this.problem);
            }
        }
        info.restrictTo(this.getRestrictTo());
        if (this.getSkipSetters() != null) {
            info.setSkipSetters(this.getSkipSetters());
        }
        info.addProperties(this.getPropertyMap());
        return info;
    }

    private final String getError(Status status) {
        switch (status) {
            case NONE: {
                return "No classname set for: " + this;
            }
            case MISSING: {
                return "Couldn't find tool class in the classpath for: " + this + "(" + this.problem + ")";
            }
            case UNSUPPORTED: {
                return "Couldn't find necessary supporting classes for: " + this + "(" + this.problem + ")";
            }
            case UNINSTANTIABLE: {
                return "Couldn't instantiate instance of tool for: " + this + "(" + this.problem + ")";
            }
        }
        return "";
    }

    @Override
    public void addConfiguration(Configuration config) {
        super.addConfiguration(config);
        if (config instanceof ToolConfiguration) {
            ToolConfiguration that = (ToolConfiguration)config;
            if (that.getClassname() != null) {
                this.setClassname(that.getClassname());
            }
            if (that.getRestrictTo() != null) {
                this.setRestrictTo(that.getRestrictTo());
            }
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (this.getKey() == null) {
            throw new NullKeyException(this);
        }
        Status status = this.getStatus();
        switch (status) {
            case VALID: 
            case OLD: {
                break;
            }
            default: {
                throw new ConfigurationException((Configuration)this, this.getError(status));
            }
        }
    }

    @Override
    public int compareTo(Configuration conf) {
        if (!(conf instanceof ToolConfiguration)) {
            throw new UnsupportedOperationException("ToolConfigurations can only be compared to other ToolConfigurations");
        }
        ToolConfiguration tool = (ToolConfiguration)conf;
        if (this.getKey() == null && tool.getKey() == null) {
            return 0;
        }
        if (this.getKey() == null) {
            return -1;
        }
        if (tool.getKey() == null) {
            return 1;
        }
        return this.getKey().compareTo(tool.getKey());
    }

    @Override
    public int hashCode() {
        if (this.getKey() == null) {
            return super.hashCode();
        }
        return this.getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getKey() == null || !(obj instanceof ToolConfiguration)) {
            return super.equals(obj);
        }
        return this.getKey().equals(((ToolConfiguration)obj).getKey());
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        if (this.getClassname() == null) {
            out.append("Tool '");
            out.append(this.key);
        } else {
            switch (this.getStatus()) {
                case VALID: {
                    break;
                }
                case OLD: {
                    out.append("Old ");
                    break;
                }
                case NONE: 
                case MISSING: {
                    out.append("Invalid ");
                    break;
                }
                case UNSUPPORTED: {
                    out.append("Unsupported ");
                    break;
                }
                case UNINSTANTIABLE: {
                    out.append("Unusable ");
                    break;
                }
            }
            out.append("Tool '");
            out.append(this.getKey());
        }
        out.append("' ");
        out.append("=> ");
        out.append(this.getClassname());
        if (this.getRestrictTo() != null) {
            out.append(" only for '");
            out.append(this.getRestrictTo());
            out.append('\'');
        }
        out.append(" ");
        this.appendProperties(out);
        return out.toString();
    }

    private static enum Status {
        VALID,
        OLD,
        NONE,
        MISSING,
        UNSUPPORTED,
        UNINSTANTIABLE;

    }
}


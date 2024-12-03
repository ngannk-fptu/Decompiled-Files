/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.readthrough;

import java.util.Properties;

public class ReadThroughCacheConfiguration
implements Cloneable {
    public static final String NAME_KEY = "name";
    public static final String GET_KEY = "get";
    private boolean modeGet = true;
    private String name = null;
    private volatile boolean valid = false;

    public ReadThroughCacheConfiguration() {
        this.validate();
    }

    public ReadThroughCacheConfiguration fromProperties(Properties properties) {
        this.valid = false;
        if (properties != null) {
            for (String property : properties.stringPropertyNames()) {
                String stringValue = properties.getProperty(property).trim();
                if (GET_KEY.equals(property)) {
                    this.setModeGet(Boolean.parseBoolean(stringValue));
                    continue;
                }
                if (NAME_KEY.equals(property)) {
                    this.setName(stringValue);
                    continue;
                }
                throw new IllegalArgumentException("Unrecognized ReadThrough cache config key: " + property);
            }
        }
        return this.build();
    }

    public Properties toProperties() {
        Properties p = new Properties();
        p.setProperty(NAME_KEY, this.getName());
        p.setProperty(GET_KEY, Boolean.toString(this.isModeGet()));
        return p;
    }

    public ReadThroughCacheConfiguration build() {
        this.validate();
        return this;
    }

    private void validate() {
        this.valid = true;
    }

    private void checkValid() {
        if (!this.valid) {
            throw new IllegalStateException("RefreshAheadCacheConfig not built yet");
        }
    }

    public ReadThroughCacheConfiguration modeGet(boolean modeGet) {
        this.setModeGet(modeGet);
        return this;
    }

    public boolean isModeGet() {
        this.checkValid();
        return this.modeGet;
    }

    public void setModeGet(boolean modeGet) {
        this.valid = false;
        this.modeGet = modeGet;
    }

    public String getName() {
        return this.name;
    }

    public ReadThroughCacheConfiguration setName(String name) {
        this.valid = false;
        this.name = name;
        return this;
    }

    public ReadThroughCacheConfiguration name(String name) {
        this.setName(name);
        return this;
    }

    public String toString() {
        return this.toProperties().toString();
    }
}


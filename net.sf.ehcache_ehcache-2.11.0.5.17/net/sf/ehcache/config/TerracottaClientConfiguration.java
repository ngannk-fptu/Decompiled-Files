/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.InvalidConfigurationException;

public class TerracottaClientConfiguration
implements Cloneable {
    public static final boolean DEFAULT_REJOIN_VALUE = false;
    public static final boolean DEFAULT_WAN_ENABLED_TSA_VALUE = false;
    private static final String TC_CONFIG_HEADER = "<tc:tc-config xmlns:tc=\"http://www.terracotta.org/config\">";
    private static final String TC_CONFIG_FOOTER = "</tc:tc-config>";
    private String url;
    private String embeddedConfig;
    private boolean rejoin = false;
    private boolean wanEnabledTSA = false;
    private volatile boolean configFrozen;

    public TerracottaClientConfiguration clone() throws CloneNotSupportedException {
        return (TerracottaClientConfiguration)super.clone();
    }

    public final TerracottaClientConfiguration url(String url) {
        this.setUrl(url);
        return this;
    }

    public final TerracottaClientConfiguration url(String host, String port) {
        if (((String)host).contains(":")) {
            host = "[" + (String)host + "]";
        }
        this.setUrl((String)host + ":" + port);
        return this;
    }

    public final void setUrl(String url) {
        this.url = url;
        this.validateConfiguration();
    }

    public final String getUrl() {
        return this.url;
    }

    public final void extractTcconfig(String text) {
        this.embeddedConfig = text;
        this.validateConfiguration();
    }

    public final String getEmbeddedConfig() {
        return TC_CONFIG_HEADER + this.embeddedConfig + TC_CONFIG_FOOTER;
    }

    public final String getOriginalEmbeddedConfig() {
        return this.embeddedConfig;
    }

    public final boolean isUrlConfig() {
        return this.url != null;
    }

    private void validateConfiguration() {
        if (this.url != null && this.embeddedConfig != null) {
            throw new InvalidConfigurationException("It is invalid to specify both a config url and an embedded config in the <terracottaConfig> element.");
        }
    }

    public boolean isRejoin() {
        return this.rejoin;
    }

    public void setRejoin(boolean rejoin) {
        if (this.configFrozen) {
            throw new CacheException("Cannot enable/disable rejoin once config has been frozen");
        }
        this.rejoin = rejoin;
    }

    public TerracottaClientConfiguration rejoin(boolean rejoin) {
        this.setRejoin(rejoin);
        return this;
    }

    public boolean isWanEnabledTSA() {
        return this.wanEnabledTSA;
    }

    public void setWanEnabledTSA(boolean wanEnabledTSA) {
        if (this.configFrozen) {
            throw new CacheException("Cannot set wanEnabledTSA once config has been frozen");
        }
        this.wanEnabledTSA = wanEnabledTSA;
    }

    public TerracottaClientConfiguration wanEnabledTSA(boolean wanEnabledTSA) {
        this.setWanEnabledTSA(wanEnabledTSA);
        return this;
    }

    public void freezeConfig() {
        this.configFrozen = true;
    }
}


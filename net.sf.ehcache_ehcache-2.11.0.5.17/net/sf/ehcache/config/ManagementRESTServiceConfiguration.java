/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.util.counter.sampled.SampledCounterConfig;
import net.sf.ehcache.util.counter.sampled.SampledRateCounterConfig;

public class ManagementRESTServiceConfiguration {
    public static final String DEFAULT_BIND = "0.0.0.0:9888";
    public static final String NO_BIND = "";
    public static final String AUTO_LOCATION = "";
    public static final int DEFAULT_SECURITY_SVC_TIMEOUT = 5000;
    private volatile boolean enabled = false;
    private volatile String securityServiceLocation;
    private volatile boolean sslEnabled;
    private volatile boolean needClientAuth;
    private volatile int securityServiceTimeout = 5000;
    private volatile String bind = "0.0.0.0:9888";
    private volatile int sampleHistorySize = 30;
    private volatile int sampleIntervalSeconds = 1;
    private volatile int sampleSearchIntervalSeconds = 10;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecurityServiceLocation() {
        return this.securityServiceLocation;
    }

    public void setSecurityServiceLocation(String securityServiceURL) {
        this.securityServiceLocation = securityServiceURL;
    }

    public int getSecurityServiceTimeout() {
        return this.securityServiceTimeout;
    }

    public void setSecurityServiceTimeout(int securityServiceTimeout) {
        this.securityServiceTimeout = securityServiceTimeout;
    }

    public String getBind() {
        return this.bind;
    }

    public String getHost() {
        if (this.bind == null) {
            return null;
        }
        return this.bind.split("\\:")[0];
    }

    public int getPort() {
        if (this.bind == null) {
            return -1;
        }
        String[] split = this.bind.split("\\:");
        if (split.length != 2) {
            return -1;
        }
        return Integer.parseInt(split[1]);
    }

    public boolean isSslEnabled() {
        return this.sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public boolean isNeedClientAuth() {
        return this.needClientAuth;
    }

    public void setNeedClientAuth(boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public int getSampleHistorySize() {
        return this.sampleHistorySize;
    }

    public void setSampleHistorySize(int sampleHistorySize) {
        this.sampleHistorySize = sampleHistorySize;
    }

    public int getSampleIntervalSeconds() {
        return this.sampleIntervalSeconds;
    }

    public void setSampleIntervalSeconds(int sampleIntervalSeconds) {
        this.sampleIntervalSeconds = sampleIntervalSeconds;
    }

    public int getSampleSearchIntervalSeconds() {
        return this.sampleSearchIntervalSeconds;
    }

    public void setSampleSearchIntervalSeconds(int sampleSearchInterval) {
        this.sampleSearchIntervalSeconds = sampleSearchInterval;
    }

    public SampledCounterConfig makeSampledCounterConfig() {
        return new SampledCounterConfig(this.getSampleIntervalSeconds(), this.getSampleHistorySize(), true, 0L);
    }

    public SampledRateCounterConfig makeSampledGetRateCounterConfig() {
        return new SampledRateCounterConfig(this.getSampleIntervalSeconds(), this.getSampleHistorySize(), true);
    }

    public SampledRateCounterConfig makeSampledSearchRateCounterConfig() {
        return new SampledRateCounterConfig(this.getSampleSearchIntervalSeconds(), this.getSampleHistorySize(), true);
    }

    public String toString() {
        return "ManagementRESTServiceConfiguration [enabled=" + this.enabled + ", securityServiceLocation=" + this.securityServiceLocation + ", sslEnabled=" + this.sslEnabled + ", needClientAuth=" + this.needClientAuth + ", securityServiceTimeout=" + this.securityServiceTimeout + ", bind=" + this.bind + ", sampleHistorySize=" + this.sampleHistorySize + ", sampleIntervalSeconds=" + this.sampleIntervalSeconds + ", sampleSearchIntervalSeconds=" + this.sampleSearchIntervalSeconds + "]";
    }
}


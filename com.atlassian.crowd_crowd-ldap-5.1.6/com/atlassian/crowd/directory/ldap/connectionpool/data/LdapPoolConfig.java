/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.diff.NormalizingDiffBuilder
 *  com.google.common.base.MoreObjects
 *  org.apache.commons.lang3.builder.DiffResult
 *  org.apache.commons.lang3.builder.Diffable
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.ldap.connectionpool.data;

import com.atlassian.crowd.common.diff.NormalizingDiffBuilder;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LdapPoolConfig
implements Diffable<LdapPoolConfig> {
    @JsonProperty(value="maxIdlePerKey")
    private final int maxIdlePerKey;
    @JsonProperty(value="maxTotal")
    private final int maxTotal;
    @JsonProperty(value="maxTotalPerKey")
    private final int maxTotalPerKey;
    @JsonProperty(value="minIdlePerKey")
    private final int minIdlePerKey;
    @JsonProperty(value="blockWhenExhausted")
    private final boolean blockWhenExhausted;
    @JsonProperty(value="evictionPolicyClassName")
    private final String evictionPolicyClassName;
    @JsonProperty(value="fairness")
    private final boolean fairness;
    @JsonProperty(value="jmxEnabled")
    private final boolean jmxEnabled;
    @JsonProperty(value="jmxNameBase")
    private final String jmxNameBase;
    @JsonProperty(value="jmxNamePrefix")
    private final String jmxNamePrefix;
    @JsonProperty(value="lifo")
    private final boolean lifo;
    @JsonProperty(value="maxWaitMillis")
    private final long maxWaitMillis;
    @JsonProperty(value="minEvictableIdleTimeMillis")
    private final long minEvictableIdleTimeMillis;
    @JsonProperty(value="numTestsPerEvictionRun")
    private final int numTestsPerEvictionRun;
    @JsonProperty(value="softMinEvictableIdleTimeMillis")
    private final long softMinEvictableIdleTimeMillis;
    @JsonProperty(value="testOnBorrow")
    private final boolean testOnBorrow;
    @JsonProperty(value="testOnCreate")
    private final boolean testOnCreate;
    @JsonProperty(value="testOnReturn")
    private final boolean testOnReturn;
    @JsonProperty(value="testWhileIdle")
    private final boolean testWhileIdle;
    @JsonProperty(value="timeBetweenEvictionRunsMillis")
    private final long timeBetweenEvictionRunsMillis;

    @JsonCreator
    protected LdapPoolConfig(@JsonProperty(value="maxIdlePerKey") int maxIdlePerKey, @JsonProperty(value="maxTotal") int maxTotal, @JsonProperty(value="maxTotalPerKey") int maxTotalPerKey, @JsonProperty(value="minIdlePerKey") int minIdlePerKey, @JsonProperty(value="blockWhenExhausted") boolean blockWhenExhausted, @JsonProperty(value="evictionPolicyClassName") String evictionPolicyClassName, @JsonProperty(value="fairness") boolean fairness, @JsonProperty(value="jmxEnabled") boolean jmxEnabled, @JsonProperty(value="jmxNameBase") String jmxNameBase, @JsonProperty(value="jmxNamePrefix") String jmxNamePrefix, @JsonProperty(value="lifo") boolean lifo, @JsonProperty(value="maxWaitMillis") long maxWaitMillis, @JsonProperty(value="minEvictableIdleTimeMillis") long minEvictableIdleTimeMillis, @JsonProperty(value="numTestsPerEvictionRun") int numTestsPerEvictionRun, @JsonProperty(value="softMinEvictableIdleTimeMillis") long softMinEvictableIdleTimeMillis, @JsonProperty(value="testOnBorrow") boolean testOnBorrow, @JsonProperty(value="testOnCreate") boolean testOnCreate, @JsonProperty(value="testOnReturn") boolean testOnReturn, @JsonProperty(value="testWhileIdle") boolean testWhileIdle, @JsonProperty(value="timeBetweenEvictionRunsMillis") long timeBetweenEvictionRunsMillis) {
        this.maxIdlePerKey = maxIdlePerKey;
        this.maxTotal = maxTotal;
        this.maxTotalPerKey = maxTotalPerKey;
        this.minIdlePerKey = minIdlePerKey;
        this.blockWhenExhausted = blockWhenExhausted;
        this.evictionPolicyClassName = evictionPolicyClassName;
        this.fairness = fairness;
        this.jmxEnabled = jmxEnabled;
        this.jmxNameBase = jmxNameBase;
        this.jmxNamePrefix = jmxNamePrefix;
        this.lifo = lifo;
        this.maxWaitMillis = maxWaitMillis;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        this.testOnBorrow = testOnBorrow;
        this.testOnCreate = testOnCreate;
        this.testOnReturn = testOnReturn;
        this.testWhileIdle = testWhileIdle;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMaxIdlePerKey() {
        return this.maxIdlePerKey;
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public int getMaxTotalPerKey() {
        return this.maxTotalPerKey;
    }

    public int getMinIdlePerKey() {
        return this.minIdlePerKey;
    }

    public boolean isBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    public String getEvictionPolicyClassName() {
        return this.evictionPolicyClassName;
    }

    public boolean isFairness() {
        return this.fairness;
    }

    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    public String getJmxNameBase() {
        return this.jmxNameBase;
    }

    public String getJmxNamePrefix() {
        return this.jmxNamePrefix;
    }

    public boolean isLifo() {
        return this.lifo;
    }

    public long getMaxWaitMillis() {
        return this.maxWaitMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return this.softMinEvictableIdleTimeMillis;
    }

    public boolean isTestOnBorrow() {
        return this.testOnBorrow;
    }

    public boolean isTestOnCreate() {
        return this.testOnCreate;
    }

    public boolean isTestOnReturn() {
        return this.testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return this.testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LdapPoolConfig that = (LdapPoolConfig)o;
        return Objects.equals(this.getMaxIdlePerKey(), that.getMaxIdlePerKey()) && Objects.equals(this.getMaxTotal(), that.getMaxTotal()) && Objects.equals(this.getMaxTotalPerKey(), that.getMaxTotalPerKey()) && Objects.equals(this.getMinIdlePerKey(), that.getMinIdlePerKey()) && Objects.equals(this.isBlockWhenExhausted(), that.isBlockWhenExhausted()) && Objects.equals(this.getEvictionPolicyClassName(), that.getEvictionPolicyClassName()) && Objects.equals(this.isFairness(), that.isFairness()) && Objects.equals(this.isJmxEnabled(), that.isJmxEnabled()) && Objects.equals(this.getJmxNameBase(), that.getJmxNameBase()) && Objects.equals(this.getJmxNamePrefix(), that.getJmxNamePrefix()) && Objects.equals(this.isLifo(), that.isLifo()) && Objects.equals(this.getMaxWaitMillis(), that.getMaxWaitMillis()) && Objects.equals(this.getMinEvictableIdleTimeMillis(), that.getMinEvictableIdleTimeMillis()) && Objects.equals(this.getNumTestsPerEvictionRun(), that.getNumTestsPerEvictionRun()) && Objects.equals(this.getSoftMinEvictableIdleTimeMillis(), that.getSoftMinEvictableIdleTimeMillis()) && Objects.equals(this.isTestOnBorrow(), that.isTestOnBorrow()) && Objects.equals(this.isTestOnCreate(), that.isTestOnCreate()) && Objects.equals(this.isTestOnReturn(), that.isTestOnReturn()) && Objects.equals(this.isTestWhileIdle(), that.isTestWhileIdle()) && Objects.equals(this.getTimeBetweenEvictionRunsMillis(), that.getTimeBetweenEvictionRunsMillis());
    }

    public int hashCode() {
        return Objects.hash(this.getMaxIdlePerKey(), this.getMaxTotal(), this.getMaxTotalPerKey(), this.getMinIdlePerKey(), this.isBlockWhenExhausted(), this.getEvictionPolicyClassName(), this.isFairness(), this.isJmxEnabled(), this.getJmxNameBase(), this.getJmxNamePrefix(), this.isLifo(), this.getMaxWaitMillis(), this.getMinEvictableIdleTimeMillis(), this.getNumTestsPerEvictionRun(), this.getSoftMinEvictableIdleTimeMillis(), this.isTestOnBorrow(), this.isTestOnCreate(), this.isTestOnReturn(), this.isTestWhileIdle(), this.getTimeBetweenEvictionRunsMillis());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("maxIdlePerKey", this.getMaxIdlePerKey()).add("maxTotal", this.getMaxTotal()).add("maxTotalPerKey", this.getMaxTotalPerKey()).add("minIdlePerKey", this.getMinIdlePerKey()).add("blockWhenExhausted", this.isBlockWhenExhausted()).add("evictionPolicyClassName", (Object)this.getEvictionPolicyClassName()).add("fairness", this.isFairness()).add("jmxEnabled", this.isJmxEnabled()).add("jmxNameBase", (Object)this.getJmxNameBase()).add("jmxNamePrefix", (Object)this.getJmxNamePrefix()).add("lifo", this.isLifo()).add("maxWaitMillis", this.getMaxWaitMillis()).add("minEvictableIdleTimeMillis", this.getMinEvictableIdleTimeMillis()).add("numTestsPerEvictionRun", this.getNumTestsPerEvictionRun()).add("softMinEvictableIdleTimeMillis", this.getSoftMinEvictableIdleTimeMillis()).add("testOnBorrow", this.isTestOnBorrow()).add("testOnCreate", this.isTestOnCreate()).add("testOnReturn", this.isTestOnReturn()).add("testWhileIdle", this.isTestWhileIdle()).add("timeBetweenEvictionRunsMillis", this.getTimeBetweenEvictionRunsMillis()).toString();
    }

    public DiffResult<LdapPoolConfig> diff(LdapPoolConfig that) {
        return new NormalizingDiffBuilder((Object)this, (Object)that, ToStringStyle.JSON_STYLE).append("maxIdlePerKey", this.maxIdlePerKey, that.maxIdlePerKey).append("maxTotal", this.maxTotal, that.maxTotal).append("maxTotalPerKey", this.maxTotalPerKey, that.maxTotalPerKey).append("minIdlePerKey", this.minIdlePerKey, that.minIdlePerKey).append("blockWhenExhausted", this.blockWhenExhausted, that.blockWhenExhausted).append("evictionPolicyClassName", (Object)this.evictionPolicyClassName, (Object)that.evictionPolicyClassName).append("fairness", this.fairness, that.fairness).append("jmxEnabled", this.jmxEnabled, that.jmxEnabled).append("jmxNameBase", (Object)this.jmxNameBase, (Object)that.jmxNameBase).append("jmxNamePrefix", (Object)this.jmxNamePrefix, (Object)that.jmxNamePrefix).append("lifo", this.lifo, that.lifo).append("maxWaitMillis", this.maxWaitMillis, that.maxWaitMillis).append("minEvictableIdleTimeMillis", this.minEvictableIdleTimeMillis, that.minEvictableIdleTimeMillis).append("numTestsPerEvictionRun", this.numTestsPerEvictionRun, that.numTestsPerEvictionRun).append("softMinEvictableIdleTimeMillis", this.softMinEvictableIdleTimeMillis, that.softMinEvictableIdleTimeMillis).append("testOnBorrow", this.testOnBorrow, that.testOnBorrow).append("testOnCreate", this.testOnCreate, that.testOnCreate).append("testOnReturn", this.testOnReturn, that.testOnReturn).append("testWhileIdle", this.testWhileIdle, that.testWhileIdle).append("timeBetweenEvictionRunsMillis", this.timeBetweenEvictionRunsMillis, that.timeBetweenEvictionRunsMillis).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LdapPoolConfig data) {
        return new Builder(data);
    }

    public static final class Builder {
        private int maxIdlePerKey;
        private int maxTotal;
        private int maxTotalPerKey;
        private int minIdlePerKey;
        private boolean blockWhenExhausted;
        private String evictionPolicyClassName;
        private boolean fairness;
        private boolean jmxEnabled;
        private String jmxNameBase;
        private String jmxNamePrefix;
        private boolean lifo;
        private long maxWaitMillis;
        private long minEvictableIdleTimeMillis;
        private int numTestsPerEvictionRun;
        private long softMinEvictableIdleTimeMillis;
        private boolean testOnBorrow;
        private boolean testOnCreate;
        private boolean testOnReturn;
        private boolean testWhileIdle;
        private long timeBetweenEvictionRunsMillis;

        private Builder() {
        }

        private Builder(LdapPoolConfig initialData) {
            this.maxIdlePerKey = initialData.maxIdlePerKey;
            this.maxTotal = initialData.maxTotal;
            this.maxTotalPerKey = initialData.maxTotalPerKey;
            this.minIdlePerKey = initialData.minIdlePerKey;
            this.blockWhenExhausted = initialData.blockWhenExhausted;
            this.evictionPolicyClassName = initialData.evictionPolicyClassName;
            this.fairness = initialData.fairness;
            this.jmxEnabled = initialData.jmxEnabled;
            this.jmxNameBase = initialData.jmxNameBase;
            this.jmxNamePrefix = initialData.jmxNamePrefix;
            this.lifo = initialData.lifo;
            this.maxWaitMillis = initialData.maxWaitMillis;
            this.minEvictableIdleTimeMillis = initialData.minEvictableIdleTimeMillis;
            this.numTestsPerEvictionRun = initialData.numTestsPerEvictionRun;
            this.softMinEvictableIdleTimeMillis = initialData.softMinEvictableIdleTimeMillis;
            this.testOnBorrow = initialData.testOnBorrow;
            this.testOnCreate = initialData.testOnCreate;
            this.testOnReturn = initialData.testOnReturn;
            this.testWhileIdle = initialData.testWhileIdle;
            this.timeBetweenEvictionRunsMillis = initialData.timeBetweenEvictionRunsMillis;
        }

        public Builder setMaxIdlePerKey(int maxIdlePerKey) {
            this.maxIdlePerKey = maxIdlePerKey;
            return this;
        }

        public Builder setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        public Builder setMaxTotalPerKey(int maxTotalPerKey) {
            this.maxTotalPerKey = maxTotalPerKey;
            return this;
        }

        public Builder setMinIdlePerKey(int minIdlePerKey) {
            this.minIdlePerKey = minIdlePerKey;
            return this;
        }

        public Builder setBlockWhenExhausted(boolean blockWhenExhausted) {
            this.blockWhenExhausted = blockWhenExhausted;
            return this;
        }

        public Builder setEvictionPolicyClassName(String evictionPolicyClassName) {
            this.evictionPolicyClassName = evictionPolicyClassName;
            return this;
        }

        public Builder setFairness(boolean fairness) {
            this.fairness = fairness;
            return this;
        }

        public Builder setJmxEnabled(boolean jmxEnabled) {
            this.jmxEnabled = jmxEnabled;
            return this;
        }

        public Builder setJmxNameBase(String jmxNameBase) {
            this.jmxNameBase = jmxNameBase;
            return this;
        }

        public Builder setJmxNamePrefix(String jmxNamePrefix) {
            this.jmxNamePrefix = jmxNamePrefix;
            return this;
        }

        public Builder setLifo(boolean lifo) {
            this.lifo = lifo;
            return this;
        }

        public Builder setMaxWaitMillis(long maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
            return this;
        }

        public Builder setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
            this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
            return this;
        }

        public Builder setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
            this.numTestsPerEvictionRun = numTestsPerEvictionRun;
            return this;
        }

        public Builder setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
            this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
            return this;
        }

        public Builder setTestOnBorrow(boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
            return this;
        }

        public Builder setTestOnCreate(boolean testOnCreate) {
            this.testOnCreate = testOnCreate;
            return this;
        }

        public Builder setTestOnReturn(boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
            return this;
        }

        public Builder setTestWhileIdle(boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
            return this;
        }

        public Builder setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
            return this;
        }

        public LdapPoolConfig build() {
            return new LdapPoolConfig(this.maxIdlePerKey, this.maxTotal, this.maxTotalPerKey, this.minIdlePerKey, this.blockWhenExhausted, this.evictionPolicyClassName, this.fairness, this.jmxEnabled, this.jmxNameBase, this.jmxNamePrefix, this.lifo, this.maxWaitMillis, this.minEvictableIdleTimeMillis, this.numTestsPerEvictionRun, this.softMinEvictableIdleTimeMillis, this.testOnBorrow, this.testOnCreate, this.testOnReturn, this.testWhileIdle, this.timeBetweenEvictionRunsMillis);
        }
    }

    private static interface Fields {
        public static final String MAX_IDLE_PER_KEY = "maxIdlePerKey";
        public static final String MAX_TOTAL = "maxTotal";
        public static final String MAX_TOTAL_PER_KEY = "maxTotalPerKey";
        public static final String MIN_IDLE_PER_KEY = "minIdlePerKey";
        public static final String BLOCK_WHEN_EXHAUSTED = "blockWhenExhausted";
        public static final String EVICTION_POLICY_CLASS_NAME = "evictionPolicyClassName";
        public static final String FAIRNESS = "fairness";
        public static final String JMX_ENABLED = "jmxEnabled";
        public static final String JMX_NAME_BASE = "jmxNameBase";
        public static final String JMX_NAME_PREFIX = "jmxNamePrefix";
        public static final String LIFO = "lifo";
        public static final String MAX_WAIT_MILLIS = "maxWaitMillis";
        public static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
        public static final String NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";
        public static final String SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = "softMinEvictableIdleTimeMillis";
        public static final String TEST_ON_BORROW = "testOnBorrow";
        public static final String TEST_ON_CREATE = "testOnCreate";
        public static final String TEST_ON_RETURN = "testOnReturn";
        public static final String TEST_WHILE_IDLE = "testWhileIdle";
        public static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
    }
}


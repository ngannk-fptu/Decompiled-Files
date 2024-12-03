/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.event.BaseEventSource;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;

public class DynamicCombinedConfiguration
extends CombinedConfiguration {
    private static final ThreadLocal<CurrentConfigHolder> CURRENT_CONFIG = new ThreadLocal();
    private final ConcurrentMap<String, CombinedConfiguration> configs = new ConcurrentHashMap<String, CombinedConfiguration>();
    private final List<ConfigData> configurations = new ArrayList<ConfigData>();
    private final Map<String, Configuration> namedConfigurations = new HashMap<String, Configuration>();
    private String keyPattern;
    private NodeCombiner nodeCombiner;
    private String loggerName = DynamicCombinedConfiguration.class.getName();
    private final ConfigurationInterpolator localSubst;

    public DynamicCombinedConfiguration(NodeCombiner comb) {
        this.setNodeCombiner(comb);
        this.initLogger(new ConfigurationLogger(DynamicCombinedConfiguration.class));
        this.localSubst = this.initLocalInterpolator();
    }

    public DynamicCombinedConfiguration() {
        this.initLogger(new ConfigurationLogger(DynamicCombinedConfiguration.class));
        this.localSubst = this.initLocalInterpolator();
    }

    public void setKeyPattern(String pattern) {
        this.keyPattern = pattern;
    }

    public String getKeyPattern() {
        return this.keyPattern;
    }

    public void setLoggerName(String name) {
        this.loggerName = name;
    }

    @Override
    public NodeCombiner getNodeCombiner() {
        return this.nodeCombiner;
    }

    @Override
    public void setNodeCombiner(NodeCombiner nodeCombiner) {
        if (nodeCombiner == null) {
            throw new IllegalArgumentException("Node combiner must not be null!");
        }
        this.nodeCombiner = nodeCombiner;
        this.invalidateAll();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addConfiguration(Configuration config, String name, String at) {
        this.beginWrite(true);
        try {
            ConfigData cd = new ConfigData(config, name, at);
            this.configurations.add(cd);
            if (name != null) {
                this.namedConfigurations.put(name, config);
            }
            this.configs.clear();
        }
        finally {
            this.endWrite();
        }
    }

    @Override
    public int getNumberOfConfigurations() {
        this.beginRead(false);
        try {
            int n = this.configurations.size();
            return n;
        }
        finally {
            this.endRead();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Configuration getConfiguration(int index) {
        this.beginRead(false);
        try {
            ConfigData cd = this.configurations.get(index);
            Configuration configuration = cd.getConfiguration();
            return configuration;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public Configuration getConfiguration(String name) {
        this.beginRead(false);
        try {
            Configuration configuration = this.namedConfigurations.get(name);
            return configuration;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public Set<String> getConfigurationNames() {
        this.beginRead(false);
        try {
            Set<String> set = this.namedConfigurations.keySet();
            return set;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public Configuration removeConfiguration(String name) {
        Configuration conf = this.getConfiguration(name);
        if (conf != null) {
            this.removeConfiguration(conf);
        }
        return conf;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeConfiguration(Configuration config) {
        this.beginWrite(false);
        try {
            for (int index = 0; index < this.getNumberOfConfigurations(); ++index) {
                if (this.configurations.get(index).getConfiguration() != config) continue;
                this.removeConfigurationAt(index);
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.endWrite();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Configuration removeConfigurationAt(int index) {
        this.beginWrite(false);
        try {
            ConfigData cd = this.configurations.remove(index);
            if (cd.getName() != null) {
                this.namedConfigurations.remove(cd.getName());
            }
            Configuration configuration = cd.getConfiguration();
            return configuration;
        }
        finally {
            this.endWrite();
        }
    }

    @Override
    protected void addPropertyInternal(String key, Object value) {
        this.getCurrentConfig().addProperty(key, value);
    }

    @Override
    protected void clearInternal() {
        this.getCurrentConfig().clear();
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.getCurrentConfig().clearProperty(key);
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.getCurrentConfig().containsKey(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return this.getCurrentConfig().getBigDecimal(key, defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return this.getCurrentConfig().getBigDecimal(key);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return this.getCurrentConfig().getBigInteger(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return this.getCurrentConfig().getBigInteger(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getCurrentConfig().getBoolean(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.getCurrentConfig().getBoolean(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return this.getCurrentConfig().getBoolean(key);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return this.getCurrentConfig().getByte(key, defaultValue);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return this.getCurrentConfig().getByte(key, defaultValue);
    }

    @Override
    public byte getByte(String key) {
        return this.getCurrentConfig().getByte(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return this.getCurrentConfig().getDouble(key, defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return this.getCurrentConfig().getDouble(key, defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return this.getCurrentConfig().getDouble(key);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return this.getCurrentConfig().getFloat(key, defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return this.getCurrentConfig().getFloat(key, defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return this.getCurrentConfig().getFloat(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return this.getCurrentConfig().getInt(key, defaultValue);
    }

    @Override
    public int getInt(String key) {
        return this.getCurrentConfig().getInt(key);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return this.getCurrentConfig().getInteger(key, defaultValue);
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.getCurrentConfig().getKeys();
    }

    @Override
    protected Iterator<String> getKeysInternal(String prefix) {
        return this.getCurrentConfig().getKeys(prefix);
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        return this.getCurrentConfig().getList(key, defaultValue);
    }

    @Override
    public List<Object> getList(String key) {
        return this.getCurrentConfig().getList(key);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return this.getCurrentConfig().getLong(key, defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return this.getCurrentConfig().getLong(key, defaultValue);
    }

    @Override
    public long getLong(String key) {
        return this.getCurrentConfig().getLong(key);
    }

    @Override
    public Properties getProperties(String key) {
        return this.getCurrentConfig().getProperties(key);
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.getCurrentConfig().getProperty(key);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return this.getCurrentConfig().getShort(key, defaultValue);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return this.getCurrentConfig().getShort(key, defaultValue);
    }

    @Override
    public short getShort(String key) {
        return this.getCurrentConfig().getShort(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return this.getCurrentConfig().getString(key, defaultValue);
    }

    @Override
    public String getString(String key) {
        return this.getCurrentConfig().getString(key);
    }

    @Override
    public String[] getStringArray(String key) {
        return this.getCurrentConfig().getStringArray(key);
    }

    @Override
    protected boolean isEmptyInternal() {
        return this.getCurrentConfig().isEmpty();
    }

    @Override
    protected int sizeInternal() {
        return this.getCurrentConfig().size();
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        this.getCurrentConfig().setProperty(key, value);
    }

    @Override
    public Configuration subset(String prefix) {
        return this.getCurrentConfig().subset(prefix);
    }

    @Override
    protected void addNodesInternal(String key, Collection<? extends ImmutableNode> nodes) {
        this.getCurrentConfig().addNodes(key, nodes);
    }

    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(String key, boolean supportUpdates) {
        return this.getCurrentConfig().configurationAt(key, supportUpdates);
    }

    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(String key) {
        return this.getCurrentConfig().configurationAt(key);
    }

    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(String key) {
        return this.getCurrentConfig().configurationsAt(key);
    }

    @Override
    protected Object clearTreeInternal(String key) {
        this.getCurrentConfig().clearTree(key);
        return Collections.emptyList();
    }

    @Override
    protected int getMaxIndexInternal(String key) {
        return this.getCurrentConfig().getMaxIndex(key);
    }

    @Override
    public Configuration interpolatedConfiguration() {
        return this.getCurrentConfig().interpolatedConfiguration();
    }

    @Override
    public Configuration getSource(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null!");
        }
        return this.getCurrentConfig().getSource(key);
    }

    @Override
    public void clearEventListeners() {
        this.configs.values().forEach(BaseEventSource::clearEventListeners);
        super.clearEventListeners();
    }

    @Override
    public <T extends Event> void addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        this.configs.values().forEach(cc -> cc.addEventListener(eventType, listener));
        super.addEventListener(eventType, listener);
    }

    @Override
    public <T extends Event> boolean removeEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        this.configs.values().forEach(cc -> cc.removeEventListener(eventType, listener));
        return super.removeEventListener(eventType, listener);
    }

    @Override
    public void clearErrorListeners() {
        this.configs.values().forEach(BaseEventSource::clearErrorListeners);
        super.clearErrorListeners();
    }

    @Override
    public void invalidate() {
        this.getCurrentConfig().invalidate();
    }

    public void invalidateAll() {
        this.configs.values().forEach(CombinedConfiguration::invalidate);
    }

    @Override
    protected void beginRead(boolean optimize) {
        CurrentConfigHolder cch = this.ensureCurrentConfiguration();
        cch.incrementLockCount();
        if (!optimize && cch.getCurrentConfiguration() == null) {
            this.beginWrite(false);
            this.endWrite();
        }
        cch.getCurrentConfiguration().beginRead(optimize);
    }

    @Override
    protected void beginWrite(boolean optimize) {
        CurrentConfigHolder cch = this.ensureCurrentConfiguration();
        cch.incrementLockCount();
        super.beginWrite(optimize);
        if (!optimize && cch.getCurrentConfiguration() == null) {
            cch.setCurrentConfiguration(this.createChildConfiguration());
            this.configs.put(cch.getKey(), cch.getCurrentConfiguration());
            this.initChildConfiguration(cch.getCurrentConfiguration());
        }
    }

    @Override
    protected void endRead() {
        CURRENT_CONFIG.get().getCurrentConfiguration().endRead();
        this.releaseLock();
    }

    @Override
    protected void endWrite() {
        super.endWrite();
        this.releaseLock();
    }

    private void releaseLock() {
        CurrentConfigHolder cch = CURRENT_CONFIG.get();
        assert (cch != null) : "No current configuration!";
        if (cch.decrementLockCountAndCheckRelease()) {
            CURRENT_CONFIG.remove();
        }
    }

    private CombinedConfiguration getCurrentConfig() {
        String key;
        CombinedConfiguration config;
        this.beginRead(false);
        try {
            config = CURRENT_CONFIG.get().getCurrentConfiguration();
            key = CURRENT_CONFIG.get().getKey();
        }
        finally {
            this.endRead();
        }
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Returning config for " + key + ": " + config);
        }
        return config;
    }

    private CombinedConfiguration createChildConfiguration() {
        return new CombinedConfiguration(this.getNodeCombiner());
    }

    private void initChildConfiguration(CombinedConfiguration config) {
        if (this.loggerName != null) {
            config.setLogger(new ConfigurationLogger(this.loggerName));
        }
        config.setExpressionEngine(this.getExpressionEngine());
        config.setConversionExpressionEngine(this.getConversionExpressionEngine());
        config.setListDelimiterHandler(this.getListDelimiterHandler());
        this.copyEventListeners(config);
        this.configurations.forEach(data -> config.addConfiguration(data.getConfiguration(), data.getName(), data.getAt()));
        config.setSynchronizer(this.getSynchronizer());
    }

    private ConfigurationInterpolator initLocalInterpolator() {
        return new ConfigurationInterpolator(){

            @Override
            protected Lookup fetchLookupForPrefix(String prefix) {
                return ConfigurationInterpolator.nullSafeLookup(DynamicCombinedConfiguration.this.getInterpolator().getLookups().get(prefix));
            }
        };
    }

    private CurrentConfigHolder ensureCurrentConfiguration() {
        CurrentConfigHolder cch = CURRENT_CONFIG.get();
        if (cch == null) {
            String key = String.valueOf(this.localSubst.interpolate(this.keyPattern));
            cch = new CurrentConfigHolder(key);
            cch.setCurrentConfiguration((CombinedConfiguration)this.configs.get(key));
            CURRENT_CONFIG.set(cch);
        }
        return cch;
    }

    private static class CurrentConfigHolder {
        private CombinedConfiguration currentConfiguration;
        private final String key;
        private int lockCount;

        public CurrentConfigHolder(String curKey) {
            this.key = curKey;
        }

        public CombinedConfiguration getCurrentConfiguration() {
            return this.currentConfiguration;
        }

        public void setCurrentConfiguration(CombinedConfiguration currentConfiguration) {
            this.currentConfiguration = currentConfiguration;
        }

        public String getKey() {
            return this.key;
        }

        public void incrementLockCount() {
            ++this.lockCount;
        }

        public boolean decrementLockCountAndCheckRelease() {
            return --this.lockCount == 0;
        }
    }

    static class ConfigData {
        private final Configuration configuration;
        private final String name;
        private final String at;

        public ConfigData(Configuration config, String n, String at) {
            this.configuration = config;
            this.name = n;
            this.at = at;
        }

        public Configuration getConfiguration() {
            return this.configuration;
        }

        public String getName() {
            return this.name;
        }

        public String getAt() {
            return this.at;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class PatternSubtreeConfigurationWrapper
extends BaseHierarchicalConfiguration
implements FileBasedConfiguration {
    private final HierarchicalConfiguration<ImmutableNode> config;
    private final String path;
    private final boolean trailing;
    private final boolean init;

    public PatternSubtreeConfigurationWrapper(HierarchicalConfiguration<ImmutableNode> config, String path) {
        this.config = config;
        this.path = path;
        this.trailing = path.endsWith("/");
        this.init = true;
    }

    @Override
    protected void addPropertyInternal(String key, Object value) {
        this.config.addProperty(this.makePath(key), value);
    }

    @Override
    protected void clearInternal() {
        this.getConfig().clear();
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.config.clearProperty(this.makePath(key));
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.config.containsKey(this.makePath(key));
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return this.config.getBigDecimal(this.makePath(key), defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return this.config.getBigDecimal(this.makePath(key));
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return this.config.getBigInteger(this.makePath(key), defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return this.config.getBigInteger(this.makePath(key));
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.config.getBoolean(this.makePath(key), defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.config.getBoolean(this.makePath(key), defaultValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return this.config.getBoolean(this.makePath(key));
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return this.config.getByte(this.makePath(key), defaultValue);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return this.config.getByte(this.makePath(key), defaultValue);
    }

    @Override
    public byte getByte(String key) {
        return this.config.getByte(this.makePath(key));
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return this.config.getDouble(this.makePath(key), defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return this.config.getDouble(this.makePath(key), defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return this.config.getDouble(this.makePath(key));
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return this.config.getFloat(this.makePath(key), defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return this.config.getFloat(this.makePath(key), defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return this.config.getFloat(this.makePath(key));
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return this.config.getInt(this.makePath(key), defaultValue);
    }

    @Override
    public int getInt(String key) {
        return this.config.getInt(this.makePath(key));
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return this.config.getInteger(this.makePath(key), defaultValue);
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.config.getKeys(this.makePath());
    }

    @Override
    protected Iterator<String> getKeysInternal(String prefix) {
        return this.config.getKeys(this.makePath(prefix));
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        return this.config.getList(this.makePath(key), defaultValue);
    }

    @Override
    public List<Object> getList(String key) {
        return this.config.getList(this.makePath(key));
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return this.config.getLong(this.makePath(key), defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return this.config.getLong(this.makePath(key), defaultValue);
    }

    @Override
    public long getLong(String key) {
        return this.config.getLong(this.makePath(key));
    }

    @Override
    public Properties getProperties(String key) {
        return this.config.getProperties(this.makePath(key));
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.config.getProperty(this.makePath(key));
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return this.config.getShort(this.makePath(key), defaultValue);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return this.config.getShort(this.makePath(key), defaultValue);
    }

    @Override
    public short getShort(String key) {
        return this.config.getShort(this.makePath(key));
    }

    @Override
    public String getString(String key, String defaultValue) {
        return this.config.getString(this.makePath(key), defaultValue);
    }

    @Override
    public String getString(String key) {
        return this.config.getString(this.makePath(key));
    }

    @Override
    public String[] getStringArray(String key) {
        return this.config.getStringArray(this.makePath(key));
    }

    @Override
    protected boolean isEmptyInternal() {
        return this.getConfig().isEmpty();
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        this.getConfig().setProperty(key, value);
    }

    @Override
    public Configuration subset(String prefix) {
        return this.getConfig().subset(prefix);
    }

    @Override
    public ExpressionEngine getExpressionEngine() {
        return this.config.getExpressionEngine();
    }

    @Override
    public void setExpressionEngine(ExpressionEngine expressionEngine) {
        if (this.init) {
            this.config.setExpressionEngine(expressionEngine);
        } else {
            super.setExpressionEngine(expressionEngine);
        }
    }

    @Override
    protected void addNodesInternal(String key, Collection<? extends ImmutableNode> nodes) {
        this.getConfig().addNodes(key, nodes);
    }

    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(String key, boolean supportUpdates) {
        return this.config.configurationAt(this.makePath(key), supportUpdates);
    }

    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(String key) {
        return this.config.configurationAt(this.makePath(key));
    }

    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(String key) {
        return this.config.configurationsAt(this.makePath(key));
    }

    @Override
    protected Object clearTreeInternal(String key) {
        this.config.clearTree(this.makePath(key));
        return Collections.emptyList();
    }

    @Override
    protected int getMaxIndexInternal(String key) {
        return this.config.getMaxIndex(this.makePath(key));
    }

    @Override
    public Configuration interpolatedConfiguration() {
        return this.getConfig().interpolatedConfiguration();
    }

    @Override
    public <T extends Event> void addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        this.getConfig().addEventListener(eventType, listener);
    }

    @Override
    public <T extends Event> boolean removeEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        return this.getConfig().removeEventListener(eventType, listener);
    }

    @Override
    public <T extends Event> Collection<EventListener<? super T>> getEventListeners(EventType<T> eventType) {
        return this.getConfig().getEventListeners(eventType);
    }

    @Override
    public void clearEventListeners() {
        this.getConfig().clearEventListeners();
    }

    @Override
    public void clearErrorListeners() {
        this.getConfig().clearErrorListeners();
    }

    @Override
    public void write(Writer writer) throws ConfigurationException, IOException {
        this.fetchFileBased().write(writer);
    }

    @Override
    public void read(Reader reader) throws ConfigurationException, IOException {
        this.fetchFileBased().read(reader);
    }

    private BaseHierarchicalConfiguration getConfig() {
        return (BaseHierarchicalConfiguration)this.config.configurationAt(this.makePath());
    }

    private String makePath() {
        String pathPattern = this.trailing ? this.path.substring(0, this.path.length() - 1) : this.path;
        return this.substitute(pathPattern);
    }

    private String makePath(String item) {
        String pathPattern = (item.isEmpty() || item.startsWith("/")) && this.trailing ? this.path.substring(0, this.path.length() - 1) : (!item.startsWith("/") || !this.trailing ? this.path + "/" : this.path);
        return this.substitute(pathPattern) + item;
    }

    private String substitute(String pattern) {
        return Objects.toString(this.getInterpolator().interpolate(pattern), null);
    }

    private FileBased fetchFileBased() throws ConfigurationException {
        if (!(this.config instanceof FileBased)) {
            throw new ConfigurationException("Wrapped configuration does not implement FileBased! No I/O operations are supported.");
        }
        return (FileBased)((Object)this.config);
    }
}


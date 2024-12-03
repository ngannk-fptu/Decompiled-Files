/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;

public class CompositeConfiguration
extends AbstractConfiguration
implements Cloneable {
    private List<Configuration> configList = new LinkedList<Configuration>();
    private Configuration inMemoryConfiguration;
    private boolean inMemoryConfigIsChild;

    public CompositeConfiguration() {
        this.clear();
    }

    public CompositeConfiguration(Configuration inMemoryConfiguration) {
        this.configList.clear();
        this.inMemoryConfiguration = inMemoryConfiguration;
        this.configList.add(inMemoryConfiguration);
    }

    public CompositeConfiguration(Collection<? extends Configuration> configurations) {
        this(new BaseConfiguration(), configurations);
    }

    public CompositeConfiguration(Configuration inMemoryConfiguration, Collection<? extends Configuration> configurations) {
        this(inMemoryConfiguration);
        if (configurations != null) {
            configurations.forEach(this::addConfiguration);
        }
    }

    public void addConfiguration(Configuration config) {
        this.addConfiguration(config, false);
    }

    public void addConfiguration(Configuration config, boolean asInMemory) {
        this.beginWrite(false);
        try {
            if (!this.configList.contains(config)) {
                if (asInMemory) {
                    this.replaceInMemoryConfiguration(config);
                    this.inMemoryConfigIsChild = true;
                }
                if (!this.inMemoryConfigIsChild) {
                    this.configList.add(this.configList.indexOf(this.inMemoryConfiguration), config);
                } else {
                    this.configList.add(config);
                }
                if (config instanceof AbstractConfiguration) {
                    ((AbstractConfiguration)config).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
                }
            }
        }
        finally {
            this.endWrite();
        }
    }

    public void addConfigurationFirst(Configuration config) {
        this.addConfigurationFirst(config, false);
    }

    public void addConfigurationFirst(Configuration config, boolean asInMemory) {
        this.beginWrite(false);
        try {
            if (!this.configList.contains(config)) {
                if (asInMemory) {
                    this.replaceInMemoryConfiguration(config);
                    this.inMemoryConfigIsChild = true;
                }
                this.configList.add(0, config);
                if (config instanceof AbstractConfiguration) {
                    ((AbstractConfiguration)config).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
                }
            }
        }
        finally {
            this.endWrite();
        }
    }

    public void removeConfiguration(Configuration config) {
        this.beginWrite(false);
        try {
            if (!config.equals(this.inMemoryConfiguration)) {
                this.configList.remove(config);
            }
        }
        finally {
            this.endWrite();
        }
    }

    public int getNumberOfConfigurations() {
        this.beginRead(false);
        try {
            int n = this.configList.size();
            return n;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    protected void clearInternal() {
        this.configList.clear();
        this.inMemoryConfiguration = new BaseConfiguration();
        ((BaseConfiguration)this.inMemoryConfiguration).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
        ((BaseConfiguration)this.inMemoryConfiguration).setListDelimiterHandler(this.getListDelimiterHandler());
        this.configList.add(this.inMemoryConfiguration);
        this.inMemoryConfigIsChild = false;
    }

    @Override
    protected void addPropertyDirect(String key, Object token) {
        this.inMemoryConfiguration.addProperty(key, token);
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.configList.stream().filter(config -> config.containsKey(key)).findFirst().map(config -> config.getProperty(key)).orElse(null);
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        LinkedHashSet keys = new LinkedHashSet();
        this.configList.forEach(config -> config.getKeys().forEachRemaining(keys::add));
        return keys.iterator();
    }

    @Override
    protected Iterator<String> getKeysInternal(String key) {
        LinkedHashSet keys = new LinkedHashSet();
        this.configList.forEach(config -> config.getKeys(key).forEachRemaining(keys::add));
        return keys.iterator();
    }

    @Override
    protected boolean isEmptyInternal() {
        return this.configList.stream().allMatch(ImmutableConfiguration::isEmpty);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.configList.forEach(config -> config.clearProperty(key));
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.configList.stream().anyMatch(config -> config.containsKey(key));
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        ArrayList<Object> list = new ArrayList<Object>();
        Iterator<Configuration> it = this.configList.iterator();
        while (it.hasNext() && list.isEmpty()) {
            Configuration config = it.next();
            if (config == this.inMemoryConfiguration || !config.containsKey(key)) continue;
            this.appendListProperty(list, config, key);
        }
        this.appendListProperty(list, this.inMemoryConfiguration, key);
        if (list.isEmpty()) {
            List<Object> resultList = defaultValue;
            return resultList;
        }
        ListIterator<Object> lit = list.listIterator();
        while (lit.hasNext()) {
            lit.set(this.interpolate(lit.next()));
        }
        return list;
    }

    @Override
    public String[] getStringArray(String key) {
        List<Object> list = this.getList(key);
        String[] tokens = new String[list.size()];
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = String.valueOf(list.get(i));
        }
        return tokens;
    }

    public Configuration getConfiguration(int index) {
        this.beginRead(false);
        try {
            Configuration configuration = this.configList.get(index);
            return configuration;
        }
        finally {
            this.endRead();
        }
    }

    public Configuration getInMemoryConfiguration() {
        this.beginRead(false);
        try {
            Configuration configuration = this.inMemoryConfiguration;
            return configuration;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public Object clone() {
        try {
            CompositeConfiguration copy = (CompositeConfiguration)super.clone();
            copy.configList = new LinkedList<Configuration>();
            copy.inMemoryConfiguration = ConfigurationUtils.cloneConfiguration(this.getInMemoryConfiguration());
            copy.configList.add(copy.inMemoryConfiguration);
            this.configList.forEach(config -> {
                if (config != this.getInMemoryConfiguration()) {
                    copy.addConfiguration(ConfigurationUtils.cloneConfiguration(config));
                }
            });
            copy.cloneInterpolator(this);
            return copy;
        }
        catch (CloneNotSupportedException cnex) {
            throw new ConfigurationRuntimeException(cnex);
        }
    }

    @Override
    public void setListDelimiterHandler(ListDelimiterHandler listDelimiterHandler) {
        if (this.inMemoryConfiguration instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.inMemoryConfiguration).setListDelimiterHandler(listDelimiterHandler);
        }
        super.setListDelimiterHandler(listDelimiterHandler);
    }

    public Configuration getSource(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null!");
        }
        Configuration source = null;
        for (Configuration conf : this.configList) {
            if (!conf.containsKey(key)) continue;
            if (source != null) {
                throw new IllegalArgumentException("The key " + key + " is defined by multiple sources!");
            }
            source = conf;
        }
        return source;
    }

    private void replaceInMemoryConfiguration(Configuration config) {
        if (!this.inMemoryConfigIsChild) {
            this.configList.remove(this.inMemoryConfiguration);
        }
        this.inMemoryConfiguration = config;
    }

    private void appendListProperty(List<Object> dest, Configuration config, String key) {
        Object value = this.interpolate(config.getProperty(key));
        if (value != null) {
            if (value instanceof Collection) {
                Collection col = (Collection)value;
                dest.addAll(col);
            } else {
                dest.add(value);
            }
        }
    }
}


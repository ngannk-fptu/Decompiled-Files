/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigMemorySize;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigResolveOptions;
import com.typesafe.config.ConfigValue;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Config
extends ConfigMergeable {
    public ConfigObject root();

    public ConfigOrigin origin();

    @Override
    public Config withFallback(ConfigMergeable var1);

    public Config resolve();

    public Config resolve(ConfigResolveOptions var1);

    public boolean isResolved();

    public Config resolveWith(Config var1);

    public Config resolveWith(Config var1, ConfigResolveOptions var2);

    public void checkValid(Config var1, String ... var2);

    public boolean hasPath(String var1);

    public boolean hasPathOrNull(String var1);

    public boolean isEmpty();

    public Set<Map.Entry<String, ConfigValue>> entrySet();

    public boolean getIsNull(String var1);

    public boolean getBoolean(String var1);

    public Number getNumber(String var1);

    public int getInt(String var1);

    public long getLong(String var1);

    public double getDouble(String var1);

    public String getString(String var1);

    public <T extends Enum<T>> T getEnum(Class<T> var1, String var2);

    public ConfigObject getObject(String var1);

    public Config getConfig(String var1);

    public Object getAnyRef(String var1);

    public ConfigValue getValue(String var1);

    public Long getBytes(String var1);

    public ConfigMemorySize getMemorySize(String var1);

    @Deprecated
    public Long getMilliseconds(String var1);

    @Deprecated
    public Long getNanoseconds(String var1);

    public long getDuration(String var1, TimeUnit var2);

    public Duration getDuration(String var1);

    public Period getPeriod(String var1);

    public TemporalAmount getTemporal(String var1);

    public ConfigList getList(String var1);

    public List<Boolean> getBooleanList(String var1);

    public List<Number> getNumberList(String var1);

    public List<Integer> getIntList(String var1);

    public List<Long> getLongList(String var1);

    public List<Double> getDoubleList(String var1);

    public List<String> getStringList(String var1);

    public <T extends Enum<T>> List<T> getEnumList(Class<T> var1, String var2);

    public List<? extends ConfigObject> getObjectList(String var1);

    public List<? extends Config> getConfigList(String var1);

    public List<? extends Object> getAnyRefList(String var1);

    public List<Long> getBytesList(String var1);

    public List<ConfigMemorySize> getMemorySizeList(String var1);

    @Deprecated
    public List<Long> getMillisecondsList(String var1);

    @Deprecated
    public List<Long> getNanosecondsList(String var1);

    public List<Long> getDurationList(String var1, TimeUnit var2);

    public List<Duration> getDurationList(String var1);

    public Config withOnlyPath(String var1);

    public Config withoutPath(String var1);

    public Config atPath(String var1);

    public Config atKey(String var1);

    public Config withValue(String var1, ConfigValue var2);
}


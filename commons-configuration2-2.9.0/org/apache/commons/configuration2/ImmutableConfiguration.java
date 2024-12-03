/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.convert.PropertyConverter;
import org.apache.commons.configuration2.ex.ConversionException;

public interface ImmutableConfiguration {
    public boolean containsKey(String var1);

    public <T> T get(Class<T> var1, String var2);

    public <T> T get(Class<T> var1, String var2, T var3);

    public Object getArray(Class<?> var1, String var2);

    @Deprecated
    public Object getArray(Class<?> var1, String var2, Object var3);

    public BigDecimal getBigDecimal(String var1);

    public BigDecimal getBigDecimal(String var1, BigDecimal var2);

    public BigInteger getBigInteger(String var1);

    public BigInteger getBigInteger(String var1, BigInteger var2);

    public boolean getBoolean(String var1);

    public boolean getBoolean(String var1, boolean var2);

    public Boolean getBoolean(String var1, Boolean var2);

    public byte getByte(String var1);

    public byte getByte(String var1, byte var2);

    public Byte getByte(String var1, Byte var2);

    public <T> Collection<T> getCollection(Class<T> var1, String var2, Collection<T> var3);

    public <T> Collection<T> getCollection(Class<T> var1, String var2, Collection<T> var3, Collection<T> var4);

    public double getDouble(String var1);

    public double getDouble(String var1, double var2);

    public Double getDouble(String var1, Double var2);

    default public Duration getDuration(String key) {
        String string = this.getString(key);
        if (string == null) {
            throw new NoSuchElementException(key);
        }
        return PropertyConverter.toDuration(string);
    }

    default public Duration getDuration(String key, Duration defaultValue) {
        Object value = this.getProperty(key);
        return value == null ? defaultValue : PropertyConverter.toDuration(value);
    }

    public String getEncodedString(String var1);

    public String getEncodedString(String var1, ConfigurationDecoder var2);

    default public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
        try {
            return Enum.valueOf(enumType, this.getString(key));
        }
        catch (IllegalArgumentException e) {
            throw new ConversionException(e);
        }
    }

    default public <T extends Enum<T>> T getEnum(String key, Class<T> enumType, T defaultValue) {
        String strValue = this.getString(key, null);
        if (strValue == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumType, strValue);
        }
        catch (IllegalArgumentException e) {
            throw new ConversionException(e);
        }
    }

    public float getFloat(String var1);

    public float getFloat(String var1, float var2);

    public Float getFloat(String var1, Float var2);

    public int getInt(String var1);

    public int getInt(String var1, int var2);

    public Integer getInteger(String var1, Integer var2);

    public Iterator<String> getKeys();

    public Iterator<String> getKeys(String var1);

    public <T> List<T> getList(Class<T> var1, String var2);

    public <T> List<T> getList(Class<T> var1, String var2, List<T> var3);

    public List<Object> getList(String var1);

    public List<Object> getList(String var1, List<?> var2);

    public long getLong(String var1);

    public long getLong(String var1, long var2);

    public Long getLong(String var1, Long var2);

    public Properties getProperties(String var1);

    public Object getProperty(String var1);

    public short getShort(String var1);

    public short getShort(String var1, short var2);

    public Short getShort(String var1, Short var2);

    public String getString(String var1);

    public String getString(String var1, String var2);

    public String[] getStringArray(String var1);

    public ImmutableConfiguration immutableSubset(String var1);

    public boolean isEmpty();

    public int size();
}


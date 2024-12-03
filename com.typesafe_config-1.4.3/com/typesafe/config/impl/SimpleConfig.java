/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigMemorySize;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigResolveOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.ConfigNull;
import com.typesafe.config.impl.ConfigNumber;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.DefaultTransformer;
import com.typesafe.config.impl.MergeableValue;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SerializedConfigValue;
import com.typesafe.config.impl.SimpleConfigList;
import com.typesafe.config.impl.SimpleConfigObject;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

final class SimpleConfig
implements Config,
MergeableValue,
Serializable {
    private static final long serialVersionUID = 1L;
    private final AbstractConfigObject object;

    SimpleConfig(AbstractConfigObject object) {
        this.object = object;
    }

    @Override
    public AbstractConfigObject root() {
        return this.object;
    }

    @Override
    public ConfigOrigin origin() {
        return this.object.origin();
    }

    @Override
    public SimpleConfig resolve() {
        return this.resolve(ConfigResolveOptions.defaults());
    }

    @Override
    public SimpleConfig resolve(ConfigResolveOptions options) {
        return this.resolveWith(this, options);
    }

    @Override
    public SimpleConfig resolveWith(Config source) {
        return this.resolveWith(source, ConfigResolveOptions.defaults());
    }

    @Override
    public SimpleConfig resolveWith(Config source, ConfigResolveOptions options) {
        AbstractConfigValue resolved = ResolveContext.resolve(this.object, ((SimpleConfig)source).object, options);
        if (resolved == this.object) {
            return this;
        }
        return new SimpleConfig((AbstractConfigObject)resolved);
    }

    private ConfigValue hasPathPeek(String pathExpression) {
        AbstractConfigValue peeked;
        Path path = Path.newPath(pathExpression);
        try {
            peeked = this.object.peekPath(path);
        }
        catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(path, e);
        }
        return peeked;
    }

    @Override
    public boolean hasPath(String pathExpression) {
        ConfigValue peeked = this.hasPathPeek(pathExpression);
        return peeked != null && peeked.valueType() != ConfigValueType.NULL;
    }

    @Override
    public boolean hasPathOrNull(String path) {
        ConfigValue peeked = this.hasPathPeek(path);
        return peeked != null;
    }

    @Override
    public boolean isEmpty() {
        return this.object.isEmpty();
    }

    private static void findPaths(Set<Map.Entry<String, ConfigValue>> entries, Path parent, AbstractConfigObject obj) {
        for (Map.Entry entry : obj.entrySet()) {
            String elem = (String)entry.getKey();
            ConfigValue v = (ConfigValue)entry.getValue();
            Path path = Path.newKey(elem);
            if (parent != null) {
                path = path.prepend(parent);
            }
            if (v instanceof AbstractConfigObject) {
                SimpleConfig.findPaths(entries, path, (AbstractConfigObject)v);
                continue;
            }
            if (v instanceof ConfigNull) continue;
            entries.add(new AbstractMap.SimpleImmutableEntry<String, ConfigValue>(path.render(), v));
        }
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        HashSet<Map.Entry<String, ConfigValue>> entries = new HashSet<Map.Entry<String, ConfigValue>>();
        SimpleConfig.findPaths(entries, null, this.object);
        return entries;
    }

    private static AbstractConfigValue throwIfNull(AbstractConfigValue v, ConfigValueType expected, Path originalPath) {
        if (v.valueType() == ConfigValueType.NULL) {
            throw new ConfigException.Null((ConfigOrigin)v.origin(), originalPath.render(), expected != null ? expected.name() : null);
        }
        return v;
    }

    private static AbstractConfigValue findKey(AbstractConfigObject self, String key, ConfigValueType expected, Path originalPath) {
        return SimpleConfig.throwIfNull(SimpleConfig.findKeyOrNull(self, key, expected, originalPath), expected, originalPath);
    }

    private static AbstractConfigValue findKeyOrNull(AbstractConfigObject self, String key, ConfigValueType expected, Path originalPath) {
        AbstractConfigValue v = self.peekAssumingResolved(key, originalPath);
        if (v == null) {
            throw new ConfigException.Missing(self.origin(), originalPath.render());
        }
        if (expected != null) {
            v = DefaultTransformer.transform(v, expected);
        }
        if (expected != null && v.valueType() != expected && v.valueType() != ConfigValueType.NULL) {
            throw new ConfigException.WrongType(v.origin(), originalPath.render(), expected.name(), v.valueType().name());
        }
        return v;
    }

    private static AbstractConfigValue findOrNull(AbstractConfigObject self, Path path, ConfigValueType expected, Path originalPath) {
        try {
            String key = path.first();
            Path next = path.remainder();
            if (next == null) {
                return SimpleConfig.findKeyOrNull(self, key, expected, originalPath);
            }
            AbstractConfigObject o = (AbstractConfigObject)SimpleConfig.findKey(self, key, ConfigValueType.OBJECT, originalPath.subPath(0, originalPath.length() - next.length()));
            assert (o != null);
            return SimpleConfig.findOrNull(o, next, expected, originalPath);
        }
        catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(path, e);
        }
    }

    AbstractConfigValue find(Path pathExpression, ConfigValueType expected, Path originalPath) {
        return SimpleConfig.throwIfNull(SimpleConfig.findOrNull(this.object, pathExpression, expected, originalPath), expected, originalPath);
    }

    AbstractConfigValue find(String pathExpression, ConfigValueType expected) {
        Path path = Path.newPath(pathExpression);
        return this.find(path, expected, path);
    }

    private AbstractConfigValue findOrNull(Path pathExpression, ConfigValueType expected, Path originalPath) {
        return SimpleConfig.findOrNull(this.object, pathExpression, expected, originalPath);
    }

    private AbstractConfigValue findOrNull(String pathExpression, ConfigValueType expected) {
        Path path = Path.newPath(pathExpression);
        return this.findOrNull(path, expected, path);
    }

    @Override
    public AbstractConfigValue getValue(String path) {
        return this.find(path, null);
    }

    @Override
    public boolean getIsNull(String path) {
        AbstractConfigValue v = this.findOrNull(path, null);
        return v.valueType() == ConfigValueType.NULL;
    }

    @Override
    public boolean getBoolean(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.BOOLEAN);
        return (Boolean)v.unwrapped();
    }

    private ConfigNumber getConfigNumber(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.NUMBER);
        return (ConfigNumber)v;
    }

    @Override
    public Number getNumber(String path) {
        return this.getConfigNumber(path).unwrapped();
    }

    @Override
    public int getInt(String path) {
        ConfigNumber n = this.getConfigNumber(path);
        return n.intValueRangeChecked(path);
    }

    @Override
    public long getLong(String path) {
        return this.getNumber(path).longValue();
    }

    @Override
    public double getDouble(String path) {
        return this.getNumber(path).doubleValue();
    }

    @Override
    public String getString(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        return (String)v.unwrapped();
    }

    @Override
    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        return this.getEnumValue(path, enumClass, v);
    }

    @Override
    public ConfigList getList(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.LIST);
        return (ConfigList)((Object)v);
    }

    @Override
    public AbstractConfigObject getObject(String path) {
        AbstractConfigObject obj = (AbstractConfigObject)this.find(path, ConfigValueType.OBJECT);
        return obj;
    }

    @Override
    public SimpleConfig getConfig(String path) {
        return this.getObject(path).toConfig();
    }

    @Override
    public Object getAnyRef(String path) {
        AbstractConfigValue v = this.find(path, null);
        return v.unwrapped();
    }

    @Override
    public Long getBytes(String path) {
        BigInteger bytes = this.getBytesBigInteger(path);
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        return this.toLong(bytes, v.origin(), path);
    }

    private BigInteger getBytesBigInteger(String path) {
        BigInteger bytes;
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        try {
            bytes = BigInteger.valueOf(this.getLong(path));
        }
        catch (ConfigException.WrongType e) {
            bytes = SimpleConfig.parseBytes((String)v.unwrapped(), v.origin(), path);
        }
        if (bytes.signum() < 0) {
            throw new ConfigException.BadValue(v.origin(), path, "Attempt to construct memory size with negative number: " + bytes);
        }
        return bytes;
    }

    private List<BigInteger> getBytesListBigInteger(String path) {
        ArrayList<BigInteger> result = new ArrayList<BigInteger>();
        ConfigList list = this.getList(path);
        for (ConfigValue v : list) {
            BigInteger bytes;
            if (v.valueType() == ConfigValueType.NUMBER) {
                bytes = BigInteger.valueOf(((Number)v.unwrapped()).longValue());
            } else if (v.valueType() == ConfigValueType.STRING) {
                String s = (String)v.unwrapped();
                bytes = SimpleConfig.parseBytes(s, v.origin(), path);
            } else {
                throw new ConfigException.WrongType(v.origin(), path, "memory size string or number of bytes", v.valueType().name());
            }
            if (bytes.signum() < 0) {
                throw new ConfigException.BadValue(v.origin(), path, "Attempt to construct ConfigMemorySize with negative number: " + bytes);
            }
            result.add(bytes);
        }
        return result;
    }

    @Override
    public ConfigMemorySize getMemorySize(String path) {
        return ConfigMemorySize.ofBytes(this.getBytesBigInteger(path));
    }

    @Override
    @Deprecated
    public Long getMilliseconds(String path) {
        return this.getDuration(path, TimeUnit.MILLISECONDS);
    }

    @Override
    @Deprecated
    public Long getNanoseconds(String path) {
        return this.getDuration(path, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getDuration(String path, TimeUnit unit) {
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        long result = unit.convert(SimpleConfig.parseDuration((String)v.unwrapped(), v.origin(), path), TimeUnit.NANOSECONDS);
        return result;
    }

    @Override
    public Duration getDuration(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        long nanos = SimpleConfig.parseDuration((String)v.unwrapped(), v.origin(), path);
        return Duration.ofNanos(nanos);
    }

    @Override
    public Period getPeriod(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.STRING);
        return SimpleConfig.parsePeriod((String)v.unwrapped(), v.origin(), path);
    }

    @Override
    public TemporalAmount getTemporal(String path) {
        try {
            return this.getDuration(path);
        }
        catch (ConfigException.BadValue e) {
            return this.getPeriod(path);
        }
    }

    private <T> List<T> getHomogeneousUnwrappedList(String path, ConfigValueType expected) {
        ArrayList<Object> l = new ArrayList<Object>();
        ConfigList list = this.getList(path);
        for (ConfigValue cv : list) {
            AbstractConfigValue v = (AbstractConfigValue)cv;
            if (expected != null) {
                v = DefaultTransformer.transform(v, expected);
            }
            if (v.valueType() != expected) {
                throw new ConfigException.WrongType(v.origin(), path, "list of " + expected.name(), "list of " + v.valueType().name());
            }
            l.add(v.unwrapped());
        }
        return l;
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        return this.getHomogeneousUnwrappedList(path, ConfigValueType.BOOLEAN);
    }

    @Override
    public List<Number> getNumberList(String path) {
        return this.getHomogeneousUnwrappedList(path, ConfigValueType.NUMBER);
    }

    @Override
    public List<Integer> getIntList(String path) {
        ArrayList<Integer> l = new ArrayList<Integer>();
        List numbers = this.getHomogeneousWrappedList(path, ConfigValueType.NUMBER);
        for (AbstractConfigValue v : numbers) {
            l.add(((ConfigNumber)v).intValueRangeChecked(path));
        }
        return l;
    }

    @Override
    public List<Long> getLongList(String path) {
        ArrayList<Long> l = new ArrayList<Long>();
        List<Number> numbers = this.getNumberList(path);
        for (Number n : numbers) {
            l.add(n.longValue());
        }
        return l;
    }

    @Override
    public List<Double> getDoubleList(String path) {
        ArrayList<Double> l = new ArrayList<Double>();
        List<Number> numbers = this.getNumberList(path);
        for (Number n : numbers) {
            l.add(n.doubleValue());
        }
        return l;
    }

    @Override
    public List<String> getStringList(String path) {
        return this.getHomogeneousUnwrappedList(path, ConfigValueType.STRING);
    }

    @Override
    public <T extends Enum<T>> List<T> getEnumList(Class<T> enumClass, String path) {
        List<T> enumNames = this.getHomogeneousWrappedList(path, ConfigValueType.STRING);
        ArrayList<T> enumList = new ArrayList<T>();
        for (ConfigString enumName : enumNames) {
            enumList.add(this.getEnumValue(path, enumClass, enumName));
        }
        return enumList;
    }

    private <T extends Enum<T>> T getEnumValue(String path, Class<T> enumClass, ConfigValue enumConfigValue) {
        String enumName = (String)enumConfigValue.unwrapped();
        try {
            return Enum.valueOf(enumClass, enumName);
        }
        catch (IllegalArgumentException e) {
            ArrayList<String> enumNames = new ArrayList<String>();
            Enum[] enumConstants = (Enum[])enumClass.getEnumConstants();
            if (enumConstants != null) {
                for (Enum enumConstant : enumConstants) {
                    enumNames.add(enumConstant.name());
                }
            }
            throw new ConfigException.BadValue(enumConfigValue.origin(), path, String.format("The enum class %s has no constant of the name '%s' (should be one of %s.)", enumClass.getSimpleName(), enumName, enumNames));
        }
    }

    private <T extends ConfigValue> List<T> getHomogeneousWrappedList(String path, ConfigValueType expected) {
        ArrayList<AbstractConfigValue> l = new ArrayList<AbstractConfigValue>();
        ConfigList list = this.getList(path);
        for (ConfigValue cv : list) {
            AbstractConfigValue v = (AbstractConfigValue)cv;
            if (expected != null) {
                v = DefaultTransformer.transform(v, expected);
            }
            if (v.valueType() != expected) {
                throw new ConfigException.WrongType(v.origin(), path, "list of " + expected.name(), "list of " + v.valueType().name());
            }
            l.add(v);
        }
        return l;
    }

    public List<ConfigObject> getObjectList(String path) {
        return this.getHomogeneousWrappedList(path, ConfigValueType.OBJECT);
    }

    @Override
    public List<? extends Config> getConfigList(String path) {
        List<ConfigObject> objects = this.getObjectList(path);
        ArrayList<Config> l = new ArrayList<Config>();
        for (ConfigObject o : objects) {
            l.add(o.toConfig());
        }
        return l;
    }

    @Override
    public List<? extends Object> getAnyRefList(String path) {
        ArrayList<Object> l = new ArrayList<Object>();
        ConfigList list = this.getList(path);
        for (ConfigValue v : list) {
            l.add(v.unwrapped());
        }
        return l;
    }

    @Override
    public List<Long> getBytesList(String path) {
        AbstractConfigValue v = this.find(path, ConfigValueType.LIST);
        return this.getBytesListBigInteger(path).stream().map(bytes -> this.toLong((BigInteger)bytes, v.origin(), path)).collect(Collectors.toList());
    }

    private Long toLong(BigInteger value, ConfigOrigin originForException, String pathForException) {
        if (value.bitLength() < 64) {
            return value.longValue();
        }
        throw new ConfigException.BadValue(originForException, pathForException, "size-in-bytes value is out of range for a 64-bit long: '" + value + "'");
    }

    @Override
    public List<ConfigMemorySize> getMemorySizeList(String path) {
        return this.getBytesListBigInteger(path).stream().map(ConfigMemorySize::ofBytes).collect(Collectors.toList());
    }

    @Override
    public List<Long> getDurationList(String path, TimeUnit unit) {
        ArrayList<Long> l = new ArrayList<Long>();
        ConfigList list = this.getList(path);
        for (ConfigValue v : list) {
            if (v.valueType() == ConfigValueType.NUMBER) {
                Long n = unit.convert(((Number)v.unwrapped()).longValue(), TimeUnit.MILLISECONDS);
                l.add(n);
                continue;
            }
            if (v.valueType() == ConfigValueType.STRING) {
                String s = (String)v.unwrapped();
                Long n = unit.convert(SimpleConfig.parseDuration(s, v.origin(), path), TimeUnit.NANOSECONDS);
                l.add(n);
                continue;
            }
            throw new ConfigException.WrongType(v.origin(), path, "duration string or number of milliseconds", v.valueType().name());
        }
        return l;
    }

    @Override
    public List<Duration> getDurationList(String path) {
        List<Long> l = this.getDurationList(path, TimeUnit.NANOSECONDS);
        ArrayList<Duration> builder = new ArrayList<Duration>(l.size());
        for (Long value : l) {
            builder.add(Duration.ofNanos(value));
        }
        return builder;
    }

    @Override
    @Deprecated
    public List<Long> getMillisecondsList(String path) {
        return this.getDurationList(path, TimeUnit.MILLISECONDS);
    }

    @Override
    @Deprecated
    public List<Long> getNanosecondsList(String path) {
        return this.getDurationList(path, TimeUnit.NANOSECONDS);
    }

    @Override
    public AbstractConfigObject toFallbackValue() {
        return this.object;
    }

    @Override
    public SimpleConfig withFallback(ConfigMergeable other) {
        return this.object.withFallback(other).toConfig();
    }

    public final boolean equals(Object other) {
        if (other instanceof SimpleConfig) {
            return this.object.equals(((SimpleConfig)other).object);
        }
        return false;
    }

    public final int hashCode() {
        return 41 * this.object.hashCode();
    }

    public String toString() {
        return "Config(" + this.object.toString() + ")";
    }

    private static String getUnits(String s) {
        char c;
        int i;
        for (i = s.length() - 1; i >= 0 && Character.isLetter(c = s.charAt(i)); --i) {
        }
        return s.substring(i + 1);
    }

    public static Period parsePeriod(String input, ConfigOrigin originForException, String pathForException) {
        ChronoUnit units;
        String originalUnitString;
        String s = ConfigImplUtil.unicodeTrim(input);
        String unitString = originalUnitString = SimpleConfig.getUnits(s);
        String numberString = ConfigImplUtil.unicodeTrim(s.substring(0, s.length() - unitString.length()));
        if (numberString.length() == 0) {
            throw new ConfigException.BadValue(originForException, pathForException, "No number in period value '" + input + "'");
        }
        if (unitString.length() > 2 && !unitString.endsWith("s")) {
            unitString = unitString + "s";
        }
        if (unitString.equals("") || unitString.equals("d") || unitString.equals("days")) {
            units = ChronoUnit.DAYS;
        } else if (unitString.equals("w") || unitString.equals("weeks")) {
            units = ChronoUnit.WEEKS;
        } else if (unitString.equals("m") || unitString.equals("mo") || unitString.equals("months")) {
            units = ChronoUnit.MONTHS;
        } else if (unitString.equals("y") || unitString.equals("years")) {
            units = ChronoUnit.YEARS;
        } else {
            throw new ConfigException.BadValue(originForException, pathForException, "Could not parse time unit '" + originalUnitString + "' (try d, w, mo, y)");
        }
        try {
            return SimpleConfig.periodOf(Integer.parseInt(numberString), units);
        }
        catch (NumberFormatException e) {
            throw new ConfigException.BadValue(originForException, pathForException, "Could not parse duration number '" + numberString + "'");
        }
    }

    private static Period periodOf(int n, ChronoUnit unit) {
        if (unit.isTimeBased()) {
            throw new DateTimeException(unit + " cannot be converted to a java.time.Period");
        }
        switch (unit) {
            case DAYS: {
                return Period.ofDays(n);
            }
            case WEEKS: {
                return Period.ofWeeks(n);
            }
            case MONTHS: {
                return Period.ofMonths(n);
            }
            case YEARS: {
                return Period.ofYears(n);
            }
        }
        throw new DateTimeException(unit + " cannot be converted to a java.time.Period");
    }

    public static long parseDuration(String input, ConfigOrigin originForException, String pathForException) {
        String originalUnitString;
        String s = ConfigImplUtil.unicodeTrim(input);
        String unitString = originalUnitString = SimpleConfig.getUnits(s);
        String numberString = ConfigImplUtil.unicodeTrim(s.substring(0, s.length() - unitString.length()));
        TimeUnit units = null;
        if (numberString.length() == 0) {
            throw new ConfigException.BadValue(originForException, pathForException, "No number in duration value '" + input + "'");
        }
        if (unitString.length() > 2 && !unitString.endsWith("s")) {
            unitString = unitString + "s";
        }
        if (unitString.equals("") || unitString.equals("ms") || unitString.equals("millis") || unitString.equals("milliseconds")) {
            units = TimeUnit.MILLISECONDS;
        } else if (unitString.equals("us") || unitString.equals("micros") || unitString.equals("microseconds")) {
            units = TimeUnit.MICROSECONDS;
        } else if (unitString.equals("ns") || unitString.equals("nanos") || unitString.equals("nanoseconds")) {
            units = TimeUnit.NANOSECONDS;
        } else if (unitString.equals("d") || unitString.equals("days")) {
            units = TimeUnit.DAYS;
        } else if (unitString.equals("h") || unitString.equals("hours")) {
            units = TimeUnit.HOURS;
        } else if (unitString.equals("s") || unitString.equals("seconds")) {
            units = TimeUnit.SECONDS;
        } else if (unitString.equals("m") || unitString.equals("minutes")) {
            units = TimeUnit.MINUTES;
        } else {
            throw new ConfigException.BadValue(originForException, pathForException, "Could not parse time unit '" + originalUnitString + "' (try ns, us, ms, s, m, h, d)");
        }
        try {
            if (numberString.matches("[+-]?[0-9]+")) {
                return units.toNanos(Long.parseLong(numberString));
            }
            long nanosInUnit = units.toNanos(1L);
            return (long)(Double.parseDouble(numberString) * (double)nanosInUnit);
        }
        catch (NumberFormatException e) {
            throw new ConfigException.BadValue(originForException, pathForException, "Could not parse duration number '" + numberString + "'");
        }
    }

    public static BigInteger parseBytes(String input, ConfigOrigin originForException, String pathForException) {
        String s = ConfigImplUtil.unicodeTrim(input);
        String unitString = SimpleConfig.getUnits(s);
        String numberString = ConfigImplUtil.unicodeTrim(s.substring(0, s.length() - unitString.length()));
        if (numberString.length() == 0) {
            throw new ConfigException.BadValue(originForException, pathForException, "No number in size-in-bytes value '" + input + "'");
        }
        MemoryUnit units = MemoryUnit.parseUnit(unitString);
        if (units == null) {
            throw new ConfigException.BadValue(originForException, pathForException, "Could not parse size-in-bytes unit '" + unitString + "' (try k, K, kB, KiB, kilobytes, kibibytes)");
        }
        try {
            BigInteger result;
            if (numberString.matches("[0-9]+")) {
                result = units.bytes.multiply(new BigInteger(numberString));
            } else {
                BigDecimal resultDecimal = new BigDecimal(units.bytes).multiply(new BigDecimal(numberString));
                result = resultDecimal.toBigInteger();
            }
            return result;
        }
        catch (NumberFormatException e) {
            throw new ConfigException.BadValue(originForException, pathForException, "Could not parse size-in-bytes number '" + numberString + "'");
        }
    }

    private AbstractConfigValue peekPath(Path path) {
        return this.root().peekPath(path);
    }

    private static void addProblem(List<ConfigException.ValidationProblem> accumulator, Path path, ConfigOrigin origin, String problem) {
        accumulator.add(new ConfigException.ValidationProblem(path.render(), origin, problem));
    }

    private static String getDesc(ConfigValueType type) {
        return type.name().toLowerCase();
    }

    private static String getDesc(ConfigValue refValue) {
        if (refValue instanceof AbstractConfigObject) {
            AbstractConfigObject obj = (AbstractConfigObject)refValue;
            if (!obj.isEmpty()) {
                return "object with keys " + obj.keySet();
            }
            return SimpleConfig.getDesc(refValue.valueType());
        }
        return SimpleConfig.getDesc(refValue.valueType());
    }

    private static void addMissing(List<ConfigException.ValidationProblem> accumulator, String refDesc, Path path, ConfigOrigin origin) {
        SimpleConfig.addProblem(accumulator, path, origin, "No setting at '" + path.render() + "', expecting: " + refDesc);
    }

    private static void addMissing(List<ConfigException.ValidationProblem> accumulator, ConfigValue refValue, Path path, ConfigOrigin origin) {
        SimpleConfig.addMissing(accumulator, SimpleConfig.getDesc(refValue), path, origin);
    }

    static void addMissing(List<ConfigException.ValidationProblem> accumulator, ConfigValueType refType, Path path, ConfigOrigin origin) {
        SimpleConfig.addMissing(accumulator, SimpleConfig.getDesc(refType), path, origin);
    }

    private static void addWrongType(List<ConfigException.ValidationProblem> accumulator, String refDesc, AbstractConfigValue actual, Path path) {
        SimpleConfig.addProblem(accumulator, path, actual.origin(), "Wrong value type at '" + path.render() + "', expecting: " + refDesc + " but got: " + SimpleConfig.getDesc(actual));
    }

    private static void addWrongType(List<ConfigException.ValidationProblem> accumulator, ConfigValue refValue, AbstractConfigValue actual, Path path) {
        SimpleConfig.addWrongType(accumulator, SimpleConfig.getDesc(refValue), actual, path);
    }

    private static void addWrongType(List<ConfigException.ValidationProblem> accumulator, ConfigValueType refType, AbstractConfigValue actual, Path path) {
        SimpleConfig.addWrongType(accumulator, SimpleConfig.getDesc(refType), actual, path);
    }

    private static boolean couldBeNull(AbstractConfigValue v) {
        return DefaultTransformer.transform(v, ConfigValueType.NULL).valueType() == ConfigValueType.NULL;
    }

    private static boolean haveCompatibleTypes(ConfigValue reference, AbstractConfigValue value) {
        if (SimpleConfig.couldBeNull((AbstractConfigValue)reference)) {
            return true;
        }
        return SimpleConfig.haveCompatibleTypes(reference.valueType(), value);
    }

    private static boolean haveCompatibleTypes(ConfigValueType referenceType, AbstractConfigValue value) {
        if (referenceType == ConfigValueType.NULL || SimpleConfig.couldBeNull(value)) {
            return true;
        }
        if (referenceType == ConfigValueType.OBJECT) {
            return value instanceof AbstractConfigObject;
        }
        if (referenceType == ConfigValueType.LIST) {
            return value instanceof SimpleConfigList || value instanceof SimpleConfigObject;
        }
        if (referenceType == ConfigValueType.STRING) {
            return true;
        }
        if (value instanceof ConfigString) {
            return true;
        }
        return referenceType == value.valueType();
    }

    private static void checkValidObject(Path path, AbstractConfigObject reference, AbstractConfigObject value, List<ConfigException.ValidationProblem> accumulator) {
        for (Map.Entry entry : reference.entrySet()) {
            String key = (String)entry.getKey();
            Path childPath = path != null ? Path.newKey(key).prepend(path) : Path.newKey(key);
            AbstractConfigValue v = value.get(key);
            if (v == null) {
                SimpleConfig.addMissing(accumulator, (ConfigValue)entry.getValue(), childPath, (ConfigOrigin)value.origin());
                continue;
            }
            SimpleConfig.checkValid(childPath, (ConfigValue)entry.getValue(), v, accumulator);
        }
    }

    private static void checkListCompatibility(Path path, SimpleConfigList listRef, SimpleConfigList listValue, List<ConfigException.ValidationProblem> accumulator) {
        if (!listRef.isEmpty() && !listValue.isEmpty()) {
            AbstractConfigValue refElement = listRef.get(0);
            for (ConfigValue elem : listValue) {
                AbstractConfigValue e = (AbstractConfigValue)elem;
                if (SimpleConfig.haveCompatibleTypes(refElement, e)) continue;
                SimpleConfig.addProblem(accumulator, path, e.origin(), "List at '" + path.render() + "' contains wrong value type, expecting list of " + SimpleConfig.getDesc(refElement) + " but got element of type " + SimpleConfig.getDesc(e));
                break;
            }
        }
    }

    static void checkValid(Path path, ConfigValueType referenceType, AbstractConfigValue value, List<ConfigException.ValidationProblem> accumulator) {
        if (SimpleConfig.haveCompatibleTypes(referenceType, value)) {
            AbstractConfigValue listValue;
            if (referenceType == ConfigValueType.LIST && value instanceof SimpleConfigObject && !((listValue = DefaultTransformer.transform(value, ConfigValueType.LIST)) instanceof SimpleConfigList)) {
                SimpleConfig.addWrongType(accumulator, referenceType, value, path);
            }
        } else {
            SimpleConfig.addWrongType(accumulator, referenceType, value, path);
        }
    }

    private static void checkValid(Path path, ConfigValue reference, AbstractConfigValue value, List<ConfigException.ValidationProblem> accumulator) {
        if (SimpleConfig.haveCompatibleTypes(reference, value)) {
            if (reference instanceof AbstractConfigObject && value instanceof AbstractConfigObject) {
                SimpleConfig.checkValidObject(path, (AbstractConfigObject)reference, (AbstractConfigObject)value, accumulator);
            } else if (reference instanceof SimpleConfigList && value instanceof SimpleConfigList) {
                SimpleConfigList listRef = (SimpleConfigList)reference;
                SimpleConfigList listValue = (SimpleConfigList)value;
                SimpleConfig.checkListCompatibility(path, listRef, listValue, accumulator);
            } else if (reference instanceof SimpleConfigList && value instanceof SimpleConfigObject) {
                SimpleConfigList listRef = (SimpleConfigList)reference;
                AbstractConfigValue listValue = DefaultTransformer.transform(value, ConfigValueType.LIST);
                if (listValue instanceof SimpleConfigList) {
                    SimpleConfig.checkListCompatibility(path, listRef, (SimpleConfigList)listValue, accumulator);
                } else {
                    SimpleConfig.addWrongType(accumulator, reference, value, path);
                }
            }
        } else {
            SimpleConfig.addWrongType(accumulator, reference, value, path);
        }
    }

    @Override
    public boolean isResolved() {
        return this.root().resolveStatus() == ResolveStatus.RESOLVED;
    }

    @Override
    public void checkValid(Config reference, String ... restrictToPaths) {
        SimpleConfig ref = (SimpleConfig)reference;
        if (ref.root().resolveStatus() != ResolveStatus.RESOLVED) {
            throw new ConfigException.BugOrBroken("do not call checkValid() with an unresolved reference config, call Config#resolve(), see Config#resolve() API docs");
        }
        if (this.root().resolveStatus() != ResolveStatus.RESOLVED) {
            throw new ConfigException.NotResolved("need to Config#resolve() each config before using it, see the API docs for Config#resolve()");
        }
        ArrayList<ConfigException.ValidationProblem> problems = new ArrayList<ConfigException.ValidationProblem>();
        if (restrictToPaths.length == 0) {
            SimpleConfig.checkValidObject(null, ref.root(), this.root(), problems);
        } else {
            for (String p : restrictToPaths) {
                Path path = Path.newPath(p);
                AbstractConfigValue refValue = ref.peekPath(path);
                if (refValue == null) continue;
                AbstractConfigValue child = this.peekPath(path);
                if (child != null) {
                    SimpleConfig.checkValid(path, refValue, child, problems);
                    continue;
                }
                SimpleConfig.addMissing(problems, refValue, path, this.origin());
            }
        }
        if (!problems.isEmpty()) {
            throw new ConfigException.ValidationFailed(problems);
        }
    }

    @Override
    public SimpleConfig withOnlyPath(String pathExpression) {
        Path path = Path.newPath(pathExpression);
        return new SimpleConfig(this.root().withOnlyPath(path));
    }

    @Override
    public SimpleConfig withoutPath(String pathExpression) {
        Path path = Path.newPath(pathExpression);
        return new SimpleConfig(this.root().withoutPath(path));
    }

    @Override
    public SimpleConfig withValue(String pathExpression, ConfigValue v) {
        Path path = Path.newPath(pathExpression);
        return new SimpleConfig(this.root().withValue(path, v));
    }

    SimpleConfig atKey(ConfigOrigin origin, String key) {
        return this.root().atKey(origin, key);
    }

    @Override
    public SimpleConfig atKey(String key) {
        return this.root().atKey(key);
    }

    @Override
    public Config atPath(String path) {
        return this.root().atPath(path);
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }

    private static enum MemoryUnit {
        BYTES("", 1024, 0),
        KILOBYTES("kilo", 1000, 1),
        MEGABYTES("mega", 1000, 2),
        GIGABYTES("giga", 1000, 3),
        TERABYTES("tera", 1000, 4),
        PETABYTES("peta", 1000, 5),
        EXABYTES("exa", 1000, 6),
        ZETTABYTES("zetta", 1000, 7),
        YOTTABYTES("yotta", 1000, 8),
        KIBIBYTES("kibi", 1024, 1),
        MEBIBYTES("mebi", 1024, 2),
        GIBIBYTES("gibi", 1024, 3),
        TEBIBYTES("tebi", 1024, 4),
        PEBIBYTES("pebi", 1024, 5),
        EXBIBYTES("exbi", 1024, 6),
        ZEBIBYTES("zebi", 1024, 7),
        YOBIBYTES("yobi", 1024, 8);

        final String prefix;
        final int powerOf;
        final int power;
        final BigInteger bytes;
        private static Map<String, MemoryUnit> unitsMap;

        private MemoryUnit(String prefix, int powerOf, int power) {
            this.prefix = prefix;
            this.powerOf = powerOf;
            this.power = power;
            this.bytes = BigInteger.valueOf(powerOf).pow(power);
        }

        private static Map<String, MemoryUnit> makeUnitsMap() {
            HashMap<String, MemoryUnit> map = new HashMap<String, MemoryUnit>();
            for (MemoryUnit unit : MemoryUnit.values()) {
                map.put(unit.prefix + "byte", unit);
                map.put(unit.prefix + "bytes", unit);
                if (unit.prefix.length() == 0) {
                    map.put("b", unit);
                    map.put("B", unit);
                    map.put("", unit);
                    continue;
                }
                String first = unit.prefix.substring(0, 1);
                String firstUpper = first.toUpperCase();
                if (unit.powerOf == 1024) {
                    map.put(first, unit);
                    map.put(firstUpper, unit);
                    map.put(firstUpper + "i", unit);
                    map.put(firstUpper + "iB", unit);
                    continue;
                }
                if (unit.powerOf == 1000) {
                    if (unit.power == 1) {
                        map.put(first + "B", unit);
                        continue;
                    }
                    map.put(firstUpper + "B", unit);
                    continue;
                }
                throw new RuntimeException("broken MemoryUnit enum");
            }
            return map;
        }

        static MemoryUnit parseUnit(String unit) {
            return unitsMap.get(unit);
        }

        static {
            unitsMap = MemoryUnit.makeUnitsMap();
        }
    }
}


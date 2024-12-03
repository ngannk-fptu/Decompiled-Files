/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.ConfigurationLookup;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.PrefixedKeysIterator;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.event.BaseEventSource;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractConfiguration
extends BaseEventSource
implements Configuration {
    private ListDelimiterHandler listDelimiterHandler;
    private ConversionHandler conversionHandler;
    private boolean throwExceptionOnMissing;
    private AtomicReference<ConfigurationInterpolator> interpolator = new AtomicReference();
    private volatile Synchronizer synchronizer;
    private ConfigurationDecoder configurationDecoder;
    private ConfigurationLogger log;

    public AbstractConfiguration() {
        this.initLogger(null);
        this.installDefaultInterpolator();
        this.listDelimiterHandler = DisabledListDelimiterHandler.INSTANCE;
        this.conversionHandler = DefaultConversionHandler.INSTANCE;
    }

    public ListDelimiterHandler getListDelimiterHandler() {
        return this.listDelimiterHandler;
    }

    public void setListDelimiterHandler(ListDelimiterHandler listDelimiterHandler) {
        if (listDelimiterHandler == null) {
            throw new IllegalArgumentException("List delimiter handler must not be null!");
        }
        this.listDelimiterHandler = listDelimiterHandler;
    }

    public ConversionHandler getConversionHandler() {
        return this.conversionHandler;
    }

    public void setConversionHandler(ConversionHandler conversionHandler) {
        if (conversionHandler == null) {
            throw new IllegalArgumentException("ConversionHandler must not be null!");
        }
        this.conversionHandler = conversionHandler;
    }

    public void setThrowExceptionOnMissing(boolean throwExceptionOnMissing) {
        this.throwExceptionOnMissing = throwExceptionOnMissing;
    }

    public boolean isThrowExceptionOnMissing() {
        return this.throwExceptionOnMissing;
    }

    @Override
    public ConfigurationInterpolator getInterpolator() {
        return this.interpolator.get();
    }

    @Override
    public final void setInterpolator(ConfigurationInterpolator ci) {
        this.interpolator.set(ci);
    }

    @Override
    public final void installInterpolator(Map<String, ? extends Lookup> prefixLookups, Collection<? extends Lookup> defLookups) {
        InterpolatorSpecification spec = new InterpolatorSpecification.Builder().withPrefixLookups(prefixLookups).withDefaultLookups(defLookups).withDefaultLookup(new ConfigurationLookup(this)).create();
        this.setInterpolator(ConfigurationInterpolator.fromSpecification(spec));
    }

    public void setPrefixLookups(Map<String, ? extends Lookup> lookups) {
        ConfigurationInterpolator ciNew;
        ConfigurationInterpolator ciOld;
        boolean success;
        do {
            ciNew = (ciOld = this.getInterpolator()) != null ? ciOld : new ConfigurationInterpolator();
            ciNew.registerLookups(lookups);
        } while (!(success = this.interpolator.compareAndSet(ciOld, ciNew)));
    }

    public void setDefaultLookups(Collection<? extends Lookup> lookups) {
        ConfigurationInterpolator ciNew;
        ConfigurationInterpolator ciOld;
        boolean success;
        do {
            Lookup confLookup;
            if ((confLookup = this.findConfigurationLookup(ciNew = (ciOld = this.getInterpolator()) != null ? ciOld : new ConfigurationInterpolator())) == null) {
                confLookup = new ConfigurationLookup(this);
            } else {
                ciNew.removeDefaultLookup(confLookup);
            }
            ciNew.addDefaultLookups(lookups);
            ciNew.addDefaultLookup(confLookup);
        } while (!(success = this.interpolator.compareAndSet(ciOld, ciNew)));
    }

    public void setParentInterpolator(ConfigurationInterpolator parent) {
        ConfigurationInterpolator ciNew;
        ConfigurationInterpolator ciOld;
        boolean success;
        do {
            ciNew = (ciOld = this.getInterpolator()) != null ? ciOld : new ConfigurationInterpolator();
            ciNew.setParentInterpolator(parent);
        } while (!(success = this.interpolator.compareAndSet(ciOld, ciNew)));
    }

    public void setConfigurationDecoder(ConfigurationDecoder configurationDecoder) {
        this.configurationDecoder = configurationDecoder;
    }

    public ConfigurationDecoder getConfigurationDecoder() {
        return this.configurationDecoder;
    }

    protected void cloneInterpolator(AbstractConfiguration orgConfig) {
        this.interpolator = new AtomicReference();
        ConfigurationInterpolator orgInterpolator = orgConfig.getInterpolator();
        List<Lookup> defaultLookups = orgInterpolator.getDefaultLookups();
        Lookup lookup = AbstractConfiguration.findConfigurationLookup(orgInterpolator, orgConfig);
        if (lookup != null) {
            defaultLookups.remove(lookup);
        }
        this.installInterpolator(orgInterpolator.getLookups(), defaultLookups);
    }

    private void installDefaultInterpolator() {
        this.installInterpolator(ConfigurationInterpolator.getDefaultPrefixLookups(), null);
    }

    private Lookup findConfigurationLookup(ConfigurationInterpolator ci) {
        return AbstractConfiguration.findConfigurationLookup(ci, this);
    }

    private static Lookup findConfigurationLookup(ConfigurationInterpolator ci, ImmutableConfiguration targetConf) {
        for (Lookup l : ci.getDefaultLookups()) {
            if (!(l instanceof ConfigurationLookup) || targetConf != ((ConfigurationLookup)l).getConfiguration()) continue;
            return l;
        }
        return null;
    }

    public ConfigurationLogger getLogger() {
        return this.log;
    }

    public void setLogger(ConfigurationLogger log) {
        this.initLogger(log);
    }

    public final void addErrorLogListener() {
        this.addEventListener(ConfigurationErrorEvent.ANY, event -> this.getLogger().warn("Internal error", event.getCause()));
    }

    @Override
    public final Synchronizer getSynchronizer() {
        Synchronizer sync = this.synchronizer;
        return sync != null ? sync : NoOpSynchronizer.INSTANCE;
    }

    @Override
    public final void setSynchronizer(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    @Override
    public final void lock(LockMode mode) {
        switch (mode) {
            case READ: {
                this.beginRead(false);
                break;
            }
            case WRITE: {
                this.beginWrite(false);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported LockMode: " + (Object)((Object)mode));
            }
        }
    }

    @Override
    public final void unlock(LockMode mode) {
        switch (mode) {
            case READ: {
                this.endRead();
                break;
            }
            case WRITE: {
                this.endWrite();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported LockMode: " + (Object)((Object)mode));
            }
        }
    }

    protected void beginRead(boolean optimize) {
        this.getSynchronizer().beginRead();
    }

    protected void endRead() {
        this.getSynchronizer().endRead();
    }

    protected void beginWrite(boolean optimize) {
        this.getSynchronizer().beginWrite();
    }

    protected void endWrite() {
        this.getSynchronizer().endWrite();
    }

    @Override
    public final void addProperty(String key, Object value) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.ADD_PROPERTY, key, value, true);
            this.addPropertyInternal(key, value);
            this.fireEvent(ConfigurationEvent.ADD_PROPERTY, key, value, false);
        }
        finally {
            this.endWrite();
        }
    }

    protected void addPropertyInternal(String key, Object value) {
        this.getListDelimiterHandler().parse(value).forEach(obj -> this.addPropertyDirect(key, obj));
    }

    protected abstract void addPropertyDirect(String var1, Object var2);

    protected String interpolate(String base) {
        Object result = this.interpolate((Object)base);
        return result == null ? null : result.toString();
    }

    protected Object interpolate(Object value) {
        ConfigurationInterpolator ci = this.getInterpolator();
        return ci != null ? ci.interpolate(value) : value;
    }

    @Override
    public Configuration subset(String prefix) {
        return new SubsetConfiguration(this, prefix, ".");
    }

    @Override
    public ImmutableConfiguration immutableSubset(String prefix) {
        return ConfigurationUtils.unmodifiableConfiguration(this.subset(prefix));
    }

    @Override
    public final void setProperty(String key, Object value) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.SET_PROPERTY, key, value, true);
            this.setPropertyInternal(key, value);
            this.fireEvent(ConfigurationEvent.SET_PROPERTY, key, value, false);
        }
        finally {
            this.endWrite();
        }
    }

    protected void setPropertyInternal(String key, Object value) {
        this.setDetailEvents(false);
        try {
            this.clearProperty(key);
            this.addProperty(key, value);
        }
        finally {
            this.setDetailEvents(true);
        }
    }

    @Override
    public final void clearProperty(String key) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.CLEAR_PROPERTY, key, null, true);
            this.clearPropertyDirect(key);
            this.fireEvent(ConfigurationEvent.CLEAR_PROPERTY, key, null, false);
        }
        finally {
            this.endWrite();
        }
    }

    protected abstract void clearPropertyDirect(String var1);

    @Override
    public final void clear() {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.CLEAR, null, null, true);
            this.clearInternal();
            this.fireEvent(ConfigurationEvent.CLEAR, null, null, false);
        }
        finally {
            this.endWrite();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void clearInternal() {
        this.setDetailEvents(false);
        boolean useIterator = true;
        try {
            Iterator<String> it = this.getKeys();
            while (it.hasNext()) {
                String key = it.next();
                if (useIterator) {
                    try {
                        it.remove();
                    }
                    catch (UnsupportedOperationException usoex) {
                        useIterator = false;
                    }
                }
                if (useIterator && this.containsKey(key)) {
                    useIterator = false;
                }
                if (useIterator) continue;
                this.clearProperty(key);
            }
        }
        finally {
            this.setDetailEvents(true);
        }
    }

    @Override
    public final Iterator<String> getKeys() {
        this.beginRead(false);
        try {
            Iterator<String> iterator = this.getKeysInternal();
            return iterator;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public final Iterator<String> getKeys(String prefix) {
        this.beginRead(false);
        try {
            Iterator<String> iterator = this.getKeysInternal(prefix);
            return iterator;
        }
        finally {
            this.endRead();
        }
    }

    protected abstract Iterator<String> getKeysInternal();

    protected Iterator<String> getKeysInternal(String prefix) {
        return new PrefixedKeysIterator(this.getKeysInternal(), prefix);
    }

    @Override
    public final Object getProperty(String key) {
        this.beginRead(false);
        try {
            Object object = this.getPropertyInternal(key);
            return object;
        }
        finally {
            this.endRead();
        }
    }

    protected abstract Object getPropertyInternal(String var1);

    @Override
    public final boolean isEmpty() {
        this.beginRead(false);
        try {
            boolean bl = this.isEmptyInternal();
            return bl;
        }
        finally {
            this.endRead();
        }
    }

    protected abstract boolean isEmptyInternal();

    @Override
    public final int size() {
        this.beginRead(false);
        try {
            int n = this.sizeInternal();
            return n;
        }
        finally {
            this.endRead();
        }
    }

    protected int sizeInternal() {
        int size = 0;
        Iterator<String> keyIt = this.getKeysInternal();
        while (keyIt.hasNext()) {
            keyIt.next();
            ++size;
        }
        return size;
    }

    @Override
    public final boolean containsKey(String key) {
        this.beginRead(false);
        try {
            boolean bl = this.containsKeyInternal(key);
            return bl;
        }
        finally {
            this.endRead();
        }
    }

    protected abstract boolean containsKeyInternal(String var1);

    @Override
    public Properties getProperties(String key) {
        return this.getProperties(key, null);
    }

    public Properties getProperties(String key, Properties defaults) {
        String[] tokens = this.getStringArray(key);
        Properties props = defaults == null ? new Properties() : new Properties(defaults);
        for (String token : tokens) {
            int equalSign = token.indexOf(61);
            if (equalSign <= 0) {
                if (tokens.length == 1 && StringUtils.isEmpty((CharSequence)key)) break;
                throw new IllegalArgumentException('\'' + token + "' does not contain an equals sign");
            }
            String pkey = token.substring(0, equalSign).trim();
            String pvalue = token.substring(equalSign + 1).trim();
            props.put(pkey, pvalue);
        }
        return props;
    }

    @Override
    public boolean getBoolean(String key) {
        Boolean b = this.convert(Boolean.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, b);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getBoolean(key, (Boolean)defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.convert(Boolean.class, key, defaultValue, false);
    }

    @Override
    public byte getByte(String key) {
        Byte b = this.convert(Byte.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, b);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return this.getByte(key, (Byte)defaultValue);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return this.convert(Byte.class, key, defaultValue, false);
    }

    @Override
    public double getDouble(String key) {
        Double d = this.convert(Double.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, d);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return this.getDouble(key, (Double)defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return this.convert(Double.class, key, defaultValue, false);
    }

    @Override
    public Duration getDuration(String key) {
        return AbstractConfiguration.checkNonNullValue(key, this.convert(Duration.class, key, null, true));
    }

    @Override
    public Duration getDuration(String key, Duration defaultValue) {
        return this.convert(Duration.class, key, defaultValue, false);
    }

    @Override
    public float getFloat(String key) {
        Float f = this.convert(Float.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, f).floatValue();
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return this.getFloat(key, Float.valueOf(defaultValue)).floatValue();
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return this.convert(Float.class, key, defaultValue, false);
    }

    @Override
    public int getInt(String key) {
        Integer i = this.convert(Integer.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, i);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return this.getInteger(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return this.convert(Integer.class, key, defaultValue, false);
    }

    @Override
    public long getLong(String key) {
        Long l = this.convert(Long.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, l);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return this.getLong(key, (Long)defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return this.convert(Long.class, key, defaultValue, false);
    }

    @Override
    public short getShort(String key) {
        Short s = this.convert(Short.class, key, null, true);
        return AbstractConfiguration.checkNonNullValue(key, s);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return this.getShort(key, (Short)defaultValue);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return this.convert(Short.class, key, defaultValue, false);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return this.convert(BigDecimal.class, key, null, true);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return this.convert(BigDecimal.class, key, defaultValue, false);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return this.convert(BigInteger.class, key, null, true);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return this.convert(BigInteger.class, key, defaultValue, false);
    }

    @Override
    public String getString(String key) {
        return this.convert(String.class, key, null, true);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String result = this.convert(String.class, key, null, false);
        return result != null ? result : this.interpolate(defaultValue);
    }

    @Override
    public String getEncodedString(String key, ConfigurationDecoder decoder) {
        if (decoder == null) {
            throw new IllegalArgumentException("ConfigurationDecoder must not be null!");
        }
        String value = this.getString(key);
        return value != null ? decoder.decode(value) : null;
    }

    @Override
    public String getEncodedString(String key) {
        ConfigurationDecoder decoder = this.getConfigurationDecoder();
        if (decoder == null) {
            throw new IllegalStateException("No default ConfigurationDecoder defined!");
        }
        return this.getEncodedString(key, decoder);
    }

    @Override
    public String[] getStringArray(String key) {
        String[] result = (String[])this.getArray(String.class, key);
        return result == null ? ArrayUtils.EMPTY_STRING_ARRAY : result;
    }

    @Override
    public List<Object> getList(String key) {
        return this.getList(key, new ArrayList());
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        ArrayList<Object> list;
        Object value = this.getProperty(key);
        if (value instanceof String) {
            list = new ArrayList<String>(1);
            list.add(this.interpolate((String)value));
        } else if (value instanceof List) {
            list = new ArrayList();
            List l = (List)value;
            l.forEach(elem -> list.add(this.interpolate(elem)));
        } else if (value == null) {
            List<?> resultList = defaultValue;
            list = resultList;
        } else {
            if (value.getClass().isArray()) {
                return Arrays.asList((Object[])value);
            }
            if (this.isScalarValue(value)) {
                return Collections.singletonList(value.toString());
            }
            throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a " + value.getClass().getName());
        }
        return list;
    }

    @Override
    public <T> T get(Class<T> cls, String key) {
        return this.convert(cls, key, null, true);
    }

    @Override
    public <T> T get(Class<T> cls, String key, T defaultValue) {
        return this.convert(cls, key, defaultValue, false);
    }

    @Override
    public Object getArray(Class<?> cls, String key) {
        return this.getArray(cls, key, null);
    }

    @Override
    public Object getArray(Class<?> cls, String key, Object defaultValue) {
        return this.convertToArray(cls, key, defaultValue);
    }

    @Override
    public <T> List<T> getList(Class<T> cls, String key) {
        return this.getList(cls, key, null);
    }

    @Override
    public <T> List<T> getList(Class<T> cls, String key, List<T> defaultValue) {
        ArrayList result = new ArrayList();
        if (this.getCollection(cls, key, result, defaultValue) == null) {
            return null;
        }
        return result;
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> cls, String key, Collection<T> target) {
        return this.getCollection(cls, key, target, null);
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> cls, String key, Collection<T> target, Collection<T> defaultValue) {
        Object src = this.getProperty(key);
        if (src == null) {
            return AbstractConfiguration.handleDefaultCollection(target, defaultValue);
        }
        ArrayList targetCol = target != null ? target : new ArrayList();
        this.getConversionHandler().toCollection(src, cls, this.getInterpolator(), targetCol);
        return targetCol;
    }

    protected boolean isScalarValue(Object value) {
        return ClassUtils.wrapperToPrimitive(value.getClass()) != null;
    }

    public void copy(Configuration c) {
        if (c != null) {
            c.lock(LockMode.READ);
            try {
                c.getKeys().forEachRemaining(key -> this.setProperty((String)key, this.encodeForCopy(c.getProperty((String)key))));
            }
            finally {
                c.unlock(LockMode.READ);
            }
        }
    }

    public void append(Configuration c) {
        if (c != null) {
            c.lock(LockMode.READ);
            try {
                c.getKeys().forEachRemaining(key -> this.addProperty((String)key, this.encodeForCopy(c.getProperty((String)key))));
            }
            finally {
                c.unlock(LockMode.READ);
            }
        }
    }

    public Configuration interpolatedConfiguration() {
        AbstractConfiguration c = (AbstractConfiguration)ConfigurationUtils.cloneConfiguration(this);
        c.setListDelimiterHandler(new DisabledListDelimiterHandler());
        this.getKeys().forEachRemaining(key -> c.setProperty((String)key, this.getList((String)key)));
        c.setListDelimiterHandler(this.getListDelimiterHandler());
        return c;
    }

    protected final void initLogger(ConfigurationLogger log) {
        this.log = log != null ? log : ConfigurationLogger.newDummyLogger();
    }

    private Object encodeForCopy(Object value) {
        if (value instanceof Collection) {
            return this.encodeListForCopy((Collection)value);
        }
        return this.getListDelimiterHandler().escape(value, ListDelimiterHandler.NOOP_TRANSFORMER);
    }

    private Object encodeListForCopy(Collection<?> values) {
        return values.stream().map(this::encodeForCopy).collect(Collectors.toList());
    }

    private <T> T getAndConvertProperty(Class<T> cls, String key, T defaultValue) {
        Object value = this.getProperty(key);
        try {
            return (T)ObjectUtils.defaultIfNull(this.getConversionHandler().to(value, cls, this.getInterpolator()), defaultValue);
        }
        catch (ConversionException cex) {
            throw new ConversionException(String.format("Key '%s' cannot be converted to class %s. Value is: '%s'.", key, cls.getName(), String.valueOf(value)), cex.getCause());
        }
    }

    private <T> T convert(Class<T> cls, String key, T defValue, boolean throwOnMissing) {
        if (cls.isArray()) {
            return cls.cast(this.convertToArray(cls.getComponentType(), key, defValue));
        }
        T result = this.getAndConvertProperty(cls, key, defValue);
        if (result == null) {
            if (throwOnMissing && this.isThrowExceptionOnMissing()) {
                AbstractConfiguration.throwMissingPropertyException(key);
            }
            return defValue;
        }
        return result;
    }

    private Object convertToArray(Class<?> cls, String key, Object defaultValue) {
        AbstractConfiguration.checkDefaultValueArray(cls, defaultValue);
        return ObjectUtils.defaultIfNull((Object)this.getConversionHandler().toArray(this.getProperty(key), cls, this.getInterpolator()), (Object)defaultValue);
    }

    private static void checkDefaultValueArray(Class<?> cls, Object defaultValue) {
        if (!(defaultValue == null || defaultValue.getClass().isArray() && cls.isAssignableFrom(defaultValue.getClass().getComponentType()))) {
            throw new IllegalArgumentException("The type of the default value (" + defaultValue.getClass() + ") is not an array of the specified class (" + cls + ")");
        }
    }

    private static <T> Collection<T> handleDefaultCollection(Collection<T> target, Collection<T> defaultValue) {
        Collection<T> result;
        if (defaultValue == null) {
            return null;
        }
        if (target == null) {
            result = new ArrayList<T>(defaultValue);
        } else {
            target.addAll(defaultValue);
            result = target;
        }
        return result;
    }

    private static <T> T checkNonNullValue(String key, T value) {
        if (value == null) {
            AbstractConfiguration.throwMissingPropertyException(key);
        }
        return value;
    }

    private static void throwMissingPropertyException(String key) {
        throw new NoSuchElementException(String.format("Key '%s' does not map to an existing object!", key));
    }
}


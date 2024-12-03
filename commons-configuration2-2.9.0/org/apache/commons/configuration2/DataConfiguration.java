/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class DataConfiguration
extends AbstractConfiguration {
    public static final String DATE_FORMAT_KEY = "org.apache.commons.configuration.format.date";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    private static final URI[] EMPTY_URI_ARRAY = new URI[0];
    private static final Locale[] EMPTY_LOCALE_ARRAY = new Locale[0];
    private static final Date[] EMPTY_DATE_ARRAY = new Date[0];
    private static final Color[] EMPTY_COLOR_ARRAY = new Color[0];
    private static final Calendar[] EMPTY_CALENDARD_ARRAY = new Calendar[0];
    private static final BigInteger[] EMPTY_BIG_INTEGER_ARRAY = new BigInteger[0];
    private static final BigDecimal[] EMPTY_BIG_DECIMAL_ARRAY = new BigDecimal[0];
    private static final ThreadLocal<String> TEMP_DATE_FORMAT = new ThreadLocal();
    private final Configuration configuration;
    private final ConversionHandler dataConversionHandler;

    public DataConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.dataConversionHandler = new DataConversionHandler();
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ConversionHandler getConversionHandler() {
        return this.dataConversionHandler;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.configuration.getProperty(key);
    }

    @Override
    protected void addPropertyInternal(String key, Object obj) {
        this.configuration.addProperty(key, obj);
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        if (this.configuration instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.configuration).addPropertyDirect(key, value);
        } else {
            this.configuration.addProperty(key, value);
        }
    }

    @Override
    protected boolean isEmptyInternal() {
        return this.configuration.isEmpty();
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.configuration.containsKey(key);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.configuration.clearProperty(key);
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        this.configuration.setProperty(key, value);
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.configuration.getKeys();
    }

    public List<Boolean> getBooleanList(String key) {
        return this.getBooleanList(key, new ArrayList<Boolean>());
    }

    public List<Boolean> getBooleanList(String key, List<Boolean> defaultValue) {
        return this.getList(Boolean.class, key, defaultValue);
    }

    public boolean[] getBooleanArray(String key) {
        return (boolean[])this.getArray(Boolean.TYPE, key);
    }

    public boolean[] getBooleanArray(String key, boolean ... defaultValue) {
        return this.get(boolean[].class, key, defaultValue);
    }

    public List<Byte> getByteList(String key) {
        return this.getByteList(key, new ArrayList<Byte>());
    }

    public List<Byte> getByteList(String key, List<Byte> defaultValue) {
        return this.getList(Byte.class, key, defaultValue);
    }

    public byte[] getByteArray(String key) {
        return this.getByteArray(key, ArrayUtils.EMPTY_BYTE_ARRAY);
    }

    public byte[] getByteArray(String key, byte ... defaultValue) {
        return this.get(byte[].class, key, defaultValue);
    }

    public List<Short> getShortList(String key) {
        return this.getShortList(key, new ArrayList<Short>());
    }

    public List<Short> getShortList(String key, List<Short> defaultValue) {
        return this.getList(Short.class, key, defaultValue);
    }

    public short[] getShortArray(String key) {
        return this.getShortArray(key, ArrayUtils.EMPTY_SHORT_ARRAY);
    }

    public short[] getShortArray(String key, short ... defaultValue) {
        return this.get(short[].class, key, defaultValue);
    }

    public List<Integer> getIntegerList(String key) {
        return this.getIntegerList(key, new ArrayList<Integer>());
    }

    public List<Integer> getIntegerList(String key, List<Integer> defaultValue) {
        return this.getList(Integer.class, key, defaultValue);
    }

    public int[] getIntArray(String key) {
        return this.getIntArray(key, ArrayUtils.EMPTY_INT_ARRAY);
    }

    public int[] getIntArray(String key, int ... defaultValue) {
        return this.get(int[].class, key, defaultValue);
    }

    public List<Long> getLongList(String key) {
        return this.getLongList(key, new ArrayList<Long>());
    }

    public List<Long> getLongList(String key, List<Long> defaultValue) {
        return this.getList(Long.class, key, defaultValue);
    }

    public long[] getLongArray(String key) {
        return this.getLongArray(key, ArrayUtils.EMPTY_LONG_ARRAY);
    }

    public long[] getLongArray(String key, long ... defaultValue) {
        return this.get(long[].class, key, defaultValue);
    }

    public List<Float> getFloatList(String key) {
        return this.getFloatList(key, new ArrayList<Float>());
    }

    public List<Float> getFloatList(String key, List<Float> defaultValue) {
        return this.getList(Float.class, key, defaultValue);
    }

    public float[] getFloatArray(String key) {
        return this.getFloatArray(key, ArrayUtils.EMPTY_FLOAT_ARRAY);
    }

    public float[] getFloatArray(String key, float ... defaultValue) {
        return this.get(float[].class, key, defaultValue);
    }

    public List<Double> getDoubleList(String key) {
        return this.getDoubleList(key, new ArrayList<Double>());
    }

    public List<Double> getDoubleList(String key, List<Double> defaultValue) {
        return this.getList(Double.class, key, defaultValue);
    }

    public double[] getDoubleArray(String key) {
        return this.getDoubleArray(key, ArrayUtils.EMPTY_DOUBLE_ARRAY);
    }

    public double[] getDoubleArray(String key, double ... defaultValue) {
        return this.get(double[].class, key, defaultValue);
    }

    public List<BigInteger> getBigIntegerList(String key) {
        return this.getBigIntegerList(key, new ArrayList<BigInteger>());
    }

    public List<BigInteger> getBigIntegerList(String key, List<BigInteger> defaultValue) {
        return this.getList(BigInteger.class, key, defaultValue);
    }

    public BigInteger[] getBigIntegerArray(String key) {
        return this.getBigIntegerArray(key, EMPTY_BIG_INTEGER_ARRAY);
    }

    public BigInteger[] getBigIntegerArray(String key, BigInteger ... defaultValue) {
        return this.get(BigInteger[].class, key, defaultValue);
    }

    public List<BigDecimal> getBigDecimalList(String key) {
        return this.getBigDecimalList(key, new ArrayList<BigDecimal>());
    }

    public List<BigDecimal> getBigDecimalList(String key, List<BigDecimal> defaultValue) {
        return this.getList(BigDecimal.class, key, defaultValue);
    }

    public BigDecimal[] getBigDecimalArray(String key) {
        return this.getBigDecimalArray(key, EMPTY_BIG_DECIMAL_ARRAY);
    }

    public BigDecimal[] getBigDecimalArray(String key, BigDecimal ... defaultValue) {
        return this.get(BigDecimal[].class, key, defaultValue);
    }

    public URI getURI(String key) {
        return this.get(URI.class, key);
    }

    public URI getURI(String key, URI defaultValue) {
        return this.get(URI.class, key, defaultValue);
    }

    public URI[] getURIArray(String key) {
        return this.getURIArray(key, EMPTY_URI_ARRAY);
    }

    public URI[] getURIArray(String key, URI ... defaultValue) {
        return this.get(URI[].class, key, defaultValue);
    }

    public List<URI> getURIList(String key) {
        return this.getURIList(key, new ArrayList<URI>());
    }

    public List<URI> getURIList(String key, List<URI> defaultValue) {
        return this.getList(URI.class, key, defaultValue);
    }

    public URL getURL(String key) {
        return this.get(URL.class, key);
    }

    public URL getURL(String key, URL defaultValue) {
        return this.get(URL.class, key, defaultValue);
    }

    public List<URL> getURLList(String key) {
        return this.getURLList(key, new ArrayList<URL>());
    }

    public List<URL> getURLList(String key, List<URL> defaultValue) {
        return this.getList(URL.class, key, defaultValue);
    }

    public URL[] getURLArray(String key) {
        return this.getURLArray(key, EMPTY_URL_ARRAY);
    }

    public URL[] getURLArray(String key, URL ... defaultValue) {
        return this.get(URL[].class, key, defaultValue);
    }

    public Date getDate(String key) {
        return this.get(Date.class, key);
    }

    public Date getDate(String key, String format) {
        Date value = this.getDate(key, null, format);
        if (value != null) {
            return value;
        }
        if (this.isThrowExceptionOnMissing()) {
            throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
        }
        return null;
    }

    public Date getDate(String key, Date defaultValue) {
        return this.getDate(key, defaultValue, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Date getDate(String key, Date defaultValue, String format) {
        TEMP_DATE_FORMAT.set(format);
        try {
            Date date = this.get(Date.class, key, defaultValue);
            return date;
        }
        finally {
            TEMP_DATE_FORMAT.remove();
        }
    }

    public List<Date> getDateList(String key) {
        return this.getDateList(key, new ArrayList<Date>());
    }

    public List<Date> getDateList(String key, String format) {
        return this.getDateList(key, new ArrayList<Date>(), format);
    }

    public List<Date> getDateList(String key, List<Date> defaultValue) {
        return this.getDateList(key, defaultValue, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Date> getDateList(String key, List<Date> defaultValue, String format) {
        TEMP_DATE_FORMAT.set(format);
        try {
            List<Date> list = this.getList(Date.class, key, defaultValue);
            return list;
        }
        finally {
            TEMP_DATE_FORMAT.remove();
        }
    }

    public Date[] getDateArray(String key) {
        return this.getDateArray(key, EMPTY_DATE_ARRAY);
    }

    public Date[] getDateArray(String key, String format) {
        return this.getDateArray(key, EMPTY_DATE_ARRAY, format);
    }

    public Date[] getDateArray(String key, Date ... defaultValue) {
        return this.getDateArray(key, defaultValue, (String)null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Date[] getDateArray(String key, Date[] defaultValue, String format) {
        TEMP_DATE_FORMAT.set(format);
        try {
            Date[] dateArray = this.get(Date[].class, key, defaultValue);
            return dateArray;
        }
        finally {
            TEMP_DATE_FORMAT.remove();
        }
    }

    public Calendar getCalendar(String key) {
        return this.get(Calendar.class, key);
    }

    public Calendar getCalendar(String key, String format) {
        Calendar value = this.getCalendar(key, null, format);
        if (value != null) {
            return value;
        }
        if (this.isThrowExceptionOnMissing()) {
            throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
        }
        return null;
    }

    public Calendar getCalendar(String key, Calendar defaultValue) {
        return this.getCalendar(key, defaultValue, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Calendar getCalendar(String key, Calendar defaultValue, String format) {
        TEMP_DATE_FORMAT.set(format);
        try {
            Calendar calendar = this.get(Calendar.class, key, defaultValue);
            return calendar;
        }
        finally {
            TEMP_DATE_FORMAT.remove();
        }
    }

    public List<Calendar> getCalendarList(String key) {
        return this.getCalendarList(key, new ArrayList<Calendar>());
    }

    public List<Calendar> getCalendarList(String key, String format) {
        return this.getCalendarList(key, new ArrayList<Calendar>(), format);
    }

    public List<Calendar> getCalendarList(String key, List<Calendar> defaultValue) {
        return this.getCalendarList(key, defaultValue, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Calendar> getCalendarList(String key, List<Calendar> defaultValue, String format) {
        TEMP_DATE_FORMAT.set(format);
        try {
            List<Calendar> list = this.getList(Calendar.class, key, defaultValue);
            return list;
        }
        finally {
            TEMP_DATE_FORMAT.remove();
        }
    }

    public Calendar[] getCalendarArray(String key) {
        return this.getCalendarArray(key, EMPTY_CALENDARD_ARRAY);
    }

    public Calendar[] getCalendarArray(String key, String format) {
        return this.getCalendarArray(key, EMPTY_CALENDARD_ARRAY, format);
    }

    public Calendar[] getCalendarArray(String key, Calendar ... defaultValue) {
        return this.getCalendarArray(key, defaultValue, (String)null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Calendar[] getCalendarArray(String key, Calendar[] defaultValue, String format) {
        TEMP_DATE_FORMAT.set(format);
        try {
            Calendar[] calendarArray = this.get(Calendar[].class, key, defaultValue);
            return calendarArray;
        }
        finally {
            TEMP_DATE_FORMAT.remove();
        }
    }

    private String getDefaultDateFormat() {
        return this.getString(DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT);
    }

    public Locale getLocale(String key) {
        return this.get(Locale.class, key);
    }

    public Locale getLocale(String key, Locale defaultValue) {
        return this.get(Locale.class, key, defaultValue);
    }

    public List<Locale> getLocaleList(String key) {
        return this.getLocaleList(key, new ArrayList<Locale>());
    }

    public List<Locale> getLocaleList(String key, List<Locale> defaultValue) {
        return this.getList(Locale.class, key, defaultValue);
    }

    public Locale[] getLocaleArray(String key) {
        return this.getLocaleArray(key, EMPTY_LOCALE_ARRAY);
    }

    public Locale[] getLocaleArray(String key, Locale ... defaultValue) {
        return this.get(Locale[].class, key, defaultValue);
    }

    public Color getColor(String key) {
        return this.get(Color.class, key);
    }

    public Color getColor(String key, Color defaultValue) {
        return this.get(Color.class, key, defaultValue);
    }

    public List<Color> getColorList(String key) {
        return this.getColorList(key, new ArrayList<Color>());
    }

    public List<Color> getColorList(String key, List<Color> defaultValue) {
        return this.getList(Color.class, key, defaultValue);
    }

    public Color[] getColorArray(String key) {
        return this.getColorArray(key, EMPTY_COLOR_ARRAY);
    }

    public Color[] getColorArray(String key, Color ... defaultValue) {
        return this.get(Color[].class, key, defaultValue);
    }

    private DefaultConversionHandler getOriginalConversionHandler() {
        ConversionHandler handler = super.getConversionHandler();
        return (DefaultConversionHandler)(handler instanceof DefaultConversionHandler ? handler : null);
    }

    private class DataConversionHandler
    extends DefaultConversionHandler {
        private DataConversionHandler() {
        }

        @Override
        public String getDateFormat() {
            if (StringUtils.isNotEmpty((CharSequence)((CharSequence)TEMP_DATE_FORMAT.get()))) {
                return (String)TEMP_DATE_FORMAT.get();
            }
            if (DataConfiguration.this.containsKey(DataConfiguration.DATE_FORMAT_KEY)) {
                return DataConfiguration.this.getDefaultDateFormat();
            }
            DefaultConversionHandler orgHandler = DataConfiguration.this.getOriginalConversionHandler();
            return orgHandler != null ? orgHandler.getDateFormat() : null;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.tika.metadata.ClimateForcast;
import org.apache.tika.metadata.CreativeCommons;
import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.MSOffice;
import org.apache.tika.metadata.Message;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.PropertyTypeException;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.metadata.TikaMimeKeys;
import org.apache.tika.utils.DateUtils;

public class Metadata
implements CreativeCommons,
Geographic,
HttpHeaders,
Message,
MSOffice,
ClimateForcast,
TIFF,
TikaMetadataKeys,
TikaMimeKeys,
Serializable {
    private static final long serialVersionUID = 5623926545693153182L;
    private Map<String, String[]> metadata = new HashMap<String, String[]>();
    public static final String NAMESPACE_PREFIX_DELIMITER = ":";
    public static final String FORMAT = "format";
    public static final String IDENTIFIER = "identifier";
    public static final String MODIFIED = "modified";
    public static final String CONTRIBUTOR = "contributor";
    public static final String COVERAGE = "coverage";
    public static final String CREATOR = "creator";
    public static final Property DATE = Property.internalDate("date");
    public static final String DESCRIPTION = "description";
    public static final String LANGUAGE = "language";
    public static final String PUBLISHER = "publisher";
    public static final String RELATION = "relation";
    public static final String RIGHTS = "rights";
    public static final String SOURCE = "source";
    public static final String SUBJECT = "subject";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    private static final DateUtils DATE_UTILS = new DateUtils();

    private static DateFormat createDateFormat(String format, TimeZone timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, new DateFormatSymbols(Locale.US));
        if (timezone != null) {
            sdf.setTimeZone(timezone);
        }
        return sdf;
    }

    private static synchronized Date parseDate(String date) {
        return DATE_UTILS.tryToParse(date);
    }

    public boolean isMultiValued(Property property) {
        return this.metadata.get(property.getName()) != null && this.metadata.get(property.getName()).length > 1;
    }

    public boolean isMultiValued(String name) {
        return this.metadata.get(name) != null && this.metadata.get(name).length > 1;
    }

    public String[] names() {
        return this.metadata.keySet().toArray(new String[this.metadata.keySet().size()]);
    }

    public String get(String name) {
        String[] values = this.metadata.get(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    public String get(Property property) {
        return this.get(property.getName());
    }

    public Integer getInt(Property property) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            return null;
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            return null;
        }
        String v = this.get(property);
        if (v == null) {
            return null;
        }
        try {
            return Integer.valueOf(v);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public Date getDate(Property property) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            return null;
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            return null;
        }
        String v = this.get(property);
        if (v != null) {
            return Metadata.parseDate(v);
        }
        return null;
    }

    public String[] getValues(Property property) {
        return this._getValues(property.getName());
    }

    public String[] getValues(String name) {
        return this._getValues(name);
    }

    private String[] _getValues(String name) {
        String[] values = this.metadata.get(name);
        if (values == null) {
            values = new String[]{};
        }
        return values;
    }

    private String[] appendedValues(String[] values, String value) {
        String[] newValues = new String[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 1] = value;
        return newValues;
    }

    public void add(String name, String value) {
        String[] values = this.metadata.get(name);
        if (values == null) {
            this.set(name, value);
        } else {
            this.metadata.put(name, this.appendedValues(values, value));
        }
    }

    public void add(Property property, String value) {
        String[] values = this.metadata.get(property.getName());
        if (values == null) {
            this.set(property, value);
        } else if (property.isMultiValuePermitted()) {
            this.set(property, this.appendedValues(values, value));
        } else {
            throw new PropertyTypeException(property.getName() + " : " + (Object)((Object)property.getPropertyType()));
        }
    }

    public void setAll(Properties properties) {
        Enumeration<?> names = properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            this.metadata.put(name, new String[]{properties.getProperty(name)});
        }
    }

    public void set(String name, String value) {
        if (value != null) {
            this.metadata.put(name, new String[]{value});
        } else {
            this.metadata.remove(name);
        }
    }

    public void set(Property property, String value) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        if (property.getPropertyType() == Property.PropertyType.COMPOSITE) {
            this.set(property.getPrimaryProperty(), value);
            if (property.getSecondaryExtractProperties() != null) {
                for (Property secondaryExtractProperty : property.getSecondaryExtractProperties()) {
                    this.set(secondaryExtractProperty, value);
                }
            }
        } else {
            this.set(property.getName(), value);
        }
    }

    public void set(Property property, String[] values) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        if (property.getPropertyType() == Property.PropertyType.COMPOSITE) {
            this.set(property.getPrimaryProperty(), values);
            if (property.getSecondaryExtractProperties() != null) {
                for (Property secondaryExtractProperty : property.getSecondaryExtractProperties()) {
                    this.set(secondaryExtractProperty, values);
                }
            }
        } else {
            this.metadata.put(property.getName(), values);
        }
    }

    public void set(Property property, int value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            throw new PropertyTypeException(Property.ValueType.INTEGER, property.getPrimaryProperty().getValueType());
        }
        this.set(property, Integer.toString(value));
    }

    public void add(Property property, int value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SEQ) {
            throw new PropertyTypeException(Property.PropertyType.SEQ, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            throw new PropertyTypeException(Property.ValueType.INTEGER, property.getPrimaryProperty().getValueType());
        }
        this.add(property, Integer.toString(value));
    }

    public int[] getIntValues(Property property) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SEQ) {
            throw new PropertyTypeException(Property.PropertyType.SEQ, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            throw new PropertyTypeException(Property.ValueType.INTEGER, property.getPrimaryProperty().getValueType());
        }
        String[] vals = this.getValues(property);
        int[] ret = new int[vals.length];
        for (int i = 0; i < vals.length; ++i) {
            ret[i] = Integer.parseInt(vals[i]);
        }
        return ret;
    }

    public void set(Property property, double value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.REAL && property.getPrimaryProperty().getValueType() != Property.ValueType.RATIONAL) {
            throw new PropertyTypeException(Property.ValueType.REAL, property.getPrimaryProperty().getValueType());
        }
        this.set(property, Double.toString(value));
    }

    public void set(Property property, Date date) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            throw new PropertyTypeException(Property.ValueType.DATE, property.getPrimaryProperty().getValueType());
        }
        String dateString = null;
        if (date != null) {
            dateString = DateUtils.formatDate(date);
        }
        this.set(property, dateString);
    }

    public void set(Property property, Calendar date) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            throw new PropertyTypeException(Property.ValueType.DATE, property.getPrimaryProperty().getValueType());
        }
        String dateString = null;
        if (date != null) {
            dateString = DateUtils.formatDate(date);
        }
        this.set(property, dateString);
    }

    public void remove(String name) {
        this.metadata.remove(name);
    }

    public int size() {
        return this.metadata.size();
    }

    public int hashCode() {
        int h = 0;
        Iterator<Map.Entry<String, String[]>> i = this.metadata.entrySet().iterator();
        while (i.hasNext()) {
            h += this.getMetadataEntryHashCode(i.next());
        }
        return h;
    }

    private int getMetadataEntryHashCode(Map.Entry<String, String[]> e) {
        return Objects.hashCode(e.getKey()) ^ Arrays.hashCode(e.getValue());
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        Metadata other = null;
        try {
            other = (Metadata)o;
        }
        catch (ClassCastException cce) {
            return false;
        }
        if (other.size() != this.size()) {
            return false;
        }
        String[] names = this.names();
        for (int i = 0; i < names.length; ++i) {
            String[] thisValues;
            String[] otherValues = other._getValues(names[i]);
            if (otherValues.length != (thisValues = this._getValues(names[i])).length) {
                return false;
            }
            for (int j = 0; j < otherValues.length; ++j) {
                if (otherValues[j].equals(thisValues[j])) continue;
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String[] names = this.names();
        for (int i = 0; i < names.length; ++i) {
            String[] values = this._getValues(names[i]);
            for (int j = 0; j < values.length; ++j) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(names[i]).append("=").append(values[j]);
            }
        }
        return buf.toString();
    }
}


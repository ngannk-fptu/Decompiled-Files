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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.tika.metadata.ClimateForcast;
import org.apache.tika.metadata.CreativeCommons;
import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Message;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.PropertyTypeException;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.metadata.TikaMimeKeys;
import org.apache.tika.metadata.writefilter.MetadataWriteFilter;
import org.apache.tika.utils.DateUtils;

public class Metadata
implements CreativeCommons,
Geographic,
HttpHeaders,
Message,
ClimateForcast,
TIFF,
TikaMimeKeys,
Serializable {
    private static final MetadataWriteFilter ACCEPT_ALL = new MetadataWriteFilter(){

        @Override
        public void filterExisting(Map<String, String[]> data) {
        }

        @Override
        public void add(String field, String value, Map<String, String[]> data) {
            String[] values = data.get(field);
            if (values == null) {
                this.set(field, value, data);
            } else {
                data.put(field, this.appendValues(values, value));
            }
        }

        @Override
        public void set(String field, String value, Map<String, String[]> data) {
            if (value != null) {
                data.put(field, new String[]{value});
            } else {
                data.remove(field);
            }
        }

        private String[] appendValues(String[] values, String value) {
            if (value == null) {
                return values;
            }
            String[] newValues = new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            newValues[newValues.length - 1] = value;
            return newValues;
        }
    };
    private static final long serialVersionUID = 5623926545693153182L;
    private static final DateUtils DATE_UTILS = new DateUtils();
    private Map<String, String[]> metadata = null;
    private MetadataWriteFilter writeFilter = ACCEPT_ALL;

    public Metadata() {
        this.metadata = new HashMap<String, String[]>();
    }

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
        return this.metadata.keySet().toArray(new String[0]);
    }

    public String get(String name) {
        String[] values = this.metadata.get(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    public void setMetadataWriteFilter(MetadataWriteFilter writeFilter) {
        this.writeFilter = writeFilter;
        this.writeFilter.filterExisting(this.metadata);
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

    public void add(String name, String value) {
        this.writeFilter.add(name, value, this.metadata);
    }

    protected void add(String name, String[] newValues) {
        String[] values = this.metadata.get(name);
        if (values == null) {
            this.set(name, newValues);
        } else {
            for (String val : newValues) {
                this.add(name, val);
            }
        }
    }

    public void add(Property property, String value) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        if (property.getPropertyType() == Property.PropertyType.COMPOSITE) {
            this.add(property.getPrimaryProperty(), value);
            if (property.getSecondaryExtractProperties() != null) {
                for (Property secondaryExtractProperty : property.getSecondaryExtractProperties()) {
                    this.add(secondaryExtractProperty, value);
                }
            }
        } else {
            String[] values = this.metadata.get(property.getName());
            if (values == null) {
                this.set(property, value);
            } else if (property.isMultiValuePermitted()) {
                this.add(property.getName(), value);
            } else {
                throw new PropertyTypeException(property.getName() + " : " + (Object)((Object)property.getPropertyType()));
            }
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
        this.writeFilter.set(name, value, this.metadata);
    }

    protected void set(String name, String[] values) {
        if (values != null) {
            this.metadata.remove(name);
            for (String v : values) {
                this.add(name, v);
            }
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
            this.set(property.getName(), values);
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

    public void set(Property property, long value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.REAL) {
            throw new PropertyTypeException(Property.ValueType.REAL, property.getPrimaryProperty().getValueType());
        }
        this.set(property, Long.toString(value));
    }

    public void set(Property property, boolean value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.BOOLEAN) {
            throw new PropertyTypeException(Property.ValueType.BOOLEAN, property.getPrimaryProperty().getValueType());
        }
        this.set(property, Boolean.toString(value));
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

    public void add(Property property, Calendar date) {
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            throw new PropertyTypeException(Property.ValueType.DATE, property.getPrimaryProperty().getValueType());
        }
        String dateString = null;
        if (date != null) {
            dateString = DateUtils.formatDate(date);
        }
        this.add(property, dateString);
    }

    public void remove(String name) {
        this.metadata.remove(name);
    }

    public int size() {
        return this.metadata.size();
    }

    public int hashCode() {
        int h = 0;
        for (Map.Entry<String, String[]> stringEntry : this.metadata.entrySet()) {
            h += this.getMetadataEntryHashCode(stringEntry);
        }
        return h;
    }

    private int getMetadataEntryHashCode(Map.Entry<String, String[]> e) {
        return Objects.hashCode(e.getKey()) ^ Arrays.hashCode(e.getValue());
    }

    public boolean equals(Object o) {
        String[] names;
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
        for (String name : names = this.names()) {
            String[] thisValues;
            String[] otherValues = other._getValues(name);
            if (otherValues.length != (thisValues = this._getValues(name)).length) {
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
        String[] names;
        StringBuffer buf = new StringBuffer();
        for (String name : names = this.names()) {
            String[] values;
            for (String value : values = this._getValues(name)) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(name).append("=").append(value);
            }
        }
        return buf.toString();
    }
}


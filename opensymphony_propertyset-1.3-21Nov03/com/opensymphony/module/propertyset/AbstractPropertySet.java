/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.Data
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySchema;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetSchema;
import com.opensymphony.util.Data;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public abstract class AbstractPropertySet
implements PropertySet {
    private static final Log logger = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$AbstractPropertySet == null ? (class$com$opensymphony$module$propertyset$AbstractPropertySet = AbstractPropertySet.class$("com.opensymphony.module.propertyset.AbstractPropertySet")) : class$com$opensymphony$module$propertyset$AbstractPropertySet));
    protected PropertySetSchema schema;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$AbstractPropertySet;

    public abstract Collection getKeys(String var1, int var2) throws PropertyException;

    public abstract int getType(String var1) throws PropertyException;

    public abstract boolean exists(String var1) throws PropertyException;

    public abstract void remove(String var1) throws PropertyException;

    public void setAsActualType(String key, Object value) throws PropertyException {
        int type = value instanceof Boolean ? 1 : (value instanceof Integer ? 2 : (value instanceof Long ? 3 : (value instanceof Double ? 4 : (value instanceof String ? (value.toString().length() > 255 ? 6 : 5) : (value instanceof Date ? 7 : (value instanceof Document ? 9 : (value instanceof byte[] ? 10 : (value instanceof Properties ? 11 : 8))))))));
        this.set(type, key, value);
    }

    public Object getAsActualType(String key) throws PropertyException {
        int type = this.getType(key);
        Object value = null;
        switch (type) {
            case 1: {
                value = new Boolean(this.getBoolean(key));
                break;
            }
            case 2: {
                value = new Integer(this.getInt(key));
                break;
            }
            case 3: {
                value = new Long(this.getLong(key));
                break;
            }
            case 4: {
                value = new Double(this.getDouble(key));
                break;
            }
            case 5: {
                value = this.getString(key);
                break;
            }
            case 6: {
                value = this.getText(key);
                break;
            }
            case 7: {
                value = this.getDate(key);
                break;
            }
            case 9: {
                value = this.getXML(key);
                break;
            }
            case 10: {
                value = this.getData(key);
                break;
            }
            case 11: {
                value = this.getProperties(key);
                break;
            }
            case 8: {
                value = this.getObject(key);
            }
        }
        return value;
    }

    public void setBoolean(String key, boolean value) {
        this.set(1, key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public boolean getBoolean(String key) {
        try {
            return (Boolean)this.get(1, key);
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    public void setData(String key, byte[] value) {
        this.set(10, key, new Data(value));
    }

    public byte[] getData(String key) {
        try {
            Object data = this.get(10, key);
            if (data instanceof Data) {
                return ((Data)data).getBytes();
            }
            if (data instanceof byte[]) {
                return (byte[])data;
            }
            logger.error((Object)("DATA type is " + data.getClass() + ", expected byte[] or Data"));
        }
        catch (NullPointerException e) {
            return null;
        }
        return null;
    }

    public void setDate(String key, Date value) {
        this.set(7, key, value);
    }

    public Date getDate(String key) {
        try {
            return (Date)this.get(7, key);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setDouble(String key, double value) {
        this.set(4, key, new Double(value));
    }

    public double getDouble(String key) {
        try {
            return (Double)this.get(4, key);
        }
        catch (NullPointerException e) {
            return 0.0;
        }
    }

    public void setInt(String key, int value) {
        this.set(2, key, new Integer(value));
    }

    public int getInt(String key) {
        try {
            return (Integer)this.get(2, key);
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public Collection getKeys() throws PropertyException {
        return this.getKeys(null, 0);
    }

    public Collection getKeys(int type) throws PropertyException {
        return this.getKeys(null, type);
    }

    public Collection getKeys(String prefix) throws PropertyException {
        return this.getKeys(prefix, 0);
    }

    public void setLong(String key, long value) {
        this.set(3, key, new Long(value));
    }

    public long getLong(String key) {
        try {
            return (Long)this.get(3, key);
        }
        catch (NullPointerException e) {
            return 0L;
        }
    }

    public void setObject(String key, Object value) {
        this.set(8, key, value);
    }

    public Object getObject(String key) {
        try {
            return this.get(8, key);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setProperties(String key, Properties value) {
        this.set(11, key, value);
    }

    public Properties getProperties(String key) {
        try {
            return (Properties)this.get(11, key);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setSchema(PropertySetSchema schema) {
        this.schema = schema;
    }

    public PropertySetSchema getSchema() {
        return this.schema;
    }

    public boolean isSettable(String property) {
        return true;
    }

    public void setString(String key, String value) {
        if (value != null && value.length() > 255) {
            throw new IllegalPropertyException("String exceeds 255 characters.");
        }
        this.set(5, key, value);
    }

    public String getString(String key) {
        try {
            return (String)this.get(5, key);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setText(String key, String value) {
        this.set(6, key, value);
    }

    public String getText(String key) {
        try {
            return (String)this.get(6, key);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setXML(String key, Document value) {
        this.set(9, key, value);
    }

    public Document getXML(String key) {
        try {
            return (Document)this.get(9, key);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void init(Map config, Map args) {
    }

    public boolean supportsType(int type) {
        return true;
    }

    public boolean supportsTypes() {
        return true;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(this.getClass().getName());
        result.append(" {\n");
        try {
            Iterator keys = this.getKeys().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                int type = this.getType(key);
                if (type <= 0) continue;
                result.append('\t');
                result.append(key);
                result.append(" = ");
                result.append(this.get(type, key));
                result.append('\n');
            }
        }
        catch (PropertyException propertyException) {
            // empty catch block
        }
        result.append("}\n");
        return result.toString();
    }

    protected abstract void setImpl(int var1, String var2, Object var3) throws PropertyException;

    protected abstract Object get(int var1, String var2) throws PropertyException;

    protected String type(int type) {
        switch (type) {
            case 1: {
                return "boolean";
            }
            case 2: {
                return "int";
            }
            case 3: {
                return "long";
            }
            case 4: {
                return "double";
            }
            case 5: {
                return "string";
            }
            case 6: {
                return "text";
            }
            case 7: {
                return "date";
            }
            case 8: {
                return "object";
            }
            case 9: {
                return "xml";
            }
            case 10: {
                return "data";
            }
            case 11: {
                return "properties";
            }
        }
        return null;
    }

    protected int type(String type) {
        if (type == null) {
            return 0;
        }
        if ((type = type.toLowerCase()).equals("boolean")) {
            return 1;
        }
        if (type.equals("int")) {
            return 2;
        }
        if (type.equals("long")) {
            return 3;
        }
        if (type.equals("double")) {
            return 4;
        }
        if (type.equals("string")) {
            return 5;
        }
        if (type.equals("text")) {
            return 6;
        }
        if (type.equals("date")) {
            return 7;
        }
        if (type.equals("object")) {
            return 8;
        }
        if (type.equals("xml")) {
            return 9;
        }
        if (type.equals("data")) {
            return 10;
        }
        if (type.equals("properties")) {
            return 11;
        }
        return 0;
    }

    private void set(int type, String key, Object value) throws PropertyException {
        if (this.schema != null) {
            PropertySchema ps = this.schema.getPropertySchema(key);
            if (ps == null && this.schema.isRestricted()) {
                throw new IllegalPropertyException("Property " + key + " not explicitly specified in restricted schema.");
            }
            if (this.supportsTypes() && ps.getType() != type) {
                throw new InvalidPropertyTypeException("Property " + key + " has invalid type " + type + " expected type=" + ps.getType());
            }
            ps.validate(value);
        }
        this.setImpl(type, key, value);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


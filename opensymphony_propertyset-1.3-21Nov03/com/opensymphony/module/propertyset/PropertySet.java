/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySetSchema;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;

public interface PropertySet {
    public static final int BOOLEAN = 1;
    public static final int DATA = 10;
    public static final int DATE = 7;
    public static final int DOUBLE = 4;
    public static final int INT = 2;
    public static final int LONG = 3;
    public static final int OBJECT = 8;
    public static final int PROPERTIES = 11;
    public static final int STRING = 5;
    public static final int TEXT = 6;
    public static final int XML = 9;

    public void setSchema(PropertySetSchema var1) throws PropertyException;

    public PropertySetSchema getSchema() throws PropertyException;

    public void setAsActualType(String var1, Object var2) throws PropertyException;

    public Object getAsActualType(String var1) throws PropertyException;

    public void setBoolean(String var1, boolean var2) throws PropertyException;

    public boolean getBoolean(String var1) throws PropertyException;

    public void setData(String var1, byte[] var2) throws PropertyException;

    public byte[] getData(String var1) throws PropertyException;

    public void setDate(String var1, Date var2) throws PropertyException;

    public Date getDate(String var1) throws PropertyException;

    public void setDouble(String var1, double var2) throws PropertyException;

    public double getDouble(String var1) throws PropertyException;

    public void setInt(String var1, int var2) throws PropertyException;

    public int getInt(String var1) throws PropertyException;

    public Collection getKeys() throws PropertyException;

    public Collection getKeys(int var1) throws PropertyException;

    public Collection getKeys(String var1) throws PropertyException;

    public Collection getKeys(String var1, int var2) throws PropertyException;

    public void setLong(String var1, long var2) throws PropertyException;

    public long getLong(String var1) throws PropertyException;

    public void setObject(String var1, Object var2) throws PropertyException;

    public Object getObject(String var1) throws PropertyException;

    public void setProperties(String var1, Properties var2) throws PropertyException;

    public Properties getProperties(String var1) throws PropertyException;

    public boolean isSettable(String var1);

    public void setString(String var1, String var2) throws PropertyException;

    public String getString(String var1) throws PropertyException;

    public void setText(String var1, String var2) throws PropertyException;

    public String getText(String var1) throws PropertyException;

    public int getType(String var1) throws PropertyException;

    public void setXML(String var1, Document var2) throws PropertyException;

    public Document getXML(String var1) throws PropertyException;

    public boolean exists(String var1) throws PropertyException;

    public void init(Map var1, Map var2);

    public void remove(String var1) throws PropertyException;

    public boolean supportsType(int var1);

    public boolean supportsTypes();
}


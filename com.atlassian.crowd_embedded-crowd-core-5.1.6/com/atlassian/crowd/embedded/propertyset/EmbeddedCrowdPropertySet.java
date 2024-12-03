/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.google.common.collect.ImmutableMap
 *  com.opensymphony.module.propertyset.IllegalPropertyException
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.map.MapPropertySet
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.embedded.propertyset;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.embedded.propertyset.DateFormats;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.google.common.collect.ImmutableMap;
import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.map.MapPropertySet;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Document;

public final class EmbeddedCrowdPropertySet
extends MapPropertySet {
    private final CrowdService crowdService;
    private final User user;

    public EmbeddedCrowdPropertySet(UserWithAttributes user, CrowdService crowdService) {
        HashMap<String, String> attributesMap = new HashMap<String, String>();
        for (String key : user.getKeys()) {
            attributesMap.put(key, user.getValue(key));
        }
        HashMap<String, HashMap<String, String>> args = new HashMap<String, HashMap<String, String>>();
        args.put("map", attributesMap);
        super.init(null, args);
        this.crowdService = crowdService;
        this.user = user;
    }

    public int getType(String key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("PropertySet does not support types");
    }

    public boolean getBoolean(String key) {
        String value = this.getString(key);
        if (value == null) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    public void setBoolean(String key, boolean value) {
        this.setString(key, Boolean.toString(value));
    }

    public int getInt(String key) {
        String value = this.getString(key);
        if (value == null) {
            return 0;
        }
        return Integer.valueOf(value);
    }

    public void setInt(String key, int value) {
        this.setString(key, Integer.toString(value));
    }

    public long getLong(String key) {
        String value = this.getString(key);
        if (value == null) {
            return 0L;
        }
        return Long.valueOf(value);
    }

    public void setLong(String key, long value) {
        this.setString(key, Long.toString(value));
    }

    public double getDouble(String key) {
        String value = this.getString(key);
        if (value == null) {
            return 0.0;
        }
        return Double.valueOf(value);
    }

    public void setDouble(String key, double value) {
        this.setString(key, Double.toString(value));
    }

    public Date getDate(String key) {
        String value = this.getString(key);
        if (value == null) {
            return null;
        }
        try {
            return DateFormats.getDateFormat().parse(value);
        }
        catch (ParseException e) {
            throw new PropertyException("Could not parse date '" + value + "'.");
        }
    }

    public void setDate(String key, Date value) {
        if (value == null) {
            this.setString(key, null);
            return;
        }
        this.setString(key, DateFormats.getDateFormat().format(value));
    }

    public void setText(String key, String value) throws IllegalPropertyException {
        super.setString(key, value);
    }

    public byte[] getData(String key) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public void setData(String key, byte[] value) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public Object getObject(String key) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public void setObject(String key, Object value) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public Properties getProperties(String key) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public void setProperties(String key, Properties value) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public Document getXML(String key) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public void setXML(String key, Document value) {
        throw new UnsupportedOperationException("Unsupported data type");
    }

    public boolean supportsTypes() {
        return false;
    }

    public synchronized Map getMap() {
        return ImmutableMap.copyOf((Map)super.getMap());
    }

    public synchronized void setMap(Map map) {
        throw new UnsupportedOperationException("Underlying map cannot be changed in this implementation");
    }

    public void remove(String key) {
        try {
            this.crowdService.removeUserAttribute(this.user, key);
        }
        catch (OperationNotPermittedException e) {
            throw new RuntimeException(e);
        }
        super.remove(key);
    }

    protected void setImpl(int type, String key, Object value) {
        if (type != 5 && type != 6) {
            throw new UnsupportedOperationException("EmbeddedCrowdPropertySet doesn't support type '" + type + "'");
        }
        try {
            this.crowdService.removeUserAttribute(this.user, key);
        }
        catch (OperationNotPermittedException e) {
            throw new RuntimeException(e);
        }
        if (value != null) {
            try {
                this.crowdService.setUserAttribute(this.user, key, (String)value);
            }
            catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }
        super.setImpl(type, key, value);
    }

    public boolean supportsType(int type) {
        switch (type) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                return true;
            }
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return false;
            }
        }
        throw new IllegalArgumentException("Unknown type " + type);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("user", (Object)this.user).toString();
    }
}


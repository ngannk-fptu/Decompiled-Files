/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.aggregate;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public class AggregatePropertySet
extends AbstractPropertySet
implements Serializable {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$aggregate$AggregatePropertySet == null ? (class$com$opensymphony$module$propertyset$aggregate$AggregatePropertySet = AggregatePropertySet.class$("com.opensymphony.module.propertyset.aggregate.AggregatePropertySet")) : class$com$opensymphony$module$propertyset$aggregate$AggregatePropertySet));
    private List propertySets;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$aggregate$AggregatePropertySet;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        Iterator i = this.propertySets.iterator();
        ArrayList keys = new ArrayList();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            try {
                keys.addAll(set.getKeys(prefix, type));
            }
            catch (PropertyException ex) {}
        }
        return keys;
    }

    public boolean isSettable(String property) {
        Iterator i = this.propertySets.iterator();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            if (!set.isSettable(property)) continue;
            return true;
        }
        return false;
    }

    public int getType(String key) throws PropertyException {
        Iterator i = this.propertySets.iterator();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            try {
                return set.getType(key);
            }
            catch (PropertyException ex) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Error checking type of " + key + ":" + ex));
            }
        }
        throw new PropertyException("No key " + key + " found");
    }

    public void addPropertySet(PropertySet propertySet) {
        this.propertySets.add(propertySet);
    }

    public boolean exists(String key) throws PropertyException {
        Iterator i = this.propertySets.iterator();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            try {
                if (!set.exists(key)) continue;
                return true;
            }
            catch (PropertyException ex) {
                log.warn((Object)("Error " + ex + " exists(" + key + ") on " + set));
            }
        }
        return false;
    }

    public void init(Map config, Map args) {
        this.propertySets = (List)args.get("PropertySets");
        if (this.propertySets == null) {
            this.propertySets = new ArrayList();
        }
    }

    public void remove(String key) throws PropertyException {
        Iterator i = this.propertySets.iterator();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            try {
                set.remove(key);
            }
            catch (PropertyException ex) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Error removing property " + key + ":" + ex));
            }
        }
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        Iterator i = this.propertySets.iterator();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            try {
                if (!set.isSettable(key)) continue;
                switch (type) {
                    case 1: {
                        set.setBoolean(key, (Boolean)value);
                        return;
                    }
                    case 2: {
                        set.setInt(key, ((Number)value).intValue());
                        return;
                    }
                    case 3: {
                        set.setLong(key, ((Number)value).longValue());
                        return;
                    }
                    case 4: {
                        set.setDouble(key, ((Number)value).doubleValue());
                        return;
                    }
                    case 5: {
                        set.setString(key, (String)value);
                        return;
                    }
                    case 6: {
                        set.setText(key, (String)value);
                        return;
                    }
                    case 7: {
                        set.setDate(key, (Date)value);
                        return;
                    }
                    case 8: {
                        set.setObject(key, value);
                        return;
                    }
                    case 9: {
                        set.setXML(key, (Document)value);
                        return;
                    }
                    case 10: {
                        set.setData(key, (byte[])value);
                        return;
                    }
                }
            }
            catch (PropertyException ex) {}
        }
    }

    protected Object get(int type, String key) throws PropertyException {
        Iterator i = this.propertySets.iterator();
        while (i.hasNext()) {
            PropertySet set = (PropertySet)i.next();
            try {
                switch (type) {
                    case 1: {
                        boolean bool = set.getBoolean(key);
                        if (bool) {
                            return Boolean.TRUE;
                        }
                        if (!set.exists(key)) break;
                        return Boolean.FALSE;
                    }
                    case 2: {
                        int maybeInt = set.getInt(key);
                        if (maybeInt == 0) break;
                        return new Integer(maybeInt);
                    }
                    case 3: {
                        long maybeLong = set.getLong(key);
                        if (maybeLong == 0L) break;
                        return new Long(maybeLong);
                    }
                    case 4: {
                        double maybeDouble = set.getDouble(key);
                        if (maybeDouble == 0.0) break;
                        return new Double(maybeDouble);
                    }
                    case 5: {
                        String string = set.getString(key);
                        if (string == null) break;
                        return string;
                    }
                    case 6: {
                        String text = set.getText(key);
                        if (text == null) break;
                        return text;
                    }
                    case 7: {
                        Date date = set.getDate(key);
                        if (date == null) break;
                        return date;
                    }
                    case 8: {
                        Object obj = set.getObject(key);
                        if (obj == null) break;
                        return obj;
                    }
                    case 9: {
                        Document doc = set.getXML(key);
                        if (doc == null) break;
                        return doc;
                    }
                    case 10: {
                        byte[] data = set.getData(key);
                        if (data == null) break;
                        return data;
                    }
                    case 11: {
                        Properties p = set.getProperties(key);
                        if (p == null) break;
                        return p;
                    }
                }
            }
            catch (PropertyException ex) {}
        }
        return null;
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


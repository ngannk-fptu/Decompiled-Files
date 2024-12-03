/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 *  com.opensymphony.user.User
 */
package com.atlassian.core.user.preferences;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.DefaultPreferences;
import com.atlassian.core.user.preferences.Preferences;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.user.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserPreferences
implements Preferences,
Serializable {
    private PropertySet backingPS = null;
    private Set defaultKeys = null;

    public UserPreferences() {
        this((PropertySet)null, true);
    }

    public UserPreferences(User pUser) {
        this(pUser, true);
    }

    public UserPreferences(PropertySet propertySet) {
        this.backingPS = propertySet;
        this.defaultKeys = new HashSet();
    }

    public UserPreferences(PropertySet propertySet, boolean bulkload) {
        this.defaultKeys = new HashSet();
        if (propertySet != null) {
            PropertySet userPs = propertySet;
            HashMap<String, Object> params = new HashMap<String, Object>(2);
            params.put("PropertySet", userPs);
            params.put("bulkload", new Boolean(bulkload));
            this.backingPS = PropertySetManager.getInstance((String)"cached", params);
        }
    }

    public UserPreferences(User pUser, boolean bulkload) {
        this.defaultKeys = new HashSet();
        if (pUser != null) {
            PropertySet userPs = pUser.getPropertySet();
            HashMap<String, Object> params = new HashMap<String, Object>(2);
            params.put("PropertySet", userPs);
            params.put("bulkload", new Boolean(bulkload));
            this.backingPS = PropertySetManager.getInstance((String)"cached", params);
        }
    }

    @Override
    public long getLong(String key) {
        if (this.defaultKeys.contains(key)) {
            return DefaultPreferences.getPreferences().getLong(key);
        }
        if (this.backingPS != null && this.backingPS.exists(key)) {
            return this.backingPS.getLong(key);
        }
        this.defaultKeys.add(key);
        return DefaultPreferences.getPreferences().getLong(key);
    }

    @Override
    public void setLong(String key, long i) throws AtlassianCoreException {
        if (this.backingPS == null) {
            throw new AtlassianCoreException("Trying to set a property on a null user this is not allowed");
        }
        this.defaultKeys.remove(key);
        this.backingPS.setLong(key, i);
    }

    @Override
    public String getString(String key) {
        if (this.defaultKeys.contains(key)) {
            return DefaultPreferences.getPreferences().getString(key);
        }
        if (this.backingPS != null && this.backingPS.exists(key)) {
            return this.backingPS.getString(key);
        }
        this.defaultKeys.add(key);
        return DefaultPreferences.getPreferences().getString(key);
    }

    @Override
    public void setString(String key, String value) throws AtlassianCoreException {
        if (this.backingPS == null) {
            throw new AtlassianCoreException("Trying to set a property on a null user this is not allowed");
        }
        this.defaultKeys.remove(key);
        this.backingPS.setString(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        if (this.defaultKeys.contains(key)) {
            return DefaultPreferences.getPreferences().getBoolean(key);
        }
        if (this.backingPS != null && this.backingPS.exists(key)) {
            return this.backingPS.getBoolean(key);
        }
        this.defaultKeys.add(key);
        return DefaultPreferences.getPreferences().getBoolean(key);
    }

    @Override
    public void setBoolean(String key, boolean b) throws AtlassianCoreException {
        if (this.backingPS == null) {
            throw new AtlassianCoreException("Trying to set a property on a null user this is not allowed");
        }
        this.defaultKeys.remove(key);
        this.backingPS.setBoolean(key, b);
    }

    @Override
    public void remove(String key) throws AtlassianCoreException {
        if (this.backingPS == null) {
            throw new AtlassianCoreException("Trying to remove a property on a null user this is not allowed");
        }
        if (!this.backingPS.exists(key)) {
            throw new AtlassianCoreException("The property with key '" + key + "' does not exist.");
        }
        this.defaultKeys.remove(key);
        this.backingPS.remove(key);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPreferences)) {
            return false;
        }
        UserPreferences userPreferences = (UserPreferences)o;
        return !(this.backingPS != null ? !UserPreferences.identical(this.backingPS, userPreferences.backingPS) : userPreferences.backingPS != null);
    }

    public int hashCode() {
        return this.backingPS != null ? this.backingPS.hashCode() : 0;
    }

    public static boolean identical(PropertySet pThis, PropertySet pThat) {
        Collection thatKeys;
        if (pThis == null && pThat == null) {
            return true;
        }
        if (pThis == null || pThat == null) {
            return false;
        }
        Collection thisKeys = pThis.getKeys();
        if (!thisKeys.containsAll(thatKeys = pThat.getKeys()) || !thatKeys.containsAll(thisKeys)) {
            return false;
        }
        for (String key : thisKeys) {
            int keyType = pThis.getType(key);
            if (1 == keyType) {
                if (pThis.getBoolean(key) == pThat.getBoolean(key)) continue;
                return false;
            }
            if (10 == keyType) {
                throw new IllegalArgumentException("DATA Comparision has not been implemented in PropertyUtil");
            }
            if (7 == keyType) {
                if (pThis.getDate(key).equals(pThat.getDate(key))) continue;
                return false;
            }
            if (4 == keyType) {
                if (pThis.getDouble(key) == pThat.getDouble(key)) continue;
                return false;
            }
            if (2 == keyType) {
                if (pThis.getInt(key) == pThat.getInt(key)) continue;
                return false;
            }
            if (8 == keyType) {
                throw new IllegalArgumentException("OBJECT Comparision has not been implemented in PropertyUtil");
            }
            if (11 == keyType) {
                throw new IllegalArgumentException("PROPERTIES Comparision has not been implemented in PropertyUtil");
            }
            if (3 == keyType) {
                if (pThis.getLong(key) == pThat.getLong(key)) continue;
                return false;
            }
            if (5 == keyType) {
                if (pThis.getString(key).equals(pThat.getString(key))) continue;
                return false;
            }
            if (6 == keyType) {
                if (pThis.getText(key).equals(pThat.getText(key))) continue;
                return false;
            }
            if (9 != keyType) continue;
            throw new IllegalArgumentException("XML Comparision has not been implemented in PropertyUtil");
        }
        return true;
    }
}


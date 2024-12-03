/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.apache.batik.util.resources.ResourceFormatException;

public class ResourceManager {
    protected ResourceBundle bundle;

    public ResourceManager(ResourceBundle rb) {
        this.bundle = rb;
    }

    public String getString(String key) throws MissingResourceException {
        return this.bundle.getString(key);
    }

    public List getStringList(String key) throws MissingResourceException {
        return this.getStringList(key, " \t\n\r\f", false);
    }

    public List getStringList(String key, String delim) throws MissingResourceException {
        return this.getStringList(key, delim, false);
    }

    public List getStringList(String key, String delim, boolean returnDelims) throws MissingResourceException {
        ArrayList<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(this.getString(key), delim, returnDelims);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    public boolean getBoolean(String key) throws MissingResourceException, ResourceFormatException {
        String b = this.getString(key);
        if (b.equals("true")) {
            return true;
        }
        if (b.equals("false")) {
            return false;
        }
        throw new ResourceFormatException("Malformed boolean", this.bundle.getClass().getName(), key);
    }

    public int getInteger(String key) throws MissingResourceException, ResourceFormatException {
        String i = this.getString(key);
        try {
            return Integer.parseInt(i);
        }
        catch (NumberFormatException e) {
            throw new ResourceFormatException("Malformed integer", this.bundle.getClass().getName(), key);
        }
    }

    public int getCharacter(String key) throws MissingResourceException, ResourceFormatException {
        String s = this.getString(key);
        if (s == null || s.length() == 0) {
            throw new ResourceFormatException("Malformed character", this.bundle.getClass().getName(), key);
        }
        return s.charAt(0);
    }
}


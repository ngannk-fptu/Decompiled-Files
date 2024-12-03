/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.core.util.XMLUtils
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 */
package com.atlassian.core.user.preferences;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.Preferences;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.core.util.XMLUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultPreferences
implements Preferences {
    static Preferences _instance;
    private PropertySet backingPS = PropertySetManager.getInstance((String)"memory", null);

    public DefaultPreferences() {
        InputStream defaults = ClassLoaderUtils.getResourceAsStream((String)"preferences-default.xml", this.getClass());
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDoc = db.parse(defaults);
            Element root = xmlDoc.getDocumentElement();
            NodeList preferences = root.getElementsByTagName("preference");
            for (int i = 0; i < preferences.getLength(); ++i) {
                Element preference = (Element)preferences.item(i);
                String operation = preference.getAttribute("type");
                if (operation == null) {
                    operation = "String";
                }
                String name = XMLUtils.getContainedText((Node)preference, (String)"name");
                String value = XMLUtils.getContainedText((Node)preference, (String)"value");
                if ("String".equals(operation)) {
                    this.backingPS.setString(name, value);
                    continue;
                }
                if ("Long".equals(operation)) {
                    this.backingPS.setLong(name, new Long(value).longValue());
                    continue;
                }
                if (!"Boolean".equals(operation)) continue;
                this.backingPS.setBoolean(name, new Boolean(value).booleanValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            defaults.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Preferences getPreferences() {
        if (_instance == null) {
            _instance = new DefaultPreferences();
        }
        return _instance;
    }

    @Override
    public long getLong(String key) {
        return this.backingPS.getLong(key);
    }

    @Override
    public void setLong(String key, long value) throws AtlassianCoreException {
        throw new AtlassianCoreException("Trying to set a Default preference this is not allowed");
    }

    @Override
    public String getString(String key) {
        return this.backingPS.getString(key);
    }

    @Override
    public void setString(String key, String value) throws AtlassianCoreException {
        throw new AtlassianCoreException("Trying to set a Default preference this is not allowed");
    }

    @Override
    public boolean getBoolean(String key) {
        return this.backingPS.getBoolean(key);
    }

    @Override
    public void setBoolean(String key, boolean b) throws AtlassianCoreException {
        throw new AtlassianCoreException("Trying to set a Default preference this is not allowed");
    }

    @Override
    public void remove(String key) throws AtlassianCoreException {
        throw new AtlassianCoreException("Trying to set a Default preference this is not allowed");
    }
}


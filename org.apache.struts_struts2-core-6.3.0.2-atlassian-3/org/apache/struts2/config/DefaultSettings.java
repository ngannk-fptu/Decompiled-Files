/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.config.DelegatingSettings;
import org.apache.struts2.config.PropertiesSettings;
import org.apache.struts2.config.Settings;

public class DefaultSettings
implements Settings {
    private static final Logger LOG = LogManager.getLogger(DefaultSettings.class);
    private Settings delegate;

    public DefaultSettings() {
        ArrayList<Settings> list = new ArrayList<Settings>();
        try {
            list.add(new PropertiesSettings("struts"));
        }
        catch (Exception e) {
            LOG.warn("DefaultSettings: Could not find or error in struts.properties", (Throwable)e);
        }
        this.delegate = new DelegatingSettings(list);
        String files = this.delegate.get("struts.custom.properties");
        if (files != null) {
            StringTokenizer customProperties = new StringTokenizer(files, ",");
            while (customProperties.hasMoreTokens()) {
                String name = customProperties.nextToken();
                try {
                    list.add(new PropertiesSettings(name));
                }
                catch (Exception e) {
                    LOG.error("DefaultSettings: Could not find {}.properties. Skipping.", (Object)name);
                }
            }
            this.delegate = new DelegatingSettings(list);
        }
    }

    @Override
    public Location getLocation(String name) {
        return this.delegate.getLocation(name);
    }

    @Override
    public String get(String aName) throws IllegalArgumentException {
        return this.delegate.get(aName);
    }

    @Override
    public Iterator list() {
        return this.delegate.list();
    }
}

